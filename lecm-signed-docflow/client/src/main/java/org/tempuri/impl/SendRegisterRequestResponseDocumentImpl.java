/*
 * An XML document type.
 * Localname: SendRegisterRequestResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.SendRegisterRequestResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one SendRegisterRequestResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class SendRegisterRequestResponseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.SendRegisterRequestResponseDocument
{
    private static final long serialVersionUID = 1L;
    
    public SendRegisterRequestResponseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName SENDREGISTERREQUESTRESPONSE$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "SendRegisterRequestResponse");
    
    
    /**
     * Gets the "SendRegisterRequestResponse" element
     */
    public org.tempuri.SendRegisterRequestResponseDocument.SendRegisterRequestResponse getSendRegisterRequestResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.SendRegisterRequestResponseDocument.SendRegisterRequestResponse target = null;
            target = (org.tempuri.SendRegisterRequestResponseDocument.SendRegisterRequestResponse)get_store().find_element_user(SENDREGISTERREQUESTRESPONSE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "SendRegisterRequestResponse" element
     */
    public void setSendRegisterRequestResponse(org.tempuri.SendRegisterRequestResponseDocument.SendRegisterRequestResponse sendRegisterRequestResponse)
    {
        generatedSetterHelperImpl(sendRegisterRequestResponse, SENDREGISTERREQUESTRESPONSE$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "SendRegisterRequestResponse" element
     */
    public org.tempuri.SendRegisterRequestResponseDocument.SendRegisterRequestResponse addNewSendRegisterRequestResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.SendRegisterRequestResponseDocument.SendRegisterRequestResponse target = null;
            target = (org.tempuri.SendRegisterRequestResponseDocument.SendRegisterRequestResponse)get_store().add_element_user(SENDREGISTERREQUESTRESPONSE$0);
            return target;
        }
    }
    /**
     * An XML SendRegisterRequestResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class SendRegisterRequestResponseImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.SendRegisterRequestResponseDocument.SendRegisterRequestResponse
    {
        private static final long serialVersionUID = 1L;
        
        public SendRegisterRequestResponseImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName SENDREGISTERREQUESTRESULT$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "SendRegisterRequestResult");
        private static final javax.xml.namespace.QName REQUESTID$2 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "requestId");
        
        
        /**
         * Gets the "SendRegisterRequestResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getSendRegisterRequestResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(SENDREGISTERREQUESTRESULT$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "SendRegisterRequestResult" element
         */
        public boolean isNilSendRegisterRequestResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(SENDREGISTERREQUESTRESULT$0, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "SendRegisterRequestResult" element
         */
        public boolean isSetSendRegisterRequestResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(SENDREGISTERREQUESTRESULT$0) != 0;
            }
        }
        
        /**
         * Sets the "SendRegisterRequestResult" element
         */
        public void setSendRegisterRequestResult(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse sendRegisterRequestResult)
        {
            generatedSetterHelperImpl(sendRegisterRequestResult, SENDREGISTERREQUESTRESULT$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "SendRegisterRequestResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewSendRegisterRequestResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(SENDREGISTERREQUESTRESULT$0);
                return target;
            }
        }
        
        /**
         * Nils the "SendRegisterRequestResult" element
         */
        public void setNilSendRegisterRequestResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(SENDREGISTERREQUESTRESULT$0, 0);
                if (target == null)
                {
                    target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(SENDREGISTERREQUESTRESULT$0);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "SendRegisterRequestResult" element
         */
        public void unsetSendRegisterRequestResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(SENDREGISTERREQUESTRESULT$0, 0);
            }
        }
        
        /**
         * Gets the "requestId" element
         */
        public java.lang.String getRequestId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(REQUESTID$2, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "requestId" element
         */
        public org.apache.xmlbeans.XmlString xgetRequestId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(REQUESTID$2, 0);
                return target;
            }
        }
        
        /**
         * Tests for nil "requestId" element
         */
        public boolean isNilRequestId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(REQUESTID$2, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "requestId" element
         */
        public boolean isSetRequestId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(REQUESTID$2) != 0;
            }
        }
        
        /**
         * Sets the "requestId" element
         */
        public void setRequestId(java.lang.String requestId)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(REQUESTID$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(REQUESTID$2);
                }
                target.setStringValue(requestId);
            }
        }
        
        /**
         * Sets (as xml) the "requestId" element
         */
        public void xsetRequestId(org.apache.xmlbeans.XmlString requestId)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(REQUESTID$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(REQUESTID$2);
                }
                target.set(requestId);
            }
        }
        
        /**
         * Nils the "requestId" element
         */
        public void setNilRequestId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(REQUESTID$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(REQUESTID$2);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "requestId" element
         */
        public void unsetRequestId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(REQUESTID$2, 0);
            }
        }
    }
}
