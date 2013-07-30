/*
 * An XML document type.
 * Localname: FinishSmsAuthentication
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.FinishSmsAuthenticationDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri;


/**
 * A document containing one FinishSmsAuthentication(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public interface FinishSmsAuthenticationDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(FinishSmsAuthenticationDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("finishsmsauthentication0658doctype");
    
    /**
     * Gets the "FinishSmsAuthentication" element
     */
    org.tempuri.FinishSmsAuthenticationDocument.FinishSmsAuthentication getFinishSmsAuthentication();
    
    /**
     * Sets the "FinishSmsAuthentication" element
     */
    void setFinishSmsAuthentication(org.tempuri.FinishSmsAuthenticationDocument.FinishSmsAuthentication finishSmsAuthentication);
    
    /**
     * Appends and returns a new empty "FinishSmsAuthentication" element
     */
    org.tempuri.FinishSmsAuthenticationDocument.FinishSmsAuthentication addNewFinishSmsAuthentication();
    
    /**
     * An XML FinishSmsAuthentication(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public interface FinishSmsAuthentication extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(FinishSmsAuthentication.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("finishsmsauthentication9902elemtype");
        
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
         * Gets the "password" element
         */
        java.lang.String getPassword();
        
        /**
         * Gets (as xml) the "password" element
         */
        org.apache.xmlbeans.XmlString xgetPassword();
        
        /**
         * Tests for nil "password" element
         */
        boolean isNilPassword();
        
        /**
         * True if has "password" element
         */
        boolean isSetPassword();
        
        /**
         * Sets the "password" element
         */
        void setPassword(java.lang.String password);
        
        /**
         * Sets (as xml) the "password" element
         */
        void xsetPassword(org.apache.xmlbeans.XmlString password);
        
        /**
         * Nils the "password" element
         */
        void setNilPassword();
        
        /**
         * Unsets the "password" element
         */
        void unsetPassword();
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static org.tempuri.FinishSmsAuthenticationDocument.FinishSmsAuthentication newInstance() {
              return (org.tempuri.FinishSmsAuthenticationDocument.FinishSmsAuthentication) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static org.tempuri.FinishSmsAuthenticationDocument.FinishSmsAuthentication newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (org.tempuri.FinishSmsAuthenticationDocument.FinishSmsAuthentication) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.tempuri.FinishSmsAuthenticationDocument newInstance() {
          return (org.tempuri.FinishSmsAuthenticationDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.tempuri.FinishSmsAuthenticationDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.tempuri.FinishSmsAuthenticationDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.tempuri.FinishSmsAuthenticationDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.FinishSmsAuthenticationDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.tempuri.FinishSmsAuthenticationDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.FinishSmsAuthenticationDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.tempuri.FinishSmsAuthenticationDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.FinishSmsAuthenticationDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.tempuri.FinishSmsAuthenticationDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.FinishSmsAuthenticationDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.tempuri.FinishSmsAuthenticationDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.FinishSmsAuthenticationDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.tempuri.FinishSmsAuthenticationDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.FinishSmsAuthenticationDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.tempuri.FinishSmsAuthenticationDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.FinishSmsAuthenticationDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.tempuri.FinishSmsAuthenticationDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.FinishSmsAuthenticationDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.tempuri.FinishSmsAuthenticationDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.FinishSmsAuthenticationDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.tempuri.FinishSmsAuthenticationDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.FinishSmsAuthenticationDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.tempuri.FinishSmsAuthenticationDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.FinishSmsAuthenticationDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.tempuri.FinishSmsAuthenticationDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.FinishSmsAuthenticationDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.tempuri.FinishSmsAuthenticationDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.FinishSmsAuthenticationDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.tempuri.FinishSmsAuthenticationDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.FinishSmsAuthenticationDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.FinishSmsAuthenticationDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.FinishSmsAuthenticationDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.FinishSmsAuthenticationDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.FinishSmsAuthenticationDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
