/*
 * An XML document type.
 * Localname: GetRegisterResponseResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.GetRegisterResponseResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one GetRegisterResponseResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class GetRegisterResponseResponseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GetRegisterResponseResponseDocument
{
    private static final long serialVersionUID = 1L;
    
    public GetRegisterResponseResponseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName GETREGISTERRESPONSERESPONSE$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "GetRegisterResponseResponse");
    
    
    /**
     * Gets the "GetRegisterResponseResponse" element
     */
    public org.tempuri.GetRegisterResponseResponseDocument.GetRegisterResponseResponse getGetRegisterResponseResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GetRegisterResponseResponseDocument.GetRegisterResponseResponse target = null;
            target = (org.tempuri.GetRegisterResponseResponseDocument.GetRegisterResponseResponse)get_store().find_element_user(GETREGISTERRESPONSERESPONSE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "GetRegisterResponseResponse" element
     */
    public void setGetRegisterResponseResponse(org.tempuri.GetRegisterResponseResponseDocument.GetRegisterResponseResponse getRegisterResponseResponse)
    {
        generatedSetterHelperImpl(getRegisterResponseResponse, GETREGISTERRESPONSERESPONSE$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "GetRegisterResponseResponse" element
     */
    public org.tempuri.GetRegisterResponseResponseDocument.GetRegisterResponseResponse addNewGetRegisterResponseResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GetRegisterResponseResponseDocument.GetRegisterResponseResponse target = null;
            target = (org.tempuri.GetRegisterResponseResponseDocument.GetRegisterResponseResponse)get_store().add_element_user(GETREGISTERRESPONSERESPONSE$0);
            return target;
        }
    }
    /**
     * An XML GetRegisterResponseResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class GetRegisterResponseResponseImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GetRegisterResponseResponseDocument.GetRegisterResponseResponse
    {
        private static final long serialVersionUID = 1L;
        
        public GetRegisterResponseResponseImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName GETREGISTERRESPONSERESULT$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "GetRegisterResponseResult");
        private static final javax.xml.namespace.QName OPERATORRESPONSE$2 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "operatorResponse");
        
        
        /**
         * Gets the "GetRegisterResponseResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getGetRegisterResponseResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(GETREGISTERRESPONSERESULT$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "GetRegisterResponseResult" element
         */
        public boolean isNilGetRegisterResponseResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(GETREGISTERRESPONSERESULT$0, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "GetRegisterResponseResult" element
         */
        public boolean isSetGetRegisterResponseResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(GETREGISTERRESPONSERESULT$0) != 0;
            }
        }
        
        /**
         * Sets the "GetRegisterResponseResult" element
         */
        public void setGetRegisterResponseResult(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getRegisterResponseResult)
        {
            generatedSetterHelperImpl(getRegisterResponseResult, GETREGISTERRESPONSERESULT$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "GetRegisterResponseResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewGetRegisterResponseResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(GETREGISTERRESPONSERESULT$0);
                return target;
            }
        }
        
        /**
         * Nils the "GetRegisterResponseResult" element
         */
        public void setNilGetRegisterResponseResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(GETREGISTERRESPONSERESULT$0, 0);
                if (target == null)
                {
                    target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(GETREGISTERRESPONSERESULT$0);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "GetRegisterResponseResult" element
         */
        public void unsetGetRegisterResponseResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(GETREGISTERRESPONSERESULT$0, 0);
            }
        }
        
        /**
         * Gets the "operatorResponse" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse getOperatorResponse()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse)get_store().find_element_user(OPERATORRESPONSE$2, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "operatorResponse" element
         */
        public boolean isNilOperatorResponse()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse)get_store().find_element_user(OPERATORRESPONSE$2, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "operatorResponse" element
         */
        public boolean isSetOperatorResponse()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(OPERATORRESPONSE$2) != 0;
            }
        }
        
        /**
         * Sets the "operatorResponse" element
         */
        public void setOperatorResponse(org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse operatorResponse)
        {
            generatedSetterHelperImpl(operatorResponse, OPERATORRESPONSE$2, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "operatorResponse" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse addNewOperatorResponse()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse)get_store().add_element_user(OPERATORRESPONSE$2);
                return target;
            }
        }
        
        /**
         * Nils the "operatorResponse" element
         */
        public void setNilOperatorResponse()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse)get_store().find_element_user(OPERATORRESPONSE$2, 0);
                if (target == null)
                {
                    target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse)get_store().add_element_user(OPERATORRESPONSE$2);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "operatorResponse" element
         */
        public void unsetOperatorResponse()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(OPERATORRESPONSE$2, 0);
            }
        }
    }
}
