/*
 * An XML document type.
 * Localname: CheckIfPersonIsEmployeeByCertificate
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri;


/**
 * A document containing one CheckIfPersonIsEmployeeByCertificate(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public interface CheckIfPersonIsEmployeeByCertificateDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(CheckIfPersonIsEmployeeByCertificateDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("checkifpersonisemployeebycertificate1e9adoctype");
    
    /**
     * Gets the "CheckIfPersonIsEmployeeByCertificate" element
     */
    org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument.CheckIfPersonIsEmployeeByCertificate getCheckIfPersonIsEmployeeByCertificate();
    
    /**
     * Sets the "CheckIfPersonIsEmployeeByCertificate" element
     */
    void setCheckIfPersonIsEmployeeByCertificate(org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument.CheckIfPersonIsEmployeeByCertificate checkIfPersonIsEmployeeByCertificate);
    
    /**
     * Appends and returns a new empty "CheckIfPersonIsEmployeeByCertificate" element
     */
    org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument.CheckIfPersonIsEmployeeByCertificate addNewCheckIfPersonIsEmployeeByCertificate();
    
    /**
     * An XML CheckIfPersonIsEmployeeByCertificate(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public interface CheckIfPersonIsEmployeeByCertificate extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(CheckIfPersonIsEmployeeByCertificate.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("checkifpersonisemployeebycertificate8750elemtype");
        
        /**
         * Gets the "certificate" element
         */
        byte[] getCertificate();
        
        /**
         * Gets (as xml) the "certificate" element
         */
        org.apache.xmlbeans.XmlBase64Binary xgetCertificate();
        
        /**
         * Tests for nil "certificate" element
         */
        boolean isNilCertificate();
        
        /**
         * True if has "certificate" element
         */
        boolean isSetCertificate();
        
        /**
         * Sets the "certificate" element
         */
        void setCertificate(byte[] certificate);
        
        /**
         * Sets (as xml) the "certificate" element
         */
        void xsetCertificate(org.apache.xmlbeans.XmlBase64Binary certificate);
        
        /**
         * Nils the "certificate" element
         */
        void setNilCertificate();
        
        /**
         * Unsets the "certificate" element
         */
        void unsetCertificate();
        
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
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument.CheckIfPersonIsEmployeeByCertificate newInstance() {
              return (org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument.CheckIfPersonIsEmployeeByCertificate) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument.CheckIfPersonIsEmployeeByCertificate newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument.CheckIfPersonIsEmployeeByCertificate) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument newInstance() {
          return (org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.CheckIfPersonIsEmployeeByCertificateDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
