/*
 * XML Type:  Grounds
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.impl;
/**
 * An XML Grounds(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses).
 *
 * This is a complex type.
 */
public class GroundsImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds
{
    private static final long serialVersionUID = 1L;
    
    public GroundsImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName ADDITIONALINFO$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "AdditionalInfo");
    private static final javax.xml.namespace.QName DOCUMENTDATE$2 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "DocumentDate");
    private static final javax.xml.namespace.QName DOCUMENTNAME$4 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "DocumentName");
    private static final javax.xml.namespace.QName DOCUMENTNUMBER$6 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "DocumentNumber");
    
    
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
     * Gets the "DocumentDate" element
     */
    public java.lang.String getDocumentDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DOCUMENTDATE$2, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "DocumentDate" element
     */
    public org.apache.xmlbeans.XmlString xgetDocumentDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DOCUMENTDATE$2, 0);
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
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DOCUMENTDATE$2, 0);
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
            return get_store().count_elements(DOCUMENTDATE$2) != 0;
        }
    }
    
    /**
     * Sets the "DocumentDate" element
     */
    public void setDocumentDate(java.lang.String documentDate)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DOCUMENTDATE$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(DOCUMENTDATE$2);
            }
            target.setStringValue(documentDate);
        }
    }
    
    /**
     * Sets (as xml) the "DocumentDate" element
     */
    public void xsetDocumentDate(org.apache.xmlbeans.XmlString documentDate)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DOCUMENTDATE$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(DOCUMENTDATE$2);
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
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DOCUMENTDATE$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(DOCUMENTDATE$2);
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
            get_store().remove_element(DOCUMENTDATE$2, 0);
        }
    }
    
    /**
     * Gets the "DocumentName" element
     */
    public java.lang.String getDocumentName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DOCUMENTNAME$4, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "DocumentName" element
     */
    public org.apache.xmlbeans.XmlString xgetDocumentName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DOCUMENTNAME$4, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "DocumentName" element
     */
    public boolean isNilDocumentName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DOCUMENTNAME$4, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "DocumentName" element
     */
    public boolean isSetDocumentName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(DOCUMENTNAME$4) != 0;
        }
    }
    
    /**
     * Sets the "DocumentName" element
     */
    public void setDocumentName(java.lang.String documentName)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DOCUMENTNAME$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(DOCUMENTNAME$4);
            }
            target.setStringValue(documentName);
        }
    }
    
    /**
     * Sets (as xml) the "DocumentName" element
     */
    public void xsetDocumentName(org.apache.xmlbeans.XmlString documentName)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DOCUMENTNAME$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(DOCUMENTNAME$4);
            }
            target.set(documentName);
        }
    }
    
    /**
     * Nils the "DocumentName" element
     */
    public void setNilDocumentName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DOCUMENTNAME$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(DOCUMENTNAME$4);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "DocumentName" element
     */
    public void unsetDocumentName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(DOCUMENTNAME$4, 0);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DOCUMENTNUMBER$6, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DOCUMENTNUMBER$6, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DOCUMENTNUMBER$6, 0);
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
            return get_store().count_elements(DOCUMENTNUMBER$6) != 0;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DOCUMENTNUMBER$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(DOCUMENTNUMBER$6);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DOCUMENTNUMBER$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(DOCUMENTNUMBER$6);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DOCUMENTNUMBER$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(DOCUMENTNUMBER$6);
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
            get_store().remove_element(DOCUMENTNUMBER$6, 0);
        }
    }
}
