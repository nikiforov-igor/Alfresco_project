/*
 * An XML document type.
 * Localname: ClearMailBox
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.ClearMailBoxDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri.impl;
/**
 * A document containing one ClearMailBox(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public class ClearMailBoxDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.ClearMailBoxDocument
{
    private static final long serialVersionUID = 1L;
    
    public ClearMailBoxDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName CLEARMAILBOX$0 = 
        new javax.xml.namespace.QName("http://tempuri.org/", "ClearMailBox");
    
    
    /**
     * Gets the "ClearMailBox" element
     */
    public org.tempuri.ClearMailBoxDocument.ClearMailBox getClearMailBox()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.ClearMailBoxDocument.ClearMailBox target = null;
            target = (org.tempuri.ClearMailBoxDocument.ClearMailBox)get_store().find_element_user(CLEARMAILBOX$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "ClearMailBox" element
     */
    public void setClearMailBox(org.tempuri.ClearMailBoxDocument.ClearMailBox clearMailBox)
    {
        generatedSetterHelperImpl(clearMailBox, CLEARMAILBOX$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "ClearMailBox" element
     */
    public org.tempuri.ClearMailBoxDocument.ClearMailBox addNewClearMailBox()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.tempuri.ClearMailBoxDocument.ClearMailBox target = null;
            target = (org.tempuri.ClearMailBoxDocument.ClearMailBox)get_store().add_element_user(CLEARMAILBOX$0);
            return target;
        }
    }
    /**
     * An XML ClearMailBox(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public static class ClearMailBoxImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.tempuri.ClearMailBoxDocument.ClearMailBox
    {
        private static final long serialVersionUID = 1L;
        
        public ClearMailBoxImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        
    }
}
