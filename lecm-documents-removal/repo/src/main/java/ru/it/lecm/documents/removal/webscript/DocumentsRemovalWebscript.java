package ru.it.lecm.documents.removal.webscript;

import java.util.HashMap;
import java.util.Map;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
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
		String ref = mandatoryParameter(PARAM_NODE_REF, req.getParameter(PARAM_NODE_REF));
		documentsRemovalService.purge(new NodeRef(ref));
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", new JSONObject());
		return result;
	}
}
