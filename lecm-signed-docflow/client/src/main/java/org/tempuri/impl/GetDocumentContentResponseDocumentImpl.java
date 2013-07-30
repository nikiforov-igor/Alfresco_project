/*
 * An XML document type.
 * Localname: GetDocumentContentResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.GetDocumentContentResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one GetDocumentContentResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class GetDocumentContentResponseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GetDocumentContentResponseDocument
{
    private static final long serialVersionUID = 1L;
    
    public GetDocumentContentResponseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName GETDOCUMENTCONTENTRESPONSE$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "GetDocumentContentResponse");
    
    
    /**
     * Gets the "GetDocumentContentResponse" element
     */
    public org.tempuri.GetDocumentContentResponseDocument.GetDocumentContentResponse getGetDocumentContentResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GetDocumentContentResponseDocument.GetDocumentContentResponse target = null;
            target = (org.tempuri.GetDocumentContentResponseDocument.GetDocumentContentResponse)get_store().find_element_user(GETDOCUMENTCONTENTRESPONSE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "GetDocumentContentResponse" element
     */
    public void setGetDocumentContentResponse(org.tempuri.GetDocumentContentResponseDocument.GetDocumentContentResponse getDocumentContentResponse)
    {
        generatedSetterHelperImpl(getDocumentContentResponse, GETDOCUMENTCONTENTRESPONSE$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "GetDocumentContentResponse" element
     */
    public org.tempuri.GetDocumentContentResponseDocument.GetDocumentContentResponse addNewGetDocumentContentResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GetDocumentContentResponseDocument.GetDocumentContentResponse target = null;
            target = (org.tempuri.GetDocumentContentResponseDocument.GetDocumentContentResponse)get_store().add_element_user(GETDOCUMENTCONTENTRESPONSE$0);
            return target;
        }
    }
    /**
     * An XML GetDocumentContentResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class GetDocumentContentResponseImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GetDocumentContentResponseDocument.GetDocumentContentResponse
    {
        private static final long serialVersionUID = 1L;
        
        public GetDocumentContentResponseImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName GETDOCUMENTCONTENTRESULT$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "GetDocumentContentResult");
        private static final javax.xml.namespace.QName DOCCONTENT$2 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "docContent");
        
        
        /**
         * Gets the "GetDocumentContentResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getGetDocumentContentResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(GETDOCUMENTCONTENTRESULT$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "GetDocumentContentResult" element
         */
        public boolean isNilGetDocumentContentResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(GETDOCUMENTCONTENTRESULT$0, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "GetDocumentContentResult" element
         */
        public boolean isSetGetDocumentContentResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(GETDOCUMENTCONTENTRESULT$0) != 0;
            }
        }
        
        /**
         * Sets the "GetDocumentContentResult" element
         */
        public void setGetDocumentContentResult(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getDocumentContentResult)
        {
            generatedSetterHelperImpl(getDocumentContentResult, GETDOCUMENTCONTENTRESULT$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "GetDocumentContentResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewGetDocumentContentResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(GETDOCUMENTCONTENTRESULT$0);
                return target;
            }
        }
        
        /**
         * Nils the "GetDocumentContentResult" element
         */
        public void setNilGetDocumentContentResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(GETDOCUMENTCONTENTRESULT$0, 0);
                if (target == null)
                {
                    target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(GETDOCUMENTCONTENTRESULT$0);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "GetDocumentContentResult" element
         */
        public void unsetGetDocumentContentResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(GETDOCUMENTCONTENTRESULT$0, 0);
            }
        }
        
        /**
         * Gets the "docContent" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentContent getDocContent()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentContent target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentContent)get_store().find_element_user(DOCCONTENT$2, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "docContent" element
         */
        public boolean isNilDocContent()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentContent target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentContent)get_store().find_element_user(DOCCONTENT$2, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "docContent" element
         */
        public boolean isSetDocContent()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(DOCCONTENT$2) != 0;
            }
        }
        
        /**
         * Sets the "docContent" element
         */
        public void setDocContent(org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentContent docContent)
        {
            generatedSetterHelperImpl(docContent, DOCCONTENT$2, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "docContent" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentContent addNewDocContent()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentContent target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentContent)get_store().add_element_user(DOCCONTENT$2);
                return target;
            }
        }
        
        /**
         * Nils the "docContent" element
         */
        public void setNilDocContent()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentContent target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentContent)get_store().find_element_user(DOCCONTENT$2, 0);
                if (target == null)
                {
                    target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentContent)get_store().add_element_user(DOCCONTENT$2);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "docContent" element
         */
        public void unsetDocContent()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(DOCCONTENT$2, 0);
            }
        }
    }
}
