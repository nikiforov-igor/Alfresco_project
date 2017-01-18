package ru.it.lecm.resolutions.shedule;

import org.alfresco.repo.action.scheduled.AbstractScheduledAction;
import org.alfresco.repo.action.scheduled.InvalidCronExpression;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.resolutions.api.ResolutionsService;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: AIvkin
 * Date: 17.01.2017
 * Time: 17:30
 */
public class ResolutionsExpiredShedule extends AbstractScheduledAction {

    private String cronExpression = "0 0 3 */1 * ?"; // каждый день в 3 часа ночи
    // "0 59 * * * ? *" - каждый час в xx:59
    // "0 0/5 * * * ? *"; - каждые 5 минут
    private String firstStartExpression = "0 */15 * * * ?"; // через 15 минут после старта

    private boolean onServerStart = false;

    private String jobName = "resolutions-sys-agent-notifier";
    private String jobGroup = "resolutions-notifications";

    private String triggerName = "resolutions-sys-agent-notifier-trigger";
    private String triggerGroup = "resolutions-notifications-trigger";

    private Scheduler scheduler;

    private NodeService nodeService;
    private DocumentService documentService;
    private NamespaceService namespaceService;

    public ResolutionsExpiredShedule() {
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
        return getActionService().createAction("resolutionsExpiredExecutor");
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

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
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

        return getResolutionsOnExecution();
    }

    private List<NodeRef> getResolutionsOnExecution() {
        List<QName> types = new ArrayList<>();
        List<String> paths = new ArrayList<>();
        List<String> statuses = new ArrayList<>();

        types.add(ResolutionsService.TYPE_RESOLUTION_DOCUMENT);
        paths.add(documentService.getDocumentsFolderPath());
        statuses.add("На исполнении");

        // Фильтр по датам
        String filters = "@lecm\\-resolutions\\:limitation\\-date: [MIN to NOW]";
        return documentService.getDocumentsByFilter(types, paths, statuses, filters, null);
    }
}
