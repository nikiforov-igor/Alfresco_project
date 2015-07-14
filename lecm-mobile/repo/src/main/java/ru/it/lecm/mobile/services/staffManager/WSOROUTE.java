
package ru.it.lecm.mobile.services.staffManager;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSO_ROUTE complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSO_ROUTE">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:DefaultNamespace}WSOBJECT">
 *       &lt;sequence>
 *         &lt;element name="SETUPDOC" type="{urn:DefaultNamespace}WSO_DOCUMENT"/>
 *         &lt;element name="APPROVAL" type="{urn:DefaultNamespace}WSO_APPROVAL"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSO_ROUTE", propOrder = {
    "setupdoc",
    "approval"
})
public class WSOROUTE
    extends WSOBJECT
{

    @XmlElement(name = "SETUPDOC", required = true, nillable = true)
    protected WSODOCUMENT setupdoc;
    @XmlElement(name = "APPROVAL", required = true, nillable = true)
    protected WSOAPPROVAL approval;

    /**
     * Gets the value of the setupdoc property.
     * 
     * @return
     *     possible object is
     *     {@link WSODOCUMENT }
     *     
     */
    public WSODOCUMENT getSETUPDOC() {
        return setupdoc;
    }

    /**
     * Sets the value of the setupdoc property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSODOCUMENT }
     *     
     */
    public void setSETUPDOC(WSODOCUMENT value) {
        this.setupdoc = value;
    }

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

}
