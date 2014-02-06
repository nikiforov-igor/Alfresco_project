package ru.it.lecm.workflow;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author vlevin
 *
 * {
 * "workflowType": "seq" || "par",
 * "listName": "имя листа согласующих",
 * "listItems": [ {
 * "order": 1,
 * "dueDate": "2013-02-01T00:00:00.000",
 * "nodeRef": "NodeRef на сотрудника"
 * } ]
 * }
 */

public class AssigneesList {
	private String workflowType;
	private String listName;
	private List<AssigneesListItem> listItems = new ArrayList<AssigneesListItem>();

	public String getWorkflowType() {
		return workflowType;
	}

	public void setWorkflowType(String workflowType) {
		this.workflowType = workflowType;
	}

	public String getListName() {
		return listName;
	}

	public void setListName(String listName) {
		this.listName = listName;
	}

	public List<AssigneesListItem> getListItems() {
		return listItems;
	}

	public void setListItems(List<AssigneesListItem> listItems) {
		this.listItems = listItems;
	}


}
