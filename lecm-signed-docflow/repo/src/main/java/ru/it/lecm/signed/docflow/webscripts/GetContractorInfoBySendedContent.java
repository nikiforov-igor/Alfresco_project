package ru.it.lecm.signed.docflow.webscripts;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.*;
import ru.it.lecm.signed.docflow.api.SignedDocflowModel;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ikhalikov
 */
public class GetContractorInfoBySendedContent extends DeclarativeWebScript{
	private static final String NODE_REF = "nodeRef";
	private static final String EMPTY_PARAMETER = "GetContragentPropertiesWebscript was called with empty parameter nodeRef";
	private static final String JSON_ERROR = "Something goes bad while processing JSON";

	private NodeService nodeService;

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}
	private final static Logger logger = LoggerFactory.getLogger(GetContractorInfoBySendedContent.class);

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		Map<String, Object> result = new HashMap<String, Object>();
		JSONObject jsonResult = new JSONObject();

		String nodeRefString = req.getParameter(NODE_REF);
		if(nodeRefString == null || nodeRefString.isEmpty()) {
			logger.error(EMPTY_PARAMETER);
			throw new WebScriptException(EMPTY_PARAMETER);
		}

		NodeRef nodeRef = new NodeRef(nodeRefString);
		if(!nodeService.hasAspect(nodeRef, SignedDocflowModel.ASPECT_CONTRACTOR_INTERACTION)) {
			try {
				jsonResult.put("success", false);
				result.put("result", jsonResult);
				return result;
			} catch (JSONException ex) {
				logger.error(JSON_ERROR, ex);
			}
		}

		NodeRef contractorRef = (NodeRef) nodeService.getProperty(nodeRef, SignedDocflowModel.PROP_CONTRACTOR_REF);
		String interactionType = (String) nodeService.getProperty(nodeRef, SignedDocflowModel.PROP_INTERACTION_TYPE);
		String contractorEmail = (String) nodeService.getProperty(nodeRef, SignedDocflowModel.PROP_CONTRACTOR_EMAIL);

		try {
			jsonResult.put("contractorRef", contractorRef);
			jsonResult.put("interactionType", interactionType);
			jsonResult.put("contractorEmail", contractorEmail);

			result.put("result", jsonResult);

			return result;
		} catch (JSONException ex) {
			logger.error(JSON_ERROR, ex);
		}

		JSONObject fail = new JSONObject("success", false);

		result.put("result", fail);
		return result;
	}
}
