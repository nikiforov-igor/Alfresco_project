
package documentManager;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for WSO_MGROUP complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="WSO_MGROUP">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:DefaultNamespace}WSO_STAFFOBJECT">
 *       &lt;sequence>
 *         &lt;element name="CHILDS" type="{urn:DefaultNamespace}WSO_COLLECTION"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSO_MGROUP", propOrder = {
        "childs"
})
@XmlSeeAlso({
        WSOGROUP.class
})
public class WSOMGROUP
        extends WSOSTAFFOBJECT {

    @XmlElement(name = "CHILDS", required = true, nillable = true)
    protected WSOCOLLECTION childs;

    /**
     * Gets the value of the childs property.
     *
     * @return possible object is
     *         {@link WSOCOLLECTION }
     */
    public WSOCOLLECTION getCHILDS() {
        return childs;
    }

    /**
     * Sets the value of the childs property.
     *
     * @param value allowed object is
     *              {@link WSOCOLLECTION }
     */
    public void setCHILDS(WSOCOLLECTION value) {
        this.childs = value;
    }

}
