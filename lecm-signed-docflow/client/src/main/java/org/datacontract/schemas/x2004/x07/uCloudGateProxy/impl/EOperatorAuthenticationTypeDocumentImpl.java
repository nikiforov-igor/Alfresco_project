/*
 * An XML document type.
 * Localname: EOperatorAuthenticationType
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxy.EOperatorAuthenticationTypeDocument
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxy.impl;
/**
 * A document containing one EOperatorAuthenticationType(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy) element.
 *
 * This is a complex type.
 */
public class EOperatorAuthenticationTypeDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.datacontract.schemas.x2004.x07.uCloudGateProxy.EOperatorAuthenticationTypeDocument
{
    private static final long serialVersionUID = 1L;
    
    public EOperatorAuthenticationTypeDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName EOPERATORAUTHENTICATIONTYPE$0 = 
        new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "EOperatorAuthenticationType");
    
    
    /**
     * Gets the "EOperatorAuthenticationType" element
     */
    public java.util.List getEOperatorAuthenticationType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(EOPERATORAUTHENTICATIONTYPE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getListValue();
        }
    }
    
    /**
     * Gets (as xml) the "EOperatorAuthenticationType" element
     */
    public org.datacontract.schemas.x2004.x07.uCloudGateProxy.EOperatorAuthenticationType xgetEOperatorAuthenticationType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.EOperatorAuthenticationType target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EOperatorAuthenticationType)get_store().find_element_user(EOPERATORAUTHENTICATIONTYPE$0, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "EOperatorAuthenticationType" element
     */
    public boolean isNilEOperatorAuthenticationType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.EOperatorAuthenticationType target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EOperatorAuthenticationType)get_store().find_element_user(EOPERATORAUTHENTICATIONTYPE$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "EOperatorAuthenticationType" element
     */
    public void setEOperatorAuthenticationType(java.util.List eOperatorAuthenticationType)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(EOPERATORAUTHENTICATIONTYPE$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(EOPERATORAUTHENTICATIONTYPE$0);
            }
            target.setListValue(eOperatorAuthenticationType);
        }
    }
    
    /**
     * Sets (as xml) the "EOperatorAuthenticationType" element
     */
    public void xsetEOperatorAuthenticationType(org.datacontract.schemas.x2004.x07.uCloudGateProxy.EOperatorAuthenticationType eOperatorAuthenticationType)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.EOperatorAuthenticationType target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EOperatorAuthenticationType)get_store().find_element_user(EOPERATORAUTHENTICATIONTYPE$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EOperatorAuthenticationType)get_store().add_element_user(EOPERATORAUTHENTICATIONTYPE$0);
            }
            target.set(eOperatorAuthenticationType);
        }
    }
    
    /**
     * Nils the "EOperatorAuthenticationType" element
     */
    public void setNilEOperatorAuthenticationType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.datacontract.schemas.x2004.x07.uCloudGateProxy.EOperatorAuthenticationType target = null;
            target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EOperatorAuthenticationType)get_store().find_element_user(EOPERATORAUTHENTICATIONTYPE$0, 0);
            if (target == null)
            {
                target = (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EOperatorAuthenticationType)get_store().add_element_user(EOPERATORAUTHENTICATIONTYPE$0);
            }
            target.setNil();
        }
    }
}
