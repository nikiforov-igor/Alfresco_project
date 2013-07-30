/*
 * An XML document type.
 * Localname: StartSmsAuthenticationResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.StartSmsAuthenticationResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one StartSmsAuthenticationResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class StartSmsAuthenticationResponseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.StartSmsAuthenticationResponseDocument
{
    private static final long serialVersionUID = 1L;
    
    public StartSmsAuthenticationResponseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName STARTSMSAUTHENTICATIONRESPONSE$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "StartSmsAuthenticationResponse");
    
    
    /**
     * Gets the "StartSmsAuthenticationResponse" element
     */
    public org.tempuri.StartSmsAuthenticationResponseDocument.StartSmsAuthenticationResponse getStartSmsAuthenticationResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.StartSmsAuthenticationResponseDocument.StartSmsAuthenticationResponse target = null;
            target = (org.tempuri.StartSmsAuthenticationResponseDocument.StartSmsAuthenticationResponse)get_store().find_element_user(STARTSMSAUTHENTICATIONRESPONSE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "StartSmsAuthenticationResponse" element
     */
    public void setStartSmsAuthenticationResponse(org.tempuri.StartSmsAuthenticationResponseDocument.StartSmsAuthenticationResponse startSmsAuthenticationResponse)
    {
        generatedSetterHelperImpl(startSmsAuthenticationResponse, STARTSMSAUTHENTICATIONRESPONSE$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "StartSmsAuthenticationResponse" element
     */
    public org.tempuri.StartSmsAuthenticationResponseDocument.StartSmsAuthenticationResponse addNewStartSmsAuthenticationResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.StartSmsAuthenticationResponseDocument.StartSmsAuthenticationResponse target = null;
            target = (org.tempuri.StartSmsAuthenticationResponseDocument.StartSmsAuthenticationResponse)get_store().add_element_user(STARTSMSAUTHENTICATIONRESPONSE$0);
            return target;
        }
    }
    /**
     * An XML StartSmsAuthenticationResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class StartSmsAuthenticationResponseImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.StartSmsAuthenticationResponseDocument.StartSmsAuthenticationResponse
    {
        private static final long serialVersionUID = 1L;
        
        public StartSmsAuthenticationResponseImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName STARTSMSAUTHENTICATIONRESULT$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "StartSmsAuthenticationResult");
        
        
        /**
         * Gets the "StartSmsAuthenticationResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getStartSmsAuthenticationResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(STARTSMSAUTHENTICATIONRESULT$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "StartSmsAuthenticationResult" element
         */
        public boolean isNilStartSmsAuthenticationResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(STARTSMSAUTHENTICATIONRESULT$0, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "StartSmsAuthenticationResult" element
         */
        public boolean isSetStartSmsAuthenticationResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(STARTSMSAUTHENTICATIONRESULT$0) != 0;
            }
        }
        
        /**
         * Sets the "StartSmsAuthenticationResult" element
         */
        public void setStartSmsAuthenticationResult(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse startSmsAuthenticationResult)
        {
            generatedSetterHelperImpl(startSmsAuthenticationResult, STARTSMSAUTHENTICATIONRESULT$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "StartSmsAuthenticationResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewStartSmsAuthenticationResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(STARTSMSAUTHENTICATIONRESULT$0);
                return target;
            }
        }
        
        /**
         * Nils the "StartSmsAuthenticationResult" element
         */
        public void setNilStartSmsAuthenticationResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(STARTSMSAUTHENTICATIONRESULT$0, 0);
                if (target == null)
                {
                    target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(STARTSMSAUTHENTICATIONRESULT$0);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "StartSmsAuthenticationResult" element
         */
        public void unsetStartSmsAuthenticationResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(STARTSMSAUTHENTICATIONRESULT$0, 0);
            }
        }
    }
}
