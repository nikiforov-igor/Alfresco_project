
package ru.it.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DepartmentLoad complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="DepartmentLoad">
 *   &lt;complexContent>
 *     &lt;extension base="{http://it.ru/}DepartmentBase">
 *       &lt;sequence>
 *         &lt;element name="ParentIdentities" type="{http://it.ru/}ArrayOfString" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DepartmentLoad", propOrder = {
    "parentIdentities"
})
public class DepartmentLoad
    extends DepartmentBase
{

    @XmlElement(name = "ParentIdentities")
    protected ArrayOfString parentIdentities;

    /**
     * Gets the value of the parentIdentities property.
     *
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *
     */
    public ArrayOfString getParentIdentities() {
        return parentIdentities;
    }

    /**
     * Sets the value of the parentIdentities property.
     *
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *
     */
    public void setParentIdentities(ArrayOfString value) {
        this.parentIdentities = value;
    }

}
