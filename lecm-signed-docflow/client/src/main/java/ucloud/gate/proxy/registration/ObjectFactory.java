
package ucloud.gate.proxy.registration;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ucloud.gate.proxy.registration package. 
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

    private final static QName _Member_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration", "Member");
    private final static QName _AddressForRegistration_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration", "AddressForRegistration");
    private final static QName _RegisterRequestForeignCert_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration", "RegisterRequestForeignCert");
    private final static QName _ArrayOfMember_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration", "ArrayOfMember");
    private final static QName _RegisterResponse_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration", "RegisterResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ucloud.gate.proxy.registration
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AddressForRegistration }
     * 
     */
    public AddressForRegistration createAddressForRegistration() {
        return new AddressForRegistration();
    }

    /**
     * Create an instance of {@link RegisterRequestForeignCert }
     * 
     */
    public RegisterRequestForeignCert createRegisterRequestForeignCert() {
        return new RegisterRequestForeignCert();
    }

    /**
     * Create an instance of {@link ArrayOfMember }
     * 
     */
    public ArrayOfMember createArrayOfMember() {
        return new ArrayOfMember();
    }

    /**
     * Create an instance of {@link Member }
     * 
     */
    public Member createMember() {
        return new Member();
    }

    /**
     * Create an instance of {@link RegisterResponse }
     * 
     */
    public RegisterResponse createRegisterResponse() {
        return new RegisterResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Member }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration", name = "Member")
    public JAXBElement<Member> createMember(Member value) {
        return new JAXBElement<Member>(_Member_QNAME, Member.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddressForRegistration }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration", name = "AddressForRegistration")
    public JAXBElement<AddressForRegistration> createAddressForRegistration(AddressForRegistration value) {
        return new JAXBElement<AddressForRegistration>(_AddressForRegistration_QNAME, AddressForRegistration.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegisterRequestForeignCert }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration", name = "RegisterRequestForeignCert")
    public JAXBElement<RegisterRequestForeignCert> createRegisterRequestForeignCert(RegisterRequestForeignCert value) {
        return new JAXBElement<RegisterRequestForeignCert>(_RegisterRequestForeignCert_QNAME, RegisterRequestForeignCert.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfMember }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration", name = "ArrayOfMember")
    public JAXBElement<ArrayOfMember> createArrayOfMember(ArrayOfMember value) {
        return new JAXBElement<ArrayOfMember>(_ArrayOfMember_QNAME, ArrayOfMember.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegisterResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Registration", name = "RegisterResponse")
    public JAXBElement<RegisterResponse> createRegisterResponse(RegisterResponse value) {
        return new JAXBElement<RegisterResponse>(_RegisterResponse_QNAME, RegisterResponse.class, null, value);
    }

}
