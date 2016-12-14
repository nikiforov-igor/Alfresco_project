package ru.it.lecm.errands.shedule;

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
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.errands.ErrandsService;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * User: mshafeev
 * Date: 26.07.13
 * Time: 10:51
 */
public class EveryDayNotificationShedule extends AbstractScheduledAction {

    private String cronExpression = "0 0 3 */1 * ?"; // каждый день в 3 часа ночи
                                                     // "0 59 * * * ? *" - каждый час в xx:59
                                                     // "0 0/5 * * * ? *" - каждые 5 минут
    private String firstStartExpression = "0 */15 * * * ?"; // через 15 минут после старта

    private boolean onServerStart = false;

    private String jobName = "errands-sys-agent-notifier";
    private String jobGroup = "errands-notifications";

    private String triggerName = "errands-sys-agent-notifier-trigger";
    private String triggerGroup = "errands-notifications-trigger";

    private Scheduler scheduler;

    private NodeService nodeService;
    private DocumentService documentService;

    public EveryDayNotificationShedule() {
        super();
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public Action getAction(NodeRef nodeRef) {
        return getActionService().createAction("errandsEveryDayNotificationExecutor");
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

        return getErrandsOnExecution();
    }

    private List<NodeRef> getErrandsOnExecution() {
        Date now = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        now = calendar.getTime();

        String filters;
        List<QName> types = new ArrayList<QName>();
        List<String> paths = new ArrayList<String>();
        List<String> statuses = new ArrayList<String>();

        types.add(ErrandsService.TYPE_ERRANDS);

        paths.add(documentService.getDocumentsFolderPath());

        statuses.add("!Отменено");
        statuses.add("!Не исполнено");
        statuses.add("!Черновик");
        statuses.add("!Исполнено");

        List<NodeRef> errandsDocuments = documentService.getDocumentsByFilter(types, paths, statuses, null, null);

        // в списке подписок у которых текущая дата меньше либо равна дате исполнения
        List<NodeRef> appropErrands = new ArrayList<NodeRef>();
        for (NodeRef errand : errandsDocuments) {
            Date endDate = (Date) nodeService.getProperty(errand, ErrandsService.PROP_ERRANDS_LIMITATION_DATE);
            if (endDate != null && (now.before(endDate) || now.equals(endDate))) {
                appropErrands.add(errand);
            }
        }
        return appropErrands;
    }
}
