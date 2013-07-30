/*
 * XML Type:  ArrayOfbase64Binary
 * Namespace: http://schemas.microsoft.com/2003/10/Serialization/Arrays
 * Java type: com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary
 *
 * Automatically generated - do not modify.
 */
package com.microsoft.schemas.x2003.x10.serialization.arrays;


/**
 * An XML ArrayOfbase64Binary(@http://schemas.microsoft.com/2003/10/Serialization/Arrays).
 *
 * This is a complex type.
 */
public interface ArrayOfbase64Binary extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(ArrayOfbase64Binary.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("arrayofbase64binary6c0etype");
    
    /**
     * Gets a List of "base64Binary" elements
     */
    java.util.List<byte[]> getBase64BinaryList();
    
    /**
     * Gets array of all "base64Binary" elements
     * @deprecated
     */
    @Deprecated
    byte[][] getBase64BinaryArray();
    
    /**
     * Gets ith "base64Binary" element
     */
    byte[] getBase64BinaryArray(int i);
    
    /**
     * Gets (as xml) a List of "base64Binary" elements
     */
    java.util.List<org.apache.xmlbeans.XmlBase64Binary> xgetBase64BinaryList();
    
    /**
     * Gets (as xml) array of all "base64Binary" elements
     * @deprecated
     */
    @Deprecated
    org.apache.xmlbeans.XmlBase64Binary[] xgetBase64BinaryArray();
    
    /**
     * Gets (as xml) ith "base64Binary" element
     */
    org.apache.xmlbeans.XmlBase64Binary xgetBase64BinaryArray(int i);
    
    /**
     * Tests for nil ith "base64Binary" element
     */
    boolean isNilBase64BinaryArray(int i);
    
    /**
     * Returns number of "base64Binary" element
     */
    int sizeOfBase64BinaryArray();
    
    /**
     * Sets array of all "base64Binary" element
     */
    void setBase64BinaryArray(byte[][] base64BinaryArray);
    
    /**
     * Sets ith "base64Binary" element
     */
    void setBase64BinaryArray(int i, byte[] base64Binary);
    
    /**
     * Sets (as xml) array of all "base64Binary" element
     */
    void xsetBase64BinaryArray(org.apache.xmlbeans.XmlBase64Binary[] base64BinaryArray);
    
    /**
     * Sets (as xml) ith "base64Binary" element
     */
    void xsetBase64BinaryArray(int i, org.apache.xmlbeans.XmlBase64Binary base64Binary);
    
    /**
     * Nils the ith "base64Binary" element
     */
    void setNilBase64BinaryArray(int i);
    
    /**
     * Inserts the value as the ith "base64Binary" element
     */
    void insertBase64Binary(int i, byte[] base64Binary);
    
    /**
     * Appends the value as the last "base64Binary" element
     */
    void addBase64Binary(byte[] base64Binary);
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "base64Binary" element
     */
    org.apache.xmlbeans.XmlBase64Binary insertNewBase64Binary(int i);
    
    /**
     * Appends and returns a new empty value (as xml) as the last "base64Binary" element
     */
    org.apache.xmlbeans.XmlBase64Binary addNewBase64Binary();
    
    /**
     * Removes the ith "base64Binary" element
     */
    void removeBase64Binary(int i);
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary newInstance() {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}
