package ru.it.lecm.signed.docflow.webscripts;

import java.io.IOException;
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
import ru.it.lecm.signed.docflow.UnicloudService;

/**
 *
 * @author VLadimir Malygin
 * @since 31.07.2013 17:52:49
 * @see <p>mailto: <a href="mailto:vmalygin@it.ru">vmalygin@it.ru</a></p>
 */
public class UnicloudWebscript extends DeclarativeWebScript {

	private final static Logger logger = LoggerFactory.getLogger(UnicloudWebscript.class);
	private final static String ACTION_DEF = "action";

	private UnicloudService unicloudService;

	public void setUnicloudService(UnicloudService unicloudService) {
		this.unicloudService = unicloudService;
	}

	private JSONObject getJsonContent(final Content content) {
		JSONObject json;
		try {
			json = new JSONObject(content.getContent());
		} catch(IOException ex) {
			String msg = "Can't read request content as json string";
			logger.error("{}. Caused by: {}", msg, ex.getMessage());
			throw new WebScriptException(msg, ex);
		} catch(JSONException ex) {
			String msg = "Can't marshall request content as json object";
			logger.error("{}. Caused by: {}", msg, ex.getMessage());
			throw new WebScriptException(msg, ex);
		}
		return json;
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

		JSONObject requestJSON = getJsonContent(content);
		JSONObject responseJSON;

		try {
			Method actionMethod = ReflectionUtils.findMethod(unicloudService.getClass(), action, JSONObject.class);
			if (actionMethod != null) {
				responseJSON = (JSONObject) ReflectionUtils.invokeMethod(actionMethod, unicloudService, requestJSON);
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
