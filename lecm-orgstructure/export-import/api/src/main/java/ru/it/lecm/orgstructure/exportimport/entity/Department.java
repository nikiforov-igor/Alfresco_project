package ru.it.lecm.orgstructure.exportimport.entity;

import java.util.Comparator;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
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
 *         &lt;element name="pid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="name-full" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="name-short" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="code" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="type">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="SEPARATED"/>
 *               &lt;enumeration value="SEGREGATED"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"id", "pid", "nameFull", "nameShort", "code", "type"})
public class Department {

	@XmlElement(required = true, namespace = "http://www.it.ru/logicECM/orgstructure/export-import/departments/1.0")
	protected String id;
	@XmlElement(required = true, namespace = "http://www.it.ru/logicECM/orgstructure/export-import/departments/1.0")
	protected String pid;
	@XmlElement(name = "name-full", required = true, namespace = "http://www.it.ru/logicECM/orgstructure/export-import/departments/1.0")
	protected String nameFull;
	@XmlElement(name = "name-short", required = true, namespace = "http://www.it.ru/logicECM/orgstructure/export-import/departments/1.0")
	protected String nameShort;
	@XmlElement(required = true, namespace = "http://www.it.ru/logicECM/orgstructure/export-import/departments/1.0")
	protected String code;
	@XmlElement(required = true, namespace = "http://www.it.ru/logicECM/orgstructure/export-import/departments/1.0")
	protected String type;
	@XmlTransient
	protected Integer sortWeigth = null;

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
	 * Gets the value of the pid property.
	 *
	 * @return
	 * possible object is
	 * {@link String }
	 *
	 */
	public String getPid() {
		return pid;
	}

	/**
	 * Sets the value of the pid property.
	 *
	 * @param value
	 * allowed object is
	 * {@link String }
	 *
	 */
	public void setPid(String value) {
		this.pid = value;
	}

	/**
	 * Gets the value of the nameFull property.
	 *
	 * @return
	 * possible object is
	 * {@link String }
	 *
	 */
	public String getNameFull() {
		return nameFull;
	}

	/**
	 * Sets the value of the nameFull property.
	 *
	 * @param value
	 * allowed object is
	 * {@link String }
	 *
	 */
	public void setNameFull(String value) {
		this.nameFull = value;
	}

	/**
	 * Gets the value of the nameShort property.
	 *
	 * @return
	 * possible object is
	 * {@link String }
	 *
	 */
	public String getNameShort() {
		return nameShort;
	}

	/**
	 * Sets the value of the nameShort property.
	 *
	 * @param value
	 * allowed object is
	 * {@link String }
	 *
	 */
	public void setNameShort(String value) {
		this.nameShort = value;
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

	/**
	 * Gets the value of the type property.
	 *
	 * @return
	 * possible object is
	 * {@link String }
	 *
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the value of the type property.
	 *
	 * @param value
	 * allowed object is
	 * {@link String }
	 *
	 */
	public void setType(String value) {
		this.type = value;
	}

	public Integer getSortWeigth() {
		return sortWeigth;
	}

	public void setSortWeigth(Integer sortWeigth) {
		this.sortWeigth = sortWeigth;
	}

	public static class SortWeigthComparator implements Comparator<Department> {

		@Override
		public int compare(Department dep1, Department dep2) {
			// сначала меньшие
			return dep1.getSortWeigth().compareTo(dep2.getSortWeigth());
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Department [");
		sb.append(" id: ").append(id).append(" name: ").append(nameShort).append(" ]");

		return sb.toString();
	}
}
