/*
 * An XML document type.
 * Localname: SignDocument
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.SignDocumentDocument1
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one SignDocument(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class SignDocumentDocument1Impl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.SignDocumentDocument1
{
    private static final long serialVersionUID = 1L;
    
    public SignDocumentDocument1Impl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName SIGNDOCUMENT$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "SignDocument");
    
    
    /**
     * Gets the "SignDocument" element
     */
    public org.tempuri.SignDocumentDocument1.SignDocument getSignDocument()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.SignDocumentDocument1.SignDocument target = null;
            target = (org.tempuri.SignDocumentDocument1.SignDocument)get_store().find_element_user(SIGNDOCUMENT$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "SignDocument" element
     */
    public void setSignDocument(org.tempuri.SignDocumentDocument1.SignDocument signDocument)
    {
        generatedSetterHelperImpl(signDocument, SIGNDOCUMENT$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "SignDocument" element
     */
    public org.tempuri.SignDocumentDocument1.SignDocument addNewSignDocument()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.SignDocumentDocument1.SignDocument target = null;
            target = (org.tempuri.SignDocumentDocument1.SignDocument)get_store().add_element_user(SIGNDOCUMENT$0);
            return target;
        }
    }
    /**
     * An XML SignDocument(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class SignDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.SignDocumentDocument1.SignDocument
    {
        private static final long serialVersionUID = 1L;
        
        public SignDocumentImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName CONTENT$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "content");
        private static final javax.xml.namespace.QName OPERATORCODE$2 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "operatorCode");
        
        
        /**
         * Gets the "content" element
         */
        public byte[] getContent()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CONTENT$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getByteArrayValue();
            }
        }
        
        /**
         * Gets (as xml) the "content" element
         */
        public org.apache.xmlbeans.XmlBase64Binary xgetContent()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBase64Binary target = null;
                target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(CONTENT$0, 0);
                return target;
            }
        }
        
        /**
         * Tests for nil "content" element
         */
        public boolean isNilContent()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBase64Binary target = null;
                target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(CONTENT$0, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "content" element
         */
        public boolean isSetContent()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(CONTENT$0) != 0;
            }
        }
        
        /**
         * Sets the "content" element
         */
        public void setContent(byte[] content)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CONTENT$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(CONTENT$0);
                }
                target.setByteArrayValue(content);
            }
        }
        
        /**
         * Sets (as xml) the "content" element
         */
        public void xsetContent(org.apache.xmlbeans.XmlBase64Binary content)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBase64Binary target = null;
                target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(CONTENT$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlBase64Binary)get_store().add_element_user(CONTENT$0);
                }
                target.set(content);
            }
        }
        
        /**
         * Nils the "content" element
         */
        public void setNilContent()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBase64Binary target = null;
                target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(CONTENT$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlBase64Binary)get_store().add_element_user(CONTENT$0);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "content" element
         */
        public void unsetContent()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(CONTENT$0, 0);
            }
        }
        
        /**
         * Gets the "operatorCode" element
         */
        public java.lang.String getOperatorCode()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(OPERATORCODE$2, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "operatorCode" element
         */
        public org.apache.xmlbeans.XmlString xgetOperatorCode()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORCODE$2, 0);
                return target;
            }
        }
        
        /**
         * Tests for nil "operatorCode" element
         */
        public boolean isNilOperatorCode()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORCODE$2, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "operatorCode" element
         */
        public boolean isSetOperatorCode()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(OPERATORCODE$2) != 0;
            }
        }
        
        /**
         * Sets the "operatorCode" element
         */
        public void setOperatorCode(java.lang.String operatorCode)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(OPERATORCODE$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(OPERATORCODE$2);
                }
                target.setStringValue(operatorCode);
            }
        }
        
        /**
         * Sets (as xml) the "operatorCode" element
         */
        public void xsetOperatorCode(org.apache.xmlbeans.XmlString operatorCode)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORCODE$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(OPERATORCODE$2);
                }
                target.set(operatorCode);
            }
        }
        
        /**
         * Nils the "operatorCode" element
         */
        public void setNilOperatorCode()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORCODE$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(OPERATORCODE$2);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "operatorCode" element
         */
        public void unsetOperatorCode()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(OPERATORCODE$2, 0);
            }
        }
    }
}
