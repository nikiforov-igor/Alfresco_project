/*
 * XML Type:  CorrectionRequest
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice;


/**
 * An XML CorrectionRequest(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice).
 *
 * This is a complex type.
 */
public interface CorrectionRequest extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(CorrectionRequest.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("correctionrequestd48etype");
    
    /**
     * Gets the "OperatorCode" element
     */
    java.lang.String getOperatorCode();
    
    /**
     * Gets (as xml) the "OperatorCode" element
     */
    org.apache.xmlbeans.XmlString xgetOperatorCode();
    
    /**
     * Tests for nil "OperatorCode" element
     */
    boolean isNilOperatorCode();
    
    /**
     * True if has "OperatorCode" element
     */
    boolean isSetOperatorCode();
    
    /**
     * Sets the "OperatorCode" element
     */
    void setOperatorCode(java.lang.String operatorCode);
    
    /**
     * Sets (as xml) the "OperatorCode" element
     */
    void xsetOperatorCode(org.apache.xmlbeans.XmlString operatorCode);
    
    /**
     * Nils the "OperatorCode" element
     */
    void setNilOperatorCode();
    
    /**
     * Unsets the "OperatorCode" element
     */
    void unsetOperatorCode();
    
    /**
     * Gets the "ReceivedDocument" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo getReceivedDocument();
    
    /**
     * Tests for nil "ReceivedDocument" element
     */
    boolean isNilReceivedDocument();
    
    /**
     * True if has "ReceivedDocument" element
     */
    boolean isSetReceivedDocument();
    
    /**
     * Sets the "ReceivedDocument" element
     */
    void setReceivedDocument(org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo receivedDocument);
    
    /**
     * Appends and returns a new empty "ReceivedDocument" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo addNewReceivedDocument();
    
    /**
     * Nils the "ReceivedDocument" element
     */
    void setNilReceivedDocument();
    
    /**
     * Unsets the "ReceivedDocument" element
     */
    void unsetReceivedDocument();
    
    /**
     * Gets the "Receiver" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase getReceiver();
    
    /**
     * Tests for nil "Receiver" element
     */
    boolean isNilReceiver();
    
    /**
     * True if has "Receiver" element
     */
    boolean isSetReceiver();
    
    /**
     * Sets the "Receiver" element
     */
    void setReceiver(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase receiver);
    
    /**
     * Appends and returns a new empty "Receiver" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase addNewReceiver();
    
    /**
     * Nils the "Receiver" element
     */
    void setNilReceiver();
    
    /**
     * Unsets the "Receiver" element
     */
    void unsetReceiver();
    
    /**
     * Gets the "RequestMessage" element
     */
    java.lang.String getRequestMessage();
    
    /**
     * Gets (as xml) the "RequestMessage" element
     */
    org.apache.xmlbeans.XmlString xgetRequestMessage();
    
    /**
     * Tests for nil "RequestMessage" element
     */
    boolean isNilRequestMessage();
    
    /**
     * True if has "RequestMessage" element
     */
    boolean isSetRequestMessage();
    
    /**
     * Sets the "RequestMessage" element
     */
    void setRequestMessage(java.lang.String requestMessage);
    
    /**
     * Sets (as xml) the "RequestMessage" element
     */
    void xsetRequestMessage(org.apache.xmlbeans.XmlString requestMessage);
    
    /**
     * Nils the "RequestMessage" element
     */
    void setNilRequestMessage();
    
    /**
     * Unsets the "RequestMessage" element
     */
    void unsetRequestMessage();
    
    /**
     * Gets the "Sender" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase getSender();
    
    /**
     * Tests for nil "Sender" element
     */
    boolean isNilSender();
    
    /**
     * True if has "Sender" element
     */
    boolean isSetSender();
    
    /**
     * Sets the "Sender" element
     */
    void setSender(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase sender);
    
    /**
     * Appends and returns a new empty "Sender" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantBase addNewSender();
    
    /**
     * Nils the "Sender" element
     */
    void setNilSender();
    
    /**
     * Unsets the "Sender" element
     */
    void unsetSender();
    
    /**
     * Gets the "Signature" element
     */
    java.lang.String getSignature();
    
    /**
     * Gets (as xml) the "Signature" element
     */
    org.apache.xmlbeans.XmlString xgetSignature();
    
    /**
     * Tests for nil "Signature" element
     */
    boolean isNilSignature();
    
    /**
     * True if has "Signature" element
     */
    boolean isSetSignature();
    
    /**
     * Sets the "Signature" element
     */
    void setSignature(java.lang.String signature);
    
    /**
     * Sets (as xml) the "Signature" element
     */
    void xsetSignature(org.apache.xmlbeans.XmlString signature);
    
    /**
     * Nils the "Signature" element
     */
    void setNilSignature();
    
    /**
     * Unsets the "Signature" element
     */
    void unsetSignature();
    
    /**
     * Gets the "Signer" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.Signer getSigner();
    
    /**
     * Tests for nil "Signer" element
     */
    boolean isNilSigner();
    
    /**
     * True if has "Signer" element
     */
    boolean isSetSigner();
    
    /**
     * Sets the "Signer" element
     */
    void setSigner(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.Signer signer);
    
    /**
     * Appends and returns a new empty "Signer" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.Signer addNewSigner();
    
    /**
     * Nils the "Signer" element
     */
    void setNilSigner();
    
    /**
     * Unsets the "Signer" element
     */
    void unsetSigner();
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest newInstance() {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.CorrectionRequest) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
