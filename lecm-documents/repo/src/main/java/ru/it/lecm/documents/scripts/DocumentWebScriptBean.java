package ru.it.lecm.documents.scripts;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.util.ParameterCheck;
import ru.it.lecm.documents.beans.DocumentService;

/**
 * User: orakovskaya
 * Date: 12.03.13
 */
public class DocumentWebScriptBean extends BaseScopableProcessorExtension {
    private DocumentService documentService;
    private NodeService nodeService;

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }
    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public String getRating(String documentNodeRef) {
        ParameterCheck.mandatory("documentNodeRef", documentNodeRef);

        NodeRef documentRef = new NodeRef(documentNodeRef);
        if (this.nodeService.exists(documentRef)) {
            return this.documentService.getRating(documentRef);
        }
        return null;
    }
    public Integer getRatedPersonCount(String documentNodeRef) {
        ParameterCheck.mandatory("documentNodeRef", documentNodeRef);

        NodeRef documentRef = new NodeRef(documentNodeRef);
        if (this.nodeService.exists(documentRef)) {
            return this.documentService.getRatedPersonCount(documentRef);
        }
        return null;
    }
    public Integer getMyRating(String documentNodeRef) {
        ParameterCheck.mandatory("documentNodeRef", documentNodeRef);

        NodeRef documentRef = new NodeRef(documentNodeRef);
        if (this.nodeService.exists(documentRef)) {
            return this.documentService.getMyRating(documentRef);
        }
        return null;
    }
    public Integer setMyRating(String documentNodeRef, String rating) {
        ParameterCheck.mandatory("documentNodeRef", documentNodeRef);

        NodeRef documentRef = new NodeRef(documentNodeRef);
        if (this.nodeService.exists(documentRef)) {
            return this.documentService.setMyRating(documentRef, Integer.parseInt(rating));
        }
        return null;
    }
}
