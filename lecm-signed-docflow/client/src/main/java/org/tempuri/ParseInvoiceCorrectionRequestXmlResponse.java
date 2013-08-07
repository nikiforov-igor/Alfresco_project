
package org.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
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
 *         &lt;element name="ParseInvoiceCorrectionRequestXmlResult" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Exceptions}GateResponse" minOccurs="0"/>
 *         &lt;element name="requestText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "parseInvoiceCorrectionRequestXmlResult",
    "requestText"
})
@XmlRootElement(name = "ParseInvoiceCorrectionRequestXmlResponse")
public class ParseInvoiceCorrectionRequestXmlResponse {

    @XmlElement(name = "ParseInvoiceCorrectionRequestXmlResult", nillable = true)
    protected GateResponse parseInvoiceCorrectionRequestXmlResult;
    @XmlElement(nillable = true)
    protected String requestText;

    /**
     * Gets the value of the parseInvoiceCorrectionRequestXmlResult property.
     * 
     * @return
     *     possible object is
     *     {@link GateResponse }
     *     
     */
    public GateResponse getParseInvoiceCorrectionRequestXmlResult() {
        return parseInvoiceCorrectionRequestXmlResult;
    }

    /**
     * Sets the value of the parseInvoiceCorrectionRequestXmlResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link GateResponse }
     *     
     */
    public void setParseInvoiceCorrectionRequestXmlResult(GateResponse value) {
        this.parseInvoiceCorrectionRequestXmlResult = value;
    }

    /**
     * Gets the value of the requestText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestText() {
        return requestText;
    }

    /**
     * Sets the value of the requestText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestText(String value) {
        this.requestText = value;
    }

}
