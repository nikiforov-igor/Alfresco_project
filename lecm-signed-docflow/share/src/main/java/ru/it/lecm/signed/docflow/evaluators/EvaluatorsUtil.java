/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.signed.docflow.evaluators;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.springframework.extensions.webscripts.ScriptRemote;
import org.springframework.extensions.webscripts.connector.Response;
import org.springframework.extensions.webscripts.connector.ResponseStatus;
import org.springframework.web.util.UriUtils;

import java.io.UnsupportedEncodingException;

/**
 *
 * @author ikhalikov
 */
public class EvaluatorsUtil {

	private final static Log logger = LogFactory.getLog(EvaluatorsUtil.class);
	private ScriptRemote scriptRemote;

	public void setScriptRemote(ScriptRemote scriptRemote) {
		this.scriptRemote = scriptRemote;
	}

	public boolean hasBusinessRoleOrBoss(String userName, String roleId){

        String url = null;
        try {

            url = "/lecm/signed/docflow/signPermission?action=signPermission&userName=" + userName + "&roleId=" + roleId;
            url = UriUtils.encodeUri(url, "UTF-8");
        } catch (UnsupportedEncodingException ignored) {
        }
        Response response = scriptRemote.connect("alfresco").get(url);
		try {
			if (response.getStatus().getCode() == ResponseStatus.STATUS_OK) {
				org.json.JSONObject resultJson = new org.json.JSONObject(response.getResponse());
				return resultJson.getBoolean("success");
			} else {
				logger.warn("Cannot get roles from server");
			}
		} catch (JSONException e) {
			logger.warn("Cannot get roles from server", e);
		}
		return false;
	}

	public boolean hasAspect(String nodeRef, String aspect){

		String url = "/lecm/signed/docflow/signPermission?action=hasAspect&nodeRef=" + nodeRef + "&aspect=" + aspect;
		Response response = scriptRemote.connect("alfresco").get(url);
		try {
			if (response.getStatus().getCode() == ResponseStatus.STATUS_OK) {
				org.json.JSONObject resultJson = new org.json.JSONObject(response.getResponse());
				return resultJson.getBoolean("success");
			} else {
				logger.warn("Cannot get result from server");
			}
		} catch (JSONException e) {
			logger.warn("Cannot get result from server", e);
		}
		return false;
	}
	
	public boolean hasProperties(String nodeRef){
		String url = "/lecm/signed/docflow/signPermission?action=hasProperties&nodeRef=" + nodeRef;
		Response response = scriptRemote.connect("alfresco").get(url);
		try {
			if (response.getStatus().getCode() == ResponseStatus.STATUS_OK) {
				org.json.JSONObject resultJson = new org.json.JSONObject(response.getResponse());
				return resultJson.getBoolean("result");
			} else {
				logger.warn("Cannot get result from server");
			}
		} catch (JSONException e) {
			logger.warn("Cannot get result from server", e);
		}
		return false;
	}
}
