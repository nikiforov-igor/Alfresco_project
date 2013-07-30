/*
 * An XML document type.
 * Localname: GetServiceVersion
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.GetServiceVersionDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one GetServiceVersion(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class GetServiceVersionDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GetServiceVersionDocument
{
    private static final long serialVersionUID = 1L;
    
    public GetServiceVersionDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName GETSERVICEVERSION$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "GetServiceVersion");
    
    
    /**
     * Gets the "GetServiceVersion" element
     */
    public org.tempuri.GetServiceVersionDocument.GetServiceVersion getGetServiceVersion()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GetServiceVersionDocument.GetServiceVersion target = null;
            target = (org.tempuri.GetServiceVersionDocument.GetServiceVersion)get_store().find_element_user(GETSERVICEVERSION$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "GetServiceVersion" element
     */
    public void setGetServiceVersion(org.tempuri.GetServiceVersionDocument.GetServiceVersion getServiceVersion)
    {
        generatedSetterHelperImpl(getServiceVersion, GETSERVICEVERSION$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "GetServiceVersion" element
     */
    public org.tempuri.GetServiceVersionDocument.GetServiceVersion addNewGetServiceVersion()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GetServiceVersionDocument.GetServiceVersion target = null;
            target = (org.tempuri.GetServiceVersionDocument.GetServiceVersion)get_store().add_element_user(GETSERVICEVERSION$0);
            return target;
        }
    }
    /**
     * An XML GetServiceVersion(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class GetServiceVersionImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GetServiceVersionDocument.GetServiceVersion
    {
        private static final long serialVersionUID = 1L;
        
        public GetServiceVersionImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        
    }
}
