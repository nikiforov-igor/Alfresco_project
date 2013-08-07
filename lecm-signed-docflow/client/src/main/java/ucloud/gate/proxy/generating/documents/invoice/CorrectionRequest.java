
package ucloud.gate.proxy.generating.documents.invoice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import ucloud.gate.proxy.docflow.ReceivedDocumentInfo;
import ucloud.gate.proxy.generating.documents.Signer;
import ucloud.gate.proxy.generating.documents.common.ParticipantBase;


/**
 * <p>Java class for CorrectionRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CorrectionRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="OperatorCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ReceivedDocument" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Docflow}ReceivedDocumentInfo" minOccurs="0"/>
 *         &lt;element name="Receiver" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common}ParticipantBase" minOccurs="0"/>
 *         &lt;element name="RequestMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Sender" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common}ParticipantBase" minOccurs="0"/>
 *         &lt;element name="Signature" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Signer" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments}Signer" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CorrectionRequest", propOrder = {
    "operatorCode",
    "receivedDocument",
    "receiver",
    "requestMessage",
    "sender",
    "signature",
    "signer"
})
public class CorrectionRequest {

    @XmlElement(name = "OperatorCode", nillable = true)
    protected String operatorCode;
    @XmlElement(name = "ReceivedDocument", nillable = true)
    protected ReceivedDocumentInfo receivedDocument;
    @XmlElement(name = "Receiver", nillable = true)
    protected ParticipantBase receiver;
    @XmlElement(name = "RequestMessage", nillable = true)
    protected String requestMessage;
    @XmlElement(name = "Sender", nillable = true)
    protected ParticipantBase sender;
    @XmlElement(name = "Signature", nillable = true)
    protected String signature;
    @XmlElement(name = "Signer", nillable = true)
    protected Signer signer;

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
     * Gets the value of the receivedDocument property.
     * 
     * @return
     *     possible object is
     *     {@link ReceivedDocumentInfo }
     *     
     */
    public ReceivedDocumentInfo getReceivedDocument() {
        return receivedDocument;
    }

    /**
     * Sets the value of the receivedDocument property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReceivedDocumentInfo }
     *     
     */
    public void setReceivedDocument(ReceivedDocumentInfo value) {
        this.receivedDocument = value;
    }

    /**
     * Gets the value of the receiver property.
     * 
     * @return
     *     possible object is
     *     {@link ParticipantBase }
     *     
     */
    public ParticipantBase getReceiver() {
        return receiver;
    }

    /**
     * Sets the value of the receiver property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParticipantBase }
     *     
     */
    public void setReceiver(ParticipantBase value) {
        this.receiver = value;
    }

    /**
     * Gets the value of the requestMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestMessage() {
        return requestMessage;
    }

    /**
     * Sets the value of the requestMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestMessage(String value) {
        this.requestMessage = value;
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
     * Gets the value of the signature property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSignature() {
        return signature;
    }

    /**
     * Sets the value of the signature property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSignature(String value) {
        this.signature = value;
    }

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

}
