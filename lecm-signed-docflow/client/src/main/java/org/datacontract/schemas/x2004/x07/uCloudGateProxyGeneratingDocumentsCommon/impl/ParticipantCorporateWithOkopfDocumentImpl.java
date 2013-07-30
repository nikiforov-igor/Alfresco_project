/*
 * An XML document type.
 * Localname: ParticipantCorporateWithOkopf
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantCorporateWithOkopfDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.impl;
/**
 * A document containing one ParticipantCorporateWithOkopf(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common) element.
 *
 * This is a complex type.
 */
public class ParticipantCorporateWithOkopfDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantCorporateWithOkopfDocument
{
    private static final long serialVersionUID = 1L;
    
    public ParticipantCorporateWithOkopfDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName PARTICIPANTCORPORATEWITHOKOPF$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common", "ParticipantCorporateWithOkopf");
    
    
    /**
     * Gets the "ParticipantCorporateWithOkopf" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantCorporateWithOkopf getParticipantCorporateWithOkopf()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantCorporateWithOkopf target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantCorporateWithOkopf)get_store().find_element_user(PARTICIPANTCORPORATEWITHOKOPF$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "ParticipantCorporateWithOkopf" element
     */
    public boolean isNilParticipantCorporateWithOkopf()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantCorporateWithOkopf target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantCorporateWithOkopf)get_store().find_element_user(PARTICIPANTCORPORATEWITHOKOPF$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "ParticipantCorporateWithOkopf" element
     */
    public void setParticipantCorporateWithOkopf(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantCorporateWithOkopf participantCorporateWithOkopf)
    {
        generatedSetterHelperImpl(participantCorporateWithOkopf, PARTICIPANTCORPORATEWITHOKOPF$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "ParticipantCorporateWithOkopf" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantCorporateWithOkopf addNewParticipantCorporateWithOkopf()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantCorporateWithOkopf target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantCorporateWithOkopf)get_store().add_element_user(PARTICIPANTCORPORATEWITHOKOPF$0);
            return target;
        }
    }
    
    /**
     * Nils the "ParticipantCorporateWithOkopf" element
     */
    public void setNilParticipantCorporateWithOkopf()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantCorporateWithOkopf target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantCorporateWithOkopf)get_store().find_element_user(PARTICIPANTCORPORATEWITHOKOPF$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantCorporateWithOkopf)get_store().add_element_user(PARTICIPANTCORPORATEWITHOKOPF$0);
            }
            target.setNil();
        }
    }
}
