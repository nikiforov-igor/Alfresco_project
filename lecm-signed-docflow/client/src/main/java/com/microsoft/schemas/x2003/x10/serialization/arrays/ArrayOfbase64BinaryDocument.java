/*
 * An XML document type.
 * Localname: ArrayOfbase64Binary
 * Namespace: http://schemas.microsoft.com/2003/10/Serialization/Arrays
 * Java type: com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64BinaryDocument
 *
 * Automatically generated - do not modify.
 */
package com.microsoft.schemas.x2003.x10.serialization.arrays;


/**
 * A document containing one ArrayOfbase64Binary(@http://schemas.microsoft.com/2003/10/Serialization/Arrays) element.
 *
 * This is a complex type.
 */
public interface ArrayOfbase64BinaryDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(ArrayOfbase64BinaryDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("arrayofbase64binary47fedoctype");
    
    /**
     * Gets the "ArrayOfbase64Binary" element
     */
    com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary getArrayOfbase64Binary();
    
    /**
     * Tests for nil "ArrayOfbase64Binary" element
     */
    boolean isNilArrayOfbase64Binary();
    
    /**
     * Sets the "ArrayOfbase64Binary" element
     */
    void setArrayOfbase64Binary(com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary arrayOfbase64Binary);
    
    /**
     * Appends and returns a new empty "ArrayOfbase64Binary" element
     */
    com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary addNewArrayOfbase64Binary();
    
    /**
     * Nils the "ArrayOfbase64Binary" element
     */
    void setNilArrayOfbase64Binary();
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64BinaryDocument newInstance() {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64BinaryDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64BinaryDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64BinaryDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64BinaryDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64BinaryDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64BinaryDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64BinaryDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64BinaryDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64BinaryDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64BinaryDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64BinaryDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64BinaryDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64BinaryDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64BinaryDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64BinaryDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64BinaryDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64BinaryDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64BinaryDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64BinaryDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64BinaryDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64BinaryDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64BinaryDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64BinaryDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64BinaryDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64BinaryDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64BinaryDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64BinaryDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64BinaryDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64BinaryDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64BinaryDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64BinaryDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64BinaryDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64BinaryDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64BinaryDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64BinaryDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
