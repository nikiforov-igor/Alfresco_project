package ru.it.lecm.security.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AccessPermission;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.it.lecm.model.BusinessRole;
import ru.it.lecm.model.Employee;
import ru.it.lecm.security.IBusinessRoleManager;
import ru.it.lecm.utils.DataMapper;
import ru.it.lecm.utils.DataMapper.MapException;

public class BusinessRoleManagerImpl 
	implements IBusinessRoleManager 
{
	final private static Logger logger = LoggerFactory.getLogger (BusinessRoleManagerImpl.class);

	// private NodeService nodeService;
	private ServiceRegistry serviceRegistry;

	public ServiceRegistry getServiceRegistry() {
		return serviceRegistry;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	@Override
	public void checkEmployee(NodeRef employeeRef) throws SecurityException {
		// Загружаем модельный объект типа Employee ...
		final NodeService nodeServ = this.serviceRegistry.getNodeService();
		final Map<QName, Serializable> props = nodeServ.getProperties( employeeRef);
		try {
			final DataMapper<Employee> mapper = DataMapper.getMapper(Employee.class);
			final Employee employee = mapper.assignPojo(new Employee(), props);
			checkEmployee(employee);
		} catch(MapException ex) {
			throw new SecurityException(ex);
		}
	}

	// NOTE: (?) Создать прямо тутпри отсутствии
	public void checkEmployee(Employee employee) throws SecurityException {
		/* Проверить наличие соот-щего пользователя Alfresco */
		final PersonService persons = this.serviceRegistry.getPersonService();
		if (!persons.personExists(employee.getLoginName())) { // НЕТУ
			final String msg = String.format(
					"Person <%s> not exists, login <%s>, node %s"
					+ "\n\t create system user for him"
					, employee.getDisplayName(), employee.getLoginName()
					, employee.getNodeRef());
			logger.error( msg);

			// поднимаем исключение если нет (автоматом сейчас не создаём)
			// variant: if (!persons.createMissingPeople()) throw "can not create automatically" else createSystemUser(...);
			throw new SecurityException(msg);
		}
	}


	@Override
	public void ensureBusinessRole(NodeRef broleRef) {
		// Загружаем в данные узла в модельный объект
		final NodeService nodeServ = this.serviceRegistry.getNodeService();

		// this.serviceRegistry.getNamespaceService().getNamespaceURI(prefix);
		final Map<QName, Serializable> broleProps = nodeServ.getProperties( broleRef);

		// 0) надо проинициализировать BusinessRole mapper-ом ...
		BusinessRole role;
		try {
			final DataMapper<BusinessRole> mapper = DataMapper.getMapper(BusinessRole.class);
			role = mapper.assignPojo( new BusinessRole(), broleProps);
		} catch (MapException e) {
			final String msg = "Cannot init business role from node "+ broleRef;
			logger.error(msg, e);
			throw new Error( msg, e);
		}

		/* 1) Гарантировать наличие теневой sec-group для указанной роли */
		final AuthorityService authServ = this.serviceRegistry.getAuthorityService();
		if (!authServ.authorityExists(role.getSecurityGroup())) {
			logger.debug( String.format( "creating security group <%s>", role.getSecurityGroup()));
			authServ.createAuthority(AuthorityType.GROUP, role.getSecurityGroup());
			logger.warn( String.format( "security group <%s> created for business role <%s>", role.getSecurityGroup(), role.getName()));
		}

		// TODO: 2) Вложить в неё sec-groups, которые соот-ют должностным позициям 

		// 3) Сформировать ACL список в указанных для этой бизнес-роли папках
		prepareACLs(role);
	}

	// @Override
	public void prepareACLs(BusinessRole role) {

		/* Сформировать для БР ACL список в соот-щие папки */
		int granted = 0, revoked = 0;
		if (role.getAccessList() != null ) {
			final PermissionService permServ = this.serviceRegistry.getPermissionService();
			for(Pair<NodeRef, Collection<AccessPermission>> pair: role.getAccessList()) {
				// pair.getFirst() : NodeRef
				// pair.getSecond() : Collection<AccessPermission>
				final NodeRef nodeRef = pair.getFirst();
				final String authority = role.getSecurityGroup();

				// для каждого отдельного разрешения ...
				if (pair.getSecond() == null) {
					// (!) удаление доступа к папке, т.к. явно заданое NULL считаем очисткой
					permServ.clearPermission(nodeRef, authority);
					revoked++;
					logger.info( String.format( "ACEs for security group <%s> / business role <%s>:\n\trevoke access <%s> to node %s"
							, role.getSecurityGroup(), role.getName()
							, authority, nodeRef
					));
				} else {
					for (AccessPermission acc: pair.getSecond()) {
						// сверяем авторизации: группы и элемента списка ...
						// (!) значение NULL совместимо
						if (acc.getAuthority() != null && !authority.equals(acc.getAuthority()) )
								throw new SecurityException( String.format("Invalid AccessPermission item: authority <%s> must be <%s>"
										, acc.getAuthority(), authority) );
						final boolean allow = acc.getAccessStatus() != AccessStatus.DENIED;
						permServ.setPermission(nodeRef, authority, acc.getPermission(), allow);
						granted++;
						logger.info( String.format( "ACEs for security group <%s> / business role <%s>:\n\t %s access <%s> to node %s"
								, role.getSecurityGroup(), role.getName()
								, (allow ? "ALLOWED" : "DENIED")
								, authority, nodeRef
						));
					}
				}
			}
		}

		if (granted + revoked == 0)
			logger.warn( String.format( "(!?) NO ACLs for security group <%s> / business role <%s>", role.getSecurityGroup(), role.getName()));
		else
			logger.warn( String.format( "ACEs for security group <%s> / business role <%s>:\n\tgranted: %s\n\trevoked:%s"
					, role.getSecurityGroup(), role.getName()
					, granted, revoked 
			));
	}

	@Override
	public void regroupUser(Employee employee, BusinessRole groupRole, boolean allow) {
		 /* Внести/убрать пользователя в sec-группу, соот-щую бизнес роли */
		final AuthorityService authServ = this.serviceRegistry.getAuthorityService();

		final String userName = employee.getLoginName();
		final String usrNameId = authServ.getName(AuthorityType.USER, employee.getLoginName());
		final String grpNameId = authServ.getName(AuthorityType.GROUP, groupRole.getSecurityGroup());

		final Set<String> current = authServ.getAuthoritiesForUser(usrNameId);
		if (allow) {
			if (current.contains(grpNameId)) {
				// уже имеется такая группа ...
				logger.warn( String.format( "user <%s> already present at security group <%s> /business role <%s>", userName, groupRole.getSecurityGroup(), groupRole.getName()));
			} else {
				authServ.addAuthority(grpNameId, usrNameId);
				logger.warn( String.format( "user <%s> added into security group <%s> /business role <%s>", userName, groupRole.getSecurityGroup(), groupRole.getName()));
			}
		} else {
			if (!current.contains(grpNameId)) {
				// уже вне этой группы ...
				logger.warn( String.format( "user <%s> already absent at security group <%s> /business role <%s>", userName, groupRole.getSecurityGroup(), groupRole.getName()));
			} else {
				authServ.removeAuthority(grpNameId, usrNameId);
				logger.warn( String.format( "user <%s> removed from security group <%s> /business role <%s>", userName, groupRole.getSecurityGroup(), groupRole.getName()));
			}
		}
	}

	@Override
	public void ensureOrgStructureUnit(NodeRef nodeRef) {
		/* В зависимости от типа элемента: 
			проверить наличие группы или пользователя, если нет тогда согласно
			типу узла: либо создать папку, либо поднять исключение (для сотрудника, 
			когда нет system-user) 
		 */
		// TODO:
	}


//	/**
//	 * Создать системного пользователя для указанного Сотрудника
//	 */
//	PersonService.PersonInfo createUserForEmployee(NodeRef emplRef) {
//		// конвертирование "lecm-orgstr:employee" +"lecm-orgstr:personal-data" -> "cm:person"
//		// persons.createPerson(properties);
//		// if (!persons.createMissingPeople()) throw "can not create automatically"; 
//		// PersonService.PersonInfo pi;
//		// this.serviceRegistry.getNodeService().getChildAssocs(nodeRef, childNodeTypeQNames)
//	}
	/*
	import org.alfresco.repo.security.permissions.impl.AccessPermissionImpl;
	import org.alfresco.service.cmr.security.PermissionService;
		void todo() {
			PermissionService ps;
			AccessPermission ap = new AccessPermissionImpl();
			NodeRef nodeRef;
			ps.getAllSetPermissions(nodeRef);
		}
	 */
}
