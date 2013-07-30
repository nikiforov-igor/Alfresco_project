/*
 * An XML document type.
 * Localname: AuthenticateByCertificateResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.AuthenticateByCertificateResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one AuthenticateByCertificateResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class AuthenticateByCertificateResponseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.AuthenticateByCertificateResponseDocument
{
    private static final long serialVersionUID = 1L;
    
    public AuthenticateByCertificateResponseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName AUTHENTICATEBYCERTIFICATERESPONSE$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "AuthenticateByCertificateResponse");
    
    
    /**
     * Gets the "AuthenticateByCertificateResponse" element
     */
    public org.tempuri.AuthenticateByCertificateResponseDocument.AuthenticateByCertificateResponse getAuthenticateByCertificateResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.AuthenticateByCertificateResponseDocument.AuthenticateByCertificateResponse target = null;
            target = (org.tempuri.AuthenticateByCertificateResponseDocument.AuthenticateByCertificateResponse)get_store().find_element_user(AUTHENTICATEBYCERTIFICATERESPONSE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "AuthenticateByCertificateResponse" element
     */
    public void setAuthenticateByCertificateResponse(org.tempuri.AuthenticateByCertificateResponseDocument.AuthenticateByCertificateResponse authenticateByCertificateResponse)
    {
        generatedSetterHelperImpl(authenticateByCertificateResponse, AUTHENTICATEBYCERTIFICATERESPONSE$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "AuthenticateByCertificateResponse" element
     */
    public org.tempuri.AuthenticateByCertificateResponseDocument.AuthenticateByCertificateResponse addNewAuthenticateByCertificateResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.AuthenticateByCertificateResponseDocument.AuthenticateByCertificateResponse target = null;
            target = (org.tempuri.AuthenticateByCertificateResponseDocument.AuthenticateByCertificateResponse)get_store().add_element_user(AUTHENTICATEBYCERTIFICATERESPONSE$0);
            return target;
        }
    }
    /**
     * An XML AuthenticateByCertificateResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class AuthenticateByCertificateResponseImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.AuthenticateByCertificateResponseDocument.AuthenticateByCertificateResponse
    {
        private static final long serialVersionUID = 1L;
        
        public AuthenticateByCertificateResponseImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName AUTHENTICATEBYCERTIFICATERESULT$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "AuthenticateByCertificateResult");
        private static final javax.xml.namespace.QName TOKEN$2 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "token");
        
        
        /**
         * Gets the "AuthenticateByCertificateResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getAuthenticateByCertificateResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(AUTHENTICATEBYCERTIFICATERESULT$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "AuthenticateByCertificateResult" element
         */
        public boolean isNilAuthenticateByCertificateResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(AUTHENTICATEBYCERTIFICATERESULT$0, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "AuthenticateByCertificateResult" element
         */
        public boolean isSetAuthenticateByCertificateResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(AUTHENTICATEBYCERTIFICATERESULT$0) != 0;
            }
        }
        
        /**
         * Sets the "AuthenticateByCertificateResult" element
         */
        public void setAuthenticateByCertificateResult(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse authenticateByCertificateResult)
        {
            generatedSetterHelperImpl(authenticateByCertificateResult, AUTHENTICATEBYCERTIFICATERESULT$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "AuthenticateByCertificateResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewAuthenticateByCertificateResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(AUTHENTICATEBYCERTIFICATERESULT$0);
                return target;
            }
        }
        
        /**
         * Nils the "AuthenticateByCertificateResult" element
         */
        public void setNilAuthenticateByCertificateResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(AUTHENTICATEBYCERTIFICATERESULT$0, 0);
                if (target == null)
                {
                    target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(AUTHENTICATEBYCERTIFICATERESULT$0);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "AuthenticateByCertificateResult" element
         */
        public void unsetAuthenticateByCertificateResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(AUTHENTICATEBYCERTIFICATERESULT$0, 0);
            }
        }
        
        /**
         * Gets the "token" element
         */
        public java.lang.String getToken()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(TOKEN$2, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "token" element
         */
        public org.apache.xmlbeans.XmlString xgetToken()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TOKEN$2, 0);
                return target;
            }
        }
        
        /**
         * Tests for nil "token" element
         */
        public boolean isNilToken()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TOKEN$2, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "token" element
         */
        public boolean isSetToken()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(TOKEN$2) != 0;
            }
        }
        
        /**
         * Sets the "token" element
         */
        public void setToken(java.lang.String token)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(TOKEN$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(TOKEN$2);
                }
                target.setStringValue(token);
            }
        }
        
        /**
         * Sets (as xml) the "token" element
         */
        public void xsetToken(org.apache.xmlbeans.XmlString token)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TOKEN$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(TOKEN$2);
                }
                target.set(token);
            }
        }
        
        /**
         * Nils the "token" element
         */
        public void setNilToken()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TOKEN$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(TOKEN$2);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "token" element
         */
        public void unsetToken()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(TOKEN$2, 0);
            }
        }
    }
}
