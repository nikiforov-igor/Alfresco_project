package ru.it.lecm.approval.extensions;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.model.Repository;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.approval.api.ApprovalListService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.springframework.extensions.webscripts.WebScriptException;

public class ApprovalServiceJavascriptExtension extends BaseScopableProcessorExtension {

	private final static Logger logger = LoggerFactory.getLogger(ApprovalServiceJavascriptExtension.class);
	private final static DateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
	private AuthenticationService authenticationService;
	private PersonService personService;
	private NodeService nodeService;
	private ServiceRegistry serviceRegistry;
	private OrgstructureBean orgstructureService;
	private Repository repository;

	public void setAuthenticationService(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
		repository.init();
	}

	private enum ApprovementType {

		PARALLEL,
		SEQUENTIAL
	}

	/**
	 * @return список согласующих по умолчанию для параллельного согласования
	 */
	public String getDefaultParallelListFolderRef() {
		return getDefaultListFolderRef(ApprovementType.PARALLEL).toString();
	}

	/**
	 * @return список согласующих по умолчанию для последовательного
	 * согласования
	 */
	public String getDefaultSequentialListFolderRef() {
		return getDefaultListFolderRef(ApprovementType.SEQUENTIAL).toString();
	}

	/**
	 * Возвращает NodeRef на каталог, в котором текущий пользователь хранит
	 * списки согласующих определенного типа.
	 *
	 * @param approvalType тип списка согласующих: "PARALLEL" или "SEQUENTIAL"
	 * @return NodeRef на каталог со списками согласующих
	 */
	private NodeRef getListFolderRef(final ApprovementType approvementType) {
		final String[] ASSIGNEES_LISTS_FOLDERS = {ApprovalListService.ASSIGNEES_LISTS_PARALLEL_FOLDER_NAME, ApprovalListService.ASSIGNEES_LISTS_SEQUENTIAL_FOLDER_NAME};
		final Map<String, NodeRef> assigneesListsFolders = new HashMap<String, NodeRef>();

		String currentUserName = authenticationService.getCurrentUserName();
		NodeRef personRef = personService.getPerson(currentUserName, false);
		NodeRef userHomeRef = repository.getUserHome(personRef);

		// Попробуем найти папку "Списки согласования".
		NodeRef assigneesListsFolderRef = null;
		List<ChildAssociationRef> homeFolderChildAssocs = nodeService.getChildAssocs(userHomeRef);
		for (ChildAssociationRef homeFolderChildAssoc : homeFolderChildAssocs) {

			NodeRef currentChildRef = homeFolderChildAssoc.getChildRef();
			String childName = (String) nodeService.getProperty(currentChildRef, ContentModel.PROP_NAME);

			if (childName.equalsIgnoreCase(ApprovalListService.ASSIGNEES_LISTS_FOLDER_NAME)) {
				assigneesListsFolderRef = currentChildRef;
				break;
			}
		}

		// Если найти не удалось...
		if (assigneesListsFolderRef == null) {

			// Создадим папку "Списки согласования".
			final Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
			properties.put(ContentModel.PROP_NAME, ApprovalListService.ASSIGNEES_LISTS_FOLDER_NAME);
			NodeRef assigneesLists = nodeService.createNode(userHomeRef,
					ContentModel.ASSOC_CONTAINS,
					QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI,
					ApprovalListService.ASSIGNEES_LISTS_FOLDER_NAME),
					ContentModel.TYPE_FOLDER,
					properties).getChildRef();

			// Создадим папки "Параллельное согласование" и "Последовательное согласование".
			for (String assigneesListFolder : ASSIGNEES_LISTS_FOLDERS) {
				properties.put(ContentModel.PROP_NAME, assigneesListFolder);
				NodeRef assigneesListsParallel = nodeService.createNode(assigneesLists,
						ContentModel.ASSOC_CONTAINS,
						QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI,
						assigneesListFolder),
						ContentModel.TYPE_FOLDER,
						properties).getChildRef();
				assigneesListsFolders.put(assigneesListFolder, assigneesListsParallel);
			}

		} else { // Если нашли...
			for (String assigneesListFolder : ASSIGNEES_LISTS_FOLDERS) {
				NodeRef assigneesListFolderRef = nodeService.getChildByName(assigneesListsFolderRef, ContentModel.ASSOC_CONTAINS, assigneesListFolder);

				assigneesListsFolders.put(assigneesListFolder, assigneesListFolderRef);
			}
		}

		if (ApprovementType.PARALLEL.equals(approvementType)) {
			return assigneesListsFolders.get(ApprovalListService.ASSIGNEES_LISTS_PARALLEL_FOLDER_NAME);
		} else if (ApprovementType.SEQUENTIAL.equals(approvementType)) {
			return assigneesListsFolders.get(ApprovalListService.ASSIGNEES_LISTS_SEQUENTIAL_FOLDER_NAME);
		} else {
			return null;
		}
	}

	/**
	 * Список согласущих данного типа по умолчанию.
	 *
	 * @param approvementType тип списка согласующих: "PARALLEL" или
	 * "SEQUENTIAL"
	 * @return NodeRef на список согласующих
	 */
	private NodeRef getDefaultListFolderRef(final ApprovementType approvementType) {
		// папка с листами согласующих
		final NodeRef parentListsFolder = getListFolderRef(approvementType);
		// ищем папку с названием "Список по умолчанию"...
		NodeRef defaultAssigneesListFolderRef = nodeService.getChildByName(parentListsFolder, ContentModel.ASSOC_CONTAINS, ApprovalListService.ASSIGNEES_DEFAULT_LIST_FOLDER_NAME);

		// если таковой не существует...
		if (defaultAssigneesListFolderRef == null) {
			// ...то мы ее создадим
			final Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
			properties.put(ContentModel.PROP_NAME, ApprovalListService.ASSIGNEES_DEFAULT_LIST_FOLDER_NAME);
			defaultAssigneesListFolderRef = nodeService.createNode(parentListsFolder,
					ContentModel.ASSOC_CONTAINS,
					QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI,
					ApprovalListService.ASSIGNEES_DEFAULT_LIST_FOLDER_NAME),
					ApprovalListService.TYPE_ASSIGNEES_LIST,
					properties).getChildRef();
		} else {
			clearAssigneesList(defaultAssigneesListFolderRef);
		}

		return defaultAssigneesListFolderRef;
	}

	/**
	 * Создать новый (или переписать существующий) лист согласующих.
	 *
	 * @param json {
	 * "approvalType": "seq" || "par",
	 * "listName": "имя листа согласущих",
	 * "listItems": [ {
	 * "order": 1,
	 * "dueDate": "2013-02-01T00:00:00.000",
	 * "nodeRef": "NodeRef на сотрудника"
	 * } ]
	 * }
	 */
	public void save(final JSONObject json) {
		String approvalType, listName;
		JSONArray listItems;
		NodeRef parentFolder;

		try {
			// разбираем, что нам там пришло в JSON'е
			approvalType = json.getString("approvalType");
			listName = json.getString("listName");
			listItems = json.getJSONArray("listItems");
		} catch (JSONException ex) {
			throw new WebScriptException("Insufficient params in JSON", ex);
		}

		// папка с листами согласующих
		parentFolder = getAsigneesListParentFolderByType(approvalType);

		// ищем лист согласующих с данным именем
		NodeRef listNodeRef = nodeService.getChildByName(parentFolder, ContentModel.ASSOC_CONTAINS, listName);

		// и удаляем, если нашли
		if (listNodeRef != null) {
			nodeService.deleteNode(listNodeRef);
		}

		Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
		properties.put(ContentModel.PROP_NAME, listName);
		// создаем новый лист согласующих
		listNodeRef = nodeService.createNode(parentFolder,
				ContentModel.ASSOC_CONTAINS,
				QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI,
				listName),
				ApprovalListService.TYPE_ASSIGNEES_LIST,
				properties).getChildRef();

		// бежим по json-массиву "listItems"
		for (int i = 0; i < listItems.length(); i++) {
			String employeeNodeRefStr, dueDateStr;
			int order;
			Date dueDate = null;

			try {
				// разбираем каждый объект, содержащийся в массиве
				JSONObject listItem = listItems.getJSONObject(i);
				employeeNodeRefStr = listItem.getString("nodeRef");
				dueDateStr = listItem.optString("dueDate");
				order = listItem.optInt("order");
			} catch (JSONException ex) {
				throw new WebScriptException("Insufficient params in JSON", ex);
			}

			try {
				// распарсить дату
				dueDate = dateParser.parse(dueDateStr);
			} catch (ParseException ex) {
				// наличие dueDate важно только в последовательном согласовании
				if ("seq".equals(approvalType)) {
					throw new WebScriptException("Invalid date format", ex);
				}
			}

			NodeRef employeeNodeRef = new NodeRef(employeeNodeRefStr);
			// к cm:name сотрудника добавим UUID для уникальности. это и будет имя элемента листа согласующих
			String listItemName = String.format("%s [%s]", nodeService.getProperty(employeeNodeRef, ContentModel.PROP_NAME).toString(), UUID.randomUUID());

			properties = new HashMap<QName, Serializable>();
			properties.put(ApprovalListService.PROP_ASSIGNEES_ITEM_ORDER, order);
			properties.put(ApprovalListService.PROP_ASSIGNEES_ITEM_DUE_DATE, dueDate);
			properties.put(ContentModel.PROP_NAME, listItemName);

			// создать новый элемент
			NodeRef itemNodeRef = nodeService.createNode(listNodeRef,
					ContentModel.ASSOC_CONTAINS,
					QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI,
					listItemName),
					ApprovalListService.TYPE_ASSIGNEES_ITEM,
					properties).getChildRef();

			// создать ассоциацию с сотрудником
			nodeService.createAssociation(itemNodeRef, employeeNodeRef, ApprovalListService.ASSOC_ASSIGNEES_ITEM_EMPLOYEE_ASSOC);
		}
	}

	/**
	 * Получить списки согласующих по типу согласования.
	 *
	 * @param json { "approvalType": "seq" || "par"}
	 * @return [ {
	 * "listName": "имя листа согласущих",
	 * "nodeRef": "NodeRef на лист согласующих"
	 * } ]
	 */
	public JSONArray getAssigneesLists(final JSONObject json) {
		String approvalType;
		JSONArray result = new JSONArray();

		try {
			// требуемый тип согласования
			approvalType = json.getString("approvalType");
		} catch (JSONException ex) {
			throw new WebScriptException("Insufficient params in JSON", ex);
		}

		// получаем все листы согласующих
		NodeRef parentFolder = getAsigneesListParentFolderByType(approvalType);
		List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(parentFolder);
		// бежим по листам
		for (ChildAssociationRef childAssoc : childAssocs) {
			NodeRef assigneesListRef = childAssoc.getChildRef();
			String assigneesListName = (String) nodeService.getProperty(assigneesListRef, ContentModel.PROP_NAME);

			if (ApprovalListService.ASSIGNEES_DEFAULT_LIST_FOLDER_NAME.equals(assigneesListName)) {
				continue;
			}

			JSONObject jsonItem = new JSONObject();

			try {
				// для каждого строим JSON-объект
				jsonItem.put("listName", assigneesListName);
				jsonItem.put("nodeRef", assigneesListRef.toString());
			} catch (JSONException ex) {
				throw new WebScriptException("Can not form JSONObject", ex);
			}
			// складываем в json-array
			result.put(jsonItem);
		}
		return result;
	}

	/**
	 * Получить содержимое листа согласующих
	 *
	 * @param json { "nodeRef": "NodeRef на список согласующих" }
	 * @return {
	 * "listName": "имя листа согласующих",
	 * "listItems": [ {
	 * "order": 0,
	 * "nodeRef": "NodeRef на сотрудника",
	 * "dueDate": ""
	 * } ]
	 * }
	 */
	public JSONObject getListContents(final JSONObject json) {
		String nodeRefStr;
		JSONObject result = new JSONObject();
		JSONArray listItems = new JSONArray();

		try {
			// NodeRef на нужный список согласующих
			nodeRefStr = json.getString("nodeRef");
		} catch (JSONException ex) {
			throw new WebScriptException("Insufficient params in JSON", ex);
		}

		NodeRef listNode = new NodeRef(nodeRefStr);
		String listName = (String) nodeService.getProperty(listNode, ContentModel.PROP_NAME);

		try {
			result.put("listName", listName);
		} catch (JSONException ex) {
			throw new WebScriptException("Can not form JSONObject", ex);
		}

		List<AssociationRef> targetAssocs = nodeService.getTargetAssocs(listNode, ApprovalListService.ASSOC_ASSIGNEES_LIST_CONTAINS_ASSIGNEES_ITEM);

		// бежим по элементам листа согласующих
		for (AssociationRef targetAssoc : targetAssocs) {
			NodeRef listItem = targetAssoc.getTargetRef();
			int order = (Integer) nodeService.getProperty(listItem, ApprovalListService.PROP_ASSIGNEES_ITEM_ORDER);
			Date dueDate = (Date) nodeService.getProperty(listItem, ApprovalListService.PROP_ASSIGNEES_ITEM_DUE_DATE);
			List<AssociationRef> employeeAssocList = nodeService.getTargetAssocs(listItem, ApprovalListService.ASSOC_ASSIGNEES_ITEM_EMPLOYEE_ASSOC);
			NodeRef employeeNode = employeeAssocList.get(0).getTargetRef();
			JSONObject listItemJSON = new JSONObject();
			try {
				// для каждого из элементов создаем json-объект
				listItemJSON.put("order", order);
				listItemJSON.put("dueDate", dueDate != null ? dateParser.format(dueDate) : "");
				listItemJSON.put("nodeRef", employeeNode);
			} catch (JSONException ex) {
				throw new WebScriptException("Can not form JSONObject", ex);
			}
			// и складываем его в json-array
			listItems.put(listItemJSON);
		}

		try {
			// json-array кладем в json-объект
			result.put("listItems", listItems);
		} catch (JSONException ex) {
			throw new WebScriptException("Can not form JSONObject", ex);
		}

		return result;
	}

	private NodeRef getAsigneesListParentFolderByType(final String approvalType) {
		NodeRef parentFolder;

		if ("par".equals(approvalType)) {
			parentFolder = getListFolderRef(ApprovementType.PARALLEL);
		} else if ("seq".equals(approvalType)) {
			parentFolder = getListFolderRef(ApprovementType.SEQUENTIAL);
		} else {
			throw new WebScriptException("Unknown approval type: " + approvalType);
		}
		return parentFolder;
	}

	public void deleteList(final JSONObject json) {
		String nodeRefStr;
		try {
			nodeRefStr = json.getString("nodeRef");
		} catch (JSONException ex) {
			throw new WebScriptException("Insufficient params in JSON", ex);
		}
		NodeRef listNode = new NodeRef(nodeRefStr);
		nodeService.deleteNode(listNode);
	}

	/**
	 * Увеличить или уменьшить порядок элемента списка согласующих.
	 *
	 * @param json {
	 * "assigneeItemNodeRef": "NodeRef элемента списка согласующих",
	 * "moveDirection": "up" || "down"
	 * }
	 * @return true - перемещение элемента по списку произошло. false - элемент
	 * первый или последний в списке и больше не двигается в указанном
	 * направлении
	 */
	public boolean changeOrder(final JSONObject json) {
		String listItemNodeRefStr, direction;
		int currentItemOrder, newItemOrder = 0;
		boolean stopReordering = false;

		try {
			listItemNodeRefStr = json.getString("assigneeItemNodeRef");
			direction = json.getString("moveDirection");
		} catch (JSONException ex) {
			throw new WebScriptException("Insufficient params in JSON", ex);
		}
		NodeRef listItemNodeRef = new NodeRef(listItemNodeRefStr);
		currentItemOrder = (Integer) nodeService.getProperty(listItemNodeRef, ApprovalListService.PROP_ASSIGNEES_ITEM_ORDER);
		List<AssociationRef> assigneesListAssocs = nodeService.getSourceAssocs(listItemNodeRef, ApprovalListService.ASSOC_ASSIGNEES_LIST_CONTAINS_ASSIGNEES_ITEM);
		NodeRef assigeesListNodeRef = assigneesListAssocs.get(0).getSourceRef();
		List<AssociationRef> listItemsTargetAssocs = nodeService.getTargetAssocs(assigeesListNodeRef, ApprovalListService.ASSOC_ASSIGNEES_LIST_CONTAINS_ASSIGNEES_ITEM);

		if ("up".equals(direction)) {
			if (currentItemOrder == 1) {
				stopReordering = true;
			} else {
				newItemOrder = currentItemOrder - 1;
			}
		} else if ("down".equals(direction)) {
			if (currentItemOrder == listItemsTargetAssocs.size()) {
				stopReordering = true;
			} else {
				newItemOrder = currentItemOrder + 1;
			}
		} else {
			throw new WebScriptException("Unknown moveDirection: " + direction);
		}

		if (stopReordering) {
			return false;
		}

		for (AssociationRef targetAssoc : listItemsTargetAssocs) {
			NodeRef item = targetAssoc.getTargetRef();
			if (item.equals(listItemNodeRef)) {
				continue;
			}
			int itemOrder = (Integer) nodeService.getProperty(item, ApprovalListService.PROP_ASSIGNEES_ITEM_ORDER);
			if (itemOrder == newItemOrder) {
				nodeService.setProperty(item, ApprovalListService.PROP_ASSIGNEES_ITEM_ORDER, currentItemOrder);
				break;
			}

		}

		nodeService.setProperty(listItemNodeRef, ApprovalListService.PROP_ASSIGNEES_ITEM_ORDER, newItemOrder);

		return true;
	}

	/**
	 * Удалить все assignees-item из списка согласующих.
	 *
	 * @param json { "assigneeListNodeRef": "NodeRef списка согласующих" }
	 */
	public void clearAssigneesList(JSONObject json) {
		String assigneesListNodeRefStr;

		try {
			assigneesListNodeRefStr = json.getString("assigneesListNodeRef");
		} catch (JSONException ex) {
			throw new WebScriptException("Insufficient params in JSON", ex);
		}

		clearAssigneesList(new NodeRef(assigneesListNodeRefStr));
	}

	/**
	 * Удалить все assignees-item из списка согласующих.
	 *
	 * @param assigneesListNodeRef NodeRef списка согласующих
	 */
	private void clearAssigneesList(NodeRef assigneesListNodeRef) {
		List<AssociationRef> listItemsAssocs = nodeService.getTargetAssocs(assigneesListNodeRef, ApprovalListService.ASSOC_ASSIGNEES_LIST_CONTAINS_ASSIGNEES_ITEM);
		for (AssociationRef listItemAssoc : listItemsAssocs) {
			NodeRef listItemNodeRef = listItemAssoc.getTargetRef();
			nodeService.removeAssociation(assigneesListNodeRef, listItemNodeRef, ApprovalListService.ASSOC_ASSIGNEES_LIST_CONTAINS_ASSIGNEES_ITEM);
			nodeService.deleteNode(listItemNodeRef);
		}
	}
}
