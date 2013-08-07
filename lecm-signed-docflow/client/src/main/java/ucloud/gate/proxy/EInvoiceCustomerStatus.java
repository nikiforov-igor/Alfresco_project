
package ucloud.gate.proxy;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EInvoiceCustomerStatus.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="EInvoiceCustomerStatus">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Unknown_0"/>
 *     &lt;enumeration value="Invoice_Received_1"/>
 *     &lt;enumeration value="OperatorConfirmationToCustomer_Received_2"/>
 *     &lt;enumeration value="CustomerRN_Sent_3"/>
 *     &lt;enumeration value="CustomerInvoiceRN_Sent_4"/>
 *     &lt;enumeration value="OperatorConfirmationToCustomerInvoiceRN_Received_5"/>
 *     &lt;enumeration value="CustomerRNToOperatorConfirmationToCustomerInvoiceRN_Sent_6"/>
 *     &lt;enumeration value="CustomerCorrection_Sent_7"/>
 *     &lt;enumeration value="VendorRNToCustomerCorrection_Received_8"/>
 *     &lt;enumeration value="Finished"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "EInvoiceCustomerStatus")
@XmlEnum
public enum EInvoiceCustomerStatus {

    @XmlEnumValue("Unknown_0")
    UNKNOWN_0("Unknown_0"),
    @XmlEnumValue("Invoice_Received_1")
    INVOICE_RECEIVED_1("Invoice_Received_1"),
    @XmlEnumValue("OperatorConfirmationToCustomer_Received_2")
    OPERATOR_CONFIRMATION_TO_CUSTOMER_RECEIVED_2("OperatorConfirmationToCustomer_Received_2"),
    @XmlEnumValue("CustomerRN_Sent_3")
    CUSTOMER_RN_SENT_3("CustomerRN_Sent_3"),
    @XmlEnumValue("CustomerInvoiceRN_Sent_4")
    CUSTOMER_INVOICE_RN_SENT_4("CustomerInvoiceRN_Sent_4"),
    @XmlEnumValue("OperatorConfirmationToCustomerInvoiceRN_Received_5")
    OPERATOR_CONFIRMATION_TO_CUSTOMER_INVOICE_RN_RECEIVED_5("OperatorConfirmationToCustomerInvoiceRN_Received_5"),
    @XmlEnumValue("CustomerRNToOperatorConfirmationToCustomerInvoiceRN_Sent_6")
    CUSTOMER_RN_TO_OPERATOR_CONFIRMATION_TO_CUSTOMER_INVOICE_RN_SENT_6("CustomerRNToOperatorConfirmationToCustomerInvoiceRN_Sent_6"),
    @XmlEnumValue("CustomerCorrection_Sent_7")
    CUSTOMER_CORRECTION_SENT_7("CustomerCorrection_Sent_7"),
    @XmlEnumValue("VendorRNToCustomerCorrection_Received_8")
    VENDOR_RN_TO_CUSTOMER_CORRECTION_RECEIVED_8("VendorRNToCustomerCorrection_Received_8"),
    @XmlEnumValue("Finished")
    FINISHED("Finished");
    private final String value;

    EInvoiceCustomerStatus(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EInvoiceCustomerStatus fromValue(String v) {
        for (EInvoiceCustomerStatus c: EInvoiceCustomerStatus.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
