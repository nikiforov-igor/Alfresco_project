/*
 * An XML document type.
 * Localname: NonformalizedDocflowInfo
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Docflow
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.NonformalizedDocflowInfoDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.impl;
/**
 * A document containing one NonformalizedDocflowInfo(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Docflow) element.
 *
 * This is a complex type.
 */
public class NonformalizedDocflowInfoDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.NonformalizedDocflowInfoDocument
{
    private static final long serialVersionUID = 1L;
    
    public NonformalizedDocflowInfoDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName NONFORMALIZEDDOCFLOWINFO$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Docflow", "NonformalizedDocflowInfo");
    
    
    /**
     * Gets the "NonformalizedDocflowInfo" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.NonformalizedDocflowInfo getNonformalizedDocflowInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.NonformalizedDocflowInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.NonformalizedDocflowInfo)get_store().find_element_user(NONFORMALIZEDDOCFLOWINFO$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "NonformalizedDocflowInfo" element
     */
    public boolean isNilNonformalizedDocflowInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.NonformalizedDocflowInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.NonformalizedDocflowInfo)get_store().find_element_user(NONFORMALIZEDDOCFLOWINFO$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "NonformalizedDocflowInfo" element
     */
    public void setNonformalizedDocflowInfo(org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.NonformalizedDocflowInfo nonformalizedDocflowInfo)
    {
        generatedSetterHelperImpl(nonformalizedDocflowInfo, NONFORMALIZEDDOCFLOWINFO$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "NonformalizedDocflowInfo" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.NonformalizedDocflowInfo addNewNonformalizedDocflowInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.NonformalizedDocflowInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.NonformalizedDocflowInfo)get_store().add_element_user(NONFORMALIZEDDOCFLOWINFO$0);
            return target;
        }
    }
    
    /**
     * Nils the "NonformalizedDocflowInfo" element
     */
    public void setNilNonformalizedDocflowInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.NonformalizedDocflowInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.NonformalizedDocflowInfo)get_store().find_element_user(NONFORMALIZEDDOCFLOWINFO$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.NonformalizedDocflowInfo)get_store().add_element_user(NONFORMALIZEDDOCFLOWINFO$0);
            }
            target.setNil();
        }
    }
}
