/*
 * XML Type:  ShipmentParticipant
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.impl;
/**
 * An XML ShipmentParticipant(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common).
 *
 * This is a complex type.
 */
public class ShipmentParticipantImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant
{
    private static final long serialVersionUID = 1L;
    
    public ShipmentParticipantImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName ISSAMEASPARTICIPANT$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common", "IsSameAsParticipant");
    private static final javax.xml.namespace.QName PARTICIPANTWITHADRESS$2 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common", "ParticipantWithAdress");
    
    
    /**
     * Gets the "IsSameAsParticipant" element
     */
    public boolean getIsSameAsParticipant()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ISSAMEASPARTICIPANT$0, 0);
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "IsSameAsParticipant" element
     */
    public org.apache.xmlbeans.XmlBoolean xgetIsSameAsParticipant()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(ISSAMEASPARTICIPANT$0, 0);
            return target;
        }
    }
    
    /**
     * True if has "IsSameAsParticipant" element
     */
    public boolean isSetIsSameAsParticipant()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(ISSAMEASPARTICIPANT$0) != 0;
        }
    }
    
    /**
     * Sets the "IsSameAsParticipant" element
     */
    public void setIsSameAsParticipant(boolean isSameAsParticipant)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ISSAMEASPARTICIPANT$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ISSAMEASPARTICIPANT$0);
            }
            target.setBooleanValue(isSameAsParticipant);
        }
    }
    
    /**
     * Sets (as xml) the "IsSameAsParticipant" element
     */
    public void xsetIsSameAsParticipant(org.apache.xmlbeans.XmlBoolean isSameAsParticipant)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(ISSAMEASPARTICIPANT$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_element_user(ISSAMEASPARTICIPANT$0);
            }
            target.set(isSameAsParticipant);
        }
    }
    
    /**
     * Unsets the "IsSameAsParticipant" element
     */
    public void unsetIsSameAsParticipant()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(ISSAMEASPARTICIPANT$0, 0);
        }
    }
    
    /**
     * Gets the "ParticipantWithAdress" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantWithAddress getParticipantWithAdress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantWithAddress target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantWithAddress)get_store().find_element_user(PARTICIPANTWITHADRESS$2, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "ParticipantWithAdress" element
     */
    public boolean isNilParticipantWithAdress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantWithAddress target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantWithAddress)get_store().find_element_user(PARTICIPANTWITHADRESS$2, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "ParticipantWithAdress" element
     */
    public boolean isSetParticipantWithAdress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(PARTICIPANTWITHADRESS$2) != 0;
        }
    }
    
    /**
     * Sets the "ParticipantWithAdress" element
     */
    public void setParticipantWithAdress(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantWithAddress participantWithAdress)
    {
        generatedSetterHelperImpl(participantWithAdress, PARTICIPANTWITHADRESS$2, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "ParticipantWithAdress" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantWithAddress addNewParticipantWithAdress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantWithAddress target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantWithAddress)get_store().add_element_user(PARTICIPANTWITHADRESS$2);
            return target;
        }
    }
    
    /**
     * Nils the "ParticipantWithAdress" element
     */
    public void setNilParticipantWithAdress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantWithAddress target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantWithAddress)get_store().find_element_user(PARTICIPANTWITHADRESS$2, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantWithAddress)get_store().add_element_user(PARTICIPANTWITHADRESS$2);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "ParticipantWithAdress" element
     */
    public void unsetParticipantWithAdress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(PARTICIPANTWITHADRESS$2, 0);
        }
    }
}
