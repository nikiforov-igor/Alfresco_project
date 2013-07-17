
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
 *         &lt;element name="GetPersonSignificItemsResult" type="{http://it.ru/}ArrayOfDataItem" minOccurs="0"/>
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
    "getPersonSignificItemsResult"
})
@XmlRootElement(name = "GetPersonSignificItemsResponse")
public class GetPersonSignificItemsResponse {

    @XmlElement(name = "GetPersonSignificItemsResult")
    protected ArrayOfDataItem getPersonSignificItemsResult;

    /**
     * Gets the value of the getPersonSignificItemsResult property.
     *
     * @return
     *     possible object is
     *     {@link ArrayOfDataItem }
     *
     */
    public ArrayOfDataItem getGetPersonSignificItemsResult() {
        return getPersonSignificItemsResult;
    }

    /**
     * Sets the value of the getPersonSignificItemsResult property.
     *
     * @param value
     *     allowed object is
     *     {@link ArrayOfDataItem }
     *
     */
    public void setGetPersonSignificItemsResult(ArrayOfDataItem value) {
        this.getPersonSignificItemsResult = value;
    }

}
