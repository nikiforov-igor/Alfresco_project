/*
 * An XML document type.
 * Localname: Torg12SellerTitleInfo
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfoDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.impl;
/**
 * A document containing one Torg12SellerTitleInfo(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses) element.
 *
 * This is a complex type.
 */
public class Torg12SellerTitleInfoDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfoDocument
{
    private static final long serialVersionUID = 1L;
    
    public Torg12SellerTitleInfoDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName TORG12SELLERTITLEINFO$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "Torg12SellerTitleInfo");
    
    
    /**
     * Gets the "Torg12SellerTitleInfo" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo getTorg12SellerTitleInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo)get_store().find_element_user(TORG12SELLERTITLEINFO$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "Torg12SellerTitleInfo" element
     */
    public boolean isNilTorg12SellerTitleInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo)get_store().find_element_user(TORG12SELLERTITLEINFO$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "Torg12SellerTitleInfo" element
     */
    public void setTorg12SellerTitleInfo(org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo torg12SellerTitleInfo)
    {
        generatedSetterHelperImpl(torg12SellerTitleInfo, TORG12SELLERTITLEINFO$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "Torg12SellerTitleInfo" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo addNewTorg12SellerTitleInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo)get_store().add_element_user(TORG12SELLERTITLEINFO$0);
            return target;
        }
    }
    
    /**
     * Nils the "Torg12SellerTitleInfo" element
     */
    public void setNilTorg12SellerTitleInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo)get_store().find_element_user(TORG12SELLERTITLEINFO$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo)get_store().add_element_user(TORG12SELLERTITLEINFO$0);
            }
            target.setNil();
        }
    }
}
