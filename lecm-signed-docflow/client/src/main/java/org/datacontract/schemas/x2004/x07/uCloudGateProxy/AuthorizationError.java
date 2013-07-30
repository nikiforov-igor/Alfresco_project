/*
 * XML Type:  AuthorizationError
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxy;


/**
 * An XML AuthorizationError(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy).
 *
 * This is a complex type.
 */
public interface AuthorizationError extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(AuthorizationError.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("authorizationerror1087type");
    
    /**
     * Gets the "AuthenticationType" element
     */
    java.util.List getAuthenticationType();
    
    /**
     * Gets (as xml) the "AuthenticationType" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxy.EOperatorAuthenticationType xgetAuthenticationType();
    
    /**
     * True if has "AuthenticationType" element
     */
    boolean isSetAuthenticationType();
    
    /**
     * Sets the "AuthenticationType" element
     */
    void setAuthenticationType(java.util.List authenticationType);
    
    /**
     * Sets (as xml) the "AuthenticationType" element
     */
    void xsetAuthenticationType(org.datacontract.schemas.x2004.x07.uCloudGateProxy.EOperatorAuthenticationType authenticationType);
    
    /**
     * Unsets the "AuthenticationType" element
     */
    void unsetAuthenticationType();
    
    /**
     * Gets the "CertificateIssuerName" element
     */
    java.lang.String getCertificateIssuerName();
    
    /**
     * Gets (as xml) the "CertificateIssuerName" element
     */
    org.apache.xmlbeans.XmlString xgetCertificateIssuerName();
    
    /**
     * Tests for nil "CertificateIssuerName" element
     */
    boolean isNilCertificateIssuerName();
    
    /**
     * True if has "CertificateIssuerName" element
     */
    boolean isSetCertificateIssuerName();
    
    /**
     * Sets the "CertificateIssuerName" element
     */
    void setCertificateIssuerName(java.lang.String certificateIssuerName);
    
    /**
     * Sets (as xml) the "CertificateIssuerName" element
     */
    void xsetCertificateIssuerName(org.apache.xmlbeans.XmlString certificateIssuerName);
    
    /**
     * Nils the "CertificateIssuerName" element
     */
    void setNilCertificateIssuerName();
    
    /**
     * Unsets the "CertificateIssuerName" element
     */
    void unsetCertificateIssuerName();
    
    /**
     * Gets the "CertificateThumbprint" element
     */
    java.lang.String getCertificateThumbprint();
    
    /**
     * Gets (as xml) the "CertificateThumbprint" element
     */
    org.apache.xmlbeans.XmlString xgetCertificateThumbprint();
    
    /**
     * Tests for nil "CertificateThumbprint" element
     */
    boolean isNilCertificateThumbprint();
    
    /**
     * True if has "CertificateThumbprint" element
     */
    boolean isSetCertificateThumbprint();
    
    /**
     * Sets the "CertificateThumbprint" element
     */
    void setCertificateThumbprint(java.lang.String certificateThumbprint);
    
    /**
     * Sets (as xml) the "CertificateThumbprint" element
     */
    void xsetCertificateThumbprint(org.apache.xmlbeans.XmlString certificateThumbprint);
    
    /**
     * Nils the "CertificateThumbprint" element
     */
    void setNilCertificateThumbprint();
    
    /**
     * Unsets the "CertificateThumbprint" element
     */
    void unsetCertificateThumbprint();
    
    /**
     * Gets the "EncryptedToken" element
     */
    java.lang.String getEncryptedToken();
    
    /**
     * Gets (as xml) the "EncryptedToken" element
     */
    org.apache.xmlbeans.XmlString xgetEncryptedToken();
    
    /**
     * Tests for nil "EncryptedToken" element
     */
    boolean isNilEncryptedToken();
    
    /**
     * True if has "EncryptedToken" element
     */
    boolean isSetEncryptedToken();
    
    /**
     * Sets the "EncryptedToken" element
     */
    void setEncryptedToken(java.lang.String encryptedToken);
    
    /**
     * Sets (as xml) the "EncryptedToken" element
     */
    void xsetEncryptedToken(org.apache.xmlbeans.XmlString encryptedToken);
    
    /**
     * Nils the "EncryptedToken" element
     */
    void setNilEncryptedToken();
    
    /**
     * Unsets the "EncryptedToken" element
     */
    void unsetEncryptedToken();
    
    /**
     * Gets the "Message" element
     */
    java.lang.String getMessage();
    
    /**
     * Gets (as xml) the "Message" element
     */
    org.apache.xmlbeans.XmlString xgetMessage();
    
    /**
     * Tests for nil "Message" element
     */
    boolean isNilMessage();
    
    /**
     * True if has "Message" element
     */
    boolean isSetMessage();
    
    /**
     * Sets the "Message" element
     */
    void setMessage(java.lang.String message);
    
    /**
     * Sets (as xml) the "Message" element
     */
    void xsetMessage(org.apache.xmlbeans.XmlString message);
    
    /**
     * Nils the "Message" element
     */
    void setNilMessage();
    
    /**
     * Unsets the "Message" element
     */
    void unsetMessage();
    
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
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError newInstance() {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.AuthorizationError) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
