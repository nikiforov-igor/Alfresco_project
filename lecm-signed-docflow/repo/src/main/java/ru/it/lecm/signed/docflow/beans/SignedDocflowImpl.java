package ru.it.lecm.signed.docflow.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.lock.LockService;
import org.alfresco.service.cmr.lock.LockType;
import org.alfresco.service.cmr.lock.NodeLockedException;
import org.alfresco.service.cmr.lock.UnableToAquireLockException;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.cmr.version.VersionHistory;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;
import org.alfresco.util.ISO8601DateFormat;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.FileNameValidator;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.csp.signing.client.exception.CryptoException;
import ru.it.lecm.documents.beans.DocumentAttachmentsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.signed.docflow.SignedDocflowEventCategory;
import ru.it.lecm.signed.docflow.api.Signature;
import ru.it.lecm.signed.docflow.api.SignedDocflow;
import ru.it.lecm.signed.docflow.api.SignedDocflowModel;
import ru.it.lecm.signed.docflow.csp.CSPSigner;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

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
	private BusinessJournalService businessJournalService;
	private DocumentAttachmentsService documentAttachmentsService;
	private VersionService versionService;
	private ContentService contentService;
	private BehaviourFilter behaviourFilter;
	private LockService lockService;
	private String dsignWrapperPath;
    private String dsignTspUrl;
    private boolean dsignEnabled;
    private boolean dsignExchangeEnabled;

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

	public void setBehaviourFilter(BehaviourFilter behaviourFilter) {
		this.behaviourFilter = behaviourFilter;
	}

	public void setLockService(LockService lockService) {
		this.lockService = lockService;
	}

	public void setDsignWrapperPath(String dsignWrapperPath) {
        this.dsignWrapperPath = dsignWrapperPath;
    }

    public void setDsignTspUrl(String dsignTspUrl) {
        this.dsignTspUrl = dsignTspUrl;
    }

    public void setDsignEnabled(boolean dsignEnabled) {
        this.dsignEnabled = dsignEnabled;
    }

	public void setDsignExchangeEnabled(boolean dsignExchangeEnabled) {
		this.dsignExchangeEnabled = dsignExchangeEnabled;
	}

	public boolean isDsignExchangeEnabled() {
		return dsignExchangeEnabled;
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
		result.put("signResponse", "SIGN_FAILURE");
		if(signatureContent == null){
			return result;
		}

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
			boolean isOurSignature = StringUtils.containsIgnoreCase(signatureOwnerOrganization, organizationShortName);

			signatureProperties.put(SignedDocflowModel.PROP_IS_VALID, true);
			signatureProperties.put(SignedDocflowModel.PROP_UPDATE_DATE, signatureProperties.get(SignedDocflowModel.PROP_SIGNING_DATE));
			signatureProperties.put(SignedDocflowModel.PROP_IS_OUR, isOurSignature);
			signatureProperties.put(SignedDocflowModel.PROP_NAME, signatureName);
			signatureProperties.put(SignedDocflowModel.PROP_CONTENT_REF, contentRef);
			signatureProperties.put(ContentModel.PROP_IS_CONTENT_INDEXED, false);
			NodeRef documentRef = documentAttachmentsService.getDocumentByAttachment(contentRef);
			if (documentRef != null) {
				signatureProperties.put(SignedDocflowModel.PROP_DOCUMENT_REF, documentRef);
			}
			NodeRef signaturesFolder = getSignedDocflowFolder();
			QName assocQName = QName.createQName(SignedDocflowModel.SIGNED_DOCFLOW_NAMESPACE, UUID.randomUUID().toString());
			NodeRef signatureNode = nodeService.createNode(signaturesFolder, ContentModel.ASSOC_CONTAINS, assocQName, SignedDocflowModel.TYPE_SIGN, signatureProperties).getChildRef();

			ContentWriter contentWriter = contentService.getWriter(signatureNode, ContentModel.PROP_CONTENT, true);
			contentWriter.putContent(signatureContent);

			nodeService.createAssociation(signatureNode, contentRef, SignedDocflowModel.ASSOC_SIGN_TO_CONTENT);
			result.put("signatureNode", signatureNode);
			result.put("signResponse", "SIGN_OK");
		} else {
			result.put("signResponse", "SIGN_ALREADY_EXIST");
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

	private void addBusinessJournalRecord(NodeRef contentRef, NodeRef signatureRef, boolean loadSign) {
		final NodeRef baseDocumentRef = documentAttachmentsService.getDocumentByAttachment(contentRef);
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
                        //TODO замена нескольких setProperty на setProperties.
                        //DONE
                        Map<QName, Serializable> properties = nodeService.getProperties(signatureRef);
			properties.put(SignedDocflowModel.PROP_IS_VALID, isValid);
			properties.put(SignedDocflowModel.PROP_UPDATE_DATE, updateDate);
                        nodeService.setProperties(signatureRef, properties);
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

	@Override
	public boolean isOurSignature(NodeRef signatureRef) {
		return (Boolean) nodeService.getProperty(signatureRef, SignedDocflowModel.PROP_IS_OUR);
	}

	@Override
	public void addDocflowIdsToContent(final NodeRef contentRef, final String documentId, final String docflowId) {
		behaviourFilter.disableBehaviour(contentRef, ContentModel.ASPECT_VERSIONABLE);
		try {

			try {
				lockService.checkForLock(contentRef);
			} catch(NodeLockedException ex) {
				lockService.unlock(contentRef);
			}

			if (nodeService.hasAspect(contentRef, SignedDocflowModel.ASPECT_DOCFLOW_IDS)) {
                            //TODO замена нескольких setProperty на setProperties.
                            //DONE
                            Map<QName, Serializable> properties = nodeService.getProperties(contentRef);
                            properties.put(SignedDocflowModel.PROP_DOCUMENT_ID, documentId);
                            properties.put(SignedDocflowModel.PROP_DOCFLOW_ID, docflowId);
                            nodeService.setProperties(contentRef, properties);
			} else {
                            Map<org.alfresco.service.namespace.QName, Serializable> properties = new HashMap<org.alfresco.service.namespace.QName, Serializable>();
                            properties.put(SignedDocflowModel.PROP_DOCUMENT_ID, documentId);
                            properties.put(SignedDocflowModel.PROP_DOCFLOW_ID, docflowId);
                            nodeService.addAspect(contentRef, SignedDocflowModel.ASPECT_DOCFLOW_IDS, properties);
			}
		} finally {
			behaviourFilter.enableBehaviour(contentRef, ContentModel.ASPECT_VERSIONABLE);
			lockSignedContentRef(contentRef);
		}
	}

	@Override
	public void lockSignedContentRef(final NodeRef contentRef) {
		try {
			lockService.checkForLock(contentRef);
			lockService.lock(contentRef, LockType.READ_ONLY_LOCK, 0);
		} catch(NodeLockedException ex) {
			logger.debug("Node {} is already locked. Lock status is {}", contentRef, lockService.getLockStatus(contentRef));
		} catch(UnableToAquireLockException ex) {
			logger.error("Can't aquire lock for node", ex);
		}
	}

	@Override
        //TODO Refactoring in progress...
        //Вроде бы вызывается только в вебскрипте. транзакция уже должна быть
	public void saveAuthenticationData(final String organizationId, final String organizationEdoId, final String token) {
            NodeRef currentEmployeeRef = orgstructureService.getCurrentEmployee();
            //TODO замена нескольких setProperty на setProperties.
            //DONE
            Map<QName, Serializable> properties = nodeService.getProperties(currentEmployeeRef);
            properties.put(SignedDocflowModel.PROP_ORGANIZATION_ID, organizationId);
            properties.put(SignedDocflowModel.PROP_ORGANIZATION_EDO_ID, organizationEdoId);
            properties.put(SignedDocflowModel.PROP_AUTH_TOKEN, token);
            nodeService.setProperties(currentEmployeeRef, properties);
	}

    @Override
	public Map<String, Object> getHashes(List<NodeRef> refsToSignList) {
        if (!dsignEnabled) {
            throw new UnsupportedOperationException("getHashes() is disabled. Check property 'dsign.enabled' in 'alfresco-global.properties' file.");
        }
		Map<String, Object> result = new HashMap<>();
        for (NodeRef refToSignList : refsToSignList) {
            String hash = generateHash(refToSignList);
            if (!hash.isEmpty()) {
                result.put(hash, refToSignList);
            }
        }
		return result;
	}

    /**
	 * вычислить хэш файла по нодрефе
	 *
	 * @param refToSignList
	 * @return hashRef
	 */
    @Override
	public String generateHash(NodeRef refToSignList) {
		ContentReader sourceReader = contentService.getReader(refToSignList, ContentModel.PROP_CONTENT);
		try {
			byte[] sourceContentBytes = IOUtils.toByteArray(sourceReader.getContentInputStream());
            byte[] targetContentBytes = CSPSigner.getProcessor(dsignWrapperPath).hashGostr3411(sourceContentBytes);
			return Hex.encodeHexString(targetContentBytes).toUpperCase();
		} catch (IOException | CryptoException ex) {
			logger.error("", ex);
		}
		return "";
	}

    @Override
    public String getSTSAAddress() {
        return dsignTspUrl;
    }

}
