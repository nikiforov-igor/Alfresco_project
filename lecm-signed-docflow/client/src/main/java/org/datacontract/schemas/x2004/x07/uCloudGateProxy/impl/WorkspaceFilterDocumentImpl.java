/*
 * An XML document type.
 * Localname: WorkspaceFilter
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxy.WorkspaceFilterDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxy.impl;
/**
 * A document containing one WorkspaceFilter(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy) element.
 *
 * This is a complex type.
 */
public class WorkspaceFilterDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxy.WorkspaceFilterDocument
{
    private static final long serialVersionUID = 1L;
    
    public WorkspaceFilterDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName WORKSPACEFILTER$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "WorkspaceFilter");
    
    
    /**
     * Gets the "WorkspaceFilter" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.WorkspaceFilter getWorkspaceFilter()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.WorkspaceFilter target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.WorkspaceFilter)get_store().find_element_user(WORKSPACEFILTER$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "WorkspaceFilter" element
     */
    public boolean isNilWorkspaceFilter()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.WorkspaceFilter target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.WorkspaceFilter)get_store().find_element_user(WORKSPACEFILTER$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "WorkspaceFilter" element
     */
    public void setWorkspaceFilter(org.datacontract.schemas.x2004.x07.uCloudGateProxy.WorkspaceFilter workspaceFilter)
    {
        generatedSetterHelperImpl(workspaceFilter, WORKSPACEFILTER$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "WorkspaceFilter" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.WorkspaceFilter addNewWorkspaceFilter()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.WorkspaceFilter target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.WorkspaceFilter)get_store().add_element_user(WORKSPACEFILTER$0);
            return target;
        }
    }
    
    /**
     * Nils the "WorkspaceFilter" element
     */
    public void setNilWorkspaceFilter()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.WorkspaceFilter target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.WorkspaceFilter)get_store().find_element_user(WORKSPACEFILTER$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.WorkspaceFilter)get_store().add_element_user(WORKSPACEFILTER$0);
            }
            target.setNil();
        }
    }
}
