package ru.it.lecm.orgstructure.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import java.util.List;
import org.alfresco.service.cmr.repository.AssociationRef;

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
	QName PROP_BUSINESS_ROLE_IDENTIFIER = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "business-role-identifier");
	QName PROP_BUSINESS_ROLE_IS_DYNAMIC = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "business-role-is-dynamic");

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

	QName TYPE_POSITION = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "position");

	/**
	 * Получение директории Организация.
	 * Если такой узел отсутствует - он НЕ создаётся.
	 */
	NodeRef getOrganizationRootRef();

	/**
	 * Получение узла Организация, в котором хрянится информация об Организации.
	 * Если такой узел отсутствует - он создаётся автоматически (внутри /CompanyHome).
	 *
	 * @return NodeRef
	 */
	NodeRef ensureOrganizationRootRef();

	/**
	 * Получение руководителя Организации
	 *
	 * @return NodeRef или NULL если руководитель не задан
	 */
	NodeRef getOrganizationBoss();

	/**
	 * Получение логотипа Организации
	 *
	 * @return NodeRef или NULL если логотип не задан
	 */
	NodeRef getOrganizationLogo();

	/**
	 * Получение Директории с Оргструктурой
	 *
	 * @return NodeRef или NULL
	 */
	NodeRef getStructureDirectory();

	/**
	 * Получение Директории с Сотрудниками
	 *
	 * @return NodeRef или NULL
	 */
	NodeRef getEmployeesDirectory();

	/**
	 * Получение Директории с Персональными данными
	 *
	 * @return NodeRef или NULL
	 */
	NodeRef getPersonalDataDirectory();

	/**
	 * Получение списка Рабочих групп для Организации
	 */
	List<NodeRef> getWorkGroups(boolean onlyActive);

	/**
	 * Получение списка дочерних подразделений
	 */
	List<NodeRef> getSubUnits(NodeRef parent, boolean onlyActive);

	/**
     * Получение списка дочерних подразделений
     */
	List<NodeRef> getSubUnits(NodeRef parent, boolean onlyActive, boolean includeSubunits);

	/**
	 * Проверка есть ли у подразделения дочерние элементы
	 */
	boolean hasChild(NodeRef parent, boolean onlyActive);

	/**
	 * Получение родительского подразделения
	 */
	NodeRef getParent(NodeRef unitRef);

	/**
	 * проверяет что объект является подразделением
	 */
	boolean isUnit(NodeRef ref);

	/**
	 * проверяет что объект является рабочей группой
	 */
	boolean isWorkGroup(NodeRef ref);

	/**
	 * проверяет что объект является бизнес ролью
	 */
	boolean isBusinessRole(NodeRef ref);

	/**
	 * проверяет что объект является сотрудником
	 */
	boolean isEmployee(NodeRef ref);

	/**
	 * проверяет что объект является штатным расписанием
	 */
	boolean isStaffList(NodeRef ref);

	/**
	 * проверяет что объект является Участником рабочей группы
	 */
	boolean isWorkForce(NodeRef ref);

	/**
	 * проверяет что объект является должностью
	 */
	boolean isPosition(NodeRef ref);

	/**
	 * Получение руководителя подразделения
	 */
	NodeRef getUnitBoss(NodeRef unitRef);

	/**
	 * Получение Штатного расписания, содержащего руководящую позицию
	 */
	NodeRef getBossStaff(NodeRef unitRef);

	/**
	 * Получение руководителя сотрудника
	 */
	NodeRef findEmployeeBoss(NodeRef employeeRef);

	/**
	 * Получение списка Штатных Расписаний для Подразделения
	 */
	List<NodeRef> getUnitStaffLists(NodeRef unitRef);

	/**
	 * Получение ссылки на сотрудника для объектов "Штатное Расписание и "Участник Рабочей группы"
	 */
	NodeRef getEmployeeByPosition(NodeRef positionRef);

	/**
	 * Получение списка сотрудников, занимающих в указанном подразделении указанную должностную позицию
	 * @param unit подразделение
	 * @param position доложностная позиция
	 * @return список ссылок на сотрудников
	 */
	List<NodeRef> getEmployeesByPosition(NodeRef unit, NodeRef position);

	/**
	 * Получение ссылки на сотрудника из объекта (lecm-orgstr:employee-link)
	 */
	NodeRef getEmployeeByLink(NodeRef linkRef);

	/**
	 * Получение списка должностных позиций в системе
	 */
	List<NodeRef> getStaffPositions(boolean onlyActive);

	/**
	 * Получение перечня сотрудников, которые занимают должностную позицию
	 */
	List<NodeRef> getPositionEmployees(NodeRef position);

	/**
	 * Получение перечня сотрудников, участвующих в рабочей группе
	 */
	List<NodeRef> getWorkGroupEmployees(NodeRef workGroup);

	/**
	 * Получение перечня сотрудников подразделения либо рабочей группы
	 */
	List<NodeRef> getOrganizationElementEmployees(NodeRef organizationElement);

	/**
	 * Получение основной должностной позиции (штатного расписания) у сотрудника
	 */
	NodeRef getEmployeePrimaryStaff(NodeRef employeeRef);

	/**
	 * Получение штатного расписания или участника Рабочей группы по ссылке на сотрудника
	 */
	NodeRef getPositionByEmployeeLink(NodeRef empLink);

	/**
	 * Получение подразделения, к которому относится штатное расписание
	 */
	NodeRef getUnitByStaff(NodeRef staffRef);

	/**
	 * Получение фотографии сотрудника
	 */
	NodeRef getEmployeePhoto(NodeRef employeeRef);

	/**
	 * Получение персональных данных сотрудника
	 */
	NodeRef getEmployeePersonalData(NodeRef employeeRef);

	/**
	 * Получение списка должностных позиций сотрудника
	 */
	List<NodeRef> getEmployeeStaffs(NodeRef employeeRef);

	/**
	 * Получение списка Участников Рабочих Групп для сотрудника
	 */
	List<NodeRef> getEmployeeWorkForces(NodeRef employeeRef);

	/**
	 * Получение списка Рабочих Групп, в которых участвует сотрудник
	 */
	List<NodeRef> getEmployeeWorkGroups(NodeRef employeeRef);

	/**
	 * Получение рабочей группы, к которому относится участник
	 */
	NodeRef getWorkGroupByWorkForce(NodeRef workRef);

	/**
	 * Получение списка ролей в рабочих группах
	 */
	List<NodeRef> getWorkRoles(boolean onlyActive);

	/**
	 * Получение ссылки на сотрудника для Позиции (Штатного расписания или Участника Рабочей группы)
	 */
	NodeRef getEmployeeLinkByPosition(NodeRef positionRef);

	/**
	 * Получение полного перечня бизнес ролей
	 */
	List<NodeRef> getBusinesRoles(boolean onlyActive);

	/**
	 * Получение перечня сотрудников, исполняющих определенную Бизнес-роль
	 */
	List<NodeRef> getEmployeesByBusinessRole(NodeRef businessRoleRef);

	/**
	 * Получение перечня организационных элементов (подразделений и рабочих групп),
	 * исполняющих определенную Бизнес-роль (включая вложенные)
	 */
	List<NodeRef> getOrganizationElementsByBusinessRole(NodeRef businessRoleRef);

	/**
	 * Получение ссылок на сотрудника
	 */
	List<NodeRef> getEmployeeLinks(NodeRef employeeRef);

	/**
	 * Получение ссылок на сотрудника в Штатных расписаниях
	 */
	List<NodeRef> getEmployeeStaffLinks(NodeRef employeeRef);

	/**
	 * Получение текущего сотрудника
	 */
	NodeRef getCurrentEmployee();

	/**
	 * Получение текущего сотрудника по имени пользователя
	 */
	NodeRef getEmployeeByPerson(String personName);

	/**
	 * Получение текущего сотрудника по NodeRef пользователя
	 */
	NodeRef getEmployeeByPerson(NodeRef person);

	/**
	 * Получение пользователя сотрудника
	 */
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
	 * получение списка подчиненных для указанного сотрудника
	 * @param employeeRef сотрудник который является боссом
	 * @return список подчиненных сотрудника по всем подразделениям.
	 *         Если сотрудник не является боссом, то список пустой
	 */
	List<NodeRef> getBossSubordinate (final NodeRef employeeRef);

	/**
	 * включить сотрудника в бизнес роль
	 * @param businesssRoleRef ссылка на бизнес роль
	 * @param employeeRef ссылка на сотрудника
	 * @return установленная ассоциация между сотрудников и бизнес ролью
	 */
	AssociationRef includeEmployeeIntoBusinessRole (final NodeRef businesssRoleRef, final NodeRef employeeRef);

	/**
	 * включить организационную единицу (подразделение или рабочую группу) в бизнес роль
	 * @param businesssRoleRef ссылка на бизнес роль
	 * @param employeeRef ссылка на подразделение или рабочую группу
	 * @return установленная ассоциация между сотрудников и орг единицей
	 */
	AssociationRef includeOrgElementIntoBusinessRole (final NodeRef businesssRoleRef, final NodeRef orgElementRef);

	/**
	 * включить позицию в структуре предприятия (участник рабочей группы, штатное расписание)
	 * @param businesssRoleRef ссылка на бизнес роль
	 * @param orgElementMemberRef ссылка на участника рабочей группы или штатное расписание
	 * @return
	 */
	AssociationRef includeOrgElementMemberIntoBusinessRole (final NodeRef businesssRoleRef, final NodeRef orgElementMemberRef);

	/**
	 * исключить сотрудника из бизнес роли
	 * @param businesssRoleRef ссылка на бизнес роль
	 * @param employeeRef ссылка на сотрудника
	 */
	void excludeEmployeeFromBusinessRole (final NodeRef businesssRoleRef, final NodeRef employeeRef);

	/**
	 * исключить орг единицу (подразделение или рабочую группу) из бизнес роли
	 * @param businesssRoleRef ссылка на бизнес роль
	 * @param employeeRef ссылка на подразделение или рабочую группу
	 */
	void excludeOrgElementFromBusinessRole (final NodeRef businesssRoleRef, final NodeRef employeeRef);

	/**
	 * исключить позицию в структуре предприятия (участник рабочей группы, штатное расписание) из бизнес роли
	 * @param businesssRoleRef ссылка на бизнес роль
	 * @param orgElementMemberRef ссылка на участника рабочей группы или штатное расписание
	 */
	void excludeOrgElementMemberFromBusinesssRole (final NodeRef businesssRoleRef, final NodeRef employeeRef);
}
