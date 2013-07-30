/*
 * An XML document type.
 * Localname: EDocflowTransactionType
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Docflow
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionTypeDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.impl;
/**
 * A document containing one EDocflowTransactionType(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Docflow) element.
 *
 * This is a complex type.
 */
public class EDocflowTransactionTypeDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionTypeDocument
{
    private static final long serialVersionUID = 1L;
    
    public EDocflowTransactionTypeDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName EDOCFLOWTRANSACTIONTYPE$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Docflow", "EDocflowTransactionType");
    
    
    /**
     * Gets the "EDocflowTransactionType" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType.Enum getEDocflowTransactionType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(EDOCFLOWTRANSACTIONTYPE$0, 0);
            if (target == null)
            {
                return null;
            }
            return (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType.Enum)target.getEnumValue();
        }
    }
    
    /**
     * Gets (as xml) the "EDocflowTransactionType" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType xgetEDocflowTransactionType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType)get_store().find_element_user(EDOCFLOWTRANSACTIONTYPE$0, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "EDocflowTransactionType" element
     */
    public boolean isNilEDocflowTransactionType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType)get_store().find_element_user(EDOCFLOWTRANSACTIONTYPE$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "EDocflowTransactionType" element
     */
    public void setEDocflowTransactionType(org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType.Enum eDocflowTransactionType)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(EDOCFLOWTRANSACTIONTYPE$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(EDOCFLOWTRANSACTIONTYPE$0);
            }
            target.setEnumValue(eDocflowTransactionType);
        }
    }
    
    /**
     * Sets (as xml) the "EDocflowTransactionType" element
     */
    public void xsetEDocflowTransactionType(org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType eDocflowTransactionType)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType)get_store().find_element_user(EDOCFLOWTRANSACTIONTYPE$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType)get_store().add_element_user(EDOCFLOWTRANSACTIONTYPE$0);
            }
            target.set(eDocflowTransactionType);
        }
    }
    
    /**
     * Nils the "EDocflowTransactionType" element
     */
    public void setNilEDocflowTransactionType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType)get_store().find_element_user(EDOCFLOWTRANSACTIONTYPE$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType)get_store().add_element_user(EDOCFLOWTRANSACTIONTYPE$0);
            }
            target.setNil();
        }
    }
}
