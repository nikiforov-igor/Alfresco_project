
package documentManager;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for WSO_MFILE complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="WSO_MFILE">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:DefaultNamespace}WSOBJECT">
 *       &lt;sequence>
 *         &lt;element name="NAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="DATE" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="VERSION" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="USER" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="FIELDNAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSO_MFILE", propOrder = {
        "name",
        "date",
        "version",
        "user",
        "fieldname"
})
@XmlSeeAlso({
        WSOFILE.class
})
public class WSOMFILE
        extends WSOBJECT {

    @XmlElement(name = "NAME", required = true, nillable = true)
    protected String name;
    @XmlElement(name = "DATE", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar date;
    @XmlElement(name = "VERSION", required = true, type = Integer.class, nillable = true)
    protected Integer version;
    @XmlElement(name = "USER", required = true, nillable = true)
    protected String user;
    @XmlElement(name = "FIELDNAME", required = true, nillable = true)
    protected String fieldname;

    /**
     * Gets the value of the name property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getNAME() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setNAME(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the date property.
     *
     * @return possible object is
     *         {@link XMLGregorianCalendar }
     */
    public XMLGregorianCalendar getDATE() {
        return date;
    }

    /**
     * Sets the value of the date property.
     *
     * @param value allowed object is
     *              {@link XMLGregorianCalendar }
     */
    public void setDATE(XMLGregorianCalendar value) {
        this.date = value;
    }

    /**
     * Gets the value of the version property.
     *
     * @return possible object is
     *         {@link Integer }
     */
    public Integer getVERSION() {
        return version;
    }

    /**
     * Sets the value of the version property.
     *
     * @param value allowed object is
     *              {@link Integer }
     */
    public void setVERSION(Integer value) {
        this.version = value;
    }

    /**
     * Gets the value of the user property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getUSER() {
        return user;
    }

    /**
     * Sets the value of the user property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUSER(String value) {
        this.user = value;
    }

    /**
     * Gets the value of the fieldname property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getFIELDNAME() {
        return fieldname;
    }

    /**
     * Sets the value of the fieldname property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setFIELDNAME(String value) {
        this.fieldname = value;
    }

}
