/*
 * An XML document type.
 * Localname: DocumentTransportData
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentTransportDataDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxy.impl;
/**
 * A document containing one DocumentTransportData(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy) element.
 *
 * This is a complex type.
 */
public class DocumentTransportDataDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentTransportDataDocument
{
    private static final long serialVersionUID = 1L;
    
    public DocumentTransportDataDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName DOCUMENTTRANSPORTDATA$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "DocumentTransportData");
    
    
    /**
     * Gets the "DocumentTransportData" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentTransportData getDocumentTransportData()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentTransportData target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentTransportData)get_store().find_element_user(DOCUMENTTRANSPORTDATA$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "DocumentTransportData" element
     */
    public boolean isNilDocumentTransportData()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentTransportData target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentTransportData)get_store().find_element_user(DOCUMENTTRANSPORTDATA$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "DocumentTransportData" element
     */
    public void setDocumentTransportData(org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentTransportData documentTransportData)
    {
        generatedSetterHelperImpl(documentTransportData, DOCUMENTTRANSPORTDATA$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "DocumentTransportData" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentTransportData addNewDocumentTransportData()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentTransportData target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentTransportData)get_store().add_element_user(DOCUMENTTRANSPORTDATA$0);
            return target;
        }
    }
    
    /**
     * Nils the "DocumentTransportData" element
     */
    public void setNilDocumentTransportData()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentTransportData target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentTransportData)get_store().find_element_user(DOCUMENTTRANSPORTDATA$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentTransportData)get_store().add_element_user(DOCUMENTTRANSPORTDATA$0);
            }
            target.setNil();
        }
    }
}
