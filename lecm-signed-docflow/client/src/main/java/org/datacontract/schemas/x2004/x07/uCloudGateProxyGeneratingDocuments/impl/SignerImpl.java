/*
 * XML Type:  Signer
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.Signer
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.impl;
/**
 * An XML Signer(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments).
 *
 * This is a complex type.
 */
public class SignerImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.Signer
{
    private static final long serialVersionUID = 1L;
    
    public SignerImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName FIRSTNAME$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments", "FirstName");
    private static final javax.xml.namespace.QName INDIVIDUALREGISTRATIONINFO$2 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments", "IndividualRegistrationInfo");
    private static final javax.xml.namespace.QName JOBTITLE$4 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments", "JobTitle");
    private static final javax.xml.namespace.QName ORGANIZATIONINN$6 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments", "OrganizationInn");
    private static final javax.xml.namespace.QName PATRONYMIC$8 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments", "Patronymic");
    private static final javax.xml.namespace.QName SURNAME$10 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments", "Surname");
    
    
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
     * Gets the "IndividualRegistrationInfo" element
     */
    public java.lang.String getIndividualRegistrationInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(INDIVIDUALREGISTRATIONINFO$2, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "IndividualRegistrationInfo" element
     */
    public org.apache.xmlbeans.XmlString xgetIndividualRegistrationInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INDIVIDUALREGISTRATIONINFO$2, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "IndividualRegistrationInfo" element
     */
    public boolean isNilIndividualRegistrationInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INDIVIDUALREGISTRATIONINFO$2, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "IndividualRegistrationInfo" element
     */
    public boolean isSetIndividualRegistrationInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(INDIVIDUALREGISTRATIONINFO$2) != 0;
        }
    }
    
    /**
     * Sets the "IndividualRegistrationInfo" element
     */
    public void setIndividualRegistrationInfo(java.lang.String individualRegistrationInfo)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(INDIVIDUALREGISTRATIONINFO$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(INDIVIDUALREGISTRATIONINFO$2);
            }
            target.setStringValue(individualRegistrationInfo);
        }
    }
    
    /**
     * Sets (as xml) the "IndividualRegistrationInfo" element
     */
    public void xsetIndividualRegistrationInfo(org.apache.xmlbeans.XmlString individualRegistrationInfo)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INDIVIDUALREGISTRATIONINFO$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(INDIVIDUALREGISTRATIONINFO$2);
            }
            target.set(individualRegistrationInfo);
        }
    }
    
    /**
     * Nils the "IndividualRegistrationInfo" element
     */
    public void setNilIndividualRegistrationInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INDIVIDUALREGISTRATIONINFO$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(INDIVIDUALREGISTRATIONINFO$2);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "IndividualRegistrationInfo" element
     */
    public void unsetIndividualRegistrationInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(INDIVIDUALREGISTRATIONINFO$2, 0);
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
     * Gets the "OrganizationInn" element
     */
    public java.lang.String getOrganizationInn()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ORGANIZATIONINN$6, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "OrganizationInn" element
     */
    public org.apache.xmlbeans.XmlString xgetOrganizationInn()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ORGANIZATIONINN$6, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "OrganizationInn" element
     */
    public boolean isNilOrganizationInn()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ORGANIZATIONINN$6, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "OrganizationInn" element
     */
    public boolean isSetOrganizationInn()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(ORGANIZATIONINN$6) != 0;
        }
    }
    
    /**
     * Sets the "OrganizationInn" element
     */
    public void setOrganizationInn(java.lang.String organizationInn)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ORGANIZATIONINN$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ORGANIZATIONINN$6);
            }
            target.setStringValue(organizationInn);
        }
    }
    
    /**
     * Sets (as xml) the "OrganizationInn" element
     */
    public void xsetOrganizationInn(org.apache.xmlbeans.XmlString organizationInn)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ORGANIZATIONINN$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(ORGANIZATIONINN$6);
            }
            target.set(organizationInn);
        }
    }
    
    /**
     * Nils the "OrganizationInn" element
     */
    public void setNilOrganizationInn()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ORGANIZATIONINN$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(ORGANIZATIONINN$6);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "OrganizationInn" element
     */
    public void unsetOrganizationInn()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(ORGANIZATIONINN$6, 0);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PATRONYMIC$8, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PATRONYMIC$8, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PATRONYMIC$8, 0);
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
            return get_store().count_elements(PATRONYMIC$8) != 0;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PATRONYMIC$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PATRONYMIC$8);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PATRONYMIC$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PATRONYMIC$8);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PATRONYMIC$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PATRONYMIC$8);
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
            get_store().remove_element(PATRONYMIC$8, 0);
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
