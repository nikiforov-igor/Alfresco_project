/*
 * An XML document type.
 * Localname: CheckIfPersonIsEmployeeByCertificateResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri;


/**
 * A document containing one CheckIfPersonIsEmployeeByCertificateResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public interface CheckIfPersonIsEmployeeByCertificateResponseDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(CheckIfPersonIsEmployeeByCertificateResponseDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("checkifpersonisemployeebycertificateresponsec57bdoctype");
    
    /**
     * Gets the "CheckIfPersonIsEmployeeByCertificateResponse" element
     */
    org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument.CheckIfPersonIsEmployeeByCertificateResponse getCheckIfPersonIsEmployeeByCertificateResponse();
    
    /**
     * Sets the "CheckIfPersonIsEmployeeByCertificateResponse" element
     */
    void setCheckIfPersonIsEmployeeByCertificateResponse(org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument.CheckIfPersonIsEmployeeByCertificateResponse checkIfPersonIsEmployeeByCertificateResponse);
    
    /**
     * Appends and returns a new empty "CheckIfPersonIsEmployeeByCertificateResponse" element
     */
    org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument.CheckIfPersonIsEmployeeByCertificateResponse addNewCheckIfPersonIsEmployeeByCertificateResponse();
    
    /**
     * An XML CheckIfPersonIsEmployeeByCertificateResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public interface CheckIfPersonIsEmployeeByCertificateResponse extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(CheckIfPersonIsEmployeeByCertificateResponse.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("checkifpersonisemployeebycertificateresponse6630elemtype");
        
        /**
         * Gets the "CheckIfPersonIsEmployeeByCertificateResult" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getCheckIfPersonIsEmployeeByCertificateResult();
        
        /**
         * Tests for nil "CheckIfPersonIsEmployeeByCertificateResult" element
         */
        boolean isNilCheckIfPersonIsEmployeeByCertificateResult();
        
        /**
         * True if has "CheckIfPersonIsEmployeeByCertificateResult" element
         */
        boolean isSetCheckIfPersonIsEmployeeByCertificateResult();
        
        /**
         * Sets the "CheckIfPersonIsEmployeeByCertificateResult" element
         */
        void setCheckIfPersonIsEmployeeByCertificateResult(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse checkIfPersonIsEmployeeByCertificateResult);
        
        /**
         * Appends and returns a new empty "CheckIfPersonIsEmployeeByCertificateResult" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewCheckIfPersonIsEmployeeByCertificateResult();
        
        /**
         * Nils the "CheckIfPersonIsEmployeeByCertificateResult" element
         */
        void setNilCheckIfPersonIsEmployeeByCertificateResult();
        
        /**
         * Unsets the "CheckIfPersonIsEmployeeByCertificateResult" element
         */
        void unsetCheckIfPersonIsEmployeeByCertificateResult();
        
        /**
         * Gets the "isEmployee" element
         */
        boolean getIsEmployee();
        
        /**
         * Gets (as xml) the "isEmployee" element
         */
        org.apache.xmlbeans.XmlBoolean xgetIsEmployee();
        
        /**
         * True if has "isEmployee" element
         */
        boolean isSetIsEmployee();
        
        /**
         * Sets the "isEmployee" element
         */
        void setIsEmployee(boolean isEmployee);
        
        /**
         * Sets (as xml) the "isEmployee" element
         */
        void xsetIsEmployee(org.apache.xmlbeans.XmlBoolean isEmployee);
        
        /**
         * Unsets the "isEmployee" element
         */
        void unsetIsEmployee();
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument.CheckIfPersonIsEmployeeByCertificateResponse newInstance() {
              return (org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument.CheckIfPersonIsEmployeeByCertificateResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument.CheckIfPersonIsEmployeeByCertificateResponse newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument.CheckIfPersonIsEmployeeByCertificateResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument newInstance() {
          return (org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.CheckIfPersonIsEmployeeByCertificateResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
