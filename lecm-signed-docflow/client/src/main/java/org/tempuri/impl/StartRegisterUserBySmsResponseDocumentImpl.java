/*
 * An XML document type.
 * Localname: StartRegisterUserBySmsResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.StartRegisterUserBySmsResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one StartRegisterUserBySmsResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class StartRegisterUserBySmsResponseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.StartRegisterUserBySmsResponseDocument
{
    private static final long serialVersionUID = 1L;
    
    public StartRegisterUserBySmsResponseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName STARTREGISTERUSERBYSMSRESPONSE$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "StartRegisterUserBySmsResponse");
    
    
    /**
     * Gets the "StartRegisterUserBySmsResponse" element
     */
    public org.tempuri.StartRegisterUserBySmsResponseDocument.StartRegisterUserBySmsResponse getStartRegisterUserBySmsResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.StartRegisterUserBySmsResponseDocument.StartRegisterUserBySmsResponse target = null;
            target = (org.tempuri.StartRegisterUserBySmsResponseDocument.StartRegisterUserBySmsResponse)get_store().find_element_user(STARTREGISTERUSERBYSMSRESPONSE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "StartRegisterUserBySmsResponse" element
     */
    public void setStartRegisterUserBySmsResponse(org.tempuri.StartRegisterUserBySmsResponseDocument.StartRegisterUserBySmsResponse startRegisterUserBySmsResponse)
    {
        generatedSetterHelperImpl(startRegisterUserBySmsResponse, STARTREGISTERUSERBYSMSRESPONSE$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "StartRegisterUserBySmsResponse" element
     */
    public org.tempuri.StartRegisterUserBySmsResponseDocument.StartRegisterUserBySmsResponse addNewStartRegisterUserBySmsResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.StartRegisterUserBySmsResponseDocument.StartRegisterUserBySmsResponse target = null;
            target = (org.tempuri.StartRegisterUserBySmsResponseDocument.StartRegisterUserBySmsResponse)get_store().add_element_user(STARTREGISTERUSERBYSMSRESPONSE$0);
            return target;
        }
    }
    /**
     * An XML StartRegisterUserBySmsResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class StartRegisterUserBySmsResponseImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.StartRegisterUserBySmsResponseDocument.StartRegisterUserBySmsResponse
    {
        private static final long serialVersionUID = 1L;
        
        public StartRegisterUserBySmsResponseImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName STARTREGISTERUSERBYSMSRESULT$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "StartRegisterUserBySmsResult");
        
        
        /**
         * Gets the "StartRegisterUserBySmsResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getStartRegisterUserBySmsResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(STARTREGISTERUSERBYSMSRESULT$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "StartRegisterUserBySmsResult" element
         */
        public boolean isNilStartRegisterUserBySmsResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(STARTREGISTERUSERBYSMSRESULT$0, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "StartRegisterUserBySmsResult" element
         */
        public boolean isSetStartRegisterUserBySmsResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(STARTREGISTERUSERBYSMSRESULT$0) != 0;
            }
        }
        
        /**
         * Sets the "StartRegisterUserBySmsResult" element
         */
        public void setStartRegisterUserBySmsResult(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse startRegisterUserBySmsResult)
        {
            generatedSetterHelperImpl(startRegisterUserBySmsResult, STARTREGISTERUSERBYSMSRESULT$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "StartRegisterUserBySmsResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewStartRegisterUserBySmsResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(STARTREGISTERUSERBYSMSRESULT$0);
                return target;
            }
        }
        
        /**
         * Nils the "StartRegisterUserBySmsResult" element
         */
        public void setNilStartRegisterUserBySmsResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(STARTREGISTERUSERBYSMSRESULT$0, 0);
                if (target == null)
                {
                    target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(STARTREGISTERUSERBYSMSRESULT$0);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "StartRegisterUserBySmsResult" element
         */
        public void unsetStartRegisterUserBySmsResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(STARTREGISTERUSERBYSMSRESULT$0, 0);
            }
        }
    }
}
