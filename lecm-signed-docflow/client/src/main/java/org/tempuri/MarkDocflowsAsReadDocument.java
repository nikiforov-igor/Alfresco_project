/*
 * An XML document type.
 * Localname: MarkDocflowsAsRead
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.MarkDocflowsAsReadDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri;


/**
 * A document containing one MarkDocflowsAsRead(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public interface MarkDocflowsAsReadDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(MarkDocflowsAsReadDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("markdocflowsasread82cedoctype");
    
    /**
     * Gets the "MarkDocflowsAsRead" element
     */
    org.tempuri.MarkDocflowsAsReadDocument.MarkDocflowsAsRead getMarkDocflowsAsRead();
    
    /**
     * Sets the "MarkDocflowsAsRead" element
     */
    void setMarkDocflowsAsRead(org.tempuri.MarkDocflowsAsReadDocument.MarkDocflowsAsRead markDocflowsAsRead);
    
    /**
     * Appends and returns a new empty "MarkDocflowsAsRead" element
     */
    org.tempuri.MarkDocflowsAsReadDocument.MarkDocflowsAsRead addNewMarkDocflowsAsRead();
    
    /**
     * An XML MarkDocflowsAsRead(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public interface MarkDocflowsAsRead extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(MarkDocflowsAsRead.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("markdocflowsasread8f90elemtype");
        
        /**
         * Gets the "docflowIdArray" element
         */
        com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid getDocflowIdArray1();
        
        /**
         * Tests for nil "docflowIdArray" element
         */
        boolean isNilDocflowIdArray1();
        
        /**
         * True if has "docflowIdArray" element
         */
        boolean isSetDocflowIdArray1();
        
        /**
         * Sets the "docflowIdArray" element
         */
        void setDocflowIdArray1(com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid docflowIdArray1);
        
        /**
         * Appends and returns a new empty "docflowIdArray" element
         */
        com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfguid addNewDocflowIdArray1();
        
        /**
         * Nils the "docflowIdArray" element
         */
        void setNilDocflowIdArray1();
        
        /**
         * Unsets the "docflowIdArray" element
         */
        void unsetDocflowIdArray1();
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static org.tempuri.MarkDocflowsAsReadDocument.MarkDocflowsAsRead newInstance() {
              return (org.tempuri.MarkDocflowsAsReadDocument.MarkDocflowsAsRead) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static org.tempuri.MarkDocflowsAsReadDocument.MarkDocflowsAsRead newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (org.tempuri.MarkDocflowsAsReadDocument.MarkDocflowsAsRead) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.tempuri.MarkDocflowsAsReadDocument newInstance() {
          return (org.tempuri.MarkDocflowsAsReadDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.tempuri.MarkDocflowsAsReadDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.tempuri.MarkDocflowsAsReadDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.tempuri.MarkDocflowsAsReadDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.MarkDocflowsAsReadDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.tempuri.MarkDocflowsAsReadDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.MarkDocflowsAsReadDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.tempuri.MarkDocflowsAsReadDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.MarkDocflowsAsReadDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.tempuri.MarkDocflowsAsReadDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.MarkDocflowsAsReadDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.tempuri.MarkDocflowsAsReadDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.MarkDocflowsAsReadDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.tempuri.MarkDocflowsAsReadDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.MarkDocflowsAsReadDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.tempuri.MarkDocflowsAsReadDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.MarkDocflowsAsReadDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.tempuri.MarkDocflowsAsReadDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.MarkDocflowsAsReadDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.tempuri.MarkDocflowsAsReadDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.MarkDocflowsAsReadDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.tempuri.MarkDocflowsAsReadDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.MarkDocflowsAsReadDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.tempuri.MarkDocflowsAsReadDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.MarkDocflowsAsReadDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.tempuri.MarkDocflowsAsReadDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.MarkDocflowsAsReadDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.tempuri.MarkDocflowsAsReadDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.MarkDocflowsAsReadDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.tempuri.MarkDocflowsAsReadDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.MarkDocflowsAsReadDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.MarkDocflowsAsReadDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.MarkDocflowsAsReadDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.MarkDocflowsAsReadDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.MarkDocflowsAsReadDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
