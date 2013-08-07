
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
 *         &lt;element name="StartRegisterUserBySmsResult" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Exceptions}GateResponse" minOccurs="0"/>
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
    "startRegisterUserBySmsResult"
})
@XmlRootElement(name = "StartRegisterUserBySmsResponse")
public class StartRegisterUserBySmsResponse {

    @XmlElement(name = "StartRegisterUserBySmsResult", nillable = true)
    protected GateResponse startRegisterUserBySmsResult;

    /**
     * Gets the value of the startRegisterUserBySmsResult property.
     * 
     * @return
     *     possible object is
     *     {@link GateResponse }
     *     
     */
    public GateResponse getStartRegisterUserBySmsResult() {
        return startRegisterUserBySmsResult;
    }

    /**
     * Sets the value of the startRegisterUserBySmsResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link GateResponse }
     *     
     */
    public void setStartRegisterUserBySmsResult(GateResponse value) {
        this.startRegisterUserBySmsResult = value;
    }

}
