
package ucloud.gate.proxy.generating.documents.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import ucloud.gate.proxy.common.Address;


/**
 * <p>Java class for ParticipantWithAddress complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ParticipantWithAddress">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Address" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Common}Address" minOccurs="0"/>
 *         &lt;element name="Participant" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common}ParticipantBase" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ParticipantWithAddress", propOrder = {
    "address",
    "participant"
})
public class ParticipantWithAddress {

    @XmlElement(name = "Address", nillable = true)
    protected Address address;
    @XmlElement(name = "Participant", nillable = true)
    protected ParticipantBase participant;

    /**
     * Gets the value of the address property.
     * 
     * @return
     *     possible object is
     *     {@link Address }
     *     
     */
    public Address getAddress() {
        return address;
    }

    /**
     * Sets the value of the address property.
     * 
     * @param value
     *     allowed object is
     *     {@link Address }
     *     
     */
    public void setAddress(Address value) {
        this.address = value;
    }

    /**
     * Gets the value of the participant property.
     * 
     * @return
     *     possible object is
     *     {@link ParticipantBase }
     *     
     */
    public ParticipantBase getParticipant() {
        return participant;
    }

    /**
     * Sets the value of the participant property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParticipantBase }
     *     
     */
    public void setParticipant(ParticipantBase value) {
        this.participant = value;
    }

}
