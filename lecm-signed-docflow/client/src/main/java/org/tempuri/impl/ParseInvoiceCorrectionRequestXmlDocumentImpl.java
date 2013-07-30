/*
 * An XML document type.
 * Localname: ParseInvoiceCorrectionRequestXml
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.ParseInvoiceCorrectionRequestXmlDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one ParseInvoiceCorrectionRequestXml(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class ParseInvoiceCorrectionRequestXmlDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.ParseInvoiceCorrectionRequestXmlDocument
{
    private static final long serialVersionUID = 1L;
    
    public ParseInvoiceCorrectionRequestXmlDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName PARSEINVOICECORRECTIONREQUESTXML$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "ParseInvoiceCorrectionRequestXml");
    
    
    /**
     * Gets the "ParseInvoiceCorrectionRequestXml" element
     */
    public org.tempuri.ParseInvoiceCorrectionRequestXmlDocument.ParseInvoiceCorrectionRequestXml getParseInvoiceCorrectionRequestXml()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.ParseInvoiceCorrectionRequestXmlDocument.ParseInvoiceCorrectionRequestXml target = null;
            target = (org.tempuri.ParseInvoiceCorrectionRequestXmlDocument.ParseInvoiceCorrectionRequestXml)get_store().find_element_user(PARSEINVOICECORRECTIONREQUESTXML$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "ParseInvoiceCorrectionRequestXml" element
     */
    public void setParseInvoiceCorrectionRequestXml(org.tempuri.ParseInvoiceCorrectionRequestXmlDocument.ParseInvoiceCorrectionRequestXml parseInvoiceCorrectionRequestXml)
    {
        generatedSetterHelperImpl(parseInvoiceCorrectionRequestXml, PARSEINVOICECORRECTIONREQUESTXML$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "ParseInvoiceCorrectionRequestXml" element
     */
    public org.tempuri.ParseInvoiceCorrectionRequestXmlDocument.ParseInvoiceCorrectionRequestXml addNewParseInvoiceCorrectionRequestXml()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.ParseInvoiceCorrectionRequestXmlDocument.ParseInvoiceCorrectionRequestXml target = null;
            target = (org.tempuri.ParseInvoiceCorrectionRequestXmlDocument.ParseInvoiceCorrectionRequestXml)get_store().add_element_user(PARSEINVOICECORRECTIONREQUESTXML$0);
            return target;
        }
    }
    /**
     * An XML ParseInvoiceCorrectionRequestXml(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class ParseInvoiceCorrectionRequestXmlImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.ParseInvoiceCorrectionRequestXmlDocument.ParseInvoiceCorrectionRequestXml
    {
        private static final long serialVersionUID = 1L;
        
        public ParseInvoiceCorrectionRequestXmlImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName XML$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "xml");
        
        
        /**
         * Gets the "xml" element
         */
        public byte[] getXml()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(XML$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getByteArrayValue();
            }
        }
        
        /**
         * Gets (as xml) the "xml" element
         */
        public org.apache.xmlbeans.XmlBase64Binary xgetXml()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBase64Binary target = null;
                target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(XML$0, 0);
                return target;
            }
        }
        
        /**
         * Tests for nil "xml" element
         */
        public boolean isNilXml()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBase64Binary target = null;
                target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(XML$0, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "xml" element
         */
        public boolean isSetXml()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(XML$0) != 0;
            }
        }
        
        /**
         * Sets the "xml" element
         */
        public void setXml(byte[] xml)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(XML$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(XML$0);
                }
                target.setByteArrayValue(xml);
            }
        }
        
        /**
         * Sets (as xml) the "xml" element
         */
        public void xsetXml(org.apache.xmlbeans.XmlBase64Binary xml)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBase64Binary target = null;
                target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(XML$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlBase64Binary)get_store().add_element_user(XML$0);
                }
                target.set(xml);
            }
        }
        
        /**
         * Nils the "xml" element
         */
        public void setNilXml()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBase64Binary target = null;
                target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(XML$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlBase64Binary)get_store().add_element_user(XML$0);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "xml" element
         */
        public void unsetXml()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(XML$0, 0);
            }
        }
    }
}
