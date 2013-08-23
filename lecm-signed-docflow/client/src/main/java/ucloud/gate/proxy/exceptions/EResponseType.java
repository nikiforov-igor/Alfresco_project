
package ucloud.gate.proxy.exceptions;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EResponseType.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="EResponseType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="OK"/>
 *     &lt;enumeration value="PartnerError"/>
 *     &lt;enumeration value="Unauthorized"/>
 *     &lt;enumeration value="BillingException"/>
 *     &lt;enumeration value="UserError"/>
 *     &lt;enumeration value="ServiceError"/>
 *     &lt;enumeration value="OperatorException"/>
 *     &lt;enumeration value="NotFoundByOperator"/>
 *     &lt;enumeration value="InternalError"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "EResponseType")
@XmlEnum
public enum EResponseType {

    OK("OK"),
    @XmlEnumValue("PartnerError")
    PARTNER_ERROR("PartnerError"),
    @XmlEnumValue("Unauthorized")
    UNAUTHORIZED("Unauthorized"),
    @XmlEnumValue("BillingException")
    BILLING_EXCEPTION("BillingException"),
    @XmlEnumValue("UserError")
    USER_ERROR("UserError"),
    @XmlEnumValue("ServiceError")
    SERVICE_ERROR("ServiceError"),
    @XmlEnumValue("OperatorException")
    OPERATOR_EXCEPTION("OperatorException"),
    @XmlEnumValue("NotFoundByOperator")
    NOT_FOUND_BY_OPERATOR("NotFoundByOperator"),
    @XmlEnumValue("InternalError")
    INTERNAL_ERROR("InternalError");

    private final String value;

    EResponseType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EResponseType fromValue(String v) {
        for (EResponseType c: EResponseType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
