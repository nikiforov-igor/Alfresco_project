
package ucloud.gate.proxy.generating.documents.invoice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import ucloud.gate.proxy.generating.documents.Signer;
import ucloud.gate.proxy.generating.documents.common.ParticipantWithAddress;
import ucloud.gate.proxy.generating.documents.common.ShipmentParticipant;


/**
 * <p>Java class for InvoiceGen complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InvoiceGen">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Consignee" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common}ShipmentParticipant" minOccurs="0"/>
 *         &lt;element name="CurrencyCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Customer" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common}ParticipantWithAddress" minOccurs="0"/>
 *         &lt;element name="DocumentDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="DocumentNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="GenerateDateTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="InformationFieldId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="InformationText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PaymentDocuments" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice}ArrayOfPaymentDocumentGen" minOccurs="0"/>
 *         &lt;element name="Products" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice}ArrayOfInvoiceProductGen" minOccurs="0"/>
 *         &lt;element name="RevisionDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="RevisionNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Shipper" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common}ShipmentParticipant" minOccurs="0"/>
 *         &lt;element name="Signer" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments}Signer" minOccurs="0"/>
 *         &lt;element name="TotalSumNoVat" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TotalSumVat" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TotalSumWithVat" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Vendor" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common}ParticipantWithAddress" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InvoiceGen", propOrder = {
    "consignee",
    "currencyCode",
    "customer",
    "documentDate",
    "documentNumber",
    "generateDateTime",
    "informationFieldId",
    "informationText",
    "paymentDocuments",
    "products",
    "revisionDate",
    "revisionNumber",
    "shipper",
    "signer",
    "totalSumNoVat",
    "totalSumVat",
    "totalSumWithVat",
    "vendor"
})
public class InvoiceGen {

    @XmlElement(name = "Consignee", nillable = true)
    protected ShipmentParticipant consignee;
    @XmlElement(name = "CurrencyCode", nillable = true)
    protected String currencyCode;
    @XmlElement(name = "Customer", nillable = true)
    protected ParticipantWithAddress customer;
    @XmlElement(name = "DocumentDate", nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar documentDate;
    @XmlElement(name = "DocumentNumber", nillable = true)
    protected String documentNumber;
    @XmlElement(name = "GenerateDateTime")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar generateDateTime;
    @XmlElement(name = "InformationFieldId", nillable = true)
    protected String informationFieldId;
    @XmlElement(name = "InformationText", nillable = true)
    protected String informationText;
    @XmlElement(name = "PaymentDocuments", nillable = true)
    protected ArrayOfPaymentDocumentGen paymentDocuments;
    @XmlElement(name = "Products", nillable = true)
    protected ArrayOfInvoiceProductGen products;
    @XmlElement(name = "RevisionDate", nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar revisionDate;
    @XmlElement(name = "RevisionNumber", nillable = true)
    protected String revisionNumber;
    @XmlElement(name = "Shipper", nillable = true)
    protected ShipmentParticipant shipper;
    @XmlElement(name = "Signer", nillable = true)
    protected Signer signer;
    @XmlElement(name = "TotalSumNoVat", nillable = true)
    protected String totalSumNoVat;
    @XmlElement(name = "TotalSumVat", nillable = true)
    protected String totalSumVat;
    @XmlElement(name = "TotalSumWithVat", nillable = true)
    protected String totalSumWithVat;
    @XmlElement(name = "Vendor", nillable = true)
    protected ParticipantWithAddress vendor;

    /**
     * Gets the value of the consignee property.
     * 
     * @return
     *     possible object is
     *     {@link ShipmentParticipant }
     *     
     */
    public ShipmentParticipant getConsignee() {
        return consignee;
    }

    /**
     * Sets the value of the consignee property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShipmentParticipant }
     *     
     */
    public void setConsignee(ShipmentParticipant value) {
        this.consignee = value;
    }

    /**
     * Gets the value of the currencyCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCurrencyCode() {
        return currencyCode;
    }

    /**
     * Sets the value of the currencyCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCurrencyCode(String value) {
        this.currencyCode = value;
    }

    /**
     * Gets the value of the customer property.
     * 
     * @return
     *     possible object is
     *     {@link ParticipantWithAddress }
     *     
     */
    public ParticipantWithAddress getCustomer() {
        return customer;
    }

    /**
     * Sets the value of the customer property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParticipantWithAddress }
     *     
     */
    public void setCustomer(ParticipantWithAddress value) {
        this.customer = value;
    }

    /**
     * Gets the value of the documentDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDocumentDate() {
        return documentDate;
    }

    /**
     * Sets the value of the documentDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDocumentDate(XMLGregorianCalendar value) {
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
     * Gets the value of the generateDateTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getGenerateDateTime() {
        return generateDateTime;
    }

    /**
     * Sets the value of the generateDateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setGenerateDateTime(XMLGregorianCalendar value) {
        this.generateDateTime = value;
    }

    /**
     * Gets the value of the informationFieldId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInformationFieldId() {
        return informationFieldId;
    }

    /**
     * Sets the value of the informationFieldId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInformationFieldId(String value) {
        this.informationFieldId = value;
    }

    /**
     * Gets the value of the informationText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInformationText() {
        return informationText;
    }

    /**
     * Sets the value of the informationText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInformationText(String value) {
        this.informationText = value;
    }

    /**
     * Gets the value of the paymentDocuments property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfPaymentDocumentGen }
     *     
     */
    public ArrayOfPaymentDocumentGen getPaymentDocuments() {
        return paymentDocuments;
    }

    /**
     * Sets the value of the paymentDocuments property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfPaymentDocumentGen }
     *     
     */
    public void setPaymentDocuments(ArrayOfPaymentDocumentGen value) {
        this.paymentDocuments = value;
    }

    /**
     * Gets the value of the products property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfInvoiceProductGen }
     *     
     */
    public ArrayOfInvoiceProductGen getProducts() {
        return products;
    }

    /**
     * Sets the value of the products property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfInvoiceProductGen }
     *     
     */
    public void setProducts(ArrayOfInvoiceProductGen value) {
        this.products = value;
    }

    /**
     * Gets the value of the revisionDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getRevisionDate() {
        return revisionDate;
    }

    /**
     * Sets the value of the revisionDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setRevisionDate(XMLGregorianCalendar value) {
        this.revisionDate = value;
    }

    /**
     * Gets the value of the revisionNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRevisionNumber() {
        return revisionNumber;
    }

    /**
     * Sets the value of the revisionNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRevisionNumber(String value) {
        this.revisionNumber = value;
    }

    /**
     * Gets the value of the shipper property.
     * 
     * @return
     *     possible object is
     *     {@link ShipmentParticipant }
     *     
     */
    public ShipmentParticipant getShipper() {
        return shipper;
    }

    /**
     * Sets the value of the shipper property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShipmentParticipant }
     *     
     */
    public void setShipper(ShipmentParticipant value) {
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
     * Gets the value of the totalSumNoVat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalSumNoVat() {
        return totalSumNoVat;
    }

    /**
     * Sets the value of the totalSumNoVat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalSumNoVat(String value) {
        this.totalSumNoVat = value;
    }

    /**
     * Gets the value of the totalSumVat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalSumVat() {
        return totalSumVat;
    }

    /**
     * Sets the value of the totalSumVat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalSumVat(String value) {
        this.totalSumVat = value;
    }

    /**
     * Gets the value of the totalSumWithVat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalSumWithVat() {
        return totalSumWithVat;
    }

    /**
     * Sets the value of the totalSumWithVat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalSumWithVat(String value) {
        this.totalSumWithVat = value;
    }

    /**
     * Gets the value of the vendor property.
     * 
     * @return
     *     possible object is
     *     {@link ParticipantWithAddress }
     *     
     */
    public ParticipantWithAddress getVendor() {
        return vendor;
    }

    /**
     * Sets the value of the vendor property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParticipantWithAddress }
     *     
     */
    public void setVendor(ParticipantWithAddress value) {
        this.vendor = value;
    }

}
