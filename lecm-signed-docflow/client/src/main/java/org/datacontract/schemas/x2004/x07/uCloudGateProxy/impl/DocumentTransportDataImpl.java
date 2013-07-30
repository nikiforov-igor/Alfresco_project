/*
 * XML Type:  DocumentTransportData
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentTransportData
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxy.impl;
/**
 * An XML DocumentTransportData(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy).
 *
 * This is a complex type.
 */
public class DocumentTransportDataImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentTransportData
{
    private static final long serialVersionUID = 1L;
    
    public DocumentTransportDataImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName OPERATORINFO$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "OperatorInfo");
    private static final javax.xml.namespace.QName RECEIVERSID$2 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "ReceiversId");
    private static final javax.xml.namespace.QName SENDERID$4 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "SenderId");
    
    
    /**
     * Gets the "OperatorInfo" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo getOperatorInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo)get_store().find_element_user(OPERATORINFO$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "OperatorInfo" element
     */
    public boolean isNilOperatorInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo)get_store().find_element_user(OPERATORINFO$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "OperatorInfo" element
     */
    public boolean isSetOperatorInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(OPERATORINFO$0) != 0;
        }
    }
    
    /**
     * Sets the "OperatorInfo" element
     */
    public void setOperatorInfo(org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo operatorInfo)
    {
        generatedSetterHelperImpl(operatorInfo, OPERATORINFO$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "OperatorInfo" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo addNewOperatorInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo)get_store().add_element_user(OPERATORINFO$0);
            return target;
        }
    }
    
    /**
     * Nils the "OperatorInfo" element
     */
    public void setNilOperatorInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo)get_store().find_element_user(OPERATORINFO$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo)get_store().add_element_user(OPERATORINFO$0);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "OperatorInfo" element
     */
    public void unsetOperatorInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(OPERATORINFO$0, 0);
        }
    }
    
    /**
     * Gets the "ReceiversId" element
     */
    public com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring getReceiversId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring target = null;
            target = (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring)get_store().find_element_user(RECEIVERSID$2, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "ReceiversId" element
     */
    public boolean isNilReceiversId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring target = null;
            target = (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring)get_store().find_element_user(RECEIVERSID$2, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "ReceiversId" element
     */
    public boolean isSetReceiversId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(RECEIVERSID$2) != 0;
        }
    }
    
    /**
     * Sets the "ReceiversId" element
     */
    public void setReceiversId(com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring receiversId)
    {
        generatedSetterHelperImpl(receiversId, RECEIVERSID$2, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "ReceiversId" element
     */
    public com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring addNewReceiversId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring target = null;
            target = (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring)get_store().add_element_user(RECEIVERSID$2);
            return target;
        }
    }
    
    /**
     * Nils the "ReceiversId" element
     */
    public void setNilReceiversId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring target = null;
            target = (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring)get_store().find_element_user(RECEIVERSID$2, 0);
            if (target == null)
            {
                target = (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring)get_store().add_element_user(RECEIVERSID$2);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "ReceiversId" element
     */
    public void unsetReceiversId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(RECEIVERSID$2, 0);
        }
    }
    
    /**
     * Gets the "SenderId" element
     */
    public java.lang.String getSenderId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SENDERID$4, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "SenderId" element
     */
    public org.apache.xmlbeans.XmlString xgetSenderId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SENDERID$4, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "SenderId" element
     */
    public boolean isNilSenderId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SENDERID$4, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "SenderId" element
     */
    public boolean isSetSenderId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(SENDERID$4) != 0;
        }
    }
    
    /**
     * Sets the "SenderId" element
     */
    public void setSenderId(java.lang.String senderId)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SENDERID$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(SENDERID$4);
            }
            target.setStringValue(senderId);
        }
    }
    
    /**
     * Sets (as xml) the "SenderId" element
     */
    public void xsetSenderId(org.apache.xmlbeans.XmlString senderId)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SENDERID$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(SENDERID$4);
            }
            target.set(senderId);
        }
    }
    
    /**
     * Nils the "SenderId" element
     */
    public void setNilSenderId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SENDERID$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(SENDERID$4);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "SenderId" element
     */
    public void unsetSenderId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(SENDERID$4, 0);
        }
    }
}
