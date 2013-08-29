package ru.it.lecm.signed.docflow.webscripts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.alfresco.service.cmr.repository.NodeRef;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import ru.it.lecm.signed.docflow.ReceiveContentByEmailService;

/**
 *
 * @author vlevin
 */
public class GetSignedContentFromPartnerWebscript extends DeclarativeWebScript {
	private ReceiveContentByEmailService receiveContentByEmailService;
	private final static Logger logger = LoggerFactory.getLogger(GetSignedContentFromPartnerWebscript.class);

	public void setReceiveContentByEmailService(ReceiveContentByEmailService receiveContentByEmailService) {
		this.receiveContentByEmailService = receiveContentByEmailService;
	}

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		String nodeRef = req.getParameter("nodeRef");
		String method = req.getParameter("method");
		final NodeRef contentNodeRef = new NodeRef(nodeRef);
		if (nodeRef == null || method == null) {
			logger.error("GetSignedContentFromPartnerWebscript was called with empty parameter");
			throw new WebScriptException("GetSignedContentFromPartnerWebscript was called with empty parameter");
		}

		JSONArray JSONresult;
		if (method.equalsIgnoreCase("email")) {
			List<String> signaturesForContentByEmail = receiveContentByEmailService.getSignaturesForContentByEmail(contentNodeRef);
			JSONresult = new JSONArray(signaturesForContentByEmail);
		} else if (method.equalsIgnoreCase("specop")) {
			JSONresult = new JSONArray();
		} else {
			String message = String.format("GetSignedContentFromPartnerWebscript was called with unknown method '%s'", method);
			logger.error(message);
			throw new WebScriptException(message);
		}
		

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", JSONresult);
		return result;
	}


}
