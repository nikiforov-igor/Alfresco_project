package ru.it.lecm.signed.docflow.model;

/**
 * Данные, необходимые для заполнения аутентификационного заголовка
 * @author VLadimir Malygin
 * @since 28.08.2013 11:44:18
 * @see <p>mailto: <a href="mailto:vmalygin@it.ru">vmalygin@it.ru</a></p>
 */
public final class AuthHeaders {

	private String partner;
	private String user;
	private String organization;
	private String operator;
	private String token;

	public AuthHeaders() {
	}

	public AuthHeaders(final String partner, final String user, final String organization, final String operator, final String token) {
		this.partner = partner;
		this.user = user;
		this.organization = organization;
		this.operator = operator;
		this.token = token;
	}

	public String getPartner() {
		return partner;
	}

	public void setPartner(String partner) {
		this.partner = partner;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
