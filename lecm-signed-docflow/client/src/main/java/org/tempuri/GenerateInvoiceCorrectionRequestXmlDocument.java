/*
 * An XML document type.
 * Localname: GenerateInvoiceCorrectionRequestXml
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri;


/**
 * A document containing one GenerateInvoiceCorrectionRequestXml(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public interface GenerateInvoiceCorrectionRequestXmlDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(GenerateInvoiceCorrectionRequestXmlDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("generateinvoicecorrectionrequestxml0118doctype");
    
    /**
     * Gets the "GenerateInvoiceCorrectionRequestXml" element
     */
    org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument.GenerateInvoiceCorrectionRequestXml getGenerateInvoiceCorrectionRequestXml();
    
    /**
     * Sets the "GenerateInvoiceCorrectionRequestXml" element
     */
    void setGenerateInvoiceCorrectionRequestXml(org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument.GenerateInvoiceCorrectionRequestXml generateInvoiceCorrectionRequestXml);
    
    /**
     * Appends and returns a new empty "GenerateInvoiceCorrectionRequestXml" element
     */
    org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument.GenerateInvoiceCorrectionRequestXml addNewGenerateInvoiceCorrectionRequestXml();
    
    /**
     * An XML GenerateInvoiceCorrectionRequestXml(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public interface GenerateInvoiceCorrectionRequestXml extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(GenerateInvoiceCorrectionRequestXml.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("generateinvoicecorrectionrequestxml3602elemtype");
        
        /**
         * Gets the "correctionRequest" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest getCorrectionRequest();
        
        /**
         * Tests for nil "correctionRequest" element
         */
        boolean isNilCorrectionRequest();
        
        /**
         * True if has "correctionRequest" element
         */
        boolean isSetCorrectionRequest();
        
        /**
         * Sets the "correctionRequest" element
         */
        void setCorrectionRequest(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest correctionRequest);
        
        /**
         * Appends and returns a new empty "correctionRequest" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest addNewCorrectionRequest();
        
        /**
         * Nils the "correctionRequest" element
         */
        void setNilCorrectionRequest();
        
        /**
         * Unsets the "correctionRequest" element
         */
        void unsetCorrectionRequest();
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument.GenerateInvoiceCorrectionRequestXml newInstance() {
              return (org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument.GenerateInvoiceCorrectionRequestXml) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument.GenerateInvoiceCorrectionRequestXml newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument.GenerateInvoiceCorrectionRequestXml) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument newInstance() {
          return (org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.GenerateInvoiceCorrectionRequestXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
