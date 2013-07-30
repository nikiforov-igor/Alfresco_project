/*
 * An XML document type.
 * Localname: Torg12BuyerTitleInfo
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12BuyerTitleInfoDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.impl;
/**
 * A document containing one Torg12BuyerTitleInfo(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses) element.
 *
 * This is a complex type.
 */
public class Torg12BuyerTitleInfoDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12BuyerTitleInfoDocument
{
    private static final long serialVersionUID = 1L;
    
    public Torg12BuyerTitleInfoDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName TORG12BUYERTITLEINFO$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "Torg12BuyerTitleInfo");
    
    
    /**
     * Gets the "Torg12BuyerTitleInfo" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12BuyerTitleInfo getTorg12BuyerTitleInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12BuyerTitleInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12BuyerTitleInfo)get_store().find_element_user(TORG12BUYERTITLEINFO$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "Torg12BuyerTitleInfo" element
     */
    public boolean isNilTorg12BuyerTitleInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12BuyerTitleInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12BuyerTitleInfo)get_store().find_element_user(TORG12BUYERTITLEINFO$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "Torg12BuyerTitleInfo" element
     */
    public void setTorg12BuyerTitleInfo(org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12BuyerTitleInfo torg12BuyerTitleInfo)
    {
        generatedSetterHelperImpl(torg12BuyerTitleInfo, TORG12BUYERTITLEINFO$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "Torg12BuyerTitleInfo" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12BuyerTitleInfo addNewTorg12BuyerTitleInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12BuyerTitleInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12BuyerTitleInfo)get_store().add_element_user(TORG12BUYERTITLEINFO$0);
            return target;
        }
    }
    
    /**
     * Nils the "Torg12BuyerTitleInfo" element
     */
    public void setNilTorg12BuyerTitleInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12BuyerTitleInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12BuyerTitleInfo)get_store().find_element_user(TORG12BUYERTITLEINFO$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12BuyerTitleInfo)get_store().add_element_user(TORG12BUYERTITLEINFO$0);
            }
            target.setNil();
        }
    }
}
