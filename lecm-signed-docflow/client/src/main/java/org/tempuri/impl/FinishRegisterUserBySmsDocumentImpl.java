/*
 * An XML document type.
 * Localname: FinishRegisterUserBySms
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.FinishRegisterUserBySmsDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one FinishRegisterUserBySms(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class FinishRegisterUserBySmsDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.FinishRegisterUserBySmsDocument
{
    private static final long serialVersionUID = 1L;
    
    public FinishRegisterUserBySmsDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName FINISHREGISTERUSERBYSMS$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "FinishRegisterUserBySms");
    
    
    /**
     * Gets the "FinishRegisterUserBySms" element
     */
    public org.tempuri.FinishRegisterUserBySmsDocument.FinishRegisterUserBySms getFinishRegisterUserBySms()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.FinishRegisterUserBySmsDocument.FinishRegisterUserBySms target = null;
            target = (org.tempuri.FinishRegisterUserBySmsDocument.FinishRegisterUserBySms)get_store().find_element_user(FINISHREGISTERUSERBYSMS$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "FinishRegisterUserBySms" element
     */
    public void setFinishRegisterUserBySms(org.tempuri.FinishRegisterUserBySmsDocument.FinishRegisterUserBySms finishRegisterUserBySms)
    {
        generatedSetterHelperImpl(finishRegisterUserBySms, FINISHREGISTERUSERBYSMS$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "FinishRegisterUserBySms" element
     */
    public org.tempuri.FinishRegisterUserBySmsDocument.FinishRegisterUserBySms addNewFinishRegisterUserBySms()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.FinishRegisterUserBySmsDocument.FinishRegisterUserBySms target = null;
            target = (org.tempuri.FinishRegisterUserBySmsDocument.FinishRegisterUserBySms)get_store().add_element_user(FINISHREGISTERUSERBYSMS$0);
            return target;
        }
    }
    /**
     * An XML FinishRegisterUserBySms(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class FinishRegisterUserBySmsImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.FinishRegisterUserBySmsDocument.FinishRegisterUserBySms
    {
        private static final long serialVersionUID = 1L;
        
        public FinishRegisterUserBySmsImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName OPERATORCODE$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "operatorCode");
        private static final javax.xml.namespace.QName MOBILENUMBER$2 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "mobileNumber");
        private static final javax.xml.namespace.QName PARTNERKEY$4 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "partnerKey");
        private static final javax.xml.namespace.QName INN$6 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "inn");
        private static final javax.xml.namespace.QName KPP$8 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "kpp");
        private static final javax.xml.namespace.QName PASSWORD$10 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "password");
        private static final javax.xml.namespace.QName USERIDBYPARTNERSYSTEM$12 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "userIdByPartnerSystem");
        
        
        /**
         * Gets the "operatorCode" element
         */
        public java.lang.String getOperatorCode()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(OPERATORCODE$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "operatorCode" element
         */
        public org.apache.xmlbeans.XmlString xgetOperatorCode()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORCODE$0, 0);
                return target;
            }
        }
        
        /**
         * Tests for nil "operatorCode" element
         */
        public boolean isNilOperatorCode()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORCODE$0, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "operatorCode" element
         */
        public boolean isSetOperatorCode()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(OPERATORCODE$0) != 0;
            }
        }
        
        /**
         * Sets the "operatorCode" element
         */
        public void setOperatorCode(java.lang.String operatorCode)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(OPERATORCODE$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(OPERATORCODE$0);
                }
                target.setStringValue(operatorCode);
            }
        }
        
        /**
         * Sets (as xml) the "operatorCode" element
         */
        public void xsetOperatorCode(org.apache.xmlbeans.XmlString operatorCode)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORCODE$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(OPERATORCODE$0);
                }
                target.set(operatorCode);
            }
        }
        
        /**
         * Nils the "operatorCode" element
         */
        public void setNilOperatorCode()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORCODE$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(OPERATORCODE$0);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "operatorCode" element
         */
        public void unsetOperatorCode()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(OPERATORCODE$0, 0);
            }
        }
        
        /**
         * Gets the "mobileNumber" element
         */
        public java.lang.String getMobileNumber()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(MOBILENUMBER$2, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "mobileNumber" element
         */
        public org.apache.xmlbeans.XmlString xgetMobileNumber()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(MOBILENUMBER$2, 0);
                return target;
            }
        }
        
        /**
         * Tests for nil "mobileNumber" element
         */
        public boolean isNilMobileNumber()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(MOBILENUMBER$2, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "mobileNumber" element
         */
        public boolean isSetMobileNumber()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(MOBILENUMBER$2) != 0;
            }
        }
        
        /**
         * Sets the "mobileNumber" element
         */
        public void setMobileNumber(java.lang.String mobileNumber)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(MOBILENUMBER$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(MOBILENUMBER$2);
                }
                target.setStringValue(mobileNumber);
            }
        }
        
        /**
         * Sets (as xml) the "mobileNumber" element
         */
        public void xsetMobileNumber(org.apache.xmlbeans.XmlString mobileNumber)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(MOBILENUMBER$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(MOBILENUMBER$2);
                }
                target.set(mobileNumber);
            }
        }
        
        /**
         * Nils the "mobileNumber" element
         */
        public void setNilMobileNumber()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(MOBILENUMBER$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(MOBILENUMBER$2);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "mobileNumber" element
         */
        public void unsetMobileNumber()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(MOBILENUMBER$2, 0);
            }
        }
        
        /**
         * Gets the "partnerKey" element
         */
        public java.lang.String getPartnerKey()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PARTNERKEY$4, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "partnerKey" element
         */
        public com.microsoft.schemas.x2003.x10.serialization.Guid xgetPartnerKey()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.microsoft.schemas.x2003.x10.serialization.Guid target = null;
                target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().find_element_user(PARTNERKEY$4, 0);
                return target;
            }
        }
        
        /**
         * True if has "partnerKey" element
         */
        public boolean isSetPartnerKey()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(PARTNERKEY$4) != 0;
            }
        }
        
        /**
         * Sets the "partnerKey" element
         */
        public void setPartnerKey(java.lang.String partnerKey)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PARTNERKEY$4, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PARTNERKEY$4);
                }
                target.setStringValue(partnerKey);
            }
        }
        
        /**
         * Sets (as xml) the "partnerKey" element
         */
        public void xsetPartnerKey(com.microsoft.schemas.x2003.x10.serialization.Guid partnerKey)
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.microsoft.schemas.x2003.x10.serialization.Guid target = null;
                target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().find_element_user(PARTNERKEY$4, 0);
                if (target == null)
                {
                    target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().add_element_user(PARTNERKEY$4);
                }
                target.set(partnerKey);
            }
        }
        
        /**
         * Unsets the "partnerKey" element
         */
        public void unsetPartnerKey()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(PARTNERKEY$4, 0);
            }
        }
        
        /**
         * Gets the "inn" element
         */
        public java.lang.String getInn()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(INN$6, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "inn" element
         */
        public org.apache.xmlbeans.XmlString xgetInn()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INN$6, 0);
                return target;
            }
        }
        
        /**
         * Tests for nil "inn" element
         */
        public boolean isNilInn()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INN$6, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "inn" element
         */
        public boolean isSetInn()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(INN$6) != 0;
            }
        }
        
        /**
         * Sets the "inn" element
         */
        public void setInn(java.lang.String inn)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(INN$6, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(INN$6);
                }
                target.setStringValue(inn);
            }
        }
        
        /**
         * Sets (as xml) the "inn" element
         */
        public void xsetInn(org.apache.xmlbeans.XmlString inn)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INN$6, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(INN$6);
                }
                target.set(inn);
            }
        }
        
        /**
         * Nils the "inn" element
         */
        public void setNilInn()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INN$6, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(INN$6);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "inn" element
         */
        public void unsetInn()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(INN$6, 0);
            }
        }
        
        /**
         * Gets the "kpp" element
         */
        public java.lang.String getKpp()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(KPP$8, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "kpp" element
         */
        public org.apache.xmlbeans.XmlString xgetKpp()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(KPP$8, 0);
                return target;
            }
        }
        
        /**
         * Tests for nil "kpp" element
         */
        public boolean isNilKpp()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(KPP$8, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "kpp" element
         */
        public boolean isSetKpp()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(KPP$8) != 0;
            }
        }
        
        /**
         * Sets the "kpp" element
         */
        public void setKpp(java.lang.String kpp)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(KPP$8, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(KPP$8);
                }
                target.setStringValue(kpp);
            }
        }
        
        /**
         * Sets (as xml) the "kpp" element
         */
        public void xsetKpp(org.apache.xmlbeans.XmlString kpp)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(KPP$8, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(KPP$8);
                }
                target.set(kpp);
            }
        }
        
        /**
         * Nils the "kpp" element
         */
        public void setNilKpp()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(KPP$8, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(KPP$8);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "kpp" element
         */
        public void unsetKpp()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(KPP$8, 0);
            }
        }
        
        /**
         * Gets the "password" element
         */
        public java.lang.String getPassword()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PASSWORD$10, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "password" element
         */
        public org.apache.xmlbeans.XmlString xgetPassword()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PASSWORD$10, 0);
                return target;
            }
        }
        
        /**
         * Tests for nil "password" element
         */
        public boolean isNilPassword()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PASSWORD$10, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "password" element
         */
        public boolean isSetPassword()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(PASSWORD$10) != 0;
            }
        }
        
        /**
         * Sets the "password" element
         */
        public void setPassword(java.lang.String password)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PASSWORD$10, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PASSWORD$10);
                }
                target.setStringValue(password);
            }
        }
        
        /**
         * Sets (as xml) the "password" element
         */
        public void xsetPassword(org.apache.xmlbeans.XmlString password)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PASSWORD$10, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PASSWORD$10);
                }
                target.set(password);
            }
        }
        
        /**
         * Nils the "password" element
         */
        public void setNilPassword()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PASSWORD$10, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PASSWORD$10);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "password" element
         */
        public void unsetPassword()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(PASSWORD$10, 0);
            }
        }
        
        /**
         * Gets the "userIdByPartnerSystem" element
         */
        public java.lang.String getUserIdByPartnerSystem()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(USERIDBYPARTNERSYSTEM$12, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "userIdByPartnerSystem" element
         */
        public com.microsoft.schemas.x2003.x10.serialization.Guid xgetUserIdByPartnerSystem()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.microsoft.schemas.x2003.x10.serialization.Guid target = null;
                target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().find_element_user(USERIDBYPARTNERSYSTEM$12, 0);
                return target;
            }
        }
        
        /**
         * True if has "userIdByPartnerSystem" element
         */
        public boolean isSetUserIdByPartnerSystem()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(USERIDBYPARTNERSYSTEM$12) != 0;
            }
        }
        
        /**
         * Sets the "userIdByPartnerSystem" element
         */
        public void setUserIdByPartnerSystem(java.lang.String userIdByPartnerSystem)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(USERIDBYPARTNERSYSTEM$12, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(USERIDBYPARTNERSYSTEM$12);
                }
                target.setStringValue(userIdByPartnerSystem);
            }
        }
        
        /**
         * Sets (as xml) the "userIdByPartnerSystem" element
         */
        public void xsetUserIdByPartnerSystem(com.microsoft.schemas.x2003.x10.serialization.Guid userIdByPartnerSystem)
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.microsoft.schemas.x2003.x10.serialization.Guid target = null;
                target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().find_element_user(USERIDBYPARTNERSYSTEM$12, 0);
                if (target == null)
                {
                    target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().add_element_user(USERIDBYPARTNERSYSTEM$12);
                }
                target.set(userIdByPartnerSystem);
            }
        }
        
        /**
         * Unsets the "userIdByPartnerSystem" element
         */
        public void unsetUserIdByPartnerSystem()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(USERIDBYPARTNERSYSTEM$12, 0);
            }
        }
    }
}
