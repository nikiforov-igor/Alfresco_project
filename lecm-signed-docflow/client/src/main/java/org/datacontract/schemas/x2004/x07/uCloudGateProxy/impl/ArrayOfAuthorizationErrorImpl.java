/*
 * XML Type:  ArrayOfAuthorizationError
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfAuthorizationError
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxy.impl;
/**
 * An XML ArrayOfAuthorizationError(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy).
 *
 * This is a complex type.
 */
public class ArrayOfAuthorizationErrorImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfAuthorizationError
{
    private static final long serialVersionUID = 1L;
    
    public ArrayOfAuthorizationErrorImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName AUTHORIZATIONERROR$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "AuthorizationError");
    
    
    /**
     * Gets a List of "AuthorizationError" elements
     */
    public java.util.List<org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError> getAuthorizationErrorList()
    {
        final class AuthorizationErrorList extends java.util.AbstractList<org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError>
        {
            @Override
            public org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError get(int i)
                { return ArrayOfAuthorizationErrorImpl.this.getAuthorizationErrorArray(i); }
            
            @Override
            public org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError set(int i, org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError o)
            {
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError old = ArrayOfAuthorizationErrorImpl.this.getAuthorizationErrorArray(i);
                ArrayOfAuthorizationErrorImpl.this.setAuthorizationErrorArray(i, o);
                return old;
            }
            
            @Override
            public void add(int i, org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError o)
                { ArrayOfAuthorizationErrorImpl.this.insertNewAuthorizationError(i).set(o); }
            
            @Override
            public org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError remove(int i)
            {
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError old = ArrayOfAuthorizationErrorImpl.this.getAuthorizationErrorArray(i);
                ArrayOfAuthorizationErrorImpl.this.removeAuthorizationError(i);
                return old;
            }
            
            @Override
            public int size()
                { return ArrayOfAuthorizationErrorImpl.this.sizeOfAuthorizationErrorArray(); }
            
        }
        
        synchronized (monitor())
        {
            check_orphaned();
            return new AuthorizationErrorList();
        }
    }
    
    /**
     * Gets array of all "AuthorizationError" elements
     * @deprecated
     */
    @Deprecated
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError[] getAuthorizationErrorArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            java.util.List<org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError> targetList = new java.util.ArrayList<org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError>();
            get_store().find_all_element_users(AUTHORIZATIONERROR$0, targetList);
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError[] result = new org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    /**
     * Gets ith "AuthorizationError" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError getAuthorizationErrorArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError)get_store().find_element_user(AUTHORIZATIONERROR$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }
    
    /**
     * Tests for nil ith "AuthorizationError" element
     */
    public boolean isNilAuthorizationErrorArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError)get_store().find_element_user(AUTHORIZATIONERROR$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return target.isNil();
        }
    }
    
    /**
     * Returns number of "AuthorizationError" element
     */
    public int sizeOfAuthorizationErrorArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(AUTHORIZATIONERROR$0);
        }
    }
    
    /**
     * Sets array of all "AuthorizationError" element  WARNING: This method is not atomicaly synchronized.
     */
    public void setAuthorizationErrorArray(org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError[] authorizationErrorArray)
    {
        check_orphaned();
        arraySetterHelper(authorizationErrorArray, AUTHORIZATIONERROR$0);
    }
    
    /**
     * Sets ith "AuthorizationError" element
     */
    public void setAuthorizationErrorArray(int i, org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError authorizationError)
    {
        generatedSetterHelperImpl(authorizationError, AUTHORIZATIONERROR$0, i, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_ARRAYITEM);
    }
    
    /**
     * Nils the ith "AuthorizationError" element
     */
    public void setNilAuthorizationErrorArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError)get_store().find_element_user(AUTHORIZATIONERROR$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            target.setNil();
        }
    }
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "AuthorizationError" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError insertNewAuthorizationError(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError)get_store().insert_element_user(AUTHORIZATIONERROR$0, i);
            return target;
        }
    }
    
    /**
     * Appends and returns a new empty value (as xml) as the last "AuthorizationError" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError addNewAuthorizationError()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError)get_store().add_element_user(AUTHORIZATIONERROR$0);
            return target;
        }
    }
    
    /**
     * Removes the ith "AuthorizationError" element
     */
    public void removeAuthorizationError(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(AUTHORIZATIONERROR$0, i);
        }
    }
}
