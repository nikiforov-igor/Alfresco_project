
package org.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import ucloud.gate.proxy.ArrayOfOperatorInfo;
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
 *         &lt;element name="GetOperatorsResult" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Exceptions}GateResponse" minOccurs="0"/>
 *         &lt;element name="operatorInfos" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy}ArrayOfOperatorInfo" minOccurs="0"/>
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
    "getOperatorsResult",
    "operatorInfos"
})
@XmlRootElement(name = "GetOperatorsResponse")
public class GetOperatorsResponse {

    @XmlElement(name = "GetOperatorsResult", nillable = true)
    protected GateResponse getOperatorsResult;
    @XmlElement(nillable = true)
    protected ArrayOfOperatorInfo operatorInfos;

    /**
     * Gets the value of the getOperatorsResult property.
     * 
     * @return
     *     possible object is
     *     {@link GateResponse }
     *     
     */
    public GateResponse getGetOperatorsResult() {
        return getOperatorsResult;
    }

    /**
     * Sets the value of the getOperatorsResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link GateResponse }
     *     
     */
    public void setGetOperatorsResult(GateResponse value) {
        this.getOperatorsResult = value;
    }

    /**
     * Gets the value of the operatorInfos property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfOperatorInfo }
     *     
     */
    public ArrayOfOperatorInfo getOperatorInfos() {
        return operatorInfos;
    }

    /**
     * Sets the value of the operatorInfos property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfOperatorInfo }
     *     
     */
    public void setOperatorInfos(ArrayOfOperatorInfo value) {
        this.operatorInfos = value;
    }

}
