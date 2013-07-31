package ru.unicloud.gate;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.ws.Holder;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.headers.Header;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Базовый класс для тестовов различных сценариев работы с сервисом Unicloud Gate
 * @author VLadimir Malygin
 * @since 06.06.2013 10:02:21
 * @see <p>mailto: <a href="mailto:vmalygin@it.ru">vmalygin@it.ru</a></p>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:gate-wcf-service-context.xml", "classpath:META-INF/cxf/cxf.xml"})
public abstract class GateWcfServiceTest {

	private final static Logger logger = LoggerFactory.getLogger(GateWcfServiceTest.class);

	protected final static String PARTNER_KEY = "C570E2AB-C55F-49BE-B56A-233046F829A8";
	protected final static String OPERATOR_CODE = "2AE";

	/**
	 * Андреев Андрей Андреевич
	 */
	protected final static String INN_700000016017 = "2B8AF239-2F74-4D22-87B7-D52B7945F10F";

	/**
	 * Васильев Василиий Васильевич
	 */
	protected final static String INN_700000004075 = "08D8D67D-5E2D-4B44-AF34-46F82CE79331";

	/**
	 * Маратов Марат Маратович
	 */
	protected final static String INN_700000008094 = "652DFF41-4309-49F4-8604-946056F808C3";

	@Autowired
	@Qualifier("gateWcfService.https")
	protected IGateWcfService service;

	/**
	 *
	 * @param partner (обязательный заголовок) - идентификатор партнера. Выдается компанией Unicloud после заключения договора.
	 * @param user идентификатор пользователя в партнерской системе.
	 * @param organization идентификатор организации в сервисе Unicloud Gate.
	 * @param operator код спецоператора ЭДО
	 * @param token токен авторизации
	 */
	protected void setAuthHeaders(String partner, String user, String organization, String operator, String token) {
		try {
			Client proxy = ClientProxy.getClient(service);
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

	protected void logGateResponce(final Logger logger, final Holder<GateResponse> gateResponse) {
		logger.error("message = {}", gateResponse.value.getMessage());
		logger.error("operatorMessage = {}", gateResponse.value.getOperatorMessage());
		logger.error("responseType = {}", gateResponse.value.getResponseType());
		logger.error("stackTrace = {}", gateResponse.value.getStackTrace());
	}
}
