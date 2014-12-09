package ru.it.lecm.orgstructure.exportimport.entity;

import java.util.Comparator;
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
 *         &lt;element name="position-id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="employee-id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="department-id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="is-primary" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="is-leading" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"id", "positionId", "employeeId", "departmentId", "description", "primary", "leading"})
public class Staff {

	@XmlElement(required = true, namespace = "http://www.it.ru/logicECM/orgstructure/export-import/staff/1.0")
	protected String id;
	@XmlElement(name = "position-id", required = true, namespace = "http://www.it.ru/logicECM/orgstructure/export-import/staff/1.0")
	protected String positionId;
	@XmlElement(name = "employee-id", namespace = "http://www.it.ru/logicECM/orgstructure/export-import/staff/1.0")
	protected String employeeId;
	@XmlElement(name = "department-id", required = true, namespace = "http://www.it.ru/logicECM/orgstructure/export-import/staff/1.0")
	protected String departmentId;
	@XmlElement(name = "description", namespace = "http://www.it.ru/logicECM/orgstructure/export-import/staff/1.0")
	protected String description;
	@XmlElement(name = "is-primary", namespace = "http://www.it.ru/logicECM/orgstructure/export-import/staff/1.0")
	protected boolean primary;
	@XmlElement(name = "is-leading", namespace = "http://www.it.ru/logicECM/orgstructure/export-import/staff/1.0")
	protected boolean leading;

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
	 * Gets the value of the positionId property.
	 *
	 * @return
	 * possible object is
	 * {@link String }
	 *
	 */
	public String getPositionId() {
		return positionId;
	}

	/**
	 * Sets the value of the positionId property.
	 *
	 * @param value
	 * allowed object is
	 * {@link String }
	 *
	 */
	public void setPositionId(String value) {
		this.positionId = value;
	}

	/**
	 * Gets the value of the employeeId property.
	 *
	 * @return
	 * possible object is
	 * {@link String }
	 *
	 */
	public String getEmployeeId() {
		return employeeId;
	}

	/**
	 * Sets the value of the employeeId property.
	 *
	 * @param value
	 * allowed object is
	 * {@link String }
	 *
	 */
	public void setEmployeeId(String value) {
		this.employeeId = value;
	}

	/**
	 * Gets the value of the departmentId property.
	 *
	 * @return
	 * possible object is
	 * {@link String }
	 *
	 */
	public String getDepartmentId() {
		return departmentId;
	}

	/**
	 * Sets the value of the departmentId property.
	 *
	 * @param value
	 * allowed object is
	 * {@link String }
	 *
	 */
	public void setDepartmentId(String value) {
		this.departmentId = value;
	}

	/**
	 * Gets the value of the description property.
	 *
	 * @return
	 * possible object is
	 * {@link String }
	 *
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the value of the description property.
	 *
	 * @param value
	 * allowed object is
	 * {@link String }
	 *
	 */
	public void setDescription(String value) {
		this.description = value;
	}

	/**
	 * Gets the value of the isPrimary property.
	 *
	 */
	public boolean isPrimary() {
		return primary;
	}

	/**
	 * Sets the value of the isPrimary property.
	 *
	 */
	public void setPrimary(boolean value) {
		this.primary = value;
	}

	/**
	 * Gets the value of the isLeading property.
	 *
	 */
	public boolean isLeading() {
		return leading;
	}

	/**
	 * Sets the value of the isLeading property.
	 *
	 */
	public void setLeading(boolean value) {
		this.leading = value;
	}

	public static class LeadershipPositionComparator implements Comparator<Staff> {

		@Override
		public int compare(Staff first, Staff second) {
			return -1 * Boolean.compare(first.isLeading(), second.isLeading());
		}

	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Staff [");
		sb.append(" id: ").append(id).append(" ]");

		return sb.toString();
	}
}
