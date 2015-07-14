
package ru.it.lecm.mobile.objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for WSO_MISSIONLABEL complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSO_MISSIONLABEL">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:DefaultNamespace}WSOBJECT">
 *       &lt;sequence>
 *         &lt;element name="SUBJECT" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="DATEPLAN" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="DATESIGN" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="ISCONTROL" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="SIGNER" type="{urn:DefaultNamespace}WSO_MPERSON"/>
 *         &lt;element name="CONTROLLER" type="{urn:DefaultNamespace}WSO_MPERSON"/>
 *         &lt;element name="MAINEXECUTOR" type="{urn:DefaultNamespace}WSO_MPERSON"/>
 *         &lt;element name="EXECUTORS" type="{urn:DefaultNamespace}WSO_COLLECTION"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSO_MISSIONLABEL", propOrder = {
    "subject",
    "dateplan",
    "datesign",
    "iscontrol",
    "signer",
    "controller",
    "mainexecutor",
    "executors"
})
public class WSOMISSIONLABEL
    extends WSOBJECT
{

    @XmlElement(name = "SUBJECT", required = true, nillable = true)
    protected String subject;
    @XmlElement(name = "DATEPLAN", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateplan;
    @XmlElement(name = "DATESIGN", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar datesign;
    @XmlElement(name = "ISCONTROL", required = true, type = Boolean.class, nillable = true)
    protected Boolean iscontrol;
    @XmlElement(name = "SIGNER", required = true, nillable = true)
    protected WSOMPERSON signer;
    @XmlElement(name = "CONTROLLER", required = true, nillable = true)
    protected WSOMPERSON controller;
    @XmlElement(name = "MAINEXECUTOR", required = true, nillable = true)
    protected WSOMPERSON mainexecutor;
    @XmlElement(name = "EXECUTORS", required = true, nillable = true)
    protected WSOCOLLECTION executors;

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
     * Gets the value of the mainexecutor property.
     * 
     * @return
     *     possible object is
     *     {@link WSOMPERSON }
     *     
     */
    public WSOMPERSON getMAINEXECUTOR() {
        return mainexecutor;
    }

    /**
     * Sets the value of the mainexecutor property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSOMPERSON }
     *     
     */
    public void setMAINEXECUTOR(WSOMPERSON value) {
        this.mainexecutor = value;
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

}
