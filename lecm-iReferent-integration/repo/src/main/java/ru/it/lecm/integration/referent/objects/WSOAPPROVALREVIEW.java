
package ru.it.lecm.integration.referent.objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for WSOAPPROVALREVIEW complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSOAPPROVALREVIEW">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:DefaultNamespace}WSOMAPPROVALREVIEW">
 *       &lt;sequence>
 *         &lt;element name="APPROVAL" type="{urn:DefaultNamespace}WSO_MAPPROVAL"/>
 *         &lt;element name="REVIEWER" type="{urn:DefaultNamespace}WSO_MPERSON"/>
 *         &lt;element name="DEADLINEDATE" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="BEGINDATE" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="REVIEWDATE" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="SOLUTION" type="{urn:DefaultNamespace}WSOAPPROVALREVIEWSOLUTION"/>
 *         &lt;element name="ATTACHMENTS" type="{urn:DefaultNamespace}WSO_COLLECTION"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSOAPPROVALREVIEW", propOrder = {
    "approval",
    "reviewer",
    "deadlinedate",
    "begindate",
    "reviewdate",
    "solution",
    "attachments"
})
public class WSOAPPROVALREVIEW
    extends WSOMAPPROVALREVIEW
{

    @XmlElement(name = "APPROVAL", required = true, nillable = true)
    protected WSOMAPPROVAL approval;
    @XmlElement(name = "REVIEWER", required = true, nillable = true)
    protected WSOMPERSON reviewer;
    @XmlElement(name = "DEADLINEDATE", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar deadlinedate;
    @XmlElement(name = "BEGINDATE", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar begindate;
    @XmlElement(name = "REVIEWDATE", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar reviewdate;
    @XmlElement(name = "SOLUTION", required = true, nillable = true)
    protected WSOAPPROVALREVIEWSOLUTION solution;
    @XmlElement(name = "ATTACHMENTS", required = true, nillable = true)
    protected WSOCOLLECTION attachments;

    /**
     * Gets the value of the approval property.
     * 
     * @return
     *     possible object is
     *     {@link WSOMAPPROVAL }
     *     
     */
    public WSOMAPPROVAL getAPPROVAL() {
        return approval;
    }

    /**
     * Sets the value of the approval property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSOMAPPROVAL }
     *     
     */
    public void setAPPROVAL(WSOMAPPROVAL value) {
        this.approval = value;
    }

    /**
     * Gets the value of the reviewer property.
     * 
     * @return
     *     possible object is
     *     {@link WSOMPERSON }
     *     
     */
    public WSOMPERSON getREVIEWER() {
        return reviewer;
    }

    /**
     * Sets the value of the reviewer property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSOMPERSON }
     *     
     */
    public void setREVIEWER(WSOMPERSON value) {
        this.reviewer = value;
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
     * Gets the value of the begindate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getBEGINDATE() {
        return begindate;
    }

    /**
     * Sets the value of the begindate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setBEGINDATE(XMLGregorianCalendar value) {
        this.begindate = value;
    }

    /**
     * Gets the value of the reviewdate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getREVIEWDATE() {
        return reviewdate;
    }

    /**
     * Sets the value of the reviewdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setREVIEWDATE(XMLGregorianCalendar value) {
        this.reviewdate = value;
    }

    /**
     * Gets the value of the solution property.
     * 
     * @return
     *     possible object is
     *     {@link WSOAPPROVALREVIEWSOLUTION }
     *     
     */
    public WSOAPPROVALREVIEWSOLUTION getSOLUTION() {
        return solution;
    }

    /**
     * Sets the value of the solution property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSOAPPROVALREVIEWSOLUTION }
     *     
     */
    public void setSOLUTION(WSOAPPROVALREVIEWSOLUTION value) {
        this.solution = value;
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

}
