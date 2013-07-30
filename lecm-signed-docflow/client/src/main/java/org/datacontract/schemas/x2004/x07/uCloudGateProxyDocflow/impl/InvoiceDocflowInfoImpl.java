/*
 * XML Type:  InvoiceDocflowInfo
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Docflow
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.InvoiceDocflowInfo
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.impl;
/**
 * An XML InvoiceDocflowInfo(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Docflow).
 *
 * This is a complex type.
 */
public class InvoiceDocflowInfoImpl extends org.datacontract.schemas.x2004.x07.uCloudGateProxy.impl.DocflowInfoBaseImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.InvoiceDocflowInfo
{
    private static final long serialVersionUID = 1L;
    
    public InvoiceDocflowInfoImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName CUSTOMERSTATUS$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Docflow", "CustomerStatus");
    private static final javax.xml.namespace.QName VENDORSTATUS$2 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Docflow", "VendorStatus");
    
    
    /**
     * Gets the "CustomerStatus" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus.Enum getCustomerStatus()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CUSTOMERSTATUS$0, 0);
            if (target == null)
            {
                return null;
            }
            return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus.Enum)target.getEnumValue();
        }
    }
    
    /**
     * Gets (as xml) the "CustomerStatus" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus xgetCustomerStatus()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus)get_store().find_element_user(CUSTOMERSTATUS$0, 0);
            return target;
        }
    }
    
    /**
     * True if has "CustomerStatus" element
     */
    public boolean isSetCustomerStatus()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(CUSTOMERSTATUS$0) != 0;
        }
    }
    
    /**
     * Sets the "CustomerStatus" element
     */
    public void setCustomerStatus(org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus.Enum customerStatus)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CUSTOMERSTATUS$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(CUSTOMERSTATUS$0);
            }
            target.setEnumValue(customerStatus);
        }
    }
    
    /**
     * Sets (as xml) the "CustomerStatus" element
     */
    public void xsetCustomerStatus(org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus customerStatus)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus)get_store().find_element_user(CUSTOMERSTATUS$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus)get_store().add_element_user(CUSTOMERSTATUS$0);
            }
            target.set(customerStatus);
        }
    }
    
    /**
     * Unsets the "CustomerStatus" element
     */
    public void unsetCustomerStatus()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(CUSTOMERSTATUS$0, 0);
        }
    }
    
    /**
     * Gets the "VendorStatus" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceVendorStatus.Enum getVendorStatus()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(VENDORSTATUS$2, 0);
            if (target == null)
            {
                return null;
            }
            return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceVendorStatus.Enum)target.getEnumValue();
        }
    }
    
    /**
     * Gets (as xml) the "VendorStatus" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceVendorStatus xgetVendorStatus()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceVendorStatus target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceVendorStatus)get_store().find_element_user(VENDORSTATUS$2, 0);
            return target;
        }
    }
    
    /**
     * True if has "VendorStatus" element
     */
    public boolean isSetVendorStatus()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(VENDORSTATUS$2) != 0;
        }
    }
    
    /**
     * Sets the "VendorStatus" element
     */
    public void setVendorStatus(org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceVendorStatus.Enum vendorStatus)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(VENDORSTATUS$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(VENDORSTATUS$2);
            }
            target.setEnumValue(vendorStatus);
        }
    }
    
    /**
     * Sets (as xml) the "VendorStatus" element
     */
    public void xsetVendorStatus(org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceVendorStatus vendorStatus)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceVendorStatus target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceVendorStatus)get_store().find_element_user(VENDORSTATUS$2, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceVendorStatus)get_store().add_element_user(VENDORSTATUS$2);
            }
            target.set(vendorStatus);
        }
    }
    
    /**
     * Unsets the "VendorStatus" element
     */
    public void unsetVendorStatus()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(VENDORSTATUS$2, 0);
        }
    }
}
