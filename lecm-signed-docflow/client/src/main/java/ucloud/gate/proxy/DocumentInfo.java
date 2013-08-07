
package ucloud.gate.proxy;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import ucloud.gate.proxy.docflow.EDocflowTransactionType;


/**
 * <p>Java class for DocumentInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DocumentInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Comment" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DocflowId" type="{http://schemas.microsoft.com/2003/10/Serialization/}guid" minOccurs="0"/>
 *         &lt;element name="DocumentId" type="{http://schemas.microsoft.com/2003/10/Serialization/}guid" minOccurs="0"/>
 *         &lt;element name="DocumentIdPartners" type="{http://schemas.microsoft.com/2003/10/Serialization/}guid" minOccurs="0"/>
 *         &lt;element name="DocumentType" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy}EDocumentType" minOccurs="0"/>
 *         &lt;element name="FileName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="IsUnread" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ParentDocumentId" type="{http://schemas.microsoft.com/2003/10/Serialization/}guid" minOccurs="0"/>
 *         &lt;element name="Receiver" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy}CompanyInfo" minOccurs="0"/>
 *         &lt;element name="Sender" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy}CompanyInfo" minOccurs="0"/>
 *         &lt;element name="TransactionType" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Docflow}EDocflowTransactionType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DocumentInfo", propOrder = {
    "comment",
    "docflowId",
    "documentId",
    "documentIdPartners",
    "documentType",
    "fileName",
    "isUnread",
    "parentDocumentId",
    "receiver",
    "sender",
    "transactionType"
})
public class DocumentInfo {

    @XmlElement(name = "Comment", nillable = true)
    protected String comment;
    @XmlElement(name = "DocflowId", nillable = true)
    protected String docflowId;
    @XmlElement(name = "DocumentId", nillable = true)
    protected String documentId;
    @XmlElement(name = "DocumentIdPartners", nillable = true)
    protected String documentIdPartners;
    @XmlElement(name = "DocumentType")
    protected EDocumentType documentType;
    @XmlElement(name = "FileName", nillable = true)
    protected String fileName;
    @XmlElement(name = "IsUnread")
    protected Boolean isUnread;
    @XmlElement(name = "ParentDocumentId", nillable = true)
    protected String parentDocumentId;
    @XmlElement(name = "Receiver", nillable = true)
    protected CompanyInfo receiver;
    @XmlElement(name = "Sender", nillable = true)
    protected CompanyInfo sender;
    @XmlElement(name = "TransactionType")
    protected EDocflowTransactionType transactionType;

    /**
     * Gets the value of the comment property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets the value of the comment property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComment(String value) {
        this.comment = value;
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
     * Gets the value of the documentId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocumentId() {
        return documentId;
    }

    /**
     * Sets the value of the documentId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocumentId(String value) {
        this.documentId = value;
    }

    /**
     * Gets the value of the documentIdPartners property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocumentIdPartners() {
        return documentIdPartners;
    }

    /**
     * Sets the value of the documentIdPartners property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocumentIdPartners(String value) {
        this.documentIdPartners = value;
    }

    /**
     * Gets the value of the documentType property.
     * 
     * @return
     *     possible object is
     *     {@link EDocumentType }
     *     
     */
    public EDocumentType getDocumentType() {
        return documentType;
    }

    /**
     * Sets the value of the documentType property.
     * 
     * @param value
     *     allowed object is
     *     {@link EDocumentType }
     *     
     */
    public void setDocumentType(EDocumentType value) {
        this.documentType = value;
    }

    /**
     * Gets the value of the fileName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets the value of the fileName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFileName(String value) {
        this.fileName = value;
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
     * Gets the value of the parentDocumentId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParentDocumentId() {
        return parentDocumentId;
    }

    /**
     * Sets the value of the parentDocumentId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParentDocumentId(String value) {
        this.parentDocumentId = value;
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
     * Gets the value of the transactionType property.
     * 
     * @return
     *     possible object is
     *     {@link EDocflowTransactionType }
     *     
     */
    public EDocflowTransactionType getTransactionType() {
        return transactionType;
    }

    /**
     * Sets the value of the transactionType property.
     * 
     * @param value
     *     allowed object is
     *     {@link EDocflowTransactionType }
     *     
     */
    public void setTransactionType(EDocflowTransactionType value) {
        this.transactionType = value;
    }

}
