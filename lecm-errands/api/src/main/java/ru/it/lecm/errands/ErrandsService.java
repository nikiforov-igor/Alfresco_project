package ru.it.lecm.errands;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 * User: AIvkin
 * Date: 09.07.13
 * Time: 12:09
 */
public interface ErrandsService {
	public static final String ERRANDS_NAMESPACE_URI = "http://www.it.ru/logicECM/errands/1.0";

	public static final QName TYPE_ERRANDS = QName.createQName(ERRANDS_NAMESPACE_URI, "document");

	/**
	 * Получение папки для черновиков
	 * @return ссылку на папку с черновиками
	 */
	public NodeRef getDraftRoot();
}
