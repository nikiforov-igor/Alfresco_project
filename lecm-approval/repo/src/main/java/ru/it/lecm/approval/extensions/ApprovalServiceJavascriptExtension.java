package ru.it.lecm.approval.extensions;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.approval.api.ApprovalListService;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.lang.time.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.springframework.extensions.webscripts.WebScriptException;

public class ApprovalServiceJavascriptExtension extends BaseScopableProcessorExtension {

	private final static DateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
	private AuthenticationService authenticationService;
	private NodeService nodeService;
	private ApprovalListService approvalListService;

	public void setAuthenticationService(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setApprovalListService(ApprovalListService approvalListService) {
		this.approvalListService = approvalListService;
	}

	/**
	 * Возвращает NodeRef на каталог, в котором текущий пользователь хранит
	 * списки согласующих.
	 *
	 * @return NodeRef на каталог со списками согласующих
	 */
	public NodeRef getListsFolderRef() {

		String currentUserName = authenticationService.getCurrentUserName();
		NodeRef approvalHomeRef = approvalListService.getApprovalFolder();

		//ищем папку с пользователем
		NodeRef userHomeRef = nodeService.getChildByName(approvalHomeRef, ContentModel.ASSOC_CONTAINS, currentUserName);
		if (userHomeRef == null) {
			QName currentUserQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, currentUserName);
			Map<QName, Serializable> props = new HashMap<QName, Serializable>();
			props.put(ContentModel.PROP_NAME, currentUserName);
			userHomeRef = nodeService.createNode(approvalHomeRef, ContentModel.ASSOC_CONTAINS, currentUserQName, ContentModel.TYPE_FOLDER, props).getChildRef();
		}
		// Попробуем найти папку "Списки согласования".
		NodeRef assigneesListsFolderRef = nodeService.getChildByName(userHomeRef, ContentModel.ASSOC_CONTAINS, ApprovalListService.ASSIGNEES_LISTS_FOLDER_NAME);

		// Если найти не удалось...
		if (assigneesListsFolderRef == null) {

			// Создадим папку "Списки согласования".
			final Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
			properties.put(ContentModel.PROP_NAME, ApprovalListService.ASSIGNEES_LISTS_FOLDER_NAME);
			NodeRef assigneesLists = nodeService.createNode(userHomeRef,
					ContentModel.ASSOC_CONTAINS,
					QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, ApprovalListService.ASSIGNEES_LISTS_FOLDER_NAME),
					ContentModel.TYPE_FOLDER,
					properties).getChildRef();

			return assigneesLists;
		} else { // Если нашли...
			return assigneesListsFolderRef;
		}
	}

	/**
	 * Список согласущих данного типа по умолчанию.
	 *
	 * @return NodeRef на список согласующих
	 */
	public NodeRef getDefaultListFolderRef() {
		// папка с листами согласующих
		final NodeRef parentListsFolder = getListsFolderRef();
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
	public String save(final JSONObject json) {
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
		parentFolder = getListsFolderRef();

		// ищем лист согласующих с данным именем
		NodeRef listNodeRef = nodeService.getChildByName(parentFolder, ContentModel.ASSOC_CONTAINS, listName);

		// и удаляем, если нашли
		if (listNodeRef != null) {
			nodeService.addAspect(listNodeRef, ContentModel.ASPECT_TEMPORARY, null);
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
		StringBuilder builder = new StringBuilder();
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

			builder.append(itemNodeRef.toString()).append(",");
		}
		builder.delete(builder.length() - 1, builder.length());
		return builder.toString();
	}

	/**
	 * Получить списки согласующих по типу согласования.
	 *
	 * @return {
	 * "defaultListRef": "NodeRef на список по умолчанию",
	 * "lists": [ {
	 * "listName": "название списка",
	 * "nodeRef": "NodeRef списка"
	 * }]
	 * }
	 */
    public JSONObject getAssigneesLists() {
        JSONObject result = new JSONObject();
        JSONArray resultArray = new JSONArray();

        // получить (попутно создав) список по умолчанию
        NodeRef defaultList = getDefaultListFolderRef();
        // получаем все листы согласующих
        NodeRef parentFolder = getListsFolderRef();
        List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(parentFolder);
        // бежим по листам
        for (ChildAssociationRef childAssoc : childAssocs) {
            NodeRef assigneesListRef = childAssoc.getChildRef();
            String assigneesListName = (String) nodeService.getProperty(assigneesListRef, ContentModel.PROP_NAME);

            JSONObject jsonItem = new JSONObject();

            try {
                // для каждого строим JSON-объект
                jsonItem.put("listName", assigneesListName);
                jsonItem.put("nodeRef", assigneesListRef.toString());
            } catch (JSONException ex) {
                throw new WebScriptException("Can not form JSONArray", ex);
            }
            // складываем в json-array
            resultArray.put(jsonItem);
        }

        try {
            result.put("lists", resultArray);
            result.put("defaultListRef", defaultList.toString());
        } catch (JSONException ex) {
            throw new WebScriptException("Can not form JSONObject", ex);
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

		List<NodeRef> listItemsNodes = getAssigneesListItems(listNode);

		// бежим по элементам листа согласующих
		for (NodeRef listItem : listItemsNodes) {
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

	public void deleteList(final JSONObject json) {
		String nodeRefStr;
		try {
			nodeRefStr = json.getString("nodeRef");
		} catch (JSONException ex) {
			throw new WebScriptException("Insufficient params in JSON", ex);
		}
		NodeRef listNode = new NodeRef(nodeRefStr);
		nodeService.addAspect(listNode, ContentModel.ASPECT_TEMPORARY, null);
		nodeService.deleteNode(listNode);
	}

	/**
	 * Увеличить или уменьшить порядок элемента списка согласующих.
	 *
	 * @param json {
	 * "assigneeItemNodeRef": "NodeRef элемента списка согласующих",
	 * "moveDirection": "up" || "down"
	 * }
	 * @return {
	 * "success": true|false (произошло ли перемещение по списку),
	 * "assigneesListNodeRef": "NodeRef списка согласующих"
	 * }
	 */
	public JSONObject changeOrder(final JSONObject json) {
		String listItemNodeRefStr, direction;
		int currentItemOrder, newItemOrder = 0;
		boolean stopReordering = false, success;
		JSONObject result = new JSONObject();

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
			success = false;
		} else {
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
			success = true;
		}

		try {
			result.put("success", success);
			result.put("assigneesListNodeRef", assigeesListNodeRef.toString());
		} catch (JSONException ex) {
			throw new WebScriptException("Can not form JSONObject", ex);
		}

		return result;
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
		List<NodeRef> listItemsItems = getAssigneesListItems(assigneesListNodeRef);
		for (NodeRef listItemNodeRef : listItemsItems) {
			nodeService.removeAssociation(assigneesListNodeRef, listItemNodeRef, ApprovalListService.ASSOC_ASSIGNEES_LIST_CONTAINS_ASSIGNEES_ITEM);
			nodeService.addAspect(listItemNodeRef, ContentModel.ASPECT_TEMPORARY, null);
			nodeService.deleteNode(listItemNodeRef);
		}
	}

	public void calculateApprovalDueDate(JSONObject json) {
		String assigneesListNodeRefStr, dueDateStr;
		NodeRef assigneesListNodeRef;
		Date dueDate;

		try {
			assigneesListNodeRefStr = json.getString("assigneesListNodeRef");
			dueDateStr = json.getString("dueDate");
		} catch (JSONException ex) {
			throw new WebScriptException("Insufficient params in JSON", ex);
		}
		try {
			dueDate = dateParser.parse(dueDateStr);
		} catch (ParseException ex) {
			throw new WebScriptException("Invalid date format", ex);
		}

		assigneesListNodeRef = new NodeRef(assigneesListNodeRefStr);
		List<NodeRef> assigneesListItems = getAssigneesListItems(assigneesListNodeRef);

		if (assigneesListItems.isEmpty()) {
			return;
		}

		dueDate = DateUtils.truncate(dueDate, Calendar.DATE);
		Date today = DateUtils.truncate(new Date(), Calendar.DATE);

		long diffDays = (dueDate.getTime() - today.getTime()) / 86400000;
		int period = Math.round(diffDays / assigneesListItems.size());

		Date previousDate;
		Date currentDate;

		nodeService.setProperty(assigneesListItems.get(0), ApprovalListService.PROP_ASSIGNEES_ITEM_DUE_DATE, DateUtils.addDays(today, period));
		for (int i = 1; i < assigneesListItems.size(); i++) {
			previousDate = (Date) nodeService.getProperty(assigneesListItems.get(i - 1), ApprovalListService.PROP_ASSIGNEES_ITEM_DUE_DATE);
			currentDate = DateUtils.addDays(previousDate, period);
			nodeService.setProperty(assigneesListItems.get(i), ApprovalListService.PROP_ASSIGNEES_ITEM_DUE_DATE, currentDate);
		}
	}

	private List<NodeRef> getAssigneesListItems(NodeRef assigneesListNodeRef) {
		List<NodeRef> result = new ArrayList<NodeRef>();
		List<AssociationRef> listItemsAssocs = nodeService.getTargetAssocs(assigneesListNodeRef, ApprovalListService.ASSOC_ASSIGNEES_LIST_CONTAINS_ASSIGNEES_ITEM);
		for (AssociationRef listItemAssoc : listItemsAssocs) {
			result.add(listItemAssoc.getTargetRef());
		}
		return result;
	}
}
