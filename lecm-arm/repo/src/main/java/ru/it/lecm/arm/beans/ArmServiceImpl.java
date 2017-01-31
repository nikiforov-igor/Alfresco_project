package ru.it.lecm.arm.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.cache.SimpleCache;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.ScriptService;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.arm.beans.childRules.*;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.SearchQueryProcessorService;
import ru.it.lecm.base.beans.TransactionNeededException;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.secretary.SecretaryService;
import ru.it.lecm.security.Types;
import ru.it.lecm.statemachine.StateMachineServiceBean;

import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.springframework.context.ApplicationEvent;

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
    private AuthorityService authorityService;
    private SearchQueryProcessorService processorService;
    private SecretaryService secretaryService;
    private ScriptService scriptService;

    private SimpleCache<String, List<ArmColumn>> columnsCache;
    private SimpleCache<NodeRef, List<ArmFilter>> filtersCache;
    private SimpleCache<NodeRef, List<NodeRef>> childNodesCache;
    private SimpleCache<NodeRef, List<String>> nodesTypesCache;
    private SimpleCache<NodeRef, Map<NodeRef, Set<String>>> accordionsCache;
    private SimpleCache<NodeRef, Map<QName, Serializable>> propertiesCache;
    private SimpleCache<NodeRef, NodeRef> parentsCache;
    private SimpleCache<String, NodeRef> armsCache;
    private SimpleCache<NodeRef, QName> typesCache;
    private SimpleCache<NodeRef, ArmBaseChildRule> childRulesCache;

    private Comparator<NodeRef> comparator = new Comparator<NodeRef>() {
        @Override
        public int compare(NodeRef o1, NodeRef o2) {
            Integer order1 = (Integer) getCachedProperties(o1).get(ArmService.PROP_ARM_ORDER);
            Integer order2 = (Integer) getCachedProperties(o2).get(ArmService.PROP_ARM_ORDER);
            int result = 0;
            if (order1 == null && order2 != null) {
                return -1;
            } else if (order1 != null && order2 == null) {
                return 1;
            } else if (order1 != null) {
                result = order1.compareTo(order2);
            }
            if (result == 0 && o1 != null && o2 != null) {
                result = o1.getId().compareTo(o2.getId());  //позволяет иметь ноды с одинаковым порядком
            }
            return result;
        }
    };
    private Comparator<NodeRef> comparatorByName = new Comparator<NodeRef>() {
        @Override
        public int compare(NodeRef o1, NodeRef o2) {
            int result = 0;
            try {
                NodeRef o1Ref = new NodeRef(o1.getStoreRef(), o1.getId().split("_")[1]);
                NodeRef o2Ref = new NodeRef(o2.getStoreRef(), o2.getId().split("_")[1]);
                String order1 = (String) nodeService.getProperty(o1Ref, OrgstructureBean.PROP_EMPLOYEE_SHORT_NAME);
                String order2 = (String) nodeService.getProperty(o2Ref, OrgstructureBean.PROP_EMPLOYEE_SHORT_NAME);
            if (order1 == null && order2 != null) {
                return -1;
            } else if (order1 != null && order2 == null) {
                return 1;
            } else if (order1 != null) {
                result = order1.compareTo(order2);
            }
                if (result == 0) {
                result = o1.getId().compareTo(o2.getId());  //позволяет иметь ноды с одинаковым порядком
                }
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
            return result;
        }
    };
    private StateMachineServiceBean stateMachineService;

    public ScriptService getScriptService() {
        return scriptService;
    }

    public void setScriptService(ScriptService scriptService) {
        this.scriptService = scriptService;
    }

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

    public void setAuthorityService(AuthorityService authorityService) {
        this.authorityService = authorityService;
    }

    public void setAccordionsCache(SimpleCache<NodeRef, Map<NodeRef, Set<String>>> accordionsCache) {
        this.accordionsCache = accordionsCache;
    }

    public void setPropertiesCache(SimpleCache<NodeRef, Map<QName, Serializable>> propertiesCache) {
        this.propertiesCache = propertiesCache;
    }

    public void setParentsCache(SimpleCache<NodeRef, NodeRef> parentsCache) {
        this.parentsCache = parentsCache;
    }

    public void setArmsCache(SimpleCache<String, NodeRef> armsCache) {
        this.armsCache = armsCache;
    }

    public void setTypesCache(SimpleCache<NodeRef, QName> typesCache) {
        this.typesCache = typesCache;
    }

    public void setChildRulesCache(SimpleCache<NodeRef, ArmBaseChildRule> childRulesCache) {
        this.childRulesCache = childRulesCache;
    }

    public void setProcessorService(SearchQueryProcessorService processorService) {
        this.processorService = processorService;
    }

    @Override
	public NodeRef getServiceRootFolder() {
		return getFolder(ARM_ROOT_ID);
	}

	@Override
	public boolean isArmAccordion(NodeRef ref) {
		return isProperType(ref, TYPE_ARM_ACCORDION);
	}

	@Override
    public boolean isRunAsArmAccordion(NodeRef ref) {
        return ref.getId().contains("_") && isProperType(new NodeRef(ref.getStoreRef(), ref.getId().split("_")[0]), TYPE_ARM_ACCORDION);
    }

	@Override
	public boolean isArmNode(NodeRef ref) {
		return isProperType(ref, TYPE_ARM_NODE);
	}

	@Override
	public boolean isArmReportsNode(NodeRef ref) {
		return isProperType(ref, TYPE_ARM_REPORTS_NODE);
	}

	@Override
	public boolean isArmElement(NodeRef ref) {
		return isProperType(ref, TYPE_ARM_ACCORDION, TYPE_ARM_NODE, TYPE_ARM_REPORTS_NODE, TYPE_ARM_HTML_NODE);
	}

	public NodeRef getDictionaryArmSettings() {
		return nodeService.getChildByName(getServiceRootFolder(), ContentModel.ASSOC_CONTAINS, ARM_SETTINGS_DICTIONARY_NAME);
	}

	@Override
	public NodeRef getArmByCode(String code) {
        if (!armsCache.contains(code)) {
            Set<QName> armTypeSet = new HashSet<>(1);
            armTypeSet.add(TYPE_ARM);

            List<ChildAssociationRef> arms = nodeService.getChildAssocs(getDictionaryArmSettings(), armTypeSet);
            if (arms != null) {
                for (ChildAssociationRef armAssoc : arms) {
                    if (getCachedProperties(armAssoc.getChildRef()).get(PROP_ARM_CODE).equals(code)) {
                        armsCache.put(code, armAssoc.getChildRef());
                    }
                }
            }
        }
        return armsCache.get(code);
	}

    @Override
    public List<NodeRef> getArmAccordions(NodeRef arm) {
        List<NodeRef> result = new ArrayList<>();
        if (!accordionsCache.contains(arm)) {
            Set<QName> typeSet = new HashSet<>(1);
            Map<NodeRef, Set<String>> accordions = new TreeMap<>(comparator);
            LinkedHashMap<NodeRef, Set<String>> allAccordions = new LinkedHashMap<>();

            List<ChildAssociationRef> accordionsAssocs;
            typeSet.add(TYPE_ARM_ACCORDION);
            accordionsAssocs = nodeService.getChildAssocs(arm, typeSet);
            if (accordionsAssocs != null) {
                for (ChildAssociationRef accordionAssoc : accordionsAssocs) {
                    NodeRef accordionRef = accordionAssoc.getChildRef();
                    Set<String> roles = new HashSet<>();
                    List<AssociationRef> associationRefs = nodeService.getTargetAssocs(accordionRef, ArmService.ASSOC_ACCORDION_BUSINESS_ROLES);
                    for (AssociationRef associationRef : associationRefs) {
                        NodeRef role = associationRef.getTargetRef();
                        String roleCode = getAutorityByBusinessRole(role);
                        roles.add(roleCode);
                    }
                    accordions.put(accordionRef, roles.isEmpty() ? null : roles);
                }
                allAccordions.putAll(accordions);
            }
            accordionsCache.put(arm, allAccordions);
        }
        Map<NodeRef, Set<String>> armAccordions = accordionsCache.get(arm);
        Set<String> auth = authorityService.getAuthoritiesForUser(AuthenticationUtil.getFullyAuthenticatedUser());
        for (Map.Entry<NodeRef, Set<String>> accEntry : armAccordions.entrySet()) {
            if (accEntry.getValue() == null) {
                result.add(accEntry.getKey());
            } else {
                for (String accRole : accEntry.getValue()) {
                    if (auth.contains(accRole)) {
                        result.add(accEntry.getKey());
                        break;
                    }
                }
            }
        }
        return result;
    }

    @Override
    public List<NodeRef> getArmsForMenu() {
        Set<QName> armTypeSet = new HashSet<>(1);
        armTypeSet.add(TYPE_ARM);

        List<NodeRef> filteredArms = new ArrayList<>();
        List<ChildAssociationRef> arms = nodeService.getChildAssocs(getDictionaryArmSettings(), armTypeSet);
        if (arms != null) {
            Set<String> auth = authorityService.getAuthoritiesForUser(AuthenticationUtil.getFullyAuthenticatedUser());

            for (ChildAssociationRef armAssoc : arms) {
                NodeRef arm = armAssoc.getChildRef();
                Boolean showInMenu = Boolean.TRUE.equals(nodeService.getProperty(arm, PROP_ARM_SHOW_IN_MENU));
                if (showInMenu) {
                    Set<String> roles = new HashSet<>();
                    List<AssociationRef> associationRefs = nodeService.getTargetAssocs(arm, ASSOC_ARM_MENU_BUSINESS_ROLES);
                    for (AssociationRef associationRef : associationRefs) {
                        NodeRef role = associationRef.getTargetRef();
                        String roleCode = getAutorityByBusinessRole(role);
                        roles.add(roleCode);
                    }
                    if (!roles.isEmpty()) {
                        for (String role : roles) {
                            if (auth.contains(role)) {
                                filteredArms.add(arm);
                                break;
                            }
                        }
                    } else {
                        filteredArms.add(arm);
                    }
                }
            }
        }

        Collections.sort(filteredArms, comparator);
        return filteredArms;
    }

    public List<NodeRef> getArmRunAsBossAccordions(NodeRef accordion) {
        List<NodeRef> result = new ArrayList<>();

        Map<NodeRef, Set<String>> accordionsRunAs = new TreeMap<>(comparatorByName);

        String userName = AuthenticationUtil.getFullyAuthenticatedUser();
        NodeRef currentEmployee = orgstructureBean.getEmployeeByPerson(userName);

        if (currentEmployee != null) {
            List<NodeRef> chiefsList = secretaryService.getChiefs(currentEmployee);
            for (NodeRef chief : chiefsList) {
                Set<String> roles = new HashSet<>();
                String roleCode = getAutorityForSecretary(chief);
                roles.add(roleCode);
                accordionsRunAs.put(new NodeRef(accordion.getStoreRef(), accordion.getId() + "_" + chief.getId()), roles);
            }
        }
        //Дополнительная проверка на тот случай, если сотрудник назначен секретарем, но реальных прав нет (не выдались из-за ошибки и т.д)
        Set<String> auth = authorityService.getAuthoritiesForUser(userName);
        for (Map.Entry<NodeRef, Set<String>> accEntry : accordionsRunAs.entrySet()) {
            for (String accRole : accEntry.getValue()) {
                if (auth.contains(accRole)) {
                    result.add(accEntry.getKey());
                    break;
                }
            }
        }
        return result;
    }

    private String getAutorityByBusinessRole(NodeRef businessRole) {
        String roleIdentifier = (String) getCachedProperties(businessRole).get(OrgstructureBean.PROP_BUSINESS_ROLE_IDENTIFIER);
        String roleName = Types.SGKind.SG_BR.getSGPos(roleIdentifier).getAlfrescoSuffix();
        return authorityService.getName(AuthorityType.GROUP, roleName);
    }

    private String getAutorityForSecretary(NodeRef chief) {
        String chiefLogin = orgstructureBean.getEmployeeLogin(chief);
        Types.SGSecretaryOfUser sgSecretary = Types.SGKind.getSGSecretaryOfUser(chief.getId(), chiefLogin);
        String roleName = sgSecretary.getAlfrescoSuffix();
        return authorityService.getName(AuthorityType.GROUP, roleName);
    }

    @Override
    public List<NodeRef> getChildNodes(NodeRef node) {
        if (childNodesCache.contains(node)) {
            return childNodesCache.get(node);
        }
        List<NodeRef> result = new ArrayList<>();

        Set<QName> typeSet = new HashSet<>(1);
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
		List<String> result = new ArrayList<>();
		String types = (String) getCachedProperties(node).get(PROP_NODE_TYPES);
		if (types != null && types.length() > 0) {
			result.addAll(Arrays.asList(types.split(",")));
		}
        nodesTypesCache.put(node, result);
		return result;
	}

	@Override
	public Collection<QName> getNodeTypesIncludeInherit(NodeRef node) {
		Collection<QName> results = new ArrayList<>();
		if (isArmElement(node)) {
			String types = (String) getCachedProperties(node).get(PROP_NODE_TYPES);
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
                    Map<QName, Serializable> props = getCachedProperties(ref);
                    filter.setTitle((String) props.get(ContentModel.PROP_NAME));
                    filter.setCode((String) props.get(PROP_FILTER_CODE));

                    Object multipleValue = props.get(PROP_FILTER_MULTIPLE);
                    if (multipleValue != null) {
                        filter.setMultipleSelect((Boolean) multipleValue);
                    }

                    List<ArmFilterValue> valueList = new ArrayList<>();
                    String valuesStr = (String) props.get(PROP_FILTER_VALUES);
                    if (!valuesStr.isEmpty())  {
                        String[] valuesArray = valuesStr.split(";");
                        if (valuesArray.length < 2) {
                        	valuesArray = valuesStr.split(",");
                        }

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
        Map<QName, Serializable> props = getCachedProperties(node);
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
                //сортируем колонки по возрастанию порядкового номера
                Collections.sort(columns, comparator);

                for (NodeRef ref : columns) {
                    Map<QName, Serializable> columnProps = getCachedProperties(ref);

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
                        Map<QName, Serializable> columnProps = getCachedProperties(ref);
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
		if (isArmElement(node)) {
            if (!childRulesCache.contains(node)) {
                List<AssociationRef> queryAssoc = nodeService.getTargetAssocs(node, ASSOC_NODE_CHILD_RULE);
                ArmBaseChildRule result = null;
                if (queryAssoc != null && queryAssoc.size() > 0) {
                    result = null;
                    NodeRef query = queryAssoc.get(0).getTargetRef();
                    QName queryType = getCachedType(query);
                    Map<QName, Serializable> props = getCachedProperties(query);
                    if (TYPE_QUERY_CHILD_RULE.equals(queryType)) {
                        result = new ArmQueryChildRule();
                        ((ArmQueryChildRule) result).setListQuery((String) props.get(PROP_LIST_QUERY_CHILD_RULE));
                        ((ArmQueryChildRule) result).setSearchService(searchService);
                        ((ArmQueryChildRule) result).setProcessorService(processorService);
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
                            List<String> selectedStatusesList = new ArrayList<>();
                            for (String str : selectedStatuses.split(",")) {
                                String status = str.trim();
                                if (status.length() > 0) {
                                    selectedStatusesList.add(status);
                                }
                            }

                            ((ArmStatusesChildRule) result).setSelectedStatuses(selectedStatusesList);
                        }
                    } else if (TYPE_XPATH_CHILD_RULE.equals(queryType)) {
                        result = new ArmXPathChildRule();
                        ((ArmXPathChildRule) result).setRootXPath((String) props.get(PROP_ROOT_XPATH));
                        String types = (String) props.get(PROP_XPATH_TYPES);
                        if (types != null && types.length() > 0) {
                            ((ArmXPathChildRule) result).setTypes(Arrays.asList(types.split(",")));
                        }
						String filter = (String) props.get(PROP_XPATH_FILTER);
						if(filter != null && !filter.isEmpty()) {
							((ArmXPathChildRule) result).setFilter(filter);
						}
                        ((ArmXPathChildRule) result).setSearchService(searchService);
                        ((ArmXPathChildRule) result).setNodeService(nodeService);
						((ArmXPathChildRule) result).setProcessorService(processorService);
                    } else if (TYPE_SCRIPT_CHILD_RULE.equals(queryType)) {
                        result = new ArmScriptChildRule();
                        ((ArmScriptChildRule) result).setScript((String) props.get(PROP_ROOT_SCRIPT));
                        ((ArmScriptChildRule) result).setScriptService(scriptService);
                        ((ArmScriptChildRule) result).setOrgstructureService(orgstructureBean);
                    }
                }
                childRulesCache.put(node, result == null ? ArmBaseChildRule.NULL_RULE : result);
            }
            ArmBaseChildRule rule = childRulesCache.get(node);
            return ArmBaseChildRule.NULL_RULE == rule ? null : rule;
        }
        return null;
	}

	public void aggregateNode(NodeRef nodeRef) {
		if (isArmAccordion(nodeRef) || isArmNode(nodeRef)) {
			Boolean isAggregationNode = (Boolean) getCachedProperties(nodeRef).get(ArmService.PROP_IS_AGGREGATION_NODE);

			if (Boolean.TRUE.equals(isAggregationNode)) {
				Set<QName> typeSet = new HashSet<>();
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
                            boolean useBrackets = true;
                            if (searchQuery.startsWith("NOT")) {
                                Matcher m = MULTIPLE_NOT_QUERY.matcher(searchQuery.toUpperCase());
                                if (!m.find()) { //
                                    useBrackets = false;
                                }
                            }
                            query.append(useBrackets ? "(" : "");
                            query.append(searchQuery);
                            query.append(useBrackets ? ")" : "");
						}
					}
					String oldQuery = (String) getCachedProperties(nodeRef).get(ArmService.PROP_SEARCH_QUERY);
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
			Map<QName, Serializable> props = getCachedProperties(query);

			if (TYPE_STATUSES_CHILD_RULE.equals(queryType)) {
				ArmStatusesChildRule node = new ArmStatusesChildRule();
				node.setRule((String) props.get(PROP_STATUSES_RULE));
				String selectedStatuses = (String) props.get(PROP_SELECTED_STATUSES);
				if (selectedStatuses != null) {
					List<String> selectedStatusesList = new ArrayList<>();
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
		return (String) getCachedProperties(nodeRef).get(ArmService.PROP_SEARCH_QUERY);
	}

    @Override
    public void invalidateCache() {
        filtersCache.clear();
        childNodesCache.clear();
        columnsCache.clear();
        nodesTypesCache.clear();
        accordionsCache.clear();
        propertiesCache.clear();
        parentsCache.clear();
        armsCache.clear();
        typesCache.clear();
        childRulesCache.clear();
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

    public Map<QName, Serializable> getCachedProperties(NodeRef nodeRef) {
        if (!propertiesCache.contains(nodeRef)) {
            propertiesCache.put(nodeRef, nodeService.getProperties(nodeRef));
        }
        return propertiesCache.get(nodeRef);
    }

    /*package*/ NodeRef getCachedParent(NodeRef nodeRef) {
        if(!parentsCache.contains(nodeRef)) {
            parentsCache.put(nodeRef, nodeService.getPrimaryParent(nodeRef).getParentRef());
        }
        return parentsCache.get(nodeRef);
    }

    /*package*/ QName getCachedType(NodeRef ref) {
        if (!typesCache.contains(ref)) {
            typesCache.put(ref, nodeService.getType(ref));
        }
        return typesCache.get(ref);
    }

    /**
     * Проверка типа ноды. Использует кэш. Наследование не учитывается.
     * @param ref нода
     * @param types проверяемые типы
     * @return true, если тип совпал с одном из переданных
     */
    @Override
    public boolean isProperType(NodeRef ref, QName... types) {
        if (ref == null || types == null || types.length == 0) {
            return false;
        }
        QName type = getCachedType(ref);
        for (QName name : types) {
            if (name.equals(type)) {
                return true;
            }
        }
        return false;
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
    public void setSecretaryService(SecretaryService secretaryService) {
        this.secretaryService = secretaryService;
	}

}