package ru.it.lecm.workflow.review.schedule;

import org.alfresco.repo.action.scheduled.AbstractScheduledAction;
import org.alfresco.repo.action.scheduled.InvalidCronExpression;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.wcalendar.IWorkCalendar;
import ru.it.lecm.workflow.review.api.ReviewService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by dkuchurkin on 11.05.2016.
 */
public class ReviewNotificationSchedule extends AbstractScheduledAction {

    private static final String DATE_FORMAT_ISO8601 = "yyyy-MM-dd'T'HH:mm:ss";

    private String cronExpression = "0 0 3 */1 * ?";
    private String firstStartExpression = "0 */15 * * * ?";
    private boolean onServerStart = false;

    private String jobName = "incoming-sys-agent-notifier";
    private String jobGroup = "incoming-notifications";
    private String triggerName = "incoming-sys-agent-notifier-trigger";
    private String triggerGroup = "incoming-notifications-trigger";

    private Scheduler scheduler;
    private DocumentService documentService;
    private ReviewService reviewService;
    private IWorkCalendar calendarBean;

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public void setFirstStartExpression(String firstStartExpression) {
        this.firstStartExpression = firstStartExpression;
    }

    public void setOnServerStart(boolean onServerStart) {
        this.onServerStart = onServerStart;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setReviewService(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    public void setCalendarBean(IWorkCalendar calendarBean) {
        this.calendarBean = calendarBean;
    }

    @Override
    public Trigger getTrigger() {
        try {
            CronTrigger trigger = new CronTrigger(triggerName, triggerGroup, onServerStart ? firstStartExpression : cronExpression);
            trigger.setJobName(jobName);
            trigger.setJobGroup(jobGroup);
            return trigger;
        } catch (final ParseException e) {
            throw new InvalidCronExpression("Invalid cron expression: n" + (onServerStart ? firstStartExpression : cronExpression));
        }
    }

    @Override
    public List<NodeRef> getNodes() {

        if (onServerStart) {
            CronTrigger trigger = (CronTrigger) getTrigger();
            try {
                trigger.setCronExpression(cronExpression);
                scheduler.rescheduleJob(getTriggerName(), getTriggerGroup(), trigger);
                onServerStart = false;
            } catch (SchedulerException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                throw new InvalidCronExpression("Invalid cron expression: n" + (onServerStart ? firstStartExpression : cronExpression));
            }
        }

        List<QName> types = Collections.singletonList(ReviewService.TYPE_REVIEW_TS_REVIEW_TABLE_ITEM);
        List<String> paths = Collections.singletonList(documentService.getDocumentsFolderPath());
        String filters = "@lecm\\-review\\-ts\\:review\\-state:\"NOT_REVIEWED\"";

        int reviewTerm = reviewService.getReviewTerm();
        int notificationTerm = reviewService.getReviewNotificationTerm();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Date upDate = calendarBean.getNextWorkingDateByDays(calendar.getTime(), 0 - (reviewTerm - notificationTerm));
        String startDateUpLimit = (new SimpleDateFormat(DATE_FORMAT_ISO8601)).format(upDate);
        filters = filters + " AND @lecm\\-review\\-ts\\:review\\-start\\-date: [MIN to \"" + startDateUpLimit + "\"]";

        return documentService.getDocumentsByFilter(types, paths, null, filters, null);
    }

    @Override
    public Action getAction(NodeRef nodeRef) {
        return getActionService().createAction("reviewNotificationExecutor");
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
        return jobGroup;
    }

    @Override
    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
    }

    @Override
    public String getTriggerName() {
        return triggerName;
    }

    @Override
    public void setTriggerGroup(String triggerGroup) {
        this.triggerGroup = triggerGroup;
    }

    @Override
    public String getTriggerGroup() {
        return triggerGroup;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        register(scheduler);
    }
}
