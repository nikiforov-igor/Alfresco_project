/*
 * An XML document type.
 * Localname: ReceivedDocumentInfo
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Docflow
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfoDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.impl;
/**
 * A document containing one ReceivedDocumentInfo(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Docflow) element.
 *
 * This is a complex type.
 */
public class ReceivedDocumentInfoDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfoDocument
{
    private static final long serialVersionUID = 1L;
    
    public ReceivedDocumentInfoDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName RECEIVEDDOCUMENTINFO$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Docflow", "ReceivedDocumentInfo");
    
    
    /**
     * Gets the "ReceivedDocumentInfo" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo getReceivedDocumentInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo)get_store().find_element_user(RECEIVEDDOCUMENTINFO$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "ReceivedDocumentInfo" element
     */
    public boolean isNilReceivedDocumentInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo)get_store().find_element_user(RECEIVEDDOCUMENTINFO$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "ReceivedDocumentInfo" element
     */
    public void setReceivedDocumentInfo(org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo receivedDocumentInfo)
    {
        generatedSetterHelperImpl(receivedDocumentInfo, RECEIVEDDOCUMENTINFO$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "ReceivedDocumentInfo" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo addNewReceivedDocumentInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo)get_store().add_element_user(RECEIVEDDOCUMENTINFO$0);
            return target;
        }
    }
    
    /**
     * Nils the "ReceivedDocumentInfo" element
     */
    public void setNilReceivedDocumentInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo)get_store().find_element_user(RECEIVEDDOCUMENTINFO$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo)get_store().add_element_user(RECEIVEDDOCUMENTINFO$0);
            }
            target.setNil();
        }
    }
}
