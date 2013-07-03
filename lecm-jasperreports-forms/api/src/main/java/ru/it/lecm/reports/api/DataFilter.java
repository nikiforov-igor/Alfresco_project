package ru.it.lecm.reports.api;

import org.alfresco.service.cmr.repository.NodeRef;

public interface DataFilter {

	/**
	 * Проверить, выполняются ли для указанного узла условия фильтра по ассоциациям
	 * @param id
	 * @return
	 */
	boolean isOk(NodeRef id); 

}
