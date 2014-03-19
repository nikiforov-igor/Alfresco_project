package ru.it.lecm.workflow.signing.webscript;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import ru.it.lecm.workflow.signing.api.SigningWorkflowModel;
import ru.it.lecm.workflow.signing.api.SigningWorkflowService;

/**
 *
 * @author vlevin
 */
public class GetSigningListDataForDocument extends DeclarativeWebScript {

	private final static Logger logger = LoggerFactory.getLogger(GetSigningListDataForDocument.class);
	private SigningWorkflowService signingWorkflowService;
	private NamespaceService namespaceService;
	private NodeService nodeService;

	public void setSigningWorkflowService(SigningWorkflowService signingWorkflowService) {
		this.signingWorkflowService = signingWorkflowService;
	}

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		NodeRef signingList;
		Map<String, Object> result = new HashMap<String, Object>();
		JSONObject json = new JSONObject();

		String documentNodeRefStr = req.getParameter("documentNodeRef");
		NodeRef signingContainer = signingWorkflowService.getOrCreateSigningFolderContainer(new NodeRef(documentNodeRefStr));
		List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(signingContainer, ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);

		if (childAssocs != null && !childAssocs.isEmpty()) {
			signingList = childAssocs.get(0).getChildRef();
		} else {
			signingList = signingContainer;
		}

		try {
			json.put("signingItemType", SigningWorkflowModel.TYPE_SIGN_RESULT_ITEM.toPrefixString(namespaceService));
			json.put("signingListRef", signingList);
		} catch (JSONException ex) {
			throw new WebScriptException("Error forming JSON", ex);
		}

		result.put("result", json);
		return result;
	}
}
