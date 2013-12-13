
package ru.it.lecm.businessjournal.remote;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for save complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="save">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="record" type="{http://remote.businessjournal.lecm.it.ru/}businessJournalStoreRecord" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "save", propOrder = {
    "record"
})
public class Save {

    protected BusinessJournalStoreRecord record;

    /**
     * Gets the value of the record property.
     * 
     * @return
     *     possible object is
     *     {@link BusinessJournalStoreRecord }
     *     
     */
    public BusinessJournalStoreRecord getRecord() {
        return record;
    }

    /**
     * Sets the value of the record property.
     * 
     * @param value
     *     allowed object is
     *     {@link BusinessJournalStoreRecord }
     *     
     */
    public void setRecord(BusinessJournalStoreRecord value) {
        this.record = value;
    }

}
