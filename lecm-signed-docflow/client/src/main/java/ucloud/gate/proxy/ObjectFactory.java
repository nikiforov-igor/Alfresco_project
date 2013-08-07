
package ucloud.gate.proxy;

import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ucloud.gate.proxy package. 
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

    private final static QName _EInvoiceVendorStatus_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "EInvoiceVendorStatus");
    private final static QName _EDocumentType_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "EDocumentType");
    private final static QName _ArrayOfOperatorInfo_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "ArrayOfOperatorInfo");
    private final static QName _ArrayOfDocflowInfoBase_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "ArrayOfDocflowInfoBase");
    private final static QName _EOperatorAuthenticationType_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "EOperatorAuthenticationType");
    private final static QName _ERelationFilter_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "ERelationFilter");
    private final static QName _DocumentTransportData_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "DocumentTransportData");
    private final static QName _ENonformalizedDocumentStatus_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "ENonformalizedDocumentStatus");
    private final static QName _RegistrationInfo_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "RegistrationInfo");
    private final static QName _EInvoiceCustomerStatus_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "EInvoiceCustomerStatus");
    private final static QName _WorkspaceFilter_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "WorkspaceFilter");
    private final static QName _ArrayOfAuthorizationError_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "ArrayOfAuthorizationError");
    private final static QName _ArrayOfDocumentInfo_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "ArrayOfDocumentInfo");
    private final static QName _DocumentToSend_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "DocumentToSend");
    private final static QName _DocflowInfoBase_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "DocflowInfoBase");
    private final static QName _ArrayOfRegistrationInfo_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "ArrayOfRegistrationInfo");
    private final static QName _EDocflowType_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "EDocflowType");
    private final static QName _OperatorInfo_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "OperatorInfo");
    private final static QName _DocumentContent_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "DocumentContent");
    private final static QName _AuthorizationError_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "AuthorizationError");
    private final static QName _DocumentInfo_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "DocumentInfo");
    private final static QName _CompanyInfo_QNAME = new QName("http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", "CompanyInfo");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ucloud.gate.proxy
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CompanyInfo }
     * 
     */
    public CompanyInfo createCompanyInfo() {
        return new CompanyInfo();
    }

    /**
     * Create an instance of {@link ArrayOfDocflowInfoBase }
     * 
     */
    public ArrayOfDocflowInfoBase createArrayOfDocflowInfoBase() {
        return new ArrayOfDocflowInfoBase();
    }

    /**
     * Create an instance of {@link ArrayOfOperatorInfo }
     * 
     */
    public ArrayOfOperatorInfo createArrayOfOperatorInfo() {
        return new ArrayOfOperatorInfo();
    }

    /**
     * Create an instance of {@link DocumentTransportData }
     * 
     */
    public DocumentTransportData createDocumentTransportData() {
        return new DocumentTransportData();
    }

    /**
     * Create an instance of {@link DocumentToSend }
     * 
     */
    public DocumentToSend createDocumentToSend() {
        return new DocumentToSend();
    }

    /**
     * Create an instance of {@link ArrayOfRegistrationInfo }
     * 
     */
    public ArrayOfRegistrationInfo createArrayOfRegistrationInfo() {
        return new ArrayOfRegistrationInfo();
    }

    /**
     * Create an instance of {@link WorkspaceFilter }
     * 
     */
    public WorkspaceFilter createWorkspaceFilter() {
        return new WorkspaceFilter();
    }

    /**
     * Create an instance of {@link AuthorizationError }
     * 
     */
    public AuthorizationError createAuthorizationError() {
        return new AuthorizationError();
    }

    /**
     * Create an instance of {@link DocflowInfoBase }
     * 
     */
    public DocflowInfoBase createDocflowInfoBase() {
        return new DocflowInfoBase();
    }

    /**
     * Create an instance of {@link OperatorInfo }
     * 
     */
    public OperatorInfo createOperatorInfo() {
        return new OperatorInfo();
    }

    /**
     * Create an instance of {@link ArrayOfDocumentInfo }
     * 
     */
    public ArrayOfDocumentInfo createArrayOfDocumentInfo() {
        return new ArrayOfDocumentInfo();
    }

    /**
     * Create an instance of {@link DocumentContent }
     * 
     */
    public DocumentContent createDocumentContent() {
        return new DocumentContent();
    }

    /**
     * Create an instance of {@link ArrayOfAuthorizationError }
     * 
     */
    public ArrayOfAuthorizationError createArrayOfAuthorizationError() {
        return new ArrayOfAuthorizationError();
    }

    /**
     * Create an instance of {@link RegistrationInfo }
     * 
     */
    public RegistrationInfo createRegistrationInfo() {
        return new RegistrationInfo();
    }

    /**
     * Create an instance of {@link DocumentInfo }
     * 
     */
    public DocumentInfo createDocumentInfo() {
        return new DocumentInfo();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EInvoiceVendorStatus }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", name = "EInvoiceVendorStatus")
    public JAXBElement<EInvoiceVendorStatus> createEInvoiceVendorStatus(EInvoiceVendorStatus value) {
        return new JAXBElement<EInvoiceVendorStatus>(_EInvoiceVendorStatus_QNAME, EInvoiceVendorStatus.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EDocumentType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", name = "EDocumentType")
    public JAXBElement<EDocumentType> createEDocumentType(EDocumentType value) {
        return new JAXBElement<EDocumentType>(_EDocumentType_QNAME, EDocumentType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfOperatorInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", name = "ArrayOfOperatorInfo")
    public JAXBElement<ArrayOfOperatorInfo> createArrayOfOperatorInfo(ArrayOfOperatorInfo value) {
        return new JAXBElement<ArrayOfOperatorInfo>(_ArrayOfOperatorInfo_QNAME, ArrayOfOperatorInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfDocflowInfoBase }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", name = "ArrayOfDocflowInfoBase")
    public JAXBElement<ArrayOfDocflowInfoBase> createArrayOfDocflowInfoBase(ArrayOfDocflowInfoBase value) {
        return new JAXBElement<ArrayOfDocflowInfoBase>(_ArrayOfDocflowInfoBase_QNAME, ArrayOfDocflowInfoBase.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link List }{@code <}{@link String }{@code >}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", name = "EOperatorAuthenticationType")
    public JAXBElement<List<String>> createEOperatorAuthenticationType(List<String> value) {
        return new JAXBElement<List<String>>(_EOperatorAuthenticationType_QNAME, ((Class) List.class), null, ((List<String> ) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ERelationFilter }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", name = "ERelationFilter")
    public JAXBElement<ERelationFilter> createERelationFilter(ERelationFilter value) {
        return new JAXBElement<ERelationFilter>(_ERelationFilter_QNAME, ERelationFilter.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DocumentTransportData }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", name = "DocumentTransportData")
    public JAXBElement<DocumentTransportData> createDocumentTransportData(DocumentTransportData value) {
        return new JAXBElement<DocumentTransportData>(_DocumentTransportData_QNAME, DocumentTransportData.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ENonformalizedDocumentStatus }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", name = "ENonformalizedDocumentStatus")
    public JAXBElement<ENonformalizedDocumentStatus> createENonformalizedDocumentStatus(ENonformalizedDocumentStatus value) {
        return new JAXBElement<ENonformalizedDocumentStatus>(_ENonformalizedDocumentStatus_QNAME, ENonformalizedDocumentStatus.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegistrationInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", name = "RegistrationInfo")
    public JAXBElement<RegistrationInfo> createRegistrationInfo(RegistrationInfo value) {
        return new JAXBElement<RegistrationInfo>(_RegistrationInfo_QNAME, RegistrationInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EInvoiceCustomerStatus }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", name = "EInvoiceCustomerStatus")
    public JAXBElement<EInvoiceCustomerStatus> createEInvoiceCustomerStatus(EInvoiceCustomerStatus value) {
        return new JAXBElement<EInvoiceCustomerStatus>(_EInvoiceCustomerStatus_QNAME, EInvoiceCustomerStatus.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WorkspaceFilter }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", name = "WorkspaceFilter")
    public JAXBElement<WorkspaceFilter> createWorkspaceFilter(WorkspaceFilter value) {
        return new JAXBElement<WorkspaceFilter>(_WorkspaceFilter_QNAME, WorkspaceFilter.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfAuthorizationError }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", name = "ArrayOfAuthorizationError")
    public JAXBElement<ArrayOfAuthorizationError> createArrayOfAuthorizationError(ArrayOfAuthorizationError value) {
        return new JAXBElement<ArrayOfAuthorizationError>(_ArrayOfAuthorizationError_QNAME, ArrayOfAuthorizationError.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfDocumentInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", name = "ArrayOfDocumentInfo")
    public JAXBElement<ArrayOfDocumentInfo> createArrayOfDocumentInfo(ArrayOfDocumentInfo value) {
        return new JAXBElement<ArrayOfDocumentInfo>(_ArrayOfDocumentInfo_QNAME, ArrayOfDocumentInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DocumentToSend }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", name = "DocumentToSend")
    public JAXBElement<DocumentToSend> createDocumentToSend(DocumentToSend value) {
        return new JAXBElement<DocumentToSend>(_DocumentToSend_QNAME, DocumentToSend.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DocflowInfoBase }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", name = "DocflowInfoBase")
    public JAXBElement<DocflowInfoBase> createDocflowInfoBase(DocflowInfoBase value) {
        return new JAXBElement<DocflowInfoBase>(_DocflowInfoBase_QNAME, DocflowInfoBase.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfRegistrationInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", name = "ArrayOfRegistrationInfo")
    public JAXBElement<ArrayOfRegistrationInfo> createArrayOfRegistrationInfo(ArrayOfRegistrationInfo value) {
        return new JAXBElement<ArrayOfRegistrationInfo>(_ArrayOfRegistrationInfo_QNAME, ArrayOfRegistrationInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EDocflowType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", name = "EDocflowType")
    public JAXBElement<EDocflowType> createEDocflowType(EDocflowType value) {
        return new JAXBElement<EDocflowType>(_EDocflowType_QNAME, EDocflowType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OperatorInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", name = "OperatorInfo")
    public JAXBElement<OperatorInfo> createOperatorInfo(OperatorInfo value) {
        return new JAXBElement<OperatorInfo>(_OperatorInfo_QNAME, OperatorInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DocumentContent }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", name = "DocumentContent")
    public JAXBElement<DocumentContent> createDocumentContent(DocumentContent value) {
        return new JAXBElement<DocumentContent>(_DocumentContent_QNAME, DocumentContent.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AuthorizationError }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", name = "AuthorizationError")
    public JAXBElement<AuthorizationError> createAuthorizationError(AuthorizationError value) {
        return new JAXBElement<AuthorizationError>(_AuthorizationError_QNAME, AuthorizationError.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DocumentInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", name = "DocumentInfo")
    public JAXBElement<DocumentInfo> createDocumentInfo(DocumentInfo value) {
        return new JAXBElement<DocumentInfo>(_DocumentInfo_QNAME, DocumentInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CompanyInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/UCloud.Gate.Proxy", name = "CompanyInfo")
    public JAXBElement<CompanyInfo> createCompanyInfo(CompanyInfo value) {
        return new JAXBElement<CompanyInfo>(_CompanyInfo_QNAME, CompanyInfo.class, null, value);
    }

}
