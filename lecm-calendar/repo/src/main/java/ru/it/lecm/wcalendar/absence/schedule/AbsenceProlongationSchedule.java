package ru.it.lecm.wcalendar.absence.schedule;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.transaction.UserTransaction;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.action.scheduled.AbstractScheduledAction;
import org.alfresco.repo.action.scheduled.InvalidCronExpression;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.wcalendar.absence.IAbsence;

/**
 * Шедулер для одля продления бессрочного отсутствия: получить через
 * searchService все ноды типа absence, у которых окончание запланировано на
 * этот час, которые являются бессрочными, активированными и не находятся в
 * архиве. Запустить над ними действие "absenceProlongationScheduleExecutor".
 *
 * @see ru.it.lecm.wcalendar.absence.schedule.AbsenceProlongationScheduleExecutor
 *
 * @author vlevin
 */
public class AbsenceProlongationSchedule extends AbstractScheduledAction {

	private String jobName = "absence-prolongation";
	private String jobGroup = "absence";
	private String triggerName = "absence-prolongation-trigger";
	private String triggerGroup = "absence-trigger";
	private Scheduler scheduler;
	private String cronExpression = "0 1 * * * ? *"; // каждый час в xx:01
	private IAbsence absenceService;
	private SearchService searchService;
	private DateFormat dateFormat = new SimpleDateFormat("yyyy\\-MM\\-dd'T'HH");
	private final String searchQueryFormat = "PARENT:\"%s\" AND TYPE:\"%s\" AND @%s:[MIN TO %s] AND @%s:true AND @%s:true AND NOT (@lecm\\-dic:active:false)";
	private final static Logger logger = LoggerFactory.getLogger(AbsenceProlongationSchedule.class);

	@Override
	public Action getAction(NodeRef nodeRef) {
		return getActionService().createAction("absenceProlongationScheduleExecutor");
	}

	@Override
	public Trigger getTrigger() {
		try {
			CronTrigger trigger = new CronTrigger(getTriggerName(), getTriggerGroup(), getCronExpression());
			trigger.setJobName(getJobName());
			trigger.setJobGroup(getJobGroup());
			return trigger;
		} catch (final ParseException e) {
			throw new InvalidCronExpression("Invalid cron expression: " + getCronExpression(), e);
		}
	}

	@Override
	public List<NodeRef> getNodes() {
		
		List<NodeRef> nodes = new ArrayList<NodeRef>();
		Date now = new Date();
		NodeRef parentContainer = null;
		
		UserTransaction userTransaction = getTransactionService().getUserTransaction();
        try
        {
            userTransaction.begin();
            
            parentContainer = absenceService.getContainer();
            
			userTransaction.commit();
        }
        catch(Throwable e)
        {
            // rollback the transaction
            try
            { 
                if (userTransaction != null) 
                {
                    userTransaction.rollback();
                }
            }
            catch (Exception ex)
            {
                // NOOP 
            }
            throw new AlfrescoRuntimeException("Service folders [notifications-dashlet] bootstrap failed", e);
        }

		SearchParameters sp = new SearchParameters();
		sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);
		String searchQuery = String.format(searchQueryFormat, parentContainer.toString(), IAbsence.TYPE_ABSENCE.toString(), IAbsence.PROP_ABSENCE_END.toString(),
				dateFormat.format(now), IAbsence.PROP_ABSENCE_UNLIMITED.toString(), IAbsence.PROP_ABSENCE_ACTIVATED.toString());
		logger.trace("Searching absences to be prolongated: " + searchQuery);
		sp.setQuery(searchQuery);
		ResultSet results = null;
		try {
			results = searchService.query(sp);
			for (ResultSetRow row : results) {
				NodeRef currentNodeRef = row.getNodeRef();
				nodes.add(currentNodeRef);
			}
		} finally {
			if (results != null) {
				results.close();
			}
		}
		return nodes;
	}

	public AbsenceProlongationSchedule() {
		super();
	}

	public void setAbsenceService(IAbsence absenceService) {
		this.absenceService = absenceService;
	}

	@Override
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	@Override
	public String getJobName() {
		return jobName;
	}

	@Override
	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}

	@Override
	public String getJobGroup() {
		return this.jobGroup;
	}

	@Override
	public void setTriggerName(String triggerName) {
		this.triggerName = triggerName;
	}

	@Override
	public String getTriggerName() {
		return this.triggerName;
	}

	@Override
	public void setTriggerGroup(String triggerGroup) {
		this.triggerGroup = triggerGroup;
	}

	@Override
	public String getTriggerGroup() {
		return this.triggerGroup;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		register(getScheduler());
	}

	public Scheduler getScheduler() {
		return scheduler;
	}

	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}
}
