/*
 * An XML document type.
 * Localname: SendDocumentResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.SendDocumentResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one SendDocumentResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class SendDocumentResponseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.SendDocumentResponseDocument
{
    private static final long serialVersionUID = 1L;
    
    public SendDocumentResponseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName SENDDOCUMENTRESPONSE$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "SendDocumentResponse");
    
    
    /**
     * Gets the "SendDocumentResponse" element
     */
    public org.tempuri.SendDocumentResponseDocument.SendDocumentResponse getSendDocumentResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.SendDocumentResponseDocument.SendDocumentResponse target = null;
            target = (org.tempuri.SendDocumentResponseDocument.SendDocumentResponse)get_store().find_element_user(SENDDOCUMENTRESPONSE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "SendDocumentResponse" element
     */
    public void setSendDocumentResponse(org.tempuri.SendDocumentResponseDocument.SendDocumentResponse sendDocumentResponse)
    {
        generatedSetterHelperImpl(sendDocumentResponse, SENDDOCUMENTRESPONSE$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "SendDocumentResponse" element
     */
    public org.tempuri.SendDocumentResponseDocument.SendDocumentResponse addNewSendDocumentResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.SendDocumentResponseDocument.SendDocumentResponse target = null;
            target = (org.tempuri.SendDocumentResponseDocument.SendDocumentResponse)get_store().add_element_user(SENDDOCUMENTRESPONSE$0);
            return target;
        }
    }
    /**
     * An XML SendDocumentResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class SendDocumentResponseImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.SendDocumentResponseDocument.SendDocumentResponse
    {
        private static final long serialVersionUID = 1L;
        
        public SendDocumentResponseImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName SENDDOCUMENTRESULT$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "SendDocumentResult");
        private static final javax.xml.namespace.QName DOCUMENTID$2 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "documentId");
        
        
        /**
         * Gets the "SendDocumentResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getSendDocumentResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(SENDDOCUMENTRESULT$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "SendDocumentResult" element
         */
        public boolean isNilSendDocumentResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(SENDDOCUMENTRESULT$0, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "SendDocumentResult" element
         */
        public boolean isSetSendDocumentResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(SENDDOCUMENTRESULT$0) != 0;
            }
        }
        
        /**
         * Sets the "SendDocumentResult" element
         */
        public void setSendDocumentResult(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse sendDocumentResult)
        {
            generatedSetterHelperImpl(sendDocumentResult, SENDDOCUMENTRESULT$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "SendDocumentResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewSendDocumentResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(SENDDOCUMENTRESULT$0);
                return target;
            }
        }
        
        /**
         * Nils the "SendDocumentResult" element
         */
        public void setNilSendDocumentResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(SENDDOCUMENTRESULT$0, 0);
                if (target == null)
                {
                    target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(SENDDOCUMENTRESULT$0);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "SendDocumentResult" element
         */
        public void unsetSendDocumentResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(SENDDOCUMENTRESULT$0, 0);
            }
        }
        
        /**
         * Gets the "documentId" element
         */
        public java.lang.String getDocumentId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DOCUMENTID$2, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "documentId" element
         */
        public com.microsoft.schemas.x2003.x10.serialization.Guid xgetDocumentId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.microsoft.schemas.x2003.x10.serialization.Guid target = null;
                target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().find_element_user(DOCUMENTID$2, 0);
                return target;
            }
        }
        
        /**
         * Tests for nil "documentId" element
         */
        public boolean isNilDocumentId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.microsoft.schemas.x2003.x10.serialization.Guid target = null;
                target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().find_element_user(DOCUMENTID$2, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "documentId" element
         */
        public boolean isSetDocumentId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(DOCUMENTID$2) != 0;
            }
        }
        
        /**
         * Sets the "documentId" element
         */
        public void setDocumentId(java.lang.String documentId)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DOCUMENTID$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(DOCUMENTID$2);
                }
                target.setStringValue(documentId);
            }
        }
        
        /**
         * Sets (as xml) the "documentId" element
         */
        public void xsetDocumentId(com.microsoft.schemas.x2003.x10.serialization.Guid documentId)
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.microsoft.schemas.x2003.x10.serialization.Guid target = null;
                target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().find_element_user(DOCUMENTID$2, 0);
                if (target == null)
                {
                    target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().add_element_user(DOCUMENTID$2);
                }
                target.set(documentId);
            }
        }
        
        /**
         * Nils the "documentId" element
         */
        public void setNilDocumentId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.microsoft.schemas.x2003.x10.serialization.Guid target = null;
                target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().find_element_user(DOCUMENTID$2, 0);
                if (target == null)
                {
                    target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().add_element_user(DOCUMENTID$2);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "documentId" element
         */
        public void unsetDocumentId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(DOCUMENTID$2, 0);
            }
        }
    }
}
