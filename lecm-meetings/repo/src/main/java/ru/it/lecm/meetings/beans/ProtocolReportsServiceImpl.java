package ru.it.lecm.meetings.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.reports.api.ReportsManager;

/**
 * @author snovikov
 */
@Deprecated
public class ProtocolReportsServiceImpl extends BaseBean implements ProtocolReportsService {
    private ReportsManager reportsManager;

    public void setReportsManager(ReportsManager reportsManager) {
        this.reportsManager = reportsManager;
    }

    @Override
    public NodeRef generateDocumentReport(final String reportCode, final String templateCode, final String documentRef) {
        NodeRef documentNodeRef = new NodeRef(documentRef);
        String categoryName = ProtocolService.ATTACHMENT_CATEGORIES_MAP.get(ProtocolService.ATTACHMENT_CATEGORIES.DOCUMENT);
        return reportsManager.buildReportAndAttachToDocumentCategory(documentNodeRef, reportCode, templateCode, categoryName);
    }

    @Override
    public NodeRef getServiceRootFolder() {
        return null;
    }
}
