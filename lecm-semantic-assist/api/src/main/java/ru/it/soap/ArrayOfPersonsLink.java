
package ru.it.soap;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfPersonsLink complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ArrayOfPersonsLink">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PersonsLink" type="{http://it.ru/}PersonsLink" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfPersonsLink", propOrder = {
    "personsLink"
})
public class ArrayOfPersonsLink {

    @XmlElement(name = "PersonsLink", nillable = true)
    protected List<PersonsLink> personsLink;

    /**
     * Gets the value of the personsLink property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the personsLink property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPersonsLink().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PersonsLink }
     *
     *
     */
    public List<PersonsLink> getPersonsLink() {
        if (personsLink == null) {
            personsLink = new ArrayList<PersonsLink>();
        }
        return this.personsLink;
    }

}
