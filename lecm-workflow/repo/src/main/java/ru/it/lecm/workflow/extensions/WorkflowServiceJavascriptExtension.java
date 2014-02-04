package ru.it.lecm.workflow.extensions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.json.JSONObject;
import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.ISO8601DateFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.extensions.webscripts.WebScriptException;
import ru.it.lecm.workflow.AssigneesList;
import ru.it.lecm.workflow.AssigneesListItem;
import ru.it.lecm.workflow.api.WorkflowAssigneesListService;

/**
 *
 * @author vlevin
 */
public class WorkflowServiceJavascriptExtension extends BaseScopableProcessorExtension {

	private final static DateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
	private WorkflowAssigneesListService workflowAssigneesListService;

	public void setWorkflowAssigneesListService(WorkflowAssigneesListService workflowAssigneesListService) {
		this.workflowAssigneesListService = workflowAssigneesListService;
	}

	public NodeRef getAssigneesListsFolder() {
		return workflowAssigneesListService.getAssigneesListsFolder();
	}

	public NodeRef getDefaultAssigneesList(JSONObject json) {
		String workflowType, concurrency;
		try {
			workflowType = json.getString("workflowType");
			concurrency = json.getString("concurrency");
		} catch (JSONException ex) {
			throw new WebScriptException("Error parsing JSON", ex);
		}

		return workflowAssigneesListService.getDefaultAssigneesList(workflowType, concurrency);
	}

	public void saveAssigneesList(JSONObject json) {
		String assigneesListRef, assigneesListName;
		try {
			assigneesListRef = json.getString("assigneesListRef");
			assigneesListName = json.getString("assigneesListName");
		} catch (JSONException ex) {
			throw new WebScriptException("Error parsing JSON", ex);
		}

		workflowAssigneesListService.saveAssigneesList(new NodeRef(assigneesListRef), assigneesListName);
	}

	/*
	 {
	 "defaultListRef": "NodeRef на список по умолчанию",
	 "lists": [ {
	 "listName": "название списка",
	 "nodeRef": "NodeRef списка"
	 }]
	 }
	 */
	public JSONObject getAssigneesLists(JSONObject json) {
		String workflowType, concurrency;
		JSONObject result = new JSONObject();
		JSONArray listsJSONArray = new JSONArray();
		try {
			workflowType = json.getString("workflowType");
			concurrency = json.getString("concurrency");

			List<NodeRef> assingeesLists = workflowAssigneesListService.getAssingeesListsForCurrentEmployee(workflowType, concurrency);
			NodeRef defaultList = workflowAssigneesListService.getDefaultAssigneesList(workflowType, concurrency);

			for (NodeRef assigneesListRef : assingeesLists) {
				String assigneesListName = workflowAssigneesListService.getNodeRefName(assigneesListRef);

				JSONObject jsonItem = new JSONObject();

				// для каждого строим JSON-объект
				jsonItem.put("listName", assigneesListName);
				jsonItem.put("nodeRef", assigneesListRef.toString());

				// складываем в json-array
				listsJSONArray.put(jsonItem);
			}

			result.put("lists", listsJSONArray);
			result.put("defaultListRef", defaultList.toString());
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
				listItemJSON.put("dueDate", dueDate != null ? dateParser.format(dueDate) : "");
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

	public void deleteList(final JSONObject json) {
		String nodeRefStr;
		try {
			nodeRefStr = json.getString("nodeRef");
		} catch (JSONException ex) {
			throw new WebScriptException("Insufficient params in JSON", ex);
		}
		NodeRef listNode = new NodeRef(nodeRefStr);
		workflowAssigneesListService.deleteAssigneesList(listNode);
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

	public void calculateAssigneesListDueDates(final JSONObject json) {
		String assigneesListNodeRefStr, dueDateStr;
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

		workflowAssigneesListService.calculateAssigneesListDueDates(new NodeRef(assigneesListNodeRefStr), dueDate);
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
	
	/*
		public JSONObject getCurrentEmployeeInfo() {
		JSONObject result = new JSONObject();

		NodeRef currentEmployee = orgstructureService.getCurrentEmployee();
		NodeRef currentListsFolder = getListsFolderRef();

		try {
			result.put("currentEmployeeRef", currentEmployee);
			result.put("currentListsFolderRef", currentListsFolder);
		} catch (JSONException ex) {
			throw new WebScriptException("Can not form JSONObject", ex);
		}

		return result;
	}
	*/
}
