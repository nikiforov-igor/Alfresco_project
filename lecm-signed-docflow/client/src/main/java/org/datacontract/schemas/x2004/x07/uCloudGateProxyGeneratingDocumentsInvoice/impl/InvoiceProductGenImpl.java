/*
 * XML Type:  InvoiceProductGen
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceProductGen
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.impl;
/**
 * An XML InvoiceProductGen(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice).
 *
 * This is a complex type.
 */
public class InvoiceProductGenImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceProductGen
{
    private static final long serialVersionUID = 1L;
    
    public InvoiceProductGenImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName AMOUNTNOVAT$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "AmountNoVat");
    private static final javax.xml.namespace.QName AMOUNTVAT$2 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "AmountVat");
    private static final javax.xml.namespace.QName AMOUNTWITHVAT$4 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "AmountWithVat");
    private static final javax.xml.namespace.QName COUNTRYCODES$6 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "CountryCodes");
    private static final javax.xml.namespace.QName CUSTOMSDECLARATIONNUMBERS$8 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "CustomsDeclarationNumbers");
    private static final javax.xml.namespace.QName EXCISEAMOUNT$10 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "ExciseAmount");
    private static final javax.xml.namespace.QName INFRORMATION$12 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "Infrormation");
    private static final javax.xml.namespace.QName MEASUREUNIT$14 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "MeasureUnit");
    private static final javax.xml.namespace.QName NAME$16 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "Name");
    private static final javax.xml.namespace.QName PRICE$18 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "Price");
    private static final javax.xml.namespace.QName QUANTITY$20 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "Quantity");
    private static final javax.xml.namespace.QName ROWNUMBER$22 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "RowNumber");
    private static final javax.xml.namespace.QName TAXRATE$24 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "TaxRate");
    
    
    /**
     * Gets the "AmountNoVat" element
     */
    public java.lang.String getAmountNoVat()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(AMOUNTNOVAT$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "AmountNoVat" element
     */
    public org.apache.xmlbeans.XmlString xgetAmountNoVat()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(AMOUNTNOVAT$0, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "AmountNoVat" element
     */
    public boolean isNilAmountNoVat()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(AMOUNTNOVAT$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "AmountNoVat" element
     */
    public boolean isSetAmountNoVat()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(AMOUNTNOVAT$0) != 0;
        }
    }
    
    /**
     * Sets the "AmountNoVat" element
     */
    public void setAmountNoVat(java.lang.String amountNoVat)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(AMOUNTNOVAT$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(AMOUNTNOVAT$0);
            }
            target.setStringValue(amountNoVat);
        }
    }
    
    /**
     * Sets (as xml) the "AmountNoVat" element
     */
    public void xsetAmountNoVat(org.apache.xmlbeans.XmlString amountNoVat)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(AMOUNTNOVAT$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(AMOUNTNOVAT$0);
            }
            target.set(amountNoVat);
        }
    }
    
    /**
     * Nils the "AmountNoVat" element
     */
    public void setNilAmountNoVat()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(AMOUNTNOVAT$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(AMOUNTNOVAT$0);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "AmountNoVat" element
     */
    public void unsetAmountNoVat()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(AMOUNTNOVAT$0, 0);
        }
    }
    
    /**
     * Gets the "AmountVat" element
     */
    public java.lang.String getAmountVat()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(AMOUNTVAT$2, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "AmountVat" element
     */
    public org.apache.xmlbeans.XmlString xgetAmountVat()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(AMOUNTVAT$2, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "AmountVat" element
     */
    public boolean isNilAmountVat()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(AMOUNTVAT$2, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "AmountVat" element
     */
    public boolean isSetAmountVat()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(AMOUNTVAT$2) != 0;
        }
    }
    
    /**
     * Sets the "AmountVat" element
     */
    public void setAmountVat(java.lang.String amountVat)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(AMOUNTVAT$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(AMOUNTVAT$2);
            }
            target.setStringValue(amountVat);
        }
    }
    
    /**
     * Sets (as xml) the "AmountVat" element
     */
    public void xsetAmountVat(org.apache.xmlbeans.XmlString amountVat)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(AMOUNTVAT$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(AMOUNTVAT$2);
            }
            target.set(amountVat);
        }
    }
    
    /**
     * Nils the "AmountVat" element
     */
    public void setNilAmountVat()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(AMOUNTVAT$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(AMOUNTVAT$2);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "AmountVat" element
     */
    public void unsetAmountVat()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(AMOUNTVAT$2, 0);
        }
    }
    
    /**
     * Gets the "AmountWithVat" element
     */
    public java.lang.String getAmountWithVat()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(AMOUNTWITHVAT$4, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "AmountWithVat" element
     */
    public org.apache.xmlbeans.XmlString xgetAmountWithVat()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(AMOUNTWITHVAT$4, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "AmountWithVat" element
     */
    public boolean isNilAmountWithVat()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(AMOUNTWITHVAT$4, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "AmountWithVat" element
     */
    public boolean isSetAmountWithVat()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(AMOUNTWITHVAT$4) != 0;
        }
    }
    
    /**
     * Sets the "AmountWithVat" element
     */
    public void setAmountWithVat(java.lang.String amountWithVat)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(AMOUNTWITHVAT$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(AMOUNTWITHVAT$4);
            }
            target.setStringValue(amountWithVat);
        }
    }
    
    /**
     * Sets (as xml) the "AmountWithVat" element
     */
    public void xsetAmountWithVat(org.apache.xmlbeans.XmlString amountWithVat)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(AMOUNTWITHVAT$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(AMOUNTWITHVAT$4);
            }
            target.set(amountWithVat);
        }
    }
    
    /**
     * Nils the "AmountWithVat" element
     */
    public void setNilAmountWithVat()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(AMOUNTWITHVAT$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(AMOUNTWITHVAT$4);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "AmountWithVat" element
     */
    public void unsetAmountWithVat()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(AMOUNTWITHVAT$4, 0);
        }
    }
    
    /**
     * Gets the "CountryCodes" element
     */
    public com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring getCountryCodes()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring target = null;
            target = (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring)get_store().find_element_user(COUNTRYCODES$6, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "CountryCodes" element
     */
    public boolean isNilCountryCodes()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring target = null;
            target = (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring)get_store().find_element_user(COUNTRYCODES$6, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "CountryCodes" element
     */
    public boolean isSetCountryCodes()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(COUNTRYCODES$6) != 0;
        }
    }
    
    /**
     * Sets the "CountryCodes" element
     */
    public void setCountryCodes(com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring countryCodes)
    {
        generatedSetterHelperImpl(countryCodes, COUNTRYCODES$6, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "CountryCodes" element
     */
    public com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring addNewCountryCodes()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring target = null;
            target = (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring)get_store().add_element_user(COUNTRYCODES$6);
            return target;
        }
    }
    
    /**
     * Nils the "CountryCodes" element
     */
    public void setNilCountryCodes()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring target = null;
            target = (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring)get_store().find_element_user(COUNTRYCODES$6, 0);
            if (target == null)
            {
                target = (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring)get_store().add_element_user(COUNTRYCODES$6);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "CountryCodes" element
     */
    public void unsetCountryCodes()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(COUNTRYCODES$6, 0);
        }
    }
    
    /**
     * Gets the "CustomsDeclarationNumbers" element
     */
    public com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring getCustomsDeclarationNumbers()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring target = null;
            target = (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring)get_store().find_element_user(CUSTOMSDECLARATIONNUMBERS$8, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "CustomsDeclarationNumbers" element
     */
    public boolean isNilCustomsDeclarationNumbers()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring target = null;
            target = (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring)get_store().find_element_user(CUSTOMSDECLARATIONNUMBERS$8, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "CustomsDeclarationNumbers" element
     */
    public boolean isSetCustomsDeclarationNumbers()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(CUSTOMSDECLARATIONNUMBERS$8) != 0;
        }
    }
    
    /**
     * Sets the "CustomsDeclarationNumbers" element
     */
    public void setCustomsDeclarationNumbers(com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring customsDeclarationNumbers)
    {
        generatedSetterHelperImpl(customsDeclarationNumbers, CUSTOMSDECLARATIONNUMBERS$8, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "CustomsDeclarationNumbers" element
     */
    public com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring addNewCustomsDeclarationNumbers()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring target = null;
            target = (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring)get_store().add_element_user(CUSTOMSDECLARATIONNUMBERS$8);
            return target;
        }
    }
    
    /**
     * Nils the "CustomsDeclarationNumbers" element
     */
    public void setNilCustomsDeclarationNumbers()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring target = null;
            target = (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring)get_store().find_element_user(CUSTOMSDECLARATIONNUMBERS$8, 0);
            if (target == null)
            {
                target = (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring)get_store().add_element_user(CUSTOMSDECLARATIONNUMBERS$8);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "CustomsDeclarationNumbers" element
     */
    public void unsetCustomsDeclarationNumbers()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(CUSTOMSDECLARATIONNUMBERS$8, 0);
        }
    }
    
    /**
     * Gets the "ExciseAmount" element
     */
    public java.lang.String getExciseAmount()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(EXCISEAMOUNT$10, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "ExciseAmount" element
     */
    public org.apache.xmlbeans.XmlString xgetExciseAmount()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(EXCISEAMOUNT$10, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "ExciseAmount" element
     */
    public boolean isNilExciseAmount()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(EXCISEAMOUNT$10, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "ExciseAmount" element
     */
    public boolean isSetExciseAmount()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(EXCISEAMOUNT$10) != 0;
        }
    }
    
    /**
     * Sets the "ExciseAmount" element
     */
    public void setExciseAmount(java.lang.String exciseAmount)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(EXCISEAMOUNT$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(EXCISEAMOUNT$10);
            }
            target.setStringValue(exciseAmount);
        }
    }
    
    /**
     * Sets (as xml) the "ExciseAmount" element
     */
    public void xsetExciseAmount(org.apache.xmlbeans.XmlString exciseAmount)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(EXCISEAMOUNT$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(EXCISEAMOUNT$10);
            }
            target.set(exciseAmount);
        }
    }
    
    /**
     * Nils the "ExciseAmount" element
     */
    public void setNilExciseAmount()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(EXCISEAMOUNT$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(EXCISEAMOUNT$10);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "ExciseAmount" element
     */
    public void unsetExciseAmount()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(EXCISEAMOUNT$10, 0);
        }
    }
    
    /**
     * Gets the "Infrormation" element
     */
    public java.lang.String getInfrormation()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(INFRORMATION$12, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Infrormation" element
     */
    public org.apache.xmlbeans.XmlString xgetInfrormation()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INFRORMATION$12, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "Infrormation" element
     */
    public boolean isNilInfrormation()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INFRORMATION$12, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Infrormation" element
     */
    public boolean isSetInfrormation()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(INFRORMATION$12) != 0;
        }
    }
    
    /**
     * Sets the "Infrormation" element
     */
    public void setInfrormation(java.lang.String infrormation)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(INFRORMATION$12, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(INFRORMATION$12);
            }
            target.setStringValue(infrormation);
        }
    }
    
    /**
     * Sets (as xml) the "Infrormation" element
     */
    public void xsetInfrormation(org.apache.xmlbeans.XmlString infrormation)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INFRORMATION$12, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(INFRORMATION$12);
            }
            target.set(infrormation);
        }
    }
    
    /**
     * Nils the "Infrormation" element
     */
    public void setNilInfrormation()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INFRORMATION$12, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(INFRORMATION$12);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Infrormation" element
     */
    public void unsetInfrormation()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(INFRORMATION$12, 0);
        }
    }
    
    /**
     * Gets the "MeasureUnit" element
     */
    public java.lang.String getMeasureUnit()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(MEASUREUNIT$14, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "MeasureUnit" element
     */
    public org.apache.xmlbeans.XmlString xgetMeasureUnit()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(MEASUREUNIT$14, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "MeasureUnit" element
     */
    public boolean isNilMeasureUnit()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(MEASUREUNIT$14, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "MeasureUnit" element
     */
    public boolean isSetMeasureUnit()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(MEASUREUNIT$14) != 0;
        }
    }
    
    /**
     * Sets the "MeasureUnit" element
     */
    public void setMeasureUnit(java.lang.String measureUnit)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(MEASUREUNIT$14, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(MEASUREUNIT$14);
            }
            target.setStringValue(measureUnit);
        }
    }
    
    /**
     * Sets (as xml) the "MeasureUnit" element
     */
    public void xsetMeasureUnit(org.apache.xmlbeans.XmlString measureUnit)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(MEASUREUNIT$14, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(MEASUREUNIT$14);
            }
            target.set(measureUnit);
        }
    }
    
    /**
     * Nils the "MeasureUnit" element
     */
    public void setNilMeasureUnit()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(MEASUREUNIT$14, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(MEASUREUNIT$14);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "MeasureUnit" element
     */
    public void unsetMeasureUnit()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(MEASUREUNIT$14, 0);
        }
    }
    
    /**
     * Gets the "Name" element
     */
    public java.lang.String getName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(NAME$16, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Name" element
     */
    public org.apache.xmlbeans.XmlString xgetName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NAME$16, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "Name" element
     */
    public boolean isNilName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NAME$16, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Name" element
     */
    public boolean isSetName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(NAME$16) != 0;
        }
    }
    
    /**
     * Sets the "Name" element
     */
    public void setName(java.lang.String name)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(NAME$16, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(NAME$16);
            }
            target.setStringValue(name);
        }
    }
    
    /**
     * Sets (as xml) the "Name" element
     */
    public void xsetName(org.apache.xmlbeans.XmlString name)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NAME$16, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(NAME$16);
            }
            target.set(name);
        }
    }
    
    /**
     * Nils the "Name" element
     */
    public void setNilName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NAME$16, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(NAME$16);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Name" element
     */
    public void unsetName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(NAME$16, 0);
        }
    }
    
    /**
     * Gets the "Price" element
     */
    public java.lang.String getPrice()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PRICE$18, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Price" element
     */
    public org.apache.xmlbeans.XmlString xgetPrice()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PRICE$18, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "Price" element
     */
    public boolean isNilPrice()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PRICE$18, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Price" element
     */
    public boolean isSetPrice()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(PRICE$18) != 0;
        }
    }
    
    /**
     * Sets the "Price" element
     */
    public void setPrice(java.lang.String price)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PRICE$18, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PRICE$18);
            }
            target.setStringValue(price);
        }
    }
    
    /**
     * Sets (as xml) the "Price" element
     */
    public void xsetPrice(org.apache.xmlbeans.XmlString price)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PRICE$18, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PRICE$18);
            }
            target.set(price);
        }
    }
    
    /**
     * Nils the "Price" element
     */
    public void setNilPrice()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PRICE$18, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PRICE$18);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Price" element
     */
    public void unsetPrice()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(PRICE$18, 0);
        }
    }
    
    /**
     * Gets the "Quantity" element
     */
    public java.lang.String getQuantity()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(QUANTITY$20, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Quantity" element
     */
    public org.apache.xmlbeans.XmlString xgetQuantity()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(QUANTITY$20, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "Quantity" element
     */
    public boolean isNilQuantity()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(QUANTITY$20, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Quantity" element
     */
    public boolean isSetQuantity()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(QUANTITY$20) != 0;
        }
    }
    
    /**
     * Sets the "Quantity" element
     */
    public void setQuantity(java.lang.String quantity)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(QUANTITY$20, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(QUANTITY$20);
            }
            target.setStringValue(quantity);
        }
    }
    
    /**
     * Sets (as xml) the "Quantity" element
     */
    public void xsetQuantity(org.apache.xmlbeans.XmlString quantity)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(QUANTITY$20, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(QUANTITY$20);
            }
            target.set(quantity);
        }
    }
    
    /**
     * Nils the "Quantity" element
     */
    public void setNilQuantity()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(QUANTITY$20, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(QUANTITY$20);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Quantity" element
     */
    public void unsetQuantity()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(QUANTITY$20, 0);
        }
    }
    
    /**
     * Gets the "RowNumber" element
     */
    public java.lang.String getRowNumber()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ROWNUMBER$22, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "RowNumber" element
     */
    public org.apache.xmlbeans.XmlString xgetRowNumber()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ROWNUMBER$22, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "RowNumber" element
     */
    public boolean isNilRowNumber()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ROWNUMBER$22, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "RowNumber" element
     */
    public boolean isSetRowNumber()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(ROWNUMBER$22) != 0;
        }
    }
    
    /**
     * Sets the "RowNumber" element
     */
    public void setRowNumber(java.lang.String rowNumber)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ROWNUMBER$22, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ROWNUMBER$22);
            }
            target.setStringValue(rowNumber);
        }
    }
    
    /**
     * Sets (as xml) the "RowNumber" element
     */
    public void xsetRowNumber(org.apache.xmlbeans.XmlString rowNumber)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ROWNUMBER$22, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(ROWNUMBER$22);
            }
            target.set(rowNumber);
        }
    }
    
    /**
     * Nils the "RowNumber" element
     */
    public void setNilRowNumber()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ROWNUMBER$22, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(ROWNUMBER$22);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "RowNumber" element
     */
    public void unsetRowNumber()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(ROWNUMBER$22, 0);
        }
    }
    
    /**
     * Gets the "TaxRate" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyCommon.ETaxRate.Enum getTaxRate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(TAXRATE$24, 0);
            if (target == null)
            {
                return null;
            }
            return (org.datacontract.schemas.x2004.x07.uCloudGateProxyCommon.ETaxRate.Enum)target.getEnumValue();
        }
    }
    
    /**
     * Gets (as xml) the "TaxRate" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyCommon.ETaxRate xgetTaxRate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyCommon.ETaxRate target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyCommon.ETaxRate)get_store().find_element_user(TAXRATE$24, 0);
            return target;
        }
    }
    
    /**
     * True if has "TaxRate" element
     */
    public boolean isSetTaxRate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(TAXRATE$24) != 0;
        }
    }
    
    /**
     * Sets the "TaxRate" element
     */
    public void setTaxRate(org.datacontract.schemas.x2004.x07.uCloudGateProxyCommon.ETaxRate.Enum taxRate)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(TAXRATE$24, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(TAXRATE$24);
            }
            target.setEnumValue(taxRate);
        }
    }
    
    /**
     * Sets (as xml) the "TaxRate" element
     */
    public void xsetTaxRate(org.datacontract.schemas.x2004.x07.uCloudGateProxyCommon.ETaxRate taxRate)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyCommon.ETaxRate target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyCommon.ETaxRate)get_store().find_element_user(TAXRATE$24, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyCommon.ETaxRate)get_store().add_element_user(TAXRATE$24);
            }
            target.set(taxRate);
        }
    }
    
    /**
     * Unsets the "TaxRate" element
     */
    public void unsetTaxRate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(TAXRATE$24, 0);
        }
    }
}
