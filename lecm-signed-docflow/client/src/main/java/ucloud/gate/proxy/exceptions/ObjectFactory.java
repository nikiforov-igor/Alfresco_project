
package ucloud.gate.proxy.exceptions;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ucloud.gate.proxy.exceptions package. 
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

    private final static QName _GateResponse_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Exceptions", "GateResponse");
    private final static QName _EResponseType_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Exceptions", "EResponseType");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ucloud.gate.proxy.exceptions
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GateResponse }
     * 
     */
    public GateResponse createGateResponse() {
        return new GateResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GateResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Exceptions", name = "GateResponse")
    public JAXBElement<GateResponse> createGateResponse(GateResponse value) {
        return new JAXBElement<GateResponse>(_GateResponse_QNAME, GateResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Exceptions", name = "EResponseType")
    public JAXBElement<EResponseType> createEResponseType(EResponseType value) {
        return new JAXBElement<EResponseType>(_EResponseType_QNAME, EResponseType.class, null, value);
    }

}
