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
import org.alfresco.service.transaction.TransactionService;
import ru.it.lecm.base.beans.BaseBean;

/**
 * @author dbashmakov
 *         Date: 27.11.12
 *         Time: 17:08
 */
public class OrgstructureBeanImpl extends BaseBean implements OrgstructureBean {

	private ServiceRegistry serviceRegistry;
	private Repository repositoryHelper;
	private TransactionService transactionService;
	private AuthenticationService authService;
	private PersonService personService;

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

	public void setAuthService(AuthenticationService authService) {
		this.authService = authService;
	}

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	/**
	 * Получение директории Организация.
	 * Если такой узел отсутствует - он НЕ создаётся.
	 */
	@Override
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

	/**
	 * Получение руководителя Организации
	 *
	 * @return NodeRef или NULL если руководитель не задан
	 */
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

	/**
	 * Получение логотипа Организации
	 *
	 * @return NodeRef или NULL если логотип не задан
	 */
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

	/**
	 * Получение Директории с Оргструктурой
	 *
	 * @return NodeRef или NULL
	 */
	@Override
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
	@Override
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
	@Override
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

	/**
	 * Получение списка дочерних подразделений
	 */
	@Override
	public List<NodeRef> getSubUnits(NodeRef parent, boolean onlyActive) {
		return getSubUnits(parent, onlyActive, false);
	}

	/**
     * Получение списка дочерних подразделений
     */
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

	/**
	 * Проверка есть ли у подразделения дочерние элементы
	 */
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

	/**
	 * Получение родительского подразделения
	 */
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

	/**
	 * проверяет что объект является подразделением
	 */
	@Override
	public boolean isUnit(NodeRef ref) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_ORGANIZATION_UNIT);
		return isProperType(ref, types);
	}

	/**
	 * проверяет что объект является рабочей группой
	 */
	@Override
	public boolean isWorkGroup(NodeRef ref) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_WORK_GROUP);
		return isProperType(ref, types);
	}

	/**
	 * проверяет что объект является бизнес ролью
	 */
	@Override
	public boolean isBusinessRole(NodeRef ref) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_BUSINESS_ROLE);
		return isProperType(ref, types);
	}

	/**
	 * проверяет что объект является сотрудником
	 */
	@Override
	public boolean isEmployee(NodeRef ref) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_EMPLOYEE);
		return isProperType(ref, types);
	}

	/**
	 * проверяет что объект является штатным расписанием
	 */
	@Override
	public boolean isStaffList(NodeRef ref) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_STAFF_LIST);
		return isProperType(ref, types);
	}
	/**
	 * проверяет что объект является Участником рабочей группы
	 */
	@Override
	public boolean isWorkForce(NodeRef ref) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_WORKFORCE);
		return isProperType(ref, types);
	}

	/**
	 * Получение руководителя подразделения
	 */
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

	/**
	 * Получение Штатного расписания, содержащего руководящую позицию
	 */
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

	/**
	 * Получение руководителя сотрудника
	 */
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

	/**
	 * Получение списка Штатных Расписаний для Подразделения
	 */
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

	/**
	 * Получение ссылки на сотрудника для объектов "Штатное Расписание и "Участник Рабочей группы"
	 */
	@Override
	public NodeRef getEmployeeByPosition(NodeRef positionRef) {
		NodeRef employeeLink = getEmployeeLinkByPosition(positionRef);
		if (employeeLink != null && !isArchive(employeeLink)){
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

	/**
	 * Получение ссылки на сотрудника из объекта (lecm-orgstr:employee-link)
	 */
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

	/**
	 * Получение списка должностных позиций в системе
	 */
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

	/**
	 * Получение перечня сотрудников, которые занимают должностную позицию
	 */
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

	/**
	 * Получение перечня сотрудников, участвующих в рабочей группе
	 */
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

	/**
	 * Получение перечня сотрудников подразделения либо рабочей группы
	 */
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

	/**
	 * Получение основной должностной позиции (штатного расписания) у сотрудника
	 */
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

	/**
	 * Получение штатного расписания или участника Рабочей группы по ссылке на сотрудника
	 */
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

	/**
	 * Получение подразделения, к которому относится штатное расписание
	 */
	@Override
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

	/**
	 * Получение персональных данных сотрудника
	 */
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

	/**
	 * Получение списка должностных позиций сотрудника
	 */
	@Override
	public List<NodeRef> getEmployeeStaffs(NodeRef employeeRef) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_STAFF_LIST);
		return getEmployeePositions(employeeRef, types);
	}

	/**
	 * Получение списка Участников Рабочих Групп для сотрудника
	 */
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

	/**
	 * Получение списка Рабочих Групп, в которых участвует сотрудник
	 */
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

	/**
	 * Получение рабочей группы, к которому относится участник
	 */
	@Override
	public NodeRef getWorkGroupByWorkForce(NodeRef workRef) {
		NodeRef groupRef = null;
		if (isWorkForce(workRef)) {
			groupRef = nodeService.getPrimaryParent(workRef).getParentRef();
		}
		return groupRef;
	}

	/**
	 * Получение списка ролей в рабочих группах
	 */
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

	/**
	 * Получение ссылки на сотрудника для Позиции (Штатного расписания или Участника Рабочей группы)
	 */
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
    
	/**
	 * Получение полного перечня бизнес ролей
	 */
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

	/**
	 * Получение перечня сотрудников, исполняющих определенную Бизнес-роль
	 */
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


	/**
	 * Получение перечня организационных элементов (подразделений и рабочих групп),
	 * исполняющих определенную Бизнес-роль (включая вложенные)
	 */
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


	/**
	 * Получение ссылок на сотрудника
	 */
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
	/**
	 * Получение ссылок на сотрудника в Штатных расписаниях
	 */
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

	/**
	 * Получение текущего сотрудника
	 */
	@Override
	public NodeRef getCurrentEmployee() {
		String username = authService.getCurrentUserName();
		return getEmployeeByPerson(username);
	}

	/**
	 * Получение текущего сотрудника по имени пользователя
	 */
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

	/**
	 * Получение текущего сотрудника по NodeRef пользователя
	 */
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

	/**
	 * Получение пользователя сотрудника
	 */
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
}