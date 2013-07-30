/*
 * An XML document type.
 * Localname: Official
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.OfficialDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.impl;
/**
 * A document containing one Official(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common) element.
 *
 * This is a complex type.
 */
public class OfficialDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.OfficialDocument
{
    private static final long serialVersionUID = 1L;
    
    public OfficialDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName OFFICIAL$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common", "Official");
    
    
    /**
     * Gets the "Official" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.Official getOfficial()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.Official target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.Official)get_store().find_element_user(OFFICIAL$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "Official" element
     */
    public boolean isNilOfficial()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.Official target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.Official)get_store().find_element_user(OFFICIAL$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "Official" element
     */
    public void setOfficial(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.Official official)
    {
        generatedSetterHelperImpl(official, OFFICIAL$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "Official" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.Official addNewOfficial()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.Official target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.Official)get_store().add_element_user(OFFICIAL$0);
            return target;
        }
    }
    
    /**
     * Nils the "Official" element
     */
    public void setNilOfficial()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.Official target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.Official)get_store().find_element_user(OFFICIAL$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.Official)get_store().add_element_user(OFFICIAL$0);
            }
            target.setNil();
        }
    }
}
