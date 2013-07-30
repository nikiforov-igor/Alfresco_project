/*
 * An XML document type.
 * Localname: EDocumentType
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentTypeDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxy.impl;
/**
 * A document containing one EDocumentType(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy) element.
 *
 * This is a complex type.
 */
public class EDocumentTypeDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentTypeDocument
{
    private static final long serialVersionUID = 1L;
    
    public EDocumentTypeDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName EDOCUMENTTYPE$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "EDocumentType");
    
    
    /**
     * Gets the "EDocumentType" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType.Enum getEDocumentType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(EDOCUMENTTYPE$0, 0);
            if (target == null)
            {
                return null;
            }
            return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType.Enum)target.getEnumValue();
        }
    }
    
    /**
     * Gets (as xml) the "EDocumentType" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType xgetEDocumentType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType)get_store().find_element_user(EDOCUMENTTYPE$0, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "EDocumentType" element
     */
    public boolean isNilEDocumentType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType)get_store().find_element_user(EDOCUMENTTYPE$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "EDocumentType" element
     */
    public void setEDocumentType(org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType.Enum eDocumentType)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(EDOCUMENTTYPE$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(EDOCUMENTTYPE$0);
            }
            target.setEnumValue(eDocumentType);
        }
    }
    
    /**
     * Sets (as xml) the "EDocumentType" element
     */
    public void xsetEDocumentType(org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType eDocumentType)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType)get_store().find_element_user(EDOCUMENTTYPE$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType)get_store().add_element_user(EDOCUMENTTYPE$0);
            }
            target.set(eDocumentType);
        }
    }
    
    /**
     * Nils the "EDocumentType" element
     */
    public void setNilEDocumentType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType)get_store().find_element_user(EDOCUMENTTYPE$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType)get_store().add_element_user(EDOCUMENTTYPE$0);
            }
            target.setNil();
        }
    }
}
