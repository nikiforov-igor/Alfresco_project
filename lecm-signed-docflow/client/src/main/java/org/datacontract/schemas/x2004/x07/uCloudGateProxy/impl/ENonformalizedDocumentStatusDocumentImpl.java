/*
 * An XML document type.
 * Localname: ENonformalizedDocumentStatus
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxy.ENonformalizedDocumentStatusDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxy.impl;
/**
 * A document containing one ENonformalizedDocumentStatus(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy) element.
 *
 * This is a complex type.
 */
public class ENonformalizedDocumentStatusDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxy.ENonformalizedDocumentStatusDocument
{
    private static final long serialVersionUID = 1L;
    
    public ENonformalizedDocumentStatusDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName ENONFORMALIZEDDOCUMENTSTATUS$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "ENonformalizedDocumentStatus");
    
    
    /**
     * Gets the "ENonformalizedDocumentStatus" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.ENonformalizedDocumentStatus.Enum getENonformalizedDocumentStatus()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ENONFORMALIZEDDOCUMENTSTATUS$0, 0);
            if (target == null)
            {
                return null;
            }
            return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ENonformalizedDocumentStatus.Enum)target.getEnumValue();
        }
    }
    
    /**
     * Gets (as xml) the "ENonformalizedDocumentStatus" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.ENonformalizedDocumentStatus xgetENonformalizedDocumentStatus()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.ENonformalizedDocumentStatus target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ENonformalizedDocumentStatus)get_store().find_element_user(ENONFORMALIZEDDOCUMENTSTATUS$0, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "ENonformalizedDocumentStatus" element
     */
    public boolean isNilENonformalizedDocumentStatus()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.ENonformalizedDocumentStatus target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ENonformalizedDocumentStatus)get_store().find_element_user(ENONFORMALIZEDDOCUMENTSTATUS$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "ENonformalizedDocumentStatus" element
     */
    public void setENonformalizedDocumentStatus(org.datacontract.schemas.x2004.x07.uCloudGateProxy.ENonformalizedDocumentStatus.Enum eNonformalizedDocumentStatus)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ENONFORMALIZEDDOCUMENTSTATUS$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ENONFORMALIZEDDOCUMENTSTATUS$0);
            }
            target.setEnumValue(eNonformalizedDocumentStatus);
        }
    }
    
    /**
     * Sets (as xml) the "ENonformalizedDocumentStatus" element
     */
    public void xsetENonformalizedDocumentStatus(org.datacontract.schemas.x2004.x07.uCloudGateProxy.ENonformalizedDocumentStatus eNonformalizedDocumentStatus)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.ENonformalizedDocumentStatus target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ENonformalizedDocumentStatus)get_store().find_element_user(ENONFORMALIZEDDOCUMENTSTATUS$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ENonformalizedDocumentStatus)get_store().add_element_user(ENONFORMALIZEDDOCUMENTSTATUS$0);
            }
            target.set(eNonformalizedDocumentStatus);
        }
    }
    
    /**
     * Nils the "ENonformalizedDocumentStatus" element
     */
    public void setNilENonformalizedDocumentStatus()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.ENonformalizedDocumentStatus target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ENonformalizedDocumentStatus)get_store().find_element_user(ENONFORMALIZEDDOCUMENTSTATUS$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ENonformalizedDocumentStatus)get_store().add_element_user(ENONFORMALIZEDDOCUMENTSTATUS$0);
            }
            target.setNil();
        }
    }
}
