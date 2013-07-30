/*
 * An XML document type.
 * Localname: InvoiceProductGen
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceProductGenDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.impl;
/**
 * A document containing one InvoiceProductGen(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice) element.
 *
 * This is a complex type.
 */
public class InvoiceProductGenDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceProductGenDocument
{
    private static final long serialVersionUID = 1L;
    
    public InvoiceProductGenDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName INVOICEPRODUCTGEN$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "InvoiceProductGen");
    
    
    /**
     * Gets the "InvoiceProductGen" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceProductGen getInvoiceProductGen()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceProductGen target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceProductGen)get_store().find_element_user(INVOICEPRODUCTGEN$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "InvoiceProductGen" element
     */
    public boolean isNilInvoiceProductGen()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceProductGen target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceProductGen)get_store().find_element_user(INVOICEPRODUCTGEN$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "InvoiceProductGen" element
     */
    public void setInvoiceProductGen(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceProductGen invoiceProductGen)
    {
        generatedSetterHelperImpl(invoiceProductGen, INVOICEPRODUCTGEN$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "InvoiceProductGen" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceProductGen addNewInvoiceProductGen()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceProductGen target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceProductGen)get_store().add_element_user(INVOICEPRODUCTGEN$0);
            return target;
        }
    }
    
    /**
     * Nils the "InvoiceProductGen" element
     */
    public void setNilInvoiceProductGen()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceProductGen target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceProductGen)get_store().find_element_user(INVOICEPRODUCTGEN$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceProductGen)get_store().add_element_user(INVOICEPRODUCTGEN$0);
            }
            target.setNil();
        }
    }
}
