/*
 * XML Type:  InvoiceGen
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice;


/**
 * An XML InvoiceGen(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice).
 *
 * This is a complex type.
 */
public interface InvoiceGen extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(InvoiceGen.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("invoicegencfd2type");
    
    /**
     * Gets the "Consignee" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant getConsignee();
    
    /**
     * Tests for nil "Consignee" element
     */
    boolean isNilConsignee();
    
    /**
     * True if has "Consignee" element
     */
    boolean isSetConsignee();
    
    /**
     * Sets the "Consignee" element
     */
    void setConsignee(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant consignee);
    
    /**
     * Appends and returns a new empty "Consignee" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant addNewConsignee();
    
    /**
     * Nils the "Consignee" element
     */
    void setNilConsignee();
    
    /**
     * Unsets the "Consignee" element
     */
    void unsetConsignee();
    
    /**
     * Gets the "CurrencyCode" element
     */
    java.lang.String getCurrencyCode();
    
    /**
     * Gets (as xml) the "CurrencyCode" element
     */
    org.apache.xmlbeans.XmlString xgetCurrencyCode();
    
    /**
     * Tests for nil "CurrencyCode" element
     */
    boolean isNilCurrencyCode();
    
    /**
     * True if has "CurrencyCode" element
     */
    boolean isSetCurrencyCode();
    
    /**
     * Sets the "CurrencyCode" element
     */
    void setCurrencyCode(java.lang.String currencyCode);
    
    /**
     * Sets (as xml) the "CurrencyCode" element
     */
    void xsetCurrencyCode(org.apache.xmlbeans.XmlString currencyCode);
    
    /**
     * Nils the "CurrencyCode" element
     */
    void setNilCurrencyCode();
    
    /**
     * Unsets the "CurrencyCode" element
     */
    void unsetCurrencyCode();
    
    /**
     * Gets the "Customer" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantWithAddress getCustomer();
    
    /**
     * Tests for nil "Customer" element
     */
    boolean isNilCustomer();
    
    /**
     * True if has "Customer" element
     */
    boolean isSetCustomer();
    
    /**
     * Sets the "Customer" element
     */
    void setCustomer(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantWithAddress customer);
    
    /**
     * Appends and returns a new empty "Customer" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantWithAddress addNewCustomer();
    
    /**
     * Nils the "Customer" element
     */
    void setNilCustomer();
    
    /**
     * Unsets the "Customer" element
     */
    void unsetCustomer();
    
    /**
     * Gets the "DocumentDate" element
     */
    java.util.Calendar getDocumentDate();
    
    /**
     * Gets (as xml) the "DocumentDate" element
     */
    org.apache.xmlbeans.XmlDateTime xgetDocumentDate();
    
    /**
     * Tests for nil "DocumentDate" element
     */
    boolean isNilDocumentDate();
    
    /**
     * True if has "DocumentDate" element
     */
    boolean isSetDocumentDate();
    
    /**
     * Sets the "DocumentDate" element
     */
    void setDocumentDate(java.util.Calendar documentDate);
    
    /**
     * Sets (as xml) the "DocumentDate" element
     */
    void xsetDocumentDate(org.apache.xmlbeans.XmlDateTime documentDate);
    
    /**
     * Nils the "DocumentDate" element
     */
    void setNilDocumentDate();
    
    /**
     * Unsets the "DocumentDate" element
     */
    void unsetDocumentDate();
    
    /**
     * Gets the "DocumentNumber" element
     */
    java.lang.String getDocumentNumber();
    
    /**
     * Gets (as xml) the "DocumentNumber" element
     */
    org.apache.xmlbeans.XmlString xgetDocumentNumber();
    
    /**
     * Tests for nil "DocumentNumber" element
     */
    boolean isNilDocumentNumber();
    
    /**
     * True if has "DocumentNumber" element
     */
    boolean isSetDocumentNumber();
    
    /**
     * Sets the "DocumentNumber" element
     */
    void setDocumentNumber(java.lang.String documentNumber);
    
    /**
     * Sets (as xml) the "DocumentNumber" element
     */
    void xsetDocumentNumber(org.apache.xmlbeans.XmlString documentNumber);
    
    /**
     * Nils the "DocumentNumber" element
     */
    void setNilDocumentNumber();
    
    /**
     * Unsets the "DocumentNumber" element
     */
    void unsetDocumentNumber();
    
    /**
     * Gets the "GenerateDateTime" element
     */
    java.util.Calendar getGenerateDateTime();
    
    /**
     * Gets (as xml) the "GenerateDateTime" element
     */
    org.apache.xmlbeans.XmlDateTime xgetGenerateDateTime();
    
    /**
     * True if has "GenerateDateTime" element
     */
    boolean isSetGenerateDateTime();
    
    /**
     * Sets the "GenerateDateTime" element
     */
    void setGenerateDateTime(java.util.Calendar generateDateTime);
    
    /**
     * Sets (as xml) the "GenerateDateTime" element
     */
    void xsetGenerateDateTime(org.apache.xmlbeans.XmlDateTime generateDateTime);
    
    /**
     * Unsets the "GenerateDateTime" element
     */
    void unsetGenerateDateTime();
    
    /**
     * Gets the "InformationFieldId" element
     */
    java.lang.String getInformationFieldId();
    
    /**
     * Gets (as xml) the "InformationFieldId" element
     */
    org.apache.xmlbeans.XmlString xgetInformationFieldId();
    
    /**
     * Tests for nil "InformationFieldId" element
     */
    boolean isNilInformationFieldId();
    
    /**
     * True if has "InformationFieldId" element
     */
    boolean isSetInformationFieldId();
    
    /**
     * Sets the "InformationFieldId" element
     */
    void setInformationFieldId(java.lang.String informationFieldId);
    
    /**
     * Sets (as xml) the "InformationFieldId" element
     */
    void xsetInformationFieldId(org.apache.xmlbeans.XmlString informationFieldId);
    
    /**
     * Nils the "InformationFieldId" element
     */
    void setNilInformationFieldId();
    
    /**
     * Unsets the "InformationFieldId" element
     */
    void unsetInformationFieldId();
    
    /**
     * Gets the "InformationText" element
     */
    java.lang.String getInformationText();
    
    /**
     * Gets (as xml) the "InformationText" element
     */
    org.apache.xmlbeans.XmlString xgetInformationText();
    
    /**
     * Tests for nil "InformationText" element
     */
    boolean isNilInformationText();
    
    /**
     * True if has "InformationText" element
     */
    boolean isSetInformationText();
    
    /**
     * Sets the "InformationText" element
     */
    void setInformationText(java.lang.String informationText);
    
    /**
     * Sets (as xml) the "InformationText" element
     */
    void xsetInformationText(org.apache.xmlbeans.XmlString informationText);
    
    /**
     * Nils the "InformationText" element
     */
    void setNilInformationText();
    
    /**
     * Unsets the "InformationText" element
     */
    void unsetInformationText();
    
    /**
     * Gets the "PaymentDocuments" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfPaymentDocumentGen getPaymentDocuments();
    
    /**
     * Tests for nil "PaymentDocuments" element
     */
    boolean isNilPaymentDocuments();
    
    /**
     * True if has "PaymentDocuments" element
     */
    boolean isSetPaymentDocuments();
    
    /**
     * Sets the "PaymentDocuments" element
     */
    void setPaymentDocuments(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfPaymentDocumentGen paymentDocuments);
    
    /**
     * Appends and returns a new empty "PaymentDocuments" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfPaymentDocumentGen addNewPaymentDocuments();
    
    /**
     * Nils the "PaymentDocuments" element
     */
    void setNilPaymentDocuments();
    
    /**
     * Unsets the "PaymentDocuments" element
     */
    void unsetPaymentDocuments();
    
    /**
     * Gets the "Products" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfInvoiceProductGen getProducts();
    
    /**
     * Tests for nil "Products" element
     */
    boolean isNilProducts();
    
    /**
     * True if has "Products" element
     */
    boolean isSetProducts();
    
    /**
     * Sets the "Products" element
     */
    void setProducts(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfInvoiceProductGen products);
    
    /**
     * Appends and returns a new empty "Products" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.ArrayOfInvoiceProductGen addNewProducts();
    
    /**
     * Nils the "Products" element
     */
    void setNilProducts();
    
    /**
     * Unsets the "Products" element
     */
    void unsetProducts();
    
    /**
     * Gets the "RevisionDate" element
     */
    java.util.Calendar getRevisionDate();
    
    /**
     * Gets (as xml) the "RevisionDate" element
     */
    org.apache.xmlbeans.XmlDateTime xgetRevisionDate();
    
    /**
     * Tests for nil "RevisionDate" element
     */
    boolean isNilRevisionDate();
    
    /**
     * True if has "RevisionDate" element
     */
    boolean isSetRevisionDate();
    
    /**
     * Sets the "RevisionDate" element
     */
    void setRevisionDate(java.util.Calendar revisionDate);
    
    /**
     * Sets (as xml) the "RevisionDate" element
     */
    void xsetRevisionDate(org.apache.xmlbeans.XmlDateTime revisionDate);
    
    /**
     * Nils the "RevisionDate" element
     */
    void setNilRevisionDate();
    
    /**
     * Unsets the "RevisionDate" element
     */
    void unsetRevisionDate();
    
    /**
     * Gets the "RevisionNumber" element
     */
    java.lang.String getRevisionNumber();
    
    /**
     * Gets (as xml) the "RevisionNumber" element
     */
    org.apache.xmlbeans.XmlString xgetRevisionNumber();
    
    /**
     * Tests for nil "RevisionNumber" element
     */
    boolean isNilRevisionNumber();
    
    /**
     * True if has "RevisionNumber" element
     */
    boolean isSetRevisionNumber();
    
    /**
     * Sets the "RevisionNumber" element
     */
    void setRevisionNumber(java.lang.String revisionNumber);
    
    /**
     * Sets (as xml) the "RevisionNumber" element
     */
    void xsetRevisionNumber(org.apache.xmlbeans.XmlString revisionNumber);
    
    /**
     * Nils the "RevisionNumber" element
     */
    void setNilRevisionNumber();
    
    /**
     * Unsets the "RevisionNumber" element
     */
    void unsetRevisionNumber();
    
    /**
     * Gets the "Shipper" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant getShipper();
    
    /**
     * Tests for nil "Shipper" element
     */
    boolean isNilShipper();
    
    /**
     * True if has "Shipper" element
     */
    boolean isSetShipper();
    
    /**
     * Sets the "Shipper" element
     */
    void setShipper(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant shipper);
    
    /**
     * Appends and returns a new empty "Shipper" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant addNewShipper();
    
    /**
     * Nils the "Shipper" element
     */
    void setNilShipper();
    
    /**
     * Unsets the "Shipper" element
     */
    void unsetShipper();
    
    /**
     * Gets the "Signer" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.Signer getSigner();
    
    /**
     * Tests for nil "Signer" element
     */
    boolean isNilSigner();
    
    /**
     * True if has "Signer" element
     */
    boolean isSetSigner();
    
    /**
     * Sets the "Signer" element
     */
    void setSigner(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.Signer signer);
    
    /**
     * Appends and returns a new empty "Signer" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocuments.Signer addNewSigner();
    
    /**
     * Nils the "Signer" element
     */
    void setNilSigner();
    
    /**
     * Unsets the "Signer" element
     */
    void unsetSigner();
    
    /**
     * Gets the "TotalSumNoVat" element
     */
    java.lang.String getTotalSumNoVat();
    
    /**
     * Gets (as xml) the "TotalSumNoVat" element
     */
    org.apache.xmlbeans.XmlString xgetTotalSumNoVat();
    
    /**
     * Tests for nil "TotalSumNoVat" element
     */
    boolean isNilTotalSumNoVat();
    
    /**
     * True if has "TotalSumNoVat" element
     */
    boolean isSetTotalSumNoVat();
    
    /**
     * Sets the "TotalSumNoVat" element
     */
    void setTotalSumNoVat(java.lang.String totalSumNoVat);
    
    /**
     * Sets (as xml) the "TotalSumNoVat" element
     */
    void xsetTotalSumNoVat(org.apache.xmlbeans.XmlString totalSumNoVat);
    
    /**
     * Nils the "TotalSumNoVat" element
     */
    void setNilTotalSumNoVat();
    
    /**
     * Unsets the "TotalSumNoVat" element
     */
    void unsetTotalSumNoVat();
    
    /**
     * Gets the "TotalSumVat" element
     */
    java.lang.String getTotalSumVat();
    
    /**
     * Gets (as xml) the "TotalSumVat" element
     */
    org.apache.xmlbeans.XmlString xgetTotalSumVat();
    
    /**
     * Tests for nil "TotalSumVat" element
     */
    boolean isNilTotalSumVat();
    
    /**
     * True if has "TotalSumVat" element
     */
    boolean isSetTotalSumVat();
    
    /**
     * Sets the "TotalSumVat" element
     */
    void setTotalSumVat(java.lang.String totalSumVat);
    
    /**
     * Sets (as xml) the "TotalSumVat" element
     */
    void xsetTotalSumVat(org.apache.xmlbeans.XmlString totalSumVat);
    
    /**
     * Nils the "TotalSumVat" element
     */
    void setNilTotalSumVat();
    
    /**
     * Unsets the "TotalSumVat" element
     */
    void unsetTotalSumVat();
    
    /**
     * Gets the "TotalSumWithVat" element
     */
    java.lang.String getTotalSumWithVat();
    
    /**
     * Gets (as xml) the "TotalSumWithVat" element
     */
    org.apache.xmlbeans.XmlString xgetTotalSumWithVat();
    
    /**
     * Tests for nil "TotalSumWithVat" element
     */
    boolean isNilTotalSumWithVat();
    
    /**
     * True if has "TotalSumWithVat" element
     */
    boolean isSetTotalSumWithVat();
    
    /**
     * Sets the "TotalSumWithVat" element
     */
    void setTotalSumWithVat(java.lang.String totalSumWithVat);
    
    /**
     * Sets (as xml) the "TotalSumWithVat" element
     */
    void xsetTotalSumWithVat(org.apache.xmlbeans.XmlString totalSumWithVat);
    
    /**
     * Nils the "TotalSumWithVat" element
     */
    void setNilTotalSumWithVat();
    
    /**
     * Unsets the "TotalSumWithVat" element
     */
    void unsetTotalSumWithVat();
    
    /**
     * Gets the "Vendor" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantWithAddress getVendor();
    
    /**
     * Tests for nil "Vendor" element
     */
    boolean isNilVendor();
    
    /**
     * True if has "Vendor" element
     */
    boolean isSetVendor();
    
    /**
     * Sets the "Vendor" element
     */
    void setVendor(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantWithAddress vendor);
    
    /**
     * Appends and returns a new empty "Vendor" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantWithAddress addNewVendor();
    
    /**
     * Nils the "Vendor" element
     */
    void setNilVendor();
    
    /**
     * Unsets the "Vendor" element
     */
    void unsetVendor();
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen newInstance() {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsInvoice.InvoiceGen) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
