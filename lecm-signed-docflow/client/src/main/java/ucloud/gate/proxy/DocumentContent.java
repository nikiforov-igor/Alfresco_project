
package ucloud.gate.proxy;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import com.microsoft.schemas.serialization.arrays.ArrayOfbase64Binary;


/**
 * <p>Java class for DocumentContent complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DocumentContent">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Content" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *         &lt;element name="Signatures" type="{http://schemas.microsoft.com/2003/10/Serialization/Arrays}ArrayOfbase64Binary" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DocumentContent", propOrder = {
    "content",
    "signatures"
})
public class DocumentContent {

    @XmlElement(name = "Content", nillable = true)
    protected byte[] content;
    @XmlElement(name = "Signatures", nillable = true)
    protected ArrayOfbase64Binary signatures;

    /**
     * Gets the value of the content property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * Sets the value of the content property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setContent(byte[] value) {
        this.content = ((byte[]) value);
    }

    /**
     * Gets the value of the signatures property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfbase64Binary }
     *     
     */
    public ArrayOfbase64Binary getSignatures() {
        return signatures;
    }

    /**
     * Sets the value of the signatures property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfbase64Binary }
     *     
     */
    public void setSignatures(ArrayOfbase64Binary value) {
        this.signatures = value;
    }

}
