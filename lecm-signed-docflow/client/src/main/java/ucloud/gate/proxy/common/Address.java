
package ucloud.gate.proxy.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Address complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Address">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Area" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Corpus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ForeignAddressText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ForeignCountryCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="House" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Index" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="IsForeignAddress" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="Locality" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Quarter" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="RegionCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Street" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Town" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="UnstructedAddressText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Address", propOrder = {
    "area",
    "corpus",
    "foreignAddressText",
    "foreignCountryCode",
    "house",
    "index",
    "isForeignAddress",
    "locality",
    "quarter",
    "regionCode",
    "street",
    "town",
    "unstructedAddressText"
})
public class Address {

    @XmlElement(name = "Area", nillable = true)
    protected String area;
    @XmlElement(name = "Corpus", nillable = true)
    protected String corpus;
    @XmlElement(name = "ForeignAddressText", nillable = true)
    protected String foreignAddressText;
    @XmlElement(name = "ForeignCountryCode", nillable = true)
    protected String foreignCountryCode;
    @XmlElement(name = "House", nillable = true)
    protected String house;
    @XmlElement(name = "Index", nillable = true)
    protected String index;
    @XmlElement(name = "IsForeignAddress")
    protected Boolean isForeignAddress;
    @XmlElement(name = "Locality", nillable = true)
    protected String locality;
    @XmlElement(name = "Quarter", nillable = true)
    protected String quarter;
    @XmlElement(name = "RegionCode", nillable = true)
    protected String regionCode;
    @XmlElement(name = "Street", nillable = true)
    protected String street;
    @XmlElement(name = "Town", nillable = true)
    protected String town;
    @XmlElement(name = "UnstructedAddressText", nillable = true)
    protected String unstructedAddressText;

    /**
     * Gets the value of the area property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArea() {
        return area;
    }

    /**
     * Sets the value of the area property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArea(String value) {
        this.area = value;
    }

    /**
     * Gets the value of the corpus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCorpus() {
        return corpus;
    }

    /**
     * Sets the value of the corpus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCorpus(String value) {
        this.corpus = value;
    }

    /**
     * Gets the value of the foreignAddressText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getForeignAddressText() {
        return foreignAddressText;
    }

    /**
     * Sets the value of the foreignAddressText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setForeignAddressText(String value) {
        this.foreignAddressText = value;
    }

    /**
     * Gets the value of the foreignCountryCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getForeignCountryCode() {
        return foreignCountryCode;
    }

    /**
     * Sets the value of the foreignCountryCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setForeignCountryCode(String value) {
        this.foreignCountryCode = value;
    }

    /**
     * Gets the value of the house property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHouse() {
        return house;
    }

    /**
     * Sets the value of the house property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHouse(String value) {
        this.house = value;
    }

    /**
     * Gets the value of the index property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIndex() {
        return index;
    }

    /**
     * Sets the value of the index property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIndex(String value) {
        this.index = value;
    }

    /**
     * Gets the value of the isForeignAddress property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsForeignAddress() {
        return isForeignAddress;
    }

    /**
     * Sets the value of the isForeignAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsForeignAddress(Boolean value) {
        this.isForeignAddress = value;
    }

    /**
     * Gets the value of the locality property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocality() {
        return locality;
    }

    /**
     * Sets the value of the locality property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocality(String value) {
        this.locality = value;
    }

    /**
     * Gets the value of the quarter property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQuarter() {
        return quarter;
    }

    /**
     * Sets the value of the quarter property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQuarter(String value) {
        this.quarter = value;
    }

    /**
     * Gets the value of the regionCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegionCode() {
        return regionCode;
    }

    /**
     * Sets the value of the regionCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegionCode(String value) {
        this.regionCode = value;
    }

    /**
     * Gets the value of the street property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStreet() {
        return street;
    }

    /**
     * Sets the value of the street property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStreet(String value) {
        this.street = value;
    }

    /**
     * Gets the value of the town property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTown() {
        return town;
    }

    /**
     * Sets the value of the town property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTown(String value) {
        this.town = value;
    }

    /**
     * Gets the value of the unstructedAddressText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnstructedAddressText() {
        return unstructedAddressText;
    }

    /**
     * Sets the value of the unstructedAddressText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnstructedAddressText(String value) {
        this.unstructedAddressText = value;
    }

}
