/*
 * An XML document type.
 * Localname: ArrayOfOperatorInfo
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfOperatorInfoDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxy.impl;
/**
 * A document containing one ArrayOfOperatorInfo(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy) element.
 *
 * This is a complex type.
 */
public class ArrayOfOperatorInfoDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfOperatorInfoDocument
{
    private static final long serialVersionUID = 1L;
    
    public ArrayOfOperatorInfoDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName ARRAYOFOPERATORINFO$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "ArrayOfOperatorInfo");
    
    
    /**
     * Gets the "ArrayOfOperatorInfo" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfOperatorInfo getArrayOfOperatorInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfOperatorInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfOperatorInfo)get_store().find_element_user(ARRAYOFOPERATORINFO$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "ArrayOfOperatorInfo" element
     */
    public boolean isNilArrayOfOperatorInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfOperatorInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfOperatorInfo)get_store().find_element_user(ARRAYOFOPERATORINFO$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "ArrayOfOperatorInfo" element
     */
    public void setArrayOfOperatorInfo(org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfOperatorInfo arrayOfOperatorInfo)
    {
        generatedSetterHelperImpl(arrayOfOperatorInfo, ARRAYOFOPERATORINFO$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "ArrayOfOperatorInfo" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfOperatorInfo addNewArrayOfOperatorInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfOperatorInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfOperatorInfo)get_store().add_element_user(ARRAYOFOPERATORINFO$0);
            return target;
        }
    }
    
    /**
     * Nils the "ArrayOfOperatorInfo" element
     */
    public void setNilArrayOfOperatorInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfOperatorInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfOperatorInfo)get_store().find_element_user(ARRAYOFOPERATORINFO$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfOperatorInfo)get_store().add_element_user(ARRAYOFOPERATORINFO$0);
            }
            target.setNil();
        }
    }
}
