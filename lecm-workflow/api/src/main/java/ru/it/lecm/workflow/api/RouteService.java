package ru.it.lecm.workflow.api;

import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.workflow.RouteType;

/**
 *
 * @author vmalygin
 */
public interface RouteService {

	NodeRef createEmptyRoute(final RouteType routeType);
}
