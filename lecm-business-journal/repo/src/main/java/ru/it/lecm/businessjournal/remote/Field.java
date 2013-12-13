
package ru.it.lecm.businessjournal.remote;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for field.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="field">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="DATE"/>
 *     &lt;enumeration value="OBJECT_TYPE"/>
 *     &lt;enumeration value="EVENT_CATEGORY"/>
 *     &lt;enumeration value="MAIN_OBJECT"/>
 *     &lt;enumeration value="INITIATOR"/>
 *     &lt;enumeration value="INITIATOR_TEXT"/>
 *     &lt;enumeration value="MAIN_OBJECT_TEXT"/>
 *     &lt;enumeration value="OBJECT_TYPE_TEXT"/>
 *     &lt;enumeration value="EVENT_CATEGORY_TEXT"/>
 *     &lt;enumeration value="OBJECT_1_TEXT"/>
 *     &lt;enumeration value="OBJECT_2_TEXT"/>
 *     &lt;enumeration value="OBJECT_3_TEXT"/>
 *     &lt;enumeration value="OBJECT_4_TEXT"/>
 *     &lt;enumeration value="OBJECT_5_TEXT"/>
 *     &lt;enumeration value="RECORD_DESCRIPTION"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "field")
@XmlEnum
public enum Field {

    DATE,
    OBJECT_TYPE,
    EVENT_CATEGORY,
    MAIN_OBJECT,
    INITIATOR,
    INITIATOR_TEXT,
    MAIN_OBJECT_TEXT,
    OBJECT_TYPE_TEXT,
    EVENT_CATEGORY_TEXT,
    OBJECT_1_TEXT,
    OBJECT_2_TEXT,
    OBJECT_3_TEXT,
    OBJECT_4_TEXT,
    OBJECT_5_TEXT,
    RECORD_DESCRIPTION;

    public String value() {
        return name();
    }

    public static Field fromValue(String v) {
        return valueOf(v);
    }

}
