/*
 * An XML document type.
 * Localname: Torg12Item
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12ItemDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.impl;
/**
 * A document containing one Torg12Item(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses) element.
 *
 * This is a complex type.
 */
public class Torg12ItemDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12ItemDocument
{
    private static final long serialVersionUID = 1L;
    
    public Torg12ItemDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName TORG12ITEM$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "Torg12Item");
    
    
    /**
     * Gets the "Torg12Item" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12Item getTorg12Item()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12Item target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12Item)get_store().find_element_user(TORG12ITEM$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "Torg12Item" element
     */
    public boolean isNilTorg12Item()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12Item target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12Item)get_store().find_element_user(TORG12ITEM$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "Torg12Item" element
     */
    public void setTorg12Item(org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12Item torg12Item)
    {
        generatedSetterHelperImpl(torg12Item, TORG12ITEM$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "Torg12Item" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12Item addNewTorg12Item()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12Item target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12Item)get_store().add_element_user(TORG12ITEM$0);
            return target;
        }
    }
    
    /**
     * Nils the "Torg12Item" element
     */
    public void setNilTorg12Item()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12Item target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12Item)get_store().find_element_user(TORG12ITEM$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12Item)get_store().add_element_user(TORG12ITEM$0);
            }
            target.setNil();
        }
    }
}
