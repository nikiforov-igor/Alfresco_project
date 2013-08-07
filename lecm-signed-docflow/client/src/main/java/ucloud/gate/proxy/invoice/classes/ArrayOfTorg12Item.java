
package ucloud.gate.proxy.invoice.classes;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfTorg12Item complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfTorg12Item">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Torg12Item" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses}Torg12Item" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfTorg12Item", propOrder = {
    "torg12Items"
})
public class ArrayOfTorg12Item {

    @XmlElement(name = "Torg12Item", nillable = true)
    protected List<Torg12Item> torg12Items;

    /**
     * Gets the value of the torg12Items property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the torg12Items property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTorg12Items().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Torg12Item }
     * 
     * 
     */
    public List<Torg12Item> getTorg12Items() {
        if (torg12Items == null) {
            torg12Items = new ArrayList<Torg12Item>();
        }
        return this.torg12Items;
    }

}
