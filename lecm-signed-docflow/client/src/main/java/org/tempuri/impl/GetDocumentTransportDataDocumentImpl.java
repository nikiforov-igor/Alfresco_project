/*
 * An XML document type.
 * Localname: GetDocumentTransportData
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.GetDocumentTransportDataDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one GetDocumentTransportData(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class GetDocumentTransportDataDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GetDocumentTransportDataDocument
{
    private static final long serialVersionUID = 1L;
    
    public GetDocumentTransportDataDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName GETDOCUMENTTRANSPORTDATA$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "GetDocumentTransportData");
    
    
    /**
     * Gets the "GetDocumentTransportData" element
     */
    public org.tempuri.GetDocumentTransportDataDocument.GetDocumentTransportData getGetDocumentTransportData()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GetDocumentTransportDataDocument.GetDocumentTransportData target = null;
            target = (org.tempuri.GetDocumentTransportDataDocument.GetDocumentTransportData)get_store().find_element_user(GETDOCUMENTTRANSPORTDATA$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "GetDocumentTransportData" element
     */
    public void setGetDocumentTransportData(org.tempuri.GetDocumentTransportDataDocument.GetDocumentTransportData getDocumentTransportData)
    {
        generatedSetterHelperImpl(getDocumentTransportData, GETDOCUMENTTRANSPORTDATA$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "GetDocumentTransportData" element
     */
    public org.tempuri.GetDocumentTransportDataDocument.GetDocumentTransportData addNewGetDocumentTransportData()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GetDocumentTransportDataDocument.GetDocumentTransportData target = null;
            target = (org.tempuri.GetDocumentTransportDataDocument.GetDocumentTransportData)get_store().add_element_user(GETDOCUMENTTRANSPORTDATA$0);
            return target;
        }
    }
    /**
     * An XML GetDocumentTransportData(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class GetDocumentTransportDataImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GetDocumentTransportDataDocument.GetDocumentTransportData
    {
        private static final long serialVersionUID = 1L;
        
        public GetDocumentTransportDataImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName SENDER$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "sender");
        private static final javax.xml.namespace.QName RECEIVER$2 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "receiver");
        private static final javax.xml.namespace.QName PREFFERABLEOPERATORCODE$4 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "prefferableOperatorCode");
        
        
        /**
         * Gets the "sender" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo getSender()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo)get_store().find_element_user(SENDER$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "sender" element
         */
        public boolean isNilSender()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo)get_store().find_element_user(SENDER$0, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "sender" element
         */
        public boolean isSetSender()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(SENDER$0) != 0;
            }
        }
        
        /**
         * Sets the "sender" element
         */
        public void setSender(org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo sender)
        {
            generatedSetterHelperImpl(sender, SENDER$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "sender" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo addNewSender()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo)get_store().add_element_user(SENDER$0);
                return target;
            }
        }
        
        /**
         * Nils the "sender" element
         */
        public void setNilSender()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo)get_store().find_element_user(SENDER$0, 0);
                if (target == null)
                {
                    target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo)get_store().add_element_user(SENDER$0);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "sender" element
         */
        public void unsetSender()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(SENDER$0, 0);
            }
        }
        
        /**
         * Gets the "receiver" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo getReceiver()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo)get_store().find_element_user(RECEIVER$2, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "receiver" element
         */
        public boolean isNilReceiver()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo)get_store().find_element_user(RECEIVER$2, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "receiver" element
         */
        public boolean isSetReceiver()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(RECEIVER$2) != 0;
            }
        }
        
        /**
         * Sets the "receiver" element
         */
        public void setReceiver(org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo receiver)
        {
            generatedSetterHelperImpl(receiver, RECEIVER$2, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "receiver" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo addNewReceiver()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo)get_store().add_element_user(RECEIVER$2);
                return target;
            }
        }
        
        /**
         * Nils the "receiver" element
         */
        public void setNilReceiver()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo)get_store().find_element_user(RECEIVER$2, 0);
                if (target == null)
                {
                    target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo)get_store().add_element_user(RECEIVER$2);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "receiver" element
         */
        public void unsetReceiver()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(RECEIVER$2, 0);
            }
        }
        
        /**
         * Gets the "prefferableOperatorCode" element
         */
        public java.lang.String getPrefferableOperatorCode()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PREFFERABLEOPERATORCODE$4, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "prefferableOperatorCode" element
         */
        public org.apache.xmlbeans.XmlString xgetPrefferableOperatorCode()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PREFFERABLEOPERATORCODE$4, 0);
                return target;
            }
        }
        
        /**
         * Tests for nil "prefferableOperatorCode" element
         */
        public boolean isNilPrefferableOperatorCode()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PREFFERABLEOPERATORCODE$4, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "prefferableOperatorCode" element
         */
        public boolean isSetPrefferableOperatorCode()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(PREFFERABLEOPERATORCODE$4) != 0;
            }
        }
        
        /**
         * Sets the "prefferableOperatorCode" element
         */
        public void setPrefferableOperatorCode(java.lang.String prefferableOperatorCode)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PREFFERABLEOPERATORCODE$4, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PREFFERABLEOPERATORCODE$4);
                }
                target.setStringValue(prefferableOperatorCode);
            }
        }
        
        /**
         * Sets (as xml) the "prefferableOperatorCode" element
         */
        public void xsetPrefferableOperatorCode(org.apache.xmlbeans.XmlString prefferableOperatorCode)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PREFFERABLEOPERATORCODE$4, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PREFFERABLEOPERATORCODE$4);
                }
                target.set(prefferableOperatorCode);
            }
        }
        
        /**
         * Nils the "prefferableOperatorCode" element
         */
        public void setNilPrefferableOperatorCode()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PREFFERABLEOPERATORCODE$4, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PREFFERABLEOPERATORCODE$4);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "prefferableOperatorCode" element
         */
        public void unsetPrefferableOperatorCode()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(PREFFERABLEOPERATORCODE$4, 0);
            }
        }
    }
}
