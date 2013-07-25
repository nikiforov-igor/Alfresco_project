
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
 *         &lt;element name="text" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fileNames" type="{http://it.ru/}ArrayOfString" minOccurs="0"/>
 *         &lt;element name="contents" type="{http://it.ru/}ArrayOfBase64Binary" minOccurs="0"/>
 *         &lt;element name="maxCount" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "text",
    "fileNames",
    "contents",
    "maxCount"
})
@XmlRootElement(name = "GetTextItems")
public class GetTextItems {

    protected String text;
    protected ArrayOfString fileNames;
    protected ArrayOfBase64Binary contents;
    protected int maxCount;

    /**
     * Gets the value of the text property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the value of the text property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setText(String value) {
        this.text = value;
    }

    /**
     * Gets the value of the fileNames property.
     *
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *
     */
    public ArrayOfString getFileNames() {
        return fileNames;
    }

    /**
     * Sets the value of the fileNames property.
     *
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *
     */
    public void setFileNames(ArrayOfString value) {
        this.fileNames = value;
    }

    /**
     * Gets the value of the contents property.
     *
     * @return
     *     possible object is
     *     {@link ArrayOfBase64Binary }
     *
     */
    public ArrayOfBase64Binary getContents() {
        return contents;
    }

    /**
     * Sets the value of the contents property.
     *
     * @param value
     *     allowed object is
     *     {@link ArrayOfBase64Binary }
     *
     */
    public void setContents(ArrayOfBase64Binary value) {
        this.contents = value;
    }

    /**
     * Gets the value of the maxCount property.
     *
     */
    public int getMaxCount() {
        return maxCount;
    }

    /**
     * Sets the value of the maxCount property.
     *
     */
    public void setMaxCount(int value) {
        this.maxCount = value;
    }

}
