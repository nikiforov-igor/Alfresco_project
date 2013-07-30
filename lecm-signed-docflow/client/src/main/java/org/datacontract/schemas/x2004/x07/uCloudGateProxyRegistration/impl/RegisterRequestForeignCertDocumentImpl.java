/*
 * An XML document type.
 * Localname: RegisterRequestForeignCert
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCertDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.impl;
/**
 * A document containing one RegisterRequestForeignCert(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration) element.
 *
 * This is a complex type.
 */
public class RegisterRequestForeignCertDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCertDocument
{
    private static final long serialVersionUID = 1L;
    
    public RegisterRequestForeignCertDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName REGISTERREQUESTFOREIGNCERT$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration", "RegisterRequestForeignCert");
    
    
    /**
     * Gets the "RegisterRequestForeignCert" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert getRegisterRequestForeignCert()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert)get_store().find_element_user(REGISTERREQUESTFOREIGNCERT$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "RegisterRequestForeignCert" element
     */
    public boolean isNilRegisterRequestForeignCert()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert)get_store().find_element_user(REGISTERREQUESTFOREIGNCERT$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "RegisterRequestForeignCert" element
     */
    public void setRegisterRequestForeignCert(org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert registerRequestForeignCert)
    {
        generatedSetterHelperImpl(registerRequestForeignCert, REGISTERREQUESTFOREIGNCERT$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "RegisterRequestForeignCert" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert addNewRegisterRequestForeignCert()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert)get_store().add_element_user(REGISTERREQUESTFOREIGNCERT$0);
            return target;
        }
    }
    
    /**
     * Nils the "RegisterRequestForeignCert" element
     */
    public void setNilRegisterRequestForeignCert()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert)get_store().find_element_user(REGISTERREQUESTFOREIGNCERT$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert)get_store().add_element_user(REGISTERREQUESTFOREIGNCERT$0);
            }
            target.setNil();
        }
    }
}
