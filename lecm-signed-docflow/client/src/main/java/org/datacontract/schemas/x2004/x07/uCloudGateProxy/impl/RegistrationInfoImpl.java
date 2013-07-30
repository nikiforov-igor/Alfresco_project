/*
 * XML Type:  RegistrationInfo
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxy.RegistrationInfo
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxy.impl;
/**
 * An XML RegistrationInfo(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy).
 *
 * This is a complex type.
 */
public class RegistrationInfoImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxy.RegistrationInfo
{
    private static final long serialVersionUID = 1L;
    
    public RegistrationInfoImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName ISORGANIZATIONREGISTERED$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "IsOrganizationRegistered");
    private static final javax.xml.namespace.QName ISUSERREGISTERED$2 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "IsUserRegistered");
    private static final javax.xml.namespace.QName OPERATORCODE$4 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "OperatorCode");
    
    
    /**
     * Gets the "IsOrganizationRegistered" element
     */
    public boolean getIsOrganizationRegistered()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ISORGANIZATIONREGISTERED$0, 0);
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "IsOrganizationRegistered" element
     */
    public org.apache.xmlbeans.XmlBoolean xgetIsOrganizationRegistered()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(ISORGANIZATIONREGISTERED$0, 0);
            return target;
        }
    }
    
    /**
     * True if has "IsOrganizationRegistered" element
     */
    public boolean isSetIsOrganizationRegistered()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(ISORGANIZATIONREGISTERED$0) != 0;
        }
    }
    
    /**
     * Sets the "IsOrganizationRegistered" element
     */
    public void setIsOrganizationRegistered(boolean isOrganizationRegistered)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ISORGANIZATIONREGISTERED$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ISORGANIZATIONREGISTERED$0);
            }
            target.setBooleanValue(isOrganizationRegistered);
        }
    }
    
    /**
     * Sets (as xml) the "IsOrganizationRegistered" element
     */
    public void xsetIsOrganizationRegistered(org.apache.xmlbeans.XmlBoolean isOrganizationRegistered)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(ISORGANIZATIONREGISTERED$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_element_user(ISORGANIZATIONREGISTERED$0);
            }
            target.set(isOrganizationRegistered);
        }
    }
    
    /**
     * Unsets the "IsOrganizationRegistered" element
     */
    public void unsetIsOrganizationRegistered()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(ISORGANIZATIONREGISTERED$0, 0);
        }
    }
    
    /**
     * Gets the "IsUserRegistered" element
     */
    public boolean getIsUserRegistered()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ISUSERREGISTERED$2, 0);
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "IsUserRegistered" element
     */
    public org.apache.xmlbeans.XmlBoolean xgetIsUserRegistered()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(ISUSERREGISTERED$2, 0);
            return target;
        }
    }
    
    /**
     * True if has "IsUserRegistered" element
     */
    public boolean isSetIsUserRegistered()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(ISUSERREGISTERED$2) != 0;
        }
    }
    
    /**
     * Sets the "IsUserRegistered" element
     */
    public void setIsUserRegistered(boolean isUserRegistered)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ISUSERREGISTERED$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ISUSERREGISTERED$2);
            }
            target.setBooleanValue(isUserRegistered);
        }
    }
    
    /**
     * Sets (as xml) the "IsUserRegistered" element
     */
    public void xsetIsUserRegistered(org.apache.xmlbeans.XmlBoolean isUserRegistered)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(ISUSERREGISTERED$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_element_user(ISUSERREGISTERED$2);
            }
            target.set(isUserRegistered);
        }
    }
    
    /**
     * Unsets the "IsUserRegistered" element
     */
    public void unsetIsUserRegistered()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(ISUSERREGISTERED$2, 0);
        }
    }
    
    /**
     * Gets the "OperatorCode" element
     */
    public java.lang.String getOperatorCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(OPERATORCODE$4, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "OperatorCode" element
     */
    public org.apache.xmlbeans.XmlString xgetOperatorCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORCODE$4, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "OperatorCode" element
     */
    public boolean isNilOperatorCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORCODE$4, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "OperatorCode" element
     */
    public boolean isSetOperatorCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(OPERATORCODE$4) != 0;
        }
    }
    
    /**
     * Sets the "OperatorCode" element
     */
    public void setOperatorCode(java.lang.String operatorCode)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(OPERATORCODE$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(OPERATORCODE$4);
            }
            target.setStringValue(operatorCode);
        }
    }
    
    /**
     * Sets (as xml) the "OperatorCode" element
     */
    public void xsetOperatorCode(org.apache.xmlbeans.XmlString operatorCode)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORCODE$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(OPERATORCODE$4);
            }
            target.set(operatorCode);
        }
    }
    
    /**
     * Nils the "OperatorCode" element
     */
    public void setNilOperatorCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORCODE$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(OPERATORCODE$4);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "OperatorCode" element
     */
    public void unsetOperatorCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(OPERATORCODE$4, 0);
        }
    }
}
