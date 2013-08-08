package ru.it.lecm.signed.docflow.webscripts;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
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
import org.springframework.util.ReflectionUtils;
import ru.it.lecm.base.DeclarativeWebScriptHelper;
import ru.it.lecm.signed.docflow.UnicloudService;
import ru.it.lecm.signed.docflow.api.SignedDocflow;
import ru.it.lecm.signed.docflow.model.UnicloudData;

/**
 *
 * @author VLadimir Malygin
 * @since 31.07.2013 17:52:49
 * @see <p>mailto: <a href="mailto:vmalygin@it.ru">vmalygin@it.ru</a></p>
 */
public class UnicloudWebscript extends DeclarativeWebScript {

	private final static Logger logger = LoggerFactory.getLogger(UnicloudWebscript.class);
	private final static String ACTION_DEF = "action";

	private SignedDocflow signedDocflowService;
	private UnicloudService unicloudService;

	public void setSignedDocflowService(SignedDocflow signedDocflowService) {
		this.signedDocflowService = signedDocflowService;
	}

	public void setUnicloudService(UnicloudService unicloudService) {
		this.unicloudService = unicloudService;
	}

	public JSONObject authenticateByCertificate(final JSONObject json) {
		String guidSign;
		String timestamp;
		String timestampSign;

		try {
			guidSign = json.getString("guidSign");
			timestamp = json.getString("timestamp");
			timestampSign = json.getString("timestampSign");
		} catch (JSONException ex) {
			String msg = "Can't parse incoming json";
			logger.error("{}. Caused by: {}", msg, ex.getMessage());
			throw new IllegalArgumentException(msg, ex);
		}
		UnicloudData result = unicloudService.authenticateByCertificate(guidSign, timestamp, timestampSign);
		return new JSONObject(result.getProperties());
	}

	public JSONObject verifySignature(final JSONObject json) {
		String contentRef;
		String signature;
		try {
			contentRef = json.getString("contentRef");
			signature = json.getString("signature");
		} catch (JSONException ex) {
			String msg = "Can't parse incoming json";
			logger.error("{}. Caused by: {}", msg, ex.getMessage());
			throw new IllegalArgumentException(msg, ex);
		}
		UnicloudData result = unicloudService.verifySignature(contentRef, signature);
		return new JSONObject(result.getProperties());
	}

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {

		final Map<String, String> templateArgs = req.getServiceMatch().getTemplateVars();
		final String action = templateArgs.get(ACTION_DEF);

		final Content content = req.getContent();
		if (content == null) {
			String msg = "UnicloudWebscript was called with empty json content";
			logger.error("{}. Executed action: {}", msg, action);
			throw new WebScriptException(String.format("%s. Executed action: %s", msg, action));
		}

		JSONObject requestJSON = DeclarativeWebScriptHelper.getJsonContent(content);
		JSONObject responseJSON;

		try {
			Method actionMethod = ReflectionUtils.findMethod(getClass(), action, JSONObject.class);
			if (actionMethod != null) {
				signedDocflowService.addAttributesToPersonalData();
				responseJSON = (JSONObject) ReflectionUtils.invokeMethod(actionMethod, this, requestJSON);
			} else {
				throw new WebScriptException(String.format("There is no method %s(JSONObject json) in UnicloudService class", action));
			}
		} catch(Exception ex) {
			String msg = "Can't execute action";
			logger.error("{} {}. Caused by: {}", new Object[] {msg, action, ex.getMessage()});
			throw new WebScriptException(String.format("%s %s", msg, action), ex);
		}

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", responseJSON);
		return result;
	}
}
