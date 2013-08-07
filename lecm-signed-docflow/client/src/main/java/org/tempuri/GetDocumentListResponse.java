
package org.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import ucloud.gate.proxy.ArrayOfDocumentInfo;
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
 *         &lt;element name="GetDocumentListResult" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Exceptions}GateResponse" minOccurs="0"/>
 *         &lt;element name="documentInfos" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy}ArrayOfDocumentInfo" minOccurs="0"/>
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
    "getDocumentListResult",
    "documentInfos"
})
@XmlRootElement(name = "GetDocumentListResponse")
public class GetDocumentListResponse {

    @XmlElement(name = "GetDocumentListResult", nillable = true)
    protected GateResponse getDocumentListResult;
    @XmlElement(nillable = true)
    protected ArrayOfDocumentInfo documentInfos;

    /**
     * Gets the value of the getDocumentListResult property.
     * 
     * @return
     *     possible object is
     *     {@link GateResponse }
     *     
     */
    public GateResponse getGetDocumentListResult() {
        return getDocumentListResult;
    }

    /**
     * Sets the value of the getDocumentListResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link GateResponse }
     *     
     */
    public void setGetDocumentListResult(GateResponse value) {
        this.getDocumentListResult = value;
    }

    /**
     * Gets the value of the documentInfos property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfDocumentInfo }
     *     
     */
    public ArrayOfDocumentInfo getDocumentInfos() {
        return documentInfos;
    }

    /**
     * Sets the value of the documentInfos property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfDocumentInfo }
     *     
     */
    public void setDocumentInfos(ArrayOfDocumentInfo value) {
        this.documentInfos = value;
    }

}
