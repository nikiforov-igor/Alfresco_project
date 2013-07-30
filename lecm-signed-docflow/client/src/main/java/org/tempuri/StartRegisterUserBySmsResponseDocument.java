/*
 * An XML document type.
 * Localname: StartRegisterUserBySmsResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.StartRegisterUserBySmsResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri;


/**
 * A document containing one StartRegisterUserBySmsResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public interface StartRegisterUserBySmsResponseDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(StartRegisterUserBySmsResponseDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("startregisteruserbysmsresponsea3bfdoctype");
    
    /**
     * Gets the "StartRegisterUserBySmsResponse" element
     */
    org.tempuri.StartRegisterUserBySmsResponseDocument.StartRegisterUserBySmsResponse getStartRegisterUserBySmsResponse();
    
    /**
     * Sets the "StartRegisterUserBySmsResponse" element
     */
    void setStartRegisterUserBySmsResponse(org.tempuri.StartRegisterUserBySmsResponseDocument.StartRegisterUserBySmsResponse startRegisterUserBySmsResponse);
    
    /**
     * Appends and returns a new empty "StartRegisterUserBySmsResponse" element
     */
    org.tempuri.StartRegisterUserBySmsResponseDocument.StartRegisterUserBySmsResponse addNewStartRegisterUserBySmsResponse();
    
    /**
     * An XML StartRegisterUserBySmsResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public interface StartRegisterUserBySmsResponse extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(StartRegisterUserBySmsResponse.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("startregisteruserbysmsresponsea530elemtype");
        
        /**
         * Gets the "StartRegisterUserBySmsResult" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getStartRegisterUserBySmsResult();
        
        /**
         * Tests for nil "StartRegisterUserBySmsResult" element
         */
        boolean isNilStartRegisterUserBySmsResult();
        
        /**
         * True if has "StartRegisterUserBySmsResult" element
         */
        boolean isSetStartRegisterUserBySmsResult();
        
        /**
         * Sets the "StartRegisterUserBySmsResult" element
         */
        void setStartRegisterUserBySmsResult(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse startRegisterUserBySmsResult);
        
        /**
         * Appends and returns a new empty "StartRegisterUserBySmsResult" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewStartRegisterUserBySmsResult();
        
        /**
         * Nils the "StartRegisterUserBySmsResult" element
         */
        void setNilStartRegisterUserBySmsResult();
        
        /**
         * Unsets the "StartRegisterUserBySmsResult" element
         */
        void unsetStartRegisterUserBySmsResult();
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static org.tempuri.StartRegisterUserBySmsResponseDocument.StartRegisterUserBySmsResponse newInstance() {
              return (org.tempuri.StartRegisterUserBySmsResponseDocument.StartRegisterUserBySmsResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static org.tempuri.StartRegisterUserBySmsResponseDocument.StartRegisterUserBySmsResponse newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (org.tempuri.StartRegisterUserBySmsResponseDocument.StartRegisterUserBySmsResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.tempuri.StartRegisterUserBySmsResponseDocument newInstance() {
          return (org.tempuri.StartRegisterUserBySmsResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.tempuri.StartRegisterUserBySmsResponseDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.tempuri.StartRegisterUserBySmsResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.tempuri.StartRegisterUserBySmsResponseDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.StartRegisterUserBySmsResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.tempuri.StartRegisterUserBySmsResponseDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.StartRegisterUserBySmsResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.tempuri.StartRegisterUserBySmsResponseDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.StartRegisterUserBySmsResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.tempuri.StartRegisterUserBySmsResponseDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.StartRegisterUserBySmsResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.tempuri.StartRegisterUserBySmsResponseDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.StartRegisterUserBySmsResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.tempuri.StartRegisterUserBySmsResponseDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.StartRegisterUserBySmsResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.tempuri.StartRegisterUserBySmsResponseDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.StartRegisterUserBySmsResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.tempuri.StartRegisterUserBySmsResponseDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.StartRegisterUserBySmsResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.tempuri.StartRegisterUserBySmsResponseDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.StartRegisterUserBySmsResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.tempuri.StartRegisterUserBySmsResponseDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.StartRegisterUserBySmsResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.tempuri.StartRegisterUserBySmsResponseDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.StartRegisterUserBySmsResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.tempuri.StartRegisterUserBySmsResponseDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.StartRegisterUserBySmsResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.tempuri.StartRegisterUserBySmsResponseDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.StartRegisterUserBySmsResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.tempuri.StartRegisterUserBySmsResponseDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.StartRegisterUserBySmsResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.StartRegisterUserBySmsResponseDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.StartRegisterUserBySmsResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.StartRegisterUserBySmsResponseDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.StartRegisterUserBySmsResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
