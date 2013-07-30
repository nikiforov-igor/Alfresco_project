/*
 * An XML document type.
 * Localname: BilateralDocflowInfo
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Docflow
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.BilateralDocflowInfoDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.impl;
/**
 * A document containing one BilateralDocflowInfo(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Docflow) element.
 *
 * This is a complex type.
 */
public class BilateralDocflowInfoDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.BilateralDocflowInfoDocument
{
    private static final long serialVersionUID = 1L;
    
    public BilateralDocflowInfoDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName BILATERALDOCFLOWINFO$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Docflow", "BilateralDocflowInfo");
    
    
    /**
     * Gets the "BilateralDocflowInfo" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.BilateralDocflowInfo getBilateralDocflowInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.BilateralDocflowInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.BilateralDocflowInfo)get_store().find_element_user(BILATERALDOCFLOWINFO$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "BilateralDocflowInfo" element
     */
    public boolean isNilBilateralDocflowInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.BilateralDocflowInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.BilateralDocflowInfo)get_store().find_element_user(BILATERALDOCFLOWINFO$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "BilateralDocflowInfo" element
     */
    public void setBilateralDocflowInfo(org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.BilateralDocflowInfo bilateralDocflowInfo)
    {
        generatedSetterHelperImpl(bilateralDocflowInfo, BILATERALDOCFLOWINFO$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "BilateralDocflowInfo" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.BilateralDocflowInfo addNewBilateralDocflowInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.BilateralDocflowInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.BilateralDocflowInfo)get_store().add_element_user(BILATERALDOCFLOWINFO$0);
            return target;
        }
    }
    
    /**
     * Nils the "BilateralDocflowInfo" element
     */
    public void setNilBilateralDocflowInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.BilateralDocflowInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.BilateralDocflowInfo)get_store().find_element_user(BILATERALDOCFLOWINFO$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.BilateralDocflowInfo)get_store().add_element_user(BILATERALDOCFLOWINFO$0);
            }
            target.setNil();
        }
    }
}
