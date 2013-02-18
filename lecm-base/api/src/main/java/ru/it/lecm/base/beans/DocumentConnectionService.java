package ru.it.lecm.base.beans;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * User: AIvkin
 * Date: 18.02.13
 * Time: 13:54
 */
public interface DocumentConnectionService {
	public static final String DOCUMENT_CONNECTIONS_ROOT_NAME = "Связи";

	/**
	 * Получение директории подписки.
	 * Если такой узел отсутствует - он НЕ создаётся.
	 */
	NodeRef getConnectionsRootRef();
}
