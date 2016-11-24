package ru.it.lecm.orgstructure.beans;

import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.security.LecmPermissionService;
import ru.it.lecm.security.Types.SGPosition;

/**
 * Основные оповещения с уровня модели LECM до уровня службы security groups.
 * Синхронизирует события создания-удаления-изменения объектов Сотрудник,
 * Подразделение, Должность, Бизнес-роль, предоставления БР для Сотрудников,
 * Должностей и Подразделений, с соответствующими операциями для службы
 * работы с security groups:
 *   ru.it.lecm.orgstructure.policies.* -> call OrgstructureSGNotifierBean -> IOrgStructureNotifiers
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
     * Назначение Сотрудника на Должностную Позицию.
     * @param employee узел типа "lecm-orgstr:employee-link"
     * @param dpid узел типа "lecm-orgstr:position"
     */
    void notifyEmploeeSetDP(NodeRef employee, NodeRef dpid);

    /**
     * Снять Сотрудника с Должностной Позиции.
     * @param employee
     * @param brole
     */
    void notifyEmploeeRemoveDP(NodeRef employee, NodeRef dpid);

    /**
     * Снять Сотрудника с роли в рабочей группе
     */
    void notifyEmployeeRemoveWG(NodeRef employee, NodeRef nodeWR, NodeRef group);

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
     * @param person
     */
    void notifyEmploeeDown(NodeRef employee, NodeRef person);

    void notifyChangedOU(NodeRef nodeDP);

    /**
     * Оповещение об изменении Подразделения.
     * @param nodeOU изменённое Подразделение
     * @param parentOU родительский узел (доп здесь будет проверка, что это
     * Подразделение, а не просто папка)
     */
    void notifyChangedOU(NodeRef nodeOU, NodeRef parent);

    void notifyDeleteOU(NodeRef nodeOU, NodeRef parent);

    /**
     * Оповещение об изменении/создании рабочей группы
     * @param nodeWG изменённая Рабочая группа
     */
    public void notifyChangedWG(NodeRef nodeWG);

    public void notifyEmployeeSetWG(NodeRef employee, NodeRef nodeWR, NodeRef group);

    /**
     * Оповещение об удалении рабочей группы
     * @param nodeWG изменённая Рабочая группа
     */
    public void notifyDeleteWG(NodeRef nodeWG);

    /**
     * Выполнить подключение БР выданных для подразделения OU и всех его
     * вложенных подразделений (и, как следствие, для Сотрудников подразделения и вложенных в него).
     * @param nodeOU исходное подразделение
     * @param include true, чтобы выдать БРоли, false чтобы отозвать
     * @param recursivelyUseParentsBR true, чтобы дополнительно выполнить привязку
     * БР назначенных для Родительских Подразделений
     *
     */
    void notifyPrivateBRolesOfOrgUnits( NodeRef nodeOU, boolean include, boolean recursivelyUseParentsBR);

    /**
     * Оповещение о предоставлении/отборе Бизнес Роли для Сотрудника/Должности/Департамента
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
     * Оповещение о делегировании (и отборе) специальной пермиссии от одного Сотрудника другому
     * @param permissionGroup пермиссия
     * @param sourceEmployee от кого
     * @param destEmployee кому
     * @param created true, если пермиссия делегируется и false, если отбирается
     */

    void notifySpecDelegationChanged(LecmPermissionService.LecmPermissionGroup permissionGroup,
                                     NodeRef sourceEmployee, NodeRef destEmployee, boolean created);

    /**
     * Оповещение о делегировании (и отборе) руководящей позиции от одного Сотрудника другому
     * @param sourceEmployee от кого
     * @param destEmployee кому
     * @param created true, если делегируется и false, если отбирается
     */
    void notifyBossDelegationChanged( NodeRef sourceEmployee, NodeRef destEmployee, boolean created);

}
