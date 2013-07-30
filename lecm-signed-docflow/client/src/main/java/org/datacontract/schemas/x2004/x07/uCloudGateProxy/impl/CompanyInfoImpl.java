/*
 * XML Type:  CompanyInfo
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxy.impl;
/**
 * An XML CompanyInfo(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy).
 *
 * This is a complex type.
 */
public class CompanyInfoImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo
{
    private static final long serialVersionUID = 1L;
    
    public CompanyInfoImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName INN$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "Inn");
    private static final javax.xml.namespace.QName KPP$2 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "Kpp");
    
    
    /**
     * Gets the "Inn" element
     */
    public java.lang.String getInn()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(INN$0, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INN$0, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INN$0, 0);
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
            return get_store().count_elements(INN$0) != 0;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(INN$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(INN$0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INN$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(INN$0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INN$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(INN$0);
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
            get_store().remove_element(INN$0, 0);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(KPP$2, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(KPP$2, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(KPP$2, 0);
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
            return get_store().count_elements(KPP$2) != 0;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(KPP$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(KPP$2);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(KPP$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(KPP$2);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(KPP$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(KPP$2);
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
            get_store().remove_element(KPP$2, 0);
        }
    }
}
