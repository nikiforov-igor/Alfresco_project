
package ru.it.lecm.businessjournal.remote;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getLastRecords complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getLastRecords">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MaxRecordsCount" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="IncludeArchived" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getLastRecords", propOrder = {
    "maxRecordsCount",
    "includeArchived"
})
public class GetLastRecords {

    @XmlElement(name = "MaxRecordsCount")
    protected int maxRecordsCount;
    @XmlElement(name = "IncludeArchived")
    protected boolean includeArchived;

    /**
     * Gets the value of the maxRecordsCount property.
     * 
     */
    public int getMaxRecordsCount() {
        return maxRecordsCount;
    }

    /**
     * Sets the value of the maxRecordsCount property.
     * 
     */
    public void setMaxRecordsCount(int value) {
        this.maxRecordsCount = value;
    }

    /**
     * Gets the value of the includeArchived property.
     * 
     */
    public boolean isIncludeArchived() {
        return includeArchived;
    }

    /**
     * Sets the value of the includeArchived property.
     * 
     */
    public void setIncludeArchived(boolean value) {
        this.includeArchived = value;
    }

}
