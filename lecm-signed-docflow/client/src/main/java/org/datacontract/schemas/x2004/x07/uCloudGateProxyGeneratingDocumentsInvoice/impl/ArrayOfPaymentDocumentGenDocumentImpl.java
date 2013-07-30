/*
 * An XML document type.
 * Localname: ArrayOfPaymentDocumentGen
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfPaymentDocumentGenDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.impl;
/**
 * A document containing one ArrayOfPaymentDocumentGen(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice) element.
 *
 * This is a complex type.
 */
public class ArrayOfPaymentDocumentGenDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfPaymentDocumentGenDocument
{
    private static final long serialVersionUID = 1L;
    
    public ArrayOfPaymentDocumentGenDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName ARRAYOFPAYMENTDOCUMENTGEN$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "ArrayOfPaymentDocumentGen");
    
    
    /**
     * Gets the "ArrayOfPaymentDocumentGen" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfPaymentDocumentGen getArrayOfPaymentDocumentGen()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfPaymentDocumentGen target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfPaymentDocumentGen)get_store().find_element_user(ARRAYOFPAYMENTDOCUMENTGEN$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "ArrayOfPaymentDocumentGen" element
     */
    public boolean isNilArrayOfPaymentDocumentGen()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfPaymentDocumentGen target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfPaymentDocumentGen)get_store().find_element_user(ARRAYOFPAYMENTDOCUMENTGEN$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "ArrayOfPaymentDocumentGen" element
     */
    public void setArrayOfPaymentDocumentGen(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfPaymentDocumentGen arrayOfPaymentDocumentGen)
    {
        generatedSetterHelperImpl(arrayOfPaymentDocumentGen, ARRAYOFPAYMENTDOCUMENTGEN$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "ArrayOfPaymentDocumentGen" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfPaymentDocumentGen addNewArrayOfPaymentDocumentGen()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfPaymentDocumentGen target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfPaymentDocumentGen)get_store().add_element_user(ARRAYOFPAYMENTDOCUMENTGEN$0);
            return target;
        }
    }
    
    /**
     * Nils the "ArrayOfPaymentDocumentGen" element
     */
    public void setNilArrayOfPaymentDocumentGen()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfPaymentDocumentGen target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfPaymentDocumentGen)get_store().find_element_user(ARRAYOFPAYMENTDOCUMENTGEN$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfPaymentDocumentGen)get_store().add_element_user(ARRAYOFPAYMENTDOCUMENTGEN$0);
            }
            target.setNil();
        }
    }
}
