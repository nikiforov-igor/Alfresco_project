/*
 * An XML document type.
 * Localname: GenerateTorg12XmlForBuyerResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.GenerateTorg12XmlForBuyerResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one GenerateTorg12XmlForBuyerResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class GenerateTorg12XmlForBuyerResponseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GenerateTorg12XmlForBuyerResponseDocument
{
    private static final long serialVersionUID = 1L;
    
    public GenerateTorg12XmlForBuyerResponseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName GENERATETORG12XMLFORBUYERRESPONSE$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "GenerateTorg12XmlForBuyerResponse");
    
    
    /**
     * Gets the "GenerateTorg12XmlForBuyerResponse" element
     */
    public org.tempuri.GenerateTorg12XmlForBuyerResponseDocument.GenerateTorg12XmlForBuyerResponse getGenerateTorg12XmlForBuyerResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GenerateTorg12XmlForBuyerResponseDocument.GenerateTorg12XmlForBuyerResponse target = null;
            target = (org.tempuri.GenerateTorg12XmlForBuyerResponseDocument.GenerateTorg12XmlForBuyerResponse)get_store().find_element_user(GENERATETORG12XMLFORBUYERRESPONSE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "GenerateTorg12XmlForBuyerResponse" element
     */
    public void setGenerateTorg12XmlForBuyerResponse(org.tempuri.GenerateTorg12XmlForBuyerResponseDocument.GenerateTorg12XmlForBuyerResponse generateTorg12XmlForBuyerResponse)
    {
        generatedSetterHelperImpl(generateTorg12XmlForBuyerResponse, GENERATETORG12XMLFORBUYERRESPONSE$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "GenerateTorg12XmlForBuyerResponse" element
     */
    public org.tempuri.GenerateTorg12XmlForBuyerResponseDocument.GenerateTorg12XmlForBuyerResponse addNewGenerateTorg12XmlForBuyerResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GenerateTorg12XmlForBuyerResponseDocument.GenerateTorg12XmlForBuyerResponse target = null;
            target = (org.tempuri.GenerateTorg12XmlForBuyerResponseDocument.GenerateTorg12XmlForBuyerResponse)get_store().add_element_user(GENERATETORG12XMLFORBUYERRESPONSE$0);
            return target;
        }
    }
    /**
     * An XML GenerateTorg12XmlForBuyerResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class GenerateTorg12XmlForBuyerResponseImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GenerateTorg12XmlForBuyerResponseDocument.GenerateTorg12XmlForBuyerResponse
    {
        private static final long serialVersionUID = 1L;
        
        public GenerateTorg12XmlForBuyerResponseImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName GENERATETORG12XMLFORBUYERRESULT$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "GenerateTorg12XmlForBuyerResult");
        private static final javax.xml.namespace.QName GENERATEDDOCUMENT$2 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "generatedDocument");
        
        
        /**
         * Gets the "GenerateTorg12XmlForBuyerResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getGenerateTorg12XmlForBuyerResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(GENERATETORG12XMLFORBUYERRESULT$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "GenerateTorg12XmlForBuyerResult" element
         */
        public boolean isNilGenerateTorg12XmlForBuyerResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(GENERATETORG12XMLFORBUYERRESULT$0, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "GenerateTorg12XmlForBuyerResult" element
         */
        public boolean isSetGenerateTorg12XmlForBuyerResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(GENERATETORG12XMLFORBUYERRESULT$0) != 0;
            }
        }
        
        /**
         * Sets the "GenerateTorg12XmlForBuyerResult" element
         */
        public void setGenerateTorg12XmlForBuyerResult(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse generateTorg12XmlForBuyerResult)
        {
            generatedSetterHelperImpl(generateTorg12XmlForBuyerResult, GENERATETORG12XMLFORBUYERRESULT$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "GenerateTorg12XmlForBuyerResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewGenerateTorg12XmlForBuyerResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(GENERATETORG12XMLFORBUYERRESULT$0);
                return target;
            }
        }
        
        /**
         * Nils the "GenerateTorg12XmlForBuyerResult" element
         */
        public void setNilGenerateTorg12XmlForBuyerResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(GENERATETORG12XMLFORBUYERRESULT$0, 0);
                if (target == null)
                {
                    target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(GENERATETORG12XMLFORBUYERRESULT$0);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "GenerateTorg12XmlForBuyerResult" element
         */
        public void unsetGenerateTorg12XmlForBuyerResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(GENERATETORG12XMLFORBUYERRESULT$0, 0);
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
