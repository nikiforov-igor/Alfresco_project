/*
 * XML Type:  DocumentContent
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentContent
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxy.impl;
/**
 * An XML DocumentContent(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy).
 *
 * This is a complex type.
 */
public class DocumentContentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentContent
{
    private static final long serialVersionUID = 1L;
    
    public DocumentContentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName CONTENT$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "Content");
    private static final javax.xml.namespace.QName SIGNATURES$2 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "Signatures");
    
    
    /**
     * Gets the "Content" element
     */
    public byte[] getContent()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CONTENT$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getByteArrayValue();
        }
    }
    
    /**
     * Gets (as xml) the "Content" element
     */
    public org.apache.xmlbeans.XmlBase64Binary xgetContent()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBase64Binary target = null;
            target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(CONTENT$0, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "Content" element
     */
    public boolean isNilContent()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBase64Binary target = null;
            target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(CONTENT$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Content" element
     */
    public boolean isSetContent()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(CONTENT$0) != 0;
        }
    }
    
    /**
     * Sets the "Content" element
     */
    public void setContent(byte[] content)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CONTENT$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(CONTENT$0);
            }
            target.setByteArrayValue(content);
        }
    }
    
    /**
     * Sets (as xml) the "Content" element
     */
    public void xsetContent(org.apache.xmlbeans.XmlBase64Binary content)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBase64Binary target = null;
            target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(CONTENT$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBase64Binary)get_store().add_element_user(CONTENT$0);
            }
            target.set(content);
        }
    }
    
    /**
     * Nils the "Content" element
     */
    public void setNilContent()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBase64Binary target = null;
            target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(CONTENT$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBase64Binary)get_store().add_element_user(CONTENT$0);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Content" element
     */
    public void unsetContent()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(CONTENT$0, 0);
        }
    }
    
    /**
     * Gets the "Signatures" element
     */
    public com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary getSignatures()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary target = null;
            target = (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary)get_store().find_element_user(SIGNATURES$2, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "Signatures" element
     */
    public boolean isNilSignatures()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary target = null;
            target = (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary)get_store().find_element_user(SIGNATURES$2, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Signatures" element
     */
    public boolean isSetSignatures()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(SIGNATURES$2) != 0;
        }
    }
    
    /**
     * Sets the "Signatures" element
     */
    public void setSignatures(com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary signatures)
    {
        generatedSetterHelperImpl(signatures, SIGNATURES$2, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "Signatures" element
     */
    public com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary addNewSignatures()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary target = null;
            target = (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary)get_store().add_element_user(SIGNATURES$2);
            return target;
        }
    }
    
    /**
     * Nils the "Signatures" element
     */
    public void setNilSignatures()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary target = null;
            target = (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary)get_store().find_element_user(SIGNATURES$2, 0);
            if (target == null)
            {
                target = (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary)get_store().add_element_user(SIGNATURES$2);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Signatures" element
     */
    public void unsetSignatures()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(SIGNATURES$2, 0);
        }
    }
}
