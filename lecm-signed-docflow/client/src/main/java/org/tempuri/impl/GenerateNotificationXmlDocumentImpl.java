/*
 * An XML document type.
 * Localname: GenerateNotificationXml
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.GenerateNotificationXmlDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one GenerateNotificationXml(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class GenerateNotificationXmlDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GenerateNotificationXmlDocument
{
    private static final long serialVersionUID = 1L;
    
    public GenerateNotificationXmlDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName GENERATENOTIFICATIONXML$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "GenerateNotificationXml");
    
    
    /**
     * Gets the "GenerateNotificationXml" element
     */
    public org.tempuri.GenerateNotificationXmlDocument.GenerateNotificationXml getGenerateNotificationXml()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GenerateNotificationXmlDocument.GenerateNotificationXml target = null;
            target = (org.tempuri.GenerateNotificationXmlDocument.GenerateNotificationXml)get_store().find_element_user(GENERATENOTIFICATIONXML$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "GenerateNotificationXml" element
     */
    public void setGenerateNotificationXml(org.tempuri.GenerateNotificationXmlDocument.GenerateNotificationXml generateNotificationXml)
    {
        generatedSetterHelperImpl(generateNotificationXml, GENERATENOTIFICATIONXML$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "GenerateNotificationXml" element
     */
    public org.tempuri.GenerateNotificationXmlDocument.GenerateNotificationXml addNewGenerateNotificationXml()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GenerateNotificationXmlDocument.GenerateNotificationXml target = null;
            target = (org.tempuri.GenerateNotificationXmlDocument.GenerateNotificationXml)get_store().add_element_user(GENERATENOTIFICATIONXML$0);
            return target;
        }
    }
    /**
     * An XML GenerateNotificationXml(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class GenerateNotificationXmlImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GenerateNotificationXmlDocument.GenerateNotificationXml
    {
        private static final long serialVersionUID = 1L;
        
        public GenerateNotificationXmlImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName SIGNER$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "signer");
        private static final javax.xml.namespace.QName SENDERCOMPANY$2 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "senderCompany");
        private static final javax.xml.namespace.QName PARENTDOCUMENT$4 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "parentDocument");
        private static final javax.xml.namespace.QName GENERATEDDOCDATE$6 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "generatedDocDate");
        private static final javax.xml.namespace.QName OPERATORCODE$8 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "operatorCode");
        
        
        /**
         * Gets the "signer" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.Signer getSigner()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.Signer target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.Signer)get_store().find_element_user(SIGNER$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "signer" element
         */
        public boolean isNilSigner()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.Signer target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.Signer)get_store().find_element_user(SIGNER$0, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "signer" element
         */
        public boolean isSetSigner()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(SIGNER$0) != 0;
            }
        }
        
        /**
         * Sets the "signer" element
         */
        public void setSigner(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.Signer signer)
        {
            generatedSetterHelperImpl(signer, SIGNER$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "signer" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.Signer addNewSigner()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.Signer target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.Signer)get_store().add_element_user(SIGNER$0);
                return target;
            }
        }
        
        /**
         * Nils the "signer" element
         */
        public void setNilSigner()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.Signer target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.Signer)get_store().find_element_user(SIGNER$0, 0);
                if (target == null)
                {
                    target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.Signer)get_store().add_element_user(SIGNER$0);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "signer" element
         */
        public void unsetSigner()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(SIGNER$0, 0);
            }
        }
        
        /**
         * Gets the "senderCompany" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase getSenderCompany()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase)get_store().find_element_user(SENDERCOMPANY$2, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "senderCompany" element
         */
        public boolean isNilSenderCompany()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase)get_store().find_element_user(SENDERCOMPANY$2, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "senderCompany" element
         */
        public boolean isSetSenderCompany()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(SENDERCOMPANY$2) != 0;
            }
        }
        
        /**
         * Sets the "senderCompany" element
         */
        public void setSenderCompany(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase senderCompany)
        {
            generatedSetterHelperImpl(senderCompany, SENDERCOMPANY$2, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "senderCompany" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase addNewSenderCompany()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase)get_store().add_element_user(SENDERCOMPANY$2);
                return target;
            }
        }
        
        /**
         * Nils the "senderCompany" element
         */
        public void setNilSenderCompany()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase)get_store().find_element_user(SENDERCOMPANY$2, 0);
                if (target == null)
                {
                    target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase)get_store().add_element_user(SENDERCOMPANY$2);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "senderCompany" element
         */
        public void unsetSenderCompany()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(SENDERCOMPANY$2, 0);
            }
        }
        
        /**
         * Gets the "parentDocument" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo getParentDocument()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo)get_store().find_element_user(PARENTDOCUMENT$4, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "parentDocument" element
         */
        public boolean isNilParentDocument()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo)get_store().find_element_user(PARENTDOCUMENT$4, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "parentDocument" element
         */
        public boolean isSetParentDocument()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(PARENTDOCUMENT$4) != 0;
            }
        }
        
        /**
         * Sets the "parentDocument" element
         */
        public void setParentDocument(org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo parentDocument)
        {
            generatedSetterHelperImpl(parentDocument, PARENTDOCUMENT$4, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "parentDocument" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo addNewParentDocument()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo)get_store().add_element_user(PARENTDOCUMENT$4);
                return target;
            }
        }
        
        /**
         * Nils the "parentDocument" element
         */
        public void setNilParentDocument()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo)get_store().find_element_user(PARENTDOCUMENT$4, 0);
                if (target == null)
                {
                    target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo)get_store().add_element_user(PARENTDOCUMENT$4);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "parentDocument" element
         */
        public void unsetParentDocument()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(PARENTDOCUMENT$4, 0);
            }
        }
        
        /**
         * Gets the "generatedDocDate" element
         */
        public java.util.Calendar getGeneratedDocDate()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(GENERATEDDOCDATE$6, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getCalendarValue();
            }
        }
        
        /**
         * Gets (as xml) the "generatedDocDate" element
         */
        public org.apache.xmlbeans.XmlDateTime xgetGeneratedDocDate()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlDateTime target = null;
                target = (org.apache.xmlbeans.XmlDateTime)get_store().find_element_user(GENERATEDDOCDATE$6, 0);
                return target;
            }
        }
        
        /**
         * Tests for nil "generatedDocDate" element
         */
        public boolean isNilGeneratedDocDate()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlDateTime target = null;
                target = (org.apache.xmlbeans.XmlDateTime)get_store().find_element_user(GENERATEDDOCDATE$6, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "generatedDocDate" element
         */
        public boolean isSetGeneratedDocDate()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(GENERATEDDOCDATE$6) != 0;
            }
        }
        
        /**
         * Sets the "generatedDocDate" element
         */
        public void setGeneratedDocDate(java.util.Calendar generatedDocDate)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(GENERATEDDOCDATE$6, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(GENERATEDDOCDATE$6);
                }
                target.setCalendarValue(generatedDocDate);
            }
        }
        
        /**
         * Sets (as xml) the "generatedDocDate" element
         */
        public void xsetGeneratedDocDate(org.apache.xmlbeans.XmlDateTime generatedDocDate)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlDateTime target = null;
                target = (org.apache.xmlbeans.XmlDateTime)get_store().find_element_user(GENERATEDDOCDATE$6, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlDateTime)get_store().add_element_user(GENERATEDDOCDATE$6);
                }
                target.set(generatedDocDate);
            }
        }
        
        /**
         * Nils the "generatedDocDate" element
         */
        public void setNilGeneratedDocDate()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlDateTime target = null;
                target = (org.apache.xmlbeans.XmlDateTime)get_store().find_element_user(GENERATEDDOCDATE$6, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlDateTime)get_store().add_element_user(GENERATEDDOCDATE$6);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "generatedDocDate" element
         */
        public void unsetGeneratedDocDate()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(GENERATEDDOCDATE$6, 0);
            }
        }
        
        /**
         * Gets the "operatorCode" element
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
         * Gets (as xml) the "operatorCode" element
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
         * Tests for nil "operatorCode" element
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
         * True if has "operatorCode" element
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
         * Sets the "operatorCode" element
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
         * Sets (as xml) the "operatorCode" element
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
         * Nils the "operatorCode" element
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
         * Unsets the "operatorCode" element
         */
        public void unsetOperatorCode()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(OPERATORCODE$8, 0);
            }
        }
    }
}
