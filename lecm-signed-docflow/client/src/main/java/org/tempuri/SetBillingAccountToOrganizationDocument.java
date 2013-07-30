/*
 * An XML document type.
 * Localname: SetBillingAccountToOrganization
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.SetBillingAccountToOrganizationDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri;


/**
 * A document containing one SetBillingAccountToOrganization(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public interface SetBillingAccountToOrganizationDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(SetBillingAccountToOrganizationDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("setbillingaccounttoorganizationf21cdoctype");
    
    /**
     * Gets the "SetBillingAccountToOrganization" element
     */
    org.tempuri.SetBillingAccountToOrganizationDocument.SetBillingAccountToOrganization getSetBillingAccountToOrganization();
    
    /**
     * Sets the "SetBillingAccountToOrganization" element
     */
    void setSetBillingAccountToOrganization(org.tempuri.SetBillingAccountToOrganizationDocument.SetBillingAccountToOrganization setBillingAccountToOrganization);
    
    /**
     * Appends and returns a new empty "SetBillingAccountToOrganization" element
     */
    org.tempuri.SetBillingAccountToOrganizationDocument.SetBillingAccountToOrganization addNewSetBillingAccountToOrganization();
    
    /**
     * An XML SetBillingAccountToOrganization(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public interface SetBillingAccountToOrganization extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(SetBillingAccountToOrganization.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("setbillingaccounttoorganization648aelemtype");
        
        /**
         * Gets the "operatorCode" element
         */
        java.lang.String getOperatorCode();
        
        /**
         * Gets (as xml) the "operatorCode" element
         */
        org.apache.xmlbeans.XmlString xgetOperatorCode();
        
        /**
         * Tests for nil "operatorCode" element
         */
        boolean isNilOperatorCode();
        
        /**
         * True if has "operatorCode" element
         */
        boolean isSetOperatorCode();
        
        /**
         * Sets the "operatorCode" element
         */
        void setOperatorCode(java.lang.String operatorCode);
        
        /**
         * Sets (as xml) the "operatorCode" element
         */
        void xsetOperatorCode(org.apache.xmlbeans.XmlString operatorCode);
        
        /**
         * Nils the "operatorCode" element
         */
        void setNilOperatorCode();
        
        /**
         * Unsets the "operatorCode" element
         */
        void unsetOperatorCode();
        
        /**
         * Gets the "signature" element
         */
        byte[] getSignature();
        
        /**
         * Gets (as xml) the "signature" element
         */
        org.apache.xmlbeans.XmlBase64Binary xgetSignature();
        
        /**
         * Tests for nil "signature" element
         */
        boolean isNilSignature();
        
        /**
         * True if has "signature" element
         */
        boolean isSetSignature();
        
        /**
         * Sets the "signature" element
         */
        void setSignature(byte[] signature);
        
        /**
         * Sets (as xml) the "signature" element
         */
        void xsetSignature(org.apache.xmlbeans.XmlBase64Binary signature);
        
        /**
         * Nils the "signature" element
         */
        void setNilSignature();
        
        /**
         * Unsets the "signature" element
         */
        void unsetSignature();
        
        /**
         * Gets the "billingLogin" element
         */
        java.lang.String getBillingLogin();
        
        /**
         * Gets (as xml) the "billingLogin" element
         */
        org.apache.xmlbeans.XmlString xgetBillingLogin();
        
        /**
         * Tests for nil "billingLogin" element
         */
        boolean isNilBillingLogin();
        
        /**
         * True if has "billingLogin" element
         */
        boolean isSetBillingLogin();
        
        /**
         * Sets the "billingLogin" element
         */
        void setBillingLogin(java.lang.String billingLogin);
        
        /**
         * Sets (as xml) the "billingLogin" element
         */
        void xsetBillingLogin(org.apache.xmlbeans.XmlString billingLogin);
        
        /**
         * Nils the "billingLogin" element
         */
        void setNilBillingLogin();
        
        /**
         * Unsets the "billingLogin" element
         */
        void unsetBillingLogin();
        
        /**
         * Gets the "billingPassword" element
         */
        java.lang.String getBillingPassword();
        
        /**
         * Gets (as xml) the "billingPassword" element
         */
        org.apache.xmlbeans.XmlString xgetBillingPassword();
        
        /**
         * Tests for nil "billingPassword" element
         */
        boolean isNilBillingPassword();
        
        /**
         * True if has "billingPassword" element
         */
        boolean isSetBillingPassword();
        
        /**
         * Sets the "billingPassword" element
         */
        void setBillingPassword(java.lang.String billingPassword);
        
        /**
         * Sets (as xml) the "billingPassword" element
         */
        void xsetBillingPassword(org.apache.xmlbeans.XmlString billingPassword);
        
        /**
         * Nils the "billingPassword" element
         */
        void setNilBillingPassword();
        
        /**
         * Unsets the "billingPassword" element
         */
        void unsetBillingPassword();
        
        /**
         * Gets the "existedAccount" element
         */
        boolean getExistedAccount();
        
        /**
         * Gets (as xml) the "existedAccount" element
         */
        org.apache.xmlbeans.XmlBoolean xgetExistedAccount();
        
        /**
         * True if has "existedAccount" element
         */
        boolean isSetExistedAccount();
        
        /**
         * Sets the "existedAccount" element
         */
        void setExistedAccount(boolean existedAccount);
        
        /**
         * Sets (as xml) the "existedAccount" element
         */
        void xsetExistedAccount(org.apache.xmlbeans.XmlBoolean existedAccount);
        
        /**
         * Unsets the "existedAccount" element
         */
        void unsetExistedAccount();
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static org.tempuri.SetBillingAccountToOrganizationDocument.SetBillingAccountToOrganization newInstance() {
              return (org.tempuri.SetBillingAccountToOrganizationDocument.SetBillingAccountToOrganization) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static org.tempuri.SetBillingAccountToOrganizationDocument.SetBillingAccountToOrganization newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (org.tempuri.SetBillingAccountToOrganizationDocument.SetBillingAccountToOrganization) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.tempuri.SetBillingAccountToOrganizationDocument newInstance() {
          return (org.tempuri.SetBillingAccountToOrganizationDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.tempuri.SetBillingAccountToOrganizationDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.tempuri.SetBillingAccountToOrganizationDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.tempuri.SetBillingAccountToOrganizationDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.SetBillingAccountToOrganizationDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.tempuri.SetBillingAccountToOrganizationDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.SetBillingAccountToOrganizationDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.tempuri.SetBillingAccountToOrganizationDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.SetBillingAccountToOrganizationDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.tempuri.SetBillingAccountToOrganizationDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.SetBillingAccountToOrganizationDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.tempuri.SetBillingAccountToOrganizationDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.SetBillingAccountToOrganizationDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.tempuri.SetBillingAccountToOrganizationDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.SetBillingAccountToOrganizationDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.tempuri.SetBillingAccountToOrganizationDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.SetBillingAccountToOrganizationDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.tempuri.SetBillingAccountToOrganizationDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.SetBillingAccountToOrganizationDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.tempuri.SetBillingAccountToOrganizationDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.SetBillingAccountToOrganizationDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.tempuri.SetBillingAccountToOrganizationDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.SetBillingAccountToOrganizationDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.tempuri.SetBillingAccountToOrganizationDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.SetBillingAccountToOrganizationDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.tempuri.SetBillingAccountToOrganizationDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.SetBillingAccountToOrganizationDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.tempuri.SetBillingAccountToOrganizationDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.SetBillingAccountToOrganizationDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.tempuri.SetBillingAccountToOrganizationDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.SetBillingAccountToOrganizationDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.SetBillingAccountToOrganizationDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.SetBillingAccountToOrganizationDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.SetBillingAccountToOrganizationDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.SetBillingAccountToOrganizationDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
