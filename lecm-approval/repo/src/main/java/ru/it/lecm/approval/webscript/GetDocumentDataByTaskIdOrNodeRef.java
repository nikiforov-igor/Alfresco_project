package ru.it.lecm.approval.webscript;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import ru.it.lecm.approval.ApprovalListServiceAbstract;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.documents.beans.DocumentService;

/**
 *
 * @author vlevin
 */
public class GetDocumentDataByTaskIdOrNodeRef extends DeclarativeWebScript {

	private WorkflowService workflowService;
	private NodeService nodeService;
	private ApprovalListServiceAbstract approvalListService;
	private DictionaryService dictionaryService;

	public void setWorkflowService(WorkflowService workflowService) {
		this.workflowService = workflowService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setApprovalListService(ApprovalListServiceAbstract approvalListService) {
		this.approvalListService = approvalListService;
	}

	public void setDictionaryService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		NodeRef documentRef = null;
		Map<String, Object> result = new HashMap<String, Object>();
		String taskID = req.getParameter("taskID");
		String nodeRefStr = req.getParameter("nodeRef");
		if ((nodeRefStr == null || nodeRefStr.isEmpty()) && (taskID != null && !taskID.isEmpty())) {
			List<NodeRef> packageContents = workflowService.getPackageContents(taskID);
			for (NodeRef node : packageContents) {
				if (dictionaryService.isSubClass(nodeService.getType(node), DocumentService.TYPE_BASE_DOCUMENT)) {
					documentRef = node;
					break;
				}
			}
		} else if ((taskID == null || taskID.isEmpty()) && (nodeRefStr != null && !nodeRefStr.isEmpty())) {
			if (NodeRef.isNodeRef(nodeRefStr)) {
				documentRef = new NodeRef(nodeRefStr);
			}
		} else {
			throw new WebScriptException("Task ID or NodeRef must be supplied");
		}


		if (documentRef == null) {
			throw new WebScriptException("No document attached");
		}

		String presentString = (String) nodeService.getProperty(documentRef, DocumentService.PROP_PRESENT_STRING);

		result.put("presentString", presentString);
		result.put("presentStringWithLink", approvalListService.wrapperLink(documentRef, presentString, BaseBean.DOCUMENT_LINK_URL));

		return result;
	}
}
