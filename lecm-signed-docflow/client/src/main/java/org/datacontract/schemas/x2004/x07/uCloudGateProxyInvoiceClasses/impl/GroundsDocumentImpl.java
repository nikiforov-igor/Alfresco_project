/*
 * An XML document type.
 * Localname: Grounds
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.GroundsDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.impl;
/**
 * A document containing one Grounds(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses) element.
 *
 * This is a complex type.
 */
public class GroundsDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.GroundsDocument
{
    private static final long serialVersionUID = 1L;
    
    public GroundsDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName GROUNDS$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "Grounds");
    
    
    /**
     * Gets the "Grounds" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds getGrounds()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds)get_store().find_element_user(GROUNDS$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "Grounds" element
     */
    public boolean isNilGrounds()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds)get_store().find_element_user(GROUNDS$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "Grounds" element
     */
    public void setGrounds(org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds grounds)
    {
        generatedSetterHelperImpl(grounds, GROUNDS$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "Grounds" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds addNewGrounds()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds)get_store().add_element_user(GROUNDS$0);
            return target;
        }
    }
    
    /**
     * Nils the "Grounds" element
     */
    public void setNilGrounds()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds)get_store().find_element_user(GROUNDS$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds)get_store().add_element_user(GROUNDS$0);
            }
            target.setNil();
        }
    }
}
