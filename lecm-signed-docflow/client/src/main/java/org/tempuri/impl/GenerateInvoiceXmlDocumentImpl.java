/*
 * An XML document type.
 * Localname: GenerateInvoiceXml
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.GenerateInvoiceXmlDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one GenerateInvoiceXml(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class GenerateInvoiceXmlDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GenerateInvoiceXmlDocument
{
    private static final long serialVersionUID = 1L;
    
    public GenerateInvoiceXmlDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName GENERATEINVOICEXML$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "GenerateInvoiceXml");
    
    
    /**
     * Gets the "GenerateInvoiceXml" element
     */
    public org.tempuri.GenerateInvoiceXmlDocument.GenerateInvoiceXml getGenerateInvoiceXml()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GenerateInvoiceXmlDocument.GenerateInvoiceXml target = null;
            target = (org.tempuri.GenerateInvoiceXmlDocument.GenerateInvoiceXml)get_store().find_element_user(GENERATEINVOICEXML$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "GenerateInvoiceXml" element
     */
    public void setGenerateInvoiceXml(org.tempuri.GenerateInvoiceXmlDocument.GenerateInvoiceXml generateInvoiceXml)
    {
        generatedSetterHelperImpl(generateInvoiceXml, GENERATEINVOICEXML$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "GenerateInvoiceXml" element
     */
    public org.tempuri.GenerateInvoiceXmlDocument.GenerateInvoiceXml addNewGenerateInvoiceXml()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GenerateInvoiceXmlDocument.GenerateInvoiceXml target = null;
            target = (org.tempuri.GenerateInvoiceXmlDocument.GenerateInvoiceXml)get_store().add_element_user(GENERATEINVOICEXML$0);
            return target;
        }
    }
    /**
     * An XML GenerateInvoiceXml(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class GenerateInvoiceXmlImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GenerateInvoiceXmlDocument.GenerateInvoiceXml
    {
        private static final long serialVersionUID = 1L;
        
        public GenerateInvoiceXmlImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName INVOICE$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "invoice");
        private static final javax.xml.namespace.QName OPERATORCODE$2 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "operatorCode");
        
        
        /**
         * Gets the "invoice" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen getInvoice()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen)get_store().find_element_user(INVOICE$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "invoice" element
         */
        public boolean isNilInvoice()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen)get_store().find_element_user(INVOICE$0, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "invoice" element
         */
        public boolean isSetInvoice()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(INVOICE$0) != 0;
            }
        }
        
        /**
         * Sets the "invoice" element
         */
        public void setInvoice(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen invoice)
        {
            generatedSetterHelperImpl(invoice, INVOICE$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "invoice" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen addNewInvoice()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen)get_store().add_element_user(INVOICE$0);
                return target;
            }
        }
        
        /**
         * Nils the "invoice" element
         */
        public void setNilInvoice()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen)get_store().find_element_user(INVOICE$0, 0);
                if (target == null)
                {
                    target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen)get_store().add_element_user(INVOICE$0);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "invoice" element
         */
        public void unsetInvoice()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(INVOICE$0, 0);
            }
        }
        
        /**
         * Gets the "operatorCode" element
         */
        public java.lang.String getOperatorCode()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(OPERATORCODE$2, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "operatorCode" element
         */
        public org.apache.xmlbeans.XmlString xgetOperatorCode()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORCODE$2, 0);
                return target;
            }
        }
        
        /**
         * Tests for nil "operatorCode" element
         */
        public boolean isNilOperatorCode()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORCODE$2, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "operatorCode" element
         */
        public boolean isSetOperatorCode()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(OPERATORCODE$2) != 0;
            }
        }
        
        /**
         * Sets the "operatorCode" element
         */
        public void setOperatorCode(java.lang.String operatorCode)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(OPERATORCODE$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(OPERATORCODE$2);
                }
                target.setStringValue(operatorCode);
            }
        }
        
        /**
         * Sets (as xml) the "operatorCode" element
         */
        public void xsetOperatorCode(org.apache.xmlbeans.XmlString operatorCode)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORCODE$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(OPERATORCODE$2);
                }
                target.set(operatorCode);
            }
        }
        
        /**
         * Nils the "operatorCode" element
         */
        public void setNilOperatorCode()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORCODE$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(OPERATORCODE$2);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "operatorCode" element
         */
        public void unsetOperatorCode()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(OPERATORCODE$2, 0);
            }
        }
    }
}
