/*
 * An XML document type.
 * Localname: ArrayOfDocumentInfo
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocumentInfoDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxy.impl;
/**
 * A document containing one ArrayOfDocumentInfo(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy) element.
 *
 * This is a complex type.
 */
public class ArrayOfDocumentInfoDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocumentInfoDocument
{
    private static final long serialVersionUID = 1L;
    
    public ArrayOfDocumentInfoDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName ARRAYOFDOCUMENTINFO$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "ArrayOfDocumentInfo");
    
    
    /**
     * Gets the "ArrayOfDocumentInfo" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocumentInfo getArrayOfDocumentInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocumentInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocumentInfo)get_store().find_element_user(ARRAYOFDOCUMENTINFO$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "ArrayOfDocumentInfo" element
     */
    public boolean isNilArrayOfDocumentInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocumentInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocumentInfo)get_store().find_element_user(ARRAYOFDOCUMENTINFO$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "ArrayOfDocumentInfo" element
     */
    public void setArrayOfDocumentInfo(org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocumentInfo arrayOfDocumentInfo)
    {
        generatedSetterHelperImpl(arrayOfDocumentInfo, ARRAYOFDOCUMENTINFO$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "ArrayOfDocumentInfo" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocumentInfo addNewArrayOfDocumentInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocumentInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocumentInfo)get_store().add_element_user(ARRAYOFDOCUMENTINFO$0);
            return target;
        }
    }
    
    /**
     * Nils the "ArrayOfDocumentInfo" element
     */
    public void setNilArrayOfDocumentInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocumentInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocumentInfo)get_store().find_element_user(ARRAYOFDOCUMENTINFO$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocumentInfo)get_store().add_element_user(ARRAYOFDOCUMENTINFO$0);
            }
            target.setNil();
        }
    }
}
