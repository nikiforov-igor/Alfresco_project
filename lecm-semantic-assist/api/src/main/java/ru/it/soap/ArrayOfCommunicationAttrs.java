
package ru.it.soap;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfCommunicationAttrs complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ArrayOfCommunicationAttrs">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CommunicationAttrs" type="{http://it.ru/}CommunicationAttrs" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfCommunicationAttrs", propOrder = {
    "communicationAttrs"
})
public class ArrayOfCommunicationAttrs {

    @XmlElement(name = "CommunicationAttrs", nillable = true)
    protected List<CommunicationAttrs> communicationAttrs;

    /**
     * Gets the value of the communicationAttrs property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the communicationAttrs property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCommunicationAttrs().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CommunicationAttrs }
     *
     *
     */
    public List<CommunicationAttrs> getCommunicationAttrs() {
        if (communicationAttrs == null) {
            communicationAttrs = new ArrayList<CommunicationAttrs>();
        }
        return this.communicationAttrs;
    }

}
