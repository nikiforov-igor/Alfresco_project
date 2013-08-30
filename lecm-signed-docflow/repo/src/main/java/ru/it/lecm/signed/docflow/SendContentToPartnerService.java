package ru.it.lecm.signed.docflow;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.TemplateService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.contractors.api.Contractors;
import ru.it.lecm.documents.beans.DocumentAttachmentsService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.regnumbers.RegNumbersService;
import ru.it.lecm.regnumbers.template.TemplateParseException;
import ru.it.lecm.regnumbers.template.TemplateRunException;
import ru.it.lecm.signed.docflow.api.SignedDocflow;
import ru.it.lecm.signed.docflow.api.SignedDocflowModel;
import ru.it.lecm.signed.docflow.model.ContentToSendData;
import ru.it.lecm.signed.docflow.model.SendDocumentData;
import ucloud.gate.proxy.exceptions.EResponseType;
import ucloud.gate.proxy.exceptions.GateResponse;

/**
 * Сервис отправки контента для контрагента
 *
 * @author VLadimir Malygin
 * @since 12.08.2013 15:57:09
 * @see <p>mailto: <a href="mailto:vmalygin@it.ru">vmalygin@it.ru</a></p>
 */
public class SendContentToPartnerService {

	private final static Logger logger = LoggerFactory.getLogger(SendContentToPartnerService.class);
	private final static String SPECOP = "SPECOP";
	private final static String EMAIL = "EMAIL";
	private final static String REGNUM_SERVICE_ID = "SIGNED_DOCFLOW_REGNUM";
	private final static String BJ_MESSAGE_SEND_ATTACHMENT = "#initiator направил контрагенту #object1 вложение #mainObject к документу #object2.";
	private final static String BJ_MESSAGE_SEND_CONTENT = "#initiator направил контрагенту #object1 файл #mainObject.";
	private final static String MAIL_SUBJ_DOCUMENT_ATTACHMENT = "Подписанные документы к договору \"%s\" (%s)";
	private final static String MAIL_SUBJ_CONTENT = "Подписанные документы (%s)";
	private final static QName ASSOC_CONTRACT_PARTNER = QName.createQName("http://www.it.ru/logicECM/contract/1.0", "partner-assoc");
	private final static NodeRef documentAttachmentMailTemplate = new NodeRef("workspace://SpacesStore/document-attachment-signatures-tmpl");
	private final static NodeRef contentMailTemplate = new NodeRef("workspace://SpacesStore/content-signatures-tmpl");
	private NodeService nodeService;
	private DocumentAttachmentsService documentAttachmentsService;
	private UnicloudService unicloudService;
	private BusinessJournalService businessJournalService;
	private JavaMailSender mailService;
	private TemplateService templateService;
	private SignedDocflow signedDocflowService;
	private FileFolderService fileFolderService;
	private ZipSignedContentService zipSignedContentService;
	private RegNumbersService regNumbersService;
	private String defaultFromEmail;

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
		PropertyCheck.mandatory(this, "regNumbersService", regNumbersService);
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

	public void setRegNumbersService(RegNumbersService regNumbersService) {
		this.regNumbersService = regNumbersService;
	}

	public void setDefaultFromEmail(String defaultFromEmail) {
		this.defaultFromEmail = defaultFromEmail;
	}

	private List<Map<String, Object>> sendUsingSPECOP(ContentToSendData contentToSend) {
		List<NodeRef> contentList = contentToSend.getContent();
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		NodeRef partnerRef = contentToSend.getPartner();

		for (NodeRef contentRef : contentList) {
			SendDocumentData sendDocumentData = unicloudService.sendDocument(contentRef, partnerRef);
			if (EResponseType.OK == sendDocumentData.getResponseType()) {
				//добавляем вложению documentId
				signedDocflowService.addDocflowIdsToContent(contentRef, sendDocumentData.getDocumentId(), sendDocumentData.getDocflowId());
				//вешаем блокировку на успешно отправленное вложение
				signedDocflowService.lockSignedContentRef(contentRef);
			}
			result.add(sendDocumentData.getProperties());
		}
		return result;
	}

	private List<Map<String, Object>> sendUsingEmail(ContentToSendData contentToSend) {
		String mailTemplate, mailSubject;
		File tempDir = null;
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		Map<String, Object> mailTemplateModel = new HashMap<String, Object>();
		List<NodeRef> contentList = contentToSend.getContent();

		for (NodeRef content : contentList) {
			GateResponse gateResponse = null;
			String regNumber = null;
			try {
				tempDir = Utils.createTmpDir();
				List<File> attachmentFiles = createAttachmentFiles(content, tempDir);
				NodeRef regnumTemplateRef = regNumbersService.getTemplateNodeByCode(REGNUM_SERVICE_ID);

				String contentName = (String) nodeService.getProperty(content, ContentModel.PROP_NAME);

				String existingDocumentID = (String) nodeService.getProperty(content, SignedDocflowModel.PROP_DOCUMENT_ID);
				if (existingDocumentID != null) {
					regNumber = existingDocumentID;
				} else {
					regNumber = regNumbersService.getNumber(null, regnumTemplateRef);
				}

				mailTemplateModel.put("signedDocument", contentName);

				if (contentToSend.isDocumentAttachment()) {
					mailTemplate = documentAttachmentMailTemplate.toString();
					NodeRef parentDocument = documentAttachmentsService.getDocumentByAttachment(contentList.get(0));
					String presentString = (String) nodeService.getProperty(parentDocument, DocumentService.PROP_PRESENT_STRING);
					mailTemplateModel.put("documentName", presentString);
					mailSubject = String.format(MAIL_SUBJ_DOCUMENT_ATTACHMENT, presentString.replaceAll("\\<.*?>", ""), regNumber);
				} else {
					mailTemplate = contentMailTemplate.toString();
					mailSubject = String.format(MAIL_SUBJ_CONTENT, regNumber);
				}

				String mailText = templateService.processTemplate("freemarker", mailTemplate, mailTemplateModel);
				MimeMessage message = mailService.createMimeMessage();
				MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
				helper.addTo(contentToSend.getEmail());
				helper.setFrom(defaultFromEmail);
				helper.setSubject(mailSubject);
				helper.setText(mailText, true);

				for (File attachment : attachmentFiles) {
					String attachmentName = MimeUtility.encodeText(attachment.getName(), "UTF-8", null);
					helper.addAttachment(attachmentName, attachment);
				}

				mailService.send(message);

				signedDocflowService.addDocflowIdsToContent(content, regNumber, null);
				signedDocflowService.lockSignedContentRef(content);
				addBusinessJournalRecord(content, contentToSend.getPartner());
			} catch (TemplateParseException ex) {
				logger.error("Error parsing template", ex);
				gateResponse = Utils.formErrorGateResponse(ex, EResponseType.INTERNAL_ERROR);
			} catch (TemplateRunException ex) {
				logger.error("Error running template", ex);
				gateResponse = Utils.formErrorGateResponse(ex, EResponseType.INTERNAL_ERROR);
			} catch (MessagingException ex) {
				logger.error("Error creating message", ex);
				gateResponse = Utils.formErrorGateResponse(ex, EResponseType.INTERNAL_ERROR);
			} catch (MailAuthenticationException ex) {
				logger.error("Error performing mail authentification", ex);
				gateResponse = Utils.formErrorGateResponse(ex, EResponseType.UNAUTHORIZED);
			} catch (MailSendException ex) {
				logger.error("Error sending mail", ex);
				gateResponse = Utils.formErrorGateResponse(ex, EResponseType.INTERNAL_ERROR);
			} catch (Exception ex) {
				logger.error("Error!", ex);
				gateResponse = Utils.formErrorGateResponse(ex, EResponseType.INTERNAL_ERROR);
			} finally {
				if (gateResponse == null) {
					gateResponse = new GateResponse();
					gateResponse.setResponseType(EResponseType.OK);
				}

				SendDocumentData sendDocumentData = new SendDocumentData(gateResponse);
				sendDocumentData.setDocumentId(regNumber);
				result.add(sendDocumentData.getProperties());
				FileUtils.deleteQuietly(tempDir);
			}
		}

		return result;
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

	public List<Map<String, Object>> send(ContentToSendData contentToSend) {
		List<NodeRef> contentList = contentToSend.getContent();
		NodeRef contentRef = contentList.get(0);
		NodeRef partnerRef = getEffectivePartner(contentRef, contentToSend.getPartner());
		String interactionType = getEffectiveInteractionType(partnerRef, contentToSend.getInteractionType());
		String email = getEffectiveEmail(partnerRef, contentToSend.getEmail());
		boolean documentAttachment = documentAttachmentsService.isDocumentAttachment(contentRef);
		contentToSend.setIsDocumentAttachment(documentAttachment);
		contentToSend.setPartner(partnerRef);
		contentToSend.setInteractionType(interactionType);
		contentToSend.setEmail(email);

		if (!documentAttachment) {
			for (NodeRef content : contentList) {
				Set<QName> aspects = nodeService.getAspects(content);
				if (!aspects.contains(SignedDocflowModel.ASPECT_CONTRACTOR_INTERACTION)) {
					Map<QName, Serializable> props = new HashMap<QName, Serializable>();
					props.put(SignedDocflowModel.PROP_CONTRACTOR_REF, partnerRef);
					props.put(SignedDocflowModel.PROP_INTERACTION_TYPE, interactionType);
					props.put(SignedDocflowModel.PROP_CONTRACTOR_EMAIL, email);
					nodeService.addAspect(content, SignedDocflowModel.ASPECT_CONTRACTOR_INTERACTION, props);
				}
			}
		}

		List<Map<String, Object>> result;
		if (SPECOP.equals(interactionType)) {
			result = sendUsingSPECOP(contentToSend);
		} else if (EMAIL.equals(interactionType)) {
			result = sendUsingEmail(contentToSend);
		} else {
			throw new IllegalArgumentException(String.format("%s is illegal interactionType with partner %s", interactionType, partnerRef));
		}
		return result;
	}

	private List<File> createAttachmentFiles(NodeRef content, File tempDir) {
		List<File> result = new ArrayList<File>();

		FileOutputStream zipFileOutput = null;
		File zipFile = null;
		try {
			String contentName = (String) nodeService.getProperty(content, ContentModel.PROP_NAME);
			zipFile = new File(tempDir, contentName + ".zip");
			zipFile.createNewFile();
			zipFileOutput = new FileOutputStream(zipFile);
			zipSignedContentService.writeZipToStream(zipFileOutput, content, true);
		} catch (IOException ex) {
			logger.error("Error creating ZIP file", ex);
		} finally {
			IOUtils.closeQuietly(zipFileOutput);
		}

		if (zipFile != null) {
			result.add(zipFile);
		}


		return result;
	}

	
}
