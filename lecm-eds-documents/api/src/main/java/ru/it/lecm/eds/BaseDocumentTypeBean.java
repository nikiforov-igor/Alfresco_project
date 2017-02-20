package ru.it.lecm.eds;

import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.eds.api.EDSDocumentService;

/**
 * Created by ABurlakov on 20.02.2017.
 */
public abstract class BaseDocumentTypeBean {
    protected NodeService nodeService;
    protected EDSDocumentService edsDocumentService;
    protected DocumentService documentService;
    protected NamespaceService namespaceService;

    public NodeService getNodeService() {
        return this.nodeService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public NamespaceService getNamespaceService() {
        return this.namespaceService;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    public EDSDocumentService getEDSDocumentService() {
        return this.edsDocumentService;
    }

    public void setEdsDocumentService(EDSDocumentService edsDocumentService) {
        this.edsDocumentService = edsDocumentService;
    }

    public DocumentService getDocumentService() {
        return this.documentService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }
}