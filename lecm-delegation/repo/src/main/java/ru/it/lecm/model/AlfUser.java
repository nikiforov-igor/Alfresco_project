package ru.it.lecm.model;

import java.util.List;

import org.alfresco.repo.security.permissions.impl.AccessPermissionImpl;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AccessPermission;
import org.alfresco.service.cmr.security.PermissionService;

/**
 * Alfresco-пользователь системы
 * (?!) ВОзможно не нужен в таком виде, т.к. достатоно знать login и использовать службу PermissionService
 * @author rabdullin
 *
 */
public class AlfUser {

	private String loginName;

	private List<String> authorities;

}
