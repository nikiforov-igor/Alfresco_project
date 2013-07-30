/*
 * XML Type:  GateResponse
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Exceptions
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions;


/**
 * An XML GateResponse(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Exceptions).
 *
 * This is a complex type.
 */
public interface GateResponse extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(GateResponse.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("gateresponse142etype");
    
    /**
     * Gets the "AuthorizationErrors" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfAuthorizationError getAuthorizationErrors();
    
    /**
     * Tests for nil "AuthorizationErrors" element
     */
    boolean isNilAuthorizationErrors();
    
    /**
     * True if has "AuthorizationErrors" element
     */
    boolean isSetAuthorizationErrors();
    
    /**
     * Sets the "AuthorizationErrors" element
     */
    void setAuthorizationErrors(org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfAuthorizationError authorizationErrors);
    
    /**
     * Appends and returns a new empty "AuthorizationErrors" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfAuthorizationError addNewAuthorizationErrors();
    
    /**
     * Nils the "AuthorizationErrors" element
     */
    void setNilAuthorizationErrors();
    
    /**
     * Unsets the "AuthorizationErrors" element
     */
    void unsetAuthorizationErrors();
    
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
     * Gets the "OperatorMessage" element
     */
    java.lang.String getOperatorMessage();
    
    /**
     * Gets (as xml) the "OperatorMessage" element
     */
    org.apache.xmlbeans.XmlString xgetOperatorMessage();
    
    /**
     * Tests for nil "OperatorMessage" element
     */
    boolean isNilOperatorMessage();
    
    /**
     * True if has "OperatorMessage" element
     */
    boolean isSetOperatorMessage();
    
    /**
     * Sets the "OperatorMessage" element
     */
    void setOperatorMessage(java.lang.String operatorMessage);
    
    /**
     * Sets (as xml) the "OperatorMessage" element
     */
    void xsetOperatorMessage(org.apache.xmlbeans.XmlString operatorMessage);
    
    /**
     * Nils the "OperatorMessage" element
     */
    void setNilOperatorMessage();
    
    /**
     * Unsets the "OperatorMessage" element
     */
    void unsetOperatorMessage();
    
    /**
     * Gets the "ResponseType" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.EResponseType.Enum getResponseType();
    
    /**
     * Gets (as xml) the "ResponseType" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.EResponseType xgetResponseType();
    
    /**
     * True if has "ResponseType" element
     */
    boolean isSetResponseType();
    
    /**
     * Sets the "ResponseType" element
     */
    void setResponseType(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.EResponseType.Enum responseType);
    
    /**
     * Sets (as xml) the "ResponseType" element
     */
    void xsetResponseType(org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.EResponseType responseType);
    
    /**
     * Unsets the "ResponseType" element
     */
    void unsetResponseType();
    
    /**
     * Gets the "StackTrace" element
     */
    java.lang.String getStackTrace();
    
    /**
     * Gets (as xml) the "StackTrace" element
     */
    org.apache.xmlbeans.XmlString xgetStackTrace();
    
    /**
     * Tests for nil "StackTrace" element
     */
    boolean isNilStackTrace();
    
    /**
     * True if has "StackTrace" element
     */
    boolean isSetStackTrace();
    
    /**
     * Sets the "StackTrace" element
     */
    void setStackTrace(java.lang.String stackTrace);
    
    /**
     * Sets (as xml) the "StackTrace" element
     */
    void xsetStackTrace(org.apache.xmlbeans.XmlString stackTrace);
    
    /**
     * Nils the "StackTrace" element
     */
    void setNilStackTrace();
    
    /**
     * Unsets the "StackTrace" element
     */
    void unsetStackTrace();
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse newInstance() {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
