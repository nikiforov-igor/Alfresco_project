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
 * Аутентификация в сервисе Unicloud Gate с локальным сертификатом
 * @author VLadimir Malygin
 * @since 18.06.2013 12:14:25
 * @see <p>mailto: <a href="mailto:vmalygin@it.ru">vmalygin@it.ru</a></p>
 */
public class AuthenticateByCertificateTest extends GateWcfServiceTest {

	private final static Logger logger = LoggerFactory.getLogger(AuthenticateByCertificateTest.class);

	private void authenticateByCertificate(String inn) throws Exception {
		String path = String.format("/AuthenticateByCertificate/timestamp-%s.sign", inn);
		ClassPathResource resource = new ClassPathResource(path);
		String base64Sign = IOUtils.toString(resource.getInputStream());
		byte signature[] = Base64.decodeBase64(base64Sign);
		String signedData = IOUtils.toString(new ClassPathResource("/AuthenticateByCertificate/timestamp.txt").getInputStream());
        Holder<GateResponse> gateResponse = new Holder<GateResponse>();
        Holder<String> token = new Holder<String>();
		service.authenticateByCertificate(OPERATOR_CODE, signature, signedData, gateResponse, token);
		logger.info("token = {}", token.value);
		logGateResponce(logger, gateResponse);
	}


	/**
	 * Аутентификация Андреев Андрей Андреевич
	 * @throws Exception
	 */
	@Test
	public void authenticate700000016017ByCertificate () throws Exception {
		logger.info("********************************************************************************");
		logger.info("authenticateByCertificate for Андреев Андрей Андреевич =========================");
		authenticateByCertificate("700000016017");
		logger.info("================================================================================");
		logger.info("********************************************************************************");
	}

	/**
	 * Аутентификация Васильев Василиий Васильевич
	 * @throws Exception
	 */
	@Test
	public void authenticate700000004075ByCertificate () throws Exception {
		logger.info("********************************************************************************");
		logger.info("authenticateByCertificate for Васильев Василиий Васильевич =====================");
		authenticateByCertificate("700000004075");
		logger.info("================================================================================");
		logger.info("********************************************************************************");
	}

	/**
	 * Аутентификация Маратов Марат Маратович
	 * @throws Exception
	 */
	@Test
	public void authenticate700000008094ByCertificate () throws Exception {
		logger.info("********************************************************************************");
		logger.info("authenticateByCertificate for Маратов Марат Маратович ==========================");
		authenticateByCertificate("700000008094");
		logger.info("================================================================================");
		logger.info("********************************************************************************");
	}
}
