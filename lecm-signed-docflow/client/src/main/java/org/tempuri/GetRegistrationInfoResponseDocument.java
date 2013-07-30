/*
 * An XML document type.
 * Localname: GetRegistrationInfoResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.GetRegistrationInfoResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri;


/**
 * A document containing one GetRegistrationInfoResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public interface GetRegistrationInfoResponseDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(GetRegistrationInfoResponseDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("getregistrationinforesponsef2d8doctype");
    
    /**
     * Gets the "GetRegistrationInfoResponse" element
     */
    org.tempuri.GetRegistrationInfoResponseDocument.GetRegistrationInfoResponse getGetRegistrationInfoResponse();
    
    /**
     * Sets the "GetRegistrationInfoResponse" element
     */
    void setGetRegistrationInfoResponse(org.tempuri.GetRegistrationInfoResponseDocument.GetRegistrationInfoResponse getRegistrationInfoResponse);
    
    /**
     * Appends and returns a new empty "GetRegistrationInfoResponse" element
     */
    org.tempuri.GetRegistrationInfoResponseDocument.GetRegistrationInfoResponse addNewGetRegistrationInfoResponse();
    
    /**
     * An XML GetRegistrationInfoResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public interface GetRegistrationInfoResponse extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(GetRegistrationInfoResponse.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("getregistrationinforesponsee482elemtype");
        
        /**
         * Gets the "GetRegistrationInfoResult" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getGetRegistrationInfoResult();
        
        /**
         * Tests for nil "GetRegistrationInfoResult" element
         */
        boolean isNilGetRegistrationInfoResult();
        
        /**
         * True if has "GetRegistrationInfoResult" element
         */
        boolean isSetGetRegistrationInfoResult();
        
        /**
         * Sets the "GetRegistrationInfoResult" element
         */
        void setGetRegistrationInfoResult(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getRegistrationInfoResult);
        
        /**
         * Appends and returns a new empty "GetRegistrationInfoResult" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewGetRegistrationInfoResult();
        
        /**
         * Nils the "GetRegistrationInfoResult" element
         */
        void setNilGetRegistrationInfoResult();
        
        /**
         * Unsets the "GetRegistrationInfoResult" element
         */
        void unsetGetRegistrationInfoResult();
        
        /**
         * Gets the "registrationInfos" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfRegistrationInfo getRegistrationInfos();
        
        /**
         * Tests for nil "registrationInfos" element
         */
        boolean isNilRegistrationInfos();
        
        /**
         * True if has "registrationInfos" element
         */
        boolean isSetRegistrationInfos();
        
        /**
         * Sets the "registrationInfos" element
         */
        void setRegistrationInfos(org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfRegistrationInfo registrationInfos);
        
        /**
         * Appends and returns a new empty "registrationInfos" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfRegistrationInfo addNewRegistrationInfos();
        
        /**
         * Nils the "registrationInfos" element
         */
        void setNilRegistrationInfos();
        
        /**
         * Unsets the "registrationInfos" element
         */
        void unsetRegistrationInfos();
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static org.tempuri.GetRegistrationInfoResponseDocument.GetRegistrationInfoResponse newInstance() {
              return (org.tempuri.GetRegistrationInfoResponseDocument.GetRegistrationInfoResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static org.tempuri.GetRegistrationInfoResponseDocument.GetRegistrationInfoResponse newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (org.tempuri.GetRegistrationInfoResponseDocument.GetRegistrationInfoResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.tempuri.GetRegistrationInfoResponseDocument newInstance() {
          return (org.tempuri.GetRegistrationInfoResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.tempuri.GetRegistrationInfoResponseDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.tempuri.GetRegistrationInfoResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.tempuri.GetRegistrationInfoResponseDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GetRegistrationInfoResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.tempuri.GetRegistrationInfoResponseDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GetRegistrationInfoResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.tempuri.GetRegistrationInfoResponseDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GetRegistrationInfoResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.tempuri.GetRegistrationInfoResponseDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GetRegistrationInfoResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.tempuri.GetRegistrationInfoResponseDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GetRegistrationInfoResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.tempuri.GetRegistrationInfoResponseDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GetRegistrationInfoResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.tempuri.GetRegistrationInfoResponseDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GetRegistrationInfoResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.tempuri.GetRegistrationInfoResponseDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GetRegistrationInfoResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.tempuri.GetRegistrationInfoResponseDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GetRegistrationInfoResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.tempuri.GetRegistrationInfoResponseDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GetRegistrationInfoResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.tempuri.GetRegistrationInfoResponseDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GetRegistrationInfoResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.tempuri.GetRegistrationInfoResponseDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GetRegistrationInfoResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.tempuri.GetRegistrationInfoResponseDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GetRegistrationInfoResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.tempuri.GetRegistrationInfoResponseDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GetRegistrationInfoResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.GetRegistrationInfoResponseDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.GetRegistrationInfoResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.GetRegistrationInfoResponseDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.GetRegistrationInfoResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
