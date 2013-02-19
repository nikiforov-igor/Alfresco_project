package ru.it.lecm.wcalendar.absence.shedule;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import ru.it.lecm.wcalendar.absence.IAbsence;

/**
 * Шедулер для окончания отсутствий: получить через searchService все ноды типа
 * absence, у которых окончание совпадает со вчерашней датой (23:59:59), которые
 * не являются бессрочными и не находятся в архиве. Запустить над ними действие
 * "absenceEndScheduleExecutor".
 *
 * @see ru.it.lecm.wcalendar.absence.shedule.AbsenceEndScheduleExecutor
 *
 * @author vlevin
 */
public class AbsenceEndShedule extends AbstractScheduledAction {

	private String jobName = "absence-end";
	private String jobGroup = "absence";
	private String triggerName = "absence-end-trigger";
	private String triggerGroup = "absence-trigger";
	private Scheduler scheduler;
	private String cronExpression = "0 1 0 * * ? *"; // каждый день в 00:01
	private IAbsence absenceService;
	private SearchService searchService;
	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	private final String searchQuery = "PARENT:\"%s\" AND TYPE:\"%s\" AND @%s:\"%s\" AND NOT (@%s:true) AND NOT (@lecm\\-dic:active:false)";

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
	public List<NodeRef> getNodes() {
		List<NodeRef> nodes = new ArrayList<NodeRef>();

		Calendar cal = Calendar.getInstance();
		// убрать следующую строку, если шедулер будет запускаться не в начале дня, а в конце
		cal.add(Calendar.DATE, -1);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 0);
		Date yesterday = cal.getTime();

		NodeRef parentContainer = absenceService.getContainer();
		
		SearchParameters sp = new SearchParameters();
		sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);
		sp.setQuery(String.format(searchQuery, parentContainer.toString(), IAbsence.TYPE_ABSENCE.toString(),
				IAbsence.PROP_ABSENCE_END.toString(), dateFormat.format(yesterday), IAbsence.PROP_ABSENCE_UNLIMITED.toString()));
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

	public AbsenceEndShedule() {
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
