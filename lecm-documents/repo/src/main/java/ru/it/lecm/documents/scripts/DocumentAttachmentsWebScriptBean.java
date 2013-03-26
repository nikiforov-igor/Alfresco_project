package ru.it.lecm.documents.scripts;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.util.ParameterCheck;
import org.mozilla.javascript.Scriptable;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.documents.beans.DocumentAttachmentsService;

import java.util.List;

/**
 * User: AIvkin
 * Date: 06.03.13
 * Time: 12:08
 */
public class DocumentAttachmentsWebScriptBean extends BaseWebScript {
    private DocumentAttachmentsService documentAttachmentsService;
    protected NodeService nodeService;

    public void setDocumentAttachmentsService(DocumentAttachmentsService documentAttachmentsService) {
        this.documentAttachmentsService = documentAttachmentsService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public ScriptNode getRootFolder(String documentNodeRef) {
        ParameterCheck.mandatory("documentNodeRef", documentNodeRef);

        NodeRef documentRef = new NodeRef(documentNodeRef);

        if (this.nodeService.exists(documentRef)) {
            NodeRef attachmentsRoot = this.documentAttachmentsService.getRootFolder(documentRef);
            if (attachmentsRoot != null) {
                return new ScriptNode(attachmentsRoot, this.serviceRegistry, getScope());
            }
        }
        return null;
    }

    public Scriptable getCategories(String documentNodeRef) {
        ParameterCheck.mandatory("documentNodeRef", documentNodeRef);

        NodeRef documentRef = new NodeRef(documentNodeRef);
        if (this.nodeService.exists(documentRef)) {
            List<NodeRef> categories = this.documentAttachmentsService.getCategories(documentRef);
            return createScriptable(categories);
        }
        return null;
    }

}
