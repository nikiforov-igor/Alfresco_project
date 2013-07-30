/*
 * XML Type:  EInvoiceCustomerStatus
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxy;


/**
 * An XML EInvoiceCustomerStatus(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy).
 *
 * This is an atomic type that is a restriction of org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus.
 */
public interface EInvoiceCustomerStatus extends org.apache.xmlbeans.XmlString
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(EInvoiceCustomerStatus.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("einvoicecustomerstatus1c50type");
    
    org.apache.xmlbeans.StringEnumAbstractBase enumValue();
    void set(org.apache.xmlbeans.StringEnumAbstractBase e);
    
    static final Enum UNKNOWN_0 = Enum.forString("Unknown_0");
    static final Enum INVOICE_RECEIVED_1 = Enum.forString("Invoice_Received_1");
    static final Enum OPERATOR_CONFIRMATION_TO_CUSTOMER_RECEIVED_2 = Enum.forString("OperatorConfirmationToCustomer_Received_2");
    static final Enum CUSTOMER_RN_SENT_3 = Enum.forString("CustomerRN_Sent_3");
    static final Enum CUSTOMER_INVOICE_RN_SENT_4 = Enum.forString("CustomerInvoiceRN_Sent_4");
    static final Enum OPERATOR_CONFIRMATION_TO_CUSTOMER_INVOICE_RN_RECEIVED_5 = Enum.forString("OperatorConfirmationToCustomerInvoiceRN_Received_5");
    static final Enum CUSTOMER_RN_TO_OPERATOR_CONFIRMATION_TO_CUSTOMER_INVOICE_RN_SENT_6 = Enum.forString("CustomerRNToOperatorConfirmationToCustomerInvoiceRN_Sent_6");
    static final Enum CUSTOMER_CORRECTION_SENT_7 = Enum.forString("CustomerCorrection_Sent_7");
    static final Enum VENDOR_RN_TO_CUSTOMER_CORRECTION_RECEIVED_8 = Enum.forString("VendorRNToCustomerCorrection_Received_8");
    static final Enum FINISHED = Enum.forString("Finished");
    
    static final int INT_UNKNOWN_0 = Enum.INT_UNKNOWN_0;
    static final int INT_INVOICE_RECEIVED_1 = Enum.INT_INVOICE_RECEIVED_1;
    static final int INT_OPERATOR_CONFIRMATION_TO_CUSTOMER_RECEIVED_2 = Enum.INT_OPERATOR_CONFIRMATION_TO_CUSTOMER_RECEIVED_2;
    static final int INT_CUSTOMER_RN_SENT_3 = Enum.INT_CUSTOMER_RN_SENT_3;
    static final int INT_CUSTOMER_INVOICE_RN_SENT_4 = Enum.INT_CUSTOMER_INVOICE_RN_SENT_4;
    static final int INT_OPERATOR_CONFIRMATION_TO_CUSTOMER_INVOICE_RN_RECEIVED_5 = Enum.INT_OPERATOR_CONFIRMATION_TO_CUSTOMER_INVOICE_RN_RECEIVED_5;
    static final int INT_CUSTOMER_RN_TO_OPERATOR_CONFIRMATION_TO_CUSTOMER_INVOICE_RN_SENT_6 = Enum.INT_CUSTOMER_RN_TO_OPERATOR_CONFIRMATION_TO_CUSTOMER_INVOICE_RN_SENT_6;
    static final int INT_CUSTOMER_CORRECTION_SENT_7 = Enum.INT_CUSTOMER_CORRECTION_SENT_7;
    static final int INT_VENDOR_RN_TO_CUSTOMER_CORRECTION_RECEIVED_8 = Enum.INT_VENDOR_RN_TO_CUSTOMER_CORRECTION_RECEIVED_8;
    static final int INT_FINISHED = Enum.INT_FINISHED;
    
    /**
     * Enumeration value class for org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus.
     * These enum values can be used as follows:
     * <pre>
     * enum.toString(); // returns the string value of the enum
     * enum.intValue(); // returns an int value, useful for switches
     * // e.g., case Enum.INT_UNKNOWN_0
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
        
        static final int INT_UNKNOWN_0 = 1;
        static final int INT_INVOICE_RECEIVED_1 = 2;
        static final int INT_OPERATOR_CONFIRMATION_TO_CUSTOMER_RECEIVED_2 = 3;
        static final int INT_CUSTOMER_RN_SENT_3 = 4;
        static final int INT_CUSTOMER_INVOICE_RN_SENT_4 = 5;
        static final int INT_OPERATOR_CONFIRMATION_TO_CUSTOMER_INVOICE_RN_RECEIVED_5 = 6;
        static final int INT_CUSTOMER_RN_TO_OPERATOR_CONFIRMATION_TO_CUSTOMER_INVOICE_RN_SENT_6 = 7;
        static final int INT_CUSTOMER_CORRECTION_SENT_7 = 8;
        static final int INT_VENDOR_RN_TO_CUSTOMER_CORRECTION_RECEIVED_8 = 9;
        static final int INT_FINISHED = 10;
        
        public static final org.apache.xmlbeans.StringEnumAbstractBase.Table table =
            new org.apache.xmlbeans.StringEnumAbstractBase.Table
        (
            new Enum[]
            {
                new Enum("Unknown_0", INT_UNKNOWN_0),
                new Enum("Invoice_Received_1", INT_INVOICE_RECEIVED_1),
                new Enum("OperatorConfirmationToCustomer_Received_2", INT_OPERATOR_CONFIRMATION_TO_CUSTOMER_RECEIVED_2),
                new Enum("CustomerRN_Sent_3", INT_CUSTOMER_RN_SENT_3),
                new Enum("CustomerInvoiceRN_Sent_4", INT_CUSTOMER_INVOICE_RN_SENT_4),
                new Enum("OperatorConfirmationToCustomerInvoiceRN_Received_5", INT_OPERATOR_CONFIRMATION_TO_CUSTOMER_INVOICE_RN_RECEIVED_5),
                new Enum("CustomerRNToOperatorConfirmationToCustomerInvoiceRN_Sent_6", INT_CUSTOMER_RN_TO_OPERATOR_CONFIRMATION_TO_CUSTOMER_INVOICE_RN_SENT_6),
                new Enum("CustomerCorrection_Sent_7", INT_CUSTOMER_CORRECTION_SENT_7),
                new Enum("VendorRNToCustomerCorrection_Received_8", INT_VENDOR_RN_TO_CUSTOMER_CORRECTION_RECEIVED_8),
                new Enum("Finished", INT_FINISHED),
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
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus newValue(java.lang.Object obj) {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus) type.newValue( obj ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus newInstance() {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxy.EInvoiceCustomerStatus) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
