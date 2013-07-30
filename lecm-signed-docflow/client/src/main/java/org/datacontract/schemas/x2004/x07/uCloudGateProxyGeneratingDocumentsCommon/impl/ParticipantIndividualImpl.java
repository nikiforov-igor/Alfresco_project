/*
 * XML Type:  ParticipantIndividual
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantIndividual
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.impl;
/**
 * An XML ParticipantIndividual(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common).
 *
 * This is a complex type.
 */
public class ParticipantIndividualImpl extends org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.impl.ParticipantBaseImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantIndividual
{
    private static final long serialVersionUID = 1L;
    
    public ParticipantIndividualImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName FIO$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common", "Fio");
    
    
    /**
     * Gets the "Fio" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.FioType getFio()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.FioType target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.FioType)get_store().find_element_user(FIO$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "Fio" element
     */
    public boolean isNilFio()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.FioType target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.FioType)get_store().find_element_user(FIO$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Fio" element
     */
    public boolean isSetFio()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(FIO$0) != 0;
        }
    }
    
    /**
     * Sets the "Fio" element
     */
    public void setFio(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.FioType fio)
    {
        generatedSetterHelperImpl(fio, FIO$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "Fio" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.FioType addNewFio()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.FioType target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.FioType)get_store().add_element_user(FIO$0);
            return target;
        }
    }
    
    /**
     * Nils the "Fio" element
     */
    public void setNilFio()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.FioType target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.FioType)get_store().find_element_user(FIO$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.FioType)get_store().add_element_user(FIO$0);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Fio" element
     */
    public void unsetFio()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(FIO$0, 0);
        }
    }
}
