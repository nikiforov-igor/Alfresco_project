/*
 * An XML document type.
 * Localname: RegisterResponse
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.impl;
/**
 * A document containing one RegisterResponse(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration) element.
 *
 * This is a complex type.
 */
public class RegisterResponseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponseDocument
{
    private static final long serialVersionUID = 1L;
    
    public RegisterResponseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName REGISTERRESPONSE$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration", "RegisterResponse");
    
    
    /**
     * Gets the "RegisterResponse" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse getRegisterResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse)get_store().find_element_user(REGISTERRESPONSE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "RegisterResponse" element
     */
    public boolean isNilRegisterResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse)get_store().find_element_user(REGISTERRESPONSE$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "RegisterResponse" element
     */
    public void setRegisterResponse(org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse registerResponse)
    {
        generatedSetterHelperImpl(registerResponse, REGISTERRESPONSE$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "RegisterResponse" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse addNewRegisterResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse)get_store().add_element_user(REGISTERRESPONSE$0);
            return target;
        }
    }
    
    /**
     * Nils the "RegisterResponse" element
     */
    public void setNilRegisterResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse)get_store().find_element_user(REGISTERRESPONSE$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse)get_store().add_element_user(REGISTERRESPONSE$0);
            }
            target.setNil();
        }
    }
}
