/*
 * An XML document type.
 * Localname: GetMutualOperatorsResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.GetMutualOperatorsResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one GetMutualOperatorsResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class GetMutualOperatorsResponseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GetMutualOperatorsResponseDocument
{
    private static final long serialVersionUID = 1L;
    
    public GetMutualOperatorsResponseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName GETMUTUALOPERATORSRESPONSE$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "GetMutualOperatorsResponse");
    
    
    /**
     * Gets the "GetMutualOperatorsResponse" element
     */
    public org.tempuri.GetMutualOperatorsResponseDocument.GetMutualOperatorsResponse getGetMutualOperatorsResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GetMutualOperatorsResponseDocument.GetMutualOperatorsResponse target = null;
            target = (org.tempuri.GetMutualOperatorsResponseDocument.GetMutualOperatorsResponse)get_store().find_element_user(GETMUTUALOPERATORSRESPONSE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "GetMutualOperatorsResponse" element
     */
    public void setGetMutualOperatorsResponse(org.tempuri.GetMutualOperatorsResponseDocument.GetMutualOperatorsResponse getMutualOperatorsResponse)
    {
        generatedSetterHelperImpl(getMutualOperatorsResponse, GETMUTUALOPERATORSRESPONSE$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "GetMutualOperatorsResponse" element
     */
    public org.tempuri.GetMutualOperatorsResponseDocument.GetMutualOperatorsResponse addNewGetMutualOperatorsResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GetMutualOperatorsResponseDocument.GetMutualOperatorsResponse target = null;
            target = (org.tempuri.GetMutualOperatorsResponseDocument.GetMutualOperatorsResponse)get_store().add_element_user(GETMUTUALOPERATORSRESPONSE$0);
            return target;
        }
    }
    /**
     * An XML GetMutualOperatorsResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class GetMutualOperatorsResponseImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GetMutualOperatorsResponseDocument.GetMutualOperatorsResponse
    {
        private static final long serialVersionUID = 1L;
        
        public GetMutualOperatorsResponseImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName GETMUTUALOPERATORSRESULT$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "GetMutualOperatorsResult");
        private static final javax.xml.namespace.QName OPERATORCODES$2 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "operatorCodes");
        
        
        /**
         * Gets the "GetMutualOperatorsResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getGetMutualOperatorsResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(GETMUTUALOPERATORSRESULT$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "GetMutualOperatorsResult" element
         */
        public boolean isNilGetMutualOperatorsResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(GETMUTUALOPERATORSRESULT$0, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "GetMutualOperatorsResult" element
         */
        public boolean isSetGetMutualOperatorsResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(GETMUTUALOPERATORSRESULT$0) != 0;
            }
        }
        
        /**
         * Sets the "GetMutualOperatorsResult" element
         */
        public void setGetMutualOperatorsResult(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getMutualOperatorsResult)
        {
            generatedSetterHelperImpl(getMutualOperatorsResult, GETMUTUALOPERATORSRESULT$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "GetMutualOperatorsResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewGetMutualOperatorsResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(GETMUTUALOPERATORSRESULT$0);
                return target;
            }
        }
        
        /**
         * Nils the "GetMutualOperatorsResult" element
         */
        public void setNilGetMutualOperatorsResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(GETMUTUALOPERATORSRESULT$0, 0);
                if (target == null)
                {
                    target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(GETMUTUALOPERATORSRESULT$0);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "GetMutualOperatorsResult" element
         */
        public void unsetGetMutualOperatorsResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(GETMUTUALOPERATORSRESULT$0, 0);
            }
        }
        
        /**
         * Gets the "operatorCodes" element
         */
        public com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring getOperatorCodes()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring target = null;
                target = (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring)get_store().find_element_user(OPERATORCODES$2, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "operatorCodes" element
         */
        public boolean isNilOperatorCodes()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring target = null;
                target = (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring)get_store().find_element_user(OPERATORCODES$2, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "operatorCodes" element
         */
        public boolean isSetOperatorCodes()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(OPERATORCODES$2) != 0;
            }
        }
        
        /**
         * Sets the "operatorCodes" element
         */
        public void setOperatorCodes(com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring operatorCodes)
        {
            generatedSetterHelperImpl(operatorCodes, OPERATORCODES$2, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "operatorCodes" element
         */
        public com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring addNewOperatorCodes()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring target = null;
                target = (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring)get_store().add_element_user(OPERATORCODES$2);
                return target;
            }
        }
        
        /**
         * Nils the "operatorCodes" element
         */
        public void setNilOperatorCodes()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring target = null;
                target = (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring)get_store().find_element_user(OPERATORCODES$2, 0);
                if (target == null)
                {
                    target = (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring)get_store().add_element_user(OPERATORCODES$2);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "operatorCodes" element
         */
        public void unsetOperatorCodes()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(OPERATORCODES$2, 0);
            }
        }
    }
}
