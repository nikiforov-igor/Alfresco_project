/*
 * An XML document type.
 * Localname: ArrayOfRegistrationInfo
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfRegistrationInfoDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxy.impl;
/**
 * A document containing one ArrayOfRegistrationInfo(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy) element.
 *
 * This is a complex type.
 */
public class ArrayOfRegistrationInfoDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfRegistrationInfoDocument
{
    private static final long serialVersionUID = 1L;
    
    public ArrayOfRegistrationInfoDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName ARRAYOFREGISTRATIONINFO$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "ArrayOfRegistrationInfo");
    
    
    /**
     * Gets the "ArrayOfRegistrationInfo" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfRegistrationInfo getArrayOfRegistrationInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfRegistrationInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfRegistrationInfo)get_store().find_element_user(ARRAYOFREGISTRATIONINFO$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "ArrayOfRegistrationInfo" element
     */
    public boolean isNilArrayOfRegistrationInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfRegistrationInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfRegistrationInfo)get_store().find_element_user(ARRAYOFREGISTRATIONINFO$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "ArrayOfRegistrationInfo" element
     */
    public void setArrayOfRegistrationInfo(org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfRegistrationInfo arrayOfRegistrationInfo)
    {
        generatedSetterHelperImpl(arrayOfRegistrationInfo, ARRAYOFREGISTRATIONINFO$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "ArrayOfRegistrationInfo" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfRegistrationInfo addNewArrayOfRegistrationInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfRegistrationInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfRegistrationInfo)get_store().add_element_user(ARRAYOFREGISTRATIONINFO$0);
            return target;
        }
    }
    
    /**
     * Nils the "ArrayOfRegistrationInfo" element
     */
    public void setNilArrayOfRegistrationInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfRegistrationInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfRegistrationInfo)get_store().find_element_user(ARRAYOFREGISTRATIONINFO$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfRegistrationInfo)get_store().add_element_user(ARRAYOFREGISTRATIONINFO$0);
            }
            target.setNil();
        }
    }
}
