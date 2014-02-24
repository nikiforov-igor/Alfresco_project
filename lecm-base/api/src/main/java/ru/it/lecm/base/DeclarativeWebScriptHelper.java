package ru.it.lecm.base;

import java.io.IOException;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.WebScriptException;

/**
 *
 * @author vlevin
 */
public final class DeclarativeWebScriptHelper {

	private final static Logger logger = LoggerFactory.getLogger(DeclarativeWebScriptHelper.class);

	private DeclarativeWebScriptHelper() {
	}

	public static JSONObject getJsonContent(final Content content) {
		JSONObject json;
		try {
			json = new JSONObject(content.getContent());
		} catch (IOException ex) {
			String msg = "Can't read request content as json string";
			logger.error("{}. Caused by: {}", msg, ex.getMessage());
			throw new WebScriptException(msg, ex);
		} catch (JSONException ex) {
			String msg = "Can't marshall request content as json object";
			logger.error("{}. Caused by: {}", msg, ex.getMessage());
			throw new WebScriptException(msg, ex);
		}
		return json;
	}

	public static JSONArray getJsonArrayContent(final Content content) {
		JSONArray json;
		try {
			json = new JSONArray(content.getContent());
		} catch (IOException ex) {
			String msg = "Can't read request content as json string";
			logger.error("{}. Caused by: {}", msg, ex.getMessage());
			throw new WebScriptException(msg, ex);
		} catch (JSONException ex) {
			String msg = "Can't marshall request content as json object";
			logger.error("{}. Caused by: {}", msg, ex.getMessage());
			throw new WebScriptException(msg, ex);
		}
		return json;
	}
	
	public static String mandatoryParameter(String parameter, String value) {
		boolean isEmpty = StringUtils.isBlank(value) || "null".equalsIgnoreCase(value);
		if (isEmpty) {
			throw new WebScriptException(String.format("%s is a mandatory parameter", parameter));
		}
		return value;
	}

	public static String optionalParameter(String parameter, String value, String defaultValue) {
		boolean isEmpty = StringUtils.isBlank(value) || "null".equalsIgnoreCase(value);
		if (isEmpty) {
			return defaultValue;
		}
		return value;
	}
}
