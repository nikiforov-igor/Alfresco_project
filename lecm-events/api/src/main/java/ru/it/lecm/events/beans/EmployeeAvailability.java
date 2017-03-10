package ru.it.lecm.events.beans;

import java.util.List;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 *
 * @author vmalygin
 */
public class EmployeeAvailability {

	private NodeRef employeeRef;
	private String email;
	private List<EWSEvent> events;

	public EmployeeAvailability() {
	}

	public EmployeeAvailability(NodeRef employeeRef, String email) {
		this.employeeRef = employeeRef;
		this.email = email;
	}

	public NodeRef getEmployeeRef() {
		return employeeRef;
	}

	public void setEmployeeRef(NodeRef employeeRef) {
		this.employeeRef = employeeRef;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<EWSEvent> getEvents() {
		return events;
	}

	public void setEvents(List<EWSEvent> events) {
		this.events = events;
	}
}
