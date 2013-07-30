/*
 * An XML document type.
 * Localname: GetRegisterResponseResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.GetRegisterResponseResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri;


/**
 * A document containing one GetRegisterResponseResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public interface GetRegisterResponseResponseDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(GetRegisterResponseResponseDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("getregisterresponseresponse5c55doctype");
    
    /**
     * Gets the "GetRegisterResponseResponse" element
     */
    org.tempuri.GetRegisterResponseResponseDocument.GetRegisterResponseResponse getGetRegisterResponseResponse();
    
    /**
     * Sets the "GetRegisterResponseResponse" element
     */
    void setGetRegisterResponseResponse(org.tempuri.GetRegisterResponseResponseDocument.GetRegisterResponseResponse getRegisterResponseResponse);
    
    /**
     * Appends and returns a new empty "GetRegisterResponseResponse" element
     */
    org.tempuri.GetRegisterResponseResponseDocument.GetRegisterResponseResponse addNewGetRegisterResponseResponse();
    
    /**
     * An XML GetRegisterResponseResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public interface GetRegisterResponseResponse extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(GetRegisterResponseResponse.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("getregisterresponseresponsebebcelemtype");
        
        /**
         * Gets the "GetRegisterResponseResult" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getGetRegisterResponseResult();
        
        /**
         * Tests for nil "GetRegisterResponseResult" element
         */
        boolean isNilGetRegisterResponseResult();
        
        /**
         * True if has "GetRegisterResponseResult" element
         */
        boolean isSetGetRegisterResponseResult();
        
        /**
         * Sets the "GetRegisterResponseResult" element
         */
        void setGetRegisterResponseResult(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getRegisterResponseResult);
        
        /**
         * Appends and returns a new empty "GetRegisterResponseResult" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewGetRegisterResponseResult();
        
        /**
         * Nils the "GetRegisterResponseResult" element
         */
        void setNilGetRegisterResponseResult();
        
        /**
         * Unsets the "GetRegisterResponseResult" element
         */
        void unsetGetRegisterResponseResult();
        
        /**
         * Gets the "operatorResponse" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse getOperatorResponse();
        
        /**
         * Tests for nil "operatorResponse" element
         */
        boolean isNilOperatorResponse();
        
        /**
         * True if has "operatorResponse" element
         */
        boolean isSetOperatorResponse();
        
        /**
         * Sets the "operatorResponse" element
         */
        void setOperatorResponse(org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse operatorResponse);
        
        /**
         * Appends and returns a new empty "operatorResponse" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse addNewOperatorResponse();
        
        /**
         * Nils the "operatorResponse" element
         */
        void setNilOperatorResponse();
        
        /**
         * Unsets the "operatorResponse" element
         */
        void unsetOperatorResponse();
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static org.tempuri.GetRegisterResponseResponseDocument.GetRegisterResponseResponse newInstance() {
              return (org.tempuri.GetRegisterResponseResponseDocument.GetRegisterResponseResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static org.tempuri.GetRegisterResponseResponseDocument.GetRegisterResponseResponse newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (org.tempuri.GetRegisterResponseResponseDocument.GetRegisterResponseResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.tempuri.GetRegisterResponseResponseDocument newInstance() {
          return (org.tempuri.GetRegisterResponseResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.tempuri.GetRegisterResponseResponseDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.tempuri.GetRegisterResponseResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.tempuri.GetRegisterResponseResponseDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GetRegisterResponseResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.tempuri.GetRegisterResponseResponseDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GetRegisterResponseResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.tempuri.GetRegisterResponseResponseDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GetRegisterResponseResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.tempuri.GetRegisterResponseResponseDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GetRegisterResponseResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.tempuri.GetRegisterResponseResponseDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GetRegisterResponseResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.tempuri.GetRegisterResponseResponseDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GetRegisterResponseResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.tempuri.GetRegisterResponseResponseDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GetRegisterResponseResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.tempuri.GetRegisterResponseResponseDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GetRegisterResponseResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.tempuri.GetRegisterResponseResponseDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GetRegisterResponseResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.tempuri.GetRegisterResponseResponseDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GetRegisterResponseResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.tempuri.GetRegisterResponseResponseDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GetRegisterResponseResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.tempuri.GetRegisterResponseResponseDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GetRegisterResponseResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.tempuri.GetRegisterResponseResponseDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GetRegisterResponseResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.tempuri.GetRegisterResponseResponseDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GetRegisterResponseResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.GetRegisterResponseResponseDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.GetRegisterResponseResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.GetRegisterResponseResponseDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.GetRegisterResponseResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
