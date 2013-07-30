/*
 * An XML document type.
 * Localname: ArrayOfInvoiceProductGen
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfInvoiceProductGenDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.impl;
/**
 * A document containing one ArrayOfInvoiceProductGen(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice) element.
 *
 * This is a complex type.
 */
public class ArrayOfInvoiceProductGenDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfInvoiceProductGenDocument
{
    private static final long serialVersionUID = 1L;
    
    public ArrayOfInvoiceProductGenDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName ARRAYOFINVOICEPRODUCTGEN$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "ArrayOfInvoiceProductGen");
    
    
    /**
     * Gets the "ArrayOfInvoiceProductGen" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfInvoiceProductGen getArrayOfInvoiceProductGen()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfInvoiceProductGen target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfInvoiceProductGen)get_store().find_element_user(ARRAYOFINVOICEPRODUCTGEN$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "ArrayOfInvoiceProductGen" element
     */
    public boolean isNilArrayOfInvoiceProductGen()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfInvoiceProductGen target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfInvoiceProductGen)get_store().find_element_user(ARRAYOFINVOICEPRODUCTGEN$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "ArrayOfInvoiceProductGen" element
     */
    public void setArrayOfInvoiceProductGen(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfInvoiceProductGen arrayOfInvoiceProductGen)
    {
        generatedSetterHelperImpl(arrayOfInvoiceProductGen, ARRAYOFINVOICEPRODUCTGEN$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "ArrayOfInvoiceProductGen" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfInvoiceProductGen addNewArrayOfInvoiceProductGen()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfInvoiceProductGen target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfInvoiceProductGen)get_store().add_element_user(ARRAYOFINVOICEPRODUCTGEN$0);
            return target;
        }
    }
    
    /**
     * Nils the "ArrayOfInvoiceProductGen" element
     */
    public void setNilArrayOfInvoiceProductGen()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfInvoiceProductGen target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfInvoiceProductGen)get_store().find_element_user(ARRAYOFINVOICEPRODUCTGEN$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfInvoiceProductGen)get_store().add_element_user(ARRAYOFINVOICEPRODUCTGEN$0);
            }
            target.setNil();
        }
    }
}
