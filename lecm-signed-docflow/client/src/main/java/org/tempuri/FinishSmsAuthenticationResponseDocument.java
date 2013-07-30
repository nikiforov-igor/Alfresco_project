/*
 * An XML document type.
 * Localname: FinishSmsAuthenticationResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.FinishSmsAuthenticationResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri;


/**
 * A document containing one FinishSmsAuthenticationResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public interface FinishSmsAuthenticationResponseDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(FinishSmsAuthenticationResponseDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("finishsmsauthenticationresponse0f39doctype");
    
    /**
     * Gets the "FinishSmsAuthenticationResponse" element
     */
    org.tempuri.FinishSmsAuthenticationResponseDocument.FinishSmsAuthenticationResponse getFinishSmsAuthenticationResponse();
    
    /**
     * Sets the "FinishSmsAuthenticationResponse" element
     */
    void setFinishSmsAuthenticationResponse(org.tempuri.FinishSmsAuthenticationResponseDocument.FinishSmsAuthenticationResponse finishSmsAuthenticationResponse);
    
    /**
     * Appends and returns a new empty "FinishSmsAuthenticationResponse" element
     */
    org.tempuri.FinishSmsAuthenticationResponseDocument.FinishSmsAuthenticationResponse addNewFinishSmsAuthenticationResponse();
    
    /**
     * An XML FinishSmsAuthenticationResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public interface FinishSmsAuthenticationResponse extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(FinishSmsAuthenticationResponse.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("finishsmsauthenticationresponse1784elemtype");
        
        /**
         * Gets the "FinishSmsAuthenticationResult" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getFinishSmsAuthenticationResult();
        
        /**
         * Tests for nil "FinishSmsAuthenticationResult" element
         */
        boolean isNilFinishSmsAuthenticationResult();
        
        /**
         * True if has "FinishSmsAuthenticationResult" element
         */
        boolean isSetFinishSmsAuthenticationResult();
        
        /**
         * Sets the "FinishSmsAuthenticationResult" element
         */
        void setFinishSmsAuthenticationResult(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse finishSmsAuthenticationResult);
        
        /**
         * Appends and returns a new empty "FinishSmsAuthenticationResult" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewFinishSmsAuthenticationResult();
        
        /**
         * Nils the "FinishSmsAuthenticationResult" element
         */
        void setNilFinishSmsAuthenticationResult();
        
        /**
         * Unsets the "FinishSmsAuthenticationResult" element
         */
        void unsetFinishSmsAuthenticationResult();
        
        /**
         * Gets the "token" element
         */
        java.lang.String getToken();
        
        /**
         * Gets (as xml) the "token" element
         */
        org.apache.xmlbeans.XmlString xgetToken();
        
        /**
         * Tests for nil "token" element
         */
        boolean isNilToken();
        
        /**
         * True if has "token" element
         */
        boolean isSetToken();
        
        /**
         * Sets the "token" element
         */
        void setToken(java.lang.String token);
        
        /**
         * Sets (as xml) the "token" element
         */
        void xsetToken(org.apache.xmlbeans.XmlString token);
        
        /**
         * Nils the "token" element
         */
        void setNilToken();
        
        /**
         * Unsets the "token" element
         */
        void unsetToken();
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static org.tempuri.FinishSmsAuthenticationResponseDocument.FinishSmsAuthenticationResponse newInstance() {
              return (org.tempuri.FinishSmsAuthenticationResponseDocument.FinishSmsAuthenticationResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static org.tempuri.FinishSmsAuthenticationResponseDocument.FinishSmsAuthenticationResponse newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (org.tempuri.FinishSmsAuthenticationResponseDocument.FinishSmsAuthenticationResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.tempuri.FinishSmsAuthenticationResponseDocument newInstance() {
          return (org.tempuri.FinishSmsAuthenticationResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.tempuri.FinishSmsAuthenticationResponseDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.tempuri.FinishSmsAuthenticationResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.tempuri.FinishSmsAuthenticationResponseDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.FinishSmsAuthenticationResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.tempuri.FinishSmsAuthenticationResponseDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.FinishSmsAuthenticationResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.tempuri.FinishSmsAuthenticationResponseDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.FinishSmsAuthenticationResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.tempuri.FinishSmsAuthenticationResponseDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.FinishSmsAuthenticationResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.tempuri.FinishSmsAuthenticationResponseDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.FinishSmsAuthenticationResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.tempuri.FinishSmsAuthenticationResponseDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.FinishSmsAuthenticationResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.tempuri.FinishSmsAuthenticationResponseDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.FinishSmsAuthenticationResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.tempuri.FinishSmsAuthenticationResponseDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.FinishSmsAuthenticationResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.tempuri.FinishSmsAuthenticationResponseDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.FinishSmsAuthenticationResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.tempuri.FinishSmsAuthenticationResponseDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.FinishSmsAuthenticationResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.tempuri.FinishSmsAuthenticationResponseDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.FinishSmsAuthenticationResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.tempuri.FinishSmsAuthenticationResponseDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.FinishSmsAuthenticationResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.tempuri.FinishSmsAuthenticationResponseDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.FinishSmsAuthenticationResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.tempuri.FinishSmsAuthenticationResponseDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.FinishSmsAuthenticationResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.FinishSmsAuthenticationResponseDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.FinishSmsAuthenticationResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.FinishSmsAuthenticationResponseDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.FinishSmsAuthenticationResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
