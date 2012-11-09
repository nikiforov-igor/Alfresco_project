package ru.it.lecm.orgstructure;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.processor.BaseProcessorExtension;
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

/**
 * @author dbashmakov
 *         Date: 30.09.12
 */
public class GetOrgstructureBean extends BaseProcessorExtension {

	public static final String TYPE_ORGANIZATION = "_ORG_";
	public static final String TYPE_ROOT = "_ROOT_";

	public static final String TYPE_EMPLOYEE = "employee";
	public static final String TYPE_WRK_GROUP = "workGroup";
	public static final String TYPE_UNIT = "organization-unit";
	public static final String TYPE_STAFF_LIST = "staff-list";

	public static final String DIRECTORY_EMPLOYEES = "employees";
	public static final String DIRECTORY_STRUCTURE = "structure";

	private static final String ORGSTRUCTURE_NAMESPACE_URI = "http://www.it.ru/lecm/org/structure/1.0";
	private static final QName DEFAULT_NAME = ContentModel.PROP_NAME;
	private static final QName IS_ACTIVE = QName.createQName("http://www.it.ru/lecm/dictionary/1.0", "active");

	public static final String NODE_REF = "nodeRef";
	public static final String TYPE = "type";
	public static final String ITEM_TYPE = "itemType";
	public static final String TITLE = "title";
	public static final String IS_LEAF = "isLeaf";
	public static final String NAME_PATTERN = "namePattern";

	private static Log logger = LogFactory.getLog(GetOrgstructureBean.class);

	public static final String ELEMENT_FULL_NAME = "element-full-name";
	public static final String ELEMENT_FULL_NAME_PATTERN = "lecm-orgstr_element-full-name";

	private static ServiceRegistry serviceRegistry;

	public String getRoots(final String type, final String ref) {
		JSONArray nodes = new JSONArray();
		NodeService nodeService = serviceRegistry.getNodeService();
		if (ref != null) {
			final NodeRef currentRef = new NodeRef(ref);
			if (type == null || type.equals(TYPE_ROOT)) {
				JSONObject root;
				List<ChildAssociationRef> childs = nodeService.getChildAssocs(currentRef);
				for (ChildAssociationRef childAssociationRef : childs) {
					QName qType = nodeService.getType(childAssociationRef.getChildRef());
					String qTypeLocalName = qType.getLocalName();
					try {
						NodeRef cRef = childAssociationRef.getChildRef();

						root = new JSONObject();
						root.put(TITLE, getElementName(nodeService, cRef));
						root.put(NODE_REF, cRef.toString());
						root.put(TYPE, qTypeLocalName);
						root.put(IS_LEAF, false);

						// Список справочников по которым будет вестись работа
						if (qTypeLocalName.equals(DIRECTORY_EMPLOYEES)) {
							root.put(ITEM_TYPE, TYPE_EMPLOYEE);
							root.put(NAME_PATTERN, "lecm-orgstr_employee-first-name[1],lecm-orgstr_employee-middle-name[1],lecm-orgstr_employee-last-name");
						} else if (qTypeLocalName.equals(DIRECTORY_STRUCTURE)) {
							root.put(ITEM_TYPE, TYPE_STAFF_LIST);
							root.put(NAME_PATTERN, ELEMENT_FULL_NAME_PATTERN);
						}
						nodes.put(root);
					} catch (JSONException e) {
						logger.error(e);
					}
				}
			}
		}
		return nodes.toString();
	}

	public String getStructure(final String type, final String ref) {
		JSONArray nodes = new JSONArray();
		NodeService nodeService = serviceRegistry.getNodeService();
		if (ref != null) {
			final NodeRef currentRef = new NodeRef(ref);
			if (type.equalsIgnoreCase(TYPE_UNIT)) {// построить дерево подразделений
				Set<QName> units = new HashSet<QName>();
				units.add(QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, TYPE_UNIT));
				// получаем список только Подразделений (внутри могут находиться другие объекты)
				List<ChildAssociationRef> childs = nodeService.getChildAssocs(currentRef, units);
				for (ChildAssociationRef child : childs) {
					Boolean isActive = (Boolean) nodeService.getProperty(child.getChildRef(), IS_ACTIVE);
					isActive = isActive != null ? isActive : Boolean.TRUE; // if property not filled -> active = true default
					if (isActive) { // выводим только активные подразделения
						JSONObject unit = new JSONObject();
						try {
							unit.put(NODE_REF, child.getChildRef().toString());
							unit.put(TYPE, TYPE_UNIT);
							unit.put(TITLE, getElementName(
									nodeService, child.getChildRef(), QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, ELEMENT_FULL_NAME)));
							unit.put(IS_LEAF, !hasChild(child, nodeService, true));

							unit.put(ITEM_TYPE, TYPE_UNIT);
							unit.put(NAME_PATTERN, ELEMENT_FULL_NAME_PATTERN);
							nodes.put(unit);
						} catch (JSONException e) {
							logger.error(e);
						}
					}
				}
			} else if (type.equalsIgnoreCase(TYPE_ORGANIZATION)) { //Вывести директорию "Структура"
				NodeRef structure = nodeService.getChildByName(currentRef, ContentModel.ASSOC_CONTAINS, "Структура");
				if (structure != null) {
					JSONObject root = new JSONObject();
					try {
						root.put(NODE_REF, structure.toString());
						root.put(TYPE, TYPE_UNIT);
						root.put(TITLE, getElementName(
								nodeService, structure, QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, ELEMENT_FULL_NAME)));
						root.put(IS_LEAF, nodeService.getChildAssocs(
								structure, RegexQNamePattern.MATCH_ALL, RegexQNamePattern.MATCH_ALL, false).isEmpty());

						root.put(ITEM_TYPE, TYPE_UNIT);
						root.put(NAME_PATTERN, ELEMENT_FULL_NAME_PATTERN);
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
		units.add(QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, TYPE_UNIT));
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

	private String getElementName(final NodeService service, final NodeRef ref) {
		return getElementName(service, ref, null, DEFAULT_NAME);
	}

	public ServiceRegistry getServiceRegistry() {
		return serviceRegistry;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}
}
