
package documentManager;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSO_DOCUMENT_NPA complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="WSO_DOCUMENT_NPA">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:DefaultNamespace}WSO_MDOCUMENT_NPA">
 *       &lt;sequence>
 *         &lt;element name="COMMONPROPS" type="{urn:DefaultNamespace}WSO_DOCUMENTCOMMONPROPERTIES"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSO_DOCUMENT_NPA", propOrder = {
        "commonprops"
})
public class WSODOCUMENTNPA
        extends WSOMDOCUMENTNPA {

    @XmlElement(name = "COMMONPROPS", required = true)
    protected WSODOCUMENTCOMMONPROPERTIES commonprops;

    /**
     * Gets the value of the commonprops property.
     *
     * @return possible object is
     *         {@link WSODOCUMENTCOMMONPROPERTIES }
     */
    public WSODOCUMENTCOMMONPROPERTIES getCOMMONPROPS() {
        return commonprops;
    }

    /**
     * Sets the value of the commonprops property.
     *
     * @param value allowed object is
     *              {@link WSODOCUMENTCOMMONPROPERTIES }
     */
    public void setCOMMONPROPS(WSODOCUMENTCOMMONPROPERTIES value) {
        this.commonprops = value;
    }

}
