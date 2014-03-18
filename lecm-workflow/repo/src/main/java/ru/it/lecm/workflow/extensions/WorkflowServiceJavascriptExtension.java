package ru.it.lecm.workflow.extensions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.json.JSONObject;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.ISO8601DateFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.extensions.webscripts.WebScriptException;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.workflow.AssigneesList;
import ru.it.lecm.workflow.AssigneesListItem;
import ru.it.lecm.workflow.api.LecmWorkflowModel;
import ru.it.lecm.workflow.api.WorkflowAssigneesListService;

/**
 *
 * @author vlevin
 */
public class WorkflowServiceJavascriptExtension extends BaseWebScript {

	private WorkflowAssigneesListService workflowAssigneesListService;
	private OrgstructureBean orgstructureService;

	public void setWorkflowAssigneesListService(WorkflowAssigneesListService workflowAssigneesListService) {
		this.workflowAssigneesListService = workflowAssigneesListService;
	}

	public NodeRef getAssigneesListsFolder() {
		return workflowAssigneesListService.getAssigneesListsFolder();
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public JSONObject getDefaultAssigneesList(JSONObject json) {
		String workflowType;
		JSONObject result = new JSONObject();
		try {
			workflowType = json.getString("workflowType");
			boolean hasRouteRef = json.has("routeRef");
			if (hasRouteRef) {
				NodeRef parentRef = new NodeRef(json.getString("routeRef"));
				result.put("defaultList", workflowAssigneesListService.getDefaultAssigneesList(parentRef, workflowType));
			} else {
				result.put("defaultList", workflowAssigneesListService.getDefaultAssigneesList(workflowType));
			}
			result.put("currentEmployee", orgstructureService.getCurrentEmployee());
		} catch (JSONException ex) {
			throw new WebScriptException("Error parsing JSON", ex);
		}

		return result;
	}

	public JSONObject saveAssigneesList(JSONObject json) {
		String assigneesListRef, assigneesListName, workflowType;
		NodeRef listNode;
		JSONObject result;
		try {
			assigneesListRef = json.getString("nodeRef");
			assigneesListName = json.getString("title");
		} catch (JSONException ex) {
			throw new WebScriptException("Error parsing JSON", ex);
		}

		listNode = new NodeRef(assigneesListRef);
		workflowType = workflowAssigneesListService.getAssigneesListWorkflowType(listNode);
		workflowAssigneesListService.saveAssigneesList(listNode, assigneesListName);
		result = getAssigneesLists(workflowType);

		return result;
	}

	/*
	 {
	 "defaultList": "NodeRef на список по умолчанию",
	 "listsFolder": "папка, которая содержит списки участников",
	 "lists": [ {
	 "title": "название списка",
	 "nodeRef": "NodeRef списка",
	 "concurrency": "параллельны или последовательный"
	 }]
	 }
	 */
	public JSONObject getAssigneesLists(JSONObject json) {
		JSONObject result = null;
		try {
			String workflowType = json.getString("workflowType");
			result = getAssigneesLists(workflowType);

		} catch (JSONException ex) {
			throw new WebScriptException("Error operating JSON", ex);
		}

		return result;
	}

	private JSONObject getAssigneesLists(String workflowType) {
		JSONObject result = new JSONObject();
		JSONArray listsJSONArray = new JSONArray();
		try {
			NodeRef defaultList = workflowAssigneesListService.getDefaultAssigneesList(workflowType);
			List<NodeRef> assingeesLists = workflowAssigneesListService.getAssingeesListsForCurrentEmployee(workflowType);

			for (NodeRef assigneesListRef : assingeesLists) {
				String assigneesListName = workflowAssigneesListService.getNodeRefName(assigneesListRef);
				String assigneesListConcurrency = workflowAssigneesListService.getAssigneesListConcurrency(assigneesListRef);

				JSONObject jsonItem = new JSONObject();

				// для каждого строим JSON-объект
				jsonItem.put("title", assigneesListName);
				jsonItem.put("nodeRef", assigneesListRef);
				jsonItem.put("concurrency", assigneesListConcurrency);

				// складываем в json-array
				listsJSONArray.put(jsonItem);
			}

			result.put("lists", listsJSONArray);
			result.put("defaultList", defaultList);
			result.put("listsFolder", workflowAssigneesListService.getAssigneesListsFolder());
		} catch (JSONException ex) {
			throw new WebScriptException("Error operating JSON", ex);
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
	public JSONObject getAssigneesListContents(final JSONObject json) {
		String nodeRefStr;
		JSONObject result = new JSONObject();
		JSONArray listItems = new JSONArray();

		try {
			// NodeRef на нужный список согласующих
			nodeRefStr = json.getString("nodeRef");

			NodeRef listNode = new NodeRef(nodeRefStr);
			AssigneesList assigneesList = workflowAssigneesListService.getAssigneesListDetail(listNode);
			String listName = assigneesList.getListName();

			result.put("listName", listName);

			// бежим по элементам листа согласующих
			for (AssigneesListItem listItem : assigneesList.getListItems()) {
				int order = listItem.getOrder();
				Date dueDate = listItem.getDueDate();
				NodeRef employeeNode = listItem.getEmployeeRef();
				JSONObject listItemJSON = new JSONObject();

				// для каждого из элементов создаем json-объект
				listItemJSON.put("order", order);
				listItemJSON.put("dueDate", dueDate != null ? new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(dueDate) : "");
				listItemJSON.put("nodeRef", employeeNode);
				// и складываем его в json-array
				listItems.put(listItemJSON);
			}

			// json-array кладем в json-объект
			result.put("listItems", listItems);
		} catch (JSONException ex) {
			throw new WebScriptException("Error operating JSON", ex);
		}

		return result;
	}

	/**
	 * Сбросить даты в списке согласующих
	 *
	 * @param json { "listRefToClear": "NodeRef на список согласующих" }
	 */
	public void clearDueDates(final JSONObject json) {
		String listRefToClear;

		try {
			listRefToClear = json.getString("listRefToClear");
		} catch (JSONException ex) {
			throw new WebScriptException("Insufficient params in JSON", ex);
		}

		NodeRef listNodeToClear = new NodeRef(listRefToClear);
		workflowAssigneesListService.clearDueDatesInAssigneesList(listNodeToClear);
	}

	public JSONObject deleteList(final JSONObject json) {
		String nodeRefStr, workflowType;
		JSONObject result;
		try {
			nodeRefStr = json.getString("nodeRef");
		} catch (JSONException ex) {
			throw new WebScriptException("Insufficient params in JSON", ex);
		}

		NodeRef listNode = new NodeRef(nodeRefStr);
		workflowType = workflowAssigneesListService.getAssigneesListWorkflowType(listNode);
		workflowAssigneesListService.deleteAssigneesList(listNode);
		result = getAssigneesLists(workflowType);

		return result;
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
		JSONObject result = new JSONObject();

		try {
			listItemNodeRefStr = json.getString("assigneeItemNodeRef");
			direction = json.getString("moveDirection");

			NodeRef listItemNodeRef = new NodeRef(listItemNodeRefStr);

			boolean success = workflowAssigneesListService.changeAssigneeOrder(listItemNodeRef, direction);

			result.put("success", success);
			result.put("assigneesListNodeRef", workflowAssigneesListService.getAssigneesListByItem(listItemNodeRef));
		} catch (JSONException ex) {
			throw new WebScriptException("Error operating JSON", ex);
		}

		return result;

	}

	public void clearAssigneesList(JSONObject json) {
		String assigneesListNodeRefStr;

		try {
			assigneesListNodeRefStr = json.getString("assigneesListNodeRef");
		} catch (JSONException ex) {
			throw new WebScriptException("Insufficient params in JSON", ex);
		}

		workflowAssigneesListService.clearAssigneesList(new NodeRef(assigneesListNodeRefStr));
	}

	public void setDueDates(JSONObject json) {
		String assigneeListNodeRefStr, workflowDueDateStr;
		try {
			assigneeListNodeRefStr = json.getString("assigneeListNodeRef");
			workflowDueDateStr = json.getString("workflowDueDate");
		} catch (JSONException ex) {
			throw new WebScriptException("Error parsing JSON", ex);
		}
		workflowAssigneesListService.setDueDates(new NodeRef(assigneeListNodeRefStr), ISO8601DateFormat.parse(workflowDueDateStr));
	}

	public JSONObject getCurrentEmployeeInfo() {
		JSONObject result = new JSONObject();

		NodeRef currentEmployee = orgstructureService.getCurrentEmployee();
		NodeRef currentListsFolder = workflowAssigneesListService.getAssigneesListsFolder();

		try {
			result.put("currentEmployeeRef", currentEmployee);
			result.put("currentListsFolderRef", currentListsFolder);
		} catch (JSONException ex) {
			throw new WebScriptException("Can not form JSONObject", ex);
		}

		return result;
	}

	public String getRouteType() {
		return LecmWorkflowModel.TYPE_ROUTE.toPrefixString(serviceRegistry.getNamespaceService());
	}

}
