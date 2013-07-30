/*
 * XML Type:  ArrayOfguid
 * Namespace: http://schemas.microsoft.com/2003/10/Serialization/Arrays
 * Java type: com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid
 *
 * Automatically generated - do not modify.
 */
package com.microsoft.schemas.x2003.x10.serialization.arrays;


/**
 * An XML ArrayOfguid(@http://schemas.microsoft.com/2003/10/Serialization/Arrays).
 *
 * This is a complex type.
 */
public interface ArrayOfguid extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(ArrayOfguid.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("arrayofguid2a27type");
    
    /**
     * Gets a List of "guid" elements
     */
    java.util.List<java.lang.String> getGuidList();
    
    /**
     * Gets array of all "guid" elements
     * @deprecated
     */
    @Deprecated
    java.lang.String[] getGuidArray();
    
    /**
     * Gets ith "guid" element
     */
    java.lang.String getGuidArray(int i);
    
    /**
     * Gets (as xml) a List of "guid" elements
     */
    java.util.List<com.microsoft.schemas.x2003.x10.serialization.Guid> xgetGuidList();
    
    /**
     * Gets (as xml) array of all "guid" elements
     * @deprecated
     */
    @Deprecated
    com.microsoft.schemas.x2003.x10.serialization.Guid[] xgetGuidArray();
    
    /**
     * Gets (as xml) ith "guid" element
     */
    com.microsoft.schemas.x2003.x10.serialization.Guid xgetGuidArray(int i);
    
    /**
     * Returns number of "guid" element
     */
    int sizeOfGuidArray();
    
    /**
     * Sets array of all "guid" element
     */
    void setGuidArray(java.lang.String[] guidArray);
    
    /**
     * Sets ith "guid" element
     */
    void setGuidArray(int i, java.lang.String guid);
    
    /**
     * Sets (as xml) array of all "guid" element
     */
    void xsetGuidArray(com.microsoft.schemas.x2003.x10.serialization.Guid[] guidArray);
    
    /**
     * Sets (as xml) ith "guid" element
     */
    void xsetGuidArray(int i, com.microsoft.schemas.x2003.x10.serialization.Guid guid);
    
    /**
     * Inserts the value as the ith "guid" element
     */
    void insertGuid(int i, java.lang.String guid);
    
    /**
     * Appends the value as the last "guid" element
     */
    void addGuid(java.lang.String guid);
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "guid" element
     */
    com.microsoft.schemas.x2003.x10.serialization.Guid insertNewGuid(int i);
    
    /**
     * Appends and returns a new empty value (as xml) as the last "guid" element
     */
    com.microsoft.schemas.x2003.x10.serialization.Guid addNewGuid();
    
    /**
     * Removes the ith "guid" element
     */
    void removeGuid(int i);
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid newInstance() {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
