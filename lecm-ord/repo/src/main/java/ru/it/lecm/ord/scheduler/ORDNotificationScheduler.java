package ru.it.lecm.ord.scheduler;

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
import ru.it.lecm.ord.api.ORDModel;
import ru.it.lecm.statemachine.StatemachineModel;

/**
 *
 * @author dbayandin
 */
public class ORDNotificationScheduler extends BaseTransactionalSchedule {

	private String jobName = "ord-notification";
	private String jobGroup = "ord";
	private String triggerName = "ord-notification-trigger";
	private String triggerGroup = "ord-trigger";
	private Scheduler scheduler;
	private String cronExpression = "0 0 0 * * ? *"; // every day in 00:00
	private SearchService searchService;
	private DateFormat dateFormat = new SimpleDateFormat("yyyy\\-M\\-dd'T'HH");
	private final String searchQueryFormat = "TYPE:\"%s\" AND =@%s:\"На исполнении\"";
	private final static Logger logger = LoggerFactory.getLogger(ORDNotificationScheduler.class);

	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	public void setDateFormat(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
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

	public String getCronExpression() {
		return cronExpression;
	}

	public ORDNotificationScheduler() {
		super();
	}

	@Override
	public List<NodeRef> getNodesInTx() {
		List<NodeRef> nodes = new ArrayList<NodeRef>();

		SearchParameters sp = new SearchParameters();
		sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);
		String searchQuery = String.format(searchQueryFormat, ORDModel.TYPE_ORD.toString(), StatemachineModel.PROP_STATUS);
		sp.setQuery(searchQuery.replaceAll("-", "\\\\-"));
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

	@Override
	public Action getAction(NodeRef nodeRef) {
		return getActionService().createAction("ordNotificationExecutor");
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

}
