
package ucloud.gate.proxy.docflow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import ucloud.gate.proxy.DocflowInfoBase;
import ucloud.gate.proxy.ENonformalizedDocumentStatus;


/**
 * <p>Java class for NonformalizedDocflowInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NonformalizedDocflowInfo">
 *   &lt;complexContent>
 *     &lt;extension base="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy}DocflowInfoBase">
 *       &lt;sequence>
 *         &lt;element name="NonformalizedDocumentStatus" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy}ENonformalizedDocumentStatus" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NonformalizedDocflowInfo", propOrder = {
    "nonformalizedDocumentStatus"
})
public class NonformalizedDocflowInfo
    extends DocflowInfoBase
{

    @XmlElement(name = "NonformalizedDocumentStatus")
    protected ENonformalizedDocumentStatus nonformalizedDocumentStatus;

    /**
     * Gets the value of the nonformalizedDocumentStatus property.
     * 
     * @return
     *     possible object is
     *     {@link ENonformalizedDocumentStatus }
     *     
     */
    public ENonformalizedDocumentStatus getNonformalizedDocumentStatus() {
        return nonformalizedDocumentStatus;
    }

    /**
     * Sets the value of the nonformalizedDocumentStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link ENonformalizedDocumentStatus }
     *     
     */
    public void setNonformalizedDocumentStatus(ENonformalizedDocumentStatus value) {
        this.nonformalizedDocumentStatus = value;
    }

}
