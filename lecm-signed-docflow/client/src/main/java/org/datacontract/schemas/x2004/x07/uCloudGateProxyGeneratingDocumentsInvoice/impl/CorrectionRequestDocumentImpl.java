/*
 * An XML document type.
 * Localname: CorrectionRequest
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequestDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.impl;
/**
 * A document containing one CorrectionRequest(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice) element.
 *
 * This is a complex type.
 */
public class CorrectionRequestDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequestDocument
{
    private static final long serialVersionUID = 1L;
    
    public CorrectionRequestDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName CORRECTIONREQUEST$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "CorrectionRequest");
    
    
    /**
     * Gets the "CorrectionRequest" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest getCorrectionRequest()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest)get_store().find_element_user(CORRECTIONREQUEST$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "CorrectionRequest" element
     */
    public boolean isNilCorrectionRequest()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest)get_store().find_element_user(CORRECTIONREQUEST$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "CorrectionRequest" element
     */
    public void setCorrectionRequest(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest correctionRequest)
    {
        generatedSetterHelperImpl(correctionRequest, CORRECTIONREQUEST$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "CorrectionRequest" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest addNewCorrectionRequest()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest)get_store().add_element_user(CORRECTIONREQUEST$0);
            return target;
        }
    }
    
    /**
     * Nils the "CorrectionRequest" element
     */
    public void setNilCorrectionRequest()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest)get_store().find_element_user(CORRECTIONREQUEST$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest)get_store().add_element_user(CORRECTIONREQUEST$0);
            }
            target.setNil();
        }
    }
}
