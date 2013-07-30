/*
 * An XML document type.
 * Localname: AuthenticateByCertificate
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.AuthenticateByCertificateDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri;


/**
 * A document containing one AuthenticateByCertificate(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public interface AuthenticateByCertificateDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(AuthenticateByCertificateDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("authenticatebycertificate9c25doctype");
    
    /**
     * Gets the "AuthenticateByCertificate" element
     */
    org.tempuri.AuthenticateByCertificateDocument.AuthenticateByCertificate getAuthenticateByCertificate();
    
    /**
     * Sets the "AuthenticateByCertificate" element
     */
    void setAuthenticateByCertificate(org.tempuri.AuthenticateByCertificateDocument.AuthenticateByCertificate authenticateByCertificate);
    
    /**
     * Appends and returns a new empty "AuthenticateByCertificate" element
     */
    org.tempuri.AuthenticateByCertificateDocument.AuthenticateByCertificate addNewAuthenticateByCertificate();
    
    /**
     * An XML AuthenticateByCertificate(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public interface AuthenticateByCertificate extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(AuthenticateByCertificate.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("authenticatebycertificate805celemtype");
        
        /**
         * Gets the "operatorCode" element
         */
        java.lang.String getOperatorCode();
        
        /**
         * Gets (as xml) the "operatorCode" element
         */
        org.apache.xmlbeans.XmlString xgetOperatorCode();
        
        /**
         * Tests for nil "operatorCode" element
         */
        boolean isNilOperatorCode();
        
        /**
         * True if has "operatorCode" element
         */
        boolean isSetOperatorCode();
        
        /**
         * Sets the "operatorCode" element
         */
        void setOperatorCode(java.lang.String operatorCode);
        
        /**
         * Sets (as xml) the "operatorCode" element
         */
        void xsetOperatorCode(org.apache.xmlbeans.XmlString operatorCode);
        
        /**
         * Nils the "operatorCode" element
         */
        void setNilOperatorCode();
        
        /**
         * Unsets the "operatorCode" element
         */
        void unsetOperatorCode();
        
        /**
         * Gets the "signature" element
         */
        byte[] getSignature();
        
        /**
         * Gets (as xml) the "signature" element
         */
        org.apache.xmlbeans.XmlBase64Binary xgetSignature();
        
        /**
         * Tests for nil "signature" element
         */
        boolean isNilSignature();
        
        /**
         * True if has "signature" element
         */
        boolean isSetSignature();
        
        /**
         * Sets the "signature" element
         */
        void setSignature(byte[] signature);
        
        /**
         * Sets (as xml) the "signature" element
         */
        void xsetSignature(org.apache.xmlbeans.XmlBase64Binary signature);
        
        /**
         * Nils the "signature" element
         */
        void setNilSignature();
        
        /**
         * Unsets the "signature" element
         */
        void unsetSignature();
        
        /**
         * Gets the "signedData" element
         */
        java.lang.String getSignedData();
        
        /**
         * Gets (as xml) the "signedData" element
         */
        org.apache.xmlbeans.XmlString xgetSignedData();
        
        /**
         * Tests for nil "signedData" element
         */
        boolean isNilSignedData();
        
        /**
         * True if has "signedData" element
         */
        boolean isSetSignedData();
        
        /**
         * Sets the "signedData" element
         */
        void setSignedData(java.lang.String signedData);
        
        /**
         * Sets (as xml) the "signedData" element
         */
        void xsetSignedData(org.apache.xmlbeans.XmlString signedData);
        
        /**
         * Nils the "signedData" element
         */
        void setNilSignedData();
        
        /**
         * Unsets the "signedData" element
         */
        void unsetSignedData();
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static org.tempuri.AuthenticateByCertificateDocument.AuthenticateByCertificate newInstance() {
              return (org.tempuri.AuthenticateByCertificateDocument.AuthenticateByCertificate) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static org.tempuri.AuthenticateByCertificateDocument.AuthenticateByCertificate newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (org.tempuri.AuthenticateByCertificateDocument.AuthenticateByCertificate) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.tempuri.AuthenticateByCertificateDocument newInstance() {
          return (org.tempuri.AuthenticateByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.tempuri.AuthenticateByCertificateDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.tempuri.AuthenticateByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.tempuri.AuthenticateByCertificateDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.AuthenticateByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.tempuri.AuthenticateByCertificateDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.AuthenticateByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.tempuri.AuthenticateByCertificateDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.AuthenticateByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.tempuri.AuthenticateByCertificateDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.AuthenticateByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.tempuri.AuthenticateByCertificateDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.AuthenticateByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.tempuri.AuthenticateByCertificateDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.AuthenticateByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.tempuri.AuthenticateByCertificateDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.AuthenticateByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.tempuri.AuthenticateByCertificateDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.AuthenticateByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.tempuri.AuthenticateByCertificateDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.AuthenticateByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.tempuri.AuthenticateByCertificateDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.AuthenticateByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.tempuri.AuthenticateByCertificateDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.AuthenticateByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.tempuri.AuthenticateByCertificateDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.AuthenticateByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.tempuri.AuthenticateByCertificateDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.AuthenticateByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.tempuri.AuthenticateByCertificateDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.AuthenticateByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.AuthenticateByCertificateDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.AuthenticateByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.AuthenticateByCertificateDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.AuthenticateByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
