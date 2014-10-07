package ru.it.lecm.dictionary.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.search.impl.lucene.LuceneQueryParserException;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import ru.it.lecm.base.beans.WriteTransactionNeededException;

/**
 * User: ORakovskaya
 * Date: 27.12.12
 */
public class DictionaryBeanImpl extends BaseBean implements DictionaryBean {
	final static protected Logger logger = LoggerFactory.getLogger(DictionaryBeanImpl.class);

	private DictionaryService dictionaryService;
	private SearchService searchService;
	private NamespaceService namespaceService;

	public void setDictionaryService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	@Override
    public NodeRef getDictionaryByName(String name) {
        return nodeService.getChildByName(getDictionariesRoot(), ContentModel.ASSOC_CONTAINS, name);
    }

	@Override
	public NodeRef getDictionaryValueByParam(String dictionaryName, QName parameterName, String parameterValue) {
		NodeRef dictionary = getDictionaryByName(dictionaryName);
		if (dictionary != null) {
			return getDictionaryValueByParam(dictionary, parameterName, parameterValue);
		}
		return null;
	}

	@Override
	public NodeRef getDictionaryValueByParam(NodeRef dictionaryRef, QName parameterName, String parameterValue) {
		String path = nodeService.getPath(dictionaryRef).toPrefixString(namespaceService);
		String type = (String) nodeService.getProperty(dictionaryRef, PROPERTY_DICTIONARY_TYPE);
		if (path != null && type != null) {
			String propParameterName = "@" + parameterName.toPrefixString(namespaceService).replace(":", "\\:");

			SearchParameters parameters = new SearchParameters();
			parameters.setLanguage(SearchService.LANGUAGE_LUCENE);
			parameters.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
			parameters.setQuery(" +PATH:\"" + path + "//*\" AND TYPE:\"" + type + "\" AND " + propParameterName + ":\"" + parameterValue + "\"");
			ResultSet resultSet = null;
			try {
				resultSet = searchService.query(parameters);
				for (ResultSetRow row : resultSet) {
					NodeRef node = row.getNodeRef();
					if (!isArchive(node)) {
						return node;
					}
				}
			} catch (LuceneQueryParserException e) {
				logger.error("Error while getting dictionary value", e);
			} catch (Exception e) {
				logger.error("Error while getting dictionary value", e);
			} finally {
				if (resultSet != null) {
					resultSet.close();
				}
			}
		}
		return null;
	}

    @Override
    public List<NodeRef> getChildren(NodeRef nodeRef) {
        List<NodeRef> activeChildren = new ArrayList<NodeRef>();
		
        if (nodeRef != null) {
            List<ChildAssociationRef> children = nodeService.getChildAssocs(nodeRef);

            if (children != null && !children.isEmpty()) {

                for (ChildAssociationRef child : children) {
                    NodeRef childRef = child.getChildRef();
                    if (!isArchive(childRef) && isDictionaryValue(childRef)) {
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
				Serializable recordClass = nodeService.getProperty(record, parameter);
				if (recordClass != null && recordClass.equals(value) && !isArchive(record)) {
					results.add(record);
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
