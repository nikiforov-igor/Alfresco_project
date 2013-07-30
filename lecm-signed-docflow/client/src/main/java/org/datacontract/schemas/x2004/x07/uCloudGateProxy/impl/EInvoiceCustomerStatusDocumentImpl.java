/*
 * An XML document type.
 * Localname: EInvoiceCustomerStatus
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatusDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxy.impl;
/**
 * A document containing one EInvoiceCustomerStatus(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy) element.
 *
 * This is a complex type.
 */
public class EInvoiceCustomerStatusDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatusDocument
{
    private static final long serialVersionUID = 1L;
    
    public EInvoiceCustomerStatusDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName EINVOICECUSTOMERSTATUS$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "EInvoiceCustomerStatus");
    
    
    /**
     * Gets the "EInvoiceCustomerStatus" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus.Enum getEInvoiceCustomerStatus()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(EINVOICECUSTOMERSTATUS$0, 0);
            if (target == null)
            {
                return null;
            }
            return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus.Enum)target.getEnumValue();
        }
    }
    
    /**
     * Gets (as xml) the "EInvoiceCustomerStatus" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus xgetEInvoiceCustomerStatus()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus)get_store().find_element_user(EINVOICECUSTOMERSTATUS$0, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "EInvoiceCustomerStatus" element
     */
    public boolean isNilEInvoiceCustomerStatus()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus)get_store().find_element_user(EINVOICECUSTOMERSTATUS$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "EInvoiceCustomerStatus" element
     */
    public void setEInvoiceCustomerStatus(org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus.Enum eInvoiceCustomerStatus)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(EINVOICECUSTOMERSTATUS$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(EINVOICECUSTOMERSTATUS$0);
            }
            target.setEnumValue(eInvoiceCustomerStatus);
        }
    }
    
    /**
     * Sets (as xml) the "EInvoiceCustomerStatus" element
     */
    public void xsetEInvoiceCustomerStatus(org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus eInvoiceCustomerStatus)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus)get_store().find_element_user(EINVOICECUSTOMERSTATUS$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus)get_store().add_element_user(EINVOICECUSTOMERSTATUS$0);
            }
            target.set(eInvoiceCustomerStatus);
        }
    }
    
    /**
     * Nils the "EInvoiceCustomerStatus" element
     */
    public void setNilEInvoiceCustomerStatus()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus)get_store().find_element_user(EINVOICECUSTOMERSTATUS$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus)get_store().add_element_user(EINVOICECUSTOMERSTATUS$0);
            }
            target.setNil();
        }
    }
}
