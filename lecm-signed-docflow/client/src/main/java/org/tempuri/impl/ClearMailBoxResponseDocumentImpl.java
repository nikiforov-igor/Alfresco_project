/*
 * An XML document type.
 * Localname: ClearMailBoxResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.ClearMailBoxResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one ClearMailBoxResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class ClearMailBoxResponseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.ClearMailBoxResponseDocument
{
    private static final long serialVersionUID = 1L;
    
    public ClearMailBoxResponseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName CLEARMAILBOXRESPONSE$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "ClearMailBoxResponse");
    
    
    /**
     * Gets the "ClearMailBoxResponse" element
     */
    public org.tempuri.ClearMailBoxResponseDocument.ClearMailBoxResponse getClearMailBoxResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.ClearMailBoxResponseDocument.ClearMailBoxResponse target = null;
            target = (org.tempuri.ClearMailBoxResponseDocument.ClearMailBoxResponse)get_store().find_element_user(CLEARMAILBOXRESPONSE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "ClearMailBoxResponse" element
     */
    public void setClearMailBoxResponse(org.tempuri.ClearMailBoxResponseDocument.ClearMailBoxResponse clearMailBoxResponse)
    {
        generatedSetterHelperImpl(clearMailBoxResponse, CLEARMAILBOXRESPONSE$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "ClearMailBoxResponse" element
     */
    public org.tempuri.ClearMailBoxResponseDocument.ClearMailBoxResponse addNewClearMailBoxResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.ClearMailBoxResponseDocument.ClearMailBoxResponse target = null;
            target = (org.tempuri.ClearMailBoxResponseDocument.ClearMailBoxResponse)get_store().add_element_user(CLEARMAILBOXRESPONSE$0);
            return target;
        }
    }
    /**
     * An XML ClearMailBoxResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class ClearMailBoxResponseImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.ClearMailBoxResponseDocument.ClearMailBoxResponse
    {
        private static final long serialVersionUID = 1L;
        
        public ClearMailBoxResponseImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName CLEARMAILBOXRESULT$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "ClearMailBoxResult");
        
        
        /**
         * Gets the "ClearMailBoxResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getClearMailBoxResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(CLEARMAILBOXRESULT$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "ClearMailBoxResult" element
         */
        public boolean isNilClearMailBoxResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(CLEARMAILBOXRESULT$0, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "ClearMailBoxResult" element
         */
        public boolean isSetClearMailBoxResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(CLEARMAILBOXRESULT$0) != 0;
            }
        }
        
        /**
         * Sets the "ClearMailBoxResult" element
         */
        public void setClearMailBoxResult(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse clearMailBoxResult)
        {
            generatedSetterHelperImpl(clearMailBoxResult, CLEARMAILBOXRESULT$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "ClearMailBoxResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewClearMailBoxResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(CLEARMAILBOXRESULT$0);
                return target;
            }
        }
        
        /**
         * Nils the "ClearMailBoxResult" element
         */
        public void setNilClearMailBoxResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(CLEARMAILBOXRESULT$0, 0);
                if (target == null)
                {
                    target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(CLEARMAILBOXRESULT$0);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "ClearMailBoxResult" element
         */
        public void unsetClearMailBoxResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(CLEARMAILBOXRESULT$0, 0);
            }
        }
    }
}
