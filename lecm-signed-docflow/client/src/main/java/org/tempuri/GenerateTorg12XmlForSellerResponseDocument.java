/*
 * An XML document type.
 * Localname: GenerateTorg12XmlForSellerResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.GenerateTorg12XmlForSellerResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri;


/**
 * A document containing one GenerateTorg12XmlForSellerResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public interface GenerateTorg12XmlForSellerResponseDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(GenerateTorg12XmlForSellerResponseDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("generatetorg12xmlforsellerresponse2d44doctype");
    
    /**
     * Gets the "GenerateTorg12XmlForSellerResponse" element
     */
    org.tempuri.GenerateTorg12XmlForSellerResponseDocument.GenerateTorg12XmlForSellerResponse getGenerateTorg12XmlForSellerResponse();
    
    /**
     * Sets the "GenerateTorg12XmlForSellerResponse" element
     */
    void setGenerateTorg12XmlForSellerResponse(org.tempuri.GenerateTorg12XmlForSellerResponseDocument.GenerateTorg12XmlForSellerResponse generateTorg12XmlForSellerResponse);
    
    /**
     * Appends and returns a new empty "GenerateTorg12XmlForSellerResponse" element
     */
    org.tempuri.GenerateTorg12XmlForSellerResponseDocument.GenerateTorg12XmlForSellerResponse addNewGenerateTorg12XmlForSellerResponse();
    
    /**
     * An XML GenerateTorg12XmlForSellerResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public interface GenerateTorg12XmlForSellerResponse extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(GenerateTorg12XmlForSellerResponse.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("generatetorg12xmlforsellerresponse1b50elemtype");
        
        /**
         * Gets the "GenerateTorg12XmlForSellerResult" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getGenerateTorg12XmlForSellerResult();
        
        /**
         * Tests for nil "GenerateTorg12XmlForSellerResult" element
         */
        boolean isNilGenerateTorg12XmlForSellerResult();
        
        /**
         * True if has "GenerateTorg12XmlForSellerResult" element
         */
        boolean isSetGenerateTorg12XmlForSellerResult();
        
        /**
         * Sets the "GenerateTorg12XmlForSellerResult" element
         */
        void setGenerateTorg12XmlForSellerResult(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse generateTorg12XmlForSellerResult);
        
        /**
         * Appends and returns a new empty "GenerateTorg12XmlForSellerResult" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewGenerateTorg12XmlForSellerResult();
        
        /**
         * Nils the "GenerateTorg12XmlForSellerResult" element
         */
        void setNilGenerateTorg12XmlForSellerResult();
        
        /**
         * Unsets the "GenerateTorg12XmlForSellerResult" element
         */
        void unsetGenerateTorg12XmlForSellerResult();
        
        /**
         * Gets the "generatedDoc" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.GeneratedDocument1 getGeneratedDoc();
        
        /**
         * Tests for nil "generatedDoc" element
         */
        boolean isNilGeneratedDoc();
        
        /**
         * True if has "generatedDoc" element
         */
        boolean isSetGeneratedDoc();
        
        /**
         * Sets the "generatedDoc" element
         */
        void setGeneratedDoc(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.GeneratedDocument1 generatedDoc);
        
        /**
         * Appends and returns a new empty "generatedDoc" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.GeneratedDocument1 addNewGeneratedDoc();
        
        /**
         * Nils the "generatedDoc" element
         */
        void setNilGeneratedDoc();
        
        /**
         * Unsets the "generatedDoc" element
         */
        void unsetGeneratedDoc();
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static org.tempuri.GenerateTorg12XmlForSellerResponseDocument.GenerateTorg12XmlForSellerResponse newInstance() {
              return (org.tempuri.GenerateTorg12XmlForSellerResponseDocument.GenerateTorg12XmlForSellerResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static org.tempuri.GenerateTorg12XmlForSellerResponseDocument.GenerateTorg12XmlForSellerResponse newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (org.tempuri.GenerateTorg12XmlForSellerResponseDocument.GenerateTorg12XmlForSellerResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.tempuri.GenerateTorg12XmlForSellerResponseDocument newInstance() {
          return (org.tempuri.GenerateTorg12XmlForSellerResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.tempuri.GenerateTorg12XmlForSellerResponseDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.tempuri.GenerateTorg12XmlForSellerResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.tempuri.GenerateTorg12XmlForSellerResponseDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateTorg12XmlForSellerResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.tempuri.GenerateTorg12XmlForSellerResponseDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateTorg12XmlForSellerResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.tempuri.GenerateTorg12XmlForSellerResponseDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateTorg12XmlForSellerResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.tempuri.GenerateTorg12XmlForSellerResponseDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateTorg12XmlForSellerResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.tempuri.GenerateTorg12XmlForSellerResponseDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateTorg12XmlForSellerResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.tempuri.GenerateTorg12XmlForSellerResponseDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateTorg12XmlForSellerResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.tempuri.GenerateTorg12XmlForSellerResponseDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateTorg12XmlForSellerResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.tempuri.GenerateTorg12XmlForSellerResponseDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateTorg12XmlForSellerResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.tempuri.GenerateTorg12XmlForSellerResponseDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateTorg12XmlForSellerResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.tempuri.GenerateTorg12XmlForSellerResponseDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateTorg12XmlForSellerResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.tempuri.GenerateTorg12XmlForSellerResponseDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateTorg12XmlForSellerResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.tempuri.GenerateTorg12XmlForSellerResponseDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateTorg12XmlForSellerResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.tempuri.GenerateTorg12XmlForSellerResponseDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateTorg12XmlForSellerResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.tempuri.GenerateTorg12XmlForSellerResponseDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateTorg12XmlForSellerResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.GenerateTorg12XmlForSellerResponseDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.GenerateTorg12XmlForSellerResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.GenerateTorg12XmlForSellerResponseDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.GenerateTorg12XmlForSellerResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
