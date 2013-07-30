/*
 * An XML document type.
 * Localname: GenerateInvoiceXml
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.GenerateInvoiceXmlDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri;


/**
 * A document containing one GenerateInvoiceXml(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public interface GenerateInvoiceXmlDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(GenerateInvoiceXmlDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("generateinvoicexmlf50bdoctype");
    
    /**
     * Gets the "GenerateInvoiceXml" element
     */
    org.tempuri.GenerateInvoiceXmlDocument.GenerateInvoiceXml getGenerateInvoiceXml();
    
    /**
     * Sets the "GenerateInvoiceXml" element
     */
    void setGenerateInvoiceXml(org.tempuri.GenerateInvoiceXmlDocument.GenerateInvoiceXml generateInvoiceXml);
    
    /**
     * Appends and returns a new empty "GenerateInvoiceXml" element
     */
    org.tempuri.GenerateInvoiceXmlDocument.GenerateInvoiceXml addNewGenerateInvoiceXml();
    
    /**
     * An XML GenerateInvoiceXml(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public interface GenerateInvoiceXml extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(GenerateInvoiceXml.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("generateinvoicexml47b0elemtype");
        
        /**
         * Gets the "invoice" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen getInvoice();
        
        /**
         * Tests for nil "invoice" element
         */
        boolean isNilInvoice();
        
        /**
         * True if has "invoice" element
         */
        boolean isSetInvoice();
        
        /**
         * Sets the "invoice" element
         */
        void setInvoice(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen invoice);
        
        /**
         * Appends and returns a new empty "invoice" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen addNewInvoice();
        
        /**
         * Nils the "invoice" element
         */
        void setNilInvoice();
        
        /**
         * Unsets the "invoice" element
         */
        void unsetInvoice();
        
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
            public static org.tempuri.GenerateInvoiceXmlDocument.GenerateInvoiceXml newInstance() {
              return (org.tempuri.GenerateInvoiceXmlDocument.GenerateInvoiceXml) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static org.tempuri.GenerateInvoiceXmlDocument.GenerateInvoiceXml newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (org.tempuri.GenerateInvoiceXmlDocument.GenerateInvoiceXml) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.tempuri.GenerateInvoiceXmlDocument newInstance() {
          return (org.tempuri.GenerateInvoiceXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.tempuri.GenerateInvoiceXmlDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.tempuri.GenerateInvoiceXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.tempuri.GenerateInvoiceXmlDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateInvoiceXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.tempuri.GenerateInvoiceXmlDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateInvoiceXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.tempuri.GenerateInvoiceXmlDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateInvoiceXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.tempuri.GenerateInvoiceXmlDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateInvoiceXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.tempuri.GenerateInvoiceXmlDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateInvoiceXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.tempuri.GenerateInvoiceXmlDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateInvoiceXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.tempuri.GenerateInvoiceXmlDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateInvoiceXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.tempuri.GenerateInvoiceXmlDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateInvoiceXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.tempuri.GenerateInvoiceXmlDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateInvoiceXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.tempuri.GenerateInvoiceXmlDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateInvoiceXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.tempuri.GenerateInvoiceXmlDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateInvoiceXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.tempuri.GenerateInvoiceXmlDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateInvoiceXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.tempuri.GenerateInvoiceXmlDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateInvoiceXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.tempuri.GenerateInvoiceXmlDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateInvoiceXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.GenerateInvoiceXmlDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.GenerateInvoiceXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.GenerateInvoiceXmlDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.GenerateInvoiceXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
