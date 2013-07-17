
package ru.it.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DataItem complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="DataItem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Spelling" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TotalCount" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="PersonsCount" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Signific" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="Identity" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Coef" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataItem", propOrder = {
    "spelling",
    "totalCount",
    "personsCount",
    "signific",
    "identity",
    "coef"
})
public class DataItem {

    @XmlElement(name = "Spelling")
    protected String spelling;
    @XmlElement(name = "TotalCount")
    protected int totalCount;
    @XmlElement(name = "PersonsCount")
    protected int personsCount;
    @XmlElement(name = "Signific")
    protected float signific;
    @XmlElement(name = "Identity")
    protected String identity;
    @XmlElement(name = "Coef")
    protected float coef;

    /**
     * Gets the value of the spelling property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSpelling() {
        return spelling;
    }

    /**
     * Sets the value of the spelling property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSpelling(String value) {
        this.spelling = value;
    }

    /**
     * Gets the value of the totalCount property.
     *
     */
    public int getTotalCount() {
        return totalCount;
    }

    /**
     * Sets the value of the totalCount property.
     *
     */
    public void setTotalCount(int value) {
        this.totalCount = value;
    }

    /**
     * Gets the value of the personsCount property.
     *
     */
    public int getPersonsCount() {
        return personsCount;
    }

    /**
     * Sets the value of the personsCount property.
     *
     */
    public void setPersonsCount(int value) {
        this.personsCount = value;
    }

    /**
     * Gets the value of the signific property.
     *
     */
    public float getSignific() {
        return signific;
    }

    /**
     * Sets the value of the signific property.
     *
     */
    public void setSignific(float value) {
        this.signific = value;
    }

    /**
     * Gets the value of the identity property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getIdentity() {
        return identity;
    }

    /**
     * Sets the value of the identity property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setIdentity(String value) {
        this.identity = value;
    }

    /**
     * Gets the value of the coef property.
     *
     */
    public float getCoef() {
        return coef;
    }

    /**
     * Sets the value of the coef property.
     *
     */
    public void setCoef(float value) {
        this.coef = value;
    }

}
