package ru.it.lecm.orgstructure.exportimport.beans;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.MutableAuthenticationService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.alfresco.util.PropertyMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.contractors.api.Contractors;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.orgstructure.beans.OrgstructureAspectsModel;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import static ru.it.lecm.orgstructure.beans.OrgstructureBean.POSITIONS_DICTIONARY_NAME;
import ru.it.lecm.orgstructure.exportimport.ExportImportHelper;
import ru.it.lecm.orgstructure.exportimport.ExportImportModel;
import ru.it.lecm.orgstructure.exportimport.entity.BusinessRole;
import ru.it.lecm.orgstructure.exportimport.entity.BusinessRoles;
import ru.it.lecm.orgstructure.exportimport.entity.CreatedItems;
import ru.it.lecm.orgstructure.exportimport.entity.Department;
import ru.it.lecm.orgstructure.exportimport.entity.Departments;
import ru.it.lecm.orgstructure.exportimport.entity.Employee;
import ru.it.lecm.orgstructure.exportimport.entity.Employees;
import ru.it.lecm.orgstructure.exportimport.entity.Position;
import ru.it.lecm.orgstructure.exportimport.entity.Positions;
import ru.it.lecm.orgstructure.exportimport.entity.Staff;
import ru.it.lecm.orgstructure.exportimport.entity.StaffList;

/**
 *
 * @author vlevin
 */
public class OrgstructureImportServiceImpl extends BaseBean implements OrgstructureImportService {

	private final static Logger logger = LoggerFactory.getLogger(OrgstructureImportServiceImpl.class);

	private OrgstructureBean orgstructureService;
	private BusinessJournalService businessJournalService;
	private DictionaryBean dictionaryService;
	private PersonService personService;
	private MutableAuthenticationService authenticationService;
	private BehaviourFilter behaviourFilter;
	private SearchService searchService;
	private NamespaceService namespaceService;

	private NodeRef positionsRoot;
	private NodeRef businessRolesRoot;
	private ExportImportHelper helper;

	private final static String DEFAULT_PASSWORD = "12345";
	private String contractorsDictionaryName;

	public void setContractorsDictionaryName(String contractorsDictionaryName) {
		this.contractorsDictionaryName = contractorsDictionaryName;
	}

	public String getContractorsDictionaryName() {
		return contractorsDictionaryName;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

	public void setDictionaryService(DictionaryBean dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	public void setBehaviourFilter(BehaviourFilter behaviourFilter) {
		this.behaviourFilter = behaviourFilter;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	public void init() {
//		PropertyCheck.mandatory(this, "nodeService", nodeService);
//		PropertyCheck.mandatory(this, "orgstructureService", orgstructureService);
//		PropertyCheck.mandatory(this, "businessJournalService", businessJournalService);
//		PropertyCheck.mandatory(this, "dictionaryService", dictionaryService);
//		PropertyCheck.mandatory(this, "transactionService", transactionService);
//		PropertyCheck.mandatory(this, "personService", personService);
//		PropertyCheck.mandatory(this, "authService", authService);
//		PropertyCheck.mandatory(this, "behaviourFilter", behaviourFilter);
//		PropertyCheck.mandatory(this, "searchService", searchService);
//		PropertyCheck.mandatory(this, "namespaceService", namespaceService);
//
//		authenticationService = (MutableAuthenticationService) authService;
//
//		positionsRoot = dictionaryService.getDictionaryByName(POSITIONS_DICTIONARY_NAME);
//		businessRolesRoot = dictionaryService.getDictionaryByName(OrgstructureBean.BUSINESS_ROLES_DICTIONARY_NAME);
//
//		helper = new ExportImportHelper(nodeService, namespaceService, searchService, orgstructureService);
	}
	
	protected void onBootstrap(ApplicationEvent event)
	{
	}

	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}

	@SuppressWarnings("unchecked")
	private <T> T parseXML(InputStream input, Class<T> clazz) {
		Object result;

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			result = jaxbUnmarshaller.unmarshal(input);
		} catch (JAXBException ex) {
			throw new RuntimeException(ex);
		}

		return (T)result;
	}

	@Override
	public Departments parseDepartmentsXML(InputStream input) {
		return parseXML(input, Departments.class);
	}

	@Override
	public Employees parseEmployeesXML(InputStream input) {
		return parseXML(input, Employees.class);
	}

	@Override
	public Positions parsePositionsXML(InputStream input) {
		return parseXML(input, Positions.class);
	}

	@Override
	public StaffList parseStaffXML(InputStream input) {
		return parseXML(input, StaffList.class);
	}

	@Override
	public BusinessRoles parseBusinessRolesXML(InputStream input) {
		return parseXML(input, BusinessRoles.class);
	}

	@Override
	public void importPositions(Positions positions, CreatedItems createdItems) {
		logger.info("Начат импорт справочника должностей");

		List<Position> positionsList = positions.getPosition();
		final int positionsListSize = positionsList.size();

		final Map<String, NodeRef> existingItems = helper.getNodeRefsIDs(orgstructureService.getStaffPositions(false));
		final Map<String, NodeRef> newlyCreatedPositions = createdItems.getPositions();

		int importedCount = 0;

		for (final Position position : positionsList) {
			boolean result;
			try {
				result = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Boolean>() {

					@Override
					public Boolean execute() throws Throwable {

						logger.info("Импортируется должность: {}", position);

						String id = position.getId();
						if (id == null) {
							logger.error("Невозможно выполнить импорт должности {}: ID не указан", position);
							return false;
						}

						String positionName = StringUtils.trim(position.getName());
						if (positionName == null) {
							logger.error("Невозможно выполнить импорт должности {}: наименование не указано", position);
							return false;
						}

						NodeRef positionNode = getValueFromTwoMaps(id, newlyCreatedPositions, existingItems);
						if (positionNode != null) {
							positionNode = updatePosition(positionNode, position);
						} else {
							positionNode = nodeService.getChildByName(positionsRoot, ContentModel.ASSOC_CONTAINS, positionName);
							if (positionNode == null) {
								positionNode = createPosition(id, position);
							} else {
								logger.error("Невозможно выполнить импорт должности {}: в системе уже существует должность {} с именем {}",new Object[] { position, positionNode, positionName });
								return false;
							}
						}

						newlyCreatedPositions.put(id, positionNode);
						logger.info("Успешно выполнен импорт должности {}", position);
						return true;
					}

				}, false);
			} catch (RuntimeException ex) {
				logger.error("Невозможно выполнить импорт должности {}; причина: {}", position, ex.getMessage());
				if (logger.isDebugEnabled()) {
					logger.error("Error", ex);
				}
				result = false;
			}
			if (result) {
				importedCount++;
			}
		}

		logger.info("Импортировано {} должностей из {}", importedCount, positionsListSize);
		logger.info("Закончен импорт справочника должностей");
	}

	private NodeRef createPosition(String id, Position position) {
		PropertyMap props = new PropertyMap();
		props.put(ContentModel.PROP_NAME, StringUtils.trim(position.getName()));
		props.put(OrgstructureBean.PROP_STAFF_POSITION_NAME_D, StringUtils.trim(position.getNameDative()));
		props.put(OrgstructureBean.PROP_STAFF_POSITION_NAME_G, StringUtils.trim(position.getNameGenitive()));
		props.put(OrgstructureBean.PROP_STAFF_POSITION_CODE, StringUtils.trim(position.getCode()));

		NodeRef createdStaffPosition = nodeService.createNode(positionsRoot, ContentModel.ASSOC_CONTAINS, generateRandomQName(), OrgstructureBean.TYPE_STAFF_POSITION, props).getChildRef();

		helper.addID(createdStaffPosition, id);

		return createdStaffPosition;
	}

	private NodeRef updatePosition(NodeRef positionNode, Position position) {
		Map<QName, Serializable> props = nodeService.getProperties(positionNode);
		props.put(ExportImportModel.PROP_ID, position.getId());
		props.put(ContentModel.PROP_NAME, StringUtils.trim(position.getName()));
		props.put(OrgstructureBean.PROP_STAFF_POSITION_NAME_D, StringUtils.trim(position.getNameDative()));
		props.put(OrgstructureBean.PROP_STAFF_POSITION_NAME_G, StringUtils.trim(position.getNameGenitive()));
		props.put(OrgstructureBean.PROP_STAFF_POSITION_CODE, StringUtils.trim(position.getCode()));
		props.put(IS_ACTIVE, true);
		nodeService.setProperties(positionNode, props);

		logger.info("Должность с ID {} уже существует. Существующая должность {} обновлена и активирована.", position, positionNode);
		return positionNode;
	}

	@Override
	public void importEmployees(Employees employees, CreatedItems createdItems) {
		logger.info("Начат импорт списка сотрудников");

		final Map<String, NodeRef> existingItems = helper.getNodeRefsIDs(helper.getAllEmployees(false));
		final Map<String, NodeRef> newlyCreatedEmployees = createdItems.getEmployees();

		List<Employee> employeeList = employees.getEmployee();
		final int employeeListSize = employeeList.size();

		int importedCount = 0;

		for (final Employee employee : employeeList) {
			boolean result;
			try {
				result = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Boolean>() {

					@Override
					public Boolean execute() throws Throwable {
						logger.info("Импортируется сотрудник: {}", employee);

						String id = employee.getId();
						if (id == null) {
							logger.error("Невозможно выполнить импорт сотрудника {}: ID не указан", employee);
							return false;
						}

						String firstName = StringUtils.trimToNull(employee.getFirstname());
						if (firstName == null) {
							logger.error("Невозможно выполнить импорт сотрудника {}: имя не указано", employee);
							return false;
						}

						String lastName = StringUtils.trimToNull(employee.getLastname());
						if (lastName == null) {
							logger.error("Невозможно выполнить импорт сотрудника {}: фамилия не указана", employee);
							return false;
						}

						String middleName = StringUtils.trimToNull(employee.getMiddlename());
						if (middleName == null) {
							logger.error("Невозможно выполнить импорт сотрудника {}: отчество не указано", employee);
							return false;
						}

						String email = StringUtils.trimToNull(employee.getEmail());
						if (email == null) {
							logger.error("Невозможно выполнить импорт сотрудника {}: электронный адрес не указан", employee);
							return false;
						}

						NodeRef employeeNode = getValueFromTwoMaps(id, newlyCreatedEmployees, existingItems);
						if (employeeNode != null) {
							employeeNode = updateEmployee(employeeNode, employee);
						} else {
							employeeNode = createEmployee(id, employee);
						}

						newlyCreatedEmployees.put(id, employeeNode);

						logger.info("Успешно выполнен импорт сотрудника {}", employee);
						return true;
					}

				}, false, true);
			} catch (RuntimeException ex) {
				logger.error("Невозможно выполнить импорт сотрудника {}; причина: {}", employee, ex.getMessage());
				if (logger.isDebugEnabled()) {
					logger.error("Error", ex);
				}
				result = false;
				behaviourFilter.enableBehaviour(ContentModel.TYPE_PERSON);
			}
			if (result) {
				importedCount++;
			}
		}
		logger.info("Импортировано {} сотрудников из {}", importedCount, employeeListSize);
		logger.info("Закончен импорт сотрудников");
	}

	private NodeRef createPerson(String login, String mail, String firstName, String lastName) {
		// Полиси для создания сотрудника вызывается при коммите транзакции, что нам не подходит.
		// Придется создавать сотрудника руками.
		behaviourFilter.disableBehaviour(ContentModel.TYPE_PERSON);

		PropertyMap props = new PropertyMap();
		props.put(ContentModel.PROP_NAME, login);
		props.put(ContentModel.PROP_EMAIL, mail);
		props.put(ContentModel.PROP_USERNAME, login);
		props.put(ContentModel.PROP_FIRSTNAME, firstName);
		props.put(ContentModel.PROP_LASTNAME, lastName);

		NodeRef personNode = personService.createPerson(props);
		authenticationService.createAuthentication(login, DEFAULT_PASSWORD.toCharArray());
		authenticationService.setAuthenticationEnabled(login, true);
		// personService.notifyPerson(employeeLogin, DEFAULT_PASSWORD);

		return personNode;
	}

	private NodeRef createEmployee(String id, Employee employee) {
		String email = StringUtils.trim(employee.getEmail());
		String firstName = StringUtils.trim(employee.getFirstname());
		String lastName = StringUtils.trim(employee.getLastname());

		PropertyMap props = new PropertyMap();
		props.put(OrgstructureBean.PROP_EMPLOYEE_EMAIL, email);
		props.put(OrgstructureBean.PROP_EMPLOYEE_FIRST_NAME, firstName);
		props.put(OrgstructureBean.PROP_EMPLOYEE_LAST_NAME, lastName);
		props.put(OrgstructureBean.PROP_EMPLOYEE_MIDDLE_NAME, StringUtils.trim(employee.getMiddlename()));
		props.put(OrgstructureBean.PROP_EMPLOYEE_FIO_D, StringUtils.trim(employee.getNameDative()));
		props.put(OrgstructureBean.PROP_EMPLOYEE_FIO_G, StringUtils.trim(employee.getNameGenitive()));
		props.put(OrgstructureBean.PROP_EMPLOYEE_NUMBER, StringUtils.trim(employee.getNumber()));
		props.put(OrgstructureBean.PROP_EMPLOYEE_PHONE, StringUtils.trim(employee.getPhone()));
		props.put(OrgstructureBean.PROP_EMPLOYEE_SEX, StringUtils.trim(employee.getSex()));

		NodeRef employeeNode = nodeService.createNode(orgstructureService.getEmployeesDirectory(), ContentModel.ASSOC_CONTAINS,
				generateRandomQName(), OrgstructureBean.TYPE_EMPLOYEE, props).getChildRef();

		helper.addID(employeeNode, id);

		String employeeLogin = StringUtils.trimToNull(employee.getLogin());
		NodeRef personNode = null;

		if (employeeLogin != null) {
			int loginCounter = 0;
			while (personService.personExists(employeeLogin)) {
				logger.warn("Логин {} уже занят. Перебираю незанятые.", employeeLogin);
				employeeLogin = employee.getLogin() + ++loginCounter;
			}

			personNode = createPerson(employeeLogin, email, firstName, lastName);
		} else {
			logger.error("У сотрудника {} не указан логин", employee);
		}

		if (personNode != null) {
			nodeService.createAssociation(employeeNode, personNode, OrgstructureBean.ASSOC_EMPLOYEE_PERSON);
		}

		return employeeNode;
	}

	private NodeRef updateEmployee(NodeRef employeeNode, Employee employee) {
		Map<QName, Serializable> props = nodeService.getProperties(employeeNode);
		props.put(ExportImportModel.PROP_ID, employee.getId());
		props.put(OrgstructureBean.PROP_EMPLOYEE_EMAIL, StringUtils.trim(employee.getEmail()));
		props.put(OrgstructureBean.PROP_EMPLOYEE_FIRST_NAME, StringUtils.trim(employee.getFirstname()));
		props.put(OrgstructureBean.PROP_EMPLOYEE_LAST_NAME, StringUtils.trim(employee.getLastname()));
		props.put(OrgstructureBean.PROP_EMPLOYEE_MIDDLE_NAME, StringUtils.trim(employee.getMiddlename()));
		props.put(OrgstructureBean.PROP_EMPLOYEE_FIO_D, StringUtils.trim(employee.getNameDative()));
		props.put(OrgstructureBean.PROP_EMPLOYEE_FIO_G, StringUtils.trim(employee.getNameGenitive()));
		props.put(OrgstructureBean.PROP_EMPLOYEE_NUMBER, StringUtils.trim(employee.getNumber()));
		props.put(OrgstructureBean.PROP_EMPLOYEE_PHONE, StringUtils.trim(employee.getPhone()));
		props.put(OrgstructureBean.PROP_EMPLOYEE_SEX, StringUtils.trim(employee.getSex()));
		props.put(IS_ACTIVE, true);
		nodeService.setProperties(employeeNode, props);

		logger.info("Сотрудник с ID {} уже существует. Существующий сотрудник {} обновлен и активирован.", employee, employeeNode);
		return employeeNode;
	}

	@Override
	public void importDepartments(Departments departments, CreatedItems createdItems) {
		logger.info("Начат импорт списка подразделений");

		final Map<String, NodeRef> existingItems = helper.getNodeRefsIDs(helper.getAllOrgUnits(false));
		final Map<String, NodeRef> newlyCreatedDepartments = createdItems.getDepartments();

		List<Department> departmentList = departments.getDepartment();
		final int departmentListSize = departmentList.size();

		int importedCount = 0;

		generateDepartmentsSortWeigth(departmentList);
		Collections.sort(departmentList, new Department.SortWeigthComparator());

		for (final Department depart : departmentList) {
			boolean result;
			try {
				result = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Boolean>() {

					@Override
					public Boolean execute() throws Throwable {
						NodeRef parentNode, departNode;

						logger.info("Импортируется департамент: {}", depart);

						String id = depart.getId();
						if (id == null) {
							logger.error("Невозможно выполнить импорт департамента {}: ID не указан", depart);
							return false;
						}

						String nameFull = StringUtils.trimToNull(depart.getNameFull());
						String nameShort = StringUtils.trimToNull(depart.getNameShort());
						if (nameFull == null || nameShort == null) {
							logger.error("Невозможно выполнить импорт департамента {}: имя не указано", depart);
							return false;
						}

						String code = StringUtils.trimToNull(depart.getCode());
						if (code == null) {
							logger.error("Невозможно выполнить импорт департамента {}: код подразделения не указан", depart);
							return false;
						}

						String parentID = depart.getPid();
						if (parentID == null) {
							logger.error("Невозможно выполнить импорт департамента {}: не указано родительское подразделение", depart);
							return false;
						}

						if (!parentID.equals("0") && !(existingItems.containsKey(parentID) || newlyCreatedDepartments.containsKey(parentID))) {
							logger.error("Невозможно выполнить импорт департамента {}: не найдено родительское подразделение ({})", depart, parentID);
							return false;
						}

						if (parentID.equals("0")) {
							parentNode = orgstructureService.getRootUnit();
							if (parentNode == null) {
								logger.error("Не найдено корневое подразделение! Создайте его");
								return false;
							}
						} else {
							parentNode = getValueFromTwoMaps(parentID, newlyCreatedDepartments, existingItems);
						}

						departNode = getValueFromTwoMaps(id, newlyCreatedDepartments, existingItems);
						if (departNode != null) {
							departNode = updateDepartment(departNode, depart);
						} else {
							departNode = nodeService.getChildByName(parentNode, ContentModel.ASSOC_CONTAINS, nameShort);
							if (departNode == null) {
								departNode = createDepartment(id, parentNode, depart);
								if("0".equals(parentID)){
									NodeRef contractorNode = createOrgContractor(depart);
									nodeService.addAspect(departNode,OrgstructureAspectsModel.ASPECT_HAS_LINKED_ORGANIZATION,null);
									nodeService.createAssociation(departNode,contractorNode,OrgstructureAspectsModel.ASSOC_LINKED_ORGANIZATION);
								}
							} else {
								logger.error("Невозможно выполнить импорт подразделения {}: в системе уже существует подразделение {} с именем {}",new Object[] { depart, departNode, nameShort });
								return false;
							}
						}

						newlyCreatedDepartments.put(id, departNode);

						logger.info("Успешно выполнен импорт департамента {}", depart);
						return true;
					}

				}, false, true);
			} catch (RuntimeException ex) {
				logger.error("Невозможно выполнить импорт департамента {}; причина: {}", depart, ex.getMessage());
				if (logger.isDebugEnabled()) {
					logger.error("Error", ex);
				}
				result = false;
			}
			if (result) {
				importedCount++;
			}
		}

		logger.info("Импортировано {} подразделений из {}", importedCount, departmentListSize);
		logger.info("Закончен импорт списка подразделений");
	}

	/**
	 * Создает внутреннего контрагента на основе подразделения
	 * @param department
	 * @return
	 */
	private NodeRef createOrgContractor(Department department){

		PropertyMap props = new PropertyMap();
		props.put(ContentModel.PROP_NAME, StringUtils.trim(department.getNameShort()));
		props.put(Contractors.PROP_CONTRACTOR_FULLNAME, StringUtils.trim(department.getNameFull()));
		props.put(Contractors.PROP_CONTRACTOR_SHORTNAME, StringUtils.trim(department.getNameShort()));
		props.put(Contractors.PROP_CONTRACTOR_CODE, StringUtils.trim(department.getCode()));

		NodeRef contractorsDic = dictionaryService.getDictionaryByName(contractorsDictionaryName);
		NodeRef contractorNode = nodeService.createNode(contractorsDic, ContentModel.ASSOC_CONTAINS,
				generateRandomQName(),Contractors.TYPE_CONTRACTOR, props).getChildRef();
		nodeService.addAspect(contractorNode, OrgstructureAspectsModel.ASPECT_IS_ORGANIZATION, null);

		return contractorNode;
	}
	private NodeRef createDepartment(String id, NodeRef parentNode, Department department) {
		PropertyMap props = new PropertyMap();

		props.put(ContentModel.PROP_NAME, StringUtils.trim(department.getNameShort()));
		props.put(OrgstructureBean.PROP_ORG_ELEMENT_SHORT_NAME, StringUtils.trim(department.getNameShort()));
		props.put(OrgstructureBean.PROP_ORG_ELEMENT_FULL_NAME, StringUtils.trim(department.getNameFull()));
		props.put(OrgstructureBean.PROP_UNIT_CODE, StringUtils.trim(department.getCode()));
		props.put(OrgstructureBean.PROP_UNIT_TYPE, StringUtils.trim(department.getType()));

		NodeRef departNode = nodeService.createNode(parentNode, ContentModel.ASSOC_CONTAINS, generateRandomQName(), OrgstructureBean.TYPE_ORGANIZATION_UNIT, props).getChildRef();

		helper.addID(departNode, id);

		return departNode;
	}

	private NodeRef updateDepartment(NodeRef departmentNode, Department department) {
		Map<QName, Serializable> props = nodeService.getProperties(departmentNode);

		props.put(ExportImportModel.PROP_ID, department.getId());
		props.put(ContentModel.PROP_NAME, StringUtils.trim(department.getNameShort()));
		props.put(OrgstructureBean.PROP_ORG_ELEMENT_SHORT_NAME, StringUtils.trim(department.getNameShort()));
		props.put(OrgstructureBean.PROP_ORG_ELEMENT_FULL_NAME, StringUtils.trim(department.getNameFull()));
		props.put(OrgstructureBean.PROP_UNIT_CODE, StringUtils.trim(department.getCode()));
		props.put(OrgstructureBean.PROP_UNIT_TYPE, StringUtils.trim(department.getType()));
		props.put(IS_ACTIVE, true);
		nodeService.setProperties(departmentNode, props);

		logger.info("Подразделение с ID {} уже существует. Существующее подразделение {} обновлено и активировано.", department, departmentNode);
		return departmentNode;
	}

	/**
	 * Пробежать по коллекции департаментов и расставить веса, используемые при сортировке так, чтобы подразделения,
	 * стоящие выше к корню (организации) имели меньший вес. Данное действие позволит первыми создать подразделения
	 * верхнего уровня.
	 *
	 * @param departments
	 */
	private void generateDepartmentsSortWeigth(final Collection<Department> departments) {
		int currentWeigth = 0;
		String currentPid = "0";
		LinkedList<String> pids = new LinkedList<>();
		final List<Department> departmentsList = new ArrayList<>(departments);

		while (!departmentsList.isEmpty()) {
			Iterator<Department> iterator = departmentsList.iterator();
			while (iterator.hasNext()) {
				Department depart = iterator.next();
				String deptPid = depart.getPid();
				String deptId = depart.getId();

				if (deptPid == null || deptId == null) {
					depart.setSortWeigth(999999999);
					iterator.remove();
				} else if (deptPid.equals(currentPid)) {
					depart.setSortWeigth(currentWeigth);
					if (!pids.contains(deptId)) {
						pids.addLast(deptId);
					}
					iterator.remove();
				}
			}
			if (pids.isEmpty()) {
				break;
			}
			currentPid = pids.pop();
			currentWeigth++;
		}
	}

	@Override
	public void importStaff(StaffList staffList, CreatedItems createdItems) {
		logger.info("Начат импорт штатных расписаний");

		final Map<String, NodeRef> existingStaff = helper.getNodeRefsIDs(helper.getAllStaff(false));
		final Map<String, NodeRef> newlyCreatedStaff = createdItems.getStaff();

		final Map<String, NodeRef> existingDepartments = helper.getNodeRefsIDs(helper.getAllOrgUnits(true));
		final Map<String, NodeRef> newlyCreatedDepartments = createdItems.getDepartments();

		final Map<String, NodeRef> existingEmployees = helper.getNodeRefsIDs(helper.getAllEmployees(true));
		final Map<String, NodeRef> newlyCreatedEmployees = createdItems.getEmployees();

		final Map<String, NodeRef> existingPositions = helper.getNodeRefsIDs(orgstructureService.getStaffPositions(true));
		final Map<String, NodeRef> newlyCreatedPositions = createdItems.getPositions();

		List<Staff> staffs = staffList.getStaff();
		final int staffsSize = staffs.size();

		int importedCount = 0;

		Collections.sort(staffs, new Staff.LeadershipPositionComparator());

		for (final Staff staff : staffs) {
			boolean result;
			try {
				result = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Boolean>() {

					@Override
					public Boolean execute() throws Throwable {

						logger.info("Импортируется штатное расписание: {}", staff);

						String id = staff.getId();
						if (id == null) {
							logger.error("Невозможно выполнить импорт штатного расписания {}: ID не указан", staff);
							return false;
						}

						String departmentID = staff.getDepartmentId();
						if (departmentID == null) {
							logger.error("Невозможно выполнить импорт штатного расписания {}: ID подразделения не указан", staff);
							return false;
						}

						NodeRef departmentNode = getValueFromTwoMaps(departmentID, newlyCreatedDepartments, existingDepartments);
						if (departmentNode == null) {
							logger.error("Невозможно выполнить импорт штатного расписания {}: подразделение не найдено", staff);
							return false;
						}

						NodeRef employeeNode = null;
						String employeeId = staff.getEmployeeId();
						if (employeeId != null) {
							employeeNode = getValueFromTwoMaps(employeeId, newlyCreatedEmployees, existingEmployees);
							if (employeeNode == null) {
								logger.warn("В штатном расписании {} сотрудник не найден", staff);
							}
						}

						String positionId = staff.getPositionId();
						if (positionId == null) {
							logger.error("Невозможно выполнить импорт штатного расписания {}: ID штатной позиции не указан", staff);
							return false;
						}

						NodeRef positionNode = getValueFromTwoMaps(positionId, newlyCreatedPositions, existingPositions);
						if (positionNode == null) {
							logger.error("Невозможно выполнить импорт штатного расписания {}: штатная позиция не найдена", staff);
							return false;
						}

						NodeRef staffNode = getValueFromTwoMaps(id, newlyCreatedStaff, existingStaff);
						if (staffNode != null) {
							staffNode = updateStaff(staffNode, staff, employeeNode);
						} else {
							staffNode = createStaff(staff, departmentNode, positionNode, employeeNode);
						}

						newlyCreatedStaff.put(id, staffNode);

						logger.info("Успешно выполнен импорт штатного расписания {}", staff);
						return true;
					}

				}, false, true);
			} catch (RuntimeException ex) {
				logger.error("Невозможно выполнить импорт штатного расписания {}; причина: {}", staff, ex.getMessage());
				if (logger.isDebugEnabled()) {
					logger.error("Error", ex);
				}
				result = false;
			}
			if (result) {
				importedCount++;
			}
		}
		logger.info("Импортировано {} штатных расписаний из {}", importedCount, staffsSize);
		logger.info("Закончен импорт штатных расписаний");
	}

	private NodeRef createStaff(Staff staff, NodeRef departmentNode, NodeRef positionNode, NodeRef employeeNode) {
		NodeRef staffNode = orgstructureService.createStaff(departmentNode, positionNode);

		if (employeeNode != null) {
			orgstructureService.includeEmployeeIntoStaff(employeeNode, staffNode, staff.isPrimary());
		}

		if (staff.getDescription() != null) {
			nodeService.setProperty(staffNode, OrgstructureBean.PROP_STAFF_LIST_DESCRIPTION, staff.getDescription());
		}

		helper.addID(staffNode, staff.getId());

		return staffNode;
	}

	private NodeRef updateStaff(NodeRef staffNode, Staff staff, NodeRef employeeNode) {

		if (employeeNode != null) {
			orgstructureService.excludeEmployeeFromStaff(staffNode);
			orgstructureService.includeEmployeeIntoStaff(employeeNode, staffNode, staff.isPrimary());
		}

		if (staff.getDescription() != null) {
			nodeService.setProperty(staffNode, OrgstructureBean.PROP_STAFF_LIST_DESCRIPTION, staff.getDescription());
		}
		nodeService.setProperty(staffNode, IS_ACTIVE, true);
		nodeService.setProperty(staffNode, ExportImportModel.PROP_ID, staff.getId());
		logger.info("Штатное расписание с ID {} уже существует. Существующее штатное расписание {} обновлено и активировано.", staff, staffNode);
		return staffNode;
	}

	@Override
	public void importBusinessRoles(BusinessRoles businessRoles, CreatedItems createdItems) {
		logger.info("Начат импорт бизнес-ролей");

		final Map<String, NodeRef> existingBusinessRoles = helper.getBusinessRolesIDs(orgstructureService.getBusinesRoles(false));
		final Map<String, NodeRef> newlyCreatedBusinessRoles = createdItems.getBusinessRoles();

		final Map<String, NodeRef> existingStaff = helper.getNodeRefsIDs(helper.getAllStaff(true));
		final Map<String, NodeRef> newlyCreatedStaff = createdItems.getStaff();

		final Map<String, NodeRef> existingDepartments = helper.getNodeRefsIDs(helper.getAllOrgUnits(true));
		final Map<String, NodeRef> newlyCreatedDepartments = createdItems.getDepartments();

		final Map<String, NodeRef> existingEmployees = helper.getNodeRefsIDs(helper.getAllEmployees(true));
		final Map<String, NodeRef> newlyCreatedEmployees = createdItems.getEmployees();

		List<BusinessRole> businessRolesList = businessRoles.getBusinessRole();
		final int businessRolesListSize = businessRolesList.size();

		int importedCount = 0;

		for (final BusinessRole businessRole : businessRolesList) {
			boolean result;
			try {
				result = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Boolean>() {

					@Override
					public Boolean execute() throws Throwable {
						logger.info("Импортируется бизнес-роль: {}", businessRole);

						String id = businessRole.getId();
						if (id == null) {
							logger.error("Невозможно выполнить импорт бизнес-роли {}: ID не указан", businessRole);
							return false;
						}

						List<NodeRef> departmentNodes = new ArrayList<>();
						List<String> departmentsIDs = businessRole.getDepartments().getId();
						for (String departmentID : departmentsIDs) {
							NodeRef departmentNode = getValueFromTwoMaps(departmentID, newlyCreatedDepartments, existingDepartments);

							if (departmentNode == null) {
								logger.warn("В бизнес-роли {} подразделение ({}) не найдено", businessRole, departmentID);
							} else {
								departmentNodes.add(departmentNode);
							}
						}

						List<NodeRef> employeeNodes = new ArrayList<>();
						List<String> employeeIDs = businessRole.getEmployees().getId();
						for (String employeeID : employeeIDs) {
							NodeRef employeeNode = getValueFromTwoMaps(employeeID, newlyCreatedEmployees, existingEmployees);

							if (employeeNode == null) {
								logger.warn("В бизнес-роли {} сотрудник ({}) не найден", businessRole, employeeID);
							} else {
								employeeNodes.add(employeeNode);
							}
						}

						List<NodeRef> staffNodes = new ArrayList<>();
						List<String> staffIDs = businessRole.getStaffs().getId();
						for (String staffID : staffIDs) {
							NodeRef staffNode = getValueFromTwoMaps(staffID, newlyCreatedStaff, existingStaff);
							if (staffNode == null) {
								logger.error("В бизнес-роли {} штатное расписание ({}) не найдено", businessRole, staffID);
							} else {
								staffNodes.add(staffNode);
							}
						}

						NodeRef existingBusinessRole = getValueFromTwoMaps(id, newlyCreatedBusinessRoles, existingBusinessRoles);
						if (existingBusinessRole != null) {
							updateBusinessRole(existingBusinessRole, businessRole, employeeNodes, departmentNodes, staffNodes);
						} else {
							logger.info("Создается новая бизнес-роль");

							NodeRef businessRoleNode = createBusinessRole(businessRole, employeeNodes, departmentNodes, staffNodes);
							newlyCreatedBusinessRoles.put(id, businessRoleNode);
						}

						logger.info("Успешно выполнен импорт бизнес-роли {}", businessRole);

						return true;
					}

				}, false, true);
			} catch (RuntimeException ex) {
				logger.error("Невозможно выполнить импорт бизнес-роли {}; причина: {}", businessRole, ex.getMessage());
				if (logger.isDebugEnabled()) {
					logger.error("Error", ex);
				}
				result = false;
			}
			if (result) {
				importedCount++;
			}
		}
		logger.info("Импортировано {} бизнес-ролей из {}", importedCount, businessRolesListSize);
		logger.info("Закончен импорт бизнес-ролей");
	}

	private NodeRef updateBusinessRole(NodeRef businessRoleNode, BusinessRole businessRole, List<NodeRef> employeeNodes, List<NodeRef> departmentNodes, List<NodeRef> staffNodes) {
		final List<NodeRef> existingEmployees = orgstructureService.getEmployeesByBusinessRole(businessRoleNode);

		final ArrayList<NodeRef> addedEmployees = new ArrayList<>(employeeNodes);
		addedEmployees.removeAll(existingEmployees);

		for (NodeRef addedEmployee : addedEmployees) {
			orgstructureService.includeEmployeeIntoBusinessRole(businessRoleNode, addedEmployee);
		}

		final ArrayList<NodeRef> removedEmployees = new ArrayList<>(existingEmployees);
		removedEmployees.removeAll(employeeNodes);

		for (NodeRef removedEmployee : removedEmployees) {
			orgstructureService.excludeEmployeeFromBusinessRole(businessRoleNode, removedEmployee);
		}

		final List<NodeRef> existingDepartments = orgstructureService.getOrganizationElementsByBusinessRole(businessRoleNode, false);

		final ArrayList<NodeRef> addedDepartments = new ArrayList<>(departmentNodes);
		addedDepartments.removeAll(existingDepartments);

		for (NodeRef addedDepartment : addedDepartments) {
			orgstructureService.includeOrgElementIntoBusinessRole(businessRoleNode, addedDepartment);
		}

		final ArrayList<NodeRef> removedDepartments = new ArrayList<>(existingDepartments);
		removedDepartments.removeAll(departmentNodes);

		for (NodeRef removedDepartment : removedDepartments) {
			orgstructureService.excludeOrgElementFromBusinessRole(businessRoleNode, removedDepartment);
		}

		final List<NodeRef> existingStaff = orgstructureService.getOrganizationElementMembersByBusinessRole(businessRoleNode);

		final ArrayList<NodeRef> addedStaffs = new ArrayList<>(staffNodes);
		addedStaffs.removeAll(existingStaff);

		for (NodeRef addedStaff : addedStaffs) {
			orgstructureService.includeOrgElementMemberIntoBusinessRole(businessRoleNode, addedStaff);
		}

		final ArrayList<NodeRef> removedStaffs = new ArrayList<>(existingStaff);
		removedStaffs.removeAll(staffNodes);

		for (NodeRef removedStaff : removedStaffs) {
			orgstructureService.excludeOrgElementMemberFromBusinesssRole(businessRoleNode, removedStaff);
		}

		Map<QName, Serializable> props = nodeService.getProperties(businessRoleNode);
		props.put(ContentModel.PROP_NAME, StringUtils.trim(businessRole.getName()));
		props.put(OrgstructureBean.PROP_BUSINESS_ROLE_IDENTIFIER, StringUtils.trim(businessRole.getId()));
		props.put(OrgstructureBean.PROP_BUSINESS_ROLE_DESCRIPTION, StringUtils.trim(businessRole.getDescription()));
		props.put(OrgstructureBean.PROP_BUSINESS_ROLE_IS_DYNAMIC, businessRole.isDynamic());
		props.put(IS_ACTIVE, true);
		nodeService.setProperties(businessRoleNode, props);

		logger.info("Бизнес роль с ID {} уже существует. Существующая бизнес роль {} обновлена и активирована.", businessRole, businessRoleNode);
		return businessRoleNode;
	}

	private NodeRef createBusinessRole(BusinessRole businessRole, List<NodeRef> employeeNodes, List<NodeRef> departmentNodes, List<NodeRef> staffNodes) {
		String name = StringUtils.trim(businessRole.getName());
		PropertyMap props = new PropertyMap();
		props.put(ContentModel.PROP_NAME, name);
		props.put(OrgstructureBean.PROP_BUSINESS_ROLE_IDENTIFIER, StringUtils.trim(businessRole.getId()));
		props.put(OrgstructureBean.PROP_BUSINESS_ROLE_DESCRIPTION, StringUtils.trim(businessRole.getDescription()));
		props.put(OrgstructureBean.PROP_BUSINESS_ROLE_IS_DYNAMIC, businessRole.isDynamic());

		QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, name);

		NodeRef businessRoleNode = nodeService.createNode(businessRolesRoot, ContentModel.ASSOC_CONTAINS, assocQName, OrgstructureBean.TYPE_BUSINESS_ROLE, props).getChildRef();

		for (NodeRef employee : employeeNodes) {
			orgstructureService.includeEmployeeIntoBusinessRole(businessRoleNode, employee);
		}

		for (NodeRef department : departmentNodes) {
			orgstructureService.includeOrgElementIntoBusinessRole(businessRoleNode, department);
		}

		for (NodeRef staff : staffNodes) {
			orgstructureService.includeOrgElementMemberIntoBusinessRole(businessRoleNode, staff);
		}

		return businessRoleNode;
	}

	private NodeRef getValueFromTwoMaps(String key, Map<String, NodeRef> firstMap, Map<String, NodeRef> secondMap) {
		NodeRef result = firstMap.get(key);

		if (result == null) {
			result = secondMap.get(key);
		}

		return result;
	}

}
