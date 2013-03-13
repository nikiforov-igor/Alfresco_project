package ru.it.lecm.document.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.service.ServiceRegistry;
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
import java.util.HashMap;
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
    private NamespaceService namespaceService;

    public static final String CONTRACTS_ROOT_NAME = "Contracts";



    public void setRepositoryHelper(Repository repositoryHelper) {
        this.repositoryHelper = repositoryHelper;
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
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

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    /**
     * Получение папки Черновики\Contracts
     * @return
     */
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

    /**
     * Получение пути для папки черновиков
     * @return
     */
    public String getDraftPath() {
        NodeRef nodeRef = getDraftRoot();
        String path = nodeService.getPath(nodeRef).toPrefixString(namespaceService);
        return path;
    }

    /**
     * Получение пути для папки Documents
     * @return
     */
    public String getDocumentPath() {
        NodeRef nodeRef = repositoryStructureHelper.getDocumentsRef();
        String path = nodeService.getPath(nodeRef).toPrefixString(namespaceService);
        return path;
    }

}
