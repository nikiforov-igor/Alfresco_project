package ru.it.lecm.reports.utils;

import org.alfresco.repo.admin.SysAdminParams;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.UrlUtil;
import ru.it.lecm.documents.beans.DocumentService;

/**
 * User: dbashmakov
 * Date: 28.02.2017
 * Time: 14:22
 */
public class GetDocumentUrl {
    private static SysAdminParams sysAdminParams;
    private static DocumentService documentService;
    private static NodeService nodeService;

    public void setNodeService(NodeService nodeService) {
        GetDocumentUrl.nodeService = nodeService;
    }

    public void setSysAdminParams(SysAdminParams sysAdminParams) {
        GetDocumentUrl.sysAdminParams = sysAdminParams;
    }

    public void setDocumentService(DocumentService documentService) {
        GetDocumentUrl.documentService = documentService;
    }

    public static String getDocumentLink(String nodeRef) {
        QName docType = DocumentService.TYPE_BASE_DOCUMENT;
        if (NodeRef.isNodeRef(nodeRef)) {
            docType = nodeService.getType(new NodeRef(nodeRef));
        }
        return UrlUtil.getShareUrl(sysAdminParams) + "/page/" + documentService.getViewUrl(docType) + "?nodeRef=" + nodeRef;
    }
}
