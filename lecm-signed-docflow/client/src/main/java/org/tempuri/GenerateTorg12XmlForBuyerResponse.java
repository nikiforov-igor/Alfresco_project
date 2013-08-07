
package org.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import ucloud.gate.proxy.exceptions.GateResponse;
import ucloud.gate.proxy.generating.documents.GeneratedDocument;


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
 *         &lt;element name="GenerateTorg12XmlForBuyerResult" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Exceptions}GateResponse" minOccurs="0"/>
 *         &lt;element name="generatedDocument" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments}GeneratedDocument" minOccurs="0"/>
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
    "generateTorg12XmlForBuyerResult",
    "generatedDocument"
})
@XmlRootElement(name = "GenerateTorg12XmlForBuyerResponse")
public class GenerateTorg12XmlForBuyerResponse {

    @XmlElement(name = "GenerateTorg12XmlForBuyerResult", nillable = true)
    protected GateResponse generateTorg12XmlForBuyerResult;
    @XmlElement(nillable = true)
    protected GeneratedDocument generatedDocument;

    /**
     * Gets the value of the generateTorg12XmlForBuyerResult property.
     * 
     * @return
     *     possible object is
     *     {@link GateResponse }
     *     
     */
    public GateResponse getGenerateTorg12XmlForBuyerResult() {
        return generateTorg12XmlForBuyerResult;
    }

    /**
     * Sets the value of the generateTorg12XmlForBuyerResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link GateResponse }
     *     
     */
    public void setGenerateTorg12XmlForBuyerResult(GateResponse value) {
        this.generateTorg12XmlForBuyerResult = value;
    }

    /**
     * Gets the value of the generatedDocument property.
     * 
     * @return
     *     possible object is
     *     {@link GeneratedDocument }
     *     
     */
    public GeneratedDocument getGeneratedDocument() {
        return generatedDocument;
    }

    /**
     * Sets the value of the generatedDocument property.
     * 
     * @param value
     *     allowed object is
     *     {@link GeneratedDocument }
     *     
     */
    public void setGeneratedDocument(GeneratedDocument value) {
        this.generatedDocument = value;
    }

}
