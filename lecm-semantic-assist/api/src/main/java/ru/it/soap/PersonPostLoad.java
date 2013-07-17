
package ru.it.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PersonPostLoad complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="PersonPostLoad">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PersonIdentities" type="{http://it.ru/}ArrayOfString" minOccurs="0"/>
 *         &lt;element name="DepartmentIdentities" type="{http://it.ru/}ArrayOfString" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PersonPostLoad", propOrder = {
    "name",
    "personIdentities",
    "departmentIdentities"
})
public class PersonPostLoad {

    @XmlElement(name = "Name")
    protected String name;
    @XmlElement(name = "PersonIdentities")
    protected ArrayOfString personIdentities;
    @XmlElement(name = "DepartmentIdentities")
    protected ArrayOfString departmentIdentities;

    /**
     * Gets the value of the name property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the personIdentities property.
     *
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *
     */
    public ArrayOfString getPersonIdentities() {
        return personIdentities;
    }

    /**
     * Sets the value of the personIdentities property.
     *
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *
     */
    public void setPersonIdentities(ArrayOfString value) {
        this.personIdentities = value;
    }

    /**
     * Gets the value of the departmentIdentities property.
     *
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *
     */
    public ArrayOfString getDepartmentIdentities() {
        return departmentIdentities;
    }

    /**
     * Sets the value of the departmentIdentities property.
     *
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *
     */
    public void setDepartmentIdentities(ArrayOfString value) {
        this.departmentIdentities = value;
    }

}
