
package org.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import ucloud.gate.proxy.exceptions.GateResponse;
import ucloud.gate.proxy.registration.RegisterResponse;


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
 *         &lt;element name="GetRegisterResponseResult" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Exceptions}GateResponse" minOccurs="0"/>
 *         &lt;element name="operatorResponse" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration}RegisterResponse" minOccurs="0"/>
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
    "getRegisterResponseResult",
    "operatorResponse"
})
@XmlRootElement(name = "GetRegisterResponseResponse")
public class GetRegisterResponseResponse {

    @XmlElement(name = "GetRegisterResponseResult", nillable = true)
    protected GateResponse getRegisterResponseResult;
    @XmlElement(nillable = true)
    protected RegisterResponse operatorResponse;

    /**
     * Gets the value of the getRegisterResponseResult property.
     * 
     * @return
     *     possible object is
     *     {@link GateResponse }
     *     
     */
    public GateResponse getGetRegisterResponseResult() {
        return getRegisterResponseResult;
    }

    /**
     * Sets the value of the getRegisterResponseResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link GateResponse }
     *     
     */
    public void setGetRegisterResponseResult(GateResponse value) {
        this.getRegisterResponseResult = value;
    }

    /**
     * Gets the value of the operatorResponse property.
     * 
     * @return
     *     possible object is
     *     {@link RegisterResponse }
     *     
     */
    public RegisterResponse getOperatorResponse() {
        return operatorResponse;
    }

    /**
     * Sets the value of the operatorResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link RegisterResponse }
     *     
     */
    public void setOperatorResponse(RegisterResponse value) {
        this.operatorResponse = value;
    }

}
