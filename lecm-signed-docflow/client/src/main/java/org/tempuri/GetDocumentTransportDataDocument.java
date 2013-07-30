/*
 * An XML document type.
 * Localname: GetDocumentTransportData
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.GetDocumentTransportDataDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri;


/**
 * A document containing one GetDocumentTransportData(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public interface GetDocumentTransportDataDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(GetDocumentTransportDataDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("getdocumenttransportdata052edoctype");
    
    /**
     * Gets the "GetDocumentTransportData" element
     */
    org.tempuri.GetDocumentTransportDataDocument.GetDocumentTransportData getGetDocumentTransportData();
    
    /**
     * Sets the "GetDocumentTransportData" element
     */
    void setGetDocumentTransportData(org.tempuri.GetDocumentTransportDataDocument.GetDocumentTransportData getDocumentTransportData);
    
    /**
     * Appends and returns a new empty "GetDocumentTransportData" element
     */
    org.tempuri.GetDocumentTransportDataDocument.GetDocumentTransportData addNewGetDocumentTransportData();
    
    /**
     * An XML GetDocumentTransportData(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public interface GetDocumentTransportData extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(GetDocumentTransportData.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("getdocumenttransportdata6550elemtype");
        
        /**
         * Gets the "sender" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo getSender();
        
        /**
         * Tests for nil "sender" element
         */
        boolean isNilSender();
        
        /**
         * True if has "sender" element
         */
        boolean isSetSender();
        
        /**
         * Sets the "sender" element
         */
        void setSender(org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo sender);
        
        /**
         * Appends and returns a new empty "sender" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo addNewSender();
        
        /**
         * Nils the "sender" element
         */
        void setNilSender();
        
        /**
         * Unsets the "sender" element
         */
        void unsetSender();
        
        /**
         * Gets the "receiver" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo getReceiver();
        
        /**
         * Tests for nil "receiver" element
         */
        boolean isNilReceiver();
        
        /**
         * True if has "receiver" element
         */
        boolean isSetReceiver();
        
        /**
         * Sets the "receiver" element
         */
        void setReceiver(org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo receiver);
        
        /**
         * Appends and returns a new empty "receiver" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo addNewReceiver();
        
        /**
         * Nils the "receiver" element
         */
        void setNilReceiver();
        
        /**
         * Unsets the "receiver" element
         */
        void unsetReceiver();
        
        /**
         * Gets the "prefferableOperatorCode" element
         */
        java.lang.String getPrefferableOperatorCode();
        
        /**
         * Gets (as xml) the "prefferableOperatorCode" element
         */
        org.apache.xmlbeans.XmlString xgetPrefferableOperatorCode();
        
        /**
         * Tests for nil "prefferableOperatorCode" element
         */
        boolean isNilPrefferableOperatorCode();
        
        /**
         * True if has "prefferableOperatorCode" element
         */
        boolean isSetPrefferableOperatorCode();
        
        /**
         * Sets the "prefferableOperatorCode" element
         */
        void setPrefferableOperatorCode(java.lang.String prefferableOperatorCode);
        
        /**
         * Sets (as xml) the "prefferableOperatorCode" element
         */
        void xsetPrefferableOperatorCode(org.apache.xmlbeans.XmlString prefferableOperatorCode);
        
        /**
         * Nils the "prefferableOperatorCode" element
         */
        void setNilPrefferableOperatorCode();
        
        /**
         * Unsets the "prefferableOperatorCode" element
         */
        void unsetPrefferableOperatorCode();
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static org.tempuri.GetDocumentTransportDataDocument.GetDocumentTransportData newInstance() {
              return (org.tempuri.GetDocumentTransportDataDocument.GetDocumentTransportData) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static org.tempuri.GetDocumentTransportDataDocument.GetDocumentTransportData newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (org.tempuri.GetDocumentTransportDataDocument.GetDocumentTransportData) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.tempuri.GetDocumentTransportDataDocument newInstance() {
          return (org.tempuri.GetDocumentTransportDataDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.tempuri.GetDocumentTransportDataDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.tempuri.GetDocumentTransportDataDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.tempuri.GetDocumentTransportDataDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GetDocumentTransportDataDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.tempuri.GetDocumentTransportDataDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GetDocumentTransportDataDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.tempuri.GetDocumentTransportDataDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GetDocumentTransportDataDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.tempuri.GetDocumentTransportDataDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GetDocumentTransportDataDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.tempuri.GetDocumentTransportDataDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GetDocumentTransportDataDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.tempuri.GetDocumentTransportDataDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GetDocumentTransportDataDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.tempuri.GetDocumentTransportDataDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GetDocumentTransportDataDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.tempuri.GetDocumentTransportDataDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GetDocumentTransportDataDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.tempuri.GetDocumentTransportDataDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GetDocumentTransportDataDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.tempuri.GetDocumentTransportDataDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GetDocumentTransportDataDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.tempuri.GetDocumentTransportDataDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GetDocumentTransportDataDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.tempuri.GetDocumentTransportDataDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GetDocumentTransportDataDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.tempuri.GetDocumentTransportDataDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GetDocumentTransportDataDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.tempuri.GetDocumentTransportDataDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GetDocumentTransportDataDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.GetDocumentTransportDataDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.GetDocumentTransportDataDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.GetDocumentTransportDataDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.GetDocumentTransportDataDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
