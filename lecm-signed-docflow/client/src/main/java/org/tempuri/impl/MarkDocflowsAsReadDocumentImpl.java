/*
 * An XML document type.
 * Localname: MarkDocflowsAsRead
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.MarkDocflowsAsReadDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one MarkDocflowsAsRead(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class MarkDocflowsAsReadDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.MarkDocflowsAsReadDocument
{
    private static final long serialVersionUID = 1L;
    
    public MarkDocflowsAsReadDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName MARKDOCFLOWSASREAD$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "MarkDocflowsAsRead");
    
    
    /**
     * Gets the "MarkDocflowsAsRead" element
     */
    public org.tempuri.MarkDocflowsAsReadDocument.MarkDocflowsAsRead getMarkDocflowsAsRead()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.MarkDocflowsAsReadDocument.MarkDocflowsAsRead target = null;
            target = (org.tempuri.MarkDocflowsAsReadDocument.MarkDocflowsAsRead)get_store().find_element_user(MARKDOCFLOWSASREAD$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "MarkDocflowsAsRead" element
     */
    public void setMarkDocflowsAsRead(org.tempuri.MarkDocflowsAsReadDocument.MarkDocflowsAsRead markDocflowsAsRead)
    {
        generatedSetterHelperImpl(markDocflowsAsRead, MARKDOCFLOWSASREAD$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "MarkDocflowsAsRead" element
     */
    public org.tempuri.MarkDocflowsAsReadDocument.MarkDocflowsAsRead addNewMarkDocflowsAsRead()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.MarkDocflowsAsReadDocument.MarkDocflowsAsRead target = null;
            target = (org.tempuri.MarkDocflowsAsReadDocument.MarkDocflowsAsRead)get_store().add_element_user(MARKDOCFLOWSASREAD$0);
            return target;
        }
    }
    /**
     * An XML MarkDocflowsAsRead(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class MarkDocflowsAsReadImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.MarkDocflowsAsReadDocument.MarkDocflowsAsRead
    {
        private static final long serialVersionUID = 1L;
        
        public MarkDocflowsAsReadImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName DOCFLOWIDARRAY1$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "docflowIdArray");
        
        
        /**
         * Gets the "docflowIdArray" element
         */
        public com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid getDocflowIdArray1()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid target = null;
                target = (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid)get_store().find_element_user(DOCFLOWIDARRAY1$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "docflowIdArray" element
         */
        public boolean isNilDocflowIdArray1()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid target = null;
                target = (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid)get_store().find_element_user(DOCFLOWIDARRAY1$0, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "docflowIdArray" element
         */
        public boolean isSetDocflowIdArray1()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(DOCFLOWIDARRAY1$0) != 0;
            }
        }
        
        /**
         * Sets the "docflowIdArray" element
         */
        public void setDocflowIdArray1(com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid docflowIdArray1)
        {
            generatedSetterHelperImpl(docflowIdArray1, DOCFLOWIDARRAY1$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "docflowIdArray" element
         */
        public com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid addNewDocflowIdArray1()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid target = null;
                target = (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid)get_store().add_element_user(DOCFLOWIDARRAY1$0);
                return target;
            }
        }
        
        /**
         * Nils the "docflowIdArray" element
         */
        public void setNilDocflowIdArray1()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid target = null;
                target = (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid)get_store().find_element_user(DOCFLOWIDARRAY1$0, 0);
                if (target == null)
                {
                    target = (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid)get_store().add_element_user(DOCFLOWIDARRAY1$0);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "docflowIdArray" element
         */
        public void unsetDocflowIdArray1()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(DOCFLOWIDARRAY1$0, 0);
            }
        }
    }
}
