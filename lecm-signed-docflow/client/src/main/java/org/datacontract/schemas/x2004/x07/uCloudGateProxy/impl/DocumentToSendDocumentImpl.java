/*
 * An XML document type.
 * Localname: DocumentToSend
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSendDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxy.impl;
/**
 * A document containing one DocumentToSend(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy) element.
 *
 * This is a complex type.
 */
public class DocumentToSendDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSendDocument
{
    private static final long serialVersionUID = 1L;
    
    public DocumentToSendDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName DOCUMENTTOSEND$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "DocumentToSend");
    
    
    /**
     * Gets the "DocumentToSend" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend getDocumentToSend()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend)get_store().find_element_user(DOCUMENTTOSEND$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "DocumentToSend" element
     */
    public boolean isNilDocumentToSend()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend)get_store().find_element_user(DOCUMENTTOSEND$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "DocumentToSend" element
     */
    public void setDocumentToSend(org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend documentToSend)
    {
        generatedSetterHelperImpl(documentToSend, DOCUMENTTOSEND$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "DocumentToSend" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend addNewDocumentToSend()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend)get_store().add_element_user(DOCUMENTTOSEND$0);
            return target;
        }
    }
    
    /**
     * Nils the "DocumentToSend" element
     */
    public void setNilDocumentToSend()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend)get_store().find_element_user(DOCUMENTTOSEND$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend)get_store().add_element_user(DOCUMENTTOSEND$0);
            }
            target.setNil();
        }
    }
}
