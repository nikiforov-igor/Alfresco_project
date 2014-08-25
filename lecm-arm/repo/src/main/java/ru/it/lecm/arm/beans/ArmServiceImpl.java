package ru.it.lecm.arm.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.cache.SimpleCache;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.arm.beans.childRules.ArmBaseChildRule;
import ru.it.lecm.arm.beans.childRules.ArmDictionaryChildRule;
import ru.it.lecm.arm.beans.childRules.ArmQueryChildRule;
import ru.it.lecm.arm.beans.childRules.ArmStatusesChildRule;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.TransactionNeededException;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.statemachine.StateMachineServiceBean;

import java.io.Serializable;
import java.util.*;

/**
 * User: AIvkin
 * Date: 04.02.14
 * Time: 10:10
 */
public class ArmServiceImpl extends BaseBean implements ArmService {
	private Logger logger = LoggerFactory.getLogger(ArmServiceImpl.class);

    private DictionaryBean dictionaryService;
    private SearchService searchService;
	private NamespaceService namespaceService;
    private OrgstructureBean orgstructureBean;

    private SimpleCache<String, List<ArmColumn>> columnsCache;
    private SimpleCache<NodeRef, List<ArmFilter>> filtersCache;
    private SimpleCache<NodeRef, List<NodeRef>> childNodesCache;
    private SimpleCache<NodeRef, List<String>> nodesTypesCache;

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

    private StateMachineServiceBean stateMachineService;

    public void setColumnsCache(SimpleCache<String, List<ArmColumn>> columnsCache) {
        this.columnsCache = columnsCache;
    }

    public void setFiltersCache(SimpleCache<NodeRef, List<ArmFilter>> filtersCache) {
        this.filtersCache = filtersCache;
    }

    public void setChildNodesCache(SimpleCache<NodeRef, List<NodeRef>> childNodesCache) {
        this.childNodesCache = childNodesCache;
    }

    public void setNodesTypesCache(SimpleCache<NodeRef, List<String>> nodesTypesCache) {
        this.nodesTypesCache = nodesTypesCache;
    }

    public void setOrgstructureBean(OrgstructureBean orgstructureBean) {
        this.orgstructureBean = orgstructureBean;
    }

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

	@Override
	public boolean isArmReportsNode(NodeRef ref) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_ARM_REPORTS_NODE);
		return isProperType(ref, types);
	}

	@Override
	public boolean isArmElement(NodeRef ref) {
		Set<QName> types = new HashSet<QName>();
        types.add(TYPE_ARM_ACCORDION);
        types.add(TYPE_ARM_NODE);
		types.add(TYPE_ARM_REPORTS_NODE);
		types.add(TYPE_ARM_HTML_NODE);
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
        if (childNodesCache.contains(node)) {
            return childNodesCache.get(node);
        }
        List<NodeRef> result = new ArrayList<NodeRef>();

        Set<QName> typeSet = new HashSet<QName>(1);
        typeSet.add(TYPE_ARM_ACCORDION);
        typeSet.add(TYPE_ARM_NODE);
        typeSet.add(TYPE_ARM_REPORTS_NODE);
        typeSet.add(TYPE_ARM_HTML_NODE);
        List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(node, typeSet);
        if (childAssocs != null) {
            for (ChildAssociationRef child : childAssocs) {
                result.add(child.getChildRef());
            }
        }
        Collections.sort(result, comparator);
        childNodesCache.put(node, result);
        return result;
    }

	@Override
	public List<String> getNodeTypes(NodeRef node) {
        if (nodesTypesCache.contains(node)) {
            return nodesTypesCache.get(node);
        }
		List<String> result = new ArrayList<String>();
		String types = (String) nodeService.getProperty(node, PROP_NODE_TYPES);
		if (types != null && types.length() > 0) {
			result.addAll(Arrays.asList(types.split(",")));
		}
        nodesTypesCache.put(node, result);
		return result;
	}

	@Override
	public Collection<QName> getNodeTypesIncludeInherit(NodeRef node) {
		Collection<QName> results = new ArrayList<QName>();
		if (isArmElement(node)) {
			String types = (String) nodeService.getProperty(node, PROP_NODE_TYPES);
			if (types != null && types.length() > 0) {
				List<String> stringTypes = Arrays.asList(types.split(","));
				for (String type: stringTypes) {
					results.add(QName.createQName(type, namespaceService));
				}
			}
			if (results.size() == 0 && !isArmAccordion(node)) {
				results = getNodeTypesIncludeInherit(nodeService.getPrimaryParent(node).getParentRef());
			}
		}
		return results;
	}

	@Override
	public List<ArmFilter> getNodeFilters(NodeRef node) {
        if (filtersCache.contains(node)) {
            return filtersCache.get(node);
        }
		List<ArmFilter> result = new ArrayList<>();
        List<NodeRef> filters = findNodesByAssociationRef(node, ASSOC_NODE_FILTERS, null, ASSOCIATION_TYPE.TARGET);
        if (filters != null) {
            for (NodeRef ref : filters) {
                if (!isArchive(ref)) {
                    ArmFilter filter = new ArmFilter();
                    Map<QName, Serializable> props = nodeService.getProperties(ref);
                    filter.setTitle((String) props.get(ContentModel.PROP_NAME));
                    filter.setCode((String) props.get(PROP_FILTER_CODE));

                    Object multipleValue = props.get(PROP_FILTER_MULTIPLE);
                    if (multipleValue != null) {
                        filter.setMultipleSelect((Boolean) multipleValue);
                    }

                    List<ArmFilterValue> valueList = new ArrayList<ArmFilterValue>();
                    String valuesStr = (String) props.get(PROP_FILTER_VALUES);
                    if (!valuesStr.isEmpty())  {
                        String[] valuesArray = valuesStr.split(",");

                        for(String value :valuesArray){
                            if (!value.trim().isEmpty()) {
                                String[] v = value.trim().split("\\|");
                                if (v.length >= 2) {
                                    valueList.add(new ArmFilterValue(v[1],v[0]));
                                }
                            }
                        }
                    }

                    filter.setValues(valueList);

                    Object query = props.get(PROP_FILTER_QUERY);
                    if (query != null) {
                        filter.setQuery((String) query);
                    }

                    Object fClass = props.get(PROP_FILTER_CLASS);
                    if (fClass != null) {
                        filter.setFilterClass((String) fClass);
                    }
                    result.add(filter);
                }
            }
        }
        filtersCache.put(node, result);
		return result;
	}

	@Override
	public ArmCounter getNodeCounter(NodeRef node) {
        Map<QName, Serializable> props = nodeService.getProperties(node);
		Boolean counterEnable = (Boolean) props.get(PROP_COUNTER_ENABLE);
		if (counterEnable != null && counterEnable) {
			ArmCounter result = new ArmCounter();
			result.setQuery((String) props.get(PROP_COUNTER_QUERY));
			result.setDescription((String) props.get(PROP_COUNTER_DESCRIPTION));

			return result;
		}

		return null;
	}

	@Override
	public List<ArmColumn> getNodeColumns(NodeRef node) {
		List<ArmColumn> result = getUserNodeColumns(node); // получаем список колонок из настроек пользователя
        if (result.isEmpty()) { // пусто - тащим из настроек АРМ
            if (columnsCache.contains(node.toString())) {
                return columnsCache.get(node.toString());
            }
            result = new ArrayList<>();     //сбрасываем, чтобы не спутать кэши
            List<NodeRef> columns = findNodesByAssociationRef(node, ASSOC_NODE_COLUMNS, null, ASSOCIATION_TYPE.TARGET);
            if (columns != null) {
                for (NodeRef ref : columns) {
                    Map<QName, Serializable> columnProps = nodeService.getProperties(ref);

                    Object byDefaultValue = columnProps.get(PROP_COLUMN_BY_DEFAULT);
                    if (byDefaultValue == null || (Boolean)byDefaultValue) {
                        ArmColumn column = new ArmColumn(ref);

                        column.setTitle((String) columnProps.get(PROP_COLUMN_TITLE));
                        column.setField((String) columnProps.get(PROP_COLUMN_FIELD_NAME));
                        column.setFormatString((String) columnProps.get(PROP_COLUMN_FORMAT_STRING));
                        Object sortableValue = columnProps.get(PROP_COLUMN_SORTABLE);
                        if (sortableValue != null) {
                            column.setSortable((Boolean) sortableValue);
                        }
                        column.setByDefault(true);
                        result.add(column);
                    }
                }
            }
            columnsCache.put(node.toString(), result);
        }
		return result;
	}

    @Override
    public List<NodeRef> getNodeColumnsRefs(NodeRef node) {
        List<NodeRef> nodeColumns = findNodesByAssociationRef(node, ASSOC_NODE_COLUMNS, null, ASSOCIATION_TYPE.TARGET);
        if (nodeColumns.isEmpty()) {
            NodeRef parent = nodeService.getPrimaryParent(node).getParentRef();
            if (isArmElement(parent)) {
                return getNodeColumnsRefs(parent);
            }
        }
        return nodeColumns;
    }

    @Override
    public List<ArmColumn> getUserNodeColumns(NodeRef node) {
        String currentEmployeeLogin = AuthenticationUtil.getFullyAuthenticatedUser();
        String key = currentEmployeeLogin + node.toString();
        if (columnsCache.contains(key)) {
            return columnsCache.get(key);
        }
        List<ArmColumn> result = new ArrayList<>();
        NodeRef employee = orgstructureBean.getEmployeeByPerson(currentEmployeeLogin, false);
        if (employee != null) {
            final NodeRef employeeSettingsRef = getNodeUserSettings(node, currentEmployeeLogin);
            if (employeeSettingsRef != null) { // может быть пусто, так как настройки создаются при изменении списка колонок
                List<NodeRef> columns = findNodesByAssociationRef(employeeSettingsRef, ASSOC_USER_NODE_COLUMNS, null, ASSOCIATION_TYPE.TARGET);
                if (columns != null) {
                    for (NodeRef ref : columns) {
                        ArmColumn column = new ArmColumn(ref);
                        Map<QName, Serializable> columnProps = nodeService.getProperties(ref);
                        column.setTitle((String) columnProps.get(PROP_COLUMN_TITLE));
                        column.setField((String) columnProps.get(PROP_COLUMN_FIELD_NAME));
                        column.setFormatString((String) columnProps.get(PROP_COLUMN_FORMAT_STRING));
                        Object sortableValue = columnProps.get(PROP_COLUMN_SORTABLE);
                        if (sortableValue != null) {
                            column.setSortable((Boolean) sortableValue);
                        }
                        Object byDefaultValue = columnProps.get(PROP_COLUMN_BY_DEFAULT);
                        if (byDefaultValue != null) {
                            column.setByDefault((Boolean) byDefaultValue);
                        }
                        result.add(column);
                    }
                }
            }
        }
        columnsCache.put(key, result);
        return result;
    }

    private NodeRef getNodeUserSettings(final NodeRef node, String loginName) {
        return nodeService.getChildByName(node, ContentModel.ASSOC_CONTAINS, loginName);
    }

    @Override
    public NodeRef getNodeUserSettings(final NodeRef node) {
        NodeRef employee = orgstructureBean.getCurrentEmployee();
        if (employee == null) {
            logger.error("Could not get current employee. Skip creating settings object");
            return null;
        }
        String loginName = orgstructureBean.getEmployeeLogin(employee);
        return getNodeUserSettings(node, loginName);
    }

    @Override
    public NodeRef createUserSettingsForNode(final NodeRef node) throws WriteTransactionNeededException {
        try {
            lecmTransactionHelper.checkTransaction();
        } catch (TransactionNeededException ex) {
            throw new WriteTransactionNeededException("Can't create user settings for arm node " + node);
        }

        NodeRef employee = orgstructureBean.getCurrentEmployee();
        if (employee == null) {
            logger.error("Could not get current employee. Skip creating settings object");
            return null;
        }

        String loginName = orgstructureBean.getEmployeeLogin(employee);
        // создать и скрыть
        NodeRef nodeRef = createNode(node, TYPE_USER_SETTINGS, loginName, null);
        hideNode(nodeRef, true);
        return nodeRef;
    }

    @Override
	public ArmBaseChildRule getNodeChildRule(NodeRef node) {
		if (isArmNode(node)) {
			List<AssociationRef> queryAssoc = nodeService.getTargetAssocs(node, ASSOC_NODE_CHILD_RULE);
			if (queryAssoc != null && queryAssoc.size() > 0) {
				ArmBaseChildRule result = null;
				NodeRef query = queryAssoc.get(0).getTargetRef();
				QName queryType = nodeService.getType(query);
                Map<QName, Serializable> props = nodeService.getProperties(query);
                if (TYPE_QUERY_CHILD_RULE.equals(queryType)) {
					result = new ArmQueryChildRule();
					((ArmQueryChildRule) result).setListQuery((String) props.get(PROP_LIST_QUERY_CHILD_RULE));
                    ((ArmQueryChildRule) result).setSearchService(searchService);
				} else if (TYPE_DICTIONARY_CHILD_RULE.equals(queryType)) {
					result = new ArmDictionaryChildRule();
					NodeRef dictionary = findNodeByAssociationRef(query, ASSOC_DICTIONARY_CHILD_RULE, null, ASSOCIATION_TYPE.TARGET);
					((ArmDictionaryChildRule) result).setDictionary(dictionary);
                    ((ArmDictionaryChildRule) result).setDictionaryService(dictionaryService);
				} else if (TYPE_STATUSES_CHILD_RULE.equals(queryType)) {
					result = new ArmStatusesChildRule();
					((ArmStatusesChildRule) result).setRule((String) props.get(PROP_STATUSES_RULE));
                    ((ArmStatusesChildRule) result).setStateMachineService(stateMachineService);

					String selectedStatuses = (String) props.get(PROP_SELECTED_STATUSES);
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

	public void aggregateNode(NodeRef nodeRef) {
		if (isArmAccordion(nodeRef) || isArmNode(nodeRef)) {
			Boolean isAggregationNode = (Boolean) nodeService.getProperty(nodeRef, ArmService.PROP_IS_AGGREGATION_NODE);

			if (Boolean.TRUE.equals(isAggregationNode)) {
				Set<QName> typeSet = new HashSet<QName>();
				typeSet.add(ArmService.TYPE_ARM_NODE);
				List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(nodeRef, typeSet);
				if (childAssocs != null) {
					StringBuilder query = new StringBuilder();
					for (ChildAssociationRef assoc : childAssocs) {
						String searchQuery = getNodeSearchQuery(assoc.getChildRef());
						if (searchQuery != null && searchQuery.length() > 0) {
							if (query.length() > 0) {
								query.append(" OR ");
							}
							query.append("(");
							if (searchQuery.startsWith("NOT")) {
								query.append("ISNOTNULL:\"cm:name\" AND ");
							}
							query.append(searchQuery).append(")");
						}
					}
					String oldQuery = (String) nodeService.getProperty(nodeRef, ArmService.PROP_SEARCH_QUERY);
					if (!query.toString().equals(oldQuery)) {
						nodeService.setProperty(nodeRef, ArmService.PROP_SEARCH_QUERY, query.toString());
					}
				}
			}
		}
	}

	public String getNodeSearchQuery(NodeRef nodeRef) {
		List<AssociationRef> queryAssoc = nodeService.getTargetAssocs(nodeRef, ASSOC_NODE_CHILD_RULE);
		if (queryAssoc != null && queryAssoc.size() > 0) {
			NodeRef query = queryAssoc.get(0).getTargetRef();
			QName queryType = nodeService.getType(query);
			Map<QName, Serializable> props = nodeService.getProperties(query);

			if (TYPE_STATUSES_CHILD_RULE.equals(queryType)) {
				ArmStatusesChildRule node = new ArmStatusesChildRule();
				node.setRule((String) props.get(PROP_STATUSES_RULE));
				String selectedStatuses = (String) props.get(PROP_SELECTED_STATUSES);
				if (selectedStatuses != null) {
					List<String> selectedStatusesList = new ArrayList<String>();
					for (String str: selectedStatuses.split(",")) {
						String status = str.trim();
						if (status.length() > 0) {
							selectedStatusesList.add(status);
						}
					}

					node.setSelectedStatuses(selectedStatusesList);
				}

				return node.getQuery();
			}
		}
		return (String) nodeService.getProperty(nodeRef, ArmService.PROP_SEARCH_QUERY);
	}

    @Override
    public void invalidateCache() {
        filtersCache.clear();
        childNodesCache.clear();
        columnsCache.clear();
        nodesTypesCache.clear();
        logger.info("Arm cache cleared!!!");
    }

    @Override
    public void invalidateCurrentUserCache() {
        String user = AuthenticationUtil.getFullyAuthenticatedUser();

        Set<String> removeKeys = new HashSet<>();
        for (String key : columnsCache.getKeys()) {
            if (key.startsWith(user)) { //TODO доработать: сейчас возможна дополнительная очистка "чужого" кэша
                removeKeys.add(key);
            }
        }
        for (String removeKey : removeKeys) {
            columnsCache.remove(removeKey);
        }
        logger.info("Arm cache cleared for '{}'!!!", user);
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

    public void setStateMachineService(StateMachineServiceBean stateMachineService) {
        this.stateMachineService = stateMachineService;
    }
}
