/*
 * XML Type:  EDocumentType
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxy;


/**
 * An XML EDocumentType(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy).
 *
 * This is an atomic type that is a restriction of org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType.
 */
public interface EDocumentType extends org.apache.xmlbeans.XmlString
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(EDocumentType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("edocumenttype1000type");
    
    org.apache.xmlbeans.StringEnumAbstractBase enumValue();
    void set(org.apache.xmlbeans.StringEnumAbstractBase e);
    
    static final Enum UNKNOWN = Enum.forString("Unknown");
    static final Enum NON_FORMALIZED = Enum.forString("NonFormalized");
    static final Enum INVOICE = Enum.forString("Invoice");
    static final Enum INVOICE_RECEIPT = Enum.forString("InvoiceReceipt");
    static final Enum INVOICE_CONFIRMATION = Enum.forString("InvoiceConfirmation");
    static final Enum INVOICE_REVISION = Enum.forString("InvoiceRevision");
    static final Enum INVOICE_CORRECTION = Enum.forString("InvoiceCorrection");
    static final Enum INVOICE_CORRECTION_REVISION = Enum.forString("InvoiceCorrectionRevision");
    static final Enum TORG_12 = Enum.forString("Torg12");
    static final Enum AKT = Enum.forString("Akt");
    static final Enum TORG_12_BUYER_TITLE = Enum.forString("Torg12BuyerTitle");
    static final Enum AKT_BUYER_TITLE = Enum.forString("AktBuyerTitle");
    static final Enum RECEIPT_NOTIFICATION = Enum.forString("ReceiptNotification");
    static final Enum CORRECTION_REQUEST = Enum.forString("CorrectionRequest");
    
    static final int INT_UNKNOWN = Enum.INT_UNKNOWN;
    static final int INT_NON_FORMALIZED = Enum.INT_NON_FORMALIZED;
    static final int INT_INVOICE = Enum.INT_INVOICE;
    static final int INT_INVOICE_RECEIPT = Enum.INT_INVOICE_RECEIPT;
    static final int INT_INVOICE_CONFIRMATION = Enum.INT_INVOICE_CONFIRMATION;
    static final int INT_INVOICE_REVISION = Enum.INT_INVOICE_REVISION;
    static final int INT_INVOICE_CORRECTION = Enum.INT_INVOICE_CORRECTION;
    static final int INT_INVOICE_CORRECTION_REVISION = Enum.INT_INVOICE_CORRECTION_REVISION;
    static final int INT_TORG_12 = Enum.INT_TORG_12;
    static final int INT_AKT = Enum.INT_AKT;
    static final int INT_TORG_12_BUYER_TITLE = Enum.INT_TORG_12_BUYER_TITLE;
    static final int INT_AKT_BUYER_TITLE = Enum.INT_AKT_BUYER_TITLE;
    static final int INT_RECEIPT_NOTIFICATION = Enum.INT_RECEIPT_NOTIFICATION;
    static final int INT_CORRECTION_REQUEST = Enum.INT_CORRECTION_REQUEST;
    
    /**
     * Enumeration value class for org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType.
     * These enum values can be used as follows:
     * <pre>
     * enum.toString(); // returns the string value of the enum
     * enum.intValue(); // returns an int value, useful for switches
     * // e.g., case Enum.INT_UNKNOWN
     * Enum.forString(s); // returns the enum value for a string
     * Enum.forInt(i); // returns the enum value for an int
     * </pre>
     * Enumeration objects are immutable singleton objects that
     * can be compared using == object equality. They have no
     * public constructor. See the constants defined within this
     * class for all the valid values.
     */
    static final class Enum extends org.apache.xmlbeans.StringEnumAbstractBase
    {
        /**
         * Returns the enum value for a string, or null if none.
         */
        public static Enum forString(java.lang.String s)
            { return (Enum)table.forString(s); }
        /**
         * Returns the enum value corresponding to an int, or null if none.
         */
        public static Enum forInt(int i)
            { return (Enum)table.forInt(i); }
        
        private Enum(java.lang.String s, int i)
            { super(s, i); }
        
        static final int INT_UNKNOWN = 1;
        static final int INT_NON_FORMALIZED = 2;
        static final int INT_INVOICE = 3;
        static final int INT_INVOICE_RECEIPT = 4;
        static final int INT_INVOICE_CONFIRMATION = 5;
        static final int INT_INVOICE_REVISION = 6;
        static final int INT_INVOICE_CORRECTION = 7;
        static final int INT_INVOICE_CORRECTION_REVISION = 8;
        static final int INT_TORG_12 = 9;
        static final int INT_AKT = 10;
        static final int INT_TORG_12_BUYER_TITLE = 11;
        static final int INT_AKT_BUYER_TITLE = 12;
        static final int INT_RECEIPT_NOTIFICATION = 13;
        static final int INT_CORRECTION_REQUEST = 14;
        
        public static final org.apache.xmlbeans.StringEnumAbstractBase.Table table =
            new org.apache.xmlbeans.StringEnumAbstractBase.Table
        (
            new Enum[]
            {
                new Enum("Unknown", INT_UNKNOWN),
                new Enum("NonFormalized", INT_NON_FORMALIZED),
                new Enum("Invoice", INT_INVOICE),
                new Enum("InvoiceReceipt", INT_INVOICE_RECEIPT),
                new Enum("InvoiceConfirmation", INT_INVOICE_CONFIRMATION),
                new Enum("InvoiceRevision", INT_INVOICE_REVISION),
                new Enum("InvoiceCorrection", INT_INVOICE_CORRECTION),
                new Enum("InvoiceCorrectionRevision", INT_INVOICE_CORRECTION_REVISION),
                new Enum("Torg12", INT_TORG_12),
                new Enum("Akt", INT_AKT),
                new Enum("Torg12BuyerTitle", INT_TORG_12_BUYER_TITLE),
                new Enum("AktBuyerTitle", INT_AKT_BUYER_TITLE),
                new Enum("ReceiptNotification", INT_RECEIPT_NOTIFICATION),
                new Enum("CorrectionRequest", INT_CORRECTION_REQUEST),
            }
        );
        private static final long serialVersionUID = 1L;
        private java.lang.Object readResolve() { return forInt(intValue()); } 
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType newValue(java.lang.Object obj) {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType) type.newValue( obj ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType newInstance() {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
