package ru.it.lecm.orgstructure.exportimport.entity;

import java.util.HashMap;
import java.util.Map;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 *
 * @author vlevin
 */
public class CreatedItems {

	private final Map<String, NodeRef> positions;
	private final Map<String, NodeRef> departments;
	private final Map<String, NodeRef> employees;
	private final Map<String, NodeRef> staff;
	private final Map<String, NodeRef> businessRoles;

	public CreatedItems() {
		this.positions = new HashMap<>();
		this.departments = new HashMap<>();
		this.employees = new HashMap<>();
		this.staff = new HashMap<>();
		this.businessRoles = new HashMap<>();
	}

	public Map<String, NodeRef> getPositions() {
		return positions;
	}

	public Map<String, NodeRef> getDepartments() {
		return departments;
	}

	public Map<String, NodeRef> getEmployees() {
		return employees;
	}

	public Map<String, NodeRef> getStaff() {
		return staff;
	}

	public Map<String, NodeRef> getBusinessRoles() {
		return businessRoles;
	}

}
