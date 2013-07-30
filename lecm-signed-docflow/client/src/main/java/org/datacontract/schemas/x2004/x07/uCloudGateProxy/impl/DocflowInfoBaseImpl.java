/*
 * XML Type:  DocflowInfoBase
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxy.impl;
/**
 * An XML DocflowInfoBase(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy).
 *
 * This is a complex type.
 */
public class DocflowInfoBaseImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase
{
    private static final long serialVersionUID = 1L;
    
    public DocflowInfoBaseImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName DESCRIPTION$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "Description");
    private static final javax.xml.namespace.QName DOCFLOWID$2 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "DocflowId");
    private static final javax.xml.namespace.QName ISINBOUND$4 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "IsInbound");
    private static final javax.xml.namespace.QName ISUNREAD$6 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "IsUnread");
    private static final javax.xml.namespace.QName OPERATORCODE$8 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "OperatorCode");
    private static final javax.xml.namespace.QName RECEIVER$10 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "Receiver");
    private static final javax.xml.namespace.QName SENDER$12 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "Sender");
    private static final javax.xml.namespace.QName TYPE$14 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "Type");
    
    
    /**
     * Gets the "Description" element
     */
    public java.lang.String getDescription()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DESCRIPTION$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Description" element
     */
    public org.apache.xmlbeans.XmlString xgetDescription()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DESCRIPTION$0, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "Description" element
     */
    public boolean isNilDescription()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DESCRIPTION$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Description" element
     */
    public boolean isSetDescription()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(DESCRIPTION$0) != 0;
        }
    }
    
    /**
     * Sets the "Description" element
     */
    public void setDescription(java.lang.String description)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DESCRIPTION$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(DESCRIPTION$0);
            }
            target.setStringValue(description);
        }
    }
    
    /**
     * Sets (as xml) the "Description" element
     */
    public void xsetDescription(org.apache.xmlbeans.XmlString description)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DESCRIPTION$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(DESCRIPTION$0);
            }
            target.set(description);
        }
    }
    
    /**
     * Nils the "Description" element
     */
    public void setNilDescription()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DESCRIPTION$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(DESCRIPTION$0);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Description" element
     */
    public void unsetDescription()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(DESCRIPTION$0, 0);
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
     * Gets the "IsInbound" element
     */
    public boolean getIsInbound()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ISINBOUND$4, 0);
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "IsInbound" element
     */
    public org.apache.xmlbeans.XmlBoolean xgetIsInbound()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(ISINBOUND$4, 0);
            return target;
        }
    }
    
    /**
     * True if has "IsInbound" element
     */
    public boolean isSetIsInbound()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(ISINBOUND$4) != 0;
        }
    }
    
    /**
     * Sets the "IsInbound" element
     */
    public void setIsInbound(boolean isInbound)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ISINBOUND$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ISINBOUND$4);
            }
            target.setBooleanValue(isInbound);
        }
    }
    
    /**
     * Sets (as xml) the "IsInbound" element
     */
    public void xsetIsInbound(org.apache.xmlbeans.XmlBoolean isInbound)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(ISINBOUND$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_element_user(ISINBOUND$4);
            }
            target.set(isInbound);
        }
    }
    
    /**
     * Unsets the "IsInbound" element
     */
    public void unsetIsInbound()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(ISINBOUND$4, 0);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ISUNREAD$6, 0);
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
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(ISUNREAD$6, 0);
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
            return get_store().count_elements(ISUNREAD$6) != 0;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ISUNREAD$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ISUNREAD$6);
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
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(ISUNREAD$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_element_user(ISUNREAD$6);
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
            get_store().remove_element(ISUNREAD$6, 0);
        }
    }
    
    /**
     * Gets the "OperatorCode" element
     */
    public java.lang.String getOperatorCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(OPERATORCODE$8, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "OperatorCode" element
     */
    public org.apache.xmlbeans.XmlString xgetOperatorCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORCODE$8, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "OperatorCode" element
     */
    public boolean isNilOperatorCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORCODE$8, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "OperatorCode" element
     */
    public boolean isSetOperatorCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(OPERATORCODE$8) != 0;
        }
    }
    
    /**
     * Sets the "OperatorCode" element
     */
    public void setOperatorCode(java.lang.String operatorCode)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(OPERATORCODE$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(OPERATORCODE$8);
            }
            target.setStringValue(operatorCode);
        }
    }
    
    /**
     * Sets (as xml) the "OperatorCode" element
     */
    public void xsetOperatorCode(org.apache.xmlbeans.XmlString operatorCode)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORCODE$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(OPERATORCODE$8);
            }
            target.set(operatorCode);
        }
    }
    
    /**
     * Nils the "OperatorCode" element
     */
    public void setNilOperatorCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORCODE$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(OPERATORCODE$8);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "OperatorCode" element
     */
    public void unsetOperatorCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(OPERATORCODE$8, 0);
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
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo)get_store().find_element_user(RECEIVER$10, 0);
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
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo)get_store().find_element_user(RECEIVER$10, 0);
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
            return get_store().count_elements(RECEIVER$10) != 0;
        }
    }
    
    /**
     * Sets the "Receiver" element
     */
    public void setReceiver(org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo receiver)
    {
        generatedSetterHelperImpl(receiver, RECEIVER$10, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
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
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo)get_store().add_element_user(RECEIVER$10);
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
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo)get_store().find_element_user(RECEIVER$10, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo)get_store().add_element_user(RECEIVER$10);
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
            get_store().remove_element(RECEIVER$10, 0);
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
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo)get_store().find_element_user(SENDER$12, 0);
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
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo)get_store().find_element_user(SENDER$12, 0);
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
            return get_store().count_elements(SENDER$12) != 0;
        }
    }
    
    /**
     * Sets the "Sender" element
     */
    public void setSender(org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo sender)
    {
        generatedSetterHelperImpl(sender, SENDER$12, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
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
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo)get_store().add_element_user(SENDER$12);
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
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo)get_store().find_element_user(SENDER$12, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo)get_store().add_element_user(SENDER$12);
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
            get_store().remove_element(SENDER$12, 0);
        }
    }
    
    /**
     * Gets the "Type" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocflowType.Enum getType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(TYPE$14, 0);
            if (target == null)
            {
                return null;
            }
            return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocflowType.Enum)target.getEnumValue();
        }
    }
    
    /**
     * Gets (as xml) the "Type" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocflowType xgetType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocflowType target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocflowType)get_store().find_element_user(TYPE$14, 0);
            return target;
        }
    }
    
    /**
     * True if has "Type" element
     */
    public boolean isSetType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(TYPE$14) != 0;
        }
    }
    
    /**
     * Sets the "Type" element
     */
    public void setType(org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocflowType.Enum type)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(TYPE$14, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(TYPE$14);
            }
            target.setEnumValue(type);
        }
    }
    
    /**
     * Sets (as xml) the "Type" element
     */
    public void xsetType(org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocflowType type)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocflowType target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocflowType)get_store().find_element_user(TYPE$14, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocflowType)get_store().add_element_user(TYPE$14);
            }
            target.set(type);
        }
    }
    
    /**
     * Unsets the "Type" element
     */
    public void unsetType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(TYPE$14, 0);
        }
    }
}
