
package ru.it.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="com" type="{http://it.ru/}CommunicationLoad" minOccurs="0"/>
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
    "com"
})
@XmlRootElement(name = "LoadCommunication")
public class LoadCommunication {

    protected CommunicationLoad com;

    /**
     * Gets the value of the com property.
     *
     * @return
     *     possible object is
     *     {@link CommunicationLoad }
     *
     */
    public CommunicationLoad getCom() {
        return com;
    }

    /**
     * Sets the value of the com property.
     *
     * @param value
     *     allowed object is
     *     {@link CommunicationLoad }
     *
     */
    public void setCom(CommunicationLoad value) {
        this.com = value;
    }

}
