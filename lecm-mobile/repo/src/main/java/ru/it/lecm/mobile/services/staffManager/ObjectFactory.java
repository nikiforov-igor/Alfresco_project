
package ru.it.lecm.mobile.services.staffManager;

import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import javax.xml.bind.annotation.XmlRegistry;
import java.io.Serializable;
import java.util.Map;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ru.it.lecm.mobile.services.staffManager package. 
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

    private DocumentService documentService;
    private NodeService nodeService;
    private AuthenticationService authService;
    private OrgstructureBean orgstructureService;
    private NamespaceService namespaceService;
    private ContentService contentService;
    private TransactionService transactionService;

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setAuthService(AuthenticationService authService) {
        this.authService = authService;
    }

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ru.it.lecm.mobile.services.staffManager
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link WSODOCUMENTOGR }
     * 
     */
    public WSODOCUMENTOGR createWSODOCUMENTOGR() {
        return new WSODOCUMENTOGR();
    }

    /**
     * Create an instance of {@link WSOBJECT }
     * 
     */
    public WSOBJECT createWSOBJECT() {
        return new WSOBJECT();
    }

    /**
     * Create an instance of {@link WSOGROUP }
     * 
     */
    public WSOGROUP createWSOGROUP() {
        return new WSOGROUP();
    }

    /**
     * Create an instance of {@link WSOMDOCUMENTOG }
     * 
     */
    public WSOMDOCUMENTOG createWSOMDOCUMENTOG() {
        return new WSOMDOCUMENTOG();
    }

    /**
     * Create an instance of {@link WSOMAPPROVAL }
     * 
     */
    public WSOMAPPROVAL createWSOMAPPROVAL() {
        return new WSOMAPPROVAL();
    }

    /**
     * Create an instance of {@link WSOMAPPROVALREVIEW }
     * 
     */
    public WSOMAPPROVALREVIEW createWSOMAPPROVALREVIEW() {
        return new WSOMAPPROVALREVIEW();
    }

    /**
     * Create an instance of {@link WSOMTASKREPORT }
     * 
     */
    public WSOMTASKREPORT createWSOMTASKREPORT() {
        return new WSOMTASKREPORT();
    }

    /**
     * Create an instance of {@link WSOSTAFFOBJECT }
     * 
     */
    public WSOSTAFFOBJECT createWSOSTAFFOBJECT() {
        return new WSOSTAFFOBJECT();
    }

    /**
     * Create an instance of {@link WSODOCUMENTVISA }
     * 
     */
    public WSODOCUMENTVISA createWSODOCUMENTVISA() {
        return new WSODOCUMENTVISA();
    }

    /**
     * Create an instance of {@link WSOMISSIONLABEL }
     * 
     */
    public WSOMISSIONLABEL createWSOMISSIONLABEL() {
        return new WSOMISSIONLABEL();
    }

    /**
     * Create an instance of {@link WSOAPPROVAL }
     * 
     */
    public WSOAPPROVAL createWSOAPPROVAL() {
        return new WSOAPPROVAL();
    }

    /**
     * Create an instance of {@link WSODOCUMENTCOMMONPROPERTIES }
     * 
     */
    public WSODOCUMENTCOMMONPROPERTIES createWSODOCUMENTCOMMONPROPERTIES() {
        return new WSODOCUMENTCOMMONPROPERTIES();
    }

    /**
     * Create an instance of {@link WSOURLFILE }
     * 
     */
    public WSOURLFILE createWSOURLFILE() {
        return new WSOURLFILE();
    }

    /**
     * Create an instance of {@link WSOTASKREPORT }
     * 
     */
    public WSOTASKREPORT createWSOTASKREPORT() {
        return new WSOTASKREPORT();
    }

    /**
     * Create an instance of {@link WSOMGROUP }
     * 
     */
    public WSOMGROUP createWSOMGROUP() {
        return new WSOMGROUP();
    }

    /**
     * Create an instance of {@link WSOMTASK }
     * 
     */
    public WSOMTASK createWSOMTASK() {
        return new WSOMTASK();
    }

    /**
     * Create an instance of {@link WSODOCUMENTOG }
     * 
     */
    public WSODOCUMENTOG createWSODOCUMENTOG() {
        return new WSODOCUMENTOG();
    }

    /**
     * Create an instance of {@link WSOPERSON }
     * 
     */
    public WSOPERSON createWSOPERSON() {
        return new WSOPERSON();
    }

    public WSOPERSON createWSOPERSON(String person) {
        if (NodeRef.isNodeRef(person)){
            NodeRef personRef = new NodeRef(person);
            return createWSOPERSON(personRef);
        } else {
            NodeRef personRef = orgstructureService.getEmployeeByPerson(person, false);
            return createWSOPERSON(personRef);
        }
    }

    /**
     * Create an instance of {@link WSOPERSON }
     *
     */
    public WSOPERSON createWSOPERSON(NodeRef personRef) {
        Map<QName, Serializable> props = nodeService.getProperties(personRef);

        WSOPERSON person = createWSOPERSON();
        person.setID(personRef.toString());
        person.setTYPE(OrgstructureBean.TYPE_EMPLOYEE.toPrefixString(namespaceService).toUpperCase());
        person.setTITLE(getNotNullStringValue(props.get(OrgstructureBean.PROP_EMPLOYEE_SHORT_NAME)));
        Object active = props.get(BaseBean.IS_ACTIVE);
        person.setISACTIVE(active != null ? (Boolean) active : true);

        person.setEMAIL(getNotNullStringValue(props.get(OrgstructureBean.PROP_EMPLOYEE_EMAIL)));
        person.setPHONE(getNotNullStringValue(props.get(OrgstructureBean.PROP_EMPLOYEE_PHONE)));

        person.setFIRSTNAME(getNotNullStringValue(props.get(OrgstructureBean.PROP_EMPLOYEE_FIRST_NAME)));
        person.setMIDDLENAME(getNotNullStringValue(props.get(OrgstructureBean.PROP_EMPLOYEE_MIDDLE_NAME)));
        person.setLASTNAME(getNotNullStringValue(props.get(OrgstructureBean.PROP_EMPLOYEE_LAST_NAME)));

/*        NodeRef photo = orgstructureService.getEmployeePhoto(personRef);
        WSOFILE wsoPhoto = createWSOFILE(photo);

        WSOCOLLECTION.DATA data = createWSOCOLLECTIONDATA();
        data.getItem().add(wsoPhoto);
        person.setPHOTO(createWSOCOLLECTION(data));*/
        return person;
    }

    /**
     * Create an instance of {@link WSODOCUMENTORD }
     * 
     */
    public WSODOCUMENTORD createWSODOCUMENTORD() {
        return new WSODOCUMENTORD();
    }

    /**
     * Create an instance of {@link WSOMDOCUMENTOGR }
     * 
     */
    public WSOMDOCUMENTOGR createWSOMDOCUMENTOGR() {
        return new WSOMDOCUMENTOGR();
    }

    /**
     * Create an instance of {@link WSOMDOCUMENTVISA }
     * 
     */
    public WSOMDOCUMENTVISA createWSOMDOCUMENTVISA() {
        return new WSOMDOCUMENTVISA();
    }

    /**
     * Create an instance of {@link WSOURL }
     * 
     */
    public WSOURL createWSOURL() {
        return new WSOURL();
    }

    /**
     * Create an instance of {@link WSODOCUMENTIN }
     * 
     */
    public WSODOCUMENTIN createWSODOCUMENTIN() {
        return new WSODOCUMENTIN();
    }

    /**
     * Create an instance of {@link WSOMPERSON }
     * 
     */
    public WSOMPERSON createWSOMPERSON() {
        return new WSOMPERSON();
    }

    /**
     * Create an instance of {@link WSOMDOCUMENT }
     * 
     */
    public WSOMDOCUMENT createWSOMDOCUMENT() {
        return new WSOMDOCUMENT();
    }

    /**
     * Create an instance of {@link WSOMDOCUMENTOUT }
     * 
     */
    public WSOMDOCUMENTOUT createWSOMDOCUMENTOUT() {
        return new WSOMDOCUMENTOUT();
    }

    /**
     * Create an instance of {@link WSOITEM }
     * 
     */
    public WSOITEM createWSOITEM() {
        return new WSOITEM();
    }

    /**
     * Create an instance of {@link WSODOCUMENTOUT }
     * 
     */
    public WSODOCUMENTOUT createWSODOCUMENTOUT() {
        return new WSODOCUMENTOUT();
    }

    /**
     * Create an instance of {@link WSOFORMACTION }
     * 
     */
    public WSOFORMACTION createWSOFORMACTION() {
        return new WSOFORMACTION();
    }

    /**
     * Create an instance of {@link WSODOCUMENTOUTDRAFT }
     * 
     */
    public WSODOCUMENTOUTDRAFT createWSODOCUMENTOUTDRAFT() {
        return new WSODOCUMENTOUTDRAFT();
    }

    /**
     * Create an instance of {@link WSOGLOSSARY }
     * 
     */
    public WSOGLOSSARY createWSOGLOSSARY() {
        return new WSOGLOSSARY();
    }

    /**
     * Create an instance of {@link WSOCONTEXT }
     * 
     */
    public WSOCONTEXT createWSOCONTEXT() {
        return new WSOCONTEXT();
    }

    /**
     * Create an instance of {@link WSOROUTE }
     * 
     */
    public WSOROUTE createWSOROUTE() {
        return new WSOROUTE();
    }

    /**
     * Create an instance of {@link WSOGLOSSARYENTRY }
     * 
     */
    public WSOGLOSSARYENTRY createWSOGLOSSARYENTRY() {
        return new WSOGLOSSARYENTRY();
    }

    /**
     * Create an instance of {@link WSOCOLLECTION }
     * 
     */
    public WSOCOLLECTION createWSOCOLLECTION() {
        return new WSOCOLLECTION();
    }

    /**
     * Create an instance of {@link WSOFILE }
     * 
     */
    public WSOFILE createWSOFILE() {
        return new WSOFILE();
    }

    /**
     * Create an instance of {@link WSOAPPROVALREVIEW }
     * 
     */
    public WSOAPPROVALREVIEW createWSOAPPROVALREVIEW() {
        return new WSOAPPROVALREVIEW();
    }

    /**
     * Create an instance of {@link WSOMDOCUMENTOUTDRAFT }
     * 
     */
    public WSOMDOCUMENTOUTDRAFT createWSOMDOCUMENTOUTDRAFT() {
        return new WSOMDOCUMENTOUTDRAFT();
    }

    /**
     * Create an instance of {@link WSOEDS }
     * 
     */
    public WSOEDS createWSOEDS() {
        return new WSOEDS();
    }

    /**
     * Create an instance of {@link WSODOCUMENT }
     * 
     */
    public WSODOCUMENT createWSODOCUMENT() {
        return new WSODOCUMENT();
    }

    /**
     * Create an instance of {@link WSOMDOCUMENTIN }
     * 
     */
    public WSOMDOCUMENTIN createWSOMDOCUMENTIN() {
        return new WSOMDOCUMENTIN();
    }

    /**
     * Create an instance of {@link WSOLINK }
     * 
     */
    public WSOLINK createWSOLINK() {
        return new WSOLINK();
    }

    /**
     * Create an instance of {@link WSOMDISCUSSION }
     * 
     */
    public WSOMDISCUSSION createWSOMDISCUSSION() {
        return new WSOMDISCUSSION();
    }

    /**
     * Create an instance of {@link WSOMDOCUMENTORD }
     * 
     */
    public WSOMDOCUMENTORD createWSOMDOCUMENTORD() {
        return new WSOMDOCUMENTORD();
    }

    /**
     * Create an instance of {@link WSOTASK }
     * 
     */
    public WSOTASK createWSOTASK() {
        return new WSOTASK();
    }

    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    private String getNotNullStringValue(Object value) {
        return value != null ? (String) value : "";
    }
}
