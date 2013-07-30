/*
 * XML Type:  NonformalizedDocflowInfo
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Docflow
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.NonformalizedDocflowInfo
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.impl;
/**
 * An XML NonformalizedDocflowInfo(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Docflow).
 *
 * This is a complex type.
 */
public class NonformalizedDocflowInfoImpl extends org.datacontract.schemas.x2004.x07.uCloudGateProxy.impl.DocflowInfoBaseImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.NonformalizedDocflowInfo
{
    private static final long serialVersionUID = 1L;
    
    public NonformalizedDocflowInfoImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName NONFORMALIZEDDOCUMENTSTATUS$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Docflow", "NonformalizedDocumentStatus");
    
    
    /**
     * Gets the "NonformalizedDocumentStatus" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.ENonformalizedDocumentStatus.Enum getNonformalizedDocumentStatus()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(NONFORMALIZEDDOCUMENTSTATUS$0, 0);
            if (target == null)
            {
                return null;
            }
            return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ENonformalizedDocumentStatus.Enum)target.getEnumValue();
        }
    }
    
    /**
     * Gets (as xml) the "NonformalizedDocumentStatus" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.ENonformalizedDocumentStatus xgetNonformalizedDocumentStatus()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.ENonformalizedDocumentStatus target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ENonformalizedDocumentStatus)get_store().find_element_user(NONFORMALIZEDDOCUMENTSTATUS$0, 0);
            return target;
        }
    }
    
    /**
     * True if has "NonformalizedDocumentStatus" element
     */
    public boolean isSetNonformalizedDocumentStatus()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(NONFORMALIZEDDOCUMENTSTATUS$0) != 0;
        }
    }
    
    /**
     * Sets the "NonformalizedDocumentStatus" element
     */
    public void setNonformalizedDocumentStatus(org.datacontract.schemas.x2004.x07.uCloudGateProxy.ENonformalizedDocumentStatus.Enum nonformalizedDocumentStatus)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(NONFORMALIZEDDOCUMENTSTATUS$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(NONFORMALIZEDDOCUMENTSTATUS$0);
            }
            target.setEnumValue(nonformalizedDocumentStatus);
        }
    }
    
    /**
     * Sets (as xml) the "NonformalizedDocumentStatus" element
     */
    public void xsetNonformalizedDocumentStatus(org.datacontract.schemas.x2004.x07.uCloudGateProxy.ENonformalizedDocumentStatus nonformalizedDocumentStatus)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.ENonformalizedDocumentStatus target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ENonformalizedDocumentStatus)get_store().find_element_user(NONFORMALIZEDDOCUMENTSTATUS$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ENonformalizedDocumentStatus)get_store().add_element_user(NONFORMALIZEDDOCUMENTSTATUS$0);
            }
            target.set(nonformalizedDocumentStatus);
        }
    }
    
    /**
     * Unsets the "NonformalizedDocumentStatus" element
     */
    public void unsetNonformalizedDocumentStatus()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(NONFORMALIZEDDOCUMENTSTATUS$0, 0);
        }
    }
}
