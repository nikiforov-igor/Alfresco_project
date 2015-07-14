
package ru.it.lecm.mobile.objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSO_LINK complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSO_LINK">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:DefaultNamespace}WSOBJECT">
 *       &lt;sequence>
 *         &lt;element name="SOURCE" type="{urn:DefaultNamespace}WSO_MDOCUMENT"/>
 *         &lt;element name="TARGET" type="{urn:DefaultNamespace}WSO_MDOCUMENT"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSO_LINK", propOrder = {
    "source",
    "target"
})
public class WSOLINK
    extends WSOBJECT
{

    @XmlElement(name = "SOURCE", required = true, nillable = true)
    protected WSOMDOCUMENT source;
    @XmlElement(name = "TARGET", required = true, nillable = true)
    protected WSOMDOCUMENT target;

    /**
     * Gets the value of the source property.
     * 
     * @return
     *     possible object is
     *     {@link WSOMDOCUMENT }
     *     
     */
    public WSOMDOCUMENT getSOURCE() {
        return source;
    }

    /**
     * Sets the value of the source property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSOMDOCUMENT }
     *     
     */
    public void setSOURCE(WSOMDOCUMENT value) {
        this.source = value;
    }

    /**
     * Gets the value of the target property.
     * 
     * @return
     *     possible object is
     *     {@link WSOMDOCUMENT }
     *     
     */
    public WSOMDOCUMENT getTARGET() {
        return target;
    }

    /**
     * Sets the value of the target property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSOMDOCUMENT }
     *     
     */
    public void setTARGET(WSOMDOCUMENT value) {
        this.target = value;
    }

}
