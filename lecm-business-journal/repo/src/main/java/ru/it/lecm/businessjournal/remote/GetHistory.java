
package ru.it.lecm.businessjournal.remote;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getHistory complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getHistory">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MainObjectID" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="SortField" type="{http://remote.businessjournal.lecm.it.ru/}field" minOccurs="0"/>
 *         &lt;element name="sortAscending" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="IncludeSecondaryRecords" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="IncludeArchivedRecords" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getHistory", propOrder = {
    "mainObjectID",
    "sortField",
    "sortAscending",
    "includeSecondaryRecords",
    "includeArchivedRecords"
})
public class GetHistory {

    @XmlElement(name = "MainObjectID")
    protected Long mainObjectID;
    @XmlElement(name = "SortField")
    protected Field sortField;
    protected boolean sortAscending;
    @XmlElement(name = "IncludeSecondaryRecords")
    protected boolean includeSecondaryRecords;
    @XmlElement(name = "IncludeArchivedRecords")
    protected boolean includeArchivedRecords;

    /**
     * Gets the value of the mainObjectID property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getMainObjectID() {
        return mainObjectID;
    }

    /**
     * Sets the value of the mainObjectID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setMainObjectID(Long value) {
        this.mainObjectID = value;
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

    /**
     * Gets the value of the includeSecondaryRecords property.
     * 
     */
    public boolean isIncludeSecondaryRecords() {
        return includeSecondaryRecords;
    }

    /**
     * Sets the value of the includeSecondaryRecords property.
     * 
     */
    public void setIncludeSecondaryRecords(boolean value) {
        this.includeSecondaryRecords = value;
    }

    /**
     * Gets the value of the includeArchivedRecords property.
     * 
     */
    public boolean isIncludeArchivedRecords() {
        return includeArchivedRecords;
    }

    /**
     * Sets the value of the includeArchivedRecords property.
     * 
     */
    public void setIncludeArchivedRecords(boolean value) {
        this.includeArchivedRecords = value;
    }

}
