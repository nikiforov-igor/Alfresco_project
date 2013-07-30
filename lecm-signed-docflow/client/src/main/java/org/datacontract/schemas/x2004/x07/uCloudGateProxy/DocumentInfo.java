/*
 * XML Type:  DocumentInfo
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxy;


/**
 * An XML DocumentInfo(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy).
 *
 * This is a complex type.
 */
public interface DocumentInfo extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(DocumentInfo.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("documentinfo4621type");
    
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
     * Gets the "DocumentId" element
     */
    java.lang.String getDocumentId();
    
    /**
     * Gets (as xml) the "DocumentId" element
     */
    com.microsoft.schemas.x2003.x10.serialization.Guid xgetDocumentId();
    
    /**
     * Tests for nil "DocumentId" element
     */
    boolean isNilDocumentId();
    
    /**
     * True if has "DocumentId" element
     */
    boolean isSetDocumentId();
    
    /**
     * Sets the "DocumentId" element
     */
    void setDocumentId(java.lang.String documentId);
    
    /**
     * Sets (as xml) the "DocumentId" element
     */
    void xsetDocumentId(com.microsoft.schemas.x2003.x10.serialization.Guid documentId);
    
    /**
     * Nils the "DocumentId" element
     */
    void setNilDocumentId();
    
    /**
     * Unsets the "DocumentId" element
     */
    void unsetDocumentId();
    
    /**
     * Gets the "DocumentIdPartners" element
     */
    java.lang.String getDocumentIdPartners();
    
    /**
     * Gets (as xml) the "DocumentIdPartners" element
     */
    com.microsoft.schemas.x2003.x10.serialization.Guid xgetDocumentIdPartners();
    
    /**
     * Tests for nil "DocumentIdPartners" element
     */
    boolean isNilDocumentIdPartners();
    
    /**
     * True if has "DocumentIdPartners" element
     */
    boolean isSetDocumentIdPartners();
    
    /**
     * Sets the "DocumentIdPartners" element
     */
    void setDocumentIdPartners(java.lang.String documentIdPartners);
    
    /**
     * Sets (as xml) the "DocumentIdPartners" element
     */
    void xsetDocumentIdPartners(com.microsoft.schemas.x2003.x10.serialization.Guid documentIdPartners);
    
    /**
     * Nils the "DocumentIdPartners" element
     */
    void setNilDocumentIdPartners();
    
    /**
     * Unsets the "DocumentIdPartners" element
     */
    void unsetDocumentIdPartners();
    
    /**
     * Gets the "DocumentType" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType.Enum getDocumentType();
    
    /**
     * Gets (as xml) the "DocumentType" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType xgetDocumentType();
    
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
     * Gets the "IsUnread" element
     */
    boolean getIsUnread();
    
    /**
     * Gets (as xml) the "IsUnread" element
     */
    org.apache.xmlbeans.XmlBoolean xgetIsUnread();
    
    /**
     * True if has "IsUnread" element
     */
    boolean isSetIsUnread();
    
    /**
     * Sets the "IsUnread" element
     */
    void setIsUnread(boolean isUnread);
    
    /**
     * Sets (as xml) the "IsUnread" element
     */
    void xsetIsUnread(org.apache.xmlbeans.XmlBoolean isUnread);
    
    /**
     * Unsets the "IsUnread" element
     */
    void unsetIsUnread();
    
    /**
     * Gets the "ParentDocumentId" element
     */
    java.lang.String getParentDocumentId();
    
    /**
     * Gets (as xml) the "ParentDocumentId" element
     */
    com.microsoft.schemas.x2003.x10.serialization.Guid xgetParentDocumentId();
    
    /**
     * Tests for nil "ParentDocumentId" element
     */
    boolean isNilParentDocumentId();
    
    /**
     * True if has "ParentDocumentId" element
     */
    boolean isSetParentDocumentId();
    
    /**
     * Sets the "ParentDocumentId" element
     */
    void setParentDocumentId(java.lang.String parentDocumentId);
    
    /**
     * Sets (as xml) the "ParentDocumentId" element
     */
    void xsetParentDocumentId(com.microsoft.schemas.x2003.x10.serialization.Guid parentDocumentId);
    
    /**
     * Nils the "ParentDocumentId" element
     */
    void setNilParentDocumentId();
    
    /**
     * Unsets the "ParentDocumentId" element
     */
    void unsetParentDocumentId();
    
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
     * Gets the "Sender" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo getSender();
    
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
    void setSender(org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo sender);
    
    /**
     * Appends and returns a new empty "Sender" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo addNewSender();
    
    /**
     * Nils the "Sender" element
     */
    void setNilSender();
    
    /**
     * Unsets the "Sender" element
     */
    void unsetSender();
    
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
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo newInstance() {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
