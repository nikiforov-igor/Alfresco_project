
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
 *         &lt;element name="SearchFileExpertsResult" type="{http://it.ru/}ArrayOfPerson" minOccurs="0"/>
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
    "searchFileExpertsResult"
})
@XmlRootElement(name = "SearchFileExpertsResponse")
public class SearchFileExpertsResponse {

    @XmlElement(name = "SearchFileExpertsResult")
    protected ArrayOfPerson searchFileExpertsResult;

    /**
     * Gets the value of the searchFileExpertsResult property.
     *
     * @return
     *     possible object is
     *     {@link ArrayOfPerson }
     *
     */
    public ArrayOfPerson getSearchFileExpertsResult() {
        return searchFileExpertsResult;
    }

    /**
     * Sets the value of the searchFileExpertsResult property.
     *
     * @param value
     *     allowed object is
     *     {@link ArrayOfPerson }
     *
     */
    public void setSearchFileExpertsResult(ArrayOfPerson value) {
        this.searchFileExpertsResult = value;
    }

}
