/*
 * An XML document type.
 * Localname: GetOperatorsResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.GetOperatorsResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one GetOperatorsResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class GetOperatorsResponseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GetOperatorsResponseDocument
{
    private static final long serialVersionUID = 1L;
    
    public GetOperatorsResponseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName GETOPERATORSRESPONSE$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "GetOperatorsResponse");
    
    
    /**
     * Gets the "GetOperatorsResponse" element
     */
    public org.tempuri.GetOperatorsResponseDocument.GetOperatorsResponse getGetOperatorsResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GetOperatorsResponseDocument.GetOperatorsResponse target = null;
            target = (org.tempuri.GetOperatorsResponseDocument.GetOperatorsResponse)get_store().find_element_user(GETOPERATORSRESPONSE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "GetOperatorsResponse" element
     */
    public void setGetOperatorsResponse(org.tempuri.GetOperatorsResponseDocument.GetOperatorsResponse getOperatorsResponse)
    {
        generatedSetterHelperImpl(getOperatorsResponse, GETOPERATORSRESPONSE$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "GetOperatorsResponse" element
     */
    public org.tempuri.GetOperatorsResponseDocument.GetOperatorsResponse addNewGetOperatorsResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GetOperatorsResponseDocument.GetOperatorsResponse target = null;
            target = (org.tempuri.GetOperatorsResponseDocument.GetOperatorsResponse)get_store().add_element_user(GETOPERATORSRESPONSE$0);
            return target;
        }
    }
    /**
     * An XML GetOperatorsResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class GetOperatorsResponseImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GetOperatorsResponseDocument.GetOperatorsResponse
    {
        private static final long serialVersionUID = 1L;
        
        public GetOperatorsResponseImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName GETOPERATORSRESULT$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "GetOperatorsResult");
        private static final javax.xml.namespace.QName OPERATORINFOS$2 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "operatorInfos");
        
        
        /**
         * Gets the "GetOperatorsResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getGetOperatorsResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(GETOPERATORSRESULT$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "GetOperatorsResult" element
         */
        public boolean isNilGetOperatorsResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(GETOPERATORSRESULT$0, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "GetOperatorsResult" element
         */
        public boolean isSetGetOperatorsResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(GETOPERATORSRESULT$0) != 0;
            }
        }
        
        /**
         * Sets the "GetOperatorsResult" element
         */
        public void setGetOperatorsResult(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getOperatorsResult)
        {
            generatedSetterHelperImpl(getOperatorsResult, GETOPERATORSRESULT$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "GetOperatorsResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewGetOperatorsResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(GETOPERATORSRESULT$0);
                return target;
            }
        }
        
        /**
         * Nils the "GetOperatorsResult" element
         */
        public void setNilGetOperatorsResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(GETOPERATORSRESULT$0, 0);
                if (target == null)
                {
                    target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(GETOPERATORSRESULT$0);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "GetOperatorsResult" element
         */
        public void unsetGetOperatorsResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(GETOPERATORSRESULT$0, 0);
            }
        }
        
        /**
         * Gets the "operatorInfos" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfOperatorInfo getOperatorInfos()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfOperatorInfo target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfOperatorInfo)get_store().find_element_user(OPERATORINFOS$2, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "operatorInfos" element
         */
        public boolean isNilOperatorInfos()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfOperatorInfo target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfOperatorInfo)get_store().find_element_user(OPERATORINFOS$2, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "operatorInfos" element
         */
        public boolean isSetOperatorInfos()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(OPERATORINFOS$2) != 0;
            }
        }
        
        /**
         * Sets the "operatorInfos" element
         */
        public void setOperatorInfos(org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfOperatorInfo operatorInfos)
        {
            generatedSetterHelperImpl(operatorInfos, OPERATORINFOS$2, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "operatorInfos" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfOperatorInfo addNewOperatorInfos()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfOperatorInfo target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfOperatorInfo)get_store().add_element_user(OPERATORINFOS$2);
                return target;
            }
        }
        
        /**
         * Nils the "operatorInfos" element
         */
        public void setNilOperatorInfos()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfOperatorInfo target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfOperatorInfo)get_store().find_element_user(OPERATORINFOS$2, 0);
                if (target == null)
                {
                    target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfOperatorInfo)get_store().add_element_user(OPERATORINFOS$2);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "operatorInfos" element
         */
        public void unsetOperatorInfos()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(OPERATORINFOS$2, 0);
            }
        }
    }
}
