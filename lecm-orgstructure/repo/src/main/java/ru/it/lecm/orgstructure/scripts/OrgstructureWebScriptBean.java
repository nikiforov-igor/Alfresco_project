package ru.it.lecm.orgstructure.scripts;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.model.Repository;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.springframework.extensions.surf.util.ParameterCheck;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author dbashmakov
 *         Date: 27.11.12
 *         Time: 17:08
 */
public class OrgstructureWebScriptBean extends BaseScopableProcessorExtension {

	public static final String NODE_REF = "nodeRef";
	public static final String PAGE = "page";
	public static final String ITEM_TYPE = "itemType";
	public static final String TITLE = "title";
	public static final String IS_LEAF = "isLeaf";
	public static final String NAME_PATTERN = "namePattern";

	public static final String ELEMENT_FULL_NAME = "element-full-name";
	public static final String ELEMENT_FULL_NAME_PATTERN = "lecm-orgstr_element-full-name";

	private static Log logger = LogFactory.getLog(OrgstructureWebScriptBean.class);
	public static final String POSITIONS_DICTIONARY_NAME = "Должностные позиции";
	public static final String ORG_POSITIONS = "org-positions";
	public static final String ORG_ROLES = "org-roles";
	public static final String ORG_EMPLOYEES = "org-employees";
	public static final String STAFF_LIST = "staff-list";
	public static final String WORK_GROUPS = "work-groups";
	public static final String BUSINESS_ROLES = "business-roles";

	private static final QName DEFAULT_NAME = ContentModel.PROP_NAME;
	private static final QName IS_ACTIVE = QName.createQName("http://www.it.ru/lecm/dictionary/1.0", "active");

	public static final String TYPE_EMPLOYEE = "employee";
	public static final String TYPE_STRUCTURE = "structure";
	public static final String TYPE_WRK_GROUP = "workGroup";
	public static final String TYPE_UNIT = "organization-unit";
	public static final String TYPE_STAFF_LIST = "staff-list";
	public static final String TYPE_POSITION = "staffPosition";
	public static final String TYPE_ROLE = "workRole";
	public static final String TYPE_BUSINESS_ROLE = "business-role";

	/**
	 * Service registry
	 */
	protected ServiceRegistry services;

	/**
	 * Repository helper
	 */
	protected Repository repository;

	private OrgstructureBean orgstructureService;

	/**
	 * Set the service registry
	 *
	 * @param services the service registry
	 */
	public void setServiceRegistry(ServiceRegistry services) {
		this.services = services;
	}

	/**
	 * Set the repository helper
	 *
	 * @param repository the repository helper
	 */
	public void setRepositoryHelper(Repository repository) {
		this.repository = repository;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	/**
	 * Возвращает ноду Организация или создает дерево директорий Организации
	 * /**
	 * Структура директорий
	 * Организация
	 * ---Структура
	 * ---Сотрудники
	 * ---Персональные данные
	 *
	 * @return Созданную ноду Организация или Null, если произошла ошибка
	 */
	public ScriptNode getOrganization() {
		NodeRef organization = orgstructureService.ensureOrganizationRootRef();
		return new ScriptNode(organization, services, getScope());
	}

	/**
	 * Получаем список "корневых" объектов для меню в Оргструктуре
	 *
	 * @return Текстовое представление JSONArrray c объектами
	 */
	public String getRoots() {
		JSONArray nodes = new JSONArray();
		NodeService nodeService = services.getNodeService();
		repository.init();
		JSONObject root;
		NodeRef organizationRef = orgstructureService.getOrganizationRootRef();
		try {
			// Добавить Организацию
			root = new JSONObject();
			root.put(NODE_REF, organizationRef);
			root.put(PAGE, OrgstructureBean.TYPE_ORGANIZATION);
			root.put(ITEM_TYPE, OrgstructureBean.TYPE_ORGANIZATION);
			root.put(NAME_PATTERN, ELEMENT_FULL_NAME_PATTERN);

			nodes.put(root);

			final NodeRef companyHome = repository.getCompanyHome();
			NodeRef dictionariesRoot = nodeService.getChildByName(companyHome, ContentModel.ASSOC_CONTAINS, OrgstructureBean.DICTIONARIES_ROOT_NAME);

			NodeRef positions = nodeService.getChildByName(dictionariesRoot, ContentModel.ASSOC_CONTAINS, POSITIONS_DICTIONARY_NAME);
			// Добавить справочник Должности
			root = new JSONObject();
			root.put(NODE_REF, positions.toString());
			root.put(ITEM_TYPE, TYPE_POSITION);
			root.put(PAGE, ORG_POSITIONS);
			nodes.put(root);

			NodeRef roles = nodeService.getChildByName(dictionariesRoot, ContentModel.ASSOC_CONTAINS, "Роли для рабочих групп");
			// Добавить справочник Роли в рабочих группах
			root = new JSONObject();
			root.put(NODE_REF, roles.toString());
			root.put(ITEM_TYPE, TYPE_ROLE);
			root.put(PAGE, ORG_ROLES);
			nodes.put(root);

			//Добавить справочник Бизнес Роли
			NodeRef businessRoles = nodeService.getChildByName (dictionariesRoot, ContentModel.ASSOC_CONTAINS, "Бизнес роли");
			root = new JSONObject();
			root.put(NODE_REF, businessRoles.toString());
			root.put(ITEM_TYPE, TYPE_BUSINESS_ROLE);
			root.put(PAGE, BUSINESS_ROLES);

			nodes.put(root);
		} catch (JSONException e) {
			logger.error(e);
		}

		List<ChildAssociationRef> childs = nodeService.getChildAssocs(organizationRef);
		for (ChildAssociationRef childAssociationRef : childs) {
			QName qType = nodeService.getType(childAssociationRef.getChildRef());
			String qTypeLocalName = qType.getLocalName();
			try {
				NodeRef cRef = childAssociationRef.getChildRef();

				root = new JSONObject();
				root.put(NODE_REF, cRef.toString());

				if (qTypeLocalName.equals(OrgstructureBean.TYPE_DIRECTORY_EMPLOYEES)) {
					root.put(PAGE, ORG_EMPLOYEES);
					root.put(ITEM_TYPE, TYPE_EMPLOYEE);
					root.put(NAME_PATTERN, "lecm-orgstr_employee-first-name[1],lecm-orgstr_employee-middle-name[1],lecm-orgstr_employee-last-name");
				} else if (qTypeLocalName.equals(OrgstructureBean.TYPE_DIRECTORY_STRUCTURE)) {
					root.put(NODE_REF, "NOT_LOAD");
					root.put(PAGE, "orgstructure");
					root.put(ITEM_TYPE, TYPE_UNIT);
					root.put(NAME_PATTERN, ELEMENT_FULL_NAME_PATTERN);
				}
				nodes.put(root);
				//Добавить Штатное расписание, Рабочие группы
				if (qTypeLocalName.equals(OrgstructureBean.TYPE_DIRECTORY_STRUCTURE)) {
					root = new JSONObject();
					root.put(NODE_REF, cRef.toString());
					root.put(PAGE, STAFF_LIST);
					root.put(ITEM_TYPE, TYPE_STAFF_LIST);
					root.put(NAME_PATTERN, ELEMENT_FULL_NAME_PATTERN);
					nodes.put(root);

					root = new JSONObject();
					root.put(NODE_REF, cRef.toString());
					root.put(PAGE, WORK_GROUPS);
					root.put(ITEM_TYPE, TYPE_WRK_GROUP);
					root.put(NAME_PATTERN, ELEMENT_FULL_NAME_PATTERN);
					nodes.put(root);
				}
			} catch (JSONException e) {
				logger.error(e);
			}
		}
		return nodes.toString();
	}

	/**
	 * Получаем список Дочерних объектов в офрмате, используемом в дереве Оргструктуры
	 *
	 * @return Текстовое представление JSONArrray c объектами
	 */
	public String getStructure(final String type, final String ref) {
		JSONArray nodes = new JSONArray();
		NodeService nodeService = services.getNodeService();
		if (ref != null) {
			final NodeRef currentRef = new NodeRef(ref);
			if (type.equalsIgnoreCase(TYPE_UNIT)) {// построить дерево подразделений
				Set<QName> units = new HashSet<QName>();
				units.add(QName.createQName(OrgstructureBean.ORGSTRUCTURE_NAMESPACE_URI, TYPE_UNIT));
				// получаем список только Подразделений (внутри могут находиться другие объекты (Рабочие группы))
				List<ChildAssociationRef> childs = nodeService.getChildAssocs(currentRef, units);
				for (ChildAssociationRef child : childs) {
					Boolean isActive = (Boolean) nodeService.getProperty(child.getChildRef(), IS_ACTIVE);
					isActive = isActive != null ? isActive : Boolean.TRUE; // if property not filled -> active = true default
					if (isActive) { // выводим только активные подразделения
						JSONObject unit = new JSONObject();
						try {
							unit.put(NODE_REF, child.getChildRef().toString());
							unit.put(ITEM_TYPE, TYPE_UNIT);
							unit.put(TITLE, getElementName(
									nodeService, child.getChildRef(), QName.createQName(OrgstructureBean.ORGSTRUCTURE_NAMESPACE_URI, ELEMENT_FULL_NAME)));
							unit.put(IS_LEAF, !orgstructureService.hasChild(child.getChildRef(), true));
							nodes.put(unit);
						} catch (JSONException e) {
							logger.error(e);
						}
					}
				}
			} else if (type.equalsIgnoreCase(OrgstructureBean.TYPE_ORGANIZATION)) { //Вывести директорию "Структура"
				NodeRef structure = nodeService.getChildByName(currentRef, ContentModel.ASSOC_CONTAINS, OrgstructureBean.STRUCTURE_ROOT_NAME);
				if (structure != null) {
					JSONObject root = new JSONObject();
					try {
						root.put(NODE_REF, structure.toString());
						root.put(ITEM_TYPE, TYPE_STRUCTURE);
						root.put(TITLE, getElementName(
								nodeService, structure, QName.createQName(OrgstructureBean.ORGSTRUCTURE_NAMESPACE_URI, ELEMENT_FULL_NAME)));
						root.put(IS_LEAF, nodeService.getChildAssocs(
								structure, RegexQNamePattern.MATCH_ALL, RegexQNamePattern.MATCH_ALL, false).isEmpty());
						nodes.put(root);
					} catch (JSONException e) {
						logger.error(e);
					}
				}
			}
		}
		return nodes.toString();
	}

	private String getElementName(final NodeService service, final NodeRef ref, QName property, QName defaultProperty) {
		String value = null;
		if (property != null) {
			value = (String) service.getProperty(ref, property);
		}
		return value != null ? value : (String) service.getProperty(ref, defaultProperty);
	}

	private String getElementName(final NodeService service, final NodeRef ref, QName property) {
		return getElementName(service, ref, property, DEFAULT_NAME);
	}

	/**
	 * Возвращает ноду руководителя Организации
	 */
	public ScriptNode getOrganizationBoss() {
		NodeRef boss = orgstructureService.getOrganizationBoss();
		if (boss != null) {
			return new ScriptNode(boss, services, getScope());
		} else {
			return null;
		}
	}

	/**
	 * Возвращает ноду логотипа Организации
	 */
	public ScriptNode getOrganizationLogo() {
		NodeRef logo = orgstructureService.getOrganizationLogo();
		if (logo != null) {
			return new ScriptNode(logo, services, getScope());
		} else {
			return null;
		}
	}

	/**
	 * Получение полного перечня рабочих групп Организации
	 */
	public Scriptable getWorkGroups(boolean onlyActive) {
		List<NodeRef> wgs = orgstructureService.getWorkGroups(onlyActive);
		return createScriptable(wgs);
	}

	/**
	 * Получение перечня подчиненных подразделений
	 */
	public Scriptable getSubUnits(String parent, boolean onlyActive) {
		List<NodeRef> units = orgstructureService.getSubUnits(new NodeRef(parent), onlyActive);
		return createScriptable(units);
	}

	/**
	 * Возвращает список "рутовых" подразделений
	 */
	public Scriptable getRootUnits(boolean onlyActive) {
		List<NodeRef> units = orgstructureService.getSubUnits(orgstructureService.getStructureDirectory(), onlyActive);
		return createScriptable(units);
	}

	/**
	 * Получение вышестоящего подразделения
	 */
	public ScriptNode getParent(String nodeRef) {
		NodeRef parent = orgstructureService.getParent(new NodeRef(nodeRef));
		if (parent != null) {
			return new ScriptNode(parent, services, getScope());
		}
		return null;
	}

	/**
	 * Получение значений атрибутов подразделения
	 */
	public ScriptNode getUnit(String nodeRef) {
		ParameterCheck.mandatory("nodeRef", nodeRef);
		NodeRef ref = new NodeRef(nodeRef);
		if (this.services.getNodeService().exists(ref)) {
			if (orgstructureService.isUnit(ref)) {
				return new ScriptNode(ref, this.services, getScope());
			}
		}
		return null;
	}

	/**
	 * Получение информации о руководителе подразделения
	 */
	public ScriptNode findUnitBoss(String nodeRef) {
		ParameterCheck.mandatory("nodeRef", nodeRef);
		NodeRef ref = new NodeRef(nodeRef);
		if (this.services.getNodeService().exists(ref)) {
			if (orgstructureService.isUnit(ref)) {
				NodeRef bossRef = orgstructureService.getUnitBoss(ref);
				if (bossRef != null) {
					return new ScriptNode(bossRef, this.services, getScope());
				}
			}
		}
		return null;
	}

	/**
	 * Получение перечня должностей подразделения
	 */
	public Scriptable getAllStaffLists(String unit) {
		return getStaffLists(unit, false);
	}

	private Scriptable getStaffLists(String unit, boolean onlyVacant) {
		ParameterCheck.mandatory("unit", unit);
		NodeRef ref = new NodeRef(unit);
		List<NodeRef> staffs = orgstructureService.getUnitStaffLists(ref);
		if (!onlyVacant) {
			return createScriptable(staffs);
		} else {
			List<NodeRef> vstaffs = new ArrayList<NodeRef>();
			for (NodeRef staff : staffs) {
				NodeRef employeeInStaff = orgstructureService.getEmployeeByPosition(staff);
				if (employeeInStaff == null) {// сотрудник не задан - вакантно
					vstaffs.add(staff);
				}
			}
			return createScriptable(vstaffs);
		}
	}

	/**
	 * Получение перечня вакантных должностей в подразделении
	 */
	public Scriptable getVacantStaffLists(String unit) {
		return getStaffLists(unit, true);
	}

	/**
	 * Возвращает массив, пригодный для использования в веб-скриптах
	 *
	 * @return Scriptable
	 */
	private Scriptable createScriptable(List<NodeRef> refs) {
		Object[] results = new Object[refs.size()];
		for (int i = 0; i < results.length; i++) {
			results[i] = new ScriptNode(refs.get(i), services, getScope());
		}
		return Context.getCurrentContext().newArray(getScope(), results);
	}

	/**
	 * Получение полного перечня должностных позиций
	 */
	public Scriptable getStaffPositions(boolean onlyActive) {
		List<NodeRef> staffPositions = orgstructureService.getStaffPositions(onlyActive);
		return createScriptable(staffPositions);
	}

	/**
	 * Получение перечня сотрудников, которые занимают должностную позицию
	 */
	public Scriptable getPositionEmployees(String positionRef) {
		ParameterCheck.mandatory("positionRef", positionRef);
		NodeRef ref = new NodeRef(positionRef);
		List<NodeRef> employees = orgstructureService.getPositionEmployees(ref);
		return createScriptable(employees);
	}

	/**
	 * Получение информации о Рабочей группе
	 */
	public ScriptNode getWorkGroup(String groupRef) {
		ParameterCheck.mandatory("groupRef", groupRef);
		NodeRef ref = new NodeRef(groupRef);
		if (this.services.getNodeService().exists(ref)) {
			if (orgstructureService.isWorkGroup(ref)) {
				return new ScriptNode(ref, this.services, getScope());
			}
		}
		return null;
	}

	/**
	 * Получение информации о Сотрудниках, участвующих в Рабочей группе
	 */
	public Scriptable getWorkGroupEmployees(String workGroupRef) {
		ParameterCheck.mandatory("workGroupRef", workGroupRef);
		NodeRef ref = new NodeRef(workGroupRef);
		List<NodeRef> employees = orgstructureService.getWorkGroupEmployees(ref);
		return createScriptable(employees);
	}

	/**
	 * Получение информации о сотруднике
	 */
	public ScriptNode getEmployee(String employeeRef) {
		ParameterCheck.mandatory("employeeRef", employeeRef);
		NodeRef ref = new NodeRef(employeeRef);
		if (this.services.getNodeService().exists(ref)) {
			if (orgstructureService.isEmployee(ref)) {
				return new ScriptNode(ref, this.services, getScope());
			}
		}
		return null;
	}

	/**
	 * Получение персональных данных сотрудника
	 */
	public  ScriptNode getPersonData(String employeeRef) {
		ParameterCheck.mandatory("employeeRef", employeeRef);
		NodeRef ref = new NodeRef(employeeRef);
		if (this.services.getNodeService().exists(ref)) {
			if(orgstructureService.isEmployee(ref)) {
				NodeRef person = orgstructureService.getEmployeePerson(ref);
				if (person != null) {
				return new ScriptNode(person, this.services, getScope());
				}
			}
		}
		return null;
	}

	public ScriptNode getPersonDataFolder() {
		NodeRef personDataRef = null;
		personDataRef = orgstructureService.getPersonalDataDirectory();
		if (personDataRef != null) {
			return new ScriptNode(personDataRef, this.services, getScope());
		}
		return null;
	}

	/**
	 * Получение информации о руководителе сотрудника
	 */
	public ScriptNode findEmployeeBoss(String nodeRef) {
		ParameterCheck.mandatory("nodeRef", nodeRef);
		NodeRef ref = new NodeRef(nodeRef);
		if (this.services.getNodeService().exists(ref)) {
			if (orgstructureService.isEmployee(ref)) {
				// получаем основную должностную позицию
				NodeRef primaryStaff = orgstructureService.getEmployeePrimaryStaff(ref);
				NodeRef bossRef = null;
				if (primaryStaff != null) {
					// получаем подразделение для штатного расписания
					NodeRef unit = orgstructureService.getUnitByStaff(primaryStaff);
					// получаем руководителя для подразделения
					bossRef = orgstructureService.getUnitBoss(unit);
				} else {
					bossRef = orgstructureService.getOrganizationBoss();
				}
				if (bossRef != null) {
					return new ScriptNode(bossRef, this.services, getScope());
				}
			}
		}
		return null;
	}

	/**
	 * Получение фотографии сотрудника
	 */
	public ScriptNode getEmployeePhoto(String employeeRef) {
		ParameterCheck.mandatory("employeeRef", employeeRef);
		NodeRef ref = new NodeRef(employeeRef);
		if (this.services.getNodeService().exists(ref)) {
			if (orgstructureService.isEmployee(ref)) {
				NodeRef photo = orgstructureService.getEmployeePhoto(ref);
				if (photo != null) {
					return new ScriptNode(ref, this.services, getScope());
				}
			}
		}
		return null;
	}

	/**
	 * Получение должностных позиций, занимаемых сотрудником
	 */
	public Scriptable getEmployeeStaffs(String employeeRef) {
		ParameterCheck.mandatory("employeeRef", employeeRef);
		NodeRef ref = new NodeRef(employeeRef);
		List<NodeRef> staffs = orgstructureService.getEmployeeStaffs(ref);
		return createScriptable(staffs);
	}

	/**
	 * Получение Рабочих групп, в которых участвует сотрудник
	 */
	public Scriptable getEmployeeWorkGroups(String employeeRef) {
		ParameterCheck.mandatory("employeeRef", employeeRef);
		NodeRef ref = new NodeRef(employeeRef);
		List<NodeRef> staffs = orgstructureService.getEmployeeWorkGroups(ref);
		return createScriptable(staffs);
	}

	/**
	 * Возвращает ноду ссылки на сотрудника для Позиции (Штатного расписания или Участника Рабочей группы)
	 */
	public ScriptNode getEmployeeLink(String positionRef) {
		ParameterCheck.mandatory("positionRef", positionRef);
		NodeRef ref = new NodeRef(positionRef);
		NodeRef link = orgstructureService.getEmployeeLinkByPosition(ref);
		if (link != null) {
			return new ScriptNode(link, services, getScope());
		} else {
			return null;
		}
	}

	public ScriptNode getBossExists(String subUnit) {
		NodeRef sunUnitRef = new NodeRef(subUnit);
		NodeRef bossExists = orgstructureService.getBossStaff(sunUnitRef);
		if (bossExists != null) {
			return new ScriptNode(bossExists, services, getScope());
		} else {
		return null;
		}
	}

	public ScriptNode getMainJob(String employee) {
		NodeRef employeeRef = new NodeRef(employee);
		NodeRef mainJob = orgstructureService.getEmployeePrimaryStaff(employeeRef);
		if (mainJob != null) {
			return new ScriptNode(mainJob, services, getScope());
		} else {
			return null;
		}
	}
}
