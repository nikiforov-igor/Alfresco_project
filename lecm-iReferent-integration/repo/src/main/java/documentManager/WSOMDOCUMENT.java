
package documentManager;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for WSO_MDOCUMENT complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="WSO_MDOCUMENT">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:DefaultNamespace}WSOBJECT">
 *       &lt;sequence>
 *         &lt;element name="REGNUM" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="REGDATE" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="SUBJECT" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="STATUSNAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSO_MDOCUMENT", propOrder = {
        "regnum",
        "regdate",
        "subject",
        "statusname"
})
@XmlSeeAlso({
        WSOMDOCUMENTNPA.class,
        WSOMDOCUMENTINT.class,
        WSOMDOCUMENTOUT.class,
        WSOMDOCUMENTIN.class,
        WSOMDOCUMENTORD.class
})
public class WSOMDOCUMENT
        extends WSOBJECT {

    @XmlElement(name = "REGNUM", required = true, nillable = true)
    protected String regnum;
    @XmlElement(name = "REGDATE", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar regdate;
    @XmlElement(name = "SUBJECT", required = true, nillable = true)
    protected String subject;
    @XmlElement(name = "STATUSNAME", required = true, nillable = true)
    protected String statusname;

    /**
     * Gets the value of the regnum property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getREGNUM() {
        return regnum;
    }

    /**
     * Sets the value of the regnum property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setREGNUM(String value) {
        this.regnum = value;
    }

    /**
     * Gets the value of the regdate property.
     *
     * @return possible object is
     *         {@link XMLGregorianCalendar }
     */
    public XMLGregorianCalendar getREGDATE() {
        return regdate;
    }

    /**
     * Sets the value of the regdate property.
     *
     * @param value allowed object is
     *              {@link XMLGregorianCalendar }
     */
    public void setREGDATE(XMLGregorianCalendar value) {
        this.regdate = value;
    }

    /**
     * Gets the value of the subject property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getSUBJECT() {
        return subject;
    }

    /**
     * Sets the value of the subject property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSUBJECT(String value) {
        this.subject = value;
    }

    /**
     * Gets the value of the statusname property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getSTATUSNAME() {
        return statusname;
    }

    /**
     * Sets the value of the statusname property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSTATUSNAME(String value) {
        this.statusname = value;
    }

}
