
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
 *         &lt;element name="GetPersonsResult" type="{http://it.ru/}ArrayOfPerson" minOccurs="0"/>
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
    "getPersonsResult"
})
@XmlRootElement(name = "GetPersonsResponse")
public class GetPersonsResponse {

    @XmlElement(name = "GetPersonsResult")
    protected ArrayOfPerson getPersonsResult;

    /**
     * Gets the value of the getPersonsResult property.
     *
     * @return
     *     possible object is
     *     {@link ArrayOfPerson }
     *
     */
    public ArrayOfPerson getGetPersonsResult() {
        return getPersonsResult;
    }

    /**
     * Sets the value of the getPersonsResult property.
     *
     * @param value
     *     allowed object is
     *     {@link ArrayOfPerson }
     *
     */
    public void setGetPersonsResult(ArrayOfPerson value) {
        this.getPersonsResult = value;
    }

}
