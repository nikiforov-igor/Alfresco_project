/*
 * An XML document type.
 * Localname: Address
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Common
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyCommon.AddressDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyCommon.impl;
/**
 * A document containing one Address(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Common) element.
 *
 * This is a complex type.
 */
public class AddressDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyCommon.AddressDocument
{
    private static final long serialVersionUID = 1L;
    
    public AddressDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName ADDRESS$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Common", "Address");
    
    
    /**
     * Gets the "Address" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyCommon.Address getAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyCommon.Address target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyCommon.Address)get_store().find_element_user(ADDRESS$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "Address" element
     */
    public boolean isNilAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyCommon.Address target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyCommon.Address)get_store().find_element_user(ADDRESS$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "Address" element
     */
    public void setAddress(org.datacontract.schemas.x2004.x07.uCloudGateProxyCommon.Address address)
    {
        generatedSetterHelperImpl(address, ADDRESS$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "Address" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyCommon.Address addNewAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyCommon.Address target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyCommon.Address)get_store().add_element_user(ADDRESS$0);
            return target;
        }
    }
    
    /**
     * Nils the "Address" element
     */
    public void setNilAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyCommon.Address target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyCommon.Address)get_store().find_element_user(ADDRESS$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyCommon.Address)get_store().add_element_user(ADDRESS$0);
            }
            target.setNil();
        }
    }
}
