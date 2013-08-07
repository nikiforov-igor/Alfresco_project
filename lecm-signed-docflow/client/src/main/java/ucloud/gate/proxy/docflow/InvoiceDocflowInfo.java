
package ucloud.gate.proxy.docflow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import ucloud.gate.proxy.DocflowInfoBase;
import ucloud.gate.proxy.EInvoiceCustomerStatus;
import ucloud.gate.proxy.EInvoiceVendorStatus;


/**
 * <p>Java class for InvoiceDocflowInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InvoiceDocflowInfo">
 *   &lt;complexContent>
 *     &lt;extension base="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy}DocflowInfoBase">
 *       &lt;sequence>
 *         &lt;element name="CustomerStatus" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy}EInvoiceCustomerStatus" minOccurs="0"/>
 *         &lt;element name="VendorStatus" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy}EInvoiceVendorStatus" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InvoiceDocflowInfo", propOrder = {
    "customerStatus",
    "vendorStatus"
})
public class InvoiceDocflowInfo
    extends DocflowInfoBase
{

    @XmlElement(name = "CustomerStatus")
    protected EInvoiceCustomerStatus customerStatus;
    @XmlElement(name = "VendorStatus")
    protected EInvoiceVendorStatus vendorStatus;

    /**
     * Gets the value of the customerStatus property.
     * 
     * @return
     *     possible object is
     *     {@link EInvoiceCustomerStatus }
     *     
     */
    public EInvoiceCustomerStatus getCustomerStatus() {
        return customerStatus;
    }

    /**
     * Sets the value of the customerStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link EInvoiceCustomerStatus }
     *     
     */
    public void setCustomerStatus(EInvoiceCustomerStatus value) {
        this.customerStatus = value;
    }

    /**
     * Gets the value of the vendorStatus property.
     * 
     * @return
     *     possible object is
     *     {@link EInvoiceVendorStatus }
     *     
     */
    public EInvoiceVendorStatus getVendorStatus() {
        return vendorStatus;
    }

    /**
     * Sets the value of the vendorStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link EInvoiceVendorStatus }
     *     
     */
    public void setVendorStatus(EInvoiceVendorStatus value) {
        this.vendorStatus = value;
    }

}
