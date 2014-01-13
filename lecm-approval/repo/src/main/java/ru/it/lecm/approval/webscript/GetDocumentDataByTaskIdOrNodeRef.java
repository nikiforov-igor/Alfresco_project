package ru.it.lecm.approval.webscript;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import ru.it.lecm.approval.ApprovalListServiceImpl;
import ru.it.lecm.approval.Utils;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.documents.beans.DocumentService;

/**
 *
 * @author vlevin
 */
public class GetDocumentDataByTaskIdOrNodeRef extends DeclarativeWebScript {

	private WorkflowService workflowService;
	private NodeService nodeService;
	private ApprovalListServiceImpl approvalListService;
	private static final String DOCUMENT_DETAILS_URL = "/share/page/document-details";

	public void setWorkflowService(WorkflowService workflowService) {
		this.workflowService = workflowService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setApprovalListService(ApprovalListServiceImpl approvalListService) {
		this.approvalListService = approvalListService;
	}

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		NodeRef documentRef = null;
		String documentURL;
		Map<String, Object> result = new HashMap<String, Object>();
		String taskID = req.getParameter("taskID");
		String nodeRefStr = req.getParameter("nodeRef");
		if ((nodeRefStr == null || nodeRefStr.isEmpty()) && (taskID != null && !taskID.isEmpty())) {
			List<NodeRef> packageContents = workflowService.getPackageContents(taskID);
			documentRef = Utils.getObjectFromPackageContents(packageContents);
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

		if (presentString == null || presentString.length() == 0) {
			presentString = "Согласование";
			documentURL = approvalListService.wrapperLink(documentRef, presentString, DOCUMENT_DETAILS_URL);
		} else {
			documentURL = approvalListService.wrapperLink(documentRef, presentString, BaseBean.DOCUMENT_LINK_URL);
		}

		result.put("nodeRef", documentRef.toString());
		result.put("presentString", presentString);
		result.put("presentStringWithLink", documentURL);

		return result;
	}
}
