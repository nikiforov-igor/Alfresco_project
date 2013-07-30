/*
 * An XML document type.
 * Localname: GenerateRegisterRequestResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.GenerateRegisterRequestResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one GenerateRegisterRequestResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class GenerateRegisterRequestResponseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GenerateRegisterRequestResponseDocument
{
    private static final long serialVersionUID = 1L;
    
    public GenerateRegisterRequestResponseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName GENERATEREGISTERREQUESTRESPONSE$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "GenerateRegisterRequestResponse");
    
    
    /**
     * Gets the "GenerateRegisterRequestResponse" element
     */
    public org.tempuri.GenerateRegisterRequestResponseDocument.GenerateRegisterRequestResponse getGenerateRegisterRequestResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GenerateRegisterRequestResponseDocument.GenerateRegisterRequestResponse target = null;
            target = (org.tempuri.GenerateRegisterRequestResponseDocument.GenerateRegisterRequestResponse)get_store().find_element_user(GENERATEREGISTERREQUESTRESPONSE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "GenerateRegisterRequestResponse" element
     */
    public void setGenerateRegisterRequestResponse(org.tempuri.GenerateRegisterRequestResponseDocument.GenerateRegisterRequestResponse generateRegisterRequestResponse)
    {
        generatedSetterHelperImpl(generateRegisterRequestResponse, GENERATEREGISTERREQUESTRESPONSE$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "GenerateRegisterRequestResponse" element
     */
    public org.tempuri.GenerateRegisterRequestResponseDocument.GenerateRegisterRequestResponse addNewGenerateRegisterRequestResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GenerateRegisterRequestResponseDocument.GenerateRegisterRequestResponse target = null;
            target = (org.tempuri.GenerateRegisterRequestResponseDocument.GenerateRegisterRequestResponse)get_store().add_element_user(GENERATEREGISTERREQUESTRESPONSE$0);
            return target;
        }
    }
    /**
     * An XML GenerateRegisterRequestResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class GenerateRegisterRequestResponseImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GenerateRegisterRequestResponseDocument.GenerateRegisterRequestResponse
    {
        private static final long serialVersionUID = 1L;
        
        public GenerateRegisterRequestResponseImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName GENERATEREGISTERREQUESTRESULT$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "GenerateRegisterRequestResult");
        private static final javax.xml.namespace.QName CONTENT$2 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "content");
        
        
        /**
         * Gets the "GenerateRegisterRequestResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getGenerateRegisterRequestResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(GENERATEREGISTERREQUESTRESULT$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "GenerateRegisterRequestResult" element
         */
        public boolean isNilGenerateRegisterRequestResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(GENERATEREGISTERREQUESTRESULT$0, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "GenerateRegisterRequestResult" element
         */
        public boolean isSetGenerateRegisterRequestResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(GENERATEREGISTERREQUESTRESULT$0) != 0;
            }
        }
        
        /**
         * Sets the "GenerateRegisterRequestResult" element
         */
        public void setGenerateRegisterRequestResult(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse generateRegisterRequestResult)
        {
            generatedSetterHelperImpl(generateRegisterRequestResult, GENERATEREGISTERREQUESTRESULT$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "GenerateRegisterRequestResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewGenerateRegisterRequestResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(GENERATEREGISTERREQUESTRESULT$0);
                return target;
            }
        }
        
        /**
         * Nils the "GenerateRegisterRequestResult" element
         */
        public void setNilGenerateRegisterRequestResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(GENERATEREGISTERREQUESTRESULT$0, 0);
                if (target == null)
                {
                    target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(GENERATEREGISTERREQUESTRESULT$0);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "GenerateRegisterRequestResult" element
         */
        public void unsetGenerateRegisterRequestResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(GENERATEREGISTERREQUESTRESULT$0, 0);
            }
        }
        
        /**
         * Gets the "content" element
         */
        public byte[] getContent()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CONTENT$2, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getByteArrayValue();
            }
        }
        
        /**
         * Gets (as xml) the "content" element
         */
        public org.apache.xmlbeans.XmlBase64Binary xgetContent()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBase64Binary target = null;
                target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(CONTENT$2, 0);
                return target;
            }
        }
        
        /**
         * Tests for nil "content" element
         */
        public boolean isNilContent()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBase64Binary target = null;
                target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(CONTENT$2, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "content" element
         */
        public boolean isSetContent()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(CONTENT$2) != 0;
            }
        }
        
        /**
         * Sets the "content" element
         */
        public void setContent(byte[] content)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CONTENT$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(CONTENT$2);
                }
                target.setByteArrayValue(content);
            }
        }
        
        /**
         * Sets (as xml) the "content" element
         */
        public void xsetContent(org.apache.xmlbeans.XmlBase64Binary content)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBase64Binary target = null;
                target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(CONTENT$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlBase64Binary)get_store().add_element_user(CONTENT$2);
                }
                target.set(content);
            }
        }
        
        /**
         * Nils the "content" element
         */
        public void setNilContent()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBase64Binary target = null;
                target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(CONTENT$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlBase64Binary)get_store().add_element_user(CONTENT$2);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "content" element
         */
        public void unsetContent()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(CONTENT$2, 0);
            }
        }
    }
}
