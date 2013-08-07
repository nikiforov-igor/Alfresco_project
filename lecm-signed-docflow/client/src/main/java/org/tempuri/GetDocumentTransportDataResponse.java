
package org.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import ucloud.gate.proxy.DocumentTransportData;
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
 *         &lt;element name="GetDocumentTransportDataResult" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Exceptions}GateResponse" minOccurs="0"/>
 *         &lt;element name="transportData" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy}DocumentTransportData" minOccurs="0"/>
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
    "getDocumentTransportDataResult",
    "transportData"
})
@XmlRootElement(name = "GetDocumentTransportDataResponse")
public class GetDocumentTransportDataResponse {

    @XmlElement(name = "GetDocumentTransportDataResult", nillable = true)
    protected GateResponse getDocumentTransportDataResult;
    @XmlElement(nillable = true)
    protected DocumentTransportData transportData;

    /**
     * Gets the value of the getDocumentTransportDataResult property.
     * 
     * @return
     *     possible object is
     *     {@link GateResponse }
     *     
     */
    public GateResponse getGetDocumentTransportDataResult() {
        return getDocumentTransportDataResult;
    }

    /**
     * Sets the value of the getDocumentTransportDataResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link GateResponse }
     *     
     */
    public void setGetDocumentTransportDataResult(GateResponse value) {
        this.getDocumentTransportDataResult = value;
    }

    /**
     * Gets the value of the transportData property.
     * 
     * @return
     *     possible object is
     *     {@link DocumentTransportData }
     *     
     */
    public DocumentTransportData getTransportData() {
        return transportData;
    }

    /**
     * Sets the value of the transportData property.
     * 
     * @param value
     *     allowed object is
     *     {@link DocumentTransportData }
     *     
     */
    public void setTransportData(DocumentTransportData value) {
        this.transportData = value;
    }

}
