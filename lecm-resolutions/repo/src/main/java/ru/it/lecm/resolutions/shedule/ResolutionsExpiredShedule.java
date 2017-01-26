package ru.it.lecm.resolutions.shedule;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.BaseTransactionalSchedule;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.resolutions.api.ResolutionsService;

import java.util.ArrayList;
import java.util.List;

/**
 * User: AIvkin
 * Date: 17.01.2017
 * Time: 17:30
 */
public class ResolutionsExpiredShedule extends BaseTransactionalSchedule {

    private DocumentService documentService;

    public ResolutionsExpiredShedule() {
        super();
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }


    @Override
    public List<NodeRef> getNodesInTx() {
        return getResolutionsOnExecution();
    }

    private List<NodeRef> getResolutionsOnExecution() {
        List<QName> types = new ArrayList<>();
        List<String> paths = new ArrayList<>();
        List<String> statuses = new ArrayList<>();

        types.add(ResolutionsService.TYPE_RESOLUTION_DOCUMENT);
        paths.add(documentService.getDocumentsFolderPath());
        statuses.add("На исполнении");

        // Фильтр по датам
        String filters = "@lecm\\-resolutions\\:limitation\\-date: [MIN to NOW]";
        return documentService.getDocumentsByFilter(types, paths, statuses, filters, null);
    }
}
