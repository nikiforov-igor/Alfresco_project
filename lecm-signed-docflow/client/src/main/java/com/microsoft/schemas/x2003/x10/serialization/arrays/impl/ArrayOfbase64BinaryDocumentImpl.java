/*
 * An XML document type.
 * Localname: ArrayOfbase64Binary
 * Namespace: http://schemas.microsoft.com/2003/10/Serialization/Arrays
 * Java type: com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64BinaryDocument
 *
 * Automatically generated - do not modify.
 */
package com.microsoft.schemas.x2003.x10.serialization.arrays.impl;
/**
 * A document containing one ArrayOfbase64Binary(@http://schemas.microsoft.com/2003/10/Serialization/Arrays) element.
 *
 * This is a complex type.
 */
public class ArrayOfbase64BinaryDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64BinaryDocument
{
    private static final long serialVersionUID = 1L;
    
    public ArrayOfbase64BinaryDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName ARRAYOFBASE64BINARY$0 = 
        new javax.xml.namespace.QName("http://schemas.microsoft.com/2003/10/Serialization/Arrays", "ArrayOfbase64Binary");
    
    
    /**
     * Gets the "ArrayOfbase64Binary" element
     */
    public com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary getArrayOfbase64Binary()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary target = null;
            target = (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary)get_store().find_element_user(ARRAYOFBASE64BINARY$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "ArrayOfbase64Binary" element
     */
    public boolean isNilArrayOfbase64Binary()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary target = null;
            target = (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary)get_store().find_element_user(ARRAYOFBASE64BINARY$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "ArrayOfbase64Binary" element
     */
    public void setArrayOfbase64Binary(com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary arrayOfbase64Binary)
    {
        generatedSetterHelperImpl(arrayOfbase64Binary, ARRAYOFBASE64BINARY$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "ArrayOfbase64Binary" element
     */
    public com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary addNewArrayOfbase64Binary()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary target = null;
            target = (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary)get_store().add_element_user(ARRAYOFBASE64BINARY$0);
            return target;
        }
    }
    
    /**
     * Nils the "ArrayOfbase64Binary" element
     */
    public void setNilArrayOfbase64Binary()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary target = null;
            target = (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary)get_store().find_element_user(ARRAYOFBASE64BINARY$0, 0);
            if (target == null)
            {
                target = (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary)get_store().add_element_user(ARRAYOFBASE64BINARY$0);
            }
            target.setNil();
        }
    }
}
