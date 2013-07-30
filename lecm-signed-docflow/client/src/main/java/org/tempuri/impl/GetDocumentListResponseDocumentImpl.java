/*
 * An XML document type.
 * Localname: GetDocumentListResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.GetDocumentListResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one GetDocumentListResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class GetDocumentListResponseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GetDocumentListResponseDocument
{
    private static final long serialVersionUID = 1L;
    
    public GetDocumentListResponseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName GETDOCUMENTLISTRESPONSE$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "GetDocumentListResponse");
    
    
    /**
     * Gets the "GetDocumentListResponse" element
     */
    public org.tempuri.GetDocumentListResponseDocument.GetDocumentListResponse getGetDocumentListResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GetDocumentListResponseDocument.GetDocumentListResponse target = null;
            target = (org.tempuri.GetDocumentListResponseDocument.GetDocumentListResponse)get_store().find_element_user(GETDOCUMENTLISTRESPONSE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "GetDocumentListResponse" element
     */
    public void setGetDocumentListResponse(org.tempuri.GetDocumentListResponseDocument.GetDocumentListResponse getDocumentListResponse)
    {
        generatedSetterHelperImpl(getDocumentListResponse, GETDOCUMENTLISTRESPONSE$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "GetDocumentListResponse" element
     */
    public org.tempuri.GetDocumentListResponseDocument.GetDocumentListResponse addNewGetDocumentListResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GetDocumentListResponseDocument.GetDocumentListResponse target = null;
            target = (org.tempuri.GetDocumentListResponseDocument.GetDocumentListResponse)get_store().add_element_user(GETDOCUMENTLISTRESPONSE$0);
            return target;
        }
    }
    /**
     * An XML GetDocumentListResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class GetDocumentListResponseImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GetDocumentListResponseDocument.GetDocumentListResponse
    {
        private static final long serialVersionUID = 1L;
        
        public GetDocumentListResponseImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName GETDOCUMENTLISTRESULT$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "GetDocumentListResult");
        private static final javax.xml.namespace.QName DOCUMENTINFOS$2 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "documentInfos");
        
        
        /**
         * Gets the "GetDocumentListResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getGetDocumentListResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(GETDOCUMENTLISTRESULT$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "GetDocumentListResult" element
         */
        public boolean isNilGetDocumentListResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(GETDOCUMENTLISTRESULT$0, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "GetDocumentListResult" element
         */
        public boolean isSetGetDocumentListResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(GETDOCUMENTLISTRESULT$0) != 0;
            }
        }
        
        /**
         * Sets the "GetDocumentListResult" element
         */
        public void setGetDocumentListResult(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getDocumentListResult)
        {
            generatedSetterHelperImpl(getDocumentListResult, GETDOCUMENTLISTRESULT$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "GetDocumentListResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewGetDocumentListResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(GETDOCUMENTLISTRESULT$0);
                return target;
            }
        }
        
        /**
         * Nils the "GetDocumentListResult" element
         */
        public void setNilGetDocumentListResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(GETDOCUMENTLISTRESULT$0, 0);
                if (target == null)
                {
                    target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(GETDOCUMENTLISTRESULT$0);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "GetDocumentListResult" element
         */
        public void unsetGetDocumentListResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(GETDOCUMENTLISTRESULT$0, 0);
            }
        }
        
        /**
         * Gets the "documentInfos" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocumentInfo getDocumentInfos()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocumentInfo target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocumentInfo)get_store().find_element_user(DOCUMENTINFOS$2, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "documentInfos" element
         */
        public boolean isNilDocumentInfos()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocumentInfo target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocumentInfo)get_store().find_element_user(DOCUMENTINFOS$2, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "documentInfos" element
         */
        public boolean isSetDocumentInfos()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(DOCUMENTINFOS$2) != 0;
            }
        }
        
        /**
         * Sets the "documentInfos" element
         */
        public void setDocumentInfos(org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocumentInfo documentInfos)
        {
            generatedSetterHelperImpl(documentInfos, DOCUMENTINFOS$2, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "documentInfos" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocumentInfo addNewDocumentInfos()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocumentInfo target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocumentInfo)get_store().add_element_user(DOCUMENTINFOS$2);
                return target;
            }
        }
        
        /**
         * Nils the "documentInfos" element
         */
        public void setNilDocumentInfos()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocumentInfo target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocumentInfo)get_store().find_element_user(DOCUMENTINFOS$2, 0);
                if (target == null)
                {
                    target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocumentInfo)get_store().add_element_user(DOCUMENTINFOS$2);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "documentInfos" element
         */
        public void unsetDocumentInfos()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(DOCUMENTINFOS$2, 0);
            }
        }
    }
}
