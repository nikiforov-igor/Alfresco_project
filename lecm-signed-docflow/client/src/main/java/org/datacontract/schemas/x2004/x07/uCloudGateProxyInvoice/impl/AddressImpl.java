/*
 * XML Type:  Address
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Invoice
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.Address
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.impl;
/**
 * An XML Address(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Invoice).
 *
 * This is a complex type.
 */
public class AddressImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.Address
{
    private static final long serialVersionUID = 1L;
    
    public AddressImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName APARTMENT$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Invoice", "Apartment");
    private static final javax.xml.namespace.QName BLOCK$2 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Invoice", "Block");
    private static final javax.xml.namespace.QName BUILDING$4 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Invoice", "Building");
    private static final javax.xml.namespace.QName CITY$6 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Invoice", "City");
    private static final javax.xml.namespace.QName FOREIGNADDRESS$8 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Invoice", "ForeignAddress");
    private static final javax.xml.namespace.QName FOREIGNCOUNTRY$10 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Invoice", "ForeignCountry");
    private static final javax.xml.namespace.QName ISRUSSIANADDRESS$12 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Invoice", "IsRussianAddress");
    private static final javax.xml.namespace.QName LOCALITY$14 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Invoice", "Locality");
    private static final javax.xml.namespace.QName REGION$16 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Invoice", "Region");
    private static final javax.xml.namespace.QName STREET$18 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Invoice", "Street");
    private static final javax.xml.namespace.QName TERRITORY$20 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Invoice", "Territory");
    private static final javax.xml.namespace.QName ZIPCODE$22 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Invoice", "ZipCode");
    
    
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
     * Gets the "Block" element
     */
    public java.lang.String getBlock()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(BLOCK$2, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Block" element
     */
    public org.apache.xmlbeans.XmlString xgetBlock()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(BLOCK$2, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "Block" element
     */
    public boolean isNilBlock()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(BLOCK$2, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Block" element
     */
    public boolean isSetBlock()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(BLOCK$2) != 0;
        }
    }
    
    /**
     * Sets the "Block" element
     */
    public void setBlock(java.lang.String block)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(BLOCK$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(BLOCK$2);
            }
            target.setStringValue(block);
        }
    }
    
    /**
     * Sets (as xml) the "Block" element
     */
    public void xsetBlock(org.apache.xmlbeans.XmlString block)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(BLOCK$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(BLOCK$2);
            }
            target.set(block);
        }
    }
    
    /**
     * Nils the "Block" element
     */
    public void setNilBlock()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(BLOCK$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(BLOCK$2);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Block" element
     */
    public void unsetBlock()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(BLOCK$2, 0);
        }
    }
    
    /**
     * Gets the "Building" element
     */
    public java.lang.String getBuilding()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(BUILDING$4, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Building" element
     */
    public org.apache.xmlbeans.XmlString xgetBuilding()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(BUILDING$4, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "Building" element
     */
    public boolean isNilBuilding()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(BUILDING$4, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Building" element
     */
    public boolean isSetBuilding()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(BUILDING$4) != 0;
        }
    }
    
    /**
     * Sets the "Building" element
     */
    public void setBuilding(java.lang.String building)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(BUILDING$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(BUILDING$4);
            }
            target.setStringValue(building);
        }
    }
    
    /**
     * Sets (as xml) the "Building" element
     */
    public void xsetBuilding(org.apache.xmlbeans.XmlString building)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(BUILDING$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(BUILDING$4);
            }
            target.set(building);
        }
    }
    
    /**
     * Nils the "Building" element
     */
    public void setNilBuilding()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(BUILDING$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(BUILDING$4);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Building" element
     */
    public void unsetBuilding()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(BUILDING$4, 0);
        }
    }
    
    /**
     * Gets the "City" element
     */
    public java.lang.String getCity()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CITY$6, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "City" element
     */
    public org.apache.xmlbeans.XmlString xgetCity()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(CITY$6, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "City" element
     */
    public boolean isNilCity()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(CITY$6, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "City" element
     */
    public boolean isSetCity()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(CITY$6) != 0;
        }
    }
    
    /**
     * Sets the "City" element
     */
    public void setCity(java.lang.String city)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CITY$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(CITY$6);
            }
            target.setStringValue(city);
        }
    }
    
    /**
     * Sets (as xml) the "City" element
     */
    public void xsetCity(org.apache.xmlbeans.XmlString city)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(CITY$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(CITY$6);
            }
            target.set(city);
        }
    }
    
    /**
     * Nils the "City" element
     */
    public void setNilCity()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(CITY$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(CITY$6);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "City" element
     */
    public void unsetCity()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(CITY$6, 0);
        }
    }
    
    /**
     * Gets the "ForeignAddress" element
     */
    public java.lang.String getForeignAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(FOREIGNADDRESS$8, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "ForeignAddress" element
     */
    public org.apache.xmlbeans.XmlString xgetForeignAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FOREIGNADDRESS$8, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "ForeignAddress" element
     */
    public boolean isNilForeignAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FOREIGNADDRESS$8, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "ForeignAddress" element
     */
    public boolean isSetForeignAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(FOREIGNADDRESS$8) != 0;
        }
    }
    
    /**
     * Sets the "ForeignAddress" element
     */
    public void setForeignAddress(java.lang.String foreignAddress)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(FOREIGNADDRESS$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(FOREIGNADDRESS$8);
            }
            target.setStringValue(foreignAddress);
        }
    }
    
    /**
     * Sets (as xml) the "ForeignAddress" element
     */
    public void xsetForeignAddress(org.apache.xmlbeans.XmlString foreignAddress)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FOREIGNADDRESS$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(FOREIGNADDRESS$8);
            }
            target.set(foreignAddress);
        }
    }
    
    /**
     * Nils the "ForeignAddress" element
     */
    public void setNilForeignAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FOREIGNADDRESS$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(FOREIGNADDRESS$8);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "ForeignAddress" element
     */
    public void unsetForeignAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(FOREIGNADDRESS$8, 0);
        }
    }
    
    /**
     * Gets the "ForeignCountry" element
     */
    public java.lang.String getForeignCountry()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(FOREIGNCOUNTRY$10, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "ForeignCountry" element
     */
    public org.apache.xmlbeans.XmlString xgetForeignCountry()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FOREIGNCOUNTRY$10, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "ForeignCountry" element
     */
    public boolean isNilForeignCountry()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FOREIGNCOUNTRY$10, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "ForeignCountry" element
     */
    public boolean isSetForeignCountry()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(FOREIGNCOUNTRY$10) != 0;
        }
    }
    
    /**
     * Sets the "ForeignCountry" element
     */
    public void setForeignCountry(java.lang.String foreignCountry)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(FOREIGNCOUNTRY$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(FOREIGNCOUNTRY$10);
            }
            target.setStringValue(foreignCountry);
        }
    }
    
    /**
     * Sets (as xml) the "ForeignCountry" element
     */
    public void xsetForeignCountry(org.apache.xmlbeans.XmlString foreignCountry)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FOREIGNCOUNTRY$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(FOREIGNCOUNTRY$10);
            }
            target.set(foreignCountry);
        }
    }
    
    /**
     * Nils the "ForeignCountry" element
     */
    public void setNilForeignCountry()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FOREIGNCOUNTRY$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(FOREIGNCOUNTRY$10);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "ForeignCountry" element
     */
    public void unsetForeignCountry()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(FOREIGNCOUNTRY$10, 0);
        }
    }
    
    /**
     * Gets the "IsRussianAddress" element
     */
    public boolean getIsRussianAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ISRUSSIANADDRESS$12, 0);
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "IsRussianAddress" element
     */
    public org.apache.xmlbeans.XmlBoolean xgetIsRussianAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(ISRUSSIANADDRESS$12, 0);
            return target;
        }
    }
    
    /**
     * True if has "IsRussianAddress" element
     */
    public boolean isSetIsRussianAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(ISRUSSIANADDRESS$12) != 0;
        }
    }
    
    /**
     * Sets the "IsRussianAddress" element
     */
    public void setIsRussianAddress(boolean isRussianAddress)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ISRUSSIANADDRESS$12, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ISRUSSIANADDRESS$12);
            }
            target.setBooleanValue(isRussianAddress);
        }
    }
    
    /**
     * Sets (as xml) the "IsRussianAddress" element
     */
    public void xsetIsRussianAddress(org.apache.xmlbeans.XmlBoolean isRussianAddress)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(ISRUSSIANADDRESS$12, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_element_user(ISRUSSIANADDRESS$12);
            }
            target.set(isRussianAddress);
        }
    }
    
    /**
     * Unsets the "IsRussianAddress" element
     */
    public void unsetIsRussianAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(ISRUSSIANADDRESS$12, 0);
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
     * Gets the "Region" element
     */
    public java.lang.String getRegion()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(REGION$16, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Region" element
     */
    public org.apache.xmlbeans.XmlString xgetRegion()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(REGION$16, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "Region" element
     */
    public boolean isNilRegion()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(REGION$16, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Region" element
     */
    public boolean isSetRegion()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(REGION$16) != 0;
        }
    }
    
    /**
     * Sets the "Region" element
     */
    public void setRegion(java.lang.String region)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(REGION$16, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(REGION$16);
            }
            target.setStringValue(region);
        }
    }
    
    /**
     * Sets (as xml) the "Region" element
     */
    public void xsetRegion(org.apache.xmlbeans.XmlString region)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(REGION$16, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(REGION$16);
            }
            target.set(region);
        }
    }
    
    /**
     * Nils the "Region" element
     */
    public void setNilRegion()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(REGION$16, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(REGION$16);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Region" element
     */
    public void unsetRegion()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(REGION$16, 0);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(STREET$18, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(STREET$18, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(STREET$18, 0);
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
            return get_store().count_elements(STREET$18) != 0;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(STREET$18, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(STREET$18);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(STREET$18, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(STREET$18);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(STREET$18, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(STREET$18);
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
            get_store().remove_element(STREET$18, 0);
        }
    }
    
    /**
     * Gets the "Territory" element
     */
    public java.lang.String getTerritory()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(TERRITORY$20, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Territory" element
     */
    public org.apache.xmlbeans.XmlString xgetTerritory()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TERRITORY$20, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "Territory" element
     */
    public boolean isNilTerritory()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TERRITORY$20, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Territory" element
     */
    public boolean isSetTerritory()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(TERRITORY$20) != 0;
        }
    }
    
    /**
     * Sets the "Territory" element
     */
    public void setTerritory(java.lang.String territory)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(TERRITORY$20, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(TERRITORY$20);
            }
            target.setStringValue(territory);
        }
    }
    
    /**
     * Sets (as xml) the "Territory" element
     */
    public void xsetTerritory(org.apache.xmlbeans.XmlString territory)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TERRITORY$20, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(TERRITORY$20);
            }
            target.set(territory);
        }
    }
    
    /**
     * Nils the "Territory" element
     */
    public void setNilTerritory()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TERRITORY$20, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(TERRITORY$20);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Territory" element
     */
    public void unsetTerritory()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(TERRITORY$20, 0);
        }
    }
    
    /**
     * Gets the "ZipCode" element
     */
    public java.lang.String getZipCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ZIPCODE$22, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "ZipCode" element
     */
    public org.apache.xmlbeans.XmlString xgetZipCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ZIPCODE$22, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "ZipCode" element
     */
    public boolean isNilZipCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ZIPCODE$22, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "ZipCode" element
     */
    public boolean isSetZipCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(ZIPCODE$22) != 0;
        }
    }
    
    /**
     * Sets the "ZipCode" element
     */
    public void setZipCode(java.lang.String zipCode)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ZIPCODE$22, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ZIPCODE$22);
            }
            target.setStringValue(zipCode);
        }
    }
    
    /**
     * Sets (as xml) the "ZipCode" element
     */
    public void xsetZipCode(org.apache.xmlbeans.XmlString zipCode)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ZIPCODE$22, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(ZIPCODE$22);
            }
            target.set(zipCode);
        }
    }
    
    /**
     * Nils the "ZipCode" element
     */
    public void setNilZipCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ZIPCODE$22, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(ZIPCODE$22);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "ZipCode" element
     */
    public void unsetZipCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(ZIPCODE$22, 0);
        }
    }
}
