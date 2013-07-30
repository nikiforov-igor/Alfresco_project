/*
 * An XML document type.
 * Localname: FioType
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.FioTypeDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.impl;
/**
 * A document containing one FioType(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common) element.
 *
 * This is a complex type.
 */
public class FioTypeDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.FioTypeDocument
{
    private static final long serialVersionUID = 1L;
    
    public FioTypeDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName FIOTYPE$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common", "FioType");
    
    
    /**
     * Gets the "FioType" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.FioType getFioType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.FioType target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.FioType)get_store().find_element_user(FIOTYPE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "FioType" element
     */
    public boolean isNilFioType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.FioType target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.FioType)get_store().find_element_user(FIOTYPE$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "FioType" element
     */
    public void setFioType(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.FioType fioType)
    {
        generatedSetterHelperImpl(fioType, FIOTYPE$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "FioType" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.FioType addNewFioType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.FioType target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.FioType)get_store().add_element_user(FIOTYPE$0);
            return target;
        }
    }
    
    /**
     * Nils the "FioType" element
     */
    public void setNilFioType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.FioType target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.FioType)get_store().find_element_user(FIOTYPE$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.FioType)get_store().add_element_user(FIOTYPE$0);
            }
            target.setNil();
        }
    }
}
