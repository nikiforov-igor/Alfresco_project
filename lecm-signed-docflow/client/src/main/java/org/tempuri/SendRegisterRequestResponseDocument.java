/*
 * An XML document type.
 * Localname: SendRegisterRequestResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.SendRegisterRequestResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri;


/**
 * A document containing one SendRegisterRequestResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public interface SendRegisterRequestResponseDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(SendRegisterRequestResponseDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("sendregisterrequestresponsec39fdoctype");
    
    /**
     * Gets the "SendRegisterRequestResponse" element
     */
    org.tempuri.SendRegisterRequestResponseDocument.SendRegisterRequestResponse getSendRegisterRequestResponse();
    
    /**
     * Sets the "SendRegisterRequestResponse" element
     */
    void setSendRegisterRequestResponse(org.tempuri.SendRegisterRequestResponseDocument.SendRegisterRequestResponse sendRegisterRequestResponse);
    
    /**
     * Appends and returns a new empty "SendRegisterRequestResponse" element
     */
    org.tempuri.SendRegisterRequestResponseDocument.SendRegisterRequestResponse addNewSendRegisterRequestResponse();
    
    /**
     * An XML SendRegisterRequestResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public interface SendRegisterRequestResponse extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(SendRegisterRequestResponse.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("sendregisterrequestresponse2fd0elemtype");
        
        /**
         * Gets the "SendRegisterRequestResult" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getSendRegisterRequestResult();
        
        /**
         * Tests for nil "SendRegisterRequestResult" element
         */
        boolean isNilSendRegisterRequestResult();
        
        /**
         * True if has "SendRegisterRequestResult" element
         */
        boolean isSetSendRegisterRequestResult();
        
        /**
         * Sets the "SendRegisterRequestResult" element
         */
        void setSendRegisterRequestResult(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse sendRegisterRequestResult);
        
        /**
         * Appends and returns a new empty "SendRegisterRequestResult" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewSendRegisterRequestResult();
        
        /**
         * Nils the "SendRegisterRequestResult" element
         */
        void setNilSendRegisterRequestResult();
        
        /**
         * Unsets the "SendRegisterRequestResult" element
         */
        void unsetSendRegisterRequestResult();
        
        /**
         * Gets the "requestId" element
         */
        java.lang.String getRequestId();
        
        /**
         * Gets (as xml) the "requestId" element
         */
        org.apache.xmlbeans.XmlString xgetRequestId();
        
        /**
         * Tests for nil "requestId" element
         */
        boolean isNilRequestId();
        
        /**
         * True if has "requestId" element
         */
        boolean isSetRequestId();
        
        /**
         * Sets the "requestId" element
         */
        void setRequestId(java.lang.String requestId);
        
        /**
         * Sets (as xml) the "requestId" element
         */
        void xsetRequestId(org.apache.xmlbeans.XmlString requestId);
        
        /**
         * Nils the "requestId" element
         */
        void setNilRequestId();
        
        /**
         * Unsets the "requestId" element
         */
        void unsetRequestId();
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static org.tempuri.SendRegisterRequestResponseDocument.SendRegisterRequestResponse newInstance() {
              return (org.tempuri.SendRegisterRequestResponseDocument.SendRegisterRequestResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static org.tempuri.SendRegisterRequestResponseDocument.SendRegisterRequestResponse newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (org.tempuri.SendRegisterRequestResponseDocument.SendRegisterRequestResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.tempuri.SendRegisterRequestResponseDocument newInstance() {
          return (org.tempuri.SendRegisterRequestResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.tempuri.SendRegisterRequestResponseDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.tempuri.SendRegisterRequestResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.tempuri.SendRegisterRequestResponseDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.SendRegisterRequestResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.tempuri.SendRegisterRequestResponseDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.SendRegisterRequestResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.tempuri.SendRegisterRequestResponseDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.SendRegisterRequestResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.tempuri.SendRegisterRequestResponseDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.SendRegisterRequestResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.tempuri.SendRegisterRequestResponseDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.SendRegisterRequestResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.tempuri.SendRegisterRequestResponseDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.SendRegisterRequestResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.tempuri.SendRegisterRequestResponseDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.SendRegisterRequestResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.tempuri.SendRegisterRequestResponseDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.SendRegisterRequestResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.tempuri.SendRegisterRequestResponseDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.SendRegisterRequestResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.tempuri.SendRegisterRequestResponseDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.SendRegisterRequestResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.tempuri.SendRegisterRequestResponseDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.SendRegisterRequestResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.tempuri.SendRegisterRequestResponseDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.SendRegisterRequestResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.tempuri.SendRegisterRequestResponseDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.SendRegisterRequestResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.tempuri.SendRegisterRequestResponseDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.SendRegisterRequestResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.SendRegisterRequestResponseDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.SendRegisterRequestResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.SendRegisterRequestResponseDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.SendRegisterRequestResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
