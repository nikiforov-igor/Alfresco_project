
package org.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import com.microsoft.schemas.serialization.arrays.ArrayOfstring;
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
 *         &lt;element name="GetMutualOperatorsResult" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Exceptions}GateResponse" minOccurs="0"/>
 *         &lt;element name="operatorCodes" type="{http://schemas.microsoft.com/2003/10/Serialization/Arrays}ArrayOfstring" minOccurs="0"/>
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
    "getMutualOperatorsResult",
    "operatorCodes"
})
@XmlRootElement(name = "GetMutualOperatorsResponse")
public class GetMutualOperatorsResponse {

    @XmlElement(name = "GetMutualOperatorsResult", nillable = true)
    protected GateResponse getMutualOperatorsResult;
    @XmlElement(nillable = true)
    protected ArrayOfstring operatorCodes;

    /**
     * Gets the value of the getMutualOperatorsResult property.
     * 
     * @return
     *     possible object is
     *     {@link GateResponse }
     *     
     */
    public GateResponse getGetMutualOperatorsResult() {
        return getMutualOperatorsResult;
    }

    /**
     * Sets the value of the getMutualOperatorsResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link GateResponse }
     *     
     */
    public void setGetMutualOperatorsResult(GateResponse value) {
        this.getMutualOperatorsResult = value;
    }

    /**
     * Gets the value of the operatorCodes property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfstring }
     *     
     */
    public ArrayOfstring getOperatorCodes() {
        return operatorCodes;
    }

    /**
     * Sets the value of the operatorCodes property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfstring }
     *     
     */
    public void setOperatorCodes(ArrayOfstring value) {
        this.operatorCodes = value;
    }

}
