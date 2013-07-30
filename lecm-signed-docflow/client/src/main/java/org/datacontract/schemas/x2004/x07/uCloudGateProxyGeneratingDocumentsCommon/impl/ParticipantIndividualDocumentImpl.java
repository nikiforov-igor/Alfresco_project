/*
 * An XML document type.
 * Localname: ParticipantIndividual
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantIndividualDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.impl;
/**
 * A document containing one ParticipantIndividual(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common) element.
 *
 * This is a complex type.
 */
public class ParticipantIndividualDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantIndividualDocument
{
    private static final long serialVersionUID = 1L;
    
    public ParticipantIndividualDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName PARTICIPANTINDIVIDUAL$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common", "ParticipantIndividual");
    
    
    /**
     * Gets the "ParticipantIndividual" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantIndividual getParticipantIndividual()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantIndividual target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantIndividual)get_store().find_element_user(PARTICIPANTINDIVIDUAL$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "ParticipantIndividual" element
     */
    public boolean isNilParticipantIndividual()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantIndividual target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantIndividual)get_store().find_element_user(PARTICIPANTINDIVIDUAL$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "ParticipantIndividual" element
     */
    public void setParticipantIndividual(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantIndividual participantIndividual)
    {
        generatedSetterHelperImpl(participantIndividual, PARTICIPANTINDIVIDUAL$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "ParticipantIndividual" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantIndividual addNewParticipantIndividual()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantIndividual target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantIndividual)get_store().add_element_user(PARTICIPANTINDIVIDUAL$0);
            return target;
        }
    }
    
    /**
     * Nils the "ParticipantIndividual" element
     */
    public void setNilParticipantIndividual()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantIndividual target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantIndividual)get_store().find_element_user(PARTICIPANTINDIVIDUAL$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantIndividual)get_store().add_element_user(PARTICIPANTINDIVIDUAL$0);
            }
            target.setNil();
        }
    }
}
