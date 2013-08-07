
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
 *         &lt;element name="CheckIfPersonIsEmployeeByCertificateResult" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Exceptions}GateResponse" minOccurs="0"/>
 *         &lt;element name="isEmployee" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
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
    "checkIfPersonIsEmployeeByCertificateResult",
    "isEmployee"
})
@XmlRootElement(name = "CheckIfPersonIsEmployeeByCertificateResponse")
public class CheckIfPersonIsEmployeeByCertificateResponse {

    @XmlElement(name = "CheckIfPersonIsEmployeeByCertificateResult", nillable = true)
    protected GateResponse checkIfPersonIsEmployeeByCertificateResult;
    protected Boolean isEmployee;

    /**
     * Gets the value of the checkIfPersonIsEmployeeByCertificateResult property.
     * 
     * @return
     *     possible object is
     *     {@link GateResponse }
     *     
     */
    public GateResponse getCheckIfPersonIsEmployeeByCertificateResult() {
        return checkIfPersonIsEmployeeByCertificateResult;
    }

    /**
     * Sets the value of the checkIfPersonIsEmployeeByCertificateResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link GateResponse }
     *     
     */
    public void setCheckIfPersonIsEmployeeByCertificateResult(GateResponse value) {
        this.checkIfPersonIsEmployeeByCertificateResult = value;
    }

    /**
     * Gets the value of the isEmployee property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsEmployee() {
        return isEmployee;
    }

    /**
     * Sets the value of the isEmployee property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsEmployee(Boolean value) {
        this.isEmployee = value;
    }

}
