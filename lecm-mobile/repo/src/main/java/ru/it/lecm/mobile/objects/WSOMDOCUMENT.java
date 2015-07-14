
package ru.it.lecm.mobile.objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for WSO_MDOCUMENT complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSO_MDOCUMENT">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:DefaultNamespace}WSOBJECT">
 *       &lt;sequence>
 *         &lt;element name="REGNUM" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="REGDATE" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="SUBJECT" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
@XmlType(name = "WSO_MDOCUMENT", propOrder = {
    "regnum",
    "regdate",
    "subject",
    "statusname",
    "statusmobile"
})
@XmlSeeAlso({
    WSOMDOCUMENTOG.class,
    WSOMDOCUMENTOGR.class,
    WSOMDOCUMENTOUT.class,
    WSOMDOCUMENTOUTDRAFT.class,
    WSODOCUMENT.class,
    WSOMDOCUMENTIN.class,
    WSOMDOCUMENTORD.class
})
public class WSOMDOCUMENT
    extends WSOBJECT
{

    @XmlElement(name = "REGNUM", required = true, nillable = true)
    protected String regnum;
    @XmlElement(name = "REGDATE", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar regdate;
    @XmlElement(name = "SUBJECT", required = true, nillable = true)
    protected String subject;
    @XmlElement(name = "STATUSNAME", required = true, nillable = true)
    protected String statusname;
    @XmlElement(name = "STATUSMOBILE", required = true, nillable = true)
    protected String statusmobile;

    /**
     * Gets the value of the regnum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getREGNUM() {
        return regnum;
    }

    /**
     * Sets the value of the regnum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setREGNUM(String value) {
        this.regnum = value;
    }

    /**
     * Gets the value of the regdate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getREGDATE() {
        return regdate;
    }

    /**
     * Sets the value of the regdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setREGDATE(XMLGregorianCalendar value) {
        this.regdate = value;
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
