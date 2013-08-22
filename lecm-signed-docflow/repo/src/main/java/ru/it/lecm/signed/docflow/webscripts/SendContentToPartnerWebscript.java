package ru.it.lecm.signed.docflow.webscripts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.alfresco.service.cmr.repository.NodeRef;
import org.json.JSONArray;
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
import ru.it.lecm.base.DeclarativeWebScriptHelper;
import ru.it.lecm.signed.docflow.SendContentToPartnerService;
import ru.it.lecm.signed.docflow.model.ContentToSendData;

/**
 *
 * @author VLadimir Malygin
 * @since 12.08.2013 12:25:08
 * @see <p>mailto: <a href="mailto:vmalygin@it.ru">vmalygin@it.ru</a></p>
 */
public class SendContentToPartnerWebscript extends DeclarativeWebScript {

	private final static Logger logger = LoggerFactory.getLogger(SendContentToPartnerWebscript.class);
		
	private SendContentToPartnerService sendContentToPartnerService;

	public void setSendContentToPartnerService(SendContentToPartnerService sendContentToPartnerService) {
		this.sendContentToPartnerService = sendContentToPartnerService;
	}

	private ContentToSendData getContentToSendFromJSON(JSONObject json) {
		ContentToSendData contentToSend = new ContentToSendData();
		try {
			JSONArray contentArray = json.getJSONArray("content");
			List<NodeRef> content = new ArrayList<NodeRef>(contentArray.length());
			for (int i = 0; i < contentArray.length(); ++i) {
				content.add(new NodeRef(contentArray.getString(i)));
			}
			contentToSend.setContent(content);

			if (json.has("partner")) {
				contentToSend.setPartner(new NodeRef(json.getString("partner")));
			}
			if (json.has("email")) {
				contentToSend.setEmail(json.getString("email"));
			}
			if (json.has("interactionType")) {
				contentToSend.setInteractionType(json.getString("interactionType"));
			}
		} catch(JSONException ex) {
			String msg = "Can't parse incoming json";
			logger.error("{}. Caused by: {}", msg, ex.getMessage());
			throw new IllegalArgumentException(msg, ex);
		}
		return contentToSend;
	}
	
	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		final Content content = req.getContent();
		if (content == null) {
			String msg = "SendContentToPartnerWebscript was called with empty json content";
			logger.error("{}", msg);
			throw new WebScriptException(String.format("%s", msg));
		}

		JSONObject requestJSON = DeclarativeWebScriptHelper.getJsonContent(content);

		ContentToSendData contentToSend = getContentToSendFromJSON(requestJSON);
		List<Map<String, Object>> sendContentList = sendContentToPartnerService.send(contentToSend);
		
		
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", new JSONArray(sendContentList));
		return result;
	}
}
