
package ucloud.gate.proxy;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AuthorizationError complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AuthorizationError">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AuthenticationType" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy}EOperatorAuthenticationType" minOccurs="0"/>
 *         &lt;element name="CertificateIssuerName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CertificateThumbprint" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="EncryptedToken" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Message" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OperatorCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthorizationError", propOrder = {
    "authenticationType",
    "certificateIssuerName",
    "certificateThumbprint",
    "encryptedToken",
    "message",
    "operatorCode"
})
public class AuthorizationError {

    @XmlList
    @XmlElement(name = "AuthenticationType")
    protected List<String> authenticationType;
    @XmlElement(name = "CertificateIssuerName", nillable = true)
    protected String certificateIssuerName;
    @XmlElement(name = "CertificateThumbprint", nillable = true)
    protected String certificateThumbprint;
    @XmlElement(name = "EncryptedToken", nillable = true)
    protected String encryptedToken;
    @XmlElement(name = "Message", nillable = true)
    protected String message;
    @XmlElement(name = "OperatorCode", nillable = true)
    protected String operatorCode;

    /**
     * Gets the value of the authenticationType property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the authenticationType property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAuthenticationType().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getAuthenticationType() {
        if (authenticationType == null) {
            authenticationType = new ArrayList<String>();
        }
        return this.authenticationType;
    }

    /**
     * Gets the value of the certificateIssuerName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCertificateIssuerName() {
        return certificateIssuerName;
    }

    /**
     * Sets the value of the certificateIssuerName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCertificateIssuerName(String value) {
        this.certificateIssuerName = value;
    }

    /**
     * Gets the value of the certificateThumbprint property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCertificateThumbprint() {
        return certificateThumbprint;
    }

    /**
     * Sets the value of the certificateThumbprint property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCertificateThumbprint(String value) {
        this.certificateThumbprint = value;
    }

    /**
     * Gets the value of the encryptedToken property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEncryptedToken() {
        return encryptedToken;
    }

    /**
     * Sets the value of the encryptedToken property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEncryptedToken(String value) {
        this.encryptedToken = value;
    }

    /**
     * Gets the value of the message property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the value of the message property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessage(String value) {
        this.message = value;
    }

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

}
