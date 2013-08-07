
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
 *         &lt;element name="GenerateRegisterRequestResult" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Exceptions}GateResponse" minOccurs="0"/>
 *         &lt;element name="content" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
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
    "generateRegisterRequestResult",
    "content"
})
@XmlRootElement(name = "GenerateRegisterRequestResponse")
public class GenerateRegisterRequestResponse {

    @XmlElement(name = "GenerateRegisterRequestResult", nillable = true)
    protected GateResponse generateRegisterRequestResult;
    @XmlElement(nillable = true)
    protected byte[] content;

    /**
     * Gets the value of the generateRegisterRequestResult property.
     * 
     * @return
     *     possible object is
     *     {@link GateResponse }
     *     
     */
    public GateResponse getGenerateRegisterRequestResult() {
        return generateRegisterRequestResult;
    }

    /**
     * Sets the value of the generateRegisterRequestResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link GateResponse }
     *     
     */
    public void setGenerateRegisterRequestResult(GateResponse value) {
        this.generateRegisterRequestResult = value;
    }

    /**
     * Gets the value of the content property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * Sets the value of the content property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setContent(byte[] value) {
        this.content = ((byte[]) value);
    }

}
