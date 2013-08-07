
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
 *         &lt;element name="StartSmsAuthenticationResult" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Exceptions}GateResponse" minOccurs="0"/>
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
    "startSmsAuthenticationResult"
})
@XmlRootElement(name = "StartSmsAuthenticationResponse")
public class StartSmsAuthenticationResponse {

    @XmlElement(name = "StartSmsAuthenticationResult", nillable = true)
    protected GateResponse startSmsAuthenticationResult;

    /**
     * Gets the value of the startSmsAuthenticationResult property.
     * 
     * @return
     *     possible object is
     *     {@link GateResponse }
     *     
     */
    public GateResponse getStartSmsAuthenticationResult() {
        return startSmsAuthenticationResult;
    }

    /**
     * Sets the value of the startSmsAuthenticationResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link GateResponse }
     *     
     */
    public void setStartSmsAuthenticationResult(GateResponse value) {
        this.startSmsAuthenticationResult = value;
    }

}
