
package ucloud.gate.proxy.generating.documents.invoice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import com.microsoft.schemas.serialization.arrays.ArrayOfstring;
import ucloud.gate.proxy.common.ETaxRate;


/**
 * <p>Java class for InvoiceProductGen complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InvoiceProductGen">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AmountNoVat" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AmountVat" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AmountWithVat" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CountryCodes" type="{http://schemas.microsoft.com/2003/10/Serialization/Arrays}ArrayOfstring" minOccurs="0"/>
 *         &lt;element name="CustomsDeclarationNumbers" type="{http://schemas.microsoft.com/2003/10/Serialization/Arrays}ArrayOfstring" minOccurs="0"/>
 *         &lt;element name="ExciseAmount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Infrormation" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MeasureUnit" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Price" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Quantity" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="RowNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TaxRate" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Common}ETaxRate" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InvoiceProductGen", propOrder = {
    "amountNoVat",
    "amountVat",
    "amountWithVat",
    "countryCodes",
    "customsDeclarationNumbers",
    "exciseAmount",
    "infrormation",
    "measureUnit",
    "name",
    "price",
    "quantity",
    "rowNumber",
    "taxRate"
})
public class InvoiceProductGen {

    @XmlElement(name = "AmountNoVat", nillable = true)
    protected String amountNoVat;
    @XmlElement(name = "AmountVat", nillable = true)
    protected String amountVat;
    @XmlElement(name = "AmountWithVat", nillable = true)
    protected String amountWithVat;
    @XmlElement(name = "CountryCodes", nillable = true)
    protected ArrayOfstring countryCodes;
    @XmlElement(name = "CustomsDeclarationNumbers", nillable = true)
    protected ArrayOfstring customsDeclarationNumbers;
    @XmlElement(name = "ExciseAmount", nillable = true)
    protected String exciseAmount;
    @XmlElement(name = "Infrormation", nillable = true)
    protected String infrormation;
    @XmlElement(name = "MeasureUnit", nillable = true)
    protected String measureUnit;
    @XmlElement(name = "Name", nillable = true)
    protected String name;
    @XmlElement(name = "Price", nillable = true)
    protected String price;
    @XmlElement(name = "Quantity", nillable = true)
    protected String quantity;
    @XmlElement(name = "RowNumber", nillable = true)
    protected String rowNumber;
    @XmlElement(name = "TaxRate")
    protected ETaxRate taxRate;

    /**
     * Gets the value of the amountNoVat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAmountNoVat() {
        return amountNoVat;
    }

    /**
     * Sets the value of the amountNoVat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAmountNoVat(String value) {
        this.amountNoVat = value;
    }

    /**
     * Gets the value of the amountVat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAmountVat() {
        return amountVat;
    }

    /**
     * Sets the value of the amountVat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAmountVat(String value) {
        this.amountVat = value;
    }

    /**
     * Gets the value of the amountWithVat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAmountWithVat() {
        return amountWithVat;
    }

    /**
     * Sets the value of the amountWithVat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAmountWithVat(String value) {
        this.amountWithVat = value;
    }

    /**
     * Gets the value of the countryCodes property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfstring }
     *     
     */
    public ArrayOfstring getCountryCodes() {
        return countryCodes;
    }

    /**
     * Sets the value of the countryCodes property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfstring }
     *     
     */
    public void setCountryCodes(ArrayOfstring value) {
        this.countryCodes = value;
    }

    /**
     * Gets the value of the customsDeclarationNumbers property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfstring }
     *     
     */
    public ArrayOfstring getCustomsDeclarationNumbers() {
        return customsDeclarationNumbers;
    }

    /**
     * Sets the value of the customsDeclarationNumbers property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfstring }
     *     
     */
    public void setCustomsDeclarationNumbers(ArrayOfstring value) {
        this.customsDeclarationNumbers = value;
    }

    /**
     * Gets the value of the exciseAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExciseAmount() {
        return exciseAmount;
    }

    /**
     * Sets the value of the exciseAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExciseAmount(String value) {
        this.exciseAmount = value;
    }

    /**
     * Gets the value of the infrormation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInfrormation() {
        return infrormation;
    }

    /**
     * Sets the value of the infrormation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInfrormation(String value) {
        this.infrormation = value;
    }

    /**
     * Gets the value of the measureUnit property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMeasureUnit() {
        return measureUnit;
    }

    /**
     * Sets the value of the measureUnit property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMeasureUnit(String value) {
        this.measureUnit = value;
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
     * Gets the value of the rowNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRowNumber() {
        return rowNumber;
    }

    /**
     * Sets the value of the rowNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRowNumber(String value) {
        this.rowNumber = value;
    }

    /**
     * Gets the value of the taxRate property.
     * 
     * @return
     *     possible object is
     *     {@link ETaxRate }
     *     
     */
    public ETaxRate getTaxRate() {
        return taxRate;
    }

    /**
     * Sets the value of the taxRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link ETaxRate }
     *     
     */
    public void setTaxRate(ETaxRate value) {
        this.taxRate = value;
    }

}
