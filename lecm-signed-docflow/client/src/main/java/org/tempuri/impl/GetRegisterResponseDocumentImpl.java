/*
 * An XML document type.
 * Localname: GetRegisterResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.GetRegisterResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one GetRegisterResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class GetRegisterResponseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GetRegisterResponseDocument
{
    private static final long serialVersionUID = 1L;
    
    public GetRegisterResponseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName GETREGISTERRESPONSE$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "GetRegisterResponse");
    
    
    /**
     * Gets the "GetRegisterResponse" element
     */
    public org.tempuri.GetRegisterResponseDocument.GetRegisterResponse getGetRegisterResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GetRegisterResponseDocument.GetRegisterResponse target = null;
            target = (org.tempuri.GetRegisterResponseDocument.GetRegisterResponse)get_store().find_element_user(GETREGISTERRESPONSE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "GetRegisterResponse" element
     */
    public void setGetRegisterResponse(org.tempuri.GetRegisterResponseDocument.GetRegisterResponse getRegisterResponse)
    {
        generatedSetterHelperImpl(getRegisterResponse, GETREGISTERRESPONSE$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "GetRegisterResponse" element
     */
    public org.tempuri.GetRegisterResponseDocument.GetRegisterResponse addNewGetRegisterResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GetRegisterResponseDocument.GetRegisterResponse target = null;
            target = (org.tempuri.GetRegisterResponseDocument.GetRegisterResponse)get_store().add_element_user(GETREGISTERRESPONSE$0);
            return target;
        }
    }
    /**
     * An XML GetRegisterResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class GetRegisterResponseImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GetRegisterResponseDocument.GetRegisterResponse
    {
        private static final long serialVersionUID = 1L;
        
        public GetRegisterResponseImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName PACKETID$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "packetId");
        private static final javax.xml.namespace.QName OPERATORCODE$2 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "operatorCode");
        
        
        /**
         * Gets the "packetId" element
         */
        public java.lang.String getPacketId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PACKETID$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "packetId" element
         */
        public org.apache.xmlbeans.XmlString xgetPacketId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PACKETID$0, 0);
                return target;
            }
        }
        
        /**
         * Tests for nil "packetId" element
         */
        public boolean isNilPacketId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PACKETID$0, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "packetId" element
         */
        public boolean isSetPacketId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(PACKETID$0) != 0;
            }
        }
        
        /**
         * Sets the "packetId" element
         */
        public void setPacketId(java.lang.String packetId)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PACKETID$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PACKETID$0);
                }
                target.setStringValue(packetId);
            }
        }
        
        /**
         * Sets (as xml) the "packetId" element
         */
        public void xsetPacketId(org.apache.xmlbeans.XmlString packetId)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PACKETID$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PACKETID$0);
                }
                target.set(packetId);
            }
        }
        
        /**
         * Nils the "packetId" element
         */
        public void setNilPacketId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PACKETID$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PACKETID$0);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "packetId" element
         */
        public void unsetPacketId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(PACKETID$0, 0);
            }
        }
        
        /**
         * Gets the "operatorCode" element
         */
        public java.lang.String getOperatorCode()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(OPERATORCODE$2, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "operatorCode" element
         */
        public org.apache.xmlbeans.XmlString xgetOperatorCode()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORCODE$2, 0);
                return target;
            }
        }
        
        /**
         * Tests for nil "operatorCode" element
         */
        public boolean isNilOperatorCode()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORCODE$2, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * True if has "operatorCode" element
         */
        public boolean isSetOperatorCode()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(OPERATORCODE$2) != 0;
            }
        }
        
        /**
         * Sets the "operatorCode" element
         */
        public void setOperatorCode(java.lang.String operatorCode)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(OPERATORCODE$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(OPERATORCODE$2);
                }
                target.setStringValue(operatorCode);
            }
        }
        
        /**
         * Sets (as xml) the "operatorCode" element
         */
        public void xsetOperatorCode(org.apache.xmlbeans.XmlString operatorCode)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORCODE$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(OPERATORCODE$2);
                }
                target.set(operatorCode);
            }
        }
        
        /**
         * Nils the "operatorCode" element
         */
        public void setNilOperatorCode()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORCODE$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(OPERATORCODE$2);
                }
                target.setNil();
            }
        }
        
        /**
         * Unsets the "operatorCode" element
         */
        public void unsetOperatorCode()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(OPERATORCODE$2, 0);
            }
        }
    }
}
