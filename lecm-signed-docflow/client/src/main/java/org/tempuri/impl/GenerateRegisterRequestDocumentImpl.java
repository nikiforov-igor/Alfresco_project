/*
 * An XML document type.
 * Localname: GenerateRegisterRequest
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.GenerateRegisterRequestDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one GenerateRegisterRequest(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class GenerateRegisterRequestDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GenerateRegisterRequestDocument
{
    private static final long serialVersionUID = 1L;
    
    public GenerateRegisterRequestDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName GENERATEREGISTERREQUEST$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "GenerateRegisterRequest");
    
    
    /**
     * Gets the "GenerateRegisterRequest" element
     */
    public org.tempuri.GenerateRegisterRequestDocument.GenerateRegisterRequest getGenerateRegisterRequest()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GenerateRegisterRequestDocument.GenerateRegisterRequest target = null;
            target = (org.tempuri.GenerateRegisterRequestDocument.GenerateRegisterRequest)get_store().find_element_user(GENERATEREGISTERREQUEST$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "GenerateRegisterRequest" element
     */
    public void setGenerateRegisterRequest(org.tempuri.GenerateRegisterRequestDocument.GenerateRegisterRequest generateRegisterRequest)
    {
        generatedSetterHelperImpl(generateRegisterRequest, GENERATEREGISTERREQUEST$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "GenerateRegisterRequest" element
     */
    public org.tempuri.GenerateRegisterRequestDocument.GenerateRegisterRequest addNewGenerateRegisterRequest()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GenerateRegisterRequestDocument.GenerateRegisterRequest target = null;
            target = (org.tempuri.GenerateRegisterRequestDocument.GenerateRegisterRequest)get_store().add_element_user(GENERATEREGISTERREQUEST$0);
            return target;
        }
    }
    /**
     * An XML GenerateRegisterRequest(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class GenerateRegisterRequestImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GenerateRegisterRequestDocument.GenerateRegisterRequest
    {
        private static final long serialVersionUID = 1L;
        
        public GenerateRegisterRequestImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName REQUEST$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "request");
        private static final javax.xml.namespace.QName OPERATORCODE$2 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "operatorCode");
        
        
        /**
         * Gets the "request" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert getRequest()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert)get_store().find_element_user(REQUEST$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "request" element
         */
        public boolean isNilRequest()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert)get_store().find_element_user(REQUEST$0, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "request" element
         */
        public boolean isSetRequest()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(REQUEST$0) != 0;
            }
        }
        
        /**
         * Sets the "request" element
         */
        public void setRequest(org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert request)
        {
            generatedSetterHelperImpl(request, REQUEST$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "request" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert addNewRequest()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert)get_store().add_element_user(REQUEST$0);
                return target;
            }
        }
        
        /**
         * Nils the "request" element
         */
        public void setNilRequest()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert)get_store().find_element_user(REQUEST$0, 0);
                if (target == null)
                {
                    target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert)get_store().add_element_user(REQUEST$0);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "request" element
         */
        public void unsetRequest()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(REQUEST$0, 0);
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
