/*
 * An XML document type.
 * Localname: GetServiceVersionResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.GetServiceVersionResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one GetServiceVersionResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class GetServiceVersionResponseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GetServiceVersionResponseDocument
{
    private static final long serialVersionUID = 1L;
    
    public GetServiceVersionResponseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName GETSERVICEVERSIONRESPONSE$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "GetServiceVersionResponse");
    
    
    /**
     * Gets the "GetServiceVersionResponse" element
     */
    public org.tempuri.GetServiceVersionResponseDocument.GetServiceVersionResponse getGetServiceVersionResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GetServiceVersionResponseDocument.GetServiceVersionResponse target = null;
            target = (org.tempuri.GetServiceVersionResponseDocument.GetServiceVersionResponse)get_store().find_element_user(GETSERVICEVERSIONRESPONSE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "GetServiceVersionResponse" element
     */
    public void setGetServiceVersionResponse(org.tempuri.GetServiceVersionResponseDocument.GetServiceVersionResponse getServiceVersionResponse)
    {
        generatedSetterHelperImpl(getServiceVersionResponse, GETSERVICEVERSIONRESPONSE$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "GetServiceVersionResponse" element
     */
    public org.tempuri.GetServiceVersionResponseDocument.GetServiceVersionResponse addNewGetServiceVersionResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GetServiceVersionResponseDocument.GetServiceVersionResponse target = null;
            target = (org.tempuri.GetServiceVersionResponseDocument.GetServiceVersionResponse)get_store().add_element_user(GETSERVICEVERSIONRESPONSE$0);
            return target;
        }
    }
    /**
     * An XML GetServiceVersionResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class GetServiceVersionResponseImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GetServiceVersionResponseDocument.GetServiceVersionResponse
    {
        private static final long serialVersionUID = 1L;
        
        public GetServiceVersionResponseImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName GETSERVICEVERSIONRESULT$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "GetServiceVersionResult");
        private static final javax.xml.namespace.QName VERSION$2 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "version");
        
        
        /**
         * Gets the "GetServiceVersionResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getGetServiceVersionResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(GETSERVICEVERSIONRESULT$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "GetServiceVersionResult" element
         */
        public boolean isNilGetServiceVersionResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(GETSERVICEVERSIONRESULT$0, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "GetServiceVersionResult" element
         */
        public boolean isSetGetServiceVersionResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(GETSERVICEVERSIONRESULT$0) != 0;
            }
        }
        
        /**
         * Sets the "GetServiceVersionResult" element
         */
        public void setGetServiceVersionResult(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getServiceVersionResult)
        {
            generatedSetterHelperImpl(getServiceVersionResult, GETSERVICEVERSIONRESULT$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "GetServiceVersionResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewGetServiceVersionResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(GETSERVICEVERSIONRESULT$0);
                return target;
            }
        }
        
        /**
         * Nils the "GetServiceVersionResult" element
         */
        public void setNilGetServiceVersionResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(GETSERVICEVERSIONRESULT$0, 0);
                if (target == null)
                {
                    target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(GETSERVICEVERSIONRESULT$0);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "GetServiceVersionResult" element
         */
        public void unsetGetServiceVersionResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(GETSERVICEVERSIONRESULT$0, 0);
            }
        }
        
        /**
         * Gets the "version" element
         */
        public java.lang.String getVersion()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(VERSION$2, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "version" element
         */
        public org.apache.xmlbeans.XmlString xgetVersion()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(VERSION$2, 0);
                return target;
            }
        }
        
        /**
         * Tests for nil "version" element
         */
        public boolean isNilVersion()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(VERSION$2, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "version" element
         */
        public boolean isSetVersion()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(VERSION$2) != 0;
            }
        }
        
        /**
         * Sets the "version" element
         */
        public void setVersion(java.lang.String version)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(VERSION$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(VERSION$2);
                }
                target.setStringValue(version);
            }
        }
        
        /**
         * Sets (as xml) the "version" element
         */
        public void xsetVersion(org.apache.xmlbeans.XmlString version)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(VERSION$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(VERSION$2);
                }
                target.set(version);
            }
        }
        
        /**
         * Nils the "version" element
         */
        public void setNilVersion()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(VERSION$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(VERSION$2);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "version" element
         */
        public void unsetVersion()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(VERSION$2, 0);
            }
        }
    }
}
