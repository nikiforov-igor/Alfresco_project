
package org.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import ucloud.gate.proxy.DocumentToSend;


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
 *         &lt;element name="doc" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy}DocumentToSend" minOccurs="0"/>
 *         &lt;element name="operatorCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="billingTicket" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "doc",
    "operatorCode",
    "billingTicket"
})
@XmlRootElement(name = "SendDocument")
public class SendDocument {

    @XmlElement(nillable = true)
    protected DocumentToSend doc;
    @XmlElement(nillable = true)
    protected String operatorCode;
    @XmlElement(nillable = true)
    protected String billingTicket;

    /**
     * Gets the value of the doc property.
     * 
     * @return
     *     possible object is
     *     {@link DocumentToSend }
     *     
     */
    public DocumentToSend getDoc() {
        return doc;
    }

    /**
     * Sets the value of the doc property.
     * 
     * @param value
     *     allowed object is
     *     {@link DocumentToSend }
     *     
     */
    public void setDoc(DocumentToSend value) {
        this.doc = value;
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

    /**
     * Gets the value of the billingTicket property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBillingTicket() {
        return billingTicket;
    }

    /**
     * Sets the value of the billingTicket property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBillingTicket(String value) {
        this.billingTicket = value;
    }

}
