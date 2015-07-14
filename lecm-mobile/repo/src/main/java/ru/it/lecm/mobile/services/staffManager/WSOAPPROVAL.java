
package ru.it.lecm.mobile.services.staffManager;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for WSO_APPROVAL complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSO_APPROVAL">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:DefaultNamespace}WSO_MAPPROVAL">
 *       &lt;sequence>
 *         &lt;element name="COMMENT" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="DOCUMENT" type="{urn:DefaultNamespace}WSO_MDOCUMENT"/>
 *         &lt;element name="DEADLINEDATE" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="APPROVALREVIEWS" type="{urn:DefaultNamespace}WSO_COLLECTION"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSO_APPROVAL", propOrder = {
    "comment",
    "document",
    "deadlinedate",
    "approvalreviews"
})
public class WSOAPPROVAL
    extends WSOMAPPROVAL
{

    @XmlElement(name = "COMMENT", required = true, nillable = true)
    protected String comment;
    @XmlElement(name = "DOCUMENT", required = true, nillable = true)
    protected WSOMDOCUMENT document;
    @XmlElement(name = "DEADLINEDATE", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar deadlinedate;
    @XmlElement(name = "APPROVALREVIEWS", required = true, nillable = true)
    protected WSOCOLLECTION approvalreviews;

    /**
     * Gets the value of the comment property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCOMMENT() {
        return comment;
    }

    /**
     * Sets the value of the comment property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCOMMENT(String value) {
        this.comment = value;
    }

    /**
     * Gets the value of the document property.
     * 
     * @return
     *     possible object is
     *     {@link WSOMDOCUMENT }
     *     
     */
    public WSOMDOCUMENT getDOCUMENT() {
        return document;
    }

    /**
     * Sets the value of the document property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSOMDOCUMENT }
     *     
     */
    public void setDOCUMENT(WSOMDOCUMENT value) {
        this.document = value;
    }

    /**
     * Gets the value of the deadlinedate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDEADLINEDATE() {
        return deadlinedate;
    }

    /**
     * Sets the value of the deadlinedate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDEADLINEDATE(XMLGregorianCalendar value) {
        this.deadlinedate = value;
    }

    /**
     * Gets the value of the approvalreviews property.
     * 
     * @return
     *     possible object is
     *     {@link WSOCOLLECTION }
     *     
     */
    public WSOCOLLECTION getAPPROVALREVIEWS() {
        return approvalreviews;
    }

    /**
     * Sets the value of the approvalreviews property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSOCOLLECTION }
     *     
     */
    public void setAPPROVALREVIEWS(WSOCOLLECTION value) {
        this.approvalreviews = value;
    }

}
