/*
 * An XML document type.
 * Localname: GetDocumentTransportDataResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.GetDocumentTransportDataResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one GetDocumentTransportDataResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class GetDocumentTransportDataResponseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GetDocumentTransportDataResponseDocument
{
    private static final long serialVersionUID = 1L;
    
    public GetDocumentTransportDataResponseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName GETDOCUMENTTRANSPORTDATARESPONSE$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "GetDocumentTransportDataResponse");
    
    
    /**
     * Gets the "GetDocumentTransportDataResponse" element
     */
    public org.tempuri.GetDocumentTransportDataResponseDocument.GetDocumentTransportDataResponse getGetDocumentTransportDataResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GetDocumentTransportDataResponseDocument.GetDocumentTransportDataResponse target = null;
            target = (org.tempuri.GetDocumentTransportDataResponseDocument.GetDocumentTransportDataResponse)get_store().find_element_user(GETDOCUMENTTRANSPORTDATARESPONSE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "GetDocumentTransportDataResponse" element
     */
    public void setGetDocumentTransportDataResponse(org.tempuri.GetDocumentTransportDataResponseDocument.GetDocumentTransportDataResponse getDocumentTransportDataResponse)
    {
        generatedSetterHelperImpl(getDocumentTransportDataResponse, GETDOCUMENTTRANSPORTDATARESPONSE$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "GetDocumentTransportDataResponse" element
     */
    public org.tempuri.GetDocumentTransportDataResponseDocument.GetDocumentTransportDataResponse addNewGetDocumentTransportDataResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GetDocumentTransportDataResponseDocument.GetDocumentTransportDataResponse target = null;
            target = (org.tempuri.GetDocumentTransportDataResponseDocument.GetDocumentTransportDataResponse)get_store().add_element_user(GETDOCUMENTTRANSPORTDATARESPONSE$0);
            return target;
        }
    }
    /**
     * An XML GetDocumentTransportDataResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class GetDocumentTransportDataResponseImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GetDocumentTransportDataResponseDocument.GetDocumentTransportDataResponse
    {
        private static final long serialVersionUID = 1L;
        
        public GetDocumentTransportDataResponseImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName GETDOCUMENTTRANSPORTDATARESULT$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "GetDocumentTransportDataResult");
        private static final javax.xml.namespace.QName TRANSPORTDATA$2 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "transportData");
        
        
        /**
         * Gets the "GetDocumentTransportDataResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getGetDocumentTransportDataResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(GETDOCUMENTTRANSPORTDATARESULT$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "GetDocumentTransportDataResult" element
         */
        public boolean isNilGetDocumentTransportDataResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(GETDOCUMENTTRANSPORTDATARESULT$0, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "GetDocumentTransportDataResult" element
         */
        public boolean isSetGetDocumentTransportDataResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(GETDOCUMENTTRANSPORTDATARESULT$0) != 0;
            }
        }
        
        /**
         * Sets the "GetDocumentTransportDataResult" element
         */
        public void setGetDocumentTransportDataResult(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getDocumentTransportDataResult)
        {
            generatedSetterHelperImpl(getDocumentTransportDataResult, GETDOCUMENTTRANSPORTDATARESULT$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "GetDocumentTransportDataResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewGetDocumentTransportDataResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(GETDOCUMENTTRANSPORTDATARESULT$0);
                return target;
            }
        }
        
        /**
         * Nils the "GetDocumentTransportDataResult" element
         */
        public void setNilGetDocumentTransportDataResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(GETDOCUMENTTRANSPORTDATARESULT$0, 0);
                if (target == null)
                {
                    target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(GETDOCUMENTTRANSPORTDATARESULT$0);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "GetDocumentTransportDataResult" element
         */
        public void unsetGetDocumentTransportDataResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(GETDOCUMENTTRANSPORTDATARESULT$0, 0);
            }
        }
        
        /**
         * Gets the "transportData" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentTransportData getTransportData()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentTransportData target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentTransportData)get_store().find_element_user(TRANSPORTDATA$2, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "transportData" element
         */
        public boolean isNilTransportData()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentTransportData target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentTransportData)get_store().find_element_user(TRANSPORTDATA$2, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "transportData" element
         */
        public boolean isSetTransportData()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(TRANSPORTDATA$2) != 0;
            }
        }
        
        /**
         * Sets the "transportData" element
         */
        public void setTransportData(org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentTransportData transportData)
        {
            generatedSetterHelperImpl(transportData, TRANSPORTDATA$2, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "transportData" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentTransportData addNewTransportData()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentTransportData target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentTransportData)get_store().add_element_user(TRANSPORTDATA$2);
                return target;
            }
        }
        
        /**
         * Nils the "transportData" element
         */
        public void setNilTransportData()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentTransportData target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentTransportData)get_store().find_element_user(TRANSPORTDATA$2, 0);
                if (target == null)
                {
                    target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentTransportData)get_store().add_element_user(TRANSPORTDATA$2);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "transportData" element
         */
        public void unsetTransportData()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(TRANSPORTDATA$2, 0);
            }
        }
    }
}
