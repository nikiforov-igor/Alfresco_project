/*
 * XML Type:  OrganizationInfo
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Invoice
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.impl;
/**
 * An XML OrganizationInfo(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Invoice).
 *
 * This is a complex type.
 */
public class OrganizationInfoImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo
{
    private static final long serialVersionUID = 1L;
    
    public OrganizationInfoImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName ADDRESS$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Invoice", "Address");
    private static final javax.xml.namespace.QName BANKACCOUNTNUMBER$2 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Invoice", "BankAccountNumber");
    private static final javax.xml.namespace.QName BANKID$4 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Invoice", "BankId");
    private static final javax.xml.namespace.QName BANKNAME$6 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Invoice", "BankName");
    private static final javax.xml.namespace.QName DEPARTMENT$8 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Invoice", "Department");
    private static final javax.xml.namespace.QName FAX$10 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Invoice", "Fax");
    private static final javax.xml.namespace.QName INN$12 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Invoice", "Inn");
    private static final javax.xml.namespace.QName ISSOLEPROPRIETOR$14 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Invoice", "IsSoleProprietor");
    private static final javax.xml.namespace.QName KPP$16 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Invoice", "Kpp");
    private static final javax.xml.namespace.QName NAME$18 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Invoice", "Name");
    private static final javax.xml.namespace.QName OKDP$20 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Invoice", "Okdp");
    private static final javax.xml.namespace.QName OKOPF$22 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Invoice", "Okopf");
    private static final javax.xml.namespace.QName OKPO$24 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Invoice", "Okpo");
    private static final javax.xml.namespace.QName PHONE$26 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Invoice", "Phone");
    
    
    /**
     * Gets the "Address" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.Address getAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.Address target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.Address)get_store().find_element_user(ADDRESS$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "Address" element
     */
    public boolean isNilAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.Address target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.Address)get_store().find_element_user(ADDRESS$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Address" element
     */
    public boolean isSetAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(ADDRESS$0) != 0;
        }
    }
    
    /**
     * Sets the "Address" element
     */
    public void setAddress(org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.Address address)
    {
        generatedSetterHelperImpl(address, ADDRESS$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "Address" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.Address addNewAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.Address target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.Address)get_store().add_element_user(ADDRESS$0);
            return target;
        }
    }
    
    /**
     * Nils the "Address" element
     */
    public void setNilAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.Address target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.Address)get_store().find_element_user(ADDRESS$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.Address)get_store().add_element_user(ADDRESS$0);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Address" element
     */
    public void unsetAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(ADDRESS$0, 0);
        }
    }
    
    /**
     * Gets the "BankAccountNumber" element
     */
    public java.lang.String getBankAccountNumber()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(BANKACCOUNTNUMBER$2, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "BankAccountNumber" element
     */
    public org.apache.xmlbeans.XmlString xgetBankAccountNumber()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(BANKACCOUNTNUMBER$2, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "BankAccountNumber" element
     */
    public boolean isNilBankAccountNumber()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(BANKACCOUNTNUMBER$2, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "BankAccountNumber" element
     */
    public boolean isSetBankAccountNumber()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(BANKACCOUNTNUMBER$2) != 0;
        }
    }
    
    /**
     * Sets the "BankAccountNumber" element
     */
    public void setBankAccountNumber(java.lang.String bankAccountNumber)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(BANKACCOUNTNUMBER$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(BANKACCOUNTNUMBER$2);
            }
            target.setStringValue(bankAccountNumber);
        }
    }
    
    /**
     * Sets (as xml) the "BankAccountNumber" element
     */
    public void xsetBankAccountNumber(org.apache.xmlbeans.XmlString bankAccountNumber)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(BANKACCOUNTNUMBER$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(BANKACCOUNTNUMBER$2);
            }
            target.set(bankAccountNumber);
        }
    }
    
    /**
     * Nils the "BankAccountNumber" element
     */
    public void setNilBankAccountNumber()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(BANKACCOUNTNUMBER$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(BANKACCOUNTNUMBER$2);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "BankAccountNumber" element
     */
    public void unsetBankAccountNumber()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(BANKACCOUNTNUMBER$2, 0);
        }
    }
    
    /**
     * Gets the "BankId" element
     */
    public java.lang.String getBankId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(BANKID$4, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "BankId" element
     */
    public org.apache.xmlbeans.XmlString xgetBankId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(BANKID$4, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "BankId" element
     */
    public boolean isNilBankId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(BANKID$4, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "BankId" element
     */
    public boolean isSetBankId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(BANKID$4) != 0;
        }
    }
    
    /**
     * Sets the "BankId" element
     */
    public void setBankId(java.lang.String bankId)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(BANKID$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(BANKID$4);
            }
            target.setStringValue(bankId);
        }
    }
    
    /**
     * Sets (as xml) the "BankId" element
     */
    public void xsetBankId(org.apache.xmlbeans.XmlString bankId)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(BANKID$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(BANKID$4);
            }
            target.set(bankId);
        }
    }
    
    /**
     * Nils the "BankId" element
     */
    public void setNilBankId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(BANKID$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(BANKID$4);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "BankId" element
     */
    public void unsetBankId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(BANKID$4, 0);
        }
    }
    
    /**
     * Gets the "BankName" element
     */
    public java.lang.String getBankName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(BANKNAME$6, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "BankName" element
     */
    public org.apache.xmlbeans.XmlString xgetBankName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(BANKNAME$6, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "BankName" element
     */
    public boolean isNilBankName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(BANKNAME$6, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "BankName" element
     */
    public boolean isSetBankName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(BANKNAME$6) != 0;
        }
    }
    
    /**
     * Sets the "BankName" element
     */
    public void setBankName(java.lang.String bankName)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(BANKNAME$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(BANKNAME$6);
            }
            target.setStringValue(bankName);
        }
    }
    
    /**
     * Sets (as xml) the "BankName" element
     */
    public void xsetBankName(org.apache.xmlbeans.XmlString bankName)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(BANKNAME$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(BANKNAME$6);
            }
            target.set(bankName);
        }
    }
    
    /**
     * Nils the "BankName" element
     */
    public void setNilBankName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(BANKNAME$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(BANKNAME$6);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "BankName" element
     */
    public void unsetBankName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(BANKNAME$6, 0);
        }
    }
    
    /**
     * Gets the "Department" element
     */
    public java.lang.String getDepartment()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DEPARTMENT$8, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Department" element
     */
    public org.apache.xmlbeans.XmlString xgetDepartment()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DEPARTMENT$8, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "Department" element
     */
    public boolean isNilDepartment()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DEPARTMENT$8, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Department" element
     */
    public boolean isSetDepartment()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(DEPARTMENT$8) != 0;
        }
    }
    
    /**
     * Sets the "Department" element
     */
    public void setDepartment(java.lang.String department)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DEPARTMENT$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(DEPARTMENT$8);
            }
            target.setStringValue(department);
        }
    }
    
    /**
     * Sets (as xml) the "Department" element
     */
    public void xsetDepartment(org.apache.xmlbeans.XmlString department)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DEPARTMENT$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(DEPARTMENT$8);
            }
            target.set(department);
        }
    }
    
    /**
     * Nils the "Department" element
     */
    public void setNilDepartment()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DEPARTMENT$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(DEPARTMENT$8);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Department" element
     */
    public void unsetDepartment()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(DEPARTMENT$8, 0);
        }
    }
    
    /**
     * Gets the "Fax" element
     */
    public java.lang.String getFax()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(FAX$10, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Fax" element
     */
    public org.apache.xmlbeans.XmlString xgetFax()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FAX$10, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "Fax" element
     */
    public boolean isNilFax()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FAX$10, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Fax" element
     */
    public boolean isSetFax()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(FAX$10) != 0;
        }
    }
    
    /**
     * Sets the "Fax" element
     */
    public void setFax(java.lang.String fax)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(FAX$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(FAX$10);
            }
            target.setStringValue(fax);
        }
    }
    
    /**
     * Sets (as xml) the "Fax" element
     */
    public void xsetFax(org.apache.xmlbeans.XmlString fax)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FAX$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(FAX$10);
            }
            target.set(fax);
        }
    }
    
    /**
     * Nils the "Fax" element
     */
    public void setNilFax()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FAX$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(FAX$10);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Fax" element
     */
    public void unsetFax()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(FAX$10, 0);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(INN$12, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INN$12, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INN$12, 0);
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
            return get_store().count_elements(INN$12) != 0;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(INN$12, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(INN$12);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INN$12, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(INN$12);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INN$12, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(INN$12);
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
            get_store().remove_element(INN$12, 0);
        }
    }
    
    /**
     * Gets the "IsSoleProprietor" element
     */
    public boolean getIsSoleProprietor()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ISSOLEPROPRIETOR$14, 0);
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "IsSoleProprietor" element
     */
    public org.apache.xmlbeans.XmlBoolean xgetIsSoleProprietor()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(ISSOLEPROPRIETOR$14, 0);
            return target;
        }
    }
    
    /**
     * True if has "IsSoleProprietor" element
     */
    public boolean isSetIsSoleProprietor()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(ISSOLEPROPRIETOR$14) != 0;
        }
    }
    
    /**
     * Sets the "IsSoleProprietor" element
     */
    public void setIsSoleProprietor(boolean isSoleProprietor)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ISSOLEPROPRIETOR$14, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ISSOLEPROPRIETOR$14);
            }
            target.setBooleanValue(isSoleProprietor);
        }
    }
    
    /**
     * Sets (as xml) the "IsSoleProprietor" element
     */
    public void xsetIsSoleProprietor(org.apache.xmlbeans.XmlBoolean isSoleProprietor)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(ISSOLEPROPRIETOR$14, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_element_user(ISSOLEPROPRIETOR$14);
            }
            target.set(isSoleProprietor);
        }
    }
    
    /**
     * Unsets the "IsSoleProprietor" element
     */
    public void unsetIsSoleProprietor()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(ISSOLEPROPRIETOR$14, 0);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(KPP$16, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(KPP$16, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(KPP$16, 0);
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
            return get_store().count_elements(KPP$16) != 0;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(KPP$16, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(KPP$16);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(KPP$16, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(KPP$16);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(KPP$16, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(KPP$16);
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
            get_store().remove_element(KPP$16, 0);
        }
    }
    
    /**
     * Gets the "Name" element
     */
    public java.lang.String getName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(NAME$18, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Name" element
     */
    public org.apache.xmlbeans.XmlString xgetName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NAME$18, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "Name" element
     */
    public boolean isNilName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NAME$18, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Name" element
     */
    public boolean isSetName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(NAME$18) != 0;
        }
    }
    
    /**
     * Sets the "Name" element
     */
    public void setName(java.lang.String name)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(NAME$18, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(NAME$18);
            }
            target.setStringValue(name);
        }
    }
    
    /**
     * Sets (as xml) the "Name" element
     */
    public void xsetName(org.apache.xmlbeans.XmlString name)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NAME$18, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(NAME$18);
            }
            target.set(name);
        }
    }
    
    /**
     * Nils the "Name" element
     */
    public void setNilName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NAME$18, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(NAME$18);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Name" element
     */
    public void unsetName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(NAME$18, 0);
        }
    }
    
    /**
     * Gets the "Okdp" element
     */
    public java.lang.String getOkdp()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(OKDP$20, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Okdp" element
     */
    public org.apache.xmlbeans.XmlString xgetOkdp()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OKDP$20, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "Okdp" element
     */
    public boolean isNilOkdp()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OKDP$20, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Okdp" element
     */
    public boolean isSetOkdp()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(OKDP$20) != 0;
        }
    }
    
    /**
     * Sets the "Okdp" element
     */
    public void setOkdp(java.lang.String okdp)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(OKDP$20, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(OKDP$20);
            }
            target.setStringValue(okdp);
        }
    }
    
    /**
     * Sets (as xml) the "Okdp" element
     */
    public void xsetOkdp(org.apache.xmlbeans.XmlString okdp)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OKDP$20, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(OKDP$20);
            }
            target.set(okdp);
        }
    }
    
    /**
     * Nils the "Okdp" element
     */
    public void setNilOkdp()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OKDP$20, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(OKDP$20);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Okdp" element
     */
    public void unsetOkdp()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(OKDP$20, 0);
        }
    }
    
    /**
     * Gets the "Okopf" element
     */
    public java.lang.String getOkopf()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(OKOPF$22, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Okopf" element
     */
    public org.apache.xmlbeans.XmlString xgetOkopf()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OKOPF$22, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "Okopf" element
     */
    public boolean isNilOkopf()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OKOPF$22, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Okopf" element
     */
    public boolean isSetOkopf()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(OKOPF$22) != 0;
        }
    }
    
    /**
     * Sets the "Okopf" element
     */
    public void setOkopf(java.lang.String okopf)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(OKOPF$22, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(OKOPF$22);
            }
            target.setStringValue(okopf);
        }
    }
    
    /**
     * Sets (as xml) the "Okopf" element
     */
    public void xsetOkopf(org.apache.xmlbeans.XmlString okopf)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OKOPF$22, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(OKOPF$22);
            }
            target.set(okopf);
        }
    }
    
    /**
     * Nils the "Okopf" element
     */
    public void setNilOkopf()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OKOPF$22, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(OKOPF$22);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Okopf" element
     */
    public void unsetOkopf()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(OKOPF$22, 0);
        }
    }
    
    /**
     * Gets the "Okpo" element
     */
    public java.lang.String getOkpo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(OKPO$24, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Okpo" element
     */
    public org.apache.xmlbeans.XmlString xgetOkpo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OKPO$24, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "Okpo" element
     */
    public boolean isNilOkpo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OKPO$24, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Okpo" element
     */
    public boolean isSetOkpo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(OKPO$24) != 0;
        }
    }
    
    /**
     * Sets the "Okpo" element
     */
    public void setOkpo(java.lang.String okpo)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(OKPO$24, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(OKPO$24);
            }
            target.setStringValue(okpo);
        }
    }
    
    /**
     * Sets (as xml) the "Okpo" element
     */
    public void xsetOkpo(org.apache.xmlbeans.XmlString okpo)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OKPO$24, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(OKPO$24);
            }
            target.set(okpo);
        }
    }
    
    /**
     * Nils the "Okpo" element
     */
    public void setNilOkpo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OKPO$24, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(OKPO$24);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Okpo" element
     */
    public void unsetOkpo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(OKPO$24, 0);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PHONE$26, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PHONE$26, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PHONE$26, 0);
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
            return get_store().count_elements(PHONE$26) != 0;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PHONE$26, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PHONE$26);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PHONE$26, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PHONE$26);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PHONE$26, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PHONE$26);
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
            get_store().remove_element(PHONE$26, 0);
        }
    }
}
