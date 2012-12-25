package ru.it.lecm.orgstructure.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import java.util.List;

/**
 * User: PMelnikov
 * Date: 25.12.12
 * Time: 16:59
 */
public interface OrgstructureBean {

	String ORGSTRUCTURE_NAMESPACE_URI = "http://www.it.ru/lecm/org/structure/1.0";
	String TYPE_ORGANIZATION = "organization";
	String TYPE_DIRECTORY_EMPLOYEES = "employees";
	String TYPE_DIRECTORY_STRUCTURE = "structure";
	String TYPE_DIRECTORY_PERSONAL_DATA = "personal-data-container";
	/**
	 * Корневой узел Организации
	 */
	String ORGANIZATION_ROOT_NAME = "Организация";
	String STRUCTURE_ROOT_NAME = "Структура";
	String EMPLOYEES_ROOT_NAME = "Сотрудники";
	String PERSONAL_DATA_ROOT_NAME = "Персональные данные";
	String DICTIONARIES_ROOT_NAME = "Dictionary";
	String POSITIONS_DICTIONARY_NAME = "Должностные позиции";
	String ROLES_DICTIONARY_NAME = "Роли для рабочих групп";
	String BUSINESS_ROLES_DICTIONARY_NAME = "Бизнес роли";
	QName ASSOC_ORG_BOSS = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "org-boss-assoc");
	QName ASSOC_ORG_LOGO = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "org-logo-assoc");
	QName ASSOC_EMPLOYEE_LINK_EMPLOYEE = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "employee-link-employee-assoc");
	QName ASSOC_ELEMENT_MEMBER_POSITION = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "element-member-position-assoc");
	QName ASSOC_ELEMENT_MEMBER_EMPLOYEE = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "element-member-employee-assoc");
	QName ASSOC_EMPLOYEE_PHOTO = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "employee-photo-assoc");
	QName ASSOC_EMPLOYEE_PERSON_DATA = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "employee-person-data-assoc");
	QName ASSOC_EMPLOYEE_PERSON = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "employee-person-assoc");
	QName ASSOC_BUSINESS_ROLE_EMPLOYEE = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "business-role-employee-assoc");
	QName ASSOC_BUSINESS_ROLE_ORGANIZATION_ELEMENT = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "business-role-organization-element-assoc");
	QName ASSOC_BUSINESS_ROLE_ORGANIZATION_ELEMENT_MEMBER = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "business-role-organization-element-member-assoc");
	QName PROP_STAFF_LIST_IS_BOSS = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "staff-list-is-boss");
	QName PROP_EMP_LINK_IS_PRIMARY = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "employee-link-is-primary");
	QName IS_ACTIVE = QName.createQName("http://www.it.ru/lecm/dictionary/1.0", "active");
	QName TYPE_ORGANIZATION_UNIT = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "organization-unit");
	QName TYPE_STRUCTURE = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "structure");
	QName TYPE_WORK_GROUP = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "workGroup");
	QName TYPE_STAFF_LIST = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "staff-list");
	QName TYPE_WORKFORCE = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "workforce");
	QName TYPE_EMPLOYEE_LINK = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "employee-link");
	QName TYPE_STAFF_POSITION = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "staffPosition");
	QName TYPE_WORK_ROLE = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "workRole");
	QName TYPE_EMPLOYEE = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "employee");
	QName TYPE_PERSONAL_DATA = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "personal-data");
	QName TYPE_BUSINESS_ROLE = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "business-role");

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

	boolean isArchive(NodeRef ref);

	List<NodeRef> getBusinesRoles(boolean onlyActive);

	List<NodeRef> getEmployeesByBusinessRole(NodeRef businessRoleRef);

	List<NodeRef> getOrganizationElementsByBusinessRole(NodeRef businessRoleRef);

	List<NodeRef> getEmployeeLinks(NodeRef employeeRef);

	List<NodeRef> getEmployeeStaffLinks(NodeRef employeeRef);
}
