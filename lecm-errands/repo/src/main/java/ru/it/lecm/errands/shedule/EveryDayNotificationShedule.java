package ru.it.lecm.errands.shedule;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.quartz.CronTrigger;
import org.quartz.SchedulerException;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.errands.ErrandsService;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import ru.it.lecm.base.beans.BaseTransactionalSchedule;

/**
 * User: mshafeev
 * Date: 26.07.13
 * Time: 10:51
 */
public class EveryDayNotificationShedule extends BaseTransactionalSchedule {

    private DocumentService documentService;

    public EveryDayNotificationShedule() {
        super();
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    @Override
    public List<NodeRef> getNodesInTx() {
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
