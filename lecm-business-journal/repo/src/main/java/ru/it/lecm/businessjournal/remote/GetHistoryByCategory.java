
package ru.it.lecm.businessjournal.remote;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for getHistoryByCategory complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getHistoryByCategory">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="mainObjId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="categoriesList" type="{http://www.w3.org/2001/XMLSchema}long" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="sortField" type="{http://remote.businessjournal.lecm.it.ru/}field" minOccurs="0"/>
 *         &lt;element name="sortAscending" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getHistoryByCategory", propOrder = {
    "mainObjId",
    "categoriesList",
    "sortField",
    "sortAscending"
})
public class GetHistoryByCategory {

    protected Long mainObjId;
    @XmlElement(type = Long.class)
    protected List<Long> categoriesList;
    protected Field sortField;
    protected boolean sortAscending;

    /**
     * Gets the value of the mainObjId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getMainObjId() {
        return mainObjId;
    }

    /**
     * Sets the value of the mainObjId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setMainObjId(Long value) {
        this.mainObjId = value;
    }

    /**
     * Gets the value of the categoriesList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the categoriesList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCategoriesList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Long }
     * 
     * 
     */
    public List<Long> getCategoriesList() {
        if (categoriesList == null) {
            categoriesList = new ArrayList<Long>();
        }
        return this.categoriesList;
    }

    /**
     * Gets the value of the sortField property.
     * 
     * @return
     *     possible object is
     *     {@link Field }
     *     
     */
    public Field getSortField() {
        return sortField;
    }

    /**
     * Sets the value of the sortField property.
     * 
     * @param value
     *     allowed object is
     *     {@link Field }
     *     
     */
    public void setSortField(Field value) {
        this.sortField = value;
    }

    /**
     * Gets the value of the sortAscending property.
     * 
     */
    public boolean isSortAscending() {
        return sortAscending;
    }

    /**
     * Sets the value of the sortAscending property.
     * 
     */
    public void setSortAscending(boolean value) {
        this.sortAscending = value;
    }

}
