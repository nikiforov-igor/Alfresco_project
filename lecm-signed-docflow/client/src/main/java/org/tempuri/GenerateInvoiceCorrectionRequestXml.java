
package org.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import ucloud.gate.proxy.generating.documents.invoice.CorrectionRequest;


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
 *         &lt;element name="correctionRequest" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice}CorrectionRequest" minOccurs="0"/>
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
    "correctionRequest"
})
@XmlRootElement(name = "GenerateInvoiceCorrectionRequestXml")
public class GenerateInvoiceCorrectionRequestXml {

    @XmlElement(nillable = true)
    protected CorrectionRequest correctionRequest;

    /**
     * Gets the value of the correctionRequest property.
     * 
     * @return
     *     possible object is
     *     {@link CorrectionRequest }
     *     
     */
    public CorrectionRequest getCorrectionRequest() {
        return correctionRequest;
    }

    /**
     * Sets the value of the correctionRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link CorrectionRequest }
     *     
     */
    public void setCorrectionRequest(CorrectionRequest value) {
        this.correctionRequest = value;
    }

}
