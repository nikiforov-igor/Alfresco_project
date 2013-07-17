
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
 *         &lt;element name="personId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="thumbnail" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "personId",
    "thumbnail"
})
@XmlRootElement(name = "GetPersonPhoto")
public class GetPersonPhoto {

    protected int personId;
    protected boolean thumbnail;

    /**
     * Gets the value of the personId property.
     *
     */
    public int getPersonId() {
        return personId;
    }

    /**
     * Sets the value of the personId property.
     *
     */
    public void setPersonId(int value) {
        this.personId = value;
    }

    /**
     * Gets the value of the thumbnail property.
     *
     */
    public boolean isThumbnail() {
        return thumbnail;
    }

    /**
     * Sets the value of the thumbnail property.
     *
     */
    public void setThumbnail(boolean value) {
        this.thumbnail = value;
    }

}
