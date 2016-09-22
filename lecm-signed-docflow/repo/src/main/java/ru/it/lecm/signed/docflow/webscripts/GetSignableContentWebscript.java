package ru.it.lecm.signed.docflow.webscripts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.documents.beans.DocumentAttachmentsService;
import ru.it.lecm.signed.docflow.api.SignedDocflow;

/**
 *
 * @author ikhalikov
 */
public class GetSignableContentWebscript extends DeclarativeWebScript {

	private DocumentAttachmentsService documentAttachmentsService;
	private NodeService nodeService;
	private SignedDocflow signedDocflowService;

	public void setSignedDocflowService(SignedDocflow signedDocflowService) {
		this.signedDocflowService = signedDocflowService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}
	private final static Logger logger = LoggerFactory.getLogger(SignContentWebscript.class);

	public void setDocumentAttachmentsService(DocumentAttachmentsService documentAttachmentsService) {
		this.documentAttachmentsService = documentAttachmentsService;
	}

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		Map<String, Object> result = new HashMap<String, Object>();
		JSONArray JSONres = new JSONArray();

		final NodeRef docNodeRef = new NodeRef(req.getParameter("nodeRef"));
		if(!nodeService.exists(docNodeRef)){
			String errorMsg = String.format("NodeRef %s does not exist", docNodeRef.toString());
			logger.error(errorMsg);
			throw new WebScriptException(errorMsg);
		}

		List<NodeRef> categories = new ArrayList<NodeRef>();
		try {
			categories = documentAttachmentsService.getCategories(docNodeRef);
		}catch(WriteTransactionNeededException e){
			logger.error("error: ",e);
		}
		for (NodeRef nodeRef : categories) {
			Map<String, Object> resultObject = new HashMap<String, Object>();
			JSONArray contentArray = new JSONArray();
			String categoryName = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_NAME);
			List<NodeRef> attachments = documentAttachmentsService.getAttachmentsByCategory(docNodeRef, categoryName);
			for (NodeRef attachRef : attachments) {
				if (signedDocflowService.isSignable(attachRef)) {
					Map<String, String> attachmentObject = new HashMap<String, String>();
					attachmentObject.put("name", (String) nodeService.getProperty(attachRef, ContentModel.PROP_NAME));
					attachmentObject.put("nodeRef", attachRef.toString());
					contentArray.put(attachmentObject);
				}
			}
			if (contentArray.length() != 0) {
				resultObject.put("categoryName", categoryName);
				resultObject.put("content", contentArray);
				JSONres.put(resultObject);
			}
		}

		result.put("result", JSONres);
		return result;
	}
}
