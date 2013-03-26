package ru.it.lecm.documents.scripts;


import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.mozilla.javascript.Scriptable;
import org.springframework.extensions.surf.util.ParameterCheck;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.documents.beans.DocumentConnectionService;

import java.util.List;

/**
 * User: AIvkin
 * Date: 18.02.13
 * Time: 14:02
 */
public class DocumentConnectionWebScriptBean extends BaseWebScript {
	private DocumentConnectionService documentConnectionService;
	protected NodeService nodeService;

	public void setDocumentConnectionService(DocumentConnectionService documentConnectionService) {
		this.documentConnectionService = documentConnectionService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public ScriptNode getRootFolder(String documentNodeRef) {
		org.alfresco.util.ParameterCheck.mandatory("documentNodeRef", documentNodeRef);

		NodeRef documentRef = new NodeRef(documentNodeRef);

		if (this.nodeService.exists(documentRef)) {
			NodeRef attachmentsRoot = this.documentConnectionService.getRootFolder(documentRef);
			if (attachmentsRoot != null) {
				return new ScriptNode(attachmentsRoot, this.serviceRegistry, getScope());
			}
		}
		return null;
	}

	public ScriptNode getDefaultConnectionType(String primaryDocumentNodeRef, String connectedDocumentNodeRef) {
		ParameterCheck.mandatory("primaryDocumentNodeRef", primaryDocumentNodeRef);
		ParameterCheck.mandatory("connectedDocumentNodeRef", connectedDocumentNodeRef);

		NodeRef primaryDocumentRef = new NodeRef(primaryDocumentNodeRef);
		NodeRef connectedDocumentRef = new NodeRef(connectedDocumentNodeRef);

		if (this.nodeService.exists(primaryDocumentRef) && this.nodeService.exists(connectedDocumentRef)) {
			NodeRef defaultConnectionType = this.documentConnectionService.getDefaultConnectionType(primaryDocumentRef, connectedDocumentRef);
			if (defaultConnectionType != null) {
				return new ScriptNode(defaultConnectionType, this.serviceRegistry, getScope());
			}
		}
		return null;
	}

	public Scriptable getAvailableConnectionTypes(String primaryDocumentNodeRef, String connectedDocumentNodeRef) {
		ParameterCheck.mandatory("primaryDocumentNodeRef", primaryDocumentNodeRef);
		ParameterCheck.mandatory("connectedDocumentNodeRef", connectedDocumentNodeRef);

		NodeRef primaryDocumentRef = new NodeRef(primaryDocumentNodeRef);
		NodeRef connectedDocumentRef = new NodeRef(connectedDocumentNodeRef);

		if (this.nodeService.exists(primaryDocumentRef) && this.nodeService.exists(connectedDocumentRef)) {
			List<NodeRef> availableConnectionType = this.documentConnectionService.getAvailableConnectionTypes(primaryDocumentRef, connectedDocumentRef);
			if (availableConnectionType != null) {
				return createScriptable(availableConnectionType);
			}
		}
		return null;
	}

    public Scriptable getExistConnectionTypes(String primaryDocumentNodeRef, String connectedDocumentNodeRef) {
        ParameterCheck.mandatory("primaryDocumentNodeRef", primaryDocumentNodeRef);
        ParameterCheck.mandatory("connectedDocumentNodeRef", connectedDocumentNodeRef);

        NodeRef primaryDocumentRef = new NodeRef(primaryDocumentNodeRef);
        NodeRef connectedDocumentRef = new NodeRef(connectedDocumentNodeRef);

        if (this.nodeService.exists(primaryDocumentRef) && this.nodeService.exists(connectedDocumentRef)) {
            List<NodeRef> existConnectionType = this.documentConnectionService.getExistsConnectionTypes(primaryDocumentRef, connectedDocumentRef);
            if (existConnectionType != null) {
                return createScriptable(existConnectionType);
            }
        }
        return null;
    }

	public Scriptable getAllConnectionTypes() {
		List<NodeRef> allConnectionType = this.documentConnectionService.getAllConnectionTypes();
		if (allConnectionType != null) {
			return createScriptable(allConnectionType);
		}
		return null;
	}

	public Scriptable getConnectionsWithDocument(String documentNodeRef) {
		ParameterCheck.mandatory("вocumentNodeRef", documentNodeRef);
		NodeRef documentRef = new NodeRef(documentNodeRef);
		if (this.nodeService.exists(documentRef)) {
			List<NodeRef> connections = this.documentConnectionService.getConnectionsWithDocument(documentRef);
			return createScriptable(connections);
		}
		return null;
	}

}
