/*
 * An XML document type.
 * Localname: MarkDocflowsAsReadResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.MarkDocflowsAsReadResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri;


/**
 * A document containing one MarkDocflowsAsReadResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public interface MarkDocflowsAsReadResponseDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(MarkDocflowsAsReadResponseDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("markdocflowsasreadresponseb5afdoctype");
    
    /**
     * Gets the "MarkDocflowsAsReadResponse" element
     */
    org.tempuri.MarkDocflowsAsReadResponseDocument.MarkDocflowsAsReadResponse getMarkDocflowsAsReadResponse();
    
    /**
     * Sets the "MarkDocflowsAsReadResponse" element
     */
    void setMarkDocflowsAsReadResponse(org.tempuri.MarkDocflowsAsReadResponseDocument.MarkDocflowsAsReadResponse markDocflowsAsReadResponse);
    
    /**
     * Appends and returns a new empty "MarkDocflowsAsReadResponse" element
     */
    org.tempuri.MarkDocflowsAsReadResponseDocument.MarkDocflowsAsReadResponse addNewMarkDocflowsAsReadResponse();
    
    /**
     * An XML MarkDocflowsAsReadResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public interface MarkDocflowsAsReadResponse extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(MarkDocflowsAsReadResponse.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("markdocflowsasreadresponseb430elemtype");
        
        /**
         * Gets the "MarkDocflowsAsReadResult" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getMarkDocflowsAsReadResult();
        
        /**
         * Tests for nil "MarkDocflowsAsReadResult" element
         */
        boolean isNilMarkDocflowsAsReadResult();
        
        /**
         * True if has "MarkDocflowsAsReadResult" element
         */
        boolean isSetMarkDocflowsAsReadResult();
        
        /**
         * Sets the "MarkDocflowsAsReadResult" element
         */
        void setMarkDocflowsAsReadResult(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse markDocflowsAsReadResult);
        
        /**
         * Appends and returns a new empty "MarkDocflowsAsReadResult" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewMarkDocflowsAsReadResult();
        
        /**
         * Nils the "MarkDocflowsAsReadResult" element
         */
        void setNilMarkDocflowsAsReadResult();
        
        /**
         * Unsets the "MarkDocflowsAsReadResult" element
         */
        void unsetMarkDocflowsAsReadResult();
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static org.tempuri.MarkDocflowsAsReadResponseDocument.MarkDocflowsAsReadResponse newInstance() {
              return (org.tempuri.MarkDocflowsAsReadResponseDocument.MarkDocflowsAsReadResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static org.tempuri.MarkDocflowsAsReadResponseDocument.MarkDocflowsAsReadResponse newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (org.tempuri.MarkDocflowsAsReadResponseDocument.MarkDocflowsAsReadResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.tempuri.MarkDocflowsAsReadResponseDocument newInstance() {
          return (org.tempuri.MarkDocflowsAsReadResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.tempuri.MarkDocflowsAsReadResponseDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.tempuri.MarkDocflowsAsReadResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.tempuri.MarkDocflowsAsReadResponseDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.MarkDocflowsAsReadResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.tempuri.MarkDocflowsAsReadResponseDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.MarkDocflowsAsReadResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.tempuri.MarkDocflowsAsReadResponseDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.MarkDocflowsAsReadResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.tempuri.MarkDocflowsAsReadResponseDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.MarkDocflowsAsReadResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.tempuri.MarkDocflowsAsReadResponseDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.MarkDocflowsAsReadResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.tempuri.MarkDocflowsAsReadResponseDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.MarkDocflowsAsReadResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.tempuri.MarkDocflowsAsReadResponseDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.MarkDocflowsAsReadResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.tempuri.MarkDocflowsAsReadResponseDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.MarkDocflowsAsReadResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.tempuri.MarkDocflowsAsReadResponseDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.MarkDocflowsAsReadResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.tempuri.MarkDocflowsAsReadResponseDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.MarkDocflowsAsReadResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.tempuri.MarkDocflowsAsReadResponseDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.MarkDocflowsAsReadResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.tempuri.MarkDocflowsAsReadResponseDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.MarkDocflowsAsReadResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.tempuri.MarkDocflowsAsReadResponseDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.MarkDocflowsAsReadResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.tempuri.MarkDocflowsAsReadResponseDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.MarkDocflowsAsReadResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.MarkDocflowsAsReadResponseDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.MarkDocflowsAsReadResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.MarkDocflowsAsReadResponseDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.MarkDocflowsAsReadResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
