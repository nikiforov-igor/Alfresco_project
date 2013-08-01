package ru.it.lecm.signed.docflow.webscripts;

import java.util.HashMap;
import java.util.Map;
import org.alfresco.service.cmr.repository.NodeRef;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import ru.it.lecm.signed.docflow.api.SignedDocflow;

/**
 *
 * @author vlevin
 */
public class SetSignableWebscript extends DeclarativeWebScript {

	private SignedDocflow signedDocflowService;

	public void setSignedDocflowService(SignedDocflow signedDocflowService) {
		this.signedDocflowService = signedDocflowService;
	}

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		Map<String, Object> result = new HashMap<String, Object>();
		String nodeRefStr = req.getParameter("nodeRef");
		String action = req.getParameter("action");
		if (nodeRefStr != null && !nodeRefStr.isEmpty() && action != null && !action.isEmpty()) {
			NodeRef nodeRef = new NodeRef(nodeRefStr);
			if ("true".equals(action)) {
				signedDocflowService.addSignableAspect(nodeRef);
			} else if ("false".equals(action)) {
				signedDocflowService.removeSignableAspect(nodeRef);
			} else {
				throw new WebScriptException("action must be true or false. current action is " + action);
			}

		} else {
			throw new WebScriptException("nodeRef  and action must be supplied!");
		}
		return result;
	}
}
