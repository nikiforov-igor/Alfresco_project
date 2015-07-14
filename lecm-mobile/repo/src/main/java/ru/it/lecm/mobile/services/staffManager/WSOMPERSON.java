
package ru.it.lecm.mobile.services.staffManager;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSO_MPERSON complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSO_MPERSON">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:DefaultNamespace}WSO_STAFFOBJECT">
 *       &lt;sequence>
 *         &lt;element name="ISACTIVE" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSO_MPERSON", propOrder = {
    "isactive"
})
@XmlSeeAlso({
    WSOPERSON.class
})
public class WSOMPERSON
    extends WSOSTAFFOBJECT
{

    @XmlElement(name = "ISACTIVE", required = true, type = Boolean.class, nillable = true)
    protected Boolean isactive;

    /**
     * Gets the value of the isactive property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isISACTIVE() {
        return isactive;
    }

    /**
     * Sets the value of the isactive property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setISACTIVE(Boolean value) {
        this.isactive = value;
    }

}
