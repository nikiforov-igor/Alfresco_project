/*
 * An XML document type.
 * Localname: GenerateRegisterRequestResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.GenerateRegisterRequestResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri;


/**
 * A document containing one GenerateRegisterRequestResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public interface GenerateRegisterRequestResponseDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(GenerateRegisterRequestResponseDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("generateregisterrequestresponse7012doctype");
    
    /**
     * Gets the "GenerateRegisterRequestResponse" element
     */
    org.tempuri.GenerateRegisterRequestResponseDocument.GenerateRegisterRequestResponse getGenerateRegisterRequestResponse();
    
    /**
     * Sets the "GenerateRegisterRequestResponse" element
     */
    void setGenerateRegisterRequestResponse(org.tempuri.GenerateRegisterRequestResponseDocument.GenerateRegisterRequestResponse generateRegisterRequestResponse);
    
    /**
     * Appends and returns a new empty "GenerateRegisterRequestResponse" element
     */
    org.tempuri.GenerateRegisterRequestResponseDocument.GenerateRegisterRequestResponse addNewGenerateRegisterRequestResponse();
    
    /**
     * An XML GenerateRegisterRequestResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public interface GenerateRegisterRequestResponse extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(GenerateRegisterRequestResponse.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("generateregisterrequestresponse62f6elemtype");
        
        /**
         * Gets the "GenerateRegisterRequestResult" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getGenerateRegisterRequestResult();
        
        /**
         * Tests for nil "GenerateRegisterRequestResult" element
         */
        boolean isNilGenerateRegisterRequestResult();
        
        /**
         * True if has "GenerateRegisterRequestResult" element
         */
        boolean isSetGenerateRegisterRequestResult();
        
        /**
         * Sets the "GenerateRegisterRequestResult" element
         */
        void setGenerateRegisterRequestResult(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse generateRegisterRequestResult);
        
        /**
         * Appends and returns a new empty "GenerateRegisterRequestResult" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewGenerateRegisterRequestResult();
        
        /**
         * Nils the "GenerateRegisterRequestResult" element
         */
        void setNilGenerateRegisterRequestResult();
        
        /**
         * Unsets the "GenerateRegisterRequestResult" element
         */
        void unsetGenerateRegisterRequestResult();
        
        /**
         * Gets the "content" element
         */
        byte[] getContent();
        
        /**
         * Gets (as xml) the "content" element
         */
        org.apache.xmlbeans.XmlBase64Binary xgetContent();
        
        /**
         * Tests for nil "content" element
         */
        boolean isNilContent();
        
        /**
         * True if has "content" element
         */
        boolean isSetContent();
        
        /**
         * Sets the "content" element
         */
        void setContent(byte[] content);
        
        /**
         * Sets (as xml) the "content" element
         */
        void xsetContent(org.apache.xmlbeans.XmlBase64Binary content);
        
        /**
         * Nils the "content" element
         */
        void setNilContent();
        
        /**
         * Unsets the "content" element
         */
        void unsetContent();
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static org.tempuri.GenerateRegisterRequestResponseDocument.GenerateRegisterRequestResponse newInstance() {
              return (org.tempuri.GenerateRegisterRequestResponseDocument.GenerateRegisterRequestResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static org.tempuri.GenerateRegisterRequestResponseDocument.GenerateRegisterRequestResponse newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (org.tempuri.GenerateRegisterRequestResponseDocument.GenerateRegisterRequestResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.tempuri.GenerateRegisterRequestResponseDocument newInstance() {
          return (org.tempuri.GenerateRegisterRequestResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.tempuri.GenerateRegisterRequestResponseDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.tempuri.GenerateRegisterRequestResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.tempuri.GenerateRegisterRequestResponseDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateRegisterRequestResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.tempuri.GenerateRegisterRequestResponseDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateRegisterRequestResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.tempuri.GenerateRegisterRequestResponseDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateRegisterRequestResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.tempuri.GenerateRegisterRequestResponseDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateRegisterRequestResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.tempuri.GenerateRegisterRequestResponseDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateRegisterRequestResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.tempuri.GenerateRegisterRequestResponseDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateRegisterRequestResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.tempuri.GenerateRegisterRequestResponseDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateRegisterRequestResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.tempuri.GenerateRegisterRequestResponseDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateRegisterRequestResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.tempuri.GenerateRegisterRequestResponseDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateRegisterRequestResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.tempuri.GenerateRegisterRequestResponseDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateRegisterRequestResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.tempuri.GenerateRegisterRequestResponseDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateRegisterRequestResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.tempuri.GenerateRegisterRequestResponseDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateRegisterRequestResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.tempuri.GenerateRegisterRequestResponseDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateRegisterRequestResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.tempuri.GenerateRegisterRequestResponseDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateRegisterRequestResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.GenerateRegisterRequestResponseDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.GenerateRegisterRequestResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.GenerateRegisterRequestResponseDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.GenerateRegisterRequestResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
