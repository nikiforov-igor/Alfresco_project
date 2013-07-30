/*
 * An XML document type.
 * Localname: RegisterUserByCertificateResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.RegisterUserByCertificateResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one RegisterUserByCertificateResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class RegisterUserByCertificateResponseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.RegisterUserByCertificateResponseDocument
{
    private static final long serialVersionUID = 1L;
    
    public RegisterUserByCertificateResponseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName REGISTERUSERBYCERTIFICATERESPONSE$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "RegisterUserByCertificateResponse");
    
    
    /**
     * Gets the "RegisterUserByCertificateResponse" element
     */
    public org.tempuri.RegisterUserByCertificateResponseDocument.RegisterUserByCertificateResponse getRegisterUserByCertificateResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.RegisterUserByCertificateResponseDocument.RegisterUserByCertificateResponse target = null;
            target = (org.tempuri.RegisterUserByCertificateResponseDocument.RegisterUserByCertificateResponse)get_store().find_element_user(REGISTERUSERBYCERTIFICATERESPONSE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "RegisterUserByCertificateResponse" element
     */
    public void setRegisterUserByCertificateResponse(org.tempuri.RegisterUserByCertificateResponseDocument.RegisterUserByCertificateResponse registerUserByCertificateResponse)
    {
        generatedSetterHelperImpl(registerUserByCertificateResponse, REGISTERUSERBYCERTIFICATERESPONSE$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "RegisterUserByCertificateResponse" element
     */
    public org.tempuri.RegisterUserByCertificateResponseDocument.RegisterUserByCertificateResponse addNewRegisterUserByCertificateResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.RegisterUserByCertificateResponseDocument.RegisterUserByCertificateResponse target = null;
            target = (org.tempuri.RegisterUserByCertificateResponseDocument.RegisterUserByCertificateResponse)get_store().add_element_user(REGISTERUSERBYCERTIFICATERESPONSE$0);
            return target;
        }
    }
    /**
     * An XML RegisterUserByCertificateResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class RegisterUserByCertificateResponseImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.RegisterUserByCertificateResponseDocument.RegisterUserByCertificateResponse
    {
        private static final long serialVersionUID = 1L;
        
        public RegisterUserByCertificateResponseImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName REGISTERUSERBYCERTIFICATERESULT$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "RegisterUserByCertificateResult");
        private static final javax.xml.namespace.QName ORGANIZATIONID$2 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "organizationId");
        private static final javax.xml.namespace.QName ORGANIZATIONEDOID$4 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "organizationEdoId");
        
        
        /**
         * Gets the "RegisterUserByCertificateResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getRegisterUserByCertificateResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(REGISTERUSERBYCERTIFICATERESULT$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "RegisterUserByCertificateResult" element
         */
        public boolean isNilRegisterUserByCertificateResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(REGISTERUSERBYCERTIFICATERESULT$0, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "RegisterUserByCertificateResult" element
         */
        public boolean isSetRegisterUserByCertificateResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(REGISTERUSERBYCERTIFICATERESULT$0) != 0;
            }
        }
        
        /**
         * Sets the "RegisterUserByCertificateResult" element
         */
        public void setRegisterUserByCertificateResult(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse registerUserByCertificateResult)
        {
            generatedSetterHelperImpl(registerUserByCertificateResult, REGISTERUSERBYCERTIFICATERESULT$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "RegisterUserByCertificateResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewRegisterUserByCertificateResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(REGISTERUSERBYCERTIFICATERESULT$0);
                return target;
            }
        }
        
        /**
         * Nils the "RegisterUserByCertificateResult" element
         */
        public void setNilRegisterUserByCertificateResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(REGISTERUSERBYCERTIFICATERESULT$0, 0);
                if (target == null)
                {
                    target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(REGISTERUSERBYCERTIFICATERESULT$0);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "RegisterUserByCertificateResult" element
         */
        public void unsetRegisterUserByCertificateResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(REGISTERUSERBYCERTIFICATERESULT$0, 0);
            }
        }
        
        /**
         * Gets the "organizationId" element
         */
        public java.lang.String getOrganizationId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ORGANIZATIONID$2, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "organizationId" element
         */
        public com.microsoft.schemas.x2003.x10.serialization.Guid xgetOrganizationId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.microsoft.schemas.x2003.x10.serialization.Guid target = null;
                target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().find_element_user(ORGANIZATIONID$2, 0);
                return target;
            }
        }
        
        /**
         * Tests for nil "organizationId" element
         */
        public boolean isNilOrganizationId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.microsoft.schemas.x2003.x10.serialization.Guid target = null;
                target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().find_element_user(ORGANIZATIONID$2, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "organizationId" element
         */
        public boolean isSetOrganizationId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(ORGANIZATIONID$2) != 0;
            }
        }
        
        /**
         * Sets the "organizationId" element
         */
        public void setOrganizationId(java.lang.String organizationId)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ORGANIZATIONID$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ORGANIZATIONID$2);
                }
                target.setStringValue(organizationId);
            }
        }
        
        /**
         * Sets (as xml) the "organizationId" element
         */
        public void xsetOrganizationId(com.microsoft.schemas.x2003.x10.serialization.Guid organizationId)
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.microsoft.schemas.x2003.x10.serialization.Guid target = null;
                target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().find_element_user(ORGANIZATIONID$2, 0);
                if (target == null)
                {
                    target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().add_element_user(ORGANIZATIONID$2);
                }
                target.set(organizationId);
            }
        }
        
        /**
         * Nils the "organizationId" element
         */
        public void setNilOrganizationId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.microsoft.schemas.x2003.x10.serialization.Guid target = null;
                target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().find_element_user(ORGANIZATIONID$2, 0);
                if (target == null)
                {
                    target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().add_element_user(ORGANIZATIONID$2);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "organizationId" element
         */
        public void unsetOrganizationId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(ORGANIZATIONID$2, 0);
            }
        }
        
        /**
         * Gets the "organizationEdoId" element
         */
        public java.lang.String getOrganizationEdoId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ORGANIZATIONEDOID$4, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "organizationEdoId" element
         */
        public org.apache.xmlbeans.XmlString xgetOrganizationEdoId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ORGANIZATIONEDOID$4, 0);
                return target;
            }
        }
        
        /**
         * Tests for nil "organizationEdoId" element
         */
        public boolean isNilOrganizationEdoId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ORGANIZATIONEDOID$4, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "organizationEdoId" element
         */
        public boolean isSetOrganizationEdoId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(ORGANIZATIONEDOID$4) != 0;
            }
        }
        
        /**
         * Sets the "organizationEdoId" element
         */
        public void setOrganizationEdoId(java.lang.String organizationEdoId)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ORGANIZATIONEDOID$4, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ORGANIZATIONEDOID$4);
                }
                target.setStringValue(organizationEdoId);
            }
        }
        
        /**
         * Sets (as xml) the "organizationEdoId" element
         */
        public void xsetOrganizationEdoId(org.apache.xmlbeans.XmlString organizationEdoId)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ORGANIZATIONEDOID$4, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(ORGANIZATIONEDOID$4);
                }
                target.set(organizationEdoId);
            }
        }
        
        /**
         * Nils the "organizationEdoId" element
         */
        public void setNilOrganizationEdoId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ORGANIZATIONEDOID$4, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(ORGANIZATIONEDOID$4);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "organizationEdoId" element
         */
        public void unsetOrganizationEdoId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(ORGANIZATIONEDOID$4, 0);
            }
        }
    }
}
