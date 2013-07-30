/*
 * XML Type:  DocflowInfoBase
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxy;


/**
 * An XML DocflowInfoBase(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy).
 *
 * This is a complex type.
 */
public interface DocflowInfoBase extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(DocflowInfoBase.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("docflowinfobase27abtype");
    
    /**
     * Gets the "Description" element
     */
    java.lang.String getDescription();
    
    /**
     * Gets (as xml) the "Description" element
     */
    org.apache.xmlbeans.XmlString xgetDescription();
    
    /**
     * Tests for nil "Description" element
     */
    boolean isNilDescription();
    
    /**
     * True if has "Description" element
     */
    boolean isSetDescription();
    
    /**
     * Sets the "Description" element
     */
    void setDescription(java.lang.String description);
    
    /**
     * Sets (as xml) the "Description" element
     */
    void xsetDescription(org.apache.xmlbeans.XmlString description);
    
    /**
     * Nils the "Description" element
     */
    void setNilDescription();
    
    /**
     * Unsets the "Description" element
     */
    void unsetDescription();
    
    /**
     * Gets the "DocflowId" element
     */
    java.lang.String getDocflowId();
    
    /**
     * Gets (as xml) the "DocflowId" element
     */
    com.microsoft.schemas.x2003.x10.serialization.Guid xgetDocflowId();
    
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
     * Unsets the "DocflowId" element
     */
    void unsetDocflowId();
    
    /**
     * Gets the "IsInbound" element
     */
    boolean getIsInbound();
    
    /**
     * Gets (as xml) the "IsInbound" element
     */
    org.apache.xmlbeans.XmlBoolean xgetIsInbound();
    
    /**
     * True if has "IsInbound" element
     */
    boolean isSetIsInbound();
    
    /**
     * Sets the "IsInbound" element
     */
    void setIsInbound(boolean isInbound);
    
    /**
     * Sets (as xml) the "IsInbound" element
     */
    void xsetIsInbound(org.apache.xmlbeans.XmlBoolean isInbound);
    
    /**
     * Unsets the "IsInbound" element
     */
    void unsetIsInbound();
    
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
     * Gets the "Type" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocflowType.Enum getType();
    
    /**
     * Gets (as xml) the "Type" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocflowType xgetType();
    
    /**
     * True if has "Type" element
     */
    boolean isSetType();
    
    /**
     * Sets the "Type" element
     */
    void setType(org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocflowType.Enum type);
    
    /**
     * Sets (as xml) the "Type" element
     */
    void xsetType(org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocflowType type);
    
    /**
     * Unsets the "Type" element
     */
    void unsetType();
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase newInstance() {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
