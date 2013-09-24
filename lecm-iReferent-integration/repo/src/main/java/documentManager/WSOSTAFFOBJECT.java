
package documentManager;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for WSO_STAFFOBJECT complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="WSO_STAFFOBJECT">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:DefaultNamespace}WSOBJECT">
 *       &lt;sequence>
 *         &lt;element name="PARENTS" type="{urn:DefaultNamespace}WSO_COLLECTION"/>
 *         &lt;element name="ATTACHMENTS" type="{urn:DefaultNamespace}WSO_COLLECTION"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSO_STAFFOBJECT", propOrder = {
        "parents",
        "attachments"
})
@XmlSeeAlso({
        WSOMGROUP.class,
        WSOMPERSON.class
})
public class WSOSTAFFOBJECT
        extends WSOBJECT {

    @XmlElement(name = "PARENTS", required = true, nillable = true)
    protected WSOCOLLECTION parents;
    @XmlElement(name = "ATTACHMENTS", required = true, nillable = true)
    protected WSOCOLLECTION attachments;

    /**
     * Gets the value of the parents property.
     *
     * @return possible object is
     *         {@link WSOCOLLECTION }
     */
    public WSOCOLLECTION getPARENTS() {
        return parents;
    }

    /**
     * Sets the value of the parents property.
     *
     * @param value allowed object is
     *              {@link WSOCOLLECTION }
     */
    public void setPARENTS(WSOCOLLECTION value) {
        this.parents = value;
    }

    /**
     * Gets the value of the attachments property.
     *
     * @return possible object is
     *         {@link WSOCOLLECTION }
     */
    public WSOCOLLECTION getATTACHMENTS() {
        return attachments;
    }

    /**
     * Sets the value of the attachments property.
     *
     * @param value allowed object is
     *              {@link WSOCOLLECTION }
     */
    public void setATTACHMENTS(WSOCOLLECTION value) {
        this.attachments = value;
    }

}
