/*
 * XML Type:  SignerDetails
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.SignerDetails
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.impl;
/**
 * An XML SignerDetails(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses).
 *
 * This is a complex type.
 */
public class SignerDetailsImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.SignerDetails
{
    private static final long serialVersionUID = 1L;
    
    public SignerDetailsImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName FIRSTNAME$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "FirstName");
    private static final javax.xml.namespace.QName INN$2 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "Inn");
    private static final javax.xml.namespace.QName JOBTITLE$4 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "JobTitle");
    private static final javax.xml.namespace.QName PATRONYMIC$6 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "Patronymic");
    private static final javax.xml.namespace.QName SOLEPROPRIETORREGISTRATIONCERTIFICATE$8 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "SoleProprietorRegistrationCertificate");
    private static final javax.xml.namespace.QName SURNAME$10 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "Surname");
    
    
    /**
     * Gets the "FirstName" element
     */
    public java.lang.String getFirstName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(FIRSTNAME$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "FirstName" element
     */
    public org.apache.xmlbeans.XmlString xgetFirstName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FIRSTNAME$0, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "FirstName" element
     */
    public boolean isNilFirstName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FIRSTNAME$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "FirstName" element
     */
    public boolean isSetFirstName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(FIRSTNAME$0) != 0;
        }
    }
    
    /**
     * Sets the "FirstName" element
     */
    public void setFirstName(java.lang.String firstName)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(FIRSTNAME$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(FIRSTNAME$0);
            }
            target.setStringValue(firstName);
        }
    }
    
    /**
     * Sets (as xml) the "FirstName" element
     */
    public void xsetFirstName(org.apache.xmlbeans.XmlString firstName)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FIRSTNAME$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(FIRSTNAME$0);
            }
            target.set(firstName);
        }
    }
    
    /**
     * Nils the "FirstName" element
     */
    public void setNilFirstName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FIRSTNAME$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(FIRSTNAME$0);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "FirstName" element
     */
    public void unsetFirstName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(FIRSTNAME$0, 0);
        }
    }
    
    /**
     * Gets the "Inn" element
     */
    public java.lang.String getInn()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(INN$2, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Inn" element
     */
    public org.apache.xmlbeans.XmlString xgetInn()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INN$2, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "Inn" element
     */
    public boolean isNilInn()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INN$2, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Inn" element
     */
    public boolean isSetInn()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(INN$2) != 0;
        }
    }
    
    /**
     * Sets the "Inn" element
     */
    public void setInn(java.lang.String inn)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(INN$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(INN$2);
            }
            target.setStringValue(inn);
        }
    }
    
    /**
     * Sets (as xml) the "Inn" element
     */
    public void xsetInn(org.apache.xmlbeans.XmlString inn)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INN$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(INN$2);
            }
            target.set(inn);
        }
    }
    
    /**
     * Nils the "Inn" element
     */
    public void setNilInn()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INN$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(INN$2);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Inn" element
     */
    public void unsetInn()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(INN$2, 0);
        }
    }
    
    /**
     * Gets the "JobTitle" element
     */
    public java.lang.String getJobTitle()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(JOBTITLE$4, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "JobTitle" element
     */
    public org.apache.xmlbeans.XmlString xgetJobTitle()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(JOBTITLE$4, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "JobTitle" element
     */
    public boolean isNilJobTitle()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(JOBTITLE$4, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "JobTitle" element
     */
    public boolean isSetJobTitle()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(JOBTITLE$4) != 0;
        }
    }
    
    /**
     * Sets the "JobTitle" element
     */
    public void setJobTitle(java.lang.String jobTitle)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(JOBTITLE$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(JOBTITLE$4);
            }
            target.setStringValue(jobTitle);
        }
    }
    
    /**
     * Sets (as xml) the "JobTitle" element
     */
    public void xsetJobTitle(org.apache.xmlbeans.XmlString jobTitle)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(JOBTITLE$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(JOBTITLE$4);
            }
            target.set(jobTitle);
        }
    }
    
    /**
     * Nils the "JobTitle" element
     */
    public void setNilJobTitle()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(JOBTITLE$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(JOBTITLE$4);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "JobTitle" element
     */
    public void unsetJobTitle()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(JOBTITLE$4, 0);
        }
    }
    
    /**
     * Gets the "Patronymic" element
     */
    public java.lang.String getPatronymic()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PATRONYMIC$6, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Patronymic" element
     */
    public org.apache.xmlbeans.XmlString xgetPatronymic()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PATRONYMIC$6, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "Patronymic" element
     */
    public boolean isNilPatronymic()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PATRONYMIC$6, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Patronymic" element
     */
    public boolean isSetPatronymic()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(PATRONYMIC$6) != 0;
        }
    }
    
    /**
     * Sets the "Patronymic" element
     */
    public void setPatronymic(java.lang.String patronymic)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PATRONYMIC$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PATRONYMIC$6);
            }
            target.setStringValue(patronymic);
        }
    }
    
    /**
     * Sets (as xml) the "Patronymic" element
     */
    public void xsetPatronymic(org.apache.xmlbeans.XmlString patronymic)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PATRONYMIC$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PATRONYMIC$6);
            }
            target.set(patronymic);
        }
    }
    
    /**
     * Nils the "Patronymic" element
     */
    public void setNilPatronymic()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PATRONYMIC$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PATRONYMIC$6);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Patronymic" element
     */
    public void unsetPatronymic()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(PATRONYMIC$6, 0);
        }
    }
    
    /**
     * Gets the "SoleProprietorRegistrationCertificate" element
     */
    public java.lang.String getSoleProprietorRegistrationCertificate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SOLEPROPRIETORREGISTRATIONCERTIFICATE$8, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "SoleProprietorRegistrationCertificate" element
     */
    public org.apache.xmlbeans.XmlString xgetSoleProprietorRegistrationCertificate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SOLEPROPRIETORREGISTRATIONCERTIFICATE$8, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "SoleProprietorRegistrationCertificate" element
     */
    public boolean isNilSoleProprietorRegistrationCertificate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SOLEPROPRIETORREGISTRATIONCERTIFICATE$8, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "SoleProprietorRegistrationCertificate" element
     */
    public boolean isSetSoleProprietorRegistrationCertificate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(SOLEPROPRIETORREGISTRATIONCERTIFICATE$8) != 0;
        }
    }
    
    /**
     * Sets the "SoleProprietorRegistrationCertificate" element
     */
    public void setSoleProprietorRegistrationCertificate(java.lang.String soleProprietorRegistrationCertificate)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SOLEPROPRIETORREGISTRATIONCERTIFICATE$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(SOLEPROPRIETORREGISTRATIONCERTIFICATE$8);
            }
            target.setStringValue(soleProprietorRegistrationCertificate);
        }
    }
    
    /**
     * Sets (as xml) the "SoleProprietorRegistrationCertificate" element
     */
    public void xsetSoleProprietorRegistrationCertificate(org.apache.xmlbeans.XmlString soleProprietorRegistrationCertificate)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SOLEPROPRIETORREGISTRATIONCERTIFICATE$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(SOLEPROPRIETORREGISTRATIONCERTIFICATE$8);
            }
            target.set(soleProprietorRegistrationCertificate);
        }
    }
    
    /**
     * Nils the "SoleProprietorRegistrationCertificate" element
     */
    public void setNilSoleProprietorRegistrationCertificate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SOLEPROPRIETORREGISTRATIONCERTIFICATE$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(SOLEPROPRIETORREGISTRATIONCERTIFICATE$8);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "SoleProprietorRegistrationCertificate" element
     */
    public void unsetSoleProprietorRegistrationCertificate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(SOLEPROPRIETORREGISTRATIONCERTIFICATE$8, 0);
        }
    }
    
    /**
     * Gets the "Surname" element
     */
    public java.lang.String getSurname()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SURNAME$10, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Surname" element
     */
    public org.apache.xmlbeans.XmlString xgetSurname()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SURNAME$10, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "Surname" element
     */
    public boolean isNilSurname()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SURNAME$10, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Surname" element
     */
    public boolean isSetSurname()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(SURNAME$10) != 0;
        }
    }
    
    /**
     * Sets the "Surname" element
     */
    public void setSurname(java.lang.String surname)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SURNAME$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(SURNAME$10);
            }
            target.setStringValue(surname);
        }
    }
    
    /**
     * Sets (as xml) the "Surname" element
     */
    public void xsetSurname(org.apache.xmlbeans.XmlString surname)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SURNAME$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(SURNAME$10);
            }
            target.set(surname);
        }
    }
    
    /**
     * Nils the "Surname" element
     */
    public void setNilSurname()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SURNAME$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(SURNAME$10);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Surname" element
     */
    public void unsetSurname()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(SURNAME$10, 0);
        }
    }
}
