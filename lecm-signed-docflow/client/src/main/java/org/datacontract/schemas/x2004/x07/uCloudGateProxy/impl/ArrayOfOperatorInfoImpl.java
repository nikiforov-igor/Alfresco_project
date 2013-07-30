/*
 * XML Type:  ArrayOfOperatorInfo
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfOperatorInfo
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxy.impl;
/**
 * An XML ArrayOfOperatorInfo(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy).
 *
 * This is a complex type.
 */
public class ArrayOfOperatorInfoImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfOperatorInfo
{
    private static final long serialVersionUID = 1L;
    
    public ArrayOfOperatorInfoImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName OPERATORINFO$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "OperatorInfo");
    
    
    /**
     * Gets a List of "OperatorInfo" elements
     */
    public java.util.List<org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo> getOperatorInfoList()
    {
        final class OperatorInfoList extends java.util.AbstractList<org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo>
        {
            @Override
            public org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo get(int i)
                { return ArrayOfOperatorInfoImpl.this.getOperatorInfoArray(i); }
            
            @Override
            public org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo set(int i, org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo o)
            {
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo old = ArrayOfOperatorInfoImpl.this.getOperatorInfoArray(i);
                ArrayOfOperatorInfoImpl.this.setOperatorInfoArray(i, o);
                return old;
            }
            
            @Override
            public void add(int i, org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo o)
                { ArrayOfOperatorInfoImpl.this.insertNewOperatorInfo(i).set(o); }
            
            @Override
            public org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo remove(int i)
            {
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo old = ArrayOfOperatorInfoImpl.this.getOperatorInfoArray(i);
                ArrayOfOperatorInfoImpl.this.removeOperatorInfo(i);
                return old;
            }
            
            @Override
            public int size()
                { return ArrayOfOperatorInfoImpl.this.sizeOfOperatorInfoArray(); }
            
        }
        
        synchronized (monitor())
        {
            check_orphaned();
            return new OperatorInfoList();
        }
    }
    
    /**
     * Gets array of all "OperatorInfo" elements
     * @deprecated
     */
    @Deprecated
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo[] getOperatorInfoArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            java.util.List<org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo> targetList = new java.util.ArrayList<org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo>();
            get_store().find_all_element_users(OPERATORINFO$0, targetList);
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo[] result = new org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    /**
     * Gets ith "OperatorInfo" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo getOperatorInfoArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo)get_store().find_element_user(OPERATORINFO$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }
    
    /**
     * Tests for nil ith "OperatorInfo" element
     */
    public boolean isNilOperatorInfoArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo)get_store().find_element_user(OPERATORINFO$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return target.isNil();
        }
    }
    
    /**
     * Returns number of "OperatorInfo" element
     */
    public int sizeOfOperatorInfoArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(OPERATORINFO$0);
        }
    }
    
    /**
     * Sets array of all "OperatorInfo" element  WARNING: This method is not atomicaly synchronized.
     */
    public void setOperatorInfoArray(org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo[] operatorInfoArray)
    {
        check_orphaned();
        arraySetterHelper(operatorInfoArray, OPERATORINFO$0);
    }
    
    /**
     * Sets ith "OperatorInfo" element
     */
    public void setOperatorInfoArray(int i, org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo operatorInfo)
    {
        generatedSetterHelperImpl(operatorInfo, OPERATORINFO$0, i, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_ARRAYITEM);
    }
    
    /**
     * Nils the ith "OperatorInfo" element
     */
    public void setNilOperatorInfoArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo)get_store().find_element_user(OPERATORINFO$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            target.setNil();
        }
    }
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "OperatorInfo" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo insertNewOperatorInfo(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo)get_store().insert_element_user(OPERATORINFO$0, i);
            return target;
        }
    }
    
    /**
     * Appends and returns a new empty value (as xml) as the last "OperatorInfo" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo addNewOperatorInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo)get_store().add_element_user(OPERATORINFO$0);
            return target;
        }
    }
    
    /**
     * Removes the ith "OperatorInfo" element
     */
    public void removeOperatorInfo(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(OPERATORINFO$0, i);
        }
    }
}
