package ru.it.lecm.businessjournal.schedule;

import org.alfresco.repo.action.scheduled.AbstractScheduledAction;
import org.alfresco.repo.action.scheduled.InvalidCronExpression;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.repository.NodeRef;
import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.businessjournal.beans.BusinessJournalRecord;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import ru.it.lecm.base.beans.LecmTransactionHelper;

/**
 * @author dbashmakov
 *         Date: 23.01.13
 *         Time: 14:57
 */
public class BusinessJournalArchiveSchedule extends AbstractScheduledAction {

	private final static Logger logger = LoggerFactory.getLogger(BusinessJournalArchiveSchedule.class);

	/*
	 * The name of the job
	 */
	private String jobName = "business-journal-archiver";

	/*
	 * The job group
	 */
	private String jobGroup = "business-journal";

	/*
	 * The name of the trigger
	 */
	private String triggerName = "business-journal-archiver-trigger";

	/*
	 * The name of the trigger group
	 */
	private String triggerGroup = "business-journal-trigger";

	/*
	 * The scheduler
	 */
	private Scheduler scheduler;
	private BusinessJournalService businessJournalService;
	private BusinessJournalArchiverSettings archiverSettings;
        private LecmTransactionHelper lecmTransactionHelper;

        public void setLecmTransactionHelper(LecmTransactionHelper lecmTransactionHelper) {
            this.lecmTransactionHelper = lecmTransactionHelper;
        }

	public BusinessJournalArchiveSchedule() {
		super();
	}

	/**
	 * Set the business journal service.
	 *
	 * @param businessJournalService
	 */

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

	/**
	 * Get the scheduler.
	 *
	 * @return - the scheduler.
	 */
	public Scheduler getScheduler() {
		return scheduler;
	}

	/**
	 * Set the scheduler.
	 *
	 * @param scheduler
	 */
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	@Override
	public Trigger getTrigger() {
		try {
			CronTrigger trigger =  new CronTrigger(getTriggerName(), getTriggerGroup(), getArchiverSettings().getCronExpression());
			trigger.setJobName(getJobName());
			trigger.setJobGroup(getJobGroup());
			return trigger;
		} catch (final ParseException e) {
			throw new InvalidCronExpression("Invalid chron expression: n" + getArchiverSettings().getCronExpression());
		}
	}

	@Override
        public List<NodeRef> getNodes() {
            return lecmTransactionHelper.doInRWTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<List<NodeRef>>() {
                @Override
                public List<NodeRef> execute() throws Throwable {
                    int days = Integer.parseInt(getArchiverSettings().getDeep());
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DATE, -days);
                    List<BusinessJournalRecord> records = businessJournalService.getRecordsByInterval(null, calendar.getTime());
                    for (BusinessJournalRecord record : records) {
                        boolean success = businessJournalService.moveRecordToArchive(record.getNodeId());
                        logger.debug(String.format("Результат перемещения записи в архив: [%s] - успех [%s]", record.getNodeId(), success));
                    }
                    return new ArrayList<NodeRef>();
                }
            });
        }

	@Override
	public Action getAction(NodeRef nodeRef) {
		return getActionService().createAction("businessJournalArchiveScheduleExecutor");
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

	public void setArchiverSettings(BusinessJournalArchiverSettings archiverSettings) {
		this.archiverSettings = archiverSettings;
	}

	public BusinessJournalArchiverSettings getArchiverSettings() {
		return archiverSettings;
	}
}
