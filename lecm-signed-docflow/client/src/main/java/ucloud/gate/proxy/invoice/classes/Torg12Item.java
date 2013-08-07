
package ucloud.gate.proxy.invoice.classes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Torg12Item complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Torg12Item">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AdditionalInfo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Feature" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="GrossQuantity" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="NomenclatureArticle" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ParcelCapacity" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ParcelType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ParcelsQuantity" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Price" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Quantity" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Sort" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Subtotal" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SubtotalWithVatExcluded" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TaxRate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="UnitCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="UnitName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Vat" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Torg12Item", propOrder = {
    "additionalInfo",
    "code",
    "feature",
    "grossQuantity",
    "name",
    "nomenclatureArticle",
    "parcelCapacity",
    "parcelType",
    "parcelsQuantity",
    "price",
    "quantity",
    "sort",
    "subtotal",
    "subtotalWithVatExcluded",
    "taxRate",
    "unitCode",
    "unitName",
    "vat"
})
public class Torg12Item {

    @XmlElement(name = "AdditionalInfo", nillable = true)
    protected String additionalInfo;
    @XmlElement(name = "Code", nillable = true)
    protected String code;
    @XmlElement(name = "Feature", nillable = true)
    protected String feature;
    @XmlElement(name = "GrossQuantity", nillable = true)
    protected String grossQuantity;
    @XmlElement(name = "Name", nillable = true)
    protected String name;
    @XmlElement(name = "NomenclatureArticle", nillable = true)
    protected String nomenclatureArticle;
    @XmlElement(name = "ParcelCapacity", nillable = true)
    protected String parcelCapacity;
    @XmlElement(name = "ParcelType", nillable = true)
    protected String parcelType;
    @XmlElement(name = "ParcelsQuantity", nillable = true)
    protected String parcelsQuantity;
    @XmlElement(name = "Price", nillable = true)
    protected String price;
    @XmlElement(name = "Quantity", nillable = true)
    protected String quantity;
    @XmlElement(name = "Sort", nillable = true)
    protected String sort;
    @XmlElement(name = "Subtotal", nillable = true)
    protected String subtotal;
    @XmlElement(name = "SubtotalWithVatExcluded", nillable = true)
    protected String subtotalWithVatExcluded;
    @XmlElement(name = "TaxRate", nillable = true)
    protected String taxRate;
    @XmlElement(name = "UnitCode", nillable = true)
    protected String unitCode;
    @XmlElement(name = "UnitName", nillable = true)
    protected String unitName;
    @XmlElement(name = "Vat", nillable = true)
    protected String vat;

    /**
     * Gets the value of the additionalInfo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdditionalInfo() {
        return additionalInfo;
    }

    /**
     * Sets the value of the additionalInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdditionalInfo(String value) {
        this.additionalInfo = value;
    }

    /**
     * Gets the value of the code property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCode(String value) {
        this.code = value;
    }

    /**
     * Gets the value of the feature property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFeature() {
        return feature;
    }

    /**
     * Sets the value of the feature property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFeature(String value) {
        this.feature = value;
    }

    /**
     * Gets the value of the grossQuantity property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGrossQuantity() {
        return grossQuantity;
    }

    /**
     * Sets the value of the grossQuantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGrossQuantity(String value) {
        this.grossQuantity = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
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
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the nomenclatureArticle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNomenclatureArticle() {
        return nomenclatureArticle;
    }

    /**
     * Sets the value of the nomenclatureArticle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNomenclatureArticle(String value) {
        this.nomenclatureArticle = value;
    }

    /**
     * Gets the value of the parcelCapacity property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParcelCapacity() {
        return parcelCapacity;
    }

    /**
     * Sets the value of the parcelCapacity property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParcelCapacity(String value) {
        this.parcelCapacity = value;
    }

    /**
     * Gets the value of the parcelType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParcelType() {
        return parcelType;
    }

    /**
     * Sets the value of the parcelType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParcelType(String value) {
        this.parcelType = value;
    }

    /**
     * Gets the value of the parcelsQuantity property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParcelsQuantity() {
        return parcelsQuantity;
    }

    /**
     * Sets the value of the parcelsQuantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParcelsQuantity(String value) {
        this.parcelsQuantity = value;
    }

    /**
     * Gets the value of the price property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrice() {
        return price;
    }

    /**
     * Sets the value of the price property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrice(String value) {
        this.price = value;
    }

    /**
     * Gets the value of the quantity property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQuantity() {
        return quantity;
    }

    /**
     * Sets the value of the quantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQuantity(String value) {
        this.quantity = value;
    }

    /**
     * Gets the value of the sort property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSort() {
        return sort;
    }

    /**
     * Sets the value of the sort property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSort(String value) {
        this.sort = value;
    }

    /**
     * Gets the value of the subtotal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubtotal() {
        return subtotal;
    }

    /**
     * Sets the value of the subtotal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubtotal(String value) {
        this.subtotal = value;
    }

    /**
     * Gets the value of the subtotalWithVatExcluded property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubtotalWithVatExcluded() {
        return subtotalWithVatExcluded;
    }

    /**
     * Sets the value of the subtotalWithVatExcluded property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubtotalWithVatExcluded(String value) {
        this.subtotalWithVatExcluded = value;
    }

    /**
     * Gets the value of the taxRate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTaxRate() {
        return taxRate;
    }

    /**
     * Sets the value of the taxRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTaxRate(String value) {
        this.taxRate = value;
    }

    /**
     * Gets the value of the unitCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnitCode() {
        return unitCode;
    }

    /**
     * Sets the value of the unitCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnitCode(String value) {
        this.unitCode = value;
    }

    /**
     * Gets the value of the unitName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnitName() {
        return unitName;
    }

    /**
     * Sets the value of the unitName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnitName(String value) {
        this.unitName = value;
    }

    /**
     * Gets the value of the vat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVat() {
        return vat;
    }

    /**
     * Sets the value of the vat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVat(String value) {
        this.vat = value;
    }

}
