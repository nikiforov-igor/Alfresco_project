package ru.it.lecm.dictionary.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ORakovskaya
 * Date: 27.12.12
 */
public class DictionaryBeanImpl implements DictionaryBean {

    /**
     * Service registry
     */
    protected ServiceRegistry services;

    /**
     * Repository helper
     */
    protected Repository repository;

    /**
     * Set the service registry
     *
     * @param services the service registry
     */
    public void setServiceRegistry(ServiceRegistry services) {
        this.services = services;
    }

    /**
     * Set the repository helper
     *
     * @param repository the repository helper
     */
    public void setRepositoryHelper(Repository repository) {
        this.repository = repository;
    }

    @Override
    public NodeRef getDictionaryByName(String name) {
        NodeService nodeService = services.getNodeService();
        repository.init();

        final NodeRef companyHome = repository.getCompanyHome();
        NodeRef dictionariesRoot = nodeService.getChildByName(companyHome, ContentModel.ASSOC_CONTAINS, DictionaryBean.DICTIONARIES_ROOT_NAME);

        return nodeService.getChildByName(dictionariesRoot, ContentModel.ASSOC_CONTAINS, name);
    }

    @Override
    public List<NodeRef> getChildren(NodeRef nodeRef) {
        List<NodeRef> activeChildren = new ArrayList<NodeRef>();

        if (nodeRef != null) {
            NodeService nodeService = services.getNodeService();
            List<ChildAssociationRef> children = nodeService.getChildAssocs(nodeRef);

            if (children != null && !children.isEmpty()) {

                for (ChildAssociationRef child : children) {
                    NodeRef childRef = child.getChildRef();
                    Boolean isActive = (Boolean) nodeService.getProperty(childRef, IS_ACTIVE);
                    isActive = isActive != null ? isActive : Boolean.TRUE; // if property not filled -> active = true default

                    if (isActive) {
                        activeChildren.add(childRef);
                    }
                }
            }
        }

        return activeChildren;
    }
}
