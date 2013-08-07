
package ucloud.gate.proxy.docflow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import com.microsoft.schemas.serialization.arrays.ArrayOfstring;
import ucloud.gate.proxy.EDocflowType;
import ucloud.gate.proxy.generating.documents.common.ParticipantBase;


/**
 * <p>Java class for ReceivedDocumentInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReceivedDocumentInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Date" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DocflowType" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy}EDocflowType" minOccurs="0"/>
 *         &lt;element name="FileName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Number" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ReceiveDateTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="Sender" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common}ParticipantBase" minOccurs="0"/>
 *         &lt;element name="Signatures" type="{http://schemas.microsoft.com/2003/10/Serialization/Arrays}ArrayOfstring" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReceivedDocumentInfo", propOrder = {
    "date",
    "docflowType",
    "fileName",
    "number",
    "receiveDateTime",
    "sender",
    "signatures"
})
public class ReceivedDocumentInfo {

    @XmlElement(name = "Date", nillable = true)
    protected String date;
    @XmlElement(name = "DocflowType")
    protected EDocflowType docflowType;
    @XmlElement(name = "FileName", nillable = true)
    protected String fileName;
    @XmlElement(name = "Number", nillable = true)
    protected String number;
    @XmlElement(name = "ReceiveDateTime")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar receiveDateTime;
    @XmlElement(name = "Sender", nillable = true)
    protected ParticipantBase sender;
    @XmlElement(name = "Signatures", nillable = true)
    protected ArrayOfstring signatures;

    /**
     * Gets the value of the date property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets the value of the date property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDate(String value) {
        this.date = value;
    }

    /**
     * Gets the value of the docflowType property.
     * 
     * @return
     *     possible object is
     *     {@link EDocflowType }
     *     
     */
    public EDocflowType getDocflowType() {
        return docflowType;
    }

    /**
     * Sets the value of the docflowType property.
     * 
     * @param value
     *     allowed object is
     *     {@link EDocflowType }
     *     
     */
    public void setDocflowType(EDocflowType value) {
        this.docflowType = value;
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
     * Gets the value of the number property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumber() {
        return number;
    }

    /**
     * Sets the value of the number property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumber(String value) {
        this.number = value;
    }

    /**
     * Gets the value of the receiveDateTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getReceiveDateTime() {
        return receiveDateTime;
    }

    /**
     * Sets the value of the receiveDateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setReceiveDateTime(XMLGregorianCalendar value) {
        this.receiveDateTime = value;
    }

    /**
     * Gets the value of the sender property.
     * 
     * @return
     *     possible object is
     *     {@link ParticipantBase }
     *     
     */
    public ParticipantBase getSender() {
        return sender;
    }

    /**
     * Sets the value of the sender property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParticipantBase }
     *     
     */
    public void setSender(ParticipantBase value) {
        this.sender = value;
    }

    /**
     * Gets the value of the signatures property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfstring }
     *     
     */
    public ArrayOfstring getSignatures() {
        return signatures;
    }

    /**
     * Sets the value of the signatures property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfstring }
     *     
     */
    public void setSignatures(ArrayOfstring value) {
        this.signatures = value;
    }

}
