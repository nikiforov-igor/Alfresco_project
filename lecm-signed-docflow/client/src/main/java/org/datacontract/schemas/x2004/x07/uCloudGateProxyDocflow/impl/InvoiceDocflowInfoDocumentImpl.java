/*
 * An XML document type.
 * Localname: InvoiceDocflowInfo
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Docflow
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.InvoiceDocflowInfoDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.impl;
/**
 * A document containing one InvoiceDocflowInfo(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Docflow) element.
 *
 * This is a complex type.
 */
public class InvoiceDocflowInfoDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.InvoiceDocflowInfoDocument
{
    private static final long serialVersionUID = 1L;
    
    public InvoiceDocflowInfoDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName INVOICEDOCFLOWINFO$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Docflow", "InvoiceDocflowInfo");
    
    
    /**
     * Gets the "InvoiceDocflowInfo" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.InvoiceDocflowInfo getInvoiceDocflowInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.InvoiceDocflowInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.InvoiceDocflowInfo)get_store().find_element_user(INVOICEDOCFLOWINFO$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "InvoiceDocflowInfo" element
     */
    public boolean isNilInvoiceDocflowInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.InvoiceDocflowInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.InvoiceDocflowInfo)get_store().find_element_user(INVOICEDOCFLOWINFO$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "InvoiceDocflowInfo" element
     */
    public void setInvoiceDocflowInfo(org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.InvoiceDocflowInfo invoiceDocflowInfo)
    {
        generatedSetterHelperImpl(invoiceDocflowInfo, INVOICEDOCFLOWINFO$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "InvoiceDocflowInfo" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.InvoiceDocflowInfo addNewInvoiceDocflowInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.InvoiceDocflowInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.InvoiceDocflowInfo)get_store().add_element_user(INVOICEDOCFLOWINFO$0);
            return target;
        }
    }
    
    /**
     * Nils the "InvoiceDocflowInfo" element
     */
    public void setNilInvoiceDocflowInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.InvoiceDocflowInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.InvoiceDocflowInfo)get_store().find_element_user(INVOICEDOCFLOWINFO$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.InvoiceDocflowInfo)get_store().add_element_user(INVOICEDOCFLOWINFO$0);
            }
            target.setNil();
        }
    }
}
