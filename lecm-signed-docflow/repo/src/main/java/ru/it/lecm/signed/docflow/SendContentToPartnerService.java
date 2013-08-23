package ru.it.lecm.signed.docflow;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.alfresco.service.cmr.lock.LockService;
import org.alfresco.service.cmr.lock.LockType;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.TemplateService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.mail.javamail.JavaMailSender;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.contractors.api.Contractors;
import ru.it.lecm.documents.beans.DocumentAttachmentsService;
import ru.it.lecm.signed.docflow.api.SignedDocflow;
import ru.it.lecm.signed.docflow.model.ContentToSendData;

/**
 * Сервис отправки контента для контрагента
 *
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
	private NodeRef documentAttachmentMailTemplate;
	private NodeRef contentMailTemplate;
	private NodeService nodeService;
	private LockService lockService;
	private DocumentAttachmentsService documentAttachmentsService;
	private UnicloudService unicloudService;
	private BusinessJournalService businessJournalService;
	private JavaMailSender mailService;
	private TemplateService templateService;
	private SignedDocflow signedDocflowService;
	private FileFolderService fileFolderService;
	private ZipSignedContentService zipSignedContentService;
	private final static Log logger = LogFactory.getLog(SendContentToPartnerService.class);

	public void init() {
		PropertyCheck.mandatory(this, "nodeService", nodeService);
		PropertyCheck.mandatory(this, "documentAttachmentsService", documentAttachmentsService);
		PropertyCheck.mandatory(this, "unicloudService", unicloudService);
		PropertyCheck.mandatory(this, "businessJournalService", businessJournalService);
		PropertyCheck.mandatory(this, "mailService", mailService);
		PropertyCheck.mandatory(this, "templateService", templateService);
		PropertyCheck.mandatory(this, "signedDocflowService", signedDocflowService);
		PropertyCheck.mandatory(this, "fileFolderService", fileFolderService);
		PropertyCheck.mandatory(this, "zipSignedContentService", zipSignedContentService);
	}

	public void setZipSignedContentService(ZipSignedContentService zipSignedContentService) {
		this.zipSignedContentService = zipSignedContentService;
	}

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

	public void setMailService(JavaMailSender mailService) {
		this.mailService = mailService;
	}

	public void setTemplateService(TemplateService templateService) {
		this.templateService = templateService;
	}

	public void setSignedDocflowService(SignedDocflow signedDocflowService) {
		this.signedDocflowService = signedDocflowService;
	}

	public void setFileFolderService(FileFolderService fileFolderService) {
		this.fileFolderService = fileFolderService;
	}

	public void setLockService(LockService lockService) {
		this.lockService = lockService;
	}

	private List<Map<String, Object>> sendUsingSPECOP(ContentToSendData contentToSend) {
		List<NodeRef> contentList = contentToSend.getContent();
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		NodeRef partnerRef = contentToSend.getPartner();
		for (NodeRef contentRef : contentList) {
			result.add(unicloudService.sendDocument(contentRef, partnerRef).getProperties());
		}
		return result;
	}

	private List<Map<String, Object>> sendUsingEmail(ContentToSendData contentToSend) {
		throw new UnsupportedOperationException("Not implemented yet!");
		/*
		 List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		 String mailTemplate;
		 Map<String, Object> mailTemplateModel = new HashMap<String, Object>();
		 File tempDir = FileUtils.createTmpDir();
		 List<NodeRef> contentList = contentToSend.getContent();
		 List<File> attachmentFiles = createAttachmentFiles(contentList, tempDir);
		 if (contentToSend.isDocumentAttachment()) {
		 mailTemplate = "DOCUMENT_ATACHMENT_TEMPLATE";
		 } else {
		 mailTemplate = "CONTENT_TEMPLATE";
		 }
		 String mailText = templateService.processTemplate("freemarker", mailTemplate, mailTemplateModel);

		 return result;
		 */
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
			effectiveInteractionType = (String) nodeService.getProperty(parnerRef, Contractors.PROP_CONTRACTOR_INTERACTION_TYPE);
		} else {
			effectiveInteractionType = interactionType;
		}
		return effectiveInteractionType;
	}

	private String getEffectiveEmail(final NodeRef parnerRef, final String email) {
		String effectiveEmail;
		if (StringUtils.isEmpty(email)) {
			effectiveEmail = (String) nodeService.getProperty(parnerRef, Contractors.PROP_CONTRACTOR_EMAIL);
		} else {
			effectiveEmail = email;
		}
		return effectiveEmail;
	}

	private void nodeLock(List<NodeRef> nodeRefList) {
		lockService.lock(nodeRefList, LockType.NODE_LOCK, 0);
	}

	public List<Map<String, Object>> send(ContentToSendData contentToSend) {
		List<NodeRef> contentList = contentToSend.getContent();
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>(contentList.size());
		NodeRef contentRef = contentList.get(0);
		NodeRef partnerRef = getEffectivePartner(contentRef, contentToSend.getPartner());
		String interactionType = getEffectiveInteractionType(partnerRef, contentToSend.getInteractionType());
		String email = getEffectiveEmail(partnerRef, contentToSend.getEmail());

		contentToSend.setIsDocumentAttachment(documentAttachmentsService.isDocumentAttachment(contentRef));
		contentToSend.setPartner(partnerRef);
		contentToSend.setInteractionType(interactionType);
		contentToSend.setEmail(email);

		nodeLock(contentList);

		if (SPECOP.equals(interactionType)) {
			result = sendUsingSPECOP(contentToSend);
		} else if (EMAIL.equals(interactionType)) {
			result = sendUsingEmail(contentToSend);
		} else {
			throw new IllegalArgumentException(String.format("%s is illegal interactionType with partner %s", interactionType, partnerRef));
		}
		addBusinessJournalRecord(contentRef, partnerRef);

		return result;
	}

	private List<File> createAttachmentFiles(List<NodeRef> signedContentList, File tempDir) {
		List<File> result = new ArrayList<File>();
		for (NodeRef contentRef : signedContentList) {
			FileOutputStream zipFileOutput = null;
			File zipFile = null;
			try {
				String contentName = (String) nodeService.getProperty(contentRef, ContentModel.PROP_NAME);
				zipFile = new File(tempDir, contentName);
				zipFile.createNewFile();
				zipFileOutput = new FileOutputStream(zipFile);
				zipSignedContentService.writeZipToStream(zipFileOutput, contentRef, true);
			} catch (IOException ex) {
				logger.error("Error creating ZIP file", ex);
			} finally {
				IOUtils.closeQuietly(zipFileOutput);
			}

			if (zipFile != null) {
				result.add(zipFile);
			}

		}
		return result;
	}
}
