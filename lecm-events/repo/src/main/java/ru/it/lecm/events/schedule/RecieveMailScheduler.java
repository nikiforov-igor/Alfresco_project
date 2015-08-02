package ru.it.lecm.events.schedule;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import org.alfresco.repo.action.scheduled.AbstractScheduledAction;
import org.alfresco.repo.action.scheduled.InvalidCronExpression;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.repository.NodeRef;
import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.events.mail.incoming.MailReciever;

/**
 *
 * @author vkuprin
 */
public class RecieveMailScheduler extends AbstractScheduledAction{

	private String jobName = "events-recieve-mail";
	private String jobGroup = "events-mail";
	private String triggerName = "events-mail-trigger";
	private String triggerGroup = "events-mail-trigger-group";
	private Scheduler scheduler;
	private String cronExpression = "0 */30 * * * ? *"; // every 30 minutes
	private final static Logger logger = LoggerFactory.getLogger(RecieveMailScheduler.class);
	
	private MailReciever mailReciever;

	public MailReciever getMailReciever() {
		return mailReciever;
	}

	public void setMailReciever(MailReciever mailReciever) {
		this.mailReciever = mailReciever;
	}

	@Override
	public String getJobName() {
		return jobName;
	}

	@Override
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	@Override
	public String getJobGroup() {
		return jobGroup;
	}

	@Override
	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}

	@Override
	public String getTriggerName() {
		return triggerName;
	}

	@Override
	public void setTriggerName(String triggerName) {
		this.triggerName = triggerName;
	}

	@Override
	public String getTriggerGroup() {
		return triggerGroup;
	}

	@Override
	public void setTriggerGroup(String triggerGroup) {
		this.triggerGroup = triggerGroup;
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
		
			getTransactionService().getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>() {

				@Override
				public Void execute() throws Throwable {
					try {			
						mailReciever.recieveMail();
					} catch (WriteTransactionNeededException ex) {
						throw new RuntimeException(ex);
					}
					return null;
				}
			}, false, true);
		return Collections.EMPTY_LIST;
	}

	@Override
	public Action getAction(NodeRef nodeRef) {
		return null;
	}

	

	@Override
	public void afterPropertiesSet() throws Exception {
		register(getScheduler());
	}
	
}
