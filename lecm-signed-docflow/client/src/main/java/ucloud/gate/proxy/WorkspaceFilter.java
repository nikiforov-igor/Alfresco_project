
package ucloud.gate.proxy;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for WorkspaceFilter complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WorkspaceFilter">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="LastFetchTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="Relation" type="{http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy}ERelationFilter" minOccurs="0"/>
 *         &lt;element name="UnreadOnly" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WorkspaceFilter", propOrder = {
    "lastFetchTime",
    "relation",
    "unreadOnly"
})
public class WorkspaceFilter {

    @XmlElement(name = "LastFetchTime")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastFetchTime;
    @XmlElement(name = "Relation")
    protected ERelationFilter relation;
    @XmlElement(name = "UnreadOnly")
    protected Boolean unreadOnly;

    /**
     * Gets the value of the lastFetchTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getLastFetchTime() {
        return lastFetchTime;
    }

    /**
     * Sets the value of the lastFetchTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setLastFetchTime(XMLGregorianCalendar value) {
        this.lastFetchTime = value;
    }

    /**
     * Gets the value of the relation property.
     * 
     * @return
     *     possible object is
     *     {@link ERelationFilter }
     *     
     */
    public ERelationFilter getRelation() {
        return relation;
    }

    /**
     * Sets the value of the relation property.
     * 
     * @param value
     *     allowed object is
     *     {@link ERelationFilter }
     *     
     */
    public void setRelation(ERelationFilter value) {
        this.relation = value;
    }

    /**
     * Gets the value of the unreadOnly property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isUnreadOnly() {
        return unreadOnly;
    }

    /**
     * Sets the value of the unreadOnly property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setUnreadOnly(Boolean value) {
        this.unreadOnly = value;
    }

}
