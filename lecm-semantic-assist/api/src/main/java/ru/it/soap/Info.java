
package ru.it.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Info complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="Info">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="IndexId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Comment" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PersonsCount" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="TermsCount" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="BigramsCount" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="ContactsCount" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="PersonsLinksCount" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="UploadIndexRegim" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="NeedUploadIndex" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="CommunicationsLoadCount" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="CallsLoadCount" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="TermsLoadCount" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="BigramsLoadCount" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="DiskFreeSpace" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="DiskIndexSize" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Info", propOrder = {
    "indexId",
    "comment",
    "personsCount",
    "termsCount",
    "bigramsCount",
    "contactsCount",
    "personsLinksCount",
    "uploadIndexRegim",
    "needUploadIndex",
    "communicationsLoadCount",
    "callsLoadCount",
    "termsLoadCount",
    "bigramsLoadCount",
    "diskFreeSpace",
    "diskIndexSize"
})
public class Info {

    @XmlElement(name = "IndexId")
    protected String indexId;
    @XmlElement(name = "Comment")
    protected String comment;
    @XmlElement(name = "PersonsCount")
    protected long personsCount;
    @XmlElement(name = "TermsCount")
    protected long termsCount;
    @XmlElement(name = "BigramsCount")
    protected long bigramsCount;
    @XmlElement(name = "ContactsCount")
    protected long contactsCount;
    @XmlElement(name = "PersonsLinksCount")
    protected long personsLinksCount;
    @XmlElement(name = "UploadIndexRegim")
    protected boolean uploadIndexRegim;
    @XmlElement(name = "NeedUploadIndex")
    protected boolean needUploadIndex;
    @XmlElement(name = "CommunicationsLoadCount")
    protected long communicationsLoadCount;
    @XmlElement(name = "CallsLoadCount")
    protected long callsLoadCount;
    @XmlElement(name = "TermsLoadCount")
    protected long termsLoadCount;
    @XmlElement(name = "BigramsLoadCount")
    protected long bigramsLoadCount;
    @XmlElement(name = "DiskFreeSpace")
    protected long diskFreeSpace;
    @XmlElement(name = "DiskIndexSize")
    protected long diskIndexSize;

    /**
     * Gets the value of the indexId property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getIndexId() {
        return indexId;
    }

    /**
     * Sets the value of the indexId property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setIndexId(String value) {
        this.indexId = value;
    }

    /**
     * Gets the value of the comment property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets the value of the comment property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setComment(String value) {
        this.comment = value;
    }

    /**
     * Gets the value of the personsCount property.
     *
     */
    public long getPersonsCount() {
        return personsCount;
    }

    /**
     * Sets the value of the personsCount property.
     *
     */
    public void setPersonsCount(long value) {
        this.personsCount = value;
    }

    /**
     * Gets the value of the termsCount property.
     *
     */
    public long getTermsCount() {
        return termsCount;
    }

    /**
     * Sets the value of the termsCount property.
     *
     */
    public void setTermsCount(long value) {
        this.termsCount = value;
    }

    /**
     * Gets the value of the bigramsCount property.
     *
     */
    public long getBigramsCount() {
        return bigramsCount;
    }

    /**
     * Sets the value of the bigramsCount property.
     *
     */
    public void setBigramsCount(long value) {
        this.bigramsCount = value;
    }

    /**
     * Gets the value of the contactsCount property.
     *
     */
    public long getContactsCount() {
        return contactsCount;
    }

    /**
     * Sets the value of the contactsCount property.
     *
     */
    public void setContactsCount(long value) {
        this.contactsCount = value;
    }

    /**
     * Gets the value of the personsLinksCount property.
     *
     */
    public long getPersonsLinksCount() {
        return personsLinksCount;
    }

    /**
     * Sets the value of the personsLinksCount property.
     *
     */
    public void setPersonsLinksCount(long value) {
        this.personsLinksCount = value;
    }

    /**
     * Gets the value of the uploadIndexRegim property.
     *
     */
    public boolean isUploadIndexRegim() {
        return uploadIndexRegim;
    }

    /**
     * Sets the value of the uploadIndexRegim property.
     *
     */
    public void setUploadIndexRegim(boolean value) {
        this.uploadIndexRegim = value;
    }

    /**
     * Gets the value of the needUploadIndex property.
     *
     */
    public boolean isNeedUploadIndex() {
        return needUploadIndex;
    }

    /**
     * Sets the value of the needUploadIndex property.
     *
     */
    public void setNeedUploadIndex(boolean value) {
        this.needUploadIndex = value;
    }

    /**
     * Gets the value of the communicationsLoadCount property.
     *
     */
    public long getCommunicationsLoadCount() {
        return communicationsLoadCount;
    }

    /**
     * Sets the value of the communicationsLoadCount property.
     *
     */
    public void setCommunicationsLoadCount(long value) {
        this.communicationsLoadCount = value;
    }

    /**
     * Gets the value of the callsLoadCount property.
     *
     */
    public long getCallsLoadCount() {
        return callsLoadCount;
    }

    /**
     * Sets the value of the callsLoadCount property.
     *
     */
    public void setCallsLoadCount(long value) {
        this.callsLoadCount = value;
    }

    /**
     * Gets the value of the termsLoadCount property.
     *
     */
    public long getTermsLoadCount() {
        return termsLoadCount;
    }

    /**
     * Sets the value of the termsLoadCount property.
     *
     */
    public void setTermsLoadCount(long value) {
        this.termsLoadCount = value;
    }

    /**
     * Gets the value of the bigramsLoadCount property.
     *
     */
    public long getBigramsLoadCount() {
        return bigramsLoadCount;
    }

    /**
     * Sets the value of the bigramsLoadCount property.
     *
     */
    public void setBigramsLoadCount(long value) {
        this.bigramsLoadCount = value;
    }

    /**
     * Gets the value of the diskFreeSpace property.
     *
     */
    public long getDiskFreeSpace() {
        return diskFreeSpace;
    }

    /**
     * Sets the value of the diskFreeSpace property.
     *
     */
    public void setDiskFreeSpace(long value) {
        this.diskFreeSpace = value;
    }

    /**
     * Gets the value of the diskIndexSize property.
     *
     */
    public long getDiskIndexSize() {
        return diskIndexSize;
    }

    /**
     * Sets the value of the diskIndexSize property.
     *
     */
    public void setDiskIndexSize(long value) {
        this.diskIndexSize = value;
    }

}
