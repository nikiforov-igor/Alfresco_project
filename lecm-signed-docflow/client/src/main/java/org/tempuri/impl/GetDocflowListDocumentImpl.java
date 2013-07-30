/*
 * An XML document type.
 * Localname: GetDocflowList
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.GetDocflowListDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one GetDocflowList(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class GetDocflowListDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GetDocflowListDocument
{
    private static final long serialVersionUID = 1L;
    
    public GetDocflowListDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName GETDOCFLOWLIST$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "GetDocflowList");
    
    
    /**
     * Gets the "GetDocflowList" element
     */
    public org.tempuri.GetDocflowListDocument.GetDocflowList getGetDocflowList()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GetDocflowListDocument.GetDocflowList target = null;
            target = (org.tempuri.GetDocflowListDocument.GetDocflowList)get_store().find_element_user(GETDOCFLOWLIST$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "GetDocflowList" element
     */
    public void setGetDocflowList(org.tempuri.GetDocflowListDocument.GetDocflowList getDocflowList)
    {
        generatedSetterHelperImpl(getDocflowList, GETDOCFLOWLIST$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "GetDocflowList" element
     */
    public org.tempuri.GetDocflowListDocument.GetDocflowList addNewGetDocflowList()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GetDocflowListDocument.GetDocflowList target = null;
            target = (org.tempuri.GetDocflowListDocument.GetDocflowList)get_store().add_element_user(GETDOCFLOWLIST$0);
            return target;
        }
    }
    /**
     * An XML GetDocflowList(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class GetDocflowListImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GetDocflowListDocument.GetDocflowList
    {
        private static final long serialVersionUID = 1L;
        
        public GetDocflowListImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName FILTER$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "filter");
        
        
        /**
         * Gets the "filter" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxy.WorkspaceFilter getFilter()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.WorkspaceFilter target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.WorkspaceFilter)get_store().find_element_user(FILTER$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "filter" element
         */
        public boolean isNilFilter()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.WorkspaceFilter target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.WorkspaceFilter)get_store().find_element_user(FILTER$0, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "filter" element
         */
        public boolean isSetFilter()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(FILTER$0) != 0;
            }
        }
        
        /**
         * Sets the "filter" element
         */
        public void setFilter(org.datacontract.schemas.x2004.x07.uCloudGateProxy.WorkspaceFilter filter)
        {
            generatedSetterHelperImpl(filter, FILTER$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "filter" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxy.WorkspaceFilter addNewFilter()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.WorkspaceFilter target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.WorkspaceFilter)get_store().add_element_user(FILTER$0);
                return target;
            }
        }
        
        /**
         * Nils the "filter" element
         */
        public void setNilFilter()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.WorkspaceFilter target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.WorkspaceFilter)get_store().find_element_user(FILTER$0, 0);
                if (target == null)
                {
                    target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.WorkspaceFilter)get_store().add_element_user(FILTER$0);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "filter" element
         */
        public void unsetFilter()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(FILTER$0, 0);
            }
        }
    }
}
