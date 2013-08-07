
package ucloud.gate.proxy;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import com.microsoft.schemas.serialization.arrays.ArrayOfstring;


/**
 * <p>Java class for DocumentTransportData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DocumentTransportData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="OperatorInfo" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy}OperatorInfo" minOccurs="0"/>
 *         &lt;element name="ReceiversId" type="{http://schemas.microsoft.com/2003/10/Serialization/Arrays}ArrayOfstring" minOccurs="0"/>
 *         &lt;element name="SenderId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DocumentTransportData", propOrder = {
    "operatorInfo",
    "receiversId",
    "senderId"
})
public class DocumentTransportData {

    @XmlElement(name = "OperatorInfo", nillable = true)
    protected OperatorInfo operatorInfo;
    @XmlElement(name = "ReceiversId", nillable = true)
    protected ArrayOfstring receiversId;
    @XmlElement(name = "SenderId", nillable = true)
    protected String senderId;

    /**
     * Gets the value of the operatorInfo property.
     * 
     * @return
     *     possible object is
     *     {@link OperatorInfo }
     *     
     */
    public OperatorInfo getOperatorInfo() {
        return operatorInfo;
    }

    /**
     * Sets the value of the operatorInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link OperatorInfo }
     *     
     */
    public void setOperatorInfo(OperatorInfo value) {
        this.operatorInfo = value;
    }

    /**
     * Gets the value of the receiversId property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfstring }
     *     
     */
    public ArrayOfstring getReceiversId() {
        return receiversId;
    }

    /**
     * Sets the value of the receiversId property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfstring }
     *     
     */
    public void setReceiversId(ArrayOfstring value) {
        this.receiversId = value;
    }

    /**
     * Gets the value of the senderId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSenderId() {
        return senderId;
    }

    /**
     * Sets the value of the senderId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSenderId(String value) {
        this.senderId = value;
    }

}
