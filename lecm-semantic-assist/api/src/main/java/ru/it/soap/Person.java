
package ru.it.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Person complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="Person">
 *   &lt;complexContent>
 *     &lt;extension base="{http://it.ru/}PersonBase">
 *       &lt;sequence>
 *         &lt;element name="Id" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Spelling" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DepartmentId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="TermsCount" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="BigramsCount" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="SignificTermsCount" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Rank" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Person", propOrder = {
    "id",
    "spelling",
    "departmentId",
    "termsCount",
    "bigramsCount",
    "significTermsCount",
    "rank"
})
public class Person
    extends PersonBase
{

    @XmlElement(name = "Id")
    protected int id;
    @XmlElement(name = "Spelling")
    protected String spelling;
    @XmlElement(name = "DepartmentId")
    protected int departmentId;
    @XmlElement(name = "TermsCount")
    protected int termsCount;
    @XmlElement(name = "BigramsCount")
    protected int bigramsCount;
    @XmlElement(name = "SignificTermsCount")
    protected int significTermsCount;
    @XmlElement(name = "Rank")
    protected float rank;

    /**
     * Gets the value of the id property.
     *
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     *
     */
    public void setId(int value) {
        this.id = value;
    }

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
     * Gets the value of the departmentId property.
     *
     */
    public int getDepartmentId() {
        return departmentId;
    }

    /**
     * Sets the value of the departmentId property.
     *
     */
    public void setDepartmentId(int value) {
        this.departmentId = value;
    }

    /**
     * Gets the value of the termsCount property.
     *
     */
    public int getTermsCount() {
        return termsCount;
    }

    /**
     * Sets the value of the termsCount property.
     *
     */
    public void setTermsCount(int value) {
        this.termsCount = value;
    }

    /**
     * Gets the value of the bigramsCount property.
     *
     */
    public int getBigramsCount() {
        return bigramsCount;
    }

    /**
     * Sets the value of the bigramsCount property.
     *
     */
    public void setBigramsCount(int value) {
        this.bigramsCount = value;
    }

    /**
     * Gets the value of the significTermsCount property.
     *
     */
    public int getSignificTermsCount() {
        return significTermsCount;
    }

    /**
     * Sets the value of the significTermsCount property.
     *
     */
    public void setSignificTermsCount(int value) {
        this.significTermsCount = value;
    }

    /**
     * Gets the value of the rank property.
     *
     */
    public float getRank() {
        return rank;
    }

    /**
     * Sets the value of the rank property.
     *
     */
    public void setRank(float value) {
        this.rank = value;
    }

}
