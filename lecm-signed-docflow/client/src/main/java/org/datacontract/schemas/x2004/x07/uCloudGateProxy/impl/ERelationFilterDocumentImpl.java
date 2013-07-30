/*
 * An XML document type.
 * Localname: ERelationFilter
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxy.ERelationFilterDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxy.impl;
/**
 * A document containing one ERelationFilter(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy) element.
 *
 * This is a complex type.
 */
public class ERelationFilterDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxy.ERelationFilterDocument
{
    private static final long serialVersionUID = 1L;
    
    public ERelationFilterDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName ERELATIONFILTER$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "ERelationFilter");
    
    
    /**
     * Gets the "ERelationFilter" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.ERelationFilter.Enum getERelationFilter()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ERELATIONFILTER$0, 0);
            if (target == null)
            {
                return null;
            }
            return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ERelationFilter.Enum)target.getEnumValue();
        }
    }
    
    /**
     * Gets (as xml) the "ERelationFilter" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.ERelationFilter xgetERelationFilter()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.ERelationFilter target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ERelationFilter)get_store().find_element_user(ERELATIONFILTER$0, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "ERelationFilter" element
     */
    public boolean isNilERelationFilter()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.ERelationFilter target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ERelationFilter)get_store().find_element_user(ERELATIONFILTER$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "ERelationFilter" element
     */
    public void setERelationFilter(org.datacontract.schemas.x2004.x07.uCloudGateProxy.ERelationFilter.Enum eRelationFilter)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ERELATIONFILTER$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ERELATIONFILTER$0);
            }
            target.setEnumValue(eRelationFilter);
        }
    }
    
    /**
     * Sets (as xml) the "ERelationFilter" element
     */
    public void xsetERelationFilter(org.datacontract.schemas.x2004.x07.uCloudGateProxy.ERelationFilter eRelationFilter)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.ERelationFilter target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ERelationFilter)get_store().find_element_user(ERELATIONFILTER$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ERelationFilter)get_store().add_element_user(ERELATIONFILTER$0);
            }
            target.set(eRelationFilter);
        }
    }
    
    /**
     * Nils the "ERelationFilter" element
     */
    public void setNilERelationFilter()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.ERelationFilter target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ERelationFilter)get_store().find_element_user(ERELATIONFILTER$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ERelationFilter)get_store().add_element_user(ERELATIONFILTER$0);
            }
            target.setNil();
        }
    }
}
