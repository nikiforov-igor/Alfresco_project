package ru.unicloud.gate;

import javax.xml.ws.Holder;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import ucloud.gate.proxy.exceptions.GateResponse;

/**
 * Подписание произвольного документа на стороне Unicloud Gate
 * @author VLadimir Malygin
 * @since 18.06.2013 15:17:28
 * @see <p>mailto: <a href="mailto:vmalygin@it.ru">vmalygin@it.ru</a></p>
 */
public class SignDocumentTest extends GateWcfServiceTest {

	private final static Logger logger = LoggerFactory.getLogger(SignDocumentTest.class);

	@Test
	public void signDocument() throws Exception {
		ClassPathResource resource = new ClassPathResource("/AuthenticateByCertificate/timestamp-700000008094.sign");
		String base64Sign = IOUtils.toString(resource.getInputStream());
		byte signature[] = Base64.decodeBase64(base64Sign);
		String signedData = IOUtils.toString(new ClassPathResource("/AuthenticateByCertificate/timestamp.txt").getInputStream());
        Holder<GateResponse> gateResponse = new Holder<GateResponse>();
        Holder<String> token = new Holder<String>();
		service.authenticateByCertificate(OPERATOR_CODE, signature, signedData, gateResponse, token);

		setAuthHeaders(PARTNER_KEY, INN_700000008094, "12a218ce-2245-4498-ae73-ad655ac07749", OPERATOR_CODE, token.value);

//		byte[] content = IOUtils.toByteArray(new ClassPathResource("/SignDocument/document.pdf").getInputStream());
		byte[] content = IOUtils.toByteArray(new ClassPathResource("/SignDocument/THIRDPARTYLICENSEREADME.txt").getInputStream());
		gateResponse = new Holder<GateResponse>();
		Holder<byte[]> docSignature = new Holder<byte[]>();
//		service.signDocument(content, OPERATOR_CODE, gateResponse, docSignature);
//		if (docSignature.value != null) {
//			logger.info(IOUtils.toString(docSignature.value, "UTF-8"));
//			logger.info(Base64.encodeBase64String(docSignature.value));
//		}
//		logGateResponce(logger, gateResponse);
	}

}
