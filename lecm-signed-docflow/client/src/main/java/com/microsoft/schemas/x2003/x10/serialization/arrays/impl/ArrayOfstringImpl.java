/*
 * XML Type:  ArrayOfstring
 * Namespace: http://schemas.microsoft.com/2003/10/Serialization/Arrays
 * Java type: com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring
 *
 * Automatically generated - do not modify.
 */
package com.microsoft.schemas.x2003.x10.serialization.arrays.impl;
/**
 * An XML ArrayOfstring(@http://schemas.microsoft.com/2003/10/Serialization/Arrays).
 *
 * This is a complex type.
 */
public class ArrayOfstringImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring
{
    private static final long serialVersionUID = 1L;
    
    public ArrayOfstringImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName STRING$0 = 
        new javax.xml.namespace.QName("http://schemas.microsoft.com/2003/10/Serialization/Arrays", "string");
    
    
    /**
     * Gets a List of "string" elements
     */
    public java.util.List<java.lang.String> getStringList()
    {
        final class StringList extends java.util.AbstractList<java.lang.String>
        {
            @Override
            public java.lang.String get(int i)
                { return ArrayOfstringImpl.this.getStringArray(i); }
            
            @Override
            public java.lang.String set(int i, java.lang.String o)
            {
                java.lang.String old = ArrayOfstringImpl.this.getStringArray(i);
                ArrayOfstringImpl.this.setStringArray(i, o);
                return old;
            }
            
            @Override
            public void add(int i, java.lang.String o)
                { ArrayOfstringImpl.this.insertString(i, o); }
            
            @Override
            public java.lang.String remove(int i)
            {
                java.lang.String old = ArrayOfstringImpl.this.getStringArray(i);
                ArrayOfstringImpl.this.removeString(i);
                return old;
            }
            
            @Override
            public int size()
                { return ArrayOfstringImpl.this.sizeOfStringArray(); }
            
        }
        
        synchronized (monitor())
        {
            check_orphaned();
            return new StringList();
        }
    }
    
    /**
     * Gets array of all "string" elements
     * @deprecated
     */
    @Deprecated
    public java.lang.String[] getStringArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            java.util.List<org.apache.xmlbeans.XmlString> targetList = new java.util.ArrayList<org.apache.xmlbeans.XmlString>();
            get_store().find_all_element_users(STRING$0, targetList);
            java.lang.String[] result = new java.lang.String[targetList.size()];
            for (int i = 0, len = targetList.size() ; i < len ; i++)
                result[i] = ((org.apache.xmlbeans.SimpleValue)targetList.get(i)).getStringValue();
            return result;
        }
    }
    
    /**
     * Gets ith "string" element
     */
    public java.lang.String getStringArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(STRING$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) a List of "string" elements
     */
    public java.util.List<org.apache.xmlbeans.XmlString> xgetStringList()
    {
        final class StringList extends java.util.AbstractList<org.apache.xmlbeans.XmlString>
        {
            @Override
            public org.apache.xmlbeans.XmlString get(int i)
                { return ArrayOfstringImpl.this.xgetStringArray(i); }
            
            @Override
            public org.apache.xmlbeans.XmlString set(int i, org.apache.xmlbeans.XmlString o)
            {
                org.apache.xmlbeans.XmlString old = ArrayOfstringImpl.this.xgetStringArray(i);
                ArrayOfstringImpl.this.xsetStringArray(i, o);
                return old;
            }
            
            @Override
            public void add(int i, org.apache.xmlbeans.XmlString o)
                { ArrayOfstringImpl.this.insertNewString(i).set(o); }
            
            @Override
            public org.apache.xmlbeans.XmlString remove(int i)
            {
                org.apache.xmlbeans.XmlString old = ArrayOfstringImpl.this.xgetStringArray(i);
                ArrayOfstringImpl.this.removeString(i);
                return old;
            }
            
            @Override
            public int size()
                { return ArrayOfstringImpl.this.sizeOfStringArray(); }
            
        }
        
        synchronized (monitor())
        {
            check_orphaned();
            return new StringList();
        }
    }
    
    /**
     * Gets array of all "string" elements
     * @deprecated
     */
    @Deprecated
    public org.apache.xmlbeans.XmlString[] xgetStringArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            java.util.List<org.apache.xmlbeans.XmlString> targetList = new java.util.ArrayList<org.apache.xmlbeans.XmlString>();
            get_store().find_all_element_users(STRING$0, targetList);
            org.apache.xmlbeans.XmlString[] result = new org.apache.xmlbeans.XmlString[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    /**
     * Gets (as xml) ith "string" element
     */
    public org.apache.xmlbeans.XmlString xgetStringArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(STRING$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }
    
    /**
     * Tests for nil ith "string" element
     */
    public boolean isNilStringArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(STRING$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return target.isNil();
        }
    }
    
    /**
     * Returns number of "string" element
     */
    public int sizeOfStringArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(STRING$0);
        }
    }
    
    /**
     * Sets array of all "string" element
     */
    public void setStringArray(java.lang.String[] stringArray)
    {
        synchronized (monitor())
        {
            check_orphaned();
            arraySetterHelper(stringArray, STRING$0);
        }
    }
    
    /**
     * Sets ith "string" element
     */
    public void setStringArray(int i, java.lang.String string)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(STRING$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            target.setStringValue(string);
        }
    }
    
    /**
     * Sets (as xml) array of all "string" element
     */
    public void xsetStringArray(org.apache.xmlbeans.XmlString[]stringArray)
    {
        synchronized (monitor())
        {
            check_orphaned();
            arraySetterHelper(stringArray, STRING$0);
        }
    }
    
    /**
     * Sets (as xml) ith "string" element
     */
    public void xsetStringArray(int i, org.apache.xmlbeans.XmlString string)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(STRING$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            target.set(string);
        }
    }
    
    /**
     * Nils the ith "string" element
     */
    public void setNilStringArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(STRING$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            target.setNil();
        }
    }
    
    /**
     * Inserts the value as the ith "string" element
     */
    public void insertString(int i, java.lang.String string)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = 
                (org.apache.xmlbeans.SimpleValue)get_store().insert_element_user(STRING$0, i);
            target.setStringValue(string);
        }
    }
    
    /**
     * Appends the value as the last "string" element
     */
    public void addString(java.lang.String string)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(STRING$0);
            target.setStringValue(string);
        }
    }
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "string" element
     */
    public org.apache.xmlbeans.XmlString insertNewString(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().insert_element_user(STRING$0, i);
            return target;
        }
    }
    
    /**
     * Appends and returns a new empty value (as xml) as the last "string" element
     */
    public org.apache.xmlbeans.XmlString addNewString()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(STRING$0);
            return target;
        }
    }
    
    /**
     * Removes the ith "string" element
     */
    public void removeString(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(STRING$0, i);
        }
    }
}
