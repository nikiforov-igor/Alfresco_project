/*
 * XML Type:  ParticipantCorporateWithOkopf
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantCorporateWithOkopf
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.impl;
/**
 * An XML ParticipantCorporateWithOkopf(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common).
 *
 * This is a complex type.
 */
public class ParticipantCorporateWithOkopfImpl extends org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.impl.ParticipantCorporateImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantCorporateWithOkopf
{
    private static final long serialVersionUID = 1L;
    
    public ParticipantCorporateWithOkopfImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName OKOPF$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common", "Okopf");
    
    
    /**
     * Gets the "Okopf" element
     */
    public java.lang.String getOkopf()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(OKOPF$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Okopf" element
     */
    public org.apache.xmlbeans.XmlString xgetOkopf()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OKOPF$0, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "Okopf" element
     */
    public boolean isNilOkopf()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OKOPF$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Okopf" element
     */
    public boolean isSetOkopf()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(OKOPF$0) != 0;
        }
    }
    
    /**
     * Sets the "Okopf" element
     */
    public void setOkopf(java.lang.String okopf)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(OKOPF$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(OKOPF$0);
            }
            target.setStringValue(okopf);
        }
    }
    
    /**
     * Sets (as xml) the "Okopf" element
     */
    public void xsetOkopf(org.apache.xmlbeans.XmlString okopf)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OKOPF$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(OKOPF$0);
            }
            target.set(okopf);
        }
    }
    
    /**
     * Nils the "Okopf" element
     */
    public void setNilOkopf()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OKOPF$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(OKOPF$0);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Okopf" element
     */
    public void unsetOkopf()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(OKOPF$0, 0);
        }
    }
}
