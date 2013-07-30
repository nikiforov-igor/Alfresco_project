/*
 * An XML document type.
 * Localname: GetRegistrationInfoResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.GetRegistrationInfoResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one GetRegistrationInfoResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class GetRegistrationInfoResponseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GetRegistrationInfoResponseDocument
{
    private static final long serialVersionUID = 1L;
    
    public GetRegistrationInfoResponseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName GETREGISTRATIONINFORESPONSE$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "GetRegistrationInfoResponse");
    
    
    /**
     * Gets the "GetRegistrationInfoResponse" element
     */
    public org.tempuri.GetRegistrationInfoResponseDocument.GetRegistrationInfoResponse getGetRegistrationInfoResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GetRegistrationInfoResponseDocument.GetRegistrationInfoResponse target = null;
            target = (org.tempuri.GetRegistrationInfoResponseDocument.GetRegistrationInfoResponse)get_store().find_element_user(GETREGISTRATIONINFORESPONSE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "GetRegistrationInfoResponse" element
     */
    public void setGetRegistrationInfoResponse(org.tempuri.GetRegistrationInfoResponseDocument.GetRegistrationInfoResponse getRegistrationInfoResponse)
    {
        generatedSetterHelperImpl(getRegistrationInfoResponse, GETREGISTRATIONINFORESPONSE$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "GetRegistrationInfoResponse" element
     */
    public org.tempuri.GetRegistrationInfoResponseDocument.GetRegistrationInfoResponse addNewGetRegistrationInfoResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GetRegistrationInfoResponseDocument.GetRegistrationInfoResponse target = null;
            target = (org.tempuri.GetRegistrationInfoResponseDocument.GetRegistrationInfoResponse)get_store().add_element_user(GETREGISTRATIONINFORESPONSE$0);
            return target;
        }
    }
    /**
     * An XML GetRegistrationInfoResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class GetRegistrationInfoResponseImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GetRegistrationInfoResponseDocument.GetRegistrationInfoResponse
    {
        private static final long serialVersionUID = 1L;
        
        public GetRegistrationInfoResponseImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName GETREGISTRATIONINFORESULT$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "GetRegistrationInfoResult");
        private static final javax.xml.namespace.QName REGISTRATIONINFOS$2 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "registrationInfos");
        
        
        /**
         * Gets the "GetRegistrationInfoResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getGetRegistrationInfoResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(GETREGISTRATIONINFORESULT$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "GetRegistrationInfoResult" element
         */
        public boolean isNilGetRegistrationInfoResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(GETREGISTRATIONINFORESULT$0, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "GetRegistrationInfoResult" element
         */
        public boolean isSetGetRegistrationInfoResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(GETREGISTRATIONINFORESULT$0) != 0;
            }
        }
        
        /**
         * Sets the "GetRegistrationInfoResult" element
         */
        public void setGetRegistrationInfoResult(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getRegistrationInfoResult)
        {
            generatedSetterHelperImpl(getRegistrationInfoResult, GETREGISTRATIONINFORESULT$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "GetRegistrationInfoResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewGetRegistrationInfoResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(GETREGISTRATIONINFORESULT$0);
                return target;
            }
        }
        
        /**
         * Nils the "GetRegistrationInfoResult" element
         */
        public void setNilGetRegistrationInfoResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(GETREGISTRATIONINFORESULT$0, 0);
                if (target == null)
                {
                    target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(GETREGISTRATIONINFORESULT$0);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "GetRegistrationInfoResult" element
         */
        public void unsetGetRegistrationInfoResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(GETREGISTRATIONINFORESULT$0, 0);
            }
        }
        
        /**
         * Gets the "registrationInfos" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfRegistrationInfo getRegistrationInfos()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfRegistrationInfo target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfRegistrationInfo)get_store().find_element_user(REGISTRATIONINFOS$2, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "registrationInfos" element
         */
        public boolean isNilRegistrationInfos()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfRegistrationInfo target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfRegistrationInfo)get_store().find_element_user(REGISTRATIONINFOS$2, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "registrationInfos" element
         */
        public boolean isSetRegistrationInfos()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(REGISTRATIONINFOS$2) != 0;
            }
        }
        
        /**
         * Sets the "registrationInfos" element
         */
        public void setRegistrationInfos(org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfRegistrationInfo registrationInfos)
        {
            generatedSetterHelperImpl(registrationInfos, REGISTRATIONINFOS$2, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "registrationInfos" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfRegistrationInfo addNewRegistrationInfos()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfRegistrationInfo target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfRegistrationInfo)get_store().add_element_user(REGISTRATIONINFOS$2);
                return target;
            }
        }
        
        /**
         * Nils the "registrationInfos" element
         */
        public void setNilRegistrationInfos()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfRegistrationInfo target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfRegistrationInfo)get_store().find_element_user(REGISTRATIONINFOS$2, 0);
                if (target == null)
                {
                    target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfRegistrationInfo)get_store().add_element_user(REGISTRATIONINFOS$2);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "registrationInfos" element
         */
        public void unsetRegistrationInfos()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(REGISTRATIONINFOS$2, 0);
            }
        }
    }
}
