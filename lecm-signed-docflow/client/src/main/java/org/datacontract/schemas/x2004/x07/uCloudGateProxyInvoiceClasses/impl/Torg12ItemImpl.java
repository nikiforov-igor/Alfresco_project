/*
 * XML Type:  Torg12Item
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12Item
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.impl;
/**
 * An XML Torg12Item(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses).
 *
 * This is a complex type.
 */
public class Torg12ItemImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12Item
{
    private static final long serialVersionUID = 1L;
    
    public Torg12ItemImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName ADDITIONALINFO$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "AdditionalInfo");
    private static final javax.xml.namespace.QName CODE$2 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "Code");
    private static final javax.xml.namespace.QName FEATURE$4 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "Feature");
    private static final javax.xml.namespace.QName GROSSQUANTITY$6 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "GrossQuantity");
    private static final javax.xml.namespace.QName NAME$8 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "Name");
    private static final javax.xml.namespace.QName NOMENCLATUREARTICLE$10 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "NomenclatureArticle");
    private static final javax.xml.namespace.QName PARCELCAPACITY$12 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "ParcelCapacity");
    private static final javax.xml.namespace.QName PARCELTYPE$14 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "ParcelType");
    private static final javax.xml.namespace.QName PARCELSQUANTITY$16 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "ParcelsQuantity");
    private static final javax.xml.namespace.QName PRICE$18 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "Price");
    private static final javax.xml.namespace.QName QUANTITY$20 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "Quantity");
    private static final javax.xml.namespace.QName SORT$22 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "Sort");
    private static final javax.xml.namespace.QName SUBTOTAL$24 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "Subtotal");
    private static final javax.xml.namespace.QName SUBTOTALWITHVATEXCLUDED$26 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "SubtotalWithVatExcluded");
    private static final javax.xml.namespace.QName TAXRATE$28 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "TaxRate");
    private static final javax.xml.namespace.QName UNITCODE$30 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "UnitCode");
    private static final javax.xml.namespace.QName UNITNAME$32 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "UnitName");
    private static final javax.xml.namespace.QName VAT$34 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "Vat");
    
    
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
     * Gets the "Code" element
     */
    public java.lang.String getCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CODE$2, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Code" element
     */
    public org.apache.xmlbeans.XmlString xgetCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(CODE$2, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "Code" element
     */
    public boolean isNilCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(CODE$2, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Code" element
     */
    public boolean isSetCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(CODE$2) != 0;
        }
    }
    
    /**
     * Sets the "Code" element
     */
    public void setCode(java.lang.String code)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CODE$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(CODE$2);
            }
            target.setStringValue(code);
        }
    }
    
    /**
     * Sets (as xml) the "Code" element
     */
    public void xsetCode(org.apache.xmlbeans.XmlString code)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(CODE$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(CODE$2);
            }
            target.set(code);
        }
    }
    
    /**
     * Nils the "Code" element
     */
    public void setNilCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(CODE$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(CODE$2);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Code" element
     */
    public void unsetCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(CODE$2, 0);
        }
    }
    
    /**
     * Gets the "Feature" element
     */
    public java.lang.String getFeature()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(FEATURE$4, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Feature" element
     */
    public org.apache.xmlbeans.XmlString xgetFeature()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FEATURE$4, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "Feature" element
     */
    public boolean isNilFeature()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FEATURE$4, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Feature" element
     */
    public boolean isSetFeature()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(FEATURE$4) != 0;
        }
    }
    
    /**
     * Sets the "Feature" element
     */
    public void setFeature(java.lang.String feature)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(FEATURE$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(FEATURE$4);
            }
            target.setStringValue(feature);
        }
    }
    
    /**
     * Sets (as xml) the "Feature" element
     */
    public void xsetFeature(org.apache.xmlbeans.XmlString feature)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FEATURE$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(FEATURE$4);
            }
            target.set(feature);
        }
    }
    
    /**
     * Nils the "Feature" element
     */
    public void setNilFeature()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FEATURE$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(FEATURE$4);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Feature" element
     */
    public void unsetFeature()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(FEATURE$4, 0);
        }
    }
    
    /**
     * Gets the "GrossQuantity" element
     */
    public java.lang.String getGrossQuantity()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(GROSSQUANTITY$6, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "GrossQuantity" element
     */
    public org.apache.xmlbeans.XmlString xgetGrossQuantity()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(GROSSQUANTITY$6, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "GrossQuantity" element
     */
    public boolean isNilGrossQuantity()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(GROSSQUANTITY$6, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "GrossQuantity" element
     */
    public boolean isSetGrossQuantity()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(GROSSQUANTITY$6) != 0;
        }
    }
    
    /**
     * Sets the "GrossQuantity" element
     */
    public void setGrossQuantity(java.lang.String grossQuantity)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(GROSSQUANTITY$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(GROSSQUANTITY$6);
            }
            target.setStringValue(grossQuantity);
        }
    }
    
    /**
     * Sets (as xml) the "GrossQuantity" element
     */
    public void xsetGrossQuantity(org.apache.xmlbeans.XmlString grossQuantity)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(GROSSQUANTITY$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(GROSSQUANTITY$6);
            }
            target.set(grossQuantity);
        }
    }
    
    /**
     * Nils the "GrossQuantity" element
     */
    public void setNilGrossQuantity()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(GROSSQUANTITY$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(GROSSQUANTITY$6);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "GrossQuantity" element
     */
    public void unsetGrossQuantity()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(GROSSQUANTITY$6, 0);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(NAME$8, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NAME$8, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NAME$8, 0);
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
            return get_store().count_elements(NAME$8) != 0;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(NAME$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(NAME$8);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NAME$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(NAME$8);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NAME$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(NAME$8);
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
            get_store().remove_element(NAME$8, 0);
        }
    }
    
    /**
     * Gets the "NomenclatureArticle" element
     */
    public java.lang.String getNomenclatureArticle()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(NOMENCLATUREARTICLE$10, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "NomenclatureArticle" element
     */
    public org.apache.xmlbeans.XmlString xgetNomenclatureArticle()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NOMENCLATUREARTICLE$10, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "NomenclatureArticle" element
     */
    public boolean isNilNomenclatureArticle()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NOMENCLATUREARTICLE$10, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "NomenclatureArticle" element
     */
    public boolean isSetNomenclatureArticle()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(NOMENCLATUREARTICLE$10) != 0;
        }
    }
    
    /**
     * Sets the "NomenclatureArticle" element
     */
    public void setNomenclatureArticle(java.lang.String nomenclatureArticle)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(NOMENCLATUREARTICLE$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(NOMENCLATUREARTICLE$10);
            }
            target.setStringValue(nomenclatureArticle);
        }
    }
    
    /**
     * Sets (as xml) the "NomenclatureArticle" element
     */
    public void xsetNomenclatureArticle(org.apache.xmlbeans.XmlString nomenclatureArticle)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NOMENCLATUREARTICLE$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(NOMENCLATUREARTICLE$10);
            }
            target.set(nomenclatureArticle);
        }
    }
    
    /**
     * Nils the "NomenclatureArticle" element
     */
    public void setNilNomenclatureArticle()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NOMENCLATUREARTICLE$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(NOMENCLATUREARTICLE$10);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "NomenclatureArticle" element
     */
    public void unsetNomenclatureArticle()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(NOMENCLATUREARTICLE$10, 0);
        }
    }
    
    /**
     * Gets the "ParcelCapacity" element
     */
    public java.lang.String getParcelCapacity()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PARCELCAPACITY$12, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "ParcelCapacity" element
     */
    public org.apache.xmlbeans.XmlString xgetParcelCapacity()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PARCELCAPACITY$12, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "ParcelCapacity" element
     */
    public boolean isNilParcelCapacity()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PARCELCAPACITY$12, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "ParcelCapacity" element
     */
    public boolean isSetParcelCapacity()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(PARCELCAPACITY$12) != 0;
        }
    }
    
    /**
     * Sets the "ParcelCapacity" element
     */
    public void setParcelCapacity(java.lang.String parcelCapacity)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PARCELCAPACITY$12, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PARCELCAPACITY$12);
            }
            target.setStringValue(parcelCapacity);
        }
    }
    
    /**
     * Sets (as xml) the "ParcelCapacity" element
     */
    public void xsetParcelCapacity(org.apache.xmlbeans.XmlString parcelCapacity)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PARCELCAPACITY$12, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PARCELCAPACITY$12);
            }
            target.set(parcelCapacity);
        }
    }
    
    /**
     * Nils the "ParcelCapacity" element
     */
    public void setNilParcelCapacity()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PARCELCAPACITY$12, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PARCELCAPACITY$12);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "ParcelCapacity" element
     */
    public void unsetParcelCapacity()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(PARCELCAPACITY$12, 0);
        }
    }
    
    /**
     * Gets the "ParcelType" element
     */
    public java.lang.String getParcelType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PARCELTYPE$14, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "ParcelType" element
     */
    public org.apache.xmlbeans.XmlString xgetParcelType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PARCELTYPE$14, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "ParcelType" element
     */
    public boolean isNilParcelType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PARCELTYPE$14, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "ParcelType" element
     */
    public boolean isSetParcelType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(PARCELTYPE$14) != 0;
        }
    }
    
    /**
     * Sets the "ParcelType" element
     */
    public void setParcelType(java.lang.String parcelType)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PARCELTYPE$14, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PARCELTYPE$14);
            }
            target.setStringValue(parcelType);
        }
    }
    
    /**
     * Sets (as xml) the "ParcelType" element
     */
    public void xsetParcelType(org.apache.xmlbeans.XmlString parcelType)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PARCELTYPE$14, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PARCELTYPE$14);
            }
            target.set(parcelType);
        }
    }
    
    /**
     * Nils the "ParcelType" element
     */
    public void setNilParcelType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PARCELTYPE$14, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PARCELTYPE$14);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "ParcelType" element
     */
    public void unsetParcelType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(PARCELTYPE$14, 0);
        }
    }
    
    /**
     * Gets the "ParcelsQuantity" element
     */
    public java.lang.String getParcelsQuantity()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PARCELSQUANTITY$16, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "ParcelsQuantity" element
     */
    public org.apache.xmlbeans.XmlString xgetParcelsQuantity()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PARCELSQUANTITY$16, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "ParcelsQuantity" element
     */
    public boolean isNilParcelsQuantity()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PARCELSQUANTITY$16, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "ParcelsQuantity" element
     */
    public boolean isSetParcelsQuantity()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(PARCELSQUANTITY$16) != 0;
        }
    }
    
    /**
     * Sets the "ParcelsQuantity" element
     */
    public void setParcelsQuantity(java.lang.String parcelsQuantity)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PARCELSQUANTITY$16, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PARCELSQUANTITY$16);
            }
            target.setStringValue(parcelsQuantity);
        }
    }
    
    /**
     * Sets (as xml) the "ParcelsQuantity" element
     */
    public void xsetParcelsQuantity(org.apache.xmlbeans.XmlString parcelsQuantity)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PARCELSQUANTITY$16, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PARCELSQUANTITY$16);
            }
            target.set(parcelsQuantity);
        }
    }
    
    /**
     * Nils the "ParcelsQuantity" element
     */
    public void setNilParcelsQuantity()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PARCELSQUANTITY$16, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PARCELSQUANTITY$16);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "ParcelsQuantity" element
     */
    public void unsetParcelsQuantity()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(PARCELSQUANTITY$16, 0);
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
     * Gets the "Sort" element
     */
    public java.lang.String getSort()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SORT$22, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Sort" element
     */
    public org.apache.xmlbeans.XmlString xgetSort()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SORT$22, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "Sort" element
     */
    public boolean isNilSort()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SORT$22, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Sort" element
     */
    public boolean isSetSort()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(SORT$22) != 0;
        }
    }
    
    /**
     * Sets the "Sort" element
     */
    public void setSort(java.lang.String sort)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SORT$22, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(SORT$22);
            }
            target.setStringValue(sort);
        }
    }
    
    /**
     * Sets (as xml) the "Sort" element
     */
    public void xsetSort(org.apache.xmlbeans.XmlString sort)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SORT$22, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(SORT$22);
            }
            target.set(sort);
        }
    }
    
    /**
     * Nils the "Sort" element
     */
    public void setNilSort()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SORT$22, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(SORT$22);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Sort" element
     */
    public void unsetSort()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(SORT$22, 0);
        }
    }
    
    /**
     * Gets the "Subtotal" element
     */
    public java.lang.String getSubtotal()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SUBTOTAL$24, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Subtotal" element
     */
    public org.apache.xmlbeans.XmlString xgetSubtotal()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SUBTOTAL$24, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "Subtotal" element
     */
    public boolean isNilSubtotal()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SUBTOTAL$24, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Subtotal" element
     */
    public boolean isSetSubtotal()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(SUBTOTAL$24) != 0;
        }
    }
    
    /**
     * Sets the "Subtotal" element
     */
    public void setSubtotal(java.lang.String subtotal)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SUBTOTAL$24, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(SUBTOTAL$24);
            }
            target.setStringValue(subtotal);
        }
    }
    
    /**
     * Sets (as xml) the "Subtotal" element
     */
    public void xsetSubtotal(org.apache.xmlbeans.XmlString subtotal)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SUBTOTAL$24, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(SUBTOTAL$24);
            }
            target.set(subtotal);
        }
    }
    
    /**
     * Nils the "Subtotal" element
     */
    public void setNilSubtotal()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SUBTOTAL$24, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(SUBTOTAL$24);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Subtotal" element
     */
    public void unsetSubtotal()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(SUBTOTAL$24, 0);
        }
    }
    
    /**
     * Gets the "SubtotalWithVatExcluded" element
     */
    public java.lang.String getSubtotalWithVatExcluded()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SUBTOTALWITHVATEXCLUDED$26, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "SubtotalWithVatExcluded" element
     */
    public org.apache.xmlbeans.XmlString xgetSubtotalWithVatExcluded()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SUBTOTALWITHVATEXCLUDED$26, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "SubtotalWithVatExcluded" element
     */
    public boolean isNilSubtotalWithVatExcluded()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SUBTOTALWITHVATEXCLUDED$26, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "SubtotalWithVatExcluded" element
     */
    public boolean isSetSubtotalWithVatExcluded()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(SUBTOTALWITHVATEXCLUDED$26) != 0;
        }
    }
    
    /**
     * Sets the "SubtotalWithVatExcluded" element
     */
    public void setSubtotalWithVatExcluded(java.lang.String subtotalWithVatExcluded)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SUBTOTALWITHVATEXCLUDED$26, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(SUBTOTALWITHVATEXCLUDED$26);
            }
            target.setStringValue(subtotalWithVatExcluded);
        }
    }
    
    /**
     * Sets (as xml) the "SubtotalWithVatExcluded" element
     */
    public void xsetSubtotalWithVatExcluded(org.apache.xmlbeans.XmlString subtotalWithVatExcluded)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SUBTOTALWITHVATEXCLUDED$26, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(SUBTOTALWITHVATEXCLUDED$26);
            }
            target.set(subtotalWithVatExcluded);
        }
    }
    
    /**
     * Nils the "SubtotalWithVatExcluded" element
     */
    public void setNilSubtotalWithVatExcluded()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SUBTOTALWITHVATEXCLUDED$26, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(SUBTOTALWITHVATEXCLUDED$26);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "SubtotalWithVatExcluded" element
     */
    public void unsetSubtotalWithVatExcluded()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(SUBTOTALWITHVATEXCLUDED$26, 0);
        }
    }
    
    /**
     * Gets the "TaxRate" element
     */
    public java.lang.String getTaxRate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(TAXRATE$28, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "TaxRate" element
     */
    public org.apache.xmlbeans.XmlString xgetTaxRate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TAXRATE$28, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "TaxRate" element
     */
    public boolean isNilTaxRate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TAXRATE$28, 0);
            if (target == null) return false;
            return target.isNil();
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
            return get_store().count_elements(TAXRATE$28) != 0;
        }
    }
    
    /**
     * Sets the "TaxRate" element
     */
    public void setTaxRate(java.lang.String taxRate)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(TAXRATE$28, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(TAXRATE$28);
            }
            target.setStringValue(taxRate);
        }
    }
    
    /**
     * Sets (as xml) the "TaxRate" element
     */
    public void xsetTaxRate(org.apache.xmlbeans.XmlString taxRate)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TAXRATE$28, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(TAXRATE$28);
            }
            target.set(taxRate);
        }
    }
    
    /**
     * Nils the "TaxRate" element
     */
    public void setNilTaxRate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TAXRATE$28, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(TAXRATE$28);
            }
            target.setNil();
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
            get_store().remove_element(TAXRATE$28, 0);
        }
    }
    
    /**
     * Gets the "UnitCode" element
     */
    public java.lang.String getUnitCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(UNITCODE$30, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "UnitCode" element
     */
    public org.apache.xmlbeans.XmlString xgetUnitCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(UNITCODE$30, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "UnitCode" element
     */
    public boolean isNilUnitCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(UNITCODE$30, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "UnitCode" element
     */
    public boolean isSetUnitCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(UNITCODE$30) != 0;
        }
    }
    
    /**
     * Sets the "UnitCode" element
     */
    public void setUnitCode(java.lang.String unitCode)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(UNITCODE$30, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(UNITCODE$30);
            }
            target.setStringValue(unitCode);
        }
    }
    
    /**
     * Sets (as xml) the "UnitCode" element
     */
    public void xsetUnitCode(org.apache.xmlbeans.XmlString unitCode)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(UNITCODE$30, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(UNITCODE$30);
            }
            target.set(unitCode);
        }
    }
    
    /**
     * Nils the "UnitCode" element
     */
    public void setNilUnitCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(UNITCODE$30, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(UNITCODE$30);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "UnitCode" element
     */
    public void unsetUnitCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(UNITCODE$30, 0);
        }
    }
    
    /**
     * Gets the "UnitName" element
     */
    public java.lang.String getUnitName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(UNITNAME$32, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "UnitName" element
     */
    public org.apache.xmlbeans.XmlString xgetUnitName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(UNITNAME$32, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "UnitName" element
     */
    public boolean isNilUnitName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(UNITNAME$32, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "UnitName" element
     */
    public boolean isSetUnitName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(UNITNAME$32) != 0;
        }
    }
    
    /**
     * Sets the "UnitName" element
     */
    public void setUnitName(java.lang.String unitName)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(UNITNAME$32, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(UNITNAME$32);
            }
            target.setStringValue(unitName);
        }
    }
    
    /**
     * Sets (as xml) the "UnitName" element
     */
    public void xsetUnitName(org.apache.xmlbeans.XmlString unitName)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(UNITNAME$32, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(UNITNAME$32);
            }
            target.set(unitName);
        }
    }
    
    /**
     * Nils the "UnitName" element
     */
    public void setNilUnitName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(UNITNAME$32, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(UNITNAME$32);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "UnitName" element
     */
    public void unsetUnitName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(UNITNAME$32, 0);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(VAT$34, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(VAT$34, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(VAT$34, 0);
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
            return get_store().count_elements(VAT$34) != 0;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(VAT$34, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(VAT$34);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(VAT$34, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(VAT$34);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(VAT$34, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(VAT$34);
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
            get_store().remove_element(VAT$34, 0);
        }
    }
}
