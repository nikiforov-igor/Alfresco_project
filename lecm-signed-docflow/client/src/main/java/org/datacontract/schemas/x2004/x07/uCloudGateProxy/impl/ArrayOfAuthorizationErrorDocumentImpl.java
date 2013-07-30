/*
 * An XML document type.
 * Localname: ArrayOfAuthorizationError
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfAuthorizationErrorDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxy.impl;
/**
 * A document containing one ArrayOfAuthorizationError(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy) element.
 *
 * This is a complex type.
 */
public class ArrayOfAuthorizationErrorDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfAuthorizationErrorDocument
{
    private static final long serialVersionUID = 1L;
    
    public ArrayOfAuthorizationErrorDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName ARRAYOFAUTHORIZATIONERROR$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "ArrayOfAuthorizationError");
    
    
    /**
     * Gets the "ArrayOfAuthorizationError" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfAuthorizationError getArrayOfAuthorizationError()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfAuthorizationError target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfAuthorizationError)get_store().find_element_user(ARRAYOFAUTHORIZATIONERROR$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "ArrayOfAuthorizationError" element
     */
    public boolean isNilArrayOfAuthorizationError()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfAuthorizationError target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfAuthorizationError)get_store().find_element_user(ARRAYOFAUTHORIZATIONERROR$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "ArrayOfAuthorizationError" element
     */
    public void setArrayOfAuthorizationError(org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfAuthorizationError arrayOfAuthorizationError)
    {
        generatedSetterHelperImpl(arrayOfAuthorizationError, ARRAYOFAUTHORIZATIONERROR$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "ArrayOfAuthorizationError" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfAuthorizationError addNewArrayOfAuthorizationError()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfAuthorizationError target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfAuthorizationError)get_store().add_element_user(ARRAYOFAUTHORIZATIONERROR$0);
            return target;
        }
    }
    
    /**
     * Nils the "ArrayOfAuthorizationError" element
     */
    public void setNilArrayOfAuthorizationError()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfAuthorizationError target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfAuthorizationError)get_store().find_element_user(ARRAYOFAUTHORIZATIONERROR$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfAuthorizationError)get_store().add_element_user(ARRAYOFAUTHORIZATIONERROR$0);
            }
            target.setNil();
        }
    }
}
