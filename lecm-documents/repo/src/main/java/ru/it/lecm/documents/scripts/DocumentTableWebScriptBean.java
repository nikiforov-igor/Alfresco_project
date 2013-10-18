package ru.it.lecm.documents.scripts;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.documents.beans.DocumentTableService;

/**
 * User: AIvkin
 * Date: 18.10.13
 * Time: 13:57
 */
public class DocumentTableWebScriptBean extends BaseWebScript {
	private DocumentTableService documentTableService;
	protected NodeService nodeService;

	public void setDocumentTableService(DocumentTableService documentTableService) {
		this.documentTableService = documentTableService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public ScriptNode getRootFolder(String documentNodeRef) {
		org.alfresco.util.ParameterCheck.mandatory("documentNodeRef", documentNodeRef);

		NodeRef documentRef = new NodeRef(documentNodeRef);

		if (this.nodeService.exists(documentRef)) {
			NodeRef root = this.documentTableService.getRootFolder(documentRef);
			if (root != null) {
				return new ScriptNode(root, this.serviceRegistry, getScope());
			}
		}
		return null;
	}
}
