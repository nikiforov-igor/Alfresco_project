/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.it.lecm.nd.scheduler;

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
import ru.it.lecm.nd.api.NDModel;
import ru.it.lecm.statemachine.StatemachineModel;

/**
 *
 * @author ikhalikov
 */
public class OutOfDateScheduler extends BaseTransactionalSchedule {

	private String jobName = "switch-nd-status";
	private String jobGroup = "nd";
	private String triggerName = "switch-nd-status-trigger";
	private String triggerGroup = "nd-trigger";
	private Scheduler scheduler;
	private String cronExpression = "0 1 * * * ? *"; // каждый час в xx:01
	private SearchService searchService;
	private final String searchQueryFormat = "TYPE:\"%s\" AND @%s:[MIN TO NOW] AND @%s:\"Действует\"";
	private final static Logger logger = LoggerFactory.getLogger(OutOfDateScheduler.class);

	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
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

	public OutOfDateScheduler() {
		super();
	}

	@Override
	public List<NodeRef> getNodesInTx() {
		List<NodeRef> nodes = new ArrayList<NodeRef>();
		SearchParameters sp = new SearchParameters();
		sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);
		String searchQuery = String.format(searchQueryFormat, NDModel.TYPE_ND.toString(), NDModel.PROP_ND_END, StatemachineModel.PROP_STATUS);
		sp.setQuery(searchQuery.replaceAll("-", "\\\\-"));
		ResultSet results = null;
		try {
			results = searchService.query(sp);
			for (ResultSetRow row : results) {
				NodeRef currentNodeRef = row.getNodeRef();
				nodes.add(currentNodeRef);
			}
		} catch (Exception ex) {
			if (logger.isDebugEnabled()) {
				logger.error(ex.getMessage(), ex);
			} else {
				logger.error(ex.getMessage());
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
		return getActionService().createAction("outOfDateExecutor");
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
