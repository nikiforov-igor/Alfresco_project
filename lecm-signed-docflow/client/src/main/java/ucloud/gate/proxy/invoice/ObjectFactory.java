
package ucloud.gate.proxy.invoice;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ucloud.gate.proxy.invoice package. 
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

    private final static QName _Address_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Invoice", "Address");
    private final static QName _OrganizationInfo_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Invoice", "OrganizationInfo");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ucloud.gate.proxy.invoice
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link OrganizationInfo }
     * 
     */
    public OrganizationInfo createOrganizationInfo() {
        return new OrganizationInfo();
    }

    /**
     * Create an instance of {@link Address }
     * 
     */
    public Address createAddress() {
        return new Address();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Address }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Invoice", name = "Address")
    public JAXBElement<Address> createAddress(Address value) {
        return new JAXBElement<Address>(_Address_QNAME, Address.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OrganizationInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy.Invoice", name = "OrganizationInfo")
    public JAXBElement<OrganizationInfo> createOrganizationInfo(OrganizationInfo value) {
        return new JAXBElement<OrganizationInfo>(_OrganizationInfo_QNAME, OrganizationInfo.class, null, value);
    }

}
