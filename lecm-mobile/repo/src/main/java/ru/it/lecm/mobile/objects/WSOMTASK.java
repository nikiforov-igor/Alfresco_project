
package ru.it.lecm.mobile.objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for WSO_MTASK complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSO_MTASK">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:DefaultNamespace}WSOBJECT">
 *       &lt;sequence>
 *         &lt;element name="DOCUMENT" type="{urn:DefaultNamespace}WSO_MDOCUMENT"/>
 *         &lt;element name="PARENT" type="{urn:DefaultNamespace}WSO_MTASK"/>
 *         &lt;element name="SUBJECT" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ISCONTROL" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="CHILDS" type="{urn:DefaultNamespace}WSO_COLLECTION"/>
 *         &lt;element name="DATEPLAN" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="SIGNER" type="{urn:DefaultNamespace}WSO_MPERSON"/>
 *         &lt;element name="CONTROLLER" type="{urn:DefaultNamespace}WSO_MPERSON"/>
 *         &lt;element name="MAINEXECUTORS" type="{urn:DefaultNamespace}WSO_COLLECTION"/>
 *         &lt;element name="EXECUTORS" type="{urn:DefaultNamespace}WSO_COLLECTION"/>
 *         &lt;element name="SUBEXECUTORS" type="{urn:DefaultNamespace}WSO_COLLECTION"/>
 *         &lt;element name="STATUSNAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="STATUSMOBILE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSO_MTASK", propOrder = {
    "document",
    "parent",
    "subject",
    "iscontrol",
    "childs",
    "dateplan",
    "signer",
    "controller",
    "mainexecutors",
    "executors",
    "subexecutors",
    "statusname",
    "statusmobile"
})
@XmlSeeAlso({
    WSOTASK.class
})
public class WSOMTASK
    extends WSOBJECT
{

    @XmlElement(name = "DOCUMENT", required = true, nillable = true)
    protected WSOMDOCUMENT document;
    @XmlElement(name = "PARENT", required = true, nillable = true)
    protected WSOMTASK parent;
    @XmlElement(name = "SUBJECT", required = true, nillable = true)
    protected String subject;
    @XmlElement(name = "ISCONTROL", required = true, type = Boolean.class, nillable = true)
    protected Boolean iscontrol;
    @XmlElement(name = "CHILDS", required = true, nillable = true)
    protected WSOCOLLECTION childs;
    @XmlElement(name = "DATEPLAN", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateplan;
    @XmlElement(name = "SIGNER", required = true, nillable = true)
    protected WSOMPERSON signer;
    @XmlElement(name = "CONTROLLER", required = true, nillable = true)
    protected WSOMPERSON controller;
    @XmlElement(name = "MAINEXECUTORS", required = true, nillable = true)
    protected WSOCOLLECTION mainexecutors;
    @XmlElement(name = "EXECUTORS", required = true, nillable = true)
    protected WSOCOLLECTION executors;
    @XmlElement(name = "SUBEXECUTORS", required = true, nillable = true)
    protected WSOCOLLECTION subexecutors;
    @XmlElement(name = "STATUSNAME", required = true, nillable = true)
    protected String statusname;
    @XmlElement(name = "STATUSMOBILE", required = true, nillable = true)
    protected String statusmobile;

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
     * Gets the value of the parent property.
     * 
     * @return
     *     possible object is
     *     {@link WSOMTASK }
     *     
     */
    public WSOMTASK getPARENT() {
        return parent;
    }

    /**
     * Sets the value of the parent property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSOMTASK }
     *     
     */
    public void setPARENT(WSOMTASK value) {
        this.parent = value;
    }

    /**
     * Gets the value of the subject property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSUBJECT() {
        return subject;
    }

    /**
     * Sets the value of the subject property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSUBJECT(String value) {
        this.subject = value;
    }

    /**
     * Gets the value of the iscontrol property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isISCONTROL() {
        return iscontrol;
    }

    /**
     * Sets the value of the iscontrol property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setISCONTROL(Boolean value) {
        this.iscontrol = value;
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
     * Gets the value of the dateplan property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDATEPLAN() {
        return dateplan;
    }

    /**
     * Sets the value of the dateplan property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDATEPLAN(XMLGregorianCalendar value) {
        this.dateplan = value;
    }

    /**
     * Gets the value of the signer property.
     * 
     * @return
     *     possible object is
     *     {@link WSOMPERSON }
     *     
     */
    public WSOMPERSON getSIGNER() {
        return signer;
    }

    /**
     * Sets the value of the signer property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSOMPERSON }
     *     
     */
    public void setSIGNER(WSOMPERSON value) {
        this.signer = value;
    }

    /**
     * Gets the value of the controller property.
     * 
     * @return
     *     possible object is
     *     {@link WSOMPERSON }
     *     
     */
    public WSOMPERSON getCONTROLLER() {
        return controller;
    }

    /**
     * Sets the value of the controller property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSOMPERSON }
     *     
     */
    public void setCONTROLLER(WSOMPERSON value) {
        this.controller = value;
    }

    /**
     * Gets the value of the mainexecutors property.
     * 
     * @return
     *     possible object is
     *     {@link WSOCOLLECTION }
     *     
     */
    public WSOCOLLECTION getMAINEXECUTORS() {
        return mainexecutors;
    }

    /**
     * Sets the value of the mainexecutors property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSOCOLLECTION }
     *     
     */
    public void setMAINEXECUTORS(WSOCOLLECTION value) {
        this.mainexecutors = value;
    }

    /**
     * Gets the value of the executors property.
     * 
     * @return
     *     possible object is
     *     {@link WSOCOLLECTION }
     *     
     */
    public WSOCOLLECTION getEXECUTORS() {
        return executors;
    }

    /**
     * Sets the value of the executors property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSOCOLLECTION }
     *     
     */
    public void setEXECUTORS(WSOCOLLECTION value) {
        this.executors = value;
    }

    /**
     * Gets the value of the subexecutors property.
     * 
     * @return
     *     possible object is
     *     {@link WSOCOLLECTION }
     *     
     */
    public WSOCOLLECTION getSUBEXECUTORS() {
        return subexecutors;
    }

    /**
     * Sets the value of the subexecutors property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSOCOLLECTION }
     *     
     */
    public void setSUBEXECUTORS(WSOCOLLECTION value) {
        this.subexecutors = value;
    }

    /**
     * Gets the value of the statusname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSTATUSNAME() {
        return statusname;
    }

    /**
     * Sets the value of the statusname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSTATUSNAME(String value) {
        this.statusname = value;
    }

    /**
     * Gets the value of the statusmobile property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSTATUSMOBILE() {
        return statusmobile;
    }

    /**
     * Sets the value of the statusmobile property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSTATUSMOBILE(String value) {
        this.statusmobile = value;
    }

}
