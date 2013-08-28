package ru.it.lecm.signed.docflow.model;

import java.util.Map;
import ucloud.gate.proxy.exceptions.GateResponse;

/**
 *
 * @author VLadimir Malygin
 * @since 08.08.2013 15:23:20
 * @see <p>mailto: <a href="mailto:vmalygin@it.ru">vmalygin@it.ru</a></p>
 */
public final class AuthenticationData extends UnicloudData {

	private String organizationId;
	private String organizationEdoId;
	private String token;

	public AuthenticationData() {
		super();
	}

	public AuthenticationData(final GateResponse gateResponse) {
		super(gateResponse);
	}

	@Override
	protected Map<String, Object> putOwnProperties(Map<String, Object> properties) {
		properties.put("organizationId", organizationId);
		properties.put("organizationEdoId", organizationEdoId);
		properties.put("token", token);
		return properties;
	}

	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	public String getOrganizationEdoId() {
		return organizationEdoId;
	}

	public void setOrganizationEdoId(String organizationEdoId) {
		this.organizationEdoId = organizationEdoId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
