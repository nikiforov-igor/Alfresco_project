/*
 * An XML document type.
 * Localname: GetDocumentTransportDataResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.GetDocumentTransportDataResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri;


/**
 * A document containing one GetDocumentTransportDataResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public interface GetDocumentTransportDataResponseDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(GetDocumentTransportDataResponseDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("getdocumenttransportdataresponsed80fdoctype");
    
    /**
     * Gets the "GetDocumentTransportDataResponse" element
     */
    org.tempuri.GetDocumentTransportDataResponseDocument.GetDocumentTransportDataResponse getGetDocumentTransportDataResponse();
    
    /**
     * Sets the "GetDocumentTransportDataResponse" element
     */
    void setGetDocumentTransportDataResponse(org.tempuri.GetDocumentTransportDataResponseDocument.GetDocumentTransportDataResponse getDocumentTransportDataResponse);
    
    /**
     * Appends and returns a new empty "GetDocumentTransportDataResponse" element
     */
    org.tempuri.GetDocumentTransportDataResponseDocument.GetDocumentTransportDataResponse addNewGetDocumentTransportDataResponse();
    
    /**
     * An XML GetDocumentTransportDataResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public interface GetDocumentTransportDataResponse extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(GetDocumentTransportDataResponse.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("getdocumenttransportdataresponsedeb0elemtype");
        
        /**
         * Gets the "GetDocumentTransportDataResult" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getGetDocumentTransportDataResult();
        
        /**
         * Tests for nil "GetDocumentTransportDataResult" element
         */
        boolean isNilGetDocumentTransportDataResult();
        
        /**
         * True if has "GetDocumentTransportDataResult" element
         */
        boolean isSetGetDocumentTransportDataResult();
        
        /**
         * Sets the "GetDocumentTransportDataResult" element
         */
        void setGetDocumentTransportDataResult(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getDocumentTransportDataResult);
        
        /**
         * Appends and returns a new empty "GetDocumentTransportDataResult" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewGetDocumentTransportDataResult();
        
        /**
         * Nils the "GetDocumentTransportDataResult" element
         */
        void setNilGetDocumentTransportDataResult();
        
        /**
         * Unsets the "GetDocumentTransportDataResult" element
         */
        void unsetGetDocumentTransportDataResult();
        
        /**
         * Gets the "transportData" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentTransportData getTransportData();
        
        /**
         * Tests for nil "transportData" element
         */
        boolean isNilTransportData();
        
        /**
         * True if has "transportData" element
         */
        boolean isSetTransportData();
        
        /**
         * Sets the "transportData" element
         */
        void setTransportData(org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentTransportData transportData);
        
        /**
         * Appends and returns a new empty "transportData" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentTransportData addNewTransportData();
        
        /**
         * Nils the "transportData" element
         */
        void setNilTransportData();
        
        /**
         * Unsets the "transportData" element
         */
        void unsetTransportData();
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static org.tempuri.GetDocumentTransportDataResponseDocument.GetDocumentTransportDataResponse newInstance() {
              return (org.tempuri.GetDocumentTransportDataResponseDocument.GetDocumentTransportDataResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static org.tempuri.GetDocumentTransportDataResponseDocument.GetDocumentTransportDataResponse newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (org.tempuri.GetDocumentTransportDataResponseDocument.GetDocumentTransportDataResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.tempuri.GetDocumentTransportDataResponseDocument newInstance() {
          return (org.tempuri.GetDocumentTransportDataResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.tempuri.GetDocumentTransportDataResponseDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.tempuri.GetDocumentTransportDataResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.tempuri.GetDocumentTransportDataResponseDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GetDocumentTransportDataResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.tempuri.GetDocumentTransportDataResponseDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GetDocumentTransportDataResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.tempuri.GetDocumentTransportDataResponseDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GetDocumentTransportDataResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.tempuri.GetDocumentTransportDataResponseDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GetDocumentTransportDataResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.tempuri.GetDocumentTransportDataResponseDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GetDocumentTransportDataResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.tempuri.GetDocumentTransportDataResponseDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GetDocumentTransportDataResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.tempuri.GetDocumentTransportDataResponseDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GetDocumentTransportDataResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.tempuri.GetDocumentTransportDataResponseDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GetDocumentTransportDataResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.tempuri.GetDocumentTransportDataResponseDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GetDocumentTransportDataResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.tempuri.GetDocumentTransportDataResponseDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GetDocumentTransportDataResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.tempuri.GetDocumentTransportDataResponseDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GetDocumentTransportDataResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.tempuri.GetDocumentTransportDataResponseDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GetDocumentTransportDataResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.tempuri.GetDocumentTransportDataResponseDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GetDocumentTransportDataResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.tempuri.GetDocumentTransportDataResponseDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GetDocumentTransportDataResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.GetDocumentTransportDataResponseDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.GetDocumentTransportDataResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.GetDocumentTransportDataResponseDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.GetDocumentTransportDataResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
