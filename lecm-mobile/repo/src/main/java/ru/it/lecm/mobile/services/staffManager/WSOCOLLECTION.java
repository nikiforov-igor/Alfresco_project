
package ru.it.lecm.mobile.services.staffManager;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSO_COLLECTION complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSO_COLLECTION">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="COUNT" type="{http://www.w3.org/2001/XMLSchema}short"/>
 *         &lt;element name="DATA" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSO_COLLECTION", propOrder = {
    "count",
    "data"
})
public class WSOCOLLECTION {

    @XmlElement(name = "COUNT")
    protected short count;
    @XmlElement(name = "DATA")
    protected List<Object> data;

    /**
     * Gets the value of the count property.
     * 
     */
    public short getCOUNT() {
        return count;
    }

    /**
     * Sets the value of the count property.
     * 
     */
    public void setCOUNT(short value) {
        this.count = value;
    }

    /**
     * Gets the value of the data property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the data property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDATA().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getDATA() {
        if (data == null) {
            data = new ArrayList<Object>();
        }
        return this.data;
    }

}
