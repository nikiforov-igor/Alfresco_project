
package ucloud.gate.proxy.generating.documents.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ShipmentParticipant complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ShipmentParticipant">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="IsSameAsParticipant" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ParticipantWithAdress" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common}ParticipantWithAddress" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ShipmentParticipant", propOrder = {
    "isSameAsParticipant",
    "participantWithAdress"
})
public class ShipmentParticipant {

    @XmlElement(name = "IsSameAsParticipant")
    protected Boolean isSameAsParticipant;
    @XmlElement(name = "ParticipantWithAdress", nillable = true)
    protected ParticipantWithAddress participantWithAdress;

    /**
     * Gets the value of the isSameAsParticipant property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsSameAsParticipant() {
        return isSameAsParticipant;
    }

    /**
     * Sets the value of the isSameAsParticipant property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsSameAsParticipant(Boolean value) {
        this.isSameAsParticipant = value;
    }

    /**
     * Gets the value of the participantWithAdress property.
     * 
     * @return
     *     possible object is
     *     {@link ParticipantWithAddress }
     *     
     */
    public ParticipantWithAddress getParticipantWithAdress() {
        return participantWithAdress;
    }

    /**
     * Sets the value of the participantWithAdress property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParticipantWithAddress }
     *     
     */
    public void setParticipantWithAdress(ParticipantWithAddress value) {
        this.participantWithAdress = value;
    }

}
