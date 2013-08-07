
package ucloud.gate.proxy;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ENonformalizedDocumentStatus.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ENonformalizedDocumentStatus">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Unknown"/>
 *     &lt;enumeration value="OutboundNoRecipientSignatureRequest"/>
 *     &lt;enumeration value="OutboundWaitingForRecipientSignature"/>
 *     &lt;enumeration value="OutboundWithRecipientSignature"/>
 *     &lt;enumeration value="OutboundRecipientSignatureRequestRejected"/>
 *     &lt;enumeration value="InboundNoRecipientSignatureRequest"/>
 *     &lt;enumeration value="InboundWaitingForRecipientSignature"/>
 *     &lt;enumeration value="InboundWithRecipientSignature"/>
 *     &lt;enumeration value="InboundRecipientSignatureRequestRejected"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ENonformalizedDocumentStatus")
@XmlEnum
public enum ENonformalizedDocumentStatus {

    @XmlEnumValue("Unknown")
    UNKNOWN("Unknown"),
    @XmlEnumValue("OutboundNoRecipientSignatureRequest")
    OUTBOUND_NO_RECIPIENT_SIGNATURE_REQUEST("OutboundNoRecipientSignatureRequest"),
    @XmlEnumValue("OutboundWaitingForRecipientSignature")
    OUTBOUND_WAITING_FOR_RECIPIENT_SIGNATURE("OutboundWaitingForRecipientSignature"),
    @XmlEnumValue("OutboundWithRecipientSignature")
    OUTBOUND_WITH_RECIPIENT_SIGNATURE("OutboundWithRecipientSignature"),
    @XmlEnumValue("OutboundRecipientSignatureRequestRejected")
    OUTBOUND_RECIPIENT_SIGNATURE_REQUEST_REJECTED("OutboundRecipientSignatureRequestRejected"),
    @XmlEnumValue("InboundNoRecipientSignatureRequest")
    INBOUND_NO_RECIPIENT_SIGNATURE_REQUEST("InboundNoRecipientSignatureRequest"),
    @XmlEnumValue("InboundWaitingForRecipientSignature")
    INBOUND_WAITING_FOR_RECIPIENT_SIGNATURE("InboundWaitingForRecipientSignature"),
    @XmlEnumValue("InboundWithRecipientSignature")
    INBOUND_WITH_RECIPIENT_SIGNATURE("InboundWithRecipientSignature"),
    @XmlEnumValue("InboundRecipientSignatureRequestRejected")
    INBOUND_RECIPIENT_SIGNATURE_REQUEST_REJECTED("InboundRecipientSignatureRequestRejected");
    private final String value;

    ENonformalizedDocumentStatus(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ENonformalizedDocumentStatus fromValue(String v) {
        for (ENonformalizedDocumentStatus c: ENonformalizedDocumentStatus.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
