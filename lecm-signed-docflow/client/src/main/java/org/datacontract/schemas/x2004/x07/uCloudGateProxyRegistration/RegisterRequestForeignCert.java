/*
 * XML Type:  RegisterRequestForeignCert
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration;


/**
 * An XML RegisterRequestForeignCert(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration).
 *
 * This is a complex type.
 */
public interface RegisterRequestForeignCert extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(RegisterRequestForeignCert.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("registerrequestforeigncert3743type");
    
    /**
     * Gets the "Email" element
     */
    java.lang.String getEmail();
    
    /**
     * Gets (as xml) the "Email" element
     */
    org.apache.xmlbeans.XmlString xgetEmail();
    
    /**
     * Tests for nil "Email" element
     */
    boolean isNilEmail();
    
    /**
     * True if has "Email" element
     */
    boolean isSetEmail();
    
    /**
     * Sets the "Email" element
     */
    void setEmail(java.lang.String email);
    
    /**
     * Sets (as xml) the "Email" element
     */
    void xsetEmail(org.apache.xmlbeans.XmlString email);
    
    /**
     * Nils the "Email" element
     */
    void setNilEmail();
    
    /**
     * Unsets the "Email" element
     */
    void unsetEmail();
    
    /**
     * Gets the "FullName" element
     */
    java.lang.String getFullName();
    
    /**
     * Gets (as xml) the "FullName" element
     */
    org.apache.xmlbeans.XmlString xgetFullName();
    
    /**
     * Tests for nil "FullName" element
     */
    boolean isNilFullName();
    
    /**
     * True if has "FullName" element
     */
    boolean isSetFullName();
    
    /**
     * Sets the "FullName" element
     */
    void setFullName(java.lang.String fullName);
    
    /**
     * Sets (as xml) the "FullName" element
     */
    void xsetFullName(org.apache.xmlbeans.XmlString fullName);
    
    /**
     * Nils the "FullName" element
     */
    void setNilFullName();
    
    /**
     * Unsets the "FullName" element
     */
    void unsetFullName();
    
    /**
     * Gets the "Inn" element
     */
    java.lang.String getInn();
    
    /**
     * Gets (as xml) the "Inn" element
     */
    org.apache.xmlbeans.XmlString xgetInn();
    
    /**
     * Tests for nil "Inn" element
     */
    boolean isNilInn();
    
    /**
     * Sets the "Inn" element
     */
    void setInn(java.lang.String inn);
    
    /**
     * Sets (as xml) the "Inn" element
     */
    void xsetInn(org.apache.xmlbeans.XmlString inn);
    
    /**
     * Nils the "Inn" element
     */
    void setNilInn();
    
    /**
     * Gets the "Kpp" element
     */
    java.lang.String getKpp();
    
    /**
     * Gets (as xml) the "Kpp" element
     */
    org.apache.xmlbeans.XmlString xgetKpp();
    
    /**
     * Tests for nil "Kpp" element
     */
    boolean isNilKpp();
    
    /**
     * True if has "Kpp" element
     */
    boolean isSetKpp();
    
    /**
     * Sets the "Kpp" element
     */
    void setKpp(java.lang.String kpp);
    
    /**
     * Sets (as xml) the "Kpp" element
     */
    void xsetKpp(org.apache.xmlbeans.XmlString kpp);
    
    /**
     * Nils the "Kpp" element
     */
    void setNilKpp();
    
    /**
     * Unsets the "Kpp" element
     */
    void unsetKpp();
    
    /**
     * Gets the "LocationAddress" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.AddressForRegistration getLocationAddress();
    
    /**
     * Tests for nil "LocationAddress" element
     */
    boolean isNilLocationAddress();
    
    /**
     * True if has "LocationAddress" element
     */
    boolean isSetLocationAddress();
    
    /**
     * Sets the "LocationAddress" element
     */
    void setLocationAddress(org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.AddressForRegistration locationAddress);
    
    /**
     * Appends and returns a new empty "LocationAddress" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.AddressForRegistration addNewLocationAddress();
    
    /**
     * Nils the "LocationAddress" element
     */
    void setNilLocationAddress();
    
    /**
     * Unsets the "LocationAddress" element
     */
    void unsetLocationAddress();
    
    /**
     * Gets the "Members" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.ArrayOfMember getMembers();
    
    /**
     * Tests for nil "Members" element
     */
    boolean isNilMembers();
    
    /**
     * True if has "Members" element
     */
    boolean isSetMembers();
    
    /**
     * Sets the "Members" element
     */
    void setMembers(org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.ArrayOfMember members);
    
    /**
     * Appends and returns a new empty "Members" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.ArrayOfMember addNewMembers();
    
    /**
     * Nils the "Members" element
     */
    void setNilMembers();
    
    /**
     * Unsets the "Members" element
     */
    void unsetMembers();
    
    /**
     * Gets the "MobliePhone" element
     */
    java.lang.String getMobliePhone();
    
    /**
     * Gets (as xml) the "MobliePhone" element
     */
    org.apache.xmlbeans.XmlString xgetMobliePhone();
    
    /**
     * Tests for nil "MobliePhone" element
     */
    boolean isNilMobliePhone();
    
    /**
     * True if has "MobliePhone" element
     */
    boolean isSetMobliePhone();
    
    /**
     * Sets the "MobliePhone" element
     */
    void setMobliePhone(java.lang.String mobliePhone);
    
    /**
     * Sets (as xml) the "MobliePhone" element
     */
    void xsetMobliePhone(org.apache.xmlbeans.XmlString mobliePhone);
    
    /**
     * Nils the "MobliePhone" element
     */
    void setNilMobliePhone();
    
    /**
     * Unsets the "MobliePhone" element
     */
    void unsetMobliePhone();
    
    /**
     * Gets the "Phone" element
     */
    java.lang.String getPhone();
    
    /**
     * Gets (as xml) the "Phone" element
     */
    org.apache.xmlbeans.XmlString xgetPhone();
    
    /**
     * Tests for nil "Phone" element
     */
    boolean isNilPhone();
    
    /**
     * True if has "Phone" element
     */
    boolean isSetPhone();
    
    /**
     * Sets the "Phone" element
     */
    void setPhone(java.lang.String phone);
    
    /**
     * Sets (as xml) the "Phone" element
     */
    void xsetPhone(org.apache.xmlbeans.XmlString phone);
    
    /**
     * Nils the "Phone" element
     */
    void setNilPhone();
    
    /**
     * Unsets the "Phone" element
     */
    void unsetPhone();
    
    /**
     * Gets the "PostalAddress" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.AddressForRegistration getPostalAddress();
    
    /**
     * Tests for nil "PostalAddress" element
     */
    boolean isNilPostalAddress();
    
    /**
     * True if has "PostalAddress" element
     */
    boolean isSetPostalAddress();
    
    /**
     * Sets the "PostalAddress" element
     */
    void setPostalAddress(org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.AddressForRegistration postalAddress);
    
    /**
     * Appends and returns a new empty "PostalAddress" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.AddressForRegistration addNewPostalAddress();
    
    /**
     * Nils the "PostalAddress" element
     */
    void setNilPostalAddress();
    
    /**
     * Unsets the "PostalAddress" element
     */
    void unsetPostalAddress();
    
    /**
     * Gets the "ShortName" element
     */
    java.lang.String getShortName();
    
    /**
     * Gets (as xml) the "ShortName" element
     */
    org.apache.xmlbeans.XmlString xgetShortName();
    
    /**
     * Tests for nil "ShortName" element
     */
    boolean isNilShortName();
    
    /**
     * Sets the "ShortName" element
     */
    void setShortName(java.lang.String shortName);
    
    /**
     * Sets (as xml) the "ShortName" element
     */
    void xsetShortName(org.apache.xmlbeans.XmlString shortName);
    
    /**
     * Nils the "ShortName" element
     */
    void setNilShortName();
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert newInstance() {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyRegistration.RegisterRequestForeignCert) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
