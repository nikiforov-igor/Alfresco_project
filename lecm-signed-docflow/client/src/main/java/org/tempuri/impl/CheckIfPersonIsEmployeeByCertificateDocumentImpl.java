/*
 * An XML document type.
 * Localname: CheckIfPersonIsEmployeeByCertificate
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one CheckIfPersonIsEmployeeByCertificate(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class CheckIfPersonIsEmployeeByCertificateDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument
{
    private static final long serialVersionUID = 1L;
    
    public CheckIfPersonIsEmployeeByCertificateDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName CHECKIFPERSONISEMPLOYEEBYCERTIFICATE$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "CheckIfPersonIsEmployeeByCertificate");
    
    
    /**
     * Gets the "CheckIfPersonIsEmployeeByCertificate" element
     */
    public org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument.CheckIfPersonIsEmployeeByCertificate getCheckIfPersonIsEmployeeByCertificate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument.CheckIfPersonIsEmployeeByCertificate target = null;
            target = (org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument.CheckIfPersonIsEmployeeByCertificate)get_store().find_element_user(CHECKIFPERSONISEMPLOYEEBYCERTIFICATE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "CheckIfPersonIsEmployeeByCertificate" element
     */
    public void setCheckIfPersonIsEmployeeByCertificate(org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument.CheckIfPersonIsEmployeeByCertificate checkIfPersonIsEmployeeByCertificate)
    {
        generatedSetterHelperImpl(checkIfPersonIsEmployeeByCertificate, CHECKIFPERSONISEMPLOYEEBYCERTIFICATE$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "CheckIfPersonIsEmployeeByCertificate" element
     */
    public org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument.CheckIfPersonIsEmployeeByCertificate addNewCheckIfPersonIsEmployeeByCertificate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument.CheckIfPersonIsEmployeeByCertificate target = null;
            target = (org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument.CheckIfPersonIsEmployeeByCertificate)get_store().add_element_user(CHECKIFPERSONISEMPLOYEEBYCERTIFICATE$0);
            return target;
        }
    }
    /**
     * An XML CheckIfPersonIsEmployeeByCertificate(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class CheckIfPersonIsEmployeeByCertificateImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument.CheckIfPersonIsEmployeeByCertificate
    {
        private static final long serialVersionUID = 1L;
        
        public CheckIfPersonIsEmployeeByCertificateImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName CERTIFICATE$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "certificate");
        private static final javax.xml.namespace.QName OPERATORCODE$2 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "operatorCode");
        private static final javax.xml.namespace.QName ORGANIZATIONINN$4 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "organizationInn");
        private static final javax.xml.namespace.QName ORGANIZATIONKPP$6 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "organizationKpp");
        
        
        /**
         * Gets the "certificate" element
         */
        public byte[] getCertificate()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CERTIFICATE$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getByteArrayValue();
            }
        }
        
        /**
         * Gets (as xml) the "certificate" element
         */
        public org.apache.xmlbeans.XmlBase64Binary xgetCertificate()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBase64Binary target = null;
                target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(CERTIFICATE$0, 0);
                return target;
            }
        }
        
        /**
         * Tests for nil "certificate" element
         */
        public boolean isNilCertificate()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBase64Binary target = null;
                target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(CERTIFICATE$0, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "certificate" element
         */
        public boolean isSetCertificate()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(CERTIFICATE$0) != 0;
            }
        }
        
        /**
         * Sets the "certificate" element
         */
        public void setCertificate(byte[] certificate)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CERTIFICATE$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(CERTIFICATE$0);
                }
                target.setByteArrayValue(certificate);
            }
        }
        
        /**
         * Sets (as xml) the "certificate" element
         */
        public void xsetCertificate(org.apache.xmlbeans.XmlBase64Binary certificate)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBase64Binary target = null;
                target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(CERTIFICATE$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlBase64Binary)get_store().add_element_user(CERTIFICATE$0);
                }
                target.set(certificate);
            }
        }
        
        /**
         * Nils the "certificate" element
         */
        public void setNilCertificate()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBase64Binary target = null;
                target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(CERTIFICATE$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlBase64Binary)get_store().add_element_user(CERTIFICATE$0);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "certificate" element
         */
        public void unsetCertificate()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(CERTIFICATE$0, 0);
            }
        }
        
        /**
         * Gets the "operatorCode" element
         */
        public java.lang.String getOperatorCode()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(OPERATORCODE$2, 0);
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
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORCODE$2, 0);
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
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORCODE$2, 0);
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
                return get_store().count_elements(OPERATORCODE$2) != 0;
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
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(OPERATORCODE$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(OPERATORCODE$2);
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
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORCODE$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(OPERATORCODE$2);
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
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORCODE$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(OPERATORCODE$2);
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
                get_store().remove_element(OPERATORCODE$2, 0);
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
    }
}
