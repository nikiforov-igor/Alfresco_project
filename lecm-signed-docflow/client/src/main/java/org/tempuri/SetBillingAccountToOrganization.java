
package org.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element name="operatorCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="signature" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *         &lt;element name="billingLogin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="billingPassword" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="existedAccount" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
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
    "operatorCode",
    "signature",
    "billingLogin",
    "billingPassword",
    "existedAccount"
})
@XmlRootElement(name = "SetBillingAccountToOrganization")
public class SetBillingAccountToOrganization {

    @XmlElement(nillable = true)
    protected String operatorCode;
    @XmlElement(nillable = true)
    protected byte[] signature;
    @XmlElement(nillable = true)
    protected String billingLogin;
    @XmlElement(nillable = true)
    protected String billingPassword;
    protected Boolean existedAccount;

    /**
     * Gets the value of the operatorCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOperatorCode() {
        return operatorCode;
    }

    /**
     * Sets the value of the operatorCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOperatorCode(String value) {
        this.operatorCode = value;
    }

    /**
     * Gets the value of the signature property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getSignature() {
        return signature;
    }

    /**
     * Sets the value of the signature property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setSignature(byte[] value) {
        this.signature = ((byte[]) value);
    }

    /**
     * Gets the value of the billingLogin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBillingLogin() {
        return billingLogin;
    }

    /**
     * Sets the value of the billingLogin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBillingLogin(String value) {
        this.billingLogin = value;
    }

    /**
     * Gets the value of the billingPassword property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBillingPassword() {
        return billingPassword;
    }

    /**
     * Sets the value of the billingPassword property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBillingPassword(String value) {
        this.billingPassword = value;
    }

    /**
     * Gets the value of the existedAccount property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isExistedAccount() {
        return existedAccount;
    }

    /**
     * Sets the value of the existedAccount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setExistedAccount(Boolean value) {
        this.existedAccount = value;
    }

}
