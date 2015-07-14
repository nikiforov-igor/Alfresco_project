
package ru.it.lecm.mobile.objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSO_EDS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSO_EDS">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SIGNATURE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="WHO" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="WHEN" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="FIELDS" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="STAGE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSO_EDS", propOrder = {
    "signature",
    "who",
    "when",
    "fields",
    "stage"
})
public class WSOEDS {

    @XmlElement(name = "SIGNATURE", required = true, nillable = true)
    protected String signature;
    @XmlElement(name = "WHO", required = true, nillable = true)
    protected String who;
    @XmlElement(name = "WHEN", required = true, nillable = true)
    protected String when;
    @XmlElement(name = "FIELDS", required = true, nillable = true)
    protected String fields;
    @XmlElement(name = "STAGE", required = true, nillable = true)
    protected String stage;

    /**
     * Gets the value of the signature property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSIGNATURE() {
        return signature;
    }

    /**
     * Sets the value of the signature property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSIGNATURE(String value) {
        this.signature = value;
    }

    /**
     * Gets the value of the who property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWHO() {
        return who;
    }

    /**
     * Sets the value of the who property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWHO(String value) {
        this.who = value;
    }

    /**
     * Gets the value of the when property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWHEN() {
        return when;
    }

    /**
     * Sets the value of the when property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWHEN(String value) {
        this.when = value;
    }

    /**
     * Gets the value of the fields property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFIELDS() {
        return fields;
    }

    /**
     * Sets the value of the fields property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFIELDS(String value) {
        this.fields = value;
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

}
