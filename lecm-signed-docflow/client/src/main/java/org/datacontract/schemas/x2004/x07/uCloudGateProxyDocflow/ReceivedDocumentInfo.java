/*
 * XML Type:  ReceivedDocumentInfo
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Docflow
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow;


/**
 * An XML ReceivedDocumentInfo(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Docflow).
 *
 * This is a complex type.
 */
public interface ReceivedDocumentInfo extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(ReceivedDocumentInfo.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("receiveddocumentinfo5ebatype");
    
    /**
     * Gets the "Date" element
     */
    java.lang.String getDate();
    
    /**
     * Gets (as xml) the "Date" element
     */
    org.apache.xmlbeans.XmlString xgetDate();
    
    /**
     * Tests for nil "Date" element
     */
    boolean isNilDate();
    
    /**
     * True if has "Date" element
     */
    boolean isSetDate();
    
    /**
     * Sets the "Date" element
     */
    void setDate(java.lang.String date);
    
    /**
     * Sets (as xml) the "Date" element
     */
    void xsetDate(org.apache.xmlbeans.XmlString date);
    
    /**
     * Nils the "Date" element
     */
    void setNilDate();
    
    /**
     * Unsets the "Date" element
     */
    void unsetDate();
    
    /**
     * Gets the "DocflowType" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocflowType.Enum getDocflowType();
    
    /**
     * Gets (as xml) the "DocflowType" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocflowType xgetDocflowType();
    
    /**
     * True if has "DocflowType" element
     */
    boolean isSetDocflowType();
    
    /**
     * Sets the "DocflowType" element
     */
    void setDocflowType(org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocflowType.Enum docflowType);
    
    /**
     * Sets (as xml) the "DocflowType" element
     */
    void xsetDocflowType(org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocflowType docflowType);
    
    /**
     * Unsets the "DocflowType" element
     */
    void unsetDocflowType();
    
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
     * Gets the "Number" element
     */
    java.lang.String getNumber();
    
    /**
     * Gets (as xml) the "Number" element
     */
    org.apache.xmlbeans.XmlString xgetNumber();
    
    /**
     * Tests for nil "Number" element
     */
    boolean isNilNumber();
    
    /**
     * True if has "Number" element
     */
    boolean isSetNumber();
    
    /**
     * Sets the "Number" element
     */
    void setNumber(java.lang.String number);
    
    /**
     * Sets (as xml) the "Number" element
     */
    void xsetNumber(org.apache.xmlbeans.XmlString number);
    
    /**
     * Nils the "Number" element
     */
    void setNilNumber();
    
    /**
     * Unsets the "Number" element
     */
    void unsetNumber();
    
    /**
     * Gets the "ReceiveDateTime" element
     */
    java.util.Calendar getReceiveDateTime();
    
    /**
     * Gets (as xml) the "ReceiveDateTime" element
     */
    org.apache.xmlbeans.XmlDateTime xgetReceiveDateTime();
    
    /**
     * True if has "ReceiveDateTime" element
     */
    boolean isSetReceiveDateTime();
    
    /**
     * Sets the "ReceiveDateTime" element
     */
    void setReceiveDateTime(java.util.Calendar receiveDateTime);
    
    /**
     * Sets (as xml) the "ReceiveDateTime" element
     */
    void xsetReceiveDateTime(org.apache.xmlbeans.XmlDateTime receiveDateTime);
    
    /**
     * Unsets the "ReceiveDateTime" element
     */
    void unsetReceiveDateTime();
    
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
     * Gets the "Signatures" element
     */
    com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring getSignatures();
    
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
    void setSignatures(com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring signatures);
    
    /**
     * Appends and returns a new empty "Signatures" element
     */
    com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfstring addNewSignatures();
    
    /**
     * Nils the "Signatures" element
     */
    void setNilSignatures();
    
    /**
     * Unsets the "Signatures" element
     */
    void unsetSignatures();
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo newInstance() {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.ReceivedDocumentInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
