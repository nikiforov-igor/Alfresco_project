package ru.it.lecm.signed.docflow.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.lang.StringUtils;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.documents.beans.DocumentAttachmentsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.signed.docflow.SignedDocflowEventCategory;
import ru.it.lecm.signed.docflow.UnicloudService;
import ru.it.lecm.signed.docflow.api.SignedDocflow;
import static ru.it.lecm.signed.docflow.api.SignedDocflow.ASSOC_SIGN_TO_CONTENT;

/**
 *
 * @author vlevin
 */
public class SignedDocflowImpl extends BaseBean implements SignedDocflow {

	public final static String SIGNED_DOCFLOW_FOLDER = "SIGNED_DOCFLOW_FOLDER";
	private final static String BJ_MESSAGE_DOCUMENT_ATTACHMENT_SIGN = "#initiator подписал файл #mainobject к документу #object1.";
	private final static String BJ_MESSAGE_CONTENT_SIGN = "#initiator подписал ЭП вложение #mainobject.";
	private OrgstructureBean orgstructureService;
	private UnicloudService unicloudService;
	private BusinessJournalService businessJournalService;
	private DocumentAttachmentsService documentAttachmentsService;

	public void setUnicloudService(UnicloudService unicloudService) {
		this.unicloudService = unicloudService;
	}

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

	public void setDocumentAttachmentsService(DocumentAttachmentsService documentAttachmentsService) {
		this.documentAttachmentsService = documentAttachmentsService;
	}

	@Override
	public void addAttributesToOrganization() {
		AuthenticationUtil.runAsSystem(new RunAsWork<Void>() {
			@Override
			public Void doWork() throws Exception {
				RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper();
				return transactionHelper.doInTransaction(new RetryingTransactionCallback<Void>() {
					@Override
					public Void execute() throws Throwable {
						NodeRef organizationRef = orgstructureService.getOrganization();
						Set<QName> aspects = nodeService.getAspects(organizationRef);
						if (!aspects.contains(ASPECT_ORGANIZATION_ATTRS)) {
							Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
							properties.put(PROP_OPERATOR_CODE, "");
							properties.put(PROP_PARTNER_KEY, "");
							properties.put(PROP_ORGANIZATION_ID, "");
							properties.put(PROP_ORGANIZATION_EDO_ID, "");
							properties.put(PROP_APPLET_LIC_KEY, "");
							properties.put(PROP_APPLET_CERT, "");
							properties.put(PROP_APPLET_CONTAINER, "");
							nodeService.addAspect(organizationRef, ASPECT_ORGANIZATION_ATTRS, properties);
						}
						return null;
					}
				}, false, true);
			}
		});
	}

	@Override
	public void addAttributesToPersonalData() {
		AuthenticationUtil.runAsSystem(new RunAsWork<Void>() {

			@Override
			public Void doWork() throws Exception {
				RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper();
				return transactionHelper.doInTransaction(new RetryingTransactionCallback<Void>() {

					@Override
					public Void execute() throws Throwable {
						NodeRef currentEmployeeRef = orgstructureService.getCurrentEmployee();
						NodeRef personalDataRef = orgstructureService.getEmployeePersonalData(currentEmployeeRef);
						Set<QName> aspects = nodeService.getAspects(personalDataRef);
						if(!aspects.contains(SignedDocflow.ASPECT_PERSONAL_DATA_ATTRS)) {
							Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
//							properties.put(SignedDocflow.PROP_AUTH_TOKEN, "");
//							properties.put(SignedDocflow.PROP_CERT_THUMBPRINT, "");
//							properties.put(SignedDocflow.PROP_AUTH_TYPE, "");
							nodeService.addAspect(personalDataRef, SignedDocflow.ASPECT_PERSONAL_DATA_ATTRS, properties);
						}
						return null;
					}
				}, false, true);
			}
		});
	}

	public void init() {
		PropertyCheck.mandatory(this, "nodeService", nodeService);
		PropertyCheck.mandatory(this, "transactionService", transactionService);
		PropertyCheck.mandatory(this, "unicloudService", unicloudService);
		PropertyCheck.mandatory(this, "businessJournalService", businessJournalService);

		addAttributesToOrganization();
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	@Override
	public NodeRef getSignedDocflowFolder() {
		return getFolder(SIGNED_DOCFLOW_FOLDER);
	}

	@Override
	public NodeRef getServiceRootFolder() {
		return getSignedDocflowFolder();
	}

	@Override
	public boolean isDocflowable(NodeRef nodeRef) {
		Set<QName> aspects = nodeService.getAspects(nodeRef);
		return aspects.contains(ASPECT_DOCFLOWABLE);
	}

	@Override
	public boolean isSignable(NodeRef nodeRef) {
		Set<QName> aspects = nodeService.getAspects(nodeRef);
		return aspects.contains(ASPECT_SIGNABLE);
	}

	@Override
	public void addDocflowableAspect(NodeRef nodeRef) {
		nodeService.addAspect(nodeRef, ASPECT_DOCFLOWABLE, null);
	}

	@Override
	public void removeDocflowableAspect(NodeRef nodeRef) {
		nodeService.removeAspect(nodeRef, ASPECT_DOCFLOWABLE);
	}

	@Override
	public void addSignableAspect(NodeRef nodeRef) {
		nodeService.addAspect(nodeRef, ASPECT_SIGNABLE, null);
	}

	@Override
	public void removeSignableAspect(NodeRef nodeRef) {
		nodeService.removeAspect(nodeRef, ASPECT_SIGNABLE);
	}

	@Override
	public Map<String, Object> signContent(final Map<QName, Serializable> signatureProperties) {
//		final String signatureContent = (String) signatureProperties.get(PROP_SIGNATURE_CONTENT);
		final String contentRefStr = (String) signatureProperties.remove(ASSOC_SIGN_TO_CONTENT);
		final NodeRef contentRef = new NodeRef(contentRefStr);

		Map<String, Object> result = new HashMap<String, Object>();

//		Map<String, Object> verifySignatureResponse = unicloudService.verifySignature(contentRefStr, signatureContent);
//		if (verifySignatureResponse.containsKey("isSignatureValid")) {
//			boolean isSignatureValid = (Boolean) verifySignatureResponse.get("isSignatureValid");
//			if (isSignatureValid) {

		// проверяем, прикрепрена ли уже к контенту валидна подпись с фингерпринтом, совпадающим с нашим
		boolean alreadySigned = false;
		String currentFingerprint = (String) signatureProperties.get(PROP_CERT_FINGERPRINT);
		List<NodeRef> signaturesAttachedToContent = getSignaturesByContent(contentRef);
		for (NodeRef attachedSignature : signaturesAttachedToContent) {
			if (!isSignatureValid(attachedSignature)) {
				continue;
			}
			String attachedFingerprint = getFingerprintBySignature(attachedSignature);
			if (currentFingerprint.equalsIgnoreCase(attachedFingerprint)) {
				alreadySigned = true;
				break;
			}
		}

		if (!alreadySigned) {

			// проверяем, принадлежит ли подпись нашей организации
			// считается, что подпись наша, если организация из атрибутивного состава подписи входит в сокращенное название нашей организации
			String organizationShortName = orgstructureService.getOrganizationShortName();
			String signatureOwnerOrganization = (String) signatureProperties.get(PROP_OWNER_ORGANIZATION);
			boolean isOurSignature = StringUtils.containsIgnoreCase(organizationShortName, signatureOwnerOrganization);

			signatureProperties.put(PROP_IS_VALID, true);
			signatureProperties.put(PROP_UPDATE_DATE, signatureProperties.get(PROP_SIGNING_DATE));
			signatureProperties.put(PROP_IS_OUR, isOurSignature);
			NodeRef signaturesFolder = getSignedDocflowFolder();
			QName assocQName = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, UUID.randomUUID().toString());
			NodeRef signatureNode = nodeService.createNode(signaturesFolder, ContentModel.ASSOC_CONTAINS, assocQName, TYPE_SIGN, signatureProperties).getChildRef();
			nodeService.createAssociation(signatureNode, contentRef, ASSOC_SIGN_TO_CONTENT);
			addBusinessJournalRecord(contentRef, signatureNode);
			result.put("success", true);
		} else {
			result.put("success", false);
		}
//			} else {
//				throw new WebScriptException(String.format("Error verifying signature: pair document [%s] signature was corrupted on the way to server", contentRefStr));
//			}
//		}

		return result;
	}

	private void addBusinessJournalRecord(NodeRef contentRef, NodeRef signatureRef) {
		final NodeRef baseDocumentRef = documentAttachmentsService.getDocumentByAttachment(contentRef);
		final String messageTemplate;
		final List<String> objects = new ArrayList<String>();

		if (baseDocumentRef != null) {
			messageTemplate = BJ_MESSAGE_DOCUMENT_ATTACHMENT_SIGN;
			objects.add(baseDocumentRef.toString());
		} else {
			messageTemplate = BJ_MESSAGE_CONTENT_SIGN;
		}

		objects.add(signatureRef.toString());

		businessJournalService.log(authService.getCurrentUserName(), contentRef, SignedDocflowEventCategory.SIGNATURE, messageTemplate, objects);
	}

	@Override
	public List<NodeRef> getSignaturesByContent(NodeRef contentRef) {
		return findNodesByAssociationRef(contentRef, ASSOC_SIGN_TO_CONTENT, TYPE_SIGN, BaseBean.ASSOCIATION_TYPE.SOURCE);
	}

	@Override
	public String getFingerprintBySignature(NodeRef signatureRef) {
		return (String) nodeService.getProperty(signatureRef, PROP_CERT_FINGERPRINT);
	}

	@Override
	public NodeRef getContentBySignature(NodeRef signatureRef) {
		return findNodeByAssociationRef(signatureRef, ASSOC_SIGN_TO_CONTENT, ContentModel.TYPE_CONTENT, BaseBean.ASSOCIATION_TYPE.TARGET);
	}

	@Override
	public String getSignatureContentBySignature(NodeRef signatureRef) {
		return (String) nodeService.getProperty(signatureRef, PROP_SIGNATURE_CONTENT);
	}

	@Override
	public boolean isSignatureValid(NodeRef signatureRef) {
		return (Boolean) nodeService.getProperty(signatureRef, PROP_IS_VALID);
	}
}
