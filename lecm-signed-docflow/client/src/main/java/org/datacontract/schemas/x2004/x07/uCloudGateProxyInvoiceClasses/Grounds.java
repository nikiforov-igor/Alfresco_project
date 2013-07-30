/*
 * XML Type:  Grounds
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses;


/**
 * An XML Grounds(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses).
 *
 * This is a complex type.
 */
public interface Grounds extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(Grounds.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("grounds4735type");
    
    /**
     * Gets the "AdditionalInfo" element
     */
    java.lang.String getAdditionalInfo();
    
    /**
     * Gets (as xml) the "AdditionalInfo" element
     */
    org.apache.xmlbeans.XmlString xgetAdditionalInfo();
    
    /**
     * Tests for nil "AdditionalInfo" element
     */
    boolean isNilAdditionalInfo();
    
    /**
     * True if has "AdditionalInfo" element
     */
    boolean isSetAdditionalInfo();
    
    /**
     * Sets the "AdditionalInfo" element
     */
    void setAdditionalInfo(java.lang.String additionalInfo);
    
    /**
     * Sets (as xml) the "AdditionalInfo" element
     */
    void xsetAdditionalInfo(org.apache.xmlbeans.XmlString additionalInfo);
    
    /**
     * Nils the "AdditionalInfo" element
     */
    void setNilAdditionalInfo();
    
    /**
     * Unsets the "AdditionalInfo" element
     */
    void unsetAdditionalInfo();
    
    /**
     * Gets the "DocumentDate" element
     */
    java.lang.String getDocumentDate();
    
    /**
     * Gets (as xml) the "DocumentDate" element
     */
    org.apache.xmlbeans.XmlString xgetDocumentDate();
    
    /**
     * Tests for nil "DocumentDate" element
     */
    boolean isNilDocumentDate();
    
    /**
     * True if has "DocumentDate" element
     */
    boolean isSetDocumentDate();
    
    /**
     * Sets the "DocumentDate" element
     */
    void setDocumentDate(java.lang.String documentDate);
    
    /**
     * Sets (as xml) the "DocumentDate" element
     */
    void xsetDocumentDate(org.apache.xmlbeans.XmlString documentDate);
    
    /**
     * Nils the "DocumentDate" element
     */
    void setNilDocumentDate();
    
    /**
     * Unsets the "DocumentDate" element
     */
    void unsetDocumentDate();
    
    /**
     * Gets the "DocumentName" element
     */
    java.lang.String getDocumentName();
    
    /**
     * Gets (as xml) the "DocumentName" element
     */
    org.apache.xmlbeans.XmlString xgetDocumentName();
    
    /**
     * Tests for nil "DocumentName" element
     */
    boolean isNilDocumentName();
    
    /**
     * True if has "DocumentName" element
     */
    boolean isSetDocumentName();
    
    /**
     * Sets the "DocumentName" element
     */
    void setDocumentName(java.lang.String documentName);
    
    /**
     * Sets (as xml) the "DocumentName" element
     */
    void xsetDocumentName(org.apache.xmlbeans.XmlString documentName);
    
    /**
     * Nils the "DocumentName" element
     */
    void setNilDocumentName();
    
    /**
     * Unsets the "DocumentName" element
     */
    void unsetDocumentName();
    
    /**
     * Gets the "DocumentNumber" element
     */
    java.lang.String getDocumentNumber();
    
    /**
     * Gets (as xml) the "DocumentNumber" element
     */
    org.apache.xmlbeans.XmlString xgetDocumentNumber();
    
    /**
     * Tests for nil "DocumentNumber" element
     */
    boolean isNilDocumentNumber();
    
    /**
     * True if has "DocumentNumber" element
     */
    boolean isSetDocumentNumber();
    
    /**
     * Sets the "DocumentNumber" element
     */
    void setDocumentNumber(java.lang.String documentNumber);
    
    /**
     * Sets (as xml) the "DocumentNumber" element
     */
    void xsetDocumentNumber(org.apache.xmlbeans.XmlString documentNumber);
    
    /**
     * Nils the "DocumentNumber" element
     */
    void setNilDocumentNumber();
    
    /**
     * Unsets the "DocumentNumber" element
     */
    void unsetDocumentNumber();
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds newInstance() {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
