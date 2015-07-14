
package ru.it.lecm.mobile.objects;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for WSO_DOCUMENTVISA complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSO_DOCUMENTVISA">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:DefaultNamespace}WSO_MDOCUMENTVISA">
 *       &lt;sequence>
 *         &lt;element name="DOCUMENT" type="{urn:DefaultNamespace}WSO_MDOCUMENT"/>
 *         &lt;element name="APPVERSION" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="ACTIVITYNAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="BLOCKNUM" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="BLOCKNAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="REVIEWERNAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="REVIEWER" type="{urn:DefaultNamespace}WSO_MPERSON"/>
 *         &lt;element name="REALREVIEWER" type="{urn:DefaultNamespace}WSO_MPERSON"/>
 *         &lt;element name="INDATE" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="SIGNDATE" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="DECISION" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="COMMENT" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="HASATTACH" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="STATUS" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="CHILDS" type="{urn:DefaultNamespace}WSO_COLLECTION"/>
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
@XmlType(name = "WSO_DOCUMENTVISA", propOrder = {
    "document",
    "appversion",
    "activityname",
    "blocknum",
    "blockname",
    "reviewername",
    "reviewer",
    "realreviewer",
    "indate",
    "signdate",
    "decision",
    "comment",
    "hasattach",
    "status",
    "childs",
    "attachments"
})
public class WSODOCUMENTVISA
    extends WSOMDOCUMENTVISA
{

    @XmlElement(name = "DOCUMENT", required = true, nillable = true)
    protected WSOMDOCUMENT document;
    @XmlElement(name = "APPVERSION", required = true, nillable = true)
    protected BigInteger appversion;
    @XmlElement(name = "ACTIVITYNAME", required = true, nillable = true)
    protected String activityname;
    @XmlElement(name = "BLOCKNUM", required = true, nillable = true)
    protected BigInteger blocknum;
    @XmlElement(name = "BLOCKNAME", required = true, nillable = true)
    protected String blockname;
    @XmlElement(name = "REVIEWERNAME", required = true, nillable = true)
    protected String reviewername;
    @XmlElement(name = "REVIEWER", required = true, nillable = true)
    protected WSOMPERSON reviewer;
    @XmlElement(name = "REALREVIEWER", required = true, nillable = true)
    protected WSOMPERSON realreviewer;
    @XmlElement(name = "INDATE", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar indate;
    @XmlElement(name = "SIGNDATE", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar signdate;
    @XmlElement(name = "DECISION", required = true, nillable = true)
    protected String decision;
    @XmlElement(name = "COMMENT", required = true, nillable = true)
    protected String comment;
    @XmlElement(name = "HASATTACH", required = true, type = Boolean.class, nillable = true)
    protected Boolean hasattach;
    @XmlElement(name = "STATUS", required = true, nillable = true)
    protected BigInteger status;
    @XmlElement(name = "CHILDS", required = true, nillable = true)
    protected WSOCOLLECTION childs;
    @XmlElement(name = "ATTACHMENTS", required = true, nillable = true)
    protected WSOCOLLECTION attachments;

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
     * Gets the value of the appversion property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getAPPVERSION() {
        return appversion;
    }

    /**
     * Sets the value of the appversion property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setAPPVERSION(BigInteger value) {
        this.appversion = value;
    }

    /**
     * Gets the value of the activityname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getACTIVITYNAME() {
        return activityname;
    }

    /**
     * Sets the value of the activityname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setACTIVITYNAME(String value) {
        this.activityname = value;
    }

    /**
     * Gets the value of the blocknum property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getBLOCKNUM() {
        return blocknum;
    }

    /**
     * Sets the value of the blocknum property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setBLOCKNUM(BigInteger value) {
        this.blocknum = value;
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
     * Gets the value of the reviewername property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getREVIEWERNAME() {
        return reviewername;
    }

    /**
     * Sets the value of the reviewername property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setREVIEWERNAME(String value) {
        this.reviewername = value;
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
     * Gets the value of the realreviewer property.
     * 
     * @return
     *     possible object is
     *     {@link WSOMPERSON }
     *     
     */
    public WSOMPERSON getREALREVIEWER() {
        return realreviewer;
    }

    /**
     * Sets the value of the realreviewer property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSOMPERSON }
     *     
     */
    public void setREALREVIEWER(WSOMPERSON value) {
        this.realreviewer = value;
    }

    /**
     * Gets the value of the indate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getINDATE() {
        return indate;
    }

    /**
     * Sets the value of the indate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setINDATE(XMLGregorianCalendar value) {
        this.indate = value;
    }

    /**
     * Gets the value of the signdate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getSIGNDATE() {
        return signdate;
    }

    /**
     * Sets the value of the signdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setSIGNDATE(XMLGregorianCalendar value) {
        this.signdate = value;
    }

    /**
     * Gets the value of the decision property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDECISION() {
        return decision;
    }

    /**
     * Sets the value of the decision property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDECISION(String value) {
        this.decision = value;
    }

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
     * Gets the value of the hasattach property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isHASATTACH() {
        return hasattach;
    }

    /**
     * Sets the value of the hasattach property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setHASATTACH(Boolean value) {
        this.hasattach = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSTATUS() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSTATUS(BigInteger value) {
        this.status = value;
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
