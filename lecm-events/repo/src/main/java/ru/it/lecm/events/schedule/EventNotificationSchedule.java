package ru.it.lecm.events.schedule;

import org.alfresco.repo.action.scheduled.AbstractScheduledAction;
import org.alfresco.repo.action.scheduled.InvalidCronExpression;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import ru.it.lecm.documents.beans.DocumentGlobalSettingsService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.events.beans.EventsService;
import ru.it.lecm.wcalendar.IWorkCalendar;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * User: pmelnikov
 * Date: 21.01.14
 * Time: 13:17
 */
public class EventNotificationSchedule extends AbstractScheduledAction {

    private String cronExpression = "0 0 3 */1 * ?"; // каждый день в 3 часа ночи
    // "0 59 * * * ? *" - каждый час в xx:59
    // "0 0/5 * * * ? *" - каждые 5 минут
    private String firstStartExpression = "0 */15 * * * ?"; // через 15 минут после старта

    private boolean onServerStart = false;

    private String jobName = "events-sys-agent-notifier";
    private String jobGroup = "events-notifications";

    private String triggerName = "events-sys-agent-notifier-trigger";
    private String triggerGroup = "events-notifications-trigger";

    private Scheduler scheduler;

    private NodeService nodeService;
    private DocumentService documentService;
    private IWorkCalendar calendarBean;
    private DocumentGlobalSettingsService documentGlobalSettings;

    public EventNotificationSchedule() {
        super();
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void setDocumentGlobalSettings(DocumentGlobalSettingsService documentGlobalSettings) {
        this.documentGlobalSettings = documentGlobalSettings;
    }

    @Override
    public Action getAction(NodeRef nodeRef) {
        return getActionService().createAction("events.eventsNotificationExecutor");
    }

    public void setCalendarBean(IWorkCalendar calendarBean) {
        this.calendarBean = calendarBean;
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

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setFirstStartExpression(String firstStartExpression) {
        this.firstStartExpression = firstStartExpression;
    }

    public void setOnServerStart(boolean onServerStart) {
        this.onServerStart = onServerStart;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    @Override
    public Trigger getTrigger() {
        try {
            CronTrigger trigger = new CronTrigger(getTriggerName(), getTriggerGroup(), onServerStart ? firstStartExpression : cronExpression);
            trigger.setJobName(getJobName());
            trigger.setJobGroup(getJobGroup());
            return trigger;
        } catch (final ParseException e) {
            throw new InvalidCronExpression("Invalid chron expression: n" + (onServerStart ? firstStartExpression : cronExpression));
        }
    }

    @Override
    public List<NodeRef> getNodes() {
        if (onServerStart) { // если был запуск на старте - подменяем триггер на основной
            CronTrigger trigger = (CronTrigger) getTrigger();
            try {
                trigger.setCronExpression(cronExpression);
                getScheduler().rescheduleJob(getTriggerName(), getTriggerGroup(), trigger);
                onServerStart = false; // включаем основной триггер
            } catch (final ParseException ignored) {
            } catch (final SchedulerException ignored) {
            }
        }

        return getEventsOnExecution();
    }

    private List<NodeRef> getEventsOnExecution() {
        Date start = new Date();

        Calendar calendar = Calendar.getInstance();
        int days = documentGlobalSettings.getSettingsNDays();
        Date end = calendarBean.getNextWorkingDate(new Date(), days, Calendar.DAY_OF_MONTH);
        calendar.setTime(end);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        end = calendar.getTime();

        String filters;
        List<QName> types = new ArrayList<QName>();
        List<String> paths = new ArrayList<String>();
        List<String> statuses = new ArrayList<String>();

        types.add(EventsService.TYPE_EVENT);

        DateFormat DateFormatISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        filters = "@lecm\\-events\\:from\\-date: [\"" + DateFormatISO8601.format(start) + "\" to \"" + DateFormatISO8601.format(end) + "\"] AND @lecm\\-events\\:removed: false";
        return documentService.getDocumentsByFilter(types, paths, statuses, filters, null);
    }

}
