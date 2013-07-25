
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
 *         &lt;element name="fileName" type="{http://it.ru/}ArrayOfString" minOccurs="0"/>
 *         &lt;element name="content" type="{http://it.ru/}ArrayOfBase64Binary" minOccurs="0"/>
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
    "fileName",
    "content"
})
@XmlRootElement(name = "SearchFilesExperts")
public class SearchFilesExperts {

    protected ArrayOfString fileName;
    protected ArrayOfBase64Binary content;

    /**
     * Gets the value of the fileName property.
     *
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *
     */
    public ArrayOfString getFileName() {
        return fileName;
    }

    /**
     * Sets the value of the fileName property.
     *
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *
     */
    public void setFileName(ArrayOfString value) {
        this.fileName = value;
    }

    /**
     * Gets the value of the content property.
     *
     * @return
     *     possible object is
     *     {@link ArrayOfBase64Binary }
     *
     */
    public ArrayOfBase64Binary getContent() {
        return content;
    }

    /**
     * Sets the value of the content property.
     *
     * @param value
     *     allowed object is
     *     {@link ArrayOfBase64Binary }
     *
     */
    public void setContent(ArrayOfBase64Binary value) {
        this.content = value;
    }

}
