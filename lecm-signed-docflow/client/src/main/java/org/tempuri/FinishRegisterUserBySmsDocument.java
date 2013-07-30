/*
 * An XML document type.
 * Localname: FinishRegisterUserBySms
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.FinishRegisterUserBySmsDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri;


/**
 * A document containing one FinishRegisterUserBySms(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public interface FinishRegisterUserBySmsDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(FinishRegisterUserBySmsDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("finishregisteruserbysmse3bbdoctype");
    
    /**
     * Gets the "FinishRegisterUserBySms" element
     */
    org.tempuri.FinishRegisterUserBySmsDocument.FinishRegisterUserBySms getFinishRegisterUserBySms();
    
    /**
     * Sets the "FinishRegisterUserBySms" element
     */
    void setFinishRegisterUserBySms(org.tempuri.FinishRegisterUserBySmsDocument.FinishRegisterUserBySms finishRegisterUserBySms);
    
    /**
     * Appends and returns a new empty "FinishRegisterUserBySms" element
     */
    org.tempuri.FinishRegisterUserBySmsDocument.FinishRegisterUserBySms addNewFinishRegisterUserBySms();
    
    /**
     * An XML FinishRegisterUserBySms(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public interface FinishRegisterUserBySms extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(FinishRegisterUserBySms.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("finishregisteruserbysms8e08elemtype");
        
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
         * Gets the "mobileNumber" element
         */
        java.lang.String getMobileNumber();
        
        /**
         * Gets (as xml) the "mobileNumber" element
         */
        org.apache.xmlbeans.XmlString xgetMobileNumber();
        
        /**
         * Tests for nil "mobileNumber" element
         */
        boolean isNilMobileNumber();
        
        /**
         * True if has "mobileNumber" element
         */
        boolean isSetMobileNumber();
        
        /**
         * Sets the "mobileNumber" element
         */
        void setMobileNumber(java.lang.String mobileNumber);
        
        /**
         * Sets (as xml) the "mobileNumber" element
         */
        void xsetMobileNumber(org.apache.xmlbeans.XmlString mobileNumber);
        
        /**
         * Nils the "mobileNumber" element
         */
        void setNilMobileNumber();
        
        /**
         * Unsets the "mobileNumber" element
         */
        void unsetMobileNumber();
        
        /**
         * Gets the "partnerKey" element
         */
        java.lang.String getPartnerKey();
        
        /**
         * Gets (as xml) the "partnerKey" element
         */
        com.microsoft.schemas.x2003.x10.serialization.Guid xgetPartnerKey();
        
        /**
         * True if has "partnerKey" element
         */
        boolean isSetPartnerKey();
        
        /**
         * Sets the "partnerKey" element
         */
        void setPartnerKey(java.lang.String partnerKey);
        
        /**
         * Sets (as xml) the "partnerKey" element
         */
        void xsetPartnerKey(com.microsoft.schemas.x2003.x10.serialization.Guid partnerKey);
        
        /**
         * Unsets the "partnerKey" element
         */
        void unsetPartnerKey();
        
        /**
         * Gets the "inn" element
         */
        java.lang.String getInn();
        
        /**
         * Gets (as xml) the "inn" element
         */
        org.apache.xmlbeans.XmlString xgetInn();
        
        /**
         * Tests for nil "inn" element
         */
        boolean isNilInn();
        
        /**
         * True if has "inn" element
         */
        boolean isSetInn();
        
        /**
         * Sets the "inn" element
         */
        void setInn(java.lang.String inn);
        
        /**
         * Sets (as xml) the "inn" element
         */
        void xsetInn(org.apache.xmlbeans.XmlString inn);
        
        /**
         * Nils the "inn" element
         */
        void setNilInn();
        
        /**
         * Unsets the "inn" element
         */
        void unsetInn();
        
        /**
         * Gets the "kpp" element
         */
        java.lang.String getKpp();
        
        /**
         * Gets (as xml) the "kpp" element
         */
        org.apache.xmlbeans.XmlString xgetKpp();
        
        /**
         * Tests for nil "kpp" element
         */
        boolean isNilKpp();
        
        /**
         * True if has "kpp" element
         */
        boolean isSetKpp();
        
        /**
         * Sets the "kpp" element
         */
        void setKpp(java.lang.String kpp);
        
        /**
         * Sets (as xml) the "kpp" element
         */
        void xsetKpp(org.apache.xmlbeans.XmlString kpp);
        
        /**
         * Nils the "kpp" element
         */
        void setNilKpp();
        
        /**
         * Unsets the "kpp" element
         */
        void unsetKpp();
        
        /**
         * Gets the "password" element
         */
        java.lang.String getPassword();
        
        /**
         * Gets (as xml) the "password" element
         */
        org.apache.xmlbeans.XmlString xgetPassword();
        
        /**
         * Tests for nil "password" element
         */
        boolean isNilPassword();
        
        /**
         * True if has "password" element
         */
        boolean isSetPassword();
        
        /**
         * Sets the "password" element
         */
        void setPassword(java.lang.String password);
        
        /**
         * Sets (as xml) the "password" element
         */
        void xsetPassword(org.apache.xmlbeans.XmlString password);
        
        /**
         * Nils the "password" element
         */
        void setNilPassword();
        
        /**
         * Unsets the "password" element
         */
        void unsetPassword();
        
        /**
         * Gets the "userIdByPartnerSystem" element
         */
        java.lang.String getUserIdByPartnerSystem();
        
        /**
         * Gets (as xml) the "userIdByPartnerSystem" element
         */
        com.microsoft.schemas.x2003.x10.serialization.Guid xgetUserIdByPartnerSystem();
        
        /**
         * True if has "userIdByPartnerSystem" element
         */
        boolean isSetUserIdByPartnerSystem();
        
        /**
         * Sets the "userIdByPartnerSystem" element
         */
        void setUserIdByPartnerSystem(java.lang.String userIdByPartnerSystem);
        
        /**
         * Sets (as xml) the "userIdByPartnerSystem" element
         */
        void xsetUserIdByPartnerSystem(com.microsoft.schemas.x2003.x10.serialization.Guid userIdByPartnerSystem);
        
        /**
         * Unsets the "userIdByPartnerSystem" element
         */
        void unsetUserIdByPartnerSystem();
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static org.tempuri.FinishRegisterUserBySmsDocument.FinishRegisterUserBySms newInstance() {
              return (org.tempuri.FinishRegisterUserBySmsDocument.FinishRegisterUserBySms) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static org.tempuri.FinishRegisterUserBySmsDocument.FinishRegisterUserBySms newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (org.tempuri.FinishRegisterUserBySmsDocument.FinishRegisterUserBySms) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.tempuri.FinishRegisterUserBySmsDocument newInstance() {
          return (org.tempuri.FinishRegisterUserBySmsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.tempuri.FinishRegisterUserBySmsDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.tempuri.FinishRegisterUserBySmsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.tempuri.FinishRegisterUserBySmsDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.FinishRegisterUserBySmsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.tempuri.FinishRegisterUserBySmsDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.FinishRegisterUserBySmsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.tempuri.FinishRegisterUserBySmsDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.FinishRegisterUserBySmsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.tempuri.FinishRegisterUserBySmsDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.FinishRegisterUserBySmsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.tempuri.FinishRegisterUserBySmsDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.FinishRegisterUserBySmsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.tempuri.FinishRegisterUserBySmsDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.FinishRegisterUserBySmsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.tempuri.FinishRegisterUserBySmsDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.FinishRegisterUserBySmsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.tempuri.FinishRegisterUserBySmsDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.FinishRegisterUserBySmsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.tempuri.FinishRegisterUserBySmsDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.FinishRegisterUserBySmsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.tempuri.FinishRegisterUserBySmsDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.FinishRegisterUserBySmsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.tempuri.FinishRegisterUserBySmsDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.FinishRegisterUserBySmsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.tempuri.FinishRegisterUserBySmsDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.FinishRegisterUserBySmsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.tempuri.FinishRegisterUserBySmsDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.FinishRegisterUserBySmsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.tempuri.FinishRegisterUserBySmsDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.FinishRegisterUserBySmsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.FinishRegisterUserBySmsDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.FinishRegisterUserBySmsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.FinishRegisterUserBySmsDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.FinishRegisterUserBySmsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
