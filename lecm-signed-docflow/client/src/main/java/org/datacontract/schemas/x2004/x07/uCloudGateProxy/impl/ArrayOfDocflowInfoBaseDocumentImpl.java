/*
 * An XML document type.
 * Localname: ArrayOfDocflowInfoBase
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocflowInfoBaseDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxy.impl;
/**
 * A document containing one ArrayOfDocflowInfoBase(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy) element.
 *
 * This is a complex type.
 */
public class ArrayOfDocflowInfoBaseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocflowInfoBaseDocument
{
    private static final long serialVersionUID = 1L;
    
    public ArrayOfDocflowInfoBaseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName ARRAYOFDOCFLOWINFOBASE$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "ArrayOfDocflowInfoBase");
    
    
    /**
     * Gets the "ArrayOfDocflowInfoBase" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocflowInfoBase getArrayOfDocflowInfoBase()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocflowInfoBase target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocflowInfoBase)get_store().find_element_user(ARRAYOFDOCFLOWINFOBASE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "ArrayOfDocflowInfoBase" element
     */
    public boolean isNilArrayOfDocflowInfoBase()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocflowInfoBase target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocflowInfoBase)get_store().find_element_user(ARRAYOFDOCFLOWINFOBASE$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "ArrayOfDocflowInfoBase" element
     */
    public void setArrayOfDocflowInfoBase(org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocflowInfoBase arrayOfDocflowInfoBase)
    {
        generatedSetterHelperImpl(arrayOfDocflowInfoBase, ARRAYOFDOCFLOWINFOBASE$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "ArrayOfDocflowInfoBase" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocflowInfoBase addNewArrayOfDocflowInfoBase()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocflowInfoBase target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocflowInfoBase)get_store().add_element_user(ARRAYOFDOCFLOWINFOBASE$0);
            return target;
        }
    }
    
    /**
     * Nils the "ArrayOfDocflowInfoBase" element
     */
    public void setNilArrayOfDocflowInfoBase()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocflowInfoBase target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocflowInfoBase)get_store().find_element_user(ARRAYOFDOCFLOWINFOBASE$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocflowInfoBase)get_store().add_element_user(ARRAYOFDOCFLOWINFOBASE$0);
            }
            target.setNil();
        }
    }
}
