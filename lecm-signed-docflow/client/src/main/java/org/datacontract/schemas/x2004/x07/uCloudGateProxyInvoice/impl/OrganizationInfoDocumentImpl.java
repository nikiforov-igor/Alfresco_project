/*
 * An XML document type.
 * Localname: OrganizationInfo
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Invoice
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfoDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.impl;
/**
 * A document containing one OrganizationInfo(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Invoice) element.
 *
 * This is a complex type.
 */
public class OrganizationInfoDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfoDocument
{
    private static final long serialVersionUID = 1L;
    
    public OrganizationInfoDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName ORGANIZATIONINFO$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Invoice", "OrganizationInfo");
    
    
    /**
     * Gets the "OrganizationInfo" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo getOrganizationInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo)get_store().find_element_user(ORGANIZATIONINFO$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "OrganizationInfo" element
     */
    public boolean isNilOrganizationInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo)get_store().find_element_user(ORGANIZATIONINFO$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "OrganizationInfo" element
     */
    public void setOrganizationInfo(org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo organizationInfo)
    {
        generatedSetterHelperImpl(organizationInfo, ORGANIZATIONINFO$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "OrganizationInfo" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo addNewOrganizationInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo)get_store().add_element_user(ORGANIZATIONINFO$0);
            return target;
        }
    }
    
    /**
     * Nils the "OrganizationInfo" element
     */
    public void setNilOrganizationInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo)get_store().find_element_user(ORGANIZATIONINFO$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo)get_store().add_element_user(ORGANIZATIONINFO$0);
            }
            target.setNil();
        }
    }
}
