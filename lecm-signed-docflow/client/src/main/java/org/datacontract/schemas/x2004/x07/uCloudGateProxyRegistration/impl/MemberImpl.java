/*
 * XML Type:  Member
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.Member
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.impl;
/**
 * An XML Member(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration).
 *
 * This is a complex type.
 */
public class MemberImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.Member
{
    private static final long serialVersionUID = 1L;
    
    public MemberImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName CERTIFICATE$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration", "Certificate");
    private static final javax.xml.namespace.QName EMAIL$2 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration", "Email");
    private static final javax.xml.namespace.QName FIRSTNAME$4 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration", "FirstName");
    private static final javax.xml.namespace.QName LASTNAME$6 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration", "LastName");
    private static final javax.xml.namespace.QName MEMBERID$8 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration", "MemberId");
    private static final javax.xml.namespace.QName ORGUNIT$10 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration", "OrgUnit");
    private static final javax.xml.namespace.QName PATRONYMIC$12 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration", "Patronymic");
    private static final javax.xml.namespace.QName POST$14 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration", "Post");
    
    
    /**
     * Gets the "Certificate" element
     */
    public java.lang.String getCertificate()
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
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Certificate" element
     */
    public org.apache.xmlbeans.XmlString xgetCertificate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(CERTIFICATE$0, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "Certificate" element
     */
    public boolean isNilCertificate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(CERTIFICATE$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Certificate" element
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
     * Sets the "Certificate" element
     */
    public void setCertificate(java.lang.String certificate)
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
            target.setStringValue(certificate);
        }
    }
    
    /**
     * Sets (as xml) the "Certificate" element
     */
    public void xsetCertificate(org.apache.xmlbeans.XmlString certificate)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(CERTIFICATE$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(CERTIFICATE$0);
            }
            target.set(certificate);
        }
    }
    
    /**
     * Nils the "Certificate" element
     */
    public void setNilCertificate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(CERTIFICATE$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(CERTIFICATE$0);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Certificate" element
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
     * Gets the "Email" element
     */
    public java.lang.String getEmail()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(EMAIL$2, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Email" element
     */
    public org.apache.xmlbeans.XmlString xgetEmail()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(EMAIL$2, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "Email" element
     */
    public boolean isNilEmail()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(EMAIL$2, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Email" element
     */
    public boolean isSetEmail()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(EMAIL$2) != 0;
        }
    }
    
    /**
     * Sets the "Email" element
     */
    public void setEmail(java.lang.String email)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(EMAIL$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(EMAIL$2);
            }
            target.setStringValue(email);
        }
    }
    
    /**
     * Sets (as xml) the "Email" element
     */
    public void xsetEmail(org.apache.xmlbeans.XmlString email)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(EMAIL$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(EMAIL$2);
            }
            target.set(email);
        }
    }
    
    /**
     * Nils the "Email" element
     */
    public void setNilEmail()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(EMAIL$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(EMAIL$2);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Email" element
     */
    public void unsetEmail()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(EMAIL$2, 0);
        }
    }
    
    /**
     * Gets the "FirstName" element
     */
    public java.lang.String getFirstName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(FIRSTNAME$4, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FIRSTNAME$4, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FIRSTNAME$4, 0);
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
            return get_store().count_elements(FIRSTNAME$4) != 0;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(FIRSTNAME$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(FIRSTNAME$4);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FIRSTNAME$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(FIRSTNAME$4);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FIRSTNAME$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(FIRSTNAME$4);
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
            get_store().remove_element(FIRSTNAME$4, 0);
        }
    }
    
    /**
     * Gets the "LastName" element
     */
    public java.lang.String getLastName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(LASTNAME$6, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "LastName" element
     */
    public org.apache.xmlbeans.XmlString xgetLastName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(LASTNAME$6, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "LastName" element
     */
    public boolean isNilLastName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(LASTNAME$6, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "LastName" element
     */
    public boolean isSetLastName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(LASTNAME$6) != 0;
        }
    }
    
    /**
     * Sets the "LastName" element
     */
    public void setLastName(java.lang.String lastName)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(LASTNAME$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(LASTNAME$6);
            }
            target.setStringValue(lastName);
        }
    }
    
    /**
     * Sets (as xml) the "LastName" element
     */
    public void xsetLastName(org.apache.xmlbeans.XmlString lastName)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(LASTNAME$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(LASTNAME$6);
            }
            target.set(lastName);
        }
    }
    
    /**
     * Nils the "LastName" element
     */
    public void setNilLastName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(LASTNAME$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(LASTNAME$6);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "LastName" element
     */
    public void unsetLastName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(LASTNAME$6, 0);
        }
    }
    
    /**
     * Gets the "MemberId" element
     */
    public java.lang.String getMemberId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(MEMBERID$8, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "MemberId" element
     */
    public com.microsoft.schemas.x2003.x10.serialization.Guid xgetMemberId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.microsoft.schemas.x2003.x10.serialization.Guid target = null;
            target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().find_element_user(MEMBERID$8, 0);
            return target;
        }
    }
    
    /**
     * True if has "MemberId" element
     */
    public boolean isSetMemberId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(MEMBERID$8) != 0;
        }
    }
    
    /**
     * Sets the "MemberId" element
     */
    public void setMemberId(java.lang.String memberId)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(MEMBERID$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(MEMBERID$8);
            }
            target.setStringValue(memberId);
        }
    }
    
    /**
     * Sets (as xml) the "MemberId" element
     */
    public void xsetMemberId(com.microsoft.schemas.x2003.x10.serialization.Guid memberId)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.microsoft.schemas.x2003.x10.serialization.Guid target = null;
            target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().find_element_user(MEMBERID$8, 0);
            if (target == null)
            {
                target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().add_element_user(MEMBERID$8);
            }
            target.set(memberId);
        }
    }
    
    /**
     * Unsets the "MemberId" element
     */
    public void unsetMemberId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(MEMBERID$8, 0);
        }
    }
    
    /**
     * Gets the "OrgUnit" element
     */
    public java.lang.String getOrgUnit()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ORGUNIT$10, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "OrgUnit" element
     */
    public org.apache.xmlbeans.XmlString xgetOrgUnit()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ORGUNIT$10, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "OrgUnit" element
     */
    public boolean isNilOrgUnit()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ORGUNIT$10, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "OrgUnit" element
     */
    public boolean isSetOrgUnit()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(ORGUNIT$10) != 0;
        }
    }
    
    /**
     * Sets the "OrgUnit" element
     */
    public void setOrgUnit(java.lang.String orgUnit)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ORGUNIT$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ORGUNIT$10);
            }
            target.setStringValue(orgUnit);
        }
    }
    
    /**
     * Sets (as xml) the "OrgUnit" element
     */
    public void xsetOrgUnit(org.apache.xmlbeans.XmlString orgUnit)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ORGUNIT$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(ORGUNIT$10);
            }
            target.set(orgUnit);
        }
    }
    
    /**
     * Nils the "OrgUnit" element
     */
    public void setNilOrgUnit()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ORGUNIT$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(ORGUNIT$10);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "OrgUnit" element
     */
    public void unsetOrgUnit()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(ORGUNIT$10, 0);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PATRONYMIC$12, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PATRONYMIC$12, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PATRONYMIC$12, 0);
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
            return get_store().count_elements(PATRONYMIC$12) != 0;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PATRONYMIC$12, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PATRONYMIC$12);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PATRONYMIC$12, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PATRONYMIC$12);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PATRONYMIC$12, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PATRONYMIC$12);
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
            get_store().remove_element(PATRONYMIC$12, 0);
        }
    }
    
    /**
     * Gets the "Post" element
     */
    public java.lang.String getPost()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(POST$14, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Post" element
     */
    public org.apache.xmlbeans.XmlString xgetPost()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(POST$14, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "Post" element
     */
    public boolean isNilPost()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(POST$14, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Post" element
     */
    public boolean isSetPost()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(POST$14) != 0;
        }
    }
    
    /**
     * Sets the "Post" element
     */
    public void setPost(java.lang.String post)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(POST$14, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(POST$14);
            }
            target.setStringValue(post);
        }
    }
    
    /**
     * Sets (as xml) the "Post" element
     */
    public void xsetPost(org.apache.xmlbeans.XmlString post)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(POST$14, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(POST$14);
            }
            target.set(post);
        }
    }
    
    /**
     * Nils the "Post" element
     */
    public void setNilPost()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(POST$14, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(POST$14);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Post" element
     */
    public void unsetPost()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(POST$14, 0);
        }
    }
}
