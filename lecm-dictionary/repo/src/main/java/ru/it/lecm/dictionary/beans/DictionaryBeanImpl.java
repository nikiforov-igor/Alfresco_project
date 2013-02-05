package ru.it.lecm.dictionary.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.BaseBean;

/**
 * User: ORakovskaya
 * Date: 27.12.12
 */
public class DictionaryBeanImpl extends BaseBean implements DictionaryBean {

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
        repository.init();

        final NodeRef companyHome = repository.getCompanyHome();
        NodeRef dictionariesRoot = nodeService.getChildByName(companyHome, ContentModel.ASSOC_CONTAINS, DictionaryBean.DICTIONARIES_ROOT_NAME);

        return nodeService.getChildByName(dictionariesRoot, ContentModel.ASSOC_CONTAINS, name);
    }

    @Override
    public List<NodeRef> getChildren(NodeRef nodeRef) {
        List<NodeRef> activeChildren = new ArrayList<NodeRef>();

        if (nodeRef != null) {
            List<ChildAssociationRef> children = nodeService.getChildAssocs(nodeRef);

            if (children != null && !children.isEmpty()) {

                for (ChildAssociationRef child : children) {
                    NodeRef childRef = child.getChildRef();
                    if (!isArchive(childRef)) {
                        activeChildren.add(childRef);
                    }
                }
            }
        }

        return activeChildren;
    }

	@Override
	public List<NodeRef> getRecordsByParamValue(String dictionaryName, QName parameter, Serializable value) {
		List<NodeRef> results = new ArrayList<NodeRef>();
		NodeRef obTypeDictionary = getDictionaryByName(dictionaryName);
		if (obTypeDictionary != null && value != null && parameter != null) {
			List<ChildAssociationRef> dicValues = nodeService.getChildAssocs(obTypeDictionary);
			for (ChildAssociationRef dicValue : dicValues) {
				NodeRef record = dicValue.getChildRef();
				if (!isArchive(record)) {
					Serializable recordClass = nodeService.getProperty(record, parameter);
					if (recordClass.equals(value)) {
						results.add(record);
					}
				}

			}
		}
		return results;
	}

	@Override
	public NodeRef getRecordByParamValue(String dictionaryName, QName parameter, Serializable value) {
		NodeRef result = null;
		List<NodeRef> results = getRecordsByParamValue(dictionaryName, parameter, value);
		if (results.size() > 0) {
			result = results.get(results.size() - 1);
		}
		return result;
	}
}
