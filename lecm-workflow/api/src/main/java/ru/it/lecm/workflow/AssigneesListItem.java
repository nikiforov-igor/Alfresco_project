package ru.it.lecm.workflow;

import java.util.Date;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 *
 * @author vlevin
 *
 * {
 * "approvalType": "seq" || "par",
 * "listName": "имя листа согласующих",
 * "listItems": [ {
 * "order": 1,
 * "dueDate": "2013-02-01T00:00:00.000",
 * "nodeRef": "NodeRef на сотрудника"
 * } ]
 * }
 */
public class AssigneesListItem {
	private int order;
	private Date dueDate;
	private NodeRef employeeRef;

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public NodeRef getEmployeeRef() {
		return employeeRef;
	}

	public void setEmployeeRef(NodeRef employeeRef) {
		this.employeeRef = employeeRef;
	}

	
}
