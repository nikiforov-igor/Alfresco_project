/*
 * An XML document type.
 * Localname: AuthenticateByCertificateResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.AuthenticateByCertificateResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri;


/**
 * A document containing one AuthenticateByCertificateResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public interface AuthenticateByCertificateResponseDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(AuthenticateByCertificateResponseDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("authenticatebycertificateresponse8806doctype");
    
    /**
     * Gets the "AuthenticateByCertificateResponse" element
     */
    org.tempuri.AuthenticateByCertificateResponseDocument.AuthenticateByCertificateResponse getAuthenticateByCertificateResponse();
    
    /**
     * Sets the "AuthenticateByCertificateResponse" element
     */
    void setAuthenticateByCertificateResponse(org.tempuri.AuthenticateByCertificateResponseDocument.AuthenticateByCertificateResponse authenticateByCertificateResponse);
    
    /**
     * Appends and returns a new empty "AuthenticateByCertificateResponse" element
     */
    org.tempuri.AuthenticateByCertificateResponseDocument.AuthenticateByCertificateResponse addNewAuthenticateByCertificateResponse();
    
    /**
     * An XML AuthenticateByCertificateResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public interface AuthenticateByCertificateResponse extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(AuthenticateByCertificateResponse.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("authenticatebycertificateresponse439eelemtype");
        
        /**
         * Gets the "AuthenticateByCertificateResult" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getAuthenticateByCertificateResult();
        
        /**
         * Tests for nil "AuthenticateByCertificateResult" element
         */
        boolean isNilAuthenticateByCertificateResult();
        
        /**
         * True if has "AuthenticateByCertificateResult" element
         */
        boolean isSetAuthenticateByCertificateResult();
        
        /**
         * Sets the "AuthenticateByCertificateResult" element
         */
        void setAuthenticateByCertificateResult(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse authenticateByCertificateResult);
        
        /**
         * Appends and returns a new empty "AuthenticateByCertificateResult" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewAuthenticateByCertificateResult();
        
        /**
         * Nils the "AuthenticateByCertificateResult" element
         */
        void setNilAuthenticateByCertificateResult();
        
        /**
         * Unsets the "AuthenticateByCertificateResult" element
         */
        void unsetAuthenticateByCertificateResult();
        
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
            public static org.tempuri.AuthenticateByCertificateResponseDocument.AuthenticateByCertificateResponse newInstance() {
              return (org.tempuri.AuthenticateByCertificateResponseDocument.AuthenticateByCertificateResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static org.tempuri.AuthenticateByCertificateResponseDocument.AuthenticateByCertificateResponse newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (org.tempuri.AuthenticateByCertificateResponseDocument.AuthenticateByCertificateResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.tempuri.AuthenticateByCertificateResponseDocument newInstance() {
          return (org.tempuri.AuthenticateByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.tempuri.AuthenticateByCertificateResponseDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.tempuri.AuthenticateByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.tempuri.AuthenticateByCertificateResponseDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.AuthenticateByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.tempuri.AuthenticateByCertificateResponseDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.AuthenticateByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.tempuri.AuthenticateByCertificateResponseDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.AuthenticateByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.tempuri.AuthenticateByCertificateResponseDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.AuthenticateByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.tempuri.AuthenticateByCertificateResponseDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.AuthenticateByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.tempuri.AuthenticateByCertificateResponseDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.AuthenticateByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.tempuri.AuthenticateByCertificateResponseDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.AuthenticateByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.tempuri.AuthenticateByCertificateResponseDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.AuthenticateByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.tempuri.AuthenticateByCertificateResponseDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.AuthenticateByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.tempuri.AuthenticateByCertificateResponseDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.AuthenticateByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.tempuri.AuthenticateByCertificateResponseDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.AuthenticateByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.tempuri.AuthenticateByCertificateResponseDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.AuthenticateByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.tempuri.AuthenticateByCertificateResponseDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.AuthenticateByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.tempuri.AuthenticateByCertificateResponseDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.AuthenticateByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.AuthenticateByCertificateResponseDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.AuthenticateByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.AuthenticateByCertificateResponseDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.AuthenticateByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
