package ru.it.lecm.signed.docflow.webscripts;

import java.util.HashMap;
import java.util.Map;
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

/**
 *
 * @author VLadimir Malygin
 * @since 12.08.2013 12:25:08
 * @see <p>mailto: <a href="mailto:vmalygin@it.ru">vmalygin@it.ru</a></p>
 */
public class SendContentToPartnerWebscript extends DeclarativeWebScript {

	private final static Logger logger = LoggerFactory.getLogger(SendContentToPartnerWebscript.class);

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		final Content content = req.getContent();
		if (content == null) {
			String msg = "SendContentToPartnerWebscript was called with empty json content";
			logger.error("{}", msg);
			throw new WebScriptException(String.format("%s", msg));
		}

		JSONObject requestJSON = DeclarativeWebScriptHelper.getJsonContent(content);
		JSONObject responseJSON = new JSONObject();

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", responseJSON);
		return result;
	}
}
