package ru.it.lecm.workflow.approval.webscript.deprecated;

import java.util.HashMap;
import java.util.Map;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import ru.it.lecm.workflow.approval.api.deprecated.ApprovalResultModel;
import ru.it.lecm.workflow.approval.api.deprecated.ApprovalService;

/**
 *
 * @author vlevin
 */
@Deprecated
public class GetApprovalListDataForDocument extends DeclarativeWebScript {

	private final static Logger logger = LoggerFactory.getLogger(GetApprovalListDataForDocument.class);
	private ApprovalService approvalService;
	private NamespaceService namespaceService;
	private NodeService nodeService;

	public void setApprovalService(ApprovalService approvalService) {
		this.approvalService = approvalService;
	}

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		Map<String, Object> result = new HashMap<String, Object>();
		JSONObject json = new JSONObject();

		String documentNodeRefStr = req.getParameter("documentNodeRef");
		NodeRef documentRef = new NodeRef(documentNodeRefStr);
		NodeRef approvalContainer = approvalService.getOrCreateApprovalFolderContainer(documentRef);

		try {
			json.put("approvalListType", ApprovalResultModel.TYPE_APPROVAL_LIST.toPrefixString(namespaceService));
			json.put("approvalItemType", ApprovalResultModel.TYPE_APPROVAL_ITEM.toPrefixString(namespaceService));
			json.put("approvalContainer", approvalContainer);
			json.put("approvalContainerPath", nodeService.getPath(approvalContainer).toPrefixString(namespaceService));
		} catch (JSONException ex) {
			throw new WebScriptException("Error forming JSON", ex);
		}

		result.put("result", json);
		return result;
	}

}
