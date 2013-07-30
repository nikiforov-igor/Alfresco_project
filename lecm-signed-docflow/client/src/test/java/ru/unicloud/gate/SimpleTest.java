package ru.unicloud.gate;

import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.ws.Holder;
import org.apache.cxf.databinding.source.SourceDataBinding;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.headers.Header;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.xmlbeans.XmlBeansDataBinding;
import org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfOperatorInfo;
import org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo;
import org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Тест для простых методов сервиса Unicloud Gate
 * чтобы просто проверить его работоспособность
 * @author VLadimir Malygin
 * @since 18.06.2013 10:03:11
 * @see <p>mailto: <a href="mailto:vmalygin@it.ru">vmalygin@it.ru</a></p>
 */
public class SimpleTest extends GateWcfServiceTest {

	private final static Logger logger = LoggerFactory.getLogger(SimpleTest.class);

	@Test
	public void getServiceVersion() throws Exception {

		Client proxy = ClientProxy.getClient(service);
		List<Header> headersList = new ArrayList<Header>();

		Header testSoapHeader1 = new Header(new QName("uri:singz.ws.sample", "soapheader1"), "SOAP Header Message 1", new JAXBDataBinding(String.class));
		Header testSoapHeader2 = new Header(new QName("uri:singz.ws.sample", "soapheader2"), "SOAP Header Message 2", new JAXBDataBinding(String.class));

		headersList.add(testSoapHeader1);
		headersList.add(testSoapHeader2);

		proxy.getRequestContext().put(Header.HEADER_LIST, headersList);

		Holder<GateResponse> gateResponce = new Holder<GateResponse>();
		Holder<String> version = new Holder<String>();
		service.getServiceVersion(gateResponce, version);
		logger.info(version.value);
	}

	@Test
	public void getOperators() throws Exception {

		Client proxy = ClientProxy.getClient(service);
		List<Header> headersList = new ArrayList<Header>();

		Header testSoapHeader1 = new Header(new QName("uri:singz.ws.sample", "soapheader1"), "SOAP Header Message 3", new JAXBDataBinding(String.class));
		Header testSoapHeader2 = new Header(new QName("uri:singz.ws.sample", "soapheader2"), "SOAP Header Message 4", new JAXBDataBinding(String.class));

		headersList.add(testSoapHeader1);
		headersList.add(testSoapHeader2);

		proxy.getRequestContext().put(Header.HEADER_LIST, headersList);

		Holder<GateResponse> gateResponce = new Holder<GateResponse>();
		Holder<ArrayOfOperatorInfo> operatorsHolder = new Holder<ArrayOfOperatorInfo>();
		service.getOperators(gateResponce, operatorsHolder);
		List<OperatorInfo> operators = operatorsHolder.value.getOperatorInfoList();
		for (OperatorInfo operator : operators) {
			logger.info("AuthenticationType = {}", operator.getAuthenticationType());
			logger.info("CertificateIssuerName = {}", operator.getCertificateIssuerName());
			logger.info("Code = {}", operator.getCode());
			logger.info("Extension = {}", operator.getExtension());
			logger.info("Inn = {}", operator.getInn());
			logger.info("IsRemoteSignEnabled = {}", operator.getIsRemoteSignEnabled());
			logger.info("Name = {}", operator.getName());
		}
	}
}
