
package ucloud.gate.proxy.generating.documents.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ParticipantCorporateWithOkopf complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ParticipantCorporateWithOkopf">
 *   &lt;complexContent>
 *     &lt;extension base="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common}ParticipantCorporate">
 *       &lt;sequence>
 *         &lt;element name="Okopf" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ParticipantCorporateWithOkopf", propOrder = {
    "okopf"
})
public class ParticipantCorporateWithOkopf
    extends ParticipantCorporate
{

    @XmlElement(name = "Okopf", nillable = true)
    protected String okopf;

    /**
     * Gets the value of the okopf property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOkopf() {
        return okopf;
    }

    /**
     * Sets the value of the okopf property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOkopf(String value) {
        this.okopf = value;
    }

}
