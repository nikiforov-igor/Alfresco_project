package ru.it.lecm.secretary;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * Сервис для нарезки прав секретарям в различных ситуациях
 * @author vmalygin
 */
public interface SecretarySecurityService {

	/**
	 * выполнить нарезку прав для секретаря
	 * в этом методе происходит определение
	 *  - является ли сотрудник боссом
	 *  - является ли сотрудник делегатом
	 *  - является ли сотрудник простым смертным
	 * в завыисимости от этого происходит нарезка прав
	 * @param chief - "руководитель"
	 * @param secretary - секретарь
	 * @return true если удалось нарезать права, false - не удалось
	 */
	boolean addSecretary(final NodeRef chief, final NodeRef secretary);

	/**
	 * выполнить нарезку прав для секретаря, в случае если "руководитель" - простой сотрудник
	 * @param chief - "руководитель"
	 * @param secretary - секретарь
	 * @return true если удалось нарезать права, false - не удалось
	 */
	boolean addSecretarySimple(final NodeRef chief, final NodeRef secretary);

	/**
	 * выполнить нарезку прав для секретаря, в случае если "руководитель" - руководитель по оргштатке или его полный делегат
	 * дает секретарю права подчиненных руководителя
	 * @param chief - "руководитель" - руководитель по оргштатке или полный делегат
	 * @param secretary - секретарь
	 * @return true если удалось нарезать права, false - не удалось
	 */
	boolean addSecretaryBossOnly(final NodeRef chief, final NodeRef secretary);

	/**
	 * лишить секретаря прав его "руководителя" с учетом руководящей позиции и делегирования
	 * в этом методе происходит определение
	 *  - является ли сотрудник боссом
	 *  - является ли сотрудник делегатом
	 *  - является ли сотрудник простым смертным
	 * @param chief - "руководитель"
	 * @param secretary - секретарь
	 * @return true если удалось лишить прав, false - не удалось
	 */
	boolean removeSecretary(final NodeRef chief, final NodeRef secretary);

	/**
	 * сделать "руководителем" боссом в указанном подразделении или лишить его прав руководителя
	 * @param chief - "руководитель"
	 * @param unit - "подразделение"
	 * @param isBoss - true - мы хотим сделать его боссом, false - мы хотим чтобы он перестал быть боссом
	 */
	void makeChiefBossOrEmployee(final NodeRef chief, final NodeRef unit, final boolean isBoss);

	/**
	 * сделать секретаря боссом в указанном подразделении или лишить его прав руководителя
	 * @param secretary
	 * @param isBoss
	 */
	void makeSecretaryBossOrEmployee(final NodeRef secretary, final boolean isBoss);

	/**
	 * удалить группу SG_SECRETARY для сотрудника employee если она есть и она пустая
	 * @param employee сотрудник у которого может быть группа SG_SECRETARY
	 * @return true если группа есть, пустая и ее удалось удалить false в противном случае
	 */
	boolean removeSGSecretary(final NodeRef employee);

	/**
	 * удалить группу SG_SECRETARY для сотрудника employee если она есть
	 * не важно пустая она или нет - все равно удалим ее
	 * @param employee сотрудник у которого может быть группа SG_SECRETARY
	 * @return true если группа есть и ее удалось удалить false в противном случае
	 */
	boolean purgeSGSecretary(final NodeRef employee);

	void notifyChiefDelegationChanged(final NodeRef bossEmployee, final NodeRef chiefBossAssistant, boolean created);
}
