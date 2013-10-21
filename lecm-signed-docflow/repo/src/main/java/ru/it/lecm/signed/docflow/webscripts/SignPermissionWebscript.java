package ru.it.lecm.signed.docflow.webscripts;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 *
 * @author ikhalikov
 */
public class SignPermissionWebscript extends DeclarativeWebScript {

	private static final String SIGN_PERMISSION_ACTION = "signPermission";
	private static final String HAS_ASPECT_ACTION = "hasAspect";
	private static final String HAS_PROPERTIES_ACTION = "hasProperties";
	private NodeService nodeService;

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}
	private OrgstructureBean orgstructureService;

	Map<String, Object> executeSignPermissionAction(WebScriptRequest req) {
		Map<String, Object> result = new HashMap<String, Object>();
		String roleId = req.getParameter("roleId");
		String userName = req.getParameter("userName");
		NodeRef employee = orgstructureService.getEmployeeByPerson(userName);

		boolean hasBR = orgstructureService.isEmployeeHasBusinessRole(employee, roleId, true, true);
		boolean isBoss = orgstructureService.isBoss(employee, true);
		JSONObject jsonRes = new JSONObject();
		try {
//			jsonRes.put("success", hasBR || isBoss);
                        jsonRes.put("success", hasBR);
		} catch (JSONException ex) {
			Logger.getLogger(SignPermissionWebscript.class.getName()).log(Level.SEVERE, null, ex);
		}
		result.put("result", jsonRes);
		return result;
	}

	Map<String, Object> executeHasPropertiesAction(WebScriptRequest req) throws JSONException{
		Map<String, Object> result = new HashMap<String, Object>();
		JSONObject jsonRes = new JSONObject();
		Map<String, Object> properties = new HashMap<String, Object>();
		NodeRef node = new NodeRef(req.getParameter("nodeRef"));
		Serializable lockOwner = nodeService.getProperty(node, ContentModel.PROP_LOCK_OWNER);
		Serializable lockType = nodeService.getProperty(node, ContentModel.PROP_LOCK_TYPE);
		jsonRes.put("result", (lockOwner != null && lockType != null));
		result.put("result", jsonRes);
		return result;
	}

	Map<String, Object> executeHasAspectAction(WebScriptRequest req) throws JSONException {
		Map<String, Object> result = new HashMap<String, Object>();
		//TODO: Сделать поддержку для передачи префикса модели
		QName aspect = QName.createQName("{http://www.alfresco.org/model/content/1.0}"+req.getParameter("aspect"));
		NodeRef nodeRef = new NodeRef(req.getParameter("nodeRef"));
		JSONObject jsonRes = new JSONObject();

		if(nodeService.getAspects(nodeRef).contains(aspect)){
			jsonRes.put("success", true);
		} else {
			jsonRes.put("success", false);
		}
		result.put("result", jsonRes);
		return result;
	}

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		Map<String, Object> result = new HashMap<String, Object>();
		String action = req.getParameter("action");

		if(action.equals(SIGN_PERMISSION_ACTION)){
			return executeSignPermissionAction(req);
		}

		if(action.equals(HAS_ASPECT_ACTION)){
			try {
				return executeHasAspectAction(req);
			} catch (JSONException ex) {
				Logger.getLogger(SignPermissionWebscript.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		if(action.equals(HAS_PROPERTIES_ACTION)){
			try {
				return executeHasPropertiesAction(req);
			} catch (JSONException ex) {
				Logger.getLogger(SignPermissionWebscript.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return result;
	}
}
