/*
 * XML Type:  ParticipantWithAddress
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantWithAddress
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.impl;
/**
 * An XML ParticipantWithAddress(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common).
 *
 * This is a complex type.
 */
public class ParticipantWithAddressImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantWithAddress
{
    private static final long serialVersionUID = 1L;
    
    public ParticipantWithAddressImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName ADDRESS$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common", "Address");
    private static final javax.xml.namespace.QName PARTICIPANT$2 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common", "Participant");
    
    
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
     * True if has "Address" element
     */
    public boolean isSetAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(ADDRESS$0) != 0;
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
    
    /**
     * Unsets the "Address" element
     */
    public void unsetAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(ADDRESS$0, 0);
        }
    }
    
    /**
     * Gets the "Participant" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase getParticipant()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase)get_store().find_element_user(PARTICIPANT$2, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "Participant" element
     */
    public boolean isNilParticipant()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase)get_store().find_element_user(PARTICIPANT$2, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Participant" element
     */
    public boolean isSetParticipant()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(PARTICIPANT$2) != 0;
        }
    }
    
    /**
     * Sets the "Participant" element
     */
    public void setParticipant(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase participant)
    {
        generatedSetterHelperImpl(participant, PARTICIPANT$2, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "Participant" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase addNewParticipant()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase)get_store().add_element_user(PARTICIPANT$2);
            return target;
        }
    }
    
    /**
     * Nils the "Participant" element
     */
    public void setNilParticipant()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase)get_store().find_element_user(PARTICIPANT$2, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase)get_store().add_element_user(PARTICIPANT$2);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Participant" element
     */
    public void unsetParticipant()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(PARTICIPANT$2, 0);
        }
    }
}
