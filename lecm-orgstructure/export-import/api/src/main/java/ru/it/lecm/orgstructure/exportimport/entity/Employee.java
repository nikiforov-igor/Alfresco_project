package ru.it.lecm.orgstructure.exportimport.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * Java class for anonymous complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="lastname" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="firstname" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="middlename" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="sex">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="MALE"/>
 *               &lt;enumeration value="FEMALE"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="phone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="email" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="number" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="name-genitive" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="name-dative" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="login" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"id", "lastname", "firstname", "middlename", "sex", "phone", "email", "number", "nameGenitive", "nameDative", "login"})
public class Employee {

	@XmlElement(required = true, namespace = "http://www.it.ru/logicECM/orgstructure/export-import/employees/1.0")
	protected String id;
	@XmlElement(required = true, namespace = "http://www.it.ru/logicECM/orgstructure/export-import/employees/1.0")
	protected String lastname;
	@XmlElement(required = true, namespace = "http://www.it.ru/logicECM/orgstructure/export-import/employees/1.0")
	protected String firstname;
	@XmlElement(required = true, namespace = "http://www.it.ru/logicECM/orgstructure/export-import/employees/1.0")
	protected String middlename;
	@XmlElement(required = true, namespace = "http://www.it.ru/logicECM/orgstructure/export-import/employees/1.0")
	protected String sex;
	@XmlElement(namespace = "http://www.it.ru/logicECM/orgstructure/export-import/employees/1.0")
	protected String phone;
	@XmlElement(required = true, namespace = "http://www.it.ru/logicECM/orgstructure/export-import/employees/1.0")
	protected String email;
	@XmlElement(namespace = "http://www.it.ru/logicECM/orgstructure/export-import/employees/1.0")
	protected String number;
	@XmlElement(name = "name-genitive", namespace = "http://www.it.ru/logicECM/orgstructure/export-import/employees/1.0")
	protected String nameGenitive;
	@XmlElement(name = "name-dative", namespace = "http://www.it.ru/logicECM/orgstructure/export-import/employees/1.0")
	protected String nameDative;
	@XmlElement(required = true, namespace = "http://www.it.ru/logicECM/orgstructure/export-import/employees/1.0")
	protected String login;

	/**
	 * Gets the value of the id property.
	 *
	 * @return
	 * possible object is
	 * {@link String }
	 *
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the value of the id property.
	 *
	 * @param value
	 * allowed object is
	 * {@link String }
	 *
	 */
	public void setId(String value) {
		this.id = value;
	}

	/**
	 * Gets the value of the lastname property.
	 *
	 * @return
	 * possible object is
	 * {@link String }
	 *
	 */
	public String getLastname() {
		return StringUtils.defaultString(lastname);
	}

	/**
	 * Sets the value of the lastname property.
	 *
	 * @param value
	 * allowed object is
	 * {@link String }
	 *
	 */
	public void setLastname(String value) {
		this.lastname = StringUtils.defaultString(value);
	}

	/**
	 * Gets the value of the firstname property.
	 *
	 * @return
	 * possible object is
	 * {@link String }
	 *
	 */
	public String getFirstname() {
		return StringUtils.defaultString(firstname);
	}

	/**
	 * Sets the value of the firstname property.
	 *
	 * @param value
	 * allowed object is
	 * {@link String }
	 *
	 */
	public void setFirstname(String value) {
		this.firstname = StringUtils.defaultString(value);
	}

	/**
	 * Gets the value of the middlename property.
	 *
	 * @return
	 * possible object is
	 * {@link String }
	 *
	 */
	public String getMiddlename() {
		return StringUtils.defaultString(middlename);
	}

	/**
	 * Sets the value of the middlename property.
	 *
	 * @param value
	 * allowed object is
	 * {@link String }
	 *
	 */
	public void setMiddlename(String value) {
		this.middlename = StringUtils.defaultString(value);
	}

	/**
	 * Gets the value of the sex property.
	 *
	 * @return
	 * possible object is
	 * {@link String }
	 *
	 */
	public String getSex() {
		return StringUtils.defaultString(sex);
	}

	/**
	 * Sets the value of the sex property.
	 *
	 * @param value
	 * allowed object is
	 * {@link String }
	 *
	 */
	public void setSex(String value) {
		this.sex = StringUtils.defaultString(value);
	}

	/**
	 * Gets the value of the phone property.
	 *
	 * @return
	 * possible object is
	 * {@link String }
	 *
	 */
	public String getPhone() {
		return StringUtils.defaultString(phone);
	}

	/**
	 * Sets the value of the phone property.
	 *
	 * @param value
	 * allowed object is
	 * {@link String }
	 *
	 */
	public void setPhone(String value) {
		this.phone = StringUtils.defaultString(value);
	}

	/**
	 * Gets the value of the email property.
	 *
	 * @return
	 * possible object is
	 * {@link String }
	 *
	 */
	public String getEmail() {
		return StringUtils.defaultString(email);
	}

	/**
	 * Sets the value of the email property.
	 *
	 * @param value
	 * allowed object is
	 * {@link String }
	 *
	 */
	public void setEmail(String value) {
		this.email = StringUtils.defaultString(value);
	}

	/**
	 * Gets the value of the number property.
	 *
	 * @return
	 * possible object is
	 * {@link String }
	 *
	 */
	public String getNumber() {
		return StringUtils.defaultString(number);
	}

	/**
	 * Sets the value of the number property.
	 *
	 * @param value
	 * allowed object is
	 * {@link String }
	 *
	 */
	public void setNumber(String value) {
		this.number = StringUtils.defaultString(value);
	}

	/**
	 * Gets the value of the nameGenitive property.
	 *
	 * @return
	 * possible object is
	 * {@link String }
	 *
	 */
	public String getNameGenitive() {
		return StringUtils.defaultString(nameGenitive);
	}

	/**
	 * Sets the value of the nameGenitive property.
	 *
	 * @param value
	 * allowed object is
	 * {@link String }
	 *
	 */
	public void setNameGenitive(String value) {
		this.nameGenitive = StringUtils.defaultString(value);
	}

	/**
	 * Gets the value of the nameDative property.
	 *
	 * @return
	 * possible object is
	 * {@link String }
	 *
	 */
	public String getNameDative() {
		return StringUtils.defaultString(nameDative);
	}

	/**
	 * Sets the value of the nameDative property.
	 *
	 * @param value
	 * allowed object is
	 * {@link String }
	 *
	 */
	public void setNameDative(String value) {
		this.nameDative = StringUtils.defaultString(value);
	}

	/**
	 * Gets the value of the login property.
	 *
	 * @return
	 * possible object is
	 * {@link String }
	 *
	 */
	public String getLogin() {
		return StringUtils.defaultString(login);
	}

	/**
	 * Sets the value of the login property.
	 *
	 * @param value
	 * allowed object is
	 * {@link String }
	 *
	 */
	public void setLogin(String value) {
		this.login = StringUtils.defaultString(value);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Employee [");
		sb.append(" id: ").append(id).append(" name: ").append(lastname).append(" ").
				append(firstname).append(" ").append(middlename).append(" ]");

		return sb.toString();
	}
}
