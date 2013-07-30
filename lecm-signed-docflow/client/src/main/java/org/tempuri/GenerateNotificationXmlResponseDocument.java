/*
 * An XML document type.
 * Localname: GenerateNotificationXmlResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.GenerateNotificationXmlResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri;


/**
 * A document containing one GenerateNotificationXmlResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public interface GenerateNotificationXmlResponseDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(GenerateNotificationXmlResponseDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("generatenotificationxmlresponse44d2doctype");
    
    /**
     * Gets the "GenerateNotificationXmlResponse" element
     */
    org.tempuri.GenerateNotificationXmlResponseDocument.GenerateNotificationXmlResponse getGenerateNotificationXmlResponse();
    
    /**
     * Sets the "GenerateNotificationXmlResponse" element
     */
    void setGenerateNotificationXmlResponse(org.tempuri.GenerateNotificationXmlResponseDocument.GenerateNotificationXmlResponse generateNotificationXmlResponse);
    
    /**
     * Appends and returns a new empty "GenerateNotificationXmlResponse" element
     */
    org.tempuri.GenerateNotificationXmlResponseDocument.GenerateNotificationXmlResponse addNewGenerateNotificationXmlResponse();
    
    /**
     * An XML GenerateNotificationXmlResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public interface GenerateNotificationXmlResponse extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(GenerateNotificationXmlResponse.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("generatenotificationxmlresponsedc76elemtype");
        
        /**
         * Gets the "GenerateNotificationXmlResult" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getGenerateNotificationXmlResult();
        
        /**
         * Tests for nil "GenerateNotificationXmlResult" element
         */
        boolean isNilGenerateNotificationXmlResult();
        
        /**
         * True if has "GenerateNotificationXmlResult" element
         */
        boolean isSetGenerateNotificationXmlResult();
        
        /**
         * Sets the "GenerateNotificationXmlResult" element
         */
        void setGenerateNotificationXmlResult(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse generateNotificationXmlResult);
        
        /**
         * Appends and returns a new empty "GenerateNotificationXmlResult" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewGenerateNotificationXmlResult();
        
        /**
         * Nils the "GenerateNotificationXmlResult" element
         */
        void setNilGenerateNotificationXmlResult();
        
        /**
         * Unsets the "GenerateNotificationXmlResult" element
         */
        void unsetGenerateNotificationXmlResult();
        
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
            public static org.tempuri.GenerateNotificationXmlResponseDocument.GenerateNotificationXmlResponse newInstance() {
              return (org.tempuri.GenerateNotificationXmlResponseDocument.GenerateNotificationXmlResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static org.tempuri.GenerateNotificationXmlResponseDocument.GenerateNotificationXmlResponse newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (org.tempuri.GenerateNotificationXmlResponseDocument.GenerateNotificationXmlResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.tempuri.GenerateNotificationXmlResponseDocument newInstance() {
          return (org.tempuri.GenerateNotificationXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.tempuri.GenerateNotificationXmlResponseDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.tempuri.GenerateNotificationXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.tempuri.GenerateNotificationXmlResponseDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateNotificationXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.tempuri.GenerateNotificationXmlResponseDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateNotificationXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.tempuri.GenerateNotificationXmlResponseDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateNotificationXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.tempuri.GenerateNotificationXmlResponseDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateNotificationXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.tempuri.GenerateNotificationXmlResponseDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateNotificationXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.tempuri.GenerateNotificationXmlResponseDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateNotificationXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.tempuri.GenerateNotificationXmlResponseDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateNotificationXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.tempuri.GenerateNotificationXmlResponseDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateNotificationXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.tempuri.GenerateNotificationXmlResponseDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateNotificationXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.tempuri.GenerateNotificationXmlResponseDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateNotificationXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.tempuri.GenerateNotificationXmlResponseDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateNotificationXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.tempuri.GenerateNotificationXmlResponseDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateNotificationXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.tempuri.GenerateNotificationXmlResponseDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateNotificationXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.tempuri.GenerateNotificationXmlResponseDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateNotificationXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.GenerateNotificationXmlResponseDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.GenerateNotificationXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.GenerateNotificationXmlResponseDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.GenerateNotificationXmlResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
