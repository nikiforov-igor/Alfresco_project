
package org.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import ucloud.gate.proxy.ArrayOfRegistrationInfo;
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
 *         &lt;element name="GetRegistrationInfoResult" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Exceptions}GateResponse" minOccurs="0"/>
 *         &lt;element name="registrationInfos" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy}ArrayOfRegistrationInfo" minOccurs="0"/>
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
    "getRegistrationInfoResult",
    "registrationInfos"
})
@XmlRootElement(name = "GetRegistrationInfoResponse")
public class GetRegistrationInfoResponse {

    @XmlElement(name = "GetRegistrationInfoResult", nillable = true)
    protected GateResponse getRegistrationInfoResult;
    @XmlElement(nillable = true)
    protected ArrayOfRegistrationInfo registrationInfos;

    /**
     * Gets the value of the getRegistrationInfoResult property.
     * 
     * @return
     *     possible object is
     *     {@link GateResponse }
     *     
     */
    public GateResponse getGetRegistrationInfoResult() {
        return getRegistrationInfoResult;
    }

    /**
     * Sets the value of the getRegistrationInfoResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link GateResponse }
     *     
     */
    public void setGetRegistrationInfoResult(GateResponse value) {
        this.getRegistrationInfoResult = value;
    }

    /**
     * Gets the value of the registrationInfos property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfRegistrationInfo }
     *     
     */
    public ArrayOfRegistrationInfo getRegistrationInfos() {
        return registrationInfos;
    }

    /**
     * Sets the value of the registrationInfos property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfRegistrationInfo }
     *     
     */
    public void setRegistrationInfos(ArrayOfRegistrationInfo value) {
        this.registrationInfos = value;
    }

}
