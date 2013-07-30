/*
 * An XML document type.
 * Localname: GenerateNotificationXmlResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.GenerateNotificationXmlResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one GenerateNotificationXmlResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class GenerateNotificationXmlResponseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GenerateNotificationXmlResponseDocument
{
    private static final long serialVersionUID = 1L;
    
    public GenerateNotificationXmlResponseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName GENERATENOTIFICATIONXMLRESPONSE$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "GenerateNotificationXmlResponse");
    
    
    /**
     * Gets the "GenerateNotificationXmlResponse" element
     */
    public org.tempuri.GenerateNotificationXmlResponseDocument.GenerateNotificationXmlResponse getGenerateNotificationXmlResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GenerateNotificationXmlResponseDocument.GenerateNotificationXmlResponse target = null;
            target = (org.tempuri.GenerateNotificationXmlResponseDocument.GenerateNotificationXmlResponse)get_store().find_element_user(GENERATENOTIFICATIONXMLRESPONSE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "GenerateNotificationXmlResponse" element
     */
    public void setGenerateNotificationXmlResponse(org.tempuri.GenerateNotificationXmlResponseDocument.GenerateNotificationXmlResponse generateNotificationXmlResponse)
    {
        generatedSetterHelperImpl(generateNotificationXmlResponse, GENERATENOTIFICATIONXMLRESPONSE$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "GenerateNotificationXmlResponse" element
     */
    public org.tempuri.GenerateNotificationXmlResponseDocument.GenerateNotificationXmlResponse addNewGenerateNotificationXmlResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GenerateNotificationXmlResponseDocument.GenerateNotificationXmlResponse target = null;
            target = (org.tempuri.GenerateNotificationXmlResponseDocument.GenerateNotificationXmlResponse)get_store().add_element_user(GENERATENOTIFICATIONXMLRESPONSE$0);
            return target;
        }
    }
    /**
     * An XML GenerateNotificationXmlResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class GenerateNotificationXmlResponseImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GenerateNotificationXmlResponseDocument.GenerateNotificationXmlResponse
    {
        private static final long serialVersionUID = 1L;
        
        public GenerateNotificationXmlResponseImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName GENERATENOTIFICATIONXMLRESULT$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "GenerateNotificationXmlResult");
        private static final javax.xml.namespace.QName GENERATEDDOC$2 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "generatedDoc");
        
        
        /**
         * Gets the "GenerateNotificationXmlResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getGenerateNotificationXmlResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(GENERATENOTIFICATIONXMLRESULT$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "GenerateNotificationXmlResult" element
         */
        public boolean isNilGenerateNotificationXmlResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(GENERATENOTIFICATIONXMLRESULT$0, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "GenerateNotificationXmlResult" element
         */
        public boolean isSetGenerateNotificationXmlResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(GENERATENOTIFICATIONXMLRESULT$0) != 0;
            }
        }
        
        /**
         * Sets the "GenerateNotificationXmlResult" element
         */
        public void setGenerateNotificationXmlResult(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse generateNotificationXmlResult)
        {
            generatedSetterHelperImpl(generateNotificationXmlResult, GENERATENOTIFICATIONXMLRESULT$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "GenerateNotificationXmlResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewGenerateNotificationXmlResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(GENERATENOTIFICATIONXMLRESULT$0);
                return target;
            }
        }
        
        /**
         * Nils the "GenerateNotificationXmlResult" element
         */
        public void setNilGenerateNotificationXmlResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(GENERATENOTIFICATIONXMLRESULT$0, 0);
                if (target == null)
                {
                    target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(GENERATENOTIFICATIONXMLRESULT$0);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "GenerateNotificationXmlResult" element
         */
        public void unsetGenerateNotificationXmlResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(GENERATENOTIFICATIONXMLRESULT$0, 0);
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
