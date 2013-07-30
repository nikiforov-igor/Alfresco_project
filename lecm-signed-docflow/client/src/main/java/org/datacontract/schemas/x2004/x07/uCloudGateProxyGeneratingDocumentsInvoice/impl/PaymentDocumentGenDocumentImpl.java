/*
 * An XML document type.
 * Localname: PaymentDocumentGen
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.PaymentDocumentGenDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.impl;
/**
 * A document containing one PaymentDocumentGen(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice) element.
 *
 * This is a complex type.
 */
public class PaymentDocumentGenDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.PaymentDocumentGenDocument
{
    private static final long serialVersionUID = 1L;
    
    public PaymentDocumentGenDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName PAYMENTDOCUMENTGEN$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "PaymentDocumentGen");
    
    
    /**
     * Gets the "PaymentDocumentGen" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.PaymentDocumentGen getPaymentDocumentGen()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.PaymentDocumentGen target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.PaymentDocumentGen)get_store().find_element_user(PAYMENTDOCUMENTGEN$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "PaymentDocumentGen" element
     */
    public boolean isNilPaymentDocumentGen()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.PaymentDocumentGen target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.PaymentDocumentGen)get_store().find_element_user(PAYMENTDOCUMENTGEN$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "PaymentDocumentGen" element
     */
    public void setPaymentDocumentGen(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.PaymentDocumentGen paymentDocumentGen)
    {
        generatedSetterHelperImpl(paymentDocumentGen, PAYMENTDOCUMENTGEN$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "PaymentDocumentGen" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.PaymentDocumentGen addNewPaymentDocumentGen()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.PaymentDocumentGen target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.PaymentDocumentGen)get_store().add_element_user(PAYMENTDOCUMENTGEN$0);
            return target;
        }
    }
    
    /**
     * Nils the "PaymentDocumentGen" element
     */
    public void setNilPaymentDocumentGen()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.PaymentDocumentGen target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.PaymentDocumentGen)get_store().find_element_user(PAYMENTDOCUMENTGEN$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.PaymentDocumentGen)get_store().add_element_user(PAYMENTDOCUMENTGEN$0);
            }
            target.setNil();
        }
    }
}
