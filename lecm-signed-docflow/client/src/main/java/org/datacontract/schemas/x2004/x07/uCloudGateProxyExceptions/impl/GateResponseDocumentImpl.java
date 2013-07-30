/*
 * An XML document type.
 * Localname: GateResponse
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Exceptions
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.impl;
/**
 * A document containing one GateResponse(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Exceptions) element.
 *
 * This is a complex type.
 */
public class GateResponseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponseDocument
{
    private static final long serialVersionUID = 1L;
    
    public GateResponseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName GATERESPONSE$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Exceptions", "GateResponse");
    
    
    /**
     * Gets the "GateResponse" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getGateResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(GATERESPONSE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "GateResponse" element
     */
    public boolean isNilGateResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(GATERESPONSE$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "GateResponse" element
     */
    public void setGateResponse(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse gateResponse)
    {
        generatedSetterHelperImpl(gateResponse, GATERESPONSE$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "GateResponse" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewGateResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(GATERESPONSE$0);
            return target;
        }
    }
    
    /**
     * Nils the "GateResponse" element
     */
    public void setNilGateResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(GATERESPONSE$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(GATERESPONSE$0);
            }
            target.setNil();
        }
    }
}
