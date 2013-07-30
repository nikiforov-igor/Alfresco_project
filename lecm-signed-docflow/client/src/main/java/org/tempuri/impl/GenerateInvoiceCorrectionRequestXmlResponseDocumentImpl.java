/*
 * An XML document type.
 * Localname: GenerateInvoiceCorrectionRequestXmlResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one GenerateInvoiceCorrectionRequestXmlResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class GenerateInvoiceCorrectionRequestXmlResponseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument
{
    private static final long serialVersionUID = 1L;
    
    public GenerateInvoiceCorrectionRequestXmlResponseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName GENERATEINVOICECORRECTIONREQUESTXMLRESPONSE$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "GenerateInvoiceCorrectionRequestXmlResponse");
    
    
    /**
     * Gets the "GenerateInvoiceCorrectionRequestXmlResponse" element
     */
    public org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument.GenerateInvoiceCorrectionRequestXmlResponse getGenerateInvoiceCorrectionRequestXmlResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument.GenerateInvoiceCorrectionRequestXmlResponse target = null;
            target = (org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument.GenerateInvoiceCorrectionRequestXmlResponse)get_store().find_element_user(GENERATEINVOICECORRECTIONREQUESTXMLRESPONSE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "GenerateInvoiceCorrectionRequestXmlResponse" element
     */
    public void setGenerateInvoiceCorrectionRequestXmlResponse(org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument.GenerateInvoiceCorrectionRequestXmlResponse generateInvoiceCorrectionRequestXmlResponse)
    {
        generatedSetterHelperImpl(generateInvoiceCorrectionRequestXmlResponse, GENERATEINVOICECORRECTIONREQUESTXMLRESPONSE$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "GenerateInvoiceCorrectionRequestXmlResponse" element
     */
    public org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument.GenerateInvoiceCorrectionRequestXmlResponse addNewGenerateInvoiceCorrectionRequestXmlResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument.GenerateInvoiceCorrectionRequestXmlResponse target = null;
            target = (org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument.GenerateInvoiceCorrectionRequestXmlResponse)get_store().add_element_user(GENERATEINVOICECORRECTIONREQUESTXMLRESPONSE$0);
            return target;
        }
    }
    /**
     * An XML GenerateInvoiceCorrectionRequestXmlResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class GenerateInvoiceCorrectionRequestXmlResponseImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument.GenerateInvoiceCorrectionRequestXmlResponse
    {
        private static final long serialVersionUID = 1L;
        
        public GenerateInvoiceCorrectionRequestXmlResponseImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName GENERATEINVOICECORRECTIONREQUESTXMLRESULT$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "GenerateInvoiceCorrectionRequestXmlResult");
        private static final javax.xml.namespace.QName GENERATEDDOCUMENT$2 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "generatedDocument");
        
        
        /**
         * Gets the "GenerateInvoiceCorrectionRequestXmlResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getGenerateInvoiceCorrectionRequestXmlResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(GENERATEINVOICECORRECTIONREQUESTXMLRESULT$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "GenerateInvoiceCorrectionRequestXmlResult" element
         */
        public boolean isNilGenerateInvoiceCorrectionRequestXmlResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(GENERATEINVOICECORRECTIONREQUESTXMLRESULT$0, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "GenerateInvoiceCorrectionRequestXmlResult" element
         */
        public boolean isSetGenerateInvoiceCorrectionRequestXmlResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(GENERATEINVOICECORRECTIONREQUESTXMLRESULT$0) != 0;
            }
        }
        
        /**
         * Sets the "GenerateInvoiceCorrectionRequestXmlResult" element
         */
        public void setGenerateInvoiceCorrectionRequestXmlResult(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse generateInvoiceCorrectionRequestXmlResult)
        {
            generatedSetterHelperImpl(generateInvoiceCorrectionRequestXmlResult, GENERATEINVOICECORRECTIONREQUESTXMLRESULT$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "GenerateInvoiceCorrectionRequestXmlResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewGenerateInvoiceCorrectionRequestXmlResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(GENERATEINVOICECORRECTIONREQUESTXMLRESULT$0);
                return target;
            }
        }
        
        /**
         * Nils the "GenerateInvoiceCorrectionRequestXmlResult" element
         */
        public void setNilGenerateInvoiceCorrectionRequestXmlResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(GENERATEINVOICECORRECTIONREQUESTXMLRESULT$0, 0);
                if (target == null)
                {
                    target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(GENERATEINVOICECORRECTIONREQUESTXMLRESULT$0);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "GenerateInvoiceCorrectionRequestXmlResult" element
         */
        public void unsetGenerateInvoiceCorrectionRequestXmlResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(GENERATEINVOICECORRECTIONREQUESTXMLRESULT$0, 0);
            }
        }
        
        /**
         * Gets the "generatedDocument" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.GeneratedDocument1 getGeneratedDocument()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.GeneratedDocument1 target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.GeneratedDocument1)get_store().find_element_user(GENERATEDDOCUMENT$2, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "generatedDocument" element
         */
        public boolean isNilGeneratedDocument()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.GeneratedDocument1 target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.GeneratedDocument1)get_store().find_element_user(GENERATEDDOCUMENT$2, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "generatedDocument" element
         */
        public boolean isSetGeneratedDocument()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(GENERATEDDOCUMENT$2) != 0;
            }
        }
        
        /**
         * Sets the "generatedDocument" element
         */
        public void setGeneratedDocument(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.GeneratedDocument1 generatedDocument)
        {
            generatedSetterHelperImpl(generatedDocument, GENERATEDDOCUMENT$2, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "generatedDocument" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.GeneratedDocument1 addNewGeneratedDocument()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.GeneratedDocument1 target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.GeneratedDocument1)get_store().add_element_user(GENERATEDDOCUMENT$2);
                return target;
            }
        }
        
        /**
         * Nils the "generatedDocument" element
         */
        public void setNilGeneratedDocument()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.GeneratedDocument1 target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.GeneratedDocument1)get_store().find_element_user(GENERATEDDOCUMENT$2, 0);
                if (target == null)
                {
                    target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.GeneratedDocument1)get_store().add_element_user(GENERATEDDOCUMENT$2);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "generatedDocument" element
         */
        public void unsetGeneratedDocument()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(GENERATEDDOCUMENT$2, 0);
            }
        }
    }
}
