package ru.it.lecm.signed.docflow.webscripts;

import java.util.HashMap;
import java.util.Map;
import org.alfresco.service.cmr.repository.NodeRef;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import ru.it.lecm.signed.docflow.ReceiveContentByEmailService;
import ru.it.lecm.signed.docflow.UnicloudService;
import ru.it.lecm.signed.docflow.model.ReceiveDocumentData;

/**
 *
 * @author vlevin
 */
public class GetSignedContentFromPartnerWebscript extends DeclarativeWebScript {

	private final static Logger logger = LoggerFactory.getLogger(GetSignedContentFromPartnerWebscript.class);

	private ReceiveContentByEmailService receiveContentByEmailService;
	private UnicloudService unicloudService;

	public void setReceiveContentByEmailService(ReceiveContentByEmailService receiveContentByEmailService) {
		this.receiveContentByEmailService = receiveContentByEmailService;
	}

	public void setUnicloudService(UnicloudService unicloudService) {
		this.unicloudService = unicloudService;
	}

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		String nodeRef = req.getParameter("nodeRef");
		String method = req.getParameter("method");
		final NodeRef contentNodeRef = new NodeRef(nodeRef);
		if (nodeRef == null || method == null) {
			String msg = "GetSignedContentFromPartnerWebscript was called with empty parameter";
			logger.error(msg);
			throw new WebScriptException(msg);
		}

		JSONObject jsonResult;
		if ("email".equalsIgnoreCase(method)) {
			Map<String, Object> signaturesForContentByEmail = receiveContentByEmailService.getSignaturesForContentByEmail(contentNodeRef);
			jsonResult = new JSONObject(signaturesForContentByEmail);
		} else if ("specop".equalsIgnoreCase(method)) {
			ReceiveDocumentData receiveDocumentData = unicloudService.receiveDocuments(contentNodeRef);
			jsonResult = new JSONObject(receiveDocumentData.getProperties());
		} else {
			String message = String.format("GetSignedContentFromPartnerWebscript was called with unknown method '%s'", method);
			logger.error(message);
			throw new WebScriptException(message);
		}

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", jsonResult);
		return result;
	}
}
