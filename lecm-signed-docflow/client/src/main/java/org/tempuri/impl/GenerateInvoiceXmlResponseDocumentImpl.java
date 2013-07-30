/*
 * An XML document type.
 * Localname: GenerateInvoiceXmlResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.GenerateInvoiceXmlResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one GenerateInvoiceXmlResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class GenerateInvoiceXmlResponseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GenerateInvoiceXmlResponseDocument
{
    private static final long serialVersionUID = 1L;
    
    public GenerateInvoiceXmlResponseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName GENERATEINVOICEXMLRESPONSE$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "GenerateInvoiceXmlResponse");
    
    
    /**
     * Gets the "GenerateInvoiceXmlResponse" element
     */
    public org.tempuri.GenerateInvoiceXmlResponseDocument.GenerateInvoiceXmlResponse getGenerateInvoiceXmlResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GenerateInvoiceXmlResponseDocument.GenerateInvoiceXmlResponse target = null;
            target = (org.tempuri.GenerateInvoiceXmlResponseDocument.GenerateInvoiceXmlResponse)get_store().find_element_user(GENERATEINVOICEXMLRESPONSE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "GenerateInvoiceXmlResponse" element
     */
    public void setGenerateInvoiceXmlResponse(org.tempuri.GenerateInvoiceXmlResponseDocument.GenerateInvoiceXmlResponse generateInvoiceXmlResponse)
    {
        generatedSetterHelperImpl(generateInvoiceXmlResponse, GENERATEINVOICEXMLRESPONSE$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "GenerateInvoiceXmlResponse" element
     */
    public org.tempuri.GenerateInvoiceXmlResponseDocument.GenerateInvoiceXmlResponse addNewGenerateInvoiceXmlResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GenerateInvoiceXmlResponseDocument.GenerateInvoiceXmlResponse target = null;
            target = (org.tempuri.GenerateInvoiceXmlResponseDocument.GenerateInvoiceXmlResponse)get_store().add_element_user(GENERATEINVOICEXMLRESPONSE$0);
            return target;
        }
    }
    /**
     * An XML GenerateInvoiceXmlResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class GenerateInvoiceXmlResponseImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GenerateInvoiceXmlResponseDocument.GenerateInvoiceXmlResponse
    {
        private static final long serialVersionUID = 1L;
        
        public GenerateInvoiceXmlResponseImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName GENERATEINVOICEXMLRESULT$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "GenerateInvoiceXmlResult");
        private static final javax.xml.namespace.QName GENERATEDDOC$2 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "generatedDoc");
        
        
        /**
         * Gets the "GenerateInvoiceXmlResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getGenerateInvoiceXmlResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(GENERATEINVOICEXMLRESULT$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "GenerateInvoiceXmlResult" element
         */
        public boolean isNilGenerateInvoiceXmlResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(GENERATEINVOICEXMLRESULT$0, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "GenerateInvoiceXmlResult" element
         */
        public boolean isSetGenerateInvoiceXmlResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(GENERATEINVOICEXMLRESULT$0) != 0;
            }
        }
        
        /**
         * Sets the "GenerateInvoiceXmlResult" element
         */
        public void setGenerateInvoiceXmlResult(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse generateInvoiceXmlResult)
        {
            generatedSetterHelperImpl(generateInvoiceXmlResult, GENERATEINVOICEXMLRESULT$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "GenerateInvoiceXmlResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewGenerateInvoiceXmlResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(GENERATEINVOICEXMLRESULT$0);
                return target;
            }
        }
        
        /**
         * Nils the "GenerateInvoiceXmlResult" element
         */
        public void setNilGenerateInvoiceXmlResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(GENERATEINVOICEXMLRESULT$0, 0);
                if (target == null)
                {
                    target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(GENERATEINVOICEXMLRESULT$0);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "GenerateInvoiceXmlResult" element
         */
        public void unsetGenerateInvoiceXmlResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(GENERATEINVOICEXMLRESULT$0, 0);
            }
        }
        
        /**
         * Gets the "generatedDoc" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend getGeneratedDoc()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend)get_store().find_element_user(GENERATEDDOC$2, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "generatedDoc" element
         */
        public boolean isNilGeneratedDoc()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend)get_store().find_element_user(GENERATEDDOC$2, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "generatedDoc" element
         */
        public boolean isSetGeneratedDoc()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(GENERATEDDOC$2) != 0;
            }
        }
        
        /**
         * Sets the "generatedDoc" element
         */
        public void setGeneratedDoc(org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend generatedDoc)
        {
            generatedSetterHelperImpl(generatedDoc, GENERATEDDOC$2, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "generatedDoc" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend addNewGeneratedDoc()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend)get_store().add_element_user(GENERATEDDOC$2);
                return target;
            }
        }
        
        /**
         * Nils the "generatedDoc" element
         */
        public void setNilGeneratedDoc()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend)get_store().find_element_user(GENERATEDDOC$2, 0);
                if (target == null)
                {
                    target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend)get_store().add_element_user(GENERATEDDOC$2);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "generatedDoc" element
         */
        public void unsetGeneratedDoc()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(GENERATEDDOC$2, 0);
            }
        }
    }
}
