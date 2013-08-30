package ru.it.lecm.signed.docflow.webscripts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import ru.it.lecm.contractors.api.Contractors;
import ru.it.lecm.documents.beans.DocumentAttachmentsService;
import ru.it.lecm.signed.docflow.ReceiveContentByEmailService;
import ru.it.lecm.signed.docflow.UnicloudService;
import ru.it.lecm.signed.docflow.model.ReceiveDocumentData;

/**
 *
 * @author vlevin
 */
public class GetSignedContentFromPartnerWebscript extends DeclarativeWebScript {

	private final static Logger logger = LoggerFactory.getLogger(GetSignedContentFromPartnerWebscript.class);
	private final static QName ASSOC_CONTRACT_PARTNER = QName.createQName("http://www.it.ru/logicECM/contract/1.0", "partner-assoc");

	private ReceiveContentByEmailService receiveContentByEmailService;
	private UnicloudService unicloudService;
	private DocumentAttachmentsService documentAttachmentsService;
	private NodeService nodeService;

	public void setReceiveContentByEmailService(ReceiveContentByEmailService receiveContentByEmailService) {
		this.receiveContentByEmailService = receiveContentByEmailService;
	}

	public void setUnicloudService(UnicloudService unicloudService) {
		this.unicloudService = unicloudService;
	}

	public void setDocumentAttachmentsService(DocumentAttachmentsService documentAttachmentsService) {
		this.documentAttachmentsService = documentAttachmentsService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		String nodeRef = req.getParameter("nodeRef");
		String method = req.getParameter("method");
		if (nodeRef == null) {
			String msg = "nodeRef is Required parameter for GetSignedContentFromPartnerWebscript, it can't be null";
			logger.error(msg);
			throw new WebScriptException(msg);
		}
		NodeRef contentRef = new NodeRef(nodeRef);

		if (method == null) {
			NodeRef documentRef = documentAttachmentsService.getDocumentByAttachment(contentRef);
			if(documentRef == null) {
				String msg = String.format("NodeRef %s is not an attachment, you must set interationType method explicitly", contentRef);
				logger.error(msg);
				throw new WebScriptException(msg);
			} else {
				List<AssociationRef> assocs = nodeService.getTargetAssocs(documentRef, ASSOC_CONTRACT_PARTNER);
				if (assocs != null && !assocs.isEmpty()) {
					NodeRef partnerRef = assocs.get(0).getTargetRef();
					method = (String) nodeService.getProperty(partnerRef, Contractors.PROP_CONTRACTOR_INTERACTION_TYPE);
				} else {
					String msg = String.format("Document %s with attachment %s must have a partner with specified interactionType.", documentRef, contentRef);
					logger.error(msg);
					throw new WebScriptException(msg);
				}
			}
		}

		JSONObject jsonResult;
		ReceiveDocumentData receiveDocumentData;
		if ("email".equalsIgnoreCase(method)) {
			receiveDocumentData = receiveContentByEmailService.getSignaturesForContentByEmail(contentRef);
		} else if ("specop".equalsIgnoreCase(method)) {
			receiveDocumentData = unicloudService.receiveDocuments(contentRef);
		} else {
			String message = String.format("GetSignedContentFromPartnerWebscript was called with unknown method '%s'", method);
			logger.error(message);
			throw new WebScriptException(message);
		}

		jsonResult = new JSONObject(receiveDocumentData.getProperties());
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", jsonResult);
		return result;
	}
}
