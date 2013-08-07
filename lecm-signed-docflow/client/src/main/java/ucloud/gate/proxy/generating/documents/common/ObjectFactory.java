
package ucloud.gate.proxy.generating.documents.common;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ucloud.gate.proxy.generating.documents.common package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ParticipantCorporateWithOkopf_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common", "ParticipantCorporateWithOkopf");
    private final static QName _ParticipantIndividual_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common", "ParticipantIndividual");
    private final static QName _ParticipantBase_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common", "ParticipantBase");
    private final static QName _ShipmentParticipant_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common", "ShipmentParticipant");
    private final static QName _Official_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common", "Official");
    private final static QName _Attorney_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common", "Attorney");
    private final static QName _ParticipantWithAddress_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common", "ParticipantWithAddress");
    private final static QName _FioType_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common", "FioType");
    private final static QName _ParticipantCorporate_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common", "ParticipantCorporate");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ucloud.gate.proxy.generating.documents.common
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Official }
     * 
     */
    public Official createOfficial() {
        return new Official();
    }

    /**
     * Create an instance of {@link ShipmentParticipant }
     * 
     */
    public ShipmentParticipant createShipmentParticipant() {
        return new ShipmentParticipant();
    }

    /**
     * Create an instance of {@link FioType }
     * 
     */
    public FioType createFioType() {
        return new FioType();
    }

    /**
     * Create an instance of {@link ParticipantWithAddress }
     * 
     */
    public ParticipantWithAddress createParticipantWithAddress() {
        return new ParticipantWithAddress();
    }

    /**
     * Create an instance of {@link ParticipantCorporate }
     * 
     */
    public ParticipantCorporate createParticipantCorporate() {
        return new ParticipantCorporate();
    }

    /**
     * Create an instance of {@link Attorney }
     * 
     */
    public Attorney createAttorney() {
        return new Attorney();
    }

    /**
     * Create an instance of {@link ParticipantCorporateWithOkopf }
     * 
     */
    public ParticipantCorporateWithOkopf createParticipantCorporateWithOkopf() {
        return new ParticipantCorporateWithOkopf();
    }

    /**
     * Create an instance of {@link ParticipantIndividual }
     * 
     */
    public ParticipantIndividual createParticipantIndividual() {
        return new ParticipantIndividual();
    }

    /**
     * Create an instance of {@link ParticipantBase }
     * 
     */
    public ParticipantBase createParticipantBase() {
        return new ParticipantBase();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ParticipantCorporateWithOkopf }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common", name = "ParticipantCorporateWithOkopf")
    public JAXBElement<ParticipantCorporateWithOkopf> createParticipantCorporateWithOkopf(ParticipantCorporateWithOkopf value) {
        return new JAXBElement<ParticipantCorporateWithOkopf>(_ParticipantCorporateWithOkopf_QNAME, ParticipantCorporateWithOkopf.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ParticipantIndividual }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common", name = "ParticipantIndividual")
    public JAXBElement<ParticipantIndividual> createParticipantIndividual(ParticipantIndividual value) {
        return new JAXBElement<ParticipantIndividual>(_ParticipantIndividual_QNAME, ParticipantIndividual.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ParticipantBase }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common", name = "ParticipantBase")
    public JAXBElement<ParticipantBase> createParticipantBase(ParticipantBase value) {
        return new JAXBElement<ParticipantBase>(_ParticipantBase_QNAME, ParticipantBase.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ShipmentParticipant }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common", name = "ShipmentParticipant")
    public JAXBElement<ShipmentParticipant> createShipmentParticipant(ShipmentParticipant value) {
        return new JAXBElement<ShipmentParticipant>(_ShipmentParticipant_QNAME, ShipmentParticipant.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Official }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common", name = "Official")
    public JAXBElement<Official> createOfficial(Official value) {
        return new JAXBElement<Official>(_Official_QNAME, Official.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Attorney }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common", name = "Attorney")
    public JAXBElement<Attorney> createAttorney(Attorney value) {
        return new JAXBElement<Attorney>(_Attorney_QNAME, Attorney.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ParticipantWithAddress }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common", name = "ParticipantWithAddress")
    public JAXBElement<ParticipantWithAddress> createParticipantWithAddress(ParticipantWithAddress value) {
        return new JAXBElement<ParticipantWithAddress>(_ParticipantWithAddress_QNAME, ParticipantWithAddress.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FioType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common", name = "FioType")
    public JAXBElement<FioType> createFioType(FioType value) {
        return new JAXBElement<FioType>(_FioType_QNAME, FioType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ParticipantCorporate }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Common", name = "ParticipantCorporate")
    public JAXBElement<ParticipantCorporate> createParticipantCorporate(ParticipantCorporate value) {
        return new JAXBElement<ParticipantCorporate>(_ParticipantCorporate_QNAME, ParticipantCorporate.class, null, value);
    }

}
