package ru.it.lecm.dictionary.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.search.impl.lucene.LuceneQueryParserException;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;

import java.io.Serializable;
import java.util.*;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.springframework.context.ApplicationEvent;

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
            parameterValue = parameterValue != null ? parameterValue.replaceAll(":", "\\\\:").replaceAll("-", "\\\\-") : "";
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
	public List<NodeRef> getChildrenSortedByName(NodeRef nodeRef) {
		List<NodeRef> resultList = new ArrayList<>();

		Path path = nodeService.getPath(nodeRef);
		SearchParameters sp = new SearchParameters();
		sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);
		sp.setQuery("PATH:\"" + path.toPrefixString(namespaceService) + "/*\" AND NOT @lecm\\-dic\\:active:false");
		sp.addSort("@" + ContentModel.PROP_NAME, true);
		ResultSet resultSet = searchService.query(sp);

		if (resultSet != null) {
			for (ResultSetRow row : resultSet) {
				resultList.add(row.getNodeRef());
			}
		}

		return resultList;
	}


	@Override
	public List<NodeRef> getAllChildren(NodeRef nodeRef) {

		List<NodeRef> resultList = new ArrayList<>();

		if (nodeRef != null) {
			Path path = nodeService.getPath(nodeRef);
			QName type = nodeService.getType(nodeRef);
			SearchParameters sp = new SearchParameters();
			sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
			sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);
			sp.setQuery("PATH:\"" + path.toPrefixString(namespaceService) + "//*\" AND TYPE:\"" + type.toPrefixString(namespaceService) + "\" AND NOT @lecm\\-dic\\:active:false");
			ResultSet resultSet = searchService.query(sp);

			if (resultSet != null) {
				for (ResultSetRow row : resultSet) {
					resultList.add(row.getNodeRef());
				}
			}
		}

		return resultList;
	}

	@Override
	public List<NodeRef> getRecordsByParamValue(String dictionaryName, QName parameter, Serializable value) {
        return getRecordsByParamValueInternal(dictionaryName, parameter, value, false);
	}

    private List<NodeRef> getRecordsByParamValueInternal(String dictionaryName, QName parameter, Serializable value, boolean returnLastOnly) {
        List<NodeRef> results = new ArrayList<>();
        NodeRef obTypeDictionary = getDictionaryByName(dictionaryName);
        if (obTypeDictionary != null && value != null && parameter != null) {
            List<ChildAssociationRef> dicValues = nodeService.getChildAssocs(obTypeDictionary);
            if (returnLastOnly) {
                Collections.reverse(dicValues);
            }
            for (ChildAssociationRef dicValue : dicValues) {
                NodeRef record = dicValue.getChildRef();
                Serializable recordClass = nodeService.getProperty(record, parameter);
                if (recordClass != null && recordClass.equals(value) && !isArchive(record)) {
                    results.add(record);
                    if (returnLastOnly) {
                        break;
                    }
                }
            }
        }
        return results;
    }

    @Override
	public NodeRef getRecordByParamValue(String dictionaryName, QName parameter, Serializable value) {
		NodeRef result = null;
		List<NodeRef> results = getRecordsByParamValueInternal(dictionaryName, parameter, value, true);
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
			while (parent != null && parent.getParentRef() !=null && !isDictionary(parent.getParentRef())) {
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

	@Override
	public Serializable getAllDictionaryTypes() {
		List<QName> types = new ArrayList<>();
		types.addAll(dictionaryService.getSubTypes(TYPE_PLANE_DICTIONARY_VALUE, true));
		types.addAll(dictionaryService.getSubTypes(TYPE_HIERARCHICAL_DICTIONARY_VALUE, true));
		types.remove(TYPE_PLANE_DICTIONARY_VALUE);
		types.remove(TYPE_HIERARCHICAL_DICTIONARY_VALUE);

		ArrayList<String> result = new ArrayList<>();
		for (QName type : types) {
			TypeDefinition typedef = dictionaryService.getType(type);
			boolean isPlane = dictionaryService.isSubClass(type, TYPE_PLANE_DICTIONARY_VALUE);
			String title = StringUtils.defaultString(typedef.getTitle(dictionaryService));
			result.add(String.format("%s|%s|%s", type.toPrefixString(namespaceService), isPlane, title));
		}

		return result;
	}

	@Override
	public Serializable getExistDictionaryTypes() {
		ArrayList<String> result = new ArrayList<>();

		List<NodeRef> dictionaries = new ArrayList<>();
		NodeRef root = getDictionariesRoot();

		if (root != null) {
			List<ChildAssociationRef> children = nodeService.getChildAssocs(root);

			if (children != null && !children.isEmpty()) {

				for (ChildAssociationRef child : children) {
					NodeRef childRef = child.getChildRef();
					if (!isArchive(childRef) && isDictionary(childRef)) {
						dictionaries.add(childRef);
					}
				}
			}
		}

		for (NodeRef dictionary : dictionaries) {
			String dicType = (String)nodeService.getProperty(dictionary, PROPERTY_DICTIONARY_TYPE);
			QName type = QName.createQName(dicType, namespaceService);
			TypeDefinition typedef = dictionaryService.getType(type);
			boolean isPlane = dictionaryService.isSubClass(type, TYPE_PLANE_DICTIONARY_VALUE);
			String title = StringUtils.defaultString(typedef.getTitle(dictionaryService));
			result.add(String.format("%s|%s|%s", type.toPrefixString(namespaceService), isPlane, title));
		}

		return result;
	}

	@Override
	public Serializable getDictionaryTypeProperties(final String dicType) {
		QName type = QName.createQName(dicType, namespaceService);
		TypeDefinition typedef = dictionaryService.getType(type);
		Map<QName, PropertyDefinition> propDefs = typedef.getProperties();
		Collection<PropertyDefinition> properties = propDefs.values();

		ArrayList<String> result = new ArrayList<>();
		PropertyDefinition cmNameDef = dictionaryService.getProperty(ContentModel.PROP_NAME);
		PropertyDefinition cmTitleDef = dictionaryService.getProperty(ContentModel.PROP_TITLE);
		result.add(String.format("%s|%s", ContentModel.PROP_NAME.toPrefixString(namespaceService), StringUtils.defaultString(cmNameDef.getTitle(dictionaryService))));
		result.add(String.format("%s|%s", ContentModel.PROP_TITLE.toPrefixString(namespaceService), StringUtils.defaultString(cmTitleDef.getTitle(dictionaryService))));

		for (PropertyDefinition property : properties) {
			if (type.isMatch(property.getContainerClass().getName())) {
				String name = property.getName().toPrefixString(namespaceService);
				String title = StringUtils.defaultString(property.getTitle(dictionaryService));
				result.add(String.format("%s|%s", name, title));
			}
		}
		return result;
	}
}
