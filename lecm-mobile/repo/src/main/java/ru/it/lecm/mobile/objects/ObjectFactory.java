
package ru.it.lecm.mobile.objects;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.admin.SysAdminParams;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.errands.ErrandsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.secretary.SecretaryService;
import ru.it.lecm.statemachine.bean.UserActionsService;
import ru.it.lecm.statemachine.StatemachineModel;
import ru.it.lecm.statemachine.bean.UserActionsService;

import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;


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
    private static final transient Logger log = LoggerFactory.getLogger(ObjectFactory.class);

    private DocumentService documentService;
    private NodeService nodeService;
    private AuthenticationService authService;
    private OrgstructureBean orgstructureService;
    private NamespaceService namespaceService;
    private ContentService contentService;
    private TransactionService transactionService;
    private UserActionsService actionsService;
    private SysAdminParams sysAdminParams;
    private DictionaryService dictionaryService;
    private SecretaryService secretaryService;

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

    public void setSysAdminParams(SysAdminParams sysAdminParams) {
        this.sysAdminParams = sysAdminParams;
    }

    public void setDictionaryService(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
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

    public WSOGROUP createWSOGROUP(String nodeRef) {
        if (NodeRef.isNodeRef(nodeRef)){
            return createWSOGROUP(new NodeRef(nodeRef));
        }
        return new WSOGROUP();
    }

    public WSOGROUP createWSOGROUP(NodeRef groupRef) {
        return createWSOGROUP(groupRef, true, true);
    }
    /**
     * Create an instance of {@link WSOGROUP }
     *
     */
    public WSOGROUP createWSOGROUP(NodeRef groupRef, boolean includeParent, boolean includeChilds) {
        WSOGROUP group = createWSOGROUP();
        group.setID(groupRef.toString());
        group.setTYPE(nodeService.getType(groupRef).toPrefixString(namespaceService).toUpperCase());
        group.setTITLE(getNotNullStringValue(nodeService.getProperty(groupRef, OrgstructureBean.PROP_ORG_ELEMENT_FULL_NAME)));

        if (includeParent) {
            NodeRef parent = orgstructureService.getParentUnit(groupRef);
            WSOCOLLECTION parentsCollection = createWSOCOLLECTION();
            if (parent != null) {
                parentsCollection.getDATA().add(createWSOGROUP(parent, false, false));
            }
            parentsCollection.setCOUNT((short) parentsCollection.getDATA().size());
            group.setPARENTS(parentsCollection);
        }
        if (includeChilds) {
            WSOCOLLECTION childsData = createWSOCOLLECTION();
            // сотрудники
            List<NodeRef> employees = orgstructureService.getUnitEmployees(groupRef);
            for (NodeRef employee : employees) {
                NodeRef primaryUnit = orgstructureService.getPrimaryOrgUnit(employee);
                if (primaryUnit != null && primaryUnit.equals(groupRef)) {
                    childsData.getDATA().add(createWSOPERSON(employee));
                }
            }
            // подразделения
            List<NodeRef> childs = orgstructureService.getSubUnits(groupRef, true, false);
            for (NodeRef child : childs) {
                childsData.getDATA().add(createWSOGROUP(child));
            }
            childsData.setCOUNT((short) childs.size());
            group.setCHILDS(childsData);
        }

        NodeRef boss = orgstructureService.getUnitBoss(groupRef);
        if (boss != null) {
            group.setLEADER(createWSOMPERSON(boss));
        }

        return group;
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
        WSOPERSON personObj = createWSOPERSON();
        personObj.setID(person);
        personObj.setTITLE(person);
        return  personObj;
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

        NodeRef photo = orgstructureService.getEmployeePhoto(personRef);
        if (photo != null ){
            WSOFILE wsoPhoto = createWSOFILE(photo);
            person.setPHOTO(wsoPhoto);
        }

        WSOCOLLECTION secretaries = new WSOCOLLECTION();
        List<NodeRef> secretatiesList = secretaryService.getSecretaries(personRef);
        for (NodeRef nodeRef : secretatiesList) {
            secretaries.getDATA().add(createWSOPERSON(nodeRef));
        }
        secretaries.setCOUNT((short) secretaries.getDATA().size());

        person.setTRUSTERS(secretaries);

        NodeRef primaryStaff = orgstructureService.getEmployeePrimaryStaff(personRef);
        if (primaryStaff != null) {
            NodeRef position = orgstructureService.getPositionByStaff(primaryStaff);
            NodeRef unit = orgstructureService.getUnitByStaff(primaryStaff);
            if (position != null && unit != null) {
                String positionName = (String) nodeService.getProperty(position, ContentModel.PROP_NAME);
                String unitName = (String) nodeService.getProperty(unit, OrgstructureBean.PROP_ORG_ELEMENT_SHORT_NAME);
                WSOCOLLECTION postCollection = new WSOCOLLECTION();

                postCollection.getDATA().add(positionName + " " + unitName);
                postCollection.setCOUNT((short) postCollection.getDATA().size());

                person.setPOST(postCollection);
            }
        }
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

    public WSOMPERSON createWSOMPERSON(NodeRef personRef) {
        Map<QName, Serializable> props = nodeService.getProperties(personRef);

        WSOMPERSON person = createWSOMPERSON();
        person.setID(personRef.toString());
        person.setTYPE(OrgstructureBean.TYPE_EMPLOYEE.toPrefixString(namespaceService).toUpperCase());
        person.setTITLE(getNotNullStringValue(props.get(OrgstructureBean.PROP_EMPLOYEE_SHORT_NAME)));
        Object active = props.get(BaseBean.IS_ACTIVE);
        person.setISACTIVE(active != null ? (Boolean) active : true);

        return person;
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

    public WSOROUTE createWSOROUTE(QName type) {
        WSOROUTE route = createWSOROUTE();
        WSODOCUMENT document = createWSODOCUMENT();
        WSODOCUMENTCOMMONPROPERTIES properties = createWSODOCUMENTCOMMONPROPERTIES();
        TypeDefinition definition = dictionaryService.getType(type);
        properties.setDOCTYPE(definition.getTitle(dictionaryService));
        document.setCOMMONPROPS(properties);
        route.setSETUPDOC(document);
        return route;
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
        WSOCOLLECTION wsocollection = new WSOCOLLECTION();
        wsocollection.setCOUNT((short) 0);
        return wsocollection;
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
    public WSOFILE createWSOFILE(final NodeRef fileRef) {
        final WSOFILE file = createWSOFILE();
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

    public WSODOCUMENT createWSODOCUMENT(NodeRef documentRef, String docType, String status) {
        WSODOCUMENT doc = createWSODOCUMENT();
        doc.setID(documentRef.toString());
        doc.setTITLE((String) nodeService.getProperty(documentRef, DocumentService.PROP_PRESENT_STRING));
        doc.setSUBJECT((String) nodeService.getProperty(documentRef, DocumentService.PROP_PRESENT_STRING));
        QName type = nodeService.getType(documentRef);
        String typePrefixString = type.toPrefixString(namespaceService);
        doc.setTYPE(typePrefixString);
        WSODOCUMENTCOMMONPROPERTIES properties = createWSODOCUMENTCOMMONPROPERTIES();
        properties.setDOCTYPE(docType);

        //Attach
        WSOCOLLECTION attachments = getAttachments(documentRef);
        properties.setATTACHMENTS(attachments);

        doc.setCOMMONPROPS(properties);

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

        doc.setSTATUSNAME(status);
        doc.setSTATUSMOBILE(status);

        //Расширение
        WSOCOLLECTION extension = createWSOCOLLECTION();

        //Действия
        final String actions = getActionExtension(documentRef);
        WSOITEM item = createExtentionItem("actions", actions);
        extension.getDATA().add(item);

        //Свойства
        Map<QName, Serializable> props = nodeService.getProperties(documentRef);
        for(Map.Entry<QName, Serializable> property : props.entrySet()) {
            if (!(property.getValue() instanceof Locale)) {
                item = createExtentionItem(property.getKey().toPrefixString(namespaceService), property.getValue());
                extension.getDATA().add(item);
            }
        }

        extension.setCOUNT((short) extension.getDATA().size());
        doc.setEXTENSION(extension);
        return doc;
    }

    private WSOITEM createExtentionItem(String id, Object value) {
        WSOITEM item = createWSOITEM();
        item.setID(id);
        WSOCOLLECTION itemValue = createWSOCOLLECTION();
        itemValue.getDATA().add(value);
        itemValue.setCOUNT((short) 1);
        item.setVALUES(itemValue);
        return item;
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

    public WSOTASK createWSOTASK(NodeRef documentRef, String docType, String status) {
        WSOTASK doc = createWSOTASK();
        doc.setID(documentRef.toString());
        doc.setTITLE((String) nodeService.getProperty(documentRef, DocumentService.PROP_PRESENT_STRING));
        doc.setSUBJECT((String) nodeService.getProperty(documentRef, DocumentService.PROP_PRESENT_STRING));
        QName type = nodeService.getType(documentRef);
        String typePrefixString = type.toPrefixString(namespaceService);
        doc.setTYPE(typePrefixString);
        WSODOCUMENTCOMMONPROPERTIES properties = createWSODOCUMENTCOMMONPROPERTIES();
        properties.setDOCTYPE(docType);
        WSOCOLLECTION attachments = getAttachments(documentRef);
        doc.setATTACHMENTS(attachments);

        doc.setSTATUSNAME(status);
        doc.setSTATUSMOBILE(status);

        //Расширение
        WSOCOLLECTION extension = createWSOCOLLECTION();

        //Действия
        final String actions = getActionExtension(documentRef);
        WSOITEM item = createExtentionItem("actions", actions);
        extension.getDATA().add(item);

        //Свойства
        Map<QName, Serializable> props = nodeService.getProperties(documentRef);

        doc.setTITLE(props.get(ErrandsService.PROP_ERRANDS_TITLE).toString());
        List<AssociationRef> assoc = nodeService.getTargetAssocs(documentRef, ErrandsService.ASSOC_ERRANDS_CONTROLLER);
        if (!assoc.isEmpty()) {
            doc.setCONTROLLER(createWSOMPERSON(assoc.get(0).getTargetRef()));
        }
        assoc = nodeService.getTargetAssocs(documentRef, ErrandsService.ASSOC_ERRANDS_EXECUTOR);
        if (!assoc.isEmpty()) {
            WSOCOLLECTION wsocollection = createWSOCOLLECTION();
            wsocollection.getDATA().add(createWSOMPERSON(assoc.get(0).getTargetRef()));
            wsocollection.setCOUNT((short)1);
            doc.setEXECUTORS(wsocollection);
        }

        /*Date executionDate = (Date) props.get(ErrandsService.PROP_ERRANDS_EXECUTION_DATE);
        Calendar cal = Calendar.getInstance();
        cal.setTime(executionDate);
        try {
            //doc.setDATEPLAN(DatatypeFactory.newInstance().newXMLGregorianCalendar((GregorianCalendar) cal));
        } catch (DatatypeConfigurationException e) {
        }
*/
        for(Map.Entry<QName, Serializable> property : props.entrySet()) {
            if (!(property.getValue() instanceof Locale)) {
                item = createExtentionItem(property.getKey().toPrefixString(namespaceService), property.getValue());
                extension.getDATA().add(item);
            }
        }

        extension.setCOUNT((short) extension.getDATA().size());
        doc.setEXTENSION(extension);
        return doc;

    }

    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public void setActionsService(UserActionsService actionsService) {
        this.actionsService = actionsService;
    }

    public void setSecretaryService(SecretaryService secretaryService) {
        this.secretaryService = secretaryService;
    }

    private WSOCOLLECTION getAttachments(NodeRef documentRef) {
        //Attach
        String context = sysAdminParams.getAlfrescoContext();
        WSOCOLLECTION attachments = createWSOCOLLECTION();
        List<AssociationRef> attachmentRefs = nodeService.getSourceAssocs(documentRef, DocumentService.ASSOC_PARENT_DOCUMENT);
        for (AssociationRef attach : attachmentRefs) {
            WSOURLFILE file = createWSOURLFILE();
            file.setID(attach.getSourceRef().toString());
            file.setNAME(nodeService.getProperty(attach.getSourceRef(), ContentModel.PROP_NAME).toString());
            WSOURL url = createWSOURL();
            url.setURL(context + "/service/api/node/content/workspace/SpacesStore/" + attach.getSourceRef().getId());
            file.setREFERENCE(url);
            attachments.getDATA().add(file);
        }

        attachments.setCOUNT((short) attachments.getDATA().size());
        return attachments;
    }

    private String getActionExtension(NodeRef nodeRef) {
        HashMap<String, Object> actions =  actionsService.getActions(nodeRef);
        String result = "";
        ArrayList<HashMap<String, Object>> list = (ArrayList<HashMap<String,Object>>) actions.get("actions");
        if (list != null) {
            for (HashMap<String, Object> action : list) {
                result += "{" + action.get("label") + "}";
            }
        }
        return result;
    }

    private String getNotNullStringValue(Object value) {
        return value != null ? (String) value : "";
    }

}
