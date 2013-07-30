/*
 * XML Type:  RegisterResponse
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.impl;
/**
 * An XML RegisterResponse(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration).
 *
 * This is a complex type.
 */
public class RegisterResponseImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse
{
    private static final long serialVersionUID = 1L;
    
    public RegisterResponseImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName ABONENTIDBYOPERATOR$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration", "AbonentIdByOperator");
    private static final javax.xml.namespace.QName CERTIFICATE$2 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration", "Certificate");
    private static final javax.xml.namespace.QName RESPONSEDATETIME$4 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration", "ResponseDateTime");
    private static final javax.xml.namespace.QName RESULTCODE$6 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration", "ResultCode");
    private static final javax.xml.namespace.QName RESULTMESSAGE$8 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration", "ResultMessage");
    
    
    /**
     * Gets the "AbonentIdByOperator" element
     */
    public java.lang.String getAbonentIdByOperator()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ABONENTIDBYOPERATOR$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "AbonentIdByOperator" element
     */
    public com.microsoft.schemas.x2003.x10.serialization.Guid xgetAbonentIdByOperator()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.microsoft.schemas.x2003.x10.serialization.Guid target = null;
            target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().find_element_user(ABONENTIDBYOPERATOR$0, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "AbonentIdByOperator" element
     */
    public boolean isNilAbonentIdByOperator()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.microsoft.schemas.x2003.x10.serialization.Guid target = null;
            target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().find_element_user(ABONENTIDBYOPERATOR$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "AbonentIdByOperator" element
     */
    public boolean isSetAbonentIdByOperator()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(ABONENTIDBYOPERATOR$0) != 0;
        }
    }
    
    /**
     * Sets the "AbonentIdByOperator" element
     */
    public void setAbonentIdByOperator(java.lang.String abonentIdByOperator)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ABONENTIDBYOPERATOR$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ABONENTIDBYOPERATOR$0);
            }
            target.setStringValue(abonentIdByOperator);
        }
    }
    
    /**
     * Sets (as xml) the "AbonentIdByOperator" element
     */
    public void xsetAbonentIdByOperator(com.microsoft.schemas.x2003.x10.serialization.Guid abonentIdByOperator)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.microsoft.schemas.x2003.x10.serialization.Guid target = null;
            target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().find_element_user(ABONENTIDBYOPERATOR$0, 0);
            if (target == null)
            {
                target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().add_element_user(ABONENTIDBYOPERATOR$0);
            }
            target.set(abonentIdByOperator);
        }
    }
    
    /**
     * Nils the "AbonentIdByOperator" element
     */
    public void setNilAbonentIdByOperator()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.microsoft.schemas.x2003.x10.serialization.Guid target = null;
            target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().find_element_user(ABONENTIDBYOPERATOR$0, 0);
            if (target == null)
            {
                target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().add_element_user(ABONENTIDBYOPERATOR$0);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "AbonentIdByOperator" element
     */
    public void unsetAbonentIdByOperator()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(ABONENTIDBYOPERATOR$0, 0);
        }
    }
    
    /**
     * Gets the "Certificate" element
     */
    public byte[] getCertificate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CERTIFICATE$2, 0);
            if (target == null)
            {
                return null;
            }
            return target.getByteArrayValue();
        }
    }
    
    /**
     * Gets (as xml) the "Certificate" element
     */
    public org.apache.xmlbeans.XmlBase64Binary xgetCertificate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBase64Binary target = null;
            target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(CERTIFICATE$2, 0);
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
            org.apache.xmlbeans.XmlBase64Binary target = null;
            target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(CERTIFICATE$2, 0);
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
            return get_store().count_elements(CERTIFICATE$2) != 0;
        }
    }
    
    /**
     * Sets the "Certificate" element
     */
    public void setCertificate(byte[] certificate)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CERTIFICATE$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(CERTIFICATE$2);
            }
            target.setByteArrayValue(certificate);
        }
    }
    
    /**
     * Sets (as xml) the "Certificate" element
     */
    public void xsetCertificate(org.apache.xmlbeans.XmlBase64Binary certificate)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBase64Binary target = null;
            target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(CERTIFICATE$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBase64Binary)get_store().add_element_user(CERTIFICATE$2);
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
            org.apache.xmlbeans.XmlBase64Binary target = null;
            target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(CERTIFICATE$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBase64Binary)get_store().add_element_user(CERTIFICATE$2);
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
            get_store().remove_element(CERTIFICATE$2, 0);
        }
    }
    
    /**
     * Gets the "ResponseDateTime" element
     */
    public java.lang.String getResponseDateTime()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(RESPONSEDATETIME$4, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "ResponseDateTime" element
     */
    public org.apache.xmlbeans.XmlString xgetResponseDateTime()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(RESPONSEDATETIME$4, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "ResponseDateTime" element
     */
    public boolean isNilResponseDateTime()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(RESPONSEDATETIME$4, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "ResponseDateTime" element
     */
    public boolean isSetResponseDateTime()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(RESPONSEDATETIME$4) != 0;
        }
    }
    
    /**
     * Sets the "ResponseDateTime" element
     */
    public void setResponseDateTime(java.lang.String responseDateTime)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(RESPONSEDATETIME$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(RESPONSEDATETIME$4);
            }
            target.setStringValue(responseDateTime);
        }
    }
    
    /**
     * Sets (as xml) the "ResponseDateTime" element
     */
    public void xsetResponseDateTime(org.apache.xmlbeans.XmlString responseDateTime)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(RESPONSEDATETIME$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(RESPONSEDATETIME$4);
            }
            target.set(responseDateTime);
        }
    }
    
    /**
     * Nils the "ResponseDateTime" element
     */
    public void setNilResponseDateTime()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(RESPONSEDATETIME$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(RESPONSEDATETIME$4);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "ResponseDateTime" element
     */
    public void unsetResponseDateTime()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(RESPONSEDATETIME$4, 0);
        }
    }
    
    /**
     * Gets the "ResultCode" element
     */
    public java.lang.String getResultCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(RESULTCODE$6, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "ResultCode" element
     */
    public org.apache.xmlbeans.XmlString xgetResultCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(RESULTCODE$6, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "ResultCode" element
     */
    public boolean isNilResultCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(RESULTCODE$6, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "ResultCode" element
     */
    public boolean isSetResultCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(RESULTCODE$6) != 0;
        }
    }
    
    /**
     * Sets the "ResultCode" element
     */
    public void setResultCode(java.lang.String resultCode)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(RESULTCODE$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(RESULTCODE$6);
            }
            target.setStringValue(resultCode);
        }
    }
    
    /**
     * Sets (as xml) the "ResultCode" element
     */
    public void xsetResultCode(org.apache.xmlbeans.XmlString resultCode)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(RESULTCODE$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(RESULTCODE$6);
            }
            target.set(resultCode);
        }
    }
    
    /**
     * Nils the "ResultCode" element
     */
    public void setNilResultCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(RESULTCODE$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(RESULTCODE$6);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "ResultCode" element
     */
    public void unsetResultCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(RESULTCODE$6, 0);
        }
    }
    
    /**
     * Gets the "ResultMessage" element
     */
    public java.lang.String getResultMessage()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(RESULTMESSAGE$8, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "ResultMessage" element
     */
    public org.apache.xmlbeans.XmlString xgetResultMessage()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(RESULTMESSAGE$8, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "ResultMessage" element
     */
    public boolean isNilResultMessage()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(RESULTMESSAGE$8, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "ResultMessage" element
     */
    public boolean isSetResultMessage()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(RESULTMESSAGE$8) != 0;
        }
    }
    
    /**
     * Sets the "ResultMessage" element
     */
    public void setResultMessage(java.lang.String resultMessage)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(RESULTMESSAGE$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(RESULTMESSAGE$8);
            }
            target.setStringValue(resultMessage);
        }
    }
    
    /**
     * Sets (as xml) the "ResultMessage" element
     */
    public void xsetResultMessage(org.apache.xmlbeans.XmlString resultMessage)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(RESULTMESSAGE$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(RESULTMESSAGE$8);
            }
            target.set(resultMessage);
        }
    }
    
    /**
     * Nils the "ResultMessage" element
     */
    public void setNilResultMessage()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(RESULTMESSAGE$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(RESULTMESSAGE$8);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "ResultMessage" element
     */
    public void unsetResultMessage()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(RESULTMESSAGE$8, 0);
        }
    }
}
