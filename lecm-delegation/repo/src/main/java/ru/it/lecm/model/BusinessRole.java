package ru.it.lecm.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AccessPermission;
import org.alfresco.util.Pair;

/**
 * Модельный класс для представления бизнес-роли в достаточном виде для 
 * security-слоя в LogicECM
 */
public class BusinessRole implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Название бизнес роли
	 */
	private String name;

	/**
	 * Название соот-щей security-group Альфреско
	 */
	private String securityGroup;

	/**
	 * Список сотрудников с данной БР 
	 */
	// private List<Employee> userList;

	/**
	 * Папки (связанные с данной БР) с соот-щими правами на эту папку данной БР.
	 */
	private List<Pair<NodeRef, Collection<AccessPermission>>> accessList;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSecurityGroup() {
		return securityGroup;
	}

	public void setSecurityGroup(String securityGroup) {
		this.securityGroup = securityGroup;
	}

//	public List<Employee> getUserList() {
//		return userList;
//	}
//
//	public void setUserList(List<Employee> userList) {
//		this.userList = userList;
//	}

	public List<Pair<NodeRef, Collection<AccessPermission>>> getAccessList() {
		return accessList;
	}

	public void setAccessList(
			List<Pair<NodeRef, Collection<AccessPermission>>> accessList) {
		this.accessList = accessList;
	}

	@Override
	public String toString() {
		return "BusinessRole [name=" + name
				+ ", securityGroup=" + securityGroup
				+ ", accessList=" + accessList
				// + ", userList=" + userList
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((securityGroup == null) ? 0 : securityGroup.hashCode());
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
		final BusinessRole other = (BusinessRole) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (securityGroup == null) {
			if (other.securityGroup != null)
				return false;
		} else if (!securityGroup.equals(other.securityGroup))
			return false;
		return true;
	}

}
