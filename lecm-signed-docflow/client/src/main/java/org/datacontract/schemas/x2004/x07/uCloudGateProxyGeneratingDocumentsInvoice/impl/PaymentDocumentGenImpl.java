/*
 * XML Type:  PaymentDocumentGen
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.PaymentDocumentGen
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.impl;
/**
 * An XML PaymentDocumentGen(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice).
 *
 * This is a complex type.
 */
public class PaymentDocumentGenImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.PaymentDocumentGen
{
    private static final long serialVersionUID = 1L;
    
    public PaymentDocumentGenImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName DOCUMENTDATE$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "DocumentDate");
    private static final javax.xml.namespace.QName DOCUMENTNUMBER$2 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "DocumentNumber");
    
    
    /**
     * Gets the "DocumentDate" element
     */
    public java.util.Calendar getDocumentDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DOCUMENTDATE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getCalendarValue();
        }
    }
    
    /**
     * Gets (as xml) the "DocumentDate" element
     */
    public org.apache.xmlbeans.XmlDateTime xgetDocumentDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlDateTime target = null;
            target = (org.apache.xmlbeans.XmlDateTime)get_store().find_element_user(DOCUMENTDATE$0, 0);
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
            org.apache.xmlbeans.XmlDateTime target = null;
            target = (org.apache.xmlbeans.XmlDateTime)get_store().find_element_user(DOCUMENTDATE$0, 0);
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
            return get_store().count_elements(DOCUMENTDATE$0) != 0;
        }
    }
    
    /**
     * Sets the "DocumentDate" element
     */
    public void setDocumentDate(java.util.Calendar documentDate)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DOCUMENTDATE$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(DOCUMENTDATE$0);
            }
            target.setCalendarValue(documentDate);
        }
    }
    
    /**
     * Sets (as xml) the "DocumentDate" element
     */
    public void xsetDocumentDate(org.apache.xmlbeans.XmlDateTime documentDate)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlDateTime target = null;
            target = (org.apache.xmlbeans.XmlDateTime)get_store().find_element_user(DOCUMENTDATE$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlDateTime)get_store().add_element_user(DOCUMENTDATE$0);
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
            org.apache.xmlbeans.XmlDateTime target = null;
            target = (org.apache.xmlbeans.XmlDateTime)get_store().find_element_user(DOCUMENTDATE$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlDateTime)get_store().add_element_user(DOCUMENTDATE$0);
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
            get_store().remove_element(DOCUMENTDATE$0, 0);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DOCUMENTNUMBER$2, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DOCUMENTNUMBER$2, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DOCUMENTNUMBER$2, 0);
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
            return get_store().count_elements(DOCUMENTNUMBER$2) != 0;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DOCUMENTNUMBER$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(DOCUMENTNUMBER$2);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DOCUMENTNUMBER$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(DOCUMENTNUMBER$2);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DOCUMENTNUMBER$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(DOCUMENTNUMBER$2);
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
            get_store().remove_element(DOCUMENTNUMBER$2, 0);
        }
    }
}
