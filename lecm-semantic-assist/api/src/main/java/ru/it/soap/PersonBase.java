
package ru.it.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PersonBase complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="PersonBase">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="IsExpired" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="Attrs" type="{http://it.ru/}ArrayOfPersonAttrs" minOccurs="0"/>
 *         &lt;element name="ExternIdentities" type="{http://it.ru/}ArrayOfString" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PersonBase", propOrder = {
    "isExpired",
    "attrs",
    "externIdentities"
})
@XmlSeeAlso({
    PersonLoad.class,
    Person.class
})
public class PersonBase {

    @XmlElement(name = "IsExpired")
    protected boolean isExpired;
    @XmlElement(name = "Attrs")
    protected ArrayOfPersonAttrs attrs;
    @XmlElement(name = "ExternIdentities")
    protected ArrayOfString externIdentities;

    /**
     * Gets the value of the isExpired property.
     *
     */
    public boolean isIsExpired() {
        return isExpired;
    }

    /**
     * Sets the value of the isExpired property.
     *
     */
    public void setIsExpired(boolean value) {
        this.isExpired = value;
    }

    /**
     * Gets the value of the attrs property.
     *
     * @return
     *     possible object is
     *     {@link ArrayOfPersonAttrs }
     *
     */
    public ArrayOfPersonAttrs getAttrs() {
        return attrs;
    }

    /**
     * Sets the value of the attrs property.
     *
     * @param value
     *     allowed object is
     *     {@link ArrayOfPersonAttrs }
     *
     */
    public void setAttrs(ArrayOfPersonAttrs value) {
        this.attrs = value;
    }

    /**
     * Gets the value of the externIdentities property.
     *
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *
     */
    public ArrayOfString getExternIdentities() {
        return externIdentities;
    }

    /**
     * Sets the value of the externIdentities property.
     *
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *
     */
    public void setExternIdentities(ArrayOfString value) {
        this.externIdentities = value;
    }

}
