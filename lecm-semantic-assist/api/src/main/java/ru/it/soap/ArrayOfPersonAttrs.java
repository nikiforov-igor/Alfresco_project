
package ru.it.soap;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfPersonAttrs complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ArrayOfPersonAttrs">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PersonAttrs" type="{http://it.ru/}PersonAttrs" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfPersonAttrs", propOrder = {
    "personAttrs"
})
public class ArrayOfPersonAttrs {

    @XmlElement(name = "PersonAttrs", nillable = true)
    protected List<PersonAttrs> personAttrs;

    /**
     * Gets the value of the personAttrs property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the personAttrs property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPersonAttrs().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PersonAttrs }
     *
     *
     */
    public List<PersonAttrs> getPersonAttrs() {
        if (personAttrs == null) {
            personAttrs = new ArrayList<PersonAttrs>();
        }
        return this.personAttrs;
    }

}
