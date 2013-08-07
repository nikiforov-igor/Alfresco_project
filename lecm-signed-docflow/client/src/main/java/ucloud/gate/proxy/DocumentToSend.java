
package ucloud.gate.proxy;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import com.microsoft.schemas.serialization.arrays.ArrayOfbase64Binary;
import ucloud.gate.proxy.docflow.EDocflowTransactionType;


/**
 * <p>Java class for DocumentToSend complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DocumentToSend">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Comment" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Content" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *         &lt;element name="DocflowId" type="{http://schemas.microsoft.com/2003/10/Serialization/}guid" minOccurs="0"/>
 *         &lt;element name="DocumentType" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy}EDocumentType" minOccurs="0"/>
 *         &lt;element name="FileName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Id" type="{http://schemas.microsoft.com/2003/10/Serialization/}guid" minOccurs="0"/>
 *         &lt;element name="Receiver" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy}CompanyInfo" minOccurs="0"/>
 *         &lt;element name="Signatures" type="{http://schemas.microsoft.com/2003/10/Serialization/Arrays}ArrayOfbase64Binary" minOccurs="0"/>
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
@XmlType(name = "DocumentToSend", propOrder = {
    "comment",
    "content",
    "docflowId",
    "documentType",
    "fileName",
    "id",
    "receiver",
    "signatures",
    "transactionType"
})
public class DocumentToSend {

    @XmlElement(name = "Comment", nillable = true)
    protected String comment;
    @XmlElement(name = "Content", nillable = true)
    protected byte[] content;
    @XmlElement(name = "DocflowId", nillable = true)
    protected String docflowId;
    @XmlElement(name = "DocumentType", nillable = true)
    protected EDocumentType documentType;
    @XmlElement(name = "FileName", nillable = true)
    protected String fileName;
    @XmlElement(name = "Id", nillable = true)
    protected String id;
    @XmlElement(name = "Receiver", nillable = true)
    protected CompanyInfo receiver;
    @XmlElement(name = "Signatures", nillable = true)
    protected ArrayOfbase64Binary signatures;
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
     * Gets the value of the content property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * Sets the value of the content property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setContent(byte[] value) {
        this.content = ((byte[]) value);
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
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
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
     * Gets the value of the signatures property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfbase64Binary }
     *     
     */
    public ArrayOfbase64Binary getSignatures() {
        return signatures;
    }

    /**
     * Sets the value of the signatures property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfbase64Binary }
     *     
     */
    public void setSignatures(ArrayOfbase64Binary value) {
        this.signatures = value;
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
