package ru.it.lecm.documents.scripts;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.util.ParameterCheck;
import ru.it.lecm.documents.beans.DocumentAttachmentsService;

/**
 * User: AIvkin
 * Date: 06.03.13
 * Time: 12:08
 */
public class DocumentAttachmentsWebScriptBean extends BaseScopableProcessorExtension {
    private DocumentAttachmentsService documentAttachmentsService;
    private ServiceRegistry serviceRegistry;
    protected NodeService nodeService;

    public void setDocumentAttachmentsService(DocumentAttachmentsService documentAttachmentsService) {
        this.documentAttachmentsService = documentAttachmentsService;
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public ScriptNode getAttachmentsRootFolder(String documentNodeRef) {
        ParameterCheck.mandatory("documentNodeRef", documentNodeRef);

        NodeRef documentRef = new NodeRef(documentNodeRef);

        if (this.nodeService.exists(documentRef)) {
            NodeRef attachmentsRoot = this.documentAttachmentsService.getAttacmentRootFolder(documentRef);
            if (attachmentsRoot != null) {
                return new ScriptNode(attachmentsRoot, this.serviceRegistry, getScope());
            }
        }
        return null;
    }
}
