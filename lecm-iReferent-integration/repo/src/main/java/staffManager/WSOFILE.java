
package staffManager;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSO_FILE complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSO_FILE">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:DefaultNamespace}WSO_MFILE">
 *       &lt;sequence>
 *         &lt;element name="BODY" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSO_FILE", propOrder = {
    "body"
})
public class WSOFILE
    extends WSOMFILE
{

    @XmlElement(name = "BODY", required = true, nillable = true)
    protected byte[] body;

    /**
     * Gets the value of the body property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getBODY() {
        return body;
    }

    /**
     * Sets the value of the body property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setBODY(byte[] value) {
        this.body = ((byte[]) value);
    }

}
