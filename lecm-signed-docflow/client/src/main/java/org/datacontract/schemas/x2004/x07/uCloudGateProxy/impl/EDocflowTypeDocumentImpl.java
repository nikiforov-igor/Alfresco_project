/*
 * An XML document type.
 * Localname: EDocflowType
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocflowTypeDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxy.impl;
/**
 * A document containing one EDocflowType(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy) element.
 *
 * This is a complex type.
 */
public class EDocflowTypeDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocflowTypeDocument
{
    private static final long serialVersionUID = 1L;
    
    public EDocflowTypeDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName EDOCFLOWTYPE$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "EDocflowType");
    
    
    /**
     * Gets the "EDocflowType" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocflowType.Enum getEDocflowType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(EDOCFLOWTYPE$0, 0);
            if (target == null)
            {
                return null;
            }
            return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocflowType.Enum)target.getEnumValue();
        }
    }
    
    /**
     * Gets (as xml) the "EDocflowType" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocflowType xgetEDocflowType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocflowType target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocflowType)get_store().find_element_user(EDOCFLOWTYPE$0, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "EDocflowType" element
     */
    public boolean isNilEDocflowType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocflowType target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocflowType)get_store().find_element_user(EDOCFLOWTYPE$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "EDocflowType" element
     */
    public void setEDocflowType(org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocflowType.Enum eDocflowType)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(EDOCFLOWTYPE$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(EDOCFLOWTYPE$0);
            }
            target.setEnumValue(eDocflowType);
        }
    }
    
    /**
     * Sets (as xml) the "EDocflowType" element
     */
    public void xsetEDocflowType(org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocflowType eDocflowType)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocflowType target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocflowType)get_store().find_element_user(EDOCFLOWTYPE$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocflowType)get_store().add_element_user(EDOCFLOWTYPE$0);
            }
            target.set(eDocflowType);
        }
    }
    
    /**
     * Nils the "EDocflowType" element
     */
    public void setNilEDocflowType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocflowType target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocflowType)get_store().find_element_user(EDOCFLOWTYPE$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocflowType)get_store().add_element_user(EDOCFLOWTYPE$0);
            }
            target.setNil();
        }
    }
}
