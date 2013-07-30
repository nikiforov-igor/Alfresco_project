/*
 * XML Type:  Torg12BuyerTitleInfo
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12BuyerTitleInfo
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.impl;
/**
 * An XML Torg12BuyerTitleInfo(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses).
 *
 * This is a complex type.
 */
public class Torg12BuyerTitleInfoImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12BuyerTitleInfo
{
    private static final long serialVersionUID = 1L;
    
    public Torg12BuyerTitleInfoImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName ACCEPTEDBY$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "AcceptedBy");
    private static final javax.xml.namespace.QName ADDITIONALINFO$2 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "AdditionalInfo");
    private static final javax.xml.namespace.QName ATTORNEY$4 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "Attorney");
    private static final javax.xml.namespace.QName RECEIVEDBY$6 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "ReceivedBy");
    private static final javax.xml.namespace.QName SHIPMENTRECEIPTDATE$8 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "ShipmentReceiptDate");
    private static final javax.xml.namespace.QName SIGNER$10 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "Signer");
    
    
    /**
     * Gets the "AcceptedBy" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official getAcceptedBy()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official)get_store().find_element_user(ACCEPTEDBY$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "AcceptedBy" element
     */
    public boolean isNilAcceptedBy()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official)get_store().find_element_user(ACCEPTEDBY$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "AcceptedBy" element
     */
    public boolean isSetAcceptedBy()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(ACCEPTEDBY$0) != 0;
        }
    }
    
    /**
     * Sets the "AcceptedBy" element
     */
    public void setAcceptedBy(org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official acceptedBy)
    {
        generatedSetterHelperImpl(acceptedBy, ACCEPTEDBY$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "AcceptedBy" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official addNewAcceptedBy()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official)get_store().add_element_user(ACCEPTEDBY$0);
            return target;
        }
    }
    
    /**
     * Nils the "AcceptedBy" element
     */
    public void setNilAcceptedBy()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official)get_store().find_element_user(ACCEPTEDBY$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official)get_store().add_element_user(ACCEPTEDBY$0);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "AcceptedBy" element
     */
    public void unsetAcceptedBy()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(ACCEPTEDBY$0, 0);
        }
    }
    
    /**
     * Gets the "AdditionalInfo" element
     */
    public java.lang.String getAdditionalInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ADDITIONALINFO$2, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ADDITIONALINFO$2, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ADDITIONALINFO$2, 0);
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
            return get_store().count_elements(ADDITIONALINFO$2) != 0;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ADDITIONALINFO$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ADDITIONALINFO$2);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ADDITIONALINFO$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(ADDITIONALINFO$2);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ADDITIONALINFO$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(ADDITIONALINFO$2);
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
            get_store().remove_element(ADDITIONALINFO$2, 0);
        }
    }
    
    /**
     * Gets the "Attorney" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.Attorney getAttorney()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.Attorney target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.Attorney)get_store().find_element_user(ATTORNEY$4, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "Attorney" element
     */
    public boolean isNilAttorney()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.Attorney target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.Attorney)get_store().find_element_user(ATTORNEY$4, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Attorney" element
     */
    public boolean isSetAttorney()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(ATTORNEY$4) != 0;
        }
    }
    
    /**
     * Sets the "Attorney" element
     */
    public void setAttorney(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.Attorney attorney)
    {
        generatedSetterHelperImpl(attorney, ATTORNEY$4, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "Attorney" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.Attorney addNewAttorney()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.Attorney target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.Attorney)get_store().add_element_user(ATTORNEY$4);
            return target;
        }
    }
    
    /**
     * Nils the "Attorney" element
     */
    public void setNilAttorney()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.Attorney target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.Attorney)get_store().find_element_user(ATTORNEY$4, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.Attorney)get_store().add_element_user(ATTORNEY$4);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Attorney" element
     */
    public void unsetAttorney()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(ATTORNEY$4, 0);
        }
    }
    
    /**
     * Gets the "ReceivedBy" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official getReceivedBy()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official)get_store().find_element_user(RECEIVEDBY$6, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "ReceivedBy" element
     */
    public boolean isNilReceivedBy()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official)get_store().find_element_user(RECEIVEDBY$6, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "ReceivedBy" element
     */
    public boolean isSetReceivedBy()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(RECEIVEDBY$6) != 0;
        }
    }
    
    /**
     * Sets the "ReceivedBy" element
     */
    public void setReceivedBy(org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official receivedBy)
    {
        generatedSetterHelperImpl(receivedBy, RECEIVEDBY$6, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "ReceivedBy" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official addNewReceivedBy()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official)get_store().add_element_user(RECEIVEDBY$6);
            return target;
        }
    }
    
    /**
     * Nils the "ReceivedBy" element
     */
    public void setNilReceivedBy()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official)get_store().find_element_user(RECEIVEDBY$6, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official)get_store().add_element_user(RECEIVEDBY$6);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "ReceivedBy" element
     */
    public void unsetReceivedBy()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(RECEIVEDBY$6, 0);
        }
    }
    
    /**
     * Gets the "ShipmentReceiptDate" element
     */
    public java.lang.String getShipmentReceiptDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SHIPMENTRECEIPTDATE$8, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "ShipmentReceiptDate" element
     */
    public org.apache.xmlbeans.XmlString xgetShipmentReceiptDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SHIPMENTRECEIPTDATE$8, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "ShipmentReceiptDate" element
     */
    public boolean isNilShipmentReceiptDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SHIPMENTRECEIPTDATE$8, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "ShipmentReceiptDate" element
     */
    public boolean isSetShipmentReceiptDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(SHIPMENTRECEIPTDATE$8) != 0;
        }
    }
    
    /**
     * Sets the "ShipmentReceiptDate" element
     */
    public void setShipmentReceiptDate(java.lang.String shipmentReceiptDate)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SHIPMENTRECEIPTDATE$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(SHIPMENTRECEIPTDATE$8);
            }
            target.setStringValue(shipmentReceiptDate);
        }
    }
    
    /**
     * Sets (as xml) the "ShipmentReceiptDate" element
     */
    public void xsetShipmentReceiptDate(org.apache.xmlbeans.XmlString shipmentReceiptDate)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SHIPMENTRECEIPTDATE$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(SHIPMENTRECEIPTDATE$8);
            }
            target.set(shipmentReceiptDate);
        }
    }
    
    /**
     * Nils the "ShipmentReceiptDate" element
     */
    public void setNilShipmentReceiptDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SHIPMENTRECEIPTDATE$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(SHIPMENTRECEIPTDATE$8);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "ShipmentReceiptDate" element
     */
    public void unsetShipmentReceiptDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(SHIPMENTRECEIPTDATE$8, 0);
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
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Signer)get_store().find_element_user(SIGNER$10, 0);
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
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Signer)get_store().find_element_user(SIGNER$10, 0);
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
            return get_store().count_elements(SIGNER$10) != 0;
        }
    }
    
    /**
     * Sets the "Signer" element
     */
    public void setSigner(org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Signer signer)
    {
        generatedSetterHelperImpl(signer, SIGNER$10, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
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
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Signer)get_store().add_element_user(SIGNER$10);
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
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Signer)get_store().find_element_user(SIGNER$10, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Signer)get_store().add_element_user(SIGNER$10);
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
            get_store().remove_element(SIGNER$10, 0);
        }
    }
}
