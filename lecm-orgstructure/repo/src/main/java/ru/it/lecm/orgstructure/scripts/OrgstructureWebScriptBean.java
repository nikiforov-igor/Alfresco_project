package ru.it.lecm.orgstructure.scripts;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

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

	private static final QName DEFAULT_NAME = ContentModel.PROP_NAME;
	private static final QName IS_ACTIVE = QName.createQName("http://www.it.ru/lecm/dictionary/1.0", "active");
	/**
	 * Service registry
	 */
	protected ServiceRegistry services;

	/**
	 * Repository helper */
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
	 * Создает дерево директорий Организации
	 * /**
	 * Структура директорий
	 * Организация
	 * ---Структура
	 * ---Сотрудники
	 * ---Персональные данные
	 *
	 * @return Созданную ноду Организация или Null, если произошла ошибка
	 */
	public ScriptNode getOrganizationDirectory() {
		NodeRef organization = orgstructureService.ensureOrganizationRootRef();
		return new ScriptNode(organization, services, getScope());
	}

	/**
	 * Получаем список "корневых" объектов для меню в Оргструктуре

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
			root.put(ITEM_TYPE, OrgstructureBean.TYPE_POSITION);
			root.put(PAGE, ORG_POSITIONS);
			nodes.put(root);

			NodeRef roles = nodeService.getChildByName(dictionariesRoot, ContentModel.ASSOC_CONTAINS, "Роли для рабочих групп");
			// Добавить справочник Роли в рабочих группах
			root = new JSONObject();
			root.put(NODE_REF, roles.toString());
			root.put(ITEM_TYPE, OrgstructureBean.TYPE_ROLE);
			root.put(PAGE, ORG_ROLES);

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
					root.put(ITEM_TYPE, OrgstructureBean.TYPE_EMPLOYEE);
					root.put(NAME_PATTERN, "lecm-orgstr_employee-first-name[1],lecm-orgstr_employee-middle-name[1],lecm-orgstr_employee-last-name");
				} else if (qTypeLocalName.equals(OrgstructureBean.TYPE_DIRECTORY_STRUCTURE)) {
					root.put(NODE_REF, "_NOT_LOAD_");
					root.put(PAGE, "orgstructure");
					root.put(ITEM_TYPE, OrgstructureBean.TYPE_UNIT);
					root.put(NAME_PATTERN, ELEMENT_FULL_NAME_PATTERN);
				}
				nodes.put(root);
				//Добавить Штатное расписание, Рабочие группы
				if (qTypeLocalName.equals(OrgstructureBean.TYPE_DIRECTORY_STRUCTURE)) {
					root = new JSONObject();
					root.put(NODE_REF, cRef.toString());
					root.put(PAGE, STAFF_LIST);
					root.put(ITEM_TYPE, OrgstructureBean.TYPE_STAFF_LIST);
					root.put(NAME_PATTERN, ELEMENT_FULL_NAME_PATTERN);
					nodes.put(root);

					root = new JSONObject();
					root.put(NODE_REF, cRef.toString());
					root.put(PAGE, WORK_GROUPS);
					root.put(ITEM_TYPE, OrgstructureBean.TYPE_WRK_GROUP);
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
	 * @return Текстовое представление JSONArrray c объектами
	 */
	public String getStructure(final String type, final String ref) {
		JSONArray nodes = new JSONArray();
		NodeService nodeService = services.getNodeService();
		if (ref != null) {
			final NodeRef currentRef = new NodeRef(ref);
			if (type.equalsIgnoreCase(OrgstructureBean.TYPE_UNIT)) {// построить дерево подразделений
				Set<QName> units = new HashSet<QName>();
				units.add(QName.createQName(OrgstructureBean.ORGSTRUCTURE_NAMESPACE_URI, OrgstructureBean.TYPE_UNIT));
				// получаем список только Подразделений (внутри могут находиться другие объекты (Рабочие группы))
				List<ChildAssociationRef> childs = nodeService.getChildAssocs(currentRef, units);
				for (ChildAssociationRef child : childs) {
					Boolean isActive = (Boolean) nodeService.getProperty(child.getChildRef(), IS_ACTIVE);
					isActive = isActive != null ? isActive : Boolean.TRUE; // if property not filled -> active = true default
					if (isActive) { // выводим только активные подразделения
						JSONObject unit = new JSONObject();
						try {
							unit.put(NODE_REF, child.getChildRef().toString());
							unit.put(ITEM_TYPE, OrgstructureBean.TYPE_UNIT);
							unit.put(TITLE, getElementName(
									nodeService, child.getChildRef(), QName.createQName(OrgstructureBean.ORGSTRUCTURE_NAMESPACE_URI, ELEMENT_FULL_NAME)));
							unit.put(IS_LEAF, !hasChild(child, nodeService, true));
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
						root.put(ITEM_TYPE, OrgstructureBean.TYPE_STRUCTURE);
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

	private boolean hasChild(ChildAssociationRef child, NodeService nodeService, boolean onlyActive) {
		Set<QName> units = new HashSet<QName>();
		units.add(QName.createQName(OrgstructureBean.ORGSTRUCTURE_NAMESPACE_URI, OrgstructureBean.TYPE_UNIT));
		// получаем список только Подразделений (внутри могут находиться другие объекты)
		List<ChildAssociationRef> childs = nodeService.getChildAssocs(child.getChildRef(), units);
		boolean hasChild = !childs.isEmpty();
		if (onlyActive && !childs.isEmpty()) {
			hasChild = false;
			for (ChildAssociationRef ref : childs) {
				Boolean isActive = (Boolean) nodeService.getProperty(ref.getChildRef(), IS_ACTIVE);
				isActive = isActive != null ? isActive : Boolean.TRUE; // if property not filled -> active = true default
				if (isActive) {
					hasChild = isActive; // if one active exist -> hasChild == true
					break;
				}
			}
		}
		return hasChild;
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
}
