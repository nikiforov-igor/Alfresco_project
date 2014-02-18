package ru.it.lecm.arm.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.arm.beans.childRules.ArmBaseChildRule;
import ru.it.lecm.arm.beans.childRules.ArmDictionaryChildRule;
import ru.it.lecm.arm.beans.childRules.ArmQueryChildRule;
import ru.it.lecm.arm.beans.childRules.ArmStatusesChildRule;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.dictionary.beans.DictionaryBean;

import java.util.*;

/**
 * User: AIvkin
 * Date: 04.02.14
 * Time: 10:10
 */
public class ArmServiceImpl extends BaseBean implements ArmService {

    private DictionaryBean dictionaryService;
    private SearchService searchService;
	private NamespaceService namespaceService;
    private Comparator<NodeRef> comparator = new Comparator<NodeRef>() {
        @Override
        public int compare(NodeRef o1, NodeRef o2) {
            Integer order1 = (Integer) nodeService.getProperty(o1, ArmService.PROP_ARM_ORDER);
            Integer order2 = (Integer) nodeService.getProperty(o2, ArmService.PROP_ARM_ORDER);
            if (order1 == null && order2 != null) {
                return -1;
            } else if (order1 != null && order2 == null) {
                return 1;
            } else if (order1 != null) {
                return order1.compareTo(order2);
            }

            return 0;
        }
    };

    @Override
	public NodeRef getServiceRootFolder() {
		return getFolder(ARM_ROOT_ID);
	}

	@Override
	public boolean isArmAccordion(NodeRef ref) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_ARM_ACCORDION);
		return isProperType(ref, types);
	}

	@Override
	public boolean isArmNode(NodeRef ref) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_ARM_NODE);
		return isProperType(ref, types);
	}

	public NodeRef getDictionaryArmSettings() {
		return nodeService.getChildByName(getServiceRootFolder(), ContentModel.ASSOC_CONTAINS, ARM_SETTINGS_DICTIONARY_NAME);
	}

	@Override
	public NodeRef getArmByCode(String code) {
		Set<QName> armTypeSet = new HashSet<QName>(1);
		armTypeSet.add(TYPE_ARM);

		List<ChildAssociationRef> arms = nodeService.getChildAssocs(getDictionaryArmSettings(), armTypeSet);
		if (arms != null) {
			for (ChildAssociationRef armAssoc : arms) {
				if (nodeService.getProperty(armAssoc.getChildRef(), PROP_ARM_CODE).equals(code)) {
					return armAssoc.getChildRef();
				}
			}
		}
		return null;
	}

	@Override
	public List<NodeRef> getArmAccordions(NodeRef arm) {
		List<NodeRef> result = new ArrayList<NodeRef>();

		Set<QName> typeSet = new HashSet<QName>(1);
		typeSet.add(TYPE_ARM_ACCORDION);
		List<ChildAssociationRef> accordionsAssocs = nodeService.getChildAssocs(arm, typeSet);
		if (accordionsAssocs != null) {
			for (ChildAssociationRef accordionAssoc : accordionsAssocs) {
				result.add(accordionAssoc.getChildRef());
			}
		}
        Collections.sort(result, comparator);
		return result;
	}

	@Override
	public List<NodeRef> getChildNodes(NodeRef node) {
		List<NodeRef> result = new ArrayList<NodeRef>();

		Set<QName> typeSet = new HashSet<QName>(1);
		typeSet.add(TYPE_ARM_ACCORDION);
		typeSet.add(TYPE_ARM_NODE);
		List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(node, typeSet);
		if (childAssocs != null) {
			for (ChildAssociationRef child : childAssocs) {
				result.add(child.getChildRef());
			}
		}
        Collections.sort(result, comparator);
		return result;
	}

	@Override
	public List<String> getNodeTypes(NodeRef node) {
		List<String> result = new ArrayList<String>();
		String types = (String) nodeService.getProperty(node, PROP_NODE_TYPES);
		if (types != null && types.length() > 0) {
			result.addAll(Arrays.asList(types.split(",")));
		}

		return result;
	}

	@Override
	public Collection<QName> getNodeTypesIncludeInherit(NodeRef node) {
		Collection<QName> results = new ArrayList<QName>();
		if (isArmNode(node) || isArmAccordion(node)) {
			String types = (String) nodeService.getProperty(node, PROP_NODE_TYPES);
			if (types != null && types.length() > 0) {
				List<String> stringTypes = Arrays.asList(types.split(","));
				for (String type: stringTypes) {
					results.add(QName.createQName(type, namespaceService));
				}
			}
			if (results.size() == 0 && isArmNode(node)) {
				results = getNodeTypesIncludeInherit(nodeService.getPrimaryParent(node).getParentRef());
			}
		}
		return results;
	}

	@Override
	public List<ArmFilter> getNodeFilters(NodeRef node) {
		List<ArmFilter> result = new ArrayList<ArmFilter>();
        List<NodeRef> filters = findNodesByAssociationRef(node, ASSOC_NODE_FILTERS, TYPE_ARM_FILTER, ASSOCIATION_TYPE.TARGET);
        if (filters != null) {
            for (NodeRef ref : filters) {
                ArmFilter filter = new ArmFilter();
                filter.setTitle((String) nodeService.getProperty(ref, ContentModel.PROP_NAME));
                filter.setCode((String) nodeService.getProperty(ref, PROP_FILTER_CODE));

                Object multipleValue = nodeService.getProperty(ref, PROP_FILTER_MULTIPLE);
                if (multipleValue != null) {
                    filter.setMultipleSelect((Boolean) multipleValue);
                }

                filter.setValues((String) nodeService.getProperty(ref, PROP_FILTER_VALUES));

                Object query = nodeService.getProperty(ref, PROP_FILTER_QUERY);
                if (query != null) {
                    filter.setQuery((String) query);
                }

                Object fClass = nodeService.getProperty(ref, PROP_FILTER_CLASS);
                if (fClass != null) {
                    filter.setFilterClass((String) fClass);
                }
                result.add(filter);
            }
        }

		return result;
	}

	@Override
	public ArmCounter getNodeCounter(NodeRef node) {
		boolean counterEnable = (Boolean) nodeService.getProperty(node, PROP_COUNTER_ENABLE);
		if (counterEnable) {
			ArmCounter result = new ArmCounter();
			result.setQuery((String) nodeService.getProperty(node, PROP_COUNTER_QUERY));
			result.setDescription((String) nodeService.getProperty(node, PROP_COUNTER_DESCRIPTION));

			return result;
		}

		return null;
	}

	@Override
	public List<ArmColumn> getNodeColumns(NodeRef node) {
		List<ArmColumn> result = new ArrayList<ArmColumn>();
		List<NodeRef> columns = findNodesByAssociationRef(node, ASSOC_NODE_COLUMNS, TYPE_ARM_COLUMN, ASSOCIATION_TYPE.TARGET);
		if (columns != null) {
			for (NodeRef ref : columns) {
				ArmColumn column = new ArmColumn();
				column.setTitle((String) nodeService.getProperty(ref, PROP_COLUMN_TITLE));
				column.setField((String) nodeService.getProperty(ref, PROP_COLUMN_FIELD_NAME));
				column.setFormatString((String) nodeService.getProperty(ref, PROP_COLUMN_FORMAT_STRING));
                Object sortableValue = nodeService.getProperty(ref, PROP_COLUMN_SORTABLE);
                if (sortableValue != null) {
                    column.setSortable((Boolean) sortableValue);
                }
				result.add(column);
			}
		}

		return result;
	}

	@Override
	public ArmBaseChildRule getNodeChildRule(NodeRef node) {
		if (isArmNode(node)) {
			List<AssociationRef> queryAssoc = nodeService.getTargetAssocs(node, ASSOC_NODE_CHILD_RULE);
			if (queryAssoc != null && queryAssoc.size() > 0) {
				ArmBaseChildRule result = null;
				NodeRef query = queryAssoc.get(0).getTargetRef();
				QName queryType = nodeService.getType(query);
				if (TYPE_QUERY_CHILD_RULE.equals(queryType)) {
					result = new ArmQueryChildRule();
					((ArmQueryChildRule) result).setListQuery((String) nodeService.getProperty(query, PROP_LIST_QUERY_CHILD_RULE));
                    ((ArmQueryChildRule) result).setSearchService(searchService);
				} else if (TYPE_DICTIONARY_CHILD_RULE.equals(queryType)) {
					result = new ArmDictionaryChildRule();
					NodeRef dictionary = findNodeByAssociationRef(query, ASSOC_DICTIONARY_CHILD_RULE, DictionaryBean.TYPE_DICTIONARY, ASSOCIATION_TYPE.TARGET);
					((ArmDictionaryChildRule) result).setDictionary(dictionary);
                    ((ArmDictionaryChildRule) result).setDictionaryService(dictionaryService);
				} else if (TYPE_STATUSES_CHILD_RULE.equals(queryType)) {
					result = new ArmStatusesChildRule();
					((ArmStatusesChildRule) result).setRule((String) nodeService.getProperty(query, PROP_STATUSES_RULE));

					String selectedStatuses = (String) nodeService.getProperty(query, PROP_SELECTED_STATUSES);
					if (selectedStatuses != null) {
						List<String> selectedStatusesList = new ArrayList<String>();
						for (String str: selectedStatuses.split(",")) {
							String status = str.trim();
							if (status.length() > 0) {
								selectedStatusesList.add(status);
							}
						}

						((ArmStatusesChildRule) result).setSelectedStatuses(selectedStatusesList);
					}
				}
				return result;
			}
		}
		return null;
	}

    public void setDictionaryService(DictionaryBean dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}
}
