/*
 * An XML document type.
 * Localname: AddressForRegistration
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.AddressForRegistrationDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.impl;
/**
 * A document containing one AddressForRegistration(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration) element.
 *
 * This is a complex type.
 */
public class AddressForRegistrationDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.AddressForRegistrationDocument
{
    private static final long serialVersionUID = 1L;
    
    public AddressForRegistrationDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName ADDRESSFORREGISTRATION$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration", "AddressForRegistration");
    
    
    /**
     * Gets the "AddressForRegistration" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.AddressForRegistration getAddressForRegistration()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.AddressForRegistration target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.AddressForRegistration)get_store().find_element_user(ADDRESSFORREGISTRATION$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "AddressForRegistration" element
     */
    public boolean isNilAddressForRegistration()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.AddressForRegistration target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.AddressForRegistration)get_store().find_element_user(ADDRESSFORREGISTRATION$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "AddressForRegistration" element
     */
    public void setAddressForRegistration(org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.AddressForRegistration addressForRegistration)
    {
        generatedSetterHelperImpl(addressForRegistration, ADDRESSFORREGISTRATION$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "AddressForRegistration" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.AddressForRegistration addNewAddressForRegistration()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.AddressForRegistration target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.AddressForRegistration)get_store().add_element_user(ADDRESSFORREGISTRATION$0);
            return target;
        }
    }
    
    /**
     * Nils the "AddressForRegistration" element
     */
    public void setNilAddressForRegistration()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.AddressForRegistration target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.AddressForRegistration)get_store().find_element_user(ADDRESSFORREGISTRATION$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.AddressForRegistration)get_store().add_element_user(ADDRESSFORREGISTRATION$0);
            }
            target.setNil();
        }
    }
}
