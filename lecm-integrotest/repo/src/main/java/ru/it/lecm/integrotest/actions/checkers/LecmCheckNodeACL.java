package ru.it.lecm.integrotest.actions.checkers;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;

import ru.it.lecm.integrotest.TestFailException;
import ru.it.lecm.integrotest.actions.LecmActionBase;
import ru.it.lecm.integrotest.utils.NodeRefData;
import ru.it.lecm.integrotest.utils.Utils;
import ru.it.lecm.security.events.INodeACLBuilder.StdPermission;

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

	/**
	 * Значение по-умолчанию для проверки возможности записи в атрибут
	 */
	private static final String DEFAULT_WRITE_VALUE = "abc_write_test";

	/* проверяемый узел (папка или документ), см также название */
	private NodeRef nodeRef;

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

	/**
	 * Таблица прав доступа (value) для пользователей (key)
	 */
	public interface UserAccessTable {
		/**
		 * @return список пользователь(key) -> соответствующее право 
		 */
		public Map<String, StdPermission> getUsersAccess();

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
		private Map<String, StdPermission> usersAccessMap;  // для строк в формате функции Utils.makeBRoleMapping

		@Override
		public void validateAndLogIfError() {
			chkAndLogCondition( this.usersAccessMap == null || this.usersAccessMap.isEmpty(), "No users to permission mapping assigned");

			// ... данные всегда сбалансированы, если они указаны
		}

		@Override
		public Map<String, StdPermission> getUsersAccess() {
			return this.usersAccessMap;
		}

		public void setUsersAccess(Map<String, StdPermission> value) {
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
		private Map<String, StdPermission> roleAccess; // для строк в формате функции Utils.makeBRoleMapping

		/* 
		 * перечисление бизнес-ролей (key) и соот-щих логинов пользователей (value),
		 * чтобы в итоге проверять доступ по бизнес-ролям
		 */
		private Map<String, String> roleUsers; // строка в формате функции Utils.makeSplitMapping


		/**
		 * @return карта БР -> доступ
		 */
		public Map<String, StdPermission> getRoleAccess() {
			return roleAccess;
		}

		public void setRoleAccess(Map<String, StdPermission> roleAccess) {
			this.roleAccess = roleAccess;
			logger.debug( String.format( "after setRoleAccess access table:\n\t %s", this.roleAccess));
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
			logger.debug( String.format( "after setRoleUsers access table:\n\t %s", this.roleUsers));
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
		public Map<String, StdPermission> getUsersAccess() {
			final Map<String, StdPermission> result = new HashMap<String, StdPermission>();
			for(Map.Entry<String, StdPermission> e: roleAccess.entrySet() ) {
				final String brole = e.getKey();
				final StdPermission perm = e.getValue();
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
		this.findRef.clear(); // drop
	}

	/**
	 * Задать проверяемый узел.
	 * @param value
	 */
	public void setNodeRefStr(String value) {
		this.setNodeRef( new NodeRef(value));
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
		if (this.nodeRef == null && this.findRef.hasRefData() ) {
			this.nodeRef = this.findRef.findNodeRef( getContext().getFinder());
		}

		chkConfigArgs();

		final StringBuilder sb = new StringBuilder();

		// проверка доступа по бизнес ролям, через конкретных пользователей...
		boolean failed = false;
		int i = 0;
		final Map<String, StdPermission> accTable = this.accessMap.getUsersAccess();
		for(Map.Entry<String, StdPermission> e: accTable.entrySet() ) {
			i++;
			final String usrLogin = e.getKey();; // соответствующий пользователь
			final StdPermission perm = e.getValue();
			final boolean flagOK = chkNodeUserAccess( nodeRef, usrLogin, perm);
			sb.append( String.format( "\n\t %d (%s) %s access for user '%s'"
						, i, (flagOK ? "OK" : "!? FAIL"), perm, usrLogin));
			if (!flagOK)
				failed = true;
		}

		logger.info( String.format( "%s access check for node: %s\n%s", (failed ? "(!) BAD" : "SUCCESS"), this.nodeRef, sb.toString() ));

		if (failed)
			throw new TestFailException( String.format( "Failed access check for node: %s\n%s", this.nodeRef, sb.toString() ));
	}


	static boolean chkAndLogCondition(boolean condStop, String msg) {
		if (condStop) { // при статусе = "ошибка": логирование и поднятие исключения
			logger.warn(msg);
			throw new RuntimeException(msg);
		}
		return !condStop;
	} 

	/**
	 * Проверить сконфигурированные параметры
	 */
	private void chkConfigArgs() {
		chkAndLogCondition( nodeRef == null || nodeRef.getId().trim().length() == 0, "No node ref assigned");
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
	private boolean chkNodeUserAccess( final NodeRef ref, final String usrLogin, final StdPermission perm) {
		// AuthenticationUtil.runAs(runAsWork, uid);
		final QName prop = getContext().getFinder().makeQName(this.rwCheckingPropName);

		final NodeService nodeServ = getContext().getPublicServices().getNodeService();

		final NodeRef person = getContext().getPublicServices().getPersonService().getPerson(usrLogin);

		final Boolean result = AuthenticationUtil.runAs( new RunAsWork<Boolean>(){
			@Override
			public Boolean doWork() throws Exception {
				return doAccessCheck(usrLogin, nodeServ, ref, prop, perm); 
			}}
			, person.getId()
		);

		return result;
	}

	@SuppressWarnings("unused")
	boolean doAccessCheck(String usrLogin, NodeService nodeServ, NodeRef ref, QName prop, StdPermission perm)
	{
		// DONE: типизировать исключение только для случая org.alfresco.repo.security.permissions.AccessDeniedException
		switch(perm) {
			case noaccess: {  // : должно свалиться при чтении
				try {
					final Serializable x = nodeServ.getProperty(ref, prop);
					// раз прочитано -> доступ имеется, а это не правильно при deny
					return false;
				} catch(org.alfresco.repo.security.permissions.AccessDeniedException tx) {
					logger.warn( String.format("check AccessDeny of user '%s' for node '%s':\n%s" 
							, usrLogin, ref, tx.getMessage()));
					return true; // падение правильное, т.к. нет доступа ...
				}
			} 
			case readonly: {  // : должно читаться
				try {
					final Serializable x = nodeServ.getProperty(ref, prop);
					// раз прочитано -> доступ имеется, правильно
					return true;
				} catch(org.alfresco.repo.security.permissions.AccessDeniedException tx) {
					logger.warn( String.format("check ReadAccess of user '%s' for node '%s'/attribute '%s':\n%s" 
							, usrLogin, ref, prop, tx.getMessage()));
					return false;
				}
			} 
			case full: {  // : должно писаться в некоторый атрибут
				try {
					// раз можно писать -> доступ на чтение тоже должен иметься ...
					final Serializable x = nodeServ.getProperty(ref, prop);

					// пишем сконфигурированное значение ...
					nodeServ.setProperty(ref, prop, this.writeValue);

					// восстановим прежнее значение атрибута
					nodeServ.setProperty(ref, prop, x);

					return true;
				} catch(org.alfresco.repo.security.permissions.AccessDeniedException tx) {
					logger.warn( String.format("check WriteAccess of user '%s' for node '%s'/attribute '%s':\n%s" 
							, usrLogin, ref, prop, tx.getMessage()));
					return false;
				}
			}
		}

		// неизвестное значение - бросаемся исключением ... 
		return chkAndLogCondition( false, String.format( "Permission '%s' is not supported", perm));
	}

}
