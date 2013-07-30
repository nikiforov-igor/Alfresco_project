/*
 * XML Type:  OperatorInfo
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxy.impl;
/**
 * An XML OperatorInfo(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy).
 *
 * This is a complex type.
 */
public class OperatorInfoImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo
{
    private static final long serialVersionUID = 1L;
    
    public OperatorInfoImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName AUTHENTICATIONTYPE$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "AuthenticationType");
    private static final javax.xml.namespace.QName CERTIFICATEISSUERNAME$2 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "CertificateIssuerName");
    private static final javax.xml.namespace.QName CODE$4 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "Code");
    private static final javax.xml.namespace.QName EXTENSION$6 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "Extension");
    private static final javax.xml.namespace.QName INN$8 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "Inn");
    private static final javax.xml.namespace.QName ISREMOTESIGNENABLED$10 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "IsRemoteSignEnabled");
    private static final javax.xml.namespace.QName NAME$12 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "Name");
    
    
    /**
     * Gets the "AuthenticationType" element
     */
    public java.util.List getAuthenticationType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(AUTHENTICATIONTYPE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getListValue();
        }
    }
    
    /**
     * Gets (as xml) the "AuthenticationType" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.EOperatorAuthenticationType xgetAuthenticationType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.EOperatorAuthenticationType target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EOperatorAuthenticationType)get_store().find_element_user(AUTHENTICATIONTYPE$0, 0);
            return target;
        }
    }
    
    /**
     * True if has "AuthenticationType" element
     */
    public boolean isSetAuthenticationType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(AUTHENTICATIONTYPE$0) != 0;
        }
    }
    
    /**
     * Sets the "AuthenticationType" element
     */
    public void setAuthenticationType(java.util.List authenticationType)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(AUTHENTICATIONTYPE$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(AUTHENTICATIONTYPE$0);
            }
            target.setListValue(authenticationType);
        }
    }
    
    /**
     * Sets (as xml) the "AuthenticationType" element
     */
    public void xsetAuthenticationType(org.datacontract.schemas.x2004.x07.uCloudGateProxy.EOperatorAuthenticationType authenticationType)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.EOperatorAuthenticationType target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EOperatorAuthenticationType)get_store().find_element_user(AUTHENTICATIONTYPE$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EOperatorAuthenticationType)get_store().add_element_user(AUTHENTICATIONTYPE$0);
            }
            target.set(authenticationType);
        }
    }
    
    /**
     * Unsets the "AuthenticationType" element
     */
    public void unsetAuthenticationType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(AUTHENTICATIONTYPE$0, 0);
        }
    }
    
    /**
     * Gets the "CertificateIssuerName" element
     */
    public java.lang.String getCertificateIssuerName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CERTIFICATEISSUERNAME$2, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "CertificateIssuerName" element
     */
    public org.apache.xmlbeans.XmlString xgetCertificateIssuerName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(CERTIFICATEISSUERNAME$2, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "CertificateIssuerName" element
     */
    public boolean isNilCertificateIssuerName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(CERTIFICATEISSUERNAME$2, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "CertificateIssuerName" element
     */
    public boolean isSetCertificateIssuerName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(CERTIFICATEISSUERNAME$2) != 0;
        }
    }
    
    /**
     * Sets the "CertificateIssuerName" element
     */
    public void setCertificateIssuerName(java.lang.String certificateIssuerName)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CERTIFICATEISSUERNAME$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(CERTIFICATEISSUERNAME$2);
            }
            target.setStringValue(certificateIssuerName);
        }
    }
    
    /**
     * Sets (as xml) the "CertificateIssuerName" element
     */
    public void xsetCertificateIssuerName(org.apache.xmlbeans.XmlString certificateIssuerName)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(CERTIFICATEISSUERNAME$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(CERTIFICATEISSUERNAME$2);
            }
            target.set(certificateIssuerName);
        }
    }
    
    /**
     * Nils the "CertificateIssuerName" element
     */
    public void setNilCertificateIssuerName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(CERTIFICATEISSUERNAME$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(CERTIFICATEISSUERNAME$2);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "CertificateIssuerName" element
     */
    public void unsetCertificateIssuerName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(CERTIFICATEISSUERNAME$2, 0);
        }
    }
    
    /**
     * Gets the "Code" element
     */
    public java.lang.String getCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CODE$4, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Code" element
     */
    public org.apache.xmlbeans.XmlString xgetCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(CODE$4, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "Code" element
     */
    public boolean isNilCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(CODE$4, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Code" element
     */
    public boolean isSetCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(CODE$4) != 0;
        }
    }
    
    /**
     * Sets the "Code" element
     */
    public void setCode(java.lang.String code)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CODE$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(CODE$4);
            }
            target.setStringValue(code);
        }
    }
    
    /**
     * Sets (as xml) the "Code" element
     */
    public void xsetCode(org.apache.xmlbeans.XmlString code)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(CODE$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(CODE$4);
            }
            target.set(code);
        }
    }
    
    /**
     * Nils the "Code" element
     */
    public void setNilCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(CODE$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(CODE$4);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Code" element
     */
    public void unsetCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(CODE$4, 0);
        }
    }
    
    /**
     * Gets the "Extension" element
     */
    public java.lang.String getExtension()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(EXTENSION$6, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Extension" element
     */
    public org.apache.xmlbeans.XmlString xgetExtension()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(EXTENSION$6, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "Extension" element
     */
    public boolean isNilExtension()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(EXTENSION$6, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Extension" element
     */
    public boolean isSetExtension()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(EXTENSION$6) != 0;
        }
    }
    
    /**
     * Sets the "Extension" element
     */
    public void setExtension(java.lang.String extension)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(EXTENSION$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(EXTENSION$6);
            }
            target.setStringValue(extension);
        }
    }
    
    /**
     * Sets (as xml) the "Extension" element
     */
    public void xsetExtension(org.apache.xmlbeans.XmlString extension)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(EXTENSION$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(EXTENSION$6);
            }
            target.set(extension);
        }
    }
    
    /**
     * Nils the "Extension" element
     */
    public void setNilExtension()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(EXTENSION$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(EXTENSION$6);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Extension" element
     */
    public void unsetExtension()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(EXTENSION$6, 0);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(INN$8, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INN$8, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INN$8, 0);
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
            return get_store().count_elements(INN$8) != 0;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(INN$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(INN$8);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INN$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(INN$8);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INN$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(INN$8);
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
            get_store().remove_element(INN$8, 0);
        }
    }
    
    /**
     * Gets the "IsRemoteSignEnabled" element
     */
    public boolean getIsRemoteSignEnabled()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ISREMOTESIGNENABLED$10, 0);
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "IsRemoteSignEnabled" element
     */
    public org.apache.xmlbeans.XmlBoolean xgetIsRemoteSignEnabled()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(ISREMOTESIGNENABLED$10, 0);
            return target;
        }
    }
    
    /**
     * True if has "IsRemoteSignEnabled" element
     */
    public boolean isSetIsRemoteSignEnabled()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(ISREMOTESIGNENABLED$10) != 0;
        }
    }
    
    /**
     * Sets the "IsRemoteSignEnabled" element
     */
    public void setIsRemoteSignEnabled(boolean isRemoteSignEnabled)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ISREMOTESIGNENABLED$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ISREMOTESIGNENABLED$10);
            }
            target.setBooleanValue(isRemoteSignEnabled);
        }
    }
    
    /**
     * Sets (as xml) the "IsRemoteSignEnabled" element
     */
    public void xsetIsRemoteSignEnabled(org.apache.xmlbeans.XmlBoolean isRemoteSignEnabled)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(ISREMOTESIGNENABLED$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_element_user(ISREMOTESIGNENABLED$10);
            }
            target.set(isRemoteSignEnabled);
        }
    }
    
    /**
     * Unsets the "IsRemoteSignEnabled" element
     */
    public void unsetIsRemoteSignEnabled()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(ISREMOTESIGNENABLED$10, 0);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(NAME$12, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NAME$12, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NAME$12, 0);
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
            return get_store().count_elements(NAME$12) != 0;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(NAME$12, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(NAME$12);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NAME$12, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(NAME$12);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NAME$12, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(NAME$12);
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
            get_store().remove_element(NAME$12, 0);
        }
    }
}
