
package ucloud.gate.proxy.generating.documents.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ParticipantIndividual complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ParticipantIndividual">
 *   &lt;complexContent>
 *     &lt;extension base="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common}ParticipantBase">
 *       &lt;sequence>
 *         &lt;element name="Fio" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common}FioType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ParticipantIndividual", propOrder = {
    "fio"
})
public class ParticipantIndividual
    extends ParticipantBase
{

    @XmlElement(name = "Fio", nillable = true)
    protected FioType fio;

    /**
     * Gets the value of the fio property.
     * 
     * @return
     *     possible object is
     *     {@link FioType }
     *     
     */
    public FioType getFio() {
        return fio;
    }

    /**
     * Sets the value of the fio property.
     * 
     * @param value
     *     allowed object is
     *     {@link FioType }
     *     
     */
    public void setFio(FioType value) {
        this.fio = value;
    }

}
