
package ru.it.lecm.businessjournal.remote;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for getRecords complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getRecords">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SortField" type="{http://remote.businessjournal.lecm.it.ru/}field" minOccurs="0"/>
 *         &lt;element name="SortAscending" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="StartIndex" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="MaxResults" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Filter">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="entry" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="key" type="{http://remote.businessjournal.lecm.it.ru/}field" minOccurs="0"/>
 *                             &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="AndFilter" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
@XmlType(name = "getRecords", propOrder = {
    "sortField",
    "sortAscending",
    "startIndex",
    "maxResults",
    "filter",
    "andFilter",
    "includeArchived"
})
public class GetRecords {

    @XmlElement(name = "SortField")
    protected Field sortField;
    @XmlElement(name = "SortAscending")
    protected boolean sortAscending;
    @XmlElement(name = "StartIndex")
    protected int startIndex;
    @XmlElement(name = "MaxResults")
    protected int maxResults;
    @XmlElement(name = "Filter", required = true)
    protected GetRecords.Filter filter;
    @XmlElement(name = "AndFilter")
    protected boolean andFilter;
    @XmlElement(name = "IncludeArchived")
    protected boolean includeArchived;

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
     * Gets the value of the startIndex property.
     * 
     */
    public int getStartIndex() {
        return startIndex;
    }

    /**
     * Sets the value of the startIndex property.
     * 
     */
    public void setStartIndex(int value) {
        this.startIndex = value;
    }

    /**
     * Gets the value of the maxResults property.
     * 
     */
    public int getMaxResults() {
        return maxResults;
    }

    /**
     * Sets the value of the maxResults property.
     * 
     */
    public void setMaxResults(int value) {
        this.maxResults = value;
    }

    /**
     * Gets the value of the filter property.
     * 
     * @return
     *     possible object is
     *     {@link GetRecords.Filter }
     *     
     */
    public GetRecords.Filter getFilter() {
        return filter;
    }

    /**
     * Sets the value of the filter property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetRecords.Filter }
     *     
     */
    public void setFilter(GetRecords.Filter value) {
        this.filter = value;
    }

    /**
     * Gets the value of the andFilter property.
     * 
     */
    public boolean isAndFilter() {
        return andFilter;
    }

    /**
     * Sets the value of the andFilter property.
     * 
     */
    public void setAndFilter(boolean value) {
        this.andFilter = value;
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


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="entry" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="key" type="{http://remote.businessjournal.lecm.it.ru/}field" minOccurs="0"/>
     *                   &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "entry"
    })
    public static class Filter {

        protected List<GetRecords.Filter.Entry> entry;

        /**
         * Gets the value of the entry property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the entry property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEntry().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link GetRecords.Filter.Entry }
         * 
         * 
         */
        public List<GetRecords.Filter.Entry> getEntry() {
            if (entry == null) {
                entry = new ArrayList<GetRecords.Filter.Entry>();
            }
            return this.entry;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="key" type="{http://remote.businessjournal.lecm.it.ru/}field" minOccurs="0"/>
         *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "key",
            "value"
        })
        public static class Entry {

            protected Field key;
            protected String value;

            /**
             * Gets the value of the key property.
             * 
             * @return
             *     possible object is
             *     {@link Field }
             *     
             */
            public Field getKey() {
                return key;
            }

            /**
             * Sets the value of the key property.
             * 
             * @param value
             *     allowed object is
             *     {@link Field }
             *     
             */
            public void setKey(Field value) {
                this.key = value;
            }

            /**
             * Gets the value of the value property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getValue() {
                return value;
            }

            /**
             * Sets the value of the value property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setValue(String value) {
                this.value = value;
            }

        }

    }

}
