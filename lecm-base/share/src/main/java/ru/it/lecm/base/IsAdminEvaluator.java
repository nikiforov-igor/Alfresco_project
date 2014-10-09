package ru.it.lecm.base;

import org.alfresco.web.evaluator.BaseEvaluator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.springframework.extensions.surf.util.URLEncoder;
import org.springframework.extensions.webscripts.ScriptRemote;
import org.springframework.extensions.webscripts.connector.Response;
import org.springframework.extensions.webscripts.connector.ResponseStatus;

/**
 * User: AIvkin
 * Date: 30.05.13
 * Time: 10:03
 */
public class IsAdminEvaluator extends BaseEvaluator {
	private final static Log logger = LogFactory.getLog(IsAdminEvaluator.class);
	private ScriptRemote scriptRemote;

	public void setScriptRemote(ScriptRemote scriptRemote) {
		this.scriptRemote = scriptRemote;
	}

	@Override
	public boolean evaluate(JSONObject jsonObject) {
		String login = getUserId();
		String url = "/lecm/security/api/isAdmin?login=" + URLEncoder.encodeUri(login);
		Response response = scriptRemote.connect("alfresco").get(url);
		try {
			if (response.getStatus().getCode() == ResponseStatus.STATUS_OK) {
				org.json.JSONObject resultJson = new org.json.JSONObject(response.getResponse());
				return resultJson.getBoolean("isAdmin");
			} else {
				logger.warn("Cannot get isAdmin from server");
			}
		} catch (JSONException e) {
			logger.warn("Cannot get isAdmin from server", e);
		}
		return false;
	}
}
