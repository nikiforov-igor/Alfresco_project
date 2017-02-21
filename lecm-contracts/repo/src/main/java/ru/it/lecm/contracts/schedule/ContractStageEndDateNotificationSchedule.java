package ru.it.lecm.contracts.schedule;

import org.alfresco.repo.action.scheduled.AbstractScheduledAction;
import org.alfresco.repo.action.scheduled.InvalidCronExpression;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import ru.it.lecm.documents.beans.DocumentGlobalSettingsService;
import ru.it.lecm.documents.beans.DocumentService;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * User: AZinovin
 * Date: 12.05.16
 * Time: 10:51
 */
public class ContractStageEndDateNotificationSchedule extends AbstractScheduledAction {

    public static final QName TYPE_CONTRACT_STAGE = QName.createQName("http://www.it.ru/logicECM/contract/table-structure/1.0", "stage");
    private String cronExpression = "";

    private String jobName = "";
    private String jobGroup = "";

    private String triggerName = "";
    private String triggerGroup = "";
    private Scheduler scheduler;

    private DocumentService documentService;

    private DocumentGlobalSettingsService documentGlobalSettings;

    DateFormat DateFormatISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public ContractStageEndDateNotificationSchedule() {
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
        return getActionService().createAction("contractStageEndDateNotificationExecutor");
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

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setDocumentGlobalSettings(DocumentGlobalSettingsService documentGlobalSettings) {
        this.documentGlobalSettings = documentGlobalSettings;
    }

    @Override
    public Trigger getTrigger() {
        try {
            CronTrigger trigger = new CronTrigger(getTriggerName(), getTriggerGroup(), cronExpression);
            trigger.setJobName(getJobName());
            trigger.setJobGroup(getJobGroup());
            return trigger;
        } catch (final ParseException e) {
            throw new InvalidCronExpression("Invalid cron expression: " + cronExpression);
        }
    }

    @Override
    public List<NodeRef> getNodes() {
        Date now = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        final String MIN = DateFormatISO8601.format(calendar.getTime());

        calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_YEAR, documentGlobalSettings.getSettingsNDays());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 0);

        final String MAX = DateFormatISO8601.format(calendar.getTime());

        String filters;
        List<QName> types = new ArrayList<>();
        List<String> paths = new ArrayList<>();

        types.add(TYPE_CONTRACT_STAGE);

        paths.add(documentService.getDocumentsFolderPath());

        filters = "@lecm\\-contract\\-table\\-structure\\:end\\-date: [\"" + MIN + "\" to \"" + MAX + "\"]  AND NOT lecm\\-contract\\-table\\-structure\\:stage\\-status:\"Закрыт\"";

        return documentService.getDocumentsByFilter(types, paths, null, filters, null);
    }

}