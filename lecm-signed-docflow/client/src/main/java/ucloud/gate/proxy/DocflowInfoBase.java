
package ucloud.gate.proxy;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import ucloud.gate.proxy.docflow.BilateralDocflowInfo;
import ucloud.gate.proxy.docflow.InvoiceDocflowInfo;
import ucloud.gate.proxy.docflow.NonformalizedDocflowInfo;


/**
 * <p>Java class for DocflowInfoBase complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DocflowInfoBase">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DocflowId" type="{http://schemas.microsoft.com/2003/10/Serialization/}guid" minOccurs="0"/>
 *         &lt;element name="IsInbound" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="IsUnread" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="OperatorCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Receiver" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy}CompanyInfo" minOccurs="0"/>
 *         &lt;element name="Sender" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy}CompanyInfo" minOccurs="0"/>
 *         &lt;element name="Type" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy}EDocflowType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DocflowInfoBase", propOrder = {
    "description",
    "docflowId",
    "isInbound",
    "isUnread",
    "operatorCode",
    "receiver",
    "sender",
    "type"
})
@XmlSeeAlso({
    NonformalizedDocflowInfo.class,
    InvoiceDocflowInfo.class,
    BilateralDocflowInfo.class
})
public class DocflowInfoBase {

    @XmlElement(name = "Description", nillable = true)
    protected String description;
    @XmlElement(name = "DocflowId")
    protected String docflowId;
    @XmlElement(name = "IsInbound")
    protected Boolean isInbound;
    @XmlElement(name = "IsUnread")
    protected Boolean isUnread;
    @XmlElement(name = "OperatorCode", nillable = true)
    protected String operatorCode;
    @XmlElement(name = "Receiver", nillable = true)
    protected CompanyInfo receiver;
    @XmlElement(name = "Sender", nillable = true)
    protected CompanyInfo sender;
    @XmlElement(name = "Type")
    protected EDocflowType type;

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the docflowId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocflowId() {
        return docflowId;
    }

    /**
     * Sets the value of the docflowId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocflowId(String value) {
        this.docflowId = value;
    }

    /**
     * Gets the value of the isInbound property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsInbound() {
        return isInbound;
    }

    /**
     * Sets the value of the isInbound property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsInbound(Boolean value) {
        this.isInbound = value;
    }

    /**
     * Gets the value of the isUnread property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsUnread() {
        return isUnread;
    }

    /**
     * Sets the value of the isUnread property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsUnread(Boolean value) {
        this.isUnread = value;
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
     * Gets the value of the receiver property.
     * 
     * @return
     *     possible object is
     *     {@link CompanyInfo }
     *     
     */
    public CompanyInfo getReceiver() {
        return receiver;
    }

    /**
     * Sets the value of the receiver property.
     * 
     * @param value
     *     allowed object is
     *     {@link CompanyInfo }
     *     
     */
    public void setReceiver(CompanyInfo value) {
        this.receiver = value;
    }

    /**
     * Gets the value of the sender property.
     * 
     * @return
     *     possible object is
     *     {@link CompanyInfo }
     *     
     */
    public CompanyInfo getSender() {
        return sender;
    }

    /**
     * Sets the value of the sender property.
     * 
     * @param value
     *     allowed object is
     *     {@link CompanyInfo }
     *     
     */
    public void setSender(CompanyInfo value) {
        this.sender = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link EDocflowType }
     *     
     */
    public EDocflowType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link EDocflowType }
     *     
     */
    public void setType(EDocflowType value) {
        this.type = value;
    }

}
