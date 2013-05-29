package ru.it.lecm.statemachine.evaluators;

import org.alfresco.web.evaluator.BaseEvaluator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.springframework.extensions.webscripts.ScriptRemote;
import org.springframework.extensions.webscripts.connector.Response;
import org.springframework.extensions.webscripts.connector.ResponseStatus;

/**
 * User: AIvkin
 * Date: 29.05.13
 * Time: 14:41
 */
public class FinalStateMachineStatusEvaluator extends BaseEvaluator {
	private final static Log logger = LogFactory.getLog(FinalStateMachineStatusEvaluator.class);
	private ScriptRemote scriptRemote;

	public void setScriptRemote(ScriptRemote scriptRemote) {
		this.scriptRemote = scriptRemote;
	}

	@Override
	public boolean evaluate(JSONObject jsonObject) {
		String nodeRef = getArg("nodeRef");

		String url = "/lecm/statemachine/isFinal?nodeRef=" + nodeRef;
		Response response = scriptRemote.connect("alfresco").get(url);
		try {
			if (response.getStatus().getCode() == ResponseStatus.STATUS_OK) {
				org.json.JSONObject resultJson = new org.json.JSONObject(response.getResponse());
				return resultJson.getBoolean("isFinal");
			} else {
				logger.warn("Cannot get isFinal document from server");
			}
		} catch (JSONException e) {
			logger.warn("Cannot get isFinal document from server", e);
		}
		return true;
	}
}
