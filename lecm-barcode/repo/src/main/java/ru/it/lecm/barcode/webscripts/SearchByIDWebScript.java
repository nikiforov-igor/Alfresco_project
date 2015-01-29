package ru.it.lecm.barcode.webscripts;

import java.io.IOException;
import java.nio.charset.Charset;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import ru.it.lecm.documents.beans.DocumentService;

/**
 * Получение NodeRef по DbID
 * Created by AZinovin on 15.10.2014.
 */
public class SearchByIDWebScript extends AbstractWebScript {

	private NodeService nodeService;
	private DocumentService documentService;

	private final static Logger logger = LoggerFactory.getLogger(SearchByIDWebScript.class);

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
		String dbIdStr = req.getParameter("dbid");
		long dbId;
		NodeRef nodeRef;
		try {
			dbId = Long.parseLong(dbIdStr);
			nodeRef = nodeService.getNodeRef(dbId);
		} catch (NumberFormatException ex) {
			logger.warn("Can not get parse " + dbIdStr + " as long", ex);
			nodeRef = null;
		}

		JSONObject wf = new JSONObject();

		try {
			boolean nodeRefIsValid = nodeRef != null && (nodeService.exists(nodeRef) && documentService.isDocument(nodeRef));

			wf.put("nodeRef", nodeRefIsValid ? nodeRef.toString() : "");
		} catch (JSONException ex) {
			logger.warn("Error forming JSON response", ex);
		}

		res.setContentType("application/json");
		res.setContentEncoding(Charset.defaultCharset().displayName());
		res.getWriter().write(wf.toString());
	}
}
