/*
 * XML Type:  Torg12SellerTitleInfo
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.impl;
/**
 * An XML Torg12SellerTitleInfo(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses).
 *
 * This is a complex type.
 */
public class Torg12SellerTitleInfoImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo
{
    private static final long serialVersionUID = 1L;
    
    public Torg12SellerTitleInfoImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName ADDITIONALINFO$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "AdditionalInfo");
    private static final javax.xml.namespace.QName ATTACHMENTSHEETSQUANTITY$2 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "AttachmentSheetsQuantity");
    private static final javax.xml.namespace.QName BUYERDOCFLOWPARTICIPANT$4 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "BuyerDocflowParticipant");
    private static final javax.xml.namespace.QName CHIEFACCOUNTANT$6 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "ChiefAccountant");
    private static final javax.xml.namespace.QName CONSIGNEE$8 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "Consignee");
    private static final javax.xml.namespace.QName DOCUMENTDATE$10 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "DocumentDate");
    private static final javax.xml.namespace.QName DOCUMENTNUMBER$12 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "DocumentNumber");
    private static final javax.xml.namespace.QName GROSSQUANTITYTOTAL$14 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "GrossQuantityTotal");
    private static final javax.xml.namespace.QName GROSSQUANTITYTOTALINWORDS$16 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "GrossQuantityTotalInWords");
    private static final javax.xml.namespace.QName GROUNDS$18 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "Grounds");
    private static final javax.xml.namespace.QName ITEMS$20 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "Items");
    private static final javax.xml.namespace.QName NETQUANTITYTOTAL$22 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "NetQuantityTotal");
    private static final javax.xml.namespace.QName NETQUANTITYTOTALINWORDS$24 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "NetQuantityTotalInWords");
    private static final javax.xml.namespace.QName OPERATIONCODE$26 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "OperationCode");
    private static final javax.xml.namespace.QName PARCELSQUANTITYTOTAL$28 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "ParcelsQuantityTotal");
    private static final javax.xml.namespace.QName PARCELSQUANTITYTOTALINWORDS$30 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "ParcelsQuantityTotalInWords");
    private static final javax.xml.namespace.QName PAYER$32 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "Payer");
    private static final javax.xml.namespace.QName QUANTITYTOTAL$34 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "QuantityTotal");
    private static final javax.xml.namespace.QName SELLERDOCFLOWPARTICIPANT$36 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "SellerDocflowParticipant");
    private static final javax.xml.namespace.QName SHIPPER$38 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "Shipper");
    private static final javax.xml.namespace.QName SIGNER$40 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "Signer");
    private static final javax.xml.namespace.QName SUPPLIER$42 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "Supplier");
    private static final javax.xml.namespace.QName SUPPLYALLOWEDBY$44 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "SupplyAllowedBy");
    private static final javax.xml.namespace.QName SUPPLYDATE$46 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "SupplyDate");
    private static final javax.xml.namespace.QName SUPPLYPERFORMEDBY$48 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "SupplyPerformedBy");
    private static final javax.xml.namespace.QName TOTAL$50 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "Total");
    private static final javax.xml.namespace.QName TOTALINWORDS$52 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "TotalInWords");
    private static final javax.xml.namespace.QName TOTALWITHVATEXCLUDED$54 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "TotalWithVatExcluded");
    private static final javax.xml.namespace.QName VAT$56 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "Vat");
    private static final javax.xml.namespace.QName WAYBILLDATE$58 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "WaybillDate");
    private static final javax.xml.namespace.QName WAYBILLNUMBER$60 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "WaybillNumber");
    
    
    /**
     * Gets the "AdditionalInfo" element
     */
    public java.lang.String getAdditionalInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ADDITIONALINFO$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "AdditionalInfo" element
     */
    public org.apache.xmlbeans.XmlString xgetAdditionalInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ADDITIONALINFO$0, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "AdditionalInfo" element
     */
    public boolean isNilAdditionalInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ADDITIONALINFO$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "AdditionalInfo" element
     */
    public boolean isSetAdditionalInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(ADDITIONALINFO$0) != 0;
        }
    }
    
    /**
     * Sets the "AdditionalInfo" element
     */
    public void setAdditionalInfo(java.lang.String additionalInfo)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ADDITIONALINFO$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ADDITIONALINFO$0);
            }
            target.setStringValue(additionalInfo);
        }
    }
    
    /**
     * Sets (as xml) the "AdditionalInfo" element
     */
    public void xsetAdditionalInfo(org.apache.xmlbeans.XmlString additionalInfo)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ADDITIONALINFO$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(ADDITIONALINFO$0);
            }
            target.set(additionalInfo);
        }
    }
    
    /**
     * Nils the "AdditionalInfo" element
     */
    public void setNilAdditionalInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ADDITIONALINFO$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(ADDITIONALINFO$0);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "AdditionalInfo" element
     */
    public void unsetAdditionalInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(ADDITIONALINFO$0, 0);
        }
    }
    
    /**
     * Gets the "AttachmentSheetsQuantity" element
     */
    public java.lang.String getAttachmentSheetsQuantity()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ATTACHMENTSHEETSQUANTITY$2, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "AttachmentSheetsQuantity" element
     */
    public org.apache.xmlbeans.XmlString xgetAttachmentSheetsQuantity()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ATTACHMENTSHEETSQUANTITY$2, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "AttachmentSheetsQuantity" element
     */
    public boolean isNilAttachmentSheetsQuantity()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ATTACHMENTSHEETSQUANTITY$2, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "AttachmentSheetsQuantity" element
     */
    public boolean isSetAttachmentSheetsQuantity()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(ATTACHMENTSHEETSQUANTITY$2) != 0;
        }
    }
    
    /**
     * Sets the "AttachmentSheetsQuantity" element
     */
    public void setAttachmentSheetsQuantity(java.lang.String attachmentSheetsQuantity)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ATTACHMENTSHEETSQUANTITY$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ATTACHMENTSHEETSQUANTITY$2);
            }
            target.setStringValue(attachmentSheetsQuantity);
        }
    }
    
    /**
     * Sets (as xml) the "AttachmentSheetsQuantity" element
     */
    public void xsetAttachmentSheetsQuantity(org.apache.xmlbeans.XmlString attachmentSheetsQuantity)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ATTACHMENTSHEETSQUANTITY$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(ATTACHMENTSHEETSQUANTITY$2);
            }
            target.set(attachmentSheetsQuantity);
        }
    }
    
    /**
     * Nils the "AttachmentSheetsQuantity" element
     */
    public void setNilAttachmentSheetsQuantity()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ATTACHMENTSHEETSQUANTITY$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(ATTACHMENTSHEETSQUANTITY$2);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "AttachmentSheetsQuantity" element
     */
    public void unsetAttachmentSheetsQuantity()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(ATTACHMENTSHEETSQUANTITY$2, 0);
        }
    }
    
    /**
     * Gets the "BuyerDocflowParticipant" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo getBuyerDocflowParticipant()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo)get_store().find_element_user(BUYERDOCFLOWPARTICIPANT$4, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "BuyerDocflowParticipant" element
     */
    public boolean isNilBuyerDocflowParticipant()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo)get_store().find_element_user(BUYERDOCFLOWPARTICIPANT$4, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "BuyerDocflowParticipant" element
     */
    public boolean isSetBuyerDocflowParticipant()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(BUYERDOCFLOWPARTICIPANT$4) != 0;
        }
    }
    
    /**
     * Sets the "BuyerDocflowParticipant" element
     */
    public void setBuyerDocflowParticipant(org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo buyerDocflowParticipant)
    {
        generatedSetterHelperImpl(buyerDocflowParticipant, BUYERDOCFLOWPARTICIPANT$4, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "BuyerDocflowParticipant" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo addNewBuyerDocflowParticipant()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo)get_store().add_element_user(BUYERDOCFLOWPARTICIPANT$4);
            return target;
        }
    }
    
    /**
     * Nils the "BuyerDocflowParticipant" element
     */
    public void setNilBuyerDocflowParticipant()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo)get_store().find_element_user(BUYERDOCFLOWPARTICIPANT$4, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo)get_store().add_element_user(BUYERDOCFLOWPARTICIPANT$4);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "BuyerDocflowParticipant" element
     */
    public void unsetBuyerDocflowParticipant()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(BUYERDOCFLOWPARTICIPANT$4, 0);
        }
    }
    
    /**
     * Gets the "ChiefAccountant" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official getChiefAccountant()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official)get_store().find_element_user(CHIEFACCOUNTANT$6, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "ChiefAccountant" element
     */
    public boolean isNilChiefAccountant()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official)get_store().find_element_user(CHIEFACCOUNTANT$6, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "ChiefAccountant" element
     */
    public boolean isSetChiefAccountant()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(CHIEFACCOUNTANT$6) != 0;
        }
    }
    
    /**
     * Sets the "ChiefAccountant" element
     */
    public void setChiefAccountant(org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official chiefAccountant)
    {
        generatedSetterHelperImpl(chiefAccountant, CHIEFACCOUNTANT$6, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "ChiefAccountant" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official addNewChiefAccountant()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official)get_store().add_element_user(CHIEFACCOUNTANT$6);
            return target;
        }
    }
    
    /**
     * Nils the "ChiefAccountant" element
     */
    public void setNilChiefAccountant()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official)get_store().find_element_user(CHIEFACCOUNTANT$6, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official)get_store().add_element_user(CHIEFACCOUNTANT$6);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "ChiefAccountant" element
     */
    public void unsetChiefAccountant()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(CHIEFACCOUNTANT$6, 0);
        }
    }
    
    /**
     * Gets the "Consignee" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo getConsignee()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo)get_store().find_element_user(CONSIGNEE$8, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "Consignee" element
     */
    public boolean isNilConsignee()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo)get_store().find_element_user(CONSIGNEE$8, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Consignee" element
     */
    public boolean isSetConsignee()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(CONSIGNEE$8) != 0;
        }
    }
    
    /**
     * Sets the "Consignee" element
     */
    public void setConsignee(org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo consignee)
    {
        generatedSetterHelperImpl(consignee, CONSIGNEE$8, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "Consignee" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo addNewConsignee()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo)get_store().add_element_user(CONSIGNEE$8);
            return target;
        }
    }
    
    /**
     * Nils the "Consignee" element
     */
    public void setNilConsignee()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo)get_store().find_element_user(CONSIGNEE$8, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo)get_store().add_element_user(CONSIGNEE$8);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Consignee" element
     */
    public void unsetConsignee()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(CONSIGNEE$8, 0);
        }
    }
    
    /**
     * Gets the "DocumentDate" element
     */
    public java.lang.String getDocumentDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DOCUMENTDATE$10, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "DocumentDate" element
     */
    public org.apache.xmlbeans.XmlString xgetDocumentDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DOCUMENTDATE$10, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "DocumentDate" element
     */
    public boolean isNilDocumentDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DOCUMENTDATE$10, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "DocumentDate" element
     */
    public boolean isSetDocumentDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(DOCUMENTDATE$10) != 0;
        }
    }
    
    /**
     * Sets the "DocumentDate" element
     */
    public void setDocumentDate(java.lang.String documentDate)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DOCUMENTDATE$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(DOCUMENTDATE$10);
            }
            target.setStringValue(documentDate);
        }
    }
    
    /**
     * Sets (as xml) the "DocumentDate" element
     */
    public void xsetDocumentDate(org.apache.xmlbeans.XmlString documentDate)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DOCUMENTDATE$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(DOCUMENTDATE$10);
            }
            target.set(documentDate);
        }
    }
    
    /**
     * Nils the "DocumentDate" element
     */
    public void setNilDocumentDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DOCUMENTDATE$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(DOCUMENTDATE$10);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "DocumentDate" element
     */
    public void unsetDocumentDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(DOCUMENTDATE$10, 0);
        }
    }
    
    /**
     * Gets the "DocumentNumber" element
     */
    public java.lang.String getDocumentNumber()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DOCUMENTNUMBER$12, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "DocumentNumber" element
     */
    public org.apache.xmlbeans.XmlString xgetDocumentNumber()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DOCUMENTNUMBER$12, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "DocumentNumber" element
     */
    public boolean isNilDocumentNumber()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DOCUMENTNUMBER$12, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "DocumentNumber" element
     */
    public boolean isSetDocumentNumber()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(DOCUMENTNUMBER$12) != 0;
        }
    }
    
    /**
     * Sets the "DocumentNumber" element
     */
    public void setDocumentNumber(java.lang.String documentNumber)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DOCUMENTNUMBER$12, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(DOCUMENTNUMBER$12);
            }
            target.setStringValue(documentNumber);
        }
    }
    
    /**
     * Sets (as xml) the "DocumentNumber" element
     */
    public void xsetDocumentNumber(org.apache.xmlbeans.XmlString documentNumber)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DOCUMENTNUMBER$12, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(DOCUMENTNUMBER$12);
            }
            target.set(documentNumber);
        }
    }
    
    /**
     * Nils the "DocumentNumber" element
     */
    public void setNilDocumentNumber()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DOCUMENTNUMBER$12, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(DOCUMENTNUMBER$12);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "DocumentNumber" element
     */
    public void unsetDocumentNumber()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(DOCUMENTNUMBER$12, 0);
        }
    }
    
    /**
     * Gets the "GrossQuantityTotal" element
     */
    public java.lang.String getGrossQuantityTotal()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(GROSSQUANTITYTOTAL$14, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "GrossQuantityTotal" element
     */
    public org.apache.xmlbeans.XmlString xgetGrossQuantityTotal()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(GROSSQUANTITYTOTAL$14, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "GrossQuantityTotal" element
     */
    public boolean isNilGrossQuantityTotal()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(GROSSQUANTITYTOTAL$14, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "GrossQuantityTotal" element
     */
    public boolean isSetGrossQuantityTotal()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(GROSSQUANTITYTOTAL$14) != 0;
        }
    }
    
    /**
     * Sets the "GrossQuantityTotal" element
     */
    public void setGrossQuantityTotal(java.lang.String grossQuantityTotal)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(GROSSQUANTITYTOTAL$14, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(GROSSQUANTITYTOTAL$14);
            }
            target.setStringValue(grossQuantityTotal);
        }
    }
    
    /**
     * Sets (as xml) the "GrossQuantityTotal" element
     */
    public void xsetGrossQuantityTotal(org.apache.xmlbeans.XmlString grossQuantityTotal)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(GROSSQUANTITYTOTAL$14, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(GROSSQUANTITYTOTAL$14);
            }
            target.set(grossQuantityTotal);
        }
    }
    
    /**
     * Nils the "GrossQuantityTotal" element
     */
    public void setNilGrossQuantityTotal()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(GROSSQUANTITYTOTAL$14, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(GROSSQUANTITYTOTAL$14);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "GrossQuantityTotal" element
     */
    public void unsetGrossQuantityTotal()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(GROSSQUANTITYTOTAL$14, 0);
        }
    }
    
    /**
     * Gets the "GrossQuantityTotalInWords" element
     */
    public java.lang.String getGrossQuantityTotalInWords()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(GROSSQUANTITYTOTALINWORDS$16, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "GrossQuantityTotalInWords" element
     */
    public org.apache.xmlbeans.XmlString xgetGrossQuantityTotalInWords()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(GROSSQUANTITYTOTALINWORDS$16, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "GrossQuantityTotalInWords" element
     */
    public boolean isNilGrossQuantityTotalInWords()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(GROSSQUANTITYTOTALINWORDS$16, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "GrossQuantityTotalInWords" element
     */
    public boolean isSetGrossQuantityTotalInWords()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(GROSSQUANTITYTOTALINWORDS$16) != 0;
        }
    }
    
    /**
     * Sets the "GrossQuantityTotalInWords" element
     */
    public void setGrossQuantityTotalInWords(java.lang.String grossQuantityTotalInWords)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(GROSSQUANTITYTOTALINWORDS$16, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(GROSSQUANTITYTOTALINWORDS$16);
            }
            target.setStringValue(grossQuantityTotalInWords);
        }
    }
    
    /**
     * Sets (as xml) the "GrossQuantityTotalInWords" element
     */
    public void xsetGrossQuantityTotalInWords(org.apache.xmlbeans.XmlString grossQuantityTotalInWords)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(GROSSQUANTITYTOTALINWORDS$16, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(GROSSQUANTITYTOTALINWORDS$16);
            }
            target.set(grossQuantityTotalInWords);
        }
    }
    
    /**
     * Nils the "GrossQuantityTotalInWords" element
     */
    public void setNilGrossQuantityTotalInWords()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(GROSSQUANTITYTOTALINWORDS$16, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(GROSSQUANTITYTOTALINWORDS$16);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "GrossQuantityTotalInWords" element
     */
    public void unsetGrossQuantityTotalInWords()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(GROSSQUANTITYTOTALINWORDS$16, 0);
        }
    }
    
    /**
     * Gets the "Grounds" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds getGrounds()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds)get_store().find_element_user(GROUNDS$18, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "Grounds" element
     */
    public boolean isNilGrounds()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds)get_store().find_element_user(GROUNDS$18, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Grounds" element
     */
    public boolean isSetGrounds()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(GROUNDS$18) != 0;
        }
    }
    
    /**
     * Sets the "Grounds" element
     */
    public void setGrounds(org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds grounds)
    {
        generatedSetterHelperImpl(grounds, GROUNDS$18, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "Grounds" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds addNewGrounds()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds)get_store().add_element_user(GROUNDS$18);
            return target;
        }
    }
    
    /**
     * Nils the "Grounds" element
     */
    public void setNilGrounds()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds)get_store().find_element_user(GROUNDS$18, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds)get_store().add_element_user(GROUNDS$18);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Grounds" element
     */
    public void unsetGrounds()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(GROUNDS$18, 0);
        }
    }
    
    /**
     * Gets the "Items" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.ArrayOfTorg12Item getItems()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.ArrayOfTorg12Item target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.ArrayOfTorg12Item)get_store().find_element_user(ITEMS$20, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "Items" element
     */
    public boolean isNilItems()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.ArrayOfTorg12Item target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.ArrayOfTorg12Item)get_store().find_element_user(ITEMS$20, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Items" element
     */
    public boolean isSetItems()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(ITEMS$20) != 0;
        }
    }
    
    /**
     * Sets the "Items" element
     */
    public void setItems(org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.ArrayOfTorg12Item items)
    {
        generatedSetterHelperImpl(items, ITEMS$20, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "Items" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.ArrayOfTorg12Item addNewItems()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.ArrayOfTorg12Item target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.ArrayOfTorg12Item)get_store().add_element_user(ITEMS$20);
            return target;
        }
    }
    
    /**
     * Nils the "Items" element
     */
    public void setNilItems()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.ArrayOfTorg12Item target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.ArrayOfTorg12Item)get_store().find_element_user(ITEMS$20, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.ArrayOfTorg12Item)get_store().add_element_user(ITEMS$20);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Items" element
     */
    public void unsetItems()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(ITEMS$20, 0);
        }
    }
    
    /**
     * Gets the "NetQuantityTotal" element
     */
    public java.lang.String getNetQuantityTotal()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(NETQUANTITYTOTAL$22, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "NetQuantityTotal" element
     */
    public org.apache.xmlbeans.XmlString xgetNetQuantityTotal()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NETQUANTITYTOTAL$22, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "NetQuantityTotal" element
     */
    public boolean isNilNetQuantityTotal()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NETQUANTITYTOTAL$22, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "NetQuantityTotal" element
     */
    public boolean isSetNetQuantityTotal()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(NETQUANTITYTOTAL$22) != 0;
        }
    }
    
    /**
     * Sets the "NetQuantityTotal" element
     */
    public void setNetQuantityTotal(java.lang.String netQuantityTotal)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(NETQUANTITYTOTAL$22, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(NETQUANTITYTOTAL$22);
            }
            target.setStringValue(netQuantityTotal);
        }
    }
    
    /**
     * Sets (as xml) the "NetQuantityTotal" element
     */
    public void xsetNetQuantityTotal(org.apache.xmlbeans.XmlString netQuantityTotal)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NETQUANTITYTOTAL$22, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(NETQUANTITYTOTAL$22);
            }
            target.set(netQuantityTotal);
        }
    }
    
    /**
     * Nils the "NetQuantityTotal" element
     */
    public void setNilNetQuantityTotal()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NETQUANTITYTOTAL$22, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(NETQUANTITYTOTAL$22);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "NetQuantityTotal" element
     */
    public void unsetNetQuantityTotal()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(NETQUANTITYTOTAL$22, 0);
        }
    }
    
    /**
     * Gets the "NetQuantityTotalInWords" element
     */
    public java.lang.String getNetQuantityTotalInWords()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(NETQUANTITYTOTALINWORDS$24, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "NetQuantityTotalInWords" element
     */
    public org.apache.xmlbeans.XmlString xgetNetQuantityTotalInWords()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NETQUANTITYTOTALINWORDS$24, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "NetQuantityTotalInWords" element
     */
    public boolean isNilNetQuantityTotalInWords()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NETQUANTITYTOTALINWORDS$24, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "NetQuantityTotalInWords" element
     */
    public boolean isSetNetQuantityTotalInWords()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(NETQUANTITYTOTALINWORDS$24) != 0;
        }
    }
    
    /**
     * Sets the "NetQuantityTotalInWords" element
     */
    public void setNetQuantityTotalInWords(java.lang.String netQuantityTotalInWords)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(NETQUANTITYTOTALINWORDS$24, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(NETQUANTITYTOTALINWORDS$24);
            }
            target.setStringValue(netQuantityTotalInWords);
        }
    }
    
    /**
     * Sets (as xml) the "NetQuantityTotalInWords" element
     */
    public void xsetNetQuantityTotalInWords(org.apache.xmlbeans.XmlString netQuantityTotalInWords)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NETQUANTITYTOTALINWORDS$24, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(NETQUANTITYTOTALINWORDS$24);
            }
            target.set(netQuantityTotalInWords);
        }
    }
    
    /**
     * Nils the "NetQuantityTotalInWords" element
     */
    public void setNilNetQuantityTotalInWords()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NETQUANTITYTOTALINWORDS$24, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(NETQUANTITYTOTALINWORDS$24);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "NetQuantityTotalInWords" element
     */
    public void unsetNetQuantityTotalInWords()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(NETQUANTITYTOTALINWORDS$24, 0);
        }
    }
    
    /**
     * Gets the "OperationCode" element
     */
    public java.lang.String getOperationCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(OPERATIONCODE$26, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "OperationCode" element
     */
    public org.apache.xmlbeans.XmlString xgetOperationCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATIONCODE$26, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "OperationCode" element
     */
    public boolean isNilOperationCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATIONCODE$26, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "OperationCode" element
     */
    public boolean isSetOperationCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(OPERATIONCODE$26) != 0;
        }
    }
    
    /**
     * Sets the "OperationCode" element
     */
    public void setOperationCode(java.lang.String operationCode)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(OPERATIONCODE$26, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(OPERATIONCODE$26);
            }
            target.setStringValue(operationCode);
        }
    }
    
    /**
     * Sets (as xml) the "OperationCode" element
     */
    public void xsetOperationCode(org.apache.xmlbeans.XmlString operationCode)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATIONCODE$26, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(OPERATIONCODE$26);
            }
            target.set(operationCode);
        }
    }
    
    /**
     * Nils the "OperationCode" element
     */
    public void setNilOperationCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATIONCODE$26, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(OPERATIONCODE$26);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "OperationCode" element
     */
    public void unsetOperationCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(OPERATIONCODE$26, 0);
        }
    }
    
    /**
     * Gets the "ParcelsQuantityTotal" element
     */
    public java.lang.String getParcelsQuantityTotal()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PARCELSQUANTITYTOTAL$28, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "ParcelsQuantityTotal" element
     */
    public org.apache.xmlbeans.XmlString xgetParcelsQuantityTotal()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PARCELSQUANTITYTOTAL$28, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "ParcelsQuantityTotal" element
     */
    public boolean isNilParcelsQuantityTotal()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PARCELSQUANTITYTOTAL$28, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "ParcelsQuantityTotal" element
     */
    public boolean isSetParcelsQuantityTotal()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(PARCELSQUANTITYTOTAL$28) != 0;
        }
    }
    
    /**
     * Sets the "ParcelsQuantityTotal" element
     */
    public void setParcelsQuantityTotal(java.lang.String parcelsQuantityTotal)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PARCELSQUANTITYTOTAL$28, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PARCELSQUANTITYTOTAL$28);
            }
            target.setStringValue(parcelsQuantityTotal);
        }
    }
    
    /**
     * Sets (as xml) the "ParcelsQuantityTotal" element
     */
    public void xsetParcelsQuantityTotal(org.apache.xmlbeans.XmlString parcelsQuantityTotal)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PARCELSQUANTITYTOTAL$28, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PARCELSQUANTITYTOTAL$28);
            }
            target.set(parcelsQuantityTotal);
        }
    }
    
    /**
     * Nils the "ParcelsQuantityTotal" element
     */
    public void setNilParcelsQuantityTotal()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PARCELSQUANTITYTOTAL$28, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PARCELSQUANTITYTOTAL$28);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "ParcelsQuantityTotal" element
     */
    public void unsetParcelsQuantityTotal()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(PARCELSQUANTITYTOTAL$28, 0);
        }
    }
    
    /**
     * Gets the "ParcelsQuantityTotalInWords" element
     */
    public java.lang.String getParcelsQuantityTotalInWords()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PARCELSQUANTITYTOTALINWORDS$30, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "ParcelsQuantityTotalInWords" element
     */
    public org.apache.xmlbeans.XmlString xgetParcelsQuantityTotalInWords()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PARCELSQUANTITYTOTALINWORDS$30, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "ParcelsQuantityTotalInWords" element
     */
    public boolean isNilParcelsQuantityTotalInWords()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PARCELSQUANTITYTOTALINWORDS$30, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "ParcelsQuantityTotalInWords" element
     */
    public boolean isSetParcelsQuantityTotalInWords()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(PARCELSQUANTITYTOTALINWORDS$30) != 0;
        }
    }
    
    /**
     * Sets the "ParcelsQuantityTotalInWords" element
     */
    public void setParcelsQuantityTotalInWords(java.lang.String parcelsQuantityTotalInWords)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PARCELSQUANTITYTOTALINWORDS$30, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PARCELSQUANTITYTOTALINWORDS$30);
            }
            target.setStringValue(parcelsQuantityTotalInWords);
        }
    }
    
    /**
     * Sets (as xml) the "ParcelsQuantityTotalInWords" element
     */
    public void xsetParcelsQuantityTotalInWords(org.apache.xmlbeans.XmlString parcelsQuantityTotalInWords)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PARCELSQUANTITYTOTALINWORDS$30, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PARCELSQUANTITYTOTALINWORDS$30);
            }
            target.set(parcelsQuantityTotalInWords);
        }
    }
    
    /**
     * Nils the "ParcelsQuantityTotalInWords" element
     */
    public void setNilParcelsQuantityTotalInWords()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PARCELSQUANTITYTOTALINWORDS$30, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PARCELSQUANTITYTOTALINWORDS$30);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "ParcelsQuantityTotalInWords" element
     */
    public void unsetParcelsQuantityTotalInWords()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(PARCELSQUANTITYTOTALINWORDS$30, 0);
        }
    }
    
    /**
     * Gets the "Payer" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo getPayer()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo)get_store().find_element_user(PAYER$32, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "Payer" element
     */
    public boolean isNilPayer()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo)get_store().find_element_user(PAYER$32, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Payer" element
     */
    public boolean isSetPayer()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(PAYER$32) != 0;
        }
    }
    
    /**
     * Sets the "Payer" element
     */
    public void setPayer(org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo payer)
    {
        generatedSetterHelperImpl(payer, PAYER$32, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "Payer" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo addNewPayer()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo)get_store().add_element_user(PAYER$32);
            return target;
        }
    }
    
    /**
     * Nils the "Payer" element
     */
    public void setNilPayer()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo)get_store().find_element_user(PAYER$32, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo)get_store().add_element_user(PAYER$32);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Payer" element
     */
    public void unsetPayer()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(PAYER$32, 0);
        }
    }
    
    /**
     * Gets the "QuantityTotal" element
     */
    public java.lang.String getQuantityTotal()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(QUANTITYTOTAL$34, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "QuantityTotal" element
     */
    public org.apache.xmlbeans.XmlString xgetQuantityTotal()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(QUANTITYTOTAL$34, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "QuantityTotal" element
     */
    public boolean isNilQuantityTotal()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(QUANTITYTOTAL$34, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "QuantityTotal" element
     */
    public boolean isSetQuantityTotal()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(QUANTITYTOTAL$34) != 0;
        }
    }
    
    /**
     * Sets the "QuantityTotal" element
     */
    public void setQuantityTotal(java.lang.String quantityTotal)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(QUANTITYTOTAL$34, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(QUANTITYTOTAL$34);
            }
            target.setStringValue(quantityTotal);
        }
    }
    
    /**
     * Sets (as xml) the "QuantityTotal" element
     */
    public void xsetQuantityTotal(org.apache.xmlbeans.XmlString quantityTotal)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(QUANTITYTOTAL$34, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(QUANTITYTOTAL$34);
            }
            target.set(quantityTotal);
        }
    }
    
    /**
     * Nils the "QuantityTotal" element
     */
    public void setNilQuantityTotal()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(QUANTITYTOTAL$34, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(QUANTITYTOTAL$34);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "QuantityTotal" element
     */
    public void unsetQuantityTotal()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(QUANTITYTOTAL$34, 0);
        }
    }
    
    /**
     * Gets the "SellerDocflowParticipant" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo getSellerDocflowParticipant()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo)get_store().find_element_user(SELLERDOCFLOWPARTICIPANT$36, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "SellerDocflowParticipant" element
     */
    public boolean isNilSellerDocflowParticipant()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo)get_store().find_element_user(SELLERDOCFLOWPARTICIPANT$36, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "SellerDocflowParticipant" element
     */
    public boolean isSetSellerDocflowParticipant()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(SELLERDOCFLOWPARTICIPANT$36) != 0;
        }
    }
    
    /**
     * Sets the "SellerDocflowParticipant" element
     */
    public void setSellerDocflowParticipant(org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo sellerDocflowParticipant)
    {
        generatedSetterHelperImpl(sellerDocflowParticipant, SELLERDOCFLOWPARTICIPANT$36, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "SellerDocflowParticipant" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo addNewSellerDocflowParticipant()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo)get_store().add_element_user(SELLERDOCFLOWPARTICIPANT$36);
            return target;
        }
    }
    
    /**
     * Nils the "SellerDocflowParticipant" element
     */
    public void setNilSellerDocflowParticipant()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo)get_store().find_element_user(SELLERDOCFLOWPARTICIPANT$36, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo)get_store().add_element_user(SELLERDOCFLOWPARTICIPANT$36);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "SellerDocflowParticipant" element
     */
    public void unsetSellerDocflowParticipant()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(SELLERDOCFLOWPARTICIPANT$36, 0);
        }
    }
    
    /**
     * Gets the "Shipper" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo getShipper()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo)get_store().find_element_user(SHIPPER$38, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "Shipper" element
     */
    public boolean isNilShipper()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo)get_store().find_element_user(SHIPPER$38, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Shipper" element
     */
    public boolean isSetShipper()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(SHIPPER$38) != 0;
        }
    }
    
    /**
     * Sets the "Shipper" element
     */
    public void setShipper(org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo shipper)
    {
        generatedSetterHelperImpl(shipper, SHIPPER$38, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "Shipper" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo addNewShipper()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo)get_store().add_element_user(SHIPPER$38);
            return target;
        }
    }
    
    /**
     * Nils the "Shipper" element
     */
    public void setNilShipper()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo)get_store().find_element_user(SHIPPER$38, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo)get_store().add_element_user(SHIPPER$38);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Shipper" element
     */
    public void unsetShipper()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(SHIPPER$38, 0);
        }
    }
    
    /**
     * Gets the "Signer" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Signer getSigner()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Signer target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Signer)get_store().find_element_user(SIGNER$40, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "Signer" element
     */
    public boolean isNilSigner()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Signer target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Signer)get_store().find_element_user(SIGNER$40, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Signer" element
     */
    public boolean isSetSigner()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(SIGNER$40) != 0;
        }
    }
    
    /**
     * Sets the "Signer" element
     */
    public void setSigner(org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Signer signer)
    {
        generatedSetterHelperImpl(signer, SIGNER$40, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "Signer" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Signer addNewSigner()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Signer target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Signer)get_store().add_element_user(SIGNER$40);
            return target;
        }
    }
    
    /**
     * Nils the "Signer" element
     */
    public void setNilSigner()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Signer target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Signer)get_store().find_element_user(SIGNER$40, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Signer)get_store().add_element_user(SIGNER$40);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Signer" element
     */
    public void unsetSigner()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(SIGNER$40, 0);
        }
    }
    
    /**
     * Gets the "Supplier" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo getSupplier()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo)get_store().find_element_user(SUPPLIER$42, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "Supplier" element
     */
    public boolean isNilSupplier()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo)get_store().find_element_user(SUPPLIER$42, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Supplier" element
     */
    public boolean isSetSupplier()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(SUPPLIER$42) != 0;
        }
    }
    
    /**
     * Sets the "Supplier" element
     */
    public void setSupplier(org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo supplier)
    {
        generatedSetterHelperImpl(supplier, SUPPLIER$42, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "Supplier" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo addNewSupplier()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo)get_store().add_element_user(SUPPLIER$42);
            return target;
        }
    }
    
    /**
     * Nils the "Supplier" element
     */
    public void setNilSupplier()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo)get_store().find_element_user(SUPPLIER$42, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo)get_store().add_element_user(SUPPLIER$42);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Supplier" element
     */
    public void unsetSupplier()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(SUPPLIER$42, 0);
        }
    }
    
    /**
     * Gets the "SupplyAllowedBy" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official getSupplyAllowedBy()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official)get_store().find_element_user(SUPPLYALLOWEDBY$44, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "SupplyAllowedBy" element
     */
    public boolean isNilSupplyAllowedBy()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official)get_store().find_element_user(SUPPLYALLOWEDBY$44, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "SupplyAllowedBy" element
     */
    public boolean isSetSupplyAllowedBy()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(SUPPLYALLOWEDBY$44) != 0;
        }
    }
    
    /**
     * Sets the "SupplyAllowedBy" element
     */
    public void setSupplyAllowedBy(org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official supplyAllowedBy)
    {
        generatedSetterHelperImpl(supplyAllowedBy, SUPPLYALLOWEDBY$44, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "SupplyAllowedBy" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official addNewSupplyAllowedBy()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official)get_store().add_element_user(SUPPLYALLOWEDBY$44);
            return target;
        }
    }
    
    /**
     * Nils the "SupplyAllowedBy" element
     */
    public void setNilSupplyAllowedBy()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official)get_store().find_element_user(SUPPLYALLOWEDBY$44, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official)get_store().add_element_user(SUPPLYALLOWEDBY$44);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "SupplyAllowedBy" element
     */
    public void unsetSupplyAllowedBy()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(SUPPLYALLOWEDBY$44, 0);
        }
    }
    
    /**
     * Gets the "SupplyDate" element
     */
    public java.lang.String getSupplyDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SUPPLYDATE$46, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "SupplyDate" element
     */
    public org.apache.xmlbeans.XmlString xgetSupplyDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SUPPLYDATE$46, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "SupplyDate" element
     */
    public boolean isNilSupplyDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SUPPLYDATE$46, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "SupplyDate" element
     */
    public boolean isSetSupplyDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(SUPPLYDATE$46) != 0;
        }
    }
    
    /**
     * Sets the "SupplyDate" element
     */
    public void setSupplyDate(java.lang.String supplyDate)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SUPPLYDATE$46, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(SUPPLYDATE$46);
            }
            target.setStringValue(supplyDate);
        }
    }
    
    /**
     * Sets (as xml) the "SupplyDate" element
     */
    public void xsetSupplyDate(org.apache.xmlbeans.XmlString supplyDate)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SUPPLYDATE$46, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(SUPPLYDATE$46);
            }
            target.set(supplyDate);
        }
    }
    
    /**
     * Nils the "SupplyDate" element
     */
    public void setNilSupplyDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SUPPLYDATE$46, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(SUPPLYDATE$46);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "SupplyDate" element
     */
    public void unsetSupplyDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(SUPPLYDATE$46, 0);
        }
    }
    
    /**
     * Gets the "SupplyPerformedBy" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official getSupplyPerformedBy()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official)get_store().find_element_user(SUPPLYPERFORMEDBY$48, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "SupplyPerformedBy" element
     */
    public boolean isNilSupplyPerformedBy()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official)get_store().find_element_user(SUPPLYPERFORMEDBY$48, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "SupplyPerformedBy" element
     */
    public boolean isSetSupplyPerformedBy()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(SUPPLYPERFORMEDBY$48) != 0;
        }
    }
    
    /**
     * Sets the "SupplyPerformedBy" element
     */
    public void setSupplyPerformedBy(org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official supplyPerformedBy)
    {
        generatedSetterHelperImpl(supplyPerformedBy, SUPPLYPERFORMEDBY$48, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "SupplyPerformedBy" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official addNewSupplyPerformedBy()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official)get_store().add_element_user(SUPPLYPERFORMEDBY$48);
            return target;
        }
    }
    
    /**
     * Nils the "SupplyPerformedBy" element
     */
    public void setNilSupplyPerformedBy()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official)get_store().find_element_user(SUPPLYPERFORMEDBY$48, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official)get_store().add_element_user(SUPPLYPERFORMEDBY$48);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "SupplyPerformedBy" element
     */
    public void unsetSupplyPerformedBy()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(SUPPLYPERFORMEDBY$48, 0);
        }
    }
    
    /**
     * Gets the "Total" element
     */
    public java.lang.String getTotal()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(TOTAL$50, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Total" element
     */
    public org.apache.xmlbeans.XmlString xgetTotal()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TOTAL$50, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "Total" element
     */
    public boolean isNilTotal()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TOTAL$50, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Total" element
     */
    public boolean isSetTotal()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(TOTAL$50) != 0;
        }
    }
    
    /**
     * Sets the "Total" element
     */
    public void setTotal(java.lang.String total)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(TOTAL$50, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(TOTAL$50);
            }
            target.setStringValue(total);
        }
    }
    
    /**
     * Sets (as xml) the "Total" element
     */
    public void xsetTotal(org.apache.xmlbeans.XmlString total)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TOTAL$50, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(TOTAL$50);
            }
            target.set(total);
        }
    }
    
    /**
     * Nils the "Total" element
     */
    public void setNilTotal()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TOTAL$50, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(TOTAL$50);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Total" element
     */
    public void unsetTotal()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(TOTAL$50, 0);
        }
    }
    
    /**
     * Gets the "TotalInWords" element
     */
    public java.lang.String getTotalInWords()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(TOTALINWORDS$52, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "TotalInWords" element
     */
    public org.apache.xmlbeans.XmlString xgetTotalInWords()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TOTALINWORDS$52, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "TotalInWords" element
     */
    public boolean isNilTotalInWords()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TOTALINWORDS$52, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "TotalInWords" element
     */
    public boolean isSetTotalInWords()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(TOTALINWORDS$52) != 0;
        }
    }
    
    /**
     * Sets the "TotalInWords" element
     */
    public void setTotalInWords(java.lang.String totalInWords)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(TOTALINWORDS$52, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(TOTALINWORDS$52);
            }
            target.setStringValue(totalInWords);
        }
    }
    
    /**
     * Sets (as xml) the "TotalInWords" element
     */
    public void xsetTotalInWords(org.apache.xmlbeans.XmlString totalInWords)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TOTALINWORDS$52, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(TOTALINWORDS$52);
            }
            target.set(totalInWords);
        }
    }
    
    /**
     * Nils the "TotalInWords" element
     */
    public void setNilTotalInWords()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TOTALINWORDS$52, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(TOTALINWORDS$52);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "TotalInWords" element
     */
    public void unsetTotalInWords()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(TOTALINWORDS$52, 0);
        }
    }
    
    /**
     * Gets the "TotalWithVatExcluded" element
     */
    public java.lang.String getTotalWithVatExcluded()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(TOTALWITHVATEXCLUDED$54, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "TotalWithVatExcluded" element
     */
    public org.apache.xmlbeans.XmlString xgetTotalWithVatExcluded()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TOTALWITHVATEXCLUDED$54, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "TotalWithVatExcluded" element
     */
    public boolean isNilTotalWithVatExcluded()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TOTALWITHVATEXCLUDED$54, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "TotalWithVatExcluded" element
     */
    public boolean isSetTotalWithVatExcluded()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(TOTALWITHVATEXCLUDED$54) != 0;
        }
    }
    
    /**
     * Sets the "TotalWithVatExcluded" element
     */
    public void setTotalWithVatExcluded(java.lang.String totalWithVatExcluded)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(TOTALWITHVATEXCLUDED$54, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(TOTALWITHVATEXCLUDED$54);
            }
            target.setStringValue(totalWithVatExcluded);
        }
    }
    
    /**
     * Sets (as xml) the "TotalWithVatExcluded" element
     */
    public void xsetTotalWithVatExcluded(org.apache.xmlbeans.XmlString totalWithVatExcluded)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TOTALWITHVATEXCLUDED$54, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(TOTALWITHVATEXCLUDED$54);
            }
            target.set(totalWithVatExcluded);
        }
    }
    
    /**
     * Nils the "TotalWithVatExcluded" element
     */
    public void setNilTotalWithVatExcluded()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TOTALWITHVATEXCLUDED$54, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(TOTALWITHVATEXCLUDED$54);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "TotalWithVatExcluded" element
     */
    public void unsetTotalWithVatExcluded()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(TOTALWITHVATEXCLUDED$54, 0);
        }
    }
    
    /**
     * Gets the "Vat" element
     */
    public java.lang.String getVat()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(VAT$56, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Vat" element
     */
    public org.apache.xmlbeans.XmlString xgetVat()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(VAT$56, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "Vat" element
     */
    public boolean isNilVat()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(VAT$56, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Vat" element
     */
    public boolean isSetVat()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(VAT$56) != 0;
        }
    }
    
    /**
     * Sets the "Vat" element
     */
    public void setVat(java.lang.String vat)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(VAT$56, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(VAT$56);
            }
            target.setStringValue(vat);
        }
    }
    
    /**
     * Sets (as xml) the "Vat" element
     */
    public void xsetVat(org.apache.xmlbeans.XmlString vat)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(VAT$56, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(VAT$56);
            }
            target.set(vat);
        }
    }
    
    /**
     * Nils the "Vat" element
     */
    public void setNilVat()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(VAT$56, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(VAT$56);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Vat" element
     */
    public void unsetVat()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(VAT$56, 0);
        }
    }
    
    /**
     * Gets the "WaybillDate" element
     */
    public java.lang.String getWaybillDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(WAYBILLDATE$58, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "WaybillDate" element
     */
    public org.apache.xmlbeans.XmlString xgetWaybillDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(WAYBILLDATE$58, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "WaybillDate" element
     */
    public boolean isNilWaybillDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(WAYBILLDATE$58, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "WaybillDate" element
     */
    public boolean isSetWaybillDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(WAYBILLDATE$58) != 0;
        }
    }
    
    /**
     * Sets the "WaybillDate" element
     */
    public void setWaybillDate(java.lang.String waybillDate)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(WAYBILLDATE$58, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(WAYBILLDATE$58);
            }
            target.setStringValue(waybillDate);
        }
    }
    
    /**
     * Sets (as xml) the "WaybillDate" element
     */
    public void xsetWaybillDate(org.apache.xmlbeans.XmlString waybillDate)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(WAYBILLDATE$58, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(WAYBILLDATE$58);
            }
            target.set(waybillDate);
        }
    }
    
    /**
     * Nils the "WaybillDate" element
     */
    public void setNilWaybillDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(WAYBILLDATE$58, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(WAYBILLDATE$58);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "WaybillDate" element
     */
    public void unsetWaybillDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(WAYBILLDATE$58, 0);
        }
    }
    
    /**
     * Gets the "WaybillNumber" element
     */
    public java.lang.String getWaybillNumber()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(WAYBILLNUMBER$60, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "WaybillNumber" element
     */
    public org.apache.xmlbeans.XmlString xgetWaybillNumber()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(WAYBILLNUMBER$60, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "WaybillNumber" element
     */
    public boolean isNilWaybillNumber()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(WAYBILLNUMBER$60, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "WaybillNumber" element
     */
    public boolean isSetWaybillNumber()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(WAYBILLNUMBER$60) != 0;
        }
    }
    
    /**
     * Sets the "WaybillNumber" element
     */
    public void setWaybillNumber(java.lang.String waybillNumber)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(WAYBILLNUMBER$60, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(WAYBILLNUMBER$60);
            }
            target.setStringValue(waybillNumber);
        }
    }
    
    /**
     * Sets (as xml) the "WaybillNumber" element
     */
    public void xsetWaybillNumber(org.apache.xmlbeans.XmlString waybillNumber)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(WAYBILLNUMBER$60, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(WAYBILLNUMBER$60);
            }
            target.set(waybillNumber);
        }
    }
    
    /**
     * Nils the "WaybillNumber" element
     */
    public void setNilWaybillNumber()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(WAYBILLNUMBER$60, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(WAYBILLNUMBER$60);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "WaybillNumber" element
     */
    public void unsetWaybillNumber()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(WAYBILLNUMBER$60, 0);
        }
    }
}
