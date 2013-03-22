package ru.it.lecm.integrotest.actions.checkers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AccessPermission;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.namespace.QName;
import org.slf4j.LoggerFactory;

import ru.it.lecm.integrotest.TestFailException;
import ru.it.lecm.integrotest.actions.LecmActionBase;
import ru.it.lecm.integrotest.utils.NodeRefData;
import ru.it.lecm.integrotest.utils.Utils;

/**
 * Класс для проверки доступа к узлам.
 * Задётся:
 *   - узел (или поисковый запрос по атрибуту),
 *   - таблица нарезки прав по БР,
 *   - таблица нарези БР по пользователям,
 *   - атрибут и значение, по которым производить проверку доступа на запись.
 *
 */
public class LecmCheckNodeACL extends LecmActionBase {

	private static final String STR_TABLE_DELIMITER = "\n  ========================================";

	/**
	 * Значение по-умолчанию для проверки возможности записи в атрибут
	 */
	private static final String DEFAULT_WRITE_VALUE = "abc_write_test";

	/* проверяемый узел (папка или документ), см также название */
	private NodeRef nodeRef;
	private String nodeRefMacros;

	/**
	 * Атрибут, используемый для проверки чтения-записи.
	 * Формат строки - в виде для QName "{type}prop"
	 */
	private String rwCheckingPropName;

	/*
	 * поисковые параметры узла для случая, когда узел задаётся не напрямую, 
	 * а запросом: "typeName", "propName", "value"
	 */
	private final NodeRefData findRef = new NodeRefData();

	/**
	 * Значение для тестирования возможности записи
	 */
	private String writeValue = DEFAULT_WRITE_VALUE;


	private UserAccessTable accessMap;
	private boolean continueOnErrors = true;
	private boolean dontThrowErrors = false;

	/**
	 * Таблица прав доступа (value) для пользователей (key)
	 */
	public interface UserAccessTable {
		/**
		 * @return список пользователь(key) -> соответствующее право (lecm-группа полномочий "LECM_BASIC_PG_") 
		 */
		public Map<String, String> getUsersAccess();

		/**
		 * Проверить корректнгость данных, при ошибках - выполнить журналирование
		 * и поднять исключение
		 */
		public void validateAndLogIfError();
	}

	/**
	 * Доступ сразу для пользователей
	 */
	static class UserWithPermissions implements UserAccessTable {

		// ключ = login пользователя, value=соот-щее право
		private Map<String, String> usersAccessMap;  // для строк в формате функции Utils.makeBRoleMapping

		@Override
		public void validateAndLogIfError() {
			chkAndLogCondition( this.usersAccessMap == null || this.usersAccessMap.isEmpty(), "No users to permission mapping assigned");

			// ... данные всегда сбалансированы, если они указаны
		}

		@Override
		public Map<String, String> getUsersAccess() {
			return this.usersAccessMap;
		}

		public void setUsersAccess(Map<String, String> value) {
			this.usersAccessMap = value;
		}

		/**
		 * Задать пользователей и их доступ, в виде:
		 * 		пользователь:доступ; пользователь:доступ ...
		 * @param value строка в формате функции Utils.makeBRoleMapping
		 */
		public void setUsersAccessMap(String value) {
			setUsersAccess( Utils.makeBRoleMapping(value));
		}

	}

	/**
	 * Доступ с использованием бизнес-ролей:
	 *   БР -> доступ
	 *   БР -> пользователь
	 */
	static class UserWithBR implements UserAccessTable {

		/* перечисление бизнес-ролей (key) и соот-щих разрешений (value) */
		private Map<String, String> roleAccess; // для строк в формате функции Utils.makeBRoleMapping

		/* 
		 * перечисление бизнес-ролей (key) и соот-щих логинов пользователей (value),
		 * чтобы в итоге проверять доступ по бизнес-ролям
		 */
		private Map<String, String> roleUsers; // строка в формате функции Utils.makeSplitMapping


		/**
		 * @return карта БР -> доступ
		 */
		public Map<String, String> getRoleAccess() {
			return roleAccess;
		}

		public void setRoleAccess(Map<String, String> roleAccess) {
			this.roleAccess = roleAccess;
			LoggerFactory.getLogger (UserWithBR.class).debug( String.format( "after setRoleAccess access table:\n\t %s", this.roleAccess));
		}

		/**
		 * Задать бизнес-роли и их доступ, в виде:
		 * 		БР:доступ; БР:доступ ...
		 * @param value строка в формате функции Utils.makeBRoleMapping
		 */
		public void setRoleAccessMap(String value) {
			setRoleAccess( Utils.makeBRoleMapping(value));
		}

		/**
		 * @return карта БР -> пользователь
		 */
		public Map<String, String> getRoleUsers() {
			return roleUsers;
		}

		/**
		 * Задать соответствие бизнес ролей и id Сотрудников, обладающих ролью
		 * @param roleUsers
		 */
		public void setRoleUsers(Map<String, String> roleUsers) {
			this.roleUsers = roleUsers;
			LoggerFactory.getLogger (UserWithBR.class).debug( String.format( "after setRoleUsers access table:\n\t %s", this.roleUsers));
		}

		/**
		 * Задать соответствие бизнес ролей и логинов пользователей, соот-щие 
		 * Сотрудники которых обладают этой ролью:
		 * @param value перечисление в виде "роль1:user1; роль2:user2 ..."
		 * т.е. список через запятую или точку с запятой из элементов вида "роль=логин_сотрудника"
		 */
		public void setRoleUsersMap(String value) {
			setRoleUsers( Utils.makeSplitMapping(value));
		}

		@Override
		public void validateAndLogIfError() {
			// ... данные всегда сбалансированы, если они указаны
			chkAndLogCondition( roleAccess == null || roleAccess.isEmpty(), "No busness roles mapping assigned");
			chkAndLogCondition( roleUsers == null || roleUsers.isEmpty(), "No users by busness roles mapping assigned");

			// проверить чтобы для всех ролей были указаны пользователи
			chkAndLogCondition( !roleUsers.keySet().containsAll(roleAccess.keySet()), "Some business roles has not been mapped into users");
		}

		@Override
		public Map<String, String> getUsersAccess() {
			final Map<String, String> result = new HashMap<String, String>();
			for(Map.Entry<String, String> e: roleAccess.entrySet() ) {
				final String brole = e.getKey();
				final String perm = (e.getValue() != null) ? e.getValue().trim() : "";
				final String usrLogin = roleUsers.get(brole); // соответствующий этой БР Пользователь
				result.put( usrLogin, perm);
			}
			return result;
		}

	}

	public NodeRef getNodeRef() {
		return nodeRef;
	}

	/**
	 * Задать проверяемый узел.
	 * @param value
	 */
	public void setNodeRef(NodeRef value) {
		this.nodeRef = value;
		// this.findRef.clear(); // drop
	}

	/**
	 * Задать проверяемый узел.
	 * @param value
	 */
	public void setNodeRefStr(String value) {
		this.setNodeRef( new NodeRef(value));
	}

	/**
	 * Задать макрос, который надо будет использовать для вычисления nodeRef 
	 * @param macrosValue
	 */
	public void setNodeRefMacros(String macrosValue) {
		this.nodeRefMacros = macrosValue;
	}

	/**
	 * @param macros в виде "список_аргументов.аргумент"
	 * список аргументов - один из "result", "work", "config",
	 * аргумент - ключ для получения значения из этого списка.
	 * например <property name="nodeByMacros" value="result.createdId" />
	 */
	public NodeRef findNodeByMacros(String macros) {
		return (NodeRef) getArgsAssigner().getMacroValue(macros);
	}

	public NodeRefData getFindRef() {
		return this.findRef;
	}

	/**
	 * @return Атрибут, используемый для проверки чтения-записи.
	 * Формат строки - в виде для QName
	 */
	public String getRwCheckingPropName() {
		return rwCheckingPropName;
	}

	/**
	 * Задать название атрибута, который будет использоваться для проверки чтения-записи.
	 * @param rwCheckingPropName Формат строки - в виде для QName.
	 */
	public void setRwCheckingPropName(String rwCheckingPropName) {
		this.rwCheckingPropName = rwCheckingPropName;
	}

	/**
	 * @return Значение для тестирования возможности записи
	 */
	public String getWriteValue() {
		return writeValue;
	}

	/**
	 * @param value Значение для тестирования возможности записи
	 */
	public void setWriteValue(String value) {
		this.writeValue = value;
	}

	public UserAccessTable getAccessMap() {
		return accessMap;
	}

	public void setAccessMap(UserAccessTable accessMap) {
		this.accessMap = accessMap;
	}

	/**
	 * @return true (это значение по-умолчанию), если при ошибках проверки надо продолжить без поднятия исключения,
	 * false = поднимать исключения при ошибках проверки доступа.
	 */
	public boolean isContinueOnErrors() {
		return continueOnErrors;
	}

	/**
	 * @param flag true (это значение по-умолчанию), если при ошибках проверки надо продолжить без поднятия исключения,
	 * false = поднимать исключения при ошибках проверки доступа.
	 */
	public void setContinueOnErrors(boolean flag) {
		this.continueOnErrors = flag;
	}

	/**
	 * @return true, чтобы при исключениях о неверных проверках НЕ поднимать исключения из своего метода run(),
	 * (по-умол false)
	 */
	public boolean isDontThrowErrors() {
		return dontThrowErrors;
	}

	public void setDontThrowErrors(boolean dontThrowErrors) {
		this.dontThrowErrors = dontThrowErrors;
	}

	/**
	 * Фабричный метод получения мапы (пользователь-доступ)
	 * @param userAndAccess строка в виде "login:доступ; login:доступ"
	 * @return
	 */
	public static UserAccessTable makeUserToPermAccessTable(String userAndAccess) {
		final UserWithPermissions result = new UserWithPermissions();
		result.setUsersAccessMap(userAndAccess);
		return result;
	}

	/**
	 * Фабричный метод получения мапы (БР-доступ) и (БР-пользователь)
	 * @param brAndAccess строка в виде "br:доступ; br:доступ"
	 * @param brAndUser строка в виде "br:login; br:login"
	 * @return
	 */
	public static UserAccessTable makeBusinessRoleToUserAccessTable(String brAndAccess, String brAndUser) {
		final UserWithBR result = new UserWithBR();
		result.setRoleAccessMap(brAndAccess);
		result.setRoleUsersMap(brAndUser);
		return result;
	}

	@Override
	public void run() {
		// получение узла ...
		NodeRef docRef = this.nodeRef;
		if (docRef == null && this.findRef.hasRefData() ) {
			docRef = this.findRef.findNodeRef( getContext().getFinder());
		}

		if (docRef == null && this.nodeRefMacros != null) {
			docRef = findNodeByMacros( this.nodeRefMacros);
		}

		logger.debug( String.format("checking users access for node {%s}", docRef));

		chkConfigArgs();

		if (logger.isDebugEnabled()) { 
			final StringBuilder dump = Utils.makeAttrDump(docRef, getContext().getNodeService(), String.format("\nAnalyzing node {%s}:\n", docRef));
			final Set<AccessPermission> perms = getContext().getPermissionService().getAllSetPermissions(docRef);
			if (perms == null)
				dump.append("\n\t (!) ACL is NULL\n");
			else
				dump.append( String.format("\n\t ACL counter %d: [%s]\n", perms.size(), perms.toString() ));
			logger.debug( dump.toString());
		}

		final StringBuilder sb = new StringBuilder();

		// проверка доступа по бизнес ролям, через конкретных пользователей...
		boolean failed = false;
		int i = 0;
		final Map<String, String> accTable = this.accessMap.getUsersAccess();
		sb.append( STR_TABLE_DELIMITER);
		sb.append( String.format( "\n  %2s\t %5s\t %3s\t '%s'", "nn", "isOk", "access", "User Login"));
		sb.append( STR_TABLE_DELIMITER);
		for(Map.Entry<String, String> e: accTable.entrySet() ) {
			i++;
			final String usrLogin = e.getKey(); // соответствующий пользователь
			final String perm = e.getValue();
			final boolean flagOK = chkNodeUserAccess( docRef, usrLogin, perm);
			sb.append( String.format( "\n  %2d\t %5s\t %3s\t '%s'", i, (flagOK ? "OK" : "*FAIL"), perm, usrLogin));

			if (logger.isTraceEnabled()) { // выдаём состав авторизаций пользователя
				sb.append( String.format( "\n\t\t [%s]", getContext().getAuthorityService().getAuthoritiesForUser(usrLogin) ));
			}
			if (!flagOK)
				failed = true;
		}
		sb.append( STR_TABLE_DELIMITER);

		logger.info( String.format( "%s access check for node: %s\n%s", (failed ? "(!) BAD" : "SUCCESS"), docRef, sb.toString() ));

		if (failed && !isDontThrowErrors())
			throw new TestFailException( isContinueOnErrors(), String.format( "Failed access check for node: %s\n%s", docRef, sb.toString() ));
	}


	static boolean chkAndLogCondition( boolean condStop, String msg) {
		if (condStop) { // при статусе = "ошибка": логирование и поднятие исключения
			LoggerFactory.getLogger (LecmCheckNodeACL.class).warn(msg);
			throw new RuntimeException(msg);
		}
		return !condStop;
	} 

	/**
	 * Проверить сконфигурированные параметры
	 */
	private void chkConfigArgs() {
		chkAndLogCondition( (nodeRef == null || nodeRef.getId().trim().length() == 0) 
				&& (!this.findRef.hasRefData())
				&& (this.nodeRefMacros == null || this.nodeRefMacros.trim().length() == 0)
				, "No node ref assigned");
		chkAndLogCondition( rwCheckingPropName == null || rwCheckingPropName.trim().length() == 0, "No check property assigned");

		chkAndLogCondition( accessMap == null, "No access mapping assigned");
		accessMap.validateAndLogIfError();
	}

	/**
	 * Проверить обеспечивается ли уровень доступа пользователя к указанному документу.
	 * @param ref 
	 * @param usrLogin
	 * @param perm
	 * @return
	 */
	private boolean chkNodeUserAccess( final NodeRef ref, final String usrLogin, final String perm) {
		// AuthenticationUtil.runAs(runAsWork, uid);
		final QName prop = getContext().getFinder().makeQName(this.rwCheckingPropName);

		// final NodeService nodeServ = getContext().getPublicServices().getNodeService();

		// final NodeRef person = getContext().getPublicServices().getPersonService().getPerson(usrLogin);

		final RunAsWork<Boolean> runner = new RunAsWork<Boolean>() {
			@Override
			public Boolean doWork() throws Exception {
//				// для проверки записи надо будет пишущую транзакцию ...
//				final boolean transReadonly = (perm != StdPermission.full);
//				final boolean transReaquiersNew = false;
//				final Boolean flag = getContext().getTransactionService().getRetryingTransactionHelper().doInTransaction(
//					new RetryingTransactionHelper.RetryingTransactionCallback<Boolean>() {
//						@Override
//						public Boolean execute() throws Throwable {
//							return doAccessCheck(usrLogin, nodeServ, ref, prop, perm); 
//						}
//
//					}, transReadonly, transReaquiersNew);
//				return flag;
				return doAccessCheck(usrLogin, getContext().getPublicServices(), ref, prop, perm);
			}
		};

		final Boolean result = AuthenticationUtil.runAs( runner, usrLogin); // person.getId());

		return result;
	}

	@SuppressWarnings("unused")
	boolean doAccessCheck(String usrLogin, ServiceRegistry registry, NodeRef ref, QName prop, String perm)
	{
		final NodeService nodeServ = registry.getNodeService();
	
		// DONE: типизировать исключение только для случая org.alfresco.repo.security.permissions.AccessDeniedException
		String stage = String.format( "read property '%s' as '%s'", prop, usrLogin);

		if (logger.isDebugEnabled()) {
			final AccessStatus accRead = registry.getPermissionService().hasPermission(ref, "Read");
			final AccessStatus accWrite = registry.getPermissionService().hasPermission(ref, "Write");
			logger.debug( String.format( "\n\t {%s} %5s %10s read(%7s) write(%s)", ref, perm, usrLogin, accRead, accWrite) );
		}

		try {
			final boolean deny = (perm == null || "deny".equalsIgnoreCase(perm));
			final String checkPermission = deny ? "Read" : perm;
			final AccessStatus accPerm = registry.getPermissionService().hasPermission(ref, checkPermission); // ((perm == StdPermission.full) ? "Write" : "Read");
			return (!deny) ? (accPerm == AccessStatus.ALLOWED) : (accPerm == AccessStatus.DENIED);
		} catch (Throwable ex) {
			final String info = String.format( "Permission '%s' is not supported", perm);
			// неизвестное значение - бросаемся исключением ... 
			logger.error( info, ex);
			return chkAndLogCondition( false, info);
		}
	}

}
