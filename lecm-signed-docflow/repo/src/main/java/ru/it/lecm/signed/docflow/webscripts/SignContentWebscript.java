package ru.it.lecm.signed.docflow.webscripts;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.alfresco.model.ContentModel;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ISO8601DateFormat;
import org.json.JSONArray;
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
import ru.it.lecm.signed.docflow.api.SignedDocflowModel;

/**
 *
 * @author vlevin
 */
public class SignContentWebscript extends DeclarativeWebScript {

	private SignedDocflow signedDocflowService;
	private final static Logger logger = LoggerFactory.getLogger(SignContentWebscript.class);
	private final static QName[] propertiesToParse = {
		SignedDocflowModel.PROP_OWNER,
		SignedDocflowModel.PROP_OWNER_POSITION,
		SignedDocflowModel.PROP_OWNER_ORGANIZATION,
		SignedDocflowModel.PROP_SIGNING_DATE,
		SignedDocflowModel.PROP_SERIAL_NUMBER,
		SignedDocflowModel.PROP_VALID_FROM,
		SignedDocflowModel.PROP_VALID_THROUGH,
		SignedDocflowModel.PROP_CA,
		ContentModel.PROP_CONTENT,
		SignedDocflowModel.ASSOC_SIGN_TO_CONTENT,
		SignedDocflowModel.PROP_CERT_FINGERPRINT,
		SignedDocflowModel.PROP_CERT_FINGERPRINT
	};

	public void setSignedDocflowService(SignedDocflow signedDocflowService) {
		this.signedDocflowService = signedDocflowService;
	}

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		Map<String, Object> result = new HashMap<String, Object>();
		JSONObject jsonResponse;
		JSONArray jsonResponseArray = new JSONArray();
		final Content content = req.getContent();
		if (content == null) {
			logger.error("SignContentWebscript was called with empty json content");
			throw new WebScriptException("SignContentWebscript was called with empty json content");
		}
		JSONArray jsonRequest = DeclarativeWebScriptHelper.getJsonArrayContent(content);
		for (int i = 0; i < jsonRequest.length(); i++) {
			Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
			for (QName property : propertiesToParse) {
				try {
					Serializable value;
					if (SignedDocflowModel.PROP_VALID_FROM.equals(property)
							|| SignedDocflowModel.PROP_VALID_THROUGH.equals(property)
							|| SignedDocflowModel.PROP_SIGNING_DATE.equals(property)) {
						value = ISO8601DateFormat.parse(jsonRequest.getJSONObject(i).getString(property.getPrefixString()));
					} else {
						value = jsonRequest.getJSONObject(i).getString(property.getPrefixString());
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
			jsonResponseArray.put(jsonResponse);
		}
		result.put("result", jsonResponseArray);
		return result;
	}
}
