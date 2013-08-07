
package org.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import ucloud.gate.proxy.DocumentToSend;
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
 *         &lt;element name="GenerateInvoiceXmlResult" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Exceptions}GateResponse" minOccurs="0"/>
 *         &lt;element name="generatedDoc" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy}DocumentToSend" minOccurs="0"/>
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
    "generateInvoiceXmlResult",
    "generatedDoc"
})
@XmlRootElement(name = "GenerateInvoiceXmlResponse")
public class GenerateInvoiceXmlResponse {

    @XmlElement(name = "GenerateInvoiceXmlResult", nillable = true)
    protected GateResponse generateInvoiceXmlResult;
    @XmlElement(nillable = true)
    protected DocumentToSend generatedDoc;

    /**
     * Gets the value of the generateInvoiceXmlResult property.
     * 
     * @return
     *     possible object is
     *     {@link GateResponse }
     *     
     */
    public GateResponse getGenerateInvoiceXmlResult() {
        return generateInvoiceXmlResult;
    }

    /**
     * Sets the value of the generateInvoiceXmlResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link GateResponse }
     *     
     */
    public void setGenerateInvoiceXmlResult(GateResponse value) {
        this.generateInvoiceXmlResult = value;
    }

    /**
     * Gets the value of the generatedDoc property.
     * 
     * @return
     *     possible object is
     *     {@link DocumentToSend }
     *     
     */
    public DocumentToSend getGeneratedDoc() {
        return generatedDoc;
    }

    /**
     * Sets the value of the generatedDoc property.
     * 
     * @param value
     *     allowed object is
     *     {@link DocumentToSend }
     *     
     */
    public void setGeneratedDoc(DocumentToSend value) {
        this.generatedDoc = value;
    }

}
