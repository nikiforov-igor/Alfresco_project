package ru.it.lecm.documents.evaluators;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.ScriptRemote;
import org.springframework.extensions.webscripts.connector.Response;
import org.springframework.extensions.webscripts.connector.ResponseStatus;

/**
 *
 * @author snovikov
 */
public class EvaluatorsUtil {
	private final static Log logger = LogFactory.getLog(EvaluatorsUtil.class);
	private ScriptRemote scriptRemote;

	public void setScriptRemote(ScriptRemote scriptRemote) {
		this.scriptRemote = scriptRemote;
	}

	public boolean hasPermission(String nodeRef, String permission, String aspect, String user){
		StringBuilder paramsBuilder = new StringBuilder();
		paramsBuilder.append("nodeRef=").append(nodeRef).append("&permission=").append(permission);
		paramsBuilder.append("&aspect=").append(aspect).append("&user=").append(user);
		String url = "/lecm/documents/isEmpHasPermToReadAttachment?"+paramsBuilder.toString();

		Response response = scriptRemote.connect("alfresco").get(url);

		if (response.getStatus().getCode() == ResponseStatus.STATUS_OK){
			String res = response.getResponse();
			return Boolean.valueOf(res);
		}

		return false;
	}

	public boolean checkReadOnlyCategory(String nodeRef){
		String url = "/lecm/document/attachments/api/getCategoryByAttachment?nodeRef="+nodeRef;

		Response response = scriptRemote.connect("alfresco").get(url);

		try{
			if (response.getStatus().getCode() == ResponseStatus.STATUS_OK) {
				JSONObject json = new JSONObject(response.getResponse());
				return json.has("isReadOnly") && json.getBoolean("isReadOnly");
			}else{
				logger.warn("Cannot get result from server");
			}
		}
		 catch (JSONException ex) {
			logger.warn("Cannot get result from server", ex);
		}

		return true;
	}

	public boolean hasPermissionOnAttachment(String login, String nodeRef, String permission) {
		return hasPermission(nodeRef, permission, "lecm-document-aspects:lecm-attachment", login);
	}
}
