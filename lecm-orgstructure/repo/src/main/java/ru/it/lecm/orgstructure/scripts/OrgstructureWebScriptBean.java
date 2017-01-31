package ru.it.lecm.orgstructure.scripts;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.javascript.Scriptable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.surf.util.ParameterCheck;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.wcalendar.absence.IAbsence;

import java.util.*;

/**
 * @author dbashmakov
 *         Date: 27.11.12
 *         Time: 17:08
 */
public class OrgstructureWebScriptBean extends BaseWebScript {
	public static final String NODE_REF = "nodeRef";
	public static final String ITEM_TYPE = "itemType";
	public static final String TITLE = "title";
	public static final String LABEL = "label";
	public static final String IS_LEAF = "isLeaf";

	public static final QName ELEMENT_FULL_NAME = QName.createQName(OrgstructureBean.ORGSTRUCTURE_NAMESPACE_URI, "element-full-name");
	public static final QName ELEMENT_SHORT_NAME = QName.createQName(OrgstructureBean.ORGSTRUCTURE_NAMESPACE_URI, "element-short-name");

	private static final Logger logger = LoggerFactory.getLogger (OrgstructureWebScriptBean.class);
	public static final String POSITIONS_DICTIONARY_NAME = "Должностные позиции";
	public static final String WORKGROUPS_ROLES_DICTIONARY_NAMES = "Роли для рабочих групп";
	public static final String BUSINESS_ROLES_DICTIONARY_NAMES = "Бизнес роли";
	public static final String PAGE_ORG_POSITIONS = "org-positions";
	public static final String PAGE_ORG_ROLES = "org-roles";
	public static final String PAGE_ORG_EMPLOYEES = "org-employees";
	public static final String PAGE_ORG_STAFF_LIST = "org-staff-list";
	public static final String PAGE_ORG_WORK_GROUPS = "org-work-groups";
	public static final String PAGE_ORG_BUSINESS_ROLES = "org-business-roles";
	public static final String PAGE_ORG_PROFILE = "org-profile";
	public static final String PAGE_ORG_STRUCTURE= "org-structure";

	private static final QName DEFAULT_NAME = ContentModel.PROP_NAME;
    private static final String DEFAULT_EMPTY_TEXT = "<Не указано>";
    public static final String TYPE_UNIT = "organization-unit";

    private final Map<String, Integer> ROOTS = new HashMap<String, Integer>() {{
        put(PAGE_ORG_EMPLOYEES, 1);
        put(PAGE_ORG_STAFF_LIST, 2);
        put(PAGE_ORG_STRUCTURE, 3);
        put(PAGE_ORG_POSITIONS, 4);
        put(PAGE_ORG_ROLES, 5);
        put(PAGE_ORG_WORK_GROUPS, 6);
        put(PAGE_ORG_BUSINESS_ROLES, 7);
        put(PAGE_ORG_PROFILE, 8);
    }};

    private DictionaryBean dictionaryService;

	private OrgstructureBean orgstructureService;

    private IAbsence absenceService;

    public void setAbsenceService(IAbsence absenceService) {
        this.absenceService = absenceService;
    }

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public void setDictionaryService(DictionaryBean dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	/**
	 * Возвращает ноду Организация
	 * /**
	 * Структура директорий
	 * Организация
	 * ---Структура
	 * ------Холдинг
	 * ---------Орг1
	 * ---------Орг2
	 * ---------Орг3
	 * ---Сотрудники
	 * ---Персональные данные
	 *
	 * @return  ноду Организация
	 */
	public ScriptNode getOrganization() {
		NodeRef organization = orgstructureService.getOrganization();
		return organization != null ? new ScriptNode(organization, serviceRegistry, getScope()) : null;
	}

	/**
	 * Возвращает ноду Организация/Структура
	 * Структура директорий
	 * Организация
	 * ---Структура
	 * ------Холдинг
	 * ---------Орг1
	 * ---------Орг2
	 * ---------Орг3
	 * ---Сотрудники
	 * ---Персональные данные
	 * @return
	 */
	public ScriptNode getStructure() {
		NodeRef structureRef = orgstructureService.getStructureDirectory();
		return structureRef != null ? new ScriptNode(structureRef, serviceRegistry, getScope()) : null;
	}

	/**
	 * Возвращает ноду Организация/Структура/Холдинг
	 * Структура директорий
	 * Организация
	 * ---Структура
	 * ------Холдинг
	 * ---------Орг1
	 * ---------Орг2
	 * ---------Орг3
	 * ---Сотрудники
	 * ---Персональные данные
	 * @return
	 */
	public ScriptNode getHolding() {
		NodeRef holdingRef = orgstructureService.getHolding();
		return holdingRef != null ? new ScriptNode(holdingRef, serviceRegistry, getScope()) : null;
	}

	/**
	 * Возвращает ноду Основного подразделения организации
	 *
	 *
	 * @return  ноду Подразделения
	 */

	public ScriptNode getRootUnit() {
		NodeRef mainOrganizationUnit = orgstructureService.getRootUnit();
		return new ScriptNode(mainOrganizationUnit, serviceRegistry, getScope());
	}

	/**
	 * Получаем список Дочерних объектов в формате, используемом в дереве Оргструктуры
	 *
	 * @return Текстовое представление JSONArrray c объектами
	 */
    public String getStructure(final String type, final String ref) {
        List<JSONObject> nodes = new ArrayList<JSONObject>();
        if (ref != null) {
            final NodeRef currentRef = new NodeRef(ref);
            if (type.equalsIgnoreCase(TYPE_UNIT)) {// построить дерево подразделений
                // получаем список только Подразделений (внутри могут находиться другие объекты (Рабочие группы))
                List<NodeRef> childs = orgstructureService.getSubUnits(currentRef, true);
                for (NodeRef child : childs) {
                    JSONObject unit = new JSONObject();
                    try {
                        unit.put(NODE_REF, child.toString());
                        unit.put(ITEM_TYPE,
                                OrgstructureBean.TYPE_ORGANIZATION_UNIT.toPrefixString(serviceRegistry.getNamespaceService()));
                        unit.put(LABEL, getElementName(
                                child, ELEMENT_SHORT_NAME));
                        unit.put(TITLE, getElementName(
                                child, ELEMENT_FULL_NAME));
                        unit.put(IS_LEAF, !orgstructureService.hasChild(child, true));
                        nodes.add(unit);
                    } catch (JSONException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
                sort(nodes, LABEL, true);
            } else if (type.equalsIgnoreCase(OrgstructureBean.TYPE_ORGANIZATION.toPrefixString())) { //Вывести директорию "Структура"
                NodeRef structure = orgstructureService.getStructureDirectory();
                NodeRef organization = orgstructureService.getOrganization();
                if (structure != null & organization != null) {
                    JSONObject root = new JSONObject();
                    try {
                        root.put(NODE_REF, structure.toString());
                        root.put(ITEM_TYPE, OrgstructureBean.TYPE_STRUCTURE.toPrefixString(serviceRegistry.getNamespaceService()));
                        root.put(LABEL, getElementName(
                                organization, ELEMENT_SHORT_NAME, null, DEFAULT_EMPTY_TEXT));
                        root.put(TITLE, getElementName(
                                organization, ELEMENT_FULL_NAME, null, DEFAULT_EMPTY_TEXT));

                        root.put(IS_LEAF, !orgstructureService.hasChild(structure, true));
                        nodes.add(root);
                    } catch (JSONException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        }
        return nodes.toString();
    }

	/**
	 * Сортировка объекта списка JSONObject по значению
	 * @param jsonObject - список JSONObject
	 * @param sortField - строка по которой сортируем
	 * @param sorting true - desc, false - asc
	 */
	private void sort(List<JSONObject> jsonObject, final String sortField, final Boolean sorting) {
		Collections.sort(jsonObject, new Comparator<JSONObject>() {
			@Override
			public int compare(JSONObject a, JSONObject b) {
				try {
					String valA;
					String valB;
					valA = a.getString(sortField);
					valB = b.getString(sortField);
					int comp = valA.compareTo(valB);
					if (sorting) {
						if (comp > 0) return 1;
						if (comp < 0) return -1;
					} else {
						if (comp > 0) return -1;
						if (comp < 0) return 1;
					}
					return 0;
				} catch (JSONException e) {
					logger.error (e.getMessage (), e);
				}
				return 0;
			}
		});
	}

    private String getElementName(final NodeRef ref, QName property, QName defaultProperty, String defaultText) {
        NodeService service = serviceRegistry.getNodeService();
        String value = null;
        if (property != null) {
            value = (String) service.getProperty(ref, property);
            if (value == null && defaultProperty != null) {
                value = (String) service.getProperty(ref, defaultProperty);
            }
        }
        return value != null ? value : defaultText;
    }

    private String getElementName(final NodeRef ref, QName property) {
        return getElementName(ref, property, DEFAULT_NAME, DEFAULT_EMPTY_TEXT);
    }

	/**
	 * Возвращает ноду руководителя Организации
	 */
	public ScriptNode getOrganizationBoss() {
		NodeRef boss = orgstructureService.getOrganizationBoss();
		if (boss != null) {
			return new ScriptNode(boss, serviceRegistry, getScope());
		} else {
			return null;
		}
	}

    /**
     *
     */
    public ScriptNode getEmployeesDirectory() {
        NodeRef employees = orgstructureService.getEmployeesDirectory();
        if (employees != null) {
            return new ScriptNode(employees, serviceRegistry, getScope());
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
			return new ScriptNode(logo, serviceRegistry, getScope());
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
	 * Получение перечня дочерних подразделений
	 */
	public Scriptable getSubUnits(String parent, boolean onlyActive) {
		List<NodeRef> units = orgstructureService.getSubUnits(new NodeRef(parent), onlyActive);
		return createScriptable(units);
	}

	/**
	 * Получение перечня дочерних подразделений
	 */
	public Scriptable getSubUnits(String parent, boolean onlyActive,  boolean includeSubunits) {
		List<NodeRef> units = orgstructureService.getSubUnits(new NodeRef(parent), onlyActive, includeSubunits);
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
     * @param unitRef подразделение
     * @param returnSelf возвращать ссылку на самого себя, если подразделение является корнем
     * @return ссылка на вышестоящее подразделение
     */
	public ScriptNode getParentUnit(String unitRef, boolean returnSelf) {
        ParameterCheck.mandatory("unitRef", unitRef);
        if (orgstructureService.hasAccessToOrgElement(new NodeRef(unitRef))) {
            NodeRef parent = orgstructureService.getParentUnit(new NodeRef(unitRef));
            if (parent != null) {
                return new ScriptNode(parent, serviceRegistry, getScope());
            } else if (returnSelf) {
                return new ScriptNode(new NodeRef(unitRef), serviceRegistry, getScope());
            }
        }
		return null;
	}

	/**
	 * Получение значений атрибутов подразделения
	 */
	public ScriptNode getUnit(String unitRef) {
		ParameterCheck.mandatory("unitRef", unitRef);
		NodeRef ref = new NodeRef(unitRef);
		if (this.serviceRegistry.getNodeService().exists(ref) && orgstructureService.hasAccessToOrgElement(ref)) {
			if (orgstructureService.isUnit(ref)) {
				return new ScriptNode(ref, this.serviceRegistry, getScope());
			}
		}
		return null;
	}

	/**
	 * Получение информации о руководителе подразделения
	 */
	public ScriptNode findUnitBoss(String unitRef) {
		ParameterCheck.mandatory("unitRef", unitRef);
		NodeRef unit = new NodeRef(unitRef);
		if (serviceRegistry.getNodeService().exists(unit) && orgstructureService.hasAccessToOrgElement(unit)) {
			if (orgstructureService.isUnit(unit)) {
				NodeRef bossRef = orgstructureService.getUnitBoss(unit);
				if (bossRef != null) {
					return new ScriptNode(bossRef, serviceRegistry, getScope());
				}
			}
		}
		return null;
	}

	/**
	 * Получение перечня должностей подразделения
	 */
	public Scriptable getAllStaffLists(String unitRef) {
		return getStaffLists(unitRef, false);
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

	public Scriptable getOrgRoleEmployees(ScriptNode orgRole) {
		ParameterCheck.mandatory("orgRoleRef", orgRole);
		List<NodeRef> employees = orgstructureService.getOrgRoleEmployees(orgRole.getNodeRef());

        return createScriptable(employees);
    }

	/**
	 * Получение перечня вакантных должностей в подразделении
	 */
	public Scriptable getVacantStaffLists(String unitRef) {
		return getStaffLists(unitRef, true);
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
		if (serviceRegistry.getNodeService().exists(ref)) {
			if (orgstructureService.isWorkGroup(ref)) {
				return new ScriptNode(ref, serviceRegistry, getScope());
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
		if (serviceRegistry.getNodeService().exists(ref) && orgstructureService.hasAccessToOrgElement(ref)) {
			if (orgstructureService.isEmployee(ref)) {
				return new ScriptNode(ref, serviceRegistry, getScope());
			}
		}
		return null;
	}

	/**
	 * Получение информации о сотруднике по ссылке
	 */
	public ScriptNode getEmployeeByLink(String employeeLinkNodeRef) {
		ParameterCheck.mandatory("employeeLinkNodeRef", employeeLinkNodeRef);
		NodeRef ref = new NodeRef(employeeLinkNodeRef);
		if (serviceRegistry.getNodeService().exists(ref)) {
			NodeRef employeeRef = orgstructureService.getEmployeeByLink(ref);
			if (orgstructureService.isEmployee(employeeRef)) {
				return new ScriptNode(employeeRef, serviceRegistry, getScope());
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
		if (serviceRegistry.getNodeService().exists(ref)) {
			if(orgstructureService.isEmployee(ref)) {
				NodeRef person = orgstructureService.getEmployeePersonalData(ref);
				if (person != null) {
					return new ScriptNode(person, serviceRegistry, getScope());
				}
			}
		}
		return null;
	}

	/**
	 * Получение папки персональных данных сотрудника
	 */
	public ScriptNode getPersonDataFolder() {
		NodeRef personDataRef = orgstructureService.getPersonalDataDirectory();
		if (personDataRef != null) {
			return new ScriptNode(personDataRef, serviceRegistry, getScope());
		}
		return null;
	}

	/**
	 * Получение информации о руководителе сотрудника
	 */
	public ScriptNode findEmployeeBoss(String employeeRef) {
		ParameterCheck.mandatory("employeeRef", employeeRef);
		NodeRef ref = new NodeRef(employeeRef);
		NodeRef bossRef = orgstructureService.findEmployeeBoss(ref);
		if (bossRef != null) {
			return new ScriptNode(bossRef, serviceRegistry, getScope());
		}
		return null;
	}

	/**
	 * Получение информации о руководителе сотрудника
	 */
	public ScriptNode findEmployeeBoss(ScriptNode employeeRef) {
		ParameterCheck.mandatory("employeeRef", employeeRef);
		NodeRef bossRef = orgstructureService.findEmployeeBoss(employeeRef.getNodeRef());

		return bossRef != null ? new ScriptNode(bossRef, serviceRegistry, getScope()) : null;
	}

	/**
	 * Получение информации о подразделении сотрудника
	 */
	public ScriptNode getUnitByStaff(String nodeRef) {
		ParameterCheck.mandatory("nodeRef", nodeRef);
		NodeRef ref = new NodeRef(nodeRef);
		if (serviceRegistry.getNodeService().exists(ref)) {
			NodeRef unitRef = orgstructureService.getUnitByStaff(ref);
			if (orgstructureService.isUnit(unitRef)) {
				return new ScriptNode(unitRef, serviceRegistry, getScope());
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
		if (serviceRegistry.getNodeService().exists(ref)) {
			if (orgstructureService.isEmployee(ref)) {
				NodeRef photo = orgstructureService.getEmployeePhoto(ref);
				if (photo != null) {
					return new ScriptNode(photo, serviceRegistry, getScope());
				}
			}
		}
		return null;
	}

	/**
	 * Получение должностных позиций, занимаемых сотрудником
	 */
	public Scriptable getPositionList(String employeeRef) {
		ParameterCheck.mandatory("employeeRef", employeeRef);
		NodeRef ref = new NodeRef(employeeRef);
		List<NodeRef> staffs = orgstructureService.getEmployeeStaffs(ref);
		return createScriptable(staffs);
	}

	/**
	 * Получение списка сотрудников, занимающих в указанном подразделении указанную должностную позицию
	 * @param unitRef подразделение
	 * @param positionRef доложностная позиция
	 * @return список ссылок на сотрудников
	 */
	public Scriptable getEmployeesByPosition(String unitRef, String positionRef) {
		ParameterCheck.mandatory("unitRef", unitRef);
		ParameterCheck.mandatory("positionRef", positionRef);
		List<NodeRef> employees = orgstructureService.getEmployeesByPosition(new NodeRef(unitRef), new NodeRef(positionRef));
		return createScriptable(employees);
	}

    /**
     * Получение ссылки на сотрудника для объектов "Штатное Расписание и "Участник Рабочей группы"
     * @param positionRef ссылка на должностную позицию
     * @return ссылка на сотрудника
     */
    public ScriptNode getEmployeeByPosition(ScriptNode positionRef) {
        NodeRef employee = orgstructureService.getEmployeeByPosition(positionRef.getNodeRef());
        if (employee != null) {
            return new ScriptNode(employee, serviceRegistry, getScope());
        }
        return null;
    }

    /**
     * Получение ссылки на сотрудника для объектов "Штатное Расписание и "Участник Рабочей группы"
     * @param positionRef должностная позиция
     * @return ссылка на сотрудника
     */
    public ScriptNode getEmployeeByPosition(NodeRef positionRef) {
        NodeRef nodeRef = orgstructureService.getEmployeeByPosition(positionRef);
        if (nodeRef != null) {
            return new ScriptNode(nodeRef, serviceRegistry, getScope());
        }
        return null;
    }

	/**
	 * Получение Рабочих групп, в которых участвует сотрудник
	 */
	public Scriptable getEmployeeWorkGroups(String employeeRef) {
		ParameterCheck.mandatory("employeeRef", employeeRef);
		NodeRef ref = new NodeRef(employeeRef);
		List<NodeRef> groups = orgstructureService.getEmployeeWorkGroups(ref);
		return createScriptable(groups);
	}

	/**
	 * Возвращает ноду ссылки на сотрудника для Позиции (Штатного расписания или Участника Рабочей группы)
	 */
	public ScriptNode getEmployeeLink(String positionRef) {
		ParameterCheck.mandatory("positionRef", positionRef);
		NodeRef ref = new NodeRef(positionRef);
		NodeRef link = orgstructureService.getEmployeeLinkByPosition(ref);
		if (link != null) {
			return new ScriptNode(link, serviceRegistry, getScope());
		} else {
			return null;
		}
	}

    /**
     * Получение руководящей позиции в выбранном подразделении
     * @param unitRef ссылка на подразделение
     * @return руководящая позиция
     */
	public ScriptNode getBossStaff(String unitRef) {
        ParameterCheck.mandatory("unitRef", unitRef);
		NodeRef unit = new NodeRef(unitRef);
		NodeRef bossExists = orgstructureService.getBossStaff(unit);
		if (bossExists != null) {
			return new ScriptNode(bossExists, serviceRegistry, getScope());
		} else {
		    return null;
		}
	}

    /**
     * Получить основную должностную позицию сотрудника
     */
	public ScriptNode getPrimaryPosition(String employeeRef) {
        ParameterCheck.mandatory("employeeRef", employeeRef);
		NodeRef mainJob = orgstructureService.getEmployeePrimaryStaff(new NodeRef(employeeRef));
		if (mainJob != null) {
			return new ScriptNode(mainJob, serviceRegistry, getScope());
		} else {
			return null;
		}
	}

    /**
     * Получение списка ролей в рабочих группах, занимаемых сотрудником
     */
    public Scriptable getEmployeeRoles(String employeeRef) {
        ParameterCheck.mandatory("employeeRef", employeeRef);
        NodeRef ref = new NodeRef(employeeRef);
        List<NodeRef> roles = orgstructureService.getEmployeeWorkForces(ref);
        return createScriptable(roles);
    }

	/**
	 * Получение полного перечня бизнес ролей
	 */
	public Scriptable getBusinesRoles(boolean onlyActive) {
		List<NodeRef> businesRoles = orgstructureService.getBusinesRoles(onlyActive);
		return createScriptable(businesRoles);
	}

	/**
	 * Получение перечня сотрудников, исполняющих определенную Бизнес-роль
	 */
	public Scriptable getEmployeesByBusinessRole(String businessRoleRef) {
		return getEmployeesByBusinessRole(businessRoleRef, false);
	}
    /**
     * Получение перечня сотрудников, исполняющих определенную Бизнес-роль
     */
	public Scriptable getEmployeesByBusinessRole(String businessRoleRef, boolean withDelegation) {
		ParameterCheck.mandatory("businessRoleRef", businessRoleRef);
		NodeRef ref = new NodeRef(businessRoleRef);
		List<NodeRef> results = orgstructureService.getEmployeesByBusinessRole(ref, withDelegation);
		return createScriptable(results);
	}
    /**
     * Получение перечня сотрудников, исполняющих определенную Бизнес-роль
     */
	public Scriptable getEmployeesByBusinessRoleId(String businessRoleId, boolean withDelegation) {
		ParameterCheck.mandatory("businessRoleId", businessRoleId);
		List<NodeRef> results = orgstructureService.getEmployeesByBusinessRole(businessRoleId, withDelegation);
		if (results != null) {
			return createScriptable(results);
		}
		return null;
	}

	/**
	 * Получение списка ссылок на заданного сотрудника по ссылке на Штатное расписание
	 */
	public Scriptable getEmployeeLinksByLink(String linkRef) {
		ParameterCheck.mandatory("linkRef", linkRef);
		NodeRef ref = new NodeRef(linkRef);
		NodeRef employee = orgstructureService.getEmployeeByLink(ref);
		if (employee != null && orgstructureService.hasAccessToOrgElement(employee)) {
			List<NodeRef> links = orgstructureService.getEmployeeLinks(employee);
			return createScriptable(links);
		} else {
			return null;
		}
	}

	/**
	 * Получение информации о текущем сотруднике
	 */
	public ScriptNode getCurrentEmployee() {
		NodeRef employeeRef = orgstructureService.getCurrentEmployee();
		if (orgstructureService.isEmployee(employeeRef)) {
			return new ScriptNode(employeeRef, serviceRegistry, getScope());
		}
		return null;
	}

    /**
	 * Является ли текущий сотрудник руководителем
	 */
	public String isCurrentBoss() {
        return Boolean.toString(orgstructureService.isCurrentBoss());
	}

	/**
	 * получить список подразделений в которые входит сотрудник согласно штатному расписанию
	 * этот список будет содержать или все подразделения или только те, где сотрудник является боссом
	 * @param employeeRef ссылка на сотрудника
	 * @param bossUnitsOnly флаг показывающий что нас интересуют только те подразделения где сотрудник - босс
	 * @return список подразделений или пустой список
	 */
	public Scriptable getEmployeeUnits (final String employeeRef, final boolean bossUnitsOnly) {
		ParameterCheck.mandatory ("employeeRef", employeeRef);
		return createScriptable (orgstructureService.getEmployeeUnits (new NodeRef (employeeRef), bossUnitsOnly));
	}

	/**
	 * получение списка сотрудников в указанном подразделении
	 * @param unitRef ссылка на подразделение
	 * @return список сотрудников в подразделении или пустой список
	 */
	public Scriptable getEmployeesInUnit(final String unitRef) {
		ParameterCheck.mandatory("unitRef", unitRef);
		return createScriptable(orgstructureService.getOrganizationElementEmployees(new NodeRef(unitRef)));
	}

	/**
	 * получение списка сотрудников в указанном подразделении
	 * @param unit подразделение
	 * @return список сотрудников в подразделении или пустой список
	 */
	public Scriptable getEmployeesInUnit(ScriptNode unit) {
		ParameterCheck.mandatory("unit", unit);
		return createScriptable(orgstructureService.getOrganizationElementEmployees(unit.getNodeRef()));
	}

	/**
	 * получение списка подчиненных для указанного сотрудника
	 * @param bossRef сотрудник который является руководителем
	 * @return список подчиненных сотрудника по всем подразделениям.
	 *         Если сотрудник не является руководителем, то список пустой
	 */
	public Scriptable getBossSubordinate(final String bossRef) {
		ParameterCheck.mandatory("bossRef", bossRef);
		return createScriptable(orgstructureService.getBossSubordinate(new NodeRef(bossRef)));
	}

    public Scriptable getBossSubordinate(final String bossRef, final boolean withDelegation) {
		ParameterCheck.mandatory("bossRef", bossRef);
		return createScriptable(orgstructureService.getBossSubordinate(new NodeRef(bossRef), withDelegation));
	}

	/**
	 * получить бизнес роль "Технолог" из общего справочника бизнес ролей
	 * @return ScriptNode на бизнес роль "Технолог" или null если таковой бизнес роли нет
	 */
	public ScriptNode getBusinessRoleDelegationEngineer() {
		NodeRef engineerRef = orgstructureService.getBusinessRoleDelegationEngineer();
		if (engineerRef != null) {
			return new ScriptNode(engineerRef, serviceRegistry, getScope());
		}
		return null;
	}

	/**
	 * получить бизнес роль "Технолог календарей" из общего справочника бизнес ролей
	 * @return ScriptNode на бизнес роль "Технолог календарей" или null если таковой бизнес роли нет
	 */
	public ScriptNode getBusinessRoleCalendarEngineer() {
		NodeRef engineerRef = orgstructureService.getBusinessRoleCalendarEngineer();
		if (engineerRef != null) {
			return new ScriptNode(engineerRef, serviceRegistry, getScope());
		}
		return null;
	}

    /**
	 * Проверка, имеет ли сотрудник роль "Технолог календарей".
	 *
	 * @return true если сотрудник имеет роль "Технолог календарей".
	 */
	public boolean isCalendarEngineer(final String employeeRef) {
		return orgstructureService.isCalendarEngineer(new NodeRef(employeeRef));
	}

	/**
	 * Проверка, занимает ли сотрудник руководящую позицию.
	 *
	 * @return true если сотрудник занимает где-либо руководящую позицию.
	 */
	public boolean isBoss(final String employeeRef) {
		return orgstructureService.isBoss(new NodeRef(employeeRef));
	}

    /**
	 * Проверка, занимает ли сотрудник руководящую позицию c учётом делегирования
	 *
	 * @return true если сотрудник занимает где-либо руководящую позицию.
	 */
	public boolean isBoss(final String employeeRef, final boolean withDelegation) {
		return orgstructureService.isBoss(new NodeRef(employeeRef), withDelegation);
	}

    /**
     * является ли указанный пользователь Технологом делегирования
     * @param employeeRef ссылка на сотрудника
     * @return true/false
     */
	public boolean isDelegationEngineer (final String employeeRef) {
		return orgstructureService.isDelegationEngineer (new NodeRef (employeeRef));
	}

    /**
     * имеет ли текущий пользователь у себя в подчинении другого пользователя (без учета делегирования)
     * @param bossRef ссылка на сотрудника-руковидителя
     * @param subordinateRef ссылка на сотрудника-возможного подчиненного
     * @return true/false
     */
    public boolean hasSubogetrdinate (final String bossRef, final String subordinateRef) {
		return orgstructureService.hasSubordinate (new NodeRef (bossRef), new NodeRef (subordinateRef));
	}

	/**
	 * Получение информации о ролях текущего сотрудника
	 */
	public Scriptable getCurrentEmployeeRoles() {
		NodeRef employeeRef = orgstructureService.getCurrentEmployee();
		Set<NodeRef> roles = new HashSet<NodeRef>();
		//получаем роли согласно оргштатки
		roles.addAll(orgstructureService.getEmployeeRoles(employeeRef));
		//получаем роли согласно делегированию полномочий
		roles.addAll(orgstructureService.getEmployeeRolesWithDelegation(employeeRef));
		//объединяем в общую кучу и возвращаем
		return createScriptable(new ArrayList<NodeRef>(roles));
	}

    /**
     * Получение информации о всех роляx сотрудника
     */
    public Scriptable getEmployeeBusinessRoles(String employeeRef) {
        NodeRef employee = new NodeRef(employeeRef);
		Set<NodeRef> roles = new HashSet<NodeRef>();
        roles.addAll(orgstructureService.getEmployeeRoles(employee));
		roles.addAll(orgstructureService.getEmployeeRolesWithDelegation(employee));
        return createScriptable(new ArrayList<NodeRef>(roles));
    }
    /**
     * Получение информации о собственных роляx сотрудника
     */
    public Scriptable getEmployeeOnlyBusinessRoles(String employeeRef) {
        NodeRef employee = new NodeRef(employeeRef);
		Set<NodeRef> roles = new HashSet<NodeRef>();
        roles.addAll(orgstructureService.getEmployeeRoles(employee));
        return createScriptable(new ArrayList<NodeRef>(roles));
    }
    /**
     * Получение информации о роляx сотрудника, которые ему делегированы
     */
    public Scriptable getEmployeeDelegatedBusinessRoles(String employeeRef) {
        NodeRef employee = new NodeRef(employeeRef);
		Set<NodeRef> roles = new HashSet<NodeRef>();
		roles.addAll(orgstructureService.getEmployeeRolesWithDelegation(employee));
        return createScriptable(new ArrayList<NodeRef>(roles));
    }

    /**
     * Роли назначенные сотруднику
     * @param employeeRef
     * @return
     */
    public Scriptable getEmployeeDirectRoles(String employeeRef) {
        NodeRef employee = new NodeRef(employeeRef);
        Set<NodeRef> roles = new HashSet<NodeRef>();
        roles.addAll(orgstructureService.getEmployeeDirectRoles(employee));
        return createScriptable(new ArrayList<NodeRef>(roles));
    }

    /**
     * Роли назначенные сотруднику через подразделения
     *
     * @param employeeRef
     * @return
     */
    public Scriptable getEmployeeUnitRoles(String employeeRef) {
        NodeRef employee = new NodeRef(employeeRef);
        Set<NodeRef> roles = new HashSet<NodeRef>();
        roles.addAll(orgstructureService.getEmployeeUnitRoles(employee));
        return createScriptable(new ArrayList<NodeRef>(roles));
    }

    /**
     * Роли назначенные сотруднику через рабочую группу
     *
     * @param employeeRef
     * @return
     */
    public Scriptable getEmployeeWGRoles(String employeeRef) {
        NodeRef employee = new NodeRef(employeeRef);
        Set<NodeRef> roles = new HashSet<NodeRef>();
        roles.addAll(orgstructureService.getEmployeeWGRoles(employee));
        return createScriptable(new ArrayList<NodeRef>(roles));
    }

    /**
     * Роли назначенные сотруднику через должностную позицию
     *
     * @param employeeRef
     * @return
     */
    public Scriptable getEmployeeDPRoles(String employeeRef) {
        NodeRef employee = new NodeRef(employeeRef);
        Set<NodeRef> roles = new HashSet<NodeRef>();
        roles.addAll(orgstructureService.getEmployeeDPRoles(employee));
        return createScriptable(new ArrayList<NodeRef>(roles));
    }

    /**
     * Получение корневой папки из оргструктуры по ее типу
     * @param rootType тип папки (ROOT Map<String, Integer>)
     * @return
     */
    public String getRoot(String rootType) {
        Integer key = ROOTS.get(rootType);
        JSONObject settings = new JSONObject();

        NodeService nodeService = serviceRegistry.getNodeService();
        NodeRef organizationRef = orgstructureService.getOrganization();
        List<ChildAssociationRef> childs = nodeService.getChildAssocs(organizationRef);
        try {
            switch (key) {
                case 1: {
                    for (ChildAssociationRef childAssociationRef : childs) {
                        String qTypeLocalName = nodeService.getType(childAssociationRef.getChildRef()).getLocalName();
                        NodeRef cRef = childAssociationRef.getChildRef();
                        if (qTypeLocalName.equals(OrgstructureBean.TYPE_DIRECTORY_EMPLOYEES)) {
                            settings.put(NODE_REF, cRef.toString());
                            settings.put(ITEM_TYPE, OrgstructureBean.TYPE_EMPLOYEE.toPrefixString(serviceRegistry.getNamespaceService()));
                            break;
                        }
                    }
                }
                break;
                case 2: {
                    settings.put(NODE_REF, "NOT_LOAD");
                }
                break;
                case 3: {
                    settings.put(NODE_REF, "NOT_LOAD");
                }
                break;
                case 4: {
                    NodeRef positions = dictionaryService.getDictionaryByName(POSITIONS_DICTIONARY_NAME);
                    settings.put(NODE_REF, positions.toString());
                    settings.put(ITEM_TYPE, OrgstructureBean.TYPE_STAFF_POSITION.toPrefixString(serviceRegistry.getNamespaceService()));
                }
                break;
                case 5: {
                    NodeRef roles = dictionaryService.getDictionaryByName(WORKGROUPS_ROLES_DICTIONARY_NAMES);
                    settings.put(NODE_REF, roles.toString());
                    settings.put(ITEM_TYPE, OrgstructureBean.TYPE_WORK_ROLE.toPrefixString(serviceRegistry.getNamespaceService()));
                }
                break;
                case 6: {
                    for (ChildAssociationRef childAssociationRef : childs) {
                        String qTypeLocalName = nodeService.getType(childAssociationRef.getChildRef()).getLocalName();
                        NodeRef cRef = childAssociationRef.getChildRef();
                        if (qTypeLocalName.equals(OrgstructureBean.TYPE_DIRECTORY_STRUCTURE)) {
                            settings.put(NODE_REF, cRef.toString());
                            settings.put(ITEM_TYPE, OrgstructureBean.TYPE_WORK_GROUP.toPrefixString(serviceRegistry.getNamespaceService()));
                            break;
                        }
                    }
                }
                break;
                case 7: {
                    NodeRef businessRoles = dictionaryService.getDictionaryByName(BUSINESS_ROLES_DICTIONARY_NAMES);
                    settings.put(NODE_REF, businessRoles.toString());
                    settings.put(ITEM_TYPE, OrgstructureBean.TYPE_BUSINESS_ROLE.toPrefixString(serviceRegistry.getNamespaceService()));
                }
                break;
                case 8: {
                    settings.put(NODE_REF, organizationRef.toString());
                }
                break;
                default: {
                    settings = new JSONObject();
                }
            }
        } catch (JSONException e) {
            logger.error(e.getMessage (), e);
        }
        return settings.toString();
    }

    /**
     * Проверка наличия активных отсутствий у сотрудника/подразделения/штатного расписания
     * @param nodeRefStr проверяемый объект
     * @return список активных отсутствий
     */
    public Scriptable checkNodeRefForAbsence(String nodeRefStr)
    {
        NodeRef nodeRef = new NodeRef(nodeRefStr);

        final List<NodeRef> nodeRefEmployees = orgstructureService.getNodeRefEmployees(nodeRef);
        List<NodeRef> absences = new ArrayList<NodeRef>();
        for(NodeRef employee : nodeRefEmployees){
            if (absenceService.isEmployeeAbsentToday(employee)){
                absences.add(employee) ;
            }
        }

        return createScriptable(absences);

    }

    /**
     * Получение подразделения, где сотрудник числится на основной должностной позиции
     */
    public ScriptNode getPrimaryOrgUnit(String employeeRef) {
        ParameterCheck.mandatory("employeeRef", employeeRef);
        NodeRef ref = new NodeRef(employeeRef);
        NodeRef unit = orgstructureService.getPrimaryOrgUnit(ref);
        if (unit != null) {
            return new ScriptNode(unit, serviceRegistry, getScope());
        }
        return null;
    }

    /**
     * Получение подразделения, где сотрудник числится на основной должностной позиции
     */
    public ScriptNode getPrimaryOrgUnit(ScriptNode employeeRef) {
        ParameterCheck.mandatory("employeeRef", employeeRef);
        NodeRef unit = orgstructureService.getPrimaryOrgUnit(employeeRef.getNodeRef());
        if (unit != null) {
            return new ScriptNode(unit, serviceRegistry, getScope());
        }
        return null;
    }
    /**
     * Проверяет наличие бизнес-роли у сотрудника
     * @param employeeRef ссылка на сотрудника
     * @param businessRole бизнес роль
     * @return true если у сотрудника есть бизнесс-роль
     */
    public boolean hasBusinessRole(String employeeRef, String businessRole) {
        return orgstructureService.isEmployeeHasBusinessRole(new NodeRef(employeeRef), businessRole);
    }

    /**
     * Проверяет наличие бизнес-роли у сотрудника
     * @param employee сотрудник
     * @param businessRole бизнес роль
     * @return true если у сотрудника есть бизнесс-роль
     */
    public boolean hasBusinessRole(ScriptNode employee, String businessRole) {
        return orgstructureService.isEmployeeHasBusinessRole(employee.getNodeRef(), businessRole);
    }

    /**
     * Входит ли сотрудник в данную рабочую группу
     * @param employeeRef ссылка на сотрудника
     * @param workGroupRef ссылка на рабочую группу
     * @return true/false
     */
    public boolean isInWorkGroup(String employeeRef, String workGroupRef) {
        ParameterCheck.mandatory("workGroupRef", workGroupRef);
        NodeRef ref = new NodeRef(workGroupRef);
        List<NodeRef> employees = orgstructureService.getWorkGroupEmployees(ref);
        return employees.contains(new NodeRef(employeeRef));
    }

    /**
     * Имеет ли текущий пользователь у себя в подчинении другого пользователя
     * @param bossRef - руководитель
     * @param subordinateRef - подчиненный
     * @param checkPrimary - учитывать только руководство по основной должностной позиции
     * @return true если в имеет в подчинении иначе false
     */
    public boolean isBossOf(final String bossRef, final String subordinateRef, boolean checkPrimary) {
        return orgstructureService.isBossOf(new NodeRef (bossRef), new NodeRef (subordinateRef), checkPrimary);
    }

    /**
     * Возвращает список всех сотрудников
     * @return
     */
    public Scriptable getAllEmployees() {
        List<NodeRef> employees = orgstructureService.getAllEmployees();
        return createScriptable(employees);
    }

    /**
     * Проверка, имеет ли текущий сотрудник заданную бизнес-роль
     * @param roleId код бизнес-роли
     * @return true/false
     */
    public boolean isCurrentEmployeeHasBusinessRole(String roleId) {
		ParameterCheck.mandatory("roleId", roleId);
		return orgstructureService.isCurrentEmployeeHasBusinessRole(roleId);
	}

    /**
     * Проверка, имеет ли сотрудник заданную бизнес-роль с насторойками делегирования или без
     * @param employee сотрудник
     * @param roleId код бизнес-роли
     * @param withDelegation учитывать ли делегирование
     * @param inheritSubordinatesRoles учитывать ли субординацию
     * @return true/false
     */
    public boolean isEmployeeHasBusinessRole(ScriptNode employee, String roleId, boolean withDelegation, boolean inheritSubordinatesRoles) {
		ParameterCheck.mandatory("roleId", roleId);
		return orgstructureService.isEmployeeHasBusinessRole(employee.getNodeRef(), roleId, withDelegation, inheritSubordinatesRoles);
	}

    /**
     * Получить логин сотрудника
     * @param employee сотрудник
     */
    public String getEmployeeLogin(ScriptNode employee) {
		return orgstructureService.getEmployeeLogin(employee.getNodeRef());
	}

    /**
     * Получить сотрудника по заданному логину
     * @param login логин пользователя
     */
    public ScriptNode getEmployeeByLogin(String login) {
        NodeRef employeeByPerson = orgstructureService.getEmployeeByPerson(login);
        return employeeByPerson != null ? new ScriptNode(employeeByPerson, serviceRegistry, getScope()) : null;
    }

    /**
     * Возвращает Authority для папки подразделения
     * @param unit подразделение
     * @param shared общая папка или нет
     */
    public String getOrgstructureUnitAuthority(ScriptNode unit, boolean shared) {
        return orgstructureService.getOrgstructureUnitAuthority(unit.getNodeRef(), shared);
    }

	/**
	 * Проверка, что объект является сотрудником
	 */
	public boolean isEmployee(ScriptNode obj) {
		ParameterCheck.mandatory("obj", obj);
		return orgstructureService.isEmployee(obj.getNodeRef());
	}

	/**
	 * Проверка, что объект является подразделением
	 */
	public boolean isUnit(ScriptNode obj) {
		ParameterCheck.mandatory("obj", obj);
		return orgstructureService.isUnit(obj.getNodeRef());
	}

    public boolean hasAccessToOrgElement(ScriptNode obj, boolean useStrictFilterByOrg) {
        ParameterCheck.mandatory("obj", obj);
        return orgstructureService.hasAccessToOrgElement(obj.getNodeRef(), useStrictFilterByOrg);
    }

    public Scriptable getOrganizationEmployees(final String organizationRef) {
        List<NodeRef> employees = orgstructureService.getOrganizationEmployees(new NodeRef(organizationRef));
        return createScriptable(employees);
    }

    public Scriptable getOrganizationEmployees(final ScriptNode organizationRef) {
        List<NodeRef> employees = orgstructureService.getOrganizationEmployees(organizationRef.getNodeRef());
        return createScriptable(employees);
    }

    public boolean hasGlobalOrganizationsAccess() {
        return orgstructureService.hasGlobalOrganizationsAccess();
    }

    public ScriptNode getEmployeeOrganization(ScriptNode employee) {
        NodeRef employeeOrganization = orgstructureService.getEmployeeOrganization(employee.getNodeRef());
        return employeeOrganization != null ? new ScriptNode(employeeOrganization, serviceRegistry, getScope()) : null;
    }

    public ScriptNode getUnitByOrganization(ScriptNode organization) {
		NodeRef unitByOrganization = orgstructureService.getUnitByOrganization(organization.getNodeRef());
		return unitByOrganization != null ? new ScriptNode(unitByOrganization, serviceRegistry, getScope()) : null;
	}

	public ScriptNode getBusinessRoleByIdentifier(String roleId) {
		NodeRef role = orgstructureService.getBusinessRoleByIdentifier(roleId);
		if (role != null) {
			return new ScriptNode(role, serviceRegistry, getScope());
		} else {
			return null;
		}
	}
	
	public void autoEmployeesTie() {
		orgstructureService.autoEmployeesTie();
	}
}
