/*
 * An XML document type.
 * Localname: ShipmentParticipant
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipantDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.impl;
/**
 * A document containing one ShipmentParticipant(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common) element.
 *
 * This is a complex type.
 */
public class ShipmentParticipantDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipantDocument
{
    private static final long serialVersionUID = 1L;
    
    public ShipmentParticipantDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName SHIPMENTPARTICIPANT$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common", "ShipmentParticipant");
    
    
    /**
     * Gets the "ShipmentParticipant" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant getShipmentParticipant()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant)get_store().find_element_user(SHIPMENTPARTICIPANT$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "ShipmentParticipant" element
     */
    public boolean isNilShipmentParticipant()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant)get_store().find_element_user(SHIPMENTPARTICIPANT$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "ShipmentParticipant" element
     */
    public void setShipmentParticipant(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant shipmentParticipant)
    {
        generatedSetterHelperImpl(shipmentParticipant, SHIPMENTPARTICIPANT$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "ShipmentParticipant" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant addNewShipmentParticipant()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant)get_store().add_element_user(SHIPMENTPARTICIPANT$0);
            return target;
        }
    }
    
    /**
     * Nils the "ShipmentParticipant" element
     */
    public void setNilShipmentParticipant()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant)get_store().find_element_user(SHIPMENTPARTICIPANT$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant)get_store().add_element_user(SHIPMENTPARTICIPANT$0);
            }
            target.setNil();
        }
    }
}
