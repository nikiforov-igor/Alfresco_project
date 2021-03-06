package ru.it.lecm.eds.schedule;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.BaseTransactionalSchedule;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.eds.api.EDSDocumentService;

import java.util.*;

public class EveryDayStatusShedule extends BaseTransactionalSchedule {

    private DocumentService documentService;
    private NamespaceService namespaceService;
    private String[] statuses = null;
    private QName type;

    public EveryDayStatusShedule(QName type) {
        super();
        this.type = type;
    }

    public void setStatuses(String[] statuses) {
        this.statuses = statuses;
    }

    public void setType(QName type) {
        this.type = type;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    @Override
    public List<NodeRef> getNodesInTx() {
        return getDocuments();
    }

    private List<NodeRef> getDocuments() {
        Date now = new Date();
        String filters;
        List<QName> types = new ArrayList<QName>();
        List<String> paths = new ArrayList<String>();
        List<String> statuses = new ArrayList<String>();
        types.add(type);
        paths.add(documentService.getDocumentsFolderPath());
        if (this.statuses != null) {
            statuses.addAll(Arrays.asList(this.statuses));
        }
        filters = "@lecm\\-eds\\-document\\:is\\-expired: false";
        // Фильтр по датам
        QName dateProperty = EDSDocumentService.PROP_EXECUTION_DATE;
        String property = dateProperty.toPrefixString(namespaceService);
        property = property.replaceAll(":", "\\\\:").replaceAll("-", "\\\\-");
        filters += " AND @" + property + ": [MIN to NOW]";
        List<NodeRef> documents = documentService.getDocumentsByFilter(types, paths, statuses, filters, null);

        // в списке подписок у которых дата исполнения меньше текущей
        List<NodeRef> appropDocuments = new ArrayList<NodeRef>();
        for (NodeRef document : documents) {
            Date endDate = (Date) nodeService.getProperty(document, dateProperty);
            if (endDate != null && endDate.before(now)) {
                appropDocuments.add(document);
            }
        }
        return appropDocuments;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }
}
