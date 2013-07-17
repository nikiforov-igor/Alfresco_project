
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
 *         &lt;element name="post" type="{http://it.ru/}PersonPostLoad" minOccurs="0"/>
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
    "post"
})
@XmlRootElement(name = "LoadPost")
public class LoadPost {

    protected PersonPostLoad post;

    /**
     * Gets the value of the post property.
     *
     * @return
     *     possible object is
     *     {@link PersonPostLoad }
     *
     */
    public PersonPostLoad getPost() {
        return post;
    }

    /**
     * Sets the value of the post property.
     *
     * @param value
     *     allowed object is
     *     {@link PersonPostLoad }
     *
     */
    public void setPost(PersonPostLoad value) {
        this.post = value;
    }

}
