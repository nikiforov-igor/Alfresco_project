/*
 * XML Type:  ArrayOfDocflowInfoBase
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocflowInfoBase
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxy.impl;
/**
 * An XML ArrayOfDocflowInfoBase(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy).
 *
 * This is a complex type.
 */
public class ArrayOfDocflowInfoBaseImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocflowInfoBase
{
    private static final long serialVersionUID = 1L;
    
    public ArrayOfDocflowInfoBaseImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName DOCFLOWINFOBASE$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "DocflowInfoBase");
    
    
    /**
     * Gets a List of "DocflowInfoBase" elements
     */
    public java.util.List<org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase> getDocflowInfoBaseList()
    {
        final class DocflowInfoBaseList extends java.util.AbstractList<org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase>
        {
            @Override
            public org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase get(int i)
                { return ArrayOfDocflowInfoBaseImpl.this.getDocflowInfoBaseArray(i); }
            
            @Override
            public org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase set(int i, org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase o)
            {
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase old = ArrayOfDocflowInfoBaseImpl.this.getDocflowInfoBaseArray(i);
                ArrayOfDocflowInfoBaseImpl.this.setDocflowInfoBaseArray(i, o);
                return old;
            }
            
            @Override
            public void add(int i, org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase o)
                { ArrayOfDocflowInfoBaseImpl.this.insertNewDocflowInfoBase(i).set(o); }
            
            @Override
            public org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase remove(int i)
            {
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase old = ArrayOfDocflowInfoBaseImpl.this.getDocflowInfoBaseArray(i);
                ArrayOfDocflowInfoBaseImpl.this.removeDocflowInfoBase(i);
                return old;
            }
            
            @Override
            public int size()
                { return ArrayOfDocflowInfoBaseImpl.this.sizeOfDocflowInfoBaseArray(); }
            
        }
        
        synchronized (monitor())
        {
            check_orphaned();
            return new DocflowInfoBaseList();
        }
    }
    
    /**
     * Gets array of all "DocflowInfoBase" elements
     * @deprecated
     */
    @Deprecated
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase[] getDocflowInfoBaseArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            java.util.List<org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase> targetList = new java.util.ArrayList<org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase>();
            get_store().find_all_element_users(DOCFLOWINFOBASE$0, targetList);
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase[] result = new org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    /**
     * Gets ith "DocflowInfoBase" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase getDocflowInfoBaseArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase)get_store().find_element_user(DOCFLOWINFOBASE$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }
    
    /**
     * Tests for nil ith "DocflowInfoBase" element
     */
    public boolean isNilDocflowInfoBaseArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase)get_store().find_element_user(DOCFLOWINFOBASE$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return target.isNil();
        }
    }
    
    /**
     * Returns number of "DocflowInfoBase" element
     */
    public int sizeOfDocflowInfoBaseArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(DOCFLOWINFOBASE$0);
        }
    }
    
    /**
     * Sets array of all "DocflowInfoBase" element  WARNING: This method is not atomicaly synchronized.
     */
    public void setDocflowInfoBaseArray(org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase[] docflowInfoBaseArray)
    {
        check_orphaned();
        arraySetterHelper(docflowInfoBaseArray, DOCFLOWINFOBASE$0);
    }
    
    /**
     * Sets ith "DocflowInfoBase" element
     */
    public void setDocflowInfoBaseArray(int i, org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase docflowInfoBase)
    {
        generatedSetterHelperImpl(docflowInfoBase, DOCFLOWINFOBASE$0, i, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_ARRAYITEM);
    }
    
    /**
     * Nils the ith "DocflowInfoBase" element
     */
    public void setNilDocflowInfoBaseArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase)get_store().find_element_user(DOCFLOWINFOBASE$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            target.setNil();
        }
    }
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "DocflowInfoBase" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase insertNewDocflowInfoBase(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase)get_store().insert_element_user(DOCFLOWINFOBASE$0, i);
            return target;
        }
    }
    
    /**
     * Appends and returns a new empty value (as xml) as the last "DocflowInfoBase" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase addNewDocflowInfoBase()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase)get_store().add_element_user(DOCFLOWINFOBASE$0);
            return target;
        }
    }
    
    /**
     * Removes the ith "DocflowInfoBase" element
     */
    public void removeDocflowInfoBase(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(DOCFLOWINFOBASE$0, i);
        }
    }
}
