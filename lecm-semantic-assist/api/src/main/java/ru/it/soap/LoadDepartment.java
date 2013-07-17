
package ru.it.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element name="dep" type="{http://it.ru/}DepartmentLoad" minOccurs="0"/>
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
    "dep"
})
@XmlRootElement(name = "LoadDepartment")
public class LoadDepartment {

    protected DepartmentLoad dep;

    /**
     * Gets the value of the dep property.
     *
     * @return
     *     possible object is
     *     {@link DepartmentLoad }
     *
     */
    public DepartmentLoad getDep() {
        return dep;
    }

    /**
     * Sets the value of the dep property.
     *
     * @param value
     *     allowed object is
     *     {@link DepartmentLoad }
     *
     */
    public void setDep(DepartmentLoad value) {
        this.dep = value;
    }

}
