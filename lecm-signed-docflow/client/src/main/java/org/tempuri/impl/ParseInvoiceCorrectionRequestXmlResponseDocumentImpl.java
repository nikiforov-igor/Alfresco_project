/*
 * An XML document type.
 * Localname: ParseInvoiceCorrectionRequestXmlResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one ParseInvoiceCorrectionRequestXmlResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class ParseInvoiceCorrectionRequestXmlResponseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument
{
    private static final long serialVersionUID = 1L;
    
    public ParseInvoiceCorrectionRequestXmlResponseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName PARSEINVOICECORRECTIONREQUESTXMLRESPONSE$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "ParseInvoiceCorrectionRequestXmlResponse");
    
    
    /**
     * Gets the "ParseInvoiceCorrectionRequestXmlResponse" element
     */
    public org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument.ParseInvoiceCorrectionRequestXmlResponse getParseInvoiceCorrectionRequestXmlResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument.ParseInvoiceCorrectionRequestXmlResponse target = null;
            target = (org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument.ParseInvoiceCorrectionRequestXmlResponse)get_store().find_element_user(PARSEINVOICECORRECTIONREQUESTXMLRESPONSE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "ParseInvoiceCorrectionRequestXmlResponse" element
     */
    public void setParseInvoiceCorrectionRequestXmlResponse(org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument.ParseInvoiceCorrectionRequestXmlResponse parseInvoiceCorrectionRequestXmlResponse)
    {
        generatedSetterHelperImpl(parseInvoiceCorrectionRequestXmlResponse, PARSEINVOICECORRECTIONREQUESTXMLRESPONSE$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "ParseInvoiceCorrectionRequestXmlResponse" element
     */
    public org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument.ParseInvoiceCorrectionRequestXmlResponse addNewParseInvoiceCorrectionRequestXmlResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument.ParseInvoiceCorrectionRequestXmlResponse target = null;
            target = (org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument.ParseInvoiceCorrectionRequestXmlResponse)get_store().add_element_user(PARSEINVOICECORRECTIONREQUESTXMLRESPONSE$0);
            return target;
        }
    }
    /**
     * An XML ParseInvoiceCorrectionRequestXmlResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class ParseInvoiceCorrectionRequestXmlResponseImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument.ParseInvoiceCorrectionRequestXmlResponse
    {
        private static final long serialVersionUID = 1L;
        
        public ParseInvoiceCorrectionRequestXmlResponseImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName PARSEINVOICECORRECTIONREQUESTXMLRESULT$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "ParseInvoiceCorrectionRequestXmlResult");
        private static final javax.xml.namespace.QName REQUESTTEXT$2 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "requestText");
        
        
        /**
         * Gets the "ParseInvoiceCorrectionRequestXmlResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getParseInvoiceCorrectionRequestXmlResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(PARSEINVOICECORRECTIONREQUESTXMLRESULT$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "ParseInvoiceCorrectionRequestXmlResult" element
         */
        public boolean isNilParseInvoiceCorrectionRequestXmlResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(PARSEINVOICECORRECTIONREQUESTXMLRESULT$0, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "ParseInvoiceCorrectionRequestXmlResult" element
         */
        public boolean isSetParseInvoiceCorrectionRequestXmlResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(PARSEINVOICECORRECTIONREQUESTXMLRESULT$0) != 0;
            }
        }
        
        /**
         * Sets the "ParseInvoiceCorrectionRequestXmlResult" element
         */
        public void setParseInvoiceCorrectionRequestXmlResult(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse parseInvoiceCorrectionRequestXmlResult)
        {
            generatedSetterHelperImpl(parseInvoiceCorrectionRequestXmlResult, PARSEINVOICECORRECTIONREQUESTXMLRESULT$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "ParseInvoiceCorrectionRequestXmlResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewParseInvoiceCorrectionRequestXmlResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(PARSEINVOICECORRECTIONREQUESTXMLRESULT$0);
                return target;
            }
        }
        
        /**
         * Nils the "ParseInvoiceCorrectionRequestXmlResult" element
         */
        public void setNilParseInvoiceCorrectionRequestXmlResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(PARSEINVOICECORRECTIONREQUESTXMLRESULT$0, 0);
                if (target == null)
                {
                    target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(PARSEINVOICECORRECTIONREQUESTXMLRESULT$0);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "ParseInvoiceCorrectionRequestXmlResult" element
         */
        public void unsetParseInvoiceCorrectionRequestXmlResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(PARSEINVOICECORRECTIONREQUESTXMLRESULT$0, 0);
            }
        }
        
        /**
         * Gets the "requestText" element
         */
        public java.lang.String getRequestText()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(REQUESTTEXT$2, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "requestText" element
         */
        public org.apache.xmlbeans.XmlString xgetRequestText()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(REQUESTTEXT$2, 0);
                return target;
            }
        }
        
        /**
         * Tests for nil "requestText" element
         */
        public boolean isNilRequestText()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(REQUESTTEXT$2, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "requestText" element
         */
        public boolean isSetRequestText()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(REQUESTTEXT$2) != 0;
            }
        }
        
        /**
         * Sets the "requestText" element
         */
        public void setRequestText(java.lang.String requestText)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(REQUESTTEXT$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(REQUESTTEXT$2);
                }
                target.setStringValue(requestText);
            }
        }
        
        /**
         * Sets (as xml) the "requestText" element
         */
        public void xsetRequestText(org.apache.xmlbeans.XmlString requestText)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(REQUESTTEXT$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(REQUESTTEXT$2);
                }
                target.set(requestText);
            }
        }
        
        /**
         * Nils the "requestText" element
         */
        public void setNilRequestText()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(REQUESTTEXT$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(REQUESTTEXT$2);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "requestText" element
         */
        public void unsetRequestText()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(REQUESTTEXT$2, 0);
            }
        }
    }
}
