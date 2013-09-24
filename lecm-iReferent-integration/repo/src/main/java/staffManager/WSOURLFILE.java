
package staffManager;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSO_URLFILE complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSO_URLFILE">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:DefaultNamespace}WSOBJECT">
 *       &lt;sequence>
 *         &lt;element name="OBJECT" type="{urn:DefaultNamespace}WSOBJECT"/>
 *         &lt;element name="OBJECTPROPERTYNAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="NAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CREATIONDATE" type="{http://www.w3.org/2001/XMLSchema}anySimpleType"/>
 *         &lt;element name="VERSION" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="AUTHOR" type="{urn:DefaultNamespace}WSO_MPERSON"/>
 *         &lt;element name="REFERENCE" type="{urn:DefaultNamespace}WSO_URL"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSO_URLFILE", propOrder = {
    "object",
    "objectpropertyname",
    "name",
    "creationdate",
    "version",
    "author",
    "reference"
})
public class WSOURLFILE
    extends WSOBJECT
{

    @XmlElement(name = "OBJECT", required = true, nillable = true)
    protected WSOBJECT object;
    @XmlElement(name = "OBJECTPROPERTYNAME", required = true, nillable = true)
    protected String objectpropertyname;
    @XmlElement(name = "NAME", required = true, nillable = true)
    protected String name;
    @XmlElement(name = "CREATIONDATE", required = true, nillable = true)
    @XmlSchemaType(name = "anySimpleType")
    protected Object creationdate;
    @XmlElement(name = "VERSION", required = true, type = Integer.class, nillable = true)
    protected Integer version;
    @XmlElement(name = "AUTHOR", required = true, nillable = true)
    protected WSOMPERSON author;
    @XmlElement(name = "REFERENCE", required = true, nillable = true)
    protected WSOURL reference;

    /**
     * Gets the value of the object property.
     * 
     * @return
     *     possible object is
     *     {@link WSOBJECT }
     *     
     */
    public WSOBJECT getOBJECT() {
        return object;
    }

    /**
     * Sets the value of the object property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSOBJECT }
     *     
     */
    public void setOBJECT(WSOBJECT value) {
        this.object = value;
    }

    /**
     * Gets the value of the objectpropertyname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOBJECTPROPERTYNAME() {
        return objectpropertyname;
    }

    /**
     * Sets the value of the objectpropertyname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOBJECTPROPERTYNAME(String value) {
        this.objectpropertyname = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNAME() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNAME(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the creationdate property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getCREATIONDATE() {
        return creationdate;
    }

    /**
     * Sets the value of the creationdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setCREATIONDATE(Object value) {
        this.creationdate = value;
    }

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getVERSION() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setVERSION(Integer value) {
        this.version = value;
    }

    /**
     * Gets the value of the author property.
     * 
     * @return
     *     possible object is
     *     {@link WSOMPERSON }
     *     
     */
    public WSOMPERSON getAUTHOR() {
        return author;
    }

    /**
     * Sets the value of the author property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSOMPERSON }
     *     
     */
    public void setAUTHOR(WSOMPERSON value) {
        this.author = value;
    }

    /**
     * Gets the value of the reference property.
     * 
     * @return
     *     possible object is
     *     {@link WSOURL }
     *     
     */
    public WSOURL getREFERENCE() {
        return reference;
    }

    /**
     * Sets the value of the reference property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSOURL }
     *     
     */
    public void setREFERENCE(WSOURL value) {
        this.reference = value;
    }

}
