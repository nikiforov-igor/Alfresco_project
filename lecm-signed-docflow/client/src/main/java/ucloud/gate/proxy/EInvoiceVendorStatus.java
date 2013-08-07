
package ucloud.gate.proxy;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EInvoiceVendorStatus.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="EInvoiceVendorStatus">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Invoice_Sent_0"/>
 *     &lt;enumeration value="OperatorConfirmationToVendor_Received_1"/>
 *     &lt;enumeration value="VendorRN_Sent_2"/>
 *     &lt;enumeration value="CustomerInvoiceRN_Received_3"/>
 *     &lt;enumeration value="CustomerCorrection_Received_4"/>
 *     &lt;enumeration value="VendorRNToCustomerCorrection_Sent_5"/>
 *     &lt;enumeration value="Finished"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "EInvoiceVendorStatus")
@XmlEnum
public enum EInvoiceVendorStatus {

    @XmlEnumValue("Invoice_Sent_0")
    INVOICE_SENT_0("Invoice_Sent_0"),
    @XmlEnumValue("OperatorConfirmationToVendor_Received_1")
    OPERATOR_CONFIRMATION_TO_VENDOR_RECEIVED_1("OperatorConfirmationToVendor_Received_1"),
    @XmlEnumValue("VendorRN_Sent_2")
    VENDOR_RN_SENT_2("VendorRN_Sent_2"),
    @XmlEnumValue("CustomerInvoiceRN_Received_3")
    CUSTOMER_INVOICE_RN_RECEIVED_3("CustomerInvoiceRN_Received_3"),
    @XmlEnumValue("CustomerCorrection_Received_4")
    CUSTOMER_CORRECTION_RECEIVED_4("CustomerCorrection_Received_4"),
    @XmlEnumValue("VendorRNToCustomerCorrection_Sent_5")
    VENDOR_RN_TO_CUSTOMER_CORRECTION_SENT_5("VendorRNToCustomerCorrection_Sent_5"),
    @XmlEnumValue("Finished")
    FINISHED("Finished");
    private final String value;

    EInvoiceVendorStatus(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EInvoiceVendorStatus fromValue(String v) {
        for (EInvoiceVendorStatus c: EInvoiceVendorStatus.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
