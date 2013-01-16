package ru.it.lecm.orgstructure.beans;

import java.util.Collection;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import java.util.List;

/**
 * User: PMelnikov
 * Date: 25.12.12
 * Time: 16:59
 */
public interface OrgstructureBean {

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
	public static final QName ASSOC_ORG_BOSS = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "org-boss-assoc");
	public static final QName ASSOC_ORG_LOGO = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "org-logo-assoc");
	public static final QName ASSOC_EMPLOYEE_LINK_EMPLOYEE = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "employee-link-employee-assoc");
	public static final QName ASSOC_ELEMENT_MEMBER_POSITION = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "element-member-position-assoc");
	public static final QName ASSOC_ELEMENT_MEMBER_EMPLOYEE = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "element-member-employee-assoc");
	public static final QName ASSOC_EMPLOYEE_PHOTO = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "employee-photo-assoc");
	public static final QName ASSOC_EMPLOYEE_PERSON_DATA = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "employee-person-data-assoc");
	public static final QName ASSOC_EMPLOYEE_PERSON = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "employee-person-assoc");
	public static final QName ASSOC_BUSINESS_ROLE_EMPLOYEE = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "business-role-employee-assoc");
	public static final QName ASSOC_BUSINESS_ROLE_ORGANIZATION_ELEMENT = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "business-role-organization-element-assoc");
	public static final QName ASSOC_BUSINESS_ROLE_ORGANIZATION_ELEMENT_MEMBER = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "business-role-organization-element-member-assoc");
	public static final QName PROP_STAFF_LIST_IS_BOSS = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "staff-list-is-boss");
	public static final QName PROP_EMP_LINK_IS_PRIMARY = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "employee-link-is-primary");
	public static final QName PROP_BUSINESS_ROLE_IDENTIFIER = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "business-role-identifier");
	public static final QName PROP_BUSINESS_ROLE_IS_DYNAMIC = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "business-role-is-dynamic");

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

	public static final QName TYPE_POSITION = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "position");

	NodeRef getOrganizationRootRef();

	NodeRef ensureOrganizationRootRef();

	NodeRef getOrganizationBoss();

	NodeRef getOrganizationLogo();

	NodeRef getStructureDirectory();

	NodeRef getEmployeesDirectory();

	NodeRef getPersonalDataDirectory();

	List<NodeRef> getWorkGroups(boolean onlyActive);

	List<NodeRef> getSubUnits(NodeRef parent, boolean onlyActive);

	List<NodeRef> getSubUnits(NodeRef parent, boolean onlyActive, boolean includeSubunits);

	boolean hasChild(NodeRef parent, boolean onlyActive);

	NodeRef getParent(NodeRef unitRef);

	boolean isUnit(NodeRef ref);

	boolean isWorkGroup(NodeRef ref);

	boolean isBusinessRole(NodeRef ref);

	boolean isEmployee(NodeRef ref);

	boolean isStaffList(NodeRef ref);

	boolean isWorkForce(NodeRef ref);

	boolean isPosition(NodeRef ref);

	NodeRef getUnitBoss(NodeRef unitRef);

	NodeRef getBossStaff(NodeRef unitRef);

	NodeRef findEmployeeBoss(NodeRef employeeRef);

	List<NodeRef> getUnitStaffLists(NodeRef unitRef);

	NodeRef getEmployeeByPosition(NodeRef positionRef);

	List<NodeRef> getEmployeesByPosition(NodeRef unit, NodeRef position);

	NodeRef getEmployeeByLink(NodeRef linkRef);

	List<NodeRef> getStaffPositions(boolean onlyActive);

	List<NodeRef> getPositionEmployees(NodeRef position);

	List<NodeRef> getWorkGroupEmployees(NodeRef workGroup);

	List<NodeRef> getOrganizationElementEmployees(NodeRef organizationElement);

	NodeRef getEmployeePrimaryStaff(NodeRef employeeRef);

	NodeRef getPositionByEmployeeLink(NodeRef empLink);

	NodeRef getUnitByStaff(NodeRef staffRef);

	NodeRef getEmployeePhoto(NodeRef employeeRef);

	NodeRef getEmployeePersonalData(NodeRef employeeRef);

	List<NodeRef> getEmployeeStaffs(NodeRef employeeRef);

	List<NodeRef> getEmployeeWorkForces(NodeRef employeeRef);

	List<NodeRef> getEmployeeWorkGroups(NodeRef employeeRef);

	NodeRef getWorkGroupByWorkForce(NodeRef workRef);

	List<NodeRef> getWorkRoles(boolean onlyActive);

	NodeRef getEmployeeLinkByPosition(NodeRef positionRef);

	List<NodeRef> getBusinesRoles(boolean onlyActive);

	List<NodeRef> getEmployeesByBusinessRole(NodeRef businessRoleRef);

	List<NodeRef> getOrganizationElementsByBusinessRole(NodeRef businessRoleRef);

	List<NodeRef> getEmployeeLinks(NodeRef employeeRef);

	List<NodeRef> getEmployeeStaffLinks(NodeRef employeeRef);

	NodeRef getCurrentEmployee();

	NodeRef getEmployeeByPerson(String personName);

	NodeRef getEmployeeByPerson(NodeRef person);

	NodeRef getPersonForEmployee(NodeRef employee);

	NodeRef getPositionByStaff(NodeRef staffList);

	NodeRef getRoleByWorkForce(NodeRef staffList);

	/**
	 * получить список подразделений в которые входит сотрудник согласно штатному расписанию
	 * этот список будет содержать или все подразделения или только те, где сотрудник является боссом
	 * @param employeeRef ссылка на фотрудника
	 * @param bossUnitsOnly флаг показывающий что нас интересуют только те подразделения где сотрудник - босс
	 * @return список подразделений или пустой список
	 */
	List<NodeRef> getEmployeeUnits (final NodeRef employeeRef, final boolean bossUnitsOnly);

	/**
	 * получение списка сотрудников в указанном подразделении
	 * @param unitRef ссылка на подразделение
	 * @return список сотрудников в подразделении или пустой список
	 */
//	List<NodeRef> getEmployeesInUnit (final NodeRef unitRef);

	/**
	 * получение списка подчиненных для указанного сотрудника
	 * @param employeeRef сотрудник который является боссом
	 * @return список подчиненных сотрудника по всем подразделениям.
	 *         Если сотрудник не является боссом, то список пустой
	 */
	List<NodeRef> getBossSubordinate (final NodeRef employeeRef);
}
