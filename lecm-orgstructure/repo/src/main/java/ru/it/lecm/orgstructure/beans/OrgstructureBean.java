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

	private ServiceRegistry serviceRegistry;
	private Repository repositoryHelper;
	private TransactionService transactionService;
	private NodeService nodeService;

	public static final QName ASSOC_ORG_BOSS = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "org-boss-assoc");
	public static final QName ASSOC_ORG_LOGO = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "org-logo-assoc");
	public static final QName ASSOC_EMPLOYEE_LINK_EMPLOYEE = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "employee-link-employee-assoc");
	public static final QName ASSOC_ELEMENT_MEMBER_POSITION = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "element-member-position-assoc");
	public static final QName ASSOC_ELEMENT_MEMBER_EMPLOYEE = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "element-member-employee-assoc");

	public static final QName PROP_STAFF_LIST_IS_BOSS = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "staff-list-is-boss");

	public static final QName IS_ACTIVE = QName.createQName("http://www.it.ru/lecm/dictionary/1.0", "active");

	public static final QName TYPE_ORGANIZATION_UNIT = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "organization-unit");
	public static final QName TYPE_STRUCTURE = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "structure");
	public static final QName TYPE_WORK_GROUP = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "workGroup");
	public static final QName TYPE_STAFF_LIST = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "staff-list");
	public static final QName TYPE_WORKFORCE = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "workforce");
	public static final QName TYPE_EMPLOYEE_LINK = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "employee-link");
	public static final QName TYPE_STAFF_POSITION = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "staffPosition");
	public static final QName TYPE_WORK_ROLE = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "workRole");

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
	 *
	 * @return NodeRef
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
			if (boss != null && boss.size() > 0){
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
			if (logo != null && logo.size() > 0){
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
	 *
	 * @return List<NodeRef>
	 */
	public List<NodeRef> getWorkGroups(boolean onlyActive) {
		List<NodeRef> results = new ArrayList<NodeRef>();
		NodeRef structureDirectory = getStructureDirectory();
		if (structureDirectory != null) {

			Set<QName> workgroups = new HashSet<QName>();
			workgroups.add(TYPE_WORK_GROUP);
			List<ChildAssociationRef> wgs = nodeService.getChildAssocs(structureDirectory, workgroups);
			for (ChildAssociationRef wg : wgs) {
				if (onlyActive){
					if ((Boolean)nodeService.getProperty(wg.getChildRef(), IS_ACTIVE)){
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
	 *
	 * @return List<NodeRef>
	 */
	public List<NodeRef> getSubUnits(NodeRef parent, boolean onlyActive) {
		List<NodeRef> results = new ArrayList<NodeRef>();
		Set<QName> units = new HashSet<QName>();
		units.add(TYPE_ORGANIZATION_UNIT);

		List<ChildAssociationRef> uRefs = nodeService.getChildAssocs(parent, units);
		for (ChildAssociationRef uRef : uRefs) {
			if (onlyActive){
				if ((Boolean)nodeService.getProperty(uRef.getChildRef(), IS_ACTIVE)){
					results.add(uRef.getChildRef());
				}
			} else {
				results.add(uRef.getChildRef());
			}
		}
		return results;
	}

	public boolean hasChild(NodeRef parent,boolean onlyActive) {
		List<NodeRef> childs = getSubUnits(parent, onlyActive);
		boolean hasChild = !childs.isEmpty();
		if (onlyActive && !childs.isEmpty()) {
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
	 *
	 * @return NodeRef родителя или NULL, если подразделение не имеет родителя
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
	 *
	 * @return true - если подразделение или false - в ином случае
	 */
	public boolean isUnit(NodeRef ref) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_ORGANIZATION_UNIT);
		return isProperType(ref, types);
	}

	/**
	 * проверяет что объект является рабочей группой
	 *
	 * @return true - если рабочая группа или false - в ином случае
	 */
	public boolean isWorkGroup(NodeRef ref) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_WORK_GROUP);
		return isProperType(ref, types);
	}

	/**
	 * проверяет что объект имеет подходящий тип
	 *
	 * @return true - если объект подходит по критериям или false - в ином случае
	 */
	public boolean isProperType(NodeRef ref, Set<QName> types) {
		QName type = nodeService.getType(ref);
		return types.contains(type);
	}
	/**
	 * Получение руководителя подразделения
	 *
	 * @return NodeRef руководителя или NULL, если руководитель не найден или объект не является подразделением
	 */
	public NodeRef getBoss(NodeRef unitRef) {
		NodeRef bossLink = null;
		if (isUnit(unitRef)) { // ищем руководителя Подразделения
			NodeRef bossStaff = getBossStaff(unitRef);
			if (bossStaff != null) {
				//вытаскиваем ссылку на сотрудника и непосредственно сотрудника (если ссылка имеется)
				bossLink = getEmployee(bossStaff);
			}
			if (bossLink == null) {
				// если не нашли руководителя в текущем подразделении, пробуем найти в вышестоящем
				NodeRef parent = getParent(unitRef);
				if (parent != null) {
					bossLink = getBoss(parent);
				} else {
					// дошли до директории Структура, пробуем получить руководителя Организации
					bossLink = getOrganizationBoss();
				}

			}
		}
		return bossLink;
	}

	public NodeRef getBossStaff(NodeRef unitRef) {
		// Получаем список штатных расписаний
		List<NodeRef> staffs = getStaffLists(unitRef);
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
	 * Получение списка Штатных Расписаний для Подразделения
	 *
	 * @return List<NodeRef>
	 */
	public List<NodeRef> getStaffLists(NodeRef unitRef) {
		List<NodeRef> results = new ArrayList<NodeRef>();
		if (isUnit(unitRef)) {
			Set<QName> staffs = new HashSet<QName>();
			staffs.add(TYPE_STAFF_LIST);

			List<ChildAssociationRef> sls = nodeService.getChildAssocs(unitRef, staffs);
			for (ChildAssociationRef sl : sls) {
				results.add(sl.getChildRef());
			}
		}
		return results;
	}

	/**
	 * Получение ссылки на сотрудника для объектов "Штатное Расписание и "Участник Рабочей группы"
	 *
	 * @return ссылка на объект (lecm-orgstr:employee-link)
	 */
	public NodeRef getEmployee(NodeRef ref) {
		Set<QName> properTypes = new HashSet<QName>();
		properTypes.add(TYPE_STAFF_LIST);
		properTypes.add(TYPE_WORKFORCE);

		if (isProperType(ref, properTypes)) {
			Set<QName> link = new HashSet<QName>();
			link.add(TYPE_EMPLOYEE_LINK);

			List<ChildAssociationRef> links = nodeService.getChildAssocs(ref, link);
			if (links.size() > 0) {
				NodeRef linkRef = links.get(0).getChildRef();
				return getEmployeeFromLink(linkRef);
			}
		}
		return null;
	}

	/**
	 * Получение ссылки на сотрудника из объекта (lecm-orgstr:employee-link)
	 *
	 * @return ссылка на сотрудника (lecm-orgstr:employee) или Null, если у объекта некорректный тип
	 */
	public NodeRef getEmployeeFromLink(NodeRef ref) {
		Set<QName> properTypes = new HashSet<QName>();
		properTypes.add(TYPE_EMPLOYEE_LINK);

		if (isProperType(ref, properTypes)) {
			List<AssociationRef> links = nodeService.getTargetAssocs(ref, ASSOC_EMPLOYEE_LINK_EMPLOYEE);
			// сотрудник всегда существует и только один
			return links.get(0).getTargetRef();
		}
		return null;
	}

	/**
	 * Получение списка должностных позиций
	 *
	 * @return List<NodeRef>
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
	 *
	 * @return List<NodeRef> - перечень сотрудников
	 */
	public List<NodeRef> getPositionEmployees(NodeRef position) {
		List<NodeRef> results = new ArrayList<NodeRef>();
		Set<QName> properTypes = new HashSet<QName>();
		properTypes.add(TYPE_STAFF_POSITION);

		if (isProperType(position, properTypes)) { // если должностная позиция
			// получаем список объектов Штатное расписание для заданной позиции
			List<AssociationRef> staffs = nodeService.getSourceAssocs(position, ASSOC_ELEMENT_MEMBER_POSITION);
			for (AssociationRef staff : staffs) {
				Set<QName> links = new HashSet<QName>();
				links.add(TYPE_EMPLOYEE_LINK);
				// из штатного расписания получает ссылку на сотрудника
				List<ChildAssociationRef> empLinks = nodeService.getChildAssocs(staff.getSourceRef(), links);
				if (empLinks.size() > 0) { // сотрудник задан -> по ссылке получаем сотрудника
					NodeRef employee = getEmployeeFromLink(empLinks.get(0).getChildRef());
					results.add(employee);
				}
			}
		}
		return results;
	}

	/**
	 * Получение перечня сотрудников, участвующих в рабочей группе
	 *
	 * @return List<NodeRef> - перечень сотрудников
	 */
	public List<NodeRef> getWorkGroupEmployees(NodeRef workGroup) {
		List<NodeRef> results = new ArrayList<NodeRef>();
		Set<QName> properTypes = new HashSet<QName>();
		properTypes.add(TYPE_WORK_GROUP);

		if (isProperType(workGroup, properTypes)) { // если рабочая группа
			// получаем участников для рабочей группы
			Set<QName> workforces = new HashSet<QName>();
			workforces.add(TYPE_WORKFORCE);
			List<ChildAssociationRef> workForces = nodeService.getChildAssocs(workGroup, workforces);
			for (ChildAssociationRef wf : workForces) {
				Set<QName> links = new HashSet<QName>();
				links.add(TYPE_EMPLOYEE_LINK);
				//получает ссылку на сотрудника
				List<ChildAssociationRef> empLinks = nodeService.getChildAssocs(wf.getChildRef(), links);
				if (empLinks.size() > 0) { // сотрудник задан -> по ссылке получаем сотрудника
					NodeRef employee = getEmployeeFromLink(empLinks.get(0).getChildRef());
					results.add(employee);
				}
			}
		}
		return results;
	}
}
