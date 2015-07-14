
package ru.it.lecm.mobile.services.staffManager;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for WSO_APPROVALREVIEW complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSO_APPROVALREVIEW">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:DefaultNamespace}WSO_MAPPROVALREVIEW">
 *       &lt;sequence>
 *         &lt;element name="APPROVAL" type="{urn:DefaultNamespace}WSO_APPROVAL"/>
 *         &lt;element name="REVIEWER" type="{urn:DefaultNamespace}WSO_MPERSON"/>
 *         &lt;element name="DEADLINEDATE" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="BEGINDATE" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="ANSWERDATE" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="SOLUTION" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="REMARK" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="STAGE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ATTACHMENTS" type="{urn:DefaultNamespace}WSO_COLLECTION"/>
 *         &lt;element name="STATUS" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="BLOCKNAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="BLOCKTYPE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CHILDS" type="{urn:DefaultNamespace}WSO_COLLECTION"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSO_APPROVALREVIEW", propOrder = {
    "approval",
    "reviewer",
    "deadlinedate",
    "begindate",
    "answerdate",
    "solution",
    "remark",
    "stage",
    "attachments",
    "status",
    "blockname",
    "blocktype",
    "childs"
})
public class WSOAPPROVALREVIEW
    extends WSOMAPPROVALREVIEW
{

    @XmlElement(name = "APPROVAL", required = true, nillable = true)
    protected WSOAPPROVAL approval;
    @XmlElement(name = "REVIEWER", required = true, nillable = true)
    protected WSOMPERSON reviewer;
    @XmlElement(name = "DEADLINEDATE", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar deadlinedate;
    @XmlElement(name = "BEGINDATE", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar begindate;
    @XmlElement(name = "ANSWERDATE", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar answerdate;
    @XmlElement(name = "SOLUTION", required = true, nillable = true)
    protected String solution;
    @XmlElement(name = "REMARK", required = true, nillable = true)
    protected String remark;
    @XmlElement(name = "STAGE", required = true, nillable = true)
    protected String stage;
    @XmlElement(name = "ATTACHMENTS", required = true, nillable = true)
    protected WSOCOLLECTION attachments;
    @XmlElement(name = "STATUS", required = true, nillable = true)
    protected String status;
    @XmlElement(name = "BLOCKNAME", required = true, nillable = true)
    protected String blockname;
    @XmlElement(name = "BLOCKTYPE", required = true, nillable = true)
    protected String blocktype;
    @XmlElement(name = "CHILDS", required = true, nillable = true)
    protected WSOCOLLECTION childs;

    /**
     * Gets the value of the approval property.
     * 
     * @return
     *     possible object is
     *     {@link WSOAPPROVAL }
     *     
     */
    public WSOAPPROVAL getAPPROVAL() {
        return approval;
    }

    /**
     * Sets the value of the approval property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSOAPPROVAL }
     *     
     */
    public void setAPPROVAL(WSOAPPROVAL value) {
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
     * Gets the value of the answerdate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getANSWERDATE() {
        return answerdate;
    }

    /**
     * Sets the value of the answerdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setANSWERDATE(XMLGregorianCalendar value) {
        this.answerdate = value;
    }

    /**
     * Gets the value of the solution property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSOLUTION() {
        return solution;
    }

    /**
     * Sets the value of the solution property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSOLUTION(String value) {
        this.solution = value;
    }

    /**
     * Gets the value of the remark property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getREMARK() {
        return remark;
    }

    /**
     * Sets the value of the remark property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setREMARK(String value) {
        this.remark = value;
    }

    /**
     * Gets the value of the stage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSTAGE() {
        return stage;
    }

    /**
     * Sets the value of the stage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSTAGE(String value) {
        this.stage = value;
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
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSTATUS() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSTATUS(String value) {
        this.status = value;
    }

    /**
     * Gets the value of the blockname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBLOCKNAME() {
        return blockname;
    }

    /**
     * Sets the value of the blockname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBLOCKNAME(String value) {
        this.blockname = value;
    }

    /**
     * Gets the value of the blocktype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBLOCKTYPE() {
        return blocktype;
    }

    /**
     * Sets the value of the blocktype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBLOCKTYPE(String value) {
        this.blocktype = value;
    }

    /**
     * Gets the value of the childs property.
     * 
     * @return
     *     possible object is
     *     {@link WSOCOLLECTION }
     *     
     */
    public WSOCOLLECTION getCHILDS() {
        return childs;
    }

    /**
     * Sets the value of the childs property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSOCOLLECTION }
     *     
     */
    public void setCHILDS(WSOCOLLECTION value) {
        this.childs = value;
    }

}
