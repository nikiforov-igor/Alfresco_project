/*
 * XML Type:  ArrayOfPaymentDocumentGen
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfPaymentDocumentGen
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.impl;
/**
 * An XML ArrayOfPaymentDocumentGen(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice).
 *
 * This is a complex type.
 */
public class ArrayOfPaymentDocumentGenImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfPaymentDocumentGen
{
    private static final long serialVersionUID = 1L;
    
    public ArrayOfPaymentDocumentGenImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName PAYMENTDOCUMENTGEN$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "PaymentDocumentGen");
    
    
    /**
     * Gets a List of "PaymentDocumentGen" elements
     */
    public java.util.List<org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.PaymentDocumentGen> getPaymentDocumentGenList()
    {
        final class PaymentDocumentGenList extends java.util.AbstractList<org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.PaymentDocumentGen>
        {
            @Override
            public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.PaymentDocumentGen get(int i)
                { return ArrayOfPaymentDocumentGenImpl.this.getPaymentDocumentGenArray(i); }
            
            @Override
            public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.PaymentDocumentGen set(int i, org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.PaymentDocumentGen o)
            {
                org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.PaymentDocumentGen old = ArrayOfPaymentDocumentGenImpl.this.getPaymentDocumentGenArray(i);
                ArrayOfPaymentDocumentGenImpl.this.setPaymentDocumentGenArray(i, o);
                return old;
            }
            
            @Override
            public void add(int i, org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.PaymentDocumentGen o)
                { ArrayOfPaymentDocumentGenImpl.this.insertNewPaymentDocumentGen(i).set(o); }
            
            @Override
            public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.PaymentDocumentGen remove(int i)
            {
                org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.PaymentDocumentGen old = ArrayOfPaymentDocumentGenImpl.this.getPaymentDocumentGenArray(i);
                ArrayOfPaymentDocumentGenImpl.this.removePaymentDocumentGen(i);
                return old;
            }
            
            @Override
            public int size()
                { return ArrayOfPaymentDocumentGenImpl.this.sizeOfPaymentDocumentGenArray(); }
            
        }
        
        synchronized (monitor())
        {
            check_orphaned();
            return new PaymentDocumentGenList();
        }
    }
    
    /**
     * Gets array of all "PaymentDocumentGen" elements
     * @deprecated
     */
    @Deprecated
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.PaymentDocumentGen[] getPaymentDocumentGenArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            java.util.List<org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.PaymentDocumentGen> targetList = new java.util.ArrayList<org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.PaymentDocumentGen>();
            get_store().find_all_element_users(PAYMENTDOCUMENTGEN$0, targetList);
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.PaymentDocumentGen[] result = new org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.PaymentDocumentGen[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    /**
     * Gets ith "PaymentDocumentGen" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.PaymentDocumentGen getPaymentDocumentGenArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.PaymentDocumentGen target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.PaymentDocumentGen)get_store().find_element_user(PAYMENTDOCUMENTGEN$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }
    
    /**
     * Tests for nil ith "PaymentDocumentGen" element
     */
    public boolean isNilPaymentDocumentGenArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.PaymentDocumentGen target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.PaymentDocumentGen)get_store().find_element_user(PAYMENTDOCUMENTGEN$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return target.isNil();
        }
    }
    
    /**
     * Returns number of "PaymentDocumentGen" element
     */
    public int sizeOfPaymentDocumentGenArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(PAYMENTDOCUMENTGEN$0);
        }
    }
    
    /**
     * Sets array of all "PaymentDocumentGen" element  WARNING: This method is not atomicaly synchronized.
     */
    public void setPaymentDocumentGenArray(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.PaymentDocumentGen[] paymentDocumentGenArray)
    {
        check_orphaned();
        arraySetterHelper(paymentDocumentGenArray, PAYMENTDOCUMENTGEN$0);
    }
    
    /**
     * Sets ith "PaymentDocumentGen" element
     */
    public void setPaymentDocumentGenArray(int i, org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.PaymentDocumentGen paymentDocumentGen)
    {
        generatedSetterHelperImpl(paymentDocumentGen, PAYMENTDOCUMENTGEN$0, i, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_ARRAYITEM);
    }
    
    /**
     * Nils the ith "PaymentDocumentGen" element
     */
    public void setNilPaymentDocumentGenArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.PaymentDocumentGen target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.PaymentDocumentGen)get_store().find_element_user(PAYMENTDOCUMENTGEN$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            target.setNil();
        }
    }
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "PaymentDocumentGen" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.PaymentDocumentGen insertNewPaymentDocumentGen(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.PaymentDocumentGen target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.PaymentDocumentGen)get_store().insert_element_user(PAYMENTDOCUMENTGEN$0, i);
            return target;
        }
    }
    
    /**
     * Appends and returns a new empty value (as xml) as the last "PaymentDocumentGen" element
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
     * Removes the ith "PaymentDocumentGen" element
     */
    public void removePaymentDocumentGen(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(PAYMENTDOCUMENTGEN$0, i);
        }
    }
}
