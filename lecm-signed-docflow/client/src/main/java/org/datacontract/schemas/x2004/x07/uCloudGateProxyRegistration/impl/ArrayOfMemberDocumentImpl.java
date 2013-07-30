/*
 * An XML document type.
 * Localname: ArrayOfMember
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.ArrayOfMemberDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.impl;
/**
 * A document containing one ArrayOfMember(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration) element.
 *
 * This is a complex type.
 */
public class ArrayOfMemberDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.ArrayOfMemberDocument
{
    private static final long serialVersionUID = 1L;
    
    public ArrayOfMemberDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName ARRAYOFMEMBER$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration", "ArrayOfMember");
    
    
    /**
     * Gets the "ArrayOfMember" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.ArrayOfMember getArrayOfMember()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.ArrayOfMember target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.ArrayOfMember)get_store().find_element_user(ARRAYOFMEMBER$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "ArrayOfMember" element
     */
    public boolean isNilArrayOfMember()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.ArrayOfMember target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.ArrayOfMember)get_store().find_element_user(ARRAYOFMEMBER$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "ArrayOfMember" element
     */
    public void setArrayOfMember(org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.ArrayOfMember arrayOfMember)
    {
        generatedSetterHelperImpl(arrayOfMember, ARRAYOFMEMBER$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "ArrayOfMember" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.ArrayOfMember addNewArrayOfMember()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.ArrayOfMember target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.ArrayOfMember)get_store().add_element_user(ARRAYOFMEMBER$0);
            return target;
        }
    }
    
    /**
     * Nils the "ArrayOfMember" element
     */
    public void setNilArrayOfMember()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.ArrayOfMember target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.ArrayOfMember)get_store().find_element_user(ARRAYOFMEMBER$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.ArrayOfMember)get_store().add_element_user(ARRAYOFMEMBER$0);
            }
            target.setNil();
        }
    }
}
