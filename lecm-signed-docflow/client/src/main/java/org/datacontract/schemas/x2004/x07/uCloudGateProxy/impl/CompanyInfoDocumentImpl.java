/*
 * An XML document type.
 * Localname: CompanyInfo
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfoDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxy.impl;
/**
 * A document containing one CompanyInfo(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy) element.
 *
 * This is a complex type.
 */
public class CompanyInfoDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfoDocument
{
    private static final long serialVersionUID = 1L;
    
    public CompanyInfoDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName COMPANYINFO$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "CompanyInfo");
    
    
    /**
     * Gets the "CompanyInfo" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo getCompanyInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo)get_store().find_element_user(COMPANYINFO$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "CompanyInfo" element
     */
    public boolean isNilCompanyInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo)get_store().find_element_user(COMPANYINFO$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "CompanyInfo" element
     */
    public void setCompanyInfo(org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo companyInfo)
    {
        generatedSetterHelperImpl(companyInfo, COMPANYINFO$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "CompanyInfo" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo addNewCompanyInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo)get_store().add_element_user(COMPANYINFO$0);
            return target;
        }
    }
    
    /**
     * Nils the "CompanyInfo" element
     */
    public void setNilCompanyInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo)get_store().find_element_user(COMPANYINFO$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo)get_store().add_element_user(COMPANYINFO$0);
            }
            target.setNil();
        }
    }
}
