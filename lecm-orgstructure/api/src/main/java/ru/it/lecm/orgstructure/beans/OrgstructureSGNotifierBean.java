package ru.it.lecm.orgstructure.beans;

import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;

import ru.it.lecm.security.Types.SGPosition;

/**
 * Основные оповещения с уровня модели LECM до уровня службы security groups.
 * Синхронизирует события создания-удаления-изменения объектов Сотрудник,
 * Подразделение, Должность, Бизнес-роль, предоставления БР для Сотрудников, 
 * Должностей, Подразделений с соответствующими операциями для службы 
 * работы с security groups:
 *   ru.it.lecm.orgstructure.policies.* -> call IOrgStructureNotifiers
 * 
 * @author rabdullin
 */
public interface OrgstructureSGNotifierBean {

	void notifyNodeCreated(SGPosition pos);

	void notifyNodeDeactivated(SGPosition pos);

	/**
	 * Оповещение о создании/изменении должностной позиции
	 * @param nodeDP  узел должностной позиции
	 */
	void notifyChangeDP(NodeRef nodeDP);

	/**
	 * Оповещение о создании/изменении должностной позиции
	 * @param nodeDP  узел должностной позиции
	 * @param isBoss true, если она является руководящей
	 * @param orgUnit
	 */
	void notifyChangeDP(NodeRef nodeDP, boolean isBoss, NodeRef orgUnit);

	/**
	 * Назначение БР для Сотрудника.
	 * @param employee
	 * @param brole
	 */
	void notifyEmploeeSetBR(NodeRef employee, NodeRef brole);

	/**
	 * Убрать БР у Сотрудника
	 * @param employee
	 * @param brole
	 */
	void notifyEmploeeRemoveBR(NodeRef employee, NodeRef brole);

	/**
	 * Назначение DP для Сотрудника.
	 * @param employee узел типа "lecm-orgstr:employee-link"
	 * @param dpid узел типа "lecm-orgstr:position"
	 */
	void notifyEmploeeSetDP(NodeRef employee, NodeRef dpid);

	/**
	 * Убрать БР у Сотрудника
	 * @param employee
	 * @param brole
	 */
	void notifyEmploeeRemoveDP(NodeRef employee, NodeRef dpid);

	/**
	 * Нотификация о связывании Сотрудника и пользователя Альфреско.
	 * @param employee
	 * @param isActive true, если флажок "активен" включен
	 */
	void notifyEmploeeTie(NodeRef employee, Boolean isActive);

	/**
	 * Нотификация о связывании Сотрудника и пользователя Альфреско.
	 * @param employee
	 */
	void notifyEmploeeTie(NodeRef employee);

	/**
	 * Нотификация об отвязывании Сотрудника и пользователя Альфреско.
	 * @param employee
	 * @param userLogin
	 */
	void notifyEmploeeDown(NodeRef employee);

	void notifyChangedOU(NodeRef nodeDP);

	/**
	 * Оповещение об изменении/создании орг-единицы
	 * @param nodeOU изменённое Подразделение
	 * @param parentOU родительский узел (доп здесь будет проверка, что это
	 * Подразделение, а не просто папка)
	 */
	void notifyChangedOU(NodeRef nodeOU, NodeRef parent);

	void notifyDeleteOU(NodeRef nodeOU, NodeRef parent);

	/**
	 * Выполнить подключение БР выданных для подразделения OU и всех его вложенных,
	 * для Сотрудников подразделения OU (и вложенных)
	 * @param nodeOU исходное подразделение
	 * @param include true, чтобы выдать БРоли, false чтобы отозвать
	 * @param recursivelyUseParentsBR true, чтобы дополнительно выполнить привязку
	 * БР назначенных для Родительских Подразделений
	 *
	 */
	void notifyPrivateBRolesOfOrgUnits( NodeRef nodeOU, boolean include, boolean recursivelyUseParentsBR);

	/**
	 * Оповещение о предоставлении/отборе Бизнес Роли для  Сотрудника/Должности/Департамента
	 * @param nodeAssocRef связь объекта (target) и БР (source)
	 * @param created true, если БР предоставляется и false, если отбирается
	 */
	void notifyBRAssociationChanged(AssociationRef nodeAssocRef, boolean created);


	/**
	 * Оповещение о делегировании (и отборе) бизнес роли от одного Сотрудника другому
	 * @param brole бизнес роль
	 * @param sourceEmployee от кого
	 * @param destEmployee кому
	 * @param created true, если БР делегируется и false, если отбирается
	 */
	void notifyBRDelegationChanged( NodeRef brole, NodeRef sourceEmployee, NodeRef destEmployee, boolean created);


	/**
	 * Оповещение о делегировании (и отборе) руководящей позиции от одного Сотрудника другому
	 * @param sourceEmployee от кого
	 * @param destEmployee кому
	 * @param created true, если делегируется и false, если отбирается
	 */
	void notifyBossDelegationChanged( NodeRef sourceEmployee, NodeRef destEmployee, boolean created);

}
