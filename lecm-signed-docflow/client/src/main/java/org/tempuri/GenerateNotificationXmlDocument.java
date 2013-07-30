/*
 * An XML document type.
 * Localname: GenerateNotificationXml
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.GenerateNotificationXmlDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri;


/**
 * A document containing one GenerateNotificationXml(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public interface GenerateNotificationXmlDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(GenerateNotificationXmlDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("generatenotificationxmle4f1doctype");
    
    /**
     * Gets the "GenerateNotificationXml" element
     */
    org.tempuri.GenerateNotificationXmlDocument.GenerateNotificationXml getGenerateNotificationXml();
    
    /**
     * Sets the "GenerateNotificationXml" element
     */
    void setGenerateNotificationXml(org.tempuri.GenerateNotificationXmlDocument.GenerateNotificationXml generateNotificationXml);
    
    /**
     * Appends and returns a new empty "GenerateNotificationXml" element
     */
    org.tempuri.GenerateNotificationXmlDocument.GenerateNotificationXml addNewGenerateNotificationXml();
    
    /**
     * An XML GenerateNotificationXml(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public interface GenerateNotificationXml extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(GenerateNotificationXml.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("generatenotificationxmld8f4elemtype");
        
        /**
         * Gets the "signer" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.Signer getSigner();
        
        /**
         * Tests for nil "signer" element
         */
        boolean isNilSigner();
        
        /**
         * True if has "signer" element
         */
        boolean isSetSigner();
        
        /**
         * Sets the "signer" element
         */
        void setSigner(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.Signer signer);
        
        /**
         * Appends and returns a new empty "signer" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.Signer addNewSigner();
        
        /**
         * Nils the "signer" element
         */
        void setNilSigner();
        
        /**
         * Unsets the "signer" element
         */
        void unsetSigner();
        
        /**
         * Gets the "senderCompany" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase getSenderCompany();
        
        /**
         * Tests for nil "senderCompany" element
         */
        boolean isNilSenderCompany();
        
        /**
         * True if has "senderCompany" element
         */
        boolean isSetSenderCompany();
        
        /**
         * Sets the "senderCompany" element
         */
        void setSenderCompany(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase senderCompany);
        
        /**
         * Appends and returns a new empty "senderCompany" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase addNewSenderCompany();
        
        /**
         * Nils the "senderCompany" element
         */
        void setNilSenderCompany();
        
        /**
         * Unsets the "senderCompany" element
         */
        void unsetSenderCompany();
        
        /**
         * Gets the "parentDocument" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo getParentDocument();
        
        /**
         * Tests for nil "parentDocument" element
         */
        boolean isNilParentDocument();
        
        /**
         * True if has "parentDocument" element
         */
        boolean isSetParentDocument();
        
        /**
         * Sets the "parentDocument" element
         */
        void setParentDocument(org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo parentDocument);
        
        /**
         * Appends and returns a new empty "parentDocument" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo addNewParentDocument();
        
        /**
         * Nils the "parentDocument" element
         */
        void setNilParentDocument();
        
        /**
         * Unsets the "parentDocument" element
         */
        void unsetParentDocument();
        
        /**
         * Gets the "generatedDocDate" element
         */
        java.util.Calendar getGeneratedDocDate();
        
        /**
         * Gets (as xml) the "generatedDocDate" element
         */
        org.apache.xmlbeans.XmlDateTime xgetGeneratedDocDate();
        
        /**
         * Tests for nil "generatedDocDate" element
         */
        boolean isNilGeneratedDocDate();
        
        /**
         * True if has "generatedDocDate" element
         */
        boolean isSetGeneratedDocDate();
        
        /**
         * Sets the "generatedDocDate" element
         */
        void setGeneratedDocDate(java.util.Calendar generatedDocDate);
        
        /**
         * Sets (as xml) the "generatedDocDate" element
         */
        void xsetGeneratedDocDate(org.apache.xmlbeans.XmlDateTime generatedDocDate);
        
        /**
         * Nils the "generatedDocDate" element
         */
        void setNilGeneratedDocDate();
        
        /**
         * Unsets the "generatedDocDate" element
         */
        void unsetGeneratedDocDate();
        
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
            public static org.tempuri.GenerateNotificationXmlDocument.GenerateNotificationXml newInstance() {
              return (org.tempuri.GenerateNotificationXmlDocument.GenerateNotificationXml) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static org.tempuri.GenerateNotificationXmlDocument.GenerateNotificationXml newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (org.tempuri.GenerateNotificationXmlDocument.GenerateNotificationXml) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.tempuri.GenerateNotificationXmlDocument newInstance() {
          return (org.tempuri.GenerateNotificationXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.tempuri.GenerateNotificationXmlDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.tempuri.GenerateNotificationXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.tempuri.GenerateNotificationXmlDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateNotificationXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.tempuri.GenerateNotificationXmlDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateNotificationXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.tempuri.GenerateNotificationXmlDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateNotificationXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.tempuri.GenerateNotificationXmlDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateNotificationXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.tempuri.GenerateNotificationXmlDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateNotificationXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.tempuri.GenerateNotificationXmlDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateNotificationXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.tempuri.GenerateNotificationXmlDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateNotificationXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.tempuri.GenerateNotificationXmlDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateNotificationXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.tempuri.GenerateNotificationXmlDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateNotificationXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.tempuri.GenerateNotificationXmlDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateNotificationXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.tempuri.GenerateNotificationXmlDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateNotificationXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.tempuri.GenerateNotificationXmlDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateNotificationXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.tempuri.GenerateNotificationXmlDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateNotificationXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.tempuri.GenerateNotificationXmlDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateNotificationXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.GenerateNotificationXmlDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.GenerateNotificationXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.GenerateNotificationXmlDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.GenerateNotificationXmlDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
