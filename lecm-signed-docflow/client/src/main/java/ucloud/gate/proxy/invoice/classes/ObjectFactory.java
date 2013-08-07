
package ucloud.gate.proxy.invoice.classes;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ucloud.gate.proxy.invoice.classes package. 
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

    private final static QName _Torg12BuyerTitleInfo_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "Torg12BuyerTitleInfo");
    private final static QName _Torg12SellerTitleInfo_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "Torg12SellerTitleInfo");
    private final static QName _Grounds_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "Grounds");
    private final static QName _Torg12Item_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "Torg12Item");
    private final static QName _SignerDetails_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "SignerDetails");
    private final static QName _ArrayOfTorg12Item_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "ArrayOfTorg12Item");
    private final static QName _Official_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "Official");
    private final static QName _Signer_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", "Signer");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ucloud.gate.proxy.invoice.classes
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Grounds }
     * 
     */
    public Grounds createGrounds() {
        return new Grounds();
    }

    /**
     * Create an instance of {@link SignerDetails }
     * 
     */
    public SignerDetails createSignerDetails() {
        return new SignerDetails();
    }

    /**
     * Create an instance of {@link Torg12SellerTitleInfo }
     * 
     */
    public Torg12SellerTitleInfo createTorg12SellerTitleInfo() {
        return new Torg12SellerTitleInfo();
    }

    /**
     * Create an instance of {@link Signer }
     * 
     */
    public Signer createSigner() {
        return new Signer();
    }

    /**
     * Create an instance of {@link Torg12Item }
     * 
     */
    public Torg12Item createTorg12Item() {
        return new Torg12Item();
    }

    /**
     * Create an instance of {@link ArrayOfTorg12Item }
     * 
     */
    public ArrayOfTorg12Item createArrayOfTorg12Item() {
        return new ArrayOfTorg12Item();
    }

    /**
     * Create an instance of {@link Torg12BuyerTitleInfo }
     * 
     */
    public Torg12BuyerTitleInfo createTorg12BuyerTitleInfo() {
        return new Torg12BuyerTitleInfo();
    }

    /**
     * Create an instance of {@link Official }
     * 
     */
    public Official createOfficial() {
        return new Official();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Torg12BuyerTitleInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", name = "Torg12BuyerTitleInfo")
    public JAXBElement<Torg12BuyerTitleInfo> createTorg12BuyerTitleInfo(Torg12BuyerTitleInfo value) {
        return new JAXBElement<Torg12BuyerTitleInfo>(_Torg12BuyerTitleInfo_QNAME, Torg12BuyerTitleInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Torg12SellerTitleInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", name = "Torg12SellerTitleInfo")
    public JAXBElement<Torg12SellerTitleInfo> createTorg12SellerTitleInfo(Torg12SellerTitleInfo value) {
        return new JAXBElement<Torg12SellerTitleInfo>(_Torg12SellerTitleInfo_QNAME, Torg12SellerTitleInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Grounds }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", name = "Grounds")
    public JAXBElement<Grounds> createGrounds(Grounds value) {
        return new JAXBElement<Grounds>(_Grounds_QNAME, Grounds.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Torg12Item }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", name = "Torg12Item")
    public JAXBElement<Torg12Item> createTorg12Item(Torg12Item value) {
        return new JAXBElement<Torg12Item>(_Torg12Item_QNAME, Torg12Item.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SignerDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", name = "SignerDetails")
    public JAXBElement<SignerDetails> createSignerDetails(SignerDetails value) {
        return new JAXBElement<SignerDetails>(_SignerDetails_QNAME, SignerDetails.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfTorg12Item }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", name = "ArrayOfTorg12Item")
    public JAXBElement<ArrayOfTorg12Item> createArrayOfTorg12Item(ArrayOfTorg12Item value) {
        return new JAXBElement<ArrayOfTorg12Item>(_ArrayOfTorg12Item_QNAME, ArrayOfTorg12Item.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Official }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", name = "Official")
    public JAXBElement<Official> createOfficial(Official value) {
        return new JAXBElement<Official>(_Official_QNAME, Official.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Signer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.InvoiceClasses", name = "Signer")
    public JAXBElement<Signer> createSigner(Signer value) {
        return new JAXBElement<Signer>(_Signer_QNAME, Signer.class, null, value);
    }

}
