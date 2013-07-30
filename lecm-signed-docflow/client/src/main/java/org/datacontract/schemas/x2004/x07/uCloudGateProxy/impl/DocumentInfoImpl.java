/*
 * XML Type:  DocumentInfo
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxy.impl;
/**
 * An XML DocumentInfo(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy).
 *
 * This is a complex type.
 */
public class DocumentInfoImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo
{
    private static final long serialVersionUID = 1L;
    
    public DocumentInfoImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName COMMENT$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "Comment");
    private static final javax.xml.namespace.QName DOCFLOWID$2 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "DocflowId");
    private static final javax.xml.namespace.QName DOCUMENTID$4 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "DocumentId");
    private static final javax.xml.namespace.QName DOCUMENTIDPARTNERS$6 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "DocumentIdPartners");
    private static final javax.xml.namespace.QName DOCUMENTTYPE$8 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "DocumentType");
    private static final javax.xml.namespace.QName FILENAME$10 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "FileName");
    private static final javax.xml.namespace.QName ISUNREAD$12 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "IsUnread");
    private static final javax.xml.namespace.QName PARENTDOCUMENTID$14 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "ParentDocumentId");
    private static final javax.xml.namespace.QName RECEIVER$16 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "Receiver");
    private static final javax.xml.namespace.QName SENDER$18 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "Sender");
    private static final javax.xml.namespace.QName TRANSACTIONTYPE$20 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "TransactionType");
    
    
    /**
     * Gets the "Comment" element
     */
    public java.lang.String getComment()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(COMMENT$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Comment" element
     */
    public org.apache.xmlbeans.XmlString xgetComment()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(COMMENT$0, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "Comment" element
     */
    public boolean isNilComment()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(COMMENT$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Comment" element
     */
    public boolean isSetComment()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(COMMENT$0) != 0;
        }
    }
    
    /**
     * Sets the "Comment" element
     */
    public void setComment(java.lang.String comment)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(COMMENT$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(COMMENT$0);
            }
            target.setStringValue(comment);
        }
    }
    
    /**
     * Sets (as xml) the "Comment" element
     */
    public void xsetComment(org.apache.xmlbeans.XmlString comment)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(COMMENT$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(COMMENT$0);
            }
            target.set(comment);
        }
    }
    
    /**
     * Nils the "Comment" element
     */
    public void setNilComment()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(COMMENT$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(COMMENT$0);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Comment" element
     */
    public void unsetComment()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(COMMENT$0, 0);
        }
    }
    
    /**
     * Gets the "DocflowId" element
     */
    public java.lang.String getDocflowId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DOCFLOWID$2, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "DocflowId" element
     */
    public com.microsoft.schemas.x2003.x10.serialization.Guid xgetDocflowId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.microsoft.schemas.x2003.x10.serialization.Guid target = null;
            target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().find_element_user(DOCFLOWID$2, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "DocflowId" element
     */
    public boolean isNilDocflowId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.microsoft.schemas.x2003.x10.serialization.Guid target = null;
            target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().find_element_user(DOCFLOWID$2, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "DocflowId" element
     */
    public boolean isSetDocflowId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(DOCFLOWID$2) != 0;
        }
    }
    
    /**
     * Sets the "DocflowId" element
     */
    public void setDocflowId(java.lang.String docflowId)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DOCFLOWID$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(DOCFLOWID$2);
            }
            target.setStringValue(docflowId);
        }
    }
    
    /**
     * Sets (as xml) the "DocflowId" element
     */
    public void xsetDocflowId(com.microsoft.schemas.x2003.x10.serialization.Guid docflowId)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.microsoft.schemas.x2003.x10.serialization.Guid target = null;
            target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().find_element_user(DOCFLOWID$2, 0);
            if (target == null)
            {
                target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().add_element_user(DOCFLOWID$2);
            }
            target.set(docflowId);
        }
    }
    
    /**
     * Nils the "DocflowId" element
     */
    public void setNilDocflowId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.microsoft.schemas.x2003.x10.serialization.Guid target = null;
            target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().find_element_user(DOCFLOWID$2, 0);
            if (target == null)
            {
                target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().add_element_user(DOCFLOWID$2);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "DocflowId" element
     */
    public void unsetDocflowId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(DOCFLOWID$2, 0);
        }
    }
    
    /**
     * Gets the "DocumentId" element
     */
    public java.lang.String getDocumentId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DOCUMENTID$4, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "DocumentId" element
     */
    public com.microsoft.schemas.x2003.x10.serialization.Guid xgetDocumentId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.microsoft.schemas.x2003.x10.serialization.Guid target = null;
            target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().find_element_user(DOCUMENTID$4, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "DocumentId" element
     */
    public boolean isNilDocumentId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.microsoft.schemas.x2003.x10.serialization.Guid target = null;
            target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().find_element_user(DOCUMENTID$4, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "DocumentId" element
     */
    public boolean isSetDocumentId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(DOCUMENTID$4) != 0;
        }
    }
    
    /**
     * Sets the "DocumentId" element
     */
    public void setDocumentId(java.lang.String documentId)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DOCUMENTID$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(DOCUMENTID$4);
            }
            target.setStringValue(documentId);
        }
    }
    
    /**
     * Sets (as xml) the "DocumentId" element
     */
    public void xsetDocumentId(com.microsoft.schemas.x2003.x10.serialization.Guid documentId)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.microsoft.schemas.x2003.x10.serialization.Guid target = null;
            target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().find_element_user(DOCUMENTID$4, 0);
            if (target == null)
            {
                target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().add_element_user(DOCUMENTID$4);
            }
            target.set(documentId);
        }
    }
    
    /**
     * Nils the "DocumentId" element
     */
    public void setNilDocumentId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.microsoft.schemas.x2003.x10.serialization.Guid target = null;
            target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().find_element_user(DOCUMENTID$4, 0);
            if (target == null)
            {
                target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().add_element_user(DOCUMENTID$4);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "DocumentId" element
     */
    public void unsetDocumentId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(DOCUMENTID$4, 0);
        }
    }
    
    /**
     * Gets the "DocumentIdPartners" element
     */
    public java.lang.String getDocumentIdPartners()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DOCUMENTIDPARTNERS$6, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "DocumentIdPartners" element
     */
    public com.microsoft.schemas.x2003.x10.serialization.Guid xgetDocumentIdPartners()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.microsoft.schemas.x2003.x10.serialization.Guid target = null;
            target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().find_element_user(DOCUMENTIDPARTNERS$6, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "DocumentIdPartners" element
     */
    public boolean isNilDocumentIdPartners()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.microsoft.schemas.x2003.x10.serialization.Guid target = null;
            target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().find_element_user(DOCUMENTIDPARTNERS$6, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "DocumentIdPartners" element
     */
    public boolean isSetDocumentIdPartners()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(DOCUMENTIDPARTNERS$6) != 0;
        }
    }
    
    /**
     * Sets the "DocumentIdPartners" element
     */
    public void setDocumentIdPartners(java.lang.String documentIdPartners)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DOCUMENTIDPARTNERS$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(DOCUMENTIDPARTNERS$6);
            }
            target.setStringValue(documentIdPartners);
        }
    }
    
    /**
     * Sets (as xml) the "DocumentIdPartners" element
     */
    public void xsetDocumentIdPartners(com.microsoft.schemas.x2003.x10.serialization.Guid documentIdPartners)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.microsoft.schemas.x2003.x10.serialization.Guid target = null;
            target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().find_element_user(DOCUMENTIDPARTNERS$6, 0);
            if (target == null)
            {
                target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().add_element_user(DOCUMENTIDPARTNERS$6);
            }
            target.set(documentIdPartners);
        }
    }
    
    /**
     * Nils the "DocumentIdPartners" element
     */
    public void setNilDocumentIdPartners()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.microsoft.schemas.x2003.x10.serialization.Guid target = null;
            target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().find_element_user(DOCUMENTIDPARTNERS$6, 0);
            if (target == null)
            {
                target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().add_element_user(DOCUMENTIDPARTNERS$6);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "DocumentIdPartners" element
     */
    public void unsetDocumentIdPartners()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(DOCUMENTIDPARTNERS$6, 0);
        }
    }
    
    /**
     * Gets the "DocumentType" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType.Enum getDocumentType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DOCUMENTTYPE$8, 0);
            if (target == null)
            {
                return null;
            }
            return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType.Enum)target.getEnumValue();
        }
    }
    
    /**
     * Gets (as xml) the "DocumentType" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType xgetDocumentType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType)get_store().find_element_user(DOCUMENTTYPE$8, 0);
            return target;
        }
    }
    
    /**
     * True if has "DocumentType" element
     */
    public boolean isSetDocumentType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(DOCUMENTTYPE$8) != 0;
        }
    }
    
    /**
     * Sets the "DocumentType" element
     */
    public void setDocumentType(org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType.Enum documentType)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DOCUMENTTYPE$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(DOCUMENTTYPE$8);
            }
            target.setEnumValue(documentType);
        }
    }
    
    /**
     * Sets (as xml) the "DocumentType" element
     */
    public void xsetDocumentType(org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType documentType)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType)get_store().find_element_user(DOCUMENTTYPE$8, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType)get_store().add_element_user(DOCUMENTTYPE$8);
            }
            target.set(documentType);
        }
    }
    
    /**
     * Unsets the "DocumentType" element
     */
    public void unsetDocumentType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(DOCUMENTTYPE$8, 0);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(FILENAME$10, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FILENAME$10, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FILENAME$10, 0);
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
            return get_store().count_elements(FILENAME$10) != 0;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(FILENAME$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(FILENAME$10);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FILENAME$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(FILENAME$10);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FILENAME$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(FILENAME$10);
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
            get_store().remove_element(FILENAME$10, 0);
        }
    }
    
    /**
     * Gets the "IsUnread" element
     */
    public boolean getIsUnread()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ISUNREAD$12, 0);
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "IsUnread" element
     */
    public org.apache.xmlbeans.XmlBoolean xgetIsUnread()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(ISUNREAD$12, 0);
            return target;
        }
    }
    
    /**
     * True if has "IsUnread" element
     */
    public boolean isSetIsUnread()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(ISUNREAD$12) != 0;
        }
    }
    
    /**
     * Sets the "IsUnread" element
     */
    public void setIsUnread(boolean isUnread)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ISUNREAD$12, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ISUNREAD$12);
            }
            target.setBooleanValue(isUnread);
        }
    }
    
    /**
     * Sets (as xml) the "IsUnread" element
     */
    public void xsetIsUnread(org.apache.xmlbeans.XmlBoolean isUnread)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(ISUNREAD$12, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_element_user(ISUNREAD$12);
            }
            target.set(isUnread);
        }
    }
    
    /**
     * Unsets the "IsUnread" element
     */
    public void unsetIsUnread()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(ISUNREAD$12, 0);
        }
    }
    
    /**
     * Gets the "ParentDocumentId" element
     */
    public java.lang.String getParentDocumentId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PARENTDOCUMENTID$14, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "ParentDocumentId" element
     */
    public com.microsoft.schemas.x2003.x10.serialization.Guid xgetParentDocumentId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.microsoft.schemas.x2003.x10.serialization.Guid target = null;
            target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().find_element_user(PARENTDOCUMENTID$14, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "ParentDocumentId" element
     */
    public boolean isNilParentDocumentId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.microsoft.schemas.x2003.x10.serialization.Guid target = null;
            target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().find_element_user(PARENTDOCUMENTID$14, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "ParentDocumentId" element
     */
    public boolean isSetParentDocumentId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(PARENTDOCUMENTID$14) != 0;
        }
    }
    
    /**
     * Sets the "ParentDocumentId" element
     */
    public void setParentDocumentId(java.lang.String parentDocumentId)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PARENTDOCUMENTID$14, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PARENTDOCUMENTID$14);
            }
            target.setStringValue(parentDocumentId);
        }
    }
    
    /**
     * Sets (as xml) the "ParentDocumentId" element
     */
    public void xsetParentDocumentId(com.microsoft.schemas.x2003.x10.serialization.Guid parentDocumentId)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.microsoft.schemas.x2003.x10.serialization.Guid target = null;
            target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().find_element_user(PARENTDOCUMENTID$14, 0);
            if (target == null)
            {
                target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().add_element_user(PARENTDOCUMENTID$14);
            }
            target.set(parentDocumentId);
        }
    }
    
    /**
     * Nils the "ParentDocumentId" element
     */
    public void setNilParentDocumentId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.microsoft.schemas.x2003.x10.serialization.Guid target = null;
            target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().find_element_user(PARENTDOCUMENTID$14, 0);
            if (target == null)
            {
                target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().add_element_user(PARENTDOCUMENTID$14);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "ParentDocumentId" element
     */
    public void unsetParentDocumentId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(PARENTDOCUMENTID$14, 0);
        }
    }
    
    /**
     * Gets the "Receiver" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo getReceiver()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo)get_store().find_element_user(RECEIVER$16, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "Receiver" element
     */
    public boolean isNilReceiver()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo)get_store().find_element_user(RECEIVER$16, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Receiver" element
     */
    public boolean isSetReceiver()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(RECEIVER$16) != 0;
        }
    }
    
    /**
     * Sets the "Receiver" element
     */
    public void setReceiver(org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo receiver)
    {
        generatedSetterHelperImpl(receiver, RECEIVER$16, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "Receiver" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo addNewReceiver()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo)get_store().add_element_user(RECEIVER$16);
            return target;
        }
    }
    
    /**
     * Nils the "Receiver" element
     */
    public void setNilReceiver()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo)get_store().find_element_user(RECEIVER$16, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo)get_store().add_element_user(RECEIVER$16);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Receiver" element
     */
    public void unsetReceiver()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(RECEIVER$16, 0);
        }
    }
    
    /**
     * Gets the "Sender" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo getSender()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo)get_store().find_element_user(SENDER$18, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "Sender" element
     */
    public boolean isNilSender()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo)get_store().find_element_user(SENDER$18, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Sender" element
     */
    public boolean isSetSender()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(SENDER$18) != 0;
        }
    }
    
    /**
     * Sets the "Sender" element
     */
    public void setSender(org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo sender)
    {
        generatedSetterHelperImpl(sender, SENDER$18, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "Sender" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo addNewSender()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo)get_store().add_element_user(SENDER$18);
            return target;
        }
    }
    
    /**
     * Nils the "Sender" element
     */
    public void setNilSender()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo)get_store().find_element_user(SENDER$18, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo)get_store().add_element_user(SENDER$18);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Sender" element
     */
    public void unsetSender()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(SENDER$18, 0);
        }
    }
    
    /**
     * Gets the "TransactionType" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType.Enum getTransactionType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(TRANSACTIONTYPE$20, 0);
            if (target == null)
            {
                return null;
            }
            return (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType.Enum)target.getEnumValue();
        }
    }
    
    /**
     * Gets (as xml) the "TransactionType" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType xgetTransactionType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType)get_store().find_element_user(TRANSACTIONTYPE$20, 0);
            return target;
        }
    }
    
    /**
     * True if has "TransactionType" element
     */
    public boolean isSetTransactionType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(TRANSACTIONTYPE$20) != 0;
        }
    }
    
    /**
     * Sets the "TransactionType" element
     */
    public void setTransactionType(org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType.Enum transactionType)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(TRANSACTIONTYPE$20, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(TRANSACTIONTYPE$20);
            }
            target.setEnumValue(transactionType);
        }
    }
    
    /**
     * Sets (as xml) the "TransactionType" element
     */
    public void xsetTransactionType(org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType transactionType)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType)get_store().find_element_user(TRANSACTIONTYPE$20, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType)get_store().add_element_user(TRANSACTIONTYPE$20);
            }
            target.set(transactionType);
        }
    }
    
    /**
     * Unsets the "TransactionType" element
     */
    public void unsetTransactionType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(TRANSACTIONTYPE$20, 0);
        }
    }
}
