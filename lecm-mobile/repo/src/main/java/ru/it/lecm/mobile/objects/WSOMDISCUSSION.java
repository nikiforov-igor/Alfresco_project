
package ru.it.lecm.mobile.objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for WSO_MDISCUSSION complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSO_MDISCUSSION">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:DefaultNamespace}WSOBJECT">
 *       &lt;sequence>
 *         &lt;element name="AUTHOR" type="{urn:DefaultNamespace}WSO_MPERSON"/>
 *         &lt;element name="PARENTDOCID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="MESSAGE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="PARENTDISCUSSIONID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="READERS" type="{urn:DefaultNamespace}WSO_COLLECTION"/>
 *         &lt;element name="RESPONSES" type="{urn:DefaultNamespace}WSO_COLLECTION"/>
 *         &lt;element name="CREATIONDATE" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSO_MDISCUSSION", propOrder = {
    "author",
    "parentdocid",
    "message",
    "parentdiscussionid",
    "readers",
    "responses",
    "creationdate"
})
public class WSOMDISCUSSION
    extends WSOBJECT
{

    @XmlElement(name = "AUTHOR", required = true, nillable = true)
    protected WSOMPERSON author;
    @XmlElement(name = "PARENTDOCID", required = true, nillable = true)
    protected String parentdocid;
    @XmlElement(name = "MESSAGE", required = true, nillable = true)
    protected String message;
    @XmlElement(name = "PARENTDISCUSSIONID", required = true, nillable = true)
    protected String parentdiscussionid;
    @XmlElement(name = "READERS", required = true, nillable = true)
    protected WSOCOLLECTION readers;
    @XmlElement(name = "RESPONSES", required = true, nillable = true)
    protected WSOCOLLECTION responses;
    @XmlElement(name = "CREATIONDATE", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar creationdate;

    /**
     * Gets the value of the author property.
     * 
     * @return
     *     possible object is
     *     {@link WSOMPERSON }
     *     
     */
    public WSOMPERSON getAUTHOR() {
        return author;
    }

    /**
     * Sets the value of the author property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSOMPERSON }
     *     
     */
    public void setAUTHOR(WSOMPERSON value) {
        this.author = value;
    }

    /**
     * Gets the value of the parentdocid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPARENTDOCID() {
        return parentdocid;
    }

    /**
     * Sets the value of the parentdocid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPARENTDOCID(String value) {
        this.parentdocid = value;
    }

    /**
     * Gets the value of the message property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMESSAGE() {
        return message;
    }

    /**
     * Sets the value of the message property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMESSAGE(String value) {
        this.message = value;
    }

    /**
     * Gets the value of the parentdiscussionid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPARENTDISCUSSIONID() {
        return parentdiscussionid;
    }

    /**
     * Sets the value of the parentdiscussionid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPARENTDISCUSSIONID(String value) {
        this.parentdiscussionid = value;
    }

    /**
     * Gets the value of the readers property.
     * 
     * @return
     *     possible object is
     *     {@link WSOCOLLECTION }
     *     
     */
    public WSOCOLLECTION getREADERS() {
        return readers;
    }

    /**
     * Sets the value of the readers property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSOCOLLECTION }
     *     
     */
    public void setREADERS(WSOCOLLECTION value) {
        this.readers = value;
    }

    /**
     * Gets the value of the responses property.
     * 
     * @return
     *     possible object is
     *     {@link WSOCOLLECTION }
     *     
     */
    public WSOCOLLECTION getRESPONSES() {
        return responses;
    }

    /**
     * Sets the value of the responses property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSOCOLLECTION }
     *     
     */
    public void setRESPONSES(WSOCOLLECTION value) {
        this.responses = value;
    }

    /**
     * Gets the value of the creationdate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCREATIONDATE() {
        return creationdate;
    }

    /**
     * Sets the value of the creationdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCREATIONDATE(XMLGregorianCalendar value) {
        this.creationdate = value;
    }

}
