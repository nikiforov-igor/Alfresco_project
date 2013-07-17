
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
 *         &lt;element name="LoadCallResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "loadCallResult"
})
@XmlRootElement(name = "LoadCallResponse")
public class LoadCallResponse {

    @XmlElement(name = "LoadCallResult")
    protected boolean loadCallResult;

    /**
     * Gets the value of the loadCallResult property.
     *
     */
    public boolean isLoadCallResult() {
        return loadCallResult;
    }

    /**
     * Sets the value of the loadCallResult property.
     *
     */
    public void setLoadCallResult(boolean value) {
        this.loadCallResult = value;
    }

}
