
package org.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import ucloud.gate.proxy.docflow.ReceivedDocumentInfo;
import ucloud.gate.proxy.generating.documents.Signer;
import ucloud.gate.proxy.generating.documents.common.ParticipantBase;


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
 *         &lt;element name="signer" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments}Signer" minOccurs="0"/>
 *         &lt;element name="senderCompany" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common}ParticipantBase" minOccurs="0"/>
 *         &lt;element name="parentDocument" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Docflow}ReceivedDocumentInfo" minOccurs="0"/>
 *         &lt;element name="generatedDocDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="operatorCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "signer",
    "senderCompany",
    "parentDocument",
    "generatedDocDate",
    "operatorCode"
})
@XmlRootElement(name = "GenerateNotificationXml")
public class GenerateNotificationXml {

    @XmlElement(nillable = true)
    protected Signer signer;
    @XmlElement(nillable = true)
    protected ParticipantBase senderCompany;
    @XmlElement(nillable = true)
    protected ReceivedDocumentInfo parentDocument;
    @XmlElement(nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar generatedDocDate;
    @XmlElement(nillable = true)
    protected String operatorCode;

    /**
     * Gets the value of the signer property.
     * 
     * @return
     *     possible object is
     *     {@link Signer }
     *     
     */
    public Signer getSigner() {
        return signer;
    }

    /**
     * Sets the value of the signer property.
     * 
     * @param value
     *     allowed object is
     *     {@link Signer }
     *     
     */
    public void setSigner(Signer value) {
        this.signer = value;
    }

    /**
     * Gets the value of the senderCompany property.
     * 
     * @return
     *     possible object is
     *     {@link ParticipantBase }
     *     
     */
    public ParticipantBase getSenderCompany() {
        return senderCompany;
    }

    /**
     * Sets the value of the senderCompany property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParticipantBase }
     *     
     */
    public void setSenderCompany(ParticipantBase value) {
        this.senderCompany = value;
    }

    /**
     * Gets the value of the parentDocument property.
     * 
     * @return
     *     possible object is
     *     {@link ReceivedDocumentInfo }
     *     
     */
    public ReceivedDocumentInfo getParentDocument() {
        return parentDocument;
    }

    /**
     * Sets the value of the parentDocument property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReceivedDocumentInfo }
     *     
     */
    public void setParentDocument(ReceivedDocumentInfo value) {
        this.parentDocument = value;
    }

    /**
     * Gets the value of the generatedDocDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getGeneratedDocDate() {
        return generatedDocDate;
    }

    /**
     * Sets the value of the generatedDocDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setGeneratedDocDate(XMLGregorianCalendar value) {
        this.generatedDocDate = value;
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
