/*
 * XML Type:  CorrectionRequest
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.impl;
/**
 * An XML CorrectionRequest(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice).
 *
 * This is a complex type.
 */
public class CorrectionRequestImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest
{
    private static final long serialVersionUID = 1L;
    
    public CorrectionRequestImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName OPERATORCODE$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "OperatorCode");
    private static final javax.xml.namespace.QName RECEIVEDDOCUMENT$2 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "ReceivedDocument");
    private static final javax.xml.namespace.QName RECEIVER$4 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "Receiver");
    private static final javax.xml.namespace.QName REQUESTMESSAGE$6 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "RequestMessage");
    private static final javax.xml.namespace.QName SENDER$8 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "Sender");
    private static final javax.xml.namespace.QName SIGNATURE$10 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "Signature");
    private static final javax.xml.namespace.QName SIGNER$12 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "Signer");
    
    
    /**
     * Gets the "OperatorCode" element
     */
    public java.lang.String getOperatorCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(OPERATORCODE$0, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORCODE$0, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORCODE$0, 0);
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
            return get_store().count_elements(OPERATORCODE$0) != 0;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(OPERATORCODE$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(OPERATORCODE$0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORCODE$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(OPERATORCODE$0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORCODE$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(OPERATORCODE$0);
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
            get_store().remove_element(OPERATORCODE$0, 0);
        }
    }
    
    /**
     * Gets the "ReceivedDocument" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo getReceivedDocument()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo)get_store().find_element_user(RECEIVEDDOCUMENT$2, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "ReceivedDocument" element
     */
    public boolean isNilReceivedDocument()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo)get_store().find_element_user(RECEIVEDDOCUMENT$2, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "ReceivedDocument" element
     */
    public boolean isSetReceivedDocument()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(RECEIVEDDOCUMENT$2) != 0;
        }
    }
    
    /**
     * Sets the "ReceivedDocument" element
     */
    public void setReceivedDocument(org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo receivedDocument)
    {
        generatedSetterHelperImpl(receivedDocument, RECEIVEDDOCUMENT$2, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "ReceivedDocument" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo addNewReceivedDocument()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo)get_store().add_element_user(RECEIVEDDOCUMENT$2);
            return target;
        }
    }
    
    /**
     * Nils the "ReceivedDocument" element
     */
    public void setNilReceivedDocument()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo)get_store().find_element_user(RECEIVEDDOCUMENT$2, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo)get_store().add_element_user(RECEIVEDDOCUMENT$2);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "ReceivedDocument" element
     */
    public void unsetReceivedDocument()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(RECEIVEDDOCUMENT$2, 0);
        }
    }
    
    /**
     * Gets the "Receiver" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase getReceiver()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase)get_store().find_element_user(RECEIVER$4, 0);
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
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase)get_store().find_element_user(RECEIVER$4, 0);
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
            return get_store().count_elements(RECEIVER$4) != 0;
        }
    }
    
    /**
     * Sets the "Receiver" element
     */
    public void setReceiver(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase receiver)
    {
        generatedSetterHelperImpl(receiver, RECEIVER$4, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "Receiver" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase addNewReceiver()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase)get_store().add_element_user(RECEIVER$4);
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
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase)get_store().find_element_user(RECEIVER$4, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase)get_store().add_element_user(RECEIVER$4);
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
            get_store().remove_element(RECEIVER$4, 0);
        }
    }
    
    /**
     * Gets the "RequestMessage" element
     */
    public java.lang.String getRequestMessage()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(REQUESTMESSAGE$6, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "RequestMessage" element
     */
    public org.apache.xmlbeans.XmlString xgetRequestMessage()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(REQUESTMESSAGE$6, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "RequestMessage" element
     */
    public boolean isNilRequestMessage()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(REQUESTMESSAGE$6, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "RequestMessage" element
     */
    public boolean isSetRequestMessage()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(REQUESTMESSAGE$6) != 0;
        }
    }
    
    /**
     * Sets the "RequestMessage" element
     */
    public void setRequestMessage(java.lang.String requestMessage)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(REQUESTMESSAGE$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(REQUESTMESSAGE$6);
            }
            target.setStringValue(requestMessage);
        }
    }
    
    /**
     * Sets (as xml) the "RequestMessage" element
     */
    public void xsetRequestMessage(org.apache.xmlbeans.XmlString requestMessage)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(REQUESTMESSAGE$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(REQUESTMESSAGE$6);
            }
            target.set(requestMessage);
        }
    }
    
    /**
     * Nils the "RequestMessage" element
     */
    public void setNilRequestMessage()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(REQUESTMESSAGE$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(REQUESTMESSAGE$6);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "RequestMessage" element
     */
    public void unsetRequestMessage()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(REQUESTMESSAGE$6, 0);
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
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase)get_store().find_element_user(SENDER$8, 0);
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
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase)get_store().find_element_user(SENDER$8, 0);
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
            return get_store().count_elements(SENDER$8) != 0;
        }
    }
    
    /**
     * Sets the "Sender" element
     */
    public void setSender(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase sender)
    {
        generatedSetterHelperImpl(sender, SENDER$8, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
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
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase)get_store().add_element_user(SENDER$8);
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
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase)get_store().find_element_user(SENDER$8, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase)get_store().add_element_user(SENDER$8);
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
            get_store().remove_element(SENDER$8, 0);
        }
    }
    
    /**
     * Gets the "Signature" element
     */
    public java.lang.String getSignature()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SIGNATURE$10, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Signature" element
     */
    public org.apache.xmlbeans.XmlString xgetSignature()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SIGNATURE$10, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "Signature" element
     */
    public boolean isNilSignature()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SIGNATURE$10, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Signature" element
     */
    public boolean isSetSignature()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(SIGNATURE$10) != 0;
        }
    }
    
    /**
     * Sets the "Signature" element
     */
    public void setSignature(java.lang.String signature)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SIGNATURE$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(SIGNATURE$10);
            }
            target.setStringValue(signature);
        }
    }
    
    /**
     * Sets (as xml) the "Signature" element
     */
    public void xsetSignature(org.apache.xmlbeans.XmlString signature)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SIGNATURE$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(SIGNATURE$10);
            }
            target.set(signature);
        }
    }
    
    /**
     * Nils the "Signature" element
     */
    public void setNilSignature()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SIGNATURE$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(SIGNATURE$10);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Signature" element
     */
    public void unsetSignature()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(SIGNATURE$10, 0);
        }
    }
    
    /**
     * Gets the "Signer" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.Signer getSigner()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.Signer target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.Signer)get_store().find_element_user(SIGNER$12, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "Signer" element
     */
    public boolean isNilSigner()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.Signer target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.Signer)get_store().find_element_user(SIGNER$12, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Signer" element
     */
    public boolean isSetSigner()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(SIGNER$12) != 0;
        }
    }
    
    /**
     * Sets the "Signer" element
     */
    public void setSigner(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.Signer signer)
    {
        generatedSetterHelperImpl(signer, SIGNER$12, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "Signer" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.Signer addNewSigner()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.Signer target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.Signer)get_store().add_element_user(SIGNER$12);
            return target;
        }
    }
    
    /**
     * Nils the "Signer" element
     */
    public void setNilSigner()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.Signer target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.Signer)get_store().find_element_user(SIGNER$12, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.Signer)get_store().add_element_user(SIGNER$12);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Signer" element
     */
    public void unsetSigner()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(SIGNER$12, 0);
        }
    }
}
