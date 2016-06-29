package ru.it.lecm.workflow.reservation.webscripts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import ru.it.lecm.workflow.Utils;
import ru.it.lecm.workflow.reservation.ReservationAspectsModel;

/**
 *
 * @author vlevin
 */
public class GetReservationTaskMessageByNodeRef extends DeclarativeWebScript {

	private WorkflowService workflowService;
	private NodeService nodeService;


	public void setWorkflowService(WorkflowService workflowService) {
		this.workflowService = workflowService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		NodeRef documentRef = null;
		
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

		String reservationTaskMessage = (String) nodeService.getProperty(documentRef, ReservationAspectsModel.PROP_RESERVE_TASK_MESSAGE);
		
		try {
			resultJSON.put("nodeRef", documentRef.toString());
			resultJSON.put("reservationTaskMessage", reservationTaskMessage);
		} catch (JSONException ex) {
			throw new WebScriptException("Error formin JSON response", ex);
		}

		result.put("result", resultJSON);

		return result;
	}

}
