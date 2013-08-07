
package org.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import ucloud.gate.proxy.DocumentContent;
import ucloud.gate.proxy.exceptions.GateResponse;


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
 *         &lt;element name="GetDocumentContentResult" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Exceptions}GateResponse" minOccurs="0"/>
 *         &lt;element name="docContent" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy}DocumentContent" minOccurs="0"/>
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
    "getDocumentContentResult",
    "docContent"
})
@XmlRootElement(name = "GetDocumentContentResponse")
public class GetDocumentContentResponse {

    @XmlElement(name = "GetDocumentContentResult", nillable = true)
    protected GateResponse getDocumentContentResult;
    @XmlElement(nillable = true)
    protected DocumentContent docContent;

    /**
     * Gets the value of the getDocumentContentResult property.
     * 
     * @return
     *     possible object is
     *     {@link GateResponse }
     *     
     */
    public GateResponse getGetDocumentContentResult() {
        return getDocumentContentResult;
    }

    /**
     * Sets the value of the getDocumentContentResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link GateResponse }
     *     
     */
    public void setGetDocumentContentResult(GateResponse value) {
        this.getDocumentContentResult = value;
    }

    /**
     * Gets the value of the docContent property.
     * 
     * @return
     *     possible object is
     *     {@link DocumentContent }
     *     
     */
    public DocumentContent getDocContent() {
        return docContent;
    }

    /**
     * Sets the value of the docContent property.
     * 
     * @param value
     *     allowed object is
     *     {@link DocumentContent }
     *     
     */
    public void setDocContent(DocumentContent value) {
        this.docContent = value;
    }

}
