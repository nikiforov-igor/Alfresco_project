package ru.it.lecm.signed.docflow;

import com.microsoft.schemas.serialization.arrays.ArrayOfbase64Binary;
import com.microsoft.schemas.serialization.arrays.ArrayOfguid;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.ws.Holder;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.headers.Header;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tempuri.IGateWcfService;
import ru.it.lecm.contractors.api.Contractors;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.signed.docflow.api.Signature;
import ru.it.lecm.signed.docflow.api.SignedDocflow;
import ru.it.lecm.signed.docflow.api.SignedDocflowModel;
import ru.it.lecm.signed.docflow.model.AuthHeaders;
import ru.it.lecm.signed.docflow.model.AuthenticationData;
import ru.it.lecm.signed.docflow.model.ReceiveDocumentData;
import ru.it.lecm.signed.docflow.model.SendDocumentData;
import ru.it.lecm.signed.docflow.model.SignatureData;
import ru.it.lecm.signed.docflow.model.UnicloudData;
import ucloud.gate.proxy.ArrayOfDocflowInfoBase;
import ucloud.gate.proxy.ArrayOfDocumentInfo;
import ucloud.gate.proxy.ArrayOfOperatorInfo;
import ucloud.gate.proxy.CompanyInfo;
import ucloud.gate.proxy.DocflowInfoBase;
import ucloud.gate.proxy.DocumentContent;
import ucloud.gate.proxy.DocumentInfo;
import ucloud.gate.proxy.DocumentToSend;
import ucloud.gate.proxy.EDocumentType;
import ucloud.gate.proxy.OperatorInfo;
import ucloud.gate.proxy.WorkspaceFilter;
import ucloud.gate.proxy.docflow.EDocflowTransactionType;
import ucloud.gate.proxy.exceptions.EResponseType;
import ucloud.gate.proxy.exceptions.GateResponse;

/**
 * высокоуровневый сервис, обертка для SOAP-клиента
 * @author VLadimir Malygin
 * @since 31.07.2013 12:37:34
 * @see <p>mailto: <a href="mailto:vmalygin@it.ru">vmalygin@it.ru</a></p>
 */
public class UnicloudService {

	private final static Logger logger = LoggerFactory.getLogger(UnicloudService.class);

	private IGateWcfService gateWcfService;
	private NodeService nodeService;
	private OrgstructureBean orgstructureService;
	private ContentService contentService;
	private SignedDocflow signedDocflowService;

	public void setGateWcfService(IGateWcfService gateWcfService) {
		this.gateWcfService = gateWcfService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}

	public void setSignedDocflowService(SignedDocflow signedDocflowService) {
		this.signedDocflowService = signedDocflowService;
	}

	public void init() {
//		if (logger.isTraceEnabled()) {
//			try {
//				Holder<GateResponse> gateResponse = new Holder<GateResponse>();
//				Holder<String> version = new Holder<String>();
//				gateWcfService.getServiceVersion(gateResponse, version);
//				if (version.value != null) {
//					logger.trace(version.value);
//				} else {
//					Utils.logGateResponse(gateResponse, logger);
//				}
//
//				gateResponse = new Holder<GateResponse>();
//				Holder<ArrayOfOperatorInfo> operatorsHolder = new Holder<ArrayOfOperatorInfo>();
//				gateWcfService.getOperators(gateResponse, operatorsHolder);
//				if (operatorsHolder.value != null) {
//				List<OperatorInfo> operators = operatorsHolder.value.getOperatorInfos();
//					for (OperatorInfo operator : operators) {
//						logger.trace("AuthenticationType = {}", operator.getAuthenticationType());
//						logger.trace("CertificateIssuerName = {}", operator.getCertificateIssuerName());
//						logger.trace("Code = {}", operator.getCode());
//						logger.trace("Extension = {}", operator.getExtension());
//						logger.trace("Inn = {}", operator.getInn());
//						logger.trace("IsRemoteSignEnabled = {}", operator.isIsRemoteSignEnabled());
//						logger.trace("Name = {}", operator.getName());
//					}
//				} else {
//					Utils.logGateResponse(gateResponse, logger);
//				}
//			} catch (Exception ex) {
//				logger.error(ex.getMessage());
//			}
//		}
	}

	/**
	 *
	 * @param partner (обязательный заголовок) - идентификатор партнера. Выдается компанией Unicloud после заключения договора.
	 * @param user идентификатор пользователя в партнерской системе.
	 * @param organization идентификатор организации в сервисе Unicloud Gate.
	 * @param operator код спецоператора ЭДО
	 * @param token токен авторизации
	 */
	private void addAuthHeaders(AuthHeaders authHeaders) {
		try {
			Client proxy = ClientProxy.getClient(gateWcfService);
			List<Header> headersList = new ArrayList<Header>();

			headersList.add(new Header(new QName("uauth", "partner"), authHeaders.getPartner(), new JAXBDataBinding(String.class)));
			headersList.add(new Header(new QName("uauth", "user"), authHeaders.getUser(), new JAXBDataBinding(String.class)));
			headersList.add(new Header(new QName("uauth", "organization"), authHeaders.getOrganization(), new JAXBDataBinding(String.class)));
			headersList.add(new Header(new QName("uauth", "token_".concat(authHeaders.getOperator())), authHeaders.getToken(), new JAXBDataBinding(String.class)));

			proxy.getRequestContext().put(Header.HEADER_LIST, headersList);
		} catch(JAXBException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

	private void removeAuthHeaders() {
		Client proxy = ClientProxy.getClient(gateWcfService);
		List<Header> headersList = (List<Header>)proxy.getRequestContext().remove(Header.HEADER_LIST);
		if (logger.isTraceEnabled() && headersList != null) {
			logger.trace("These headers was removed from request context");
			for (Header header : headersList) {
				logger.trace("Header {}, value {}", header.getName(), header.getObject());
			}
		}
	}

	private AuthenticationData registerUserByCertificate(final String operatorCode, final String partnerKey, final String organizationInn, final String organizationKpp, final byte[] sign, final String userId) {
		Holder<GateResponse> gateResponse = new Holder<GateResponse>();
		Holder<String> organizationId = new Holder<String>();
		Holder<String> organizationEdoId = new Holder<String>();
		gateWcfService.registerUserByCertificate(operatorCode, partnerKey, organizationInn, organizationKpp, sign, userId, gateResponse, organizationId, organizationEdoId);
		AuthenticationData authentication;
		if (gateResponse.value != null) {
			authentication = new AuthenticationData(gateResponse.value);
			if (EResponseType.OK == gateResponse.value.getResponseType()) {
				authentication.setOrganizationId(organizationId.value);
				authentication.setOrganizationEdoId(organizationEdoId.value);
			} else {
				Utils.logGateResponse(gateResponse, logger);
			}
		} else {
			String msg = "Error invoking unicloud gate. GateResponse can't be null!";
			logger.error(msg);
			throw new IllegalStateException(msg);
		}
		return authentication;
	}

	public AuthenticationData authenticateByCertificate(final String guidSignBase64, final String timestamp, final String timestampSignBase64) {

		byte[] guidSign = Base64.decodeBase64(guidSignBase64);
		byte[] timestampSign = Base64.decodeBase64(timestampSignBase64);


		NodeRef organizationRef = orgstructureService.getOrganization();
		NodeRef employeeRef = orgstructureService.getCurrentEmployee();
		String operatorCode = (String)nodeService.getProperty(employeeRef, SignedDocflowModel.PROP_OPERATOR_CODE);
		String partnerKey = (String)nodeService.getProperty(employeeRef, SignedDocflowModel.PROP_PARTNER_KEY);
		String inn = (String)nodeService.getProperty(organizationRef, OrgstructureBean.PROP_ORG_TIN);
		String kpp = (String)nodeService.getProperty(organizationRef, OrgstructureBean.PROP_ORG_KPP);
		String employeeId = employeeRef.getId();

		AuthenticationData auth = registerUserByCertificate(operatorCode, partnerKey, inn, kpp, guidSign, employeeId);
		if (EResponseType.OK == auth.getResponseType()) {
			Holder<GateResponse> gateResponse = new Holder<GateResponse>();
			Holder<String> token = new Holder<String>();
			gateWcfService.authenticateByCertificate(operatorCode, timestampSign, timestamp, gateResponse, token);
			if (gateResponse.value != null) {
				auth.setGateResponse(gateResponse.value);
				if (EResponseType.OK == gateResponse.value.getResponseType()) {
					auth.setToken(token.value);
					signedDocflowService.saveAuthenticationData(auth.getOrganizationId(), auth.getOrganizationEdoId(), auth.getToken());
				} else {
					Utils.logGateResponse(gateResponse, logger);
				}
			} else {
				String msg = "Error invoking unicloud gate. GateResponse can't be null!";
				logger.error(msg);
				throw new IllegalStateException(msg);
			}
		}
		return auth;
	}

	public SignatureData verifySignature(String contentRef, String signature) {
		NodeRef employeeRef = orgstructureService.getCurrentEmployee();
		String operatorCode = (String)nodeService.getProperty(employeeRef, SignedDocflowModel.PROP_OPERATOR_CODE);

		ContentReader reader = contentService.getReader(new NodeRef(contentRef), ContentModel.PROP_CONTENT);
		byte[] sign = Base64.decodeBase64(signature);
		byte[] content;
		if (reader != null) {
			try {
				content = IOUtils.toByteArray(reader.getContentInputStream());
			} catch(Exception ex) {
				String msg = "Can't read nodeRef's content";
				logger.error("{}. Caused by: ", msg, ex.getMessage());
				throw new IllegalArgumentException(msg, ex);
			}
		} else {
			throw new IllegalStateException("Content can't be null.");
		}

		SignatureData signatureData = new SignatureData();

		Holder<GateResponse> gateResponse = new Holder<GateResponse>();
		Holder<String> signerInfo = new Holder<String>();
		Holder<Boolean> isSignatureValid = new Holder<Boolean>();
		gateWcfService.verifySignature(content, sign, operatorCode, gateResponse, signerInfo, isSignatureValid);
		if (gateResponse.value != null) {
			signatureData.setGateResponse(gateResponse.value);
			if (EResponseType.OK == gateResponse.value.getResponseType()) {
				signatureData.setSignerInfo(signerInfo.value);
				signatureData.setIsSignatureValid(isSignatureValid.value);
			} else {
				Utils.logGateResponse(gateResponse, logger);
			}
		} else {
			String msg = "Error invoking unicloud gate. GateResponse can't be null!";
			logger.error(msg);
			throw new IllegalStateException(msg);
		}

		return signatureData;
	}

	private <T extends UnicloudData> T checkAuthenticationData(final AuthHeaders authHeaders, final Class<T> unicloudDataClass) {
		T unicloudData = null;
		String organizationId = authHeaders.getOrganization();
		String token = authHeaders.getToken();
		if (StringUtils.isEmpty(organizationId) || StringUtils.isEmpty(token)) {
			String orgIdMsg = StringUtils.isEmpty(organizationId) ? "Organization Id not found. " : "";
			String tokenMsg = StringUtils.isEmpty(token) ? "Token not found. " : "";
			GateResponse fakeResponse = new GateResponse();
			fakeResponse.setMessage(String.format("%s%sMaybe you are not authorized, try again", orgIdMsg, tokenMsg));
			fakeResponse.setOperatorMessage(null);
			fakeResponse.setResponseType(EResponseType.UNAUTHORIZED);
			fakeResponse.setStackTrace(ExceptionUtils.getStackTrace(new Exception()));

			try {
				unicloudData = unicloudDataClass.newInstance();
				unicloudData.setGateResponse(fakeResponse);
			} catch (IllegalAccessException ex) {
				logger.error(ex.getMessage(), ex);
			} catch (InstantiationException ex) {
				logger.error(ex.getMessage(), ex);
			}
		}

		return unicloudData;
	}

	public SendDocumentData sendDocument(final NodeRef contentRef, final NodeRef contractorRef) {
		NodeRef employeeRef = orgstructureService.getCurrentEmployee();
		AuthHeaders authHeaders = new AuthHeaders();
		authHeaders.setPartner((String)nodeService.getProperty(employeeRef, SignedDocflowModel.PROP_PARTNER_KEY));
		authHeaders.setUser(employeeRef.getId());
		authHeaders.setOrganization((String)nodeService.getProperty(employeeRef, SignedDocflowModel.PROP_ORGANIZATION_ID));
		authHeaders.setOperator((String)nodeService.getProperty(employeeRef, SignedDocflowModel.PROP_OPERATOR_CODE));
		authHeaders.setToken((String)nodeService.getProperty(employeeRef, SignedDocflowModel.PROP_AUTH_TOKEN));
		String docflowId = UUID.randomUUID().toString(); //каждая отправка документа инициирует новый docflowID

		SendDocumentData sendDocumentData = checkAuthenticationData(authHeaders, SendDocumentData.class);
		if (sendDocumentData != null) {
			return sendDocumentData;
		}

		//получить ИНН и КПП контрагента
		CompanyInfo companyInfo = new CompanyInfo();
		companyInfo.setInn((String)nodeService.getProperty(contractorRef, Contractors.PROP_CONTRACTOR_INN));
		companyInfo.setKpp((String)nodeService.getProperty(contractorRef, Contractors.PROP_CONTRACTOR_KPP));

		//получаем список подписей нашей организации
		//подписи уже проверены на клиенте и валидны
		List<Signature> signatures = signedDocflowService.getSignatures(contentRef);
		ArrayOfbase64Binary ourSignatures = new ArrayOfbase64Binary();
		for (Signature signature : signatures) {
			if (signature.getOur()) {
				ourSignatures.getBase64Binaries().add(signature.getSignatureContent().getBytes());
			}
		}

		//получаем контент документа
		//получаем название документа
		//получаем ИД документа
		ContentReader contentReader = contentService.getReader(contentRef, ContentModel.PROP_CONTENT);
		DocumentToSend doc = new DocumentToSend();
		doc.setContent(Utils.contentToByteArray(contentReader));
		doc.setDocflowId(docflowId);
		doc.setDocumentType(EDocumentType.NON_FORMALIZED);
		doc.setFileName((String)nodeService.getProperty(contentRef, ContentModel.PROP_NAME));
		doc.setId(contentRef.getId());
		doc.setReceiver(companyInfo);
		doc.setSignatures(ourSignatures);
		doc.setTransactionType(EDocflowTransactionType.WAITING_FOR_RECIPIENT_SIGNATURE); //двухнаправленный ЭДО

		addAuthHeaders(authHeaders);
		Holder<GateResponse> gateResponse = new Holder<GateResponse>();
		Holder<String> documentId = new Holder<String>();
		try {
			gateWcfService.sendDocument(doc, authHeaders.getOperator(), null, gateResponse, documentId);
		} catch(Exception ex) {
			gateResponse.value = new GateResponse();
			gateResponse.value.setMessage(ex.getMessage());
			gateResponse.value.setOperatorMessage(null);
			gateResponse.value.setResponseType(EResponseType.INTERNAL_ERROR);
			gateResponse.value.setStackTrace(ExceptionUtils.getStackTrace(ex));
		} finally {
			removeAuthHeaders();
		}

		if (gateResponse.value != null) {
			sendDocumentData = new SendDocumentData(gateResponse.value);
			if (EResponseType.OK == gateResponse.value.getResponseType()) {
				sendDocumentData.setDocumentId(documentId.value);
				sendDocumentData.setDocflowId(docflowId);
			} else {
				Utils.logGateResponse(gateResponse, logger);
			}
		} else {
			String msg = "Error invoking unicloud gate. GateResponse can't be null!";
			logger.error(msg);
			throw new IllegalStateException(msg);
		}

		return sendDocumentData;
	}

	private void markDocflowsAsRead(final AuthHeaders authHeaders, final ArrayOfguid readDocflows) {
		//добавляем аутентификационный заголовок
		if (!readDocflows.getGuids().isEmpty()) {
			addAuthHeaders(authHeaders);
			try {
				GateResponse gateResponse = gateWcfService.markDocflowsAsRead(readDocflows);
				if (EResponseType.OK == gateResponse.getResponseType()) {
					logger.debug("Docflows were successfully marked as read.");
				} else {
					Utils.logGateResponse(gateResponse, logger);
				}
			} catch(Exception ex) {
				logger.error("Error marking docflows as read.", ex);
			} finally {
				removeAuthHeaders();
			}
		}
	}

	private List<DocflowInfoBase> getContractorDocflows(final AuthHeaders authHeaders, final NodeRef contentRef, final Holder<GateResponse> gateResponse) {
		String docflowId = (String)nodeService.getProperty(contentRef, SignedDocflowModel.PROP_DOCFLOW_ID);

		WorkspaceFilter filter = new WorkspaceFilter();
		filter.setUnreadOnly(true);
		Holder<ArrayOfDocflowInfoBase> docflows = new Holder<ArrayOfDocflowInfoBase>();
		//добавляем аутентификационный заголовок
		addAuthHeaders(authHeaders);
		try {
			gateWcfService.getDocflowList(filter, gateResponse, docflows);
		} catch(Exception ex) {
			logger.error("getDocflowList internal error", ex);
		} finally {
			removeAuthHeaders();
		}
		List<DocflowInfoBase> contractorDocflows;
		if (gateResponse.value != null) {
			contractorDocflows = new ArrayList<DocflowInfoBase>();
			if (EResponseType.OK == gateResponse.value.getResponseType()) {
				List<DocflowInfoBase> docflowInfoBases = docflows.value.getDocflowInfoBases();
				for (DocflowInfoBase docflowInfoBase : docflowInfoBases) {
					String contractorDocflowId = docflowInfoBase.getDocflowId();

					if (docflowId.equals(contractorDocflowId)) {
						contractorDocflows.add(docflowInfoBase);
					}
				}
			} else {
				Utils.logGateResponse(gateResponse, logger);
			}
		} else {
			String msg = "Error invoking unicloud gate. GateResponse can't be null!";
			logger.error(msg);
			throw new IllegalStateException(msg);
		}
		return contractorDocflows;
	}

	private List<DocumentInfo> getContractorDocumentInfos(final AuthHeaders authHeaders, final String docflowId, final Holder<GateResponse> gateResponse) {

		Holder<ArrayOfDocumentInfo> documentInfos = new Holder<ArrayOfDocumentInfo>();
		//добавляем аутентификационный заголовок
		addAuthHeaders(authHeaders);
		try {
			gateWcfService.getDocumentList(docflowId, gateResponse, documentInfos);
		} catch(Exception ex) {
			logger.error("getContractorDocumentInfos internal error", ex);
		} finally {
			removeAuthHeaders();
		}
		List<DocumentInfo> documentInfoList;
		if (gateResponse.value != null) {
			if (EResponseType.OK == gateResponse.value.getResponseType()) {
				documentInfoList = documentInfos.value.getDocumentInfos();
			} else {
				documentInfoList = new ArrayList<DocumentInfo>();
				Utils.logGateResponse(gateResponse, logger);
			}
		} else {
			String msg = "Error invoking unicloud gate. GateResponse can't be null!";
			logger.error(msg);
			throw new IllegalStateException(msg);
		}
		return documentInfoList;
	}

	private DocumentContent getContractorDocument(final AuthHeaders authHeaders, final String documentId, final Holder<GateResponse> gateResponse) {
		Holder<DocumentContent> documentContent = new Holder<DocumentContent>();
		//добавляем аутентификационный заголовок
		addAuthHeaders(authHeaders);
		try {
			gateWcfService.getDocumentContent(documentId, true, gateResponse, documentContent);
		} catch(Exception ex) {
			logger.error("getContractorDocument internal error", ex);
		} finally {
			removeAuthHeaders();
		}
		DocumentContent document;
		if (gateResponse.value != null) {
			if (EResponseType.OK == gateResponse.value.getResponseType()) {
				document = documentContent.value;
			} else {
				document = null;
				Utils.logGateResponse(gateResponse, logger);
			}
		} else {
			String msg = "Error invoking unicloud gate. GateResponse can't be null!";
			logger.error(msg);
			throw new IllegalStateException(msg);
		}
		return document;
	}

	public ReceiveDocumentData receiveDocuments(final NodeRef contentRef/*, final NodeRef contractorRef*/) {
		NodeRef employeeRef = orgstructureService.getCurrentEmployee();
		AuthHeaders authHeaders = new AuthHeaders();
		authHeaders.setPartner((String)nodeService.getProperty(employeeRef, SignedDocflowModel.PROP_PARTNER_KEY));
		authHeaders.setUser(employeeRef.getId());
		authHeaders.setOrganization((String)nodeService.getProperty(employeeRef, SignedDocflowModel.PROP_ORGANIZATION_ID));
		authHeaders.setOperator((String)nodeService.getProperty(employeeRef, SignedDocflowModel.PROP_OPERATOR_CODE));
		authHeaders.setToken((String)nodeService.getProperty(employeeRef, SignedDocflowModel.PROP_AUTH_TOKEN));

		//проверка аутентификационных параметров
		//убеждаемся в том, у нас есть organizationId и token
		//в противном случае возвращаем ошибку аутентификации
		ReceiveDocumentData receiveDocumentData = checkAuthenticationData(authHeaders, ReceiveDocumentData.class);
		if (receiveDocumentData != null) {
			return receiveDocumentData;
		} else {
			receiveDocumentData = new ReceiveDocumentData();
		}

		Holder<GateResponse> gateResponse = new Holder<GateResponse>();
		List<DocflowInfoBase> docflows = getContractorDocflows(authHeaders, contentRef, gateResponse);
		//бежим по свежепрочитанным docflow и достаем их них DocumentInfo
		//а также строим список docflows которые будут отмечены как прочитанные
		ArrayOfguid readDocflows = new ArrayOfguid();
		List<DocumentInfo> receiptNotifications = new ArrayList<DocumentInfo>();
		List<DocumentInfo> recipientSignatures = new ArrayList<DocumentInfo>();
		List<DocumentInfo> rejectedSignatures = new ArrayList<DocumentInfo>();
		for(DocflowInfoBase docflow : docflows) {
			String docflowId = docflow.getDocflowId();
			readDocflows.getGuids().add(docflowId);
			List<DocumentInfo> documentInfos = getContractorDocumentInfos(authHeaders, docflowId, gateResponse);
			logger.debug("docflow type = {}", docflow.getType());
			for(DocumentInfo documentInfo : documentInfos) {
				EDocumentType documentType = documentInfo.getDocumentType();
				EDocflowTransactionType docflowTransactionType = documentInfo.getTransactionType();
				//получили уведомление о прочтении
				if (EDocumentType.RECEIPT_NOTIFICATION == documentType && EDocflowTransactionType.RECIPIENT_RECEIVE_NOTIFICATION == docflowTransactionType) {
					receiptNotifications.add(documentInfo);
				}
				//партнер подписал документ и прислал подписи
				if (EDocumentType.NON_FORMALIZED == documentType && EDocflowTransactionType.WITH_RECIPIENT_SIGNATURE == docflowTransactionType) {
					recipientSignatures.add(documentInfo);
				}

				if (EDocumentType.NON_FORMALIZED == documentType && EDocflowTransactionType.SIGNATURE_REQUEST_REJECTED == docflowTransactionType) {
					rejectedSignatures.add(documentInfo);
				}
			}
		}

		//получаем непосредственно подписи по документам
		List<DocumentContent> documents = new ArrayList<DocumentContent>();
		for (DocumentInfo documentInfo : recipientSignatures) {
			DocumentContent document = getContractorDocument(authHeaders, documentInfo.getDocumentId(), gateResponse);
			documents.add(document);
		}

		//отмечаем как прочитанные
		markDocflowsAsRead(authHeaders, readDocflows);

		//вычленяем из списка документов подписи
		receiveDocumentData.setIsRead(!receiptNotifications.isEmpty());
		receiveDocumentData.setSignatures(new ArrayList<String>());
		for(DocumentContent document : documents) {
			List<byte[]> signatures = document.getSignatures().getBase64Binaries();
			for(int i = 0; i < signatures.size(); ++i) {
				byte[] sign = signatures.get(i);
				if (Base64.isArrayByteBase64(sign)) {
					receiveDocumentData.getSignatures().add(new String(sign));
				} else {
					receiveDocumentData.getSignatures().add(Base64.encodeBase64String(sign));
				}
			}
		}
		receiveDocumentData.setGateResponse(gateResponse.value);
		return receiveDocumentData;
	}
}
