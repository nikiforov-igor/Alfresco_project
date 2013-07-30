/*
 * An XML document type.
 * Localname: FinishSmsAuthenticationResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.FinishSmsAuthenticationResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one FinishSmsAuthenticationResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class FinishSmsAuthenticationResponseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.FinishSmsAuthenticationResponseDocument
{
    private static final long serialVersionUID = 1L;
    
    public FinishSmsAuthenticationResponseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName FINISHSMSAUTHENTICATIONRESPONSE$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "FinishSmsAuthenticationResponse");
    
    
    /**
     * Gets the "FinishSmsAuthenticationResponse" element
     */
    public org.tempuri.FinishSmsAuthenticationResponseDocument.FinishSmsAuthenticationResponse getFinishSmsAuthenticationResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.FinishSmsAuthenticationResponseDocument.FinishSmsAuthenticationResponse target = null;
            target = (org.tempuri.FinishSmsAuthenticationResponseDocument.FinishSmsAuthenticationResponse)get_store().find_element_user(FINISHSMSAUTHENTICATIONRESPONSE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "FinishSmsAuthenticationResponse" element
     */
    public void setFinishSmsAuthenticationResponse(org.tempuri.FinishSmsAuthenticationResponseDocument.FinishSmsAuthenticationResponse finishSmsAuthenticationResponse)
    {
        generatedSetterHelperImpl(finishSmsAuthenticationResponse, FINISHSMSAUTHENTICATIONRESPONSE$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "FinishSmsAuthenticationResponse" element
     */
    public org.tempuri.FinishSmsAuthenticationResponseDocument.FinishSmsAuthenticationResponse addNewFinishSmsAuthenticationResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.FinishSmsAuthenticationResponseDocument.FinishSmsAuthenticationResponse target = null;
            target = (org.tempuri.FinishSmsAuthenticationResponseDocument.FinishSmsAuthenticationResponse)get_store().add_element_user(FINISHSMSAUTHENTICATIONRESPONSE$0);
            return target;
        }
    }
    /**
     * An XML FinishSmsAuthenticationResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class FinishSmsAuthenticationResponseImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.FinishSmsAuthenticationResponseDocument.FinishSmsAuthenticationResponse
    {
        private static final long serialVersionUID = 1L;
        
        public FinishSmsAuthenticationResponseImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName FINISHSMSAUTHENTICATIONRESULT$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "FinishSmsAuthenticationResult");
        private static final javax.xml.namespace.QName TOKEN$2 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "token");
        
        
        /**
         * Gets the "FinishSmsAuthenticationResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getFinishSmsAuthenticationResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(FINISHSMSAUTHENTICATIONRESULT$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "FinishSmsAuthenticationResult" element
         */
        public boolean isNilFinishSmsAuthenticationResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(FINISHSMSAUTHENTICATIONRESULT$0, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "FinishSmsAuthenticationResult" element
         */
        public boolean isSetFinishSmsAuthenticationResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(FINISHSMSAUTHENTICATIONRESULT$0) != 0;
            }
        }
        
        /**
         * Sets the "FinishSmsAuthenticationResult" element
         */
        public void setFinishSmsAuthenticationResult(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse finishSmsAuthenticationResult)
        {
            generatedSetterHelperImpl(finishSmsAuthenticationResult, FINISHSMSAUTHENTICATIONRESULT$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "FinishSmsAuthenticationResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewFinishSmsAuthenticationResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(FINISHSMSAUTHENTICATIONRESULT$0);
                return target;
            }
        }
        
        /**
         * Nils the "FinishSmsAuthenticationResult" element
         */
        public void setNilFinishSmsAuthenticationResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(FINISHSMSAUTHENTICATIONRESULT$0, 0);
                if (target == null)
                {
                    target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(FINISHSMSAUTHENTICATIONRESULT$0);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "FinishSmsAuthenticationResult" element
         */
        public void unsetFinishSmsAuthenticationResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(FINISHSMSAUTHENTICATIONRESULT$0, 0);
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
