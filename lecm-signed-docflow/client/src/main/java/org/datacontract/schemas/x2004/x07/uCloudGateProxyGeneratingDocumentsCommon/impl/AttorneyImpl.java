/*
 * XML Type:  Attorney
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.Attorney
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.impl;
/**
 * An XML Attorney(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common).
 *
 * This is a complex type.
 */
public class AttorneyImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.Attorney
{
    private static final long serialVersionUID = 1L;
    
    public AttorneyImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName DATE$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common", "Date");
    private static final javax.xml.namespace.QName ISSUERADDITIONALINFO$2 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common", "IssuerAdditionalInfo");
    private static final javax.xml.namespace.QName ISSUERORGANIZATIONNAME$4 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common", "IssuerOrganizationName");
    private static final javax.xml.namespace.QName ISSUERPERSON$6 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common", "IssuerPerson");
    private static final javax.xml.namespace.QName NUMBER$8 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common", "Number");
    private static final javax.xml.namespace.QName RECIPIENTADDITIONALINFO$10 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common", "RecipientAdditionalInfo");
    private static final javax.xml.namespace.QName RECIPIENTPERSON$12 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common", "RecipientPerson");
    
    
    /**
     * Gets the "Date" element
     */
    public java.util.Calendar getDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DATE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getCalendarValue();
        }
    }
    
    /**
     * Gets (as xml) the "Date" element
     */
    public org.apache.xmlbeans.XmlDateTime xgetDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlDateTime target = null;
            target = (org.apache.xmlbeans.XmlDateTime)get_store().find_element_user(DATE$0, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "Date" element
     */
    public boolean isNilDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlDateTime target = null;
            target = (org.apache.xmlbeans.XmlDateTime)get_store().find_element_user(DATE$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Date" element
     */
    public boolean isSetDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(DATE$0) != 0;
        }
    }
    
    /**
     * Sets the "Date" element
     */
    public void setDate(java.util.Calendar date)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DATE$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(DATE$0);
            }
            target.setCalendarValue(date);
        }
    }
    
    /**
     * Sets (as xml) the "Date" element
     */
    public void xsetDate(org.apache.xmlbeans.XmlDateTime date)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlDateTime target = null;
            target = (org.apache.xmlbeans.XmlDateTime)get_store().find_element_user(DATE$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlDateTime)get_store().add_element_user(DATE$0);
            }
            target.set(date);
        }
    }
    
    /**
     * Nils the "Date" element
     */
    public void setNilDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlDateTime target = null;
            target = (org.apache.xmlbeans.XmlDateTime)get_store().find_element_user(DATE$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlDateTime)get_store().add_element_user(DATE$0);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Date" element
     */
    public void unsetDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(DATE$0, 0);
        }
    }
    
    /**
     * Gets the "IssuerAdditionalInfo" element
     */
    public java.lang.String getIssuerAdditionalInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ISSUERADDITIONALINFO$2, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "IssuerAdditionalInfo" element
     */
    public org.apache.xmlbeans.XmlString xgetIssuerAdditionalInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ISSUERADDITIONALINFO$2, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "IssuerAdditionalInfo" element
     */
    public boolean isNilIssuerAdditionalInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ISSUERADDITIONALINFO$2, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "IssuerAdditionalInfo" element
     */
    public boolean isSetIssuerAdditionalInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(ISSUERADDITIONALINFO$2) != 0;
        }
    }
    
    /**
     * Sets the "IssuerAdditionalInfo" element
     */
    public void setIssuerAdditionalInfo(java.lang.String issuerAdditionalInfo)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ISSUERADDITIONALINFO$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ISSUERADDITIONALINFO$2);
            }
            target.setStringValue(issuerAdditionalInfo);
        }
    }
    
    /**
     * Sets (as xml) the "IssuerAdditionalInfo" element
     */
    public void xsetIssuerAdditionalInfo(org.apache.xmlbeans.XmlString issuerAdditionalInfo)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ISSUERADDITIONALINFO$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(ISSUERADDITIONALINFO$2);
            }
            target.set(issuerAdditionalInfo);
        }
    }
    
    /**
     * Nils the "IssuerAdditionalInfo" element
     */
    public void setNilIssuerAdditionalInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ISSUERADDITIONALINFO$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(ISSUERADDITIONALINFO$2);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "IssuerAdditionalInfo" element
     */
    public void unsetIssuerAdditionalInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(ISSUERADDITIONALINFO$2, 0);
        }
    }
    
    /**
     * Gets the "IssuerOrganizationName" element
     */
    public java.lang.String getIssuerOrganizationName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ISSUERORGANIZATIONNAME$4, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "IssuerOrganizationName" element
     */
    public org.apache.xmlbeans.XmlString xgetIssuerOrganizationName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ISSUERORGANIZATIONNAME$4, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "IssuerOrganizationName" element
     */
    public boolean isNilIssuerOrganizationName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ISSUERORGANIZATIONNAME$4, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "IssuerOrganizationName" element
     */
    public boolean isSetIssuerOrganizationName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(ISSUERORGANIZATIONNAME$4) != 0;
        }
    }
    
    /**
     * Sets the "IssuerOrganizationName" element
     */
    public void setIssuerOrganizationName(java.lang.String issuerOrganizationName)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ISSUERORGANIZATIONNAME$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ISSUERORGANIZATIONNAME$4);
            }
            target.setStringValue(issuerOrganizationName);
        }
    }
    
    /**
     * Sets (as xml) the "IssuerOrganizationName" element
     */
    public void xsetIssuerOrganizationName(org.apache.xmlbeans.XmlString issuerOrganizationName)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ISSUERORGANIZATIONNAME$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(ISSUERORGANIZATIONNAME$4);
            }
            target.set(issuerOrganizationName);
        }
    }
    
    /**
     * Nils the "IssuerOrganizationName" element
     */
    public void setNilIssuerOrganizationName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ISSUERORGANIZATIONNAME$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(ISSUERORGANIZATIONNAME$4);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "IssuerOrganizationName" element
     */
    public void unsetIssuerOrganizationName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(ISSUERORGANIZATIONNAME$4, 0);
        }
    }
    
    /**
     * Gets the "IssuerPerson" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.Official getIssuerPerson()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.Official target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.Official)get_store().find_element_user(ISSUERPERSON$6, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "IssuerPerson" element
     */
    public boolean isNilIssuerPerson()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.Official target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.Official)get_store().find_element_user(ISSUERPERSON$6, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "IssuerPerson" element
     */
    public boolean isSetIssuerPerson()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(ISSUERPERSON$6) != 0;
        }
    }
    
    /**
     * Sets the "IssuerPerson" element
     */
    public void setIssuerPerson(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.Official issuerPerson)
    {
        generatedSetterHelperImpl(issuerPerson, ISSUERPERSON$6, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "IssuerPerson" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.Official addNewIssuerPerson()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.Official target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.Official)get_store().add_element_user(ISSUERPERSON$6);
            return target;
        }
    }
    
    /**
     * Nils the "IssuerPerson" element
     */
    public void setNilIssuerPerson()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.Official target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.Official)get_store().find_element_user(ISSUERPERSON$6, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.Official)get_store().add_element_user(ISSUERPERSON$6);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "IssuerPerson" element
     */
    public void unsetIssuerPerson()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(ISSUERPERSON$6, 0);
        }
    }
    
    /**
     * Gets the "Number" element
     */
    public java.lang.String getNumber()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(NUMBER$8, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Number" element
     */
    public org.apache.xmlbeans.XmlString xgetNumber()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NUMBER$8, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "Number" element
     */
    public boolean isNilNumber()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NUMBER$8, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Number" element
     */
    public boolean isSetNumber()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(NUMBER$8) != 0;
        }
    }
    
    /**
     * Sets the "Number" element
     */
    public void setNumber(java.lang.String number)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(NUMBER$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(NUMBER$8);
            }
            target.setStringValue(number);
        }
    }
    
    /**
     * Sets (as xml) the "Number" element
     */
    public void xsetNumber(org.apache.xmlbeans.XmlString number)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NUMBER$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(NUMBER$8);
            }
            target.set(number);
        }
    }
    
    /**
     * Nils the "Number" element
     */
    public void setNilNumber()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NUMBER$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(NUMBER$8);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Number" element
     */
    public void unsetNumber()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(NUMBER$8, 0);
        }
    }
    
    /**
     * Gets the "RecipientAdditionalInfo" element
     */
    public java.lang.String getRecipientAdditionalInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(RECIPIENTADDITIONALINFO$10, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "RecipientAdditionalInfo" element
     */
    public org.apache.xmlbeans.XmlString xgetRecipientAdditionalInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(RECIPIENTADDITIONALINFO$10, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "RecipientAdditionalInfo" element
     */
    public boolean isNilRecipientAdditionalInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(RECIPIENTADDITIONALINFO$10, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "RecipientAdditionalInfo" element
     */
    public boolean isSetRecipientAdditionalInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(RECIPIENTADDITIONALINFO$10) != 0;
        }
    }
    
    /**
     * Sets the "RecipientAdditionalInfo" element
     */
    public void setRecipientAdditionalInfo(java.lang.String recipientAdditionalInfo)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(RECIPIENTADDITIONALINFO$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(RECIPIENTADDITIONALINFO$10);
            }
            target.setStringValue(recipientAdditionalInfo);
        }
    }
    
    /**
     * Sets (as xml) the "RecipientAdditionalInfo" element
     */
    public void xsetRecipientAdditionalInfo(org.apache.xmlbeans.XmlString recipientAdditionalInfo)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(RECIPIENTADDITIONALINFO$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(RECIPIENTADDITIONALINFO$10);
            }
            target.set(recipientAdditionalInfo);
        }
    }
    
    /**
     * Nils the "RecipientAdditionalInfo" element
     */
    public void setNilRecipientAdditionalInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(RECIPIENTADDITIONALINFO$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(RECIPIENTADDITIONALINFO$10);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "RecipientAdditionalInfo" element
     */
    public void unsetRecipientAdditionalInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(RECIPIENTADDITIONALINFO$10, 0);
        }
    }
    
    /**
     * Gets the "RecipientPerson" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.Official getRecipientPerson()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.Official target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.Official)get_store().find_element_user(RECIPIENTPERSON$12, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "RecipientPerson" element
     */
    public boolean isNilRecipientPerson()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.Official target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.Official)get_store().find_element_user(RECIPIENTPERSON$12, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "RecipientPerson" element
     */
    public boolean isSetRecipientPerson()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(RECIPIENTPERSON$12) != 0;
        }
    }
    
    /**
     * Sets the "RecipientPerson" element
     */
    public void setRecipientPerson(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.Official recipientPerson)
    {
        generatedSetterHelperImpl(recipientPerson, RECIPIENTPERSON$12, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "RecipientPerson" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.Official addNewRecipientPerson()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.Official target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.Official)get_store().add_element_user(RECIPIENTPERSON$12);
            return target;
        }
    }
    
    /**
     * Nils the "RecipientPerson" element
     */
    public void setNilRecipientPerson()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.Official target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.Official)get_store().find_element_user(RECIPIENTPERSON$12, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.Official)get_store().add_element_user(RECIPIENTPERSON$12);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "RecipientPerson" element
     */
    public void unsetRecipientPerson()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(RECIPIENTPERSON$12, 0);
        }
    }
}
