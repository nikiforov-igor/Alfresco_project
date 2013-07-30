/*
 * An XML document type.
 * Localname: CheckIfPersonIsEmployeeByCertificateResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one CheckIfPersonIsEmployeeByCertificateResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class CheckIfPersonIsEmployeeByCertificateResponseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument
{
    private static final long serialVersionUID = 1L;
    
    public CheckIfPersonIsEmployeeByCertificateResponseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName CHECKIFPERSONISEMPLOYEEBYCERTIFICATERESPONSE$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "CheckIfPersonIsEmployeeByCertificateResponse");
    
    
    /**
     * Gets the "CheckIfPersonIsEmployeeByCertificateResponse" element
     */
    public org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument.CheckIfPersonIsEmployeeByCertificateResponse getCheckIfPersonIsEmployeeByCertificateResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument.CheckIfPersonIsEmployeeByCertificateResponse target = null;
            target = (org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument.CheckIfPersonIsEmployeeByCertificateResponse)get_store().find_element_user(CHECKIFPERSONISEMPLOYEEBYCERTIFICATERESPONSE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "CheckIfPersonIsEmployeeByCertificateResponse" element
     */
    public void setCheckIfPersonIsEmployeeByCertificateResponse(org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument.CheckIfPersonIsEmployeeByCertificateResponse checkIfPersonIsEmployeeByCertificateResponse)
    {
        generatedSetterHelperImpl(checkIfPersonIsEmployeeByCertificateResponse, CHECKIFPERSONISEMPLOYEEBYCERTIFICATERESPONSE$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "CheckIfPersonIsEmployeeByCertificateResponse" element
     */
    public org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument.CheckIfPersonIsEmployeeByCertificateResponse addNewCheckIfPersonIsEmployeeByCertificateResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument.CheckIfPersonIsEmployeeByCertificateResponse target = null;
            target = (org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument.CheckIfPersonIsEmployeeByCertificateResponse)get_store().add_element_user(CHECKIFPERSONISEMPLOYEEBYCERTIFICATERESPONSE$0);
            return target;
        }
    }
    /**
     * An XML CheckIfPersonIsEmployeeByCertificateResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class CheckIfPersonIsEmployeeByCertificateResponseImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument.CheckIfPersonIsEmployeeByCertificateResponse
    {
        private static final long serialVersionUID = 1L;
        
        public CheckIfPersonIsEmployeeByCertificateResponseImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName CHECKIFPERSONISEMPLOYEEBYCERTIFICATERESULT$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "CheckIfPersonIsEmployeeByCertificateResult");
        private static final javax.xml.namespace.QName ISEMPLOYEE$2 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "isEmployee");
        
        
        /**
         * Gets the "CheckIfPersonIsEmployeeByCertificateResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getCheckIfPersonIsEmployeeByCertificateResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(CHECKIFPERSONISEMPLOYEEBYCERTIFICATERESULT$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "CheckIfPersonIsEmployeeByCertificateResult" element
         */
        public boolean isNilCheckIfPersonIsEmployeeByCertificateResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(CHECKIFPERSONISEMPLOYEEBYCERTIFICATERESULT$0, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "CheckIfPersonIsEmployeeByCertificateResult" element
         */
        public boolean isSetCheckIfPersonIsEmployeeByCertificateResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(CHECKIFPERSONISEMPLOYEEBYCERTIFICATERESULT$0) != 0;
            }
        }
        
        /**
         * Sets the "CheckIfPersonIsEmployeeByCertificateResult" element
         */
        public void setCheckIfPersonIsEmployeeByCertificateResult(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse checkIfPersonIsEmployeeByCertificateResult)
        {
            generatedSetterHelperImpl(checkIfPersonIsEmployeeByCertificateResult, CHECKIFPERSONISEMPLOYEEBYCERTIFICATERESULT$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "CheckIfPersonIsEmployeeByCertificateResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewCheckIfPersonIsEmployeeByCertificateResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(CHECKIFPERSONISEMPLOYEEBYCERTIFICATERESULT$0);
                return target;
            }
        }
        
        /**
         * Nils the "CheckIfPersonIsEmployeeByCertificateResult" element
         */
        public void setNilCheckIfPersonIsEmployeeByCertificateResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(CHECKIFPERSONISEMPLOYEEBYCERTIFICATERESULT$0, 0);
                if (target == null)
                {
                    target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(CHECKIFPERSONISEMPLOYEEBYCERTIFICATERESULT$0);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "CheckIfPersonIsEmployeeByCertificateResult" element
         */
        public void unsetCheckIfPersonIsEmployeeByCertificateResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(CHECKIFPERSONISEMPLOYEEBYCERTIFICATERESULT$0, 0);
            }
        }
        
        /**
         * Gets the "isEmployee" element
         */
        public boolean getIsEmployee()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ISEMPLOYEE$2, 0);
                if (target == null)
                {
                    return false;
                }
                return target.getBooleanValue();
            }
        }
        
        /**
         * Gets (as xml) the "isEmployee" element
         */
        public org.apache.xmlbeans.XmlBoolean xgetIsEmployee()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBoolean target = null;
                target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(ISEMPLOYEE$2, 0);
                return target;
            }
        }
        
        /**
         * True if has "isEmployee" element
         */
        public boolean isSetIsEmployee()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(ISEMPLOYEE$2) != 0;
            }
        }
        
        /**
         * Sets the "isEmployee" element
         */
        public void setIsEmployee(boolean isEmployee)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ISEMPLOYEE$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ISEMPLOYEE$2);
                }
                target.setBooleanValue(isEmployee);
            }
        }
        
        /**
         * Sets (as xml) the "isEmployee" element
         */
        public void xsetIsEmployee(org.apache.xmlbeans.XmlBoolean isEmployee)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBoolean target = null;
                target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(ISEMPLOYEE$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlBoolean)get_store().add_element_user(ISEMPLOYEE$2);
                }
                target.set(isEmployee);
            }
        }
        
        /**
         * Unsets the "isEmployee" element
         */
        public void unsetIsEmployee()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(ISEMPLOYEE$2, 0);
            }
        }
    }
}
