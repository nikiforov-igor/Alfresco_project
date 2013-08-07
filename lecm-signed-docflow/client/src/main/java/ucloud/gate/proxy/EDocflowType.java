
package ucloud.gate.proxy;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EDocflowType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="EDocflowType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Unknown"/>
 *     &lt;enumeration value="NonFormalized"/>
 *     &lt;enumeration value="BilateralNonFormalized"/>
 *     &lt;enumeration value="Invoice"/>
 *     &lt;enumeration value="InvoiceCorrection"/>
 *     &lt;enumeration value="Torg12"/>
 *     &lt;enumeration value="Akt"/>
 *     &lt;enumeration value="InvoiceRevision"/>
 *     &lt;enumeration value="InvoiceCorrectionRevision"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "EDocflowType")
@XmlEnum
public enum EDocflowType {

    @XmlEnumValue("Unknown")
    UNKNOWN("Unknown"),
    @XmlEnumValue("NonFormalized")
    NON_FORMALIZED("NonFormalized"),
    @XmlEnumValue("BilateralNonFormalized")
    BILATERAL_NON_FORMALIZED("BilateralNonFormalized"),
    @XmlEnumValue("Invoice")
    INVOICE("Invoice"),
    @XmlEnumValue("InvoiceCorrection")
    INVOICE_CORRECTION("InvoiceCorrection"),
    @XmlEnumValue("Torg12")
    TORG_12("Torg12"),
    @XmlEnumValue("Akt")
    AKT("Akt"),
    @XmlEnumValue("InvoiceRevision")
    INVOICE_REVISION("InvoiceRevision"),
    @XmlEnumValue("InvoiceCorrectionRevision")
    INVOICE_CORRECTION_REVISION("InvoiceCorrectionRevision");
    private final String value;

    EDocflowType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EDocflowType fromValue(String v) {
        for (EDocflowType c: EDocflowType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
