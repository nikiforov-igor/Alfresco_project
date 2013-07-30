/*
 * XML Type:  ArrayOfRegistrationInfo
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfRegistrationInfo
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxy.impl;
/**
 * An XML ArrayOfRegistrationInfo(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy).
 *
 * This is a complex type.
 */
public class ArrayOfRegistrationInfoImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfRegistrationInfo
{
    private static final long serialVersionUID = 1L;
    
    public ArrayOfRegistrationInfoImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName REGISTRATIONINFO$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "RegistrationInfo");
    
    
    /**
     * Gets a List of "RegistrationInfo" elements
     */
    public java.util.List<org.datacontract.schemas.x2004.x07.uCloudGateProxy.RegistrationInfo> getRegistrationInfoList()
    {
        final class RegistrationInfoList extends java.util.AbstractList<org.datacontract.schemas.x2004.x07.uCloudGateProxy.RegistrationInfo>
        {
            @Override
            public org.datacontract.schemas.x2004.x07.uCloudGateProxy.RegistrationInfo get(int i)
                { return ArrayOfRegistrationInfoImpl.this.getRegistrationInfoArray(i); }
            
            @Override
            public org.datacontract.schemas.x2004.x07.uCloudGateProxy.RegistrationInfo set(int i, org.datacontract.schemas.x2004.x07.uCloudGateProxy.RegistrationInfo o)
            {
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.RegistrationInfo old = ArrayOfRegistrationInfoImpl.this.getRegistrationInfoArray(i);
                ArrayOfRegistrationInfoImpl.this.setRegistrationInfoArray(i, o);
                return old;
            }
            
            @Override
            public void add(int i, org.datacontract.schemas.x2004.x07.uCloudGateProxy.RegistrationInfo o)
                { ArrayOfRegistrationInfoImpl.this.insertNewRegistrationInfo(i).set(o); }
            
            @Override
            public org.datacontract.schemas.x2004.x07.uCloudGateProxy.RegistrationInfo remove(int i)
            {
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.RegistrationInfo old = ArrayOfRegistrationInfoImpl.this.getRegistrationInfoArray(i);
                ArrayOfRegistrationInfoImpl.this.removeRegistrationInfo(i);
                return old;
            }
            
            @Override
            public int size()
                { return ArrayOfRegistrationInfoImpl.this.sizeOfRegistrationInfoArray(); }
            
        }
        
        synchronized (monitor())
        {
            check_orphaned();
            return new RegistrationInfoList();
        }
    }
    
    /**
     * Gets array of all "RegistrationInfo" elements
     * @deprecated
     */
    @Deprecated
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.RegistrationInfo[] getRegistrationInfoArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            java.util.List<org.datacontract.schemas.x2004.x07.uCloudGateProxy.RegistrationInfo> targetList = new java.util.ArrayList<org.datacontract.schemas.x2004.x07.uCloudGateProxy.RegistrationInfo>();
            get_store().find_all_element_users(REGISTRATIONINFO$0, targetList);
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.RegistrationInfo[] result = new org.datacontract.schemas.x2004.x07.uCloudGateProxy.RegistrationInfo[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    /**
     * Gets ith "RegistrationInfo" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.RegistrationInfo getRegistrationInfoArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.RegistrationInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.RegistrationInfo)get_store().find_element_user(REGISTRATIONINFO$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }
    
    /**
     * Tests for nil ith "RegistrationInfo" element
     */
    public boolean isNilRegistrationInfoArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.RegistrationInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.RegistrationInfo)get_store().find_element_user(REGISTRATIONINFO$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return target.isNil();
        }
    }
    
    /**
     * Returns number of "RegistrationInfo" element
     */
    public int sizeOfRegistrationInfoArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(REGISTRATIONINFO$0);
        }
    }
    
    /**
     * Sets array of all "RegistrationInfo" element  WARNING: This method is not atomicaly synchronized.
     */
    public void setRegistrationInfoArray(org.datacontract.schemas.x2004.x07.uCloudGateProxy.RegistrationInfo[] registrationInfoArray)
    {
        check_orphaned();
        arraySetterHelper(registrationInfoArray, REGISTRATIONINFO$0);
    }
    
    /**
     * Sets ith "RegistrationInfo" element
     */
    public void setRegistrationInfoArray(int i, org.datacontract.schemas.x2004.x07.uCloudGateProxy.RegistrationInfo registrationInfo)
    {
        generatedSetterHelperImpl(registrationInfo, REGISTRATIONINFO$0, i, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_ARRAYITEM);
    }
    
    /**
     * Nils the ith "RegistrationInfo" element
     */
    public void setNilRegistrationInfoArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.RegistrationInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.RegistrationInfo)get_store().find_element_user(REGISTRATIONINFO$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            target.setNil();
        }
    }
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "RegistrationInfo" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.RegistrationInfo insertNewRegistrationInfo(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.RegistrationInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.RegistrationInfo)get_store().insert_element_user(REGISTRATIONINFO$0, i);
            return target;
        }
    }
    
    /**
     * Appends and returns a new empty value (as xml) as the last "RegistrationInfo" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.RegistrationInfo addNewRegistrationInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.RegistrationInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.RegistrationInfo)get_store().add_element_user(REGISTRATIONINFO$0);
            return target;
        }
    }
    
    /**
     * Removes the ith "RegistrationInfo" element
     */
    public void removeRegistrationInfo(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(REGISTRATIONINFO$0, i);
        }
    }
}
