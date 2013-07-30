/*
 * An XML document type.
 * Localname: FinishRegisterUserBySmsResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.FinishRegisterUserBySmsResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one FinishRegisterUserBySmsResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class FinishRegisterUserBySmsResponseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.FinishRegisterUserBySmsResponseDocument
{
    private static final long serialVersionUID = 1L;
    
    public FinishRegisterUserBySmsResponseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName FINISHREGISTERUSERBYSMSRESPONSE$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "FinishRegisterUserBySmsResponse");
    
    
    /**
     * Gets the "FinishRegisterUserBySmsResponse" element
     */
    public org.tempuri.FinishRegisterUserBySmsResponseDocument.FinishRegisterUserBySmsResponse getFinishRegisterUserBySmsResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.FinishRegisterUserBySmsResponseDocument.FinishRegisterUserBySmsResponse target = null;
            target = (org.tempuri.FinishRegisterUserBySmsResponseDocument.FinishRegisterUserBySmsResponse)get_store().find_element_user(FINISHREGISTERUSERBYSMSRESPONSE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "FinishRegisterUserBySmsResponse" element
     */
    public void setFinishRegisterUserBySmsResponse(org.tempuri.FinishRegisterUserBySmsResponseDocument.FinishRegisterUserBySmsResponse finishRegisterUserBySmsResponse)
    {
        generatedSetterHelperImpl(finishRegisterUserBySmsResponse, FINISHREGISTERUSERBYSMSRESPONSE$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "FinishRegisterUserBySmsResponse" element
     */
    public org.tempuri.FinishRegisterUserBySmsResponseDocument.FinishRegisterUserBySmsResponse addNewFinishRegisterUserBySmsResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.FinishRegisterUserBySmsResponseDocument.FinishRegisterUserBySmsResponse target = null;
            target = (org.tempuri.FinishRegisterUserBySmsResponseDocument.FinishRegisterUserBySmsResponse)get_store().add_element_user(FINISHREGISTERUSERBYSMSRESPONSE$0);
            return target;
        }
    }
    /**
     * An XML FinishRegisterUserBySmsResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class FinishRegisterUserBySmsResponseImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.FinishRegisterUserBySmsResponseDocument.FinishRegisterUserBySmsResponse
    {
        private static final long serialVersionUID = 1L;
        
        public FinishRegisterUserBySmsResponseImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName FINISHREGISTERUSERBYSMSRESULT$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "FinishRegisterUserBySmsResult");
        private static final javax.xml.namespace.QName ORGANIZATIONID$2 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "organizationId");
        private static final javax.xml.namespace.QName ORGANIZATIONEDOID$4 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "organizationEdoId");
        
        
        /**
         * Gets the "FinishRegisterUserBySmsResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getFinishRegisterUserBySmsResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(FINISHREGISTERUSERBYSMSRESULT$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "FinishRegisterUserBySmsResult" element
         */
        public boolean isNilFinishRegisterUserBySmsResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(FINISHREGISTERUSERBYSMSRESULT$0, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "FinishRegisterUserBySmsResult" element
         */
        public boolean isSetFinishRegisterUserBySmsResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(FINISHREGISTERUSERBYSMSRESULT$0) != 0;
            }
        }
        
        /**
         * Sets the "FinishRegisterUserBySmsResult" element
         */
        public void setFinishRegisterUserBySmsResult(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse finishRegisterUserBySmsResult)
        {
            generatedSetterHelperImpl(finishRegisterUserBySmsResult, FINISHREGISTERUSERBYSMSRESULT$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "FinishRegisterUserBySmsResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewFinishRegisterUserBySmsResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(FINISHREGISTERUSERBYSMSRESULT$0);
                return target;
            }
        }
        
        /**
         * Nils the "FinishRegisterUserBySmsResult" element
         */
        public void setNilFinishRegisterUserBySmsResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(FINISHREGISTERUSERBYSMSRESULT$0, 0);
                if (target == null)
                {
                    target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(FINISHREGISTERUSERBYSMSRESULT$0);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "FinishRegisterUserBySmsResult" element
         */
        public void unsetFinishRegisterUserBySmsResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(FINISHREGISTERUSERBYSMSRESULT$0, 0);
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
