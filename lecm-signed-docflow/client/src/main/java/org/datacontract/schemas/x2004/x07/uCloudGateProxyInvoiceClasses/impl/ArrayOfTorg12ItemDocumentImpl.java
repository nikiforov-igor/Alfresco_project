/*
 * An XML document type.
 * Localname: ArrayOfTorg12Item
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.ArrayOfTorg12ItemDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.impl;
/**
 * A document containing one ArrayOfTorg12Item(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses) element.
 *
 * This is a complex type.
 */
public class ArrayOfTorg12ItemDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.ArrayOfTorg12ItemDocument
{
    private static final long serialVersionUID = 1L;
    
    public ArrayOfTorg12ItemDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName ARRAYOFTORG12ITEM$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "ArrayOfTorg12Item");
    
    
    /**
     * Gets the "ArrayOfTorg12Item" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.ArrayOfTorg12Item getArrayOfTorg12Item()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.ArrayOfTorg12Item target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.ArrayOfTorg12Item)get_store().find_element_user(ARRAYOFTORG12ITEM$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "ArrayOfTorg12Item" element
     */
    public boolean isNilArrayOfTorg12Item()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.ArrayOfTorg12Item target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.ArrayOfTorg12Item)get_store().find_element_user(ARRAYOFTORG12ITEM$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "ArrayOfTorg12Item" element
     */
    public void setArrayOfTorg12Item(org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.ArrayOfTorg12Item arrayOfTorg12Item)
    {
        generatedSetterHelperImpl(arrayOfTorg12Item, ARRAYOFTORG12ITEM$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "ArrayOfTorg12Item" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.ArrayOfTorg12Item addNewArrayOfTorg12Item()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.ArrayOfTorg12Item target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.ArrayOfTorg12Item)get_store().add_element_user(ARRAYOFTORG12ITEM$0);
            return target;
        }
    }
    
    /**
     * Nils the "ArrayOfTorg12Item" element
     */
    public void setNilArrayOfTorg12Item()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.ArrayOfTorg12Item target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.ArrayOfTorg12Item)get_store().find_element_user(ARRAYOFTORG12ITEM$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.ArrayOfTorg12Item)get_store().add_element_user(ARRAYOFTORG12ITEM$0);
            }
            target.setNil();
        }
    }
}
