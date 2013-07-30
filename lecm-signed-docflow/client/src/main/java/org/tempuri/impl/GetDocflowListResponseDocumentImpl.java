/*
 * An XML document type.
 * Localname: GetDocflowListResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.GetDocflowListResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one GetDocflowListResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class GetDocflowListResponseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GetDocflowListResponseDocument
{
    private static final long serialVersionUID = 1L;
    
    public GetDocflowListResponseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName GETDOCFLOWLISTRESPONSE$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "GetDocflowListResponse");
    
    
    /**
     * Gets the "GetDocflowListResponse" element
     */
    public org.tempuri.GetDocflowListResponseDocument.GetDocflowListResponse getGetDocflowListResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GetDocflowListResponseDocument.GetDocflowListResponse target = null;
            target = (org.tempuri.GetDocflowListResponseDocument.GetDocflowListResponse)get_store().find_element_user(GETDOCFLOWLISTRESPONSE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "GetDocflowListResponse" element
     */
    public void setGetDocflowListResponse(org.tempuri.GetDocflowListResponseDocument.GetDocflowListResponse getDocflowListResponse)
    {
        generatedSetterHelperImpl(getDocflowListResponse, GETDOCFLOWLISTRESPONSE$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "GetDocflowListResponse" element
     */
    public org.tempuri.GetDocflowListResponseDocument.GetDocflowListResponse addNewGetDocflowListResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GetDocflowListResponseDocument.GetDocflowListResponse target = null;
            target = (org.tempuri.GetDocflowListResponseDocument.GetDocflowListResponse)get_store().add_element_user(GETDOCFLOWLISTRESPONSE$0);
            return target;
        }
    }
    /**
     * An XML GetDocflowListResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class GetDocflowListResponseImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GetDocflowListResponseDocument.GetDocflowListResponse
    {
        private static final long serialVersionUID = 1L;
        
        public GetDocflowListResponseImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName GETDOCFLOWLISTRESULT$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "GetDocflowListResult");
        private static final javax.xml.namespace.QName DOCFLOWS$2 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "docflows");
        
        
        /**
         * Gets the "GetDocflowListResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getGetDocflowListResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(GETDOCFLOWLISTRESULT$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "GetDocflowListResult" element
         */
        public boolean isNilGetDocflowListResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(GETDOCFLOWLISTRESULT$0, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "GetDocflowListResult" element
         */
        public boolean isSetGetDocflowListResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(GETDOCFLOWLISTRESULT$0) != 0;
            }
        }
        
        /**
         * Sets the "GetDocflowListResult" element
         */
        public void setGetDocflowListResult(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getDocflowListResult)
        {
            generatedSetterHelperImpl(getDocflowListResult, GETDOCFLOWLISTRESULT$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "GetDocflowListResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewGetDocflowListResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(GETDOCFLOWLISTRESULT$0);
                return target;
            }
        }
        
        /**
         * Nils the "GetDocflowListResult" element
         */
        public void setNilGetDocflowListResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(GETDOCFLOWLISTRESULT$0, 0);
                if (target == null)
                {
                    target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(GETDOCFLOWLISTRESULT$0);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "GetDocflowListResult" element
         */
        public void unsetGetDocflowListResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(GETDOCFLOWLISTRESULT$0, 0);
            }
        }
        
        /**
         * Gets the "docflows" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocflowInfoBase getDocflows()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocflowInfoBase target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocflowInfoBase)get_store().find_element_user(DOCFLOWS$2, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "docflows" element
         */
        public boolean isNilDocflows()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocflowInfoBase target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocflowInfoBase)get_store().find_element_user(DOCFLOWS$2, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "docflows" element
         */
        public boolean isSetDocflows()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(DOCFLOWS$2) != 0;
            }
        }
        
        /**
         * Sets the "docflows" element
         */
        public void setDocflows(org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocflowInfoBase docflows)
        {
            generatedSetterHelperImpl(docflows, DOCFLOWS$2, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "docflows" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocflowInfoBase addNewDocflows()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocflowInfoBase target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocflowInfoBase)get_store().add_element_user(DOCFLOWS$2);
                return target;
            }
        }
        
        /**
         * Nils the "docflows" element
         */
        public void setNilDocflows()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocflowInfoBase target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocflowInfoBase)get_store().find_element_user(DOCFLOWS$2, 0);
                if (target == null)
                {
                    target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocflowInfoBase)get_store().add_element_user(DOCFLOWS$2);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "docflows" element
         */
        public void unsetDocflows()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(DOCFLOWS$2, 0);
            }
        }
    }
}
