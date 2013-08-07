
package ucloud.gate.proxy.invoice.classes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Signer complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Signer">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SignerCertificate" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *         &lt;element name="SignerDetails" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses}SignerDetails" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Signer", propOrder = {
    "signerCertificate",
    "signerDetails"
})
public class Signer {

    @XmlElement(name = "SignerCertificate", nillable = true)
    protected byte[] signerCertificate;
    @XmlElement(name = "SignerDetails", nillable = true)
    protected SignerDetails signerDetails;

    /**
     * Gets the value of the signerCertificate property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getSignerCertificate() {
        return signerCertificate;
    }

    /**
     * Sets the value of the signerCertificate property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setSignerCertificate(byte[] value) {
        this.signerCertificate = ((byte[]) value);
    }

    /**
     * Gets the value of the signerDetails property.
     * 
     * @return
     *     possible object is
     *     {@link SignerDetails }
     *     
     */
    public SignerDetails getSignerDetails() {
        return signerDetails;
    }

    /**
     * Sets the value of the signerDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link SignerDetails }
     *     
     */
    public void setSignerDetails(SignerDetails value) {
        this.signerDetails = value;
    }

}
