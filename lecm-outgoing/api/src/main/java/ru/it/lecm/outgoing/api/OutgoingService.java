package ru.it.lecm.outgoing.api;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 *
 * @author vmalygin
 */
public interface OutgoingService {
	/**
     * Оборачиваем узел в ссылку html страницы
     * @param nodeRef
     * @param description
     * @param linkUrl
     * @return Строка с html--ссылкой
	 */
	String wrapperLink(final NodeRef nodeRef, final String description, final String linkUrl);
}
