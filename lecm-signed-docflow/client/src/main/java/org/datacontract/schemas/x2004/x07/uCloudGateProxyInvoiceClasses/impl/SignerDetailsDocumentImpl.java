/*
 * An XML document type.
 * Localname: SignerDetails
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.SignerDetailsDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.impl;
/**
 * A document containing one SignerDetails(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses) element.
 *
 * This is a complex type.
 */
public class SignerDetailsDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.SignerDetailsDocument
{
    private static final long serialVersionUID = 1L;
    
    public SignerDetailsDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName SIGNERDETAILS$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "SignerDetails");
    
    
    /**
     * Gets the "SignerDetails" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.SignerDetails getSignerDetails()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.SignerDetails target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.SignerDetails)get_store().find_element_user(SIGNERDETAILS$0, 0);
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
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.SignerDetails)get_store().find_element_user(SIGNERDETAILS$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "SignerDetails" element
     */
    public void setSignerDetails(org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.SignerDetails signerDetails)
    {
        generatedSetterHelperImpl(signerDetails, SIGNERDETAILS$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
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
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.SignerDetails)get_store().add_element_user(SIGNERDETAILS$0);
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
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.SignerDetails)get_store().find_element_user(SIGNERDETAILS$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.SignerDetails)get_store().add_element_user(SIGNERDETAILS$0);
            }
            target.setNil();
        }
    }
}
