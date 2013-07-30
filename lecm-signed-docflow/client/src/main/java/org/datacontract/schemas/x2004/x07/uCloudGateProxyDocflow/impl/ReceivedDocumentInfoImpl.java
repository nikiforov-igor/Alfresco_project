/*
 * XML Type:  ReceivedDocumentInfo
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Docflow
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.impl;
/**
 * An XML ReceivedDocumentInfo(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Docflow).
 *
 * This is a complex type.
 */
public class ReceivedDocumentInfoImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo
{
    private static final long serialVersionUID = 1L;
    
    public ReceivedDocumentInfoImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName DATE$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Docflow", "Date");
    private static final javax.xml.namespace.QName DOCFLOWTYPE$2 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Docflow", "DocflowType");
    private static final javax.xml.namespace.QName FILENAME$4 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Docflow", "FileName");
    private static final javax.xml.namespace.QName NUMBER$6 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Docflow", "Number");
    private static final javax.xml.namespace.QName RECEIVEDATETIME$8 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Docflow", "ReceiveDateTime");
    private static final javax.xml.namespace.QName SENDER$10 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Docflow", "Sender");
    private static final javax.xml.namespace.QName SIGNATURES$12 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Docflow", "Signatures");
    
    
    /**
     * Gets the "Date" element
     */
    public java.lang.String getDate()
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
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Date" element
     */
    public org.apache.xmlbeans.XmlString xgetDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DATE$0, 0);
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
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DATE$0, 0);
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
    public void setDate(java.lang.String date)
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
            target.setStringValue(date);
        }
    }
    
    /**
     * Sets (as xml) the "Date" element
     */
    public void xsetDate(org.apache.xmlbeans.XmlString date)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DATE$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(DATE$0);
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
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DATE$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(DATE$0);
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
     * Gets the "DocflowType" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocflowType.Enum getDocflowType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DOCFLOWTYPE$2, 0);
            if (target == null)
            {
                return null;
            }
            return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocflowType.Enum)target.getEnumValue();
        }
    }
    
    /**
     * Gets (as xml) the "DocflowType" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocflowType xgetDocflowType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocflowType target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocflowType)get_store().find_element_user(DOCFLOWTYPE$2, 0);
            return target;
        }
    }
    
    /**
     * True if has "DocflowType" element
     */
    public boolean isSetDocflowType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(DOCFLOWTYPE$2) != 0;
        }
    }
    
    /**
     * Sets the "DocflowType" element
     */
    public void setDocflowType(org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocflowType.Enum docflowType)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DOCFLOWTYPE$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(DOCFLOWTYPE$2);
            }
            target.setEnumValue(docflowType);
        }
    }
    
    /**
     * Sets (as xml) the "DocflowType" element
     */
    public void xsetDocflowType(org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocflowType docflowType)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocflowType target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocflowType)get_store().find_element_user(DOCFLOWTYPE$2, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocflowType)get_store().add_element_user(DOCFLOWTYPE$2);
            }
            target.set(docflowType);
        }
    }
    
    /**
     * Unsets the "DocflowType" element
     */
    public void unsetDocflowType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(DOCFLOWTYPE$2, 0);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(FILENAME$4, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FILENAME$4, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FILENAME$4, 0);
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
            return get_store().count_elements(FILENAME$4) != 0;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(FILENAME$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(FILENAME$4);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FILENAME$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(FILENAME$4);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FILENAME$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(FILENAME$4);
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
            get_store().remove_element(FILENAME$4, 0);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(NUMBER$6, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NUMBER$6, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NUMBER$6, 0);
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
            return get_store().count_elements(NUMBER$6) != 0;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(NUMBER$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(NUMBER$6);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NUMBER$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(NUMBER$6);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NUMBER$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(NUMBER$6);
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
            get_store().remove_element(NUMBER$6, 0);
        }
    }
    
    /**
     * Gets the "ReceiveDateTime" element
     */
    public java.util.Calendar getReceiveDateTime()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(RECEIVEDATETIME$8, 0);
            if (target == null)
            {
                return null;
            }
            return target.getCalendarValue();
        }
    }
    
    /**
     * Gets (as xml) the "ReceiveDateTime" element
     */
    public org.apache.xmlbeans.XmlDateTime xgetReceiveDateTime()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlDateTime target = null;
            target = (org.apache.xmlbeans.XmlDateTime)get_store().find_element_user(RECEIVEDATETIME$8, 0);
            return target;
        }
    }
    
    /**
     * True if has "ReceiveDateTime" element
     */
    public boolean isSetReceiveDateTime()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(RECEIVEDATETIME$8) != 0;
        }
    }
    
    /**
     * Sets the "ReceiveDateTime" element
     */
    public void setReceiveDateTime(java.util.Calendar receiveDateTime)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(RECEIVEDATETIME$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(RECEIVEDATETIME$8);
            }
            target.setCalendarValue(receiveDateTime);
        }
    }
    
    /**
     * Sets (as xml) the "ReceiveDateTime" element
     */
    public void xsetReceiveDateTime(org.apache.xmlbeans.XmlDateTime receiveDateTime)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlDateTime target = null;
            target = (org.apache.xmlbeans.XmlDateTime)get_store().find_element_user(RECEIVEDATETIME$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlDateTime)get_store().add_element_user(RECEIVEDATETIME$8);
            }
            target.set(receiveDateTime);
        }
    }
    
    /**
     * Unsets the "ReceiveDateTime" element
     */
    public void unsetReceiveDateTime()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(RECEIVEDATETIME$8, 0);
        }
    }
    
    /**
     * Gets the "Sender" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase getSender()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase)get_store().find_element_user(SENDER$10, 0);
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
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase)get_store().find_element_user(SENDER$10, 0);
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
            return get_store().count_elements(SENDER$10) != 0;
        }
    }
    
    /**
     * Sets the "Sender" element
     */
    public void setSender(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase sender)
    {
        generatedSetterHelperImpl(sender, SENDER$10, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "Sender" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase addNewSender()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase)get_store().add_element_user(SENDER$10);
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
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase)get_store().find_element_user(SENDER$10, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase)get_store().add_element_user(SENDER$10);
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
            get_store().remove_element(SENDER$10, 0);
        }
    }
    
    /**
     * Gets the "Signatures" element
     */
    public com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring getSignatures()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring target = null;
            target = (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring)get_store().find_element_user(SIGNATURES$12, 0);
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
            com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring target = null;
            target = (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring)get_store().find_element_user(SIGNATURES$12, 0);
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
            return get_store().count_elements(SIGNATURES$12) != 0;
        }
    }
    
    /**
     * Sets the "Signatures" element
     */
    public void setSignatures(com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring signatures)
    {
        generatedSetterHelperImpl(signatures, SIGNATURES$12, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "Signatures" element
     */
    public com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring addNewSignatures()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring target = null;
            target = (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring)get_store().add_element_user(SIGNATURES$12);
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
            com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring target = null;
            target = (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring)get_store().find_element_user(SIGNATURES$12, 0);
            if (target == null)
            {
                target = (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring)get_store().add_element_user(SIGNATURES$12);
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
            get_store().remove_element(SIGNATURES$12, 0);
        }
    }
}
