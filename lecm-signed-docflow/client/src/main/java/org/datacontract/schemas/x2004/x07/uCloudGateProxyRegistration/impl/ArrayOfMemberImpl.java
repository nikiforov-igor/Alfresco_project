/*
 * XML Type:  ArrayOfMember
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.ArrayOfMember
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.impl;
/**
 * An XML ArrayOfMember(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration).
 *
 * This is a complex type.
 */
public class ArrayOfMemberImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.ArrayOfMember
{
    private static final long serialVersionUID = 1L;
    
    public ArrayOfMemberImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName MEMBER$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration", "Member");
    
    
    /**
     * Gets a List of "Member" elements
     */
    public java.util.List<org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.Member> getMemberList()
    {
        final class MemberList extends java.util.AbstractList<org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.Member>
        {
            @Override
            public org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.Member get(int i)
                { return ArrayOfMemberImpl.this.getMemberArray(i); }
            
            @Override
            public org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.Member set(int i, org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.Member o)
            {
                org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.Member old = ArrayOfMemberImpl.this.getMemberArray(i);
                ArrayOfMemberImpl.this.setMemberArray(i, o);
                return old;
            }
            
            @Override
            public void add(int i, org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.Member o)
                { ArrayOfMemberImpl.this.insertNewMember(i).set(o); }
            
            @Override
            public org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.Member remove(int i)
            {
                org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.Member old = ArrayOfMemberImpl.this.getMemberArray(i);
                ArrayOfMemberImpl.this.removeMember(i);
                return old;
            }
            
            @Override
            public int size()
                { return ArrayOfMemberImpl.this.sizeOfMemberArray(); }
            
        }
        
        synchronized (monitor())
        {
            check_orphaned();
            return new MemberList();
        }
    }
    
    /**
     * Gets array of all "Member" elements
     * @deprecated
     */
    @Deprecated
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.Member[] getMemberArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            java.util.List<org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.Member> targetList = new java.util.ArrayList<org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.Member>();
            get_store().find_all_element_users(MEMBER$0, targetList);
            org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.Member[] result = new org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.Member[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    /**
     * Gets ith "Member" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.Member getMemberArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.Member target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.Member)get_store().find_element_user(MEMBER$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }
    
    /**
     * Tests for nil ith "Member" element
     */
    public boolean isNilMemberArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.Member target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.Member)get_store().find_element_user(MEMBER$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return target.isNil();
        }
    }
    
    /**
     * Returns number of "Member" element
     */
    public int sizeOfMemberArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(MEMBER$0);
        }
    }
    
    /**
     * Sets array of all "Member" element  WARNING: This method is not atomicaly synchronized.
     */
    public void setMemberArray(org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.Member[] memberArray)
    {
        check_orphaned();
        arraySetterHelper(memberArray, MEMBER$0);
    }
    
    /**
     * Sets ith "Member" element
     */
    public void setMemberArray(int i, org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.Member member)
    {
        generatedSetterHelperImpl(member, MEMBER$0, i, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_ARRAYITEM);
    }
    
    /**
     * Nils the ith "Member" element
     */
    public void setNilMemberArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.Member target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.Member)get_store().find_element_user(MEMBER$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            target.setNil();
        }
    }
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "Member" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.Member insertNewMember(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.Member target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.Member)get_store().insert_element_user(MEMBER$0, i);
            return target;
        }
    }
    
    /**
     * Appends and returns a new empty value (as xml) as the last "Member" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.Member addNewMember()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.Member target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.Member)get_store().add_element_user(MEMBER$0);
            return target;
        }
    }
    
    /**
     * Removes the ith "Member" element
     */
    public void removeMember(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(MEMBER$0, i);
        }
    }
}
