package ru.it.lecm.security.beans;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AccessPermission;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.util.Pair;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import ru.it.lecm.security.Types;
import ru.it.lecm.security.events.INodeACLBuilder;

public class LECMAclBuilderBean
	implements InitializingBean, INodeACLBuilder
{
	final static protected Logger logger = LoggerFactory.getLogger (LECMAclBuilderBean.class);

	private NodeService nodeService;
	private PermissionService permissionService;

	/**
	 * Соответствия внутрипрограммных разрешений и тех, которые имеются в Альфреско (см const ACCPERM_EMPTY)
	 * (названия ACCPERM_EMPTY должны быть введены в permissionDefinitions.xml)
	 */
	final private Map<StdPermission, String> permNames = new HashMap<StdPermission, String>();

	final public static String ACCPERM_EMPTY = "deny"; // "LECM_NO_ACCESS";
	final public static String ACCPERM_READ  = "Consumer"; // "LECM_READ_ACCESS";
	final public static String ACCPERM_EDIT  = "Editor"; // "LECM_FULL_ACCESS";

	public LECMAclBuilderBean() {
		permNames.put(StdPermission.noaccess, ACCPERM_EMPTY);
		permNames.put(StdPermission.readonly, ACCPERM_READ);
		permNames.put(StdPermission.full, ACCPERM_EDIT);
	}

	public NodeService getNodeService() {
		return nodeService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public PermissionService getPermissionService() {
		return permissionService;
	}

	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}

	/**
	 * Сформировать карту "Вид доступа -> Код PG в Альфреске"
	 * @param aliases ключ = (noaccess | readonly | full), значение = название permissionGroup
	 */
	public void setPermissionAliases(Map<String, String> aliases) {
		if (aliases == null) {
			logger.info("Permission aliases: NULL assignement ignored and stay unchanged");
			return;
		}

		final StringBuilder sb = new StringBuilder("Permission aliases:\n");
		this.permNames.clear();
		for (Map.Entry<String, String> e: aliases.entrySet()) {
			final StdPermission perm = StdPermission.findPermission(e.getKey());
			if (perm == null) {
				sb.append( String.format( "\tskipping unknown permission '%s'\n", e.getKey()));
				continue;
			}
			this.permNames.put( perm, e.getValue());
			sb.append( String.format( "\t%s \t as %s\n", perm.name(), e.getValue()));
		}
		logger.info(sb.toString());
	}

	static String getMapInfo( Map<String, StdPermission> map) {
		final StringBuilder sb = new StringBuilder();
		if (map == null || map.isEmpty()) {
			sb.append("\t EMPTY \n");
		} else {
			sb.append( " ===========================");
			sb.append( "\n [NN] role\t\t Access\n");
			sb.append( " ===========================");
			int i = 0;
			for (Map.Entry< String, StdPermission> item: map.entrySet()) {
				sb.append( String.format( " [%d] '%s'\t %s\n", (++i), item.getKey()
						, ((item.getValue() == null) ? "NULL" : item.getValue().toString()) 
				));
			}
			sb.append( " ===========================");
		}
		return sb.toString();
	}


	/**
	 * Сформировать карту "БР-Доступ" согласно списку, заданному в виде строки.
	 * @param value список через ';' из записей "бизнес-роль:доступ;..."
	 *  	где роль = название роли (мнемоника),
	 *  		доступ = (noaccess | readonly | full)
	 * 			если доступ опущен, принимается за пустой
	 * @return
	 */
	final static Map<String, StdPermission> makeBRoleMapping(String value) {
		final Map<String, StdPermission> result = new HashMap<String, StdPermission>();
		final String[] parts = value.split(";");
		if (parts != null) {
			for(String broleAccess: parts) {
				try {
					final String[] roleAcc = broleAccess.split(":");
					if (roleAcc.length == 0) continue;
					final String brole = roleAcc[0].trim();
					final StdPermission access = (roleAcc.length > 1) ? StdPermission.findPermission(roleAcc[1].trim()) : null;
					result.put( brole, (access != null) ? access : StdPermission.noaccess);
				} catch(Throwable t) {
					logger.error( String.format("Check invalid map point '%s',\n\t expected to be 'BRole:access'\n\t\t, where access is (noaccess | readonly | full),\n\t\t BRole = mnemonic of business role"
							, broleAccess), t);
				}
			}
		}
		return result;
	}

	/**
	 * Вернуть Альфреско-название для указанного типа разрешения
	 * @param perm
	 * @param permDefault используется при perm == null
	 * @return
	 */
	private String getPermName(StdPermission perm, StdPermission permDefault) {
		if (perm == null) perm = permDefault;
		final String result = (permNames.containsKey(perm)) ? permNames.get(perm) : ACCPERM_EMPTY;
		return result;
	}

	/**
	 * Вернуть Альфреско-название для указанного типа разрешения
	 * @param perm
	 * @return
	 */
	private String getPermName(StdPermission perm) {
		return getPermName(perm, null);
	}


	@Override
	public void afterPropertiesSet() throws Exception {
		PropertyCheck.mandatory(this, "nodeService", nodeService);
		PropertyCheck.mandatory(this, "permissionService", permissionService);

		logger.info("initialized");
	}

	String makeFullBRMEAuthName(String userId, String roleCode) {
		// TODO: возможно стоит сделать обращение через authorityService.getName(xxx) 
		return "GROUP_" + Types.SGKind.getSGBusinessRolePos(userId, roleCode).getAlfrescoSuffix(); 
	}

	/**
	 * Сформировать ПОЛНОЕ название security-группы Альфреско, которую надо 
	 * сопоставлять указанному модельному объекту
	 * @param kind тип модельного объекта
	 * @param objId Id объекта
	 * @return
	 */
	String makeFullSGName(Types.SGKind kind, String objId) {
		// TODO: возможно стоит сделать обращение через authorityService.getName(xxx) 
		return "GROUP_" + kind.getSGPos(objId).getAlfrescoSuffix();
	}


	@Override
	public void grantDynamicRole(String roleCode, NodeRef nodeRef, String userId) {
		final String authority = makeFullBRMEAuthName(userId, roleCode);
		final String permission = getPermName(StdPermission.readonly); // выдать право на чтение - при смене статуса должно будет выполниться перегенерирование ... 
		permissionService.setPermission( nodeRef, authority, permission, true);
		logger.warn(String.format("Dynamic role '%s' for user '%s' granted for document '%s' by security group <%s>", roleCode, userId, nodeRef, authority));
	}

	@Override
	public void revokeDynamicRole(String roleCode, NodeRef nodeRef, String userId) {
		final String authority = makeFullBRMEAuthName(userId, roleCode);
		permissionService.clearPermission( nodeRef, authority);
		logger.warn(String.format("Dynamic role '%s' for user '%s' revoked from document '%s'", roleCode, userId, nodeRef));
	}

	@Override
	public void rebuildStaticACL(NodeRef nodeRef, Map<String, StdPermission> accessMap) 
	{
		final StringBuilder sb = new StringBuilder( String.format("Rebuild Static Roles for folder/node %s by accessor %s \n", nodeRef, getMapInfo(accessMap)));

		// получить полный текущий ACL ...
		final Set<AccessPermission> current = permissionService.getAllSetPermissions(nodeRef);
		logger.debug("current doc ACL list is "+ current);

		if (accessMap == null || accessMap.isEmpty()) {
			permissionService.deletePermissions(nodeRef);
			sb.append("\t Permission list cleared for node "+ nodeRef);
		} else {
			/* TODO: для правильной работы в ситациях, когда пользователь имеет несколько бизнес ролей в одном документе
			 * надо сортировать его ACE-права в ACL так, чтобы пишущие шли раньше читающих (!)
			 * т.е. надо получить список всех БР пользователя, сгенерировать для них ACE, отсортировать по важности и потом вывести в ACL.
			 */

			// замена на корректный доступ в текущем статусе
			for(Map.Entry<String, StdPermission> entry: accessMap.entrySet()) {

				final String brole = entry.getKey(); 

				final String authority = makeFullSGName( Types.SGKind.SG_BR, brole);

				// удаление прежней ДР пользователя ...
				permissionService.clearPermission(nodeRef, authority);

				// выдаём новый доступ по Статической БР ...
				final StdPermission perm = accessMap.get(brole);
				final String rawPerm = getPermName( perm, StdPermission.noaccess);
				final boolean allowed = !ACCPERM_EMPTY.equals(rawPerm);
				if (allowed) { // ALLOW
					permissionService.setPermission(nodeRef, authority, rawPerm, true);
				}
				sb.append(String.format("\t'%s' \t as '%s'\n", authority, rawPerm));
			}
		}

		if (logger.isInfoEnabled())
			logger.info( sb.toString());
	}


	@Override
	public void rebuildACL(NodeRef nodeRef, Map<String, StdPermission> accessMap)
	{
		final StringBuilder sb = new StringBuilder( String.format("Rebuild Dynamic Roles for folder/node %s by accessor %s \n", nodeRef, getMapInfo(accessMap)));

		// получить полный текущий ACL ...
		final Set<AccessPermission> current = permissionService.getAllSetPermissions(nodeRef);
		logger.debug("current doc ACL list is "+ current);

		// вычленить id пользователей с динамическими ролями ...
		// (пользователь, ДБР)
		final Set< Pair<String, String> > pairs = filterByDynamicRoles(current);

		/* TODO: для правильной работы в ситациях, когда пользователь имеет несколько бизнес ролей в одном документе
		 * надо сортировать его ACE-права в ACL так, чтобы пишущие шли раньше читающих (!)
		 * т.е. надо получить список всех БР пользователя, сгенерировать для них ACE, отсортировать по важности и потом вывести в ACL.
		 */

		// замена на корректный доступ в текущем статусе
		for(Pair<String, String> item: pairs) {

			final String userId = item.getFirst(); 
			final String brole = item.getSecond();

			// TODO: возможно стоит сделать обращение через authorityService.getName(xxx) 
			final String authority = makeFullBRMEAuthName(userId, brole);

			// удаление прежней ДР пользователя ...
			permissionService.clearPermission(nodeRef, authority);

			// выдаём новый доступ по ДБР для Пользователя ...
			final StdPermission perm = accessMap.get(brole);
			final String rawPerm = getPermName( perm, StdPermission.noaccess);
			final boolean allowed = !ACCPERM_EMPTY.equals(rawPerm);

			/*
			 *  @NOTE: (!) здесь может быть rawPerm = "deny", allowed = false, но прав с названием deny не существует
			 *  permissionService.setPermission(nodeRef, authority, rawPerm, allowed); - не проходит для deny
			 */
			if (allowed) { // ALLOW
				permissionService.setPermission(nodeRef, authority, rawPerm, true);
			} else { // DENY 
				permissionService.setPermission(nodeRef, authority, "Read", false);
			}
			sb.append(String.format("\t'%s' \t as '%s'\n", authority, rawPerm));
		}

		if (logger.isInfoEnabled())
			logger.info( sb.toString());
	}

	/**
	 * Выделить в ACL-списке динамические роли, сформировать из них пары Пользователь-Роль
	 * @param acl
	 * @return уникальные пары пользователь - выданная ему роль
	 */
	private Set<Pair<String, String>> filterByDynamicRoles(
			Set<AccessPermission> acl) 
	{
		final Set<Pair<String, String>> result = new HashSet<Pair<String,String>>();
		for(Iterator<AccessPermission> iter = acl.iterator(); iter.hasNext(); ) {
			final AccessPermission ap = iter.next();
			if (Types.isDynamicRole(ap.getAuthority())) {
				// iter.remove();
				result.add( Types.getUserRolePair(ap.getAuthority()));
			} 
		}
		return result;
	}

}
