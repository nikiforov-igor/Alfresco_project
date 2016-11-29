package ru.it.lecm.orgstructure.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.cache.SimpleCache;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.NoSuchPersonException;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.delegation.IDelegation;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.orgstructure.policies.PolicyUtils;

import java.io.Serializable;
import java.util.*;

/**
 * @author dbashmakov Date: 27.11.12 Time: 17:08
 */
public class OrgstructureBeanImpl extends BaseBean implements OrgstructureBean {

	final static protected Logger logger = LoggerFactory.getLogger(OrgstructureBeanImpl.class);
	private PersonService personService;
	private DictionaryBean dictionaryService;
	private NodeRef organizationRootRef;
	private Repository repositoryHelper;
	private AuthorityService authorityService;
    private SimpleCache<String, NodeRef> userOrganizationsCache;
	private NamespaceService namespaceService;
	private OrgstructureSGNotifierBean orgSGNotifier;

	public void setOrgSGNotifier(OrgstructureSGNotifierBean orgSGNotifier) {
		this.orgSGNotifier = orgSGNotifier;
	}

    public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	public void setDictionaryService(DictionaryBean dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	public void setRepositoryHelper(Repository repositoryHelper) {
		this.repositoryHelper = repositoryHelper;
	}

    public void setUserOrganizationsCache(SimpleCache<String, NodeRef> userOrganizationsCache) {
        this.userOrganizationsCache = userOrganizationsCache;
    }

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	public void setAuthorityService(AuthorityService authorityService) {
        this.authorityService = authorityService;
    }
    @Override
    public SimpleCache<String, NodeRef> getUserOrganizationsCache() {
        return userOrganizationsCache;
    }

    @Override
    public boolean hasAccessToOrgElement(String userName, NodeRef orgElement) {
        return hasAccessToOrgElement(userName, orgElement, false); //разрешаем доступк к элементам без организации по умолчанию
    }

    private boolean hasAccessToOrgElement(String userName, NodeRef orgElement, boolean doNotAccessWithEmpty) {
        if (userName != null) {
            if (userName.equalsIgnoreCase("System") || userName.equalsIgnoreCase("workflow")) {
                return true;
            }
            NodeRef currentEmployee = getEmployeeByPerson(userName, false);
            if (currentEmployee != null) {
                // доступ к самому себе или при наличии бизнес-роли - доступ разрешен
            	Set<String> auth = authorityService.getAuthoritiesForUser(userName);
                if (currentEmployee.equals(orgElement) || auth.contains("GROUP_LECM_GLOBAL_ORGANIZATIONS_ACCESS")) {
//                        isEmployeeHasBusinessRole(getEmployeeByPerson(userName), "BR_GLOBAL_ORGANIZATIONS_ACCESS", false, false, false)) {
                    return true;
                }

                NodeRef employeeOrganization = getUserOrganization(userName, currentEmployee);
                NodeRef orgElementOrganization = getOrganization(orgElement);

                if (employeeOrganization == null) {
                    return (orgElementOrganization == null) && !doNotAccessWithEmpty;
                }
                // тут сотрудник с организацией
                if (orgElementOrganization == null) { // доступ к элементам, у которых нет организации разрешен в зависимости от флага
                    return !doNotAccessWithEmpty;
                }

                // Если у сотрудника нет организации - доступ есть только к тем структурам, у которых тоже нет организации
                // Если у сотрудника есть организация - доступ есть к тем структурам, у которых нет орагниазции + у которых организация == организации сотрудника
                return employeeOrganization.equals(orgElementOrganization);
            }
        }
        return false;
    }

    @Override
    public boolean hasAccessToOrgElement(NodeRef orgElement) {
        return hasAccessToOrgElement(authService.getCurrentUserName(), orgElement);
    }
    @Override
    public boolean hasAccessToOrgElement(NodeRef orgElement, boolean doNotAccessWithEmpty) {
        return hasAccessToOrgElement(authService.getCurrentUserName(), orgElement, doNotAccessWithEmpty);
    }

    @Override
    public List<NodeRef> getOrganizationEmployees(NodeRef organizationRef) {
        Set<NodeRef> employees = new HashSet<>();
        for (String userName : userOrganizationsCache.getKeys()) {
            if (userOrganizationsCache.get(userName) != null && userOrganizationsCache.get(userName).equals(organizationRef)){
                NodeRef employee = getEmployeeByPerson(userName);
                if (employee != null) {
                    employees.add(getEmployeeByPerson(userName));
                }
            }
        }
        return new ArrayList<>(employees);
    }
	
	@Override
	protected void onBootstrap(ApplicationEvent event)
	{
		AuthenticationUtil.RunAsWork<NodeRef> raw = new AuthenticationUtil.RunAsWork<NodeRef>() {
			@Override
			public NodeRef doWork() throws Exception {
				return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
					@Override
					public NodeRef execute() throws Throwable {
						final String rootName = ORGANIZATION_ROOT_NAME;
						final NodeRef rootDir = getServiceRootFolder();
						
						/**
						 * Структура директорий Организация ---Структура
						 * ---Сотрудники ---Персональные данные
						 */
						NodeRef organizationRef = nodeService.getChildByName(rootDir, ContentModel.ASSOC_CONTAINS, rootName);

						final QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
						if (organizationRef == null) { // create ROOT
							final QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, rootName);
							final ChildAssociationRef associationRef = nodeService.createNode(rootDir, assocTypeQName, assocQName, TYPE_ORGANIZATION, getNamedProps(rootName));
							organizationRef = associationRef.getChildRef();
							logger.info(String.format("OU Root '%s' created as %s", rootName, organizationRef));
						}
						
						organizationRootRef = organizationRef;

						// Структура
						NodeRef structureRef = nodeService.getChildByName(organizationRef, ContentModel.ASSOC_CONTAINS, STRUCTURE_ROOT_NAME);
						if (structureRef == null) {
							final QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, STRUCTURE_ROOT_NAME);
							final QName nodeTypeQName = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, TYPE_DIRECTORY_STRUCTURE);
							final ChildAssociationRef ref = nodeService.createNode(organizationRef, assocTypeQName, assocQName, nodeTypeQName, getNamedProps(STRUCTURE_ROOT_NAME));
							structureRef = ref.getChildRef();
							logger.info(String.format("OU Structure '%s' created as %s", STRUCTURE_ROOT_NAME, structureRef));
						}
						//Холдинг
						if (nodeService.getChildByName(structureRef, ContentModel.ASSOC_CONTAINS, HOLDING_ROOT_NAME) == null) {
							final QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, HOLDING_ROOT_NAME);
							final QName nodeTypeQName = TYPE_ORGANIZATION_UNIT;
							Map<QName, Serializable> props = getNamedProps(HOLDING_ROOT_NAME);
							props.put(PROP_ORG_ELEMENT_FULL_NAME, HOLDING_ROOT_NAME);
							props.put(PROP_ORG_ELEMENT_SHORT_NAME, HOLDING_ROOT_NAME);
							props.put(PROP_UNIT_CODE, HOLDING_ROOT_NAME);
							props.put(PROP_UNIT_TYPE, "SEGREGATED");
							final ChildAssociationRef ref = nodeService.createNode(structureRef, assocTypeQName, assocQName, nodeTypeQName, props);
							logger.info(String.format("OU Holding '%s' created as %s", HOLDING_ROOT_NAME, ref.getChildRef()));
						}

						// Сотрудники
						if (nodeService.getChildByName(organizationRef, ContentModel.ASSOC_CONTAINS, EMPLOYEES_ROOT_NAME) == null) {
							final QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, EMPLOYEES_ROOT_NAME);
							final QName nodeTypeQName = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, TYPE_DIRECTORY_EMPLOYEES);
							final ChildAssociationRef ref = nodeService.createNode(organizationRef, assocTypeQName, assocQName, nodeTypeQName, getNamedProps(EMPLOYEES_ROOT_NAME));
							logger.info(String.format("OU Employees '%s' created as %s", EMPLOYEES_ROOT_NAME, ref.getChildRef()));
						}

						// Персональные данные
						if (nodeService.getChildByName(organizationRef, ContentModel.ASSOC_CONTAINS, PERSONAL_DATA_ROOT_NAME) == null) {
							final QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, PERSONAL_DATA_ROOT_NAME);
							final QName nodeTypeQName = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, TYPE_DIRECTORY_PERSONAL_DATA);
							final ChildAssociationRef ref = nodeService.createNode(organizationRef, assocTypeQName, assocQName, nodeTypeQName, getNamedProps(PERSONAL_DATA_ROOT_NAME));
							logger.info(String.format("OU Personal Data '%s' created as %s", PERSONAL_DATA_ROOT_NAME, ref.getChildRef()));
						}

						//Основная папка с документами
						NodeRef companyHome = repositoryHelper.getCompanyHome();
						if (nodeService.getChildByName(companyHome, ContentModel.ASSOC_CONTAINS, DOCUMENT_ROOT_NAME) == null) {
							final QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, DOCUMENT_ROOT_NAME);
							final ChildAssociationRef ref = nodeService.createNode(companyHome, assocTypeQName, assocQName, ContentModel.TYPE_FOLDER, getNamedProps(DOCUMENT_ROOT_NAME));
							serviceRegistry.getPermissionService().setInheritParentPermissions(ref.getChildRef(), false);
							serviceRegistry.getPermissionService().setPermission(ref.getChildRef(), PermissionService.ALL_AUTHORITIES, PermissionService.CONSUMER, true);
							logger.info(String.format("OU Document Data '%s' created as %s", DOCUMENT_ROOT_NAME, ref.getChildRef()));
						}

						return organizationRef;
					}

					private Map<QName, Serializable> getNamedProps(String name) {
						final Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1); //optional map of properties to keyed by their qualified names
						properties.put(ContentModel.PROP_NAME, name);
						return properties;
					}
				},false,true);
			}
		};
		organizationRootRef = AuthenticationUtil.runAsSystem(raw);
	}

	@Override
	public NodeRef getOrganization() {
		return organizationRootRef;
	}

	@Override
	public NodeRef getOrganizationBoss() {
		NodeRef bossRef = null;
		NodeRef organization = getOrganization();
		if (organization != null) {
			NodeRef structure = getStructureDirectory();
			List<ChildAssociationRef> units = nodeService.getChildAssocs(structure);
			for (ChildAssociationRef unit : units) {
				if (!isArchive(unit.getChildRef())) {
					bossRef = getUnitBoss(unit.getChildRef());
					if (bossRef != null) {
						break;
					}
				}
			}
		}
		return bossRef;
	}

	@Override
	public NodeRef getOrganizationLogo() {
		NodeRef logoRef = null;
		NodeRef organization = getOrganization();
		if (organization != null) {
			List<AssociationRef> logo = nodeService.getTargetAssocs(organization, ASSOC_ORG_LOGO);
			if (logo != null && logo.size() > 0) {
				logoRef = logo.get(0).getTargetRef();
			}
		}
		return logoRef;
	}

	@Override
	public NodeRef getStructureDirectory() {
		NodeRef structure = null;
		NodeRef organization = getOrganization();
		if (organization != null) {
			structure = nodeService.getChildByName(organization, ContentModel.ASSOC_CONTAINS, STRUCTURE_ROOT_NAME);
		}
		return structure;
	}

	@Override
	public NodeRef getHolding() {
		NodeRef structureRef = getStructureDirectory();
		return (structureRef != null) ? nodeService.getChildByName(structureRef, ContentModel.ASSOC_CONTAINS, HOLDING_ROOT_NAME) : null;
	}

	@Override
	public NodeRef getEmployeesDirectory() {
		NodeRef emp = null;
		NodeRef organization = getOrganization();
		if (organization != null) {
			emp = nodeService.getChildByName(organization, ContentModel.ASSOC_CONTAINS, EMPLOYEES_ROOT_NAME);
		}
		return emp;
	}

	@Override
	public NodeRef getPersonalDataDirectory() {
		NodeRef pd = null;
		NodeRef organization = getOrganization();
		if (organization != null) {
			pd = nodeService.getChildByName(organization, ContentModel.ASSOC_CONTAINS, PERSONAL_DATA_ROOT_NAME);
		}
		return pd;
	}

	@Override
	public List<NodeRef> getWorkGroups(boolean onlyActive) {
		List<NodeRef> results = new ArrayList<NodeRef>();
		NodeRef structureDirectory = getStructureDirectory();
		if (structureDirectory != null) {
			Set<QName> workgroups = new HashSet<QName>();
			workgroups.add(TYPE_WORK_GROUP);
			List<ChildAssociationRef> wgs = nodeService.getChildAssocs(structureDirectory, workgroups);
			for (ChildAssociationRef wg : wgs) {
				if (onlyActive) {
					if (!isArchive(wg.getChildRef())) {
						results.add(wg.getChildRef());
					}
				} else {
					results.add(wg.getChildRef());
				}
			}
		}
		return results;
	}

	@Override
	public List<NodeRef> getSubUnits(NodeRef parent, boolean onlyActive) {
		return getSubUnits(parent, onlyActive, false);
	}

	@Override
	public List<NodeRef> getSubUnits(NodeRef parent, boolean onlyActive, boolean includeSubunits) {
		return getSubUnits(parent, onlyActive, includeSubunits, true);
	}

    @Override
    public List<NodeRef> getSubUnits(NodeRef parent, boolean onlyActive, boolean includeSubunits, boolean checkAccess) {
        List<NodeRef> results = new ArrayList<NodeRef>();
        Set<QName> units = new HashSet<QName>();
        units.add(TYPE_ORGANIZATION_UNIT);

        List<ChildAssociationRef> uRefs = nodeService.getChildAssocs(parent, units);
        for (ChildAssociationRef uRef : uRefs) {
            if (!onlyActive || !isArchive(uRef.getChildRef())) {
                if (!checkAccess || hasAccessToOrgElement(uRef.getChildRef())){
                    results.add(uRef.getChildRef());
                    if (includeSubunits) {
                        results.addAll(getSubUnits(uRef.getChildRef(), onlyActive, true));
                    }
                }
            }
        }
        return results;
    }

	@Override
	public boolean hasChild(NodeRef parent, boolean onlyActive) {
		return !getSubUnits(parent, onlyActive).isEmpty();
	}

	@Override
	public NodeRef getParentUnit(NodeRef unitRef) {
        return getParentUnit(unitRef, true);
	}

	@Override
    public NodeRef getParentUnit(NodeRef unitRef, boolean checkAccess) {
        if (!checkAccess || hasAccessToOrgElement(unitRef)) {
            ChildAssociationRef parentRef = nodeService.getPrimaryParent(unitRef);
            if (parentRef != null) {
                NodeRef parent = parentRef.getParentRef();
                if (isUnit(parent)) {
                    return parent;
                }
            }
        }
        return null;
    }

	@Override
	public boolean isUnit(NodeRef ref) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_ORGANIZATION_UNIT);
		return isProperType(ref, types);
	}

	@Override
	public boolean isWorkGroup(NodeRef ref) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_WORK_GROUP);
		return isProperType(ref, types);
	}

	@Override
	public boolean isWorkRole(NodeRef ref) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_WORK_ROLE);
		return isProperType(ref, types);
	}

	@Override
	public boolean isBusinessRole(NodeRef ref) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_BUSINESS_ROLE);
		return isProperType(ref, types);
	}

	@Override
	public boolean isEmployee(NodeRef ref) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_EMPLOYEE);
		return isProperType(ref, types);
	}

	@Override
	public boolean isOrganizationElement(NodeRef ref) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_ORGANIZATION_ELEMENT);
		return isProperType(ref, types);
	}

	@Override
	public boolean isOrganizationElementMember(NodeRef ref) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_ORGANIZATION_ELEMENT_MEMBER);
		return isProperType(ref, types);
	}

	@Override
	public boolean isStaffList(NodeRef ref) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_STAFF_LIST);
		return isProperType(ref, types);
	}

	@Override
	public boolean isWorkForce(NodeRef ref) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_WORKFORCE);
		return isProperType(ref, types);
	}

	@Override
	public boolean isPosition(NodeRef ref) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_STAFF_POSITION);
		return isProperType(ref, types);
	}

	@Override
	public boolean isCurrentBoss() {
		NodeRef employeeRef = getCurrentEmployee();
		return isBoss(employeeRef, true);
	}

	@Override
	public NodeRef getUnitBoss(NodeRef unitRef) {
		NodeRef bossLink = null;
		if (isUnit(unitRef)) { // ищем руководителя Подразделения
			NodeRef bossStaff = getBossStaff(unitRef);
			if (bossStaff != null) {
				//вытаскиваем ссылку на сотрудника и непосредственно сотрудника (если ссылка имеется)
				bossLink = getEmployeeByPosition(bossStaff);
			}
			if (bossLink == null) {
				// если не нашли руководителя в текущем подразделении, пробуем найти в вышестоящем
				NodeRef parent = getParentUnit(unitRef);
				if (parent != null) {
					bossLink = getUnitBoss(parent);
				}
			}
		}
		return bossLink;
	}

	@Override
    public NodeRef getBossStaff(NodeRef unitRef) {
        NodeRef bossStaff = null;
        // Получаем список штатных расписаний
        List<NodeRef> staffs = getUnitStaffLists(unitRef);
        // находим то, которое помечено как руководящая позиция

        for (NodeRef staff : staffs) {
            if ((Boolean) nodeService.getProperty(staff, PROP_STAFF_LIST_IS_BOSS)) {
                bossStaff = staff;
                break;
            }
        }
        return bossStaff;
    }

	@Override
	public NodeRef findEmployeeBoss(NodeRef employeeRef) {
		NodeRef bossRef = null;
		if (nodeService.exists(employeeRef)) {
			if (isEmployee(employeeRef)) {
				// получаем основную должностную позицию
				NodeRef primaryStaff = getEmployeePrimaryStaff(employeeRef);
				if (primaryStaff != null) {
					// получаем подразделение для штатного расписания
					NodeRef unit = getUnitByStaff(primaryStaff);
					// получаем руководителя для подразделения
					bossRef = getUnitBoss(unit);
					//сотрудник не может быть руководителем у себя (кроме случая, если он руководитель организации)
					while (bossRef != null && bossRef.equals(employeeRef) && ((unit = getParentUnit(unit)) != null)) {
						bossRef = getUnitBoss(unit);
					}
				}
			}
			//если не нашли - возвращаем руководителя организации
			if (bossRef == null || bossRef.equals(employeeRef)) {
				bossRef = getOrganizationBoss();
			}
		}
		return bossRef;
	}

	@Override
	public List<NodeRef> getUnitStaffLists(NodeRef unitRef) {
		return getUnitStaffLists(unitRef, true);
	}

    @Override
    public List<NodeRef> getUnitStaffLists(NodeRef unitRef, boolean checkAccess) {
        List<NodeRef> results = new ArrayList<NodeRef>();
        if (isUnit(unitRef) && (!checkAccess || hasAccessToOrgElement(unitRef))) {
            Set<QName> staffs = new HashSet<QName>();
            staffs.add(TYPE_STAFF_LIST);

            List<ChildAssociationRef> sls = nodeService.getChildAssocs(unitRef, staffs);
            for (ChildAssociationRef sl : sls) {
                if (!isArchive(sl.getChildRef())) {
                    results.add(sl.getChildRef());
                }
            }
        }
        return results;
    }
	@Override
	public List<NodeRef> getOrgRoleEmployees(NodeRef nodeRef) {
		if (!isWorkRole(nodeRef)) { // если не роль для рабочей группы
			return new ArrayList<NodeRef>();
		}

		List<NodeRef> results = new ArrayList<NodeRef>();
		List<AssociationRef> workForceAssocs = nodeService.getSourceAssocs(nodeRef, ASSOC_ELEMENT_MEMBER_POSITION);
		for (AssociationRef workForceAssoc : workForceAssocs) {
			NodeRef workForce = workForceAssoc.getSourceRef();
			if (isArchive(workForce)) {
				continue;
			}

			//получает ссылку на сотрудника
			List<AssociationRef> empLinks = nodeService.getTargetAssocs(workForce, ASSOC_ELEMENT_MEMBER_EMPLOYEE);
			if (empLinks.size() > 0) { // сотрудник задан -> по ссылке получаем сотрудника
				NodeRef employee = getEmployeeByLink(empLinks.get(0).getTargetRef());
                if (hasAccessToOrgElement(employee)) {
                    results.add(employee);
                }
			}
		}

		return results;
	}

	@Override
	public NodeRef getEmployeeByPosition(NodeRef positionRef) {
        return getEmployeeByPosition(positionRef, true);
    }

    @Override
    public NodeRef getEmployeeByPosition(NodeRef positionRef, boolean checkAccess) {
        NodeRef employeeLink = getEmployeeLinkByPosition(positionRef);
        if (employeeLink != null && !isArchive(employeeLink)) {
            NodeRef employee = getEmployeeByLink(employeeLink);
            return (!checkAccess || hasAccessToOrgElement(employee)) ? employee : null;
        }
        return null;
    }

	@Override
    public List<NodeRef> getEmployeesByPosition(NodeRef unit, NodeRef position) {
        Set<NodeRef> result = new HashSet<NodeRef>();
        List<NodeRef> staffLists = getUnitStaffLists(unit);
        for (NodeRef staffList : staffLists) {
            if (isArchive(staffList)) {
                continue;
            }
            List<AssociationRef> posAssoc = nodeService.getTargetAssocs(staffList, ASSOC_ELEMENT_MEMBER_POSITION);
            if (posAssoc.get(0).getTargetRef().equals(position)) { //ссылка на должность - всегда одна и обязательна
                NodeRef employee = getEmployeeByPosition(staffList);
                if (employee != null && hasAccessToOrgElement(employee)) {
                    result.add(employee);
                }
            }
        }
        return new ArrayList<NodeRef>(result);
    }

	@Override
	public List<NodeRef> getEmployeesByPosition(NodeRef position) {
		List<NodeRef> result = new ArrayList<NodeRef>();

		List<AssociationRef> staffListAssocList = nodeService.getSourceAssocs(position, ASSOC_ELEMENT_MEMBER_POSITION);
		for (AssociationRef staffListAssoc : staffListAssocList) {
			NodeRef staffList = staffListAssoc.getSourceRef();
			NodeRef employee = getEmployeeByPosition(staffList);
			if (employee != null && hasAccessToOrgElement(employee)) {
				result.add(employee);
			}
		}

		return result;
	}

	@Override
	public NodeRef getEmployeeByLink(NodeRef linkRef) {
		Set<QName> properTypes = new HashSet<QName>();
		properTypes.add(TYPE_EMPLOYEE_LINK);

		if (isProperType(linkRef, properTypes)) {
			List<AssociationRef> links = nodeService.getTargetAssocs(linkRef, ASSOC_EMPLOYEE_LINK_EMPLOYEE);
			// сотрудник всегда существует и только один
			return links.get(0).getTargetRef();
		}
		return null;
	}

	@Override
	public List<NodeRef> getStaffPositions(boolean onlyActive) {
		List<NodeRef> results = new ArrayList<NodeRef>();
		NodeRef positionsRoot = dictionaryService.getDictionaryByName(POSITIONS_DICTIONARY_NAME);

		Set<QName> positions = new HashSet<QName>();
		positions.add(TYPE_STAFF_POSITION);

		List<ChildAssociationRef> staffPositions = nodeService.getChildAssocs(positionsRoot, positions);
		for (ChildAssociationRef staffPosition : staffPositions) {
			if (!onlyActive) {
				results.add(staffPosition.getChildRef());
			} else {
				if ((Boolean) nodeService.getProperty(staffPosition.getChildRef(), IS_ACTIVE)) {
					results.add(staffPosition.getChildRef());
				}
			}
		}
		return results;
	}

	@Override
	public List<NodeRef> getPositionEmployees(NodeRef position) {
		List<NodeRef> results = new ArrayList<NodeRef>();
		Set<QName> properTypes = new HashSet<QName>();
		properTypes.add(TYPE_STAFF_POSITION);

		if (isProperType(position, properTypes)) { // если должностная позиция
			// получаем список объектов Штатное расписание для заданной позиции
			List<AssociationRef> staffs = nodeService.getSourceAssocs(position, ASSOC_ELEMENT_MEMBER_POSITION);
			for (AssociationRef staff : staffs) {
				if (!isArchive(staff.getSourceRef())) {
					Set<QName> links = new HashSet<QName>();
					links.add(TYPE_EMPLOYEE_LINK);
					// из штатного расписания получает ссылку на сотрудника
					List<ChildAssociationRef> empLinks = nodeService.getChildAssocs(staff.getSourceRef(), links);
					if (empLinks.size() > 0) { // сотрудник задан -> по ссылке получаем сотрудника
						NodeRef employee = getEmployeeByLink(empLinks.get(0).getChildRef());
                        if (hasAccessToOrgElement(employee)) {
                            results.add(employee);
                        }
					}
				}
			}
		}
		return results;
	}

	@Override
	public List<NodeRef> getWorkGroupEmployees(NodeRef workGroup) {
		List<NodeRef> results = new ArrayList<NodeRef>();
		if (isWorkGroup(workGroup)) { // если рабочая группа
			// получаем участников для рабочей группы
			Set<QName> workforces = new HashSet<QName>();
			workforces.add(TYPE_WORKFORCE);
			List<ChildAssociationRef> workForces = nodeService.getChildAssocs(workGroup, workforces);
			for (ChildAssociationRef wf : workForces) {
				if (!isArchive(wf.getChildRef())) {
					Set<QName> links = new HashSet<QName>();
					links.add(TYPE_EMPLOYEE_LINK);
					//получает ссылку на сотрудника
					List<ChildAssociationRef> empLinks = nodeService.getChildAssocs(wf.getChildRef(), links);
					if (empLinks.size() > 0) { // сотрудник задан -> по ссылке получаем сотрудника
						NodeRef employee = getEmployeeByLink(empLinks.get(0).getChildRef());
                        if (hasAccessToOrgElement(employee)) {
                            results.add(employee);
                        }
					}
				}
			}
		}
		return results;
	}

	@Override
	public List<NodeRef> getOrganizationElementEmployees(NodeRef organizationElement) {
		return getOrganizationElementEmployees(organizationElement, true);
	}

    private List<NodeRef> getOrganizationElementEmployees(NodeRef organizationElement, boolean checkAccess) {
        Set<NodeRef> results = new HashSet<NodeRef>();
        if (isWorkGroup(organizationElement) || isUnit(organizationElement)) { // если рабочая группа
            // получаем участников для рабочей группы
            Set<QName> workforces = new HashSet<QName>();
            workforces.add(TYPE_WORKFORCE);
            workforces.add(TYPE_STAFF_LIST);
            List<ChildAssociationRef> orgElementMembers = nodeService.getChildAssocs(organizationElement, workforces);
            for (ChildAssociationRef wf : orgElementMembers) {
                if (!isArchive(wf.getChildRef())) {
                    Set<QName> links = new HashSet<QName>();
                    links.add(TYPE_EMPLOYEE_LINK);
                    //получает ссылку на сотрудника
                    List<ChildAssociationRef> empLinks = nodeService.getChildAssocs(wf.getChildRef(), links);
                    if (empLinks.size() > 0) { // сотрудник задан -> по ссылке получаем сотрудника
                        NodeRef employee = getEmployeeByLink(empLinks.get(0).getChildRef());
                        if (!isArchive(employee) && (!checkAccess || hasAccessToOrgElement(employee))) {
                            results.add(employee);
                        }
                    }
                }
            }
        }
        return new ArrayList<NodeRef>(results);
    }

	@Override
	public NodeRef getEmployeePrimaryStaff(NodeRef employeeRef) {
		NodeRef primaryStaff = null;
		if (isEmployee(employeeRef) && hasAccessToOrgElement(employeeRef)) {
			List<AssociationRef> links = nodeService.getSourceAssocs(employeeRef, ASSOC_EMPLOYEE_LINK_EMPLOYEE);
			for (AssociationRef link : links) {
				if (!isArchive(link.getSourceRef()) && (Boolean) nodeService.getProperty(link.getSourceRef(), PROP_EMP_LINK_IS_PRIMARY)) {
					primaryStaff = getPositionByEmployeeLink(link.getSourceRef());
					if (primaryStaff != null && isStaffList(primaryStaff)) {
						break;
					}
				}
			}
		}
		return primaryStaff;
	}

	@Override
	public NodeRef getPositionByEmployeeLink(NodeRef empLink) {
		NodeRef staff = null;

		Set<QName> properTypes = new HashSet<QName>();
		properTypes.add(TYPE_EMPLOYEE_LINK);
		if (isProperType(empLink, properTypes)) {
			List<AssociationRef> staffs = nodeService.getSourceAssocs(empLink, ASSOC_ELEMENT_MEMBER_EMPLOYEE);
			if (staffs.size() > 0) {
				NodeRef ref = staffs.get(0).getSourceRef();
				if (!isArchive(ref)) {
					staff = ref;
				}
			}
		}
		return staff;
	}

	@Override
	public NodeRef getUnitByStaff(NodeRef staffRef) {
		return getUnitByStaff(staffRef, true);
	}

    private NodeRef getUnitByStaff(NodeRef staffRef, boolean checkAccess) {
        NodeRef unitRef = null;
        if (isStaffList(staffRef)) {
            unitRef = nodeService.getPrimaryParent(staffRef).getParentRef();
        }
        return (!checkAccess || hasAccessToOrgElement(unitRef)) ? unitRef : null;
    }

	@Override
	public NodeRef getUnitByCode(String code) {
		if (code == null) {
			return null;
		}
		List<NodeRef> childAssocs = getSubUnits(getStructureDirectory(), true, true);
		for (NodeRef unitRef : childAssocs) {
			String unitCode = (String) nodeService.getProperty(unitRef, PROP_UNIT_CODE);
			if (code.equals(unitCode)) {
				return unitRef;
			}
		}

		return null;
	}

	@Override
	public NodeRef getUnitBoss(String unitCode) {
		NodeRef unitRef = getUnitByCode(unitCode);
		return getUnitBoss(unitRef);
	}

	@Override
	public NodeRef getRootUnit() {
		List<NodeRef> units = getSubUnits(getStructureDirectory(), true, false, false);
		if (units.size() > 0) {
			return units.get(0);
		}
		return null;
	}

	@Override
	public NodeRef getEmployeePhoto(NodeRef employeeRef) {
		NodeRef photoRef = null;
		if (isEmployee(employeeRef) && hasAccessToOrgElement(employeeRef)) {
			List<AssociationRef> photos = nodeService.getTargetAssocs(employeeRef, ASSOC_EMPLOYEE_PHOTO);
			if (photos.size() > 0) {
				photoRef = photos.get(0).getTargetRef();
			}
		}
		return photoRef;
	}

	@Override
	public NodeRef getEmployeePersonalData(NodeRef employeeRef) {
		NodeRef personRef = null;
		if (isEmployee(employeeRef)) {
			List<AssociationRef> personData = nodeService.getTargetAssocs(employeeRef, ASSOC_EMPLOYEE_PERSON_DATA);
			if (personData.size() > 0) {
				personRef = personData.get(0).getTargetRef();
			}
		}
		return personRef;
	}

	@Override
	public List<NodeRef> getEmployeeStaffs(NodeRef employeeRef) {
		return getEmployeeStaffs(employeeRef, true);
	}

    private List<NodeRef> getEmployeeStaffs(NodeRef employeeRef, boolean checkAccess) {
        Set<QName> types = new HashSet<QName>();
        types.add(TYPE_STAFF_LIST);
        return getEmployeePositions(employeeRef, types, checkAccess);
    }

	@Override
	public List<NodeRef> getEmployeeWorkForces(NodeRef employeeRef) {
		return getEmployeeWorkForces(employeeRef, true);
	}

    private List<NodeRef> getEmployeeWorkForces(NodeRef employeeRef, boolean checkAccess) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_WORKFORCE);
		return getEmployeePositions(employeeRef, types, checkAccess);
	}

	private List<NodeRef> getEmployeePositions(NodeRef employeeRef, Set<QName> types) {
		return getEmployeePositions(employeeRef, types, true);
	}

    private List<NodeRef> getEmployeePositions(NodeRef employeeRef, Set<QName> types, boolean checkAccess) {
		List<NodeRef> positions = new ArrayList<NodeRef>();
		if (isEmployee(employeeRef) && (!checkAccess || hasAccessToOrgElement(employeeRef))) {
			List<AssociationRef> links = nodeService.getSourceAssocs(employeeRef, ASSOC_EMPLOYEE_LINK_EMPLOYEE);
			for (AssociationRef link : links) {
				if (!isArchive(link.getSourceRef())) {
					NodeRef staff = getPositionByEmployeeLink(link.getSourceRef());
					if (staff != null && isProperType(staff, types)) {
						positions.add(staff);
					}
				}
			}
		}
		return positions;
	}

	@Override
	public List<NodeRef> getEmployeeWorkGroups(NodeRef employeeRef) {
		return getEmployeeWorkGroups(employeeRef, true);
	}

    private List<NodeRef> getEmployeeWorkGroups(NodeRef employeeRef, boolean checkAccess) {
		List<NodeRef> wGroups = new ArrayList<NodeRef>();
		// получаем список объектов Участник рабочей группы
		List<NodeRef> workForces = getEmployeeWorkForces(employeeRef, checkAccess);
		for (NodeRef workForce : workForces) {
			NodeRef group = getWorkGroupByWorkForce(workForce);
			if (group != null && !isArchive(group)) {
				wGroups.add(group);
			}
		}
		return wGroups;
	}

	@Override
	public NodeRef getWorkGroupByWorkForce(NodeRef workRef) {
		NodeRef groupRef = null;
		if (isWorkForce(workRef)) {
			groupRef = nodeService.getPrimaryParent(workRef).getParentRef();
		}
		return groupRef;
	}

	@Override
	public List<NodeRef> getWorkRoles(boolean onlyActive) {
		List<NodeRef> results = new ArrayList<NodeRef>();
		NodeRef rolesRoot = dictionaryService.getDictionaryByName(ROLES_DICTIONARY_NAME);

		Set<QName> positions = new HashSet<QName>();
		positions.add(TYPE_WORK_ROLE);

		List<ChildAssociationRef> workRoles = nodeService.getChildAssocs(rolesRoot, positions);
		for (ChildAssociationRef workRole : workRoles) {
			if (!onlyActive) {
				results.add(workRole.getChildRef());
			} else {
				if (!isArchive(workRole.getChildRef())) {
					results.add(workRole.getChildRef());
				}
			}
		}
		return results;
	}

	@Override
	public NodeRef getEmployeeLinkByPosition(NodeRef positionRef) {
		NodeRef employeeLink = null;
		Set<QName> properTypes = new HashSet<QName>();
		properTypes.add(TYPE_STAFF_LIST);
		properTypes.add(TYPE_WORKFORCE);

		if (isProperType(positionRef, properTypes)) {
			Set<QName> link = new HashSet<QName>();
			link.add(TYPE_EMPLOYEE_LINK);

			List<ChildAssociationRef> links = nodeService.getChildAssocs(positionRef, link);
			if (links.size() > 0) {
				NodeRef empRef = links.get(0).getChildRef();
				if (!isArchive(empRef)) {
					employeeLink = empRef;
				}
			}
		}
		return employeeLink;
	}

	@Override
	public NodeRef getEmployeeLinkByWorkRole(NodeRef role) {
		NodeRef employeeLink = null;
		Set<QName> properTypes = new HashSet<QName>();
		properTypes.add(TYPE_WORK_ROLE);
		properTypes.add(TYPE_WORKFORCE);

		if (isProperType(role, properTypes)) {
			Set<QName> link = new HashSet<QName>();
			link.add(TYPE_EMPLOYEE_LINK);

			List<ChildAssociationRef> links = nodeService.getChildAssocs(role, link);
			if (links.size() > 0) {
				NodeRef empRef = links.get(0).getChildRef();
				if (!isArchive(empRef)) {
					employeeLink = empRef;
				}
			}
		}
		return employeeLink;
	}

	@Override
	public List<NodeRef> getBusinesRoles(boolean onlyActive) {
		List<NodeRef> results = new ArrayList<NodeRef>();
		NodeRef businessRolesRoot = dictionaryService.getDictionaryByName(BUSINESS_ROLES_DICTIONARY_NAME);

		Set<QName> roles = new HashSet<QName>();
		roles.add(TYPE_BUSINESS_ROLE);

		List<ChildAssociationRef> businessRoles = nodeService.getChildAssocs(businessRolesRoot, roles);
		for (ChildAssociationRef businessRole : businessRoles) {
			if (!onlyActive || !isArchive(businessRole.getChildRef())) {
				results.add(businessRole.getChildRef());
			} else {
				if ((Boolean) nodeService.getProperty(businessRole.getChildRef(), IS_ACTIVE)) {
					results.add(businessRole.getChildRef());
				}
			}
		}
		return results;
	}

	private Set<NodeRef> getEmployeesByBusinessRoleInternal(final NodeRef businessRoleRef) {
		Set<NodeRef> results = new HashSet<NodeRef>();
		if (isBusinessRole(businessRoleRef)) { // если бизнес роль
			// получаем сотрудников
			// напрямую имеющих роль
			List<AssociationRef> employees = nodeService.getTargetAssocs(businessRoleRef, ASSOC_BUSINESS_ROLE_EMPLOYEE);
			for (AssociationRef empChildRef : employees) {
				if (!isArchive(empChildRef.getTargetRef()) && hasAccessToOrgElement(empChildRef.getTargetRef())) {
					results.add(empChildRef.getTargetRef());
				}
			}
			//через структурные единицы (подразделения и рабочие группы)
			List<NodeRef> elementsByBusinessRole = getOrganizationElementsByBusinessRole(businessRoleRef);
			for (NodeRef orgElement : elementsByBusinessRole) {
				List<NodeRef> organizationElementEmployees = getOrganizationElementEmployees(orgElement);
				results.addAll(organizationElementEmployees);
			}
			//через позиции (должности и роли в рабочих группах)
			Set<NodeRef> results1 = new HashSet<NodeRef>();
			if (isBusinessRole(businessRoleRef)) { // если бизнес роль
				// получаем организационные элементы (подразделения и рабочие группы)
				List<AssociationRef> orgElementMembers = nodeService.getTargetAssocs(businessRoleRef, ASSOC_BUSINESS_ROLE_ORGANIZATION_ELEMENT_MEMBER);
				for (AssociationRef orgElementChildRef : orgElementMembers) {
					if (!isArchive(orgElementChildRef.getTargetRef())) {
						NodeRef employeeByPosition = getEmployeeByPosition(orgElementChildRef.getTargetRef());
						if (employeeByPosition != null && !isArchive(employeeByPosition) && hasAccessToOrgElement(employeeByPosition)) {
							results1.add(employeeByPosition);
						}
					}
				}
			}
			results.addAll(new ArrayList<NodeRef>(results1));
		}
		return results;
	}

	@Override
	public List<NodeRef> getEmployeesByBusinessRole(String businessRoleId) {
		return getEmployeesByBusinessRole(businessRoleId, false);
	}

	@Override
	public List<NodeRef> getEmployeesByBusinessRole(String businessRoleId, boolean withDelegation) {
		NodeRef businessRole = getBusinessRoleByIdentifier(businessRoleId);
		if (businessRole != null) {
			return getEmployeesByBusinessRole(businessRole, withDelegation);
		}
		return null;
	}

	@Override
	public List<NodeRef> getEmployeesByBusinessRole(NodeRef businessRoleRef) {
		return getEmployeesByBusinessRole(businessRoleRef, false);
	}

	@Override
	public List<NodeRef> getEmployeesByBusinessRole(NodeRef businessRoleRef, boolean withDelegation) {
		//получаем сотрудников по бизнес роли, согласно оргштатки
		Set<NodeRef> results = getEmployeesByBusinessRoleInternal(businessRoleRef);
		if (withDelegation) {
            //пробегаемся по сотрудникам, смотрим их активные параметры делегирования
			//через активные параметры делегирования забираем доверенное лицо
			Set<NodeRef> trustees = new HashSet<NodeRef>();
			for (NodeRef employeeRef : results) {
				List<NodeRef> delegationOptsList = findNodesByAssociationRef(employeeRef, IDelegation.ASSOC_DELEGATION_OPTS_OWNER, IDelegation.TYPE_DELEGATION_OPTS, ASSOCIATION_TYPE.SOURCE);
				for (NodeRef delegationOpts : delegationOptsList) {
                    // Получаем ответственного, того сотрудника, кому мы делегировали полномочия
					// только для активного делегирования
					if (isActiveDelegationOpts(delegationOpts)) {
						NodeRef trustee = findNodeByAssociationRef(delegationOpts, IDelegation.ASSOC_DELEGATION_OPTS_TRUSTEE, TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
						if (trustee != null && hasAccessToOrgElement(trustee)) {
							trustees.add(trustee);
						}
					}
				}
			}
			results.addAll(trustees);
			//получаем сотрудников по бизнес роли через активные доверенности
			List<NodeRef> procuraciesList = findNodesByAssociationRef(businessRoleRef, IDelegation.ASSOC_PROCURACY_BUSINESS_ROLE, IDelegation.TYPE_PROCURACY, ASSOCIATION_TYPE.SOURCE);
			for (NodeRef procuracyRef : procuraciesList) {
				//только для активных доверенностей
				if (isProcuracyActive(procuracyRef)) {
					NodeRef trustee = findNodeByAssociationRef(procuracyRef, IDelegation.ASSOC_PROCURACY_TRUSTEE, TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
					if (trustee != null && hasAccessToOrgElement(trustee)) {
						results.add(trustee);
					}
				}
			}
		}
		return new ArrayList<NodeRef>(results);
	}

	@Override
	public List<NodeRef> getOrganizationElementsByBusinessRole(NodeRef businessRoleRef, boolean subUnits) {
		List<NodeRef> results = new ArrayList<NodeRef>();
		if (isBusinessRole(businessRoleRef)) { // если бизнес роль
			// получаем организационные элементы (подразделения и рабочие группы)
			List<AssociationRef> orgElements = nodeService.getTargetAssocs(businessRoleRef, ASSOC_BUSINESS_ROLE_ORGANIZATION_ELEMENT);
			for (AssociationRef orgElementChildRef : orgElements) {
				NodeRef orgElementNode = orgElementChildRef.getTargetRef();
				if (!isArchive(orgElementNode)) {
					results.add(orgElementNode);
					if (subUnits) {
						results.addAll(getSubUnits(orgElementNode, true, true));
					}
				}
			}
		}
		return results;
	}

	@Override
	public List<NodeRef> getOrganizationElementsByBusinessRole(NodeRef businessRoleRef) {
		return getOrganizationElementsByBusinessRole(businessRoleRef, true);
	}

	@Override
	public List<NodeRef> getOrganizationElementMembersByBusinessRole(NodeRef businessRoleRef) {
		List<NodeRef> results = new ArrayList<>();

		if (isBusinessRole(businessRoleRef)) {
			List<AssociationRef> orgElementsMembers = nodeService.getTargetAssocs(businessRoleRef, ASSOC_BUSINESS_ROLE_ORGANIZATION_ELEMENT_MEMBER);
			for (AssociationRef orgElementMemberChildRef : orgElementsMembers) {
				NodeRef orgElementMemberNode = orgElementMemberChildRef.getTargetRef();
				if (!isArchive(orgElementMemberNode)) {
					results.add(orgElementMemberNode);
				}
			}
		}

		return results;
	}

	@Override
	public List<NodeRef> getEmployeeLinks(NodeRef employeeRef) {
		return getEmployeeLinks(employeeRef, false);
	}

	@Override
	public List<NodeRef> getEmployeeLinks(NodeRef employeeRef, boolean includeArchived) {
		List<NodeRef> links = new ArrayList<NodeRef>();
		if (isEmployee(employeeRef) && hasAccessToOrgElement(employeeRef)) {
			List<AssociationRef> lRefs = nodeService.getSourceAssocs(employeeRef, ASSOC_EMPLOYEE_LINK_EMPLOYEE);
			for (AssociationRef lRef : lRefs) {
				if (!includeArchived && isArchive(lRef.getSourceRef())) {
					continue;
				}
				links.add(lRef.getSourceRef());
			}
		}
		return links;
	}

	@Override
	public List<NodeRef> getUnitEmployees(NodeRef unitRef) {
		return getUnitEmployees(unitRef, true);
	}
    @Override
	public List<NodeRef> getUnitEmployees(NodeRef unitRef, boolean checkAccess) {
		List<NodeRef> employees = new ArrayList<NodeRef>();
		// Получаем список штатных расписаний
		List<NodeRef> staffs = getUnitStaffLists(unitRef, checkAccess);
		Set<NodeRef> employessSet = new HashSet<NodeRef>();
		for (NodeRef staff : staffs) {
			if (!isArchive(staff)) {
				NodeRef employee = getEmployeeByPosition(staff, checkAccess);
				if (employee != null && !isArchive(employee)) {
					employessSet.add(employee);
				}
			}
		}
		employees.addAll(employessSet);
		return employees;
	}

	@Override
	public List<NodeRef> getEmployeeStaffLinks(NodeRef employeeRef) {
		List<NodeRef> links = new ArrayList<NodeRef>();
		if (isEmployee(employeeRef)) {
			List<NodeRef> aLinks = getEmployeeLinks(employeeRef);
			for (NodeRef link : aLinks) {
				NodeRef position = getPositionByEmployeeLink(link);
				if (position != null && !isArchive(position) && isStaffList(position)) {
					links.add(link);
				}
			}
		}
		return links;
	}

	@Override
	public NodeRef getCurrentEmployee() {
		String fullyAuthenticatedUser = AuthenticationUtil.getFullyAuthenticatedUser();
		return getEmployeeByPerson(fullyAuthenticatedUser, false);  //не проверяем права текущего сотрудника к текущему сотруднику - доступен всегда
	}

	@Override
	public boolean isCurrentUserTheSystemUser() {
		return authService.isCurrentUserTheSystemUser();
	}

	@Override
	public NodeRef getEmployeeByPerson(String personName) {
		return getEmployeeByPerson(personName, true);
	}

    @Override
    public NodeRef getEmployeeByPerson(String personName, boolean checkAccess) {
        if (personName != null) {
            NodeRef personNodeRef;
            try {
                personNodeRef = personService.getPerson(personName, false);
            } catch (NoSuchPersonException e) {
                return null;
            }
            if (personNodeRef != null) {
                return getEmployeeByPerson(personNodeRef, checkAccess);
            }
        }
        return null;
    }

	@Override
	public NodeRef getEmployeeByPerson(NodeRef person) {
		return getEmployeeByPerson(person, true);
	}

	@Override
	public NodeRef getEmployeeByPerson(NodeRef person, boolean checkAccess) {
		List<AssociationRef> lRefs = nodeService.getSourceAssocs(person, ASSOC_EMPLOYEE_PERSON);
		for (AssociationRef lRef : lRefs) {
			if (!isArchive(lRef.getSourceRef()) && (!checkAccess || hasAccessToOrgElement(lRef.getSourceRef()))) {
				return lRef.getSourceRef();
			}
		}
		return null;
	}

	@Override
	public NodeRef getPersonForEmployee(NodeRef employee) {
		List<AssociationRef> persons = nodeService.getTargetAssocs(employee, ASSOC_EMPLOYEE_PERSON);
		if (persons.size() > 0) {
			return persons.get(0).getTargetRef();
		} else {
			return null;
		}
	}

	@Override
	public NodeRef getPositionByStaff(NodeRef staffList) {
		return findNodeByAssociationRef(staffList, ASSOC_ELEMENT_MEMBER_POSITION, TYPE_STAFF_POSITION, ASSOCIATION_TYPE.TARGET);
	}

	@Override
	public NodeRef getRoleByWorkForce(NodeRef workforce) {
		return findNodeByAssociationRef(workforce, ASSOC_ELEMENT_MEMBER_POSITION, TYPE_WORK_ROLE, ASSOCIATION_TYPE.TARGET);
	}

	@Override
	public List<NodeRef> getEmployeeUnits(final NodeRef employeeRef, final boolean bossUnitsOnly) {
		return getEmployeeUnits(employeeRef, bossUnitsOnly, false, true);
	}

	@Override
	public List<NodeRef> getEmployeeUnits (final NodeRef employeeRef, final boolean bossUnitsOnly, final boolean checkAccess) {
		return getEmployeeUnits(employeeRef, bossUnitsOnly, false, checkAccess);
	}

    private List<NodeRef> getEmployeeUnits(final NodeRef employeeRef, final boolean bossUnitsOnly, final boolean allParents, final boolean checkAccess) {
        //получаем список штатных расписаний сотрудника
        List<NodeRef> staffs = getEmployeeStaffs(employeeRef, checkAccess);
        List<NodeRef> units = new ArrayList<NodeRef>();
        for (NodeRef staffRef : staffs) {
            //для каждого штатного расписания вытаскиваем подразделение
            NodeRef unitRef = getUnitByStaff(staffRef, checkAccess);
            //узнаем является ли указанный сотрудник боссом по своему штатному расписанию
            Boolean isBoss = (Boolean) nodeService.getProperty(staffRef, PROP_STAFF_LIST_IS_BOSS);
            if (bossUnitsOnly && isBoss) {
                units.add(unitRef);
            } else if (!bossUnitsOnly) {
                units.add(unitRef);
            }
            if (allParents) {
                units.addAll(getHigherUnits(unitRef, checkAccess));
            }
        }
        return units;
    }


    @Override
	public List<NodeRef> getBossSubordinate(final NodeRef employeeRef) {
		return getBossSubordinate(employeeRef, false);
	}

	@Override
	public List<NodeRef> getBossSubordinate(final NodeRef employeeRef, final boolean withDelegation) {
		return getBossSubordinate(employeeRef, withDelegation, true);
	}

    private List<NodeRef> getBossSubordinate(final NodeRef employeeRef, final boolean withDelegation, final boolean checkAccess) {
        Set<NodeRef> employees = new HashSet<NodeRef>();
        employees.addAll(getBossSubordinateInternal(employeeRef, checkAccess));
        if (withDelegation) {
            final List<NodeRef> bosses = getBosses(employeeRef);
            for (NodeRef boss : bosses) {
                final List<NodeRef> bossSubordinateInternal = getBossSubordinateInternal(boss, checkAccess);
                employees.addAll(bossSubordinateInternal);
            }
        }

        return new ArrayList<NodeRef>(employees);
    }

	private List<NodeRef> getBossSubordinateInternal(final NodeRef employeeRef, final boolean checkAccess) {
		//получаем список подразделений где этот сотрудник является боссом
		Collection<NodeRef> units = getEmployeeUnits(employeeRef, true, false, checkAccess);
		Set<NodeRef> employees = new HashSet<NodeRef>();
		for (NodeRef unitRef : units) {
			//берем сотрудников из непосредственно этого подразделения
			employees.addAll(getOrganizationElementEmployees(unitRef, false));
			//берем все дочерние подразделения и собираем сотрудников уже из них
			List<NodeRef> subUnits = getSubUnits(unitRef, true, true);
			for (NodeRef subUnitRef : subUnits) {
				employees.addAll(getOrganizationElementEmployees(subUnitRef, false));
			}
		}
		//начальника выгоняем из множества сотрудников
		employees.remove(employeeRef);
		return new ArrayList<NodeRef>(employees);
	}

	/**
	 * найти или создать связь между бизнес ролью и NodeRef-ой указанного типа
	 *
	 * @param businesssRoleRef бизнес роль у которой привязываем NodeRef
	 * @param targetRef привязываемый NodeRef
	 * @param assocName имя типа ассоциации
	 * @return AssociationRef где source это бизнес роль, target это целевой
	 * NodeRef
	 */
	private AssociationRef getOrCreateBusinessRoleAssoc(NodeRef businesssRoleRef, NodeRef targetRef, QName assocName) {
		List<AssociationRef> associationRefs = nodeService.getTargetAssocs(businesssRoleRef, assocName);
		AssociationRef targetAssoc = null;
		if (associationRefs != null) {
			for (AssociationRef associationRef : associationRefs) {
				if (associationRef.getTargetRef().equals(targetRef)) {
					targetAssoc = associationRef;
					break;
				}
			}
		}
		if (targetAssoc == null) {
			targetAssoc = nodeService.createAssociation(businesssRoleRef, targetRef, assocName);
		}
		return targetAssoc;
	}

	@Override
	public AssociationRef includeEmployeeIntoBusinessRole(final NodeRef businesssRoleRef, final NodeRef employeeRef) {
		return getOrCreateBusinessRoleAssoc(businesssRoleRef, employeeRef, ASSOC_BUSINESS_ROLE_EMPLOYEE);
	}

	@Override
	public AssociationRef includeOrgElementIntoBusinessRole(final NodeRef businesssRoleRef, final NodeRef orgElementRef) {
		return getOrCreateBusinessRoleAssoc(businesssRoleRef, orgElementRef, ASSOC_BUSINESS_ROLE_ORGANIZATION_ELEMENT);
	}

	@Override
	public AssociationRef includeOrgElementMemberIntoBusinessRole(final NodeRef businesssRoleRef, final NodeRef orgElementMemberRef) {
		return getOrCreateBusinessRoleAssoc(businesssRoleRef, orgElementMemberRef, ASSOC_BUSINESS_ROLE_ORGANIZATION_ELEMENT_MEMBER);
	}

	/**
	 * поиск и удаление у бизнес роли ассоциации указанного типа на конкретный
	 * NodeRef
	 *
	 * @param businesssRoleRef бизнес роль у которой удаляем ассоциацию
	 * @param targetRef целевой NodeRef на который указывает удаляемая
	 * ассоциация
	 * @param assocName имя типа ассоциации
	 */
	private void findAndRemoveBusinessRoleAssoc(NodeRef businesssRoleRef, NodeRef targetRef, QName assocName) {
		List<AssociationRef> associationRefs = nodeService.getTargetAssocs(businesssRoleRef, assocName);
		if (associationRefs != null) {
			for (AssociationRef associationRef : associationRefs) {
				if (associationRef.getTargetRef().equals(targetRef)) {
					nodeService.removeAssociation(businesssRoleRef, targetRef, assocName);
					break;
				}
			}
		}
	}

	@Override
	public void excludeEmployeeFromBusinessRole(final NodeRef businesssRoleRef, final NodeRef employeeRef) {
        if (hasAccessToOrgElement(employeeRef)) {
            findAndRemoveBusinessRoleAssoc(businesssRoleRef, employeeRef, ASSOC_BUSINESS_ROLE_EMPLOYEE);
        }
	}

	@Override
	public void excludeOrgElementFromBusinessRole(final NodeRef businesssRoleRef, final NodeRef employeeRef) {
        if (hasAccessToOrgElement(employeeRef)) {
            findAndRemoveBusinessRoleAssoc(businesssRoleRef, employeeRef, ASSOC_BUSINESS_ROLE_ORGANIZATION_ELEMENT);
        }
	}

	@Override
	public void excludeOrgElementMemberFromBusinesssRole(final NodeRef businesssRoleRef, final NodeRef employeeRef) {
        if (hasAccessToOrgElement(employeeRef)) {
            findAndRemoveBusinessRoleAssoc(businesssRoleRef, employeeRef, ASSOC_BUSINESS_ROLE_ORGANIZATION_ELEMENT_MEMBER);
        }
	}

	@Override
	public NodeRef getBusinessRoleByIdentifier(final String businessRoleIdentifier) {
		NodeRef businessRolesDictionaryRef = dictionaryService.getDictionaryByName(BUSINESS_ROLES_DICTIONARY_NAME);
		List<NodeRef> children = dictionaryService.getChildren(businessRolesDictionaryRef);
		NodeRef brRef = null;
		for (NodeRef child : children) {
			Serializable id = nodeService.getProperty(child, PROP_BUSINESS_ROLE_IDENTIFIER);
			if (id != null && businessRoleIdentifier.equals(id.toString())) {
				brRef = child;
				break;
			}
		}
		return brRef;
	}

	@Override
	public NodeRef getBusinessRoleDelegationEngineer() {
		return getBusinessRoleByIdentifier(BUSINESS_ROLE_ENGINEER_ID);
	}

	@Override
	public NodeRef getBusinessRoleCalendarEngineer() {
		return getBusinessRoleByIdentifier(BUSINESS_ROLE_CALENDAR_ENGINEER_ID);
	}

	@Override
	public void fireEmployee(final NodeRef employeeRef) {
//		TODO: Судя по всему, нигде не используется, но транзакцию на всякий случай выпилю.
		if (isEmployee(employeeRef) && hasAccessToOrgElement(employeeRef)) {
			nodeService.setProperty(employeeRef, IS_ACTIVE, false);
		}
	}

	@Override
	public void restoreEmployee(final NodeRef employeeRef) {
//		TODO: Судя по всему, нигде не используется, но транзакцию на всякий случай выпилю.
		if (isEmployee(employeeRef) && hasAccessToOrgElement(employeeRef)) {
			nodeService.setProperty(employeeRef, IS_ACTIVE, true);
		}
	}

	@Override
	public void makeStaffBossOrEmployee(final NodeRef orgElementMemberRef, final boolean isBoss) {
//		TODO: Судя по всему, нигде не используется, но транзакцию на всякий случай выпилю.
		//флаг руководящей позиции актуален ТОЛЬКО для штатных расписаний
		if (isStaffList(orgElementMemberRef)) {
			//получим отдел в котором есть это штатное расписание
			NodeRef unitRef = getUnitByStaff(orgElementMemberRef);
			//в этом отделе пытаемся найти руководящую позицию
			NodeRef bossStaffRef = getBossStaff(unitRef);
			//если руководящей позиции нет, или ее надо снять
			if (bossStaffRef == null || !isBoss) {
				nodeService.setProperty(orgElementMemberRef, PROP_STAFF_LIST_IS_BOSS, isBoss);
			}
		}
	}

	@Override
	public NodeRef createStaff(final NodeRef orgElement, final NodeRef staffPosition) {
//		TODO: Судя по всему, нигде не используется(хотя есть в Integral Test), но транзакцию на всякий случай выпилю.
		//если переданные параметры это подразделение и должность то заводим штатное расписание
		NodeRef staffRef = null;
		if (isUnit(orgElement) && isPosition(staffPosition)) {
			QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, UUID.randomUUID().toString());
			ChildAssociationRef childAssociationRef = nodeService.createNode(orgElement, ContentModel.ASSOC_CONTAINS, assocQName, TYPE_STAFF_LIST);
			nodeService.createAssociation(childAssociationRef.getChildRef(), staffPosition, ASSOC_ELEMENT_MEMBER_POSITION);
			return childAssociationRef.getChildRef();
		}
		return staffRef;
	}

	@Override
	public void includeEmployeeIntoStaff(final NodeRef employeeRef, final NodeRef orgElementMemberRef, final boolean isPrimary) {
//		TODO: Судя по всему, нигде не используется(хотя есть в Integral Test), но транзакцию на всякий случай выпилю.
		if (isEmployee(employeeRef) && hasAccessToOrgElement(employeeRef) && isStaffList(orgElementMemberRef) && getEmployeeByPosition(orgElementMemberRef) == null) {
			QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, UUID.randomUUID().toString());
            HashMap<QName, Serializable> props = new HashMap<>();
			NodeRef employeeLinkRef = nodeService.createNode(orgElementMemberRef, ASSOC_ELEMENT_MEMBER_EMPLOYEE, assocQName, TYPE_EMPLOYEE_LINK, props).getChildRef();
			nodeService.createAssociation(employeeLinkRef, employeeRef, ASSOC_EMPLOYEE_LINK_EMPLOYEE);
            if (isPrimary) {
                //флаг выставляется уже после создания ассоциации, т.к. на создании ассоциации срабатывает полиси, сбрасывающая флаг
                List<NodeRef> employeeLinks = getEmployeeLinks(employeeRef);
                for (NodeRef employeeLink : employeeLinks) {
                    if (employeeLink.equals(employeeLinkRef)) {
                        nodeService.setProperty(employeeLink, PROP_EMP_LINK_IS_PRIMARY, true);
                    } else if (Boolean.TRUE.equals(nodeService.getProperty(employeeLink, PROP_EMP_LINK_IS_PRIMARY))) {
                        nodeService.setProperty(employeeLink, PROP_EMP_LINK_IS_PRIMARY, false);
                    }
                }
            }
		}
	}

	@Override
	public void excludeEmployeeFromStaff(final NodeRef orgElementMemberRef) {
//		TODO: Судя по всему, нигде не используется, но транзакцию на всякий случай выпилю.
		if (isStaffList(orgElementMemberRef)) {
			NodeRef employeeLinkRef = getEmployeeLinkByPosition(orgElementMemberRef);
			nodeService.removeAssociation(orgElementMemberRef, employeeLinkRef, ASSOC_ELEMENT_MEMBER_EMPLOYEE);
			nodeService.deleteNode(employeeLinkRef);
		}
	}

	@Override
	public ChildAssociationRef moveOrgElement(final NodeRef unitRef, final NodeRef parentUnitRef) {
//		TODO: Судя по всему, нигде не используется, но транзакцию на всякий случай выпилю.
		//если родитель null то переместить в корень надо
        if (hasAccessToOrgElement(unitRef)) {
            NodeRef parentRef;
            if (parentUnitRef == null) {
                parentRef = getStructureDirectory();
            } else {
                parentRef = parentUnitRef;
            }
            String name = nodeService.getProperty(unitRef, ContentModel.PROP_NAME).toString();
            QName assocQname = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, name);
            return nodeService.moveNode(unitRef, parentRef, ContentModel.ASSOC_CONTAINS, assocQname);
        } else {
            return null;
        }
	}

	@Override
	public boolean isCalendarEngineer(final NodeRef employeeRef) {
		return hasAccessToOrgElement(employeeRef) && isEmployeeHasBusinessRole(employeeRef, BUSINESS_ROLE_CALENDAR_ENGINEER_ID, true);
	}

	private boolean isBossInternal(final NodeRef employeeRef) {
		boolean isBoss = false;
		if (nodeService.exists(employeeRef) && isEmployee(employeeRef)) {
			// получаем основную должностную позицию
			List<NodeRef> employeeStaffs = getEmployeeStaffs(employeeRef);
			for (NodeRef employeeStaff : employeeStaffs) {
				if (employeeStaff != null) {
					// получаем подразделение для штатного расписания
					NodeRef unit = getUnitByStaff(employeeStaff);
					// получаем руководителя для подразделения
					NodeRef bossRef = getUnitBoss(unit);
					isBoss = employeeRef.equals(bossRef);
					if (isBoss) {
						break;
					}
				}
			}
		}
		return isBoss;
	}

	@Override
	public boolean isDelegationEngineer(NodeRef employeeRef) {
		return hasAccessToOrgElement(employeeRef) && isEmployeeHasBusinessRole(employeeRef, BUSINESS_ROLE_ENGINEER_ID, true);
	}

	private boolean hasSubordinateInternal(NodeRef bossRef, NodeRef subordinateRef) {
		boolean hasSubordinate = hasAccessToOrgElement(bossRef) && hasAccessToOrgElement(subordinateRef) && bossRef.equals(subordinateRef);
		if (!hasSubordinate) {
			List<NodeRef> subordinates = getBossSubordinate(bossRef);
			hasSubordinate = subordinates.contains(subordinateRef);
		}
		return hasSubordinate;
	}

	@Override
	public boolean isCurrentEmployeeHasBusinessRole(final String businessRoleIdentifier) {
		return isEmployeeHasBusinessRole(getCurrentEmployee(), businessRoleIdentifier);
	}

	@Override
	public String getEmployeeLogin(NodeRef employee) {
		if (employee == null || !isEmployee(employee)) {
			return null;
		}
		final NodeRef person = getPersonForEmployee(employee);
		if (person == null) {
			logger.warn("Employee {} is not linked to system user", employee.toString());
			return null;
		}
		return (String) nodeService.getProperty(person, ContentModel.PROP_USERNAME);
	}

	@Override
	public List<NodeRef> getEmployeeRoles(NodeRef employeeRef) {
		return getEmployeeRoles(employeeRef, false, false);
	}

	@Override
	public List<NodeRef> getEmployeeRoles(NodeRef employeeRef, boolean includeDelegatedRoles) {
		return getEmployeeRoles(employeeRef, includeDelegatedRoles, false);
	}

	@Override
	public Set<NodeRef> getEmployeeDirectRoles(NodeRef employeeRef) {
		return getEmployeeDirectRoles(employeeRef, true);
	}

    private Set<NodeRef> getEmployeeDirectRoles(NodeRef employeeRef, boolean checkAccess) {
        Set<NodeRef> results = new HashSet<NodeRef>();
        // роли непосредственно имеющиеся у сотрудника
        if (!checkAccess || hasAccessToOrgElement(employeeRef)) {
            List<AssociationRef> employeeRoles = nodeService.getSourceAssocs(employeeRef, ASSOC_BUSINESS_ROLE_EMPLOYEE);
            for (AssociationRef empRolesChildRef : employeeRoles) {
                if (!isArchive(empRolesChildRef.getSourceRef())) {
                    results.add(empRolesChildRef.getSourceRef());
                }
            }
        }

        return results;
    }

	@Override
	public Set<NodeRef> getEmployeeUnitRoles(NodeRef employeeRef) {
		return getEmployeeUnitRoles(employeeRef, true);
	}

    private Set<NodeRef> getEmployeeUnitRoles(NodeRef employeeRef, boolean checkAccess) {
        Set<NodeRef> results = new HashSet<NodeRef>();
        List<NodeRef> employeeUnits = getEmployeeUnits(employeeRef, false, true, checkAccess);
        for (NodeRef employeeUnit : employeeUnits) {
            List<AssociationRef> unitRoles = nodeService.getSourceAssocs(employeeUnit, ASSOC_BUSINESS_ROLE_ORGANIZATION_ELEMENT);
            for (AssociationRef unitRoleChildRef : unitRoles) {
                if (!isArchive(unitRoleChildRef.getSourceRef())) {
                    results.add(unitRoleChildRef.getSourceRef());
                }
            }
        }
        return results;
    }

	@Override
	public Set<NodeRef> getEmployeeWGRoles(NodeRef employeeRef) {
		return getEmployeeWGRoles(employeeRef, true);
	}

    private Set<NodeRef> getEmployeeWGRoles(NodeRef employeeRef, boolean checkAccess) {
        Set<NodeRef> results = new HashSet<NodeRef>();
        List<NodeRef> employeeGroups = getEmployeeWorkGroups(employeeRef, checkAccess);
        for (NodeRef employeeGroup : employeeGroups) {
            List<AssociationRef> groupRoles = nodeService.getSourceAssocs(employeeGroup, ASSOC_BUSINESS_ROLE_ORGANIZATION_ELEMENT);
            for (AssociationRef groupRoleChildRef : groupRoles) {
                if (!isArchive(groupRoleChildRef.getSourceRef())) {
                    results.add(groupRoleChildRef.getSourceRef());
                }
            }
        }
        return results;
    }

	@Override
	public Set<NodeRef> getEmployeeDPRoles(NodeRef employeeRef) {
		return getEmployeeDPRoles(employeeRef, true);
	}

    private Set<NodeRef> getEmployeeDPRoles(NodeRef employeeRef, boolean checkAccess) {
        Set<NodeRef> results = new HashSet<NodeRef>();
        Set<QName> types = new HashSet<QName>();
        types.add(TYPE_STAFF_LIST);
        types.add(TYPE_WORKFORCE);
        List<NodeRef> positionsRefs = getEmployeePositions(employeeRef, types, checkAccess);
        for (NodeRef positionRef : positionsRefs) {
            List<AssociationRef> positionRoles = nodeService.getSourceAssocs(positionRef, ASSOC_BUSINESS_ROLE_ORGANIZATION_ELEMENT_MEMBER);
            for (AssociationRef positionRoleChildRef : positionRoles) {
                if (!isArchive(positionRoleChildRef.getSourceRef())) {
                    results.add(positionRoleChildRef.getSourceRef());
                }
            }
        }
        return results;
    }

	@Override
	public List<NodeRef> getEmployeeRoles(NodeRef employeeRef, boolean includeDelegatedRoles, boolean inheritSubordinatesRoles) {
		return getEmployeeRoles(employeeRef, includeDelegatedRoles, inheritSubordinatesRoles, true);
	}

    private List<NodeRef> getEmployeeRoles(NodeRef employeeRef, boolean includeDelegatedRoles, boolean inheritSubordinatesRoles, boolean checkAccess) {
        Set<NodeRef> results = new HashSet<NodeRef>();
        if (isEmployee(employeeRef)) {
            // роли непосредственно имеющиеся у сотрудника
            results.addAll(getEmployeeDirectRoles(employeeRef, checkAccess));
            // роли для подразделений сотрудника
            results.addAll(getEmployeeUnitRoles(employeeRef, checkAccess));
            // роли для РГ сотрудника
            results.addAll(getEmployeeWGRoles(employeeRef, checkAccess));
            // роли для позиций сотрудника
            results.addAll(getEmployeeDPRoles(employeeRef, checkAccess));
        }
        if (includeDelegatedRoles) {
            results.addAll(getEmployeeRolesWithDelegation(employeeRef, checkAccess));
        }
        if (inheritSubordinatesRoles) {
            List<NodeRef> subordinates = getBossSubordinate(employeeRef, includeDelegatedRoles, checkAccess);
            if (subordinates != null) {
                for (NodeRef subordinateEmployee : subordinates) {
                    results.addAll(getEmployeeRoles(subordinateEmployee, includeDelegatedRoles, false, checkAccess));
                }
            }
        }
        return new ArrayList<NodeRef>(results);
    }

	@Override
	public List<NodeRef> getEmployeeRolesWithDelegation(final NodeRef employeeRef) {
		return getEmployeeRolesWithDelegation(employeeRef, true);
	}

    private List<NodeRef> getEmployeeRolesWithDelegation(final NodeRef employeeRef, boolean checkAccess) {
        Set<NodeRef> roles = new HashSet<NodeRef>();
        //получаем бизнес роли через активные доверенности
        List<NodeRef> procuracies = getActiveProcuracies(employeeRef, checkAccess);
        for (NodeRef procuracy : procuracies) {
            NodeRef role = findNodeByAssociationRef(procuracy, IDelegation.ASSOC_PROCURACY_BUSINESS_ROLE, OrgstructureBean.TYPE_BUSINESS_ROLE, ASSOCIATION_TYPE.TARGET);
            if (role != null) {
                roles.add(role);
            }
        }
        //получаем список delegation-opts, где employeeRef участвует в виде доверенного лица\
        if (!checkAccess || hasAccessToOrgElement(employeeRef)) {
            List<NodeRef> delegationOptsList = findNodesByAssociationRef(employeeRef, IDelegation.ASSOC_DELEGATION_OPTS_TRUSTEE, IDelegation.TYPE_DELEGATION_OPTS, ASSOCIATION_TYPE.SOURCE);
            for (NodeRef delegationOpts : delegationOptsList) {
                // Получаем хозяина настроек делегировая, который доверил нешему чуваку что-либо
                // только для активного делегирования
                if (isActiveDelegationOpts(delegationOpts)) {
                    NodeRef owner = findNodeByAssociationRef(delegationOpts, IDelegation.ASSOC_DELEGATION_OPTS_OWNER, TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
                    //берем все бизнес роли хозяина
                    roles.addAll(getEmployeeRoles(owner, false, false, checkAccess));
                }
            }
        }
        return new ArrayList<NodeRef>(roles);
    }

    @Override
    public Map<NodeRef, List<NodeRef>> getEmployeeDelegatedRolesWithOwner(NodeRef employeeRef) {
        Map<NodeRef, List<NodeRef>> result = new HashMap<NodeRef, List<NodeRef>>();

        List<NodeRef> procuracies = getActiveProcuracies(employeeRef, true);
        List<NodeRef> delegationOptsList = new ArrayList<NodeRef>();
        NodeRef delegateOpts, owner = null;
        for (NodeRef procuracy : procuracies) {
            NodeRef role = findNodeByAssociationRef(procuracy, IDelegation.ASSOC_PROCURACY_BUSINESS_ROLE, OrgstructureBean.TYPE_BUSINESS_ROLE, ASSOCIATION_TYPE.TARGET);
            List<ChildAssociationRef> delegateOptsAssoc = nodeService.getParentAssocs(procuracy);
            for (ChildAssociationRef ch : delegateOptsAssoc) {
                delegateOpts = ch.getParentRef();
                if (isActiveDelegationOpts(delegateOpts)) {
                    owner = findNodeByAssociationRef(delegateOpts, IDelegation.ASSOC_DELEGATION_OPTS_OWNER, TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
                }
            }
            List<NodeRef> tmpList = new ArrayList<NodeRef>();
            if (result.containsKey(owner)) {
                tmpList.addAll(result.get(owner));
                tmpList.add(role);
                result.put(owner, tmpList);
            } else {
                tmpList.add(role);
                result.put(owner, tmpList);
            }
        }
        if (hasAccessToOrgElement(employeeRef)) {
            delegationOptsList.addAll(findNodesByAssociationRef(employeeRef, IDelegation.ASSOC_DELEGATION_OPTS_TRUSTEE, IDelegation.TYPE_DELEGATION_OPTS, ASSOCIATION_TYPE.SOURCE));
        }
        for (NodeRef delegationOpts : delegationOptsList) {
            // Получаем хозяина настроек делегировая, который доверил нешему чуваку что-либо
            // только для активного делегирования
            if (isActiveDelegationOpts(delegationOpts)) {
                owner = findNodeByAssociationRef(delegationOpts, IDelegation.ASSOC_DELEGATION_OPTS_OWNER, TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
                List<NodeRef> tmpList = new ArrayList<NodeRef>();
                if (result.containsKey(owner)) {
                    tmpList.addAll(result.get(owner));
                    tmpList.addAll(getEmployeeRoles(owner, true, true));
                    result.put(owner, tmpList);
                } else {
                    tmpList.addAll(getEmployeeRoles(owner, true, true));
                    result.put(owner, tmpList);
                }

            }
        }
        return result;

    }

    private List<NodeRef> getHigherUnits(NodeRef unitRef) {
        return getHigherUnits(unitRef, true);
    }

    private List<NodeRef> getHigherUnits(NodeRef unitRef, boolean checkAccess) {
        List<NodeRef> units = new ArrayList<NodeRef>();
        NodeRef parent = getParentUnit(unitRef, checkAccess);
        if (parent != null) {
            units.add(parent);
            units.addAll(getHigherUnits(parent, checkAccess));
        }
        return units;
    }

	/**
	 * проверяет что доверенность является активной доверенность активна если у
	 * нее и у параметров делегирования одновременно установлен active=true
	 *
	 * @param procuracyRef ссылка на доверенность
	 */
	private boolean isProcuracyActive(final NodeRef procuracyRef) {
		boolean isProcuracy = isProperType(procuracyRef, IDelegation.TYPE_PROCURACY);
		boolean isActive = false;
		if (isProcuracy) {
			NodeRef trusteeRef = findNodeByAssociationRef(procuracyRef, IDelegation.ASSOC_PROCURACY_TRUSTEE, TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
			boolean isProcuracyActive = Boolean.TRUE.equals(nodeService.getProperty(procuracyRef, IS_ACTIVE)) && trusteeRef != null;
			List<ChildAssociationRef> parents = nodeService.getParentAssocs(procuracyRef, IDelegation.ASSOC_DELEGATION_OPTS_PROCURACY, RegexQNamePattern.MATCH_ALL);
			boolean isDelegationActive = false;
			if ((parents != null) && (!parents.isEmpty())) {
				NodeRef delegationOpts = parents.get(0).getParentRef();
				isDelegationActive = isActiveDelegationOpts(delegationOpts);
			}
			isActive = isProcuracyActive && isDelegationActive;
		}
		return isActive;
	}

	/**
	 * получает список активных доверенностей для указанного сотрудника
	 * lecm-orgstr:employee
	 *
	 * @param employeeRef ссылка на сотрудника
	 * @return список активных NodeRef lecm-d8n:procuracy или пустой список
	 */
	private List<NodeRef> getActiveProcuracies(final NodeRef employeeRef, boolean checkAccess) {
		List<NodeRef> activeProcuracies = new ArrayList<NodeRef>();
		if (isEmployee(employeeRef) && (!checkAccess || hasAccessToOrgElement(employeeRef))) {
			List<NodeRef> procuracies = findNodesByAssociationRef(employeeRef, IDelegation.ASSOC_PROCURACY_TRUSTEE, IDelegation.TYPE_PROCURACY, BaseBean.ASSOCIATION_TYPE.SOURCE);
			for (NodeRef procuracy : procuracies) {
				if (isProcuracyActive(procuracy)) {
					activeProcuracies.add(procuracy);
				}
			}
		}
		return activeProcuracies;
	}

	/**
	 * проверяет делегировали ли сотруднику указанную бизнес роль
	 *
	 * @param procuracyRef ссылка на доверенность
	 * @param businessRoleRef ссылка на бизнес роль
	 * @return true - бизнес роль делегировали, false вы противном случае
	 */
	private boolean hasTrustedBusinessRole(final NodeRef procuracyRef, final NodeRef businessRoleRef) {
		boolean hasTrustedBusinessRole = false;
		if (isProperType(procuracyRef, IDelegation.TYPE_PROCURACY)) {
			NodeRef candidateRef = findNodeByAssociationRef(procuracyRef, IDelegation.ASSOC_PROCURACY_BUSINESS_ROLE, OrgstructureBean.TYPE_BUSINESS_ROLE, ASSOCIATION_TYPE.TARGET);
			if (candidateRef != null) {
				hasTrustedBusinessRole = candidateRef.equals(businessRoleRef);
			}
		}
		return hasTrustedBusinessRole;
	}

	/**
	 * получает список босов, которые делегировали указанному employeeRef свои
	 * права руководителя (активые делегирования)
	 *
	 * @param employeeRef
	 * @return список босов, которые делегировали
	 */
	private List<NodeRef> getBosses(final NodeRef employeeRef) {
		List<NodeRef> bossRefs = new ArrayList<NodeRef>();
        if (hasAccessToOrgElement(employeeRef)) {
            List<NodeRef> delegationOptsList = findNodesByAssociationRef(employeeRef, IDelegation.ASSOC_DELEGATION_OPTS_TRUSTEE, IDelegation.TYPE_DELEGATION_OPTS, ASSOCIATION_TYPE.SOURCE);
            for (NodeRef delegationOpts : delegationOptsList) {
                if (isActiveDelegationOpts(delegationOpts)) {
                    NodeRef owner = findNodeByAssociationRef(delegationOpts, IDelegation.ASSOC_DELEGATION_OPTS_OWNER, TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
                    if (isBoss(owner, false)) {
                        bossRefs.add(owner);
                    }
                }
            }
        }
		return bossRefs;
	}

	private boolean isActiveDelegationOpts(NodeRef delegationOpts) {
		if (isProperType(delegationOpts, IDelegation.TYPE_DELEGATION_OPTS)) {
			return (Boolean) nodeService.getProperty(delegationOpts, IS_ACTIVE);
		}

		return false;
	}

	@Override
	public boolean isBoss(final NodeRef nodeRef) {
		return isBoss(nodeRef, false);
	}

	@Override
	public boolean isBoss(final NodeRef nodeRef, final boolean withDelegation) {
		boolean isBoss = isBossInternal(nodeRef);
		if (withDelegation) {
			isBoss = isBoss || !getBosses(nodeRef).isEmpty();
		}
		return isBoss;
	}

	@Override
	public boolean hasSubordinate(NodeRef bossRef, NodeRef subordinateRef) {
		return hasSubordinate(bossRef, subordinateRef, false);
	}

	@Override
	public boolean hasSubordinate(NodeRef bossRef, NodeRef subordinateRef, boolean withDelegation) {
		boolean hasSubordinate = hasSubordinateInternal(bossRef, subordinateRef);
		if (withDelegation) {
			boolean hasDelegatedSubordinate = false;
			List<NodeRef> bosses = getBosses(bossRef);
			for (NodeRef boss : bosses) {
				hasDelegatedSubordinate = hasSubordinateInternal(boss, subordinateRef);
				if (hasDelegatedSubordinate) {
					break;
				}
			}
			hasSubordinate = hasSubordinate || hasDelegatedSubordinate;
		}
		return hasSubordinate;
	}

	@Override
	public boolean isEmployeeHasBusinessRole(NodeRef employeeRef, String businessRoleIdentifier) {
		return isEmployeeHasBusinessRole(employeeRef, businessRoleIdentifier, false);
	}

	@Override
    public boolean isEmployeeHasBusinessRole(NodeRef employeeRef, String businessRoleIdentifier, boolean withDelegation) {
        return isEmployeeHasBusinessRole(employeeRef,businessRoleIdentifier, withDelegation, false);
    }

	@Override
	public boolean isEmployeeHasBusinessRole(NodeRef employeeRef, String businessRoleIdentifier, boolean withDelegation, boolean inheritSubordinatesRoles) {
		return isEmployeeHasBusinessRole(employeeRef, businessRoleIdentifier, withDelegation, inheritSubordinatesRoles, true);
	}

	private boolean isEmployeeHasBusinessRole(final NodeRef employeeRef, String businessRoleIdentifier, boolean withDelegation, boolean inheritSubordinatesRoles, boolean checkAccess) {
		boolean result;
		if (withDelegation && inheritSubordinatesRoles) {
			Set<String> auth = AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Set<String>>() {
				@Override
				public Set<String> doWork() throws Exception {
					return serviceRegistry.getAuthorityService().getAuthoritiesForUser(getEmployeeLogin(employeeRef));
				}
			});

			result = auth.contains("GROUP__LECM$BR!" + businessRoleIdentifier);
		} else {
			List<NodeRef> allEmployeeBusinessRoles = getEmployeeRoles(employeeRef, withDelegation, inheritSubordinatesRoles, checkAccess);
			NodeRef businessRole = getBusinessRoleByIdentifier(businessRoleIdentifier);

			result = businessRole != null && allEmployeeBusinessRoles.contains(businessRole);
		}
		return result;
	}

	@Override
	public List<NodeRef> getNodeRefEmployees(NodeRef nodeRef) {
		List<NodeRef> absences = new ArrayList<NodeRef>();

		if (this.isEmployee(nodeRef) && hasAccessToOrgElement(nodeRef)) {
			absences.add(nodeRef);
		} else if (this.isStaffList(nodeRef) || this.isWorkForce(nodeRef)) {
			logger.info("getNodeRefEmployees: this.isStaffList(nodeRef) || this.isWorkForce(nodeRef) ");
			if (!isArchive(nodeRef)) {
				NodeRef employee = this.getEmployeeByPosition(nodeRef);
				if (employee != null && !isArchive(employee)) {
					absences.add(employee);
				}
			}
		} else if (this.isUnit(nodeRef)) {
			final List<NodeRef> organizationElementEmployees = this.getOrganizationElementEmployees(nodeRef);
			for (NodeRef employee : organizationElementEmployees) {
				absences.add(employee);
			}
		}

		return absences;
	}

	@Override
	public NodeRef getServiceRootFolder() {
            return getFolder(ORGANIZATION_ROOT_ID);
	}

	@Override
	public NodeRef getPrimaryOrgUnit(NodeRef employeeRef) {
		NodeRef unit = null;
		if (nodeService.exists(employeeRef)) {
			if (isEmployee(employeeRef)) {
				// получаем основную должностную позицию
				NodeRef primaryStaff = getEmployeePrimaryStaff(employeeRef);
				if (primaryStaff != null) {
					// получаем подразделение
					unit = getUnitByStaff(primaryStaff);
				}
			}
		}
		return unit;
	}

	@Override
	public boolean isBossOf(NodeRef bossRef, NodeRef subordinateRef, boolean checkPrimary) {
		NodeRef unit;
		Set<NodeRef> employees = new HashSet<NodeRef>();

		if (checkPrimary) {
			if (nodeService.exists(bossRef)) {
				if (isEmployee(bossRef)) {
					//проверка является ли боссом по основной должностной позиции
					if (isBoss(bossRef)) {
						// получаем подразделение где сотрудник числится на основной должностной позиции
						unit = getPrimaryOrgUnit(bossRef);
						if (unit != null) {
							//берем сотрудников из непосредственно этого подразделения
							employees.addAll(getOrganizationElementEmployees(unit));
							//берем все дочерние подразделения и собираем сотрудников уже из них
							List<NodeRef> subUnits = getSubUnits(unit, true, true);
							for (NodeRef subUnitRef : subUnits) {
								employees.addAll(getOrganizationElementEmployees(subUnitRef));
							}
							//начальника выгоняем из множества сотрудников
							employees.remove(bossRef);
						}
					}
				}
			}
			return employees.contains(subordinateRef);
		} else {
			return hasSubordinate(bossRef, subordinateRef);
		}
	}

	@Override
	public List<NodeRef> getAllEmployees() {
		return getAllEmployees(false);
	}

    private List<NodeRef> getAllEmployees(boolean doNotCheckAccess) {
		List<NodeRef> employees = new ArrayList<NodeRef>();

		NodeRef organizationRef = getOrganization();
		NodeRef employeesRef = nodeService.getChildByName(organizationRef, ContentModel.ASSOC_CONTAINS, EMPLOYEES_ROOT_NAME);
		if (employeesRef != null) {
			for (ChildAssociationRef child : nodeService.getChildAssocs(employeesRef)) {
                if (doNotCheckAccess || hasAccessToOrgElement(child.getChildRef())) {
                    employees.add(child.getChildRef());
                }
			}
		}

		return employees;
	}

	@Override
	public String getOrganizationShortName() {
		Serializable shortName = nodeService.getProperty(getOrganization(), PROP_ORG_ELEMENT_SHORT_NAME);
		return shortName != null ? (String) shortName : null;
	}

	@Override
	public String getOrganizationFullName() {
		Serializable fullName = nodeService.getProperty(getOrganization(), PROP_ORG_ELEMENT_FULL_NAME);
		return fullName != null ? (String) fullName : null;
	}

	@Override
	public String getOrgstructureUnitAuthority(NodeRef unit, boolean shared) {
		if (shared) {
			return "GROUP_" + PolicyUtils.makeOrgUnitPos(unit, nodeService).getAlfrescoSuffix();
		} else {
			return "GROUP_" + PolicyUtils.makeOrgUnitPrivatePos(unit, nodeService).getAlfrescoSuffix();
		}
	}

	@Override
	public boolean isDynamicBusinessRole(NodeRef roleRef) {
		return roleRef != null && ((Boolean) nodeService.getProperty(roleRef, PROP_BUSINESS_ROLE_IS_DYNAMIC));
	}

	@Override
	public String getBusinessRoleIdentifier(NodeRef roleRef) {
		return (String) nodeService.getProperty(roleRef, PROP_BUSINESS_ROLE_IDENTIFIER);
	}

    @Override
    public NodeRef getEmployeeOrganization(NodeRef employee) {
        NodeRef organization = null;
        String userName = getEmployeeLogin(employee);
        if (userName != null) {
            if (getUserOrganizationsCache().contains(userName)) {
                organization = getUserOrganizationsCache().get(userName);
            } else {
                if (nodeService.hasAspect(employee, OrgstructureAspectsModel.ASPECT_HAS_LINKED_ORGANIZATION)) {
                    List<AssociationRef> orgAssoc = nodeService.getTargetAssocs(employee, OrgstructureAspectsModel.ASSOC_LINKED_ORGANIZATION);
                    if (orgAssoc != null && orgAssoc.size() > 0) {
                        organization = orgAssoc.get(0).getTargetRef();
                    }
                }
                getUserOrganizationsCache().put(userName, organization);
            }
        }
        return organization;
    }

    @Override
    public NodeRef getUserOrganization(String userName) {
        NodeRef organization = null;
        if (userName != null) {
            if (getUserOrganizationsCache().contains(userName)) {
                organization = getUserOrganizationsCache().get(userName);
            } else {
                NodeRef employee = getEmployeeByPerson(userName);
                if (employee != null) {
                    if (nodeService.hasAspect(employee, OrgstructureAspectsModel.ASPECT_HAS_LINKED_ORGANIZATION)) {
                        List<AssociationRef> orgAssoc = nodeService.getTargetAssocs(employee, OrgstructureAspectsModel.ASSOC_LINKED_ORGANIZATION);
                        if (orgAssoc != null && orgAssoc.size() > 0) {
                            organization = orgAssoc.get(0).getTargetRef();
                        }
                    }
                    getUserOrganizationsCache().put(userName, organization);
                }
            }
        }
        return organization;
    }

    /**
     * Копия предыдущего метода, но для внутреннего использования - когда сотрудник заранее известен,
     * чтобы не вызывать повторно ru.it.lecm.orgstructure.beans.OrgstructureBeanImpl#getEmployeeByPerson(java.lang.String, boolean)
     * @param userName логин
     * @param employee сотрудник (должен соответствовать переданному логину)
     * @return организация
     */
    private NodeRef getUserOrganization(String userName, NodeRef employee) {
        NodeRef organization = null;
        if (userName != null) {
            if (getUserOrganizationsCache().contains(userName)) {
                organization = getUserOrganizationsCache().get(userName);
            } else {
                if (employee != null) {
                    if (nodeService.hasAspect(employee, OrgstructureAspectsModel.ASPECT_HAS_LINKED_ORGANIZATION)) {
                        List<AssociationRef> orgAssoc = nodeService.getTargetAssocs(employee, OrgstructureAspectsModel.ASSOC_LINKED_ORGANIZATION);
                        if (orgAssoc != null && orgAssoc.size() > 0) {
                            organization = orgAssoc.get(0).getTargetRef();
                        }
                    }
                    getUserOrganizationsCache().put(userName, organization);
                }
            }
        }
        return organization;
    }

    @Override
    public NodeRef getOrganization(NodeRef orgElement) {
        NodeRef organization = null;
        if (orgElement != null) {
            QName type = nodeService.getType(orgElement);
            if (type.equals(TYPE_EMPLOYEE)){
                return getEmployeeOrganization(orgElement);
            } else {
                if (nodeService.hasAspect(orgElement, OrgstructureAspectsModel.ASPECT_HAS_LINKED_ORGANIZATION)) {
                    List<AssociationRef> contractorAssoc = nodeService.getTargetAssocs(orgElement, OrgstructureAspectsModel.ASSOC_LINKED_ORGANIZATION);
                    if (!contractorAssoc.isEmpty()) {
                        organization = contractorAssoc.get(0).getTargetRef();
                    }
                }
            }
        }
        return organization;
    }

	private NodeRef getOrg(NodeRef unit, NodeRef rootUnit) {
		ChildAssociationRef parent = nodeService.getPrimaryParent(unit);
		if (parent != null) {
			if (rootUnit.equals(parent.getParentRef())) {
				return unit;
			} else {
				return getOrg(parent.getParentRef(), rootUnit);
			}
		} else {
			return null;
		}
	}

	@Override
	public NodeRef getUnitByOrganization(NodeRef organization) {

		SearchService searchService = serviceRegistry.getSearchService();
		SearchParameters sp = new SearchParameters();
		sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);
		String path = "/app:company_home/cm:Business_x0020_platform/cm:LECM/cm:Сервис_x0020_Структура_x0020_организации_x0020_и_x0020_сотрудников/cm:Организация/cm:Структура//*";
		String type = "lecm-orgstr:organization-unit";
		sp.setQuery(String.format("+PATH:\"%s\" AND TYPE:\"%s\" AND @lecm\\-orgstr\\-aspects\\:linked\\-organization\\-assoc\\-ref:\"%s\" AND NOT @lecm\\-dic\\:active:false", path, type, organization.toString()));
		ResultSet results = null;

		try {
			results = searchService.query(sp);
			if (results != null && results.length() > 0) {
				return results.getNodeRef(0);
			}
		} finally {
			if (results != null) {
				results.close();
			}
		}

		return null;
	}

    @Override
    public boolean hasOrgChilds(NodeRef unit, boolean checkAccess) {
        Set<QName> units = new HashSet<QName>();
        units.add(OrgstructureBean.TYPE_ORGANIZATION_UNIT);

        List<ChildAssociationRef> uRefs = nodeService.getChildAssocs(unit, units);
        for (ChildAssociationRef uRef : uRefs) {
            if (!isArchive(uRef.getChildRef())) {
                if (!checkAccess || hasAccessToOrgElement(uRef.getChildRef())) {
                    return true;
                }
            }
        }

        // Получаем список штатных расписаний
        List<NodeRef> staffs = getUnitStaffLists(unit, false);
        for (NodeRef staff : staffs) {
            if (!isArchive(staff)) {
                NodeRef employee = getEmployeeByPosition(staff, false);
                if (employee != null && !isArchive(employee)) {
                    if (!checkAccess || hasAccessToOrgElement(employee)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

	public List<NodeRef> getCurrentEmployeeHighestUnits() {
		List<NodeRef> result = new ArrayList<>();
		NodeRef employee = getCurrentEmployee();
		List<NodeRef> employeeUnits = getEmployeeUnits(employee, false, true, false);

		for (NodeRef employeeUnit : employeeUnits) {
			NodeRef parent = nodeService.getPrimaryParent(employeeUnit).getParentRef();
			NodeRef potentialOrganizationsRoot = nodeService.getPrimaryParent(parent).getParentRef();
			if(TYPE_STRUCTURE.equals(nodeService.getType(potentialOrganizationsRoot))) {
				result.add(employeeUnit);
			}
		}

		return result;
	}

    public boolean hasGlobalOrganizationsAccess() {
        Set<String> auth = authorityService.getAuthoritiesForUser(AuthenticationUtil.getFullyAuthenticatedUser());
        return auth.contains("GROUP_LECM_GLOBAL_ORGANIZATIONS_ACCESS");
    }
	
	@Override
	public void autoEmployeesTie() {
		orgSGNotifier.autoTieAllEmployeers();
	}
}
