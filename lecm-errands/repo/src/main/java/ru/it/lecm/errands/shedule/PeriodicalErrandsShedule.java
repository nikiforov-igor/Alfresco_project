package ru.it.lecm.errands.shedule;

import org.alfresco.repo.action.scheduled.AbstractScheduledAction;
import org.alfresco.repo.action.scheduled.InvalidCronExpression;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.lang3.time.DateFormatUtils;
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
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * User: PMelnikov
 * Date: 05.08.14
 * Time: 15:24
 */
public class PeriodicalErrandsShedule extends AbstractScheduledAction {

    private final static Logger logger = LoggerFactory.getLogger(PeriodicalErrandsShedule.class);

    private String cronExpression = "0 0 3 */1 * ?"; // каждый день в 3 часа ночи
                                                     // "0 59 * * * ? *" - каждый час в xx:59
                                                     // "0 0/5 * * * ? *"; - каждые 5 минут
    private String firstStartExpression = "0 */15 * * * ?"; // через 15 минут после старта

    private boolean onServerStart = false;

    private String jobName = "periodical-errands-sys-agent";
    private String jobGroup = "periodical-errands";

    private String triggerName = "periodical-errands-sys-agent-trigger";
    private String triggerGroup = "periodical-errands-sys-agent-trigger-group";

    private Scheduler scheduler;

    private DocumentService documentService;
    private ErrandsService errandsService;
    private NodeService nodeService;

    public PeriodicalErrandsShedule() {
        super();
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void setErrandsService(ErrandsService errandsService) {
        this.errandsService = errandsService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
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
        return getActionService().createAction("periodicalErrandsExecutor");
    }

    private List<NodeRef> getErrandsOnExecution() {
        logger.info("Start periodical errands schedule");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date endPeriod = calendar.getTime();
        calendar.add(Calendar.HOUR_OF_DAY, 24);
        Date startPeriod = calendar.getTime();

        String startPeriodDate = BaseBean.DateFormatISO8601.format(startPeriod);
        String endPeriodDate = BaseBean.DateFormatISO8601.format(endPeriod);

        List<QName> types = new ArrayList<>(1);
        types.add(ErrandsService.TYPE_ERRANDS);
        List<String> statuses = new ArrayList<>(1);
        statuses.add("На периодическом исполнении");

       String filters = "@lecm\\-errands\\:period\\-start:[MIN to \"" + startPeriodDate + "\"] AND (@lecm\\-errands\\:period\\-end:[\"" + endPeriodDate + "\" to MAX] OR @lecm\\-errands\\:periodically\\-radio:" +
                "\"" + ErrandsService.PeriodicallyRadio.ENDLESS.toString() + "\" OR" +
                " @lecm\\-errands\\:periodically\\-radio:\"" + ErrandsService.PeriodicallyRadio.REPEAT_COUNT.toString() + "\")";

        Set<NodeRef> periodicalErrands = new HashSet<>(documentService.getDocumentsByFilter(types, null, statuses, filters, null));
        logger.info("Found " + periodicalErrands.size() + " periodical rules");


        // Фильтруем по количеству повторов
        periodicalErrands = periodicalErrands.stream().filter(new Predicate<NodeRef>() {
            @Override
            public boolean test(NodeRef nodeRef) {
                final String periodicallyRadio = (String) nodeService.getProperty(nodeRef, ErrandsService.PROP_ERRANDS_PERIODICALLY_RADIO);
                if (ErrandsService.PeriodicallyRadio.REPEAT_COUNT.toString().equals(periodicallyRadio)) {
                    List<NodeRef> childErrands = errandsService.getChildErrands(nodeRef);
                    Integer reiterationCount = (Integer) nodeService.getProperty(nodeRef, ErrandsService.PROP_ERRANDS_REITERATION_COUNT);
                    return childErrands != null && reiterationCount != null && childErrands.size() < reiterationCount;
                }
                return true;
            }
        }).collect(Collectors.toSet());

        // Так же в результат добавляем периодические поручения, создание которых было отложено на сегодня
        final Map<String, Set<NodeRef>> delayedErrandsByDate = errandsService.getDelayedErrandsByDate();
        final String todayDateStr = DateFormatUtils.format(new Date(), "dd-MM-yyyy");
        final Set<NodeRef> delayedErrandsForToday = delayedErrandsByDate.get(todayDateStr);
        if (delayedErrandsForToday != null) {
            periodicalErrands.addAll(delayedErrandsForToday);
            // Удаляем из списка отложенных на сегодня, чтобы исключить повторную обработку
            delayedErrandsByDate.remove(todayDateStr);
            getTransactionService().getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>() {
                @Override
                public Void execute() throws Throwable {
                    errandsService.setDelayedErrandsByDate(delayedErrandsByDate);
                    return null;
                }
            }, false, true);
            logger.debug("Found " + delayedErrandsForToday.size() + " delayed periodical errands.");
        }
        logger.info("Final list of periodical errands to process: {} ", periodicalErrands);

        return new ArrayList<NodeRef>(periodicalErrands);
    }
}
