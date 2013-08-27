/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.signed.docflow.webscripts;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import ru.it.lecm.signed.docflow.api.SignedDocflow;
import ru.it.lecm.signed.docflow.api.SignedDocflowModel;

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
		JSONObject jsonRes = new JSONObject();
		String nodeRefString = req.getParameter(NODE_REF);
		if(nodeRefString.isEmpty()){
			logger.error(EMPTY_PARAMETER);
			throw new WebScriptException(EMPTY_PARAMETER);
		}
		NodeRef nodeRef = new NodeRef(nodeRefString);
		
		if(!nodeService.hasAspect(nodeRef, SignedDocflowModel.ASPECT_CONTRACTOR_INTERACTION)){
			try {
				jsonRes.put("success", false);
				result.put("result", jsonRes);
				return result;
			} catch (JSONException ex) {
				logger.error(JSON_ERROR, ex);
			}
		}
		
		NodeRef contractorRef = (NodeRef) nodeService.getProperty(nodeRef, SignedDocflowModel.PROP_CONTRACTOR_REF);
		String interactionType = (String) nodeService.getProperty(nodeRef, SignedDocflowModel.PROP_INTERACTION_TYPE);
		String contractorEmail = (String) nodeService.getProperty(nodeRef, SignedDocflowModel.PROP_CONTRACTOR_EMAIL);
		
		try {
				jsonRes.put("contractorRef", contractorRef);
				jsonRes.put("interactionType", interactionType);
				jsonRes.put("contractorEmail", contractorEmail);
				result.put("result", jsonRes);
				return result;
			} catch (JSONException ex) {
				logger.error(JSON_ERROR, ex);
			}
		JSONObject fail = new JSONObject("success", false);
		result.put("result", fail);
		return result;	
	}
	
}
