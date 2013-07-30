/*
 * XML Type:  ArrayOfDocumentInfo
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocumentInfo
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxy.impl;
/**
 * An XML ArrayOfDocumentInfo(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy).
 *
 * This is a complex type.
 */
public class ArrayOfDocumentInfoImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocumentInfo
{
    private static final long serialVersionUID = 1L;
    
    public ArrayOfDocumentInfoImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName DOCUMENTINFO$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "DocumentInfo");
    
    
    /**
     * Gets a List of "DocumentInfo" elements
     */
    public java.util.List<org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo> getDocumentInfoList()
    {
        final class DocumentInfoList extends java.util.AbstractList<org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo>
        {
            @Override
            public org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo get(int i)
                { return ArrayOfDocumentInfoImpl.this.getDocumentInfoArray(i); }
            
            @Override
            public org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo set(int i, org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo o)
            {
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo old = ArrayOfDocumentInfoImpl.this.getDocumentInfoArray(i);
                ArrayOfDocumentInfoImpl.this.setDocumentInfoArray(i, o);
                return old;
            }
            
            @Override
            public void add(int i, org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo o)
                { ArrayOfDocumentInfoImpl.this.insertNewDocumentInfo(i).set(o); }
            
            @Override
            public org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo remove(int i)
            {
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo old = ArrayOfDocumentInfoImpl.this.getDocumentInfoArray(i);
                ArrayOfDocumentInfoImpl.this.removeDocumentInfo(i);
                return old;
            }
            
            @Override
            public int size()
                { return ArrayOfDocumentInfoImpl.this.sizeOfDocumentInfoArray(); }
            
        }
        
        synchronized (monitor())
        {
            check_orphaned();
            return new DocumentInfoList();
        }
    }
    
    /**
     * Gets array of all "DocumentInfo" elements
     * @deprecated
     */
    @Deprecated
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo[] getDocumentInfoArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            java.util.List<org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo> targetList = new java.util.ArrayList<org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo>();
            get_store().find_all_element_users(DOCUMENTINFO$0, targetList);
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo[] result = new org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    /**
     * Gets ith "DocumentInfo" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo getDocumentInfoArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo)get_store().find_element_user(DOCUMENTINFO$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }
    
    /**
     * Tests for nil ith "DocumentInfo" element
     */
    public boolean isNilDocumentInfoArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo)get_store().find_element_user(DOCUMENTINFO$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return target.isNil();
        }
    }
    
    /**
     * Returns number of "DocumentInfo" element
     */
    public int sizeOfDocumentInfoArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(DOCUMENTINFO$0);
        }
    }
    
    /**
     * Sets array of all "DocumentInfo" element  WARNING: This method is not atomicaly synchronized.
     */
    public void setDocumentInfoArray(org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo[] documentInfoArray)
    {
        check_orphaned();
        arraySetterHelper(documentInfoArray, DOCUMENTINFO$0);
    }
    
    /**
     * Sets ith "DocumentInfo" element
     */
    public void setDocumentInfoArray(int i, org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo documentInfo)
    {
        generatedSetterHelperImpl(documentInfo, DOCUMENTINFO$0, i, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_ARRAYITEM);
    }
    
    /**
     * Nils the ith "DocumentInfo" element
     */
    public void setNilDocumentInfoArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo)get_store().find_element_user(DOCUMENTINFO$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            target.setNil();
        }
    }
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "DocumentInfo" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo insertNewDocumentInfo(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo)get_store().insert_element_user(DOCUMENTINFO$0, i);
            return target;
        }
    }
    
    /**
     * Appends and returns a new empty value (as xml) as the last "DocumentInfo" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo addNewDocumentInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo)get_store().add_element_user(DOCUMENTINFO$0);
            return target;
        }
    }
    
    /**
     * Removes the ith "DocumentInfo" element
     */
    public void removeDocumentInfo(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(DOCUMENTINFO$0, i);
        }
    }
}
