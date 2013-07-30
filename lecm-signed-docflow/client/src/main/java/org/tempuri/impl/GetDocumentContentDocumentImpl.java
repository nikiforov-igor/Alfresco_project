/*
 * An XML document type.
 * Localname: GetDocumentContent
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.GetDocumentContentDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one GetDocumentContent(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class GetDocumentContentDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GetDocumentContentDocument
{
    private static final long serialVersionUID = 1L;
    
    public GetDocumentContentDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName GETDOCUMENTCONTENT$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "GetDocumentContent");
    
    
    /**
     * Gets the "GetDocumentContent" element
     */
    public org.tempuri.GetDocumentContentDocument.GetDocumentContent getGetDocumentContent()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GetDocumentContentDocument.GetDocumentContent target = null;
            target = (org.tempuri.GetDocumentContentDocument.GetDocumentContent)get_store().find_element_user(GETDOCUMENTCONTENT$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "GetDocumentContent" element
     */
    public void setGetDocumentContent(org.tempuri.GetDocumentContentDocument.GetDocumentContent getDocumentContent)
    {
        generatedSetterHelperImpl(getDocumentContent, GETDOCUMENTCONTENT$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "GetDocumentContent" element
     */
    public org.tempuri.GetDocumentContentDocument.GetDocumentContent addNewGetDocumentContent()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GetDocumentContentDocument.GetDocumentContent target = null;
            target = (org.tempuri.GetDocumentContentDocument.GetDocumentContent)get_store().add_element_user(GETDOCUMENTCONTENT$0);
            return target;
        }
    }
    /**
     * An XML GetDocumentContent(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class GetDocumentContentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GetDocumentContentDocument.GetDocumentContent
    {
        private static final long serialVersionUID = 1L;
        
        public GetDocumentContentImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName DOCUMENTID$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "documentId");
        private static final javax.xml.namespace.QName GETSIGNATURESONLY$2 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "getSignaturesOnly");
        
        
        /**
         * Gets the "documentId" element
         */
        public java.lang.String getDocumentId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DOCUMENTID$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "documentId" element
         */
        public com.microsoft.schemas.x2003.x10.serialization.Guid xgetDocumentId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.microsoft.schemas.x2003.x10.serialization.Guid target = null;
                target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().find_element_user(DOCUMENTID$0, 0);
                return target;
            }
        }
        
        /**
         * True if has "documentId" element
         */
        public boolean isSetDocumentId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(DOCUMENTID$0) != 0;
            }
        }
        
        /**
         * Sets the "documentId" element
         */
        public void setDocumentId(java.lang.String documentId)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DOCUMENTID$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(DOCUMENTID$0);
                }
                target.setStringValue(documentId);
            }
        }
        
        /**
         * Sets (as xml) the "documentId" element
         */
        public void xsetDocumentId(com.microsoft.schemas.x2003.x10.serialization.Guid documentId)
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.microsoft.schemas.x2003.x10.serialization.Guid target = null;
                target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().find_element_user(DOCUMENTID$0, 0);
                if (target == null)
                {
                    target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().add_element_user(DOCUMENTID$0);
                }
                target.set(documentId);
            }
        }
        
        /**
         * Unsets the "documentId" element
         */
        public void unsetDocumentId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(DOCUMENTID$0, 0);
            }
        }
        
        /**
         * Gets the "getSignaturesOnly" element
         */
        public boolean getGetSignaturesOnly()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(GETSIGNATURESONLY$2, 0);
                if (target == null)
                {
                    return false;
                }
                return target.getBooleanValue();
            }
        }
        
        /**
         * Gets (as xml) the "getSignaturesOnly" element
         */
        public org.apache.xmlbeans.XmlBoolean xgetGetSignaturesOnly()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBoolean target = null;
                target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(GETSIGNATURESONLY$2, 0);
                return target;
            }
        }
        
        /**
         * True if has "getSignaturesOnly" element
         */
        public boolean isSetGetSignaturesOnly()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(GETSIGNATURESONLY$2) != 0;
            }
        }
        
        /**
         * Sets the "getSignaturesOnly" element
         */
        public void setGetSignaturesOnly(boolean getSignaturesOnly)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(GETSIGNATURESONLY$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(GETSIGNATURESONLY$2);
                }
                target.setBooleanValue(getSignaturesOnly);
            }
        }
        
        /**
         * Sets (as xml) the "getSignaturesOnly" element
         */
        public void xsetGetSignaturesOnly(org.apache.xmlbeans.XmlBoolean getSignaturesOnly)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBoolean target = null;
                target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(GETSIGNATURESONLY$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlBoolean)get_store().add_element_user(GETSIGNATURESONLY$2);
                }
                target.set(getSignaturesOnly);
            }
        }
        
        /**
         * Unsets the "getSignaturesOnly" element
         */
        public void unsetGetSignaturesOnly()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(GETSIGNATURESONLY$2, 0);
            }
        }
    }
}
