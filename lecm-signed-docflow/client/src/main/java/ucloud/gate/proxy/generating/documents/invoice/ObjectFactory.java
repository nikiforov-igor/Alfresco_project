
package ucloud.gate.proxy.generating.documents.invoice;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ucloud.gate.proxy.generating.documents.invoice package. 
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

    private final static QName _PaymentDocumentGen_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "PaymentDocumentGen");
    private final static QName _InvoiceProductGen_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "InvoiceProductGen");
    private final static QName _ArrayOfPaymentDocumentGen_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "ArrayOfPaymentDocumentGen");
    private final static QName _ArrayOfInvoiceProductGen_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "ArrayOfInvoiceProductGen");
    private final static QName _CorrectionRequest_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "CorrectionRequest");
    private final static QName _InvoiceGen_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", "InvoiceGen");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ucloud.gate.proxy.generating.documents.invoice
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link InvoiceProductGen }
     * 
     */
    public InvoiceProductGen createInvoiceProductGen() {
        return new InvoiceProductGen();
    }

    /**
     * Create an instance of {@link ArrayOfPaymentDocumentGen }
     * 
     */
    public ArrayOfPaymentDocumentGen createArrayOfPaymentDocumentGen() {
        return new ArrayOfPaymentDocumentGen();
    }

    /**
     * Create an instance of {@link PaymentDocumentGen }
     * 
     */
    public PaymentDocumentGen createPaymentDocumentGen() {
        return new PaymentDocumentGen();
    }

    /**
     * Create an instance of {@link InvoiceGen }
     * 
     */
    public InvoiceGen createInvoiceGen() {
        return new InvoiceGen();
    }

    /**
     * Create an instance of {@link ArrayOfInvoiceProductGen }
     * 
     */
    public ArrayOfInvoiceProductGen createArrayOfInvoiceProductGen() {
        return new ArrayOfInvoiceProductGen();
    }

    /**
     * Create an instance of {@link CorrectionRequest }
     * 
     */
    public CorrectionRequest createCorrectionRequest() {
        return new CorrectionRequest();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PaymentDocumentGen }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", name = "PaymentDocumentGen")
    public JAXBElement<PaymentDocumentGen> createPaymentDocumentGen(PaymentDocumentGen value) {
        return new JAXBElement<PaymentDocumentGen>(_PaymentDocumentGen_QNAME, PaymentDocumentGen.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InvoiceProductGen }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", name = "InvoiceProductGen")
    public JAXBElement<InvoiceProductGen> createInvoiceProductGen(InvoiceProductGen value) {
        return new JAXBElement<InvoiceProductGen>(_InvoiceProductGen_QNAME, InvoiceProductGen.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfPaymentDocumentGen }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", name = "ArrayOfPaymentDocumentGen")
    public JAXBElement<ArrayOfPaymentDocumentGen> createArrayOfPaymentDocumentGen(ArrayOfPaymentDocumentGen value) {
        return new JAXBElement<ArrayOfPaymentDocumentGen>(_ArrayOfPaymentDocumentGen_QNAME, ArrayOfPaymentDocumentGen.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfInvoiceProductGen }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", name = "ArrayOfInvoiceProductGen")
    public JAXBElement<ArrayOfInvoiceProductGen> createArrayOfInvoiceProductGen(ArrayOfInvoiceProductGen value) {
        return new JAXBElement<ArrayOfInvoiceProductGen>(_ArrayOfInvoiceProductGen_QNAME, ArrayOfInvoiceProductGen.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CorrectionRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", name = "CorrectionRequest")
    public JAXBElement<CorrectionRequest> createCorrectionRequest(CorrectionRequest value) {
        return new JAXBElement<CorrectionRequest>(_CorrectionRequest_QNAME, CorrectionRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InvoiceGen }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments.Invoice", name = "InvoiceGen")
    public JAXBElement<InvoiceGen> createInvoiceGen(InvoiceGen value) {
        return new JAXBElement<InvoiceGen>(_InvoiceGen_QNAME, InvoiceGen.class, null, value);
    }

}
