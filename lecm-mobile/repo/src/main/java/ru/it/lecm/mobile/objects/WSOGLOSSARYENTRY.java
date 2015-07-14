
package ru.it.lecm.mobile.objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSO_GLOSSARYENTRY complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSO_GLOSSARYENTRY">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:DefaultNamespace}WSOBJECT">
 *       &lt;sequence>
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
@XmlType(name = "WSO_GLOSSARYENTRY", propOrder = {
    "childs"
})
public class WSOGLOSSARYENTRY
    extends WSOBJECT
{

    @XmlElement(name = "CHILDS", required = true, nillable = true)
    protected WSOCOLLECTION childs;

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
