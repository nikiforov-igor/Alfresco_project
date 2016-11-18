package ru.it.lecm.contracts.schedule;

import org.alfresco.service.cmr.repository.NodeRef;
import org.quartz.CronTrigger;
import org.quartz.SchedulerException;
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

    private ContractsBeanImpl contractsService;

    public InititatorEveryDayNotificationSchedule() {
        super();
    }

    public void setContractsService(ContractsBeanImpl contractsService) {
        this.contractsService = contractsService;
    }

    @Override
    public List<NodeRef> getNodesInTx() {
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
