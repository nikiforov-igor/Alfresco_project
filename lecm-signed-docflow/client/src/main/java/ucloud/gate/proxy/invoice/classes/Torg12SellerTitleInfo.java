
package ucloud.gate.proxy.invoice.classes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import ucloud.gate.proxy.CompanyInfo;
import ucloud.gate.proxy.invoice.OrganizationInfo;


/**
 * <p>Java class for Torg12SellerTitleInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Torg12SellerTitleInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AdditionalInfo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AttachmentSheetsQuantity" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BuyerDocflowParticipant" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy}CompanyInfo" minOccurs="0"/>
 *         &lt;element name="ChiefAccountant" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses}Official" minOccurs="0"/>
 *         &lt;element name="Consignee" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Invoice}OrganizationInfo" minOccurs="0"/>
 *         &lt;element name="DocumentDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DocumentNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="GrossQuantityTotal" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="GrossQuantityTotalInWords" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Grounds" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses}Grounds" minOccurs="0"/>
 *         &lt;element name="Items" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses}ArrayOfTorg12Item" minOccurs="0"/>
 *         &lt;element name="NetQuantityTotal" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="NetQuantityTotalInWords" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OperationCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ParcelsQuantityTotal" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ParcelsQuantityTotalInWords" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Payer" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Invoice}OrganizationInfo" minOccurs="0"/>
 *         &lt;element name="QuantityTotal" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SellerDocflowParticipant" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy}CompanyInfo" minOccurs="0"/>
 *         &lt;element name="Shipper" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Invoice}OrganizationInfo" minOccurs="0"/>
 *         &lt;element name="Signer" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses}Signer" minOccurs="0"/>
 *         &lt;element name="Supplier" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Invoice}OrganizationInfo" minOccurs="0"/>
 *         &lt;element name="SupplyAllowedBy" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses}Official" minOccurs="0"/>
 *         &lt;element name="SupplyDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SupplyPerformedBy" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses}Official" minOccurs="0"/>
 *         &lt;element name="Total" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TotalInWords" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TotalWithVatExcluded" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Vat" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="WaybillDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="WaybillNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Torg12SellerTitleInfo", propOrder = {
    "additionalInfo",
    "attachmentSheetsQuantity",
    "buyerDocflowParticipant",
    "chiefAccountant",
    "consignee",
    "documentDate",
    "documentNumber",
    "grossQuantityTotal",
    "grossQuantityTotalInWords",
    "grounds",
    "items",
    "netQuantityTotal",
    "netQuantityTotalInWords",
    "operationCode",
    "parcelsQuantityTotal",
    "parcelsQuantityTotalInWords",
    "payer",
    "quantityTotal",
    "sellerDocflowParticipant",
    "shipper",
    "signer",
    "supplier",
    "supplyAllowedBy",
    "supplyDate",
    "supplyPerformedBy",
    "total",
    "totalInWords",
    "totalWithVatExcluded",
    "vat",
    "waybillDate",
    "waybillNumber"
})
public class Torg12SellerTitleInfo {

    @XmlElement(name = "AdditionalInfo", nillable = true)
    protected String additionalInfo;
    @XmlElement(name = "AttachmentSheetsQuantity", nillable = true)
    protected String attachmentSheetsQuantity;
    @XmlElement(name = "BuyerDocflowParticipant", nillable = true)
    protected CompanyInfo buyerDocflowParticipant;
    @XmlElement(name = "ChiefAccountant", nillable = true)
    protected Official chiefAccountant;
    @XmlElement(name = "Consignee", nillable = true)
    protected OrganizationInfo consignee;
    @XmlElement(name = "DocumentDate", nillable = true)
    protected String documentDate;
    @XmlElement(name = "DocumentNumber", nillable = true)
    protected String documentNumber;
    @XmlElement(name = "GrossQuantityTotal", nillable = true)
    protected String grossQuantityTotal;
    @XmlElement(name = "GrossQuantityTotalInWords", nillable = true)
    protected String grossQuantityTotalInWords;
    @XmlElement(name = "Grounds", nillable = true)
    protected Grounds grounds;
    @XmlElement(name = "Items", nillable = true)
    protected ArrayOfTorg12Item items;
    @XmlElement(name = "NetQuantityTotal", nillable = true)
    protected String netQuantityTotal;
    @XmlElement(name = "NetQuantityTotalInWords", nillable = true)
    protected String netQuantityTotalInWords;
    @XmlElement(name = "OperationCode", nillable = true)
    protected String operationCode;
    @XmlElement(name = "ParcelsQuantityTotal", nillable = true)
    protected String parcelsQuantityTotal;
    @XmlElement(name = "ParcelsQuantityTotalInWords", nillable = true)
    protected String parcelsQuantityTotalInWords;
    @XmlElement(name = "Payer", nillable = true)
    protected OrganizationInfo payer;
    @XmlElement(name = "QuantityTotal", nillable = true)
    protected String quantityTotal;
    @XmlElement(name = "SellerDocflowParticipant", nillable = true)
    protected CompanyInfo sellerDocflowParticipant;
    @XmlElement(name = "Shipper", nillable = true)
    protected OrganizationInfo shipper;
    @XmlElement(name = "Signer", nillable = true)
    protected Signer signer;
    @XmlElement(name = "Supplier", nillable = true)
    protected OrganizationInfo supplier;
    @XmlElement(name = "SupplyAllowedBy", nillable = true)
    protected Official supplyAllowedBy;
    @XmlElement(name = "SupplyDate", nillable = true)
    protected String supplyDate;
    @XmlElement(name = "SupplyPerformedBy", nillable = true)
    protected Official supplyPerformedBy;
    @XmlElement(name = "Total", nillable = true)
    protected String total;
    @XmlElement(name = "TotalInWords", nillable = true)
    protected String totalInWords;
    @XmlElement(name = "TotalWithVatExcluded", nillable = true)
    protected String totalWithVatExcluded;
    @XmlElement(name = "Vat", nillable = true)
    protected String vat;
    @XmlElement(name = "WaybillDate", nillable = true)
    protected String waybillDate;
    @XmlElement(name = "WaybillNumber", nillable = true)
    protected String waybillNumber;

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
     * Gets the value of the attachmentSheetsQuantity property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAttachmentSheetsQuantity() {
        return attachmentSheetsQuantity;
    }

    /**
     * Sets the value of the attachmentSheetsQuantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAttachmentSheetsQuantity(String value) {
        this.attachmentSheetsQuantity = value;
    }

    /**
     * Gets the value of the buyerDocflowParticipant property.
     * 
     * @return
     *     possible object is
     *     {@link CompanyInfo }
     *     
     */
    public CompanyInfo getBuyerDocflowParticipant() {
        return buyerDocflowParticipant;
    }

    /**
     * Sets the value of the buyerDocflowParticipant property.
     * 
     * @param value
     *     allowed object is
     *     {@link CompanyInfo }
     *     
     */
    public void setBuyerDocflowParticipant(CompanyInfo value) {
        this.buyerDocflowParticipant = value;
    }

    /**
     * Gets the value of the chiefAccountant property.
     * 
     * @return
     *     possible object is
     *     {@link Official }
     *     
     */
    public Official getChiefAccountant() {
        return chiefAccountant;
    }

    /**
     * Sets the value of the chiefAccountant property.
     * 
     * @param value
     *     allowed object is
     *     {@link Official }
     *     
     */
    public void setChiefAccountant(Official value) {
        this.chiefAccountant = value;
    }

    /**
     * Gets the value of the consignee property.
     * 
     * @return
     *     possible object is
     *     {@link OrganizationInfo }
     *     
     */
    public OrganizationInfo getConsignee() {
        return consignee;
    }

    /**
     * Sets the value of the consignee property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrganizationInfo }
     *     
     */
    public void setConsignee(OrganizationInfo value) {
        this.consignee = value;
    }

    /**
     * Gets the value of the documentDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocumentDate() {
        return documentDate;
    }

    /**
     * Sets the value of the documentDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocumentDate(String value) {
        this.documentDate = value;
    }

    /**
     * Gets the value of the documentNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the value of the documentNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocumentNumber(String value) {
        this.documentNumber = value;
    }

    /**
     * Gets the value of the grossQuantityTotal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGrossQuantityTotal() {
        return grossQuantityTotal;
    }

    /**
     * Sets the value of the grossQuantityTotal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGrossQuantityTotal(String value) {
        this.grossQuantityTotal = value;
    }

    /**
     * Gets the value of the grossQuantityTotalInWords property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGrossQuantityTotalInWords() {
        return grossQuantityTotalInWords;
    }

    /**
     * Sets the value of the grossQuantityTotalInWords property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGrossQuantityTotalInWords(String value) {
        this.grossQuantityTotalInWords = value;
    }

    /**
     * Gets the value of the grounds property.
     * 
     * @return
     *     possible object is
     *     {@link Grounds }
     *     
     */
    public Grounds getGrounds() {
        return grounds;
    }

    /**
     * Sets the value of the grounds property.
     * 
     * @param value
     *     allowed object is
     *     {@link Grounds }
     *     
     */
    public void setGrounds(Grounds value) {
        this.grounds = value;
    }

    /**
     * Gets the value of the items property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfTorg12Item }
     *     
     */
    public ArrayOfTorg12Item getItems() {
        return items;
    }

    /**
     * Sets the value of the items property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfTorg12Item }
     *     
     */
    public void setItems(ArrayOfTorg12Item value) {
        this.items = value;
    }

    /**
     * Gets the value of the netQuantityTotal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNetQuantityTotal() {
        return netQuantityTotal;
    }

    /**
     * Sets the value of the netQuantityTotal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNetQuantityTotal(String value) {
        this.netQuantityTotal = value;
    }

    /**
     * Gets the value of the netQuantityTotalInWords property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNetQuantityTotalInWords() {
        return netQuantityTotalInWords;
    }

    /**
     * Sets the value of the netQuantityTotalInWords property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNetQuantityTotalInWords(String value) {
        this.netQuantityTotalInWords = value;
    }

    /**
     * Gets the value of the operationCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOperationCode() {
        return operationCode;
    }

    /**
     * Sets the value of the operationCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOperationCode(String value) {
        this.operationCode = value;
    }

    /**
     * Gets the value of the parcelsQuantityTotal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParcelsQuantityTotal() {
        return parcelsQuantityTotal;
    }

    /**
     * Sets the value of the parcelsQuantityTotal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParcelsQuantityTotal(String value) {
        this.parcelsQuantityTotal = value;
    }

    /**
     * Gets the value of the parcelsQuantityTotalInWords property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParcelsQuantityTotalInWords() {
        return parcelsQuantityTotalInWords;
    }

    /**
     * Sets the value of the parcelsQuantityTotalInWords property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParcelsQuantityTotalInWords(String value) {
        this.parcelsQuantityTotalInWords = value;
    }

    /**
     * Gets the value of the payer property.
     * 
     * @return
     *     possible object is
     *     {@link OrganizationInfo }
     *     
     */
    public OrganizationInfo getPayer() {
        return payer;
    }

    /**
     * Sets the value of the payer property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrganizationInfo }
     *     
     */
    public void setPayer(OrganizationInfo value) {
        this.payer = value;
    }

    /**
     * Gets the value of the quantityTotal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQuantityTotal() {
        return quantityTotal;
    }

    /**
     * Sets the value of the quantityTotal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQuantityTotal(String value) {
        this.quantityTotal = value;
    }

    /**
     * Gets the value of the sellerDocflowParticipant property.
     * 
     * @return
     *     possible object is
     *     {@link CompanyInfo }
     *     
     */
    public CompanyInfo getSellerDocflowParticipant() {
        return sellerDocflowParticipant;
    }

    /**
     * Sets the value of the sellerDocflowParticipant property.
     * 
     * @param value
     *     allowed object is
     *     {@link CompanyInfo }
     *     
     */
    public void setSellerDocflowParticipant(CompanyInfo value) {
        this.sellerDocflowParticipant = value;
    }

    /**
     * Gets the value of the shipper property.
     * 
     * @return
     *     possible object is
     *     {@link OrganizationInfo }
     *     
     */
    public OrganizationInfo getShipper() {
        return shipper;
    }

    /**
     * Sets the value of the shipper property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrganizationInfo }
     *     
     */
    public void setShipper(OrganizationInfo value) {
        this.shipper = value;
    }

    /**
     * Gets the value of the signer property.
     * 
     * @return
     *     possible object is
     *     {@link Signer }
     *     
     */
    public Signer getSigner() {
        return signer;
    }

    /**
     * Sets the value of the signer property.
     * 
     * @param value
     *     allowed object is
     *     {@link Signer }
     *     
     */
    public void setSigner(Signer value) {
        this.signer = value;
    }

    /**
     * Gets the value of the supplier property.
     * 
     * @return
     *     possible object is
     *     {@link OrganizationInfo }
     *     
     */
    public OrganizationInfo getSupplier() {
        return supplier;
    }

    /**
     * Sets the value of the supplier property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrganizationInfo }
     *     
     */
    public void setSupplier(OrganizationInfo value) {
        this.supplier = value;
    }

    /**
     * Gets the value of the supplyAllowedBy property.
     * 
     * @return
     *     possible object is
     *     {@link Official }
     *     
     */
    public Official getSupplyAllowedBy() {
        return supplyAllowedBy;
    }

    /**
     * Sets the value of the supplyAllowedBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link Official }
     *     
     */
    public void setSupplyAllowedBy(Official value) {
        this.supplyAllowedBy = value;
    }

    /**
     * Gets the value of the supplyDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSupplyDate() {
        return supplyDate;
    }

    /**
     * Sets the value of the supplyDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSupplyDate(String value) {
        this.supplyDate = value;
    }

    /**
     * Gets the value of the supplyPerformedBy property.
     * 
     * @return
     *     possible object is
     *     {@link Official }
     *     
     */
    public Official getSupplyPerformedBy() {
        return supplyPerformedBy;
    }

    /**
     * Sets the value of the supplyPerformedBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link Official }
     *     
     */
    public void setSupplyPerformedBy(Official value) {
        this.supplyPerformedBy = value;
    }

    /**
     * Gets the value of the total property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotal() {
        return total;
    }

    /**
     * Sets the value of the total property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotal(String value) {
        this.total = value;
    }

    /**
     * Gets the value of the totalInWords property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalInWords() {
        return totalInWords;
    }

    /**
     * Sets the value of the totalInWords property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalInWords(String value) {
        this.totalInWords = value;
    }

    /**
     * Gets the value of the totalWithVatExcluded property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalWithVatExcluded() {
        return totalWithVatExcluded;
    }

    /**
     * Sets the value of the totalWithVatExcluded property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalWithVatExcluded(String value) {
        this.totalWithVatExcluded = value;
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

    /**
     * Gets the value of the waybillDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWaybillDate() {
        return waybillDate;
    }

    /**
     * Sets the value of the waybillDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWaybillDate(String value) {
        this.waybillDate = value;
    }

    /**
     * Gets the value of the waybillNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWaybillNumber() {
        return waybillNumber;
    }

    /**
     * Sets the value of the waybillNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWaybillNumber(String value) {
        this.waybillNumber = value;
    }

}
