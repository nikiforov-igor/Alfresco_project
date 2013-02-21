package ru.it.lecm.security.events;

import java.util.Map;

import org.alfresco.service.cmr.repository.NodeRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Интерфейс сервиса для нарезки прав на папки и файлы.
 * Предполагаемая схема использования:
 *   1) Статический доступ организуется нарезкой прав на статус-папки - метод grantStaticRole;
 *   2) Динамический доступ реализуется в два этапа - выдать динамическую роль 
 * пользователю на документ и пересчитать права в документе на все выданные.
 *     2.1) в нужный момент, внешним кодом должен быть вызван метод grantDynamicRole 
 * для явной выдачи пользователю Динамической Роли в рамках конкретного документа,
 *     2.2) при сменах статуса документа внешний код должен явно вызвать rebuildACL,
 * чтобы выполнилась перенарезка прав на ВСЕ ВЫДАННЫЕ на этот момент Динамические
 * БР в документе.
 *
 * @author rabdullin
 */
public interface INodeACLBuilder {

	/**
	 * Предоставить Динамическую Роль на документ/папку указанному пользователю.
	 * @param roleCode id Динамической Роли
	 * @param nodeRef документа или папки
	 * @param userId id Сотрудника
	 */
	void grantDynamicRole( String roleCode, NodeRef nodeRef, String userId);

	/**
	 * Отобрать у Сотрудника динамическую роль в документе/папке
	 * @param roleCode id Динамической Роли
	 * @param nodeRef документа или папки
	 * @param userId id Сотрудника
	 */
	void revokeDynamicRole( String roleCode, NodeRef nodeRef, String userId);

	/**
	 * Перестроить ACL-список Динамических прав для указанного документа/папки
	 * согласно указанным правам доступа для бизнес-ролей
	 * @param nodeRef ref-документа или папки
	 * @param accessMap карта нарезки прав: здесь ключ - это код Динамической 
	 * Бизнес Роли (динамической групповой, например, "Инициаторы" или "Читатели"),
	 * значение - права доступа на документ для своей Динамической БР.
	 */
	void rebuildACL(NodeRef nodeRef, Map<String, StdPermission> accessMap);

	/**
	 * Прописать Статические Роли на папку (документ).
	 * @param nodeRef id статусной-папки
	 * @param accessMap карта нарезки прав: здесь ключ - это код Динамической 
	 * Бизнес Роли (динамической групповой, например, "Инициаторы" или "Читатели"),
	 * значение - права доступа на документ для своей Динамической БР.
	 */
	void rebuildStaticACL(NodeRef nodeRef, Map<String, StdPermission> accessMap);

	/**
	 * Виды доступа к данным
	 */
	public enum StdPermission {
		noaccess("-"),
		readonly("R"),
		full("RW");

		final static private Logger logger = LoggerFactory.getLogger(StdPermission.class);

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
					if ("read".equals(name) || "r".equals(name))
						return StdPermission.readonly;
					if ("write".equals(name) || "w".equals(name) || "+".equals(name))
						return StdPermission.full;
					if ("deny".equals(name) || "-".equals(name))
						return StdPermission.noaccess;
					return Enum.valueOf(StdPermission.class, name);
				} catch (Throwable t) {
					logger.error( String.format("Unknown mnemonic ignored: (%s) '%s'", StdPermission.class.getSimpleName(), name), t);
				}
			}
			return null;
		}
	}
}
