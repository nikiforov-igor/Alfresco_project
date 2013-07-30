/*
 * An XML document type.
 * Localname: GetDocumentList
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.GetDocumentListDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one GetDocumentList(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class GetDocumentListDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GetDocumentListDocument
{
    private static final long serialVersionUID = 1L;
    
    public GetDocumentListDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName GETDOCUMENTLIST$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "GetDocumentList");
    
    
    /**
     * Gets the "GetDocumentList" element
     */
    public org.tempuri.GetDocumentListDocument.GetDocumentList getGetDocumentList()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GetDocumentListDocument.GetDocumentList target = null;
            target = (org.tempuri.GetDocumentListDocument.GetDocumentList)get_store().find_element_user(GETDOCUMENTLIST$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "GetDocumentList" element
     */
    public void setGetDocumentList(org.tempuri.GetDocumentListDocument.GetDocumentList getDocumentList)
    {
        generatedSetterHelperImpl(getDocumentList, GETDOCUMENTLIST$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "GetDocumentList" element
     */
    public org.tempuri.GetDocumentListDocument.GetDocumentList addNewGetDocumentList()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GetDocumentListDocument.GetDocumentList target = null;
            target = (org.tempuri.GetDocumentListDocument.GetDocumentList)get_store().add_element_user(GETDOCUMENTLIST$0);
            return target;
        }
    }
    /**
     * An XML GetDocumentList(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class GetDocumentListImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GetDocumentListDocument.GetDocumentList
    {
        private static final long serialVersionUID = 1L;
        
        public GetDocumentListImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName DOCFLOWID$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "docflowId");
        
        
        /**
         * Gets the "docflowId" element
         */
        public java.lang.String getDocflowId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DOCFLOWID$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "docflowId" element
         */
        public com.microsoft.schemas.x2003.x10.serialization.Guid xgetDocflowId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.microsoft.schemas.x2003.x10.serialization.Guid target = null;
                target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().find_element_user(DOCFLOWID$0, 0);
                return target;
            }
        }
        
        /**
         * True if has "docflowId" element
         */
        public boolean isSetDocflowId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(DOCFLOWID$0) != 0;
            }
        }
        
        /**
         * Sets the "docflowId" element
         */
        public void setDocflowId(java.lang.String docflowId)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DOCFLOWID$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(DOCFLOWID$0);
                }
                target.setStringValue(docflowId);
            }
        }
        
        /**
         * Sets (as xml) the "docflowId" element
         */
        public void xsetDocflowId(com.microsoft.schemas.x2003.x10.serialization.Guid docflowId)
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.microsoft.schemas.x2003.x10.serialization.Guid target = null;
                target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().find_element_user(DOCFLOWID$0, 0);
                if (target == null)
                {
                    target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().add_element_user(DOCFLOWID$0);
                }
                target.set(docflowId);
            }
        }
        
        /**
         * Unsets the "docflowId" element
         */
        public void unsetDocflowId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(DOCFLOWID$0, 0);
            }
        }
    }
}
