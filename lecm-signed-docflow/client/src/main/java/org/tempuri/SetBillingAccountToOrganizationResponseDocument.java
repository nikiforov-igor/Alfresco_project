/*
 * An XML document type.
 * Localname: SetBillingAccountToOrganizationResponse
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.SetBillingAccountToOrganizationResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri;


/**
 * A document containing one SetBillingAccountToOrganizationResponse(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public interface SetBillingAccountToOrganizationResponseDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(SetBillingAccountToOrganizationResponseDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("setbillingaccounttoorganizationresponsef6fddoctype");
    
    /**
     * Gets the "SetBillingAccountToOrganizationResponse" element
     */
    org.tempuri.SetBillingAccountToOrganizationResponseDocument.SetBillingAccountToOrganizationResponse getSetBillingAccountToOrganizationResponse();
    
    /**
     * Sets the "SetBillingAccountToOrganizationResponse" element
     */
    void setSetBillingAccountToOrganizationResponse(org.tempuri.SetBillingAccountToOrganizationResponseDocument.SetBillingAccountToOrganizationResponse setBillingAccountToOrganizationResponse);
    
    /**
     * Appends and returns a new empty "SetBillingAccountToOrganizationResponse" element
     */
    org.tempuri.SetBillingAccountToOrganizationResponseDocument.SetBillingAccountToOrganizationResponse addNewSetBillingAccountToOrganizationResponse();
    
    /**
     * An XML SetBillingAccountToOrganizationResponse(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public interface SetBillingAccountToOrganizationResponse extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(SetBillingAccountToOrganizationResponse.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("setbillingaccounttoorganizationresponsea60celemtype");
        
        /**
         * Gets the "SetBillingAccountToOrganizationResult" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse getSetBillingAccountToOrganizationResult();
        
        /**
         * Tests for nil "SetBillingAccountToOrganizationResult" element
         */
        boolean isNilSetBillingAccountToOrganizationResult();
        
        /**
         * True if has "SetBillingAccountToOrganizationResult" element
         */
        boolean isSetSetBillingAccountToOrganizationResult();
        
        /**
         * Sets the "SetBillingAccountToOrganizationResult" element
         */
        void setSetBillingAccountToOrganizationResult(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse setBillingAccountToOrganizationResult);
        
        /**
         * Appends and returns a new empty "SetBillingAccountToOrganizationResult" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse addNewSetBillingAccountToOrganizationResult();
        
        /**
         * Nils the "SetBillingAccountToOrganizationResult" element
         */
        void setNilSetBillingAccountToOrganizationResult();
        
        /**
         * Unsets the "SetBillingAccountToOrganizationResult" element
         */
        void unsetSetBillingAccountToOrganizationResult();
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static org.tempuri.SetBillingAccountToOrganizationResponseDocument.SetBillingAccountToOrganizationResponse newInstance() {
              return (org.tempuri.SetBillingAccountToOrganizationResponseDocument.SetBillingAccountToOrganizationResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static org.tempuri.SetBillingAccountToOrganizationResponseDocument.SetBillingAccountToOrganizationResponse newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (org.tempuri.SetBillingAccountToOrganizationResponseDocument.SetBillingAccountToOrganizationResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.tempuri.SetBillingAccountToOrganizationResponseDocument newInstance() {
          return (org.tempuri.SetBillingAccountToOrganizationResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.tempuri.SetBillingAccountToOrganizationResponseDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.tempuri.SetBillingAccountToOrganizationResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.tempuri.SetBillingAccountToOrganizationResponseDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.SetBillingAccountToOrganizationResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.tempuri.SetBillingAccountToOrganizationResponseDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.SetBillingAccountToOrganizationResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.tempuri.SetBillingAccountToOrganizationResponseDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.SetBillingAccountToOrganizationResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.tempuri.SetBillingAccountToOrganizationResponseDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.SetBillingAccountToOrganizationResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.tempuri.SetBillingAccountToOrganizationResponseDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.SetBillingAccountToOrganizationResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.tempuri.SetBillingAccountToOrganizationResponseDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.SetBillingAccountToOrganizationResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.tempuri.SetBillingAccountToOrganizationResponseDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.SetBillingAccountToOrganizationResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.tempuri.SetBillingAccountToOrganizationResponseDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.SetBillingAccountToOrganizationResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.tempuri.SetBillingAccountToOrganizationResponseDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.SetBillingAccountToOrganizationResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.tempuri.SetBillingAccountToOrganizationResponseDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.SetBillingAccountToOrganizationResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.tempuri.SetBillingAccountToOrganizationResponseDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.SetBillingAccountToOrganizationResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.tempuri.SetBillingAccountToOrganizationResponseDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.SetBillingAccountToOrganizationResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.tempuri.SetBillingAccountToOrganizationResponseDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.SetBillingAccountToOrganizationResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.tempuri.SetBillingAccountToOrganizationResponseDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.SetBillingAccountToOrganizationResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.SetBillingAccountToOrganizationResponseDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.SetBillingAccountToOrganizationResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.SetBillingAccountToOrganizationResponseDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.SetBillingAccountToOrganizationResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
