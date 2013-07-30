/*
 * XML Type:  Signer
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Signer
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.impl;
/**
 * An XML Signer(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses).
 *
 * This is a complex type.
 */
public class SignerImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Signer
{
    private static final long serialVersionUID = 1L;
    
    public SignerImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName SIGNERCERTIFICATE$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "SignerCertificate");
    private static final javax.xml.namespace.QName SIGNERDETAILS$2 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "SignerDetails");
    
    
    /**
     * Gets the "SignerCertificate" element
     */
    public byte[] getSignerCertificate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SIGNERCERTIFICATE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getByteArrayValue();
        }
    }
    
    /**
     * Gets (as xml) the "SignerCertificate" element
     */
    public org.apache.xmlbeans.XmlBase64Binary xgetSignerCertificate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBase64Binary target = null;
            target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(SIGNERCERTIFICATE$0, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "SignerCertificate" element
     */
    public boolean isNilSignerCertificate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBase64Binary target = null;
            target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(SIGNERCERTIFICATE$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "SignerCertificate" element
     */
    public boolean isSetSignerCertificate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(SIGNERCERTIFICATE$0) != 0;
        }
    }
    
    /**
     * Sets the "SignerCertificate" element
     */
    public void setSignerCertificate(byte[] signerCertificate)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SIGNERCERTIFICATE$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(SIGNERCERTIFICATE$0);
            }
            target.setByteArrayValue(signerCertificate);
        }
    }
    
    /**
     * Sets (as xml) the "SignerCertificate" element
     */
    public void xsetSignerCertificate(org.apache.xmlbeans.XmlBase64Binary signerCertificate)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBase64Binary target = null;
            target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(SIGNERCERTIFICATE$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBase64Binary)get_store().add_element_user(SIGNERCERTIFICATE$0);
            }
            target.set(signerCertificate);
        }
    }
    
    /**
     * Nils the "SignerCertificate" element
     */
    public void setNilSignerCertificate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBase64Binary target = null;
            target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(SIGNERCERTIFICATE$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBase64Binary)get_store().add_element_user(SIGNERCERTIFICATE$0);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "SignerCertificate" element
     */
    public void unsetSignerCertificate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(SIGNERCERTIFICATE$0, 0);
        }
    }
    
    /**
     * Gets the "SignerDetails" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.SignerDetails getSignerDetails()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.SignerDetails target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.SignerDetails)get_store().find_element_user(SIGNERDETAILS$2, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "SignerDetails" element
     */
    public boolean isNilSignerDetails()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.SignerDetails target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.SignerDetails)get_store().find_element_user(SIGNERDETAILS$2, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "SignerDetails" element
     */
    public boolean isSetSignerDetails()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(SIGNERDETAILS$2) != 0;
        }
    }
    
    /**
     * Sets the "SignerDetails" element
     */
    public void setSignerDetails(org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.SignerDetails signerDetails)
    {
        generatedSetterHelperImpl(signerDetails, SIGNERDETAILS$2, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "SignerDetails" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.SignerDetails addNewSignerDetails()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.SignerDetails target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.SignerDetails)get_store().add_element_user(SIGNERDETAILS$2);
            return target;
        }
    }
    
    /**
     * Nils the "SignerDetails" element
     */
    public void setNilSignerDetails()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.SignerDetails target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.SignerDetails)get_store().find_element_user(SIGNERDETAILS$2, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.SignerDetails)get_store().add_element_user(SIGNERDETAILS$2);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "SignerDetails" element
     */
    public void unsetSignerDetails()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(SIGNERDETAILS$2, 0);
        }
    }
}
