package ru.it.lecm.workflow.routes.schedule;

import java.text.ParseException;
import java.util.ArrayList;
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
import ru.it.lecm.workflow.routes.api.RoutesModel;

/**
 *
 * @author vlevin
 */
public class DeleteTempRoutesSchedule extends BaseTransactionalSchedule {

	private Scheduler scheduler;
	private SearchService searchService;
	private String jobName = "routes-delete-temp";
	private String jobGroup = "routes";
	private String triggerName = "routes-delete-temp-trigger";
	private String triggerGroup = "routes-trigger";
	private String cronExpression = "0 0 4 * * ? *"; // каждый день в 04:00
	private final String searchQueryFormat = "(+TYPE:\"%s\" OR +TYPE:\"%s\") AND (+ASPECT:\"sys:temporary\" OR +ASPECT:\"lecm-workflow:temp\")";
	private final static Logger logger = LoggerFactory.getLogger(DeleteTempRoutesSchedule.class);

	@Override
	public Action getAction(NodeRef nodeRef) {
		return getActionService().createAction("deleteAction");
	}

	@Override
	public List<NodeRef> getNodesInTx() {
		List<NodeRef> nodes = new ArrayList<>();

		SearchParameters sp = new SearchParameters();
		sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);
		String searchQuery = String.format(searchQueryFormat, RoutesModel.TYPE_ROUTE, RoutesModel.TYPE_STAGE);
		logger.trace("Searching temp routes to be deleted: " + searchQuery);
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

	public DeleteTempRoutesSchedule() {
		super();
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
