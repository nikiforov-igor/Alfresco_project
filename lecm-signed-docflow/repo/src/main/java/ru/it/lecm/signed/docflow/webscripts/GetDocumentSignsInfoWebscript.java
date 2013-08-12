/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.signed.docflow.webscripts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import ru.it.lecm.documents.beans.DocumentAttachmentsService;
import ru.it.lecm.signed.docflow.api.Signature;
import ru.it.lecm.signed.docflow.api.SignedDocflow;

/**
 *
 * @author ikhalikov
 */
public class GetDocumentSignsInfoWebscript extends DeclarativeWebScript{
	private DocumentAttachmentsService documentAttachmentsService;
	private NodeService nodeService;
	private SignedDocflow signedDocflowService;

	public void setSignedDocflowService(SignedDocflow signedDocflowService) {
		this.signedDocflowService = signedDocflowService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setDocumentAttachmentsService(DocumentAttachmentsService documentAttachmentsService) {
		this.documentAttachmentsService = documentAttachmentsService;
	}
	private final static Logger logger = LoggerFactory.getLogger(GetDocumentSignsInfoWebscript.class);
	
	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		JSONArray JSONres = new JSONArray();
		Map<String, Object> result = new HashMap<String, Object>();
		String nodeRef = req.getParameter("nodeRef");
		final NodeRef docNodeRef = new NodeRef(nodeRef);
		if (nodeRef == null) {
			logger.error("GetDocumentSignsInfoWebscript was called with empty parameter");
			throw new WebScriptException("GetDocumentSignsInfoWebscript was called with empty parameter");
		}
		
		List<NodeRef> categories = documentAttachmentsService.getCategories(docNodeRef);
		
		for (NodeRef categoryRef : categories) {
			Map<String, Object> jsonResponse = new HashMap<String, Object>();
			Map<String, Object> resultObject = new HashMap<String, Object>();
			JSONArray contentArray = new JSONArray();
			String categoryName = (String) nodeService.getProperty(categoryRef, ContentModel.PROP_NAME);
			List<NodeRef> attachments = documentAttachmentsService.getAttachmentsByCategory(docNodeRef, categoryName);
			for (NodeRef attachRef : attachments) {
				if (signedDocflowService.isSignable(attachRef)) {
					Map<String, Object> attachmentObject = new HashMap<String, Object>();
					
					JSONArray signsInfoJSON = new JSONArray();
					List<Signature> signs = signedDocflowService.getSignatures(attachRef);
					
					for (Signature sign : signs) {
						Map<String, Object> signInfo = new HashMap<String, Object>();
						signInfo.put("isOur", sign.getOur());
						signInfo.put("organization", sign.getOwnerOrganization());
						signInfo.put("position", sign.getOwnerPosition());
						signInfo.put("FIO", sign.getOwner());
						signInfo.put("signDate", sign.getSigningDateString());
						signInfo.put("isValid", sign.getValid());
						signInfo.put("lastValidate", sign.getUpdateDateString());
						signInfo.put("nodeRef", sign.getNodeRef());
						signsInfoJSON.put(signInfo);
					}
					attachmentObject.put("fileName", (String) nodeService.getProperty(attachRef, ContentModel.PROP_NAME));
					attachmentObject.put("nodeRef", attachRef.toString());
					attachmentObject.put("signsInfo", signsInfoJSON);
					contentArray.put(attachmentObject);
				}
			}
			
				jsonResponse.put("categoryName", categoryName);
				jsonResponse.put("signedContent", contentArray);
				JSONres.put(jsonResponse);
			
		}
		result.put("result", JSONres);
		return result;
	}
	
	
}
