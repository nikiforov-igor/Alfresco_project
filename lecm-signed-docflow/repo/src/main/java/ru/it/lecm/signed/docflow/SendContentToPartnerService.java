package ru.it.lecm.signed.docflow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.alfresco.service.cmr.lock.LockService;
import org.alfresco.service.cmr.lock.LockType;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ParameterCheck;
import org.apache.commons.lang.StringUtils;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.contractors.api.Contractors;
import ru.it.lecm.documents.beans.DocumentAttachmentsService;
import ru.it.lecm.signed.docflow.model.ContentToSendData;
import ru.it.lecm.signed.docflow.model.UnicloudData;

/**
 * Сервис отправки контента для контрагента
 * @author VLadimir Malygin
 * @since 12.08.2013 15:57:09
 * @see <p>mailto: <a href="mailto:vmalygin@it.ru">vmalygin@it.ru</a></p>
 */
public class SendContentToPartnerService {

	private final static String SPECOP = "SPECOP";
	private final static String EMAIL = "EMAIL";
	private final static String BJ_MESSAGE_SEND_ATTACHMENT = "#initiator направил контрагенту #object1 вложение #mainObject к документу #object2.";
	private final static String BJ_MESSAGE_SEND_CONTENT = "#initiator направил контрагенту #object1 файл #mainObject.";

	private final static QName ASSOC_CONTRACT_PARTNER = QName.createQName("http://www.it.ru/logicECM/contract/1.0", "partner-assoc");

	private NodeService nodeService;
	private LockService lockService;

	public void setLockService(LockService lockService) {
		this.lockService = lockService;
	}
	private DocumentAttachmentsService documentAttachmentsService;
	private UnicloudService unicloudService;
	private BusinessJournalService businessJournalService;

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setDocumentAttachmentsService(DocumentAttachmentsService documentAttachmentsService) {
		this.documentAttachmentsService = documentAttachmentsService;
	}

	public void setUnicloudService(UnicloudService unicloudService) {
		this.unicloudService = unicloudService;
	}

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

	private UnicloudData sendUsingSPECOP(NodeRef contentRef, NodeRef partnerRef) {
		return unicloudService.sendDocument(contentRef, partnerRef);
	}

	private Map<String, Object> sendUsingEmail(NodeRef contentRef, NodeRef partnerRef, String email) {
		throw new UnsupportedOperationException("Not implemented yet!");
	}

	private void addBusinessJournalRecord(NodeRef contentRef, NodeRef partnerRef) {
		String messageTemplate;

		NodeRef documentRef = documentAttachmentsService.getDocumentByAttachment(contentRef);

		List<String> objects = new ArrayList<String>();
		objects.add(partnerRef.toString());

		if (documentRef != null) {
			objects.add(documentRef.toString());
			messageTemplate = BJ_MESSAGE_SEND_ATTACHMENT;
		} else {
			messageTemplate = BJ_MESSAGE_SEND_CONTENT;
		}

		businessJournalService.log(contentRef, SignedDocflowEventCategory.SIGNED_DOCFLOW, messageTemplate, objects);
	}

	private Map<String, Object> send(NodeRef contentRef, NodeRef partnerRef, String interactionType, String email) {
		ParameterCheck.mandatory("contentRef", contentRef);
		ParameterCheck.mandatory("partnerRef", partnerRef);
		ParameterCheck.mandatory("interactionType", interactionType);
		ParameterCheck.mandatory("email", email);

		Map<String, Object> result;

		if (SPECOP.equals(interactionType)) {
			UnicloudData specopData = sendUsingSPECOP(contentRef, partnerRef);
			result = specopData.getProperties();
		} else if (EMAIL.equals(interactionType)) {
			result = sendUsingEmail(contentRef, partnerRef, email);
		} else {
			throw new IllegalArgumentException(String.format("%s is illegal interactionType with partner %s", interactionType, partnerRef));
		}
		addBusinessJournalRecord(contentRef, partnerRef);
		return result;
	}

	private NodeRef getEffectivePartner(final NodeRef contentRef, final NodeRef partnerRef) {
		NodeRef effectivePartner;
		NodeRef documentRef = documentAttachmentsService.getDocumentByAttachment(contentRef);
		if (documentRef != null) {
			List<AssociationRef> assocs = nodeService.getTargetAssocs(documentRef, ASSOC_CONTRACT_PARTNER);
			if (assocs != null && !assocs.isEmpty()) {
				effectivePartner = assocs.get(0).getTargetRef();
			} else {
				effectivePartner = partnerRef;
			}
		} else {
			effectivePartner = partnerRef;
		}
		return effectivePartner;
	}

	private String getEffectiveInteractionType(final NodeRef parnerRef, final String interactionType) {
		String effectiveInteractionType;
		if (StringUtils.isEmpty(interactionType)) {
			effectiveInteractionType = (String)nodeService.getProperty(parnerRef, Contractors.PROP_CONTRACTOR_INTERACTION_TYPE);
		} else {
			effectiveInteractionType = interactionType;
		}
		return effectiveInteractionType;
	}

	private String getEffectiveEmail(final NodeRef parnerRef, final String email) {
		String effectiveEmail;
		if (StringUtils.isEmpty(email)) {
			effectiveEmail = (String)nodeService.getProperty(parnerRef, Contractors.PROP_CONTRACTOR_EMAIL);
		} else {
			effectiveEmail = email;
		}
		return effectiveEmail;
	}
	
		private void nodeLock(List<NodeRef> nodeRefList){
		lockService.lock(nodeRefList, LockType.NODE_LOCK, 0);
	}
	
	public List<Map<String, Object>> send(ContentToSendData contentToSend) {		
		List<NodeRef> contentList = contentToSend.getContent();
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>(contentList.size());
		for (NodeRef contentRef : contentList) {
			NodeRef partnerRef = getEffectivePartner(contentRef, contentToSend.getPartner());
			String interactionType = getEffectiveInteractionType(partnerRef, contentToSend.getInteractionType());
			String email = getEffectiveEmail(partnerRef, contentToSend.getEmail());
			result.add(send(contentRef, partnerRef, interactionType, email));
			nodeLock(contentList);
		}
		return result;
	}
}
