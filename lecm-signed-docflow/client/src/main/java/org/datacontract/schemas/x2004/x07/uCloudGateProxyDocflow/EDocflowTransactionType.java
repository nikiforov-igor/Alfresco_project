/*
 * XML Type:  EDocflowTransactionType
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Docflow
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow;


/**
 * An XML EDocflowTransactionType(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Docflow).
 *
 * This is an atomic type that is a restriction of org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType.
 */
public interface EDocflowTransactionType extends org.apache.xmlbeans.XmlString
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(EDocflowTransactionType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("edocflowtransactiontype5915type");
    
    org.apache.xmlbeans.StringEnumAbstractBase enumValue();
    void set(org.apache.xmlbeans.StringEnumAbstractBase e);
    
    static final Enum UNKNOWN = Enum.forString("Unknown");
    static final Enum INVOICE_INVOICE = Enum.forString("InvoiceInvoice");
    static final Enum INVOICE_OPERATOR_CONFIRMATION_TO_VENDOR = Enum.forString("InvoiceOperatorConfirmationToVendor");
    static final Enum INVOICE_OPERATOR_CONFIRMATION_TO_CUSTOMER = Enum.forString("InvoiceOperatorConfirmationToCustomer");
    static final Enum INVOICE_VENDOR_RN = Enum.forString("InvoiceVendorRN");
    static final Enum INVOICE_CUSTOMER_RN = Enum.forString("InvoiceCustomerRN");
    static final Enum INVOICE_CUSTOMER_INVOICE_RN = Enum.forString("InvoiceCustomerInvoiceRN");
    static final Enum INVOICE_OPERATOR_CONFIRMATION_TO_CUSTOMER_INVOICE_RN = Enum.forString("InvoiceOperatorConfirmationToCustomerInvoiceRN");
    static final Enum INVOICE_CUSTOMER_RN_TO_OPERATOR_CONFIRMATION_TO_CUSTOMER_INVOICE_RN = Enum.forString("InvoiceCustomerRNToOperatorConfirmationToCustomerInvoiceRN");
    static final Enum INVOICE_CUSTOMER_CORRECTION = Enum.forString("InvoiceCustomerCorrection");
    static final Enum INVOICE_VENDOR_RN_TO_CUSTOMER_CORRECTION = Enum.forString("InvoiceVendorRNToCustomerCorrection");
    static final Enum NO_RECIPIENT_SIGNATURE_REQUEST = Enum.forString("NoRecipientSignatureRequest");
    static final Enum WAITING_FOR_RECIPIENT_SIGNATURE = Enum.forString("WaitingForRecipientSignature");
    static final Enum WITH_RECIPIENT_SIGNATURE = Enum.forString("WithRecipientSignature");
    static final Enum SIGNATURE_REQUEST_REJECTED = Enum.forString("SignatureRequestRejected");
    static final Enum RECIPIENT_RECEIVE_NOTIFICATION = Enum.forString("RecipientReceiveNotification");
    
    static final int INT_UNKNOWN = Enum.INT_UNKNOWN;
    static final int INT_INVOICE_INVOICE = Enum.INT_INVOICE_INVOICE;
    static final int INT_INVOICE_OPERATOR_CONFIRMATION_TO_VENDOR = Enum.INT_INVOICE_OPERATOR_CONFIRMATION_TO_VENDOR;
    static final int INT_INVOICE_OPERATOR_CONFIRMATION_TO_CUSTOMER = Enum.INT_INVOICE_OPERATOR_CONFIRMATION_TO_CUSTOMER;
    static final int INT_INVOICE_VENDOR_RN = Enum.INT_INVOICE_VENDOR_RN;
    static final int INT_INVOICE_CUSTOMER_RN = Enum.INT_INVOICE_CUSTOMER_RN;
    static final int INT_INVOICE_CUSTOMER_INVOICE_RN = Enum.INT_INVOICE_CUSTOMER_INVOICE_RN;
    static final int INT_INVOICE_OPERATOR_CONFIRMATION_TO_CUSTOMER_INVOICE_RN = Enum.INT_INVOICE_OPERATOR_CONFIRMATION_TO_CUSTOMER_INVOICE_RN;
    static final int INT_INVOICE_CUSTOMER_RN_TO_OPERATOR_CONFIRMATION_TO_CUSTOMER_INVOICE_RN = Enum.INT_INVOICE_CUSTOMER_RN_TO_OPERATOR_CONFIRMATION_TO_CUSTOMER_INVOICE_RN;
    static final int INT_INVOICE_CUSTOMER_CORRECTION = Enum.INT_INVOICE_CUSTOMER_CORRECTION;
    static final int INT_INVOICE_VENDOR_RN_TO_CUSTOMER_CORRECTION = Enum.INT_INVOICE_VENDOR_RN_TO_CUSTOMER_CORRECTION;
    static final int INT_NO_RECIPIENT_SIGNATURE_REQUEST = Enum.INT_NO_RECIPIENT_SIGNATURE_REQUEST;
    static final int INT_WAITING_FOR_RECIPIENT_SIGNATURE = Enum.INT_WAITING_FOR_RECIPIENT_SIGNATURE;
    static final int INT_WITH_RECIPIENT_SIGNATURE = Enum.INT_WITH_RECIPIENT_SIGNATURE;
    static final int INT_SIGNATURE_REQUEST_REJECTED = Enum.INT_SIGNATURE_REQUEST_REJECTED;
    static final int INT_RECIPIENT_RECEIVE_NOTIFICATION = Enum.INT_RECIPIENT_RECEIVE_NOTIFICATION;
    
    /**
     * Enumeration value class for org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType.
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
        static final int INT_INVOICE_INVOICE = 2;
        static final int INT_INVOICE_OPERATOR_CONFIRMATION_TO_VENDOR = 3;
        static final int INT_INVOICE_OPERATOR_CONFIRMATION_TO_CUSTOMER = 4;
        static final int INT_INVOICE_VENDOR_RN = 5;
        static final int INT_INVOICE_CUSTOMER_RN = 6;
        static final int INT_INVOICE_CUSTOMER_INVOICE_RN = 7;
        static final int INT_INVOICE_OPERATOR_CONFIRMATION_TO_CUSTOMER_INVOICE_RN = 8;
        static final int INT_INVOICE_CUSTOMER_RN_TO_OPERATOR_CONFIRMATION_TO_CUSTOMER_INVOICE_RN = 9;
        static final int INT_INVOICE_CUSTOMER_CORRECTION = 10;
        static final int INT_INVOICE_VENDOR_RN_TO_CUSTOMER_CORRECTION = 11;
        static final int INT_NO_RECIPIENT_SIGNATURE_REQUEST = 12;
        static final int INT_WAITING_FOR_RECIPIENT_SIGNATURE = 13;
        static final int INT_WITH_RECIPIENT_SIGNATURE = 14;
        static final int INT_SIGNATURE_REQUEST_REJECTED = 15;
        static final int INT_RECIPIENT_RECEIVE_NOTIFICATION = 16;
        
        public static final org.apache.xmlbeans.StringEnumAbstractBase.Table table =
            new org.apache.xmlbeans.StringEnumAbstractBase.Table
        (
            new Enum[]
            {
                new Enum("Unknown", INT_UNKNOWN),
                new Enum("InvoiceInvoice", INT_INVOICE_INVOICE),
                new Enum("InvoiceOperatorConfirmationToVendor", INT_INVOICE_OPERATOR_CONFIRMATION_TO_VENDOR),
                new Enum("InvoiceOperatorConfirmationToCustomer", INT_INVOICE_OPERATOR_CONFIRMATION_TO_CUSTOMER),
                new Enum("InvoiceVendorRN", INT_INVOICE_VENDOR_RN),
                new Enum("InvoiceCustomerRN", INT_INVOICE_CUSTOMER_RN),
                new Enum("InvoiceCustomerInvoiceRN", INT_INVOICE_CUSTOMER_INVOICE_RN),
                new Enum("InvoiceOperatorConfirmationToCustomerInvoiceRN", INT_INVOICE_OPERATOR_CONFIRMATION_TO_CUSTOMER_INVOICE_RN),
                new Enum("InvoiceCustomerRNToOperatorConfirmationToCustomerInvoiceRN", INT_INVOICE_CUSTOMER_RN_TO_OPERATOR_CONFIRMATION_TO_CUSTOMER_INVOICE_RN),
                new Enum("InvoiceCustomerCorrection", INT_INVOICE_CUSTOMER_CORRECTION),
                new Enum("InvoiceVendorRNToCustomerCorrection", INT_INVOICE_VENDOR_RN_TO_CUSTOMER_CORRECTION),
                new Enum("NoRecipientSignatureRequest", INT_NO_RECIPIENT_SIGNATURE_REQUEST),
                new Enum("WaitingForRecipientSignature", INT_WAITING_FOR_RECIPIENT_SIGNATURE),
                new Enum("WithRecipientSignature", INT_WITH_RECIPIENT_SIGNATURE),
                new Enum("SignatureRequestRejected", INT_SIGNATURE_REQUEST_REJECTED),
                new Enum("RecipientReceiveNotification", INT_RECIPIENT_RECEIVE_NOTIFICATION),
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
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType newValue(java.lang.Object obj) {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType) type.newValue( obj ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType newInstance() {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
