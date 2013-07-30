/*
 * XML Type:  RegisterResponse
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration;


/**
 * An XML RegisterResponse(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration).
 *
 * This is a complex type.
 */
public interface RegisterResponse extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(RegisterResponse.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("registerresponse900btype");
    
    /**
     * Gets the "AbonentIdByOperator" element
     */
    java.lang.String getAbonentIdByOperator();
    
    /**
     * Gets (as xml) the "AbonentIdByOperator" element
     */
    com.microsoft.schemas.x2003.x10.serialization.Guid xgetAbonentIdByOperator();
    
    /**
     * Tests for nil "AbonentIdByOperator" element
     */
    boolean isNilAbonentIdByOperator();
    
    /**
     * True if has "AbonentIdByOperator" element
     */
    boolean isSetAbonentIdByOperator();
    
    /**
     * Sets the "AbonentIdByOperator" element
     */
    void setAbonentIdByOperator(java.lang.String abonentIdByOperator);
    
    /**
     * Sets (as xml) the "AbonentIdByOperator" element
     */
    void xsetAbonentIdByOperator(com.microsoft.schemas.x2003.x10.serialization.Guid abonentIdByOperator);
    
    /**
     * Nils the "AbonentIdByOperator" element
     */
    void setNilAbonentIdByOperator();
    
    /**
     * Unsets the "AbonentIdByOperator" element
     */
    void unsetAbonentIdByOperator();
    
    /**
     * Gets the "Certificate" element
     */
    byte[] getCertificate();
    
    /**
     * Gets (as xml) the "Certificate" element
     */
    org.apache.xmlbeans.XmlBase64Binary xgetCertificate();
    
    /**
     * Tests for nil "Certificate" element
     */
    boolean isNilCertificate();
    
    /**
     * True if has "Certificate" element
     */
    boolean isSetCertificate();
    
    /**
     * Sets the "Certificate" element
     */
    void setCertificate(byte[] certificate);
    
    /**
     * Sets (as xml) the "Certificate" element
     */
    void xsetCertificate(org.apache.xmlbeans.XmlBase64Binary certificate);
    
    /**
     * Nils the "Certificate" element
     */
    void setNilCertificate();
    
    /**
     * Unsets the "Certificate" element
     */
    void unsetCertificate();
    
    /**
     * Gets the "ResponseDateTime" element
     */
    java.lang.String getResponseDateTime();
    
    /**
     * Gets (as xml) the "ResponseDateTime" element
     */
    org.apache.xmlbeans.XmlString xgetResponseDateTime();
    
    /**
     * Tests for nil "ResponseDateTime" element
     */
    boolean isNilResponseDateTime();
    
    /**
     * True if has "ResponseDateTime" element
     */
    boolean isSetResponseDateTime();
    
    /**
     * Sets the "ResponseDateTime" element
     */
    void setResponseDateTime(java.lang.String responseDateTime);
    
    /**
     * Sets (as xml) the "ResponseDateTime" element
     */
    void xsetResponseDateTime(org.apache.xmlbeans.XmlString responseDateTime);
    
    /**
     * Nils the "ResponseDateTime" element
     */
    void setNilResponseDateTime();
    
    /**
     * Unsets the "ResponseDateTime" element
     */
    void unsetResponseDateTime();
    
    /**
     * Gets the "ResultCode" element
     */
    java.lang.String getResultCode();
    
    /**
     * Gets (as xml) the "ResultCode" element
     */
    org.apache.xmlbeans.XmlString xgetResultCode();
    
    /**
     * Tests for nil "ResultCode" element
     */
    boolean isNilResultCode();
    
    /**
     * True if has "ResultCode" element
     */
    boolean isSetResultCode();
    
    /**
     * Sets the "ResultCode" element
     */
    void setResultCode(java.lang.String resultCode);
    
    /**
     * Sets (as xml) the "ResultCode" element
     */
    void xsetResultCode(org.apache.xmlbeans.XmlString resultCode);
    
    /**
     * Nils the "ResultCode" element
     */
    void setNilResultCode();
    
    /**
     * Unsets the "ResultCode" element
     */
    void unsetResultCode();
    
    /**
     * Gets the "ResultMessage" element
     */
    java.lang.String getResultMessage();
    
    /**
     * Gets (as xml) the "ResultMessage" element
     */
    org.apache.xmlbeans.XmlString xgetResultMessage();
    
    /**
     * Tests for nil "ResultMessage" element
     */
    boolean isNilResultMessage();
    
    /**
     * True if has "ResultMessage" element
     */
    boolean isSetResultMessage();
    
    /**
     * Sets the "ResultMessage" element
     */
    void setResultMessage(java.lang.String resultMessage);
    
    /**
     * Sets (as xml) the "ResultMessage" element
     */
    void xsetResultMessage(org.apache.xmlbeans.XmlString resultMessage);
    
    /**
     * Nils the "ResultMessage" element
     */
    void setNilResultMessage();
    
    /**
     * Unsets the "ResultMessage" element
     */
    void unsetResultMessage();
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse newInstance() {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
