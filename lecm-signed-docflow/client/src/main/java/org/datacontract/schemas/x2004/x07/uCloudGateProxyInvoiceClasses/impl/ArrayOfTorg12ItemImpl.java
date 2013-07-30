/*
 * XML Type:  ArrayOfTorg12Item
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.ArrayOfTorg12Item
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.impl;
/**
 * An XML ArrayOfTorg12Item(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses).
 *
 * This is a complex type.
 */
public class ArrayOfTorg12ItemImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.ArrayOfTorg12Item
{
    private static final long serialVersionUID = 1L;
    
    public ArrayOfTorg12ItemImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName TORG12ITEM$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "Torg12Item");
    
    
    /**
     * Gets a List of "Torg12Item" elements
     */
    public java.util.List<org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12Item> getTorg12ItemList()
    {
        final class Torg12ItemList extends java.util.AbstractList<org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12Item>
        {
            @Override
            public org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12Item get(int i)
                { return ArrayOfTorg12ItemImpl.this.getTorg12ItemArray(i); }
            
            @Override
            public org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12Item set(int i, org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12Item o)
            {
                org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12Item old = ArrayOfTorg12ItemImpl.this.getTorg12ItemArray(i);
                ArrayOfTorg12ItemImpl.this.setTorg12ItemArray(i, o);
                return old;
            }
            
            @Override
            public void add(int i, org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12Item o)
                { ArrayOfTorg12ItemImpl.this.insertNewTorg12Item(i).set(o); }
            
            @Override
            public org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12Item remove(int i)
            {
                org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12Item old = ArrayOfTorg12ItemImpl.this.getTorg12ItemArray(i);
                ArrayOfTorg12ItemImpl.this.removeTorg12Item(i);
                return old;
            }
            
            @Override
            public int size()
                { return ArrayOfTorg12ItemImpl.this.sizeOfTorg12ItemArray(); }
            
        }
        
        synchronized (monitor())
        {
            check_orphaned();
            return new Torg12ItemList();
        }
    }
    
    /**
     * Gets array of all "Torg12Item" elements
     * @deprecated
     */
    @Deprecated
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12Item[] getTorg12ItemArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            java.util.List<org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12Item> targetList = new java.util.ArrayList<org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12Item>();
            get_store().find_all_element_users(TORG12ITEM$0, targetList);
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12Item[] result = new org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12Item[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    /**
     * Gets ith "Torg12Item" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12Item getTorg12ItemArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12Item target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12Item)get_store().find_element_user(TORG12ITEM$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }
    
    /**
     * Tests for nil ith "Torg12Item" element
     */
    public boolean isNilTorg12ItemArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12Item target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12Item)get_store().find_element_user(TORG12ITEM$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return target.isNil();
        }
    }
    
    /**
     * Returns number of "Torg12Item" element
     */
    public int sizeOfTorg12ItemArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(TORG12ITEM$0);
        }
    }
    
    /**
     * Sets array of all "Torg12Item" element  WARNING: This method is not atomicaly synchronized.
     */
    public void setTorg12ItemArray(org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12Item[] torg12ItemArray)
    {
        check_orphaned();
        arraySetterHelper(torg12ItemArray, TORG12ITEM$0);
    }
    
    /**
     * Sets ith "Torg12Item" element
     */
    public void setTorg12ItemArray(int i, org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12Item torg12Item)
    {
        generatedSetterHelperImpl(torg12Item, TORG12ITEM$0, i, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_ARRAYITEM);
    }
    
    /**
     * Nils the ith "Torg12Item" element
     */
    public void setNilTorg12ItemArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12Item target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12Item)get_store().find_element_user(TORG12ITEM$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            target.setNil();
        }
    }
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "Torg12Item" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12Item insertNewTorg12Item(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12Item target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12Item)get_store().insert_element_user(TORG12ITEM$0, i);
            return target;
        }
    }
    
    /**
     * Appends and returns a new empty value (as xml) as the last "Torg12Item" element
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
     * Removes the ith "Torg12Item" element
     */
    public void removeTorg12Item(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(TORG12ITEM$0, i);
        }
    }
}
