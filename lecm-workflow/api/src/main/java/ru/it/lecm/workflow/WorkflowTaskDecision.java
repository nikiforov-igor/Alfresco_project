package ru.it.lecm.workflow;

import java.util.Date;

/**
 * Базовый класс в котором описаны атрибуты принятого решения по регламенту
 *
 * @author vmalygin
 */
public class WorkflowTaskDecision {

	private String id;
	private String userName;
	private String previousUserName;
	private String decision;
	private Date startDate;
	private Date dueDate;

	public WorkflowTaskDecision() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPreviousUserName() {
		return previousUserName;
	}

	public void setPreviousUserName(String previousUserName) {
		this.previousUserName = previousUserName;
	}

	public String getDecision() {
		return decision;
	}

	public void setDecision(String decision) {
		this.decision = decision;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
}
