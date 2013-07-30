/*
 * An XML document type.
 * Localname: AuthenticateByCertificate
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.AuthenticateByCertificateDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one AuthenticateByCertificate(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class AuthenticateByCertificateDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.AuthenticateByCertificateDocument
{
    private static final long serialVersionUID = 1L;
    
    public AuthenticateByCertificateDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName AUTHENTICATEBYCERTIFICATE$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "AuthenticateByCertificate");
    
    
    /**
     * Gets the "AuthenticateByCertificate" element
     */
    public org.tempuri.AuthenticateByCertificateDocument.AuthenticateByCertificate getAuthenticateByCertificate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.AuthenticateByCertificateDocument.AuthenticateByCertificate target = null;
            target = (org.tempuri.AuthenticateByCertificateDocument.AuthenticateByCertificate)get_store().find_element_user(AUTHENTICATEBYCERTIFICATE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "AuthenticateByCertificate" element
     */
    public void setAuthenticateByCertificate(org.tempuri.AuthenticateByCertificateDocument.AuthenticateByCertificate authenticateByCertificate)
    {
        generatedSetterHelperImpl(authenticateByCertificate, AUTHENTICATEBYCERTIFICATE$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "AuthenticateByCertificate" element
     */
    public org.tempuri.AuthenticateByCertificateDocument.AuthenticateByCertificate addNewAuthenticateByCertificate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.AuthenticateByCertificateDocument.AuthenticateByCertificate target = null;
            target = (org.tempuri.AuthenticateByCertificateDocument.AuthenticateByCertificate)get_store().add_element_user(AUTHENTICATEBYCERTIFICATE$0);
            return target;
        }
    }
    /**
     * An XML AuthenticateByCertificate(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class AuthenticateByCertificateImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.AuthenticateByCertificateDocument.AuthenticateByCertificate
    {
        private static final long serialVersionUID = 1L;
        
        public AuthenticateByCertificateImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName OPERATORCODE$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "operatorCode");
        private static final javax.xml.namespace.QName SIGNATURE$2 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "signature");
        private static final javax.xml.namespace.QName SIGNEDDATA$4 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "signedData");
        
        
        /**
         * Gets the "operatorCode" element
         */
        public java.lang.String getOperatorCode()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(OPERATORCODE$0, 0);
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
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORCODE$0, 0);
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
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORCODE$0, 0);
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
                return get_store().count_elements(OPERATORCODE$0) != 0;
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
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(OPERATORCODE$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(OPERATORCODE$0);
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
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORCODE$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(OPERATORCODE$0);
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
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORCODE$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(OPERATORCODE$0);
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
                get_store().remove_element(OPERATORCODE$0, 0);
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
         * Gets the "signedData" element
         */
        public java.lang.String getSignedData()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SIGNEDDATA$4, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "signedData" element
         */
        public org.apache.xmlbeans.XmlString xgetSignedData()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SIGNEDDATA$4, 0);
                return target;
            }
        }
        
        /**
         * Tests for nil "signedData" element
         */
        public boolean isNilSignedData()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SIGNEDDATA$4, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "signedData" element
         */
        public boolean isSetSignedData()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(SIGNEDDATA$4) != 0;
            }
        }
        
        /**
         * Sets the "signedData" element
         */
        public void setSignedData(java.lang.String signedData)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SIGNEDDATA$4, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(SIGNEDDATA$4);
                }
                target.setStringValue(signedData);
            }
        }
        
        /**
         * Sets (as xml) the "signedData" element
         */
        public void xsetSignedData(org.apache.xmlbeans.XmlString signedData)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SIGNEDDATA$4, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(SIGNEDDATA$4);
                }
                target.set(signedData);
            }
        }
        
        /**
         * Nils the "signedData" element
         */
        public void setNilSignedData()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SIGNEDDATA$4, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(SIGNEDDATA$4);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "signedData" element
         */
        public void unsetSignedData()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(SIGNEDDATA$4, 0);
            }
        }
    }
}
