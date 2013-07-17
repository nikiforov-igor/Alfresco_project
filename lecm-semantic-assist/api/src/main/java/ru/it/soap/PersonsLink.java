
package ru.it.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PersonsLink complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="PersonsLink">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Id1" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Spelling1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Id2" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Spelling2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CommonSignificItems" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="MailsCount" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="CallsCount" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PersonsLink", propOrder = {
    "id1",
    "spelling1",
    "id2",
    "spelling2",
    "commonSignificItems",
    "mailsCount",
    "callsCount"
})
public class PersonsLink {

    @XmlElement(name = "Id1")
    protected int id1;
    @XmlElement(name = "Spelling1")
    protected String spelling1;
    @XmlElement(name = "Id2")
    protected int id2;
    @XmlElement(name = "Spelling2")
    protected String spelling2;
    @XmlElement(name = "CommonSignificItems")
    protected int commonSignificItems;
    @XmlElement(name = "MailsCount")
    protected int mailsCount;
    @XmlElement(name = "CallsCount")
    protected int callsCount;

    /**
     * Gets the value of the id1 property.
     *
     */
    public int getId1() {
        return id1;
    }

    /**
     * Sets the value of the id1 property.
     *
     */
    public void setId1(int value) {
        this.id1 = value;
    }

    /**
     * Gets the value of the spelling1 property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSpelling1() {
        return spelling1;
    }

    /**
     * Sets the value of the spelling1 property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSpelling1(String value) {
        this.spelling1 = value;
    }

    /**
     * Gets the value of the id2 property.
     *
     */
    public int getId2() {
        return id2;
    }

    /**
     * Sets the value of the id2 property.
     *
     */
    public void setId2(int value) {
        this.id2 = value;
    }

    /**
     * Gets the value of the spelling2 property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSpelling2() {
        return spelling2;
    }

    /**
     * Sets the value of the spelling2 property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSpelling2(String value) {
        this.spelling2 = value;
    }

    /**
     * Gets the value of the commonSignificItems property.
     *
     */
    public int getCommonSignificItems() {
        return commonSignificItems;
    }

    /**
     * Sets the value of the commonSignificItems property.
     *
     */
    public void setCommonSignificItems(int value) {
        this.commonSignificItems = value;
    }

    /**
     * Gets the value of the mailsCount property.
     *
     */
    public int getMailsCount() {
        return mailsCount;
    }

    /**
     * Sets the value of the mailsCount property.
     *
     */
    public void setMailsCount(int value) {
        this.mailsCount = value;
    }

    /**
     * Gets the value of the callsCount property.
     *
     */
    public int getCallsCount() {
        return callsCount;
    }

    /**
     * Sets the value of the callsCount property.
     *
     */
    public void setCallsCount(int value) {
        this.callsCount = value;
    }

}
