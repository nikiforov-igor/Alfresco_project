
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
 *         &lt;element name="LoadCommunicationResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "loadCommunicationResult"
})
@XmlRootElement(name = "LoadCommunicationResponse")
public class LoadCommunicationResponse {

    @XmlElement(name = "LoadCommunicationResult")
    protected boolean loadCommunicationResult;

    /**
     * Gets the value of the loadCommunicationResult property.
     *
     */
    public boolean isLoadCommunicationResult() {
        return loadCommunicationResult;
    }

    /**
     * Sets the value of the loadCommunicationResult property.
     *
     */
    public void setLoadCommunicationResult(boolean value) {
        this.loadCommunicationResult = value;
    }

}
