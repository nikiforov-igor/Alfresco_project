/*
 * An XML document type.
 * Localname: RegistrationInfo
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxy.RegistrationInfoDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxy.impl;
/**
 * A document containing one RegistrationInfo(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy) element.
 *
 * This is a complex type.
 */
public class RegistrationInfoDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxy.RegistrationInfoDocument
{
    private static final long serialVersionUID = 1L;
    
    public RegistrationInfoDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName REGISTRATIONINFO$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "RegistrationInfo");
    
    
    /**
     * Gets the "RegistrationInfo" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.RegistrationInfo getRegistrationInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.RegistrationInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.RegistrationInfo)get_store().find_element_user(REGISTRATIONINFO$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "RegistrationInfo" element
     */
    public boolean isNilRegistrationInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.RegistrationInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.RegistrationInfo)get_store().find_element_user(REGISTRATIONINFO$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "RegistrationInfo" element
     */
    public void setRegistrationInfo(org.datacontract.schemas.x2004.x07.uCloudGateProxy.RegistrationInfo registrationInfo)
    {
        generatedSetterHelperImpl(registrationInfo, REGISTRATIONINFO$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "RegistrationInfo" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.RegistrationInfo addNewRegistrationInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.RegistrationInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.RegistrationInfo)get_store().add_element_user(REGISTRATIONINFO$0);
            return target;
        }
    }
    
    /**
     * Nils the "RegistrationInfo" element
     */
    public void setNilRegistrationInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.RegistrationInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.RegistrationInfo)get_store().find_element_user(REGISTRATIONINFO$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.RegistrationInfo)get_store().add_element_user(REGISTRATIONINFO$0);
            }
            target.setNil();
        }
    }
}
