
package ucloud.gate.proxy.invoice.classes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import ucloud.gate.proxy.generating.documents.common.Attorney;


/**
 * <p>Java class for Torg12BuyerTitleInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Torg12BuyerTitleInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AcceptedBy" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses}Official" minOccurs="0"/>
 *         &lt;element name="AdditionalInfo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Attorney" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common}Attorney" minOccurs="0"/>
 *         &lt;element name="ReceivedBy" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses}Official" minOccurs="0"/>
 *         &lt;element name="ShipmentReceiptDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Signer" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses}Signer" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Torg12BuyerTitleInfo", propOrder = {
    "acceptedBy",
    "additionalInfo",
    "attorney",
    "receivedBy",
    "shipmentReceiptDate",
    "signer"
})
public class Torg12BuyerTitleInfo {

    @XmlElement(name = "AcceptedBy", nillable = true)
    protected Official acceptedBy;
    @XmlElement(name = "AdditionalInfo", nillable = true)
    protected String additionalInfo;
    @XmlElement(name = "Attorney", nillable = true)
    protected Attorney attorney;
    @XmlElement(name = "ReceivedBy", nillable = true)
    protected Official receivedBy;
    @XmlElement(name = "ShipmentReceiptDate", nillable = true)
    protected String shipmentReceiptDate;
    @XmlElement(name = "Signer", nillable = true)
    protected Signer signer;

    /**
     * Gets the value of the acceptedBy property.
     * 
     * @return
     *     possible object is
     *     {@link Official }
     *     
     */
    public Official getAcceptedBy() {
        return acceptedBy;
    }

    /**
     * Sets the value of the acceptedBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link Official }
     *     
     */
    public void setAcceptedBy(Official value) {
        this.acceptedBy = value;
    }

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
     * Gets the value of the attorney property.
     * 
     * @return
     *     possible object is
     *     {@link Attorney }
     *     
     */
    public Attorney getAttorney() {
        return attorney;
    }

    /**
     * Sets the value of the attorney property.
     * 
     * @param value
     *     allowed object is
     *     {@link Attorney }
     *     
     */
    public void setAttorney(Attorney value) {
        this.attorney = value;
    }

    /**
     * Gets the value of the receivedBy property.
     * 
     * @return
     *     possible object is
     *     {@link Official }
     *     
     */
    public Official getReceivedBy() {
        return receivedBy;
    }

    /**
     * Sets the value of the receivedBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link Official }
     *     
     */
    public void setReceivedBy(Official value) {
        this.receivedBy = value;
    }

    /**
     * Gets the value of the shipmentReceiptDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShipmentReceiptDate() {
        return shipmentReceiptDate;
    }

    /**
     * Sets the value of the shipmentReceiptDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShipmentReceiptDate(String value) {
        this.shipmentReceiptDate = value;
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

}
