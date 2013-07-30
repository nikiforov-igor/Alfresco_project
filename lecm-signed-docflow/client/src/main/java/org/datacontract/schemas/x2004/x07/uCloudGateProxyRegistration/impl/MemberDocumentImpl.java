/*
 * An XML document type.
 * Localname: Member
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.MemberDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.impl;
/**
 * A document containing one Member(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration) element.
 *
 * This is a complex type.
 */
public class MemberDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.MemberDocument
{
    private static final long serialVersionUID = 1L;
    
    public MemberDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName MEMBER$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration", "Member");
    
    
    /**
     * Gets the "Member" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.Member getMember()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.Member target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.Member)get_store().find_element_user(MEMBER$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "Member" element
     */
    public boolean isNilMember()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.Member target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.Member)get_store().find_element_user(MEMBER$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "Member" element
     */
    public void setMember(org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.Member member)
    {
        generatedSetterHelperImpl(member, MEMBER$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "Member" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.Member addNewMember()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.Member target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.Member)get_store().add_element_user(MEMBER$0);
            return target;
        }
    }
    
    /**
     * Nils the "Member" element
     */
    public void setNilMember()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.Member target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.Member)get_store().find_element_user(MEMBER$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.Member)get_store().add_element_user(MEMBER$0);
            }
            target.setNil();
        }
    }
}
