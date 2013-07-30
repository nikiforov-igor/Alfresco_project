/*
 * An XML document type.
 * Localname: DocflowInfoBase
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBaseDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxy.impl;
/**
 * A document containing one DocflowInfoBase(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy) element.
 *
 * This is a complex type.
 */
public class DocflowInfoBaseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBaseDocument
{
    private static final long serialVersionUID = 1L;
    
    public DocflowInfoBaseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName DOCFLOWINFOBASE$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "DocflowInfoBase");
    
    
    /**
     * Gets the "DocflowInfoBase" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase getDocflowInfoBase()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase)get_store().find_element_user(DOCFLOWINFOBASE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "DocflowInfoBase" element
     */
    public boolean isNilDocflowInfoBase()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase)get_store().find_element_user(DOCFLOWINFOBASE$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "DocflowInfoBase" element
     */
    public void setDocflowInfoBase(org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase docflowInfoBase)
    {
        generatedSetterHelperImpl(docflowInfoBase, DOCFLOWINFOBASE$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "DocflowInfoBase" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase addNewDocflowInfoBase()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase)get_store().add_element_user(DOCFLOWINFOBASE$0);
            return target;
        }
    }
    
    /**
     * Nils the "DocflowInfoBase" element
     */
    public void setNilDocflowInfoBase()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase)get_store().find_element_user(DOCFLOWINFOBASE$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase)get_store().add_element_user(DOCFLOWINFOBASE$0);
            }
            target.setNil();
        }
    }
}
