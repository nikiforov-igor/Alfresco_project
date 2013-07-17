
package ru.it.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="phoneFrom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phoneTo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="date" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="dur" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "phoneFrom",
    "phoneTo",
    "date",
    "dur"
})
@XmlRootElement(name = "LoadCall")
public class LoadCall {

    protected String phoneFrom;
    protected String phoneTo;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar date;
    protected int dur;

    /**
     * Gets the value of the phoneFrom property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPhoneFrom() {
        return phoneFrom;
    }

    /**
     * Sets the value of the phoneFrom property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPhoneFrom(String value) {
        this.phoneFrom = value;
    }

    /**
     * Gets the value of the phoneTo property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPhoneTo() {
        return phoneTo;
    }

    /**
     * Sets the value of the phoneTo property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPhoneTo(String value) {
        this.phoneTo = value;
    }

    /**
     * Gets the value of the date property.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getDate() {
        return date;
    }

    /**
     * Sets the value of the date property.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setDate(XMLGregorianCalendar value) {
        this.date = value;
    }

    /**
     * Gets the value of the dur property.
     *
     */
    public int getDur() {
        return dur;
    }

    /**
     * Sets the value of the dur property.
     *
     */
    public void setDur(int value) {
        this.dur = value;
    }

}
