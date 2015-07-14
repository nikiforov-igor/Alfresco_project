
package ru.it.lecm.mobile.objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSO_TASKREPORT complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSO_TASKREPORT">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:DefaultNamespace}WSO_MTASKREPORT">
 *       &lt;sequence>
 *         &lt;element name="TASK" type="{urn:DefaultNamespace}WSO_MTASK"/>
 *         &lt;element name="EXECUTOR" type="{urn:DefaultNamespace}WSO_MPERSON"/>
 *         &lt;element name="COMMENTS" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ATTACHMENTS" type="{urn:DefaultNamespace}WSO_COLLECTION"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSO_TASKREPORT", propOrder = {
    "task",
    "executor",
    "comments",
    "attachments"
})
public class WSOTASKREPORT
    extends WSOMTASKREPORT
{

    @XmlElement(name = "TASK", required = true, nillable = true)
    protected WSOMTASK task;
    @XmlElement(name = "EXECUTOR", required = true, nillable = true)
    protected WSOMPERSON executor;
    @XmlElement(name = "COMMENTS", required = true, nillable = true)
    protected String comments;
    @XmlElement(name = "ATTACHMENTS", required = true, nillable = true)
    protected WSOCOLLECTION attachments;

    /**
     * Gets the value of the task property.
     * 
     * @return
     *     possible object is
     *     {@link WSOMTASK }
     *     
     */
    public WSOMTASK getTASK() {
        return task;
    }

    /**
     * Sets the value of the task property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSOMTASK }
     *     
     */
    public void setTASK(WSOMTASK value) {
        this.task = value;
    }

    /**
     * Gets the value of the executor property.
     * 
     * @return
     *     possible object is
     *     {@link WSOMPERSON }
     *     
     */
    public WSOMPERSON getEXECUTOR() {
        return executor;
    }

    /**
     * Sets the value of the executor property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSOMPERSON }
     *     
     */
    public void setEXECUTOR(WSOMPERSON value) {
        this.executor = value;
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

}
