/*
 * An XML document type.
 * Localname: SendRegisterRequest
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.SendRegisterRequestDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one SendRegisterRequest(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class SendRegisterRequestDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.SendRegisterRequestDocument
{
    private static final long serialVersionUID = 1L;
    
    public SendRegisterRequestDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName SENDREGISTERREQUEST$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "SendRegisterRequest");
    
    
    /**
     * Gets the "SendRegisterRequest" element
     */
    public org.tempuri.SendRegisterRequestDocument.SendRegisterRequest getSendRegisterRequest()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.SendRegisterRequestDocument.SendRegisterRequest target = null;
            target = (org.tempuri.SendRegisterRequestDocument.SendRegisterRequest)get_store().find_element_user(SENDREGISTERREQUEST$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "SendRegisterRequest" element
     */
    public void setSendRegisterRequest(org.tempuri.SendRegisterRequestDocument.SendRegisterRequest sendRegisterRequest)
    {
        generatedSetterHelperImpl(sendRegisterRequest, SENDREGISTERREQUEST$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "SendRegisterRequest" element
     */
    public org.tempuri.SendRegisterRequestDocument.SendRegisterRequest addNewSendRegisterRequest()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.SendRegisterRequestDocument.SendRegisterRequest target = null;
            target = (org.tempuri.SendRegisterRequestDocument.SendRegisterRequest)get_store().add_element_user(SENDREGISTERREQUEST$0);
            return target;
        }
    }
    /**
     * An XML SendRegisterRequest(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class SendRegisterRequestImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.SendRegisterRequestDocument.SendRegisterRequest
    {
        private static final long serialVersionUID = 1L;
        
        public SendRegisterRequestImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName REQUESTBINARY$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "requestBinary");
        private static final javax.xml.namespace.QName SIGNATURE$2 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "signature");
        private static final javax.xml.namespace.QName OPERATORCODE$4 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "operatorCode");
        
        
        /**
         * Gets the "requestBinary" element
         */
        public byte[] getRequestBinary()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(REQUESTBINARY$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getByteArrayValue();
            }
        }
        
        /**
         * Gets (as xml) the "requestBinary" element
         */
        public org.apache.xmlbeans.XmlBase64Binary xgetRequestBinary()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBase64Binary target = null;
                target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(REQUESTBINARY$0, 0);
                return target;
            }
        }
        
        /**
         * Tests for nil "requestBinary" element
         */
        public boolean isNilRequestBinary()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBase64Binary target = null;
                target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(REQUESTBINARY$0, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "requestBinary" element
         */
        public boolean isSetRequestBinary()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(REQUESTBINARY$0) != 0;
            }
        }
        
        /**
         * Sets the "requestBinary" element
         */
        public void setRequestBinary(byte[] requestBinary)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(REQUESTBINARY$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(REQUESTBINARY$0);
                }
                target.setByteArrayValue(requestBinary);
            }
        }
        
        /**
         * Sets (as xml) the "requestBinary" element
         */
        public void xsetRequestBinary(org.apache.xmlbeans.XmlBase64Binary requestBinary)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBase64Binary target = null;
                target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(REQUESTBINARY$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlBase64Binary)get_store().add_element_user(REQUESTBINARY$0);
                }
                target.set(requestBinary);
            }
        }
        
        /**
         * Nils the "requestBinary" element
         */
        public void setNilRequestBinary()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBase64Binary target = null;
                target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(REQUESTBINARY$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlBase64Binary)get_store().add_element_user(REQUESTBINARY$0);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "requestBinary" element
         */
        public void unsetRequestBinary()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(REQUESTBINARY$0, 0);
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
