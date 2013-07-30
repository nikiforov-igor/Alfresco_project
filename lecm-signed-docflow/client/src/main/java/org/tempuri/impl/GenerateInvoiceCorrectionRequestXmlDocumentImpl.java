/*
 * An XML document type.
 * Localname: GenerateInvoiceCorrectionRequestXml
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one GenerateInvoiceCorrectionRequestXml(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class GenerateInvoiceCorrectionRequestXmlDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument
{
    private static final long serialVersionUID = 1L;
    
    public GenerateInvoiceCorrectionRequestXmlDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName GENERATEINVOICECORRECTIONREQUESTXML$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "GenerateInvoiceCorrectionRequestXml");
    
    
    /**
     * Gets the "GenerateInvoiceCorrectionRequestXml" element
     */
    public org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument.GenerateInvoiceCorrectionRequestXml getGenerateInvoiceCorrectionRequestXml()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument.GenerateInvoiceCorrectionRequestXml target = null;
            target = (org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument.GenerateInvoiceCorrectionRequestXml)get_store().find_element_user(GENERATEINVOICECORRECTIONREQUESTXML$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "GenerateInvoiceCorrectionRequestXml" element
     */
    public void setGenerateInvoiceCorrectionRequestXml(org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument.GenerateInvoiceCorrectionRequestXml generateInvoiceCorrectionRequestXml)
    {
        generatedSetterHelperImpl(generateInvoiceCorrectionRequestXml, GENERATEINVOICECORRECTIONREQUESTXML$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "GenerateInvoiceCorrectionRequestXml" element
     */
    public org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument.GenerateInvoiceCorrectionRequestXml addNewGenerateInvoiceCorrectionRequestXml()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument.GenerateInvoiceCorrectionRequestXml target = null;
            target = (org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument.GenerateInvoiceCorrectionRequestXml)get_store().add_element_user(GENERATEINVOICECORRECTIONREQUESTXML$0);
            return target;
        }
    }
    /**
     * An XML GenerateInvoiceCorrectionRequestXml(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class GenerateInvoiceCorrectionRequestXmlImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument.GenerateInvoiceCorrectionRequestXml
    {
        private static final long serialVersionUID = 1L;
        
        public GenerateInvoiceCorrectionRequestXmlImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName CORRECTIONREQUEST$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "correctionRequest");
        
        
        /**
         * Gets the "correctionRequest" element
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
         * Tests for nil "correctionRequest" element
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
         * True if has "correctionRequest" element
         */
        public boolean isSetCorrectionRequest()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(CORRECTIONREQUEST$0) != 0;
            }
        }
        
        /**
         * Sets the "correctionRequest" element
         */
        public void setCorrectionRequest(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest correctionRequest)
        {
            generatedSetterHelperImpl(correctionRequest, CORRECTIONREQUEST$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "correctionRequest" element
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
         * Nils the "correctionRequest" element
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
        
        /**
         * Unsets the "correctionRequest" element
         */
        public void unsetCorrectionRequest()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(CORRECTIONREQUEST$0, 0);
            }
        }
    }
}
