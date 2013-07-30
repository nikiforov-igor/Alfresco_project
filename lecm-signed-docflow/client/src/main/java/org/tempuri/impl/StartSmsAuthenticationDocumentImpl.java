/*
 * An XML document type.
 * Localname: StartSmsAuthentication
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.StartSmsAuthenticationDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one StartSmsAuthentication(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class StartSmsAuthenticationDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.StartSmsAuthenticationDocument
{
    private static final long serialVersionUID = 1L;
    
    public StartSmsAuthenticationDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName STARTSMSAUTHENTICATION$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "StartSmsAuthentication");
    
    
    /**
     * Gets the "StartSmsAuthentication" element
     */
    public org.tempuri.StartSmsAuthenticationDocument.StartSmsAuthentication getStartSmsAuthentication()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.StartSmsAuthenticationDocument.StartSmsAuthentication target = null;
            target = (org.tempuri.StartSmsAuthenticationDocument.StartSmsAuthentication)get_store().find_element_user(STARTSMSAUTHENTICATION$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "StartSmsAuthentication" element
     */
    public void setStartSmsAuthentication(org.tempuri.StartSmsAuthenticationDocument.StartSmsAuthentication startSmsAuthentication)
    {
        generatedSetterHelperImpl(startSmsAuthentication, STARTSMSAUTHENTICATION$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "StartSmsAuthentication" element
     */
    public org.tempuri.StartSmsAuthenticationDocument.StartSmsAuthentication addNewStartSmsAuthentication()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.StartSmsAuthenticationDocument.StartSmsAuthentication target = null;
            target = (org.tempuri.StartSmsAuthenticationDocument.StartSmsAuthentication)get_store().add_element_user(STARTSMSAUTHENTICATION$0);
            return target;
        }
    }
    /**
     * An XML StartSmsAuthentication(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class StartSmsAuthenticationImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.StartSmsAuthenticationDocument.StartSmsAuthentication
    {
        private static final long serialVersionUID = 1L;
        
        public StartSmsAuthenticationImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName OPERATORCODE$0 = 
            new javax.xml.namespace.QName("http://tempuri.org/", "operatorCode");
        
        
        /**
         * Gets the "operatorCode" element
         */
        public java.lang.String getOperatorCode()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(OPERATORCODE$0, 0);
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
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORCODE$0, 0);
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
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORCODE$0, 0);
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
                return get_store().count_elements(OPERATORCODE$0) != 0;
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
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(OPERATORCODE$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(OPERATORCODE$0);
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
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORCODE$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(OPERATORCODE$0);
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
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORCODE$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(OPERATORCODE$0);
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
                get_store().remove_element(OPERATORCODE$0, 0);
            }
        }
    }
}
