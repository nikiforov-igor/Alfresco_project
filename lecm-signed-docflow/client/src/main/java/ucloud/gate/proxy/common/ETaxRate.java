
package ucloud.gate.proxy.common;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ETaxRate.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ETaxRate">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NoVat"/>
 *     &lt;enumeration value="Percent_0"/>
 *     &lt;enumeration value="Percent_10"/>
 *     &lt;enumeration value="Percent_18"/>
 *     &lt;enumeration value="Percent_20"/>
 *     &lt;enumeration value="Percent_10_110"/>
 *     &lt;enumeration value="Percent_18_118"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ETaxRate")
@XmlEnum
public enum ETaxRate {

    @XmlEnumValue("NoVat")
    NO_VAT("NoVat"),
    @XmlEnumValue("Percent_0")
    PERCENT_0("Percent_0"),
    @XmlEnumValue("Percent_10")
    PERCENT_10("Percent_10"),
    @XmlEnumValue("Percent_18")
    PERCENT_18("Percent_18"),
    @XmlEnumValue("Percent_20")
    PERCENT_20("Percent_20"),
    @XmlEnumValue("Percent_10_110")
    PERCENT_10_110("Percent_10_110"),
    @XmlEnumValue("Percent_18_118")
    PERCENT_18_118("Percent_18_118");
    private final String value;

    ETaxRate(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ETaxRate fromValue(String v) {
        for (ETaxRate c: ETaxRate.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
