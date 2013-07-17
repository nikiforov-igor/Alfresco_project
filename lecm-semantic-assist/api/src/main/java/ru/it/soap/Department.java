
package ru.it.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Department complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="Department">
 *   &lt;complexContent>
 *     &lt;extension base="{http://it.ru/}DepartmentBase">
 *       &lt;sequence>
 *         &lt;element name="Id" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="ParentId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Department", propOrder = {
    "id",
    "parentId"
})
public class Department
    extends DepartmentBase
{

    @XmlElement(name = "Id")
    protected int id;
    @XmlElement(name = "ParentId")
    protected int parentId;

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
     * Gets the value of the parentId property.
     *
     */
    public int getParentId() {
        return parentId;
    }

    /**
     * Sets the value of the parentId property.
     *
     */
    public void setParentId(int value) {
        this.parentId = value;
    }

}
