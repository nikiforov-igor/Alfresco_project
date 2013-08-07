
package ucloud.gate.proxy.registration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RegisterRequestForeignCert complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RegisterRequestForeignCert">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Email" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FullName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Inn" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Kpp" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LocationAddress" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration}AddressForRegistration" minOccurs="0"/>
 *         &lt;element name="Members" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration}ArrayOfMember" minOccurs="0"/>
 *         &lt;element name="MobliePhone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Phone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PostalAddress" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration}AddressForRegistration" minOccurs="0"/>
 *         &lt;element name="ShortName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RegisterRequestForeignCert", propOrder = {
    "email",
    "fullName",
    "inn",
    "kpp",
    "locationAddress",
    "members",
    "mobliePhone",
    "phone",
    "postalAddress",
    "shortName"
})
public class RegisterRequestForeignCert {

    @XmlElement(name = "Email", nillable = true)
    protected String email;
    @XmlElement(name = "FullName", nillable = true)
    protected String fullName;
    @XmlElement(name = "Inn", required = true, nillable = true)
    protected String inn;
    @XmlElement(name = "Kpp", nillable = true)
    protected String kpp;
    @XmlElement(name = "LocationAddress", nillable = true)
    protected AddressForRegistration locationAddress;
    @XmlElement(name = "Members", nillable = true)
    protected ArrayOfMember members;
    @XmlElement(name = "MobliePhone", nillable = true)
    protected String mobliePhone;
    @XmlElement(name = "Phone", nillable = true)
    protected String phone;
    @XmlElement(name = "PostalAddress", nillable = true)
    protected AddressForRegistration postalAddress;
    @XmlElement(name = "ShortName", required = true, nillable = true)
    protected String shortName;

    /**
     * Gets the value of the email property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmail() {
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
    public void setEmail(String value) {
        this.email = value;
    }

    /**
     * Gets the value of the fullName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the value of the fullName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFullName(String value) {
        this.fullName = value;
    }

    /**
     * Gets the value of the inn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInn() {
        return inn;
    }

    /**
     * Sets the value of the inn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInn(String value) {
        this.inn = value;
    }

    /**
     * Gets the value of the kpp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKpp() {
        return kpp;
    }

    /**
     * Sets the value of the kpp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKpp(String value) {
        this.kpp = value;
    }

    /**
     * Gets the value of the locationAddress property.
     * 
     * @return
     *     possible object is
     *     {@link AddressForRegistration }
     *     
     */
    public AddressForRegistration getLocationAddress() {
        return locationAddress;
    }

    /**
     * Sets the value of the locationAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link AddressForRegistration }
     *     
     */
    public void setLocationAddress(AddressForRegistration value) {
        this.locationAddress = value;
    }

    /**
     * Gets the value of the members property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfMember }
     *     
     */
    public ArrayOfMember getMembers() {
        return members;
    }

    /**
     * Sets the value of the members property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfMember }
     *     
     */
    public void setMembers(ArrayOfMember value) {
        this.members = value;
    }

    /**
     * Gets the value of the mobliePhone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMobliePhone() {
        return mobliePhone;
    }

    /**
     * Sets the value of the mobliePhone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMobliePhone(String value) {
        this.mobliePhone = value;
    }

    /**
     * Gets the value of the phone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhone() {
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
    public void setPhone(String value) {
        this.phone = value;
    }

    /**
     * Gets the value of the postalAddress property.
     * 
     * @return
     *     possible object is
     *     {@link AddressForRegistration }
     *     
     */
    public AddressForRegistration getPostalAddress() {
        return postalAddress;
    }

    /**
     * Sets the value of the postalAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link AddressForRegistration }
     *     
     */
    public void setPostalAddress(AddressForRegistration value) {
        this.postalAddress = value;
    }

    /**
     * Gets the value of the shortName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * Sets the value of the shortName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShortName(String value) {
        this.shortName = value;
    }

}
