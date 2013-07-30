/*
 * XML Type:  ArrayOfInvoiceProductGen
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfInvoiceProductGen
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.impl;
/**
 * An XML ArrayOfInvoiceProductGen(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice).
 *
 * This is a complex type.
 */
public class ArrayOfInvoiceProductGenImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfInvoiceProductGen
{
    private static final long serialVersionUID = 1L;
    
    public ArrayOfInvoiceProductGenImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName INVOICEPRODUCTGEN$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "InvoiceProductGen");
    
    
    /**
     * Gets a List of "InvoiceProductGen" elements
     */
    public java.util.List<org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceProductGen> getInvoiceProductGenList()
    {
        final class InvoiceProductGenList extends java.util.AbstractList<org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceProductGen>
        {
            @Override
            public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceProductGen get(int i)
                { return ArrayOfInvoiceProductGenImpl.this.getInvoiceProductGenArray(i); }
            
            @Override
            public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceProductGen set(int i, org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceProductGen o)
            {
                org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceProductGen old = ArrayOfInvoiceProductGenImpl.this.getInvoiceProductGenArray(i);
                ArrayOfInvoiceProductGenImpl.this.setInvoiceProductGenArray(i, o);
                return old;
            }
            
            @Override
            public void add(int i, org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceProductGen o)
                { ArrayOfInvoiceProductGenImpl.this.insertNewInvoiceProductGen(i).set(o); }
            
            @Override
            public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceProductGen remove(int i)
            {
                org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceProductGen old = ArrayOfInvoiceProductGenImpl.this.getInvoiceProductGenArray(i);
                ArrayOfInvoiceProductGenImpl.this.removeInvoiceProductGen(i);
                return old;
            }
            
            @Override
            public int size()
                { return ArrayOfInvoiceProductGenImpl.this.sizeOfInvoiceProductGenArray(); }
            
        }
        
        synchronized (monitor())
        {
            check_orphaned();
            return new InvoiceProductGenList();
        }
    }
    
    /**
     * Gets array of all "InvoiceProductGen" elements
     * @deprecated
     */
    @Deprecated
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceProductGen[] getInvoiceProductGenArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            java.util.List<org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceProductGen> targetList = new java.util.ArrayList<org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceProductGen>();
            get_store().find_all_element_users(INVOICEPRODUCTGEN$0, targetList);
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceProductGen[] result = new org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceProductGen[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    /**
     * Gets ith "InvoiceProductGen" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceProductGen getInvoiceProductGenArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceProductGen target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceProductGen)get_store().find_element_user(INVOICEPRODUCTGEN$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }
    
    /**
     * Tests for nil ith "InvoiceProductGen" element
     */
    public boolean isNilInvoiceProductGenArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceProductGen target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceProductGen)get_store().find_element_user(INVOICEPRODUCTGEN$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return target.isNil();
        }
    }
    
    /**
     * Returns number of "InvoiceProductGen" element
     */
    public int sizeOfInvoiceProductGenArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(INVOICEPRODUCTGEN$0);
        }
    }
    
    /**
     * Sets array of all "InvoiceProductGen" element  WARNING: This method is not atomicaly synchronized.
     */
    public void setInvoiceProductGenArray(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceProductGen[] invoiceProductGenArray)
    {
        check_orphaned();
        arraySetterHelper(invoiceProductGenArray, INVOICEPRODUCTGEN$0);
    }
    
    /**
     * Sets ith "InvoiceProductGen" element
     */
    public void setInvoiceProductGenArray(int i, org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceProductGen invoiceProductGen)
    {
        generatedSetterHelperImpl(invoiceProductGen, INVOICEPRODUCTGEN$0, i, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_ARRAYITEM);
    }
    
    /**
     * Nils the ith "InvoiceProductGen" element
     */
    public void setNilInvoiceProductGenArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceProductGen target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceProductGen)get_store().find_element_user(INVOICEPRODUCTGEN$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            target.setNil();
        }
    }
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "InvoiceProductGen" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceProductGen insertNewInvoiceProductGen(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceProductGen target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceProductGen)get_store().insert_element_user(INVOICEPRODUCTGEN$0, i);
            return target;
        }
    }
    
    /**
     * Appends and returns a new empty value (as xml) as the last "InvoiceProductGen" element
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
     * Removes the ith "InvoiceProductGen" element
     */
    public void removeInvoiceProductGen(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(INVOICEPRODUCTGEN$0, i);
        }
    }
}
