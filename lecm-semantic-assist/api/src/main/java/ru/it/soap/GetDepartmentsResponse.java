
package ru.it.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element name="GetDepartmentsResult" type="{http://it.ru/}ArrayOfDepartment" minOccurs="0"/>
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
    "getDepartmentsResult"
})
@XmlRootElement(name = "GetDepartmentsResponse")
public class GetDepartmentsResponse {

    @XmlElement(name = "GetDepartmentsResult")
    protected ArrayOfDepartment getDepartmentsResult;

    /**
     * Gets the value of the getDepartmentsResult property.
     *
     * @return
     *     possible object is
     *     {@link ArrayOfDepartment }
     *
     */
    public ArrayOfDepartment getGetDepartmentsResult() {
        return getDepartmentsResult;
    }

    /**
     * Sets the value of the getDepartmentsResult property.
     *
     * @param value
     *     allowed object is
     *     {@link ArrayOfDepartment }
     *
     */
    public void setGetDepartmentsResult(ArrayOfDepartment value) {
        this.getDepartmentsResult = value;
    }

}
