/*
 * XML Type:  ShipmentParticipant
 * Namespace: http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common
 * Java type: org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant
 *
 * Automatically generated - do not modify.
 */
package org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon;


/**
 * An XML ShipmentParticipant(@http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common).
 *
 * This is a complex type.
 */
public interface ShipmentParticipant extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(ShipmentParticipant.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s2987C19D9FE156D389E20589B2231D61").resolveHandle("shipmentparticipantdf42type");
    
    /**
     * Gets the "IsSameAsParticipant" element
     */
    boolean getIsSameAsParticipant();
    
    /**
     * Gets (as xml) the "IsSameAsParticipant" element
     */
    org.apache.xmlbeans.XmlBoolean xgetIsSameAsParticipant();
    
    /**
     * True if has "IsSameAsParticipant" element
     */
    boolean isSetIsSameAsParticipant();
    
    /**
     * Sets the "IsSameAsParticipant" element
     */
    void setIsSameAsParticipant(boolean isSameAsParticipant);
    
    /**
     * Sets (as xml) the "IsSameAsParticipant" element
     */
    void xsetIsSameAsParticipant(org.apache.xmlbeans.XmlBoolean isSameAsParticipant);
    
    /**
     * Unsets the "IsSameAsParticipant" element
     */
    void unsetIsSameAsParticipant();
    
    /**
     * Gets the "ParticipantWithAdress" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantWithAddress getParticipantWithAdress();
    
    /**
     * Tests for nil "ParticipantWithAdress" element
     */
    boolean isNilParticipantWithAdress();
    
    /**
     * True if has "ParticipantWithAdress" element
     */
    boolean isSetParticipantWithAdress();
    
    /**
     * Sets the "ParticipantWithAdress" element
     */
    void setParticipantWithAdress(org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantWithAddress participantWithAdress);
    
    /**
     * Appends and returns a new empty "ParticipantWithAdress" element
     */
    org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ParticipantWithAddress addNewParticipantWithAdress();
    
    /**
     * Nils the "ParticipantWithAdress" element
     */
    void setNilParticipantWithAdress();
    
    /**
     * Unsets the "ParticipantWithAdress" element
     */
    void unsetParticipantWithAdress();
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant newInstance() {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        @Deprecated
        public static org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.datacontract.schemas.x2004.x07.uCloudGateProxyGeneratingDocumentsCommon.ShipmentParticipant) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
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
