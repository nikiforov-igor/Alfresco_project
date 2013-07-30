/*
 * XML Type:  GeneratedDocument
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.GeneratedDocument1
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.impl;
/**
 * An XML GeneratedDocument(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments).
 *
 * This is a complex type.
 */
public class GeneratedDocument1Impl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.GeneratedDocument1
{
    private static final long serialVersionUID = 1L;
    
    public GeneratedDocument1Impl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName CONTENT$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments", "Content");
    private static final javax.xml.namespace.QName FILENAME$2 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments", "FileName");
    
    
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
     * Gets the "FileName" element
     */
    public java.lang.String getFileName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(FILENAME$2, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "FileName" element
     */
    public org.apache.xmlbeans.XmlString xgetFileName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FILENAME$2, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "FileName" element
     */
    public boolean isNilFileName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FILENAME$2, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "FileName" element
     */
    public boolean isSetFileName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(FILENAME$2) != 0;
        }
    }
    
    /**
     * Sets the "FileName" element
     */
    public void setFileName(java.lang.String fileName)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(FILENAME$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(FILENAME$2);
            }
            target.setStringValue(fileName);
        }
    }
    
    /**
     * Sets (as xml) the "FileName" element
     */
    public void xsetFileName(org.apache.xmlbeans.XmlString fileName)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FILENAME$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(FILENAME$2);
            }
            target.set(fileName);
        }
    }
    
    /**
     * Nils the "FileName" element
     */
    public void setNilFileName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FILENAME$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(FILENAME$2);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "FileName" element
     */
    public void unsetFileName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(FILENAME$2, 0);
        }
    }
}
