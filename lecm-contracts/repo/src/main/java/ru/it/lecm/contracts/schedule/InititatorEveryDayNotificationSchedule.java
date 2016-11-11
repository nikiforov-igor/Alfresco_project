package ru.it.lecm.contracts.schedule;

import org.alfresco.repo.action.scheduled.AbstractScheduledAction;
import org.alfresco.repo.action.scheduled.InvalidCronExpression;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import ru.it.lecm.contracts.beans.ContractsBeanImpl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import ru.it.lecm.base.beans.BaseTransactionalSchedule;

/**
 * @author dbashmakov
 *         Date: 24.04.13
 *         Time: 14:11
 */
public class InititatorEveryDayNotificationSchedule extends BaseTransactionalSchedule {

    private String cronExpression = "0 0 3 */1 * ?";
    private String firstStartExpression = "0 */15 * * * ?";

    private boolean onServerStart = false;

    private String jobName = "contracts-initiator-notifier";
    private String jobGroup = "contracts-notifications";

    private String triggerName = "contracts-initiator-notifier-trigger";
    private String triggerGroup = "contracts-notifications-trigger";

    private Scheduler scheduler;

    private ContractsBeanImpl contractsService;
    private NodeService nodeService;

    public InititatorEveryDayNotificationSchedule() {
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
        return getActionService().createAction("contractsInitiatorNotificationExecutor");
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

    public void setContractsService(ContractsBeanImpl contractsService) {
        this.contractsService = contractsService;
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

        List<NodeRef> results = new ArrayList<NodeRef>();
        results.addAll(getContractsOnExecution());
        results.addAll(getContractsAfterExecution());
        return results;
    }

    private List<NodeRef> getContractsAfterExecution() {
        return contractsService.getContractsByFilter(ContractsBeanImpl.PROP_END_DATE, null, new Date(),
                Arrays.asList(contractsService.getDocumentsFolderPath()),
                Arrays.asList("Зарегистрирован", "Действует"), null, null, false);
    }

    private List<NodeRef> getContractsOnExecution() {
        Date now = new Date();

        List<NodeRef> contracts = contractsService.getContractsByFilter(ContractsBeanImpl.PROP_START_DATE, null, now,
                Arrays.asList(contractsService.getDocumentsFolderPath()),
                Arrays.asList("Зарегистрирован"), null, null, false);

        // в списке договора у которых дата начала меньше текущей, из них учтем только те, у которых текущая дата < даты окончания
        List<NodeRef> appropContracts = new ArrayList<NodeRef>();
        for (NodeRef contract : contracts) {
            Date endDate = (Date) nodeService.getProperty(contract, ContractsBeanImpl.PROP_END_DATE);
            if (endDate != null && now.before(endDate)) {
                appropContracts.add(contract);
            }
        }
        return appropContracts;
    }
}
