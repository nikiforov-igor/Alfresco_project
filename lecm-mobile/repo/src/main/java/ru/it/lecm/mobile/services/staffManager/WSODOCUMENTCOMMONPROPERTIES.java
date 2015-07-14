
package ru.it.lecm.mobile.services.staffManager;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSO_DOCUMENTCOMMONPROPERTIES complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSO_DOCUMENTCOMMONPROPERTIES">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PAGESCOUNT" type="{http://www.w3.org/2001/XMLSchema}short"/>
 *         &lt;element name="COMMENTS" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="DOCTYPE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ATTACHMENTS" type="{urn:DefaultNamespace}WSO_COLLECTION"/>
 *         &lt;element name="LINKS" type="{urn:DefaultNamespace}WSO_COLLECTION"/>
 *         &lt;element name="RECIPIENTS" type="{urn:DefaultNamespace}WSO_COLLECTION"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSO_DOCUMENTCOMMONPROPERTIES", propOrder = {
    "pagescount",
    "comments",
    "doctype",
    "attachments",
    "links",
    "recipients"
})
public class WSODOCUMENTCOMMONPROPERTIES {

    @XmlElement(name = "PAGESCOUNT")
    protected short pagescount;
    @XmlElement(name = "COMMENTS", required = true, nillable = true)
    protected String comments;
    @XmlElement(name = "DOCTYPE", required = true, nillable = true)
    protected String doctype;
    @XmlElement(name = "ATTACHMENTS", required = true, nillable = true)
    protected WSOCOLLECTION attachments;
    @XmlElement(name = "LINKS", required = true, nillable = true)
    protected WSOCOLLECTION links;
    @XmlElement(name = "RECIPIENTS", required = true, nillable = true)
    protected WSOCOLLECTION recipients;

    /**
     * Gets the value of the pagescount property.
     * 
     */
    public short getPAGESCOUNT() {
        return pagescount;
    }

    /**
     * Sets the value of the pagescount property.
     * 
     */
    public void setPAGESCOUNT(short value) {
        this.pagescount = value;
    }

    /**
     * Gets the value of the comments property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCOMMENTS() {
        return comments;
    }

    /**
     * Sets the value of the comments property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCOMMENTS(String value) {
        this.comments = value;
    }

    /**
     * Gets the value of the doctype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDOCTYPE() {
        return doctype;
    }

    /**
     * Sets the value of the doctype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDOCTYPE(String value) {
        this.doctype = value;
    }

    /**
     * Gets the value of the attachments property.
     * 
     * @return
     *     possible object is
     *     {@link WSOCOLLECTION }
     *     
     */
    public WSOCOLLECTION getATTACHMENTS() {
        return attachments;
    }

    /**
     * Sets the value of the attachments property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSOCOLLECTION }
     *     
     */
    public void setATTACHMENTS(WSOCOLLECTION value) {
        this.attachments = value;
    }

    /**
     * Gets the value of the links property.
     * 
     * @return
     *     possible object is
     *     {@link WSOCOLLECTION }
     *     
     */
    public WSOCOLLECTION getLINKS() {
        return links;
    }

    /**
     * Sets the value of the links property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSOCOLLECTION }
     *     
     */
    public void setLINKS(WSOCOLLECTION value) {
        this.links = value;
    }

    /**
     * Gets the value of the recipients property.
     * 
     * @return
     *     possible object is
     *     {@link WSOCOLLECTION }
     *     
     */
    public WSOCOLLECTION getRECIPIENTS() {
        return recipients;
    }

    /**
     * Sets the value of the recipients property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSOCOLLECTION }
     *     
     */
    public void setRECIPIENTS(WSOCOLLECTION value) {
        this.recipients = value;
    }

}
