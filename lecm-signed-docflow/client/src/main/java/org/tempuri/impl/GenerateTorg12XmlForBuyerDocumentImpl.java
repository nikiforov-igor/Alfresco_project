/*
 * An XML document type.
 * Localname: GenerateTorg12XmlForBuyer
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.GenerateTorg12XmlForBuyerDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one GenerateTorg12XmlForBuyer(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class GenerateTorg12XmlForBuyerDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GenerateTorg12XmlForBuyerDocument
{
    private static final long serialVersionUID = 1L;
    
    public GenerateTorg12XmlForBuyerDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName GENERATETORG12XMLFORBUYER$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "GenerateTorg12XmlForBuyer");
    
    
    /**
     * Gets the "GenerateTorg12XmlForBuyer" element
     */
    public org.tempuri.GenerateTorg12XmlForBuyerDocument.GenerateTorg12XmlForBuyer getGenerateTorg12XmlForBuyer()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GenerateTorg12XmlForBuyerDocument.GenerateTorg12XmlForBuyer target = null;
            target = (org.tempuri.GenerateTorg12XmlForBuyerDocument.GenerateTorg12XmlForBuyer)get_store().find_element_user(GENERATETORG12XMLFORBUYER$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "GenerateTorg12XmlForBuyer" element
     */
    public void setGenerateTorg12XmlForBuyer(org.tempuri.GenerateTorg12XmlForBuyerDocument.GenerateTorg12XmlForBuyer generateTorg12XmlForBuyer)
    {
        generatedSetterHelperImpl(generateTorg12XmlForBuyer, GENERATETORG12XMLFORBUYER$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "GenerateTorg12XmlForBuyer" element
     */
    public org.tempuri.GenerateTorg12XmlForBuyerDocument.GenerateTorg12XmlForBuyer addNewGenerateTorg12XmlForBuyer()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GenerateTorg12XmlForBuyerDocument.GenerateTorg12XmlForBuyer target = null;
            target = (org.tempuri.GenerateTorg12XmlForBuyerDocument.GenerateTorg12XmlForBuyer)get_store().add_element_user(GENERATETORG12XMLFORBUYER$0);
            return target;
        }
    }
    /**
     * An XML GenerateTorg12XmlForBuyer(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class GenerateTorg12XmlForBuyerImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GenerateTorg12XmlForBuyerDocument.GenerateTorg12XmlForBuyer
    {
        private static final long serialVersionUID = 1L;
        
        public GenerateTorg12XmlForBuyerImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName VENDORID$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "vendorId");
        private static final javax.xml.namespace.QName BUYERID$2 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "buyerId");
        private static final javax.xml.namespace.QName OPINFO$4 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "opInfo");
        private static final javax.xml.namespace.QName DATE$6 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "date");
        private static final javax.xml.namespace.QName INFO$8 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "info");
        
        
        /**
         * Gets the "vendorId" element
         */
        public java.lang.String getVendorId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(VENDORID$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "vendorId" element
         */
        public org.apache.xmlbeans.XmlString xgetVendorId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(VENDORID$0, 0);
                return target;
            }
        }
        
        /**
         * Tests for nil "vendorId" element
         */
        public boolean isNilVendorId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(VENDORID$0, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "vendorId" element
         */
        public boolean isSetVendorId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(VENDORID$0) != 0;
            }
        }
        
        /**
         * Sets the "vendorId" element
         */
        public void setVendorId(java.lang.String vendorId)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(VENDORID$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(VENDORID$0);
                }
                target.setStringValue(vendorId);
            }
        }
        
        /**
         * Sets (as xml) the "vendorId" element
         */
        public void xsetVendorId(org.apache.xmlbeans.XmlString vendorId)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(VENDORID$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(VENDORID$0);
                }
                target.set(vendorId);
            }
        }
        
        /**
         * Nils the "vendorId" element
         */
        public void setNilVendorId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(VENDORID$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(VENDORID$0);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "vendorId" element
         */
        public void unsetVendorId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(VENDORID$0, 0);
            }
        }
        
        /**
         * Gets the "buyerId" element
         */
        public java.lang.String getBuyerId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(BUYERID$2, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "buyerId" element
         */
        public org.apache.xmlbeans.XmlString xgetBuyerId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(BUYERID$2, 0);
                return target;
            }
        }
        
        /**
         * Tests for nil "buyerId" element
         */
        public boolean isNilBuyerId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(BUYERID$2, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "buyerId" element
         */
        public boolean isSetBuyerId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(BUYERID$2) != 0;
            }
        }
        
        /**
         * Sets the "buyerId" element
         */
        public void setBuyerId(java.lang.String buyerId)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(BUYERID$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(BUYERID$2);
                }
                target.setStringValue(buyerId);
            }
        }
        
        /**
         * Sets (as xml) the "buyerId" element
         */
        public void xsetBuyerId(org.apache.xmlbeans.XmlString buyerId)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(BUYERID$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(BUYERID$2);
                }
                target.set(buyerId);
            }
        }
        
        /**
         * Nils the "buyerId" element
         */
        public void setNilBuyerId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(BUYERID$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(BUYERID$2);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "buyerId" element
         */
        public void unsetBuyerId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(BUYERID$2, 0);
            }
        }
        
        /**
         * Gets the "opInfo" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo getOpInfo()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo)get_store().find_element_user(OPINFO$4, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "opInfo" element
         */
        public boolean isNilOpInfo()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo)get_store().find_element_user(OPINFO$4, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "opInfo" element
         */
        public boolean isSetOpInfo()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(OPINFO$4) != 0;
            }
        }
        
        /**
         * Sets the "opInfo" element
         */
        public void setOpInfo(org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo opInfo)
        {
            generatedSetterHelperImpl(opInfo, OPINFO$4, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "opInfo" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo addNewOpInfo()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo)get_store().add_element_user(OPINFO$4);
                return target;
            }
        }
        
        /**
         * Nils the "opInfo" element
         */
        public void setNilOpInfo()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo)get_store().find_element_user(OPINFO$4, 0);
                if (target == null)
                {
                    target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo)get_store().add_element_user(OPINFO$4);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "opInfo" element
         */
        public void unsetOpInfo()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(OPINFO$4, 0);
            }
        }
        
        /**
         * Gets the "date" element
         */
        public java.util.Calendar getDate()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DATE$6, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getCalendarValue();
            }
        }
        
        /**
         * Gets (as xml) the "date" element
         */
        public org.apache.xmlbeans.XmlDateTime xgetDate()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlDateTime target = null;
                target = (org.apache.xmlbeans.XmlDateTime)get_store().find_element_user(DATE$6, 0);
                return target;
            }
        }
        
        /**
         * True if has "date" element
         */
        public boolean isSetDate()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(DATE$6) != 0;
            }
        }
        
        /**
         * Sets the "date" element
         */
        public void setDate(java.util.Calendar date)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DATE$6, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(DATE$6);
                }
                target.setCalendarValue(date);
            }
        }
        
        /**
         * Sets (as xml) the "date" element
         */
        public void xsetDate(org.apache.xmlbeans.XmlDateTime date)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlDateTime target = null;
                target = (org.apache.xmlbeans.XmlDateTime)get_store().find_element_user(DATE$6, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlDateTime)get_store().add_element_user(DATE$6);
                }
                target.set(date);
            }
        }
        
        /**
         * Unsets the "date" element
         */
        public void unsetDate()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(DATE$6, 0);
            }
        }
        
        /**
         * Gets the "info" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12BuyerTitleInfo getInfo()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12BuyerTitleInfo target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12BuyerTitleInfo)get_store().find_element_user(INFO$8, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "info" element
         */
        public boolean isNilInfo()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12BuyerTitleInfo target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12BuyerTitleInfo)get_store().find_element_user(INFO$8, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "info" element
         */
        public boolean isSetInfo()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(INFO$8) != 0;
            }
        }
        
        /**
         * Sets the "info" element
         */
        public void setInfo(org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12BuyerTitleInfo info)
        {
            generatedSetterHelperImpl(info, INFO$8, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "info" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12BuyerTitleInfo addNewInfo()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12BuyerTitleInfo target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12BuyerTitleInfo)get_store().add_element_user(INFO$8);
                return target;
            }
        }
        
        /**
         * Nils the "info" element
         */
        public void setNilInfo()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12BuyerTitleInfo target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12BuyerTitleInfo)get_store().find_element_user(INFO$8, 0);
                if (target == null)
                {
                    target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12BuyerTitleInfo)get_store().add_element_user(INFO$8);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "info" element
         */
        public void unsetInfo()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(INFO$8, 0);
            }
        }
    }
}
