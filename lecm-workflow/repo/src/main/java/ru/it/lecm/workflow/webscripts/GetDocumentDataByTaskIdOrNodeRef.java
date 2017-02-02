package ru.it.lecm.workflow.webscripts;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.*;
import ru.it.lecm.base.beans.LecmURLService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.workflow.Utils;
import ru.it.lecm.workflow.beans.WorkflowFoldersServiceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author vlevin
 */
public class GetDocumentDataByTaskIdOrNodeRef extends DeclarativeWebScript {

	private WorkflowService workflowService;
	private NodeService nodeService;
	private WorkflowFoldersServiceImpl workflowFoldersService;
	private DocumentService documentService;
	private LecmURLService urlService;

	public void setWorkflowService(WorkflowService workflowService) {
		this.workflowService = workflowService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setWorkflowFoldersService(WorkflowFoldersServiceImpl workflowFoldersService) {
		this.workflowFoldersService = workflowFoldersService;
	}

	public void setUrlService(LecmURLService urlService) {
		this.urlService = urlService;
	}

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		NodeRef documentRef = null;
		String documentURL;
		Map<String, Object> result = new HashMap<String, Object>();
		JSONObject resultJSON = new JSONObject();
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
			presentString = "Документ";
			documentURL = workflowFoldersService.wrapperLink(documentRef, presentString, urlService.getDetailsLinkUrl());
		} else {
			documentURL = workflowFoldersService.wrapperLink(documentRef, presentString, documentService.getDocumentUrl(documentRef));
		}

		try {
			resultJSON.put("nodeRef", documentRef.toString());
			resultJSON.put("presentString", presentString);
			resultJSON.put("presentStringWithLink", documentURL);
		} catch (JSONException ex) {
			throw new WebScriptException("Error formin JSON response", ex);
		}

		result.put("result", resultJSON);

		return result;
	}

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}
}
