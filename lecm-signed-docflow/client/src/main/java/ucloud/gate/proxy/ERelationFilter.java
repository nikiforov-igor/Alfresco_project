
package ucloud.gate.proxy;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ERelationFilter.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ERelationFilter">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="All"/>
 *     &lt;enumeration value="Inbound"/>
 *     &lt;enumeration value="Outbound"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ERelationFilter")
@XmlEnum
public enum ERelationFilter {

    @XmlEnumValue("All")
    ALL("All"),
    @XmlEnumValue("Inbound")
    INBOUND("Inbound"),
    @XmlEnumValue("Outbound")
    OUTBOUND("Outbound");
    private final String value;

    ERelationFilter(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ERelationFilter fromValue(String v) {
        for (ERelationFilter c: ERelationFilter.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
