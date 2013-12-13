
package ru.it.lecm.businessjournal.remote;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for businessJournalStoreRecord complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="businessJournalStoreRecord">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="active" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="date" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="eventCategoryId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="eventCategoryStringId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="eventCategoryText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="initiatorId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="initiatorStringId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="initiatorText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mainObjectDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mainObjectId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="mainObjectStringId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nodeId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="obj1LongId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="obj1StringValue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="obj2LongId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="obj2StringValue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="obj3LongId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="obj3StringValue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="obj4LongId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="obj4StringValue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="obj5LongId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="obj5StringValue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="objectTypeId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="objectTypeStringId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="objectTypeText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="recordDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "businessJournalStoreRecord", propOrder = {
    "active",
    "date",
    "eventCategoryId",
    "eventCategoryStringId",
    "eventCategoryText",
    "initiatorId",
    "initiatorStringId",
    "initiatorText",
    "mainObjectDescription",
    "mainObjectId",
    "mainObjectStringId",
    "nodeId",
    "obj1LongId",
    "obj1StringValue",
    "obj2LongId",
    "obj2StringValue",
    "obj3LongId",
    "obj3StringValue",
    "obj4LongId",
    "obj4StringValue",
    "obj5LongId",
    "obj5StringValue",
    "objectTypeId",
    "objectTypeStringId",
    "objectTypeText",
    "recordDescription"
})
public class BusinessJournalStoreRecord {

    protected boolean active;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar date;
    protected Long eventCategoryId;
    protected String eventCategoryStringId;
    protected String eventCategoryText;
    protected Long initiatorId;
    protected String initiatorStringId;
    protected String initiatorText;
    protected String mainObjectDescription;
    protected Long mainObjectId;
    protected String mainObjectStringId;
    protected Long nodeId;
    protected Long obj1LongId;
    protected String obj1StringValue;
    protected Long obj2LongId;
    protected String obj2StringValue;
    protected Long obj3LongId;
    protected String obj3StringValue;
    protected Long obj4LongId;
    protected String obj4StringValue;
    protected Long obj5LongId;
    protected String obj5StringValue;
    protected Long objectTypeId;
    protected String objectTypeStringId;
    protected String objectTypeText;
    protected String recordDescription;

    /**
     * Gets the value of the active property.
     * 
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the value of the active property.
     * 
     */
    public void setActive(boolean value) {
        this.active = value;
    }

    /**
     * Gets the value of the date property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDate() {
        return date;
    }

    /**
     * Sets the value of the date property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDate(XMLGregorianCalendar value) {
        this.date = value;
    }

    /**
     * Gets the value of the eventCategoryId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getEventCategoryId() {
        return eventCategoryId;
    }

    /**
     * Sets the value of the eventCategoryId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setEventCategoryId(Long value) {
        this.eventCategoryId = value;
    }

    /**
     * Gets the value of the eventCategoryStringId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEventCategoryStringId() {
        return eventCategoryStringId;
    }

    /**
     * Sets the value of the eventCategoryStringId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEventCategoryStringId(String value) {
        this.eventCategoryStringId = value;
    }

    /**
     * Gets the value of the eventCategoryText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEventCategoryText() {
        return eventCategoryText;
    }

    /**
     * Sets the value of the eventCategoryText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEventCategoryText(String value) {
        this.eventCategoryText = value;
    }

    /**
     * Gets the value of the initiatorId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getInitiatorId() {
        return initiatorId;
    }

    /**
     * Sets the value of the initiatorId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setInitiatorId(Long value) {
        this.initiatorId = value;
    }

    /**
     * Gets the value of the initiatorStringId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInitiatorStringId() {
        return initiatorStringId;
    }

    /**
     * Sets the value of the initiatorStringId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInitiatorStringId(String value) {
        this.initiatorStringId = value;
    }

    /**
     * Gets the value of the initiatorText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInitiatorText() {
        return initiatorText;
    }

    /**
     * Sets the value of the initiatorText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInitiatorText(String value) {
        this.initiatorText = value;
    }

    /**
     * Gets the value of the mainObjectDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMainObjectDescription() {
        return mainObjectDescription;
    }

    /**
     * Sets the value of the mainObjectDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMainObjectDescription(String value) {
        this.mainObjectDescription = value;
    }

    /**
     * Gets the value of the mainObjectId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getMainObjectId() {
        return mainObjectId;
    }

    /**
     * Sets the value of the mainObjectId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setMainObjectId(Long value) {
        this.mainObjectId = value;
    }

    /**
     * Gets the value of the mainObjectStringId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMainObjectStringId() {
        return mainObjectStringId;
    }

    /**
     * Sets the value of the mainObjectStringId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMainObjectStringId(String value) {
        this.mainObjectStringId = value;
    }

    /**
     * Gets the value of the nodeId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getNodeId() {
        return nodeId;
    }

    /**
     * Sets the value of the nodeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setNodeId(Long value) {
        this.nodeId = value;
    }

    /**
     * Gets the value of the obj1LongId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getObj1LongId() {
        return obj1LongId;
    }

    /**
     * Sets the value of the obj1LongId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setObj1LongId(Long value) {
        this.obj1LongId = value;
    }

    /**
     * Gets the value of the obj1StringValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getObj1StringValue() {
        return obj1StringValue;
    }

    /**
     * Sets the value of the obj1StringValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setObj1StringValue(String value) {
        this.obj1StringValue = value;
    }

    /**
     * Gets the value of the obj2LongId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getObj2LongId() {
        return obj2LongId;
    }

    /**
     * Sets the value of the obj2LongId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setObj2LongId(Long value) {
        this.obj2LongId = value;
    }

    /**
     * Gets the value of the obj2StringValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getObj2StringValue() {
        return obj2StringValue;
    }

    /**
     * Sets the value of the obj2StringValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setObj2StringValue(String value) {
        this.obj2StringValue = value;
    }

    /**
     * Gets the value of the obj3LongId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getObj3LongId() {
        return obj3LongId;
    }

    /**
     * Sets the value of the obj3LongId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setObj3LongId(Long value) {
        this.obj3LongId = value;
    }

    /**
     * Gets the value of the obj3StringValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getObj3StringValue() {
        return obj3StringValue;
    }

    /**
     * Sets the value of the obj3StringValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setObj3StringValue(String value) {
        this.obj3StringValue = value;
    }

    /**
     * Gets the value of the obj4LongId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getObj4LongId() {
        return obj4LongId;
    }

    /**
     * Sets the value of the obj4LongId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setObj4LongId(Long value) {
        this.obj4LongId = value;
    }

    /**
     * Gets the value of the obj4StringValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getObj4StringValue() {
        return obj4StringValue;
    }

    /**
     * Sets the value of the obj4StringValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setObj4StringValue(String value) {
        this.obj4StringValue = value;
    }

    /**
     * Gets the value of the obj5LongId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getObj5LongId() {
        return obj5LongId;
    }

    /**
     * Sets the value of the obj5LongId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setObj5LongId(Long value) {
        this.obj5LongId = value;
    }

    /**
     * Gets the value of the obj5StringValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getObj5StringValue() {
        return obj5StringValue;
    }

    /**
     * Sets the value of the obj5StringValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setObj5StringValue(String value) {
        this.obj5StringValue = value;
    }

    /**
     * Gets the value of the objectTypeId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getObjectTypeId() {
        return objectTypeId;
    }

    /**
     * Sets the value of the objectTypeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setObjectTypeId(Long value) {
        this.objectTypeId = value;
    }

    /**
     * Gets the value of the objectTypeStringId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getObjectTypeStringId() {
        return objectTypeStringId;
    }

    /**
     * Sets the value of the objectTypeStringId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setObjectTypeStringId(String value) {
        this.objectTypeStringId = value;
    }

    /**
     * Gets the value of the objectTypeText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getObjectTypeText() {
        return objectTypeText;
    }

    /**
     * Sets the value of the objectTypeText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setObjectTypeText(String value) {
        this.objectTypeText = value;
    }

    /**
     * Gets the value of the recordDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRecordDescription() {
        return recordDescription;
    }

    /**
     * Sets the value of the recordDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRecordDescription(String value) {
        this.recordDescription = value;
    }

}
