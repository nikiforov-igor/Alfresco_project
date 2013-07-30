/*
 * An XML document type.
 * Localname: GetMutualOperators
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.GetMutualOperatorsDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one GetMutualOperators(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class GetMutualOperatorsDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GetMutualOperatorsDocument
{
    private static final long serialVersionUID = 1L;
    
    public GetMutualOperatorsDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName GETMUTUALOPERATORS$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "GetMutualOperators");
    
    
    /**
     * Gets the "GetMutualOperators" element
     */
    public org.tempuri.GetMutualOperatorsDocument.GetMutualOperators getGetMutualOperators()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GetMutualOperatorsDocument.GetMutualOperators target = null;
            target = (org.tempuri.GetMutualOperatorsDocument.GetMutualOperators)get_store().find_element_user(GETMUTUALOPERATORS$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "GetMutualOperators" element
     */
    public void setGetMutualOperators(org.tempuri.GetMutualOperatorsDocument.GetMutualOperators getMutualOperators)
    {
        generatedSetterHelperImpl(getMutualOperators, GETMUTUALOPERATORS$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "GetMutualOperators" element
     */
    public org.tempuri.GetMutualOperatorsDocument.GetMutualOperators addNewGetMutualOperators()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GetMutualOperatorsDocument.GetMutualOperators target = null;
            target = (org.tempuri.GetMutualOperatorsDocument.GetMutualOperators)get_store().add_element_user(GETMUTUALOPERATORS$0);
            return target;
        }
    }
    /**
     * An XML GetMutualOperators(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class GetMutualOperatorsImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GetMutualOperatorsDocument.GetMutualOperators
    {
        private static final long serialVersionUID = 1L;
        
        public GetMutualOperatorsImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName SENDER$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "sender");
        private static final javax.xml.namespace.QName RECEIVER$2 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "receiver");
        
        
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
    }
}
