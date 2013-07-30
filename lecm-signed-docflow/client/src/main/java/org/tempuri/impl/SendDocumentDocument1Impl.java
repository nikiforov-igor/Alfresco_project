/*
 * An XML document type.
 * Localname: SendDocument
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.SendDocumentDocument1
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one SendDocument(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class SendDocumentDocument1Impl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.SendDocumentDocument1
{
    private static final long serialVersionUID = 1L;
    
    public SendDocumentDocument1Impl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName SENDDOCUMENT$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "SendDocument");
    
    
    /**
     * Gets the "SendDocument" element
     */
    public org.tempuri.SendDocumentDocument1.SendDocument getSendDocument()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.SendDocumentDocument1.SendDocument target = null;
            target = (org.tempuri.SendDocumentDocument1.SendDocument)get_store().find_element_user(SENDDOCUMENT$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "SendDocument" element
     */
    public void setSendDocument(org.tempuri.SendDocumentDocument1.SendDocument sendDocument)
    {
        generatedSetterHelperImpl(sendDocument, SENDDOCUMENT$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "SendDocument" element
     */
    public org.tempuri.SendDocumentDocument1.SendDocument addNewSendDocument()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.SendDocumentDocument1.SendDocument target = null;
            target = (org.tempuri.SendDocumentDocument1.SendDocument)get_store().add_element_user(SENDDOCUMENT$0);
            return target;
        }
    }
    /**
     * An XML SendDocument(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class SendDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.SendDocumentDocument1.SendDocument
    {
        private static final long serialVersionUID = 1L;
        
        public SendDocumentImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName DOC$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "doc");
        private static final javax.xml.namespace.QName OPERATORCODE$2 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "operatorCode");
        private static final javax.xml.namespace.QName BILLINGTICKET$4 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "billingTicket");
        
        
        /**
         * Gets the "doc" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend getDoc()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend)get_store().find_element_user(DOC$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "doc" element
         */
        public boolean isNilDoc()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend)get_store().find_element_user(DOC$0, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "doc" element
         */
        public boolean isSetDoc()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(DOC$0) != 0;
            }
        }
        
        /**
         * Sets the "doc" element
         */
        public void setDoc(org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend doc)
        {
            generatedSetterHelperImpl(doc, DOC$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "doc" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend addNewDoc()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend)get_store().add_element_user(DOC$0);
                return target;
            }
        }
        
        /**
         * Nils the "doc" element
         */
        public void setNilDoc()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend)get_store().find_element_user(DOC$0, 0);
                if (target == null)
                {
                    target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend)get_store().add_element_user(DOC$0);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "doc" element
         */
        public void unsetDoc()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(DOC$0, 0);
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
        
        /**
         * Gets the "billingTicket" element
         */
        public java.lang.String getBillingTicket()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(BILLINGTICKET$4, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "billingTicket" element
         */
        public org.apache.xmlbeans.XmlString xgetBillingTicket()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(BILLINGTICKET$4, 0);
                return target;
            }
        }
        
        /**
         * Tests for nil "billingTicket" element
         */
        public boolean isNilBillingTicket()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(BILLINGTICKET$4, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "billingTicket" element
         */
        public boolean isSetBillingTicket()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(BILLINGTICKET$4) != 0;
            }
        }
        
        /**
         * Sets the "billingTicket" element
         */
        public void setBillingTicket(java.lang.String billingTicket)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(BILLINGTICKET$4, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(BILLINGTICKET$4);
                }
                target.setStringValue(billingTicket);
            }
        }
        
        /**
         * Sets (as xml) the "billingTicket" element
         */
        public void xsetBillingTicket(org.apache.xmlbeans.XmlString billingTicket)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(BILLINGTICKET$4, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(BILLINGTICKET$4);
                }
                target.set(billingTicket);
            }
        }
        
        /**
         * Nils the "billingTicket" element
         */
        public void setNilBillingTicket()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(BILLINGTICKET$4, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(BILLINGTICKET$4);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "billingTicket" element
         */
        public void unsetBillingTicket()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(BILLINGTICKET$4, 0);
            }
        }
    }
}
