
package org.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import ucloud.gate.proxy.OperatorInfo;
import ucloud.gate.proxy.invoice.classes.Torg12BuyerTitleInfo;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="vendorId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="buyerId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="opInfo" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy}OperatorInfo" minOccurs="0"/>
 *         &lt;element name="date" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="info" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses}Torg12BuyerTitleInfo" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "vendorId",
    "buyerId",
    "opInfo",
    "date",
    "info"
})
@XmlRootElement(name = "GenerateTorg12XmlForBuyer")
public class GenerateTorg12XmlForBuyer {

    @XmlElement(nillable = true)
    protected String vendorId;
    @XmlElement(nillable = true)
    protected String buyerId;
    @XmlElement(nillable = true)
    protected OperatorInfo opInfo;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar date;
    @XmlElement(nillable = true)
    protected Torg12BuyerTitleInfo info;

    /**
     * Gets the value of the vendorId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVendorId() {
        return vendorId;
    }

    /**
     * Sets the value of the vendorId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVendorId(String value) {
        this.vendorId = value;
    }

    /**
     * Gets the value of the buyerId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBuyerId() {
        return buyerId;
    }

    /**
     * Sets the value of the buyerId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBuyerId(String value) {
        this.buyerId = value;
    }

    /**
     * Gets the value of the opInfo property.
     * 
     * @return
     *     possible object is
     *     {@link OperatorInfo }
     *     
     */
    public OperatorInfo getOpInfo() {
        return opInfo;
    }

    /**
     * Sets the value of the opInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link OperatorInfo }
     *     
     */
    public void setOpInfo(OperatorInfo value) {
        this.opInfo = value;
    }

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
     * Gets the value of the info property.
     * 
     * @return
     *     possible object is
     *     {@link Torg12BuyerTitleInfo }
     *     
     */
    public Torg12BuyerTitleInfo getInfo() {
        return info;
    }

    /**
     * Sets the value of the info property.
     * 
     * @param value
     *     allowed object is
     *     {@link Torg12BuyerTitleInfo }
     *     
     */
    public void setInfo(Torg12BuyerTitleInfo value) {
        this.info = value;
    }

}
