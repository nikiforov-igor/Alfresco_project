/*
 * XML Type:  DocumentToSend
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxy.impl;
/**
 * An XML DocumentToSend(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy).
 *
 * This is a complex type.
 */
public class DocumentToSendImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend
{
    private static final long serialVersionUID = 1L;
    
    public DocumentToSendImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName COMMENT$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "Comment");
    private static final javax.xml.namespace.QName CONTENT$2 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "Content");
    private static final javax.xml.namespace.QName DOCFLOWID$4 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "DocflowId");
    private static final javax.xml.namespace.QName DOCUMENTTYPE$6 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "DocumentType");
    private static final javax.xml.namespace.QName FILENAME$8 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "FileName");
    private static final javax.xml.namespace.QName ID$10 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "Id");
    private static final javax.xml.namespace.QName RECEIVER$12 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "Receiver");
    private static final javax.xml.namespace.QName SIGNATURES$14 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "Signatures");
    private static final javax.xml.namespace.QName TRANSACTIONTYPE$16 = 
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
     * Gets the "Content" element
     */
    public byte[] getContent()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CONTENT$2, 0);
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
            target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(CONTENT$2, 0);
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
            target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(CONTENT$2, 0);
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
            return get_store().count_elements(CONTENT$2) != 0;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CONTENT$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(CONTENT$2);
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
            target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(CONTENT$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBase64Binary)get_store().add_element_user(CONTENT$2);
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
            target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(CONTENT$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBase64Binary)get_store().add_element_user(CONTENT$2);
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
            get_store().remove_element(CONTENT$2, 0);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DOCFLOWID$4, 0);
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
            target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().find_element_user(DOCFLOWID$4, 0);
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
            target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().find_element_user(DOCFLOWID$4, 0);
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
            return get_store().count_elements(DOCFLOWID$4) != 0;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DOCFLOWID$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(DOCFLOWID$4);
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
            target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().find_element_user(DOCFLOWID$4, 0);
            if (target == null)
            {
                target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().add_element_user(DOCFLOWID$4);
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
            target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().find_element_user(DOCFLOWID$4, 0);
            if (target == null)
            {
                target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().add_element_user(DOCFLOWID$4);
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
            get_store().remove_element(DOCFLOWID$4, 0);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DOCUMENTTYPE$6, 0);
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
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType)get_store().find_element_user(DOCUMENTTYPE$6, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "DocumentType" element
     */
    public boolean isNilDocumentType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType)get_store().find_element_user(DOCUMENTTYPE$6, 0);
            if (target == null) return false;
            return target.isNil();
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
            return get_store().count_elements(DOCUMENTTYPE$6) != 0;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DOCUMENTTYPE$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(DOCUMENTTYPE$6);
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
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType)get_store().find_element_user(DOCUMENTTYPE$6, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType)get_store().add_element_user(DOCUMENTTYPE$6);
            }
            target.set(documentType);
        }
    }
    
    /**
     * Nils the "DocumentType" element
     */
    public void setNilDocumentType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType)get_store().find_element_user(DOCUMENTTYPE$6, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType)get_store().add_element_user(DOCUMENTTYPE$6);
            }
            target.setNil();
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
            get_store().remove_element(DOCUMENTTYPE$6, 0);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(FILENAME$8, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FILENAME$8, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FILENAME$8, 0);
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
            return get_store().count_elements(FILENAME$8) != 0;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(FILENAME$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(FILENAME$8);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FILENAME$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(FILENAME$8);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FILENAME$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(FILENAME$8);
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
            get_store().remove_element(FILENAME$8, 0);
        }
    }
    
    /**
     * Gets the "Id" element
     */
    public java.lang.String getId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ID$10, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Id" element
     */
    public com.microsoft.schemas.x2003.x10.serialization.Guid xgetId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.microsoft.schemas.x2003.x10.serialization.Guid target = null;
            target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().find_element_user(ID$10, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "Id" element
     */
    public boolean isNilId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.microsoft.schemas.x2003.x10.serialization.Guid target = null;
            target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().find_element_user(ID$10, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Id" element
     */
    public boolean isSetId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(ID$10) != 0;
        }
    }
    
    /**
     * Sets the "Id" element
     */
    public void setId(java.lang.String id)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ID$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ID$10);
            }
            target.setStringValue(id);
        }
    }
    
    /**
     * Sets (as xml) the "Id" element
     */
    public void xsetId(com.microsoft.schemas.x2003.x10.serialization.Guid id)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.microsoft.schemas.x2003.x10.serialization.Guid target = null;
            target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().find_element_user(ID$10, 0);
            if (target == null)
            {
                target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().add_element_user(ID$10);
            }
            target.set(id);
        }
    }
    
    /**
     * Nils the "Id" element
     */
    public void setNilId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.microsoft.schemas.x2003.x10.serialization.Guid target = null;
            target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().find_element_user(ID$10, 0);
            if (target == null)
            {
                target = (com.microsoft.schemas.x2003.x10.serialization.Guid)get_store().add_element_user(ID$10);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Id" element
     */
    public void unsetId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(ID$10, 0);
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
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo)get_store().find_element_user(RECEIVER$12, 0);
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
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo)get_store().find_element_user(RECEIVER$12, 0);
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
            return get_store().count_elements(RECEIVER$12) != 0;
        }
    }
    
    /**
     * Sets the "Receiver" element
     */
    public void setReceiver(org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo receiver)
    {
        generatedSetterHelperImpl(receiver, RECEIVER$12, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
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
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo)get_store().add_element_user(RECEIVER$12);
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
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo)get_store().find_element_user(RECEIVER$12, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo)get_store().add_element_user(RECEIVER$12);
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
            get_store().remove_element(RECEIVER$12, 0);
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
            target = (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary)get_store().find_element_user(SIGNATURES$14, 0);
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
            target = (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary)get_store().find_element_user(SIGNATURES$14, 0);
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
            return get_store().count_elements(SIGNATURES$14) != 0;
        }
    }
    
    /**
     * Sets the "Signatures" element
     */
    public void setSignatures(com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary signatures)
    {
        generatedSetterHelperImpl(signatures, SIGNATURES$14, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
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
            target = (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary)get_store().add_element_user(SIGNATURES$14);
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
            target = (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary)get_store().find_element_user(SIGNATURES$14, 0);
            if (target == null)
            {
                target = (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary)get_store().add_element_user(SIGNATURES$14);
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
            get_store().remove_element(SIGNATURES$14, 0);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(TRANSACTIONTYPE$16, 0);
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
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType)get_store().find_element_user(TRANSACTIONTYPE$16, 0);
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
            return get_store().count_elements(TRANSACTIONTYPE$16) != 0;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(TRANSACTIONTYPE$16, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(TRANSACTIONTYPE$16);
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
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType)get_store().find_element_user(TRANSACTIONTYPE$16, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType)get_store().add_element_user(TRANSACTIONTYPE$16);
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
            get_store().remove_element(TRANSACTIONTYPE$16, 0);
        }
    }
}
