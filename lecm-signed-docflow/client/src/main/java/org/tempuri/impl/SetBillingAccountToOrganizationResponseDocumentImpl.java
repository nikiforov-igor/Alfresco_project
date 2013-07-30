/*
 * An XML document type.
 * Localname: SetBillingAccountToOrganizationResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.SetBillingAccountToOrganizationResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one SetBillingAccountToOrganizationResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class SetBillingAccountToOrganizationResponseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.SetBillingAccountToOrganizationResponseDocument
{
    private static final long serialVersionUID = 1L;
    
    public SetBillingAccountToOrganizationResponseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName SETBILLINGACCOUNTTOORGANIZATIONRESPONSE$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "SetBillingAccountToOrganizationResponse");
    
    
    /**
     * Gets the "SetBillingAccountToOrganizationResponse" element
     */
    public org.tempuri.SetBillingAccountToOrganizationResponseDocument.SetBillingAccountToOrganizationResponse getSetBillingAccountToOrganizationResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.SetBillingAccountToOrganizationResponseDocument.SetBillingAccountToOrganizationResponse target = null;
            target = (org.tempuri.SetBillingAccountToOrganizationResponseDocument.SetBillingAccountToOrganizationResponse)get_store().find_element_user(SETBILLINGACCOUNTTOORGANIZATIONRESPONSE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "SetBillingAccountToOrganizationResponse" element
     */
    public void setSetBillingAccountToOrganizationResponse(org.tempuri.SetBillingAccountToOrganizationResponseDocument.SetBillingAccountToOrganizationResponse setBillingAccountToOrganizationResponse)
    {
        generatedSetterHelperImpl(setBillingAccountToOrganizationResponse, SETBILLINGACCOUNTTOORGANIZATIONRESPONSE$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "SetBillingAccountToOrganizationResponse" element
     */
    public org.tempuri.SetBillingAccountToOrganizationResponseDocument.SetBillingAccountToOrganizationResponse addNewSetBillingAccountToOrganizationResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.SetBillingAccountToOrganizationResponseDocument.SetBillingAccountToOrganizationResponse target = null;
            target = (org.tempuri.SetBillingAccountToOrganizationResponseDocument.SetBillingAccountToOrganizationResponse)get_store().add_element_user(SETBILLINGACCOUNTTOORGANIZATIONRESPONSE$0);
            return target;
        }
    }
    /**
     * An XML SetBillingAccountToOrganizationResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class SetBillingAccountToOrganizationResponseImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.SetBillingAccountToOrganizationResponseDocument.SetBillingAccountToOrganizationResponse
    {
        private static final long serialVersionUID = 1L;
        
        public SetBillingAccountToOrganizationResponseImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName SETBILLINGACCOUNTTOORGANIZATIONRESULT$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "SetBillingAccountToOrganizationResult");
        
        
        /**
         * Gets the "SetBillingAccountToOrganizationResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getSetBillingAccountToOrganizationResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(SETBILLINGACCOUNTTOORGANIZATIONRESULT$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "SetBillingAccountToOrganizationResult" element
         */
        public boolean isNilSetBillingAccountToOrganizationResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(SETBILLINGACCOUNTTOORGANIZATIONRESULT$0, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "SetBillingAccountToOrganizationResult" element
         */
        public boolean isSetSetBillingAccountToOrganizationResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(SETBILLINGACCOUNTTOORGANIZATIONRESULT$0) != 0;
            }
        }
        
        /**
         * Sets the "SetBillingAccountToOrganizationResult" element
         */
        public void setSetBillingAccountToOrganizationResult(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse setBillingAccountToOrganizationResult)
        {
            generatedSetterHelperImpl(setBillingAccountToOrganizationResult, SETBILLINGACCOUNTTOORGANIZATIONRESULT$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "SetBillingAccountToOrganizationResult" element
         */
        public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewSetBillingAccountToOrganizationResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(SETBILLINGACCOUNTTOORGANIZATIONRESULT$0);
                return target;
            }
        }
        
        /**
         * Nils the "SetBillingAccountToOrganizationResult" element
         */
        public void setNilSetBillingAccountToOrganizationResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse target = null;
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().find_element_user(SETBILLINGACCOUNTTOORGANIZATIONRESULT$0, 0);
                if (target == null)
                {
                    target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse)get_store().add_element_user(SETBILLINGACCOUNTTOORGANIZATIONRESULT$0);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "SetBillingAccountToOrganizationResult" element
         */
        public void unsetSetBillingAccountToOrganizationResult()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(SETBILLINGACCOUNTTOORGANIZATIONRESULT$0, 0);
            }
        }
    }
}
