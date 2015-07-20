package ru.it.lecm.mobile.services.taskManager;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.GUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.RepositoryStructureHelper;
import ru.it.lecm.documents.beans.DocumentAttachmentsService;
import ru.it.lecm.errands.ErrandsService;
import ru.it.lecm.mobile.objects.*;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.io.InputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by pmelnikov on 15.07.2015.
 */
public class WSTaskManagerPort implements WSTaskManager {

    final static protected Logger logger = LoggerFactory.getLogger(WSTaskManagerPort.class);

    private ObjectFactory objectFactory;
    private TransactionService transactionService;
    private RepositoryStructureHelper repositoryStructureHelper;
    private NodeService nodeService;
    private OrgstructureBean orgstructureService;
    private ThreadPoolExecutor threadPoolExecutor;
    private DocumentAttachmentsService documentAttachmentsService;
    private ContentService contentService;

    public void setRepositoryStructureHelper(RepositoryStructureHelper repositoryStructureHelper) {
        this.repositoryStructureHelper = repositoryStructureHelper;
    }

    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    public void setThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
    }

    @Override
    public WSOEDS getfakesign() {
        return null;
    }

    @Override
    public WSOTASK gettask(String idtask, boolean includeattachments, WSOCONTEXT context) {
        return null;
    }

    @Override
    public WSOCOLLECTION gettasksbydoc(String docid, boolean ismobject, WSOCONTEXT context) {
        return null;
    }

    @Override
    public WSOTASK getstructure(String roottaskid, BigInteger childslevel, boolean ismobject, boolean includeattachments, WSOCONTEXT context) {
        return null;
    }

    @Override
    public String createresolution(String docid, WSOTASK newtask, String parenttaskid, WSOCONTEXT context) {
        return null;
    }

    @Override
    public WSOMTASK updateresolution(String missionlabelid, WSOTASK newtask, WSOCONTEXT context) {
        return null;
    }

    @Override
    public WSOMTASK deleteresolution(String missionlabelid, WSOCONTEXT context) {
        return null;
    }

    @Override
    public WSOCOLLECTION getmissionlabels(String docid, WSOCONTEXT context) {
        return null;
    }

    @Override
    public WSOCOLLECTION getresolutions(String docid, boolean ismobject, boolean includeattachments, WSOCONTEXT context) {
        return objectFactory.createWSOCOLLECTION();
    }

    @Override
    public WSOCOLLECTION gettasks(boolean ismobject, WSOCONTEXT context) {
        return null;
    }

    @Override
    public WSOCOLLECTION getrestemplates(String doctypename, WSOCONTEXT context) {
        return objectFactory.createWSOCOLLECTION();
    }

    @Override
    public WSOCOLLECTION gettaskstemplates(WSOCONTEXT context) {
        return null;
    }

    @Override
    public WSOMTASK settask(WSOTASK inTASK, WSOCONTEXT context) {
        return createErrand(inTASK, context.getUSERID());
    }

    @Override
    public WSOCOLLECTION getreports(String taskid, boolean ismobject, boolean includeattachments, WSOCONTEXT context) {
        return null;
    }

    @Override
    public WSOMTASKREPORT setreport(WSOTASKREPORT report, WSOCONTEXT context) {
        return null;
    }

    @Override
    public WSOITEM getitem() {
        return null;
    }

    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    private WSOTASK createErrand(final WSOTASK inTASK, final String user) {
        try {
            AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<WSOTASK>() {
                @Override
                public WSOTASK doWork() throws Exception {
                    return transactionService.getRetryingTransactionHelper().doInTransaction(
                            new RetryingTransactionHelper.RetryingTransactionCallback<WSOTASK>() {
                                @Override
                                public WSOTASK execute() throws Throwable {
                                    Map<QName, Serializable> props = new HashMap<>();

                                    Object value = inTASK.getSUBJECT();
                                    if (value != null) {
                                        props.put(ErrandsService.PROP_ERRANDS_CONTENT, (Serializable) value);
                                        props.put(ErrandsService.PROP_ERRANDS_TITLE, (Serializable) value);
                                    }

                                    value = inTASK.getCOMMENTS();
                                    if (value != null) {
                                        props.put(ErrandsService.PROP_ERRANDS_TITLE, (Serializable) value);
                                    }

                                    if (inTASK.getDATEPLAN() != null) {
                                        props.put(ErrandsService.PROP_ERRANDS_LIMITATION_DATE, inTASK.getDATEPLAN().toGregorianCalendar().getTime());
                                    }


                                    value = inTASK.isISCONTROL();
                                    if (value != null) {
                                        props.put(ErrandsService.PROP_ERRANDS_IS_IMPORTANT, (Serializable) value);
                                    }

                                    String name = GUID.generate();

                                    NodeRef draft = repositoryStructureHelper.getDraftsRef(user);
                                    NodeRef errand = nodeService.createNode(
                                            draft,
                                            ContentModel.ASSOC_CONTAINS,
                                            QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName.createValidLocalName(name)),
                                            ErrandsService.TYPE_ERRANDS,
                                            props).getChildRef();

                                    NodeRef author = orgstructureService.getEmployeeByPerson(user);
                                    nodeService.createAssociation(errand, author, ErrandsService.ASSOC_ERRANDS_INITIATOR);

                                    WSOCOLLECTION executors = inTASK.getEXECUTORS();
                                    if (executors != null) {
                                        for (Object executor : executors.getDATA()) {
                                            NodeRef executorRef = NodeRef.isNodeRef(((WSOPERSON) executor).getID()) ?
                                                    new NodeRef(((WSOPERSON) executor).getID()) :
                                                    null;
                                            if (executorRef != null) {
                                                nodeService.createAssociation(errand, executorRef, ErrandsService.ASSOC_ERRANDS_EXECUTOR);
                                                break;
                                            }
                                        }
                                    }

                                    WSOCOLLECTION coexecutors = inTASK.getSUBEXECUTORS();
                                    if (coexecutors != null) {
                                        for (Object coexecutor : coexecutors.getDATA()) {
                                            NodeRef coexecutorRef = NodeRef.isNodeRef(((WSOPERSON) coexecutor).getID()) ?
                                                    new NodeRef(((WSOPERSON) coexecutor).getID()) :
                                                    null;
                                            if (coexecutorRef != null) {
                                                nodeService.createAssociation(errand, coexecutorRef, ErrandsService.ASSOC_ERRANDS_CO_EXECUTORS);
                                            }
                                        }
                                    }

                                    WSOMPERSON controller = inTASK.getCONTROLLER();
                                    if (controller != null && controller.getID() != null) {
                                        nodeService.createAssociation(errand, new NodeRef(controller.getID()), ErrandsService.ASSOC_ERRANDS_CONTROLLER);
                                    }

                                    if (inTASK.getPARENT() != null && inTASK.getPARENT().getID() != null) {
                                        nodeService.createAssociation(errand, new NodeRef(inTASK.getPARENT().getID()), ErrandsService.ASSOC_ADDITIONAL_ERRANDS_DOCUMENT);
                                    } else {
                                        if (inTASK.getDOCUMENT() != null) {
                                            nodeService.createAssociation(errand, new NodeRef(inTASK.getDOCUMENT().getID()), ErrandsService.ASSOC_ADDITIONAL_ERRANDS_DOCUMENT);
                                        }
                                    }

                                    WSOCOLLECTION attachments = inTASK.getATTACHMENTS();
                                    if (attachments != null) {
                                        NodeRef userTemp = repositoryStructureHelper.getUserTemp(true);

                                        documentAttachmentsService.createRootFolder(errand);
                                        documentAttachmentsService.getCategories(errand);

                                        NodeRef category = documentAttachmentsService.getCategory("Поручение", errand);
                                        if (category != null) {
                                            for (Object attachment : attachments.getDATA()) {
                                                WSOURLFILE file = (WSOURLFILE) attachment;

                                                String fileName = getUniqueNodeName(userTemp, file.getNAME());
                                                WSOURL ref = file.getREFERENCE();
                                                String url = ref.getURL();

                                                final QName assocQName =
                                                        QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, UUID.randomUUID().toString());

                                                final Map<QName, Serializable> properties = new HashMap<>();
                                                properties.put(ContentModel.PROP_NAME, fileName);

                                                NodeRef fileNode = nodeService.createNode(userTemp, ContentModel.ASSOC_CONTAINS,
                                                        assocQName,
                                                        ContentModel.TYPE_CONTENT,
                                                        properties).getChildRef();

                                                try(InputStream in = new URL(url).openStream()) {
                                                    ContentWriter cw = contentService.getWriter(fileNode, ContentModel.PROP_CONTENT, true);
                                                    cw.setMimetype(MimetypeMap.MIMETYPE_PDF);
                                                    cw.putContent(in);

                                                    documentAttachmentsService.addAttachment(fileNode, category);
                                                } catch (Exception ex) {
                                                    logger.error(ex.getMessage(), ex);
                                                }
                                            }
                                        }
                                    }
                                    return inTASK;
                                }
                            }, false, true);
                }
            }, user);
        } catch (Exception e) {
            logger.error("Error while create errand", e);
        }
        return  inTASK;
    }

    private String getUniqueNodeName(NodeRef parent, String newName) {
        boolean exist = nodeService.getChildByName(parent, ContentModel.ASSOC_CONTAINS, newName) != null;
        int count = 0;
        while (exist) {
            count++;
            String countName = newName + "_" + count;
            exist = nodeService.getChildByName(parent, ContentModel.ASSOC_CONTAINS, countName) != null;
            if (!exist) {
                newName = countName;
            }
        }
        return newName.replaceAll("[*\'\"]", "").replaceAll("\\.$", "").trim();
    }

    public void setDocumentAttachmentsService(DocumentAttachmentsService documentAttachmentsService) {
        this.documentAttachmentsService = documentAttachmentsService;
    }

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }
}
