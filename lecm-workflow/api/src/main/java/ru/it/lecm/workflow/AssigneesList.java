package ru.it.lecm.workflow;

import java.util.ArrayList;
import java.util.List;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 *
 * @author vlevin

 {
 "concurrency": "seq" || "par",
 "listName": "имя листа согласующих",
 "listItems": [ {
 "order": 1,
 "dueDate": "2013-02-01T00:00:00.000",
 "nodeRef": "NodeRef на сотрудника"
 } ]
 }
 */
public class AssigneesList {

	private NodeRef nodeRef;
	private String concurrency;
	private String listName;
	private int daysToComplete;
	private final List<AssigneesListItem> listItems = new ArrayList<AssigneesListItem>();

	public NodeRef getNodeRef() {
		return nodeRef;
	}

	public void setNodeRef(final NodeRef nodeRef) {
		this.nodeRef = nodeRef;
	}

	public String getConcurrency() {
		return concurrency;
	}

	public void setConcurrency(final String concurrency) {
		this.concurrency = concurrency;
	}

	public String getListName() {
		return listName;
	}

	public void setListName(final String listName) {
		this.listName = listName;
	}

	public List<AssigneesListItem> getListItems() {
		return listItems;
	}

	public int getDaysToComplete() {
		return daysToComplete;
	}

	public void setDaysToComplete(int daysToComplete) {
		this.daysToComplete = daysToComplete;
	}

	@Override
	public String toString() {
		return String.format("Assignees list %s, details: [nodeRef: %s, concurrency: %s]. Contains %d items", listName, nodeRef, concurrency, listItems.size());
	}
}
