/*
 * An XML document type.
 * Localname: SignDocumentResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.SignDocumentResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one SignDocumentResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class SignDocumentResponseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.SignDocumentResponseDocument
{
    private static final long serialVersionUID = 1L;
    
    public SignDocumentResponseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName SIGNDOCUMENTRESPONSE$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "SignDocumentResponse");
    
    
    /**
     * Gets the "SignDocumentResponse" element
     */
    public org.tempuri.SignDocumentResponseDocument.SignDocumentResponse getSignDocumentResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.SignDocumentResponseDocument.SignDocumentResponse target = null;
            target = (org.tempuri.SignDocumentResponseDocument.SignDocumentResponse)get_store().find_element_user(SIGNDOCUMENTRESPONSE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "SignDocumentResponse" element
     */
    public void setSignDocumentResponse(org.tempuri.SignDocumentResponseDocument.SignDocumentResponse signDocumentResponse)
    {
        generatedSetterHelperImpl(signDocumentResponse, SIGNDOCUMENTRESPONSE$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "SignDocumentResponse" element
     */
    public org.tempuri.SignDocumentResponseDocument.SignDocumentResponse addNewSignDocumentResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.SignDocumentResponseDocument.SignDocumentResponse target = null;
            target = (org.tempuri.SignDocumentResponseDocument.SignDocumentResponse)get_store().add_element_user(SIGNDOCUMENTRESPONSE$0);
            return target;
        }
    }
    /**
     * An XML SignDocumentResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class SignDocumentResponseImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.SignDocumentResponseDocument.SignDocumentResponse
    {
        private static final long serialVersionUID = 1L;
        
        public SignDocumentResponseImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName SIGNDOCUMENTRESULT$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "SignDocumentResult");
        private static final javax.xml.namespace.QName SIGNATURE$2 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "signature");
        
        
        /**
         * Gets the "SignDocumentResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getSignDocumentResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(SIGNDOCUMENTRESULT$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "SignDocumentResult" element
         */
        public boolean isNilSignDocumentResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(SIGNDOCUMENTRESULT$0, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "SignDocumentResult" element
         */
        public boolean isSetSignDocumentResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(SIGNDOCUMENTRESULT$0) != 0;
            }
        }
        
        /**
         * Sets the "SignDocumentResult" element
         */
        public void setSignDocumentResult(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse signDocumentResult)
        {
            generatedSetterHelperImpl(signDocumentResult, SIGNDOCUMENTRESULT$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "SignDocumentResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewSignDocumentResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(SIGNDOCUMENTRESULT$0);
                return target;
            }
        }
        
        /**
         * Nils the "SignDocumentResult" element
         */
        public void setNilSignDocumentResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(SIGNDOCUMENTRESULT$0, 0);
                if (target == null)
                {
                    target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(SIGNDOCUMENTRESULT$0);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "SignDocumentResult" element
         */
        public void unsetSignDocumentResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(SIGNDOCUMENTRESULT$0, 0);
            }
        }
        
        /**
         * Gets the "signature" element
         */
        public byte[] getSignature()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SIGNATURE$2, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getByteArrayValue();
            }
        }
        
        /**
         * Gets (as xml) the "signature" element
         */
        public org.apache.xmlbeans.XmlBase64Binary xgetSignature()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBase64Binary target = null;
                target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(SIGNATURE$2, 0);
                return target;
            }
        }
        
        /**
         * Tests for nil "signature" element
         */
        public boolean isNilSignature()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBase64Binary target = null;
                target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(SIGNATURE$2, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "signature" element
         */
        public boolean isSetSignature()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(SIGNATURE$2) != 0;
            }
        }
        
        /**
         * Sets the "signature" element
         */
        public void setSignature(byte[] signature)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SIGNATURE$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(SIGNATURE$2);
                }
                target.setByteArrayValue(signature);
            }
        }
        
        /**
         * Sets (as xml) the "signature" element
         */
        public void xsetSignature(org.apache.xmlbeans.XmlBase64Binary signature)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBase64Binary target = null;
                target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(SIGNATURE$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlBase64Binary)get_store().add_element_user(SIGNATURE$2);
                }
                target.set(signature);
            }
        }
        
        /**
         * Nils the "signature" element
         */
        public void setNilSignature()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBase64Binary target = null;
                target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(SIGNATURE$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlBase64Binary)get_store().add_element_user(SIGNATURE$2);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "signature" element
         */
        public void unsetSignature()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(SIGNATURE$2, 0);
            }
        }
    }
}
