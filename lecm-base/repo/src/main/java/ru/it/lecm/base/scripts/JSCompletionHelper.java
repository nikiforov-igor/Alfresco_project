/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.it.lecm.base.scripts;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.web.context.ContextLoader;
import ru.it.lecm.base.beans.BaseWebScript;

/**
 *
 * @author ikhalikov
 */
public class JSCompletionHelper extends DeclarativeWebScript {
	private JSONObject completions = null;
	public JSONObject getExtensions() throws JSONException {
		JSONObject json = new JSONObject();
		ApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
		Map<String, BaseWebScript> extensions = context.getBeansOfType(BaseWebScript.class);

		JSONObject jsonExt = new JSONObject();
		for (Map.Entry<String, BaseWebScript> extension : extensions.entrySet()) {
			BaseWebScript baseWebScript = extension.getValue();
			baseWebScript.getExtensionName();
			Method[] methods = baseWebScript.getClass().getDeclaredMethods();
			List<Method> methodsList = new ArrayList<Method>(Arrays.asList(methods));
			JSONObject jsonMethods = new JSONObject();
			for (Method method : methodsList) {
//				String type = method.getReturnType().getName();
				JSONObject jsonMethod = new JSONObject();
				jsonMethod.put("!type", "");
				jsonMethods.put(method.getName(), jsonMethod);
			}
			jsonExt.put(baseWebScript.getExtensionName(), jsonMethods);

		}
		return jsonExt;
	}

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		Map<String, Object> res = new HashMap<String, Object>();
		try {
			if(completions == null) completions = getExtensions();
			res.put("res", completions);
		} catch (JSONException ex) {
			Logger.getLogger(JSCompletionHelper.class.getName()).log(Level.SEVERE, null, ex);
		}
		return res;
	}

}
