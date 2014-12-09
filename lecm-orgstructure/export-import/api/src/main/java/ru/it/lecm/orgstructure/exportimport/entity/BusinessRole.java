package ru.it.lecm.orgstructure.exportimport.entity;

import java.util.ArrayList;
import java.util.List;
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
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="is-dynamic" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="employees">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="departments">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="staffs">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
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
@XmlType(name = "", propOrder = {"id", "name", "description", "dynamic", "employees", "departments", "staffs"})
public class BusinessRole {

	@XmlElement(required = true, namespace = "http://www.it.ru/logicECM/orgstructure/export-import/business-role/1.0")
	protected String id;
	@XmlElement(required = true, namespace = "http://www.it.ru/logicECM/orgstructure/export-import/business-role/1.0")
	protected String name;
	@XmlElement(required = true, namespace = "http://www.it.ru/logicECM/orgstructure/export-import/business-role/1.0")
	protected String description;
	@XmlElement(name = "is-dynamic", namespace = "http://www.it.ru/logicECM/orgstructure/export-import/business-role/1.0")
	protected boolean dynamic;
	@XmlElement(required = true, namespace = "http://www.it.ru/logicECM/orgstructure/export-import/business-role/1.0")
	protected BusinessRole.Employees employees;
	@XmlElement(required = true, namespace = "http://www.it.ru/logicECM/orgstructure/export-import/business-role/1.0")
	protected BusinessRole.Departments departments;
	@XmlElement(required = true, namespace = "http://www.it.ru/logicECM/orgstructure/export-import/business-role/1.0")
	protected BusinessRole.Staffs staffs;

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
	 * Gets the value of the dynamic property.
	 *
	 */
	public boolean isDynamic() {
		return dynamic;
	}

	/**
	 * Sets the value of the dynamic property.
	 *
	 */
	public void setDynamic(boolean value) {
		this.dynamic = value;
	}

	/**
	 * Gets the value of the employees property.
	 *
	 * @return
	 * possible object is
	 * {@link BusinessRoles.BusinessRole.Employees }
	 *
	 */
	public BusinessRole.Employees getEmployees() {
		if (employees == null) {
			employees = new Employees();
		}
		return employees;
	}

	/**
	 * Sets the value of the employees property.
	 *
	 * @param value
	 * allowed object is
	 * {@link BusinessRoles.BusinessRole.Employees }
	 *
	 */
	public void setEmployees(BusinessRole.Employees value) {
		this.employees = value;
	}

	/**
	 * Gets the value of the departments property.
	 *
	 * @return
	 * possible object is
	 * {@link BusinessRoles.BusinessRole.Departments }
	 *
	 */
	public BusinessRole.Departments getDepartments() {
		if (departments == null) {
			departments = new Departments();
		}
		return departments;
	}

	/**
	 * Sets the value of the departments property.
	 *
	 * @param value
	 * allowed object is
	 * {@link BusinessRoles.BusinessRole.Departments }
	 *
	 */
	public void setDepartments(BusinessRole.Departments value) {
		this.departments = value;
	}

	/**
	 * Gets the value of the staffs property.
	 *
	 * @return
	 * possible object is
	 * {@link BusinessRoles.BusinessRole.Staffs }
	 *
	 */
	public BusinessRole.Staffs getStaffs() {
		if (staffs == null) {
			staffs = new Staffs();
		}
		return staffs;
	}

	/**
	 * Sets the value of the staffs property.
	 *
	 * @param value
	 * allowed object is
	 * {@link BusinessRoles.BusinessRole.Staffs }
	 *
	 */
	public void setStaffs(BusinessRole.Staffs value) {
		this.staffs = value;
	}

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
	 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
	 *       &lt;/sequence>
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 *
	 *
	 */
	@XmlAccessorType(value = XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = {"id"})
	public static class Departments {

		@XmlElement(required = true, namespace = "http://www.it.ru/logicECM/orgstructure/export-import/business-role/1.0")
		protected List<String> id;

		/**
		 * Gets the value of the id property.
		 *
		 * <p>
		 * This accessor method returns a reference to the live list,
		 * not a snapshot. Therefore any modification you make to the
		 * returned list will be present inside the JAXB object.
		 * This is why there is not a <CODE>set</CODE> method for the id property.
		 *
		 * <p>
		 * For example, to add a new item, do as follows:
		 * <pre>
		 *    getId().add(newItem);
		 * </pre>
		 *
		 *
		 * <p>
		 * Objects of the following type(s) are allowed in the list
		 * {@link String }
		 *
		 *
		 */
		public List<String> getId() {
			if (id == null) {
				id = new ArrayList<>();
			}
			return this.id;
		}
	}

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
	 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
	 *       &lt;/sequence>
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 *
	 *
	 */
	@XmlAccessorType(value = XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = {"id"})
	public static class Employees {

		@XmlElement(required = true, namespace = "http://www.it.ru/logicECM/orgstructure/export-import/business-role/1.0")
		protected List<String> id;

		/**
		 * Gets the value of the id property.
		 *
		 * <p>
		 * This accessor method returns a reference to the live list,
		 * not a snapshot. Therefore any modification you make to the
		 * returned list will be present inside the JAXB object.
		 * This is why there is not a <CODE>set</CODE> method for the id property.
		 *
		 * <p>
		 * For example, to add a new item, do as follows:
		 * <pre>
		 *    getId().add(newItem);
		 * </pre>
		 *
		 *
		 * <p>
		 * Objects of the following type(s) are allowed in the list
		 * {@link String }
		 *
		 *
		 */
		public List<String> getId() {
			if (id == null) {
				id = new ArrayList<>();
			}
			return this.id;
		}
	}

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
	 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
	 *       &lt;/sequence>
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 *
	 *
	 */
	@XmlAccessorType(value = XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = {"id"})
	public static class Staffs {

		@XmlElement(required = true, namespace = "http://www.it.ru/logicECM/orgstructure/export-import/business-role/1.0")
		protected List<String> id;

		/**
		 * Gets the value of the id property.
		 *
		 * <p>
		 * This accessor method returns a reference to the live list,
		 * not a snapshot. Therefore any modification you make to the
		 * returned list will be present inside the JAXB object.
		 * This is why there is not a <CODE>set</CODE> method for the id property.
		 *
		 * <p>
		 * For example, to add a new item, do as follows:
		 * <pre>
		 *    getId().add(newItem);
		 * </pre>
		 *
		 *
		 * <p>
		 * Objects of the following type(s) are allowed in the list
		 * {@link String }
		 *
		 *
		 */
		public List<String> getId() {
			if (id == null) {
				id = new ArrayList<>();
			}
			return this.id;
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("BusinessRole [");
		sb.append(" id: ").append(id).append(" name: ").append(name).append(" ]");

		return sb.toString();
	}

}
