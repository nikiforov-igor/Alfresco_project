/*
 * An XML document type.
 * Localname: DocumentContent
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentContentDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxy.impl;
/**
 * A document containing one DocumentContent(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy) element.
 *
 * This is a complex type.
 */
public class DocumentContentDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentContentDocument
{
    private static final long serialVersionUID = 1L;
    
    public DocumentContentDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName DOCUMENTCONTENT$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "DocumentContent");
    
    
    /**
     * Gets the "DocumentContent" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentContent getDocumentContent()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentContent target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentContent)get_store().find_element_user(DOCUMENTCONTENT$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "DocumentContent" element
     */
    public boolean isNilDocumentContent()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentContent target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentContent)get_store().find_element_user(DOCUMENTCONTENT$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "DocumentContent" element
     */
    public void setDocumentContent(org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentContent documentContent)
    {
        generatedSetterHelperImpl(documentContent, DOCUMENTCONTENT$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "DocumentContent" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentContent addNewDocumentContent()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentContent target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentContent)get_store().add_element_user(DOCUMENTCONTENT$0);
            return target;
        }
    }
    
    /**
     * Nils the "DocumentContent" element
     */
    public void setNilDocumentContent()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentContent target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentContent)get_store().find_element_user(DOCUMENTCONTENT$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentContent)get_store().add_element_user(DOCUMENTCONTENT$0);
            }
            target.setNil();
        }
    }
}
