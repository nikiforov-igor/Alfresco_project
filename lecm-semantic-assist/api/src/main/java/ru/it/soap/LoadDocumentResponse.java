
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
 *         &lt;element name="LoadDocumentResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "loadDocumentResult"
})
@XmlRootElement(name = "LoadDocumentResponse")
public class LoadDocumentResponse {

    @XmlElement(name = "LoadDocumentResult")
    protected boolean loadDocumentResult;

    /**
     * Gets the value of the loadDocumentResult property.
     *
     */
    public boolean isLoadDocumentResult() {
        return loadDocumentResult;
    }

    /**
     * Sets the value of the loadDocumentResult property.
     *
     */
    public void setLoadDocumentResult(boolean value) {
        this.loadDocumentResult = value;
    }

}
