package ru.it.lecm.signed.docflow.webscripts;

import java.util.HashMap;
import java.util.Map;
import org.alfresco.service.cmr.repository.NodeRef;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import ru.it.lecm.signed.docflow.api.SignedDocflowBean;

/**
 *
 * @author vlevin
 */
public class IsSignableWebscript extends DeclarativeWebScript {

	private SignedDocflowBean signedDocflowService;

	public void setSignedDocflowService(SignedDocflowBean signedDocflowService) {
		this.signedDocflowService = signedDocflowService;
	}

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		Map<String, Object> result = new HashMap<String, Object>();
		String nodeRefStr = req.getParameter("nodeRef");
		if (nodeRefStr != null && !nodeRefStr.isEmpty()) {
			boolean isSignable = signedDocflowService.isSignable(new NodeRef(nodeRefStr));
			JSONObject json = new JSONObject();
			try {
				json.put("isSignable", isSignable);
			} catch (JSONException ex) {
				throw new WebScriptException("Error forming JSONObject", ex);
			}
			result.put("result", json);
		} else {
			throw new WebScriptException("nodeRef must be supplied!");
		}
		return result;
	}
}
