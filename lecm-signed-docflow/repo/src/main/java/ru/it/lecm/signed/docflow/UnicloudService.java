package ru.it.lecm.signed.docflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.headers.Header;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tempuri.IGateWcfService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.signed.docflow.api.SignedDocflow;
import ucloud.gate.proxy.ArrayOfOperatorInfo;
import ucloud.gate.proxy.OperatorInfo;
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

	public void init() {
		if (logger.isDebugEnabled()) {
			try {
				Holder<GateResponse> gateResponse = new Holder<GateResponse>();
				Holder<String> version = new Holder<String>();
				gateWcfService.getServiceVersion(gateResponse, version);
				if (version.value != null) {
					logger.debug(version.value);
				} else {
					logGateResponse(gateResponse);
				}

				gateResponse = new Holder<GateResponse>();
				Holder<ArrayOfOperatorInfo> operatorsHolder = new Holder<ArrayOfOperatorInfo>();
				gateWcfService.getOperators(gateResponse, operatorsHolder);
				if (operatorsHolder.value != null) {
				List<OperatorInfo> operators = operatorsHolder.value.getOperatorInfos();
					for (OperatorInfo operator : operators) {
						logger.debug("AuthenticationType = {}", operator.getAuthenticationType());
						logger.debug("CertificateIssuerName = {}", operator.getCertificateIssuerName());
						logger.debug("Code = {}", operator.getCode());
						logger.debug("Extension = {}", operator.getExtension());
						logger.debug("Inn = {}", operator.getInn());
						logger.debug("IsRemoteSignEnabled = {}", operator.isIsRemoteSignEnabled());
						logger.debug("Name = {}", operator.getName());
					}
				} else {
					logGateResponse(gateResponse);
				}
			} catch (Exception ex) {
				logger.error(ex.getMessage());
			}
		}
	}

	/**
	 *
	 * @param partner (обязательный заголовок) - идентификатор партнера. Выдается компанией Unicloud после заключения договора.
	 * @param user идентификатор пользователя в партнерской системе.
	 * @param organization идентификатор организации в сервисе Unicloud Gate.
	 * @param operator код спецоператора ЭДО
	 * @param token токен авторизации
	 */
	private void setAuthHeaders(String partner, String user, String organization, String operator, String token) {
		try {
			Client proxy = ClientProxy.getClient(gateWcfService);
			List<Header> headersList = new ArrayList<Header>();

			headersList.add(new Header(new QName("uauth", "partner"), partner, new JAXBDataBinding(String.class)));
			headersList.add(new Header(new QName("uauth", "user"), user, new JAXBDataBinding(String.class)));
			headersList.add(new Header(new QName("uauth", "organization"), organization, new JAXBDataBinding(String.class)));
			headersList.add(new Header(new QName("uauth", "token_" + operator), token, new JAXBDataBinding(String.class)));

			proxy.getRequestContext().put(Header.HEADER_LIST, headersList);
		} catch(JAXBException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

	private void logGateResponse(final Holder<GateResponse> gateResponse) {
		if (gateResponse.value != null) {
			logger.error("message = {}", gateResponse.value.getMessage());
			logger.error("operatorMessage = {}", gateResponse.value.getOperatorMessage());
			logger.error("responseType = {}", gateResponse.value.getResponseType());
			logger.error("stackTrace = {}", gateResponse.value.getStackTrace());
		}
	}

	private Map<String, Object> gateResponseToMap(GateResponse gateResponse) {
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("message", gateResponse.getMessage());
		properties.put("operatorMessage", gateResponse.getOperatorMessage());
		properties.put("responseType", gateResponse.getResponseType());
		properties.put("stackTrace", gateResponse.getStackTrace());
		return properties;
	}

	public Map<String, Object> authenticateByCertificate(final String guidSignBase64, final String timestamp, final String timestampSignBase64) {

		byte[] guidSign = Base64.decodeBase64(guidSignBase64);
		byte[] timestampSign = Base64.decodeBase64(timestampSignBase64);


		NodeRef organizationRef = orgstructureService.getOrganization();
		String operatorCode = (String)nodeService.getProperty(organizationRef, SignedDocflow.PROP_OPERATOR_CODE);
		String partnerKey = (String)nodeService.getProperty(organizationRef, SignedDocflow.PROP_PARTNER_KEY);
		String inn = (String)nodeService.getProperty(organizationRef, OrgstructureBean.PROP_ORG_TIN);
		String kpp = null;
		String employeeId = orgstructureService.getCurrentEmployee().getId();

        Holder<GateResponse> gateResponse = new Holder<GateResponse>();
        Holder<String> organizationId = new Holder<String>();
		Holder<String> organizationEdoId = new Holder<String>();
		gateWcfService.registerUserByCertificate(operatorCode, partnerKey, inn, kpp, guidSign, employeeId, gateResponse, organizationId, organizationEdoId);

		Map<String, Object> result  = new HashMap<String, Object>();
		if (gateResponse.value != null) {
			String responseType = gateResponse.value.getResponseType().toString();
			if ("OK".equals(responseType)) {
				result.put("organizationId", organizationId.value);
				result.put("organizationEdoId", organizationEdoId.value);
				gateResponse = new Holder<GateResponse>();
				Holder<String> token = new Holder<String>();
				gateWcfService.authenticateByCertificate(operatorCode, timestampSign, timestamp, gateResponse, token);
				if (gateResponse.value != null) {
					responseType = gateResponse.value.getResponseType().toString();
					if ("OK".equals(responseType)) {
						result.put("token", token.value);
					} else {
						result = gateResponseToMap(gateResponse.value);
					}
				} else {
					String msg = "Error invoking unicloud gate. GateResponse can't be null!";
					logger.error(msg);
					throw new IllegalStateException(msg);
				}
			} else {
				result = gateResponseToMap(gateResponse.value);
			}
		} else {
			String msg = "Error invoking unicloud gate. GateResponse can't be null!";
			logger.error(msg);
			throw new IllegalStateException(msg);
		}

		return result;
	}

	public Map<String, Object> verifySignature(String contentRef, String signature) {
		NodeRef organizationRef = orgstructureService.getOrganization();
		String operatorCode = (String)nodeService.getProperty(organizationRef, SignedDocflow.PROP_OPERATOR_CODE);

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

		Map<String, Object> result  = new HashMap<String, Object>();

        Holder<GateResponse> gateResponse = new Holder<GateResponse>();
		Holder<String> signerInfo = new Holder<String>();
		Holder<Boolean> isSignatureValid = new Holder<Boolean>();
		gateWcfService.verifySignature(content, sign, operatorCode, gateResponse, signerInfo, isSignatureValid);
		if (gateResponse.value != null) {
			String responseType = gateResponse.value.getResponseType().toString();
			if ("OK".equals(responseType)) {
				result.put("signerInfo", signerInfo.value);
				result.put("isSignatureValid", isSignatureValid.value);
			} else {
				result = gateResponseToMap(gateResponse.value);
			}
		} else {
			String msg = "Error invoking unicloud gate. GateResponse can't be null!";
			logger.error(msg);
			throw new IllegalStateException(msg);
		}

		return result;
	}
}
