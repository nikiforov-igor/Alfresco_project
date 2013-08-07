
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
 *         &lt;element name="VerifySignatureResult" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Exceptions}GateResponse" minOccurs="0"/>
 *         &lt;element name="signerInfo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="isSignatureValid" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
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
    "verifySignatureResult",
    "signerInfo",
    "isSignatureValid"
})
@XmlRootElement(name = "VerifySignatureResponse")
public class VerifySignatureResponse {

    @XmlElement(name = "VerifySignatureResult", nillable = true)
    protected GateResponse verifySignatureResult;
    @XmlElement(nillable = true)
    protected String signerInfo;
    protected Boolean isSignatureValid;

    /**
     * Gets the value of the verifySignatureResult property.
     * 
     * @return
     *     possible object is
     *     {@link GateResponse }
     *     
     */
    public GateResponse getVerifySignatureResult() {
        return verifySignatureResult;
    }

    /**
     * Sets the value of the verifySignatureResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link GateResponse }
     *     
     */
    public void setVerifySignatureResult(GateResponse value) {
        this.verifySignatureResult = value;
    }

    /**
     * Gets the value of the signerInfo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSignerInfo() {
        return signerInfo;
    }

    /**
     * Sets the value of the signerInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSignerInfo(String value) {
        this.signerInfo = value;
    }

    /**
     * Gets the value of the isSignatureValid property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsSignatureValid() {
        return isSignatureValid;
    }

    /**
     * Sets the value of the isSignatureValid property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsSignatureValid(Boolean value) {
        this.isSignatureValid = value;
    }

}
