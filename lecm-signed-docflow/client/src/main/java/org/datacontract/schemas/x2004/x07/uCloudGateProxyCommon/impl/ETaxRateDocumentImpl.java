/*
 * An XML document type.
 * Localname: ETaxRate
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Common
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyCommon.ETaxRateDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyCommon.impl;
/**
 * A document containing one ETaxRate(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Common) element.
 *
 * This is a complex type.
 */
public class ETaxRateDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyCommon.ETaxRateDocument
{
    private static final long serialVersionUID = 1L;
    
    public ETaxRateDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName ETAXRATE$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Common", "ETaxRate");
    
    
    /**
     * Gets the "ETaxRate" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyCommon.ETaxRate.Enum getETaxRate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ETAXRATE$0, 0);
            if (target == null)
            {
                return null;
            }
            return (org.datacontract.schemas.x2004.x07.uCloudGateProxyCommon.ETaxRate.Enum)target.getEnumValue();
        }
    }
    
    /**
     * Gets (as xml) the "ETaxRate" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyCommon.ETaxRate xgetETaxRate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyCommon.ETaxRate target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyCommon.ETaxRate)get_store().find_element_user(ETAXRATE$0, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "ETaxRate" element
     */
    public boolean isNilETaxRate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyCommon.ETaxRate target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyCommon.ETaxRate)get_store().find_element_user(ETAXRATE$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "ETaxRate" element
     */
    public void setETaxRate(org.datacontract.schemas.x2004.x07.uCloudGateProxyCommon.ETaxRate.Enum eTaxRate)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ETAXRATE$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ETAXRATE$0);
            }
            target.setEnumValue(eTaxRate);
        }
    }
    
    /**
     * Sets (as xml) the "ETaxRate" element
     */
    public void xsetETaxRate(org.datacontract.schemas.x2004.x07.uCloudGateProxyCommon.ETaxRate eTaxRate)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyCommon.ETaxRate target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyCommon.ETaxRate)get_store().find_element_user(ETAXRATE$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyCommon.ETaxRate)get_store().add_element_user(ETAXRATE$0);
            }
            target.set(eTaxRate);
        }
    }
    
    /**
     * Nils the "ETaxRate" element
     */
    public void setNilETaxRate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyCommon.ETaxRate target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyCommon.ETaxRate)get_store().find_element_user(ETAXRATE$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyCommon.ETaxRate)get_store().add_element_user(ETAXRATE$0);
            }
            target.setNil();
        }
    }
}
