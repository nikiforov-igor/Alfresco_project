package ru.it.lecm.signed.docflow.beans;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.version.VersionHistory;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.FileNameValidator;
import org.alfresco.util.GUID;
import org.alfresco.util.ISO8601DateFormat;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.documents.beans.DocumentAttachmentsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.signed.docflow.api.Signature;
import ru.it.lecm.signed.docflow.SignedDocflowEventCategory;
import ru.it.lecm.signed.docflow.UnicloudService;
import ru.it.lecm.signed.docflow.api.SignedDocflow;
import ru.it.lecm.signed.docflow.api.SignedDocflowModel;

/**
 *
 * @author vlevin
 */
public class SignedDocflowImpl extends BaseBean implements SignedDocflow {

	private final static Logger logger = LoggerFactory.getLogger(SignedDocflowImpl.class);

	public final static String SIGNED_DOCFLOW_FOLDER = "SIGNED_DOCFLOW_FOLDER";
	private final static String BJ_MESSAGE_DOCUMENT_ATTACHMENT_SIGN = "#initiator подписал файл #mainobject к документу #object1.";
	private final static String BJ_MESSAGE_CONTENT_SIGN = "#initiator подписал ЭП вложение #mainobject.";
	private final static String BJ_MESSAGE_DOCUMENT_SIGN_LOAD = "#initiator загрузил подпись для файла: #mainobject к документу #object1.";
	private final static String BJ_MESSAGE_CONTENT_SIGN_LOAD = "#initiator загрузил подпись для файла: #mainobject.";
	private OrgstructureBean orgstructureService;
	private UnicloudService unicloudService;
	private BusinessJournalService businessJournalService;
	private DocumentAttachmentsService documentAttachmentsService;
	private VersionService versionService;
	private ContentService contentService;

	public void setUnicloudService(UnicloudService unicloudService) {
		this.unicloudService = unicloudService;
	}

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

	public void setDocumentAttachmentsService(DocumentAttachmentsService documentAttachmentsService) {
		this.documentAttachmentsService = documentAttachmentsService;
	}

	public void setVersionService(VersionService versionService) {
		this.versionService = versionService;
	}

	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
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
						if (!aspects.contains(SignedDocflowModel.ASPECT_ORGANIZATION_ATTRS)) {
							Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
							properties.put(SignedDocflowModel.PROP_OPERATOR_CODE, "");
							properties.put(SignedDocflowModel.PROP_PARTNER_KEY, "");
							properties.put(SignedDocflowModel.PROP_ORGANIZATION_ID, "");
							properties.put(SignedDocflowModel.PROP_ORGANIZATION_EDO_ID, "");
							properties.put(SignedDocflowModel.PROP_APPLET_LIC_KEY, "");
							properties.put(SignedDocflowModel.PROP_APPLET_CERT, "");
							properties.put(SignedDocflowModel.PROP_APPLET_CONTAINER, "");
							nodeService.addAspect(organizationRef, SignedDocflowModel.ASPECT_ORGANIZATION_ATTRS, properties);
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
						if (!aspects.contains(SignedDocflowModel.ASPECT_PERSONAL_DATA_ATTRS)) {
							Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
//							properties.put(SignedDocflowModel.PROP_AUTH_TOKEN, "");
//							properties.put(SignedDocflowModel.PROP_CERT_THUMBPRINT, "");
//							properties.put(SignedDocflowModel.PROP_AUTH_TYPE, "");
							nodeService.addAspect(personalDataRef, SignedDocflowModel.ASPECT_PERSONAL_DATA_ATTRS, properties);
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

	@Override
	public List<Signature> getSignatures(NodeRef signedContentRef) {
		List<AssociationRef> signAssocs = nodeService.getSourceAssocs(signedContentRef, SignedDocflowModel.ASSOC_SIGN_TO_CONTENT);

		List<Signature> signs = new ArrayList<Signature>();

		for (AssociationRef signAssoc : signAssocs) {
			Signature sign = new Signature();

			NodeRef signRef = signAssoc.getSourceRef();
			NodeRef contentRef = signAssoc.getTargetRef();

			Map<QName, Serializable> signProperties = nodeService.getProperties(signRef);

			sign.setNodeRef(signRef.toString());
			sign.setSignedContentName(nodeService.getProperty(contentRef, ContentModel.PROP_NAME).toString());

			sign.setOwner((String) signProperties.get(SignedDocflowModel.PROP_OWNER));
			sign.setOwnerPosition((String) signProperties.get(SignedDocflowModel.PROP_OWNER_POSITION));
			sign.setOwnerOrganization((String) signProperties.get(SignedDocflowModel.PROP_OWNER_ORGANIZATION));

			Date signingDate = (Date) signProperties.get(SignedDocflowModel.PROP_SIGNING_DATE);
			sign.setSigningDate(signingDate);
			sign.setSigningDateString(ISO8601DateFormat.format(signingDate));

			Date validFrom = (Date) signProperties.get(SignedDocflowModel.PROP_VALID_FROM);
			sign.setValidFrom(validFrom);
			sign.setValidFromString(ISO8601DateFormat.format(signingDate));

			Date validThrough = (Date) signProperties.get(SignedDocflowModel.PROP_VALID_THROUGH);
			sign.setValidThrough(validThrough);
			sign.setValidThroughString(ISO8601DateFormat.format(validThrough));

			Date updateDate = (Date) signProperties.get(SignedDocflowModel.PROP_UPDATE_DATE);
			sign.setUpdateDate(updateDate);
			sign.setUpdateDateString(ISO8601DateFormat.format(updateDate));

			sign.setSerialNumber((String) signProperties.get(SignedDocflowModel.PROP_SERIAL_NUMBER));
			sign.setCa((String) signProperties.get(SignedDocflowModel.PROP_CA));
			sign.setValid((Boolean) signProperties.get(SignedDocflowModel.PROP_IS_VALID));
			sign.setOur((Boolean) signProperties.get(SignedDocflowModel.PROP_IS_OUR));

			ContentReader contentReader = contentService.getReader(signRef, ContentModel.PROP_CONTENT);
			sign.setSignatureContent(contentReader.getContentString());

			sign.setFingerprint((String) signProperties.get(SignedDocflowModel.PROP_CERT_FINGERPRINT));

			signs.add(sign);
		}

		return signs;
	}

	@Override
	public Map<NodeRef, List<Signature>> getSignaturesInfo(List<NodeRef> nodeRefList) {
		Map<NodeRef, List<Signature>> result = new HashMap<NodeRef, List<Signature>>();
		for (NodeRef nodeRef : nodeRefList) {
			result.put(nodeRef, getSignatures(nodeRef));
		}
		return result;
	}

	@Override
	public void generateTestSigns(final NodeRef contentToSignRef) {

		RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper();
		transactionHelper.doInTransaction(new RetryingTransactionCallback<Void>() {
			@Override
			public Void execute() throws Throwable {

				Map<QName, Serializable> signProperties = new HashMap<QName, Serializable>();

				Calendar calendar = Calendar.getInstance();

				calendar.set(2011, Calendar.JANUARY, 1); // ????????? ? 2011
				Date signingDate = calendar.getTime();

				calendar.set(2012, Calendar.JANUARY, 1); // ????? ??????????? ? 2012
				Date validFrom = calendar.getTime();

				calendar.set(2013, Calendar.JANUARY, 1); // ????????????? ?? 2013
				Date validThrough = calendar.getTime();

				signProperties.put(SignedDocflowModel.PROP_OWNER, "Владелец Подписи");
				signProperties.put(SignedDocflowModel.PROP_OWNER_POSITION, "Директор");
				signProperties.put(SignedDocflowModel.PROP_OWNER_ORGANIZATION, "Организация");
				signProperties.put(SignedDocflowModel.PROP_SIGNING_DATE, signingDate);
				signProperties.put(SignedDocflowModel.PROP_SERIAL_NUMBER, "41155");
				signProperties.put(SignedDocflowModel.PROP_VALID_FROM, validFrom);
				signProperties.put(SignedDocflowModel.PROP_VALID_THROUGH, validThrough);
				signProperties.put(SignedDocflowModel.PROP_CA, "Центр Хороший");
				signProperties.put(SignedDocflowModel.PROP_UPDATE_DATE, new Date());
				signProperties.put(SignedDocflowModel.PROP_IS_VALID, true);
				signProperties.put(SignedDocflowModel.PROP_IS_OUR, false);

				ChildAssociationRef folderToSignChildAssoc = nodeService.createNode(getSignedDocflowFolder(), // ????
						ContentModel.ASSOC_CONTAINS, // ????
						QName.createQName(SignedDocflowModel.SIGNED_DOCFLOW_NAMESPACE, GUID.generate()), // ????
						SignedDocflowModel.TYPE_SIGN, signProperties); // ????

				nodeService.createAssociation(folderToSignChildAssoc.getChildRef(), contentToSignRef, SignedDocflowModel.ASSOC_SIGN_TO_CONTENT);

				signProperties.put(SignedDocflowModel.PROP_OWNER, "Иван Иванов");
				signProperties.put(SignedDocflowModel.PROP_OWNER_POSITION, "Работник Компании");
				signProperties.put(SignedDocflowModel.PROP_OWNER_ORGANIZATION, "Молоко и Продукты");
				signProperties.put(SignedDocflowModel.PROP_SIGNING_DATE, signingDate);
				signProperties.put(SignedDocflowModel.PROP_SERIAL_NUMBER, "911");
				signProperties.put(SignedDocflowModel.PROP_VALID_FROM, validFrom);
				signProperties.put(SignedDocflowModel.PROP_VALID_THROUGH, validThrough);
				signProperties.put(SignedDocflowModel.PROP_CA, "Центр Плохой");
				signProperties.put(SignedDocflowModel.PROP_UPDATE_DATE, new Date());
				signProperties.put(SignedDocflowModel.PROP_IS_VALID, true);
				signProperties.put(SignedDocflowModel.PROP_IS_OUR, true);

				folderToSignChildAssoc = nodeService.createNode(getSignedDocflowFolder(), // ????
						ContentModel.ASSOC_CONTAINS, // ????
						QName.createQName(SignedDocflowModel.SIGNED_DOCFLOW_NAMESPACE, GUID.generate()), // ????
						SignedDocflowModel.TYPE_SIGN, signProperties); // ????

				nodeService.createAssociation(folderToSignChildAssoc.getChildRef(), contentToSignRef, SignedDocflowModel.ASSOC_SIGN_TO_CONTENT);

				signProperties.put(SignedDocflowModel.PROP_OWNER, "Абдурахман Ибн Джальхад Хариди");
				signProperties.put(SignedDocflowModel.PROP_OWNER_POSITION, "Главный Жулик");
				signProperties.put(SignedDocflowModel.PROP_OWNER_ORGANIZATION, "Вечерние новости");
				signProperties.put(SignedDocflowModel.PROP_SIGNING_DATE, signingDate);
				signProperties.put(SignedDocflowModel.PROP_SERIAL_NUMBER, "8875");
				signProperties.put(SignedDocflowModel.PROP_VALID_FROM, validFrom);
				signProperties.put(SignedDocflowModel.PROP_VALID_THROUGH, validThrough);
				signProperties.put(SignedDocflowModel.PROP_CA, "Центр средней степени паршивости");
				signProperties.put(SignedDocflowModel.PROP_UPDATE_DATE, new Date());
				signProperties.put(SignedDocflowModel.PROP_IS_VALID, true);
				signProperties.put(SignedDocflowModel.PROP_IS_OUR, false);

				folderToSignChildAssoc = nodeService.createNode(getSignedDocflowFolder(), // ????
						ContentModel.ASSOC_CONTAINS, // ????
						QName.createQName(SignedDocflowModel.SIGNED_DOCFLOW_NAMESPACE, GUID.generate()), // ????
						SignedDocflowModel.TYPE_SIGN, signProperties); // ????

				nodeService.createAssociation(folderToSignChildAssoc.getChildRef(), contentToSignRef, SignedDocflowModel.ASSOC_SIGN_TO_CONTENT);

				return null;
			}
		});
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
		return aspects.contains(SignedDocflowModel.ASPECT_DOCFLOWABLE);
	}

	@Override
	public boolean isSignable(NodeRef nodeRef) {
		Set<QName> aspects = nodeService.getAspects(nodeRef);
		return aspects.contains(SignedDocflowModel.ASPECT_SIGNABLE);
	}

	private Map<String, Object> sign(final Map<QName, Serializable> signatureProperties) {
		final String signatureContent = (String) signatureProperties.remove(ContentModel.PROP_CONTENT);
		final String contentRefStr = (String) signatureProperties.remove(SignedDocflowModel.ASSOC_SIGN_TO_CONTENT);
		final NodeRef contentRef = new NodeRef(contentRefStr);

		String serialNumber = (String)signatureProperties.get(SignedDocflowModel.PROP_SERIAL_NUMBER);
		String signatureName = createSignatureName(serialNumber, contentRef);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("contentRef", contentRef);
		result.put("name", nodeService.getProperty(contentRef, ContentModel.PROP_NAME));
//		Map<String, Object> verifySignatureResponse = unicloudService.verifySignature(contentRefStr, signatureContent);
//		if (verifySignatureResponse.containsKey("isSignatureValid")) {
//			boolean isSignatureValid = (Boolean) verifySignatureResponse.get("isSignatureValid");
//			if (isSignatureValid) {

		// проверяем, прикрепрена ли уже к контенту валидна подпись с фингерпринтом, совпадающим с нашим
		boolean alreadySigned = false;
		String currentFingerprint = (String) signatureProperties.get(SignedDocflowModel.PROP_CERT_FINGERPRINT);
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
			String signatureOwnerOrganization = (String) signatureProperties.get(SignedDocflowModel.PROP_OWNER_ORGANIZATION);
			boolean isOurSignature = StringUtils.containsIgnoreCase(organizationShortName, signatureOwnerOrganization);

			signatureProperties.put(SignedDocflowModel.PROP_IS_VALID, true);
			signatureProperties.put(SignedDocflowModel.PROP_UPDATE_DATE, signatureProperties.get(SignedDocflowModel.PROP_SIGNING_DATE));
			signatureProperties.put(SignedDocflowModel.PROP_IS_OUR, isOurSignature);
			signatureProperties.put(ContentModel.PROP_NAME, signatureName);
			signatureProperties.put(SignedDocflowModel.PROP_CONTENT_REF, contentRef);
			NodeRef documentRef = getDocumentRef(contentRef);
			if (documentRef != null) {
				signatureProperties.put(SignedDocflowModel.PROP_DOCUMENT_REF, getDocumentRef(contentRef));
			}
			NodeRef signaturesFolder = getSignedDocflowFolder();
			QName assocQName = QName.createQName(SignedDocflowModel.SIGNED_DOCFLOW_NAMESPACE, UUID.randomUUID().toString());
			NodeRef signatureNode = nodeService.createNode(signaturesFolder, ContentModel.ASSOC_CONTAINS, assocQName, SignedDocflowModel.TYPE_SIGN, signatureProperties).getChildRef();

			ContentWriter contentWriter = contentService.getWriter(signatureNode, ContentModel.PROP_CONTENT, true);
			contentWriter.putContent(signatureContent);

			nodeService.createAssociation(signatureNode, contentRef, SignedDocflowModel.ASSOC_SIGN_TO_CONTENT);
			result.put("signatureNode", signatureNode);
			result.put("success", true);
		} else {
			result.put("success", false);
		}

		return result;

	}

	@Override
	public Map<String, Object> signContent(final Map<QName, Serializable> signatureProperties) {
		Map<String, Object> result = sign(signatureProperties);
		final NodeRef contentRef = (NodeRef) result.remove("contentRef");
		final NodeRef signatureNode = (NodeRef) result.remove("signatureNode");
		if(signatureNode != null) addBusinessJournalRecord(contentRef, signatureNode, false);
		return result;
	}

	@Override
	public Map<String, Object> loadSign(Map<QName, Serializable> signatureProperties) {
		Map<String, Object> result = sign(signatureProperties);
		final NodeRef contentRef = (NodeRef) result.remove("contentRef");
		final NodeRef signatureNode = (NodeRef) result.remove("signatureNode");
		if(signatureNode != null) addBusinessJournalRecord(contentRef, signatureNode, true);
		return result;
	}

	/**
	 * построить уникальное, человекочитабельное имя для подписи
	 * @param serialNumber уникальный ID, который генерит при создании сертификата удостоверяющий центр.
	 * @param contentRef контент, к которому эта подпись относится
	 * @return имя подписи, в виде "Подпись ${serialNumber} для контента ${content.name} версии ${content.version}"
	 */
	private String createSignatureName(final String serialNumber, final NodeRef contentRef) {
		String contentName = (String) nodeService.getProperty(contentRef, ContentModel.PROP_NAME);
		VersionHistory history = versionService.getVersionHistory(contentRef);
		String version = (history != null) ? history.getHeadVersion().getVersionLabel() : "1.0";
		String date = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
		String signatureName = String.format("Подпись %s для контента %s версии %s от %s", serialNumber, contentName, version, date);
		return FileNameValidator.getValidFileName(signatureName);
	}

	private NodeRef getDocumentRef(final NodeRef contentRef) {
		return documentAttachmentsService.getDocumentByAttachment(contentRef);
	}

	private void addBusinessJournalRecord(NodeRef contentRef, NodeRef signatureRef, boolean loadSign) {
		final NodeRef baseDocumentRef = getDocumentRef(contentRef);
		final String messageTemplate;
		final List<String> objects = new ArrayList<String>();

		if (!loadSign) {
			if (baseDocumentRef != null) {
				messageTemplate = BJ_MESSAGE_DOCUMENT_ATTACHMENT_SIGN;
				objects.add(baseDocumentRef.toString());
			} else {
				messageTemplate = BJ_MESSAGE_CONTENT_SIGN;
			}

			objects.add(signatureRef.toString());

			businessJournalService.log(authService.getCurrentUserName(), contentRef, SignedDocflowEventCategory.SIGNATURE, messageTemplate, objects);
		} else {
			if (baseDocumentRef != null) {
				messageTemplate = BJ_MESSAGE_DOCUMENT_SIGN_LOAD;
				objects.add(baseDocumentRef.toString());
			} else {
				messageTemplate = BJ_MESSAGE_CONTENT_SIGN_LOAD;
			}

			objects.add(signatureRef.toString());

			businessJournalService.log(authService.getCurrentUserName(), contentRef, SignedDocflowEventCategory.SIGNATURE, messageTemplate, objects);
		}
	}

	@Override
	public List<NodeRef> getSignaturesByContent(NodeRef contentRef) {
		return findNodesByAssociationRef(contentRef, SignedDocflowModel.ASSOC_SIGN_TO_CONTENT, SignedDocflowModel.TYPE_SIGN, BaseBean.ASSOCIATION_TYPE.SOURCE);
	}

	@Override
	public String getFingerprintBySignature(NodeRef signatureRef) {
		return (String) nodeService.getProperty(signatureRef, SignedDocflowModel.PROP_CERT_FINGERPRINT);
	}

	@Override
	public NodeRef getContentBySignature(NodeRef signatureRef) {
		return findNodeByAssociationRef(signatureRef, SignedDocflowModel.ASSOC_SIGN_TO_CONTENT, ContentModel.TYPE_CONTENT, BaseBean.ASSOCIATION_TYPE.TARGET);
	}

	@Override
	public String getSignatureContentBySignature(NodeRef signatureRef) {
		return contentService.getReader(signatureRef, ContentModel.PROP_CONTENT).getContentString();
	}

	@Override
	public boolean isSignatureValid(NodeRef signatureRef) {
		return (Boolean) nodeService.getProperty(signatureRef, SignedDocflowModel.PROP_IS_VALID);
	}

	@Override
	public boolean updateSignature(NodeRef signatureRef, String updateDate, boolean isValid) {
		try {
			nodeService.setProperty(signatureRef, SignedDocflowModel.PROP_IS_VALID, isValid);
			nodeService.setProperty(signatureRef, SignedDocflowModel.PROP_UPDATE_DATE, updateDate);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	@Override
	public Map<String, String> updateSignatures(JSONArray json) {
		Map<String, String> result = new HashMap<String, String>();
		result.put("result", "success");
		for (int i = 0; i < json.length(); i++) {
			try {
				NodeRef signRef = new NodeRef(json.getJSONObject(i).getString("signatureNodeRef"));
				String signingDate = json.getJSONObject(i).getString("updateDate");
				String isValid = json.getJSONObject(i).getString("isValid");
				if(!updateSignature(signRef, signingDate, Boolean.parseBoolean(isValid))){
					result.put("result", "failure");
				}

			} catch (JSONException ex) {
				logger.error("Error parsing json", ex);
				result.put("ERROR", "Somethig goes bad");
			}
		}
		return result;
	}


}
