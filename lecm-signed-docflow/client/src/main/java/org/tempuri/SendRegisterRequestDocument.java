/*
 * An XML document type.
 * Localname: SendRegisterRequest
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.SendRegisterRequestDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri;


/**
 * A document containing one SendRegisterRequest(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public interface SendRegisterRequestDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(SendRegisterRequestDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("sendregisterrequest80bedoctype");
    
    /**
     * Gets the "SendRegisterRequest" element
     */
    org.tempuri.SendRegisterRequestDocument.SendRegisterRequest getSendRegisterRequest();
    
    /**
     * Sets the "SendRegisterRequest" element
     */
    void setSendRegisterRequest(org.tempuri.SendRegisterRequestDocument.SendRegisterRequest sendRegisterRequest);
    
    /**
     * Appends and returns a new empty "SendRegisterRequest" element
     */
    org.tempuri.SendRegisterRequestDocument.SendRegisterRequest addNewSendRegisterRequest();
    
    /**
     * An XML SendRegisterRequest(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public interface SendRegisterRequest extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(SendRegisterRequest.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("sendregisterrequest0aceelemtype");
        
        /**
         * Gets the "requestBinary" element
         */
        byte[] getRequestBinary();
        
        /**
         * Gets (as xml) the "requestBinary" element
         */
        org.apache.xmlbeans.XmlBase64Binary xgetRequestBinary();
        
        /**
         * Tests for nil "requestBinary" element
         */
        boolean isNilRequestBinary();
        
        /**
         * True if has "requestBinary" element
         */
        boolean isSetRequestBinary();
        
        /**
         * Sets the "requestBinary" element
         */
        void setRequestBinary(byte[] requestBinary);
        
        /**
         * Sets (as xml) the "requestBinary" element
         */
        void xsetRequestBinary(org.apache.xmlbeans.XmlBase64Binary requestBinary);
        
        /**
         * Nils the "requestBinary" element
         */
        void setNilRequestBinary();
        
        /**
         * Unsets the "requestBinary" element
         */
        void unsetRequestBinary();
        
        /**
         * Gets the "signature" element
         */
        byte[] getSignature();
        
        /**
         * Gets (as xml) the "signature" element
         */
        org.apache.xmlbeans.XmlBase64Binary xgetSignature();
        
        /**
         * Tests for nil "signature" element
         */
        boolean isNilSignature();
        
        /**
         * True if has "signature" element
         */
        boolean isSetSignature();
        
        /**
         * Sets the "signature" element
         */
        void setSignature(byte[] signature);
        
        /**
         * Sets (as xml) the "signature" element
         */
        void xsetSignature(org.apache.xmlbeans.XmlBase64Binary signature);
        
        /**
         * Nils the "signature" element
         */
        void setNilSignature();
        
        /**
         * Unsets the "signature" element
         */
        void unsetSignature();
        
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
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static org.tempuri.SendRegisterRequestDocument.SendRegisterRequest newInstance() {
              return (org.tempuri.SendRegisterRequestDocument.SendRegisterRequest) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static org.tempuri.SendRegisterRequestDocument.SendRegisterRequest newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (org.tempuri.SendRegisterRequestDocument.SendRegisterRequest) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.tempuri.SendRegisterRequestDocument newInstance() {
          return (org.tempuri.SendRegisterRequestDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.tempuri.SendRegisterRequestDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.tempuri.SendRegisterRequestDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.tempuri.SendRegisterRequestDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.SendRegisterRequestDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.tempuri.SendRegisterRequestDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.SendRegisterRequestDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.tempuri.SendRegisterRequestDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.SendRegisterRequestDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.tempuri.SendRegisterRequestDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.SendRegisterRequestDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.tempuri.SendRegisterRequestDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.SendRegisterRequestDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.tempuri.SendRegisterRequestDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.SendRegisterRequestDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.tempuri.SendRegisterRequestDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.SendRegisterRequestDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.tempuri.SendRegisterRequestDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.SendRegisterRequestDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.tempuri.SendRegisterRequestDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.SendRegisterRequestDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.tempuri.SendRegisterRequestDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.SendRegisterRequestDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.tempuri.SendRegisterRequestDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.SendRegisterRequestDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.tempuri.SendRegisterRequestDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.SendRegisterRequestDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.tempuri.SendRegisterRequestDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.SendRegisterRequestDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.tempuri.SendRegisterRequestDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.SendRegisterRequestDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.SendRegisterRequestDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.SendRegisterRequestDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.SendRegisterRequestDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.SendRegisterRequestDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
