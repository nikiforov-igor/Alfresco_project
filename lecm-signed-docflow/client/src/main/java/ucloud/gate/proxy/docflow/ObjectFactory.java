
package ucloud.gate.proxy.docflow;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ucloud.gate.proxy.docflow package. 
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

    private final static QName _EDocflowTransactionType_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Docflow", "EDocflowTransactionType");
    private final static QName _BilateralDocflowInfo_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Docflow", "BilateralDocflowInfo");
    private final static QName _InvoiceDocflowInfo_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Docflow", "InvoiceDocflowInfo");
    private final static QName _NonformalizedDocflowInfo_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Docflow", "NonformalizedDocflowInfo");
    private final static QName _ReceivedDocumentInfo_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Docflow", "ReceivedDocumentInfo");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ucloud.gate.proxy.docflow
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link InvoiceDocflowInfo }
     * 
     */
    public InvoiceDocflowInfo createInvoiceDocflowInfo() {
        return new InvoiceDocflowInfo();
    }

    /**
     * Create an instance of {@link ReceivedDocumentInfo }
     * 
     */
    public ReceivedDocumentInfo createReceivedDocumentInfo() {
        return new ReceivedDocumentInfo();
    }

    /**
     * Create an instance of {@link BilateralDocflowInfo }
     * 
     */
    public BilateralDocflowInfo createBilateralDocflowInfo() {
        return new BilateralDocflowInfo();
    }

    /**
     * Create an instance of {@link NonformalizedDocflowInfo }
     * 
     */
    public NonformalizedDocflowInfo createNonformalizedDocflowInfo() {
        return new NonformalizedDocflowInfo();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EDocflowTransactionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Docflow", name = "EDocflowTransactionType")
    public JAXBElement<EDocflowTransactionType> createEDocflowTransactionType(EDocflowTransactionType value) {
        return new JAXBElement<EDocflowTransactionType>(_EDocflowTransactionType_QNAME, EDocflowTransactionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BilateralDocflowInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Docflow", name = "BilateralDocflowInfo")
    public JAXBElement<BilateralDocflowInfo> createBilateralDocflowInfo(BilateralDocflowInfo value) {
        return new JAXBElement<BilateralDocflowInfo>(_BilateralDocflowInfo_QNAME, BilateralDocflowInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InvoiceDocflowInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Docflow", name = "InvoiceDocflowInfo")
    public JAXBElement<InvoiceDocflowInfo> createInvoiceDocflowInfo(InvoiceDocflowInfo value) {
        return new JAXBElement<InvoiceDocflowInfo>(_InvoiceDocflowInfo_QNAME, InvoiceDocflowInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NonformalizedDocflowInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Docflow", name = "NonformalizedDocflowInfo")
    public JAXBElement<NonformalizedDocflowInfo> createNonformalizedDocflowInfo(NonformalizedDocflowInfo value) {
        return new JAXBElement<NonformalizedDocflowInfo>(_NonformalizedDocflowInfo_QNAME, NonformalizedDocflowInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReceivedDocumentInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Docflow", name = "ReceivedDocumentInfo")
    public JAXBElement<ReceivedDocumentInfo> createReceivedDocumentInfo(ReceivedDocumentInfo value) {
        return new JAXBElement<ReceivedDocumentInfo>(_ReceivedDocumentInfo_QNAME, ReceivedDocumentInfo.class, null, value);
    }

}
