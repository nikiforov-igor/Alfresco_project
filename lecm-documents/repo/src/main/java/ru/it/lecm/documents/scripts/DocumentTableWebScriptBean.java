package ru.it.lecm.documents.scripts;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.mozilla.javascript.Scriptable;
import org.springframework.extensions.surf.util.ParameterCheck;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.documents.beans.DocumentTableService;

import java.util.List;

/**
 * User: AIvkin
 * Date: 18.10.13
 * Time: 13:57
 */
public class DocumentTableWebScriptBean extends BaseWebScript {
	private DocumentTableService documentTableService;
	protected NodeService nodeService;
	private NamespaceService namespaceService;

	public void setDocumentTableService(DocumentTableService documentTableService) {
		this.documentTableService = documentTableService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
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

	public Scriptable getTableTotalRow(String documentNodeRef, String tableDataType, String tableDataAssocType) {
		ParameterCheck.mandatory("documentNodeRef", documentNodeRef);
		ParameterCheck.mandatory("tableDataType", tableDataType);
		ParameterCheck.mandatory("tableDataAssocType", tableDataAssocType);

		NodeRef documentRef = new NodeRef(documentNodeRef);
		if (nodeService.exists(documentRef)) {
			QName tableDataTypeQName = QName.createQName(tableDataType, namespaceService);
			QName tableDataAssocTypeQName = QName.createQName(tableDataAssocType, namespaceService);

			List<NodeRef> totalRows = documentTableService.getTableDataTotalRows(documentRef, tableDataTypeQName, tableDataAssocTypeQName, false);
			if (totalRows != null) {
				return createScriptable(totalRows);
			}
		}
		return null;
	}
}
