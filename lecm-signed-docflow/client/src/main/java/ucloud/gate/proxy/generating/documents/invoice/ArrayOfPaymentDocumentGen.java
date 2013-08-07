
package ucloud.gate.proxy.generating.documents.invoice;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfPaymentDocumentGen complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfPaymentDocumentGen">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PaymentDocumentGen" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice}PaymentDocumentGen" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfPaymentDocumentGen", propOrder = {
    "paymentDocumentGens"
})
public class ArrayOfPaymentDocumentGen {

    @XmlElement(name = "PaymentDocumentGen", nillable = true)
    protected List<PaymentDocumentGen> paymentDocumentGens;

    /**
     * Gets the value of the paymentDocumentGens property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the paymentDocumentGens property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPaymentDocumentGens().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PaymentDocumentGen }
     * 
     * 
     */
    public List<PaymentDocumentGen> getPaymentDocumentGens() {
        if (paymentDocumentGens == null) {
            paymentDocumentGens = new ArrayList<PaymentDocumentGen>();
        }
        return this.paymentDocumentGens;
    }

}
