/*
 * An XML document type.
 * Localname: GetMutualOperatorsResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.GetMutualOperatorsResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri;


/**
 * A document containing one GetMutualOperatorsResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public interface GetMutualOperatorsResponseDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(GetMutualOperatorsResponseDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("getmutualoperatorsresponse5552doctype");
    
    /**
     * Gets the "GetMutualOperatorsResponse" element
     */
    org.tempuri.GetMutualOperatorsResponseDocument.GetMutualOperatorsResponse getGetMutualOperatorsResponse();
    
    /**
     * Sets the "GetMutualOperatorsResponse" element
     */
    void setGetMutualOperatorsResponse(org.tempuri.GetMutualOperatorsResponseDocument.GetMutualOperatorsResponse getMutualOperatorsResponse);
    
    /**
     * Appends and returns a new empty "GetMutualOperatorsResponse" element
     */
    org.tempuri.GetMutualOperatorsResponseDocument.GetMutualOperatorsResponse addNewGetMutualOperatorsResponse();
    
    /**
     * An XML GetMutualOperatorsResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public interface GetMutualOperatorsResponse extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(GetMutualOperatorsResponse.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("getmutualoperatorsresponse5b10elemtype");
        
        /**
         * Gets the "GetMutualOperatorsResult" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getGetMutualOperatorsResult();
        
        /**
         * Tests for nil "GetMutualOperatorsResult" element
         */
        boolean isNilGetMutualOperatorsResult();
        
        /**
         * True if has "GetMutualOperatorsResult" element
         */
        boolean isSetGetMutualOperatorsResult();
        
        /**
         * Sets the "GetMutualOperatorsResult" element
         */
        void setGetMutualOperatorsResult(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getMutualOperatorsResult);
        
        /**
         * Appends and returns a new empty "GetMutualOperatorsResult" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewGetMutualOperatorsResult();
        
        /**
         * Nils the "GetMutualOperatorsResult" element
         */
        void setNilGetMutualOperatorsResult();
        
        /**
         * Unsets the "GetMutualOperatorsResult" element
         */
        void unsetGetMutualOperatorsResult();
        
        /**
         * Gets the "operatorCodes" element
         */
        com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring getOperatorCodes();
        
        /**
         * Tests for nil "operatorCodes" element
         */
        boolean isNilOperatorCodes();
        
        /**
         * True if has "operatorCodes" element
         */
        boolean isSetOperatorCodes();
        
        /**
         * Sets the "operatorCodes" element
         */
        void setOperatorCodes(com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring operatorCodes);
        
        /**
         * Appends and returns a new empty "operatorCodes" element
         */
        com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring addNewOperatorCodes();
        
        /**
         * Nils the "operatorCodes" element
         */
        void setNilOperatorCodes();
        
        /**
         * Unsets the "operatorCodes" element
         */
        void unsetOperatorCodes();
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static org.tempuri.GetMutualOperatorsResponseDocument.GetMutualOperatorsResponse newInstance() {
              return (org.tempuri.GetMutualOperatorsResponseDocument.GetMutualOperatorsResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static org.tempuri.GetMutualOperatorsResponseDocument.GetMutualOperatorsResponse newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (org.tempuri.GetMutualOperatorsResponseDocument.GetMutualOperatorsResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.tempuri.GetMutualOperatorsResponseDocument newInstance() {
          return (org.tempuri.GetMutualOperatorsResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.tempuri.GetMutualOperatorsResponseDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.tempuri.GetMutualOperatorsResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.tempuri.GetMutualOperatorsResponseDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GetMutualOperatorsResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.tempuri.GetMutualOperatorsResponseDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GetMutualOperatorsResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.tempuri.GetMutualOperatorsResponseDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GetMutualOperatorsResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.tempuri.GetMutualOperatorsResponseDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GetMutualOperatorsResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.tempuri.GetMutualOperatorsResponseDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GetMutualOperatorsResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.tempuri.GetMutualOperatorsResponseDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GetMutualOperatorsResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.tempuri.GetMutualOperatorsResponseDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GetMutualOperatorsResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.tempuri.GetMutualOperatorsResponseDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GetMutualOperatorsResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.tempuri.GetMutualOperatorsResponseDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GetMutualOperatorsResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.tempuri.GetMutualOperatorsResponseDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GetMutualOperatorsResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.tempuri.GetMutualOperatorsResponseDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GetMutualOperatorsResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.tempuri.GetMutualOperatorsResponseDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GetMutualOperatorsResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.tempuri.GetMutualOperatorsResponseDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GetMutualOperatorsResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.tempuri.GetMutualOperatorsResponseDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GetMutualOperatorsResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.GetMutualOperatorsResponseDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.GetMutualOperatorsResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.GetMutualOperatorsResponseDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.GetMutualOperatorsResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
