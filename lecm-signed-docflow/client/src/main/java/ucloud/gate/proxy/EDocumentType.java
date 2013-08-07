
package ucloud.gate.proxy;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EDocumentType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="EDocumentType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Unknown"/>
 *     &lt;enumeration value="NonFormalized"/>
 *     &lt;enumeration value="Invoice"/>
 *     &lt;enumeration value="InvoiceReceipt"/>
 *     &lt;enumeration value="InvoiceConfirmation"/>
 *     &lt;enumeration value="InvoiceRevision"/>
 *     &lt;enumeration value="InvoiceCorrection"/>
 *     &lt;enumeration value="InvoiceCorrectionRevision"/>
 *     &lt;enumeration value="Torg12"/>
 *     &lt;enumeration value="Akt"/>
 *     &lt;enumeration value="Torg12BuyerTitle"/>
 *     &lt;enumeration value="AktBuyerTitle"/>
 *     &lt;enumeration value="ReceiptNotification"/>
 *     &lt;enumeration value="CorrectionRequest"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "EDocumentType")
@XmlEnum
public enum EDocumentType {

    @XmlEnumValue("Unknown")
    UNKNOWN("Unknown"),
    @XmlEnumValue("NonFormalized")
    NON_FORMALIZED("NonFormalized"),
    @XmlEnumValue("Invoice")
    INVOICE("Invoice"),
    @XmlEnumValue("InvoiceReceipt")
    INVOICE_RECEIPT("InvoiceReceipt"),
    @XmlEnumValue("InvoiceConfirmation")
    INVOICE_CONFIRMATION("InvoiceConfirmation"),
    @XmlEnumValue("InvoiceRevision")
    INVOICE_REVISION("InvoiceRevision"),
    @XmlEnumValue("InvoiceCorrection")
    INVOICE_CORRECTION("InvoiceCorrection"),
    @XmlEnumValue("InvoiceCorrectionRevision")
    INVOICE_CORRECTION_REVISION("InvoiceCorrectionRevision"),
    @XmlEnumValue("Torg12")
    TORG_12("Torg12"),
    @XmlEnumValue("Akt")
    AKT("Akt"),
    @XmlEnumValue("Torg12BuyerTitle")
    TORG_12_BUYER_TITLE("Torg12BuyerTitle"),
    @XmlEnumValue("AktBuyerTitle")
    AKT_BUYER_TITLE("AktBuyerTitle"),
    @XmlEnumValue("ReceiptNotification")
    RECEIPT_NOTIFICATION("ReceiptNotification"),
    @XmlEnumValue("CorrectionRequest")
    CORRECTION_REQUEST("CorrectionRequest");
    private final String value;

    EDocumentType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EDocumentType fromValue(String v) {
        for (EDocumentType c: EDocumentType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
