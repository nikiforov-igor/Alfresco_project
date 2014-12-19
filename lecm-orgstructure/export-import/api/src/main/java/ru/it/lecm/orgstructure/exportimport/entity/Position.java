package ru.it.lecm.orgstructure.exportimport.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

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
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="name-genitive" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="name-dative" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="code" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"id", "name", "nameGenitive", "nameDative", "code"})
public class Position {

	@XmlElement(required = true, namespace = "http://www.it.ru/logicECM/orgstructure/export-import/positions/1.0")
	protected String id;
	@XmlElement(required = true, namespace = "http://www.it.ru/logicECM/orgstructure/export-import/positions/1.0")
	protected String name;
	@XmlElement(name = "name-genitive", namespace = "http://www.it.ru/logicECM/orgstructure/export-import/positions/1.0")
	protected String nameGenitive;
	@XmlElement(name = "name-dative", namespace = "http://www.it.ru/logicECM/orgstructure/export-import/positions/1.0")
	protected String nameDative;
	@XmlElement(namespace = "http://www.it.ru/logicECM/orgstructure/export-import/positions/1.0")
	protected String code;

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
	 * Gets the value of the name property.
	 *
	 * @return
	 * possible object is
	 * {@link String }
	 *
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the value of the name property.
	 *
	 * @param value
	 * allowed object is
	 * {@link String }
	 *
	 */
	public void setName(String value) {
		this.name = value;
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
		return nameGenitive;
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
		this.nameGenitive = value;
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
		return nameDative;
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
		this.nameDative = value;
	}

	/**
	 * Gets the value of the code property.
	 *
	 * @return
	 * possible object is
	 * {@link String }
	 *
	 */
	public String getCode() {
		return code;
	}

	/**
	 * Sets the value of the code property.
	 *
	 * @param value
	 * allowed object is
	 * {@link String }
	 *
	 */
	public void setCode(String value) {
		this.code = value;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Position [");
		sb.append(" id: ").append(id).append(" name: ").append(name).append(" ]");

		return sb.toString();
	}

}
