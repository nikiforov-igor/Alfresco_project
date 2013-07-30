package ru.unicloud.gate;

import javax.xml.ws.Holder;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * Регистрация в сервисе Unicloud Gate с локальным сертификатом
 * @author VLadimir Malygin
 * @since 07.06.2013 11:12:58
 * @see <p>mailto: <a href="mailto:vmalygin@it.ru">vmalygin@it.ru</a></p>
 */
public class RegistrationByCertificateTest extends GateWcfServiceTest {

	private final static Logger logger = LoggerFactory.getLogger(RegistrationByCertificateTest.class);

	private void registerUserByCertificate(String userId, String inn, String kpp) throws Exception {
		String path = String.format("/RegisterUserByCertificate/guid-%s.sign", inn);
		ClassPathResource resource = new ClassPathResource(path);
        String base64Sign = IOUtils.toString(resource.getInputStream());
		byte sign[] = Base64.decodeBase64(base64Sign);
        Holder<GateResponse> gateResponse = new Holder<GateResponse>();
        Holder<String> organizationId = new Holder<String>();
		Holder<String> organizationEdoId = new Holder<String>();
		service.registerUserByCertificate(OPERATOR_CODE, PARTNER_KEY, inn, kpp, sign, userId, gateResponse, organizationId, organizationEdoId);
		logger.info("organizationId = {}", organizationId.value);
		logger.info("organizationEdoId = {}", organizationEdoId.value);
		logGateResponce(logger, gateResponse);
	}

	/**
	 * Регистрация Андреев Андрей Андреевич
	 * @throws Exception
	 */
	@Test
	public void registerUser700000016017ByCertificate () throws Exception {
		logger.info("********************************************************************************");
		logger.info("registerUserByCertificate for Андреев Андрей Андреевич =========================");
		registerUserByCertificate(INN_700000016017, "700000016017", null);
		logger.info("================================================================================");
		logger.info("********************************************************************************");
	}

	/**
	 * Регистрация Васильев Василиий Васильевич
	 * @throws Exception
	 */
	@Test
	public void registerUser700000004075ByCertificate () throws Exception {
		logger.info("********************************************************************************");
		logger.info("registerUserByCertificate for Васильев Василиий Васильевич =====================");
		registerUserByCertificate(INN_700000004075, "700000004075", null);
		logger.info("================================================================================");
		logger.info("********************************************************************************");
	}

	/**
	 * Регистрация Маратов Марат Маратович
	 * @throws Exception
	 */
	@Test
	public void registerUser700000008094ByCertificate () throws Exception {
		logger.info("********************************************************************************");
		logger.info("registerUserByCertificate for Маратов Марат Маратович ==========================");
		registerUserByCertificate(INN_700000008094, "700000008094", null);
		logger.info("================================================================================");
		logger.info("********************************************************************************");
	}
}
