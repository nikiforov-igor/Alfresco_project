
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
 *         &lt;element name="LoadPersonResult" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "loadPersonResult"
})
@XmlRootElement(name = "LoadPersonResponse")
public class LoadPersonResponse {

    @XmlElement(name = "LoadPersonResult")
    protected int loadPersonResult;

    /**
     * Gets the value of the loadPersonResult property.
     *
     */
    public int getLoadPersonResult() {
        return loadPersonResult;
    }

    /**
     * Sets the value of the loadPersonResult property.
     *
     */
    public void setLoadPersonResult(int value) {
        this.loadPersonResult = value;
    }

}
