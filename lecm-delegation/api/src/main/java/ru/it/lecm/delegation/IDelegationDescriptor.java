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
}
