package ru.it.lecm.signed.docflow.webscripts;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.dictionary.DictionaryService;
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
import ru.it.lecm.documents.beans.DocumentService;
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
	private DictionaryService dictionaryService;

	public void setDictionaryService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

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

	private JSONArray getDocumentSignsInfo(final NodeRef documentRef) {
		JSONArray result = new JSONArray();
		List<NodeRef> categories =new ArrayList<NodeRef>();
		try {
			categories = documentAttachmentsService.getCategories(documentRef);
		} catch(WriteTransactionNeededException e){
			logger.error("error: ",e);
		}
		for (NodeRef categoryRef : categories) {
			JSONArray contentArray = new JSONArray();
			String categoryName = (String) nodeService.getProperty(categoryRef, ContentModel.PROP_NAME);
			List<NodeRef> attachments = documentAttachmentsService.getAttachmentsByCategory(documentRef, categoryName);
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
						signInfo.put("signature", sign.getSignatureContent());
						signsInfoJSON.put(signInfo);
					}
					attachmentObject.put("fileName", (String) nodeService.getProperty(attachRef, ContentModel.PROP_NAME));
					attachmentObject.put("nodeRef", attachRef.toString());
					attachmentObject.put("signsInfo", signsInfoJSON);
                    attachmentObject.put("contentHash", signedDocflowService.generateHash(attachRef));
					contentArray.put(attachmentObject);
				}
			}
			if(contentArray.length() != 0){
				Map<String, Object> categoryJSON = new HashMap<String, Object>();
				categoryJSON.put("categoryName", categoryName);
				categoryJSON.put("signedContent", contentArray);
				result.put(categoryJSON);
			}
		}
		return result;
	}

	private JSONArray getContentSignsInfo(final NodeRef contentRef) {

		JSONArray signsInfoJSON = new JSONArray();
		List<Signature> signs = signedDocflowService.getSignatures(contentRef);
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
			signInfo.put("signature", sign.getSignatureContent());
			signsInfoJSON.put(signInfo);
		}
		Map<String, Object> contentObject = new HashMap<String, Object>();
		contentObject.put("fileName", (String) nodeService.getProperty(contentRef, ContentModel.PROP_NAME));
		contentObject.put("nodeRef", contentRef.toString());
		contentObject.put("signsInfo", signsInfoJSON);
        contentObject.put("contentHash", signedDocflowService.generateHash(contentRef));

		JSONArray contentArray = new JSONArray();
		contentArray.put(contentObject);

		Map<String, Object> categoryJSON = new HashMap<String, Object>();
		NodeRef categoryRef = documentAttachmentsService.getCategoryByAttachment(contentRef);
		String categoryName;
		if (categoryRef != null) {
			categoryName = (String) nodeService.getProperty(categoryRef, ContentModel.PROP_NAME);
		} else {
			categoryName = "";
		}
		categoryJSON.put("categoryName", categoryName);
		categoryJSON.put("signedContent", contentArray);

		JSONArray result = new JSONArray();
		result.put(categoryJSON);

		return result;
	}

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		String nodeRef = req.getParameter("nodeRef");
		final NodeRef docNodeRef = new NodeRef(nodeRef);
		if (nodeRef == null) {
			logger.error("GetDocumentSignsInfoWebscript was called with empty parameter");
			throw new WebScriptException("GetDocumentSignsInfoWebscript was called with empty parameter");
		}

		JSONArray JSONres;
		if(dictionaryService.isSubClass(nodeService.getType(docNodeRef), DocumentService.TYPE_BASE_DOCUMENT)){
			JSONres = getDocumentSignsInfo(docNodeRef);
		} else {
			JSONres = getContentSignsInfo(docNodeRef);
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", JSONres);
		return result;
	}
}
