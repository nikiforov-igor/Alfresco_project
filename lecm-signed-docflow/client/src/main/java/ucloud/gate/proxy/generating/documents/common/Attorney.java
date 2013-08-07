
package ucloud.gate.proxy.generating.documents.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for Attorney complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Attorney">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Date" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="IssuerAdditionalInfo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="IssuerOrganizationName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="IssuerPerson" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common}Official" minOccurs="0"/>
 *         &lt;element name="Number" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="RecipientAdditionalInfo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="RecipientPerson" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common}Official" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Attorney", propOrder = {
    "date",
    "issuerAdditionalInfo",
    "issuerOrganizationName",
    "issuerPerson",
    "number",
    "recipientAdditionalInfo",
    "recipientPerson"
})
public class Attorney {

    @XmlElement(name = "Date", nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar date;
    @XmlElement(name = "IssuerAdditionalInfo", nillable = true)
    protected String issuerAdditionalInfo;
    @XmlElement(name = "IssuerOrganizationName", nillable = true)
    protected String issuerOrganizationName;
    @XmlElement(name = "IssuerPerson", nillable = true)
    protected Official issuerPerson;
    @XmlElement(name = "Number", nillable = true)
    protected String number;
    @XmlElement(name = "RecipientAdditionalInfo", nillable = true)
    protected String recipientAdditionalInfo;
    @XmlElement(name = "RecipientPerson", nillable = true)
    protected Official recipientPerson;

    /**
     * Gets the value of the date property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDate() {
        return date;
    }

    /**
     * Sets the value of the date property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDate(XMLGregorianCalendar value) {
        this.date = value;
    }

    /**
     * Gets the value of the issuerAdditionalInfo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIssuerAdditionalInfo() {
        return issuerAdditionalInfo;
    }

    /**
     * Sets the value of the issuerAdditionalInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIssuerAdditionalInfo(String value) {
        this.issuerAdditionalInfo = value;
    }

    /**
     * Gets the value of the issuerOrganizationName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIssuerOrganizationName() {
        return issuerOrganizationName;
    }

    /**
     * Sets the value of the issuerOrganizationName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIssuerOrganizationName(String value) {
        this.issuerOrganizationName = value;
    }

    /**
     * Gets the value of the issuerPerson property.
     * 
     * @return
     *     possible object is
     *     {@link Official }
     *     
     */
    public Official getIssuerPerson() {
        return issuerPerson;
    }

    /**
     * Sets the value of the issuerPerson property.
     * 
     * @param value
     *     allowed object is
     *     {@link Official }
     *     
     */
    public void setIssuerPerson(Official value) {
        this.issuerPerson = value;
    }

    /**
     * Gets the value of the number property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumber() {
        return number;
    }

    /**
     * Sets the value of the number property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumber(String value) {
        this.number = value;
    }

    /**
     * Gets the value of the recipientAdditionalInfo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRecipientAdditionalInfo() {
        return recipientAdditionalInfo;
    }

    /**
     * Sets the value of the recipientAdditionalInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRecipientAdditionalInfo(String value) {
        this.recipientAdditionalInfo = value;
    }

    /**
     * Gets the value of the recipientPerson property.
     * 
     * @return
     *     possible object is
     *     {@link Official }
     *     
     */
    public Official getRecipientPerson() {
        return recipientPerson;
    }

    /**
     * Sets the value of the recipientPerson property.
     * 
     * @param value
     *     allowed object is
     *     {@link Official }
     *     
     */
    public void setRecipientPerson(Official value) {
        this.recipientPerson = value;
    }

}
