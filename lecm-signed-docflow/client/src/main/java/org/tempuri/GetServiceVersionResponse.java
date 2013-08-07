
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
 *         &lt;element name="GetServiceVersionResult" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Exceptions}GateResponse" minOccurs="0"/>
 *         &lt;element name="version" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "getServiceVersionResult",
    "version"
})
@XmlRootElement(name = "GetServiceVersionResponse")
public class GetServiceVersionResponse {

    @XmlElement(name = "GetServiceVersionResult", nillable = true)
    protected GateResponse getServiceVersionResult;
    @XmlElement(nillable = true)
    protected String version;

    /**
     * Gets the value of the getServiceVersionResult property.
     * 
     * @return
     *     possible object is
     *     {@link GateResponse }
     *     
     */
    public GateResponse getGetServiceVersionResult() {
        return getServiceVersionResult;
    }

    /**
     * Sets the value of the getServiceVersionResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link GateResponse }
     *     
     */
    public void setGetServiceVersionResult(GateResponse value) {
        this.getServiceVersionResult = value;
    }

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersion(String value) {
        this.version = value;
    }

}
