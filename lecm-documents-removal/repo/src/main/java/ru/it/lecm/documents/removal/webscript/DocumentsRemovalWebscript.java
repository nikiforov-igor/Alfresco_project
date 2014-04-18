package ru.it.lecm.documents.removal.webscript;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import ru.it.lecm.documents.removal.DocumentsRemovalService;

/**
 *
 * @author vmalygin
 */
public class DocumentsRemovalWebscript extends DeclarativeWebScript {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(DocumentsRemovalWebscript.class);

	private final static String PARAM_NODE_REF = "nodeRef";

	private DocumentsRemovalService documentsRemovalService;

	public void setDocumentsRemovalService(DocumentsRemovalService documentsRemovalService) {
		this.documentsRemovalService = documentsRemovalService;
	}

	private String mandatoryParameter(String parameter, String value) {
		boolean isEmpty = StringUtils.isBlank(value) || "null".equalsIgnoreCase(value);
		if (isEmpty) {
			throw new WebScriptException(String.format("%s is a mandatory parameter", parameter));
		}
		return value;
	}

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		String requestContent = null;
		Map<String, Object> result = new HashMap<String, Object>();

		Map<String, ArrayList<String>> json = new HashMap<String, ArrayList<String>>();
		json.put("success", new ArrayList<String>());
		json.put("fail", new ArrayList<String>());

		try {
			requestContent = req.getContent().getContent();
		} catch (IOException ex) {
			// не страшно, если не получилось взять контент запроса. остался еще параметр
			logger.warn("Error while getting request content", ex);
		}

		if (requestContent != null && !StringUtils.isBlank(requestContent)) {
			try {
				JSONArray refJSONArray = new JSONArray(requestContent);
				for (int i = 0; i < refJSONArray.length(); i++) {
					String ref = refJSONArray.getString(i);
					try {
						documentsRemovalService.purge(new NodeRef(ref));
						json.get("success").add(ref);
					} catch (Exception ex) {
						json.get("fail").add(ref);
						String msg = "Error while deleting document %s";
						logger.warn(String.format(msg, ref), ex);
					}
				}
			} catch (JSONException ex) {
				logger.warn("Error while parsing request content", ex);
			}
		} else {
			String ref = mandatoryParameter(PARAM_NODE_REF, req.getParameter(PARAM_NODE_REF));
			try {
				documentsRemovalService.purge(new NodeRef(ref));
				json.get("success").add(ref);
			} catch (Exception ex) {
				json.get("fail").add(ref);
				String msg = "Error while deleting document %s";
				logger.warn(String.format(msg, ref), ex);
			}
		}
		result.put("result", new JSONObject(json));
		return result;
	}
}
