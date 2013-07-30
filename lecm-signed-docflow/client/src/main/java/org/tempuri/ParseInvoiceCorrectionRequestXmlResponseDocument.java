/*
 * An XML document type.
 * Localname: ParseInvoiceCorrectionRequestXmlResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri;


/**
 * A document containing one ParseInvoiceCorrectionRequestXmlResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public interface ParseInvoiceCorrectionRequestXmlResponseDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(ParseInvoiceCorrectionRequestXmlResponseDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("parseinvoicecorrectionrequestxmlresponseebcddoctype");
    
    /**
     * Gets the "ParseInvoiceCorrectionRequestXmlResponse" element
     */
    org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument.ParseInvoiceCorrectionRequestXmlResponse getParseInvoiceCorrectionRequestXmlResponse();
    
    /**
     * Sets the "ParseInvoiceCorrectionRequestXmlResponse" element
     */
    void setParseInvoiceCorrectionRequestXmlResponse(org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument.ParseInvoiceCorrectionRequestXmlResponse parseInvoiceCorrectionRequestXmlResponse);
    
    /**
     * Appends and returns a new empty "ParseInvoiceCorrectionRequestXmlResponse" element
     */
    org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument.ParseInvoiceCorrectionRequestXmlResponse addNewParseInvoiceCorrectionRequestXmlResponse();
    
    /**
     * An XML ParseInvoiceCorrectionRequestXmlResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public interface ParseInvoiceCorrectionRequestXmlResponse extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(ParseInvoiceCorrectionRequestXmlResponse.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("parseinvoicecorrectionrequestxmlresponse71f0elemtype");
        
        /**
         * Gets the "ParseInvoiceCorrectionRequestXmlResult" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getParseInvoiceCorrectionRequestXmlResult();
        
        /**
         * Tests for nil "ParseInvoiceCorrectionRequestXmlResult" element
         */
        boolean isNilParseInvoiceCorrectionRequestXmlResult();
        
        /**
         * True if has "ParseInvoiceCorrectionRequestXmlResult" element
         */
        boolean isSetParseInvoiceCorrectionRequestXmlResult();
        
        /**
         * Sets the "ParseInvoiceCorrectionRequestXmlResult" element
         */
        void setParseInvoiceCorrectionRequestXmlResult(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse parseInvoiceCorrectionRequestXmlResult);
        
        /**
         * Appends and returns a new empty "ParseInvoiceCorrectionRequestXmlResult" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewParseInvoiceCorrectionRequestXmlResult();
        
        /**
         * Nils the "ParseInvoiceCorrectionRequestXmlResult" element
         */
        void setNilParseInvoiceCorrectionRequestXmlResult();
        
        /**
         * Unsets the "ParseInvoiceCorrectionRequestXmlResult" element
         */
        void unsetParseInvoiceCorrectionRequestXmlResult();
        
        /**
         * Gets the "requestText" element
         */
        java.lang.String getRequestText();
        
        /**
         * Gets (as xml) the "requestText" element
         */
        org.apache.xmlbeans.XmlString xgetRequestText();
        
        /**
         * Tests for nil "requestText" element
         */
        boolean isNilRequestText();
        
        /**
         * True if has "requestText" element
         */
        boolean isSetRequestText();
        
        /**
         * Sets the "requestText" element
         */
        void setRequestText(java.lang.String requestText);
        
        /**
         * Sets (as xml) the "requestText" element
         */
        void xsetRequestText(org.apache.xmlbeans.XmlString requestText);
        
        /**
         * Nils the "requestText" element
         */
        void setNilRequestText();
        
        /**
         * Unsets the "requestText" element
         */
        void unsetRequestText();
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument.ParseInvoiceCorrectionRequestXmlResponse newInstance() {
              return (org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument.ParseInvoiceCorrectionRequestXmlResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument.ParseInvoiceCorrectionRequestXmlResponse newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument.ParseInvoiceCorrectionRequestXmlResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument newInstance() {
          return (org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.ParseInvoiceCorrectionRequestXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
