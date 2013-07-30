/*
 * XML Type:  Torg12SellerTitleInfo
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses;


/**
 * An XML Torg12SellerTitleInfo(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses).
 *
 * This is a complex type.
 */
public interface Torg12SellerTitleInfo extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(Torg12SellerTitleInfo.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("torg12sellertitleinfo206btype");
    
    /**
     * Gets the "AdditionalInfo" element
     */
    java.lang.String getAdditionalInfo();
    
    /**
     * Gets (as xml) the "AdditionalInfo" element
     */
    org.apache.xmlbeans.XmlString xgetAdditionalInfo();
    
    /**
     * Tests for nil "AdditionalInfo" element
     */
    boolean isNilAdditionalInfo();
    
    /**
     * True if has "AdditionalInfo" element
     */
    boolean isSetAdditionalInfo();
    
    /**
     * Sets the "AdditionalInfo" element
     */
    void setAdditionalInfo(java.lang.String additionalInfo);
    
    /**
     * Sets (as xml) the "AdditionalInfo" element
     */
    void xsetAdditionalInfo(org.apache.xmlbeans.XmlString additionalInfo);
    
    /**
     * Nils the "AdditionalInfo" element
     */
    void setNilAdditionalInfo();
    
    /**
     * Unsets the "AdditionalInfo" element
     */
    void unsetAdditionalInfo();
    
    /**
     * Gets the "AttachmentSheetsQuantity" element
     */
    java.lang.String getAttachmentSheetsQuantity();
    
    /**
     * Gets (as xml) the "AttachmentSheetsQuantity" element
     */
    org.apache.xmlbeans.XmlString xgetAttachmentSheetsQuantity();
    
    /**
     * Tests for nil "AttachmentSheetsQuantity" element
     */
    boolean isNilAttachmentSheetsQuantity();
    
    /**
     * True if has "AttachmentSheetsQuantity" element
     */
    boolean isSetAttachmentSheetsQuantity();
    
    /**
     * Sets the "AttachmentSheetsQuantity" element
     */
    void setAttachmentSheetsQuantity(java.lang.String attachmentSheetsQuantity);
    
    /**
     * Sets (as xml) the "AttachmentSheetsQuantity" element
     */
    void xsetAttachmentSheetsQuantity(org.apache.xmlbeans.XmlString attachmentSheetsQuantity);
    
    /**
     * Nils the "AttachmentSheetsQuantity" element
     */
    void setNilAttachmentSheetsQuantity();
    
    /**
     * Unsets the "AttachmentSheetsQuantity" element
     */
    void unsetAttachmentSheetsQuantity();
    
    /**
     * Gets the "BuyerDocflowParticipant" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo getBuyerDocflowParticipant();
    
    /**
     * Tests for nil "BuyerDocflowParticipant" element
     */
    boolean isNilBuyerDocflowParticipant();
    
    /**
     * True if has "BuyerDocflowParticipant" element
     */
    boolean isSetBuyerDocflowParticipant();
    
    /**
     * Sets the "BuyerDocflowParticipant" element
     */
    void setBuyerDocflowParticipant(org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo buyerDocflowParticipant);
    
    /**
     * Appends and returns a new empty "BuyerDocflowParticipant" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo addNewBuyerDocflowParticipant();
    
    /**
     * Nils the "BuyerDocflowParticipant" element
     */
    void setNilBuyerDocflowParticipant();
    
    /**
     * Unsets the "BuyerDocflowParticipant" element
     */
    void unsetBuyerDocflowParticipant();
    
    /**
     * Gets the "ChiefAccountant" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official getChiefAccountant();
    
    /**
     * Tests for nil "ChiefAccountant" element
     */
    boolean isNilChiefAccountant();
    
    /**
     * True if has "ChiefAccountant" element
     */
    boolean isSetChiefAccountant();
    
    /**
     * Sets the "ChiefAccountant" element
     */
    void setChiefAccountant(org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official chiefAccountant);
    
    /**
     * Appends and returns a new empty "ChiefAccountant" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official addNewChiefAccountant();
    
    /**
     * Nils the "ChiefAccountant" element
     */
    void setNilChiefAccountant();
    
    /**
     * Unsets the "ChiefAccountant" element
     */
    void unsetChiefAccountant();
    
    /**
     * Gets the "Consignee" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo getConsignee();
    
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
    void setConsignee(org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo consignee);
    
    /**
     * Appends and returns a new empty "Consignee" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo addNewConsignee();
    
    /**
     * Nils the "Consignee" element
     */
    void setNilConsignee();
    
    /**
     * Unsets the "Consignee" element
     */
    void unsetConsignee();
    
    /**
     * Gets the "DocumentDate" element
     */
    java.lang.String getDocumentDate();
    
    /**
     * Gets (as xml) the "DocumentDate" element
     */
    org.apache.xmlbeans.XmlString xgetDocumentDate();
    
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
    void setDocumentDate(java.lang.String documentDate);
    
    /**
     * Sets (as xml) the "DocumentDate" element
     */
    void xsetDocumentDate(org.apache.xmlbeans.XmlString documentDate);
    
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
     * Gets the "GrossQuantityTotal" element
     */
    java.lang.String getGrossQuantityTotal();
    
    /**
     * Gets (as xml) the "GrossQuantityTotal" element
     */
    org.apache.xmlbeans.XmlString xgetGrossQuantityTotal();
    
    /**
     * Tests for nil "GrossQuantityTotal" element
     */
    boolean isNilGrossQuantityTotal();
    
    /**
     * True if has "GrossQuantityTotal" element
     */
    boolean isSetGrossQuantityTotal();
    
    /**
     * Sets the "GrossQuantityTotal" element
     */
    void setGrossQuantityTotal(java.lang.String grossQuantityTotal);
    
    /**
     * Sets (as xml) the "GrossQuantityTotal" element
     */
    void xsetGrossQuantityTotal(org.apache.xmlbeans.XmlString grossQuantityTotal);
    
    /**
     * Nils the "GrossQuantityTotal" element
     */
    void setNilGrossQuantityTotal();
    
    /**
     * Unsets the "GrossQuantityTotal" element
     */
    void unsetGrossQuantityTotal();
    
    /**
     * Gets the "GrossQuantityTotalInWords" element
     */
    java.lang.String getGrossQuantityTotalInWords();
    
    /**
     * Gets (as xml) the "GrossQuantityTotalInWords" element
     */
    org.apache.xmlbeans.XmlString xgetGrossQuantityTotalInWords();
    
    /**
     * Tests for nil "GrossQuantityTotalInWords" element
     */
    boolean isNilGrossQuantityTotalInWords();
    
    /**
     * True if has "GrossQuantityTotalInWords" element
     */
    boolean isSetGrossQuantityTotalInWords();
    
    /**
     * Sets the "GrossQuantityTotalInWords" element
     */
    void setGrossQuantityTotalInWords(java.lang.String grossQuantityTotalInWords);
    
    /**
     * Sets (as xml) the "GrossQuantityTotalInWords" element
     */
    void xsetGrossQuantityTotalInWords(org.apache.xmlbeans.XmlString grossQuantityTotalInWords);
    
    /**
     * Nils the "GrossQuantityTotalInWords" element
     */
    void setNilGrossQuantityTotalInWords();
    
    /**
     * Unsets the "GrossQuantityTotalInWords" element
     */
    void unsetGrossQuantityTotalInWords();
    
    /**
     * Gets the "Grounds" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds getGrounds();
    
    /**
     * Tests for nil "Grounds" element
     */
    boolean isNilGrounds();
    
    /**
     * True if has "Grounds" element
     */
    boolean isSetGrounds();
    
    /**
     * Sets the "Grounds" element
     */
    void setGrounds(org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds grounds);
    
    /**
     * Appends and returns a new empty "Grounds" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Grounds addNewGrounds();
    
    /**
     * Nils the "Grounds" element
     */
    void setNilGrounds();
    
    /**
     * Unsets the "Grounds" element
     */
    void unsetGrounds();
    
    /**
     * Gets the "Items" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.ArrayOfTorg12Item getItems();
    
    /**
     * Tests for nil "Items" element
     */
    boolean isNilItems();
    
    /**
     * True if has "Items" element
     */
    boolean isSetItems();
    
    /**
     * Sets the "Items" element
     */
    void setItems(org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.ArrayOfTorg12Item items);
    
    /**
     * Appends and returns a new empty "Items" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.ArrayOfTorg12Item addNewItems();
    
    /**
     * Nils the "Items" element
     */
    void setNilItems();
    
    /**
     * Unsets the "Items" element
     */
    void unsetItems();
    
    /**
     * Gets the "NetQuantityTotal" element
     */
    java.lang.String getNetQuantityTotal();
    
    /**
     * Gets (as xml) the "NetQuantityTotal" element
     */
    org.apache.xmlbeans.XmlString xgetNetQuantityTotal();
    
    /**
     * Tests for nil "NetQuantityTotal" element
     */
    boolean isNilNetQuantityTotal();
    
    /**
     * True if has "NetQuantityTotal" element
     */
    boolean isSetNetQuantityTotal();
    
    /**
     * Sets the "NetQuantityTotal" element
     */
    void setNetQuantityTotal(java.lang.String netQuantityTotal);
    
    /**
     * Sets (as xml) the "NetQuantityTotal" element
     */
    void xsetNetQuantityTotal(org.apache.xmlbeans.XmlString netQuantityTotal);
    
    /**
     * Nils the "NetQuantityTotal" element
     */
    void setNilNetQuantityTotal();
    
    /**
     * Unsets the "NetQuantityTotal" element
     */
    void unsetNetQuantityTotal();
    
    /**
     * Gets the "NetQuantityTotalInWords" element
     */
    java.lang.String getNetQuantityTotalInWords();
    
    /**
     * Gets (as xml) the "NetQuantityTotalInWords" element
     */
    org.apache.xmlbeans.XmlString xgetNetQuantityTotalInWords();
    
    /**
     * Tests for nil "NetQuantityTotalInWords" element
     */
    boolean isNilNetQuantityTotalInWords();
    
    /**
     * True if has "NetQuantityTotalInWords" element
     */
    boolean isSetNetQuantityTotalInWords();
    
    /**
     * Sets the "NetQuantityTotalInWords" element
     */
    void setNetQuantityTotalInWords(java.lang.String netQuantityTotalInWords);
    
    /**
     * Sets (as xml) the "NetQuantityTotalInWords" element
     */
    void xsetNetQuantityTotalInWords(org.apache.xmlbeans.XmlString netQuantityTotalInWords);
    
    /**
     * Nils the "NetQuantityTotalInWords" element
     */
    void setNilNetQuantityTotalInWords();
    
    /**
     * Unsets the "NetQuantityTotalInWords" element
     */
    void unsetNetQuantityTotalInWords();
    
    /**
     * Gets the "OperationCode" element
     */
    java.lang.String getOperationCode();
    
    /**
     * Gets (as xml) the "OperationCode" element
     */
    org.apache.xmlbeans.XmlString xgetOperationCode();
    
    /**
     * Tests for nil "OperationCode" element
     */
    boolean isNilOperationCode();
    
    /**
     * True if has "OperationCode" element
     */
    boolean isSetOperationCode();
    
    /**
     * Sets the "OperationCode" element
     */
    void setOperationCode(java.lang.String operationCode);
    
    /**
     * Sets (as xml) the "OperationCode" element
     */
    void xsetOperationCode(org.apache.xmlbeans.XmlString operationCode);
    
    /**
     * Nils the "OperationCode" element
     */
    void setNilOperationCode();
    
    /**
     * Unsets the "OperationCode" element
     */
    void unsetOperationCode();
    
    /**
     * Gets the "ParcelsQuantityTotal" element
     */
    java.lang.String getParcelsQuantityTotal();
    
    /**
     * Gets (as xml) the "ParcelsQuantityTotal" element
     */
    org.apache.xmlbeans.XmlString xgetParcelsQuantityTotal();
    
    /**
     * Tests for nil "ParcelsQuantityTotal" element
     */
    boolean isNilParcelsQuantityTotal();
    
    /**
     * True if has "ParcelsQuantityTotal" element
     */
    boolean isSetParcelsQuantityTotal();
    
    /**
     * Sets the "ParcelsQuantityTotal" element
     */
    void setParcelsQuantityTotal(java.lang.String parcelsQuantityTotal);
    
    /**
     * Sets (as xml) the "ParcelsQuantityTotal" element
     */
    void xsetParcelsQuantityTotal(org.apache.xmlbeans.XmlString parcelsQuantityTotal);
    
    /**
     * Nils the "ParcelsQuantityTotal" element
     */
    void setNilParcelsQuantityTotal();
    
    /**
     * Unsets the "ParcelsQuantityTotal" element
     */
    void unsetParcelsQuantityTotal();
    
    /**
     * Gets the "ParcelsQuantityTotalInWords" element
     */
    java.lang.String getParcelsQuantityTotalInWords();
    
    /**
     * Gets (as xml) the "ParcelsQuantityTotalInWords" element
     */
    org.apache.xmlbeans.XmlString xgetParcelsQuantityTotalInWords();
    
    /**
     * Tests for nil "ParcelsQuantityTotalInWords" element
     */
    boolean isNilParcelsQuantityTotalInWords();
    
    /**
     * True if has "ParcelsQuantityTotalInWords" element
     */
    boolean isSetParcelsQuantityTotalInWords();
    
    /**
     * Sets the "ParcelsQuantityTotalInWords" element
     */
    void setParcelsQuantityTotalInWords(java.lang.String parcelsQuantityTotalInWords);
    
    /**
     * Sets (as xml) the "ParcelsQuantityTotalInWords" element
     */
    void xsetParcelsQuantityTotalInWords(org.apache.xmlbeans.XmlString parcelsQuantityTotalInWords);
    
    /**
     * Nils the "ParcelsQuantityTotalInWords" element
     */
    void setNilParcelsQuantityTotalInWords();
    
    /**
     * Unsets the "ParcelsQuantityTotalInWords" element
     */
    void unsetParcelsQuantityTotalInWords();
    
    /**
     * Gets the "Payer" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo getPayer();
    
    /**
     * Tests for nil "Payer" element
     */
    boolean isNilPayer();
    
    /**
     * True if has "Payer" element
     */
    boolean isSetPayer();
    
    /**
     * Sets the "Payer" element
     */
    void setPayer(org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo payer);
    
    /**
     * Appends and returns a new empty "Payer" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo addNewPayer();
    
    /**
     * Nils the "Payer" element
     */
    void setNilPayer();
    
    /**
     * Unsets the "Payer" element
     */
    void unsetPayer();
    
    /**
     * Gets the "QuantityTotal" element
     */
    java.lang.String getQuantityTotal();
    
    /**
     * Gets (as xml) the "QuantityTotal" element
     */
    org.apache.xmlbeans.XmlString xgetQuantityTotal();
    
    /**
     * Tests for nil "QuantityTotal" element
     */
    boolean isNilQuantityTotal();
    
    /**
     * True if has "QuantityTotal" element
     */
    boolean isSetQuantityTotal();
    
    /**
     * Sets the "QuantityTotal" element
     */
    void setQuantityTotal(java.lang.String quantityTotal);
    
    /**
     * Sets (as xml) the "QuantityTotal" element
     */
    void xsetQuantityTotal(org.apache.xmlbeans.XmlString quantityTotal);
    
    /**
     * Nils the "QuantityTotal" element
     */
    void setNilQuantityTotal();
    
    /**
     * Unsets the "QuantityTotal" element
     */
    void unsetQuantityTotal();
    
    /**
     * Gets the "SellerDocflowParticipant" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo getSellerDocflowParticipant();
    
    /**
     * Tests for nil "SellerDocflowParticipant" element
     */
    boolean isNilSellerDocflowParticipant();
    
    /**
     * True if has "SellerDocflowParticipant" element
     */
    boolean isSetSellerDocflowParticipant();
    
    /**
     * Sets the "SellerDocflowParticipant" element
     */
    void setSellerDocflowParticipant(org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo sellerDocflowParticipant);
    
    /**
     * Appends and returns a new empty "SellerDocflowParticipant" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo addNewSellerDocflowParticipant();
    
    /**
     * Nils the "SellerDocflowParticipant" element
     */
    void setNilSellerDocflowParticipant();
    
    /**
     * Unsets the "SellerDocflowParticipant" element
     */
    void unsetSellerDocflowParticipant();
    
    /**
     * Gets the "Shipper" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo getShipper();
    
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
    void setShipper(org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo shipper);
    
    /**
     * Appends and returns a new empty "Shipper" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo addNewShipper();
    
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
    org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Signer getSigner();
    
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
    void setSigner(org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Signer signer);
    
    /**
     * Appends and returns a new empty "Signer" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Signer addNewSigner();
    
    /**
     * Nils the "Signer" element
     */
    void setNilSigner();
    
    /**
     * Unsets the "Signer" element
     */
    void unsetSigner();
    
    /**
     * Gets the "Supplier" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo getSupplier();
    
    /**
     * Tests for nil "Supplier" element
     */
    boolean isNilSupplier();
    
    /**
     * True if has "Supplier" element
     */
    boolean isSetSupplier();
    
    /**
     * Sets the "Supplier" element
     */
    void setSupplier(org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo supplier);
    
    /**
     * Appends and returns a new empty "Supplier" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoice.OrganizationInfo addNewSupplier();
    
    /**
     * Nils the "Supplier" element
     */
    void setNilSupplier();
    
    /**
     * Unsets the "Supplier" element
     */
    void unsetSupplier();
    
    /**
     * Gets the "SupplyAllowedBy" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official getSupplyAllowedBy();
    
    /**
     * Tests for nil "SupplyAllowedBy" element
     */
    boolean isNilSupplyAllowedBy();
    
    /**
     * True if has "SupplyAllowedBy" element
     */
    boolean isSetSupplyAllowedBy();
    
    /**
     * Sets the "SupplyAllowedBy" element
     */
    void setSupplyAllowedBy(org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official supplyAllowedBy);
    
    /**
     * Appends and returns a new empty "SupplyAllowedBy" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official addNewSupplyAllowedBy();
    
    /**
     * Nils the "SupplyAllowedBy" element
     */
    void setNilSupplyAllowedBy();
    
    /**
     * Unsets the "SupplyAllowedBy" element
     */
    void unsetSupplyAllowedBy();
    
    /**
     * Gets the "SupplyDate" element
     */
    java.lang.String getSupplyDate();
    
    /**
     * Gets (as xml) the "SupplyDate" element
     */
    org.apache.xmlbeans.XmlString xgetSupplyDate();
    
    /**
     * Tests for nil "SupplyDate" element
     */
    boolean isNilSupplyDate();
    
    /**
     * True if has "SupplyDate" element
     */
    boolean isSetSupplyDate();
    
    /**
     * Sets the "SupplyDate" element
     */
    void setSupplyDate(java.lang.String supplyDate);
    
    /**
     * Sets (as xml) the "SupplyDate" element
     */
    void xsetSupplyDate(org.apache.xmlbeans.XmlString supplyDate);
    
    /**
     * Nils the "SupplyDate" element
     */
    void setNilSupplyDate();
    
    /**
     * Unsets the "SupplyDate" element
     */
    void unsetSupplyDate();
    
    /**
     * Gets the "SupplyPerformedBy" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official getSupplyPerformedBy();
    
    /**
     * Tests for nil "SupplyPerformedBy" element
     */
    boolean isNilSupplyPerformedBy();
    
    /**
     * True if has "SupplyPerformedBy" element
     */
    boolean isSetSupplyPerformedBy();
    
    /**
     * Sets the "SupplyPerformedBy" element
     */
    void setSupplyPerformedBy(org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official supplyPerformedBy);
    
    /**
     * Appends and returns a new empty "SupplyPerformedBy" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Official addNewSupplyPerformedBy();
    
    /**
     * Nils the "SupplyPerformedBy" element
     */
    void setNilSupplyPerformedBy();
    
    /**
     * Unsets the "SupplyPerformedBy" element
     */
    void unsetSupplyPerformedBy();
    
    /**
     * Gets the "Total" element
     */
    java.lang.String getTotal();
    
    /**
     * Gets (as xml) the "Total" element
     */
    org.apache.xmlbeans.XmlString xgetTotal();
    
    /**
     * Tests for nil "Total" element
     */
    boolean isNilTotal();
    
    /**
     * True if has "Total" element
     */
    boolean isSetTotal();
    
    /**
     * Sets the "Total" element
     */
    void setTotal(java.lang.String total);
    
    /**
     * Sets (as xml) the "Total" element
     */
    void xsetTotal(org.apache.xmlbeans.XmlString total);
    
    /**
     * Nils the "Total" element
     */
    void setNilTotal();
    
    /**
     * Unsets the "Total" element
     */
    void unsetTotal();
    
    /**
     * Gets the "TotalInWords" element
     */
    java.lang.String getTotalInWords();
    
    /**
     * Gets (as xml) the "TotalInWords" element
     */
    org.apache.xmlbeans.XmlString xgetTotalInWords();
    
    /**
     * Tests for nil "TotalInWords" element
     */
    boolean isNilTotalInWords();
    
    /**
     * True if has "TotalInWords" element
     */
    boolean isSetTotalInWords();
    
    /**
     * Sets the "TotalInWords" element
     */
    void setTotalInWords(java.lang.String totalInWords);
    
    /**
     * Sets (as xml) the "TotalInWords" element
     */
    void xsetTotalInWords(org.apache.xmlbeans.XmlString totalInWords);
    
    /**
     * Nils the "TotalInWords" element
     */
    void setNilTotalInWords();
    
    /**
     * Unsets the "TotalInWords" element
     */
    void unsetTotalInWords();
    
    /**
     * Gets the "TotalWithVatExcluded" element
     */
    java.lang.String getTotalWithVatExcluded();
    
    /**
     * Gets (as xml) the "TotalWithVatExcluded" element
     */
    org.apache.xmlbeans.XmlString xgetTotalWithVatExcluded();
    
    /**
     * Tests for nil "TotalWithVatExcluded" element
     */
    boolean isNilTotalWithVatExcluded();
    
    /**
     * True if has "TotalWithVatExcluded" element
     */
    boolean isSetTotalWithVatExcluded();
    
    /**
     * Sets the "TotalWithVatExcluded" element
     */
    void setTotalWithVatExcluded(java.lang.String totalWithVatExcluded);
    
    /**
     * Sets (as xml) the "TotalWithVatExcluded" element
     */
    void xsetTotalWithVatExcluded(org.apache.xmlbeans.XmlString totalWithVatExcluded);
    
    /**
     * Nils the "TotalWithVatExcluded" element
     */
    void setNilTotalWithVatExcluded();
    
    /**
     * Unsets the "TotalWithVatExcluded" element
     */
    void unsetTotalWithVatExcluded();
    
    /**
     * Gets the "Vat" element
     */
    java.lang.String getVat();
    
    /**
     * Gets (as xml) the "Vat" element
     */
    org.apache.xmlbeans.XmlString xgetVat();
    
    /**
     * Tests for nil "Vat" element
     */
    boolean isNilVat();
    
    /**
     * True if has "Vat" element
     */
    boolean isSetVat();
    
    /**
     * Sets the "Vat" element
     */
    void setVat(java.lang.String vat);
    
    /**
     * Sets (as xml) the "Vat" element
     */
    void xsetVat(org.apache.xmlbeans.XmlString vat);
    
    /**
     * Nils the "Vat" element
     */
    void setNilVat();
    
    /**
     * Unsets the "Vat" element
     */
    void unsetVat();
    
    /**
     * Gets the "WaybillDate" element
     */
    java.lang.String getWaybillDate();
    
    /**
     * Gets (as xml) the "WaybillDate" element
     */
    org.apache.xmlbeans.XmlString xgetWaybillDate();
    
    /**
     * Tests for nil "WaybillDate" element
     */
    boolean isNilWaybillDate();
    
    /**
     * True if has "WaybillDate" element
     */
    boolean isSetWaybillDate();
    
    /**
     * Sets the "WaybillDate" element
     */
    void setWaybillDate(java.lang.String waybillDate);
    
    /**
     * Sets (as xml) the "WaybillDate" element
     */
    void xsetWaybillDate(org.apache.xmlbeans.XmlString waybillDate);
    
    /**
     * Nils the "WaybillDate" element
     */
    void setNilWaybillDate();
    
    /**
     * Unsets the "WaybillDate" element
     */
    void unsetWaybillDate();
    
    /**
     * Gets the "WaybillNumber" element
     */
    java.lang.String getWaybillNumber();
    
    /**
     * Gets (as xml) the "WaybillNumber" element
     */
    org.apache.xmlbeans.XmlString xgetWaybillNumber();
    
    /**
     * Tests for nil "WaybillNumber" element
     */
    boolean isNilWaybillNumber();
    
    /**
     * True if has "WaybillNumber" element
     */
    boolean isSetWaybillNumber();
    
    /**
     * Sets the "WaybillNumber" element
     */
    void setWaybillNumber(java.lang.String waybillNumber);
    
    /**
     * Sets (as xml) the "WaybillNumber" element
     */
    void xsetWaybillNumber(org.apache.xmlbeans.XmlString waybillNumber);
    
    /**
     * Nils the "WaybillNumber" element
     */
    void setNilWaybillNumber();
    
    /**
     * Unsets the "WaybillNumber" element
     */
    void unsetWaybillNumber();
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo newInstance() {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyInvoiceClasses.Torg12SellerTitleInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
