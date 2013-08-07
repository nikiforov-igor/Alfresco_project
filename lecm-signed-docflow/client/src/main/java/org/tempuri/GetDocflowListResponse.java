
package org.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import ucloud.gate.proxy.ArrayOfDocflowInfoBase;
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
 *         &lt;element name="GetDocflowListResult" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Exceptions}GateResponse" minOccurs="0"/>
 *         &lt;element name="docflows" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy}ArrayOfDocflowInfoBase" minOccurs="0"/>
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
    "getDocflowListResult",
    "docflows"
})
@XmlRootElement(name = "GetDocflowListResponse")
public class GetDocflowListResponse {

    @XmlElement(name = "GetDocflowListResult", nillable = true)
    protected GateResponse getDocflowListResult;
    @XmlElement(nillable = true)
    protected ArrayOfDocflowInfoBase docflows;

    /**
     * Gets the value of the getDocflowListResult property.
     * 
     * @return
     *     possible object is
     *     {@link GateResponse }
     *     
     */
    public GateResponse getGetDocflowListResult() {
        return getDocflowListResult;
    }

    /**
     * Sets the value of the getDocflowListResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link GateResponse }
     *     
     */
    public void setGetDocflowListResult(GateResponse value) {
        this.getDocflowListResult = value;
    }

    /**
     * Gets the value of the docflows property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfDocflowInfoBase }
     *     
     */
    public ArrayOfDocflowInfoBase getDocflows() {
        return docflows;
    }

    /**
     * Sets the value of the docflows property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfDocflowInfoBase }
     *     
     */
    public void setDocflows(ArrayOfDocflowInfoBase value) {
        this.docflows = value;
    }

}
