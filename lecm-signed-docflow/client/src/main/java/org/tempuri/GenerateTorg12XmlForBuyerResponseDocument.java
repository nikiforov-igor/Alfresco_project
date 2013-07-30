/*
 * An XML document type.
 * Localname: GenerateTorg12XmlForBuyerResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.GenerateTorg12XmlForBuyerResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri;


/**
 * A document containing one GenerateTorg12XmlForBuyerResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public interface GenerateTorg12XmlForBuyerResponseDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(GenerateTorg12XmlForBuyerResponseDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("generatetorg12xmlforbuyerresponsefdf6doctype");
    
    /**
     * Gets the "GenerateTorg12XmlForBuyerResponse" element
     */
    org.tempuri.GenerateTorg12XmlForBuyerResponseDocument.GenerateTorg12XmlForBuyerResponse getGenerateTorg12XmlForBuyerResponse();
    
    /**
     * Sets the "GenerateTorg12XmlForBuyerResponse" element
     */
    void setGenerateTorg12XmlForBuyerResponse(org.tempuri.GenerateTorg12XmlForBuyerResponseDocument.GenerateTorg12XmlForBuyerResponse generateTorg12XmlForBuyerResponse);
    
    /**
     * Appends and returns a new empty "GenerateTorg12XmlForBuyerResponse" element
     */
    org.tempuri.GenerateTorg12XmlForBuyerResponseDocument.GenerateTorg12XmlForBuyerResponse addNewGenerateTorg12XmlForBuyerResponse();
    
    /**
     * An XML GenerateTorg12XmlForBuyerResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public interface GenerateTorg12XmlForBuyerResponse extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(GenerateTorg12XmlForBuyerResponse.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("generatetorg12xmlforbuyerresponsef77eelemtype");
        
        /**
         * Gets the "GenerateTorg12XmlForBuyerResult" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getGenerateTorg12XmlForBuyerResult();
        
        /**
         * Tests for nil "GenerateTorg12XmlForBuyerResult" element
         */
        boolean isNilGenerateTorg12XmlForBuyerResult();
        
        /**
         * True if has "GenerateTorg12XmlForBuyerResult" element
         */
        boolean isSetGenerateTorg12XmlForBuyerResult();
        
        /**
         * Sets the "GenerateTorg12XmlForBuyerResult" element
         */
        void setGenerateTorg12XmlForBuyerResult(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse generateTorg12XmlForBuyerResult);
        
        /**
         * Appends and returns a new empty "GenerateTorg12XmlForBuyerResult" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewGenerateTorg12XmlForBuyerResult();
        
        /**
         * Nils the "GenerateTorg12XmlForBuyerResult" element
         */
        void setNilGenerateTorg12XmlForBuyerResult();
        
        /**
         * Unsets the "GenerateTorg12XmlForBuyerResult" element
         */
        void unsetGenerateTorg12XmlForBuyerResult();
        
        /**
         * Gets the "generatedDocument" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.GeneratedDocument1 getGeneratedDocument();
        
        /**
         * Tests for nil "generatedDocument" element
         */
        boolean isNilGeneratedDocument();
        
        /**
         * True if has "generatedDocument" element
         */
        boolean isSetGeneratedDocument();
        
        /**
         * Sets the "generatedDocument" element
         */
        void setGeneratedDocument(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.GeneratedDocument1 generatedDocument);
        
        /**
         * Appends and returns a new empty "generatedDocument" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.GeneratedDocument1 addNewGeneratedDocument();
        
        /**
         * Nils the "generatedDocument" element
         */
        void setNilGeneratedDocument();
        
        /**
         * Unsets the "generatedDocument" element
         */
        void unsetGeneratedDocument();
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static org.tempuri.GenerateTorg12XmlForBuyerResponseDocument.GenerateTorg12XmlForBuyerResponse newInstance() {
              return (org.tempuri.GenerateTorg12XmlForBuyerResponseDocument.GenerateTorg12XmlForBuyerResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static org.tempuri.GenerateTorg12XmlForBuyerResponseDocument.GenerateTorg12XmlForBuyerResponse newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (org.tempuri.GenerateTorg12XmlForBuyerResponseDocument.GenerateTorg12XmlForBuyerResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.tempuri.GenerateTorg12XmlForBuyerResponseDocument newInstance() {
          return (org.tempuri.GenerateTorg12XmlForBuyerResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.tempuri.GenerateTorg12XmlForBuyerResponseDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.tempuri.GenerateTorg12XmlForBuyerResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.tempuri.GenerateTorg12XmlForBuyerResponseDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateTorg12XmlForBuyerResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.tempuri.GenerateTorg12XmlForBuyerResponseDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateTorg12XmlForBuyerResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.tempuri.GenerateTorg12XmlForBuyerResponseDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateTorg12XmlForBuyerResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.tempuri.GenerateTorg12XmlForBuyerResponseDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateTorg12XmlForBuyerResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.tempuri.GenerateTorg12XmlForBuyerResponseDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateTorg12XmlForBuyerResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.tempuri.GenerateTorg12XmlForBuyerResponseDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateTorg12XmlForBuyerResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.tempuri.GenerateTorg12XmlForBuyerResponseDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateTorg12XmlForBuyerResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.tempuri.GenerateTorg12XmlForBuyerResponseDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateTorg12XmlForBuyerResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.tempuri.GenerateTorg12XmlForBuyerResponseDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateTorg12XmlForBuyerResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.tempuri.GenerateTorg12XmlForBuyerResponseDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateTorg12XmlForBuyerResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.tempuri.GenerateTorg12XmlForBuyerResponseDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateTorg12XmlForBuyerResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.tempuri.GenerateTorg12XmlForBuyerResponseDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateTorg12XmlForBuyerResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.tempuri.GenerateTorg12XmlForBuyerResponseDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateTorg12XmlForBuyerResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.tempuri.GenerateTorg12XmlForBuyerResponseDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateTorg12XmlForBuyerResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.GenerateTorg12XmlForBuyerResponseDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.GenerateTorg12XmlForBuyerResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.GenerateTorg12XmlForBuyerResponseDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.GenerateTorg12XmlForBuyerResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
