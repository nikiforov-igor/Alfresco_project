/*
 * An XML document type.
 * Localname: GenerateTorg12XmlForSeller
 * Namespace: http://tempuri.org/
 * Java type: org.tempuri.GenerateTorg12XmlForSellerDocument
 *
 * Automatically generated - do not modify.
 */
package org.tempuri;


/**
 * A document containing one GenerateTorg12XmlForSeller(@http://tempuri.org/) element.
 *
 * This is a complex type.
 */
public interface GenerateTorg12XmlForSellerDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(GenerateTorg12XmlForSellerDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("generatetorg12xmlforseller5f63doctype");
    
    /**
     * Gets the "GenerateTorg12XmlForSeller" element
     */
    org.tempuri.GenerateTorg12XmlForSellerDocument.GenerateTorg12XmlForSeller getGenerateTorg12XmlForSeller();
    
    /**
     * Sets the "GenerateTorg12XmlForSeller" element
     */
    void setGenerateTorg12XmlForSeller(org.tempuri.GenerateTorg12XmlForSellerDocument.GenerateTorg12XmlForSeller generateTorg12XmlForSeller);
    
    /**
     * Appends and returns a new empty "GenerateTorg12XmlForSeller" element
     */
    org.tempuri.GenerateTorg12XmlForSellerDocument.GenerateTorg12XmlForSeller addNewGenerateTorg12XmlForSeller();
    
    /**
     * An XML GenerateTorg12XmlForSeller(@http://tempuri.org/).
     *
     * This is a complex type.
     */
    public interface GenerateTorg12XmlForSeller extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(GenerateTorg12XmlForSeller.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("generatetorg12xmlforseller00b0elemtype");
        
        /**
         * Gets the "vendorId" element
         */
        java.lang.String getVendorId();
        
        /**
         * Gets (as xml) the "vendorId" element
         */
        org.apache.xmlbeans.XmlString xgetVendorId();
        
        /**
         * Tests for nil "vendorId" element
         */
        boolean isNilVendorId();
        
        /**
         * True if has "vendorId" element
         */
        boolean isSetVendorId();
        
        /**
         * Sets the "vendorId" element
         */
        void setVendorId(java.lang.String vendorId);
        
        /**
         * Sets (as xml) the "vendorId" element
         */
        void xsetVendorId(org.apache.xmlbeans.XmlString vendorId);
        
        /**
         * Nils the "vendorId" element
         */
        void setNilVendorId();
        
        /**
         * Unsets the "vendorId" element
         */
        void unsetVendorId();
        
        /**
         * Gets the "buyerId" element
         */
        java.lang.String getBuyerId();
        
        /**
         * Gets (as xml) the "buyerId" element
         */
        org.apache.xmlbeans.XmlString xgetBuyerId();
        
        /**
         * Tests for nil "buyerId" element
         */
        boolean isNilBuyerId();
        
        /**
         * True if has "buyerId" element
         */
        boolean isSetBuyerId();
        
        /**
         * Sets the "buyerId" element
         */
        void setBuyerId(java.lang.String buyerId);
        
        /**
         * Sets (as xml) the "buyerId" element
         */
        void xsetBuyerId(org.apache.xmlbeans.XmlString buyerId);
        
        /**
         * Nils the "buyerId" element
         */
        void setNilBuyerId();
        
        /**
         * Unsets the "buyerId" element
         */
        void unsetBuyerId();
        
        /**
         * Gets the "opInfo" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo getOpInfo();
        
        /**
         * Tests for nil "opInfo" element
         */
        boolean isNilOpInfo();
        
        /**
         * True if has "opInfo" element
         */
        boolean isSetOpInfo();
        
        /**
         * Sets the "opInfo" element
         */
        void setOpInfo(org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo opInfo);
        
        /**
         * Appends and returns a new empty "opInfo" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxy.OperatorInfo addNewOpInfo();
        
        /**
         * Nils the "opInfo" element
         */
        void setNilOpInfo();
        
        /**
         * Unsets the "opInfo" element
         */
        void unsetOpInfo();
        
        /**
         * Gets the "date" element
         */
        java.util.Calendar getDate();
        
        /**
         * Gets (as xml) the "date" element
         */
        org.apache.xmlbeans.XmlDateTime xgetDate();
        
        /**
         * True if has "date" element
         */
        boolean isSetDate();
        
        /**
         * Sets the "date" element
         */
        void setDate(java.util.Calendar date);
        
        /**
         * Sets (as xml) the "date" element
         */
        void xsetDate(org.apache.xmlbeans.XmlDateTime date);
        
        /**
         * Unsets the "date" element
         */
        void unsetDate();
        
        /**
         * Gets the "info" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo getInfo();
        
        /**
         * Tests for nil "info" element
         */
        boolean isNilInfo();
        
        /**
         * True if has "info" element
         */
        boolean isSetInfo();
        
        /**
         * Sets the "info" element
         */
        void setInfo(org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo info);
        
        /**
         * Appends and returns a new empty "info" element
         */
        org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo addNewInfo();
        
        /**
         * Nils the "info" element
         */
        void setNilInfo();
        
        /**
         * Unsets the "info" element
         */
        void unsetInfo();
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static org.tempuri.GenerateTorg12XmlForSellerDocument.GenerateTorg12XmlForSeller newInstance() {
              return (org.tempuri.GenerateTorg12XmlForSellerDocument.GenerateTorg12XmlForSeller) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static org.tempuri.GenerateTorg12XmlForSellerDocument.GenerateTorg12XmlForSeller newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (org.tempuri.GenerateTorg12XmlForSellerDocument.GenerateTorg12XmlForSeller) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.tempuri.GenerateTorg12XmlForSellerDocument newInstance() {
          return (org.tempuri.GenerateTorg12XmlForSellerDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.tempuri.GenerateTorg12XmlForSellerDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.tempuri.GenerateTorg12XmlForSellerDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.tempuri.GenerateTorg12XmlForSellerDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateTorg12XmlForSellerDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.tempuri.GenerateTorg12XmlForSellerDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateTorg12XmlForSellerDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.tempuri.GenerateTorg12XmlForSellerDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateTorg12XmlForSellerDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.tempuri.GenerateTorg12XmlForSellerDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateTorg12XmlForSellerDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.tempuri.GenerateTorg12XmlForSellerDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateTorg12XmlForSellerDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.tempuri.GenerateTorg12XmlForSellerDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateTorg12XmlForSellerDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.tempuri.GenerateTorg12XmlForSellerDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateTorg12XmlForSellerDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.tempuri.GenerateTorg12XmlForSellerDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateTorg12XmlForSellerDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.tempuri.GenerateTorg12XmlForSellerDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateTorg12XmlForSellerDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.tempuri.GenerateTorg12XmlForSellerDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.tempuri.GenerateTorg12XmlForSellerDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.tempuri.GenerateTorg12XmlForSellerDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateTorg12XmlForSellerDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.tempuri.GenerateTorg12XmlForSellerDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateTorg12XmlForSellerDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.tempuri.GenerateTorg12XmlForSellerDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateTorg12XmlForSellerDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.tempuri.GenerateTorg12XmlForSellerDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.tempuri.GenerateTorg12XmlForSellerDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.GenerateTorg12XmlForSellerDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.GenerateTorg12XmlForSellerDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.tempuri.GenerateTorg12XmlForSellerDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.tempuri.GenerateTorg12XmlForSellerDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
