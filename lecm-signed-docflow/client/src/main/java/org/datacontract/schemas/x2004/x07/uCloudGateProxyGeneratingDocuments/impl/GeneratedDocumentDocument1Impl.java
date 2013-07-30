/*
 * An XML document type.
 * Localname: GeneratedDocument
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.GeneratedDocumentDocument1
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.impl;
/**
 * A document containing one GeneratedDocument(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments) element.
 *
 * This is a complex type.
 */
public class GeneratedDocumentDocument1Impl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.GeneratedDocumentDocument1
{
    private static final long serialVersionUID = 1L;
    
    public GeneratedDocumentDocument1Impl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName GENERATEDDOCUMENT$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments", "GeneratedDocument");
    
    
    /**
     * Gets the "GeneratedDocument" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.GeneratedDocument1 getGeneratedDocument()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.GeneratedDocument1 target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.GeneratedDocument1)get_store().find_element_user(GENERATEDDOCUMENT$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "GeneratedDocument" element
     */
    public boolean isNilGeneratedDocument()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.GeneratedDocument1 target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.GeneratedDocument1)get_store().find_element_user(GENERATEDDOCUMENT$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "GeneratedDocument" element
     */
    public void setGeneratedDocument(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.GeneratedDocument1 generatedDocument)
    {
        generatedSetterHelperImpl(generatedDocument, GENERATEDDOCUMENT$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "GeneratedDocument" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.GeneratedDocument1 addNewGeneratedDocument()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.GeneratedDocument1 target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.GeneratedDocument1)get_store().add_element_user(GENERATEDDOCUMENT$0);
            return target;
        }
    }
    
    /**
     * Nils the "GeneratedDocument" element
     */
    public void setNilGeneratedDocument()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.GeneratedDocument1 target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.GeneratedDocument1)get_store().find_element_user(GENERATEDDOCUMENT$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.GeneratedDocument1)get_store().add_element_user(GENERATEDDOCUMENT$0);
            }
            target.setNil();
        }
    }
}
