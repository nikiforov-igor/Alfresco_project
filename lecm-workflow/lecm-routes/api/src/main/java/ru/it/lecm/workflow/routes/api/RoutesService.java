package ru.it.lecm.workflow.routes.api;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 * Сервис маршрутов документов.
 * @author vlevin
 */
public interface RoutesService {
	NodeRef getRoutesFolder();
	NodeRef createNewTemporaryNode(NodeRef parentNode, QName nodeType);
}
