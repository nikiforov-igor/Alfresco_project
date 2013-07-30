/*
 * XML Type:  ArrayOfbase64Binary
 * Namespace: http://schemas.microsoft.com/2003/10/Serialization/Arrays
 * Java type: com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary
 *
 * Automatically generated - do not modify.
 */
package com.microsoft.schemas.x2003.x10.serialization.arrays.impl;
/**
 * An XML ArrayOfbase64Binary(@http://schemas.microsoft.com/2003/10/Serialization/Arrays).
 *
 * This is a complex type.
 */
public class ArrayOfbase64BinaryImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary
{
    private static final long serialVersionUID = 1L;
    
    public ArrayOfbase64BinaryImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName BASE64BINARY$0 = 
        new javax.xml.namespace.QName("http://schemas.microsoft.com/2003/10/Serialization/Arrays", "base64Binary");
    
    
    /**
     * Gets a List of "base64Binary" elements
     */
    public java.util.List<byte[]> getBase64BinaryList()
    {
        final class Base64BinaryList extends java.util.AbstractList<byte[]>
        {
            @Override
            public byte[] get(int i)
                { return ArrayOfbase64BinaryImpl.this.getBase64BinaryArray(i); }
            
            @Override
            public byte[] set(int i, byte[] o)
            {
                byte[] old = ArrayOfbase64BinaryImpl.this.getBase64BinaryArray(i);
                ArrayOfbase64BinaryImpl.this.setBase64BinaryArray(i, o);
                return old;
            }
            
            @Override
            public void add(int i, byte[] o)
                { ArrayOfbase64BinaryImpl.this.insertBase64Binary(i, o); }
            
            @Override
            public byte[] remove(int i)
            {
                byte[] old = ArrayOfbase64BinaryImpl.this.getBase64BinaryArray(i);
                ArrayOfbase64BinaryImpl.this.removeBase64Binary(i);
                return old;
            }
            
            @Override
            public int size()
                { return ArrayOfbase64BinaryImpl.this.sizeOfBase64BinaryArray(); }
            
        }
        
        synchronized (monitor())
        {
            check_orphaned();
            return new Base64BinaryList();
        }
    }
    
    /**
     * Gets array of all "base64Binary" elements
     * @deprecated
     */
    @Deprecated
    public byte[][] getBase64BinaryArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            java.util.List<org.apache.xmlbeans.XmlBase64Binary> targetList = new java.util.ArrayList<org.apache.xmlbeans.XmlBase64Binary>();
            get_store().find_all_element_users(BASE64BINARY$0, targetList);
            byte[][] result = new byte[targetList.size()][];
            for (int i = 0, len = targetList.size() ; i < len ; i++)
                result[i] = ((org.apache.xmlbeans.SimpleValue)targetList.get(i)).getByteArrayValue();
            return result;
        }
    }
    
    /**
     * Gets ith "base64Binary" element
     */
    public byte[] getBase64BinaryArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(BASE64BINARY$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return target.getByteArrayValue();
        }
    }
    
    /**
     * Gets (as xml) a List of "base64Binary" elements
     */
    public java.util.List<org.apache.xmlbeans.XmlBase64Binary> xgetBase64BinaryList()
    {
        final class Base64BinaryList extends java.util.AbstractList<org.apache.xmlbeans.XmlBase64Binary>
        {
            @Override
            public org.apache.xmlbeans.XmlBase64Binary get(int i)
                { return ArrayOfbase64BinaryImpl.this.xgetBase64BinaryArray(i); }
            
            @Override
            public org.apache.xmlbeans.XmlBase64Binary set(int i, org.apache.xmlbeans.XmlBase64Binary o)
            {
                org.apache.xmlbeans.XmlBase64Binary old = ArrayOfbase64BinaryImpl.this.xgetBase64BinaryArray(i);
                ArrayOfbase64BinaryImpl.this.xsetBase64BinaryArray(i, o);
                return old;
            }
            
            @Override
            public void add(int i, org.apache.xmlbeans.XmlBase64Binary o)
                { ArrayOfbase64BinaryImpl.this.insertNewBase64Binary(i).set(o); }
            
            @Override
            public org.apache.xmlbeans.XmlBase64Binary remove(int i)
            {
                org.apache.xmlbeans.XmlBase64Binary old = ArrayOfbase64BinaryImpl.this.xgetBase64BinaryArray(i);
                ArrayOfbase64BinaryImpl.this.removeBase64Binary(i);
                return old;
            }
            
            @Override
            public int size()
                { return ArrayOfbase64BinaryImpl.this.sizeOfBase64BinaryArray(); }
            
        }
        
        synchronized (monitor())
        {
            check_orphaned();
            return new Base64BinaryList();
        }
    }
    
    /**
     * Gets array of all "base64Binary" elements
     * @deprecated
     */
    @Deprecated
    public org.apache.xmlbeans.XmlBase64Binary[] xgetBase64BinaryArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            java.util.List<org.apache.xmlbeans.XmlBase64Binary> targetList = new java.util.ArrayList<org.apache.xmlbeans.XmlBase64Binary>();
            get_store().find_all_element_users(BASE64BINARY$0, targetList);
            org.apache.xmlbeans.XmlBase64Binary[] result = new org.apache.xmlbeans.XmlBase64Binary[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    /**
     * Gets (as xml) ith "base64Binary" element
     */
    public org.apache.xmlbeans.XmlBase64Binary xgetBase64BinaryArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBase64Binary target = null;
            target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(BASE64BINARY$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }
    
    /**
     * Tests for nil ith "base64Binary" element
     */
    public boolean isNilBase64BinaryArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBase64Binary target = null;
            target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(BASE64BINARY$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return target.isNil();
        }
    }
    
    /**
     * Returns number of "base64Binary" element
     */
    public int sizeOfBase64BinaryArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(BASE64BINARY$0);
        }
    }
    
    /**
     * Sets array of all "base64Binary" element
     */
    public void setBase64BinaryArray(byte[][] base64BinaryArray)
    {
        synchronized (monitor())
        {
            check_orphaned();
            arraySetterHelper(base64BinaryArray, BASE64BINARY$0);
        }
    }
    
    /**
     * Sets ith "base64Binary" element
     */
    public void setBase64BinaryArray(int i, byte[] base64Binary)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(BASE64BINARY$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            target.setByteArrayValue(base64Binary);
        }
    }
    
    /**
     * Sets (as xml) array of all "base64Binary" element
     */
    public void xsetBase64BinaryArray(org.apache.xmlbeans.XmlBase64Binary[]base64BinaryArray)
    {
        synchronized (monitor())
        {
            check_orphaned();
            arraySetterHelper(base64BinaryArray, BASE64BINARY$0);
        }
    }
    
    /**
     * Sets (as xml) ith "base64Binary" element
     */
    public void xsetBase64BinaryArray(int i, org.apache.xmlbeans.XmlBase64Binary base64Binary)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBase64Binary target = null;
            target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(BASE64BINARY$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            target.set(base64Binary);
        }
    }
    
    /**
     * Nils the ith "base64Binary" element
     */
    public void setNilBase64BinaryArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBase64Binary target = null;
            target = (org.apache.xmlbeans.XmlBase64Binary)get_store().find_element_user(BASE64BINARY$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            target.setNil();
        }
    }
    
    /**
     * Inserts the value as the ith "base64Binary" element
     */
    public void insertBase64Binary(int i, byte[] base64Binary)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = 
                (org.apache.xmlbeans.SimpleValue)get_store().insert_element_user(BASE64BINARY$0, i);
            target.setByteArrayValue(base64Binary);
        }
    }
    
    /**
     * Appends the value as the last "base64Binary" element
     */
    public void addBase64Binary(byte[] base64Binary)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(BASE64BINARY$0);
            target.setByteArrayValue(base64Binary);
        }
    }
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "base64Binary" element
     */
    public org.apache.xmlbeans.XmlBase64Binary insertNewBase64Binary(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBase64Binary target = null;
            target = (org.apache.xmlbeans.XmlBase64Binary)get_store().insert_element_user(BASE64BINARY$0, i);
            return target;
        }
    }
    
    /**
     * Appends and returns a new empty value (as xml) as the last "base64Binary" element
     */
    public org.apache.xmlbeans.XmlBase64Binary addNewBase64Binary()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBase64Binary target = null;
            target = (org.apache.xmlbeans.XmlBase64Binary)get_store().add_element_user(BASE64BINARY$0);
            return target;
        }
    }
    
    /**
     * Removes the ith "base64Binary" element
     */
    public void removeBase64Binary(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(BASE64BINARY$0, i);
        }
    }
}
