
package ucloud.gate.proxy.generating.documents;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ucloud.gate.proxy.generating.documents package. 
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

    private final static QName _Signer_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments", "Signer");
    private final static QName _GeneratedDocument_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments", "GeneratedDocument");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ucloud.gate.proxy.generating.documents
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GeneratedDocument }
     * 
     */
    public GeneratedDocument createGeneratedDocument() {
        return new GeneratedDocument();
    }

    /**
     * Create an instance of {@link Signer }
     * 
     */
    public Signer createSigner() {
        return new Signer();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Signer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments", name = "Signer")
    public JAXBElement<Signer> createSigner(Signer value) {
        return new JAXBElement<Signer>(_Signer_QNAME, Signer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GeneratedDocument }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.GeneratingDocuments", name = "GeneratedDocument")
    public JAXBElement<GeneratedDocument> createGeneratedDocument(GeneratedDocument value) {
        return new JAXBElement<GeneratedDocument>(_GeneratedDocument_QNAME, GeneratedDocument.class, null, value);
    }

}
