/*
 * An XML document type.
 * Localname: GetDocflowListResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.GetDocflowListResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri;


/**
 * A document containing one GetDocflowListResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public interface GetDocflowListResponseDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(GetDocflowListResponseDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("getdocflowlistresponsea8dbdoctype");
    
    /**
     * Gets the "GetDocflowListResponse" element
     */
    org.tempuri.GetDocflowListResponseDocument.GetDocflowListResponse getGetDocflowListResponse();
    
    /**
     * Sets the "GetDocflowListResponse" element
     */
    void setGetDocflowListResponse(org.tempuri.GetDocflowListResponseDocument.GetDocflowListResponse getDocflowListResponse);
    
    /**
     * Appends and returns a new empty "GetDocflowListResponse" element
     */
    org.tempuri.GetDocflowListResponseDocument.GetDocflowListResponse addNewGetDocflowListResponse();
    
    /**
     * An XML GetDocflowListResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public interface GetDocflowListResponse extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(GetDocflowListResponse.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("getdocflowlistresponse6eb0elemtype");
        
        /**
         * Gets the "GetDocflowListResult" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getGetDocflowListResult();
        
        /**
         * Tests for nil "GetDocflowListResult" element
         */
        boolean isNilGetDocflowListResult();
        
        /**
         * True if has "GetDocflowListResult" element
         */
        boolean isSetGetDocflowListResult();
        
        /**
         * Sets the "GetDocflowListResult" element
         */
        void setGetDocflowListResult(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getDocflowListResult);
        
        /**
         * Appends and returns a new empty "GetDocflowListResult" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewGetDocflowListResult();
        
        /**
         * Nils the "GetDocflowListResult" element
         */
        void setNilGetDocflowListResult();
        
        /**
         * Unsets the "GetDocflowListResult" element
         */
        void unsetGetDocflowListResult();
        
        /**
         * Gets the "docflows" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocflowInfoBase getDocflows();
        
        /**
         * Tests for nil "docflows" element
         */
        boolean isNilDocflows();
        
        /**
         * True if has "docflows" element
         */
        boolean isSetDocflows();
        
        /**
         * Sets the "docflows" element
         */
        void setDocflows(org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocflowInfoBase docflows);
        
        /**
         * Appends and returns a new empty "docflows" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocflowInfoBase addNewDocflows();
        
        /**
         * Nils the "docflows" element
         */
        void setNilDocflows();
        
        /**
         * Unsets the "docflows" element
         */
        void unsetDocflows();
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static org.tempuri.GetDocflowListResponseDocument.GetDocflowListResponse newInstance() {
              return (org.tempuri.GetDocflowListResponseDocument.GetDocflowListResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static org.tempuri.GetDocflowListResponseDocument.GetDocflowListResponse newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (org.tempuri.GetDocflowListResponseDocument.GetDocflowListResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.tempuri.GetDocflowListResponseDocument newInstance() {
          return (org.tempuri.GetDocflowListResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.tempuri.GetDocflowListResponseDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.tempuri.GetDocflowListResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.tempuri.GetDocflowListResponseDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GetDocflowListResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.tempuri.GetDocflowListResponseDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GetDocflowListResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.tempuri.GetDocflowListResponseDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GetDocflowListResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.tempuri.GetDocflowListResponseDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GetDocflowListResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.tempuri.GetDocflowListResponseDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GetDocflowListResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.tempuri.GetDocflowListResponseDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GetDocflowListResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.tempuri.GetDocflowListResponseDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GetDocflowListResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.tempuri.GetDocflowListResponseDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GetDocflowListResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.tempuri.GetDocflowListResponseDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GetDocflowListResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.tempuri.GetDocflowListResponseDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GetDocflowListResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.tempuri.GetDocflowListResponseDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GetDocflowListResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.tempuri.GetDocflowListResponseDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GetDocflowListResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.tempuri.GetDocflowListResponseDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GetDocflowListResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.tempuri.GetDocflowListResponseDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GetDocflowListResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.GetDocflowListResponseDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.GetDocflowListResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.GetDocflowListResponseDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.GetDocflowListResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
