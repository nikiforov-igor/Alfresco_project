/*
 * An XML document type.
 * Localname: EResponseType
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Exceptions
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.EResponseTypeDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.impl;
/**
 * A document containing one EResponseType(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Exceptions) element.
 *
 * This is a complex type.
 */
public class EResponseTypeDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.EResponseTypeDocument
{
    private static final long serialVersionUID = 1L;
    
    public EResponseTypeDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName ERESPONSETYPE$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Exceptions", "EResponseType");
    
    
    /**
     * Gets the "EResponseType" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.EResponseType.Enum getEResponseType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ERESPONSETYPE$0, 0);
            if (target == null)
            {
                return null;
            }
            return (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.EResponseType.Enum)target.getEnumValue();
        }
    }
    
    /**
     * Gets (as xml) the "EResponseType" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.EResponseType xgetEResponseType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.EResponseType target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.EResponseType)get_store().find_element_user(ERESPONSETYPE$0, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "EResponseType" element
     */
    public boolean isNilEResponseType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.EResponseType target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.EResponseType)get_store().find_element_user(ERESPONSETYPE$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "EResponseType" element
     */
    public void setEResponseType(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.EResponseType.Enum eResponseType)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ERESPONSETYPE$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ERESPONSETYPE$0);
            }
            target.setEnumValue(eResponseType);
        }
    }
    
    /**
     * Sets (as xml) the "EResponseType" element
     */
    public void xsetEResponseType(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.EResponseType eResponseType)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.EResponseType target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.EResponseType)get_store().find_element_user(ERESPONSETYPE$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.EResponseType)get_store().add_element_user(ERESPONSETYPE$0);
            }
            target.set(eResponseType);
        }
    }
    
    /**
     * Nils the "EResponseType" element
     */
    public void setNilEResponseType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.EResponseType target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.EResponseType)get_store().find_element_user(ERESPONSETYPE$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.EResponseType)get_store().add_element_user(ERESPONSETYPE$0);
            }
            target.setNil();
        }
    }
}
