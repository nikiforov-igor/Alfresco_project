/*
 * XML Type:  AuthorizationError
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxy.impl;
/**
 * An XML AuthorizationError(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy).
 *
 * This is a complex type.
 */
public class AuthorizationErrorImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError
{
    private static final long serialVersionUID = 1L;
    
    public AuthorizationErrorImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName AUTHENTICATIONTYPE$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "AuthenticationType");
    private static final javax.xml.namespace.QName CERTIFICATEISSUERNAME$2 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "CertificateIssuerName");
    private static final javax.xml.namespace.QName CERTIFICATETHUMBPRINT$4 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "CertificateThumbprint");
    private static final javax.xml.namespace.QName ENCRYPTEDTOKEN$6 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "EncryptedToken");
    private static final javax.xml.namespace.QName MESSAGE$8 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "Message");
    private static final javax.xml.namespace.QName OPERATORCODE$10 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "OperatorCode");
    
    
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
     * Gets the "CertificateThumbprint" element
     */
    public java.lang.String getCertificateThumbprint()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CERTIFICATETHUMBPRINT$4, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "CertificateThumbprint" element
     */
    public org.apache.xmlbeans.XmlString xgetCertificateThumbprint()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(CERTIFICATETHUMBPRINT$4, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "CertificateThumbprint" element
     */
    public boolean isNilCertificateThumbprint()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(CERTIFICATETHUMBPRINT$4, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "CertificateThumbprint" element
     */
    public boolean isSetCertificateThumbprint()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(CERTIFICATETHUMBPRINT$4) != 0;
        }
    }
    
    /**
     * Sets the "CertificateThumbprint" element
     */
    public void setCertificateThumbprint(java.lang.String certificateThumbprint)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CERTIFICATETHUMBPRINT$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(CERTIFICATETHUMBPRINT$4);
            }
            target.setStringValue(certificateThumbprint);
        }
    }
    
    /**
     * Sets (as xml) the "CertificateThumbprint" element
     */
    public void xsetCertificateThumbprint(org.apache.xmlbeans.XmlString certificateThumbprint)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(CERTIFICATETHUMBPRINT$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(CERTIFICATETHUMBPRINT$4);
            }
            target.set(certificateThumbprint);
        }
    }
    
    /**
     * Nils the "CertificateThumbprint" element
     */
    public void setNilCertificateThumbprint()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(CERTIFICATETHUMBPRINT$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(CERTIFICATETHUMBPRINT$4);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "CertificateThumbprint" element
     */
    public void unsetCertificateThumbprint()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(CERTIFICATETHUMBPRINT$4, 0);
        }
    }
    
    /**
     * Gets the "EncryptedToken" element
     */
    public java.lang.String getEncryptedToken()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ENCRYPTEDTOKEN$6, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "EncryptedToken" element
     */
    public org.apache.xmlbeans.XmlString xgetEncryptedToken()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ENCRYPTEDTOKEN$6, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "EncryptedToken" element
     */
    public boolean isNilEncryptedToken()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ENCRYPTEDTOKEN$6, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "EncryptedToken" element
     */
    public boolean isSetEncryptedToken()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(ENCRYPTEDTOKEN$6) != 0;
        }
    }
    
    /**
     * Sets the "EncryptedToken" element
     */
    public void setEncryptedToken(java.lang.String encryptedToken)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ENCRYPTEDTOKEN$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ENCRYPTEDTOKEN$6);
            }
            target.setStringValue(encryptedToken);
        }
    }
    
    /**
     * Sets (as xml) the "EncryptedToken" element
     */
    public void xsetEncryptedToken(org.apache.xmlbeans.XmlString encryptedToken)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ENCRYPTEDTOKEN$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(ENCRYPTEDTOKEN$6);
            }
            target.set(encryptedToken);
        }
    }
    
    /**
     * Nils the "EncryptedToken" element
     */
    public void setNilEncryptedToken()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ENCRYPTEDTOKEN$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(ENCRYPTEDTOKEN$6);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "EncryptedToken" element
     */
    public void unsetEncryptedToken()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(ENCRYPTEDTOKEN$6, 0);
        }
    }
    
    /**
     * Gets the "Message" element
     */
    public java.lang.String getMessage()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(MESSAGE$8, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Message" element
     */
    public org.apache.xmlbeans.XmlString xgetMessage()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(MESSAGE$8, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "Message" element
     */
    public boolean isNilMessage()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(MESSAGE$8, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Message" element
     */
    public boolean isSetMessage()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(MESSAGE$8) != 0;
        }
    }
    
    /**
     * Sets the "Message" element
     */
    public void setMessage(java.lang.String message)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(MESSAGE$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(MESSAGE$8);
            }
            target.setStringValue(message);
        }
    }
    
    /**
     * Sets (as xml) the "Message" element
     */
    public void xsetMessage(org.apache.xmlbeans.XmlString message)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(MESSAGE$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(MESSAGE$8);
            }
            target.set(message);
        }
    }
    
    /**
     * Nils the "Message" element
     */
    public void setNilMessage()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(MESSAGE$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(MESSAGE$8);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Message" element
     */
    public void unsetMessage()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(MESSAGE$8, 0);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(OPERATORCODE$10, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORCODE$10, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORCODE$10, 0);
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
            return get_store().count_elements(OPERATORCODE$10) != 0;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(OPERATORCODE$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(OPERATORCODE$10);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORCODE$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(OPERATORCODE$10);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORCODE$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(OPERATORCODE$10);
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
            get_store().remove_element(OPERATORCODE$10, 0);
        }
    }
}
