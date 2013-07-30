/*
 * An XML document type.
 * Localname: RegisterUserByCertificate
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.RegisterUserByCertificateDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one RegisterUserByCertificate(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class RegisterUserByCertificateDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.RegisterUserByCertificateDocument
{
    private static final long serialVersionUID = 1L;
    
    public RegisterUserByCertificateDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName REGISTERUSERBYCERTIFICATE$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "RegisterUserByCertificate");
    
    
    /**
     * Gets the "RegisterUserByCertificate" element
     */
    public org.tempuri.RegisterUserByCertificateDocument.RegisterUserByCertificate getRegisterUserByCertificate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.RegisterUserByCertificateDocument.RegisterUserByCertificate target = null;
            target = (org.tempuri.RegisterUserByCertificateDocument.RegisterUserByCertificate)get_store().find_element_user(REGISTERUSERBYCERTIFICATE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "RegisterUserByCertificate" element
     */
    public void setRegisterUserByCertificate(org.tempuri.RegisterUserByCertificateDocument.RegisterUserByCertificate registerUserByCertificate)
    {
        generatedSetterHelperImpl(registerUserByCertificate, REGISTERUSERBYCERTIFICATE$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "RegisterUserByCertificate" element
     */
    public org.tempuri.RegisterUserByCertificateDocument.RegisterUserByCertificate addNewRegisterUserByCertificate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.RegisterUserByCertificateDocument.RegisterUserByCertificate target = null;
            target = (org.tempuri.RegisterUserByCertificateDocument.RegisterUserByCertificate)get_store().add_element_user(REGISTERUSERBYCERTIFICATE$0);
            return target;
        }
    }
    /**
     * An XML RegisterUserByCertificate(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class RegisterUserByCertificateImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.RegisterUserByCertificateDocument.RegisterUserByCertificate
    {
        private static final long serialVersionUID = 1L;
        
        public RegisterUserByCertificateImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName OPERATORCODE$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "operatorCode");
        private static final javax.xml.namespace.QName PARTNERKEY$2 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "partnerKey");
        private static final javax.xml.namespace.QName ORGANIZATIONINN$4 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "organizationInn");
        private static final javax.xml.namespace.QName ORGANIZATIONKPP$6 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "organizationKpp");
        private static final javax.xml.namespace.QName SIGN$8 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "sign");
        private static final javax.xml.namespace.QName USERID$10 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "userId");
        
        
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
         * Gets the "partnerKey" element
         */
        public java.lang.String getPartnerKey()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PARTNERKEY$2, 0);
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
                target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().find_element_user(PARTNERKEY$2, 0);
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
                return get_store().count_elements(PARTNERKEY$2) != 0;
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
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PARTNERKEY$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PARTNERKEY$2);
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
                target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().find_element_user(PARTNERKEY$2, 0);
                if (target == null)
                {
                    target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().add_element_user(PARTNERKEY$2);
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
                get_store().remove_element(PARTNERKEY$2, 0);
            }
        }
        
        /**
         * Gets the "organizationInn" element
         */
        public java.lang.String getOrganizationInn()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ORGANIZATIONINN$4, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "organizationInn" element
         */
        public org.apache.xmlbeans.XmlString xgetOrganizationInn()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ORGANIZATIONINN$4, 0);
                return target;
            }
        }
        
        /**
         * Tests for nil "organizationInn" element
         */
        public boolean isNilOrganizationInn()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ORGANIZATIONINN$4, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "organizationInn" element
         */
        public boolean isSetOrganizationInn()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(ORGANIZATIONINN$4) != 0;
            }
        }
        
        /**
         * Sets the "organizationInn" element
         */
        public void setOrganizationInn(java.lang.String organizationInn)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ORGANIZATIONINN$4, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ORGANIZATIONINN$4);
                }
                target.setStringValue(organizationInn);
            }
        }
        
        /**
         * Sets (as xml) the "organizationInn" element
         */
        public void xsetOrganizationInn(org.apache.xmlbeans.XmlString organizationInn)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ORGANIZATIONINN$4, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(ORGANIZATIONINN$4);
                }
                target.set(organizationInn);
            }
        }
        
        /**
         * Nils the "organizationInn" element
         */
        public void setNilOrganizationInn()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ORGANIZATIONINN$4, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(ORGANIZATIONINN$4);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "organizationInn" element
         */
        public void unsetOrganizationInn()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(ORGANIZATIONINN$4, 0);
            }
        }
        
        /**
         * Gets the "organizationKpp" element
         */
        public java.lang.String getOrganizationKpp()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ORGANIZATIONKPP$6, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "organizationKpp" element
         */
        public org.apache.xmlbeans.XmlString xgetOrganizationKpp()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ORGANIZATIONKPP$6, 0);
                return target;
            }
        }
        
        /**
         * Tests for nil "organizationKpp" element
         */
        public boolean isNilOrganizationKpp()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ORGANIZATIONKPP$6, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "organizationKpp" element
         */
        public boolean isSetOrganizationKpp()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(ORGANIZATIONKPP$6) != 0;
            }
        }
        
        /**
         * Sets the "organizationKpp" element
         */
        public void setOrganizationKpp(java.lang.String organizationKpp)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ORGANIZATIONKPP$6, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ORGANIZATIONKPP$6);
                }
                target.setStringValue(organizationKpp);
            }
        }
        
        /**
         * Sets (as xml) the "organizationKpp" element
         */
        public void xsetOrganizationKpp(org.apache.xmlbeans.XmlString organizationKpp)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ORGANIZATIONKPP$6, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(ORGANIZATIONKPP$6);
                }
                target.set(organizationKpp);
            }
        }
        
        /**
         * Nils the "organizationKpp" element
         */
        public void setNilOrganizationKpp()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ORGANIZATIONKPP$6, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(ORGANIZATIONKPP$6);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "organizationKpp" element
         */
        public void unsetOrganizationKpp()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(ORGANIZATIONKPP$6, 0);
            }
        }
        
        /**
         * Gets the "sign" element
         */
        public byte[] getSign()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SIGN$8, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getByteArrayValue();
            }
        }
        
        /**
         * Gets (as xml) the "sign" element
         */
        public org.apache.xmlbeans.XmlBase64Binary xgetSign()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBase64Binary target = null;
                target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(SIGN$8, 0);
                return target;
            }
        }
        
        /**
         * Tests for nil "sign" element
         */
        public boolean isNilSign()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBase64Binary target = null;
                target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(SIGN$8, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "sign" element
         */
        public boolean isSetSign()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(SIGN$8) != 0;
            }
        }
        
        /**
         * Sets the "sign" element
         */
        public void setSign(byte[] sign)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SIGN$8, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(SIGN$8);
                }
                target.setByteArrayValue(sign);
            }
        }
        
        /**
         * Sets (as xml) the "sign" element
         */
        public void xsetSign(org.apache.xmlbeans.XmlBase64Binary sign)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBase64Binary target = null;
                target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(SIGN$8, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlBase64Binary)get_store().add_element_user(SIGN$8);
                }
                target.set(sign);
            }
        }
        
        /**
         * Nils the "sign" element
         */
        public void setNilSign()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBase64Binary target = null;
                target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(SIGN$8, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlBase64Binary)get_store().add_element_user(SIGN$8);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "sign" element
         */
        public void unsetSign()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(SIGN$8, 0);
            }
        }
        
        /**
         * Gets the "userId" element
         */
        public java.lang.String getUserId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(USERID$10, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "userId" element
         */
        public com.microsoft.schemas.x2003.x10.serialization.Guid xgetUserId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.microsoft.schemas.x2003.x10.serialization.Guid target = null;
                target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().find_element_user(USERID$10, 0);
                return target;
            }
        }
        
        /**
         * True if has "userId" element
         */
        public boolean isSetUserId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(USERID$10) != 0;
            }
        }
        
        /**
         * Sets the "userId" element
         */
        public void setUserId(java.lang.String userId)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(USERID$10, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(USERID$10);
                }
                target.setStringValue(userId);
            }
        }
        
        /**
         * Sets (as xml) the "userId" element
         */
        public void xsetUserId(com.microsoft.schemas.x2003.x10.serialization.Guid userId)
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.microsoft.schemas.x2003.x10.serialization.Guid target = null;
                target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().find_element_user(USERID$10, 0);
                if (target == null)
                {
                    target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().add_element_user(USERID$10);
                }
                target.set(userId);
            }
        }
        
        /**
         * Unsets the "userId" element
         */
        public void unsetUserId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(USERID$10, 0);
            }
        }
    }
}
