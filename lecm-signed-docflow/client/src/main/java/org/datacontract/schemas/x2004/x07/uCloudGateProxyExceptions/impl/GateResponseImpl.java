/*
 * XML Type:  GateResponse
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Exceptions
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.impl;
/**
 * An XML GateResponse(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Exceptions).
 *
 * This is a complex type.
 */
public class GateResponseImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse
{
    private static final long serialVersionUID = 1L;
    
    public GateResponseImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName AUTHORIZATIONERRORS$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Exceptions", "AuthorizationErrors");
    private static final javax.xml.namespace.QName MESSAGE$2 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Exceptions", "Message");
    private static final javax.xml.namespace.QName OPERATORMESSAGE$4 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Exceptions", "OperatorMessage");
    private static final javax.xml.namespace.QName RESPONSETYPE$6 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Exceptions", "ResponseType");
    private static final javax.xml.namespace.QName STACKTRACE$8 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Exceptions", "StackTrace");
    
    
    /**
     * Gets the "AuthorizationErrors" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfAuthorizationError getAuthorizationErrors()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfAuthorizationError target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfAuthorizationError)get_store().find_element_user(AUTHORIZATIONERRORS$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "AuthorizationErrors" element
     */
    public boolean isNilAuthorizationErrors()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfAuthorizationError target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfAuthorizationError)get_store().find_element_user(AUTHORIZATIONERRORS$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "AuthorizationErrors" element
     */
    public boolean isSetAuthorizationErrors()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(AUTHORIZATIONERRORS$0) != 0;
        }
    }
    
    /**
     * Sets the "AuthorizationErrors" element
     */
    public void setAuthorizationErrors(org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfAuthorizationError authorizationErrors)
    {
        generatedSetterHelperImpl(authorizationErrors, AUTHORIZATIONERRORS$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "AuthorizationErrors" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfAuthorizationError addNewAuthorizationErrors()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfAuthorizationError target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfAuthorizationError)get_store().add_element_user(AUTHORIZATIONERRORS$0);
            return target;
        }
    }
    
    /**
     * Nils the "AuthorizationErrors" element
     */
    public void setNilAuthorizationErrors()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfAuthorizationError target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfAuthorizationError)get_store().find_element_user(AUTHORIZATIONERRORS$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfAuthorizationError)get_store().add_element_user(AUTHORIZATIONERRORS$0);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "AuthorizationErrors" element
     */
    public void unsetAuthorizationErrors()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(AUTHORIZATIONERRORS$0, 0);
        }
    }
    
    /**
     * Gets the "Message" element
     */
    public java.lang.String getMessage()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(MESSAGE$2, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Message" element
     */
    public org.apache.xmlbeans.XmlString xgetMessage()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(MESSAGE$2, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "Message" element
     */
    public boolean isNilMessage()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(MESSAGE$2, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "Message" element
     */
    public boolean isSetMessage()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(MESSAGE$2) != 0;
        }
    }
    
    /**
     * Sets the "Message" element
     */
    public void setMessage(java.lang.String message)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(MESSAGE$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(MESSAGE$2);
            }
            target.setStringValue(message);
        }
    }
    
    /**
     * Sets (as xml) the "Message" element
     */
    public void xsetMessage(org.apache.xmlbeans.XmlString message)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(MESSAGE$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(MESSAGE$2);
            }
            target.set(message);
        }
    }
    
    /**
     * Nils the "Message" element
     */
    public void setNilMessage()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(MESSAGE$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(MESSAGE$2);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "Message" element
     */
    public void unsetMessage()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(MESSAGE$2, 0);
        }
    }
    
    /**
     * Gets the "OperatorMessage" element
     */
    public java.lang.String getOperatorMessage()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(OPERATORMESSAGE$4, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "OperatorMessage" element
     */
    public org.apache.xmlbeans.XmlString xgetOperatorMessage()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORMESSAGE$4, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "OperatorMessage" element
     */
    public boolean isNilOperatorMessage()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORMESSAGE$4, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "OperatorMessage" element
     */
    public boolean isSetOperatorMessage()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(OPERATORMESSAGE$4) != 0;
        }
    }
    
    /**
     * Sets the "OperatorMessage" element
     */
    public void setOperatorMessage(java.lang.String operatorMessage)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(OPERATORMESSAGE$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(OPERATORMESSAGE$4);
            }
            target.setStringValue(operatorMessage);
        }
    }
    
    /**
     * Sets (as xml) the "OperatorMessage" element
     */
    public void xsetOperatorMessage(org.apache.xmlbeans.XmlString operatorMessage)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORMESSAGE$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(OPERATORMESSAGE$4);
            }
            target.set(operatorMessage);
        }
    }
    
    /**
     * Nils the "OperatorMessage" element
     */
    public void setNilOperatorMessage()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPERATORMESSAGE$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(OPERATORMESSAGE$4);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "OperatorMessage" element
     */
    public void unsetOperatorMessage()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(OPERATORMESSAGE$4, 0);
        }
    }
    
    /**
     * Gets the "ResponseType" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.EResponseType.Enum getResponseType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(RESPONSETYPE$6, 0);
            if (target == null)
            {
                return null;
            }
            return (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.EResponseType.Enum)target.getEnumValue();
        }
    }
    
    /**
     * Gets (as xml) the "ResponseType" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.EResponseType xgetResponseType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.EResponseType target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.EResponseType)get_store().find_element_user(RESPONSETYPE$6, 0);
            return target;
        }
    }
    
    /**
     * True if has "ResponseType" element
     */
    public boolean isSetResponseType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(RESPONSETYPE$6) != 0;
        }
    }
    
    /**
     * Sets the "ResponseType" element
     */
    public void setResponseType(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.EResponseType.Enum responseType)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(RESPONSETYPE$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(RESPONSETYPE$6);
            }
            target.setEnumValue(responseType);
        }
    }
    
    /**
     * Sets (as xml) the "ResponseType" element
     */
    public void xsetResponseType(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.EResponseType responseType)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.EResponseType target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.EResponseType)get_store().find_element_user(RESPONSETYPE$6, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.EResponseType)get_store().add_element_user(RESPONSETYPE$6);
            }
            target.set(responseType);
        }
    }
    
    /**
     * Unsets the "ResponseType" element
     */
    public void unsetResponseType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(RESPONSETYPE$6, 0);
        }
    }
    
    /**
     * Gets the "StackTrace" element
     */
    public java.lang.String getStackTrace()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(STACKTRACE$8, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "StackTrace" element
     */
    public org.apache.xmlbeans.XmlString xgetStackTrace()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(STACKTRACE$8, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "StackTrace" element
     */
    public boolean isNilStackTrace()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(STACKTRACE$8, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * True if has "StackTrace" element
     */
    public boolean isSetStackTrace()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(STACKTRACE$8) != 0;
        }
    }
    
    /**
     * Sets the "StackTrace" element
     */
    public void setStackTrace(java.lang.String stackTrace)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(STACKTRACE$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(STACKTRACE$8);
            }
            target.setStringValue(stackTrace);
        }
    }
    
    /**
     * Sets (as xml) the "StackTrace" element
     */
    public void xsetStackTrace(org.apache.xmlbeans.XmlString stackTrace)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(STACKTRACE$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(STACKTRACE$8);
            }
            target.set(stackTrace);
        }
    }
    
    /**
     * Nils the "StackTrace" element
     */
    public void setNilStackTrace()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(STACKTRACE$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(STACKTRACE$8);
            }
            target.setNil();
        }
    }
    
    /**
     * Unsets the "StackTrace" element
     */
    public void unsetStackTrace()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(STACKTRACE$8, 0);
        }
    }
}
