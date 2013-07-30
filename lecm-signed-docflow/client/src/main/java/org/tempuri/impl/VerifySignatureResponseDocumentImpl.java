/*
 * An XML document type.
 * Localname: VerifySignatureResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.VerifySignatureResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one VerifySignatureResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class VerifySignatureResponseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.VerifySignatureResponseDocument
{
    private static final long serialVersionUID = 1L;
    
    public VerifySignatureResponseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName VERIFYSIGNATURERESPONSE$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "VerifySignatureResponse");
    
    
    /**
     * Gets the "VerifySignatureResponse" element
     */
    public org.tempuri.VerifySignatureResponseDocument.VerifySignatureResponse getVerifySignatureResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.VerifySignatureResponseDocument.VerifySignatureResponse target = null;
            target = (org.tempuri.VerifySignatureResponseDocument.VerifySignatureResponse)get_store().find_element_user(VERIFYSIGNATURERESPONSE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "VerifySignatureResponse" element
     */
    public void setVerifySignatureResponse(org.tempuri.VerifySignatureResponseDocument.VerifySignatureResponse verifySignatureResponse)
    {
        generatedSetterHelperImpl(verifySignatureResponse, VERIFYSIGNATURERESPONSE$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "VerifySignatureResponse" element
     */
    public org.tempuri.VerifySignatureResponseDocument.VerifySignatureResponse addNewVerifySignatureResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.VerifySignatureResponseDocument.VerifySignatureResponse target = null;
            target = (org.tempuri.VerifySignatureResponseDocument.VerifySignatureResponse)get_store().add_element_user(VERIFYSIGNATURERESPONSE$0);
            return target;
        }
    }
    /**
     * An XML VerifySignatureResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class VerifySignatureResponseImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.VerifySignatureResponseDocument.VerifySignatureResponse
    {
        private static final long serialVersionUID = 1L;
        
        public VerifySignatureResponseImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName VERIFYSIGNATURERESULT$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "VerifySignatureResult");
        private static final javax.xml.namespace.QName SIGNERINFO$2 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "signerInfo");
        private static final javax.xml.namespace.QName ISSIGNATUREVALID$4 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "isSignatureValid");
        
        
        /**
         * Gets the "VerifySignatureResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getVerifySignatureResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(VERIFYSIGNATURERESULT$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "VerifySignatureResult" element
         */
        public boolean isNilVerifySignatureResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(VERIFYSIGNATURERESULT$0, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "VerifySignatureResult" element
         */
        public boolean isSetVerifySignatureResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(VERIFYSIGNATURERESULT$0) != 0;
            }
        }
        
        /**
         * Sets the "VerifySignatureResult" element
         */
        public void setVerifySignatureResult(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse verifySignatureResult)
        {
            generatedSetterHelperImpl(verifySignatureResult, VERIFYSIGNATURERESULT$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "VerifySignatureResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewVerifySignatureResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(VERIFYSIGNATURERESULT$0);
                return target;
            }
        }
        
        /**
         * Nils the "VerifySignatureResult" element
         */
        public void setNilVerifySignatureResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(VERIFYSIGNATURERESULT$0, 0);
                if (target == null)
                {
                    target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(VERIFYSIGNATURERESULT$0);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "VerifySignatureResult" element
         */
        public void unsetVerifySignatureResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(VERIFYSIGNATURERESULT$0, 0);
            }
        }
        
        /**
         * Gets the "signerInfo" element
         */
        public java.lang.String getSignerInfo()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SIGNERINFO$2, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "signerInfo" element
         */
        public org.apache.xmlbeans.XmlString xgetSignerInfo()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SIGNERINFO$2, 0);
                return target;
            }
        }
        
        /**
         * Tests for nil "signerInfo" element
         */
        public boolean isNilSignerInfo()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SIGNERINFO$2, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "signerInfo" element
         */
        public boolean isSetSignerInfo()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(SIGNERINFO$2) != 0;
            }
        }
        
        /**
         * Sets the "signerInfo" element
         */
        public void setSignerInfo(java.lang.String signerInfo)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SIGNERINFO$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(SIGNERINFO$2);
                }
                target.setStringValue(signerInfo);
            }
        }
        
        /**
         * Sets (as xml) the "signerInfo" element
         */
        public void xsetSignerInfo(org.apache.xmlbeans.XmlString signerInfo)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SIGNERINFO$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(SIGNERINFO$2);
                }
                target.set(signerInfo);
            }
        }
        
        /**
         * Nils the "signerInfo" element
         */
        public void setNilSignerInfo()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SIGNERINFO$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(SIGNERINFO$2);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "signerInfo" element
         */
        public void unsetSignerInfo()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(SIGNERINFO$2, 0);
            }
        }
        
        /**
         * Gets the "isSignatureValid" element
         */
        public boolean getIsSignatureValid()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ISSIGNATUREVALID$4, 0);
                if (target == null)
                {
                    return false;
                }
                return target.getBooleanValue();
            }
        }
        
        /**
         * Gets (as xml) the "isSignatureValid" element
         */
        public org.apache.xmlbeans.XmlBoolean xgetIsSignatureValid()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBoolean target = null;
                target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(ISSIGNATUREVALID$4, 0);
                return target;
            }
        }
        
        /**
         * True if has "isSignatureValid" element
         */
        public boolean isSetIsSignatureValid()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(ISSIGNATUREVALID$4) != 0;
            }
        }
        
        /**
         * Sets the "isSignatureValid" element
         */
        public void setIsSignatureValid(boolean isSignatureValid)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ISSIGNATUREVALID$4, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ISSIGNATUREVALID$4);
                }
                target.setBooleanValue(isSignatureValid);
            }
        }
        
        /**
         * Sets (as xml) the "isSignatureValid" element
         */
        public void xsetIsSignatureValid(org.apache.xmlbeans.XmlBoolean isSignatureValid)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBoolean target = null;
                target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(ISSIGNATUREVALID$4, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlBoolean)get_store().add_element_user(ISSIGNATUREVALID$4);
                }
                target.set(isSignatureValid);
            }
        }
        
        /**
         * Unsets the "isSignatureValid" element
         */
        public void unsetIsSignatureValid()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(ISSIGNATUREVALID$4, 0);
            }
        }
    }
}
