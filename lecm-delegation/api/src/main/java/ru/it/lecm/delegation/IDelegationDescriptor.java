package ru.it.lecm.delegation;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 *
 * @author VLadimir Malygin
 * @since 13.12.2012 12:42:48
 * @see <p>mailto: <a href="mailto:vmalygin@it.ru">vmalygin@it.ru</a></p>
 */
public interface IDelegationDescriptor {
	/**
	 * получение nodeRef-ы папки в которой хранятся параметры делегирования
	 * @return nodeRef
	 */
	NodeRef getDelegationOptsContainer ();

	/**
	 * получение типа данных который хранится в папке DelegationOptsContainer
	 * @return
	 */
	QName getDelegationOptsItemType ();

	/**
	 * является ли указанный пользователь Технологом
	 * @param employeeRef ссылка на сотрудника
	 * @return true/false
	 */
	boolean isEngineer (NodeRef employeeRef);

	/**
	 * занимает ли указанный пользователь руководящую должность
	 * @param employeeRef ссылка на сотрудника
	 * @return true/false
	 */
	boolean isBoss (NodeRef employeeRef);

	/**
	 * имееет ли текущий пользователь у себя в подчинении другого пользователя
	 * @param bossRef employee который является боссом
	 * @param subordinateRef employee который является подчиненным
	 * @return true/false Если bossRef == subordinateRef то возвращается true
	 */
	boolean hasSubordinate (NodeRef bossRef, NodeRef subordinateRef);
}
