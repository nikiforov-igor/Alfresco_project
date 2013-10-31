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

	public Scriptable getTableTotalRow(String tableDataRef) {
		ParameterCheck.mandatory("tableDataRef", tableDataRef);

		NodeRef tableDataNodeRef = new NodeRef(tableDataRef);
		if (nodeService.exists(tableDataNodeRef) && documentTableService.isDocumentTableData(tableDataNodeRef)) {
			List<NodeRef> totalRows = documentTableService.getTableDataTotalRows(tableDataNodeRef);
			if (totalRows != null) {
				return createScriptable(totalRows);
			}
		}
		return null;
	}

    public boolean onMoveTableRowUp(String tableNodeRef, String assocType) {
        org.alfresco.util.ParameterCheck.mandatory("tableNodeRef", tableNodeRef);
        org.alfresco.util.ParameterCheck.mandatory("assocType", assocType);
        NodeRef tableRef = new NodeRef(tableNodeRef);

        return this.documentTableService.isMoveTableRowUp(tableRef, assocType);
    }

    public boolean onMoveTableRowDown(String tableNodeRef, String assocType) {
        org.alfresco.util.ParameterCheck.mandatory("tableNodeRef", tableNodeRef);
        org.alfresco.util.ParameterCheck.mandatory("assocType", assocType);
        NodeRef tableRef = new NodeRef(tableNodeRef);

        return this.documentTableService.isMoveTableRowDown(tableRef, assocType);
    }
}
