/*
 * An XML attribute type.
 * Localname: FactoryType
 * Namespace: http://schemas.microsoft.com/2003/10/Serialization/
 * Java type: com.microsoft.schemas.x2003.x10.serialization.FactoryTypeAttribute
 *
 * Automatically generated - do not modify.
 */
package com.microsoft.schemas.x2003.x10.serialization.impl;
/**
 * A document containing one FactoryType(@http://schemas.microsoft.com/2003/10/Serialization/) attribute.
 *
 * This is a complex type.
 */
public class FactoryTypeAttributeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.microsoft.schemas.x2003.x10.serialization.FactoryTypeAttribute
{
    private static final long serialVersionUID = 1L;
    
    public FactoryTypeAttributeImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName FACTORYTYPE$0 = 
        new javax.xml.namespace.QName("http://schemas.microsoft.com/2003/10/Serialization/", "FactoryType");
    
    
    /**
     * Gets the "FactoryType" attribute
     */
    public javax.xml.namespace.QName getFactoryType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(FACTORYTYPE$0);
            if (target == null)
            {
                return null;
            }
            return target.getQNameValue();
        }
    }
    
    /**
     * Gets (as xml) the "FactoryType" attribute
     */
    public org.apache.xmlbeans.XmlQName xgetFactoryType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlQName target = null;
            target = (org.apache.xmlbeans.XmlQName)get_store().find_attribute_user(FACTORYTYPE$0);
            return target;
        }
    }
    
    /**
     * True if has "FactoryType" attribute
     */
    public boolean isSetFactoryType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(FACTORYTYPE$0) != null;
        }
    }
    
    /**
     * Sets the "FactoryType" attribute
     */
    public void setFactoryType(javax.xml.namespace.QName factoryType)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(FACTORYTYPE$0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(FACTORYTYPE$0);
            }
            target.setQNameValue(factoryType);
        }
    }
    
    /**
     * Sets (as xml) the "FactoryType" attribute
     */
    public void xsetFactoryType(org.apache.xmlbeans.XmlQName factoryType)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlQName target = null;
            target = (org.apache.xmlbeans.XmlQName)get_store().find_attribute_user(FACTORYTYPE$0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlQName)get_store().add_attribute_user(FACTORYTYPE$0);
            }
            target.set(factoryType);
        }
    }
    
    /**
     * Unsets the "FactoryType" attribute
     */
    public void unsetFactoryType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(FACTORYTYPE$0);
        }
    }
}
