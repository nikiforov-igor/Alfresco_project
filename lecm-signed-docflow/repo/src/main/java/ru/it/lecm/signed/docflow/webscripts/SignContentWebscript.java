package ru.it.lecm.signed.docflow.webscripts;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ISO8601DateFormat;
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
import ru.it.lecm.base.DeclarativeWebScriptHelper;
import ru.it.lecm.signed.docflow.api.SignedDocflow;

/**
 *
 * @author vlevin
 */
public class SignContentWebscript extends DeclarativeWebScript {

	private SignedDocflow signedDocflowService;
	private final static Logger logger = LoggerFactory.getLogger(SignContentWebscript.class);
	private final static QName[] propertiesToParse = {SignedDocflow.PROP_OWNER, SignedDocflow.PROP_OWNER_POSITION, SignedDocflow.PROP_OWNER_ORGANIZATION,
		SignedDocflow.PROP_SIGNING_DATE, SignedDocflow.PROP_SERIAL_NUMBER, SignedDocflow.PROP_VALID_FROM, SignedDocflow.PROP_VALID_THROUGH, SignedDocflow.PROP_CA,
		SignedDocflow.PROP_SIGNATURE_CONTENT, SignedDocflow.ASSOC_SIGN_TO_CONTENT};

	public void setSignedDocflowService(SignedDocflow signedDocflowService) {
		this.signedDocflowService = signedDocflowService;
	}

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		Map<String, Object> result = new HashMap<String, Object>();
		JSONObject jsonResponse;
		final Content content = req.getContent();
		if (content == null) {
			logger.error("SignContentWebscript was called with empty json content");
			throw new WebScriptException("SignContentWebscript was called with empty json content");
		}
		JSONObject jsonRequest = DeclarativeWebScriptHelper.getJsonContent(content);
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
		for (QName property : propertiesToParse) {
			try {
				Serializable value;
				if (SignedDocflow.PROP_VALID_FROM.equals(property) ||
						SignedDocflow.PROP_VALID_THROUGH.equals(property) ||
						SignedDocflow.PROP_SIGNING_DATE.equals(property)) {
					value = ISO8601DateFormat.parse(jsonRequest.getString(property.getPrefixString()));
				} else {
					value = jsonRequest.getString(property.getPrefixString());
				}
				properties.put(property, value);
			} catch (JSONException ex) {
				String errorMessage = String.format("Error getting property %s from JSON request", property.getPrefixString());
				logger.error(errorMessage);
				throw new WebScriptException(errorMessage, ex);
			}
		}

		Map<String, Object> signContentResult = signedDocflowService.signContent(properties);
		jsonResponse = new JSONObject(signContentResult);

		result.put("result", jsonResponse);
		return result;
	}
}
