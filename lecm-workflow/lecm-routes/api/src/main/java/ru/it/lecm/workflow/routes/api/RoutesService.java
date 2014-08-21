package ru.it.lecm.workflow.routes.api;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * Сервис маршрутов документов.
 * @author vlevin
 */
public interface RoutesService {
	NodeRef getRoutesFolder();

}
