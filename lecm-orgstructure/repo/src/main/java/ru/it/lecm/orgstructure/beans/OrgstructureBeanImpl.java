package ru.it.lecm.orgstructure.beans;

import java.io.Serializable;
import java.util.*;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.dictionary.beans.DictionaryBean;

/**
 * @author dbashmakov
 *         Date: 27.11.12
 *         Time: 17:08
 */
public class OrgstructureBeanImpl extends BaseBean implements OrgstructureBean {

	private ServiceRegistry serviceRegistry;
	private Repository repositoryHelper;
	private AuthenticationService authService;
	private PersonService personService;
	private DictionaryBean dictionaryService;

	private final Object lock = new Object();

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public void setRepositoryHelper(Repository repositoryHelper) {
		this.repositoryHelper = repositoryHelper;
	}

	public void setAuthService(AuthenticationService authService) {
		this.authService = authService;
	}

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	public void setDictionaryService (DictionaryBean dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	@Override
	public NodeRef getOrganizationRootRef() {
		repositoryHelper.init();
		final NodeRef companyHome = repositoryHelper.getCompanyHome();
		return nodeService.getChildByName(companyHome, ContentModel.ASSOC_CONTAINS, ORGANIZATION_ROOT_NAME);
	}

	@Override
	public NodeRef ensureOrganizationRootRef() {
		final String rootName = ORGANIZATION_ROOT_NAME;
		repositoryHelper.init();
		final NodeRef companyHome = repositoryHelper.getCompanyHome();
		AuthenticationUtil.RunAsWork<NodeRef> raw = new AuthenticationUtil.RunAsWork<NodeRef>() {
			@Override
			public NodeRef doWork() throws Exception {
				return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
					@Override
					public NodeRef execute() throws Throwable {
						NodeRef organizationRef;
						synchronized (lock) {
							// еще раз пытаемся получить директорию (на случай если она уже была создана другим потоком
							organizationRef = nodeService.getChildByName(companyHome, ContentModel.ASSOC_CONTAINS, rootName);
							if (organizationRef == null) {
								QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
								QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, rootName);
								QName nodeTypeQName = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, TYPE_ORGANIZATION);

								Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1); //optional map of properties to keyed by their qualified names
								properties.put(ContentModel.PROP_NAME, rootName);
								ChildAssociationRef associationRef = nodeService.createNode(companyHome, assocTypeQName, assocQName, nodeTypeQName, properties);

								/**
								 Структура директорий
								 Организация
								 ---Структура
								 ---Сотрудники
								 ---Персональные данные
								 */
								organizationRef = associationRef.getChildRef();
								// Структура
								assocTypeQName = ContentModel.ASSOC_CONTAINS;
								assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, STRUCTURE_ROOT_NAME);
								nodeTypeQName = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, TYPE_DIRECTORY_STRUCTURE);
								properties = new HashMap<QName, Serializable>(1); //optional map of properties to keyed by their qualified names
								properties.put(ContentModel.PROP_NAME, STRUCTURE_ROOT_NAME);
								nodeService.createNode(organizationRef, assocTypeQName, assocQName, nodeTypeQName, properties);
								// Сотрудники
								assocTypeQName = ContentModel.ASSOC_CONTAINS;
								assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, EMPLOYEES_ROOT_NAME);
								nodeTypeQName = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, TYPE_DIRECTORY_EMPLOYEES);
								properties = new HashMap<QName, Serializable>(1); //optional map of properties to keyed by their qualified names
								properties.put(ContentModel.PROP_NAME, EMPLOYEES_ROOT_NAME);
								nodeService.createNode(organizationRef, assocTypeQName, assocQName, nodeTypeQName, properties);
								// Персональные данные
								assocTypeQName = ContentModel.ASSOC_CONTAINS;
								assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, PERSONAL_DATA_ROOT_NAME);
								nodeTypeQName = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, TYPE_DIRECTORY_PERSONAL_DATA);
								properties = new HashMap<QName, Serializable>(1); //optional map of properties to keyed by their qualified names
								properties.put(ContentModel.PROP_NAME, PERSONAL_DATA_ROOT_NAME);
								nodeService.createNode(organizationRef, assocTypeQName, assocQName, nodeTypeQName, properties);
							}
						}
						return organizationRef;
					}
				});
			}
		};
		return AuthenticationUtil.runAsSystem(raw);
	}

	@Override
	public NodeRef getOrganizationBoss() {
		NodeRef bossRef = null;
		NodeRef organization = getOrganizationRootRef();
		if (organization != null) {
			List<AssociationRef> boss = nodeService.getTargetAssocs(organization, ASSOC_ORG_BOSS);
			if (boss != null && boss.size() > 0) {
				bossRef = boss.get(0).getTargetRef();
			}
		}
		return bossRef;
	}

	@Override
	public NodeRef getOrganizationLogo() {
		NodeRef logoRef = null;
		NodeRef organization = getOrganizationRootRef();
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
		NodeRef organization = getOrganizationRootRef();
		if (organization != null) {
			structure = nodeService.getChildByName(organization, ContentModel.ASSOC_CONTAINS, STRUCTURE_ROOT_NAME);
		}
		return structure;
	}

	@Override
	public NodeRef getEmployeesDirectory() {
		NodeRef emp = null;
		NodeRef organization = getOrganizationRootRef();
		if (organization != null) {
			emp = nodeService.getChildByName(organization, ContentModel.ASSOC_CONTAINS, EMPLOYEES_ROOT_NAME);
		}
		return emp;
	}

	@Override
	public NodeRef getPersonalDataDirectory() {
		NodeRef pd = null;
		NodeRef organization = getOrganizationRootRef();
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
		List<NodeRef> results = new ArrayList<NodeRef>();
		Set<QName> units = new HashSet<QName>();
		units.add(TYPE_ORGANIZATION_UNIT);

		List<ChildAssociationRef> uRefs = nodeService.getChildAssocs(parent, units);
		for (ChildAssociationRef uRef : uRefs) {
			if (!onlyActive || !isArchive(uRef.getChildRef())) {
				results.add(uRef.getChildRef());
				if (includeSubunits) {
					results.addAll(getSubUnits(uRef.getChildRef(), onlyActive, includeSubunits));
				}
			}
		}
		return results;
	}

	@Override
	public boolean hasChild(NodeRef parent, boolean onlyActive) {
		List<NodeRef> childs = getSubUnits(parent, onlyActive);
		boolean hasChild = !childs.isEmpty();
		if (onlyActive && hasChild) {
			hasChild = false;
			for (NodeRef ref : childs) {
				Boolean isActive = (Boolean) nodeService.getProperty(ref, IS_ACTIVE);
				isActive = isActive != null ? isActive : Boolean.TRUE; // if property not filled -> active = true default
				if (isActive) {
					hasChild = isActive; // if one active exist -> hasChild == true
					break;
				}
			}
		}
		return hasChild;
	}

	@Override
	public NodeRef getParent(NodeRef unitRef) {
		ChildAssociationRef parentRef = nodeService.getPrimaryParent(unitRef);
		if (parentRef != null) {
			NodeRef parent = parentRef.getParentRef();
			if (isUnit(parent)) {
				return parent;
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
		types.add(TYPE_POSITION);
		return isProperType(ref, types);
	}

    @Override
    public boolean isCurrentBoss() {
        NodeRef employeeRef = getCurrentEmployee();

        if (employeeRef != null && nodeService.exists(employeeRef)) {
            if (isEmployee(employeeRef)) {
                // получаем основную должностную позицию
                NodeRef primaryStaff = getEmployeePrimaryStaff(employeeRef);
                if (primaryStaff != null) {
                    // получаем подразделение для штатного расписания
                    NodeRef unit = getUnitByStaff(primaryStaff);
                    // получаем руководителя для подразделения
                    NodeRef bossRef = getUnitBoss(unit);
                    return employeeRef.equals(bossRef);
                }
            }
        }
        return false;
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
				NodeRef parent = getParent(unitRef);
				if (parent != null) {
					bossLink = getUnitBoss(parent);
				} else {
					// дошли до директории Структура, пробуем получить руководителя Организации
					bossLink = getOrganizationBoss();
				}

			}
		}
		return bossLink;
	}

	@Override
	public NodeRef getBossStaff(NodeRef unitRef) {
		// Получаем список штатных расписаний
		List<NodeRef> staffs = getUnitStaffLists(unitRef);
		// находим то, которое помечено как руководящая позиция
		NodeRef bossStaff = null;
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
					while (bossRef.equals(employeeRef) && ((unit = getParent(unit)) != null)) {
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
		List<NodeRef> results = new ArrayList<NodeRef>();
		if (isUnit(unitRef)) {
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
	public NodeRef getEmployeeByPosition(NodeRef positionRef) {
		NodeRef employeeLink = getEmployeeLinkByPosition(positionRef);
		if (employeeLink != null && !isArchive(employeeLink)){
			return getEmployeeByLink(employeeLink);
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
				if (employee != null) {
					result.add(employee);
				}
			}
		}
		return new ArrayList<NodeRef>(result);
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
		repositoryHelper.init();
		final NodeRef companyHome = repositoryHelper.getCompanyHome();
		NodeRef dictionariesRoot = nodeService.getChildByName(companyHome, ContentModel.ASSOC_CONTAINS, OrgstructureBean.DICTIONARIES_ROOT_NAME);
		NodeRef positionsRoot = nodeService.getChildByName(dictionariesRoot, ContentModel.ASSOC_CONTAINS, POSITIONS_DICTIONARY_NAME);

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
                if (!isArchive(staff.getSourceRef())){
                    Set<QName> links = new HashSet<QName>();
                    links.add(TYPE_EMPLOYEE_LINK);
                    // из штатного расписания получает ссылку на сотрудника
                    List<ChildAssociationRef> empLinks = nodeService.getChildAssocs(staff.getSourceRef(), links);
                    if (empLinks.size() > 0) { // сотрудник задан -> по ссылке получаем сотрудника
                        NodeRef employee = getEmployeeByLink(empLinks.get(0).getChildRef());
                        results.add(employee);
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
                if (!isArchive(wf.getChildRef())){
                    Set<QName> links = new HashSet<QName>();
                    links.add(TYPE_EMPLOYEE_LINK);
                    //получает ссылку на сотрудника
                    List<ChildAssociationRef> empLinks = nodeService.getChildAssocs(wf.getChildRef(), links);
                    if (empLinks.size() > 0) { // сотрудник задан -> по ссылке получаем сотрудника
                        NodeRef employee = getEmployeeByLink(empLinks.get(0).getChildRef());
                        results.add(employee);
                    }
                }
			}
		}
		return results;
	}

	@Override
	public List<NodeRef> getOrganizationElementEmployees(NodeRef organizationElement) {
		Set<NodeRef> results = new HashSet<NodeRef>();
		if (isWorkGroup(organizationElement) || isUnit(organizationElement)) { // если рабочая группа
			// получаем участников для рабочей группы
			Set<QName> workforces = new HashSet<QName>();
			workforces.add(TYPE_WORKFORCE);
			workforces.add(TYPE_STAFF_LIST);
			List<ChildAssociationRef> orgElementMembers = nodeService.getChildAssocs(organizationElement, workforces);
			for (ChildAssociationRef wf : orgElementMembers) {
                if (!isArchive(wf.getChildRef())){
                    Set<QName> links = new HashSet<QName>();
                    links.add(TYPE_EMPLOYEE_LINK);
                    //получает ссылку на сотрудника
                    List<ChildAssociationRef> empLinks = nodeService.getChildAssocs(wf.getChildRef(), links);
                    if (empLinks.size() > 0) { // сотрудник задан -> по ссылке получаем сотрудника
                        NodeRef employee = getEmployeeByLink(empLinks.get(0).getChildRef());
	                    if (!isArchive(employee)) {
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
		if (isEmployee(employeeRef)) {
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
                if(!isArchive(ref)){
                    staff = ref;
                }
			}
		}
		return staff;
	}

	@Override
	public NodeRef getUnitByStaff(NodeRef staffRef) {
		NodeRef unitRef = null;
		if (isStaffList(staffRef)) {
			unitRef = getParent(staffRef);
		}
		return unitRef;
	}

	@Override
	public NodeRef getEmployeePhoto(NodeRef employeeRef) {
		NodeRef photoRef = null;
		if (isEmployee(employeeRef)) {
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
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_STAFF_LIST);
		return getEmployeePositions(employeeRef, types);
	}

	@Override
	public List<NodeRef> getEmployeeWorkForces(NodeRef employeeRef) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_WORKFORCE);
		return getEmployeePositions(employeeRef, types);
	}

	private List<NodeRef> getEmployeePositions(NodeRef employeeRef, Set<QName> types) {
		List<NodeRef> positions = new ArrayList<NodeRef>();
		if (isEmployee(employeeRef)) {
			List<AssociationRef> links = nodeService.getSourceAssocs(employeeRef, ASSOC_EMPLOYEE_LINK_EMPLOYEE);
			for (AssociationRef link : links) {
				if (!isArchive(link.getSourceRef())){
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
		List<NodeRef> wGroups = new ArrayList<NodeRef>();
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_WORKFORCE);
		// получаем список объектов Участник рабочей группы
		List<NodeRef> workForces = getEmployeePositions(employeeRef, types);
		for (NodeRef workForce : workForces) {
			NodeRef group = getWorkGroupByWorkForce(workForce);
			if (group != null) {
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
		repositoryHelper.init();
		final NodeRef companyHome = repositoryHelper.getCompanyHome();
		NodeRef dictionariesRoot = nodeService.getChildByName(companyHome, ContentModel.ASSOC_CONTAINS, OrgstructureBean.DICTIONARIES_ROOT_NAME);
		NodeRef positionsRoot = nodeService.getChildByName(dictionariesRoot, ContentModel.ASSOC_CONTAINS, ROLES_DICTIONARY_NAME);

		Set<QName> positions = new HashSet<QName>();
		positions.add(TYPE_WORK_ROLE);

		List<ChildAssociationRef> workRoles = nodeService.getChildAssocs(positionsRoot, positions);
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
                if(!isArchive(empRef)){
                    employeeLink = empRef;
                }
			}
		}
		return employeeLink;
	}

	@Override
	public List<NodeRef> getBusinesRoles(boolean onlyActive) {
		List<NodeRef> results = new ArrayList<NodeRef>();
		repositoryHelper.init();
		final NodeRef companyHome = repositoryHelper.getCompanyHome();
		NodeRef dictionariesRoot = nodeService.getChildByName(companyHome, ContentModel.ASSOC_CONTAINS, OrgstructureBean.DICTIONARIES_ROOT_NAME);
		NodeRef rolesRoot = nodeService.getChildByName(dictionariesRoot, ContentModel.ASSOC_CONTAINS, BUSINESS_ROLES_DICTIONARY_NAME);

		Set<QName> roles = new HashSet<QName>();
		roles.add(TYPE_BUSINESS_ROLE);

		List<ChildAssociationRef> businessRoles = nodeService.getChildAssocs(rolesRoot, roles);
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

	@Override
	public List<NodeRef> getEmployeesByBusinessRole(NodeRef businessRoleRef) {
		Set<NodeRef> results = new HashSet<NodeRef>();
		if (isBusinessRole(businessRoleRef)) { // если бизнес роль
			// получаем сотрудников
			// напрямую имеющих роль
			List<AssociationRef> employees = nodeService.getTargetAssocs(businessRoleRef, ASSOC_BUSINESS_ROLE_EMPLOYEE);
			for (AssociationRef empChildRef : employees) {
				if (!isArchive(empChildRef.getTargetRef())){
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
					if (!isArchive(orgElementChildRef.getTargetRef())){
						NodeRef employeeByPosition = getEmployeeByPosition(orgElementChildRef.getTargetRef());
						if (!isArchive(employeeByPosition)) {
							results1.add(employeeByPosition);
						}
					}
				}
			}
			results.addAll(new ArrayList<NodeRef>(results1));
		}
		return new ArrayList<NodeRef>(results);
	}


	@Override
	public List<NodeRef> getOrganizationElementsByBusinessRole(NodeRef businessRoleRef) {
		List<NodeRef> results = new ArrayList<NodeRef>();
		if (isBusinessRole(businessRoleRef)) { // если бизнес роль
			// получаем организационные элементы (подразделения и рабочие группы)
			List<AssociationRef> orgElements = nodeService.getTargetAssocs(businessRoleRef, ASSOC_BUSINESS_ROLE_ORGANIZATION_ELEMENT);
			for (AssociationRef orgElementChildRef : orgElements) {
				if (!isArchive(orgElementChildRef.getTargetRef())){
					results.add(orgElementChildRef.getTargetRef());
					results.addAll(getSubUnits(orgElementChildRef.getTargetRef(), true, true));
				}
			}
		}
		return results;
	}


	@Override
	public List<NodeRef> getEmployeeLinks(NodeRef employeeRef) {
		List<NodeRef> links = new ArrayList<NodeRef>();
		if (isEmployee(employeeRef)) {
			List<AssociationRef> lRefs = nodeService.getSourceAssocs(employeeRef, ASSOC_EMPLOYEE_LINK_EMPLOYEE);
			for (AssociationRef lRef : lRefs) {
				if (!isArchive(lRef.getSourceRef())) {
				   links.add(lRef.getSourceRef());
				}
			}
		}
		return links;
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
		String username = authService.getCurrentUserName();
		return getEmployeeByPerson(username);
	}

	@Override
	public NodeRef getEmployeeByPerson(String personName) {
		if (personName != null) {
			NodeRef personNodeRef = personService.getPerson(personName, false);
			if (personNodeRef != null) {
				return getEmployeeByPerson(personNodeRef);
			}
		}
		return null;
	}

	@Override
	public NodeRef getEmployeeByPerson(NodeRef person) {
		List<AssociationRef> lRefs = nodeService.getSourceAssocs(person, ASSOC_EMPLOYEE_PERSON);
		for (AssociationRef lRef : lRefs) {
			if (!isArchive(lRef.getSourceRef())) {
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
	public List<NodeRef> getEmployeeUnits (final NodeRef employeeRef, final boolean bossUnitsOnly) {
		//получаем список штатных расписаний сотрудника
		List<NodeRef> staffs = getEmployeeStaffs (employeeRef);
		List<NodeRef> units = new ArrayList<NodeRef> (staffs.size ());
		for (NodeRef staffRef : staffs) {
			//для каждого штатного расписания вытаскиваем подразделение
			NodeRef unitRef = getUnitByStaff (staffRef);
			//узнаем является ли указанный сотрудник боссом по своему штатному расписанию
			Boolean isBoss = (Boolean) nodeService.getProperty(staffRef, PROP_STAFF_LIST_IS_BOSS);
			if (bossUnitsOnly && isBoss) {
				units.add (unitRef);
			} else if (!bossUnitsOnly) {
				units.add (unitRef);
			}
		}
		return units;
	}

	@Override
	public List<NodeRef> getBossSubordinate (final NodeRef employeeRef) {
		//получаем список подразделений где этот сотрудник является боссом
		Collection<NodeRef> units = getEmployeeUnits (employeeRef, true);
		Set<NodeRef> employees = new HashSet<NodeRef> ();
		for (NodeRef unitRef : units) {
			//берем сотрудников из непосредственно этого подразделения
			employees.addAll (getOrganizationElementEmployees (unitRef));
			//берем все дочерние подразделения и собираем сотрудников уже из них
			List<NodeRef> subUnits = getSubUnits (unitRef, true, true);
			for (NodeRef subUnitRef : subUnits) {
				employees.addAll (getOrganizationElementEmployees (subUnitRef));
			}
		}
		//начальника выгоняем из множества сотрудников
		employees.remove (employeeRef);
		return new ArrayList<NodeRef> (employees);
	}

	/**
	 * найти или создать связь между бизнес ролью и NodeRef-ой указанного типа
	 * @param businesssRoleRef бизнес роль у которой привязываем NodeRef
	 * @param targetRef привязываемый NodeRef
	 * @param assocName имя типа ассоциации
	 * @return AssociationRef где source это бизнес роль, target это целевой NodeRef
	 */
	private AssociationRef getOrCreateBusinessRoleAssoc (NodeRef businesssRoleRef, NodeRef targetRef, QName assocName) {
		List<AssociationRef> associationRefs = nodeService.getTargetAssocs (businesssRoleRef, assocName);
		AssociationRef targetAssoc = null;
		if (associationRefs != null) {
			for (AssociationRef associationRef : associationRefs) {
				if (associationRef.getTargetRef ().equals (targetRef)) {
					targetAssoc = associationRef;
					break;
				}
			}
		}
		if (targetAssoc == null) {
			targetAssoc = nodeService.createAssociation (businesssRoleRef, targetRef, assocName);
		}
		return targetAssoc;
	}

	@Override
	public AssociationRef includeEmployeeIntoBusinessRole (final NodeRef businesssRoleRef, final NodeRef employeeRef) {
		return getOrCreateBusinessRoleAssoc (businesssRoleRef, employeeRef, ASSOC_BUSINESS_ROLE_EMPLOYEE);
	}

	@Override
	public AssociationRef includeOrgElementIntoBusinessRole (final NodeRef businesssRoleRef, final NodeRef orgElementRef) {
		return getOrCreateBusinessRoleAssoc (businesssRoleRef, orgElementRef, ASSOC_BUSINESS_ROLE_ORGANIZATION_ELEMENT);
	}

	@Override
	public AssociationRef includeOrgElementMemberIntoBusinessRole (final NodeRef businesssRoleRef, final NodeRef orgElementMemberRef) {
		return getOrCreateBusinessRoleAssoc (businesssRoleRef, orgElementMemberRef, ASSOC_BUSINESS_ROLE_ORGANIZATION_ELEMENT_MEMBER);
	}

	/**
	 * поиск и удаление у бизнес роли ассоциации указанного типа на конкретный NodeRef
	 * @param businesssRoleRef бизнес роль у которой удаляем ассоциацию
	 * @param targetRef целевой NodeRef на который указывает удаляемая ассоциация
	 * @param assocName имя типа ассоциации
	 */
	private void findAndRemoveBusinessRoleAssoc (NodeRef businesssRoleRef, NodeRef targetRef, QName assocName) {
		List<AssociationRef> associationRefs = nodeService.getTargetAssocs (businesssRoleRef, assocName);
		if (associationRefs != null) {
			for (AssociationRef associationRef : associationRefs) {
				if (associationRef.getTargetRef ().equals (targetRef)) {
					nodeService.removeAssociation (businesssRoleRef, targetRef, assocName);
					break;
				}
			}
		}
	}

	@Override
	public void excludeEmployeeFromBusinessRole (final NodeRef businesssRoleRef, final NodeRef employeeRef) {
		findAndRemoveBusinessRoleAssoc (businesssRoleRef, employeeRef, ASSOC_BUSINESS_ROLE_EMPLOYEE);
	}

	@Override
	public void excludeOrgElementFromBusinessRole (final NodeRef businesssRoleRef, final NodeRef employeeRef) {
		findAndRemoveBusinessRoleAssoc (businesssRoleRef, employeeRef, ASSOC_BUSINESS_ROLE_ORGANIZATION_ELEMENT);
	}

	@Override
	public void excludeOrgElementMemberFromBusinesssRole (final NodeRef businesssRoleRef, final NodeRef employeeRef) {
		findAndRemoveBusinessRoleAssoc (businesssRoleRef, employeeRef, ASSOC_BUSINESS_ROLE_ORGANIZATION_ELEMENT_MEMBER);
	}

	@Override
	public NodeRef getBusinessRoleEngineer () {
		NodeRef businessRolesDictionaryRef = dictionaryService.getDictionaryByName (BUSINESS_ROLES_DICTIONARY_NAME);
		List<NodeRef> children = dictionaryService.getChildren (businessRolesDictionaryRef);
		NodeRef engineerRef = null;
		for (NodeRef child : children) {
			Serializable id = nodeService.getProperty (child, PROP_BUSINESS_ROLE_IDENTIFIER);
			if (BUSINESS_ROLE_ENGINEER_ID.equals (id.toString ())) {
				engineerRef = child;
				break;
			}
		}
		return engineerRef;
	}

	@Override
	public void fireEmployee (final NodeRef employeeRef) {
		if (isEmployee (employeeRef)) {
			RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper ();
			transactionHelper.doInTransaction (new RetryingTransactionHelper.RetryingTransactionCallback<Void> () {
				@Override
				public Void execute () throws Throwable {
					nodeService.setProperty (employeeRef, IS_ACTIVE, false);
					return null;
				}
			});
		}
	}

	@Override
	public void restoreEmployee (final NodeRef employeeRef) {
		if (isEmployee (employeeRef)) {
			RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper ();
			transactionHelper.doInTransaction (new RetryingTransactionHelper.RetryingTransactionCallback<Void> () {
				@Override
				public Void execute () throws Throwable {
					nodeService.setProperty (employeeRef, IS_ACTIVE, true);
					return null;
				}
			});
		}
	}

	@Override
	public void makeStaffBossOrEmployee (final NodeRef orgElementMemberRef, final boolean isBoss) {
		//флаг руководящей позиции актуален ТОЛЬКО для штатных расписаний
		if (isStaffList (orgElementMemberRef)) {
			RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper ();
			transactionHelper.doInTransaction (new RetryingTransactionHelper.RetryingTransactionCallback<Void> () {
				@Override
				public Void execute () throws Throwable {
					//получим отдел в котором есть это штатное расписание
					NodeRef unitRef = getUnitByStaff (orgElementMemberRef);
					//в этом отделе пытаемся найти руководящую позицию
					NodeRef bossStaffRef = getBossStaff (unitRef);
					//если руководящей позиции нет, или ее надо снять
					if (bossStaffRef == null || !isBoss) {
						nodeService.setProperty (orgElementMemberRef, PROP_STAFF_LIST_IS_BOSS, isBoss);
					}
					return null;
				}
			});
		}
	}

	@Override
	public NodeRef createStaff (final NodeRef orgElement, final NodeRef staffPosition) {
		//если переданные параметры это подразделение и должность то заводим штатное расписание
		NodeRef staffRef = null;
		if (isUnit (orgElement) && isPosition (orgElement)) {
			RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper ();
			staffRef = transactionHelper.doInTransaction (new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef> () {
				@Override
				public NodeRef execute () throws Throwable {
					QName assocQName = QName.createQName (NamespaceService.CONTENT_MODEL_1_0_URI, UUID.randomUUID ().toString ());
					ChildAssociationRef childAssociationRef = nodeService.createNode (orgElement, ContentModel.ASSOC_CONTAINS, assocQName, TYPE_STAFF_LIST);
					nodeService.createAssociation (childAssociationRef.getChildRef (), staffPosition, ASSOC_ELEMENT_MEMBER_POSITION);
					return childAssociationRef.getChildRef ();
				}
			});
		}
		return staffRef;
	}

	@Override
	public void includeEmployeeIntoStaff (final NodeRef employeeRef, final NodeRef orgElementMemberRef, final boolean isPrimary) {
		if (isEmployee (employeeRef) && isStaffList (orgElementMemberRef) && getEmployeeByPosition (orgElementMemberRef) == null) {
			RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper ();
			transactionHelper.doInTransaction (new RetryingTransactionHelper.RetryingTransactionCallback<Void> () {
				@Override
				public Void execute () throws Throwable {
					QName assocQName = QName.createQName (NamespaceService.CONTENT_MODEL_1_0_URI, UUID.randomUUID ().toString ());
					HashMap<QName, Serializable> props = new HashMap<QName, Serializable> ();
					props.put (PROP_EMP_LINK_IS_PRIMARY, isPrimary);
					NodeRef employeeLinkRef = nodeService.createNode (orgElementMemberRef, ASSOC_ELEMENT_MEMBER_EMPLOYEE, assocQName, TYPE_EMPLOYEE_LINK, props).getChildRef ();
					nodeService.createAssociation (employeeLinkRef, employeeRef, ASSOC_EMPLOYEE_LINK_EMPLOYEE);
					return null;
				}
			});
		}
	}

	@Override
	public void excludeEmployeeFromStaff (final NodeRef orgElementMemberRef) {
		if (isStaffList (orgElementMemberRef)) {
			RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper ();
			transactionHelper.doInTransaction (new RetryingTransactionHelper.RetryingTransactionCallback<Void> () {
				@Override
				public Void execute () throws Throwable {
					NodeRef employeeLinkRef = getEmployeeLinkByPosition (orgElementMemberRef);
					nodeService.removeAssociation (orgElementMemberRef, employeeLinkRef, ASSOC_ELEMENT_MEMBER_EMPLOYEE);
					nodeService.deleteNode (employeeLinkRef);
					return null;
				}
			});
		}
	}

	@Override
	public ChildAssociationRef moveOrgElement (final NodeRef unitRef, final NodeRef parentUnitRef) {
		//если родитель null то переместить в корень надо
		RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper ();
		return transactionHelper.doInTransaction (new RetryingTransactionHelper.RetryingTransactionCallback<ChildAssociationRef> () {
			@Override
			public ChildAssociationRef execute () throws Throwable {
				NodeRef parentRef;
				if (parentUnitRef == null) {
					parentRef = getStructureDirectory ();
				} else {
					parentRef = parentUnitRef;
				}
				String name = nodeService.getProperty (unitRef, ContentModel.PROP_NAME).toString ();
				QName assocQname = QName.createQName (NamespaceService.CONTENT_MODEL_1_0_URI, name);
				return nodeService.moveNode (unitRef, parentRef, ContentModel.ASSOC_CONTAINS, assocQname);
			}
		});
	}
}
