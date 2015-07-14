
package ru.it.lecm.mobile.objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSO_PERSON complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSO_PERSON">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:DefaultNamespace}WSO_MPERSON">
 *       &lt;sequence>
 *         &lt;element name="EMAIL" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="PHONE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="FAX" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="POST" type="{urn:DefaultNamespace}WSO_COLLECTION"/>
 *         &lt;element name="PROXIES" type="{urn:DefaultNamespace}WSO_COLLECTION"/>
 *         &lt;element name="TRUSTERS" type="{urn:DefaultNamespace}WSO_COLLECTION"/>
 *         &lt;element name="FIRSTNAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="MIDDLENAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="LASTNAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="PHOTO" type="{urn:DefaultNamespace}WSO_FILE"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSO_PERSON", propOrder = {
    "email",
    "phone",
    "fax",
    "post",
    "proxies",
    "trusters",
    "firstname",
    "middlename",
    "lastname",
    "photo"
})
public class WSOPERSON
    extends WSOMPERSON
{

    @XmlElement(name = "EMAIL", required = true, nillable = true)
    protected String email;
    @XmlElement(name = "PHONE", required = true, nillable = true)
    protected String phone;
    @XmlElement(name = "FAX", required = true, nillable = true)
    protected String fax;
    @XmlElement(name = "POST", required = true, nillable = true)
    protected WSOCOLLECTION post;
    @XmlElement(name = "PROXIES", required = true, nillable = true)
    protected WSOCOLLECTION proxies;
    @XmlElement(name = "TRUSTERS", required = true, nillable = true)
    protected WSOCOLLECTION trusters;
    @XmlElement(name = "FIRSTNAME", required = true, nillable = true)
    protected String firstname;
    @XmlElement(name = "MIDDLENAME", required = true, nillable = true)
    protected String middlename;
    @XmlElement(name = "LASTNAME", required = true, nillable = true)
    protected String lastname;
    @XmlElement(name = "PHOTO", required = true, nillable = true)
    protected WSOFILE photo;

    /**
     * Gets the value of the email property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEMAIL() {
        return email;
    }

    /**
     * Sets the value of the email property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEMAIL(String value) {
        this.email = value;
    }

    /**
     * Gets the value of the phone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPHONE() {
        return phone;
    }

    /**
     * Sets the value of the phone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPHONE(String value) {
        this.phone = value;
    }

    /**
     * Gets the value of the fax property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFAX() {
        return fax;
    }

    /**
     * Sets the value of the fax property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFAX(String value) {
        this.fax = value;
    }

    /**
     * Gets the value of the post property.
     * 
     * @return
     *     possible object is
     *     {@link WSOCOLLECTION }
     *     
     */
    public WSOCOLLECTION getPOST() {
        return post;
    }

    /**
     * Sets the value of the post property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSOCOLLECTION }
     *     
     */
    public void setPOST(WSOCOLLECTION value) {
        this.post = value;
    }

    /**
     * Gets the value of the proxies property.
     * 
     * @return
     *     possible object is
     *     {@link WSOCOLLECTION }
     *     
     */
    public WSOCOLLECTION getPROXIES() {
        return proxies;
    }

    /**
     * Sets the value of the proxies property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSOCOLLECTION }
     *     
     */
    public void setPROXIES(WSOCOLLECTION value) {
        this.proxies = value;
    }

    /**
     * Gets the value of the trusters property.
     * 
     * @return
     *     possible object is
     *     {@link WSOCOLLECTION }
     *     
     */
    public WSOCOLLECTION getTRUSTERS() {
        return trusters;
    }

    /**
     * Sets the value of the trusters property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSOCOLLECTION }
     *     
     */
    public void setTRUSTERS(WSOCOLLECTION value) {
        this.trusters = value;
    }

    /**
     * Gets the value of the firstname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFIRSTNAME() {
        return firstname;
    }

    /**
     * Sets the value of the firstname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFIRSTNAME(String value) {
        this.firstname = value;
    }

    /**
     * Gets the value of the middlename property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMIDDLENAME() {
        return middlename;
    }

    /**
     * Sets the value of the middlename property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMIDDLENAME(String value) {
        this.middlename = value;
    }

    /**
     * Gets the value of the lastname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLASTNAME() {
        return lastname;
    }

    /**
     * Sets the value of the lastname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLASTNAME(String value) {
        this.lastname = value;
    }

    /**
     * Gets the value of the photo property.
     * 
     * @return
     *     possible object is
     *     {@link WSOFILE }
     *     
     */
    public WSOFILE getPHOTO() {
        return photo;
    }

    /**
     * Sets the value of the photo property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSOFILE }
     *     
     */
    public void setPHOTO(WSOFILE value) {
        this.photo = value;
    }

}
