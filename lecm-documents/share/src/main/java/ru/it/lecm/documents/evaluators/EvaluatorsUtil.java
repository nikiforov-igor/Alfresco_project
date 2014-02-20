package ru.it.lecm.documents.evaluators;

import java.io.UnsupportedEncodingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.ScriptRemote;
import org.springframework.extensions.webscripts.connector.Response;
import org.springframework.extensions.webscripts.connector.ResponseStatus;
import org.springframework.web.util.UriUtils;

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

}
