//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2014.11.27 at 10:26:30 AM YEKT
//
package ru.it.lecm.orgstructure.exportimport.entity;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
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
 *         &lt;element name="staff" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="position-id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="employee-id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="department-id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="is-primary" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *                   &lt;element name="is-leading" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
	"staff"
})
@XmlRootElement(name = "staff-list", namespace = "http://www.it.ru/logicECM/orgstructure/export-import/staff/1.0")
public class StaffList {

	@XmlElement(name = "staff", required = true, namespace = "http://www.it.ru/logicECM/orgstructure/export-import/staff/1.0")
	protected List<Staff> staff;

	/**
	 * Gets the value of the staff property.
	 *
	 * <p>
	 * This accessor method returns a reference to the live list,
	 * not a snapshot. Therefore any modification you make to the
	 * returned list will be present inside the JAXB object.
	 * This is why there is not a <CODE>set</CODE> method for the staff property.

 <p>
	 * For example, to add a new item, do as follows:
	 * <pre>
	 *    getStaffPosition().add(newItem);
	 * </pre>
	 *
	 *
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link StaffPositions.StaffPosition }
	 *
	 *
	 */
	public List<Staff> getStaff() {
		if (staff == null) {
			staff = new ArrayList<>();
		}
		return this.staff;
	}


}
