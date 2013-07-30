/*
 * An XML document type.
 * Localname: RegisterUserByCertificate
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.RegisterUserByCertificateDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri;


/**
 * A document containing one RegisterUserByCertificate(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public interface RegisterUserByCertificateDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(RegisterUserByCertificateDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("registeruserbycertificatea90cdoctype");
    
    /**
     * Gets the "RegisterUserByCertificate" element
     */
    org.tempuri.RegisterUserByCertificateDocument.RegisterUserByCertificate getRegisterUserByCertificate();
    
    /**
     * Sets the "RegisterUserByCertificate" element
     */
    void setRegisterUserByCertificate(org.tempuri.RegisterUserByCertificateDocument.RegisterUserByCertificate registerUserByCertificate);
    
    /**
     * Appends and returns a new empty "RegisterUserByCertificate" element
     */
    org.tempuri.RegisterUserByCertificateDocument.RegisterUserByCertificate addNewRegisterUserByCertificate();
    
    /**
     * An XML RegisterUserByCertificate(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public interface RegisterUserByCertificate extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(RegisterUserByCertificate.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("registeruserbycertificatec9aaelemtype");
        
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
         * Gets the "partnerKey" element
         */
        java.lang.String getPartnerKey();
        
        /**
         * Gets (as xml) the "partnerKey" element
         */
        com.microsoft.schemas.x2003.x10.serialization.Guid xgetPartnerKey();
        
        /**
         * True if has "partnerKey" element
         */
        boolean isSetPartnerKey();
        
        /**
         * Sets the "partnerKey" element
         */
        void setPartnerKey(java.lang.String partnerKey);
        
        /**
         * Sets (as xml) the "partnerKey" element
         */
        void xsetPartnerKey(com.microsoft.schemas.x2003.x10.serialization.Guid partnerKey);
        
        /**
         * Unsets the "partnerKey" element
         */
        void unsetPartnerKey();
        
        /**
         * Gets the "organizationInn" element
         */
        java.lang.String getOrganizationInn();
        
        /**
         * Gets (as xml) the "organizationInn" element
         */
        org.apache.xmlbeans.XmlString xgetOrganizationInn();
        
        /**
         * Tests for nil "organizationInn" element
         */
        boolean isNilOrganizationInn();
        
        /**
         * True if has "organizationInn" element
         */
        boolean isSetOrganizationInn();
        
        /**
         * Sets the "organizationInn" element
         */
        void setOrganizationInn(java.lang.String organizationInn);
        
        /**
         * Sets (as xml) the "organizationInn" element
         */
        void xsetOrganizationInn(org.apache.xmlbeans.XmlString organizationInn);
        
        /**
         * Nils the "organizationInn" element
         */
        void setNilOrganizationInn();
        
        /**
         * Unsets the "organizationInn" element
         */
        void unsetOrganizationInn();
        
        /**
         * Gets the "organizationKpp" element
         */
        java.lang.String getOrganizationKpp();
        
        /**
         * Gets (as xml) the "organizationKpp" element
         */
        org.apache.xmlbeans.XmlString xgetOrganizationKpp();
        
        /**
         * Tests for nil "organizationKpp" element
         */
        boolean isNilOrganizationKpp();
        
        /**
         * True if has "organizationKpp" element
         */
        boolean isSetOrganizationKpp();
        
        /**
         * Sets the "organizationKpp" element
         */
        void setOrganizationKpp(java.lang.String organizationKpp);
        
        /**
         * Sets (as xml) the "organizationKpp" element
         */
        void xsetOrganizationKpp(org.apache.xmlbeans.XmlString organizationKpp);
        
        /**
         * Nils the "organizationKpp" element
         */
        void setNilOrganizationKpp();
        
        /**
         * Unsets the "organizationKpp" element
         */
        void unsetOrganizationKpp();
        
        /**
         * Gets the "sign" element
         */
        byte[] getSign();
        
        /**
         * Gets (as xml) the "sign" element
         */
        org.apache.xmlbeans.XmlBase64Binary xgetSign();
        
        /**
         * Tests for nil "sign" element
         */
        boolean isNilSign();
        
        /**
         * True if has "sign" element
         */
        boolean isSetSign();
        
        /**
         * Sets the "sign" element
         */
        void setSign(byte[] sign);
        
        /**
         * Sets (as xml) the "sign" element
         */
        void xsetSign(org.apache.xmlbeans.XmlBase64Binary sign);
        
        /**
         * Nils the "sign" element
         */
        void setNilSign();
        
        /**
         * Unsets the "sign" element
         */
        void unsetSign();
        
        /**
         * Gets the "userId" element
         */
        java.lang.String getUserId();
        
        /**
         * Gets (as xml) the "userId" element
         */
        com.microsoft.schemas.x2003.x10.serialization.Guid xgetUserId();
        
        /**
         * True if has "userId" element
         */
        boolean isSetUserId();
        
        /**
         * Sets the "userId" element
         */
        void setUserId(java.lang.String userId);
        
        /**
         * Sets (as xml) the "userId" element
         */
        void xsetUserId(com.microsoft.schemas.x2003.x10.serialization.Guid userId);
        
        /**
         * Unsets the "userId" element
         */
        void unsetUserId();
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static org.tempuri.RegisterUserByCertificateDocument.RegisterUserByCertificate newInstance() {
              return (org.tempuri.RegisterUserByCertificateDocument.RegisterUserByCertificate) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static org.tempuri.RegisterUserByCertificateDocument.RegisterUserByCertificate newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (org.tempuri.RegisterUserByCertificateDocument.RegisterUserByCertificate) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.tempuri.RegisterUserByCertificateDocument newInstance() {
          return (org.tempuri.RegisterUserByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.tempuri.RegisterUserByCertificateDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.tempuri.RegisterUserByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.tempuri.RegisterUserByCertificateDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.RegisterUserByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.tempuri.RegisterUserByCertificateDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.RegisterUserByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.tempuri.RegisterUserByCertificateDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.RegisterUserByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.tempuri.RegisterUserByCertificateDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.RegisterUserByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.tempuri.RegisterUserByCertificateDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.RegisterUserByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.tempuri.RegisterUserByCertificateDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.RegisterUserByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.tempuri.RegisterUserByCertificateDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.RegisterUserByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.tempuri.RegisterUserByCertificateDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.RegisterUserByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.tempuri.RegisterUserByCertificateDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.RegisterUserByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.tempuri.RegisterUserByCertificateDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.RegisterUserByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.tempuri.RegisterUserByCertificateDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.RegisterUserByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.tempuri.RegisterUserByCertificateDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.RegisterUserByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.tempuri.RegisterUserByCertificateDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.RegisterUserByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.tempuri.RegisterUserByCertificateDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.RegisterUserByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.RegisterUserByCertificateDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.RegisterUserByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.RegisterUserByCertificateDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.RegisterUserByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
