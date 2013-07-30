/*
 * XML Type:  WorkspaceFilter
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxy.WorkspaceFilter
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxy.impl;
/**
 * An XML WorkspaceFilter(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy).
 *
 * This is a complex type.
 */
public class WorkspaceFilterImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxy.WorkspaceFilter
{
    private static final long serialVersionUID = 1L;
    
    public WorkspaceFilterImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName LASTFETCHTIME$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "LastFetchTime");
    private static final javax.xml.namespace.QName RELATION$2 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "Relation");
    private static final javax.xml.namespace.QName UNREADONLY$4 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "UnreadOnly");
    
    
    /**
     * Gets the "LastFetchTime" element
     */
    public java.util.Calendar getLastFetchTime()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(LASTFETCHTIME$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getCalendarValue();
        }
    }
    
    /**
     * Gets (as xml) the "LastFetchTime" element
     */
    public org.apache.xmlbeans.XmlDateTime xgetLastFetchTime()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlDateTime target = null;
            target = (org.apache.xmlbeans.XmlDateTime)get_store().find_element_user(LASTFETCHTIME$0, 0);
            return target;
        }
    }
    
    /**
     * True if has "LastFetchTime" element
     */
    public boolean isSetLastFetchTime()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(LASTFETCHTIME$0) != 0;
        }
    }
    
    /**
     * Sets the "LastFetchTime" element
     */
    public void setLastFetchTime(java.util.Calendar lastFetchTime)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(LASTFETCHTIME$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(LASTFETCHTIME$0);
            }
            target.setCalendarValue(lastFetchTime);
        }
    }
    
    /**
     * Sets (as xml) the "LastFetchTime" element
     */
    public void xsetLastFetchTime(org.apache.xmlbeans.XmlDateTime lastFetchTime)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlDateTime target = null;
            target = (org.apache.xmlbeans.XmlDateTime)get_store().find_element_user(LASTFETCHTIME$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlDateTime)get_store().add_element_user(LASTFETCHTIME$0);
            }
            target.set(lastFetchTime);
        }
    }
    
    /**
     * Unsets the "LastFetchTime" element
     */
    public void unsetLastFetchTime()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(LASTFETCHTIME$0, 0);
        }
    }
    
    /**
     * Gets the "Relation" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.ERelationFilter.Enum getRelation()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(RELATION$2, 0);
            if (target == null)
            {
                return null;
            }
            return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ERelationFilter.Enum)target.getEnumValue();
        }
    }
    
    /**
     * Gets (as xml) the "Relation" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.ERelationFilter xgetRelation()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.ERelationFilter target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ERelationFilter)get_store().find_element_user(RELATION$2, 0);
            return target;
        }
    }
    
    /**
     * True if has "Relation" element
     */
    public boolean isSetRelation()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(RELATION$2) != 0;
        }
    }
    
    /**
     * Sets the "Relation" element
     */
    public void setRelation(org.datacontract.schemas.x2004.x07.uCloudGateProxy.ERelationFilter.Enum relation)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(RELATION$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(RELATION$2);
            }
            target.setEnumValue(relation);
        }
    }
    
    /**
     * Sets (as xml) the "Relation" element
     */
    public void xsetRelation(org.datacontract.schemas.x2004.x07.uCloudGateProxy.ERelationFilter relation)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.ERelationFilter target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ERelationFilter)get_store().find_element_user(RELATION$2, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ERelationFilter)get_store().add_element_user(RELATION$2);
            }
            target.set(relation);
        }
    }
    
    /**
     * Unsets the "Relation" element
     */
    public void unsetRelation()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(RELATION$2, 0);
        }
    }
    
    /**
     * Gets the "UnreadOnly" element
     */
    public boolean getUnreadOnly()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(UNREADONLY$4, 0);
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "UnreadOnly" element
     */
    public org.apache.xmlbeans.XmlBoolean xgetUnreadOnly()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(UNREADONLY$4, 0);
            return target;
        }
    }
    
    /**
     * True if has "UnreadOnly" element
     */
    public boolean isSetUnreadOnly()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(UNREADONLY$4) != 0;
        }
    }
    
    /**
     * Sets the "UnreadOnly" element
     */
    public void setUnreadOnly(boolean unreadOnly)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(UNREADONLY$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(UNREADONLY$4);
            }
            target.setBooleanValue(unreadOnly);
        }
    }
    
    /**
     * Sets (as xml) the "UnreadOnly" element
     */
    public void xsetUnreadOnly(org.apache.xmlbeans.XmlBoolean unreadOnly)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(UNREADONLY$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_element_user(UNREADONLY$4);
            }
            target.set(unreadOnly);
        }
    }
    
    /**
     * Unsets the "UnreadOnly" element
     */
    public void unsetUnreadOnly()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(UNREADONLY$4, 0);
        }
    }
}
