package ru.it.lecm.security.events;

import ru.it.lecm.security.Types;

/**
 * Модель безопасности данных LECM.
 * @author rabdullin
 */
public interface IOrgStructureNotifiers {

	public static final String BEAN_NAME = "lecmSecurityGroupsBean";

	/**
	 * Оповещение о создании узла указанного типа (Сотрудник, Должность, Департамент, Рабочая группа или Бизнес-Роль).
	 * @param obj
	 * @return полное название SG-группы (личной), которая соответствует объекту nodeId
	 */
	String orgNodeCreated(Types.SGPosition obj);

	/**
	 * Оповещение о деактивации узла указанного типа (Сотрудника Должности, Департамента, Рабочая группа или Бизнес-Роли)
	 * @param obj
	 */
	void orgNodeDeactivated(Types.SGPosition obj);

	/**
	 * Связать Сотрудника с системным пользователем или наоборот отвязать
	 * @param employeeId id Сотрудника
	 * @param alfrescoUserLogin login пользователя Альфреско
	 * @param tie true для привязки пользователя, false для отвязывания.
	 */
	void orgEmployeeTie(String employeeId, String alfrescoUserLogin, boolean tie);

	/**
	 * Включить security-группу child в parent.
	 * @param child родительская SG-позиция
	 * @param parent родительская SG-позиция, значение NULL не допускается
	 * Например,
	 * 1) при задании нового родительского Департамента "АРод" для Департамента "БДоч" надо выполнить:
	 *		// SG_OU(БДоч) -> SG_OU(АРод)
	 * 		sgInclude( Types.SGKind.SG_SV.getSGPos(БДоч.id), Types.SGKind.SG_SV.getSGPos(АРод.id));
	 *
	 *		// SG_SV(АРод) -> SG_SV(БДоч)
	 * 		sgInclude( Types.SGKind.SG_SV.getSGPos(АРод.id), Types.SGKind.SG_SV.getSGPos(БДоч.id));
	 *
	 * 		// + привязка БР из родительского подразделения к каждому Сотруднику БДоч ...
	 *
	 * 2) Актививрование по Доверенностям:
	 * 		// найти список всех доверенностей, подлежащих активации, и для каждой
	 * 		// включить личную БР доверенного лица (кому) в БР доверяющего (от кого):
	 * 		sgInclude( Types.SGKind.SGPrivateBusinessRole(Кому.id, БР), Types.SGKind.SGPrivateBusinessRole(ОтКого.id, БР) );
	 * 		// и всё =)
	 */
	void sgInclude( Types.SGPosition child, Types.SGPosition parent);


	/**
	 * Проверить является ли одна группа внутри другой 
	 * @param child
	 * @param parent
	 * @return true, если child находится внутри parent
	 */
	boolean isSgInside( Types.SGPosition child, Types.SGPosition parent);

	/**
	 * Исключить security-группу child из oldParent.
	 *  (!) Смена родителя у обекта орг-штатки должна выполняться явным вызовом пар sgRemove(от старого) + sgInclude(в новую)
	 * @param child
	 * @param oldParent прежняя родительская группа
	 */
	void sgExclude( Types.SGPosition child, Types.SGPosition oldParent);

	/**
	 * Оповещение о присвоении бизнес-роли объекту орг-штатки
	 * (!) Здесь выполняется непосредственное присвоение только для указанного
	 * объекта, так что все нужные рекурсивные и др доп присвоения надо выполнить
	 * явно для каждого отдельного объекта.
	 * @param broleCode id бизнес-роли
	 * @param obj id и тип узла объекта орг-штатки
	 * Пример:
	 * 		// присвоение БР roleCode для Сотрудника employeeId
	 * 		orgBRAssigned( roleCode, Types.SGKind.SG_ME.getSGPos(employeeId));
	 *
	 * 		// присвоение БР roleCode для Должностной позиции dpId на которую назначен Сотрудник employeeId
	 * 		orgBRAssigned( roleCode, Types.SGKind.getDeputyPosition(dpId, employeeId));

	 * 		// присвоение БР roleCode для Подразделения орг-штатки orgUnitId
	 * 		orgBRAssigned( roleCode, Types.SGKind.SG_OU.getSGPos(orgUnitId));
	 */
	void orgBRAssigned(String broleCode, Types.SGPosition obj);

	/**
	 * Оповещение о снятии бизнес-роли с объекта орг-штатки
	 * @param broleCode id бизнес-роли
	 * @param obj id и тип узла объекта орг-штатки
	 */
	void orgBRRemoved(String broleCode, Types.SGPosition obj);
}
