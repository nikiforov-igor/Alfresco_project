/*
 * An XML document type.
 * Localname: VerifySignature
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.VerifySignatureDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one VerifySignature(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class VerifySignatureDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.VerifySignatureDocument
{
    private static final long serialVersionUID = 1L;
    
    public VerifySignatureDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName VERIFYSIGNATURE$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "VerifySignature");
    
    
    /**
     * Gets the "VerifySignature" element
     */
    public org.tempuri.VerifySignatureDocument.VerifySignature getVerifySignature()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.VerifySignatureDocument.VerifySignature target = null;
            target = (org.tempuri.VerifySignatureDocument.VerifySignature)get_store().find_element_user(VERIFYSIGNATURE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "VerifySignature" element
     */
    public void setVerifySignature(org.tempuri.VerifySignatureDocument.VerifySignature verifySignature)
    {
        generatedSetterHelperImpl(verifySignature, VERIFYSIGNATURE$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "VerifySignature" element
     */
    public org.tempuri.VerifySignatureDocument.VerifySignature addNewVerifySignature()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.VerifySignatureDocument.VerifySignature target = null;
            target = (org.tempuri.VerifySignatureDocument.VerifySignature)get_store().add_element_user(VERIFYSIGNATURE$0);
            return target;
        }
    }
    /**
     * An XML VerifySignature(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class VerifySignatureImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.VerifySignatureDocument.VerifySignature
    {
        private static final long serialVersionUID = 1L;
        
        public VerifySignatureImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName CONTENT$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "content");
        private static final javax.xml.namespace.QName SIGNATURE$2 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "signature");
        private static final javax.xml.namespace.QName OPERATORCODE$4 = 
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
         * Gets the "signature" element
         */
        public byte[] getSignature()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SIGNATURE$2, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getByteArrayValue();
            }
        }
        
        /**
         * Gets (as xml) the "signature" element
         */
        public org.apache.xmlbeans.XmlBase64Binary xgetSignature()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBase64Binary target = null;
                target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(SIGNATURE$2, 0);
                return target;
            }
        }
        
        /**
         * Tests for nil "signature" element
         */
        public boolean isNilSignature()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBase64Binary target = null;
                target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(SIGNATURE$2, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "signature" element
         */
        public boolean isSetSignature()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(SIGNATURE$2) != 0;
            }
        }
        
        /**
         * Sets the "signature" element
         */
        public void setSignature(byte[] signature)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SIGNATURE$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(SIGNATURE$2);
                }
                target.setByteArrayValue(signature);
            }
        }
        
        /**
         * Sets (as xml) the "signature" element
         */
        public void xsetSignature(org.apache.xmlbeans.XmlBase64Binary signature)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBase64Binary target = null;
                target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(SIGNATURE$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlBase64Binary)get_store().add_element_user(SIGNATURE$2);
                }
                target.set(signature);
            }
        }
        
        /**
         * Nils the "signature" element
         */
        public void setNilSignature()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBase64Binary target = null;
                target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(SIGNATURE$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlBase64Binary)get_store().add_element_user(SIGNATURE$2);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "signature" element
         */
        public void unsetSignature()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(SIGNATURE$2, 0);
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
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(OPERATORCODE$4, 0);
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
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORCODE$4, 0);
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
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORCODE$4, 0);
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
                return get_store().count_elements(OPERATORCODE$4) != 0;
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
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(OPERATORCODE$4, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(OPERATORCODE$4);
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
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORCODE$4, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(OPERATORCODE$4);
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
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORCODE$4, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(OPERATORCODE$4);
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
                get_store().remove_element(OPERATORCODE$4, 0);
            }
        }
    }
}
