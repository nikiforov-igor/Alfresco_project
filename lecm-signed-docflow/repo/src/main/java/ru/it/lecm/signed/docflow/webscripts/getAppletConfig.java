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
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.signed.docflow.api.SignedDocflow;

/**
 *
 * @author ikhalikov
 */
public class getAppletConfig extends DeclarativeWebScript {

	private final static Logger logger = LoggerFactory.getLogger(getAppletConfig.class);
	private SignedDocflow signedDocflowService;
	private OrgstructureBean orgstructureService;
	private NodeService nodeService;

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public void setSignedDocflowService(SignedDocflow signedDocflowService) {
		this.signedDocflowService = signedDocflowService;
	}

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, String> config = new HashMap<String, String>();
		NodeRef orgRef = orgstructureService.getOrganization();
		String licKey = (String) nodeService.getProperty(orgRef, signedDocflowService.PROP_APPLET_LIC_KEY);
		String licCert = (String) nodeService.getProperty(orgRef, signedDocflowService.PROP_APPLET_CERT);
		String licContainer = (String) nodeService.getProperty(orgRef, signedDocflowService.PROP_APPLET_CONTAINER);
		
		config.put("licKey", licKey);
		config.put("licCert", licCert);
		config.put("storeName", licContainer);

		result.put("result", new JSONObject(config));
		return result;
	}
}
