package ru.unicloud.gate;

import javax.xml.ws.Holder;
import org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse;
import org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.ArrayOfMember;
import org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert;
import org.junit.Test;

/**
 * Регистрация у спецоператора ЭДО через сервис Unicloud Gate
 * @author VLadimir Malygin
 * @since 18.06.2013 14:42:10
 * @see <p>mailto: <a href="mailto:vmalygin@it.ru">vmalygin@it.ru</a></p>
 */
public class RegisterSpecOperatorTest extends GateWcfServiceTest {

	private byte[] generateRegisterRequest() {
        Holder<GateResponse> gateResponse = new Holder<GateResponse>();
		Holder<byte[]> content = new Holder<byte[]>();

		ArrayOfMember members = ArrayOfMember.Factory.newInstance();

		RegisterRequestForeignCert registerRequest = RegisterRequestForeignCert.Factory.newInstance();
		registerRequest.setInn("700000004075");
		registerRequest.setMembers(members);
		service.generateRegisterRequest(null, OPERATOR_CODE, gateResponse, content);
		return content.value;
	}



	@Test
	public void registerSpecOperator() throws Exception {
	}
}
