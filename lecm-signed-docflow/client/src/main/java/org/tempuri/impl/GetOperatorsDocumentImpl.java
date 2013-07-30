/*
 * An XML document type.
 * Localname: GetOperators
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.GetOperatorsDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one GetOperators(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class GetOperatorsDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GetOperatorsDocument
{
    private static final long serialVersionUID = 1L;
    
    public GetOperatorsDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName GETOPERATORS$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "GetOperators");
    
    
    /**
     * Gets the "GetOperators" element
     */
    public org.tempuri.GetOperatorsDocument.GetOperators getGetOperators()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GetOperatorsDocument.GetOperators target = null;
            target = (org.tempuri.GetOperatorsDocument.GetOperators)get_store().find_element_user(GETOPERATORS$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "GetOperators" element
     */
    public void setGetOperators(org.tempuri.GetOperatorsDocument.GetOperators getOperators)
    {
        generatedSetterHelperImpl(getOperators, GETOPERATORS$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "GetOperators" element
     */
    public org.tempuri.GetOperatorsDocument.GetOperators addNewGetOperators()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.GetOperatorsDocument.GetOperators target = null;
            target = (org.tempuri.GetOperatorsDocument.GetOperators)get_store().add_element_user(GETOPERATORS$0);
            return target;
        }
    }
    /**
     * An XML GetOperators(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class GetOperatorsImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.GetOperatorsDocument.GetOperators
    {
        private static final long serialVersionUID = 1L;
        
        public GetOperatorsImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        
    }
}
