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
	 * согласно указанному статусу
	 * @param nodeRef документа или папки
	 * @param lifeCycleId  уникальный id (код) жизненного цикла
	 * @param statusId новый статус документа
	 */
	void rebuildACL(NodeRef nodeRef, String lifeCycleId, String statusId );

	/**
	 * Прописать Статические Роли на папку (документ).
	 * @param nodeRef id статусной-папки
	 * @param lifeCycleId  уникальный id (код) жизненного цикла, который соот-ет папке
	 * @param statusId стаус, которому соот-ет nodeRef
	 */
	void rebuildStaticACL(NodeRef nodeRef, String lifeCycleId, String statusId);


	/**
	 * Выполнить очистку матрицы прав статических ролей
	 */
	void clearAccessMatrix();

	/**
	 * Задать матрицу прав для указанного ЖЦ и статуса. 
	 * @param lifeCycleId  уникальный id (код) жизненного цикла
	 * @param status код статуса
	 * @param map карта нарезки прав ключ=код роли, значение=доступ
	 */
	void regAccessMatrix( String lifeCycleId, String status, Map<String, StdPermission> map);


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
}
