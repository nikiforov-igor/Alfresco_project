
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
 *         &lt;element name="MarkDocflowsAsReadResult" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Exceptions}GateResponse" minOccurs="0"/>
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
    "markDocflowsAsReadResult"
})
@XmlRootElement(name = "MarkDocflowsAsReadResponse")
public class MarkDocflowsAsReadResponse {

    @XmlElement(name = "MarkDocflowsAsReadResult", nillable = true)
    protected GateResponse markDocflowsAsReadResult;

    /**
     * Gets the value of the markDocflowsAsReadResult property.
     * 
     * @return
     *     possible object is
     *     {@link GateResponse }
     *     
     */
    public GateResponse getMarkDocflowsAsReadResult() {
        return markDocflowsAsReadResult;
    }

    /**
     * Sets the value of the markDocflowsAsReadResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link GateResponse }
     *     
     */
    public void setMarkDocflowsAsReadResult(GateResponse value) {
        this.markDocflowsAsReadResult = value;
    }

}
