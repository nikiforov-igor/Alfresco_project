/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.signed.docflow.webscripts;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
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
 * Вебскрипт для создания подписи, получения списка контента, получение списка
 * подписей, обновления данных о подписях
 *
 * @author ikhalikov
 */
public class SigningWebscript extends DeclarativeWebScript {
	
	private SignedDocflow signedDocflowService;

	public void setSignedDocflowService(SignedDocflow signedDocflowService) {
		this.signedDocflowService = signedDocflowService;
	}
	
	private final static Logger logger = LoggerFactory.getLogger(SignConfigWebscript.class);

	private final static String ACTOR_DEF = "actor";

	JSONArray executeUpdateAction(final JSONArray json) {
		JSONArray jsonResult = new JSONArray();
		Map<String, String> result = signedDocflowService.updateSignatures(json);
		jsonResult.put(result);
		return jsonResult;
	}

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		final Map<String, String> templateArgs = req.getServiceMatch().getTemplateVars();
		final String actor = templateArgs.get(ACTOR_DEF);
		
		final Content content = req.getContent();
		if (content == null) {
			String msg = "SingingWebscript was called with empty json content";
			logger.error("{}. Executed actor: {}", msg, actor);
			throw new WebScriptException(String.format("%s. Executed actor: %s", msg, actor));
		}
		
		JSONArray requestJSON = DeclarativeWebScriptHelper.getJsonArrayContent(content);
		JSONArray responseJSON;
		
		try {
			if ("update".equals(actor)) {
				responseJSON = executeUpdateAction(requestJSON);
			} else {
				throw new IllegalArgumentException(String.format("Actor %s is unknown and unsupported!", actor));
			}
		} catch(Exception ex) {
			throw new WebScriptException(null, ex);
		}
		
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", responseJSON);
		return result;
	}
}
