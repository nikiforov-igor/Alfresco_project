
package ru.it.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PersonLoad complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="PersonLoad">
 *   &lt;complexContent>
 *     &lt;extension base="{http://it.ru/}PersonBase">
 *       &lt;sequence>
 *         &lt;element name="DepIdentities" type="{http://it.ru/}ArrayOfString" minOccurs="0"/>
 *         &lt;element name="Photo" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *         &lt;element name="PhotoFileName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PersonLoad", propOrder = {
    "depIdentities",
    "photo",
    "photoFileName"
})
public class PersonLoad
    extends PersonBase
{

    @XmlElement(name = "DepIdentities")
    protected ArrayOfString depIdentities;
    @XmlElement(name = "Photo")
    protected byte[] photo;
    @XmlElement(name = "PhotoFileName")
    protected String photoFileName;

    /**
     * Gets the value of the depIdentities property.
     *
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *
     */
    public ArrayOfString getDepIdentities() {
        return depIdentities;
    }

    /**
     * Sets the value of the depIdentities property.
     *
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *
     */
    public void setDepIdentities(ArrayOfString value) {
        this.depIdentities = value;
    }

    /**
     * Gets the value of the photo property.
     *
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getPhoto() {
        return photo;
    }

    /**
     * Sets the value of the photo property.
     *
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setPhoto(byte[] value) {
        this.photo = value;
    }

    /**
     * Gets the value of the photoFileName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPhotoFileName() {
        return photoFileName;
    }

    /**
     * Sets the value of the photoFileName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPhotoFileName(String value) {
        this.photoFileName = value;
    }

}
