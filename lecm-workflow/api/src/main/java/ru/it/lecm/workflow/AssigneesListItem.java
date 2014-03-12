package ru.it.lecm.workflow;

import java.util.Date;
import org.alfresco.service.cmr.repository.NodeRef;

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
public class AssigneesListItem {

	private NodeRef nodeRef;
	private int order;
	private int daysToComplete;
	private Date dueDate;
	private NodeRef employeeRef;
	private NodeRef staffRef; //reserved for future

	public NodeRef getNodeRef() {
		return nodeRef;
	}

	public void setNodeRef(NodeRef nodeRef) {
		this.nodeRef = nodeRef;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(final int order) {
		this.order = order;
	}

	public int getDaysToComplete() {
		return daysToComplete;
	}

	public void setDaysToComplete(final int daysToComplete) {
		this.daysToComplete = daysToComplete;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(final Date dueDate) {
		this.dueDate = dueDate;
	}

	public NodeRef getEmployeeRef() {
		return employeeRef;
	}

	public void setEmployeeRef(final NodeRef employeeRef) {
		this.employeeRef = employeeRef;
	}

	public NodeRef getStaffRef() {
		return staffRef;
	}

	public void setStaffRef(final NodeRef staffRef) {
		this.staffRef = staffRef;
	}
}
