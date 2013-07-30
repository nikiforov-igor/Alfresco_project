/*
 * XML Type:  InvoiceGen
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.impl;
/**
 * An XML InvoiceGen(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice).
 *
 * This is a complex type.
 */
public class InvoiceGenImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen
{
    private static final long serialVersionUID = 1L;
    
    public InvoiceGenImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName CONSIGNEE$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "Consignee");
    private static final javax.xml.namespace.QName CURRENCYCODE$2 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "CurrencyCode");
    private static final javax.xml.namespace.QName CUSTOMER$4 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "Customer");
    private static final javax.xml.namespace.QName DOCUMENTDATE$6 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "DocumentDate");
    private static final javax.xml.namespace.QName DOCUMENTNUMBER$8 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "DocumentNumber");
    private static final javax.xml.namespace.QName GENERATEDATETIME$10 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "GenerateDateTime");
    private static final javax.xml.namespace.QName INFORMATIONFIELDID$12 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "InformationFieldId");
    private static final javax.xml.namespace.QName INFORMATIONTEXT$14 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "InformationText");
    private static final javax.xml.namespace.QName PAYMENTDOCUMENTS$16 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "PaymentDocuments");
    private static final javax.xml.namespace.QName PRODUCTS$18 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "Products");
    private static final javax.xml.namespace.QName REVISIONDATE$20 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "RevisionDate");
    private static final javax.xml.namespace.QName REVISIONNUMBER$22 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "RevisionNumber");
    private static final javax.xml.namespace.QName SHIPPER$24 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "Shipper");
    private static final javax.xml.namespace.QName SIGNER$26 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "Signer");
    private static final javax.xml.namespace.QName TOTALSUMNOVAT$28 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "TotalSumNoVat");
    private static final javax.xml.namespace.QName TOTALSUMVAT$30 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "TotalSumVat");
    private static final javax.xml.namespace.QName TOTALSUMWITHVAT$32 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "TotalSumWithVat");
    private static final javax.xml.namespace.QName VENDOR$34 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "Vendor");
    
    
    /**
     * Gets the "Consignee" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant getConsignee()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant)get_store().find_element_user(CONSIGNEE$0, 0);
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
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant)get_store().find_element_user(CONSIGNEE$0, 0);
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
            return get_store().count_elements(CONSIGNEE$0) != 0;
        }
    }
    
    /**
     * Sets the "Consignee" element
     */
    public void setConsignee(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant consignee)
    {
        generatedSetterHelperImpl(consignee, CONSIGNEE$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "Consignee" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant addNewConsignee()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant)get_store().add_element_user(CONSIGNEE$0);
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
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant)get_store().find_element_user(CONSIGNEE$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant)get_store().add_element_user(CONSIGNEE$0);
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
            get_store().remove_element(CONSIGNEE$0, 0);
        }
    }
    
    /**
     * Gets the "CurrencyCode" element
     */
    public java.lang.String getCurrencyCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CURRENCYCODE$2, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "CurrencyCode" element
     */
    public org.apache.xmlbeans.XmlString xgetCurrencyCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(CURRENCYCODE$2, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "CurrencyCode" element
     */
    public boolean isNilCurrencyCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(CURRENCYCODE$2, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "CurrencyCode" element
     */
    public boolean isSetCurrencyCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(CURRENCYCODE$2) != 0;
        }
    }
    
    /**
     * Sets the "CurrencyCode" element
     */
    public void setCurrencyCode(java.lang.String currencyCode)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CURRENCYCODE$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(CURRENCYCODE$2);
            }
            target.setStringValue(currencyCode);
        }
    }
    
    /**
     * Sets (as xml) the "CurrencyCode" element
     */
    public void xsetCurrencyCode(org.apache.xmlbeans.XmlString currencyCode)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(CURRENCYCODE$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(CURRENCYCODE$2);
            }
            target.set(currencyCode);
        }
    }
    
    /**
     * Nils the "CurrencyCode" element
     */
    public void setNilCurrencyCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(CURRENCYCODE$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(CURRENCYCODE$2);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "CurrencyCode" element
     */
    public void unsetCurrencyCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(CURRENCYCODE$2, 0);
        }
    }
    
    /**
     * Gets the "Customer" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantWithAddress getCustomer()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantWithAddress target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantWithAddress)get_store().find_element_user(CUSTOMER$4, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "Customer" element
     */
    public boolean isNilCustomer()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantWithAddress target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantWithAddress)get_store().find_element_user(CUSTOMER$4, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Customer" element
     */
    public boolean isSetCustomer()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(CUSTOMER$4) != 0;
        }
    }
    
    /**
     * Sets the "Customer" element
     */
    public void setCustomer(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantWithAddress customer)
    {
        generatedSetterHelperImpl(customer, CUSTOMER$4, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "Customer" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantWithAddress addNewCustomer()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantWithAddress target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantWithAddress)get_store().add_element_user(CUSTOMER$4);
            return target;
        }
    }
    
    /**
     * Nils the "Customer" element
     */
    public void setNilCustomer()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantWithAddress target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantWithAddress)get_store().find_element_user(CUSTOMER$4, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantWithAddress)get_store().add_element_user(CUSTOMER$4);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Customer" element
     */
    public void unsetCustomer()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(CUSTOMER$4, 0);
        }
    }
    
    /**
     * Gets the "DocumentDate" element
     */
    public java.util.Calendar getDocumentDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DOCUMENTDATE$6, 0);
            if (target == null)
            {
                return null;
            }
            return target.getCalendarValue();
        }
    }
    
    /**
     * Gets (as xml) the "DocumentDate" element
     */
    public org.apache.xmlbeans.XmlDateTime xgetDocumentDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlDateTime target = null;
            target = (org.apache.xmlbeans.XmlDateTime)get_store().find_element_user(DOCUMENTDATE$6, 0);
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
            org.apache.xmlbeans.XmlDateTime target = null;
            target = (org.apache.xmlbeans.XmlDateTime)get_store().find_element_user(DOCUMENTDATE$6, 0);
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
            return get_store().count_elements(DOCUMENTDATE$6) != 0;
        }
    }
    
    /**
     * Sets the "DocumentDate" element
     */
    public void setDocumentDate(java.util.Calendar documentDate)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DOCUMENTDATE$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(DOCUMENTDATE$6);
            }
            target.setCalendarValue(documentDate);
        }
    }
    
    /**
     * Sets (as xml) the "DocumentDate" element
     */
    public void xsetDocumentDate(org.apache.xmlbeans.XmlDateTime documentDate)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlDateTime target = null;
            target = (org.apache.xmlbeans.XmlDateTime)get_store().find_element_user(DOCUMENTDATE$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlDateTime)get_store().add_element_user(DOCUMENTDATE$6);
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
            org.apache.xmlbeans.XmlDateTime target = null;
            target = (org.apache.xmlbeans.XmlDateTime)get_store().find_element_user(DOCUMENTDATE$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlDateTime)get_store().add_element_user(DOCUMENTDATE$6);
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
            get_store().remove_element(DOCUMENTDATE$6, 0);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DOCUMENTNUMBER$8, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DOCUMENTNUMBER$8, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DOCUMENTNUMBER$8, 0);
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
            return get_store().count_elements(DOCUMENTNUMBER$8) != 0;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DOCUMENTNUMBER$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(DOCUMENTNUMBER$8);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DOCUMENTNUMBER$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(DOCUMENTNUMBER$8);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DOCUMENTNUMBER$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(DOCUMENTNUMBER$8);
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
            get_store().remove_element(DOCUMENTNUMBER$8, 0);
        }
    }
    
    /**
     * Gets the "GenerateDateTime" element
     */
    public java.util.Calendar getGenerateDateTime()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(GENERATEDATETIME$10, 0);
            if (target == null)
            {
                return null;
            }
            return target.getCalendarValue();
        }
    }
    
    /**
     * Gets (as xml) the "GenerateDateTime" element
     */
    public org.apache.xmlbeans.XmlDateTime xgetGenerateDateTime()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlDateTime target = null;
            target = (org.apache.xmlbeans.XmlDateTime)get_store().find_element_user(GENERATEDATETIME$10, 0);
            return target;
        }
    }
    
    /**
     * True if has "GenerateDateTime" element
     */
    public boolean isSetGenerateDateTime()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(GENERATEDATETIME$10) != 0;
        }
    }
    
    /**
     * Sets the "GenerateDateTime" element
     */
    public void setGenerateDateTime(java.util.Calendar generateDateTime)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(GENERATEDATETIME$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(GENERATEDATETIME$10);
            }
            target.setCalendarValue(generateDateTime);
        }
    }
    
    /**
     * Sets (as xml) the "GenerateDateTime" element
     */
    public void xsetGenerateDateTime(org.apache.xmlbeans.XmlDateTime generateDateTime)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlDateTime target = null;
            target = (org.apache.xmlbeans.XmlDateTime)get_store().find_element_user(GENERATEDATETIME$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlDateTime)get_store().add_element_user(GENERATEDATETIME$10);
            }
            target.set(generateDateTime);
        }
    }
    
    /**
     * Unsets the "GenerateDateTime" element
     */
    public void unsetGenerateDateTime()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(GENERATEDATETIME$10, 0);
        }
    }
    
    /**
     * Gets the "InformationFieldId" element
     */
    public java.lang.String getInformationFieldId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(INFORMATIONFIELDID$12, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "InformationFieldId" element
     */
    public org.apache.xmlbeans.XmlString xgetInformationFieldId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INFORMATIONFIELDID$12, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "InformationFieldId" element
     */
    public boolean isNilInformationFieldId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INFORMATIONFIELDID$12, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "InformationFieldId" element
     */
    public boolean isSetInformationFieldId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(INFORMATIONFIELDID$12) != 0;
        }
    }
    
    /**
     * Sets the "InformationFieldId" element
     */
    public void setInformationFieldId(java.lang.String informationFieldId)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(INFORMATIONFIELDID$12, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(INFORMATIONFIELDID$12);
            }
            target.setStringValue(informationFieldId);
        }
    }
    
    /**
     * Sets (as xml) the "InformationFieldId" element
     */
    public void xsetInformationFieldId(org.apache.xmlbeans.XmlString informationFieldId)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INFORMATIONFIELDID$12, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(INFORMATIONFIELDID$12);
            }
            target.set(informationFieldId);
        }
    }
    
    /**
     * Nils the "InformationFieldId" element
     */
    public void setNilInformationFieldId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INFORMATIONFIELDID$12, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(INFORMATIONFIELDID$12);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "InformationFieldId" element
     */
    public void unsetInformationFieldId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(INFORMATIONFIELDID$12, 0);
        }
    }
    
    /**
     * Gets the "InformationText" element
     */
    public java.lang.String getInformationText()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(INFORMATIONTEXT$14, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "InformationText" element
     */
    public org.apache.xmlbeans.XmlString xgetInformationText()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INFORMATIONTEXT$14, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "InformationText" element
     */
    public boolean isNilInformationText()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INFORMATIONTEXT$14, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "InformationText" element
     */
    public boolean isSetInformationText()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(INFORMATIONTEXT$14) != 0;
        }
    }
    
    /**
     * Sets the "InformationText" element
     */
    public void setInformationText(java.lang.String informationText)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(INFORMATIONTEXT$14, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(INFORMATIONTEXT$14);
            }
            target.setStringValue(informationText);
        }
    }
    
    /**
     * Sets (as xml) the "InformationText" element
     */
    public void xsetInformationText(org.apache.xmlbeans.XmlString informationText)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INFORMATIONTEXT$14, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(INFORMATIONTEXT$14);
            }
            target.set(informationText);
        }
    }
    
    /**
     * Nils the "InformationText" element
     */
    public void setNilInformationText()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INFORMATIONTEXT$14, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(INFORMATIONTEXT$14);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "InformationText" element
     */
    public void unsetInformationText()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(INFORMATIONTEXT$14, 0);
        }
    }
    
    /**
     * Gets the "PaymentDocuments" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfPaymentDocumentGen getPaymentDocuments()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfPaymentDocumentGen target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfPaymentDocumentGen)get_store().find_element_user(PAYMENTDOCUMENTS$16, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "PaymentDocuments" element
     */
    public boolean isNilPaymentDocuments()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfPaymentDocumentGen target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfPaymentDocumentGen)get_store().find_element_user(PAYMENTDOCUMENTS$16, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "PaymentDocuments" element
     */
    public boolean isSetPaymentDocuments()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(PAYMENTDOCUMENTS$16) != 0;
        }
    }
    
    /**
     * Sets the "PaymentDocuments" element
     */
    public void setPaymentDocuments(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfPaymentDocumentGen paymentDocuments)
    {
        generatedSetterHelperImpl(paymentDocuments, PAYMENTDOCUMENTS$16, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "PaymentDocuments" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfPaymentDocumentGen addNewPaymentDocuments()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfPaymentDocumentGen target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfPaymentDocumentGen)get_store().add_element_user(PAYMENTDOCUMENTS$16);
            return target;
        }
    }
    
    /**
     * Nils the "PaymentDocuments" element
     */
    public void setNilPaymentDocuments()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfPaymentDocumentGen target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfPaymentDocumentGen)get_store().find_element_user(PAYMENTDOCUMENTS$16, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfPaymentDocumentGen)get_store().add_element_user(PAYMENTDOCUMENTS$16);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "PaymentDocuments" element
     */
    public void unsetPaymentDocuments()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(PAYMENTDOCUMENTS$16, 0);
        }
    }
    
    /**
     * Gets the "Products" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfInvoiceProductGen getProducts()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfInvoiceProductGen target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfInvoiceProductGen)get_store().find_element_user(PRODUCTS$18, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "Products" element
     */
    public boolean isNilProducts()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfInvoiceProductGen target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfInvoiceProductGen)get_store().find_element_user(PRODUCTS$18, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Products" element
     */
    public boolean isSetProducts()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(PRODUCTS$18) != 0;
        }
    }
    
    /**
     * Sets the "Products" element
     */
    public void setProducts(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfInvoiceProductGen products)
    {
        generatedSetterHelperImpl(products, PRODUCTS$18, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "Products" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfInvoiceProductGen addNewProducts()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfInvoiceProductGen target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfInvoiceProductGen)get_store().add_element_user(PRODUCTS$18);
            return target;
        }
    }
    
    /**
     * Nils the "Products" element
     */
    public void setNilProducts()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfInvoiceProductGen target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfInvoiceProductGen)get_store().find_element_user(PRODUCTS$18, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfInvoiceProductGen)get_store().add_element_user(PRODUCTS$18);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Products" element
     */
    public void unsetProducts()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(PRODUCTS$18, 0);
        }
    }
    
    /**
     * Gets the "RevisionDate" element
     */
    public java.util.Calendar getRevisionDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(REVISIONDATE$20, 0);
            if (target == null)
            {
                return null;
            }
            return target.getCalendarValue();
        }
    }
    
    /**
     * Gets (as xml) the "RevisionDate" element
     */
    public org.apache.xmlbeans.XmlDateTime xgetRevisionDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlDateTime target = null;
            target = (org.apache.xmlbeans.XmlDateTime)get_store().find_element_user(REVISIONDATE$20, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "RevisionDate" element
     */
    public boolean isNilRevisionDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlDateTime target = null;
            target = (org.apache.xmlbeans.XmlDateTime)get_store().find_element_user(REVISIONDATE$20, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "RevisionDate" element
     */
    public boolean isSetRevisionDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(REVISIONDATE$20) != 0;
        }
    }
    
    /**
     * Sets the "RevisionDate" element
     */
    public void setRevisionDate(java.util.Calendar revisionDate)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(REVISIONDATE$20, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(REVISIONDATE$20);
            }
            target.setCalendarValue(revisionDate);
        }
    }
    
    /**
     * Sets (as xml) the "RevisionDate" element
     */
    public void xsetRevisionDate(org.apache.xmlbeans.XmlDateTime revisionDate)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlDateTime target = null;
            target = (org.apache.xmlbeans.XmlDateTime)get_store().find_element_user(REVISIONDATE$20, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlDateTime)get_store().add_element_user(REVISIONDATE$20);
            }
            target.set(revisionDate);
        }
    }
    
    /**
     * Nils the "RevisionDate" element
     */
    public void setNilRevisionDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlDateTime target = null;
            target = (org.apache.xmlbeans.XmlDateTime)get_store().find_element_user(REVISIONDATE$20, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlDateTime)get_store().add_element_user(REVISIONDATE$20);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "RevisionDate" element
     */
    public void unsetRevisionDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(REVISIONDATE$20, 0);
        }
    }
    
    /**
     * Gets the "RevisionNumber" element
     */
    public java.lang.String getRevisionNumber()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(REVISIONNUMBER$22, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "RevisionNumber" element
     */
    public org.apache.xmlbeans.XmlString xgetRevisionNumber()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(REVISIONNUMBER$22, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "RevisionNumber" element
     */
    public boolean isNilRevisionNumber()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(REVISIONNUMBER$22, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "RevisionNumber" element
     */
    public boolean isSetRevisionNumber()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(REVISIONNUMBER$22) != 0;
        }
    }
    
    /**
     * Sets the "RevisionNumber" element
     */
    public void setRevisionNumber(java.lang.String revisionNumber)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(REVISIONNUMBER$22, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(REVISIONNUMBER$22);
            }
            target.setStringValue(revisionNumber);
        }
    }
    
    /**
     * Sets (as xml) the "RevisionNumber" element
     */
    public void xsetRevisionNumber(org.apache.xmlbeans.XmlString revisionNumber)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(REVISIONNUMBER$22, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(REVISIONNUMBER$22);
            }
            target.set(revisionNumber);
        }
    }
    
    /**
     * Nils the "RevisionNumber" element
     */
    public void setNilRevisionNumber()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(REVISIONNUMBER$22, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(REVISIONNUMBER$22);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "RevisionNumber" element
     */
    public void unsetRevisionNumber()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(REVISIONNUMBER$22, 0);
        }
    }
    
    /**
     * Gets the "Shipper" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant getShipper()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant)get_store().find_element_user(SHIPPER$24, 0);
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
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant)get_store().find_element_user(SHIPPER$24, 0);
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
            return get_store().count_elements(SHIPPER$24) != 0;
        }
    }
    
    /**
     * Sets the "Shipper" element
     */
    public void setShipper(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant shipper)
    {
        generatedSetterHelperImpl(shipper, SHIPPER$24, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "Shipper" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant addNewShipper()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant)get_store().add_element_user(SHIPPER$24);
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
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant)get_store().find_element_user(SHIPPER$24, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant)get_store().add_element_user(SHIPPER$24);
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
            get_store().remove_element(SHIPPER$24, 0);
        }
    }
    
    /**
     * Gets the "Signer" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.Signer getSigner()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.Signer target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.Signer)get_store().find_element_user(SIGNER$26, 0);
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
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.Signer target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.Signer)get_store().find_element_user(SIGNER$26, 0);
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
            return get_store().count_elements(SIGNER$26) != 0;
        }
    }
    
    /**
     * Sets the "Signer" element
     */
    public void setSigner(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.Signer signer)
    {
        generatedSetterHelperImpl(signer, SIGNER$26, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "Signer" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.Signer addNewSigner()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.Signer target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.Signer)get_store().add_element_user(SIGNER$26);
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
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.Signer target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.Signer)get_store().find_element_user(SIGNER$26, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.Signer)get_store().add_element_user(SIGNER$26);
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
            get_store().remove_element(SIGNER$26, 0);
        }
    }
    
    /**
     * Gets the "TotalSumNoVat" element
     */
    public java.lang.String getTotalSumNoVat()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(TOTALSUMNOVAT$28, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "TotalSumNoVat" element
     */
    public org.apache.xmlbeans.XmlString xgetTotalSumNoVat()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TOTALSUMNOVAT$28, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "TotalSumNoVat" element
     */
    public boolean isNilTotalSumNoVat()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TOTALSUMNOVAT$28, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "TotalSumNoVat" element
     */
    public boolean isSetTotalSumNoVat()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(TOTALSUMNOVAT$28) != 0;
        }
    }
    
    /**
     * Sets the "TotalSumNoVat" element
     */
    public void setTotalSumNoVat(java.lang.String totalSumNoVat)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(TOTALSUMNOVAT$28, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(TOTALSUMNOVAT$28);
            }
            target.setStringValue(totalSumNoVat);
        }
    }
    
    /**
     * Sets (as xml) the "TotalSumNoVat" element
     */
    public void xsetTotalSumNoVat(org.apache.xmlbeans.XmlString totalSumNoVat)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TOTALSUMNOVAT$28, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(TOTALSUMNOVAT$28);
            }
            target.set(totalSumNoVat);
        }
    }
    
    /**
     * Nils the "TotalSumNoVat" element
     */
    public void setNilTotalSumNoVat()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TOTALSUMNOVAT$28, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(TOTALSUMNOVAT$28);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "TotalSumNoVat" element
     */
    public void unsetTotalSumNoVat()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(TOTALSUMNOVAT$28, 0);
        }
    }
    
    /**
     * Gets the "TotalSumVat" element
     */
    public java.lang.String getTotalSumVat()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(TOTALSUMVAT$30, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "TotalSumVat" element
     */
    public org.apache.xmlbeans.XmlString xgetTotalSumVat()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TOTALSUMVAT$30, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "TotalSumVat" element
     */
    public boolean isNilTotalSumVat()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TOTALSUMVAT$30, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "TotalSumVat" element
     */
    public boolean isSetTotalSumVat()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(TOTALSUMVAT$30) != 0;
        }
    }
    
    /**
     * Sets the "TotalSumVat" element
     */
    public void setTotalSumVat(java.lang.String totalSumVat)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(TOTALSUMVAT$30, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(TOTALSUMVAT$30);
            }
            target.setStringValue(totalSumVat);
        }
    }
    
    /**
     * Sets (as xml) the "TotalSumVat" element
     */
    public void xsetTotalSumVat(org.apache.xmlbeans.XmlString totalSumVat)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TOTALSUMVAT$30, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(TOTALSUMVAT$30);
            }
            target.set(totalSumVat);
        }
    }
    
    /**
     * Nils the "TotalSumVat" element
     */
    public void setNilTotalSumVat()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TOTALSUMVAT$30, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(TOTALSUMVAT$30);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "TotalSumVat" element
     */
    public void unsetTotalSumVat()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(TOTALSUMVAT$30, 0);
        }
    }
    
    /**
     * Gets the "TotalSumWithVat" element
     */
    public java.lang.String getTotalSumWithVat()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(TOTALSUMWITHVAT$32, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "TotalSumWithVat" element
     */
    public org.apache.xmlbeans.XmlString xgetTotalSumWithVat()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TOTALSUMWITHVAT$32, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "TotalSumWithVat" element
     */
    public boolean isNilTotalSumWithVat()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TOTALSUMWITHVAT$32, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "TotalSumWithVat" element
     */
    public boolean isSetTotalSumWithVat()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(TOTALSUMWITHVAT$32) != 0;
        }
    }
    
    /**
     * Sets the "TotalSumWithVat" element
     */
    public void setTotalSumWithVat(java.lang.String totalSumWithVat)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(TOTALSUMWITHVAT$32, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(TOTALSUMWITHVAT$32);
            }
            target.setStringValue(totalSumWithVat);
        }
    }
    
    /**
     * Sets (as xml) the "TotalSumWithVat" element
     */
    public void xsetTotalSumWithVat(org.apache.xmlbeans.XmlString totalSumWithVat)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TOTALSUMWITHVAT$32, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(TOTALSUMWITHVAT$32);
            }
            target.set(totalSumWithVat);
        }
    }
    
    /**
     * Nils the "TotalSumWithVat" element
     */
    public void setNilTotalSumWithVat()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TOTALSUMWITHVAT$32, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(TOTALSUMWITHVAT$32);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "TotalSumWithVat" element
     */
    public void unsetTotalSumWithVat()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(TOTALSUMWITHVAT$32, 0);
        }
    }
    
    /**
     * Gets the "Vendor" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantWithAddress getVendor()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantWithAddress target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantWithAddress)get_store().find_element_user(VENDOR$34, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "Vendor" element
     */
    public boolean isNilVendor()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantWithAddress target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantWithAddress)get_store().find_element_user(VENDOR$34, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Vendor" element
     */
    public boolean isSetVendor()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(VENDOR$34) != 0;
        }
    }
    
    /**
     * Sets the "Vendor" element
     */
    public void setVendor(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantWithAddress vendor)
    {
        generatedSetterHelperImpl(vendor, VENDOR$34, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "Vendor" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantWithAddress addNewVendor()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantWithAddress target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantWithAddress)get_store().add_element_user(VENDOR$34);
            return target;
        }
    }
    
    /**
     * Nils the "Vendor" element
     */
    public void setNilVendor()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantWithAddress target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantWithAddress)get_store().find_element_user(VENDOR$34, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantWithAddress)get_store().add_element_user(VENDOR$34);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Vendor" element
     */
    public void unsetVendor()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(VENDOR$34, 0);
        }
    }
}
