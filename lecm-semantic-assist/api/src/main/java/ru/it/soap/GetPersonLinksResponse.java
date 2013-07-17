
package ru.it.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
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
 *         &lt;element name="GetPersonLinksResult" type="{http://it.ru/}ArrayOfPersonsLink" minOccurs="0"/>
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
    "getPersonLinksResult"
})
@XmlRootElement(name = "GetPersonLinksResponse")
public class GetPersonLinksResponse {

    @XmlElement(name = "GetPersonLinksResult")
    protected ArrayOfPersonsLink getPersonLinksResult;

    /**
     * Gets the value of the getPersonLinksResult property.
     *
     * @return
     *     possible object is
     *     {@link ArrayOfPersonsLink }
     *
     */
    public ArrayOfPersonsLink getGetPersonLinksResult() {
        return getPersonLinksResult;
    }

    /**
     * Sets the value of the getPersonLinksResult property.
     *
     * @param value
     *     allowed object is
     *     {@link ArrayOfPersonsLink }
     *
     */
    public void setGetPersonLinksResult(ArrayOfPersonsLink value) {
        this.getPersonLinksResult = value;
    }

}
