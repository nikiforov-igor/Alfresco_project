/*
 * An XML document type.
 * Localname: EInvoiceVendorStatus
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceVendorStatusDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxy.impl;
/**
 * A document containing one EInvoiceVendorStatus(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy) element.
 *
 * This is a complex type.
 */
public class EInvoiceVendorStatusDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceVendorStatusDocument
{
    private static final long serialVersionUID = 1L;
    
    public EInvoiceVendorStatusDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName EINVOICEVENDORSTATUS$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "EInvoiceVendorStatus");
    
    
    /**
     * Gets the "EInvoiceVendorStatus" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceVendorStatus.Enum getEInvoiceVendorStatus()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(EINVOICEVENDORSTATUS$0, 0);
            if (target == null)
            {
                return null;
            }
            return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceVendorStatus.Enum)target.getEnumValue();
        }
    }
    
    /**
     * Gets (as xml) the "EInvoiceVendorStatus" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceVendorStatus xgetEInvoiceVendorStatus()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceVendorStatus target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceVendorStatus)get_store().find_element_user(EINVOICEVENDORSTATUS$0, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "EInvoiceVendorStatus" element
     */
    public boolean isNilEInvoiceVendorStatus()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceVendorStatus target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceVendorStatus)get_store().find_element_user(EINVOICEVENDORSTATUS$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "EInvoiceVendorStatus" element
     */
    public void setEInvoiceVendorStatus(org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceVendorStatus.Enum eInvoiceVendorStatus)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(EINVOICEVENDORSTATUS$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(EINVOICEVENDORSTATUS$0);
            }
            target.setEnumValue(eInvoiceVendorStatus);
        }
    }
    
    /**
     * Sets (as xml) the "EInvoiceVendorStatus" element
     */
    public void xsetEInvoiceVendorStatus(org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceVendorStatus eInvoiceVendorStatus)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceVendorStatus target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceVendorStatus)get_store().find_element_user(EINVOICEVENDORSTATUS$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceVendorStatus)get_store().add_element_user(EINVOICEVENDORSTATUS$0);
            }
            target.set(eInvoiceVendorStatus);
        }
    }
    
    /**
     * Nils the "EInvoiceVendorStatus" element
     */
    public void setNilEInvoiceVendorStatus()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceVendorStatus target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceVendorStatus)get_store().find_element_user(EINVOICEVENDORSTATUS$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceVendorStatus)get_store().add_element_user(EINVOICEVENDORSTATUS$0);
            }
            target.setNil();
        }
    }
}
