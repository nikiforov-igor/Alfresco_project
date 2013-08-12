package ru.it.lecm.signed.docflow;

import java.util.List;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ParameterCheck;
import org.apache.commons.lang.StringUtils;
import ru.it.lecm.contractors.api.Contractors;
import ru.it.lecm.documents.beans.DocumentAttachmentsService;
import ru.it.lecm.signed.docflow.model.ContentToSendData;

/**
 * Сервис отправки контента для контрагента
 * @author VLadimir Malygin
 * @since 12.08.2013 15:57:09
 * @see <p>mailto: <a href="mailto:vmalygin@it.ru">vmalygin@it.ru</a></p>
 */
public class SendContentToPartnerService {

	private final static String SPECOP = "SPECOP";
	private final static String EMAIL = "EMAIL";
	/*
	                 <!-- Контрагенты -->
                <association name="lecm-contract:partner-assoc">
                    <source>
                        <mandatory>false</mandatory>
                        <many>true</many>
                    </source>
                    <target>
                        <class>lecm-contractor:contractor-type</class>
                        <mandatory>true</mandatory>
                        <many>false</many>
                    </target>
                </association>
	 */
	private final static QName ASSOC_CONTRACT_PARTNER = QName.createQName("http://www.it.ru/logicECM/contract/1.0", "partner-assoc");

	private NodeService nodeService;
	private DocumentAttachmentsService documentAttachmentsService;
	private UnicloudService unicloudService;

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setDocumentAttachmentsService(DocumentAttachmentsService documentAttachmentsService) {
		this.documentAttachmentsService = documentAttachmentsService;
	}

	public void setUnicloudService(UnicloudService unicloudService) {
		this.unicloudService = unicloudService;
	}

	private void sendUsingSPECOP(NodeRef contentRef, NodeRef partnerRef) {
		
	}

	private void sendUsingEmail(NodeRef contentRef, NodeRef partnerRef, String email) {

	}

	private void send(NodeRef contentRef, NodeRef partnerRef, String interactionType, String email) {
		ParameterCheck.mandatory("contentRef", contentRef);
		ParameterCheck.mandatory("partnerRef", partnerRef);
		ParameterCheck.mandatory("interactionType", interactionType);
		ParameterCheck.mandatory("email", email);

		if (SPECOP.equals(interactionType)) {
			sendUsingSPECOP(contentRef, partnerRef);
		} else if (EMAIL.equals(interactionType)) {
			sendUsingEmail(contentRef, partnerRef, email);
		} else {
			throw new IllegalArgumentException(String.format("%s is illegal interactionType with partner %s", interactionType, partnerRef));
		}
	}

	private NodeRef getEffectivePartner(final NodeRef contentRef, final NodeRef partnerRef) {
		NodeRef effectivePartner = null;
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

	public void send(ContentToSendData contentToSend) {
		List<NodeRef> contentList = contentToSend.getContent();
		for (NodeRef contentRef : contentList) {
			NodeRef partnerRef = getEffectivePartner(contentRef, contentToSend.getPartner());
			String interactionType = getEffectiveInteractionType(partnerRef, contentToSend.getInteractionType());
			String email = getEffectiveEmail(partnerRef, contentToSend.getEmail());
			send(contentRef, partnerRef, interactionType, email);
		}
	}
}
