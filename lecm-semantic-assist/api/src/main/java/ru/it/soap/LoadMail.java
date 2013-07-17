
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
 *         &lt;element name="from" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="to" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="date" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="mailText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="attachFileName" type="{http://it.ru/}ArrayOfString" minOccurs="0"/>
 *         &lt;element name="attachContent" type="{http://it.ru/}ArrayOfBase64Binary" minOccurs="0"/>
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
    "from",
    "to",
    "date",
    "mailText",
    "attachFileName",
    "attachContent"
})
@XmlRootElement(name = "LoadMail")
public class LoadMail {

    protected String from;
    protected String to;
    @XmlElement(required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar date;
    protected String mailText;
    protected ArrayOfString attachFileName;
    protected ArrayOfBase64Binary attachContent;

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
     * Gets the value of the mailText property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMailText() {
        return mailText;
    }

    /**
     * Sets the value of the mailText property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMailText(String value) {
        this.mailText = value;
    }

    /**
     * Gets the value of the attachFileName property.
     *
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *
     */
    public ArrayOfString getAttachFileName() {
        return attachFileName;
    }

    /**
     * Sets the value of the attachFileName property.
     *
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *
     */
    public void setAttachFileName(ArrayOfString value) {
        this.attachFileName = value;
    }

    /**
     * Gets the value of the attachContent property.
     *
     * @return
     *     possible object is
     *     {@link ArrayOfBase64Binary }
     *
     */
    public ArrayOfBase64Binary getAttachContent() {
        return attachContent;
    }

    /**
     * Sets the value of the attachContent property.
     *
     * @param value
     *     allowed object is
     *     {@link ArrayOfBase64Binary }
     *
     */
    public void setAttachContent(ArrayOfBase64Binary value) {
        this.attachContent = value;
    }

}
