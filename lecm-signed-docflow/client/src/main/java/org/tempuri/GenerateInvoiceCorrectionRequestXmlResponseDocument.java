/*
 * An XML document type.
 * Localname: GenerateInvoiceCorrectionRequestXmlResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri;


/**
 * A document containing one GenerateInvoiceCorrectionRequestXmlResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public interface GenerateInvoiceCorrectionRequestXmlResponseDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(GenerateInvoiceCorrectionRequestXmlResponseDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("generateinvoicecorrectionrequestxmlresponse49f9doctype");
    
    /**
     * Gets the "GenerateInvoiceCorrectionRequestXmlResponse" element
     */
    org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument.GenerateInvoiceCorrectionRequestXmlResponse getGenerateInvoiceCorrectionRequestXmlResponse();
    
    /**
     * Sets the "GenerateInvoiceCorrectionRequestXmlResponse" element
     */
    void setGenerateInvoiceCorrectionRequestXmlResponse(org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument.GenerateInvoiceCorrectionRequestXmlResponse generateInvoiceCorrectionRequestXmlResponse);
    
    /**
     * Appends and returns a new empty "GenerateInvoiceCorrectionRequestXmlResponse" element
     */
    org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument.GenerateInvoiceCorrectionRequestXmlResponse addNewGenerateInvoiceCorrectionRequestXmlResponse();
    
    /**
     * An XML GenerateInvoiceCorrectionRequestXmlResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public interface GenerateInvoiceCorrectionRequestXmlResponse extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(GenerateInvoiceCorrectionRequestXmlResponse.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("generateinvoicecorrectionrequestxmlresponse0b04elemtype");
        
        /**
         * Gets the "GenerateInvoiceCorrectionRequestXmlResult" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getGenerateInvoiceCorrectionRequestXmlResult();
        
        /**
         * Tests for nil "GenerateInvoiceCorrectionRequestXmlResult" element
         */
        boolean isNilGenerateInvoiceCorrectionRequestXmlResult();
        
        /**
         * True if has "GenerateInvoiceCorrectionRequestXmlResult" element
         */
        boolean isSetGenerateInvoiceCorrectionRequestXmlResult();
        
        /**
         * Sets the "GenerateInvoiceCorrectionRequestXmlResult" element
         */
        void setGenerateInvoiceCorrectionRequestXmlResult(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse generateInvoiceCorrectionRequestXmlResult);
        
        /**
         * Appends and returns a new empty "GenerateInvoiceCorrectionRequestXmlResult" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewGenerateInvoiceCorrectionRequestXmlResult();
        
        /**
         * Nils the "GenerateInvoiceCorrectionRequestXmlResult" element
         */
        void setNilGenerateInvoiceCorrectionRequestXmlResult();
        
        /**
         * Unsets the "GenerateInvoiceCorrectionRequestXmlResult" element
         */
        void unsetGenerateInvoiceCorrectionRequestXmlResult();
        
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
            public static org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument.GenerateInvoiceCorrectionRequestXmlResponse newInstance() {
              return (org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument.GenerateInvoiceCorrectionRequestXmlResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument.GenerateInvoiceCorrectionRequestXmlResponse newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument.GenerateInvoiceCorrectionRequestXmlResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument newInstance() {
          return (org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.GenerateInvoiceCorrectionRequestXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
