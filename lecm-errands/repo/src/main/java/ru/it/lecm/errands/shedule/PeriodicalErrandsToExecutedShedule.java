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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.errands.ErrandsService;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * User: AIvkin
 * Date: 09.11.2017
 * Time: 14:37
 */
public class PeriodicalErrandsToExecutedShedule extends AbstractScheduledAction {

    private final static Logger logger = LoggerFactory.getLogger(PeriodicalErrandsToExecutedShedule.class);

    private String cronExpression = "0 0 3 */1 * ?"; // каждый день в 3 часа ночи
    // "0 59 * * * ? *" - каждый час в xx:59
    // "0 0/5 * * * ? *"; - каждые 5 минут
    private String firstStartExpression = "0 */15 * * * ?"; // через 15 минут после старта

    private boolean onServerStart = false;

    private String jobName = "periodical-errands-to-executed--sys-agent";
    private String jobGroup = "periodical-errands-to-executed";

    private String triggerName = "periodical-errands-to-executed-sys-agent-trigger";
    private String triggerGroup = "periodical-errands-to-executed-sys-agent-trigger-group";

    private Scheduler scheduler;

    private DocumentService documentService;
    private ErrandsService errandsService;
    private NodeService nodeService;

    public PeriodicalErrandsToExecutedShedule() {
        super();
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
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

    public void setFirstStartExpression(String firstStartExpression) {
        this.firstStartExpression = firstStartExpression;
    }

    public void setOnServerStart(boolean onServerStart) {
        this.onServerStart = onServerStart;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setErrandsService(ErrandsService errandsService) {
        this.errandsService = errandsService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
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
            } catch (final ParseException | SchedulerException ignored) {
            }
        }

        return getErrandsOnExecution();
    }

    @Override
    public Action getAction(NodeRef nodeRef) {
        return getActionService().createAction("periodicalErrandsToExecutedExecutor");
    }

    private List<NodeRef> getErrandsOnExecution() {
        logger.debug("Start periodical errands transit to executed schedule");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date currentDate = calendar.getTime();

        List<QName> types = new ArrayList<>(1);
        types.add(ErrandsService.TYPE_ERRANDS);
        List<String> statuses = new ArrayList<>(1);
        statuses.add("На периодическом исполнении");

        String filters = "@lecm\\-errands\\:period\\-end:[MIN to \"" + BaseBean.DateFormatISO8601.format(currentDate) + "\"] OR " +
                " @lecm\\-errands\\:periodically\\-radio:\"" + ErrandsService.PeriodicallyRadio.REPEAT_COUNT.toString() + "\"";

        List<NodeRef> periodicalErrands = documentService.getDocumentsByFilter(types, null, statuses, filters, null);

        // Фильтруем по количеству повторов
        periodicalErrands = periodicalErrands.stream().filter(new Predicate<NodeRef>() {
            @Override
            public boolean test(NodeRef nodeRef) {
                final String periodicallyRadio = (String) nodeService.getProperty(nodeRef, ErrandsService.PROP_ERRANDS_PERIODICALLY_RADIO);
                if (ErrandsService.PeriodicallyRadio.REPEAT_COUNT.toString().equals(periodicallyRadio)) {
                    List<NodeRef> childErrands = errandsService.getChildErrands(nodeRef);
                    Integer reiterationCount = (Integer) nodeService.getProperty(nodeRef, ErrandsService.PROP_ERRANDS_REITERATION_COUNT);
                    return childErrands != null && reiterationCount != null && childErrands.size() == reiterationCount;
                }
                return true;
            }
        }).collect(Collectors.toList());

        logger.debug("Found " + periodicalErrands.size() + " periodical rules");
        return periodicalErrands;
    }
}
