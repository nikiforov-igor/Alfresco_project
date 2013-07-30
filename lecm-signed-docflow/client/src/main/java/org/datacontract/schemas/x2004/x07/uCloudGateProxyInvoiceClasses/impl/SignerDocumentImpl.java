/*
 * An XML document type.
 * Localname: Signer
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.SignerDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.impl;
/**
 * A document containing one Signer(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses) element.
 *
 * This is a complex type.
 */
public class SignerDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.SignerDocument
{
    private static final long serialVersionUID = 1L;
    
    public SignerDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName SIGNER$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "Signer");
    
    
    /**
     * Gets the "Signer" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Signer getSigner()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Signer target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Signer)get_store().find_element_user(SIGNER$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "Signer" element
     */
    public boolean isNilSigner()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Signer target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Signer)get_store().find_element_user(SIGNER$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "Signer" element
     */
    public void setSigner(org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Signer signer)
    {
        generatedSetterHelperImpl(signer, SIGNER$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "Signer" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Signer addNewSigner()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Signer target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Signer)get_store().add_element_user(SIGNER$0);
            return target;
        }
    }
    
    /**
     * Nils the "Signer" element
     */
    public void setNilSigner()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Signer target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Signer)get_store().find_element_user(SIGNER$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Signer)get_store().add_element_user(SIGNER$0);
            }
            target.setNil();
        }
    }
}
