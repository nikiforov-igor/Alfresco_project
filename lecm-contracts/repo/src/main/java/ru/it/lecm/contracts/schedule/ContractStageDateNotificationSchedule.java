package ru.it.lecm.contracts.schedule;

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
import ru.it.lecm.contracts.beans.ContractsBeanImpl;
import ru.it.lecm.documents.beans.DocumentService;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import ru.it.lecm.base.beans.BaseTransactionalSchedule;

/**
 * User: PMelnikov
 * Date: 20.11.13
 * Time: 9:28
 */
public class ContractStageDateNotificationSchedule extends BaseTransactionalSchedule {

    private String cronExpression = "";
    private String firstStartExpression = "";

    private boolean onServerStart = false;

    private String jobName = "";
    private String jobGroup = "";

    private String triggerName = "";
    private String triggerGroup = "";
    private Scheduler scheduler;

    private NodeService nodeService;
    private DocumentService documentService;

    DateFormat DateFormatISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public ContractStageDateNotificationSchedule() {
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
        return getActionService().createAction("contractsStageDateNotificationExecutor");
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
    public List<NodeRef> getNodesInTx() {
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

        return getStagesOnExecution();
    }

    private List<NodeRef> getStagesOnExecution() {
        Date now = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        final String MIN = DateFormatISO8601.format(calendar.getTime());

        calendar.setTime(now);
        calendar.set(Calendar.HOUR_OF_DAY, 24);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        final String MAX = DateFormatISO8601.format(calendar.getTime());

        String filters;
        List<QName> types = new ArrayList<QName>();
        List<String> paths = new ArrayList<String>();

        types.add(ContractsBeanImpl.TYPE_CONTRACT_STAGE);

        paths.add(documentService.getDocumentsFolderPath());

        filters = "(@lecm\\-contract\\-table\\-structure\\:start\\-date: [\"" + MIN + "\" TO \"" + MAX + "\"] OR @lecm\\-contract\\-table\\-structure\\:end\\-date: [\"" + MIN + "\" to \"" + MAX + "\"])";

        List<NodeRef> contractStages = documentService.getDocumentsByFilter(types, paths, null, filters, null);
        return contractStages;
    }

}