/*
 * An XML document type.
 * Localname: VerifySignatureResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.VerifySignatureResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri;


/**
 * A document containing one VerifySignatureResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public interface VerifySignatureResponseDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(VerifySignatureResponseDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("verifysignatureresponse5b1adoctype");
    
    /**
     * Gets the "VerifySignatureResponse" element
     */
    org.tempuri.VerifySignatureResponseDocument.VerifySignatureResponse getVerifySignatureResponse();
    
    /**
     * Sets the "VerifySignatureResponse" element
     */
    void setVerifySignatureResponse(org.tempuri.VerifySignatureResponseDocument.VerifySignatureResponse verifySignatureResponse);
    
    /**
     * Appends and returns a new empty "VerifySignatureResponse" element
     */
    org.tempuri.VerifySignatureResponseDocument.VerifySignatureResponse addNewVerifySignatureResponse();
    
    /**
     * An XML VerifySignatureResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public interface VerifySignatureResponse extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(VerifySignatureResponse.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("verifysignatureresponsef406elemtype");
        
        /**
         * Gets the "VerifySignatureResult" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getVerifySignatureResult();
        
        /**
         * Tests for nil "VerifySignatureResult" element
         */
        boolean isNilVerifySignatureResult();
        
        /**
         * True if has "VerifySignatureResult" element
         */
        boolean isSetVerifySignatureResult();
        
        /**
         * Sets the "VerifySignatureResult" element
         */
        void setVerifySignatureResult(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse verifySignatureResult);
        
        /**
         * Appends and returns a new empty "VerifySignatureResult" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewVerifySignatureResult();
        
        /**
         * Nils the "VerifySignatureResult" element
         */
        void setNilVerifySignatureResult();
        
        /**
         * Unsets the "VerifySignatureResult" element
         */
        void unsetVerifySignatureResult();
        
        /**
         * Gets the "signerInfo" element
         */
        java.lang.String getSignerInfo();
        
        /**
         * Gets (as xml) the "signerInfo" element
         */
        org.apache.xmlbeans.XmlString xgetSignerInfo();
        
        /**
         * Tests for nil "signerInfo" element
         */
        boolean isNilSignerInfo();
        
        /**
         * True if has "signerInfo" element
         */
        boolean isSetSignerInfo();
        
        /**
         * Sets the "signerInfo" element
         */
        void setSignerInfo(java.lang.String signerInfo);
        
        /**
         * Sets (as xml) the "signerInfo" element
         */
        void xsetSignerInfo(org.apache.xmlbeans.XmlString signerInfo);
        
        /**
         * Nils the "signerInfo" element
         */
        void setNilSignerInfo();
        
        /**
         * Unsets the "signerInfo" element
         */
        void unsetSignerInfo();
        
        /**
         * Gets the "isSignatureValid" element
         */
        boolean getIsSignatureValid();
        
        /**
         * Gets (as xml) the "isSignatureValid" element
         */
        org.apache.xmlbeans.XmlBoolean xgetIsSignatureValid();
        
        /**
         * True if has "isSignatureValid" element
         */
        boolean isSetIsSignatureValid();
        
        /**
         * Sets the "isSignatureValid" element
         */
        void setIsSignatureValid(boolean isSignatureValid);
        
        /**
         * Sets (as xml) the "isSignatureValid" element
         */
        void xsetIsSignatureValid(org.apache.xmlbeans.XmlBoolean isSignatureValid);
        
        /**
         * Unsets the "isSignatureValid" element
         */
        void unsetIsSignatureValid();
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static org.tempuri.VerifySignatureResponseDocument.VerifySignatureResponse newInstance() {
              return (org.tempuri.VerifySignatureResponseDocument.VerifySignatureResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static org.tempuri.VerifySignatureResponseDocument.VerifySignatureResponse newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (org.tempuri.VerifySignatureResponseDocument.VerifySignatureResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.tempuri.VerifySignatureResponseDocument newInstance() {
          return (org.tempuri.VerifySignatureResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.tempuri.VerifySignatureResponseDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.tempuri.VerifySignatureResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.tempuri.VerifySignatureResponseDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.VerifySignatureResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.tempuri.VerifySignatureResponseDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.VerifySignatureResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.tempuri.VerifySignatureResponseDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.VerifySignatureResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.tempuri.VerifySignatureResponseDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.VerifySignatureResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.tempuri.VerifySignatureResponseDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.VerifySignatureResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.tempuri.VerifySignatureResponseDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.VerifySignatureResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.tempuri.VerifySignatureResponseDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.VerifySignatureResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.tempuri.VerifySignatureResponseDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.VerifySignatureResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.tempuri.VerifySignatureResponseDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.VerifySignatureResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.tempuri.VerifySignatureResponseDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.VerifySignatureResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.tempuri.VerifySignatureResponseDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.VerifySignatureResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.tempuri.VerifySignatureResponseDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.VerifySignatureResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.tempuri.VerifySignatureResponseDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.VerifySignatureResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.tempuri.VerifySignatureResponseDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.VerifySignatureResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.VerifySignatureResponseDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.VerifySignatureResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.VerifySignatureResponseDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.VerifySignatureResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
