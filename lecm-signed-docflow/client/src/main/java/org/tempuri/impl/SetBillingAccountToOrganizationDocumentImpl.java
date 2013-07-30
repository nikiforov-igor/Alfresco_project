/*
 * An XML document type.
 * Localname: SetBillingAccountToOrganization
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.SetBillingAccountToOrganizationDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one SetBillingAccountToOrganization(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class SetBillingAccountToOrganizationDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.SetBillingAccountToOrganizationDocument
{
    private static final long serialVersionUID = 1L;
    
    public SetBillingAccountToOrganizationDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName SETBILLINGACCOUNTTOORGANIZATION$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "SetBillingAccountToOrganization");
    
    
    /**
     * Gets the "SetBillingAccountToOrganization" element
     */
    public org.tempuri.SetBillingAccountToOrganizationDocument.SetBillingAccountToOrganization getSetBillingAccountToOrganization()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.SetBillingAccountToOrganizationDocument.SetBillingAccountToOrganization target = null;
            target = (org.tempuri.SetBillingAccountToOrganizationDocument.SetBillingAccountToOrganization)get_store().find_element_user(SETBILLINGACCOUNTTOORGANIZATION$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "SetBillingAccountToOrganization" element
     */
    public void setSetBillingAccountToOrganization(org.tempuri.SetBillingAccountToOrganizationDocument.SetBillingAccountToOrganization setBillingAccountToOrganization)
    {
        generatedSetterHelperImpl(setBillingAccountToOrganization, SETBILLINGACCOUNTTOORGANIZATION$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "SetBillingAccountToOrganization" element
     */
    public org.tempuri.SetBillingAccountToOrganizationDocument.SetBillingAccountToOrganization addNewSetBillingAccountToOrganization()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.SetBillingAccountToOrganizationDocument.SetBillingAccountToOrganization target = null;
            target = (org.tempuri.SetBillingAccountToOrganizationDocument.SetBillingAccountToOrganization)get_store().add_element_user(SETBILLINGACCOUNTTOORGANIZATION$0);
            return target;
        }
    }
    /**
     * An XML SetBillingAccountToOrganization(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class SetBillingAccountToOrganizationImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.SetBillingAccountToOrganizationDocument.SetBillingAccountToOrganization
    {
        private static final long serialVersionUID = 1L;
        
        public SetBillingAccountToOrganizationImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName OPERATORCODE$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "operatorCode");
        private static final javax.xml.namespace.QName SIGNATURE$2 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "signature");
        private static final javax.xml.namespace.QName BILLINGLOGIN$4 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "billingLogin");
        private static final javax.xml.namespace.QName BILLINGPASSWORD$6 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "billingPassword");
        private static final javax.xml.namespace.QName EXISTEDACCOUNT$8 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "existedAccount");
        
        
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
        
        /**
         * Gets the "billingLogin" element
         */
        public java.lang.String getBillingLogin()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(BILLINGLOGIN$4, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "billingLogin" element
         */
        public org.apache.xmlbeans.XmlString xgetBillingLogin()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(BILLINGLOGIN$4, 0);
                return target;
            }
        }
        
        /**
         * Tests for nil "billingLogin" element
         */
        public boolean isNilBillingLogin()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(BILLINGLOGIN$4, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "billingLogin" element
         */
        public boolean isSetBillingLogin()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(BILLINGLOGIN$4) != 0;
            }
        }
        
        /**
         * Sets the "billingLogin" element
         */
        public void setBillingLogin(java.lang.String billingLogin)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(BILLINGLOGIN$4, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(BILLINGLOGIN$4);
                }
                target.setStringValue(billingLogin);
            }
        }
        
        /**
         * Sets (as xml) the "billingLogin" element
         */
        public void xsetBillingLogin(org.apache.xmlbeans.XmlString billingLogin)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(BILLINGLOGIN$4, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(BILLINGLOGIN$4);
                }
                target.set(billingLogin);
            }
        }
        
        /**
         * Nils the "billingLogin" element
         */
        public void setNilBillingLogin()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(BILLINGLOGIN$4, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(BILLINGLOGIN$4);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "billingLogin" element
         */
        public void unsetBillingLogin()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(BILLINGLOGIN$4, 0);
            }
        }
        
        /**
         * Gets the "billingPassword" element
         */
        public java.lang.String getBillingPassword()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(BILLINGPASSWORD$6, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "billingPassword" element
         */
        public org.apache.xmlbeans.XmlString xgetBillingPassword()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(BILLINGPASSWORD$6, 0);
                return target;
            }
        }
        
        /**
         * Tests for nil "billingPassword" element
         */
        public boolean isNilBillingPassword()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(BILLINGPASSWORD$6, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "billingPassword" element
         */
        public boolean isSetBillingPassword()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(BILLINGPASSWORD$6) != 0;
            }
        }
        
        /**
         * Sets the "billingPassword" element
         */
        public void setBillingPassword(java.lang.String billingPassword)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(BILLINGPASSWORD$6, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(BILLINGPASSWORD$6);
                }
                target.setStringValue(billingPassword);
            }
        }
        
        /**
         * Sets (as xml) the "billingPassword" element
         */
        public void xsetBillingPassword(org.apache.xmlbeans.XmlString billingPassword)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(BILLINGPASSWORD$6, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(BILLINGPASSWORD$6);
                }
                target.set(billingPassword);
            }
        }
        
        /**
         * Nils the "billingPassword" element
         */
        public void setNilBillingPassword()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(BILLINGPASSWORD$6, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(BILLINGPASSWORD$6);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "billingPassword" element
         */
        public void unsetBillingPassword()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(BILLINGPASSWORD$6, 0);
            }
        }
        
        /**
         * Gets the "existedAccount" element
         */
        public boolean getExistedAccount()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(EXISTEDACCOUNT$8, 0);
                if (target == null)
                {
                    return false;
                }
                return target.getBooleanValue();
            }
        }
        
        /**
         * Gets (as xml) the "existedAccount" element
         */
        public org.apache.xmlbeans.XmlBoolean xgetExistedAccount()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBoolean target = null;
                target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(EXISTEDACCOUNT$8, 0);
                return target;
            }
        }
        
        /**
         * True if has "existedAccount" element
         */
        public boolean isSetExistedAccount()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(EXISTEDACCOUNT$8) != 0;
            }
        }
        
        /**
         * Sets the "existedAccount" element
         */
        public void setExistedAccount(boolean existedAccount)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(EXISTEDACCOUNT$8, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(EXISTEDACCOUNT$8);
                }
                target.setBooleanValue(existedAccount);
            }
        }
        
        /**
         * Sets (as xml) the "existedAccount" element
         */
        public void xsetExistedAccount(org.apache.xmlbeans.XmlBoolean existedAccount)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBoolean target = null;
                target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(EXISTEDACCOUNT$8, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlBoolean)get_store().add_element_user(EXISTEDACCOUNT$8);
                }
                target.set(existedAccount);
            }
        }
        
        /**
         * Unsets the "existedAccount" element
         */
        public void unsetExistedAccount()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(EXISTEDACCOUNT$8, 0);
            }
        }
    }
}
