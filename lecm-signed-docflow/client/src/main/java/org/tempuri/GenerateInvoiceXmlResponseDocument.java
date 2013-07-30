/*
 * An XML document type.
 * Localname: GenerateInvoiceXmlResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.GenerateInvoiceXmlResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri;


/**
 * A document containing one GenerateInvoiceXmlResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public interface GenerateInvoiceXmlResponseDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(GenerateInvoiceXmlResponseDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("generateinvoicexmlresponse9aecdoctype");
    
    /**
     * Gets the "GenerateInvoiceXmlResponse" element
     */
    org.tempuri.GenerateInvoiceXmlResponseDocument.GenerateInvoiceXmlResponse getGenerateInvoiceXmlResponse();
    
    /**
     * Sets the "GenerateInvoiceXmlResponse" element
     */
    void setGenerateInvoiceXmlResponse(org.tempuri.GenerateInvoiceXmlResponseDocument.GenerateInvoiceXmlResponse generateInvoiceXmlResponse);
    
    /**
     * Appends and returns a new empty "GenerateInvoiceXmlResponse" element
     */
    org.tempuri.GenerateInvoiceXmlResponseDocument.GenerateInvoiceXmlResponse addNewGenerateInvoiceXmlResponse();
    
    /**
     * An XML GenerateInvoiceXmlResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public interface GenerateInvoiceXmlResponse extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(GenerateInvoiceXmlResponse.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("generateinvoicexmlresponseb950elemtype");
        
        /**
         * Gets the "GenerateInvoiceXmlResult" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getGenerateInvoiceXmlResult();
        
        /**
         * Tests for nil "GenerateInvoiceXmlResult" element
         */
        boolean isNilGenerateInvoiceXmlResult();
        
        /**
         * True if has "GenerateInvoiceXmlResult" element
         */
        boolean isSetGenerateInvoiceXmlResult();
        
        /**
         * Sets the "GenerateInvoiceXmlResult" element
         */
        void setGenerateInvoiceXmlResult(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse generateInvoiceXmlResult);
        
        /**
         * Appends and returns a new empty "GenerateInvoiceXmlResult" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewGenerateInvoiceXmlResult();
        
        /**
         * Nils the "GenerateInvoiceXmlResult" element
         */
        void setNilGenerateInvoiceXmlResult();
        
        /**
         * Unsets the "GenerateInvoiceXmlResult" element
         */
        void unsetGenerateInvoiceXmlResult();
        
        /**
         * Gets the "generatedDoc" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend getGeneratedDoc();
        
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
        void setGeneratedDoc(org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend generatedDoc);
        
        /**
         * Appends and returns a new empty "generatedDoc" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend addNewGeneratedDoc();
        
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
            public static org.tempuri.GenerateInvoiceXmlResponseDocument.GenerateInvoiceXmlResponse newInstance() {
              return (org.tempuri.GenerateInvoiceXmlResponseDocument.GenerateInvoiceXmlResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static org.tempuri.GenerateInvoiceXmlResponseDocument.GenerateInvoiceXmlResponse newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (org.tempuri.GenerateInvoiceXmlResponseDocument.GenerateInvoiceXmlResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.tempuri.GenerateInvoiceXmlResponseDocument newInstance() {
          return (org.tempuri.GenerateInvoiceXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.tempuri.GenerateInvoiceXmlResponseDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.tempuri.GenerateInvoiceXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.tempuri.GenerateInvoiceXmlResponseDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateInvoiceXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.tempuri.GenerateInvoiceXmlResponseDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateInvoiceXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.tempuri.GenerateInvoiceXmlResponseDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateInvoiceXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.tempuri.GenerateInvoiceXmlResponseDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateInvoiceXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.tempuri.GenerateInvoiceXmlResponseDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateInvoiceXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.tempuri.GenerateInvoiceXmlResponseDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateInvoiceXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.tempuri.GenerateInvoiceXmlResponseDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateInvoiceXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.tempuri.GenerateInvoiceXmlResponseDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateInvoiceXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.tempuri.GenerateInvoiceXmlResponseDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateInvoiceXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.tempuri.GenerateInvoiceXmlResponseDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateInvoiceXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.tempuri.GenerateInvoiceXmlResponseDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateInvoiceXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.tempuri.GenerateInvoiceXmlResponseDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateInvoiceXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.tempuri.GenerateInvoiceXmlResponseDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateInvoiceXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.tempuri.GenerateInvoiceXmlResponseDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateInvoiceXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.GenerateInvoiceXmlResponseDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.GenerateInvoiceXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.GenerateInvoiceXmlResponseDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.GenerateInvoiceXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
