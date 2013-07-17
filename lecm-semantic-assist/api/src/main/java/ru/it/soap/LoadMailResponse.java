
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
 *         &lt;element name="LoadMailResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "loadMailResult"
})
@XmlRootElement(name = "LoadMailResponse")
public class LoadMailResponse {

    @XmlElement(name = "LoadMailResult")
    protected boolean loadMailResult;

    /**
     * Gets the value of the loadMailResult property.
     *
     */
    public boolean isLoadMailResult() {
        return loadMailResult;
    }

    /**
     * Sets the value of the loadMailResult property.
     *
     */
    public void setLoadMailResult(boolean value) {
        this.loadMailResult = value;
    }

}
