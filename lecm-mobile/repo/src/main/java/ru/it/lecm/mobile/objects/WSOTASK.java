
package ru.it.lecm.mobile.objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for WSO_TASK complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSO_TASK">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:DefaultNamespace}WSO_MTASK">
 *       &lt;sequence>
 *         &lt;element name="COMMENTS" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ATTACHMENTS" type="{urn:DefaultNamespace}WSO_COLLECTION"/>
 *         &lt;element name="VIEWERS" type="{urn:DefaultNamespace}WSO_COLLECTION"/>
 *         &lt;element name="DATEFACT" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="DATESIGN" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSO_TASK", propOrder = {
    "comments",
    "attachments",
    "viewers",
    "datefact",
    "datesign"
})
public class WSOTASK
    extends WSOMTASK
{

    @XmlElement(name = "COMMENTS", required = true, nillable = true)
    protected String comments;
    @XmlElement(name = "ATTACHMENTS", required = true, nillable = true)
    protected WSOCOLLECTION attachments;
    @XmlElement(name = "VIEWERS", required = true, nillable = true)
    protected WSOCOLLECTION viewers;
    @XmlElement(name = "DATEFACT", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar datefact;
    @XmlElement(name = "DATESIGN", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar datesign;

    /**
     * Gets the value of the comments property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCOMMENTS() {
        return comments;
    }

    /**
     * Sets the value of the comments property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCOMMENTS(String value) {
        this.comments = value;
    }

    /**
     * Gets the value of the attachments property.
     * 
     * @return
     *     possible object is
     *     {@link WSOCOLLECTION }
     *     
     */
    public WSOCOLLECTION getATTACHMENTS() {
        return attachments;
    }

    /**
     * Sets the value of the attachments property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSOCOLLECTION }
     *     
     */
    public void setATTACHMENTS(WSOCOLLECTION value) {
        this.attachments = value;
    }

    /**
     * Gets the value of the viewers property.
     * 
     * @return
     *     possible object is
     *     {@link WSOCOLLECTION }
     *     
     */
    public WSOCOLLECTION getVIEWERS() {
        return viewers;
    }

    /**
     * Sets the value of the viewers property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSOCOLLECTION }
     *     
     */
    public void setVIEWERS(WSOCOLLECTION value) {
        this.viewers = value;
    }

    /**
     * Gets the value of the datefact property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDATEFACT() {
        return datefact;
    }

    /**
     * Sets the value of the datefact property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDATEFACT(XMLGregorianCalendar value) {
        this.datefact = value;
    }

    /**
     * Gets the value of the datesign property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDATESIGN() {
        return datesign;
    }

    /**
     * Sets the value of the datesign property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDATESIGN(XMLGregorianCalendar value) {
        this.datesign = value;
    }

}
