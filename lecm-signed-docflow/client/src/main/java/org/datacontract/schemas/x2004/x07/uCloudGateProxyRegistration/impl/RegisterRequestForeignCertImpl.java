/*
 * XML Type:  RegisterRequestForeignCert
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.impl;
/**
 * An XML RegisterRequestForeignCert(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration).
 *
 * This is a complex type.
 */
public class RegisterRequestForeignCertImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert
{
    private static final long serialVersionUID = 1L;
    
    public RegisterRequestForeignCertImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName EMAIL$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration", "Email");
    private static final javax.xml.namespace.QName FULLNAME$2 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration", "FullName");
    private static final javax.xml.namespace.QName INN$4 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration", "Inn");
    private static final javax.xml.namespace.QName KPP$6 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration", "Kpp");
    private static final javax.xml.namespace.QName LOCATIONADDRESS$8 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration", "LocationAddress");
    private static final javax.xml.namespace.QName MEMBERS$10 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration", "Members");
    private static final javax.xml.namespace.QName MOBLIEPHONE$12 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration", "MobliePhone");
    private static final javax.xml.namespace.QName PHONE$14 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration", "Phone");
    private static final javax.xml.namespace.QName POSTALADDRESS$16 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration", "PostalAddress");
    private static final javax.xml.namespace.QName SHORTNAME$18 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration", "ShortName");
    
    
    /**
     * Gets the "Email" element
     */
    public java.lang.String getEmail()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(EMAIL$0, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(EMAIL$0, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(EMAIL$0, 0);
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
            return get_store().count_elements(EMAIL$0) != 0;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(EMAIL$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(EMAIL$0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(EMAIL$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(EMAIL$0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(EMAIL$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(EMAIL$0);
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
            get_store().remove_element(EMAIL$0, 0);
        }
    }
    
    /**
     * Gets the "FullName" element
     */
    public java.lang.String getFullName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(FULLNAME$2, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "FullName" element
     */
    public org.apache.xmlbeans.XmlString xgetFullName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FULLNAME$2, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "FullName" element
     */
    public boolean isNilFullName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FULLNAME$2, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "FullName" element
     */
    public boolean isSetFullName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(FULLNAME$2) != 0;
        }
    }
    
    /**
     * Sets the "FullName" element
     */
    public void setFullName(java.lang.String fullName)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(FULLNAME$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(FULLNAME$2);
            }
            target.setStringValue(fullName);
        }
    }
    
    /**
     * Sets (as xml) the "FullName" element
     */
    public void xsetFullName(org.apache.xmlbeans.XmlString fullName)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FULLNAME$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(FULLNAME$2);
            }
            target.set(fullName);
        }
    }
    
    /**
     * Nils the "FullName" element
     */
    public void setNilFullName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FULLNAME$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(FULLNAME$2);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "FullName" element
     */
    public void unsetFullName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(FULLNAME$2, 0);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(INN$4, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INN$4, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INN$4, 0);
            if (target == null) return false;
            return target.isNil();
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(INN$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(INN$4);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INN$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(INN$4);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INN$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(INN$4);
            }
            target.setNil();
        }
    }
    
    /**
     * Gets the "Kpp" element
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
     * Gets (as xml) the "Kpp" element
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
     * Tests for nil "Kpp" element
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
     * True if has "Kpp" element
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
     * Sets the "Kpp" element
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
     * Sets (as xml) the "Kpp" element
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
     * Nils the "Kpp" element
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
     * Unsets the "Kpp" element
     */
    public void unsetKpp()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(KPP$6, 0);
        }
    }
    
    /**
     * Gets the "LocationAddress" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.AddressForRegistration getLocationAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.AddressForRegistration target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.AddressForRegistration)get_store().find_element_user(LOCATIONADDRESS$8, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "LocationAddress" element
     */
    public boolean isNilLocationAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.AddressForRegistration target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.AddressForRegistration)get_store().find_element_user(LOCATIONADDRESS$8, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "LocationAddress" element
     */
    public boolean isSetLocationAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(LOCATIONADDRESS$8) != 0;
        }
    }
    
    /**
     * Sets the "LocationAddress" element
     */
    public void setLocationAddress(org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.AddressForRegistration locationAddress)
    {
        generatedSetterHelperImpl(locationAddress, LOCATIONADDRESS$8, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "LocationAddress" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.AddressForRegistration addNewLocationAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.AddressForRegistration target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.AddressForRegistration)get_store().add_element_user(LOCATIONADDRESS$8);
            return target;
        }
    }
    
    /**
     * Nils the "LocationAddress" element
     */
    public void setNilLocationAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.AddressForRegistration target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.AddressForRegistration)get_store().find_element_user(LOCATIONADDRESS$8, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.AddressForRegistration)get_store().add_element_user(LOCATIONADDRESS$8);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "LocationAddress" element
     */
    public void unsetLocationAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(LOCATIONADDRESS$8, 0);
        }
    }
    
    /**
     * Gets the "Members" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.ArrayOfMember getMembers()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.ArrayOfMember target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.ArrayOfMember)get_store().find_element_user(MEMBERS$10, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "Members" element
     */
    public boolean isNilMembers()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.ArrayOfMember target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.ArrayOfMember)get_store().find_element_user(MEMBERS$10, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Members" element
     */
    public boolean isSetMembers()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(MEMBERS$10) != 0;
        }
    }
    
    /**
     * Sets the "Members" element
     */
    public void setMembers(org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.ArrayOfMember members)
    {
        generatedSetterHelperImpl(members, MEMBERS$10, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "Members" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.ArrayOfMember addNewMembers()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.ArrayOfMember target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.ArrayOfMember)get_store().add_element_user(MEMBERS$10);
            return target;
        }
    }
    
    /**
     * Nils the "Members" element
     */
    public void setNilMembers()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.ArrayOfMember target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.ArrayOfMember)get_store().find_element_user(MEMBERS$10, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.ArrayOfMember)get_store().add_element_user(MEMBERS$10);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Members" element
     */
    public void unsetMembers()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(MEMBERS$10, 0);
        }
    }
    
    /**
     * Gets the "MobliePhone" element
     */
    public java.lang.String getMobliePhone()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(MOBLIEPHONE$12, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "MobliePhone" element
     */
    public org.apache.xmlbeans.XmlString xgetMobliePhone()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(MOBLIEPHONE$12, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "MobliePhone" element
     */
    public boolean isNilMobliePhone()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(MOBLIEPHONE$12, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "MobliePhone" element
     */
    public boolean isSetMobliePhone()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(MOBLIEPHONE$12) != 0;
        }
    }
    
    /**
     * Sets the "MobliePhone" element
     */
    public void setMobliePhone(java.lang.String mobliePhone)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(MOBLIEPHONE$12, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(MOBLIEPHONE$12);
            }
            target.setStringValue(mobliePhone);
        }
    }
    
    /**
     * Sets (as xml) the "MobliePhone" element
     */
    public void xsetMobliePhone(org.apache.xmlbeans.XmlString mobliePhone)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(MOBLIEPHONE$12, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(MOBLIEPHONE$12);
            }
            target.set(mobliePhone);
        }
    }
    
    /**
     * Nils the "MobliePhone" element
     */
    public void setNilMobliePhone()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(MOBLIEPHONE$12, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(MOBLIEPHONE$12);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "MobliePhone" element
     */
    public void unsetMobliePhone()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(MOBLIEPHONE$12, 0);
        }
    }
    
    /**
     * Gets the "Phone" element
     */
    public java.lang.String getPhone()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PHONE$14, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Phone" element
     */
    public org.apache.xmlbeans.XmlString xgetPhone()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PHONE$14, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "Phone" element
     */
    public boolean isNilPhone()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PHONE$14, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Phone" element
     */
    public boolean isSetPhone()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(PHONE$14) != 0;
        }
    }
    
    /**
     * Sets the "Phone" element
     */
    public void setPhone(java.lang.String phone)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PHONE$14, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PHONE$14);
            }
            target.setStringValue(phone);
        }
    }
    
    /**
     * Sets (as xml) the "Phone" element
     */
    public void xsetPhone(org.apache.xmlbeans.XmlString phone)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PHONE$14, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PHONE$14);
            }
            target.set(phone);
        }
    }
    
    /**
     * Nils the "Phone" element
     */
    public void setNilPhone()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PHONE$14, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PHONE$14);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Phone" element
     */
    public void unsetPhone()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(PHONE$14, 0);
        }
    }
    
    /**
     * Gets the "PostalAddress" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.AddressForRegistration getPostalAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.AddressForRegistration target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.AddressForRegistration)get_store().find_element_user(POSTALADDRESS$16, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "PostalAddress" element
     */
    public boolean isNilPostalAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.AddressForRegistration target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.AddressForRegistration)get_store().find_element_user(POSTALADDRESS$16, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "PostalAddress" element
     */
    public boolean isSetPostalAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(POSTALADDRESS$16) != 0;
        }
    }
    
    /**
     * Sets the "PostalAddress" element
     */
    public void setPostalAddress(org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.AddressForRegistration postalAddress)
    {
        generatedSetterHelperImpl(postalAddress, POSTALADDRESS$16, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "PostalAddress" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.AddressForRegistration addNewPostalAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.AddressForRegistration target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.AddressForRegistration)get_store().add_element_user(POSTALADDRESS$16);
            return target;
        }
    }
    
    /**
     * Nils the "PostalAddress" element
     */
    public void setNilPostalAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.AddressForRegistration target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.AddressForRegistration)get_store().find_element_user(POSTALADDRESS$16, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.AddressForRegistration)get_store().add_element_user(POSTALADDRESS$16);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "PostalAddress" element
     */
    public void unsetPostalAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(POSTALADDRESS$16, 0);
        }
    }
    
    /**
     * Gets the "ShortName" element
     */
    public java.lang.String getShortName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SHORTNAME$18, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "ShortName" element
     */
    public org.apache.xmlbeans.XmlString xgetShortName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SHORTNAME$18, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "ShortName" element
     */
    public boolean isNilShortName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SHORTNAME$18, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "ShortName" element
     */
    public void setShortName(java.lang.String shortName)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SHORTNAME$18, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(SHORTNAME$18);
            }
            target.setStringValue(shortName);
        }
    }
    
    /**
     * Sets (as xml) the "ShortName" element
     */
    public void xsetShortName(org.apache.xmlbeans.XmlString shortName)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SHORTNAME$18, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(SHORTNAME$18);
            }
            target.set(shortName);
        }
    }
    
    /**
     * Nils the "ShortName" element
     */
    public void setNilShortName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SHORTNAME$18, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(SHORTNAME$18);
            }
            target.setNil();
        }
    }
}
