/*
 * An XML document type.
 * Localname: StartRegisterUserBySms
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.StartRegisterUserBySmsDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one StartRegisterUserBySms(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class StartRegisterUserBySmsDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.StartRegisterUserBySmsDocument
{
    private static final long serialVersionUID = 1L;
    
    public StartRegisterUserBySmsDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName STARTREGISTERUSERBYSMS$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "StartRegisterUserBySms");
    
    
    /**
     * Gets the "StartRegisterUserBySms" element
     */
    public org.tempuri.StartRegisterUserBySmsDocument.StartRegisterUserBySms getStartRegisterUserBySms()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.StartRegisterUserBySmsDocument.StartRegisterUserBySms target = null;
            target = (org.tempuri.StartRegisterUserBySmsDocument.StartRegisterUserBySms)get_store().find_element_user(STARTREGISTERUSERBYSMS$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "StartRegisterUserBySms" element
     */
    public void setStartRegisterUserBySms(org.tempuri.StartRegisterUserBySmsDocument.StartRegisterUserBySms startRegisterUserBySms)
    {
        generatedSetterHelperImpl(startRegisterUserBySms, STARTREGISTERUSERBYSMS$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "StartRegisterUserBySms" element
     */
    public org.tempuri.StartRegisterUserBySmsDocument.StartRegisterUserBySms addNewStartRegisterUserBySms()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.StartRegisterUserBySmsDocument.StartRegisterUserBySms target = null;
            target = (org.tempuri.StartRegisterUserBySmsDocument.StartRegisterUserBySms)get_store().add_element_user(STARTREGISTERUSERBYSMS$0);
            return target;
        }
    }
    /**
     * An XML StartRegisterUserBySms(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class StartRegisterUserBySmsImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.StartRegisterUserBySmsDocument.StartRegisterUserBySms
    {
        private static final long serialVersionUID = 1L;
        
        public StartRegisterUserBySmsImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName OPERATORCODE$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "operatorCode");
        private static final javax.xml.namespace.QName MOBILENUMBER$2 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "mobileNumber");
        private static final javax.xml.namespace.QName INN$4 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "inn");
        private static final javax.xml.namespace.QName KPP$6 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "kpp");
        
        
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
         * Gets the "inn" element
         */
        public java.lang.String getInn()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(INN$4, 0);
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
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INN$4, 0);
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
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INN$4, 0);
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
                return get_store().count_elements(INN$4) != 0;
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
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(INN$4, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(INN$4);
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
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INN$4, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(INN$4);
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
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INN$4, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(INN$4);
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
                get_store().remove_element(INN$4, 0);
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
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(KPP$6, 0);
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
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(KPP$6, 0);
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
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(KPP$6, 0);
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
                return get_store().count_elements(KPP$6) != 0;
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
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(KPP$6, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(KPP$6);
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
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(KPP$6, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(KPP$6);
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
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(KPP$6, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(KPP$6);
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
                get_store().remove_element(KPP$6, 0);
            }
        }
    }
}
