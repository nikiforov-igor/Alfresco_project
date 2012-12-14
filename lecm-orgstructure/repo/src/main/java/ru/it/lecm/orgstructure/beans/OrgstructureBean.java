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
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;

/**
 * @author dbashmakov
 *         Date: 27.11.12
 *         Time: 17:08
 */
public class OrgstructureBean {

	public static final String ORGSTRUCTURE_NAMESPACE_URI = "http://www.it.ru/lecm/org/structure/1.0";

	public static final String TYPE_ORGANIZATION = "organization";


	public static final String TYPE_DIRECTORY_EMPLOYEES = "employees";
	public static final String TYPE_DIRECTORY_STRUCTURE = "structure";
	public static final String TYPE_DIRECTORY_PERSONAL_DATA = "personal-data-container";
	/**
	 * Корневой узел Организации
	 */
	public static final String ORGANIZATION_ROOT_NAME = "Организация";
	public static final String STRUCTURE_ROOT_NAME = "Структура";
	public static final String EMPLOYEES_ROOT_NAME = "Сотрудники";
	public static final String PERSONAL_DATA_ROOT_NAME = "Персональные данные";

	public static final String DICTIONARIES_ROOT_NAME = "Dictionary";
	public static final String POSITIONS_DICTIONARY_NAME = "Должностные позиции";
	public static final String ROLES_DICTIONARY_NAME = "Роли для рабочих групп";
	public static final String BUSINESS_ROLES_DICTIONARY_NAME = "Бизнес роли";

	private ServiceRegistry serviceRegistry;
	private Repository repositoryHelper;
	private TransactionService transactionService;
	private NodeService nodeService;

	public static final QName ASSOC_ORG_BOSS = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "org-boss-assoc");
	public static final QName ASSOC_ORG_LOGO = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "org-logo-assoc");
	public static final QName ASSOC_EMPLOYEE_LINK_EMPLOYEE = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "employee-link-employee-assoc");
	public static final QName ASSOC_ELEMENT_MEMBER_POSITION = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "element-member-position-assoc");
	public static final QName ASSOC_ELEMENT_MEMBER_EMPLOYEE = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "element-member-employee-assoc");
	public static final QName ASSOC_EMPLOYEE_PHOTO = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "employee-photo-assoc");
	public static final QName ASSOC_EMPLOYEE_PERSON_DATA = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "employee-person-data-assoc");
	public static final QName ASSOC_BUSINESS_ROLE_EMPLOYEE = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "business-role-employee-assoc");

	public static final QName PROP_STAFF_LIST_IS_BOSS = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "staff-list-is-boss");
	public static final QName PROP_EMP_LINK_IS_PRIMARY = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "employee-link-is-primary");

	public static final QName IS_ACTIVE = QName.createQName("http://www.it.ru/lecm/dictionary/1.0", "active");

	public static final QName TYPE_ORGANIZATION_UNIT = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "organization-unit");
	public static final QName TYPE_STRUCTURE = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "structure");
	public static final QName TYPE_WORK_GROUP = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "workGroup");
	public static final QName TYPE_STAFF_LIST = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "staff-list");
	public static final QName TYPE_WORKFORCE = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "workforce");
	public static final QName TYPE_EMPLOYEE_LINK = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "employee-link");
	public static final QName TYPE_STAFF_POSITION = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "staffPosition");
	public static final QName TYPE_WORK_ROLE = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "workRole");
	public static final QName TYPE_EMPLOYEE = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "employee");
	public static final QName TYPE_PERSONAL_DATA = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "personal-data");
	public static final QName TYPE_BUSINESS_ROLE = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "business-role");

	private final Object lock = new Object();

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public void setRepositoryHelper(Repository repositoryHelper) {
		this.repositoryHelper = repositoryHelper;
	}

	public void setTransactionService(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	/**
	 * Получение директории Организация.
	 * Если такой узел отсутствует - он НЕ создаётся.
	 */
	public NodeRef getOrganizationRootRef() {
		repositoryHelper.init();
		final NodeRef companyHome = repositoryHelper.getCompanyHome();
		return nodeService.getChildByName(companyHome, ContentModel.ASSOC_CONTAINS, ORGANIZATION_ROOT_NAME);
	}

	/**
	 * Получение узла Организация, в котором хрянится информация об Организации.
	 * Если такой узел отсутствует - он создаётся автоматически (внутри /CompanyHome).
	 *
	 * @return NodeRef
	 */
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

	/**
	 * Получение руководителя Организации
	 *
	 * @return NodeRef или NULL если руководитель не задан
	 */
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

	/**
	 * Получение логотипа Организации
	 *
	 * @return NodeRef или NULL если логотип не задан
	 */
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

	/**
	 * Получение Директории с Оргструктурой
	 *
	 * @return NodeRef или NULL
	 */
	public NodeRef getStructureDirectory() {
		NodeRef structure = null;
		NodeRef organization = getOrganizationRootRef();
		if (organization != null) {
			structure = nodeService.getChildByName(organization, ContentModel.ASSOC_CONTAINS, STRUCTURE_ROOT_NAME);
		}
		return structure;
	}

	/**
	 * Получение Директории с Сотрудниками
	 *
	 * @return NodeRef или NULL
	 */
	public NodeRef getEmployeesDirectory() {
		NodeRef emp = null;
		NodeRef organization = getOrganizationRootRef();
		if (organization != null) {
			emp = nodeService.getChildByName(organization, ContentModel.ASSOC_CONTAINS, EMPLOYEES_ROOT_NAME);
		}
		return emp;
	}

	/**
	 * Получение Директории с Персональными данными
	 *
	 * @return NodeRef или NULL
	 */
	public NodeRef getPersonalDataDirectory() {
		NodeRef pd = null;
		NodeRef organization = getOrganizationRootRef();
		if (organization != null) {
			pd = nodeService.getChildByName(organization, ContentModel.ASSOC_CONTAINS, PERSONAL_DATA_ROOT_NAME);
		}
		return pd;
	}

	/**
	 * Получение списка Рабочих групп для Организации
	 */
	public List<NodeRef> getWorkGroups(boolean onlyActive) {
		List<NodeRef> results = new ArrayList<NodeRef>();
		NodeRef structureDirectory = getStructureDirectory();
		if (structureDirectory != null) {
			Set<QName> workgroups = new HashSet<QName>();
			workgroups.add(TYPE_WORK_GROUP);
			List<ChildAssociationRef> wgs = nodeService.getChildAssocs(structureDirectory, workgroups);
			for (ChildAssociationRef wg : wgs) {
				if (onlyActive) {
					if ((Boolean) nodeService.getProperty(wg.getChildRef(), IS_ACTIVE)) {
						results.add(wg.getChildRef());
					}
				} else {
					results.add(wg.getChildRef());
				}
			}
		}
		return results;
	}

	/**
	 * Получение списка дочерних подразделений
	 */
	public List<NodeRef> getSubUnits(NodeRef parent, boolean onlyActive) {
		List<NodeRef> results = new ArrayList<NodeRef>();
		Set<QName> units = new HashSet<QName>();
		units.add(TYPE_ORGANIZATION_UNIT);

		List<ChildAssociationRef> uRefs = nodeService.getChildAssocs(parent, units);
		for (ChildAssociationRef uRef : uRefs) {
			if (onlyActive) {
				if ((Boolean) nodeService.getProperty(uRef.getChildRef(), IS_ACTIVE)) {
					results.add(uRef.getChildRef());
				}
			} else {
				results.add(uRef.getChildRef());
			}
		}
		return results;
	}

	/**
	 * Проверка есть ли у подразделения дочерние элементы
	 */
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

	/**
	 * Получение родительского подразделения
	 */
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

	/**
	 * проверяет что объект является подразделением
	 */
	public boolean isUnit(NodeRef ref) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_ORGANIZATION_UNIT);
		return isProperType(ref, types);
	}

	/**
	 * проверяет что объект является рабочей группой
	 */
	public boolean isWorkGroup(NodeRef ref) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_WORK_GROUP);
		return isProperType(ref, types);
	}

	/**
	 * проверяет что объект является бизнес ролью
	 */
	public boolean isBusinessRole(NodeRef ref) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_BUSINESS_ROLE);
		return isProperType(ref, types);
	}

	/**
	 * проверяет что объект является сотрудником
	 */
	public boolean isEmployee(NodeRef ref) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_EMPLOYEE);
		return isProperType(ref, types);
	}

	/**
	 * проверяет что объект является штатным расписанием
	 */
	public boolean isStaffList(NodeRef ref) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_STAFF_LIST);
		return isProperType(ref, types);
	}
	/**
	 * проверяет что объект является Участником рабочей группы
	 */
	public boolean isWorkForce(NodeRef ref) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_WORKFORCE);
		return isProperType(ref, types);
	}
	/**
	 * проверяет что объект имеет подходящий тип
	 */
	private boolean isProperType(NodeRef ref, Set<QName> types) {
		if (ref != null) {
			QName type = nodeService.getType(ref);
			return types.contains(type);
		} else {
			return false;
		}
	}

	/**
	 * Получение руководителя подразделения
	 */
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

	/**
	 * Получение Штатного расписания, содержащего руководящую позицию
	 */
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

	/**
	 * Получение руководителя сотрудника
	 */
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

	/**
	 * Получение списка Штатных Расписаний для Подразделения
	 */
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

	/**
	 * Получение ссылки на сотрудника для объектов "Штатное Расписание и "Участник Рабочей группы"
	 */
	public NodeRef getEmployeeByPosition(NodeRef positionRef) {
		NodeRef employeeLink = getEmployeeLinkByPosition(positionRef);
		if (employeeLink != null){
			return getEmployeeByLink(employeeLink);
		}
		return null;
	}

	/**
	 * Получение списка сотрудников, занимающих в указанном подразделении указанную должностную позицию
	 * @param unit подразделение
	 * @param position доложностная позиция
	 * @return список ссылок на сотрудников
	 */
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

	/**
	 * Получение ссылки на сотрудника из объекта (lecm-orgstr:employee-link)
	 */
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

	/**
	 * Получение списка должностных позиций в системе
	 */
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

	/**
	 * Получение перечня сотрудников, которые занимают должностную позицию
	 */
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

	/**
	 * Получение перечня сотрудников, участвующих в рабочей группе
	 */
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

	/**
	 * Получение основной должностной позиции (штатного расписания) у сотрудника
	 */
	public NodeRef getEmployeePrimaryStaff(NodeRef employeeRef) {
		NodeRef primaryStaff = null;
		if (isEmployee(employeeRef)) {
			List<AssociationRef> links = nodeService.getSourceAssocs(employeeRef, ASSOC_EMPLOYEE_LINK_EMPLOYEE);
			for (AssociationRef link : links) {
				if ((Boolean) nodeService.getProperty(link.getSourceRef(), PROP_EMP_LINK_IS_PRIMARY)) {
					primaryStaff = getPositionByEmployeeLink(link.getSourceRef());
					if (primaryStaff != null && isStaffList(primaryStaff)) {
						break;
					}
				}
			}
		}
		return primaryStaff;
	}

	/**
	 * Получение штатного расписания или участника Рабочей группы по ссылке на сотрудника
	 */
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

	/**
	 * Получение подразделения, к которому относится штатное расписание
	 */
	public NodeRef getUnitByStaff(NodeRef staffRef) {
		NodeRef unitRef = null;
		if (isStaffList(staffRef)) {
			unitRef = getParent(staffRef);
		}
		return unitRef;
	}

	/**
	 * Получение фотографии сотрудника
	 */
	public NodeRef getEmployeePhoto(NodeRef employeeRef) {
		NodeRef photoRef = null;
		if (isEmployee(employeeRef)) {
			List<AssociationRef> photos = nodeService.getTargetAssocs(photoRef, ASSOC_EMPLOYEE_PHOTO);
			if (photos.size() > 0) {
				photoRef = photos.get(0).getTargetRef();
			}
		}
		return photoRef;
	}

	/**
	 * Получение персональных данных сотрудника
	 */
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

	/**
	 * Получение списка должностных позиций сотрудника
	 */
	public List<NodeRef> getEmployeeStaffs(NodeRef employeeRef) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_STAFF_LIST);
		return getEmployeePositions(employeeRef, types);
	}

	/**
	 * Получение списка Участников Рабочих Групп для сотрудника
	 */
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
				NodeRef staff = getPositionByEmployeeLink(link.getSourceRef());
				if (staff != null && isProperType(staff, types)) {
					positions.add(staff);
				}
			}
		}
		return positions;
	}

	/**
	 * Получение списка Рабочих Групп, в которых участвует сотрудник
	 */
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

	/**
	 * Получение рабочей группы, к которому относится участник
	 */
	public NodeRef getWorkGroupByWorkForce(NodeRef workRef) {
		NodeRef groupRef = null;
		if (isWorkForce(workRef)) {
			groupRef = getParent(workRef);
		}
		return groupRef;
	}

	/**
	 * Получение списка ролей в рабочих группах
	 */
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
				if ((Boolean) nodeService.getProperty(workRole.getChildRef(), IS_ACTIVE)) {
					results.add(workRole.getChildRef());
				}
			}
		}
		return results;
	}

	/**
	 * Получение ссылки на сотрудника для Позиции (Штатного расписания или Участника Рабочей группы)
	 */
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
    
    public boolean isArchive(NodeRef ref){
	    boolean isArchive = ref.getStoreRef().getProtocol().equals("archive");
	    Boolean isActive = (Boolean) nodeService.getProperty(ref, IS_ACTIVE);
        return isArchive || (isActive != null && !isActive);
    }

	/**
	 * Получение полного перечня бизнес ролей
	 */
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

	/**
	 * Получение перечня сотрудников, исполняющих определенную Бизнес-роль
	 */
	public List<NodeRef> getEmployeesByBusinessRole(NodeRef businessRoleRef) {
		List<NodeRef> results = new ArrayList<NodeRef>();
		if (isBusinessRole(businessRoleRef)) { // если бизнес роль
			// получаем сотрудников
			List<AssociationRef> employees = nodeService.getTargetAssocs(businessRoleRef, ASSOC_BUSINESS_ROLE_EMPLOYEE);
			for (AssociationRef empChildRef : employees) {
				if (!isArchive(empChildRef.getTargetRef())){
						results.add(empChildRef.getTargetRef());
				}
			}
		}
		return results;
	}

	/**
	 * Получение ссылок на сотрудника
	 */
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
	/**
	 * Получение ссылок на сотрудника в Штатных расписаниях
	 */
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
}
