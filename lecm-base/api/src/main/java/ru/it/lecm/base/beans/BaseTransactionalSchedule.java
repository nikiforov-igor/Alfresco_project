/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.base.beans;

import java.text.ParseException;
import java.util.List;
import org.alfresco.repo.action.scheduled.AbstractScheduledAction;
import org.alfresco.repo.action.scheduled.InvalidCronExpression;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.util.PropertyCheck;
import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

/**
 * Базовый класс шедулера. Основная цель - предоставить транзакционную обёртку
 * для метода getNodes, т.к AbstractScheduledAction таковой не имеет.
 * 
 * Также класс содержит часть однотипной логики для уменьшения дублирования кода
 * 
 * @author ikhalikov
 */
public abstract class BaseTransactionalSchedule extends AbstractScheduledAction {
	
	protected String jobName;
	protected String jobGroup;
	protected String triggerName;
	protected String triggerGroup;
	protected String actionName;
	protected Scheduler scheduler;
	protected boolean onServerStart = false;
	protected String cronExpression;
	protected String firstStartExpression;
	protected boolean readOnly;

	protected LecmTransactionHelper lecmTransactionHelper;
	protected NodeService nodeService;
	protected SearchService searchService;

	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}
	
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public boolean isOnServerStart() {
		return onServerStart;
	}

	public void setOnServerStart(boolean onServerStart) {
		this.onServerStart = onServerStart;
	}

	public String getFirstStartExpression() {
		return firstStartExpression;
	}

	public void setFirstStartExpression(String firstStartExpression) {
		this.firstStartExpression = firstStartExpression;
	}
	
	@Override
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	
	@Override
	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}

	@Override
	public void setTriggerName(String triggerName) {
		this.triggerName = triggerName;
	}
	
	@Override
	public void setTriggerGroup(String triggerGroup) {
		this.triggerGroup = triggerGroup;
	}
	
	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}
	
	public void setLecmTransactionHelper(LecmTransactionHelper lecmTransactionHelper) {
		this.lecmTransactionHelper = lecmTransactionHelper;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}
	
	public Scheduler getScheduler() {
		return scheduler;
	}

	public String getActionName() {
		return actionName;
	}
	
	@Override
	public Trigger getTrigger() {
		String effectiveCronExpression = onServerStart ? firstStartExpression : cronExpression;
		try {
			CronTrigger trigger = new CronTrigger(getTriggerName(), getTriggerGroup(), effectiveCronExpression);
			trigger.setJobName(getJobName());
			trigger.setJobGroup(getJobGroup());
			return trigger;
		} catch (final ParseException e) {
			throw new InvalidCronExpression("Invalid cron expression: " + effectiveCronExpression, e);
		}
	}
	
	@Override
	public Action getAction(NodeRef nodeRef) {
		return getActionService().createAction(actionName);
	}
	
	public String getCronExpression() {
		return cronExpression;
	}
	
	@Override
	public String getJobName() {
		return jobName;
	}
	
	@Override
	public String getJobGroup() {
		return this.jobGroup;
	}
	
	@Override
	public String getTriggerName() {
		return this.triggerName;
	}
	
	@Override
	public String getTriggerGroup() {
		return this.triggerGroup;
	}

	public abstract List<NodeRef> getNodesInTx();
	
	@Override
	public void afterPropertiesSet() throws Exception {
		PropertyCheck.mandatory(this, "jobName", jobName);
		PropertyCheck.mandatory(this, "jobGroup", jobGroup);
		PropertyCheck.mandatory(this, "triggerName", triggerName);
		PropertyCheck.mandatory(this, "triggerGroup", triggerGroup);
		PropertyCheck.mandatory(this, "actionName", actionName);
		PropertyCheck.mandatory(this, "scheduler", scheduler);
		PropertyCheck.mandatory(this, "cronExpression", cronExpression);
		PropertyCheck.mandatory(this, "readOnly", readOnly);
		PropertyCheck.mandatory(this, "onServerStart", onServerStart);
		PropertyCheck.mandatory(this, "lecmTransactionHelper", lecmTransactionHelper);
		PropertyCheck.mandatory(this, "nodeService", nodeService);
		PropertyCheck.mandatory(this, "searchService", searchService);
		register(getScheduler());
	}
	
	@Override
	public List<NodeRef> getNodes() {
		if (onServerStart) { // если был запуск на старте - подменяем триггер на основной
            CronTrigger trigger = (CronTrigger) getTrigger();
            try {
                trigger.setCronExpression(cronExpression);
                getScheduler().rescheduleJob(getTriggerName(), getTriggerGroup(), trigger);
                onServerStart = false; // включаем основной триггер
            } catch (final ParseException exception) {
				throw new InvalidCronExpression("Invalid cron expression: " + cronExpression, exception);
            } catch (SchedulerException ex) {
				throw new RuntimeException("Failed to reschedule job " + jobName , ex);
			}
        }
		
		return lecmTransactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<List>() {
			@Override
			public List execute() throws Throwable {
				return getNodesInTx();
			}
		}, readOnly);
	}
	
}
