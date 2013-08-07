
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
 *         &lt;element name="GenerateNotificationXmlResult" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Exceptions}GateResponse" minOccurs="0"/>
 *         &lt;element name="generatedDoc" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments}GeneratedDocument" minOccurs="0"/>
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
    "generateNotificationXmlResult",
    "generatedDoc"
})
@XmlRootElement(name = "GenerateNotificationXmlResponse")
public class GenerateNotificationXmlResponse {

    @XmlElement(name = "GenerateNotificationXmlResult", nillable = true)
    protected GateResponse generateNotificationXmlResult;
    @XmlElement(nillable = true)
    protected GeneratedDocument generatedDoc;

    /**
     * Gets the value of the generateNotificationXmlResult property.
     * 
     * @return
     *     possible object is
     *     {@link GateResponse }
     *     
     */
    public GateResponse getGenerateNotificationXmlResult() {
        return generateNotificationXmlResult;
    }

    /**
     * Sets the value of the generateNotificationXmlResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link GateResponse }
     *     
     */
    public void setGenerateNotificationXmlResult(GateResponse value) {
        this.generateNotificationXmlResult = value;
    }

    /**
     * Gets the value of the generatedDoc property.
     * 
     * @return
     *     possible object is
     *     {@link GeneratedDocument }
     *     
     */
    public GeneratedDocument getGeneratedDoc() {
        return generatedDoc;
    }

    /**
     * Sets the value of the generatedDoc property.
     * 
     * @param value
     *     allowed object is
     *     {@link GeneratedDocument }
     *     
     */
    public void setGeneratedDoc(GeneratedDocument value) {
        this.generatedDoc = value;
    }

}
