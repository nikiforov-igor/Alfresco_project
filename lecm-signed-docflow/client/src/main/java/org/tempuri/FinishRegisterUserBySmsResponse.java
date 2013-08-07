
package org.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import ucloud.gate.proxy.exceptions.GateResponse;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FinishRegisterUserBySmsResult" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Exceptions}GateResponse" minOccurs="0"/>
 *         &lt;element name="organizationId" type="{http://schemas.microsoft.com/2003/10/Serialization/}guid" minOccurs="0"/>
 *         &lt;element name="organizationEdoId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "finishRegisterUserBySmsResult",
    "organizationId",
    "organizationEdoId"
})
@XmlRootElement(name = "FinishRegisterUserBySmsResponse")
public class FinishRegisterUserBySmsResponse {

    @XmlElement(name = "FinishRegisterUserBySmsResult", nillable = true)
    protected GateResponse finishRegisterUserBySmsResult;
    @XmlElement(nillable = true)
    protected String organizationId;
    @XmlElement(nillable = true)
    protected String organizationEdoId;

    /**
     * Gets the value of the finishRegisterUserBySmsResult property.
     * 
     * @return
     *     possible object is
     *     {@link GateResponse }
     *     
     */
    public GateResponse getFinishRegisterUserBySmsResult() {
        return finishRegisterUserBySmsResult;
    }

    /**
     * Sets the value of the finishRegisterUserBySmsResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link GateResponse }
     *     
     */
    public void setFinishRegisterUserBySmsResult(GateResponse value) {
        this.finishRegisterUserBySmsResult = value;
    }

    /**
     * Gets the value of the organizationId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrganizationId() {
        return organizationId;
    }

    /**
     * Sets the value of the organizationId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrganizationId(String value) {
        this.organizationId = value;
    }

    /**
     * Gets the value of the organizationEdoId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrganizationEdoId() {
        return organizationEdoId;
    }

    /**
     * Sets the value of the organizationEdoId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrganizationEdoId(String value) {
        this.organizationEdoId = value;
    }

}
