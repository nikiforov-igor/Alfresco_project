/*
 * An XML document type.
 * Localname: ParticipantBase
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBaseDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.impl;
/**
 * A document containing one ParticipantBase(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common) element.
 *
 * This is a complex type.
 */
public class ParticipantBaseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBaseDocument
{
    private static final long serialVersionUID = 1L;
    
    public ParticipantBaseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName PARTICIPANTBASE$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common", "ParticipantBase");
    
    
    /**
     * Gets the "ParticipantBase" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase getParticipantBase()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase)get_store().find_element_user(PARTICIPANTBASE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "ParticipantBase" element
     */
    public boolean isNilParticipantBase()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase)get_store().find_element_user(PARTICIPANTBASE$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "ParticipantBase" element
     */
    public void setParticipantBase(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase participantBase)
    {
        generatedSetterHelperImpl(participantBase, PARTICIPANTBASE$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "ParticipantBase" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase addNewParticipantBase()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase)get_store().add_element_user(PARTICIPANTBASE$0);
            return target;
        }
    }
    
    /**
     * Nils the "ParticipantBase" element
     */
    public void setNilParticipantBase()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase)get_store().find_element_user(PARTICIPANTBASE$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase)get_store().add_element_user(PARTICIPANTBASE$0);
            }
            target.setNil();
        }
    }
}
