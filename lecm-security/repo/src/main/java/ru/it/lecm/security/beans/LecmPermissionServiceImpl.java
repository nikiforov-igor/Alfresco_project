package ru.it.lecm.security.beans;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.security.permissions.PermissionReference;
import org.alfresco.repo.security.permissions.impl.ModelDAO;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AccessPermission;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.util.Pair;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import ru.it.lecm.security.LecmPermissionService;
import ru.it.lecm.security.Types;
import ru.it.lecm.security.Types.SGPosition;
import ru.it.lecm.security.Types.SGPrivateBusinessRole;
import ru.it.lecm.security.Types.SGPrivateMeOfUser;
import ru.it.lecm.security.events.IOrgStructureNotifiers;

import javax.naming.AuthenticationException;
import javax.naming.InvalidNameException;
import java.util.*;

public class LecmPermissionServiceImpl
		implements LecmPermissionService, InitializingBean
{

	final static protected Logger logger = LoggerFactory.getLogger(LecmPermissionServiceImpl.class);

	private NodeService nodeService;
	private PermissionService permissionService;
	private AuthorityService authorityService;
	private ModelDAO modelDAOService; // "permissionsModelDAO"

	/*
	 * если потребуется прозрачное присвоение БР "по факту" - т.е. выдавать
	 * автоматически личную БР при прописывании сотрудника в ACL узла 
	 * (документа) при вызове метода grantDynamicRole
	 */ 
	private IOrgStructureNotifiers orgStructureNotifiers; 

	private final SgNameResolver sgnm = new SgNameResolver(logger);

	/* 
	 * флаги наследования родительских полномочий для статического 
	 * и динамического случая выдачи прав
	 */
	private boolean staticInheritParentPermissions = false;
	private boolean dynamicInheritParentPermissions = true;
	private LecmPermissionGroup defaultAccessOnGrant = findPermissionGroup(LecmPermissionGroup.PFX_LECM_ROLE + "Reader"); // право, которое сразу предоставляется по-умолчанию при выбаче БР

	static String getMapInfo( Map<String, LecmPermissionGroup> map) {
		final StringBuilder sb = new StringBuilder();
		if (map == null || map.isEmpty()) {
			sb.append("\t EMPTY \n");
		} else {
			sb.append( " \n ==================================== \n");
			sb.append( " [NN] role\t\t Access\n");
			sb.append( " ==================================== \n");
			int i = 0;
			for (Map.Entry< String, LecmPermissionGroup> item: map.entrySet()) {
				sb.append( String.format( " [%d] '%s'\t %s\n", (++i), item.getKey()
						, ((item.getValue() == null) ? "NULL" : item.getValue().toString())
				));
			}
			sb.append( " ==================================== \n");
		}
		return sb.toString();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		PropertyCheck.mandatory(this, "nodeService", nodeService);
		PropertyCheck.mandatory(this, "permissionService", permissionService);
		PropertyCheck.mandatory(this, "modelDAOService", modelDAOService);

		PropertyCheck.mandatory(this, "authorityService", authorityService);
		PropertyCheck.mandatory(this, "orgStructureNotifiers", orgStructureNotifiers);

		initAllGroups();
		initAllPermissions();

		logger.info("initialized");
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

	public AuthorityService getAuthorityService() {
		return authorityService;
	}

	public void setAuthorityService(AuthorityService authorityService) {
		this.authorityService = authorityService;
		this.sgnm.setAuthorityService(authorityService);
	}

	public ModelDAO getModelDAOService() {
		return modelDAOService;
	}

	public void setModelDAOService(ModelDAO modelDAOService) {
		this.modelDAOService = modelDAOService;
	}

	public IOrgStructureNotifiers getOrgStructureNotifiers() {
		return orgStructureNotifiers;
	}

	public void setOrgStructureNotifiers(IOrgStructureNotifiers value) {
		this.orgStructureNotifiers = value;
	}


	/**
	 * @return флаг наследования родительских полномочий для статического случая выдачи прав
	 */
	public boolean isStaticInheritParentPermissions() {
		return staticInheritParentPermissions;
	}

	/**
	 * @param value флаг наследования родительских полномочий для статического случая выдачи прав
	 */
	public void setStaticInheritParentPermissions(boolean value) {
		this.staticInheritParentPermissions = value;
	}

	/**
	 * @return флаг наследования родительских полномочий для динамического случая выдачи прав
	 */
	public boolean isDynamicInheritParentPermissions() {
		return dynamicInheritParentPermissions;
	}

	/**
	 * @param value флаг наследования родительских полномочий для динамического случая выдачи прав
	 */
	public void setDynamicInheritParentPermissions(boolean value) {
		this.dynamicInheritParentPermissions = value;
	}

	public LecmPermissionGroup getDefaultAccessOnGrant() {
		return defaultAccessOnGrant;
	}

	public void setDefaultAccessOnGrant(LecmPermissionGroup permGroup) {
		this.defaultAccessOnGrant = permGroup;
	}

	public void setDefaultAccessOnGrant(String permGroupName) {
		this.defaultAccessOnGrant = findPermissionGroup(permGroupName);
	}

	private Map<String, LecmPermissionImpl> allPermissions = null; // new HashMap<String, LecmPermissionImpl>();

	/**
	 * Построение общесистемного списка атомарных lecm-полномочий
	 */
	private void initAllPermissions() {
		if (this.allPermissions != null) return;
		this.allPermissions = new HashMap<String, LecmPermissionImpl>();

		final Set<PermissionReference> list = modelDAOService.getAllPermissions();
		final StringBuilder sb = new StringBuilder("\n");
		if (list == null || list.isEmpty()) {
			sb.append("(!?) No atomic permissions found\n");
		} else {
			sb.append( String.format( "Permissions scanned %d:\n\t(*) marker for LECM items\n", list.size()));
			sb.append( "\t ============================================================\n" );
			sb.append( String.format( "\t  [%s]\t%s\t%s\n", "*nn", "Name", "Type" ));
			sb.append( "\t ============================================================\n" );
			int i = 0;
			for (PermissionReference item: list) {
				i++;
				final String key = makePermissionKey(item.getName());
				final boolean isLecm = key.startsWith(LecmPermission.PFX_LECM_PERMISSION.toUpperCase());
				if (isLecm)
					this.allPermissions.put( key, new LecmPermissionImpl(item.getName()) );
				// отметка звёздочкой lecm-групп
				sb.append( String.format( "\t%s[%d]\t%s\t%s\n", (isLecm ? "*" : "" ), i, item.getName(), item.getQName() ));
			}
			sb.append( "\t ============================================================\n" );
		}
		logger.info( sb.toString() );
	}


	private String makePermissionKey(String permName) {
		return (permName != null && permName.length() > 0)
					? permName.trim().toUpperCase()
					: "";
	}


	@Override
	public LecmPermission findPermission(String lecmPermissionName) {
		if (lecmPermissionName == null)
			return null;

		if (allPermissions == null)
			initAllPermissions(); // кешируем ...

		final String key = makePermissionKey(lecmPermissionName);

		// DONE: check via real list of permissions
		final LecmPermission result = 
				(allPermissions.containsKey(key)) ? allPermissions.get(key) : null;
		if (result == null) {
			// // allPermissions.put( key, new LecmPermissionImpl(lecmPermissionName));
			final String info = String.format( "Unknown permission '%s' -> skipped", lecmPermissionName);
			// throw new RuntimeException( new InvalidNameException(info));
			logger.warn(info);
		}
		return result;
	}


	private Map<String, LecmPermissionGroup> allGroups = null;

	/**
	 * Построение общесистемного списка lecm-групп полномочий
	 */
	private void initAllGroups() {
		if (this.allGroups != null) return;
		this.allGroups = new HashMap<String, LecmPermissionGroup>();

		final Set<PermissionReference> list = modelDAOService.getAllPermissions(); // modelDAOService.getAllExposedPermissions();
		final StringBuilder sb = new StringBuilder("\n");
		if (list == null || list.isEmpty()) {
			sb.append("(!?) No permission groups found\n");
		} else {
			sb.append( String.format( "Permission groups scanned %d:\n\t(*) marker for LECM items\n", list.size()));
			sb.append( "\t ============================================================\n" );
			sb.append( String.format( "\t  [%s]\t%s\t%s\n", "*nn", "Name", "Type" ));
			sb.append( "\t ============================================================\n" );
			int i = 0;
			for (PermissionReference item: list) {
				i++;
				final String key = makePermissionGroupKey(item.getName());
				final boolean isLecm = key.startsWith(LecmPermissionGroup.PFX_LECM_ROLE.toUpperCase());
				if (isLecm)
					this.allGroups.put( key, new LecmPermissionGroupImpl(item.getName()) );
				// отметка звёздочкой lecm-групп
				sb.append( String.format( "\t%s[%d]\t%s\t%s\n", (isLecm ? "*" : "" ), i, item.getName(), item.getQName() ));
			}
			sb.append( "\t ============================================================\n" );
		}
		logger.info( sb.toString() );
	}

	private String makePermissionGroupKey(String permGroupName) {
		return (permGroupName != null && permGroupName.length() > 0)
					? permGroupName.trim().toUpperCase()
					: "";
	}

	@Override
	public LecmPermissionGroup findPermissionGroup(String lecmGroupName)
	{
		if (lecmGroupName == null)
			return null;

		// созданные кешируем для единообразия, со врменем можно будет 
		// инициализировать список по данным xml-настроек permissionService.
		final String key = makePermissionGroupKey(lecmGroupName);
		if (allGroups == null) {
			if (this.modelDAOService == null) {
				// not yet initialized
				return new LecmPermissionGroupImpl(lecmGroupName);
			}
			initAllGroups(); // кешируем ...
		}

		final LecmPermissionGroup result = 
				(allGroups.containsKey(key)) ? allGroups.get(key) : null;
		if (result == null) {
			// // allGroups.put( key, new LecmPermissionGroupImpl(lecmGroupName));
			// throw new RuntimeException( new InvalidNameException(info)) );
			final String info = String.format(String.format( "Unknown permission group name '%s' -> skipped", lecmGroupName));
			logger.warn( info);
		}

		return result;
	}

	@Override
	public Collection<LecmPermissionGroup> getPermGroups() {
		initAllGroups();
		return this.allGroups.values();
	}

	@Override
	public boolean hasPermission(LecmPermissionGroup permission, NodeRef node,
			String userLogin) {
		return hasPermission( permission.getName(), node, userLogin);
	}

	@Override
	public boolean hasPermission(final String permission, final NodeRef node, final String userLogin) {
		Boolean result;

		try {
			final RunAsWork<Boolean> runner = new RunAsWork<Boolean>() {
				@Override
				public Boolean doWork() throws Exception {
					return permissionService.hasPermission(node, permission) == AccessStatus.ALLOWED;
				}
			};

			if (userLogin == null) { // проверка относительно текущего пользователя
				result = runner.doWork();
			//	} else if (ASSYSTEM.equalsIgnoreCase(userLogin)) { // выполнить от имени системы
			//		AuthenticationUtil.runAsSystem( runner);
			} else { // выполнить от имени указанного ползователя ...
				// (for safe) получение Person по сконфигурированному имени ...
				// final NodeRef person = getPersonService().getPerson(userLogin);
				// logger.debug( String.format( "hasPermission( user '%s', perm '%s', node {%s}): found Person node is %s", userLogin, permission, node, person));

				// doit...
				result = AuthenticationUtil.runAs( runner, userLogin);
			}

		} catch(Throwable ex) {
			result = false;
			logger.error( String.format( "Exception at hasPermission( user '%s', perm '%s', node {%s}):\n %s", userLogin, permission, node, ex.getMessage()), ex);
		}

		logger.info( String.format( "hasPermission( user '%s', perm '%s', node {%s}) is %s", userLogin, permission, node, result));

		return result;
	}

	@Override
	public void grantDynamicRole(String roleCode, NodeRef nodeRef,
			String employeeId, LecmPermissionGroup permissionGroup)
	{
		final SGPrivateBusinessRole posBRME = Types.SGKind.getSGMyRolePos(employeeId, roleCode);

		// оповещение основной службы о личном присвоении БР (создание "теневой группы" )
		if (this.orgStructureNotifiers != null) { 
			final SGPrivateMeOfUser posMe = (SGPrivateMeOfUser) Types.SGKind.SG_ME.getSGPos(employeeId);
			this.orgStructureNotifiers.orgBRAssigned(roleCode, posMe);
		}

		// непосредственная нарезка в ACL ...
		final String authority = sgnm.makeSGName(posBRME); // sgnm.makeFullBRMEAuthName(userId, roleCode);
		// выдать право по-умолчанию - при смене статуса может (должно будет) выполниться перегенерирование ...
		final String permission = findACEPermission(permissionGroup);
		permissionService.setPermission( nodeRef, authority, permission, true);
		logger.warn(String.format("Dynamic role '%s' for employee '%s' granted as {%s} for document '%s' by security group <%s>", roleCode, employeeId, permission, nodeRef, authority));
	}

	@Override
	public void revokeDynamicRole(String roleCode, NodeRef nodeRef,
			String employeeId) {
		final String authority = sgnm.makeFullBRMEAuthName(employeeId, roleCode);
		permissionService.clearPermission( nodeRef, authority);
		logger.warn(String.format("Dynamic role '%s' for employee '%s' revoked from document '%s'", roleCode, employeeId, nodeRef));
	}

	@Override
	public void grantAccess(LecmPermissionGroup permissionGroup, NodeRef nodeRef,
			String employeeId) 
	{
		final SGPosition posUserSpec = Types.SGKind.getSGSpecialUserRole(employeeId, permissionGroup, nodeRef);

		// оповещение основной службы о личном присвоении новой группы
		if (this.orgStructureNotifiers != null) { 
			final SGPrivateMeOfUser posMe = (SGPrivateMeOfUser) Types.SGKind.SG_ME.getSGPos(employeeId);
			this.orgStructureNotifiers.sgInclude(posMe, posUserSpec);
		}

		// непосредственная нарезка в ACL ...
		final String authority = sgnm.makeSGName(posUserSpec); // sgnm.makeFullBRMEAuthName(userId, roleCode);
		// выдать право по-умолчанию - при смене статуса может (должно будет) выполниться перегенерирование ...
		final String permission = findACEPermission(permissionGroup);
		permissionService.setPermission( nodeRef, authority, permission, true);
		logger.warn(String.format("Private role '%s' for employee '%s'\n\tGRANTED as {%s}\n\t for document '%s'\n\t by security group <%s>", permissionGroup, employeeId, permission, nodeRef, authority));
	}

	@Override
	public void revokeAccess(LecmPermissionGroup permissionGroup, NodeRef nodeRef,
			String employeeId) {
		final SGPosition posUserSpec = Types.SGKind.getSGSpecialUserRole(employeeId, permissionGroup, nodeRef);

		final String authority = sgnm.makeSGName(posUserSpec);
		permissionService.clearPermission( nodeRef, authority);
		logger.warn(String.format("Private role '%s' for employee '%s'\n\tREVOKED from document '%s'", permissionGroup, employeeId, nodeRef));
	}

	@Override
	public void rebuildStaticACL(NodeRef nodeRef,
			Map<String, LecmPermissionGroup> accessMap) {
//		logger.info( "rebuildStaticACL for node "+ nodeRef);
//		return;
		final StringBuilder sb = new StringBuilder( String.format("Rebuild Static Roles for folder/node '%s', inherit parent access rules: %s\n\t by access table: %s \r\n", nodeRef, isStaticInheritParentPermissions(), getMapInfo(accessMap)));
		try {

			// получить полный текущий ACL ...
			final Set<AccessPermission> current = permissionService.getAllSetPermissions(nodeRef);
			logger.debug( String.format( "current doc {%s} ACL list is ", nodeRef, current));

			// (!) задаём свой тип наследования статических прав от родителей
			permissionService.setInheritParentPermissions(nodeRef, this.isStaticInheritParentPermissions());

			if (accessMap == null || accessMap.isEmpty()) {
				permissionService.deletePermissions(nodeRef);
				sb.append("\t Permission list cleared for node "+ nodeRef);
			} else {
				/* TODO: для правильной работы в ситуациях, когда пользователь имеет несколько бизнес ролей в одном документе
				 * возможно стоит отсортировать все ACE-права в ACL так, чтобы пишущие шли раньше читающих (!)
				 * т.е. надо получить список всех БР пользователя, сгенерировать для них ACE, отсортировать по важности и потом вывести в ACL.
				 */
				sb.append( "\t SG-assigned list is: \n");
				// замена на корректный доступ в текущем статусе
				for(Map.Entry<String, LecmPermissionGroup> entry: accessMap.entrySet()) {
					final String brole = entry.getKey();
					final String authority = sgnm.makeFullSGName( Types.SGKind.SG_BR, brole);
					final LecmPermissionGroup perm = accessMap.get(brole);

					// выдаём новый доступ по Статической БР для Пользователя ...
					try {
						setACE(nodeRef, authority, perm, sb);
					} catch(Throwable t) { // (!) Исключения журналируем, но не поднимаем
						sb.append( "\n\t (!) exception "+ t.getMessage());
						logger.error( String.format("exception in setACE( nodeRef='%s', auth='%s', perm=%s)", nodeRef, authority, perm), t);
					}
				}
			}
		} catch(Throwable t) {  // (!) Исключения журналируем, но не поднимаем
			sb.append( "\n\t (!) exception "+ t.getMessage());
			logger.error( String.format("exception in rebuildACL( nodeRef='%s', map='%s')", nodeRef, accessMap), t);
		} finally {
			if (logger.isInfoEnabled())
				logger.info( sb.toString());
		}
	}

	@Override
	public void rebuildACL(NodeRef nodeRef,
			Map<String, LecmPermissionGroup> accessMap) {
//		logger.info( "rebuildStaticACL for node "+ nodeRef);
//		return;
		final StringBuilder sb = new StringBuilder( String.format("Rebuild Dynamic Roles for folder/node '%s', inherit parent access rules: %s\n\t by access table: %s \n", nodeRef, isDynamicInheritParentPermissions(), getMapInfo(accessMap)));
		try {
			// получить полный текущий ACL ...
			final Set<AccessPermission> current = permissionService.getAllSetPermissions(nodeRef);
			logger.debug("current doc ACL list is "+ current);

			// (!) задаём свой тип наследования динамических прав от родителей
			permissionService.setInheritParentPermissions(nodeRef, this.isDynamicInheritParentPermissions());

			// вычленить id пользователей с динамическими ролями ...
			// (пользователь, ДБР)
			final Set< Pair<String, String> > pairs = filterByDynamicRoles(current);

			/* TODO: для правильной работы в ситуациях, когда пользователь имеет несколько бизнес ролей в одном документе
			 * надо сортировать его ACE-права в ACL так, чтобы пишущие шли раньше читающих (!)
			 * т.е. надо получить список всех БР пользователя, сгенерировать для них ACE, отсортировать по важности и потом вывести в ACL.
			 */
			sb.append( "\t SG-assigned list is: \n");
			// замена на корректный доступ в текущем статусе
			for(Pair<String, String> item: pairs) {
				final String userId = item.getFirst();
				final String brole = item.getSecond();
				final String authority = sgnm.makeFullBRMEAuthName(userId, brole);
				final LecmPermissionGroup perm = accessMap.get(brole);
				// выдаём новый доступ по ДБР для Пользователя ...
				try {
					setACE(nodeRef, authority, perm, sb);
				} catch(Throwable t) { // (!) Исключения журналируем, но не поднимаем
					sb.append( "\n\t (!) exception "+ t.getMessage());
					logger.error( String.format("exception in setACE( nodeRef='%s', auth='%s', perm=%s)", nodeRef, authority, perm), t);
				}
			}
		} catch(Throwable t) { // (!) Исключения журналируем, но не поднимаем
			sb.append( "\n\t (!) exception "+ t.getMessage());
			logger.error( String.format("exception in rebuildACL( nodeRef='%s', map='%s')", nodeRef, accessMap), t);
		} finally {
			if (logger.isInfoEnabled())
				logger.info( sb.toString());
		}
	}

	/**
	 * Представлене для группы lecm-полномочий системы.
	 * Список таких групп полномочий жёстко связан с permissionDfinitions.xml Альфреско
	 * (см стд серсвис PermissionService).
	 * Выделен отдельный класс, чтобы можно было runtime-контролировать 
	 * корректность используемых названий групп и точно параметризовать методы 
	 * интерфейса LecmPermissionService вместо многозначного String. 
	 * Создание объектов типа см. LecmPermissionService.getPerm() 
	 */
	public class LecmPermissionGroupImpl
			extends PrefixedNameKeeper
			implements LecmPermissionGroup
	{
		/**
		 * @param fullLecmPermGroupName полное название lecm-группы полномочий (включая префикс PFX_LECM_ROLE = "LECM_BASIC_PG_"), 
		 * название соот-щего permissionSet Альфреско будет точно таким же.
		 * Например, "LECM_BASIC_PG_Initiator", "LECM_BASIC_PG_Reader"
		 * @throws InvalidNameException 
		 */
		public LecmPermissionGroupImpl(String fullLecmPermGroupName) {
			super(LecmPermissionGroup.PFX_LECM_ROLE, fullLecmPermGroupName);
		}
	}

	/**
	 * Представлене для отдельного lecm-разрешения системы.
	 * Список таких разрешений жёстко связан с permissionDfinitions.xml Альфреско
	 * (см стд серсвис PermissionService).
	 * Выделен отдельный класс, чтобы можно было runtime-контролировать 
	 * корректность используемых полномочий и точно параметризовать методы 
	 * интерфейса LecmPermissionService вместо многозначного String. 
	 * Создание объектов типа см. LecmPermissionService.getPermGroups()
	 */
	class LecmPermissionImpl
			extends PrefixedNameKeeper
			implements LecmPermission
	{

		/**
		 * @param fullLecmPermName полное название lecm-полномочия (включая префикс PFX_LECM_PERMISSION = "_lecmPerm_"), 
		 * название соот-щего permissionSet Альфреско будет точно таким же.
		 * Например, "_lecmPerm_SetRate", "_lecmPerm_CreateTag"
		 * @throws InvalidNameException 
		 */
		public LecmPermissionImpl(String fullLecmPermName) {
			super( LecmPermission.PFX_LECM_PERMISSION, fullLecmPermName);
		}
	}

	/**
	 * Внут класс для хранения имени  префикса
	 */
	abstract class PrefixedNameKeeper {

		// Всегда не NULL и без незначащих пробелов
		final String fullName;

		final String prefix;

		/**
		 * @param prefix префикс в названии
		 * @param fullName полное название (включая префикс)
		 * // @throws InvalidNameException 
		 */
		protected PrefixedNameKeeper(String prefix, String fullName) {
			super();
			this.prefix = (prefix != null) ? prefix.trim() : "";
			this.fullName = (fullName != null) ? fullName.trim() : "";

			// для жёсткого контроля - проверим чтобы название начиналось с префикса
			if (!this.fullName.toUpperCase().startsWith(this.prefix.toUpperCase()))
				throw new RuntimeException( new InvalidNameException( String.format( "Name '%s' must start with prefix '%s'", this.fullName, this.prefix)));
		}

		/**
		 * Вернуть префикс в названии name.
		 * @return префикс, всегда не NULL.
		 */
		public String getPrefix() {
			return prefix;
		}

		/**
		 * Вернуть полное название (с префиксом)
		 * @return название, всегда не NULL.
		 */
		public String getName() {
			return fullName;
		}

		/**
		 * Вернуть короткое название (без префикса prefix)
		 * @return короткое наименование, всегда не NULL.
		 */
		public String getShortName() {
			return (fullName.toUpperCase().startsWith( prefix.toUpperCase())) 
					? fullName.substring(prefix.length())
					: fullName;
		}

		@Override
		public String toString() {
			// return this.getClass().getSimpleName()+ "("+ getShortName() + ")";
			return this.fullName;
		}

		/**
		 * ignore-case hashcode (uppercase)
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((fullName == null) ? 0 : fullName.toUpperCase().hashCode());
			return result;
		}

		/**
		 * ignore-case compare
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final PrefixedNameKeeper other = (PrefixedNameKeeper) obj;
			return (this.getName().toUpperCase().equalsIgnoreCase( other.getName().toUpperCase()));
		}

	}


	/**
	 * Выполнить формирование ACE для списка доступа ACL указанного узла
	 * @param nodeRef
	 * @param authority
	 * @param rawPerm низкоуровневое разрешение
	 * @param destBuf для формирования журнальных сообщений, м.б. Null
	 * @throws AuthenticationException 
	 */
	
	void setACE(
			final NodeRef nodeRef
			, final String authority
			, final LecmPermissionGroup permGrp
			, final StringBuilder destBuf
			) throws AuthenticationException 
	{
		// удаление прежней auth-записи ...
		permissionService.clearPermission(nodeRef, authority);

		final String rawPerm = findACEPermission(permGrp);
		final boolean allowed = !ACCPERM_EMPTY.equals(rawPerm);

		logger.debug( String.format("calling setACE( nodeRef='%s', auth='%s', rawPerm='%s', allow=%s) ...", nodeRef, authority, rawPerm, allowed));

		if (!this.authorityService.authorityExists(authority))
			throw new AuthenticationException( String.format( "Security group not exists '%s': node '%s'", authority, nodeRef));

		/*
		 *  @NOTE: (!) здесь может быть rawPerm = "deny", allowed = false, но прав с названием deny не существует
		 *  permissionService.setPermission(nodeRef, authority, rawPerm, allowed); - не проходит для deny
		 */
		if (allowed) // ALLOW
			permissionService.setPermission(nodeRef, authority, rawPerm, true);
		else { // DENY
			/*
			 * (2013.03.01, RuSA, EERofeev) если явно прописать запрет чтения то
			 * у пользователя входящего и в группу с DENY и в другие группы 
			 * (которым чтение разрешено), потеряется право чтения, т.е. тогда 
			 * надо будет "разводить" слои доступа c индексами.
			 * Сейчас не станем усложнять и просто не пропишем ничего (clear уже было выполнено).
			 *  
			permissionService.setPermission(nodeRef, authority, "Read", false); // явный запрет
			 */
		}

		logger.debug( String.format("... called OK setACE( nodeRef='%s', auth='%s', rawPerm='%s', allow=%s)", nodeRef, authority, rawPerm, allowed));

		if (destBuf != null)
			destBuf.append(String.format("\t'%s' \t as '%s'\n", authority, rawPerm));
	}

	void setACE( final NodeRef nodeRef, final String authority, final LecmPermissionGroup permGrp)
			throws AuthenticationException
	{
		setACE(nodeRef, authority, permGrp, null);
	}

	/**
	 * Вернуть название полномочия Альфреско, которое будет соот-ть группе полномочий 
	 * @param permissionGroup группа полномочий
	 * @return Сейчас воз-ет полное имя группы permissionGroup, т.к. она уже
	 * является достаточной для Альфреско и дробить до атомарных полномочий не требуется.
	 */
	String findACEPermission(LecmPermissionGroup permissionGroup) {
		if (permissionGroup == null) permissionGroup = this.defaultAccessOnGrant;
		final String permission = (permissionGroup == null) ? null : permissionGroup.getName();
		return permission;
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
				result.add( Types.splitAuthname2UserRolePair(ap.getAuthority()));
			}
		}
		return result;
	}

}
