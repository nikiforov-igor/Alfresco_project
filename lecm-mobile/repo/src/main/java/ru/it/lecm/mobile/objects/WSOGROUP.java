
package ru.it.lecm.mobile.objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSO_GROUP complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSO_GROUP">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:DefaultNamespace}WSO_MGROUP">
 *       &lt;sequence>
 *         &lt;element name="LEADER" type="{urn:DefaultNamespace}WSO_MPERSON"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSO_GROUP", propOrder = {
    "leader"
})
public class WSOGROUP
    extends WSOMGROUP
{

    @XmlElement(name = "LEADER", required = true, nillable = true)
    protected WSOMPERSON leader;

    /**
     * Gets the value of the leader property.
     * 
     * @return
     *     possible object is
     *     {@link WSOMPERSON }
     *     
     */
    public WSOMPERSON getLEADER() {
        return leader;
    }

    /**
     * Sets the value of the leader property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSOMPERSON }
     *     
     */
    public void setLEADER(WSOMPERSON value) {
        this.leader = value;
    }

}
