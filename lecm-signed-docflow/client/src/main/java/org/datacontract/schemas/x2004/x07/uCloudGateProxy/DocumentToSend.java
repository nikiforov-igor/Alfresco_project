/*
 * XML Type:  DocumentToSend
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxy;


/**
 * An XML DocumentToSend(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy).
 *
 * This is a complex type.
 */
public interface DocumentToSend extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(DocumentToSend.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("documenttosendd0d6type");
    
    /**
     * Gets the "Comment" element
     */
    java.lang.String getComment();
    
    /**
     * Gets (as xml) the "Comment" element
     */
    org.apache.xmlbeans.XmlString xgetComment();
    
    /**
     * Tests for nil "Comment" element
     */
    boolean isNilComment();
    
    /**
     * True if has "Comment" element
     */
    boolean isSetComment();
    
    /**
     * Sets the "Comment" element
     */
    void setComment(java.lang.String comment);
    
    /**
     * Sets (as xml) the "Comment" element
     */
    void xsetComment(org.apache.xmlbeans.XmlString comment);
    
    /**
     * Nils the "Comment" element
     */
    void setNilComment();
    
    /**
     * Unsets the "Comment" element
     */
    void unsetComment();
    
    /**
     * Gets the "Content" element
     */
    byte[] getContent();
    
    /**
     * Gets (as xml) the "Content" element
     */
    org.apache.xmlbeans.XmlBase64Binary xgetContent();
    
    /**
     * Tests for nil "Content" element
     */
    boolean isNilContent();
    
    /**
     * True if has "Content" element
     */
    boolean isSetContent();
    
    /**
     * Sets the "Content" element
     */
    void setContent(byte[] content);
    
    /**
     * Sets (as xml) the "Content" element
     */
    void xsetContent(org.apache.xmlbeans.XmlBase64Binary content);
    
    /**
     * Nils the "Content" element
     */
    void setNilContent();
    
    /**
     * Unsets the "Content" element
     */
    void unsetContent();
    
    /**
     * Gets the "DocflowId" element
     */
    java.lang.String getDocflowId();
    
    /**
     * Gets (as xml) the "DocflowId" element
     */
    com.microsoft.schemas.x2003.x10.serialization.Guid xgetDocflowId();
    
    /**
     * Tests for nil "DocflowId" element
     */
    boolean isNilDocflowId();
    
    /**
     * True if has "DocflowId" element
     */
    boolean isSetDocflowId();
    
    /**
     * Sets the "DocflowId" element
     */
    void setDocflowId(java.lang.String docflowId);
    
    /**
     * Sets (as xml) the "DocflowId" element
     */
    void xsetDocflowId(com.microsoft.schemas.x2003.x10.serialization.Guid docflowId);
    
    /**
     * Nils the "DocflowId" element
     */
    void setNilDocflowId();
    
    /**
     * Unsets the "DocflowId" element
     */
    void unsetDocflowId();
    
    /**
     * Gets the "DocumentType" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType.Enum getDocumentType();
    
    /**
     * Gets (as xml) the "DocumentType" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType xgetDocumentType();
    
    /**
     * Tests for nil "DocumentType" element
     */
    boolean isNilDocumentType();
    
    /**
     * True if has "DocumentType" element
     */
    boolean isSetDocumentType();
    
    /**
     * Sets the "DocumentType" element
     */
    void setDocumentType(org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType.Enum documentType);
    
    /**
     * Sets (as xml) the "DocumentType" element
     */
    void xsetDocumentType(org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType documentType);
    
    /**
     * Nils the "DocumentType" element
     */
    void setNilDocumentType();
    
    /**
     * Unsets the "DocumentType" element
     */
    void unsetDocumentType();
    
    /**
     * Gets the "FileName" element
     */
    java.lang.String getFileName();
    
    /**
     * Gets (as xml) the "FileName" element
     */
    org.apache.xmlbeans.XmlString xgetFileName();
    
    /**
     * Tests for nil "FileName" element
     */
    boolean isNilFileName();
    
    /**
     * True if has "FileName" element
     */
    boolean isSetFileName();
    
    /**
     * Sets the "FileName" element
     */
    void setFileName(java.lang.String fileName);
    
    /**
     * Sets (as xml) the "FileName" element
     */
    void xsetFileName(org.apache.xmlbeans.XmlString fileName);
    
    /**
     * Nils the "FileName" element
     */
    void setNilFileName();
    
    /**
     * Unsets the "FileName" element
     */
    void unsetFileName();
    
    /**
     * Gets the "Id" element
     */
    java.lang.String getId();
    
    /**
     * Gets (as xml) the "Id" element
     */
    com.microsoft.schemas.x2003.x10.serialization.Guid xgetId();
    
    /**
     * Tests for nil "Id" element
     */
    boolean isNilId();
    
    /**
     * True if has "Id" element
     */
    boolean isSetId();
    
    /**
     * Sets the "Id" element
     */
    void setId(java.lang.String id);
    
    /**
     * Sets (as xml) the "Id" element
     */
    void xsetId(com.microsoft.schemas.x2003.x10.serialization.Guid id);
    
    /**
     * Nils the "Id" element
     */
    void setNilId();
    
    /**
     * Unsets the "Id" element
     */
    void unsetId();
    
    /**
     * Gets the "Receiver" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo getReceiver();
    
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
    void setReceiver(org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo receiver);
    
    /**
     * Appends and returns a new empty "Receiver" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo addNewReceiver();
    
    /**
     * Nils the "Receiver" element
     */
    void setNilReceiver();
    
    /**
     * Unsets the "Receiver" element
     */
    void unsetReceiver();
    
    /**
     * Gets the "Signatures" element
     */
    com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary getSignatures();
    
    /**
     * Tests for nil "Signatures" element
     */
    boolean isNilSignatures();
    
    /**
     * True if has "Signatures" element
     */
    boolean isSetSignatures();
    
    /**
     * Sets the "Signatures" element
     */
    void setSignatures(com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary signatures);
    
    /**
     * Appends and returns a new empty "Signatures" element
     */
    com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary addNewSignatures();
    
    /**
     * Nils the "Signatures" element
     */
    void setNilSignatures();
    
    /**
     * Unsets the "Signatures" element
     */
    void unsetSignatures();
    
    /**
     * Gets the "TransactionType" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType.Enum getTransactionType();
    
    /**
     * Gets (as xml) the "TransactionType" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType xgetTransactionType();
    
    /**
     * True if has "TransactionType" element
     */
    boolean isSetTransactionType();
    
    /**
     * Sets the "TransactionType" element
     */
    void setTransactionType(org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType.Enum transactionType);
    
    /**
     * Sets (as xml) the "TransactionType" element
     */
    void xsetTransactionType(org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType transactionType);
    
    /**
     * Unsets the "TransactionType" element
     */
    void unsetTransactionType();
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend newInstance() {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
