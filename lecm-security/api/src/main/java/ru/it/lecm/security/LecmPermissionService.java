package ru.it.lecm.security;

import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.security.Types.SGPosition;

import javax.naming.AuthenticationException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Служба работы с lecm-правами:
 *   1) получение списка всех доп lecm-полномочий на уровне Альфреско
 *   2) проверка наличия указанного lecm-полномочия Сотрудника в рамках узла
 *   3) Индивидуальные роли: выдача/отбор полномочий на узлы (папки и документы) для Сотрудников
 *   4) Динамические роли: выдача/отбор на узел
 *   5) перестройка нарезок Статических Ролей и Динамичских Ролей для узлов (соот-но, папок и документов)
 *
 * Предполагаемая схема использования Статическиз и Динамических бизнес-ролей:
 *   1) Статический доступ организуется нарезкой прав на статус-папки - метод rebuildStaticACL;
 *   2) Динамический доступ реализуется в два этапа - выдать динамическую роль
 * пользователю на документ (grantDynamicRole) и пересчитать права в документе
 * на все выданные динамически БР (rebuildACL).
 *     2.1) в нужный момент, внешним кодом должен быть вызван метод grantDynamicRole
 * для явной выдачи пользователю Динамической Роли в рамках конкретного документа,
 *     2.2) при сменах статуса документа внешний код должен явно вызвать rebuildACL,
 * чтобы выполнилась перенарезка прав на ВСЕ ВЫДАННЫЕ на этот момент Динамические
 * БР в документе.
*/
public interface LecmPermissionService {

	public static final String BEAN_NAME = "lecmPermissionServiceBean";

	/**
	 * Разрешение, соответствующее deny
	 */
	final public static String ACCPERM_EMPTY = "deny";

    final public String PERM_DOC_CREATE = "_lecmPerm_DocCreate";
    final public String  PERM_DOC_DELETE = "_lecmPerm_DocDelete";
    final public String  PERM_ATTR_LIST = "_lecmPerm_AttrList";
    final public String  PERM_ATTR_EDIT = "_lecmPerm_AttrEdit";
    final public String  PERM_CONTENT_LIST = "_lecmPerm_ContentList";
    final public String  PERM_CONTENT_ADD = "_lecmPerm_ContentAdd";
    final public String  PERM_CONTENT_VIEW = "_lecmPerm_ContentView";
    final public String  PERM_CONTENT_ADD_VER = "_lecmPerm_ContentAddVer";
    final public String  PERM_CONTENT_DELETE = "_lecmPerm_ContentDelete";
    final public String  PERM_OWN_CONTENT_DELETE = "_lecmPerm_OwnContentDelete";
    final public String  PERM_CONTENT_COPY = "_lecmPerm_ContentCopy";
    final public String  PERM_WF_LIST = "_lecmPerm_WFEnumBP";
    final public String  PERM_WF_TASK_LIST = "_lecmPerm_WFTaskList";
    final public String  PERM_HISTORY_VIEW = "_lecmPerm_HistoryView";
    final public String  PERM_TAG_VIEW = "_lecmPerm_TagView";
    final public String  PERM_TAG_CREATE = "_lecmPerm_TagCreate";
    final public String  PERM_TAG_DELETE = "_lecmPerm_TagDelete";
    final public String  PERM_LINKS_VIEW = "_lecmPerm_LinksView";
    final public String  PERM_LINKS_CREATE = "_lecmPerm_LinksCreate";
    final public String  PERM_LINKS_DELETE = "_lecmPerm_LinksDelete";
    final public String  PERM_COMMENT_CREATE = "_lecmPerm_CommentCreate";
    final public String  PERM_COMMENT_VIEW = "_lecmPerm_CommentView";
    final public String  PERM_COMMENT_DELETE = "_lecmPerm_CommentDelete";
    final public String  PERM_ACTION_EXEC = "_lecmPerm_ActionExec";
    final public String  PERM_MEMBERS_LIST = "_lecmPerm_MemberList";
    final public String  PERM_MEMBERS_ADD = "_lecmPerm_MemberAdd";
    final public String  PERM_SET_RATE = "_lecmPerm_SetRate";
	final public String  PERM_READ_ATTACHMENT = "_lecmPerm_ReadAttachment";
	final public String  PERM_WRITE_ALL_FIELDS = "_lecmPerm_WriteAllFields";
    final public String DYNAMIC_AUTH_PREFIX = "DYN_AUTH_";

    /**
	 * Получить обозначение группы полномочий по имени, среди зарегистрированных групп.
	 * @param lecmPermissionGroupName название группы, например, "LECM_BASIC_PG_Editor".
	 * Регистр на входе не важен - в любом случае, возвращаемое значение будет
	 * содержать точное название уже с учётом регистра.
	 * @return
	 */
	LecmPermissionGroup findPermissionGroup( String lecmPermissionGroupName );

	/**
	 * Найти полномочие по имени среди фактически зарегистрированных
	 * @param lecmPermissionName полное Альфреско-имя полномочия (например, "_lecmPermCreateTag", "_lecmPermViewTag").
	 * Регистр не важен, в любом возвращаемое значение будет содержать точное
	 * название с учётом регистра.
	 * @return существующее полномочие или Null, если такого полномочия не зарегистрировано
	 */
	LecmPermission findPermission( String lecmPermissionName );


	/**
	 * Получение списка PermissionGroup - названий групп привилегий Альфреско
	 * (используются для выбора в UI при настройке машины состояний и выдачи
	 * привилегий участникам ЖЦ)
	 * @return RO-список названий групп привилегий Альфреско, например, "LECM_BASIC_PG_Initiator", "LECM_BASIC_PG_Reader"
	 */
	Collection<LecmPermissionGroup> getPermGroups();

	/**
	 * Получение списка LECM-полномочий - элементарных привилегий Альфреско
	 * @return RO-список названий атомарных привилегий Альфреско, например, "_lecmPermCreateTag", "_lecmPermViewTag"
	 */
	Collection<LecmPermission> getAllPermissons();

	/**
	 * Определение наличия конкретной привилегии у Сотрудника относительно узла (документа, папки)
	 * @param permissionOrGroup группа ("LECM_BASIC_PG_Initiator") или атомарная привилегия (например, "_lecmPerm_ViewTag")
	 * @param node проверяемый узел
	 * @param userLogin login  Пользователя Альфреско
	 * @return true, если указанная привилегия permission имеется у пользователя
	 * userName для объекта node, иначе false.
	 */
	// TODO: (?) ввести исопльзование employeeId вместо userLogin
	boolean hasPermission(AlfrescoSecurityNamedItemWithPrefix permissionOrGroup, NodeRef node, String userLogin);
	boolean hasPermission(String permission, NodeRef node, String userLogin);

	/**
	 * Определение наличия конкретной привилегии у текущего сотрудника относительно узла (документа, папки)
	 * @param permission  Привелегия
	 * @param node Ссылка на узел
	 * @return true, если указанная привилегия permission имеется у текущего пользователя
	 * <br/> Пример:
	 * <b><br/>if (!permissionBean.hasPermission( "_lecmPerm_MemberList", docId)) throw new SecurityException("No access");</b>
	 */
	boolean hasPermission(String permission, NodeRef node);

	/**
	 * Проверяет, что пользователь является админом
	 * @param login логин пользователя
	 * @return
	 */
	boolean isAdmin(String login);

	/**
	 * Проверяет элемент на доступность
	 * @param nodeRef Ссылка на элемент
	 * @return true, если есть права на чтение элемента
	 */
	public boolean hasReadAccess(NodeRef nodeRef);
	public boolean hasReadAccess(final NodeRef nodeRef, final String userLogin);

	/**
	 * Проверка наличия привелегии у текущего сотрудника относительно узла (документа, папки).
	 * Если нет привелегии, выбрасывается AlfrescoRuntimeException
	 *
	 * @param permission Привелегия
	 * @param node Ссылка на узел
	 */
	public void checkPermission(final String permission, final NodeRef node);

	/**
	 * Добавление участника к узлу с заданной группой привилегий
	 * @param permissionGroup группа привилегий, например, "LECM_BASIC_PG_Initiator", "LECM_BASIC_PG_Reader"
	 * @param node узел, доступ к которому надо обеспечить
	 * @param employeeRef nodeRef Сотрудника
	 */
	void grantAccess(LecmPermissionGroup permissionGroup, NodeRef node, NodeRef employeeRef);

	/**
	 * Исключить Сотрудника из группы привилегий данного узла (документа, папки)
	 * @param permissionGroup название группы привилегий, например, "LECM_BASIC_PG_Initiator", "LECM_BASIC_PG_Reader"
	 * @param node узел, доступ к которому надо ограничить
	 * @param employeeRef nodeRef Сотрудника
	 */
	void revokeAccess(LecmPermissionGroup permissionGroup, NodeRef node, NodeRef employeeRef);

	/**
	 * Добавить для объекта (указанному позицией) право доступа к узлу (документу/папке)
	 * @param permissionGroup
	 * @param node
	 * @param securityPos sec-позиция, соот-щая объекту, для которого надо назначить разрешение
	 */
	void grantAccessByPosition(LecmPermissionGroup permissionGroup, NodeRef node, SGPosition securityPos);


	/**
	 * Исключить разрешения у объекта к данному узлу (документу/папки)
	 * @param permissionGroup название группы привилегий, например, "LECM_BASIC_PG_Initiator", "LECM_BASIC_PG_Reader"
	 * @param node узел, доступ к которому надо ограничить
	 * @param securityPos sec-позиция, соот-щая объекту, у которого надо убрать разрешение
	 */
	void revokeAccessByPosition(LecmPermissionGroup permissionGroup, NodeRef node, SGPosition securityPos);

	/**
	 * Предоставить Динамическую Роль на документ/папку указанному пользователю.
	 * @param roleCode id Динамической Роли
	 * @param nodeRef документа или папки
	 * @param employeeId id Сотрудника
	 * @param permissionGroup предоставляемый доступ, если null, то будет присвоено право по-умолчанию (конфигурируется бинами)
	 */
	void grantDynamicRole(String roleCode, NodeRef nodeRef, String employeeId, LecmPermissionGroup permissionGroup);
    void grantDynamicRole(String roleCode, NodeRef nodeRef, String employeeId, String permission);
	/**
	 * Отобрать у Сотрудника динамическую роль в документе/папке
	 * @param roleCode id Динамической Роли
	 * @param nodeRef документа или папки
	 * @param employeeId id Сотрудника
	 */
	void revokeDynamicRole( String roleCode, NodeRef nodeRef, String employeeId);

	/**
	 * Зачистить динамическую роль в документе/папке
	 * @param roleCode id Динамической Роли
	 * @param nodeRef документа или папки
	 */
	void revokeDynamicRole(String roleCode, NodeRef nodeRef);

	/**
	 * Получение всех сотрудников с динамической ролью в документе
	 * @param document документ
	 * @param roleCode динамическая роль
	 * @return список сотрудников
	 */
	public List<NodeRef> getEmployeesByDynamicRole(NodeRef document, String roleCode);

	/**
	 * Перепрописать Статические Роли на папку (документ).
	 * @param nodeRef id статусной-папки
	 * @param accessMap карта нарезки прав:
	 *    здесь ключ - это код Статической Бизнес Роли,
	 *    значение - группа полномочий (прав) доступа на узел (документ или папку) для этой Статической БР.
	 * Например, { key="Initiator", value="LECM_BASIC_PG_Editor" }
	 */
	void rebuildStaticACL(NodeRef nodeRef, Map<String, LecmPermissionGroup> accessMap);

	/**
	 * Перестроить ACL-список Динамических прав для указанного документа/папки
	 * согласно указанным правам доступа для бизнес-ролей
	 * @param nodeRef ref-документа или папки
	 * @param accessMap карта нарезки прав: здесь ключ - это код Динамической
	 * Бизнес Роли (динамической групповой, например, "Инициаторы" или "Читатели"),
	 * значение - права доступа на документ для этой Динамической БР.
	 * Например, { key="Initiator", value="LECM_BASIC_PG_Editor" }
	 */
	void rebuildACL(NodeRef nodeRef, Map<String, LecmPermissionGroup> accessMap);


	/**
	 * Отладочная проца для удобства журналирования.
	 * Сформировать таблицу lecm-прав (строки) для указанных пользователей (столбцы).
	 * Вида:
	 * ------------------------------------------------------------
	 * [nn]	     permTag         	user1	user2	...
	 * ------------------------------------------------------------
	 * [1]	_lecmPerm_CommentView	TRUE	false	...
	 * [2]	_lecmPerm_MemberList	TRUE	false	...
	 * [3]	...
	 * ------------------------------------------------------------
	 * (TRUE с большой буквы, false наоборот)
	 *
	 * @param info
	 * @param nodeRef
	 * @param userLogins список имён пользователей, относительно которых надо проверить доступ
	 * @return SB со сформированным списком
	 */
	StringBuilder trackAllLecmPermissions( String info, NodeRef nodeRef,
			String ... userLogins);

    /**
     * Выполнить формирование ACE для списка доступа ACL указанного узла
     * @param nodeRef
     * @param authority
     * @param permGrp
     * @throws AuthenticationException
     */
    void setACE(final NodeRef nodeRef, final String authority, final LecmPermissionGroup permGrp) throws AuthenticationException;

    /**
	 * Именованный security-объект Альфреско, в имено которого содержится префикс
	 */
	public interface AlfrescoSecurityNamedItemWithPrefix {

		/**
		 * Вернуть полное название объекта (Группы Привилегий с префиксом PFX_LECM_ROLE = "LECM_BASIC_PG_",
		 * или Полномочия с префиксом PFX_LECM_PERMISSION = "_lecmPerm_")
		 * @return системное наименование:
		 * 		1) of permissionGroup. Например, "LECM_BASIC_PG_Initiator", "LECM_BASIC_PG_Reader"
		 *		2) of permission. Например, "_lecmPerm_SetRate", "_lecmPerm_CreateTag"
		 * Всегда не NULL.
		 */
		public String getName();

		/**
		 * Вернуть короткое название (без префикса PFX_LECM_ROLE = "LECM_BASIC_PG_" или префикса PFX_LECM_PERMISSION = "_lecmPerm_")
		 * @return короткое системное наименование:
		 * 		1) of permissionGroup. Например, "Initiator", "Reader".
		 * 		2) of permission. Например, "SetRate", "CreateTag".
		 * Всегда не NULL.
		 */
		public String getShortName();

		/**
		 * @return характерный (для объектов данного типа) префикс внутри названия name.
		 * <br/>Пример "LECM_BASIC_PG_" или "_lecmPerm_"
		 */
		String getPrefix();

	}

	public interface LecmPermissionGroup
		extends AlfrescoSecurityNamedItemWithPrefix
	{

		/**
		 * префикс системной Роли (группы полномочий Альфреско), которую надо относить к LECM-Системе
		 * Например, "LECM_BASIC_PG_Initiator", "LECM_BASIC_PG_Reader"
		 */
		public static final String PFX_LECM_ROLE = "LECM_BASIC_PG_";

		/**
		 * "Well known" permission groups
		 */
		public static String PGROLE_Initiator = PFX_LECM_ROLE + "Initiator"; // "LECM_BASIC_PG_Initiator"
		public static String PGROLE_Reader = PFX_LECM_ROLE + "Reader"; // "LECM_BASIC_PG_Reader"
		public static String PGROLE_Member = PFX_LECM_ROLE + "Member"; // "LECM_BASIC_PG_Reader"

		public String getLabel();

	}

	public interface LecmPermission
		extends AlfrescoSecurityNamedItemWithPrefix
	{
		/**
		 * префикс в названии привилегии Альфреско, которую надо относить к LECM-Системе
		 * Например, "_lecmPermCreateTag", "_lecmPermViewTag"
		 */
		public static final String PFX_LECM_PERMISSION = "_lecmPerm_";
	}

    /**
     * Проверка наличия прав чтения у документа для сотрудника
     * @param document документ
     * @param employeeLogin логин сотрудника
     * @param roleName имя роли
     * @return
     */
    public boolean hasEmployeeDynamicRole(NodeRef document, String employeeLogin, String roleName);

    /**
     * Список существующих прав для пользователя по отношению к документу
     * @param document документ
     * @param employee сотрудник
     * @return
     */
    public List<String> getEmployeeRoles(NodeRef document, NodeRef employee);

	/**
	 * Получение имени авторити доверителя делегирования
	 * @param owner доверитель
	 * @return имя авторити доверителя
     */
	public String getAuthorityForDelegat(NodeRef owner);
}
