package ru.it.lecm.orgstructure.beans;

import org.alfresco.repo.cache.SimpleCache;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: PMelnikov
 * Date: 25.12.12
 * Time: 16:59
 */
public interface OrgstructureBean {

	String ORGSTRUCTURE_NAMESPACE_URI = "http://www.it.ru/lecm/org/structure/1.0";
	String TYPE_DIRECTORY_EMPLOYEES = "employees";
	String TYPE_DIRECTORY_STRUCTURE = "structure";
	String TYPE_DIRECTORY_PERSONAL_DATA = "personal-data-container";
	/**
	 * Корневой узел Организации
	 */
	String ORGANIZATION_ROOT_NAME = "Организация";
	String ORGANIZATION_ROOT_ID = "ORGANIZATION_ROOT_ID";
	String STRUCTURE_ROOT_NAME = "Структура";
	String HOLDING_ROOT_NAME = "Холдинг";
	String EMPLOYEES_ROOT_NAME = "Сотрудники";
	String PERSONAL_DATA_ROOT_NAME = "Персональные данные";
	String DICTIONARIES_ROOT_NAME = "Dictionary";
	String POSITIONS_DICTIONARY_NAME = "Должностные позиции";
	String ROLES_DICTIONARY_NAME = "Роли для рабочих групп";
	String BUSINESS_ROLES_DICTIONARY_NAME = "Бизнес роли";
	String DOCUMENT_ROOT_NAME = "Хранилище организации";
	String ORGANIZATION_UNIT_SHARED_FOLDER_NAME = "Общие документы";
	String ORGANIZATION_UNIT_PRIVATE_FOLDER_NAME = "Документы подразделения";
	/**
	 * идентификатор бизнес роли "Технолог"
	 */
	String BUSINESS_ROLE_ENGINEER_ID = "BR_ENGINEER";
	/**
	 * идентификатор бизнес роли "Технолог календарей"
	 */
	String BUSINESS_ROLE_CALENDAR_ENGINEER_ID = "BR_CALENDAR_ENGINEER";
    String BUSINESS_ROLE_ORGSTRUCTURE_ENGINEER_ID = "BR_ORGSTRUCTURE_ENGINEER";
	QName ASSOC_ORG_LOGO = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "org-logo-assoc");
	QName ASSOC_EMPLOYEE_LINK_EMPLOYEE = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "employee-link-employee-assoc");
	QName ASSOC_ELEMENT_MEMBER_POSITION = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "element-member-position-assoc");
	QName ASSOC_ELEMENT_MEMBER_EMPLOYEE = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "element-member-employee-assoc");
	QName ASSOC_EMPLOYEE_PHOTO = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "employee-photo-assoc");
	QName ASSOC_EMPLOYEE_PERSON_DATA = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "employee-person-data-assoc");
	QName ASSOC_EMPLOYEE_PERSON = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "employee-person-assoc");
	QName ASSOC_BUSINESS_ROLE_EMPLOYEE = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "business-role-employee-assoc");
	QName ASSOC_BUSINESS_ROLE_ORGANIZATION_ELEMENT = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "business-role-organization-element-assoc");
	QName ASSOC_ORGANIZATION_UNIT_FOLDER = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "unit-folder-assoc");
	QName ASSOC_BUSINESS_ROLE_ORGANIZATION_ELEMENT_MEMBER = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "business-role-organization-element-member-assoc");
	QName PROP_STAFF_LIST_IS_BOSS = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "staff-list-is-boss");
	QName PROP_STAFF_LIST_DESCRIPTION = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "staff-list-description");
	QName PROP_EMP_LINK_IS_PRIMARY = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "employee-link-is-primary");
	QName PROP_BUSINESS_ROLE_IDENTIFIER = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "business-role-identifier");
	QName PROP_BUSINESS_ROLE_DESCRIPTION = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "business-role-description");
	QName PROP_BUSINESS_ROLE_IS_DYNAMIC = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "business-role-is-dynamic");
	QName PROP_EMPLOYEE_EMAIL = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "employee-email");
	QName PROP_STAFF_POSITION_CODE = QName.createQName(OrgstructureBean.ORGSTRUCTURE_NAMESPACE_URI, "staffPosition-code");
	QName PROP_STAFF_POSITION_NAME_D = QName.createQName(OrgstructureBean.ORGSTRUCTURE_NAMESPACE_URI, "staffPosition-name-d");
	QName PROP_STAFF_POSITION_NAME_G = QName.createQName(OrgstructureBean.ORGSTRUCTURE_NAMESPACE_URI, "staffPosition-name-g");
	QName PROP_UNIT_CODE = QName.createQName(OrgstructureBean.ORGSTRUCTURE_NAMESPACE_URI, "unit-code");
	QName PROP_UNIT_TYPE = QName.createQName(OrgstructureBean.ORGSTRUCTURE_NAMESPACE_URI, "unit-type");
	QName PROP_EMPLOYEE_NUMBER = QName.createQName(OrgstructureBean.ORGSTRUCTURE_NAMESPACE_URI, "employee-number");
	QName PROP_EMPLOYEE_PHONE = QName.createQName(OrgstructureBean.ORGSTRUCTURE_NAMESPACE_URI, "employee-phone");
	QName PROP_EMPLOYEE_SEX = QName.createQName(OrgstructureBean.ORGSTRUCTURE_NAMESPACE_URI, "employee-sex");
	QName PROP_EMPLOYEE_PERSON_LOGIN = QName.createQName(OrgstructureBean.ORGSTRUCTURE_NAMESPACE_URI, "employee-person-login");
	QName PROP_EMPLOYEE_POSITIONS = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "employee-positions");
	QName PROP_EMPLOYEE_FIO_G = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "employee-fio-g");
	QName PROP_EMPLOYEE_FIO_D = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "employee-fio-d");

	QName PROP_EMPLOYEE_FIRST_NAME = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "employee-first-name");
	QName PROP_EMPLOYEE_MIDDLE_NAME = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "employee-middle-name");
	QName PROP_EMPLOYEE_LAST_NAME = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "employee-last-name");
	QName PROP_EMPLOYEE_SHORT_NAME = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "employee-short-name");
	QName PROP_ORG_ELEMENT_SHORT_NAME = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "element-short-name");
    QName PROP_ORG_ELEMENT_FULL_NAME = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "element-full-name");

	QName TYPE_ORGANIZATION_UNIT = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "organization-unit");
	QName TYPE_STRUCTURE = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "structure");
	QName TYPE_WORK_GROUP = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "workGroup");
	QName TYPE_STAFF_LIST = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "staff-list");
	QName TYPE_WORKFORCE = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "workforce");
	QName TYPE_EMPLOYEE_LINK = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "employee-link");
	QName TYPE_STAFF_POSITION = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "staffPosition");
	QName TYPE_WORK_ROLE = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "workRole");
	QName TYPE_EMPLOYEE = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "employee");
	QName TYPE_ORGANIZATION_ELEMENT = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "organization-element");
	QName TYPE_ORGANIZATION_ELEMENT_MEMBER = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "organization-element-member");
	QName TYPE_PERSONAL_DATA = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "personal-data");
	QName TYPE_BUSINESS_ROLE = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "business-role");

	QName TYPE_POSITION = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "position");
	QName TYPE_ORGANIZATION = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "organization");

	/**
	 * &lt;property name="lecm-orgstr:org-tin"&gt;
	 */
	QName PROP_ORG_TIN = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "org-tin");

	/**
	 * &lt;property name="lecm-orgstr:org-kpp"&gt;
	 */
	QName PROP_ORG_KPP= QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "org-kpp");

	/**
	 * Получение директории Организация.
	 * Если такой узел отсутствует - он НЕ создаётся.
	 */
	NodeRef getOrganization();

	/**
	 * Получение сокращенного название организации.
	 */
	String getOrganizationShortName();

	/**
	 * Получение полного название организации.
	 */
	String getOrganizationFullName();

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
	 * Получение директории с Холдингом
	 * @return NodeRef или NULL
	 */
	NodeRef getHolding();

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
	NodeRef getParentUnit(NodeRef unitRef);

	NodeRef getParentUnit(NodeRef unitRef, boolean checkAccess);

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
	 * проверяет что объект является Organization Element
	 */
	boolean isOrganizationElement(NodeRef ref);

    /**
	 * проверяет что объект является Organization Element Member
	 */
	boolean isOrganizationElementMember(NodeRef ref);

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
     * Проверяет, является ли текущий сотрудник руководителем
     */
    boolean isCurrentBoss();

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

    List<NodeRef> getUnitStaffLists(NodeRef unitRef, boolean checkAccess);

	/**
	 * Получение ссылки на сотрудника для объектов "Штатное Расписание и "Участник Рабочей группы"
	 */
	NodeRef getEmployeeByPosition(NodeRef positionRef);
	NodeRef getEmployeeByPosition(NodeRef positionRef, boolean checkAccess);

	/**
	 * Получение списка сотрудников, занимающих в указанном подразделении указанную должностную позицию
	 * @param unit подразделение
	 * @param position доложностная позиция
	 * @return список ссылок на сотрудников
	 */
	List<NodeRef> getEmployeesByPosition(NodeRef unit, NodeRef position);

	/**
	 * Получение списка сотрудников, занимающих указанную должностную позицию
	 * @param position доложностная позиция
	 * @return список ссылок на сотрудников
	 */
	List<NodeRef> getEmployeesByPosition(NodeRef position);

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
	 * Получение ссылки на сотрудника для роли в рабочей группе
	 */
	NodeRef getEmployeeLinkByWorkRole(NodeRef role);

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

	List<NodeRef> getEmployeesByBusinessRole(NodeRef businessRoleRef, boolean withDelegation);

	/**
	 * Получение перечня сотрудников, исполняющих определенную Бизнес-роль
	 */
	List<NodeRef> getEmployeesByBusinessRole(String businessRoleId);

	List<NodeRef> getEmployeesByBusinessRole(String businessRoleId, boolean withDelegation);

	/**
	 * Получение перечня организационных элементов (подразделений и рабочих групп),
	 * исполняющих определенную Бизнес-роль (включая вложенные)
	 */
	List<NodeRef> getOrganizationElementsByBusinessRole(NodeRef businessRoleRef);

	/**
	 * Получение перечня организационных элементов (подразделений и рабочих групп),
	 * исполняющих определенную Бизнес-роль
	 * @param subUnits указывает, включать ли вложенные подразделения
	 */
	List<NodeRef> getOrganizationElementsByBusinessRole(NodeRef businessRoleRef, boolean subUnits);

	/**
	 * Получение перечня позиций (штатное расписание), исполняющих определенную бизнес-роль
	 * @param businessRoleRef ссылка на бизнес-роль
	 * @return список ссылок на штатное расписание
	 */
	List<NodeRef> getOrganizationElementMembersByBusinessRole(NodeRef businessRoleRef);

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
	NodeRef getEmployeeByPerson(String personName, boolean checkAccess);

	/**
	 * Получение текущего сотрудника по NodeRef пользователя
	 */
	NodeRef getEmployeeByPerson(NodeRef person);
	NodeRef getEmployeeByPerson(NodeRef person, boolean checkAccess);

	/**
	 * Получение пользователя сотрудника
	 */
	NodeRef getPersonForEmployee(NodeRef employee);

	/**
	 * Получить словарную должность для указанной Штатной Должностной Позиии.
	 * @param staffList должностная позиция
	 * @return
	 */
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
	 * получить список подразделений в которые входит сотрудник согласно штатному расписанию
	 * этот список будет содержать или все подразделения или только те, где сотрудник является боссом
	 * @param employeeRef ссылка на фотрудника
	 * @param bossUnitsOnly флаг показывающий что нас интересуют только те подразделения где сотрудник - босс
	 * @param checkAccess учитывать доступ к организации
	 * @return список подразделений или пустой список
	 */
	List<NodeRef> getEmployeeUnits (final NodeRef employeeRef, final boolean bossUnitsOnly, final boolean checkAccess);

	/**
	 * получение списка подчиненных для указанного сотрудника
	 * @param employeeRef сотрудник который является боссом
	 * @return список подчиненных сотрудника по всем подразделениям.
	 *         Если сотрудник не является боссом, то список пустой
	 */
	List<NodeRef> getBossSubordinate (final NodeRef employeeRef);


    /**
     * получение списка подчиненных для указанного сотрудника c учётом делегирования
     * @param employeeRef сотрудник который является боссом
     * @param withDelegation учитывать ли делегирование?
     * @return список подчиненных сотрудника по всем подразделениям.
     *         Если сотрудник не является боссом и не имеет делегирований, то список пустой
     */
    List<NodeRef> getBossSubordinate (final NodeRef employeeRef, final boolean withDelegation);

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
	 */
	void excludeOrgElementMemberFromBusinesssRole (final NodeRef businesssRoleRef, final NodeRef employeeRef);

	/**
	 * получить бизнес роль "Технолог" из общего справочника бизнес ролей
	 * @return NodeRef на бизнес роль "Технолог" или null если таковой бизнес роли нет
	 */
	NodeRef getBusinessRoleDelegationEngineer ();

	/**
	 * получить бизнес роль "Технолог календарей" из общего справочника бизнес ролей
	 * @return NodeRef на бизнес роль "Технолог календарей" или null если таковой бизнес роли нет
	 */
	NodeRef getBusinessRoleCalendarEngineer();

	/**
	 * уволить сотрудника (деактивировать)
	 * меняет active true -> false
	 * @param employeeRef ссылка на сотрудника
	 */
	void fireEmployee (final NodeRef employeeRef);

	/**
	 * восстановить сотрудника (активировать)
	 * меняет active false -> true
	 * @param employeeRef ссылка на сотрудника
	 */
	void restoreEmployee (final NodeRef employeeRef);

	/**
	 * для указанной позиции в штатном расписании проставить или снять флаг "руководящая позиция"
	 * Если в отделе уже есть руководящая позиция то флаг проставлен не будет
	 * @param orgElementMemberRef ссылка на штатное расписание
	 * @param isBoss true - мы хотим сделать позицию руководящей, false - мы хотим снять этот флаг
	 */
	void makeStaffBossOrEmployee (final NodeRef orgElementMemberRef, final boolean isBoss);

	/**
	 * создать в подразделении штатное расписание с указанной должностью
	 * @param orgElement орг единица - подразделение
	 * @param staffPosition позиция - должностная позиция
	 * @return штатное расписание или null если мы передали что-то другое что не является подразделением и должностной позицией
	 */
	NodeRef createStaff (final NodeRef orgElement, final NodeRef staffPosition);
	/**
	 * назначить сотрудника на штатное расписание
	 * @param employeeRef ссылка на сотрудника
	 * @param orgElementMemberRef ссылка на штатное расписание
	 * @param isPrimary флаг "является основной"
	 */
	void includeEmployeeIntoStaff (final NodeRef employeeRef, final NodeRef orgElementMemberRef, final boolean isPrimary);

	/**
	 * снять сотрудника с должности
	 * @param orgElementMemberRef ссылка на штатное расписание
	 */
	void excludeEmployeeFromStaff (final NodeRef orgElementMemberRef);

	/**
	 * переместить подразделение unitRef в подразделение parentUnitRef
	 * если parentUnitRef == null то тогда двигаем в корень
	 * @param unitRef подразделение которое двигаем
	 * @param parentUnitRef куда будем двигать, новое родительское подразделение
	 * @return ChildAssociationRef указывающий на родителя и детишку после перемещения
	 */
	ChildAssociationRef moveOrgElement (final NodeRef unitRef, final NodeRef parentUnitRef);

	/**
	 * Проверка, занимает ли сотрудник руководящую позицию.
	 * РАБОТАЕТ БЕЗ УЧЕТА ДЕЛЕГИРОВАНИЯ!
	 * @param nodeRef NodeRef сотрудника (lecm-orgstr:employee)
	 * @return true если сотрудник занимает где-либо руководящую позицию.
	 */
	boolean isBoss(final NodeRef nodeRef);

	/**
	 * Проверка, имеет ли сотрудник роль "Технолог календарей".
	 *
	 * @param nodeRef NodeRef сотрудника (lecm-orgstr:employee)
	 * @return true если сотрудник имеет роль "Технолог календарей".
	 */
	boolean isCalendarEngineer(final NodeRef nodeRef);

	/**
	 * является ли указанный пользователь Технологом делегирования
	 * @param employeeRef ссылка на сотрудника
	 * @return true/false
	 */
	boolean isDelegationEngineer (NodeRef employeeRef);

	/**
	 * имеет ли текущий пользователь у себя в подчинении другого пользователя
	 * РАБОТАЕТ БЕЗ УЧЕТА ДЕЛЕГИРОВАНИЯ!
	 * @param bossRef employee который является боссом
	 * @param subordinateRef employee который является подчиненным
	 * @return true/false Если bossRef == subordinateRef то возвращается true
	 */
	boolean hasSubordinate (NodeRef bossRef, NodeRef subordinateRef);

	/**
	 * имеет ли текущий сотрудник указанную бизнес-роль
	 * @param businessRoleIdentifier идентификатор бизнес-роли
	 * @return true если сотрудник имеет роль
	 */
	boolean isCurrentEmployeeHasBusinessRole(String businessRoleIdentifier);

	/**
	 * имеет ли сотрудник указанную бизнес-роль
	 * РАБОТАЕТ БЕЗ УЧЕТА ДЕЛЕГИРОВАНИЯ!
	 * @param employeeRef ссылка на сотрудника
	 * @param businessRoleIdentifier идентификатор бизнес-роли
	 * @return true если сотрудник имеет роль
	 */
	boolean isEmployeeHasBusinessRole(NodeRef employeeRef, String businessRoleIdentifier);

	/**
	 *
	 * @return true если вызов произошел от имени системы
	 */
	boolean isCurrentUserTheSystemUser();

	String getEmployeeLogin(NodeRef employee);

        public Set<NodeRef> getEmployeeDirectRoles(NodeRef employeeRef);

        public Set<NodeRef> getEmployeeUnitRoles(NodeRef employeeRef);

        public Set<NodeRef> getEmployeeWGRoles(NodeRef employeeRef);

        public Set<NodeRef> getEmployeeDPRoles(NodeRef employeeRef);

	List<NodeRef> getEmployeeRoles(NodeRef employeeRef);

        Map<NodeRef, List<NodeRef>> getEmployeeDelegatedRolesWithOwner(NodeRef employeeRef);

    List<NodeRef> getEmployeeRoles(NodeRef employeeRef, boolean includeDelegatedRoles);

	public List<NodeRef> getEmployeeRoles(NodeRef employeeRef, boolean includeDelegatedRoles, boolean inheritSubordinatesRoles);

	/**
	 * получить список бизнес ролей сотрудника согласно действующему делегированию
	 * @param employeeRef ссылка на сотрудника
	 * @return список NodeRef на бизнес-роли, которые были делегированы сотруднику
	 */
	List<NodeRef> getEmployeeRolesWithDelegation(final NodeRef employeeRef);

    /**
     *
     * @return NodeRef бизнес-роли по идентификатору
     */
    NodeRef getBusinessRoleByIdentifier(final String businessRoleIdentifier);

	/**
	 * Проверка, занимает ли сотрудник руководящую позицию,
	 * с учетом или без учета настроек делегирования
	 * @param nodeRef NodeRef сотрудника (lecm-orgstr:employee)
	 * @param withDelegation true - учитывать параметры делегирования, false не учитывать
	 * @return true если сотрудник занимает где-либо руководящую позицию.
	 */
	boolean isBoss(final NodeRef nodeRef, final boolean withDelegation);

	/**
	 * имеет ли текущий пользователь у себя в подчинении другого пользователя
	 * с учетом или без учета настроек делегирования
	 * @param bossRef employee который является боссом
	 * @param subordinateRef employee который является подчиненным
	 * @param withDelegation true - учитывать параметры делегирования, false не учитывать
	 * @return true/false Если bossRef == subordinateRef то возвращается true
	 */
	boolean hasSubordinate (NodeRef bossRef, NodeRef subordinateRef, final boolean withDelegation);

	/**
	 * имеет ли сотрудник указанную бизнес-роль
	 * с учетом или без учета настроек делегирования
	 * @param employeeRef ссылка на сотрудника
	 * @param businessRoleIdentifier идентификатор бизнес-роли
	 * @param withDelegation true - учитывать параметры делегирования, false не учитывать
	 * @return true если сотрудник имеет роль
	 */
	boolean isEmployeeHasBusinessRole(NodeRef employeeRef, String businessRoleIdentifier, final boolean withDelegation);

	public boolean isEmployeeHasBusinessRole (NodeRef employeeRef, String businessRoleIdentifier, boolean withDelegation, boolean inheritSubordinatesRoles);

    /**
     * Получает список сотрудников оформивших отстутсвие
     * @param nodeRef Сотрудник / Позация в структуре предприятия / Подразделение
     * @return
     */
    List<NodeRef> getNodeRefEmployees(NodeRef nodeRef);

    /**
     * Возвращает список всех сотрудников
     * @return
     */
    List<NodeRef> getAllEmployees();
    /**
     * Получение подразделения, где сотрудник числится на основной должностной позиции
     * @param employeeRef ссылка на сотрудника
     * @return unit
     */
    NodeRef getPrimaryOrgUnit(NodeRef employeeRef);

    /**
     * имеет ли текущий пользователь у себя в подчинении другого пользователя
     * @param bossRef руководитель
     * @param subordinateRef сотрудник
     * @param checkPrimary если true - учитывается только руководство по основной должностной позиции
     * @return true, если сотрудник  bossRef является руководителем сотрудника subordinateRef.
     */
    boolean isBossOf(NodeRef bossRef, NodeRef subordinateRef, boolean checkPrimary);

    NodeRef getUnitByCode(String code);

    NodeRef getUnitBoss(String unitCode);

    NodeRef getRootUnit();

    List<NodeRef> getOrgRoleEmployees(NodeRef unitRef);

    boolean isWorkRole(NodeRef ref);

    List<NodeRef> getEmployeeLinks(NodeRef employeeRef, boolean includeArchived);

    List<NodeRef> getUnitEmployees(NodeRef unitRef);
    List<NodeRef> getUnitEmployees(NodeRef unitRef, boolean checkAccess);

    /**
     *  Возвращает Authority для папки подразделения
     *
     */
    public String getOrgstructureUnitAuthority(NodeRef unit, boolean shared);

	public boolean isDynamicBusinessRole(NodeRef roleRef);

	public String getBusinessRoleIdentifier(NodeRef roleRef);

    /**
     *  Возвращает организацию для сотрудника
     *
     */
    public NodeRef getEmployeeOrganization(NodeRef employee);

    /**
     *  Возвращает организацию для пользователя
     *
     */
    public NodeRef getUserOrganization(String userName);

    /**
     *  Возвращает организацию для орг элемента
     *
     */
    public NodeRef getOrganization(NodeRef orgElement);

    public NodeRef getUnitByOrganization(NodeRef organization);

    public SimpleCache<String, NodeRef> getUserOrganizationsCache();

    public boolean hasAccessToOrgElement(String userName, NodeRef orgElement);

    public boolean hasAccessToOrgElement(NodeRef orgElement);
    public boolean hasAccessToOrgElement(NodeRef orgElement, boolean doNotAccessWithEmpty);

    List<NodeRef> getOrganizationEmployees(NodeRef organizationRef);

    public List<NodeRef> getSubUnits(NodeRef parent, boolean onlyActive, boolean includeSubunits, boolean checkAccess);

    public boolean hasOrgChilds(NodeRef unit, boolean checkAccess);

    boolean hasGlobalOrganizationsAccess();

	public List<NodeRef> getCurrentEmployeeHighestUnits();
	
	/**
	 *  Привязать для всех активных Сотрудников Login/userId к sgME группам
	 */
	public void autoEmployeesTie();
}
