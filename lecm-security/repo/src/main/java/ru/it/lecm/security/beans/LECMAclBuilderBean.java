package ru.it.lecm.security.beans;

import java.util.Collections;
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
import ru.it.lecm.security.Types.SGKind;
import ru.it.lecm.security.events.INodeACLBuilder;

public class LECMAclBuilderBean
	implements InitializingBean, INodeACLBuilder
{
	private enum StdPermission {
		noaccess("-"),
		readonly("R"),
		full("RW");

		final private String info;

		private StdPermission(String info) {
			this.info = info;
		}

		public String getInfo() {
			return info;
		}

		@Override
		public String toString() {
			return String.format("perm(%s, %s)", name(), getInfo());
		}

		/**
		 * Получение объекта StdPermission по мнемоническому названию.
		 * @param name: noaccess | readonly | full
		 * @return
		 */
		public static StdPermission findPermission( String name) {
			if (name != null && name.trim().length() > 0) {
				try {
					name = name.trim().toLowerCase();
					if ("read".equals(name))
						return StdPermission.readonly;
					if ("deny".equals(name))
						return StdPermission.noaccess;
					return Enum.valueOf(StdPermission.class, name);
				} catch (Throwable t) {
					logger.error( String.format("Unknown mnemonic ignored: (%s) '%s'", StdPermission.class.getSimpleName(), name), t);
				}
			}
			return null;
		}
	}

	final static protected Logger logger = LoggerFactory.getLogger (LECMAclBuilderBean.class);

	private NodeService nodeService;
	private PermissionService permissionService;

	/**
	 * Соответствия внутрипрограммных разрешений и тех, которые имеются в Альфреско (см const ACCPERM_EMPTY)
	 * (названия ACCPERM_EMPTY должны быть введены в permissionDefinitions.xml)
	 */
	final private Map<StdPermission, String> permNames = new HashMap<StdPermission, String>();

	/**
	 * Список по статусам:
	 * (статус -> набор(Бизнес Роль -> Доступ))
	 * Может задаваться при загрузке бина и затем отслеживает изменения состава БР 
	 */
	final private Map<String, Map<String, StdPermission>> roleByStatus = new HashMap<String, Map<String,StdPermission>>();

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

	public void setRoleByStatus(Map<String, Map<String, StdPermission>> map) {
		roleByStatus.clear();

		if (map == null || map.isEmpty()) {
			logger.info("Business role mapping set EMPTY");
		} else {
			roleByStatus.putAll(map);
			logger.info("Business role mapping set to "+ roleByStatus.toString());
		}
	}

	/**
	 * Задать права для бизнес ролей солгасно статусам:
	 * @param accessMap
	 *  	ключ = название статуса;
	 *  	значения = список через ';' из 'роль:доступ'
	 *  		где роль = название роли (мнемоника),
	 *  			доступ = (noaccess | readonly | full) 
	 */
	public void setStatus2BusinessRoleMapping(Map<String, String> accessMap) {
		roleByStatus.clear();

		if (accessMap == null || accessMap.isEmpty()) {
			logger.info("Business role mapping set EMPTY");
			return;
		}

		final StringBuilder sb = new StringBuilder("Business role mapping:\n");
		for (Map.Entry<String, String> e: accessMap.entrySet()) {
			final String status = e.getKey();
			final Map<String, StdPermission> map = makeBRoleMapping( e.getValue());
			this.roleByStatus.put( status, map);
			sb.append( String.format( "\t%s \t as %s\n", status, map));
		}
		logger.info(sb.toString());
	}

	/**
	 * Сформировать карту "БР-Доступ" согласно заданному в виде строки списку.
	 * @param value список в виде "бизнес-роль:доступ;..."
	 * если доступ опущен, принимается за пустой
	 * @return
	 */
	final static Map<String, StdPermission> makeBRoleMapping(String value) {
		final Map<String, StdPermission> result = new HashMap<String, StdPermission>();
		final String[] parts = value.split(";");
		if (parts == null || parts.length == 0) {
			logger.warn("Empty business role mapping assigned");
		} else {
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


	@Override
	public void grantDynamicRole(String roleCode, NodeRef nodeRef, String userId) {
		final String authority = SGKind.getSGBusinessRolePos(nodeRef.getId(), roleCode).getAlfrescoSuffix();
		final String permission = getPermName(StdPermission.readonly); // выдать право на чтение - при смене статуса должно будет выполниться перегенерирование ... 
		permissionService.setPermission( nodeRef, authority, permission, true);
		logger.warn(String.format("Dynamic role '%s' for user '%s' granted for document '%s'", roleCode, userId, nodeRef));
	}

	@Override
	public void revokeDynamicRole(String roleCode, NodeRef nodeRef, String userId) {
		final String authority = SGKind.getSGBusinessRolePos(nodeRef.getId(), roleCode).getAlfrescoSuffix();
		permissionService.clearPermission( nodeRef, authority);
		logger.warn(String.format("Dynamic role '%s' for user '%s' revoked from document '%s'", roleCode, userId, nodeRef));
	}

	@Override
	public void rebuildACL(NodeRef nodeRef, String statusId) {
		final StringBuilder sb = new StringBuilder( String.format("Rebuild Dynamic Roles at status %s for node %s\n", statusId, nodeRef));

		// получить желаемый ACL (ДБР, доступ)...
		final Map<String, StdPermission> aclStatusMap = findPermissionsByStatus(statusId);

		// получить полный текущий ACL ...
		final Set<AccessPermission> current = permissionService.getAllSetPermissions(nodeRef);

		// вычленить id пользователей с динамическими ролями ...
		// (пользователь, ДБР)
		final Set< Pair<String, String> > pairs = filterByDynamicRoles(current);

		// замена на корректный доступ в текущем статусе
		for(Pair<String, String> item: pairs) {

			final String userId = item.getFirst(); 
			final String brole = item.getSecond();

			// TODO: возможно стоит сделать обращение через authorityService.getName(xxx) 
			final String authority = "GROUP_" + Types.SGKind.getSGBusinessRolePos(userId, brole).getAlfrescoSuffix();

			// удаление прежней ДР пользователя ...
			permissionService.clearPermission(nodeRef, authority);

			// выдаём новый доступ по ДБР для Пользователя ...
			final StdPermission perm = aclStatusMap.get(brole);
			final String rawPerm = getPermName( perm, StdPermission.noaccess);
			final boolean allowed = !ACCPERM_EMPTY.equals(rawPerm);
			if (allowed) { // ALLOW
				permissionService.setPermission(nodeRef, authority, rawPerm, true);
			} else { // DENY 
				permissionService.setPermission(nodeRef, authority, "Read", false);
			}
			sb.append(String.format("\t'%s' \t as '%s'::%s\n", authority, rawPerm, allowed));
		}

		if (logger.isInfoEnabled())
			logger.info( sb.toString());
	}

	/**
	 * Получить карту доступа, которая соответствует некоторому статусу
	 * в виде (id БизнесРоли -> Разрешение)
	 * @param statusId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, StdPermission> findPermissionsByStatus(String statusId) {
		final Map<String, StdPermission> result = roleByStatus.get(statusId);
		return (result != null) ? result : Collections.EMPTY_MAP;
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
