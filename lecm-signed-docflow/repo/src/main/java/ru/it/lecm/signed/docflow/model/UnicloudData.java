package ru.it.lecm.signed.docflow.model;

import java.util.HashMap;
import java.util.Map;
import ucloud.gate.proxy.exceptions.EResponseType;
import ucloud.gate.proxy.exceptions.GateResponse;

/**
 *
 * @author VLadimir Malygin
 * @since 08.08.2013 15:22:59
 * @see <p>mailto: <a href="mailto:vmalygin@it.ru">vmalygin@it.ru</a></p>
 */
public abstract class UnicloudData {

	private final Map<String, Object> gateResponse;

	protected UnicloudData() {
		gateResponse = new HashMap<String, Object>();
	}

	protected UnicloudData(final GateResponse gateResponse) {
		this();
		setGateResponse(gateResponse);
	}

	public final void setGateResponse(GateResponse gateResponse) {
		this.gateResponse.put("message", gateResponse.getMessage());
		this.gateResponse.put("operatorMessage", gateResponse.getOperatorMessage());
		this.gateResponse.put("responseType", gateResponse.getResponseType());
		this.gateResponse.put("stackTrace", gateResponse.getStackTrace());
	}

	public String getMessage() {
		return (String)gateResponse.get("message");
	}

	public String getOperatorMessage() {
		return (String)gateResponse.get("operatorMessage");
	}

	public EResponseType getResponseType() {
		return (EResponseType)gateResponse.get("responseType");
	}

	public String getStackTrace() {
		return (String)gateResponse.get("stackTrace");
	}

	protected abstract Map<String, Object> putOwnProperties(Map<String, Object> properties);

	/**
	 * получение перечня всех имеющихся пропертей,
	 * для дальнейшего построения json-объекта
	 * @return
	 */
	public final Map<String, Object> getProperties() {
		Map<String, Object> properties = putOwnProperties(new HashMap<String, Object>());
		properties.put("gateResponse", gateResponse);
		return properties;
	}
}
