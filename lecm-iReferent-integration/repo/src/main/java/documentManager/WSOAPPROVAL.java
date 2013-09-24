
package documentManager;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for WSOAPPROVAL complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="WSOAPPROVAL">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:DefaultNamespace}WSO_MAPPROVAL">
 *       &lt;sequence>
 *         &lt;element name="OBJECT" type="{urn:DefaultNamespace}WSOBJECT"/>
 *         &lt;element name="RESPONSIBLE" type="{urn:DefaultNamespace}WSO_MPERSON"/>
 *         &lt;element name="DEADLINEDATE" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="COMMENT" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSOAPPROVAL", propOrder = {
        "object",
        "responsible",
        "deadlinedate",
        "comment"
})
public class WSOAPPROVAL
        extends WSOMAPPROVAL {

    @XmlElement(name = "OBJECT", required = true, nillable = true)
    protected WSOBJECT object;
    @XmlElement(name = "RESPONSIBLE", required = true, nillable = true)
    protected WSOMPERSON responsible;
    @XmlElement(name = "DEADLINEDATE", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar deadlinedate;
    @XmlElement(name = "COMMENT", required = true, nillable = true)
    protected String comment;

    /**
     * Gets the value of the object property.
     *
     * @return possible object is
     *         {@link WSOBJECT }
     */
    public WSOBJECT getOBJECT() {
        return object;
    }

    /**
     * Sets the value of the object property.
     *
     * @param value allowed object is
     *              {@link WSOBJECT }
     */
    public void setOBJECT(WSOBJECT value) {
        this.object = value;
    }

    /**
     * Gets the value of the responsible property.
     *
     * @return possible object is
     *         {@link WSOMPERSON }
     */
    public WSOMPERSON getRESPONSIBLE() {
        return responsible;
    }

    /**
     * Sets the value of the responsible property.
     *
     * @param value allowed object is
     *              {@link WSOMPERSON }
     */
    public void setRESPONSIBLE(WSOMPERSON value) {
        this.responsible = value;
    }

    /**
     * Gets the value of the deadlinedate property.
     *
     * @return possible object is
     *         {@link XMLGregorianCalendar }
     */
    public XMLGregorianCalendar getDEADLINEDATE() {
        return deadlinedate;
    }

    /**
     * Sets the value of the deadlinedate property.
     *
     * @param value allowed object is
     *              {@link XMLGregorianCalendar }
     */
    public void setDEADLINEDATE(XMLGregorianCalendar value) {
        this.deadlinedate = value;
    }

    /**
     * Gets the value of the comment property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getCOMMENT() {
        return comment;
    }

    /**
     * Sets the value of the comment property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setCOMMENT(String value) {
        this.comment = value;
    }

}
