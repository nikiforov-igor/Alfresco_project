package ru.it.lecm.wcalendar.absence.schedule;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import ru.it.lecm.base.beans.BaseTransactionalSchedule;
import ru.it.lecm.wcalendar.absence.IAbsence;

/**
 * Шедулер для окончания отсутствий: получить через searchService все ноды типа
 * absence, у которых окончание запланировано на это час, которые активированны,
 * не являются бессрочными и не находятся в архиве. Запустить над ними действие
 * "absenceEndScheduleExecutor".
 *
 * @see ru.it.lecm.wcalendar.absence.schedule.AbsenceEndScheduleExecutor
 *
 * @author vlevin
 */
public class AbsenceEndSchedule extends BaseTransactionalSchedule {

	private String jobName = "absence-end";
	private String jobGroup = "absence";
	private String triggerName = "absence-end-trigger";
	private String triggerGroup = "absence-trigger";
	private Scheduler scheduler;
	private String cronExpression = "0 59 * * * ? *"; // каждый час в xx:59
	private IAbsence absenceService;
	private SearchService searchService;
	private DateFormat dateFormat = new SimpleDateFormat("yyyy\\-MM\\-dd'T'HH");
	private final String searchQueryFormat = "PARENT:\"%s\" AND TYPE:\"%s\" AND @%s:[MIN TO %s] AND NOT (@%s:true) AND @%s:true AND NOT (@lecm\\-dic:active:false)";
	private final static Logger logger = LoggerFactory.getLogger(AbsenceEndSchedule.class);

	@Override
	public Action getAction(NodeRef nodeRef) {
		return getActionService().createAction("absenceEndScheduleExecutor");
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
	public List<NodeRef> getNodesInTx() {
		List<NodeRef> nodes = new ArrayList<NodeRef>();
		Date now = new Date();
		NodeRef parentContainer = absenceService.getContainer();

		SearchParameters sp = new SearchParameters();
		sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);
		String searchQuery = String.format(searchQueryFormat, parentContainer.toString(), IAbsence.TYPE_ABSENCE.toString(), IAbsence.PROP_ABSENCE_END.toString(),
				dateFormat.format(now), IAbsence.PROP_ABSENCE_UNLIMITED.toString(), IAbsence.PROP_ABSENCE_ACTIVATED.toString());
		logger.trace("Searching absences to be ended: " + searchQuery);
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

	public AbsenceEndSchedule() {
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
