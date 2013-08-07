
package ucloud.gate.proxy.docflow;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EDocflowTransactionType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="EDocflowTransactionType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Unknown"/>
 *     &lt;enumeration value="InvoiceInvoice"/>
 *     &lt;enumeration value="InvoiceOperatorConfirmationToVendor"/>
 *     &lt;enumeration value="InvoiceOperatorConfirmationToCustomer"/>
 *     &lt;enumeration value="InvoiceVendorRN"/>
 *     &lt;enumeration value="InvoiceCustomerRN"/>
 *     &lt;enumeration value="InvoiceCustomerInvoiceRN"/>
 *     &lt;enumeration value="InvoiceOperatorConfirmationToCustomerInvoiceRN"/>
 *     &lt;enumeration value="InvoiceCustomerRNToOperatorConfirmationToCustomerInvoiceRN"/>
 *     &lt;enumeration value="InvoiceCustomerCorrection"/>
 *     &lt;enumeration value="InvoiceVendorRNToCustomerCorrection"/>
 *     &lt;enumeration value="NoRecipientSignatureRequest"/>
 *     &lt;enumeration value="WaitingForRecipientSignature"/>
 *     &lt;enumeration value="WithRecipientSignature"/>
 *     &lt;enumeration value="SignatureRequestRejected"/>
 *     &lt;enumeration value="RecipientReceiveNotification"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "EDocflowTransactionType")
@XmlEnum
public enum EDocflowTransactionType {

    @XmlEnumValue("Unknown")
    UNKNOWN("Unknown"),
    @XmlEnumValue("InvoiceInvoice")
    INVOICE_INVOICE("InvoiceInvoice"),
    @XmlEnumValue("InvoiceOperatorConfirmationToVendor")
    INVOICE_OPERATOR_CONFIRMATION_TO_VENDOR("InvoiceOperatorConfirmationToVendor"),
    @XmlEnumValue("InvoiceOperatorConfirmationToCustomer")
    INVOICE_OPERATOR_CONFIRMATION_TO_CUSTOMER("InvoiceOperatorConfirmationToCustomer"),
    @XmlEnumValue("InvoiceVendorRN")
    INVOICE_VENDOR_RN("InvoiceVendorRN"),
    @XmlEnumValue("InvoiceCustomerRN")
    INVOICE_CUSTOMER_RN("InvoiceCustomerRN"),
    @XmlEnumValue("InvoiceCustomerInvoiceRN")
    INVOICE_CUSTOMER_INVOICE_RN("InvoiceCustomerInvoiceRN"),
    @XmlEnumValue("InvoiceOperatorConfirmationToCustomerInvoiceRN")
    INVOICE_OPERATOR_CONFIRMATION_TO_CUSTOMER_INVOICE_RN("InvoiceOperatorConfirmationToCustomerInvoiceRN"),
    @XmlEnumValue("InvoiceCustomerRNToOperatorConfirmationToCustomerInvoiceRN")
    INVOICE_CUSTOMER_RN_TO_OPERATOR_CONFIRMATION_TO_CUSTOMER_INVOICE_RN("InvoiceCustomerRNToOperatorConfirmationToCustomerInvoiceRN"),
    @XmlEnumValue("InvoiceCustomerCorrection")
    INVOICE_CUSTOMER_CORRECTION("InvoiceCustomerCorrection"),
    @XmlEnumValue("InvoiceVendorRNToCustomerCorrection")
    INVOICE_VENDOR_RN_TO_CUSTOMER_CORRECTION("InvoiceVendorRNToCustomerCorrection"),
    @XmlEnumValue("NoRecipientSignatureRequest")
    NO_RECIPIENT_SIGNATURE_REQUEST("NoRecipientSignatureRequest"),
    @XmlEnumValue("WaitingForRecipientSignature")
    WAITING_FOR_RECIPIENT_SIGNATURE("WaitingForRecipientSignature"),
    @XmlEnumValue("WithRecipientSignature")
    WITH_RECIPIENT_SIGNATURE("WithRecipientSignature"),
    @XmlEnumValue("SignatureRequestRejected")
    SIGNATURE_REQUEST_REJECTED("SignatureRequestRejected"),
    @XmlEnumValue("RecipientReceiveNotification")
    RECIPIENT_RECEIVE_NOTIFICATION("RecipientReceiveNotification");
    private final String value;

    EDocflowTransactionType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EDocflowTransactionType fromValue(String v) {
        for (EDocflowTransactionType c: EDocflowTransactionType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
