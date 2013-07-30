/*
 * An XML document type.
 * Localname: RegisterUserByCertificateResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.RegisterUserByCertificateResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri;


/**
 * A document containing one RegisterUserByCertificateResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public interface RegisterUserByCertificateResponseDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(RegisterUserByCertificateResponseDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("registeruserbycertificateresponsebdeddoctype");
    
    /**
     * Gets the "RegisterUserByCertificateResponse" element
     */
    org.tempuri.RegisterUserByCertificateResponseDocument.RegisterUserByCertificateResponse getRegisterUserByCertificateResponse();
    
    /**
     * Sets the "RegisterUserByCertificateResponse" element
     */
    void setRegisterUserByCertificateResponse(org.tempuri.RegisterUserByCertificateResponseDocument.RegisterUserByCertificateResponse registerUserByCertificateResponse);
    
    /**
     * Appends and returns a new empty "RegisterUserByCertificateResponse" element
     */
    org.tempuri.RegisterUserByCertificateResponseDocument.RegisterUserByCertificateResponse addNewRegisterUserByCertificateResponse();
    
    /**
     * An XML RegisterUserByCertificateResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public interface RegisterUserByCertificateResponse extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(RegisterUserByCertificateResponse.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("registeruserbycertificateresponse07ecelemtype");
        
        /**
         * Gets the "RegisterUserByCertificateResult" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getRegisterUserByCertificateResult();
        
        /**
         * Tests for nil "RegisterUserByCertificateResult" element
         */
        boolean isNilRegisterUserByCertificateResult();
        
        /**
         * True if has "RegisterUserByCertificateResult" element
         */
        boolean isSetRegisterUserByCertificateResult();
        
        /**
         * Sets the "RegisterUserByCertificateResult" element
         */
        void setRegisterUserByCertificateResult(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse registerUserByCertificateResult);
        
        /**
         * Appends and returns a new empty "RegisterUserByCertificateResult" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewRegisterUserByCertificateResult();
        
        /**
         * Nils the "RegisterUserByCertificateResult" element
         */
        void setNilRegisterUserByCertificateResult();
        
        /**
         * Unsets the "RegisterUserByCertificateResult" element
         */
        void unsetRegisterUserByCertificateResult();
        
        /**
         * Gets the "organizationId" element
         */
        java.lang.String getOrganizationId();
        
        /**
         * Gets (as xml) the "organizationId" element
         */
        com.microsoft.schemas.x2003.x10.serialization.Guid xgetOrganizationId();
        
        /**
         * Tests for nil "organizationId" element
         */
        boolean isNilOrganizationId();
        
        /**
         * True if has "organizationId" element
         */
        boolean isSetOrganizationId();
        
        /**
         * Sets the "organizationId" element
         */
        void setOrganizationId(java.lang.String organizationId);
        
        /**
         * Sets (as xml) the "organizationId" element
         */
        void xsetOrganizationId(com.microsoft.schemas.x2003.x10.serialization.Guid organizationId);
        
        /**
         * Nils the "organizationId" element
         */
        void setNilOrganizationId();
        
        /**
         * Unsets the "organizationId" element
         */
        void unsetOrganizationId();
        
        /**
         * Gets the "organizationEdoId" element
         */
        java.lang.String getOrganizationEdoId();
        
        /**
         * Gets (as xml) the "organizationEdoId" element
         */
        org.apache.xmlbeans.XmlString xgetOrganizationEdoId();
        
        /**
         * Tests for nil "organizationEdoId" element
         */
        boolean isNilOrganizationEdoId();
        
        /**
         * True if has "organizationEdoId" element
         */
        boolean isSetOrganizationEdoId();
        
        /**
         * Sets the "organizationEdoId" element
         */
        void setOrganizationEdoId(java.lang.String organizationEdoId);
        
        /**
         * Sets (as xml) the "organizationEdoId" element
         */
        void xsetOrganizationEdoId(org.apache.xmlbeans.XmlString organizationEdoId);
        
        /**
         * Nils the "organizationEdoId" element
         */
        void setNilOrganizationEdoId();
        
        /**
         * Unsets the "organizationEdoId" element
         */
        void unsetOrganizationEdoId();
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static org.tempuri.RegisterUserByCertificateResponseDocument.RegisterUserByCertificateResponse newInstance() {
              return (org.tempuri.RegisterUserByCertificateResponseDocument.RegisterUserByCertificateResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static org.tempuri.RegisterUserByCertificateResponseDocument.RegisterUserByCertificateResponse newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (org.tempuri.RegisterUserByCertificateResponseDocument.RegisterUserByCertificateResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.tempuri.RegisterUserByCertificateResponseDocument newInstance() {
          return (org.tempuri.RegisterUserByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.tempuri.RegisterUserByCertificateResponseDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.tempuri.RegisterUserByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.tempuri.RegisterUserByCertificateResponseDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.RegisterUserByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.tempuri.RegisterUserByCertificateResponseDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.RegisterUserByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.tempuri.RegisterUserByCertificateResponseDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.RegisterUserByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.tempuri.RegisterUserByCertificateResponseDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.RegisterUserByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.tempuri.RegisterUserByCertificateResponseDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.RegisterUserByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.tempuri.RegisterUserByCertificateResponseDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.RegisterUserByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.tempuri.RegisterUserByCertificateResponseDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.RegisterUserByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.tempuri.RegisterUserByCertificateResponseDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.RegisterUserByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.tempuri.RegisterUserByCertificateResponseDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.RegisterUserByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.tempuri.RegisterUserByCertificateResponseDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.RegisterUserByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.tempuri.RegisterUserByCertificateResponseDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.RegisterUserByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.tempuri.RegisterUserByCertificateResponseDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.RegisterUserByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.tempuri.RegisterUserByCertificateResponseDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.RegisterUserByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.tempuri.RegisterUserByCertificateResponseDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.RegisterUserByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.RegisterUserByCertificateResponseDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.RegisterUserByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.RegisterUserByCertificateResponseDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.RegisterUserByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
