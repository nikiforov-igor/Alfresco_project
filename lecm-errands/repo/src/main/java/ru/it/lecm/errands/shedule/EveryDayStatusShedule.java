package ru.it.lecm.errands.shedule;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.quartz.CronTrigger;
import org.quartz.SchedulerException;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.errands.ErrandsService;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import ru.it.lecm.base.beans.BaseTransactionalSchedule;

/**
 * User: mshafeev
 * Date: 24.07.13
 * Time: 15:24
 */
public class EveryDayStatusShedule extends BaseTransactionalSchedule {

    private DocumentService documentService;
    private NamespaceService namespaceService;

    public EveryDayStatusShedule() {
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

        filters = "@lecm\\-errands\\:is\\-expired: false";
        // Фильтр по датам
        QName dateProperty = ErrandsService.PROP_ERRANDS_LIMITATION_DATE;

        String property = dateProperty.toPrefixString(namespaceService);
        property = property.replaceAll(":", "\\\\:").replaceAll("-", "\\\\-");
        filters += " AND @" + property + ": [MIN to NOW]";
        List<NodeRef> errandsDocuments = documentService.getDocumentsByFilter(types, paths, statuses, filters, null);

        // в списке подписок у которых дата исполнения меньше текущей
        List<NodeRef> appropErrands = new ArrayList<NodeRef>();
        for (NodeRef errand : errandsDocuments) {
            Date endDate = (Date) nodeService.getProperty(errand, ErrandsService.PROP_ERRANDS_LIMITATION_DATE);
            if (endDate != null && endDate.before(now)) {
                appropErrands.add(errand);
            }
        }
        return appropErrands;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }
}
