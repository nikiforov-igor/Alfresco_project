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
    private NamespaceService namespaceService;

    public EveryDayNotificationShedule() {
        super();
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
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

        filters = "@lecm\\-statemachine\\-aspects\\:is\\-draft: false AND (NOT ASPECT:\"lecm-statemachine-aspects:is-final-aspect\" OR @lecm\\-statemachine\\-aspects\\:is\\-final: false)";
        filters += " AND @lecm\\-errands\\:periodically: false";

        QName dateProperty = ErrandsService.PROP_ERRANDS_LIMITATION_DATE;
        String property = dateProperty.toPrefixString(namespaceService);
        filters += " AND ISNOTNULL: '" + property + "' AND NOT @" + property.replaceAll(":", "\\\\:").replaceAll("-", "\\\\-") + ":''";

        return documentService.getDocumentsByFilter(types, paths, statuses, filters, null);
    }
}
