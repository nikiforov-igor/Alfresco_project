/*
 * An XML document type.
 * Localname: GenerateTorg12XmlForSellerResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.GenerateTorg12XmlForSellerResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one GenerateTorg12XmlForSellerResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class GenerateTorg12XmlForSellerResponseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GenerateTorg12XmlForSellerResponseDocument
{
    private static final long serialVersionUID = 1L;
    
    public GenerateTorg12XmlForSellerResponseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName GENERATETORG12XMLFORSELLERRESPONSE$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "GenerateTorg12XmlForSellerResponse");
    
    
    /**
     * Gets the "GenerateTorg12XmlForSellerResponse" element
     */
    public org.tempuri.GenerateTorg12XmlForSellerResponseDocument.GenerateTorg12XmlForSellerResponse getGenerateTorg12XmlForSellerResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GenerateTorg12XmlForSellerResponseDocument.GenerateTorg12XmlForSellerResponse target = null;
            target = (org.tempuri.GenerateTorg12XmlForSellerResponseDocument.GenerateTorg12XmlForSellerResponse)get_store().find_element_user(GENERATETORG12XMLFORSELLERRESPONSE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "GenerateTorg12XmlForSellerResponse" element
     */
    public void setGenerateTorg12XmlForSellerResponse(org.tempuri.GenerateTorg12XmlForSellerResponseDocument.GenerateTorg12XmlForSellerResponse generateTorg12XmlForSellerResponse)
    {
        generatedSetterHelperImpl(generateTorg12XmlForSellerResponse, GENERATETORG12XMLFORSELLERRESPONSE$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "GenerateTorg12XmlForSellerResponse" element
     */
    public org.tempuri.GenerateTorg12XmlForSellerResponseDocument.GenerateTorg12XmlForSellerResponse addNewGenerateTorg12XmlForSellerResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GenerateTorg12XmlForSellerResponseDocument.GenerateTorg12XmlForSellerResponse target = null;
            target = (org.tempuri.GenerateTorg12XmlForSellerResponseDocument.GenerateTorg12XmlForSellerResponse)get_store().add_element_user(GENERATETORG12XMLFORSELLERRESPONSE$0);
            return target;
        }
    }
    /**
     * An XML GenerateTorg12XmlForSellerResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class GenerateTorg12XmlForSellerResponseImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GenerateTorg12XmlForSellerResponseDocument.GenerateTorg12XmlForSellerResponse
    {
        private static final long serialVersionUID = 1L;
        
        public GenerateTorg12XmlForSellerResponseImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName GENERATETORG12XMLFORSELLERRESULT$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "GenerateTorg12XmlForSellerResult");
        private static final javax.xml.namespace.QName GENERATEDDOC$2 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "generatedDoc");
        
        
        /**
         * Gets the "GenerateTorg12XmlForSellerResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getGenerateTorg12XmlForSellerResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(GENERATETORG12XMLFORSELLERRESULT$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "GenerateTorg12XmlForSellerResult" element
         */
        public boolean isNilGenerateTorg12XmlForSellerResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(GENERATETORG12XMLFORSELLERRESULT$0, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "GenerateTorg12XmlForSellerResult" element
         */
        public boolean isSetGenerateTorg12XmlForSellerResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(GENERATETORG12XMLFORSELLERRESULT$0) != 0;
            }
        }
        
        /**
         * Sets the "GenerateTorg12XmlForSellerResult" element
         */
        public void setGenerateTorg12XmlForSellerResult(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse generateTorg12XmlForSellerResult)
        {
            generatedSetterHelperImpl(generateTorg12XmlForSellerResult, GENERATETORG12XMLFORSELLERRESULT$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "GenerateTorg12XmlForSellerResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewGenerateTorg12XmlForSellerResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(GENERATETORG12XMLFORSELLERRESULT$0);
                return target;
            }
        }
        
        /**
         * Nils the "GenerateTorg12XmlForSellerResult" element
         */
        public void setNilGenerateTorg12XmlForSellerResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(GENERATETORG12XMLFORSELLERRESULT$0, 0);
                if (target == null)
                {
                    target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(GENERATETORG12XMLFORSELLERRESULT$0);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "GenerateTorg12XmlForSellerResult" element
         */
        public void unsetGenerateTorg12XmlForSellerResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(GENERATETORG12XMLFORSELLERRESULT$0, 0);
            }
        }
        
        /**
         * Gets the "generatedDoc" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.GeneratedDocument1 getGeneratedDoc()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.GeneratedDocument1 target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.GeneratedDocument1)get_store().find_element_user(GENERATEDDOC$2, 0);
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
                org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.GeneratedDocument1 target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.GeneratedDocument1)get_store().find_element_user(GENERATEDDOC$2, 0);
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
        public void setGeneratedDoc(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.GeneratedDocument1 generatedDoc)
        {
            generatedSetterHelperImpl(generatedDoc, GENERATEDDOC$2, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "generatedDoc" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.GeneratedDocument1 addNewGeneratedDoc()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.GeneratedDocument1 target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.GeneratedDocument1)get_store().add_element_user(GENERATEDDOC$2);
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
                org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.GeneratedDocument1 target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.GeneratedDocument1)get_store().find_element_user(GENERATEDDOC$2, 0);
                if (target == null)
                {
                    target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.GeneratedDocument1)get_store().add_element_user(GENERATEDDOC$2);
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
