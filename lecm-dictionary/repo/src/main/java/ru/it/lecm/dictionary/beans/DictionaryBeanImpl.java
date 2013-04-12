package ru.it.lecm.dictionary.beans;

import java.io.Serializable;
import java.util.*;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.BaseBean;

/**
 * User: ORakovskaya
 * Date: 27.12.12
 */
public class DictionaryBeanImpl extends BaseBean implements DictionaryBean {

	private DictionaryService dictionaryService;

	public void setDictionaryService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

    @Override
    public NodeRef getDictionaryByName(String name) {
        return nodeService.getChildByName(getDictionariesRoot(), ContentModel.ASSOC_CONTAINS, name);
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

	@Override
	public boolean isDictionary(NodeRef ref) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_DICTIONARY);
		return isProperType(ref, types);
	}

	@Override
	public boolean isHeirarchicalDictionaryValue(NodeRef ref) {
		return isProperSubType(ref, TYPE_HIERARCHICAL_DICTIONARY_VALUE);
	}

	@Override
	public boolean isPlaneDictionaryValue(NodeRef ref) {
		return isProperSubType(ref, TYPE_PLANE_DICTIONARY_VALUE);
	}

	public boolean isProperSubType(NodeRef ref, QName type) {
		QName refType = nodeService.getType(ref);
		if (refType != null) {
			return dictionaryService.isSubClass(refType, type);
		}
		return false;
	}

	@Override
	public boolean isDictionaryValue(NodeRef ref) {
		return isPlaneDictionaryValue(ref) || isHeirarchicalDictionaryValue(ref);
	}

	@Override
	public NodeRef getDictionaryByDictionaryValue(NodeRef nodeRef) {
		if (isDictionaryValue(nodeRef)) {
			ChildAssociationRef parent = nodeService.getPrimaryParent(nodeRef);
			while (parent != null && !isDictionary(parent.getParentRef())) {
				parent = nodeService.getPrimaryParent(parent.getParentRef());
			}
			if (parent != null) {
				return parent.getParentRef();
			}
		}
		return null;
	}

	@Override
	public NodeRef getDictionariesRoot() {
		return getFolder(DICTIONARIES_ROOT_ID);
	}

	@Override
	public NodeRef getServiceRootFolder() {
		return getDictionariesRoot();
	}
}
