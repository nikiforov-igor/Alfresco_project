package ru.it.lecm.model;

import java.io.Serializable;

/**
 * Сотрудник в орг-штатке.
 * @author rabdullin
 */
public class Employee implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * id узла сотрудника
	 */
	private String nodeRef;

	/**
	 * Имя (название) сотрудника
	 * (прочитано из свойства nodeRef)
	 */
	private String displayName;

	/**
	 * Alfresco-user в системе, который соот-ет сотруднику 
	 */
	private String loginName;


	public Employee() {
	}

	/**
	 * Конструктор для "новых" сотрудников, которые ещё не были созданы в Alfresco
	 * @param displayName
	 * @param loginName
	 */
	public Employee(String displayName, String loginName) {
		this(null, displayName, loginName);
	}

	public Employee(String nodeRef, String displayName, String loginName) {
		super();
		this.nodeRef = nodeRef;
		this.displayName = displayName;
		this.loginName = loginName;
	}

	@Override
	public String toString() {
		return "Employee [loginName=" + loginName+ ", nodeRef=" + nodeRef
				+ ", displayName=" + displayName + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((loginName == null) ? 0 : loginName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Employee other = (Employee) obj;
		if (loginName == null) {
			if (other.loginName != null)
				return false;
		} else if (!loginName.equals(other.loginName))
			return false;
		return true;
	}

	public String getNodeRef() {
		return nodeRef;
	}

	public void setNodeRef(String nodeRef) {
		this.nodeRef = nodeRef;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

}
