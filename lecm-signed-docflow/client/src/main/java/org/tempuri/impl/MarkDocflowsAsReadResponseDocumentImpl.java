/*
 * An XML document type.
 * Localname: MarkDocflowsAsReadResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.MarkDocflowsAsReadResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one MarkDocflowsAsReadResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class MarkDocflowsAsReadResponseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.MarkDocflowsAsReadResponseDocument
{
    private static final long serialVersionUID = 1L;
    
    public MarkDocflowsAsReadResponseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName MARKDOCFLOWSASREADRESPONSE$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "MarkDocflowsAsReadResponse");
    
    
    /**
     * Gets the "MarkDocflowsAsReadResponse" element
     */
    public org.tempuri.MarkDocflowsAsReadResponseDocument.MarkDocflowsAsReadResponse getMarkDocflowsAsReadResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.MarkDocflowsAsReadResponseDocument.MarkDocflowsAsReadResponse target = null;
            target = (org.tempuri.MarkDocflowsAsReadResponseDocument.MarkDocflowsAsReadResponse)get_store().find_element_user(MARKDOCFLOWSASREADRESPONSE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "MarkDocflowsAsReadResponse" element
     */
    public void setMarkDocflowsAsReadResponse(org.tempuri.MarkDocflowsAsReadResponseDocument.MarkDocflowsAsReadResponse markDocflowsAsReadResponse)
    {
        generatedSetterHelperImpl(markDocflowsAsReadResponse, MARKDOCFLOWSASREADRESPONSE$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "MarkDocflowsAsReadResponse" element
     */
    public org.tempuri.MarkDocflowsAsReadResponseDocument.MarkDocflowsAsReadResponse addNewMarkDocflowsAsReadResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.MarkDocflowsAsReadResponseDocument.MarkDocflowsAsReadResponse target = null;
            target = (org.tempuri.MarkDocflowsAsReadResponseDocument.MarkDocflowsAsReadResponse)get_store().add_element_user(MARKDOCFLOWSASREADRESPONSE$0);
            return target;
        }
    }
    /**
     * An XML MarkDocflowsAsReadResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class MarkDocflowsAsReadResponseImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.MarkDocflowsAsReadResponseDocument.MarkDocflowsAsReadResponse
    {
        private static final long serialVersionUID = 1L;
        
        public MarkDocflowsAsReadResponseImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName MARKDOCFLOWSASREADRESULT$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "MarkDocflowsAsReadResult");
        
        
        /**
         * Gets the "MarkDocflowsAsReadResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getMarkDocflowsAsReadResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(MARKDOCFLOWSASREADRESULT$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "MarkDocflowsAsReadResult" element
         */
        public boolean isNilMarkDocflowsAsReadResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(MARKDOCFLOWSASREADRESULT$0, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "MarkDocflowsAsReadResult" element
         */
        public boolean isSetMarkDocflowsAsReadResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(MARKDOCFLOWSASREADRESULT$0) != 0;
            }
        }
        
        /**
         * Sets the "MarkDocflowsAsReadResult" element
         */
        public void setMarkDocflowsAsReadResult(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse markDocflowsAsReadResult)
        {
            generatedSetterHelperImpl(markDocflowsAsReadResult, MARKDOCFLOWSASREADRESULT$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "MarkDocflowsAsReadResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewMarkDocflowsAsReadResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(MARKDOCFLOWSASREADRESULT$0);
                return target;
            }
        }
        
        /**
         * Nils the "MarkDocflowsAsReadResult" element
         */
        public void setNilMarkDocflowsAsReadResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(MARKDOCFLOWSASREADRESULT$0, 0);
                if (target == null)
                {
                    target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(MARKDOCFLOWSASREADRESULT$0);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "MarkDocflowsAsReadResult" element
         */
        public void unsetMarkDocflowsAsReadResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(MARKDOCFLOWSASREADRESULT$0, 0);
            }
        }
    }
}
