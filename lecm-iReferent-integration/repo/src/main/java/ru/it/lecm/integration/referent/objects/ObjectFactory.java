
package ru.it.lecm.integration.referent.objects;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.statemachine.StatemachineModel;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.*;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the staffManager package. 
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
    private final static javax.xml.namespace.QName _WSOCOLLECTIONDATA_QNAME = new javax.xml.namespace.QName("", "DATA");
    private static final transient Logger log = LoggerFactory.getLogger(ObjectFactory.class);

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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: staffManager
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link WSOTASKREPORT }
     * 
     */
    public WSOTASKREPORT createWSOTASKREPORT() {
        return new WSOTASKREPORT();
    }

    /**
     * Create an instance of {@link WSODOCUMENTINT }
     * 
     */
    public WSODOCUMENTINT createWSODOCUMENTINT() {
        return new WSODOCUMENTINT();
    }

    /**
     * Create an instance of {@link WSOTYPERESOLUTION }
     * 
     */
    public WSOTYPERESOLUTION createWSOTYPERESOLUTION() {
        return new WSOTYPERESOLUTION();
    }

    /**
     * Create an instance of {@link WSOMPERSON }
     * 
     */
    public WSOMPERSON createWSOMPERSON() {
        return new WSOMPERSON();
    }

    /**
     * Create an instance of {@link WSOMPERSON }
     *
     */
    public WSOMPERSON createWSOMPERSON(String nodeRef) {
        if (NodeRef.isNodeRef(nodeRef)) {
            return createWSOMPERSON(new NodeRef(nodeRef));
        }
        return createWSOMPERSON();
    }

    /**
     * Create an instance of {@link WSOMPERSON }
     *
     */
    public WSOMPERSON createWSOMPERSON(NodeRef personRef) {
        WSOMPERSON person = createWSOMPERSON();
        person.setID(personRef.toString());
        person.setTYPE(OrgstructureBean.TYPE_EMPLOYEE.toPrefixString(namespaceService).toUpperCase());
        Object title = nodeService.getProperty(personRef, OrgstructureBean.PROP_EMPLOYEE_SHORT_NAME);
        person.setTITLE(getNotNullStringValue(title));
        Object active = nodeService.getProperty(personRef, BaseBean.IS_ACTIVE);
        person.setISACTIVE(active != null ? (Boolean) active : true);
        return person;
    }

    /**
     * Create an instance of {@link WSOMGROUP }
     * 
     */
    public WSOMGROUP createWSOMGROUP() {
        return new WSOMGROUP();
    }

    /**
     * Create an instance of {@link WSOMGROUP }
     *
     */
    public WSOMGROUP createWSOMGROUP(String nodeRef) {
        if (NodeRef.isNodeRef(nodeRef)){
            return createWSOMGROUP(new NodeRef(nodeRef));
        }
        return new WSOMGROUP();
    }

    /**
     * Create an instance of {@link WSOMGROUP }
     *
     */
    public WSOMGROUP createWSOMGROUP(NodeRef groupRef) {
        WSOMGROUP group = createWSOMGROUP();
        group.setID(groupRef.toString());
        group.setTYPE(nodeService.getType(groupRef).toPrefixString(namespaceService).toUpperCase());
        group.setTITLE(getNotNullStringValue(nodeService.getProperty(groupRef, OrgstructureBean.PROP_ORG_ELEMENT_FULL_NAME)));

        /*NodeRef parent = orgstructureService.getParentUnit(groupRef);
        if (parent != null) {
            WSOCOLLECTION.DATA data = createWSOCOLLECTIONDATA();
            data.getItem().add(createWSOMGROUP(parent));
            group.setPARENTS(createWSOCOLLECTION(data));
        }

        List<NodeRef> childs = orgstructureService.getSubUnits(groupRef, true, true);
        if (!childs.isEmpty()) {
            WSOCOLLECTION.DATA childData = createWSOCOLLECTIONDATA();
            for (NodeRef child : childs) {
                childData.getItem().add(createWSOMGROUP(child));
            }
            group.setCHILDS(createWSOCOLLECTION(childData));
        }*/
        return group;
    }

    /**
     * Create an instance of {@link WSODOCUMENTOG }
     * 
     */
    public WSODOCUMENTOG createWSODOCUMENTOG() {
        return new WSODOCUMENTOG();
    }

    /**
     * Create an instance of {@link WSOMFILE }
     * 
     */
    public WSOMFILE createWSOMFILE() {
        return new WSOMFILE();
    }

    /**
     * Create an instance of {@link WSOMFILE }
     *
     */
    public WSOMFILE createWSOMFILE(String nodeRef) {
        if (NodeRef.isNodeRef(nodeRef)){
            return createWSOMFILE(new NodeRef(nodeRef));
        }
        return new WSOMFILE();
    }

    /**
     * Create an instance of {@link WSOMFILE }
     *
     */
    public WSOMFILE createWSOMFILE(NodeRef fileRef) {
        WSOMFILE file = new WSOMFILE();

        file.setID(fileRef.toString());
        file.setTYPE(nodeService.getType(fileRef).toPrefixString(namespaceService).toUpperCase());
        file.setTITLE(getNotNullStringValue(nodeService.getProperty(fileRef, ContentModel.PROP_NAME)));
        file.setNAME(getNotNullStringValue(nodeService.getProperty(fileRef, ContentModel.PROP_NAME)));

        return file;
    }

    /**
     * Create an instance of {@link WSOTASK }
     * 
     */
    public WSOTASK createWSOTASK() {
        return new WSOTASK();
    }

    /**
     * Create an instance of {@link WSOAPPROVALREVIEWSOLUTION }
     * 
     */
    public WSOAPPROVALREVIEWSOLUTION createWSOAPPROVALREVIEWSOLUTION() {
        return new WSOAPPROVALREVIEWSOLUTION();
    }

    /**
     * Create an instance of {@link WSOMDOCUMENTORD }
     * 
     */
    public WSOMDOCUMENTORD createWSOMDOCUMENTORD() {
        return new WSOMDOCUMENTORD();
    }

    /**
     * Create an instance of {@link WSOCONTEXT }
     * 
     */
    public WSOCONTEXT createWSOCONTEXT() {
        return new WSOCONTEXT();
    }

    /**
     * Create an instance of {@link WSOMTASK }
     * 
     */
    public WSOMTASK createWSOMTASK() {
        return new WSOMTASK();
    }

    /**
     * Create an instance of {@link WSOMAPPROVALREVIEW }
     * 
     */
    public WSOMAPPROVALREVIEW createWSOMAPPROVALREVIEW() {
        return new WSOMAPPROVALREVIEW();
    }

    /**
     * Create an instance of {@link WSOURL }
     * 
     */
    public WSOURL createWSOURL() {
        return new WSOURL();
    }

    /**
     * Create an instance of {@link WSOCOLLECTION }
     * 
     */
    public WSOCOLLECTION createWSOCOLLECTION() {
        return new WSOCOLLECTION();
    }

    /**
     * Create an instance of {@link WSOCOLLECTION }
     *
     */
    public WSOCOLLECTION createWSOCOLLECTION(WSOCOLLECTION.DATA data) {
        WSOCOLLECTION coll = createWSOCOLLECTION();
        coll.setDATA(createWSOCOLLECTIONDATA(data));
        coll.setCOUNT(data.getItem().size());
        return coll;
    }

    /**
     * Create an instance of {@link WSOLINK }
     * 
     */
    public WSOLINK createWSOLINK() {
        return new WSOLINK();
    }

    /**
     * Create an instance of {@link WSOFILE }
     * 
     */
    public WSOFILE createWSOFILE() {
        return new WSOFILE();
    }

    /**
     * Create an instance of {@link WSOFILE }
     *
     */
    public WSOFILE createWSOFILE(String nodeRef) {
        if (NodeRef.isNodeRef(nodeRef)){
            return createWSOFILE(new NodeRef(nodeRef));
        }
        return new WSOFILE();
    }

    /**
     * Create an instance of {@link WSOFILE }
     *
     */
    public WSOFILE createWSOFILE(final NodeRef fileRef) {
        final WSOFILE file = createWSOFILE();
        file.setID(fileRef.toString());
        file.setTYPE(nodeService.getType(fileRef).toPrefixString(namespaceService).toUpperCase());
        file.setTITLE(getNotNullStringValue(nodeService.getProperty(fileRef, ContentModel.PROP_NAME)));
        file.setNAME(getNotNullStringValue(nodeService.getProperty(fileRef, ContentModel.PROP_NAME)));

        transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>() {
            @Override
            public Void execute() throws Throwable {
                ByteArrayOutputStream os = null;
                InputStream is = null;
                try {
                    ContentReader reader = contentService.getReader(fileRef, ContentModel.PROP_CONTENT);
                    is = reader.getContentInputStream();
                    os = new ByteArrayOutputStream();

                    final int BUF_SIZE = 1 << 8;
                    byte[] buffer = new byte[BUF_SIZE];
                    int bytesRead;

                    while ((bytesRead = is.read(buffer)) > -1) {
                        os.write(buffer, 0, bytesRead);
                    }

                    byte[] binaryData = os.toByteArray();
                    file.setBODY(binaryData);
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                } finally {
                    IOUtils.closeQuietly(is);
                    IOUtils.closeQuietly(os);
                }
                return null;
            }
        });

        return file;
    }

    /**
     * Create an instance of {@link WSODOCUMENTNPA }
     * 
     */
    public WSODOCUMENTNPA createWSODOCUMENTNPA() {
        return new WSODOCUMENTNPA();
    }

    /**
     * Create an instance of {@link WSOMDOCUMENTINT }
     * 
     */
    public WSOMDOCUMENTINT createWSOMDOCUMENTINT() {
        return new WSOMDOCUMENTINT();
    }

    /**
     * Create an instance of {@link WSOGROUP }
     * 
     */
    public WSOGROUP createWSOGROUP() {
        return new WSOGROUP();
    }

    /**
     * Create an instance of {@link WSOGROUP }
     *
     */
    public WSOGROUP createWSOGROUP(String nodeRef) {
        if (NodeRef.isNodeRef(nodeRef)){
            return createWSOGROUP(new NodeRef(nodeRef));
        }
        return new WSOGROUP();
    }

    /**
     * Create an instance of {@link WSOGROUP }
     *
     */
    public WSOGROUP createWSOGROUP(NodeRef groupRef) {
        WSOGROUP group = createWSOGROUP();
        group.setID(groupRef.toString());
        group.setTYPE(nodeService.getType(groupRef).toPrefixString(namespaceService).toUpperCase());
        group.setTITLE(getNotNullStringValue(nodeService.getProperty(groupRef, OrgstructureBean.PROP_ORG_ELEMENT_FULL_NAME)));

        NodeRef parent = orgstructureService.getParentUnit(groupRef);
        if (parent != null) {
            WSOCOLLECTION.DATA data = createWSOCOLLECTIONDATA();
            data.getItem().add(createWSOMGROUP(parent));
            group.setPARENTS(createWSOCOLLECTION(data));
        }

        List<NodeRef> childs = orgstructureService.getSubUnits(groupRef, true, true);
        if (!childs.isEmpty()) {
            WSOCOLLECTION.DATA data = createWSOCOLLECTIONDATA();
            for (NodeRef child : childs) {
                data.getItem().add(createWSOMGROUP(child));
            }
            group.setCHILDS(createWSOCOLLECTION(data));
        }

        NodeRef boss = orgstructureService.getUnitBoss(groupRef);
        if (boss != null) {
            group.setLEADER(createWSOPERSON(boss));
        }

        return group;
    }

    /**
     * Create an instance of {@link WSOMDOCUMENTIN }
     * 
     */
    public WSOMDOCUMENTIN createWSOMDOCUMENTIN() {
        return new WSOMDOCUMENTIN();
    }

    /**
     * Create an instance of {@link WSOITEM }
     * 
     */
    public WSOITEM createWSOITEM() {
        return new WSOITEM();
    }

    /**
     * Create an instance of {@link WSODOCUMENTCOMMONPROPERTIES }
     * 
     */
    public WSODOCUMENTCOMMONPROPERTIES createWSODOCUMENTCOMMONPROPERTIES() {
        return new WSODOCUMENTCOMMONPROPERTIES();
    }

    /**
     * Create an instance of {@link WSOSTAFFOBJECT }
     * 
     */
    public WSOSTAFFOBJECT createWSOSTAFFOBJECT() {
        return new WSOSTAFFOBJECT();
    }

    /**
     * Create an instance of {@link WSOMDOCUMENT }
     * 
     */
    public WSOMDOCUMENT createWSOMDOCUMENT() {
        return new WSOMDOCUMENT();
    }

    /**
     * Create an instance of {@link WSOMDOCUMENT }
     *
     */
    public WSOMDOCUMENT createWSOMDOCUMENT(String nodeRef) {
        if (NodeRef.isNodeRef(nodeRef)){
            return createWSOMDOCUMENT(new NodeRef(nodeRef));
        }
        return createWSOMDOCUMENT();
    }

    /**
     * Create an instance of {@link WSOMDOCUMENT }
     *
     */
    public WSOMDOCUMENT createWSOMDOCUMENT(NodeRef documentRef) {
        WSOMDOCUMENT doc = new WSOMDOCUMENT();
        doc.setID(documentRef.toString());
        doc.setTITLE((String) nodeService.getProperty(documentRef, DocumentService.PROP_PRESENT_STRING));
        doc.setSUBJECT((String) nodeService.getProperty(documentRef, DocumentService.PROP_PRESENT_STRING));
        QName type = nodeService.getType(documentRef);
        doc.setTYPE(type.toPrefixString(namespaceService).toUpperCase());

        String number = documentService.getDocumentRegNumber(documentRef);
        if (number != null) {
            doc.setREGNUM(number);
        }

        try {
            Date regDate = (Date) nodeService.getProperty(documentRef, ContentModel.PROP_CREATED);
            GregorianCalendar gc = (GregorianCalendar) GregorianCalendar.getInstance();
            gc.setTime(regDate);
            doc.setREGDATE(DatatypeFactory.newInstance().newXMLGregorianCalendar(gc));
        } catch (DatatypeConfigurationException ignored) {
        }

        doc.setSTATUSNAME((String) nodeService.getProperty(documentRef, StatemachineModel.PROP_STATUS));
        return doc;
    }

    /**
     * Create an instance of {@link WSOMAPPROVAL }
     * 
     */
    public WSOMAPPROVAL createWSOMAPPROVAL() {
        return new WSOMAPPROVAL();
    }

    /**
     * Create an instance of {@link WSOPERSON }
     * 
     */
    public WSOPERSON createWSOPERSON() {
        return new WSOPERSON();
    }

    public WSOPERSON createWSOPERSON(String nodeRef) {
        if (NodeRef.isNodeRef(nodeRef)){
            NodeRef personRef = new NodeRef(nodeRef);
            return createWSOPERSON(personRef);
        }
        return new WSOPERSON();
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

        /*NodeRef photo = orgstructureService.getEmployeePhoto(personRef);
        WSOFILE wsoPhoto = createWSOFILE(photo);

        WSOCOLLECTION.DATA data = createWSOCOLLECTIONDATA();
        data.getItem().add(wsoPhoto);
        person.setPHOTO(createWSOCOLLECTION(data));*/
        return person;
    }

    /**
     * Create an instance of {@link WSOBJECT }
     * 
     */
    public WSOBJECT createWSOBJECT() {
        return new WSOBJECT();
    }

    /**
     * Create an instance of {@link WSOBJECT }
     *
     */
    public WSOBJECT createWSOBJECT(String nodeRef) {
        if (NodeRef.isNodeRef(nodeRef)) {
            return createWSOBJECT(new NodeRef(nodeRef));
        }
        return createWSOBJECT();
    }

    /**
     * Create an instance of {@link WSOBJECT }
     *
     */
    public WSOBJECT createWSOBJECT(NodeRef nodeRef) {
        WSOBJECT obj = createWSOBJECT();
        obj.setID(nodeRef.toString());
        obj.setTYPE(nodeService.getType(nodeRef).toPrefixString(namespaceService).toUpperCase());
        obj.setTITLE(getNotNullStringValue(nodeService.getProperty(nodeRef, ContentModel.PROP_NAME)));

        return obj;
    }

    /**
     * Create an instance of {@link WSOCOLLECTION.DATA }
     * 
     */
    public WSOCOLLECTION.DATA createWSOCOLLECTIONDATA() {
        return new WSOCOLLECTION.DATA();
    }

    /**
     * Create an instance of {@link WSOAPPROVALREVIEW }
     * 
     */
    public WSOAPPROVALREVIEW createWSOAPPROVALREVIEW() {
        return new WSOAPPROVALREVIEW();
    }

    /**
     * Create an instance of {@link WSODOCUMENTORD }
     * 
     */
    public WSODOCUMENTORD createWSODOCUMENTORD() {
        return new WSODOCUMENTORD();
    }

    /**
     * Create an instance of {@link WSOMTASKREPORT }
     * 
     */
    public WSOMTASKREPORT createWSOMTASKREPORT() {
        return new WSOMTASKREPORT();
    }

    /**
     * Create an instance of {@link WSOMDOCUMENTNPA }
     * 
     */
    public WSOMDOCUMENTNPA createWSOMDOCUMENTNPA() {
        return new WSOMDOCUMENTNPA();
    }

    /**
     * Create an instance of {@link WSODOCUMENTIN }
     * 
     */
    public WSODOCUMENTIN createWSODOCUMENTIN() {
        return new WSODOCUMENTIN();
    }

    /**
     * Create an instance of {@link WSODOCUMENTIN }
     *
     */
    public WSODOCUMENTIN createWSODOCUMENTIN(String nodeRef) {
        if (NodeRef.isNodeRef(nodeRef)){
            return createWSODOCUMENTIN(new NodeRef(nodeRef));
        }
        return new WSODOCUMENTIN();
    }

    /**
     * Create an instance of {@link WSODOCUMENTIN }
     *
     */
    public WSODOCUMENTIN createWSODOCUMENTIN(NodeRef documentRef) {
        WSOMDOCUMENT mDoc = createWSOMDOCUMENT(documentRef);
        WSODOCUMENTIN doc = new WSODOCUMENTIN();
        doc.setID(mDoc.getID());
        doc.setREGNUM(mDoc.getREGNUM());
        doc.setSUBJECT(mDoc.getSUBJECT());
        doc.setSTATUSNAME(mDoc.getSTATUSNAME());
        doc.setTYPE(mDoc.getTYPE());
        doc.setREGDATE(mDoc.getREGDATE());
        doc.setTITLE(mDoc.getTITLE() != null ? mDoc.getTITLE() : "");

        return doc;
    }

    /**
     * Create an instance of {@link WSOAPPROVAL }
     * 
     */
    public WSOAPPROVAL createWSOAPPROVAL() {
        return new WSOAPPROVAL();
    }

    /**
     * Create an instance of {@link WSOMDOCUMENTOUT }
     * 
     */
    public WSOMDOCUMENTOUT createWSOMDOCUMENTOUT() {
        return new WSOMDOCUMENTOUT();
    }

    /**
     * Create an instance of {@link WSODOCUMENTOUT }
     * 
     */
    public WSODOCUMENTOUT createWSODOCUMENTOUT() {
        return new WSODOCUMENTOUT();
    }

    /**
     * Create an instance of {@link WSOURLFILE }
     * 
     */
    public WSOURLFILE createWSOURLFILE() {
        return new WSOURLFILE();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WSOCOLLECTION.DATA }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "DATA", scope = WSOCOLLECTION.class)
    public JAXBElement<WSOCOLLECTION.DATA> createWSOCOLLECTIONDATA(WSOCOLLECTION.DATA value) {
        return new JAXBElement<WSOCOLLECTION.DATA>(_WSOCOLLECTIONDATA_QNAME, WSOCOLLECTION.DATA.class, WSOCOLLECTION.class, value);
    }

    private String getNotNullStringValue(Object value) {
        return value != null ? (String) value : "";
    }

    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
}
