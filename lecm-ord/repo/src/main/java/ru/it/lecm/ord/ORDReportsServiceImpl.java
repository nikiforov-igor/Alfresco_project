package ru.it.lecm.ord;

import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.ord.api.ORDModel;
import ru.it.lecm.ord.api.ORDReportsService;
import ru.it.lecm.reports.api.ReportsManager;

/**
 * @author dbayandin
 */
public class ORDReportsServiceImpl extends BaseBean implements ORDReportsService {

    private ReportsManager reportsManager;

    public void setReportsManager(ReportsManager reportsManager) {
        this.reportsManager = reportsManager;
    }

    @Override
    public NodeRef generateDocumentReport(String reportCode, String templateCode, String documentRef) {
        NodeRef documentNodeRef = new NodeRef(documentRef);
        String categoryName = ORDModel.ATTACHMENT_CATEGORIES_MAP.get(ORDModel.ATTACHMENT_CATEGORIES.DOCUMENT);
        return reportsManager.buildReportAndAttachToDocumentCategory(documentNodeRef, reportCode, templateCode, categoryName);
    }

    @Override
    public NodeRef getServiceRootFolder() {
        return null;
    }

}
