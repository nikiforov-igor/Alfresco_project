package ru.it.lecm.signed.docflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.ws.Holder;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.transaction.TransactionService;
import org.apache.commons.codec.binary.Base64;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.headers.Header;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfOperatorInfo;
import org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo;
import org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.signed.docflow.api.SignedDocflow;
import ru.unicloud.gate.IGateWcfService;

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
	private SignedDocflow signedDocflowService;
	private TransactionService transactionService;

	public void setGateWcfService(IGateWcfService gateWcfService) {
		this.gateWcfService = gateWcfService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public void setSignedDocflowService(SignedDocflow signedDocflowService) {
		this.signedDocflowService = signedDocflowService;
	}

	public void setTransactionService(TransactionService transactionService) {
		this.transactionService = transactionService;
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
				List<OperatorInfo> operators = operatorsHolder.value.getOperatorInfoList();
					for (OperatorInfo operator : operators) {
						logger.debug("AuthenticationType = {}", operator.getAuthenticationType());
						logger.debug("CertificateIssuerName = {}", operator.getCertificateIssuerName());
						logger.debug("Code = {}", operator.getCode());
						logger.debug("Extension = {}", operator.getExtension());
						logger.debug("Inn = {}", operator.getInn());
						logger.debug("IsRemoteSignEnabled = {}", operator.getIsRemoteSignEnabled());
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

		Map<String, Object> properties  = new HashMap<String, Object>();
		if (gateResponse.value != null) {
			String responseType = gateResponse.value.getResponseType().toString();
			if ("OK".equals(responseType)) {
				properties.put("organizationId", organizationId.value);
				properties.put("organizationEdoId", organizationEdoId.value);
				gateResponse = new Holder<GateResponse>();
				Holder<String> token = new Holder<String>();
				gateWcfService.authenticateByCertificate(operatorCode, timestampSign, timestamp, gateResponse, token);
				if (gateResponse.value != null) {
					responseType = gateResponse.value.getResponseType().toString();
					if ("OK".equals(responseType)) {
						properties.put("token", token.value);
					} else {
						properties = gateResponseToMap(gateResponse.value);
					}
				} else {
					String msg = "Error invoking unicloud gate. GateResponse can't be null!";
					logger.error(msg);
					throw new IllegalStateException(msg);
				}
			} else {
				properties = gateResponseToMap(gateResponse.value);
			}
		} else {
			String msg = "Error invoking unicloud gate. GateResponse can't be null!";
			logger.error(msg);
			throw new IllegalStateException(msg);
		}

		return properties;
	}
}
