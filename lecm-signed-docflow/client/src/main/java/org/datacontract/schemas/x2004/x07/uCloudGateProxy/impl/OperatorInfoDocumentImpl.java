/*
 * An XML document type.
 * Localname: OperatorInfo
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfoDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxy.impl;
/**
 * A document containing one OperatorInfo(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy) element.
 *
 * This is a complex type.
 */
public class OperatorInfoDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfoDocument
{
    private static final long serialVersionUID = 1L;
    
    public OperatorInfoDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName OPERATORINFO$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "OperatorInfo");
    
    
    /**
     * Gets the "OperatorInfo" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo getOperatorInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo)get_store().find_element_user(OPERATORINFO$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "OperatorInfo" element
     */
    public boolean isNilOperatorInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo)get_store().find_element_user(OPERATORINFO$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "OperatorInfo" element
     */
    public void setOperatorInfo(org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo operatorInfo)
    {
        generatedSetterHelperImpl(operatorInfo, OPERATORINFO$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "OperatorInfo" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo addNewOperatorInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo)get_store().add_element_user(OPERATORINFO$0);
            return target;
        }
    }
    
    /**
     * Nils the "OperatorInfo" element
     */
    public void setNilOperatorInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo)get_store().find_element_user(OPERATORINFO$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo)get_store().add_element_user(OPERATORINFO$0);
            }
            target.setNil();
        }
    }
}
