package ru.it.lecm.document.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.RepositoryStructureHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: mshafeev
 * Date: 28.02.13
 * Time: 15:01
 */
public class ContractsServiceImpl extends BaseBean {
    final protected Logger logger = LoggerFactory.getLogger(ContractsServiceImpl.class);

    private ServiceRegistry serviceRegistry;
    private Repository repositoryHelper;
    private NodeService nodeService;
    private RepositoryStructureHelper repositoryStructureHelper;
    static final String DOCUMENT_MODEL_URI = "http://www.it.ru/logicECM/contract/1.0";
    static final QName PROP_CONTRACT_DOCUMENT = QName.createQName(DOCUMENT_MODEL_URI, "lecm-contract");

    public static final String CONTRACTS_ROOT_NAME = "Contracts";
    private FileFolderService fileFolderService;



    public void setRepositoryHelper(Repository repositoryHelper) {
        this.repositoryHelper = repositoryHelper;
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
        fileFolderService = serviceRegistry.getFileFolderService();
    }

    public ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }
    public RepositoryStructureHelper getRepositoryStructureHelper() {
        return repositoryStructureHelper;
    }

    public void setRepositoryStructureHelper(RepositoryStructureHelper repositoryStructureHelper) {
        this.repositoryStructureHelper = repositoryStructureHelper;
    }
    /**
     * Создание каталога для договоров (внутри /CompanyHome)
     * @return
     */
    public void init() {
        final String rootName = CONTRACTS_ROOT_NAME;
        repositoryHelper.init();
        final NodeRef companyHome = repositoryHelper.getCompanyHome();
        AuthenticationUtil.RunAsWork<NodeRef> raw = new AuthenticationUtil.RunAsWork<NodeRef>() {
            @Override
            public NodeRef doWork() throws Exception {
                return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
                    @Override
                    public NodeRef execute() throws Throwable {
                        NodeRef nodeRef;
                            // еще раз пытаемся получить директорию (на случай если она уже была создана другим потоком
                            nodeRef = nodeService.getChildByName(companyHome, ContentModel.ASSOC_CONTAINS,
                                    rootName);
                            if (nodeRef == null) {
                                QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
                                QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, rootName);
                                QName nodeTypeQName = ContentModel.TYPE_FOLDER;

                                Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1); //optional map of properties to keyed by their qualified names
                                properties.put(ContentModel.PROP_NAME, rootName);
                                ChildAssociationRef associationRef = nodeService.createNode(companyHome, assocTypeQName, assocQName, nodeTypeQName, properties);
                                nodeRef = associationRef.getChildRef();
                            }
                        return nodeRef;
                    }
                });
            }
        };
        AuthenticationUtil.runAsSystem(raw);
    }

    public List<NodeRef> getChild(NodeRef nodeRef) {

        List<NodeRef> result = new ArrayList<NodeRef>();
        // получаем дочерние элементы
        List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(nodeRef);

        if (!childAssocs.isEmpty()) {
            for (ChildAssociationRef subNodeAss : childAssocs) {
                NodeRef childRef = subNodeAss.getChildRef();
                if (nodeService.getType(childRef ).getLocalName().equals("folder")) {
                    result.addAll(getChild(childRef));
                } else {
                    if (nodeService.getType(childRef).getLocalName().equals("document")) {
                        try {
                            fileFolderService.getFileInfo(nodeRef);    //проверка доступности
                            fileFolderService.getFileInfo(childRef);    //проверка доступности
                            result.add(childRef);
                        } catch (AccessDeniedException e) {
                            //пропускаем недоступные
                        }
                    }
                }
            }
        }

        return result;
    }

    public List<NodeRef> getContracts(NodeRef nodeRef, String sortColumnLocalName, final boolean sortAscending) {
        NodeRef documentRef = repositoryStructureHelper.getDocumentsRef();
        NodeRef person = repositoryHelper.getPerson();
        NodeRef draftRef = repositoryStructureHelper.getDraftsRef(person);

        List<NodeRef> result = getChild(draftRef);
        result.addAll(getChild(documentRef));

        return result;
    }

    public NodeRef getDraftRoot() {
        NodeRef person = repositoryHelper.getPerson();
        NodeRef draftRef = repositoryStructureHelper.getDraftsRef(person);
        NodeRef nodeRef = nodeService.getChildByName(draftRef, ContentModel.ASSOC_CONTAINS, CONTRACTS_ROOT_NAME);

        if (nodeRef == null) {
            QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
            QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, CONTRACTS_ROOT_NAME);
            QName nodeTypeQName = ContentModel.TYPE_FOLDER;

            Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1); //optional map of properties to keyed by their qualified names
            properties.put(ContentModel.PROP_NAME, CONTRACTS_ROOT_NAME);
            ChildAssociationRef associationRef = nodeService.createNode(draftRef, assocTypeQName, assocQName, nodeTypeQName, properties);

            return associationRef.getChildRef();
        }
        return nodeRef;
    }

}
