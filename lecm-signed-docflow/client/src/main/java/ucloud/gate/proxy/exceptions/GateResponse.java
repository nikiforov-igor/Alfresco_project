
package ucloud.gate.proxy.exceptions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import ucloud.gate.proxy.ArrayOfAuthorizationError;


/**
 * <p>Java class for GateResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GateResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AuthorizationErrors" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy}ArrayOfAuthorizationError" minOccurs="0"/>
 *         &lt;element name="Message" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OperatorMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ResponseType" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Exceptions}EResponseType" minOccurs="0"/>
 *         &lt;element name="StackTrace" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GateResponse", propOrder = {
    "authorizationErrors",
    "message",
    "operatorMessage",
    "responseType",
    "stackTrace"
})
public class GateResponse {

    @XmlElement(name = "AuthorizationErrors", nillable = true)
    protected ArrayOfAuthorizationError authorizationErrors;
    @XmlElement(name = "Message", nillable = true)
    protected String message;
    @XmlElement(name = "OperatorMessage", nillable = true)
    protected String operatorMessage;
    @XmlElement(name = "ResponseType")
    protected EResponseType responseType;
    @XmlElement(name = "StackTrace", nillable = true)
    protected String stackTrace;

    /**
     * Gets the value of the authorizationErrors property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfAuthorizationError }
     *     
     */
    public ArrayOfAuthorizationError getAuthorizationErrors() {
        return authorizationErrors;
    }

    /**
     * Sets the value of the authorizationErrors property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfAuthorizationError }
     *     
     */
    public void setAuthorizationErrors(ArrayOfAuthorizationError value) {
        this.authorizationErrors = value;
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
     * Gets the value of the operatorMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOperatorMessage() {
        return operatorMessage;
    }

    /**
     * Sets the value of the operatorMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOperatorMessage(String value) {
        this.operatorMessage = value;
    }

    /**
     * Gets the value of the responseType property.
     * 
     * @return
     *     possible object is
     *     {@link EResponseType }
     *     
     */
    public EResponseType getResponseType() {
        return responseType;
    }

    /**
     * Sets the value of the responseType property.
     * 
     * @param value
     *     allowed object is
     *     {@link EResponseType }
     *     
     */
    public void setResponseType(EResponseType value) {
        this.responseType = value;
    }

    /**
     * Gets the value of the stackTrace property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStackTrace() {
        return stackTrace;
    }

    /**
     * Sets the value of the stackTrace property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStackTrace(String value) {
        this.stackTrace = value;
    }

}
