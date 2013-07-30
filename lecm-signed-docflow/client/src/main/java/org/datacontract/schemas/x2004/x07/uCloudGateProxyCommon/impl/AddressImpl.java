/*
 * XML Type:  Address
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Common
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyCommon.Address
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyCommon.impl;
/**
 * An XML Address(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Common).
 *
 * This is a complex type.
 */
public class AddressImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyCommon.Address
{
    private static final long serialVersionUID = 1L;
    
    public AddressImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName AREA$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Common", "Area");
    private static final javax.xml.namespace.QName CORPUS$2 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Common", "Corpus");
    private static final javax.xml.namespace.QName FOREIGNADDRESSTEXT$4 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Common", "ForeignAddressText");
    private static final javax.xml.namespace.QName FOREIGNCOUNTRYCODE$6 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Common", "ForeignCountryCode");
    private static final javax.xml.namespace.QName HOUSE$8 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Common", "House");
    private static final javax.xml.namespace.QName INDEX$10 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Common", "Index");
    private static final javax.xml.namespace.QName ISFOREIGNADDRESS$12 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Common", "IsForeignAddress");
    private static final javax.xml.namespace.QName LOCALITY$14 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Common", "Locality");
    private static final javax.xml.namespace.QName QUARTER$16 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Common", "Quarter");
    private static final javax.xml.namespace.QName REGIONCODE$18 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Common", "RegionCode");
    private static final javax.xml.namespace.QName STREET$20 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Common", "Street");
    private static final javax.xml.namespace.QName TOWN$22 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Common", "Town");
    private static final javax.xml.namespace.QName UNSTRUCTEDADDRESSTEXT$24 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Common", "UnstructedAddressText");
    
    
    /**
     * Gets the "Area" element
     */
    public java.lang.String getArea()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(AREA$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Area" element
     */
    public org.apache.xmlbeans.XmlString xgetArea()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(AREA$0, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "Area" element
     */
    public boolean isNilArea()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(AREA$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Area" element
     */
    public boolean isSetArea()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(AREA$0) != 0;
        }
    }
    
    /**
     * Sets the "Area" element
     */
    public void setArea(java.lang.String area)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(AREA$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(AREA$0);
            }
            target.setStringValue(area);
        }
    }
    
    /**
     * Sets (as xml) the "Area" element
     */
    public void xsetArea(org.apache.xmlbeans.XmlString area)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(AREA$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(AREA$0);
            }
            target.set(area);
        }
    }
    
    /**
     * Nils the "Area" element
     */
    public void setNilArea()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(AREA$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(AREA$0);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Area" element
     */
    public void unsetArea()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(AREA$0, 0);
        }
    }
    
    /**
     * Gets the "Corpus" element
     */
    public java.lang.String getCorpus()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CORPUS$2, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Corpus" element
     */
    public org.apache.xmlbeans.XmlString xgetCorpus()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(CORPUS$2, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "Corpus" element
     */
    public boolean isNilCorpus()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(CORPUS$2, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Corpus" element
     */
    public boolean isSetCorpus()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(CORPUS$2) != 0;
        }
    }
    
    /**
     * Sets the "Corpus" element
     */
    public void setCorpus(java.lang.String corpus)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CORPUS$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(CORPUS$2);
            }
            target.setStringValue(corpus);
        }
    }
    
    /**
     * Sets (as xml) the "Corpus" element
     */
    public void xsetCorpus(org.apache.xmlbeans.XmlString corpus)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(CORPUS$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(CORPUS$2);
            }
            target.set(corpus);
        }
    }
    
    /**
     * Nils the "Corpus" element
     */
    public void setNilCorpus()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(CORPUS$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(CORPUS$2);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Corpus" element
     */
    public void unsetCorpus()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(CORPUS$2, 0);
        }
    }
    
    /**
     * Gets the "ForeignAddressText" element
     */
    public java.lang.String getForeignAddressText()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(FOREIGNADDRESSTEXT$4, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "ForeignAddressText" element
     */
    public org.apache.xmlbeans.XmlString xgetForeignAddressText()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FOREIGNADDRESSTEXT$4, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "ForeignAddressText" element
     */
    public boolean isNilForeignAddressText()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FOREIGNADDRESSTEXT$4, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "ForeignAddressText" element
     */
    public boolean isSetForeignAddressText()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(FOREIGNADDRESSTEXT$4) != 0;
        }
    }
    
    /**
     * Sets the "ForeignAddressText" element
     */
    public void setForeignAddressText(java.lang.String foreignAddressText)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(FOREIGNADDRESSTEXT$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(FOREIGNADDRESSTEXT$4);
            }
            target.setStringValue(foreignAddressText);
        }
    }
    
    /**
     * Sets (as xml) the "ForeignAddressText" element
     */
    public void xsetForeignAddressText(org.apache.xmlbeans.XmlString foreignAddressText)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FOREIGNADDRESSTEXT$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(FOREIGNADDRESSTEXT$4);
            }
            target.set(foreignAddressText);
        }
    }
    
    /**
     * Nils the "ForeignAddressText" element
     */
    public void setNilForeignAddressText()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FOREIGNADDRESSTEXT$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(FOREIGNADDRESSTEXT$4);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "ForeignAddressText" element
     */
    public void unsetForeignAddressText()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(FOREIGNADDRESSTEXT$4, 0);
        }
    }
    
    /**
     * Gets the "ForeignCountryCode" element
     */
    public java.lang.String getForeignCountryCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(FOREIGNCOUNTRYCODE$6, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "ForeignCountryCode" element
     */
    public org.apache.xmlbeans.XmlString xgetForeignCountryCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FOREIGNCOUNTRYCODE$6, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "ForeignCountryCode" element
     */
    public boolean isNilForeignCountryCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FOREIGNCOUNTRYCODE$6, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "ForeignCountryCode" element
     */
    public boolean isSetForeignCountryCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(FOREIGNCOUNTRYCODE$6) != 0;
        }
    }
    
    /**
     * Sets the "ForeignCountryCode" element
     */
    public void setForeignCountryCode(java.lang.String foreignCountryCode)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(FOREIGNCOUNTRYCODE$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(FOREIGNCOUNTRYCODE$6);
            }
            target.setStringValue(foreignCountryCode);
        }
    }
    
    /**
     * Sets (as xml) the "ForeignCountryCode" element
     */
    public void xsetForeignCountryCode(org.apache.xmlbeans.XmlString foreignCountryCode)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FOREIGNCOUNTRYCODE$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(FOREIGNCOUNTRYCODE$6);
            }
            target.set(foreignCountryCode);
        }
    }
    
    /**
     * Nils the "ForeignCountryCode" element
     */
    public void setNilForeignCountryCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FOREIGNCOUNTRYCODE$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(FOREIGNCOUNTRYCODE$6);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "ForeignCountryCode" element
     */
    public void unsetForeignCountryCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(FOREIGNCOUNTRYCODE$6, 0);
        }
    }
    
    /**
     * Gets the "House" element
     */
    public java.lang.String getHouse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(HOUSE$8, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "House" element
     */
    public org.apache.xmlbeans.XmlString xgetHouse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(HOUSE$8, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "House" element
     */
    public boolean isNilHouse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(HOUSE$8, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "House" element
     */
    public boolean isSetHouse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(HOUSE$8) != 0;
        }
    }
    
    /**
     * Sets the "House" element
     */
    public void setHouse(java.lang.String house)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(HOUSE$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(HOUSE$8);
            }
            target.setStringValue(house);
        }
    }
    
    /**
     * Sets (as xml) the "House" element
     */
    public void xsetHouse(org.apache.xmlbeans.XmlString house)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(HOUSE$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(HOUSE$8);
            }
            target.set(house);
        }
    }
    
    /**
     * Nils the "House" element
     */
    public void setNilHouse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(HOUSE$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(HOUSE$8);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "House" element
     */
    public void unsetHouse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(HOUSE$8, 0);
        }
    }
    
    /**
     * Gets the "Index" element
     */
    public java.lang.String getIndex()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(INDEX$10, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Index" element
     */
    public org.apache.xmlbeans.XmlString xgetIndex()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INDEX$10, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "Index" element
     */
    public boolean isNilIndex()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INDEX$10, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Index" element
     */
    public boolean isSetIndex()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(INDEX$10) != 0;
        }
    }
    
    /**
     * Sets the "Index" element
     */
    public void setIndex(java.lang.String index)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(INDEX$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(INDEX$10);
            }
            target.setStringValue(index);
        }
    }
    
    /**
     * Sets (as xml) the "Index" element
     */
    public void xsetIndex(org.apache.xmlbeans.XmlString index)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INDEX$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(INDEX$10);
            }
            target.set(index);
        }
    }
    
    /**
     * Nils the "Index" element
     */
    public void setNilIndex()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INDEX$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(INDEX$10);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Index" element
     */
    public void unsetIndex()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(INDEX$10, 0);
        }
    }
    
    /**
     * Gets the "IsForeignAddress" element
     */
    public boolean getIsForeignAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ISFOREIGNADDRESS$12, 0);
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "IsForeignAddress" element
     */
    public org.apache.xmlbeans.XmlBoolean xgetIsForeignAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(ISFOREIGNADDRESS$12, 0);
            return target;
        }
    }
    
    /**
     * True if has "IsForeignAddress" element
     */
    public boolean isSetIsForeignAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(ISFOREIGNADDRESS$12) != 0;
        }
    }
    
    /**
     * Sets the "IsForeignAddress" element
     */
    public void setIsForeignAddress(boolean isForeignAddress)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ISFOREIGNADDRESS$12, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ISFOREIGNADDRESS$12);
            }
            target.setBooleanValue(isForeignAddress);
        }
    }
    
    /**
     * Sets (as xml) the "IsForeignAddress" element
     */
    public void xsetIsForeignAddress(org.apache.xmlbeans.XmlBoolean isForeignAddress)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(ISFOREIGNADDRESS$12, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_element_user(ISFOREIGNADDRESS$12);
            }
            target.set(isForeignAddress);
        }
    }
    
    /**
     * Unsets the "IsForeignAddress" element
     */
    public void unsetIsForeignAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(ISFOREIGNADDRESS$12, 0);
        }
    }
    
    /**
     * Gets the "Locality" element
     */
    public java.lang.String getLocality()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(LOCALITY$14, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Locality" element
     */
    public org.apache.xmlbeans.XmlString xgetLocality()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(LOCALITY$14, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "Locality" element
     */
    public boolean isNilLocality()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(LOCALITY$14, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Locality" element
     */
    public boolean isSetLocality()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(LOCALITY$14) != 0;
        }
    }
    
    /**
     * Sets the "Locality" element
     */
    public void setLocality(java.lang.String locality)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(LOCALITY$14, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(LOCALITY$14);
            }
            target.setStringValue(locality);
        }
    }
    
    /**
     * Sets (as xml) the "Locality" element
     */
    public void xsetLocality(org.apache.xmlbeans.XmlString locality)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(LOCALITY$14, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(LOCALITY$14);
            }
            target.set(locality);
        }
    }
    
    /**
     * Nils the "Locality" element
     */
    public void setNilLocality()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(LOCALITY$14, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(LOCALITY$14);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Locality" element
     */
    public void unsetLocality()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(LOCALITY$14, 0);
        }
    }
    
    /**
     * Gets the "Quarter" element
     */
    public java.lang.String getQuarter()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(QUARTER$16, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Quarter" element
     */
    public org.apache.xmlbeans.XmlString xgetQuarter()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(QUARTER$16, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "Quarter" element
     */
    public boolean isNilQuarter()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(QUARTER$16, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Quarter" element
     */
    public boolean isSetQuarter()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(QUARTER$16) != 0;
        }
    }
    
    /**
     * Sets the "Quarter" element
     */
    public void setQuarter(java.lang.String quarter)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(QUARTER$16, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(QUARTER$16);
            }
            target.setStringValue(quarter);
        }
    }
    
    /**
     * Sets (as xml) the "Quarter" element
     */
    public void xsetQuarter(org.apache.xmlbeans.XmlString quarter)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(QUARTER$16, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(QUARTER$16);
            }
            target.set(quarter);
        }
    }
    
    /**
     * Nils the "Quarter" element
     */
    public void setNilQuarter()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(QUARTER$16, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(QUARTER$16);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Quarter" element
     */
    public void unsetQuarter()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(QUARTER$16, 0);
        }
    }
    
    /**
     * Gets the "RegionCode" element
     */
    public java.lang.String getRegionCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(REGIONCODE$18, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "RegionCode" element
     */
    public org.apache.xmlbeans.XmlString xgetRegionCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(REGIONCODE$18, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "RegionCode" element
     */
    public boolean isNilRegionCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(REGIONCODE$18, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "RegionCode" element
     */
    public boolean isSetRegionCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(REGIONCODE$18) != 0;
        }
    }
    
    /**
     * Sets the "RegionCode" element
     */
    public void setRegionCode(java.lang.String regionCode)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(REGIONCODE$18, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(REGIONCODE$18);
            }
            target.setStringValue(regionCode);
        }
    }
    
    /**
     * Sets (as xml) the "RegionCode" element
     */
    public void xsetRegionCode(org.apache.xmlbeans.XmlString regionCode)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(REGIONCODE$18, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(REGIONCODE$18);
            }
            target.set(regionCode);
        }
    }
    
    /**
     * Nils the "RegionCode" element
     */
    public void setNilRegionCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(REGIONCODE$18, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(REGIONCODE$18);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "RegionCode" element
     */
    public void unsetRegionCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(REGIONCODE$18, 0);
        }
    }
    
    /**
     * Gets the "Street" element
     */
    public java.lang.String getStreet()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(STREET$20, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Street" element
     */
    public org.apache.xmlbeans.XmlString xgetStreet()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(STREET$20, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "Street" element
     */
    public boolean isNilStreet()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(STREET$20, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Street" element
     */
    public boolean isSetStreet()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(STREET$20) != 0;
        }
    }
    
    /**
     * Sets the "Street" element
     */
    public void setStreet(java.lang.String street)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(STREET$20, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(STREET$20);
            }
            target.setStringValue(street);
        }
    }
    
    /**
     * Sets (as xml) the "Street" element
     */
    public void xsetStreet(org.apache.xmlbeans.XmlString street)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(STREET$20, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(STREET$20);
            }
            target.set(street);
        }
    }
    
    /**
     * Nils the "Street" element
     */
    public void setNilStreet()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(STREET$20, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(STREET$20);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Street" element
     */
    public void unsetStreet()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(STREET$20, 0);
        }
    }
    
    /**
     * Gets the "Town" element
     */
    public java.lang.String getTown()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(TOWN$22, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Town" element
     */
    public org.apache.xmlbeans.XmlString xgetTown()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TOWN$22, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "Town" element
     */
    public boolean isNilTown()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TOWN$22, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Town" element
     */
    public boolean isSetTown()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(TOWN$22) != 0;
        }
    }
    
    /**
     * Sets the "Town" element
     */
    public void setTown(java.lang.String town)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(TOWN$22, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(TOWN$22);
            }
            target.setStringValue(town);
        }
    }
    
    /**
     * Sets (as xml) the "Town" element
     */
    public void xsetTown(org.apache.xmlbeans.XmlString town)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TOWN$22, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(TOWN$22);
            }
            target.set(town);
        }
    }
    
    /**
     * Nils the "Town" element
     */
    public void setNilTown()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TOWN$22, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(TOWN$22);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Town" element
     */
    public void unsetTown()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(TOWN$22, 0);
        }
    }
    
    /**
     * Gets the "UnstructedAddressText" element
     */
    public java.lang.String getUnstructedAddressText()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(UNSTRUCTEDADDRESSTEXT$24, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "UnstructedAddressText" element
     */
    public org.apache.xmlbeans.XmlString xgetUnstructedAddressText()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(UNSTRUCTEDADDRESSTEXT$24, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "UnstructedAddressText" element
     */
    public boolean isNilUnstructedAddressText()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(UNSTRUCTEDADDRESSTEXT$24, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "UnstructedAddressText" element
     */
    public boolean isSetUnstructedAddressText()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(UNSTRUCTEDADDRESSTEXT$24) != 0;
        }
    }
    
    /**
     * Sets the "UnstructedAddressText" element
     */
    public void setUnstructedAddressText(java.lang.String unstructedAddressText)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(UNSTRUCTEDADDRESSTEXT$24, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(UNSTRUCTEDADDRESSTEXT$24);
            }
            target.setStringValue(unstructedAddressText);
        }
    }
    
    /**
     * Sets (as xml) the "UnstructedAddressText" element
     */
    public void xsetUnstructedAddressText(org.apache.xmlbeans.XmlString unstructedAddressText)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(UNSTRUCTEDADDRESSTEXT$24, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(UNSTRUCTEDADDRESSTEXT$24);
            }
            target.set(unstructedAddressText);
        }
    }
    
    /**
     * Nils the "UnstructedAddressText" element
     */
    public void setNilUnstructedAddressText()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(UNSTRUCTEDADDRESSTEXT$24, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(UNSTRUCTEDADDRESSTEXT$24);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "UnstructedAddressText" element
     */
    public void unsetUnstructedAddressText()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(UNSTRUCTEDADDRESSTEXT$24, 0);
        }
    }
}
