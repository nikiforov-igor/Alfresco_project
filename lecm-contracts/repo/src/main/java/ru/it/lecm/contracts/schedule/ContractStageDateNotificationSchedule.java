package ru.it.lecm.contracts.schedule;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.quartz.CronTrigger;
import org.quartz.SchedulerException;
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

    private DocumentService documentService;

    DateFormat DateFormatISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public ContractStageDateNotificationSchedule() {
        super();
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    @Override
    public List<NodeRef> getNodesInTx() {
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