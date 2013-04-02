package ru.it.lecm.security;

import org.alfresco.service.cmr.repository.NodeRef;

import java.util.Collection;
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

	/**
	 * Получить обозначение группы полномочий по имени, среди зарегистрированных групп.
	 * @param lecmPermissionGroupName назвнаие группы, например, "LECM_BASIC_PG_Editor"
	 * Регистр не важен, в любом возвращаемое значение будет содержать точное 
	 * название с учётом регистра. 
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
	 */
	boolean hasPermission(String permission, NodeRef node);

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
	 * @param employeeId id Сотрудника
	 */
	void grantAccess(LecmPermissionGroup permissionGroup, NodeRef node, String employeeId);


	/**
	 * Исключить Сотрудника из группы привилегий данного узла (документа, папки)
	 * @param permissionGroup название группы привилегий, например, "LECM_BASIC_PG_Initiator", "LECM_BASIC_PG_Reader"
	 * @param node узел, доступ к которому надо ограничить
	 * @param employeeId id Сотрудника
	 */
	void revokeAccess(LecmPermissionGroup permissionGroup, NodeRef node, String employeeId);


	/**
	 * Предоставить Динамическую Роль на документ/папку указанному пользователю.
	 * @param roleCode id Динамической Роли
	 * @param nodeRef документа или папки
	 * @param employeeId id Сотрудника
	 * @param permissionGroup предоставляемый доступ, если null, то будет присвоено право по-умолчанию (конфигурируется бинами)
	 */
	void grantDynamicRole( String roleCode, NodeRef nodeRef, String employeeId, LecmPermissionGroup permissionGroup);

	/**
	 * Отобрать у Сотрудника динамическую роль в документе/папке
	 * @param roleCode id Динамической Роли
	 * @param nodeRef документа или папки
	 * @param employeeId id Сотрудника
	 */
	void revokeDynamicRole( String roleCode, NodeRef nodeRef, String employeeId);

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

}
