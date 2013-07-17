
package ru.it.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for CommunicationLoad complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="CommunicationLoad">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="From" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="To" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Date" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="Texts" type="{http://it.ru/}ArrayOfString" minOccurs="0"/>
 *         &lt;element name="Attrs" type="{http://it.ru/}ArrayOfCommunicationAttrs" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CommunicationLoad", propOrder = {
    "from",
    "to",
    "date",
    "texts",
    "attrs"
})
public class CommunicationLoad {

    @XmlElement(name = "From")
    protected String from;
    @XmlElement(name = "To")
    protected String to;
    @XmlElement(name = "Date", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar date;
    @XmlElement(name = "Texts")
    protected ArrayOfString texts;
    @XmlElement(name = "Attrs")
    protected ArrayOfCommunicationAttrs attrs;

    /**
     * Gets the value of the from property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFrom() {
        return from;
    }

    /**
     * Sets the value of the from property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFrom(String value) {
        this.from = value;
    }

    /**
     * Gets the value of the to property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTo() {
        return to;
    }

    /**
     * Sets the value of the to property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTo(String value) {
        this.to = value;
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
     * Gets the value of the texts property.
     *
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *
     */
    public ArrayOfString getTexts() {
        return texts;
    }

    /**
     * Sets the value of the texts property.
     *
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *
     */
    public void setTexts(ArrayOfString value) {
        this.texts = value;
    }

    /**
     * Gets the value of the attrs property.
     *
     * @return
     *     possible object is
     *     {@link ArrayOfCommunicationAttrs }
     *
     */
    public ArrayOfCommunicationAttrs getAttrs() {
        return attrs;
    }

    /**
     * Sets the value of the attrs property.
     *
     * @param value
     *     allowed object is
     *     {@link ArrayOfCommunicationAttrs }
     *
     */
    public void setAttrs(ArrayOfCommunicationAttrs value) {
        this.attrs = value;
    }

}
