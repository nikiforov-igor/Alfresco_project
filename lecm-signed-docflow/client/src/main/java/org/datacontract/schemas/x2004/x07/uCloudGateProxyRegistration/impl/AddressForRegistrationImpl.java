/*
 * XML Type:  AddressForRegistration
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.AddressForRegistration
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.impl;
/**
 * An XML AddressForRegistration(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration).
 *
 * This is a complex type.
 */
public class AddressForRegistrationImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.AddressForRegistration
{
    private static final long serialVersionUID = 1L;
    
    public AddressForRegistrationImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName APARTMENT$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration", "Apartment");
    private static final javax.xml.namespace.QName AREA$2 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration", "Area");
    private static final javax.xml.namespace.QName CORPUS$4 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration", "Corpus");
    private static final javax.xml.namespace.QName HOUSE$6 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration", "House");
    private static final javax.xml.namespace.QName INDEX$8 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration", "Index");
    private static final javax.xml.namespace.QName LOCALITY$10 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration", "Locality");
    private static final javax.xml.namespace.QName REGIONCODE$12 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration", "RegionCode");
    private static final javax.xml.namespace.QName STREET$14 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration", "Street");
    private static final javax.xml.namespace.QName TOWN$16 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration", "Town");
    
    
    /**
     * Gets the "Apartment" element
     */
    public java.lang.String getApartment()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(APARTMENT$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Apartment" element
     */
    public org.apache.xmlbeans.XmlString xgetApartment()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(APARTMENT$0, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "Apartment" element
     */
    public boolean isNilApartment()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(APARTMENT$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Apartment" element
     */
    public boolean isSetApartment()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(APARTMENT$0) != 0;
        }
    }
    
    /**
     * Sets the "Apartment" element
     */
    public void setApartment(java.lang.String apartment)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(APARTMENT$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(APARTMENT$0);
            }
            target.setStringValue(apartment);
        }
    }
    
    /**
     * Sets (as xml) the "Apartment" element
     */
    public void xsetApartment(org.apache.xmlbeans.XmlString apartment)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(APARTMENT$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(APARTMENT$0);
            }
            target.set(apartment);
        }
    }
    
    /**
     * Nils the "Apartment" element
     */
    public void setNilApartment()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(APARTMENT$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(APARTMENT$0);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Apartment" element
     */
    public void unsetApartment()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(APARTMENT$0, 0);
        }
    }
    
    /**
     * Gets the "Area" element
     */
    public java.lang.String getArea()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(AREA$2, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(AREA$2, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(AREA$2, 0);
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
            return get_store().count_elements(AREA$2) != 0;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(AREA$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(AREA$2);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(AREA$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(AREA$2);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(AREA$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(AREA$2);
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
            get_store().remove_element(AREA$2, 0);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CORPUS$4, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(CORPUS$4, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(CORPUS$4, 0);
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
            return get_store().count_elements(CORPUS$4) != 0;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CORPUS$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(CORPUS$4);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(CORPUS$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(CORPUS$4);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(CORPUS$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(CORPUS$4);
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
            get_store().remove_element(CORPUS$4, 0);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(HOUSE$6, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(HOUSE$6, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(HOUSE$6, 0);
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
            return get_store().count_elements(HOUSE$6) != 0;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(HOUSE$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(HOUSE$6);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(HOUSE$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(HOUSE$6);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(HOUSE$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(HOUSE$6);
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
            get_store().remove_element(HOUSE$6, 0);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(INDEX$8, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INDEX$8, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INDEX$8, 0);
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
            return get_store().count_elements(INDEX$8) != 0;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(INDEX$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(INDEX$8);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INDEX$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(INDEX$8);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INDEX$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(INDEX$8);
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
            get_store().remove_element(INDEX$8, 0);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(LOCALITY$10, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(LOCALITY$10, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(LOCALITY$10, 0);
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
            return get_store().count_elements(LOCALITY$10) != 0;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(LOCALITY$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(LOCALITY$10);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(LOCALITY$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(LOCALITY$10);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(LOCALITY$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(LOCALITY$10);
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
            get_store().remove_element(LOCALITY$10, 0);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(REGIONCODE$12, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(REGIONCODE$12, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(REGIONCODE$12, 0);
            if (target == null) return false;
            return target.isNil();
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(REGIONCODE$12, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(REGIONCODE$12);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(REGIONCODE$12, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(REGIONCODE$12);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(REGIONCODE$12, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(REGIONCODE$12);
            }
            target.setNil();
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(STREET$14, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(STREET$14, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(STREET$14, 0);
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
            return get_store().count_elements(STREET$14) != 0;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(STREET$14, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(STREET$14);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(STREET$14, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(STREET$14);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(STREET$14, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(STREET$14);
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
            get_store().remove_element(STREET$14, 0);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(TOWN$16, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TOWN$16, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TOWN$16, 0);
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
            return get_store().count_elements(TOWN$16) != 0;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(TOWN$16, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(TOWN$16);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TOWN$16, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(TOWN$16);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TOWN$16, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(TOWN$16);
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
            get_store().remove_element(TOWN$16, 0);
        }
    }
}
