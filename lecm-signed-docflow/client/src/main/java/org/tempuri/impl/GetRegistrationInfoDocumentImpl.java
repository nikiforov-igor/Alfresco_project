/*
 * An XML document type.
 * Localname: GetRegistrationInfo
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.GetRegistrationInfoDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one GetRegistrationInfo(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class GetRegistrationInfoDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GetRegistrationInfoDocument
{
    private static final long serialVersionUID = 1L;
    
    public GetRegistrationInfoDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName GETREGISTRATIONINFO$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "GetRegistrationInfo");
    
    
    /**
     * Gets the "GetRegistrationInfo" element
     */
    public org.tempuri.GetRegistrationInfoDocument.GetRegistrationInfo getGetRegistrationInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GetRegistrationInfoDocument.GetRegistrationInfo target = null;
            target = (org.tempuri.GetRegistrationInfoDocument.GetRegistrationInfo)get_store().find_element_user(GETREGISTRATIONINFO$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "GetRegistrationInfo" element
     */
    public void setGetRegistrationInfo(org.tempuri.GetRegistrationInfoDocument.GetRegistrationInfo getRegistrationInfo)
    {
        generatedSetterHelperImpl(getRegistrationInfo, GETREGISTRATIONINFO$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "GetRegistrationInfo" element
     */
    public org.tempuri.GetRegistrationInfoDocument.GetRegistrationInfo addNewGetRegistrationInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GetRegistrationInfoDocument.GetRegistrationInfo target = null;
            target = (org.tempuri.GetRegistrationInfoDocument.GetRegistrationInfo)get_store().add_element_user(GETREGISTRATIONINFO$0);
            return target;
        }
    }
    /**
     * An XML GetRegistrationInfo(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class GetRegistrationInfoImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GetRegistrationInfoDocument.GetRegistrationInfo
    {
        private static final long serialVersionUID = 1L;
        
        public GetRegistrationInfoImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        
    }
}
